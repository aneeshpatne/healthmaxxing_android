package com.aneesh.healthmaxxing.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScaleManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun measurements(): Flow<ScaleMeasurement> = callbackFlow {
        if (!hasRequiredRuntimePermissions()) {
            close(SecurityException("Bluetooth permission is required"))
            return@callbackFlow
        }

        val adapter = bluetoothAdapter()
        if (adapter == null || !adapter.isEnabled) {
            close(IllegalStateException("Bluetooth is unavailable or disabled"))
            return@callbackFlow
        }

        var latestMeasurement = ScaleMeasurement()
        var gattRef: BluetoothGatt? = null

        val callback = object : BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    close(IllegalStateException("Scale connection failed: $status"))
                    closeGatt(gatt)
                    return
                }

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    closeGatt(gatt)
                    close()
                }
            }

            @SuppressLint("MissingPermission")
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    close(IllegalStateException("Scale service discovery failed: $status"))
                    closeGatt(gatt)
                    return
                }

                val characteristic = gatt
                    .getService(SERVICE_UUID)
                    ?.getCharacteristic(NOTIFY_CHARACTERISTIC_UUID)

                if (characteristic == null) {
                    close(IllegalStateException("Scale notify characteristic not found"))
                    closeGatt(gatt)
                    return
                }

                gatt.setCharacteristicNotification(characteristic, true)
                val descriptor = characteristic.getDescriptor(CCCD_UUID)
                if (descriptor == null) {
                    close(IllegalStateException("Scale CCCD descriptor not found"))
                    closeGatt(gatt)
                    return
                }

                writeNotificationDescriptor(gatt, descriptor)
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                handleNotification(value)
            }

            @Deprecated("Needed for Android versions before API 33.")
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                @Suppress("DEPRECATION")
                handleNotification(characteristic.value)
            }

            private fun handleNotification(value: ByteArray) {
                val decoded = ScalePacketDecoder.decode(value, latestMeasurement) ?: return
                latestMeasurement = decoded
                trySend(decoded)

                if (decoded.isFinal) {
                    closeGatt(gattRef)
                    close()
                }
            }
        }

        try {
            @SuppressLint("MissingPermission")
            val gatt = adapter.getRemoteDevice(SCALE_MAC_ADDRESS).connectGatt(context, false, callback)
            gattRef = gatt
        } catch (exception: IllegalArgumentException) {
            close(IllegalStateException("Scale Bluetooth address is invalid: $SCALE_MAC_ADDRESS", exception))
        } catch (exception: SecurityException) {
            close(SecurityException("Bluetooth permission is required", exception))
        }

        awaitClose {
            closeGatt(gattRef)
        }
    }

    private fun bluetoothAdapter(): BluetoothAdapter? {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }

    private fun hasRequiredRuntimePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    private fun writeNotificationDescriptor(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        } else {
            @Suppress("DEPRECATION")
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            @Suppress("DEPRECATION")
            gatt.writeDescriptor(descriptor)
        }
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt(gatt: BluetoothGatt?) {
        gatt?.disconnect()
        gatt?.close()
    }

    private companion object {
        const val SCALE_MAC_ADDRESS = "CF:E9:4C:03:0E:56"
        val SERVICE_UUID: UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
        val NOTIFY_CHARACTERISTIC_UUID: UUID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb")
        val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}

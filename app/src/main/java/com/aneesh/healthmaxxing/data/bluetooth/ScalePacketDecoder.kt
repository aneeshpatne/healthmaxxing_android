package com.aneesh.healthmaxxing.data.bluetooth

object ScalePacketDecoder {
    fun decode(data: ByteArray, previous: ScaleMeasurement = ScaleMeasurement()): ScaleMeasurement? {
        if (data.size == 2 && data[0].toUByteInt() == 0xF3 && data[1].toUByteInt() == 0x00) {
            return previous.copy(isFinal = true)
        }

        if (data.size != 11) return null

        val packetType = data[0].toUByteInt()
        if (packetType != 0xCF && packetType != 0xCE) return null
        if (!hasValidChecksum(data)) return null

        val flags = data[2].toUByteInt()
        val dataType = data[9].toUByteInt()
        val rawWeight = data[3].toUByteInt() or (data[4].toUByteInt() shl 8)
        val weightKg = if (rawWeight > 0) rawWeight / 100f else previous.weightKg
        val decodedHeartRate = decodeHeartRate(data, flags, dataType)
        val heartRate = decodedHeartRate ?: previous.heartRate
        val impedance = decodeImpedance(
            encodedValue = data[5].toUByteInt() or
                (data[6].toUByteInt() shl 8) or
                (data[7].toUByteInt() shl 16),
            dataType = dataType
        ) ?: previous.impedanceOhms

        return ScaleMeasurement(
            weightKg = weightKg,
            heartRate = heartRate,
            impedanceOhms = impedance,
            isFinal = decodedHeartRate != null
        )
    }

    private fun hasValidChecksum(data: ByteArray): Boolean {
        val payloadXor = data.take(10).fold(0) { acc, byte -> acc xor byte.toUByteInt() }
        return payloadXor == data.last().toUByteInt()
    }

    private fun decodeImpedance(encodedValue: Int, dataType: Int): Float? {
        if (encodedValue == 0xFFFFFF) return null

        val b0 = encodedValue and 0xFF
        val n = (encodedValue shr 12) and 0x0F
        val baseHigh = (encodedValue shr 8) and 0x0F
        val baseLow = (encodedValue shr 16) and 0xFF
        val base = (baseHigh shl 8) or baseLow
        val x2 = base - (b0 * 4 + n)
        val rawOhms = if (x2 < 1) (x2 + 1) / 2 else x2 / 2
        if (rawOhms <= 0) return null

        var ohms = rawOhms.toFloat()
        if (dataType == 0xA0 && ohms > MAX_IMPEDANCE_OHMS) {
            ohms /= 10f
        }

        return ohms.takeIf { it in MIN_IMPEDANCE_OHMS..MAX_IMPEDANCE_OHMS }
    }

    private fun decodeHeartRate(data: ByteArray, flags: Int, dataType: Int): Int? {
        return when {
            (flags and 0xC0) == 0xC0 -> data[1].toUByteInt()
            dataType == 0x03 -> data[3].toUByteInt()
            dataType == 0x04 -> data[8].toUByteInt()
            else -> null
        }
    }

    private fun Byte.toUByteInt(): Int = toInt() and 0xFF

    private const val MIN_IMPEDANCE_OHMS = 200f
    private const val MAX_IMPEDANCE_OHMS = 1200f
}

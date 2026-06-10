# ⚡ HealthMaxxing

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/DI-Dagger_Hilt-2563EB?style=for-the-badge)](https://dagger.dev/hilt/)

**HealthMaxxing** is a premium, high-performance personal health analytics dashboard designed for Android. It is engineered to help you max out your physical stats, track body composition, monitor performance metrics, and leverage AI-driven insights to achieve your physique goals.

By combining real-time Bluetooth Low Energy (BLE) smart scale synchronization with advanced body composition modeling, HealthMaxxing provides a telemetry deck for your body.

---

## 🚀 Key Features

*   **🤖 AI-Powered Insights & Telemetry**: Evaluates your metrics using advanced models to generate actionable summaries, effort scores, momentum trends, and identify your "biggest levers" for physique recomposition.
*   **📡 BLE Smart Scale Direct Sync**: Real-time connection to smart scales, parsing byte-level packets to extract body weight, heart rate, and bioelectrical impedance.
*   **📊 Multi-Dimensional Metric Dashboard**:
    *   **Essentials**: Track your overall health score (FormaScore), metabolic body age, weight trends, and full body circumferences.
    *   **Performance**: Compute advanced athletic ratios including Fat-Free Mass Index (FFMI), FMI, Lean-to-Fat distribution, and anthropometric ratios (e.g. shoulder-to-waist, chest-to-waist).
    *   **Fat & Muscle Breakdowns**: Visual gauges and charts representing Visceral Fat, Subcutaneous Fat, and Skeletal Muscle mass.
*   **🔒 Secured Local Storage**: Fast, persistent storage utilizing Jetpack DataStore Preferences for user profiles and account metadata.

---

## 🏛️ Application Architecture

HealthMaxxing is built on **Clean Architecture** principles, combining **MVVM (Model-View-ViewModel)** with Jetpack Compose. This ensures modularity, testability, and a clear separation of concerns.

```mermaid
graph TD
    subgraph UI_Layer [UI Layer - Jetpack Compose]
        A[MainActivity / AppScaffold] --> B[MetricsScreen]
        A --> C[RecordScreen]
        A --> D[WorkoutsScreen - Planned]
        A --> E[VitalsScreen - Planned]
        
        B --> VM1[InsightsViewModel]
        B --> VM2[EssentialsViewModel]
        B --> VM3[PerformanceViewModel]
        B --> VM4[FatViewModel]
        B --> VM5[MuscleViewModel]
        C --> VM6[RecordViewModel]
    end

    subgraph Domain_Repository_Layer [Repository Layer]
        VM1 & VM2 & VM3 & VM4 & VM5 & VM6 --> Repos[Essentials/Fat/Muscle/Performance Repositories]
    end

    subgraph Data_Layer [Data Layer]
        Repos --> Net[Network Module - Retrofit/OkHttp]
        Repos --> DS[Datastore Preferences]
        VM6 --> BLE[ScaleManager - BLE GATT]
    end
    
    classDef ui fill:#e0f7fa,stroke:#00acc1,stroke-width:2px;
    classDef repo fill:#fff3e0,stroke:#fb8c00,stroke-width:2px;
    classDef data fill:#f3e5f5,stroke:#8e24aa,stroke-width:2px;
    class A,B,C,D,E,VM1,VM2,VM3,VM4,VM5,VM6 ui;
    class Repos repo;
    class Net,DS,BLE data;
```

---

## 📡 BLE Smart Scale Sync Engine

The app includes a custom byte packet parser specifically written to communicate with bioelectrical impedance scales. It hooks directly into the Android Bluetooth GATT profile and streams data using Kotlin `callbackFlow`.

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant App as HealthMaxxing App
    participant BLE as ScaleManager
    participant Scale as BLE Smart Scale
    
    User->>App: Steps on Scale & opens Record Screen
    App->>BLE: Start measurements() Flow
    BLE->>Scale: Connect to MAC Address (CF:E9:4C:03:0E:56)
    Scale-->>BLE: Connection Successful
    BLE->>Scale: Discover Services
    Scale-->>BLE: Service Discover Completed (SERVICE_UUID)
    BLE->>Scale: Write descriptor to enable notifications (NOTIFY_UUID)
    
    loop Real-Time Weight Streaming
        Scale->>BLE: Notification packet (Weight in progress)
        BLE->>App: Emit updated ScaleMeasurement (Weight only)
        App->>User: Render real-time weight
    end

    Scale->>BLE: Notification packet (Impedance & Heart Rate)
    Note over BLE: ScalePacketDecoder parses packets (CF/CE payload)<br/>Verifies Checksum XOR<br/>Calculates Impedance Ohms
    BLE->>App: Emit Final ScaleMeasurement (isFinal = true)
    App->>User: Display final body composition
    BLE->>Scale: Close & Disconnect GATT
```

### Packet Decoding Logic
The `ScalePacketDecoder` extracts bytes from notifications:
1. **Preamble Validation**: Expects standard headers (`0xCF` or `0xCE` packet types).
2. **XOR Checksum**: Checks that the first 10 bytes XORed match the 11th byte.
3. **Weight Decode**: Decodes two-byte raw weight values divided by 100 to get kilograms.
4. **Impedance Extraction**: Utilizes a non-linear base calibration formula to determine muscle/water resistance:
   $$\text{ohms} = \text{base} - (\text{byte}_0 \times 4 + n)$$

---

## 🏋️ Workouts Section (Planned)

The **Workouts Screen** is currently in development. It will serve as an advanced training logger designed to map how mechanical tension drives body composition improvements.

```mermaid
mindmap
  root((Workouts Module))
    Routine Engine
      Custom Routine Builder
      Predefined Splits
      Warmup Set Calculators
    Workout Logger
      Interactive Active Session UI
      Rest Timer with Audio/Haptic Cues
      Barbell Plate Calculator
    Volume Analytics
      Weekly Sets per Muscle Group
      Dynamic 1RM Estimation
      Progressive Overload Graphs
```

### Key Modules:
*   **Progressive Overload Telemetry**: Graphs mapping total volume load (sets × reps × weight) against corresponding increases in skeletal muscle mass.
*   **Dynamic Rest Engine**: Adaptive rest timers that scale depending on the target intensity (e.g., longer rest for heavy compounds, shorter for isolation movements).
*   **Muscular Volume Heatmaps**: Visual skeletal maps showing which muscle groups are adequately stimulated and which require more volume to max out recovery capacity.

---

## 🫁 Vitals Section (Planned)

The **Vitals Screen** is designed to collect and show systemic health indicators, tracking how well your body recovers from intense physical workloads.

```mermaid
graph LR
    Wearables[Smart Ring / Watch] -->|Health Connect Sync| VitalsController[Vitals Manager]
    VitalsController --> Sleep[Sleep & Recovery Score]
    VitalsController --> Cardio[Cardiovascular Fitness]
    VitalsController --> Stress[Autonomic Stress]
    
    Sleep --> SleepStage[Sleep Architecture Stages]
    Cardio --> HRV[Heart Rate Variability]
    Cardio --> RHR[Resting Heart Rate]
    Stress --> BP[Blood Pressure & SpO2]
```

### Key Modules:
*   **Google Health Connect Integration**: Seamlessly synchronize steps, sleep stages, active calories, and daily heart rate metrics from wearable devices (Fitbit, Garmin, Whoop, Apple Watch).
*   **Heart Rate Variability (HRV) Analysis**: Monitors autonomic nervous system balance to give you a daily "Readiness Score" before your workouts.
*   **Metabolic & Cardiovascular Telemetry**: Logging and tracking blood pressure trends, SpO2 levels, and resting heart rate trajectories over time.

---

## 📋 To Be Done (Roadmap)

To bring HealthMaxxing to its absolute maximum potential, the following milestones are on the roadmap:

### Phase 1: Core Functionality Enhancements
- [ ] **Dynamic BLE Scale Discovery**: Replace the hardcoded scale MAC address (`CF:E9:4C:03:0E:56`) with a BLE scanner to support connecting to any nearby compatible smart scale.
- [ ] **Offline Caching Layer**: Implement Android Room DB to cache backend insights, essentials, and composition metrics so the dashboard functions seamlessly offline.

### Phase 2: Workouts Module
- [ ] **Build Compose UI for Workouts**: Implement custom workout sessions, routine selections, and list views.
- [ ] **Volume Tracking Engine**: Implement persistent local SQL tables to record exercises, sets, reps, and completed sessions.
- [ ] **Active Workout Overlay**: Design a persistent notification or overlay timer for quick logs during active gym sessions.

### Phase 3: Vitals Module & Wearables Sync
- [ ] **Build Compose UI for Vitals**: Implement sleep metrics, daily steps, and HRV cards.
- [ ] **Google Health Connect Setup**: Add API permissions and write the sync manager that periodically pulls background sleep and heart rate data.
- [ ] **Stress/Recovery Algorithm**: Combine sleep quality, HRV, and workout volume to calculate your body's daily muscle recovery index.

---

## 🛠️ Tech Stack & Setup

*   **Min SDK**: 30 (Android 11)
*   **Target SDK**: 36 (Android 16)
*   **Jetpack Compose**: Full declarative UI built with Material Design 3.
*   **Kotlin Coroutines & Flow**: Reactive data streams for BLE updates and network requests.
*   **Dagger Hilt**: Compile-time dependency injection.
*   **Retrofit 2 & OkHttp 4**: HTTP client for API sync with JSON serialization (Gson).
*   **Jetpack DataStore**: Safe, asynchronous key-value storage.

### Local Setup
1. Clone this repository:
   ```bash
   git clone https://github.com/aneeshpatne/healthmaxxing_android.git
   ```
2. Open the project in **Android Studio (Ladybug or later)**.
3. Sync project with Gradle files.
4. Set up an emulator or connect a physical Android device.
5. Make sure Bluetooth permissions (`BLUETOOTH_CONNECT` and `ACCESS_FINE_LOCATION`) are granted when prompted on device.
6. Run the `app` module.

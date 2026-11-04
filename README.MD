# SY4-EEG BlockMind — AXIOM 3D Hex-Reflex Profiler & Local Telemetry Architecture

**Application ID:** `com.aistudio.sy4eeg.blockmind`  
**Namespace:** `com.example`  
**Platform:** Android (Kotlin, Jetpack Compose, Material 3, Room SQLite v4, AGSL/GLSL Shader Pipeline)  
**Deployment Gate:** Local-Only Serverless Infrastructure (`check:pass:true_only` // Zero External Egress)

---

## 1. Executive Architecture Overview

**SY4-EEG BlockMind** is a local-first, hardware-coupled Android telemetry workstation, 3D Hex-Reflex shader profiler, and neuro-somatic analytics console. The system operates entirely on-device (`127.0.0.1` loopback and internal application enclave) with zero external network egress, cryptographic SHA-256 chain continuity verification, and dual-table Room SQLite persistence (`SPECTRoom`).

```
+---------------------------------------------------------------------------------------------------+
|                                   PRESENTATION & HUD DECK LAYER                                   |
|  MainActivity -> AxiomMainScreen (Adaptive Canonical Split-Screen / Portrait HUD Overlay)         |
|  +-- GlslViewport (3D Hex-Reflex Raymarched Tunnel + Top Telemetry Bar + Continuity HUD)          |
|  +-- ShaderEffectModifier (AGSL RuntimeShader API 33+ / Compose GraphicsLayer Chromatic Fallback) |
|  +-- 6 Navigation Decks (HudTab):                                                                 |
|      [TUNNEL] [EEG] [UNICRITIC] [CODEX] [SPECT] [SHARE]                                           |
+-------------------------------------------------+-------------------------------------------------+
                                                  | StateFlow<AxiomUiState> (30 Hz / 33 ms Loop)
                                                  v
+---------------------------------------------------------------------------------------------------+
|                                  STATE & ORCHESTRATION LAYER                                      |
|  AxiomViewModel (MVVM Orchestrator, Continuity Interlock, SHA-256 Verifier, Audit Dispatcher)     |
+---------------+-------------------------+-------------------------+-------------------------------+
                |                         |                         |
                v                         v                         v
+-------------------------------+ +-----------------------+ +---------------------------------------+
|   REAL-TIME SERVICE BUSES     | | HARDWARE SENSOR LAYER | |      ON-DEVICE ROOM SQLITE VAULT      |
| - SpectRoomSensorPathService  | | BiometricSensorEngine | | SpectDatabase (v4, Write-Ahead Log)   |
|   (PATH-01 .. PATH-06 Bus)    | | - Accelerometer IMU   | | - spect_room_records (SpectRoomEntity)|
| - AsCoreRgbBiometricStream    | | - Gyroscope IMU       | | - spect_stats (SpectStatEntity)       |
|   (`as[core]` RGB Service)    | | - Touch Kinematics    | | - audit_logs (AuditLogEntity)         |
| - LocalOnlyPathsInterconnect  | | - Deterministic 5-Band| | - compliance_checks (ComplianceEntity)|
|   (`check:pass:true_only`)    | |   SY4-EEG Synthesis   | | SpectRepository & MockBiometricRepo   |
+-------------------------------+ +-----------------------+ +---------------------------------------+
```

---

## 2. Directory & Module Structure

```text
app/src/main/java/com/example/
├── MainActivity.kt                              # Entry point, Edge-to-Edge setup, ViewModel & HUD host
├── data/
│   ├── SpectEntities.kt                         # @Entity definitions: SpectRoomEntity, SpectStatEntity, AuditLogEntity, ComplianceCheckEntity
│   ├── SpectDao.kt                              # @Dao interface for SPECTRoom records, telemetry stats, SHA-256 audits, and compliance
│   ├── SpectDatabase.kt                         # @Database (Room v4) singleton with seed callback for SPECTRoom & MICA/STEELS records
│   ├── SpectRepository.kt                       # Dual-write repository synchronizing spect_stats and spect_room_records
│   └── MockBiometricRepository.kt               # SharedFlow producer for high-frequency mock RGB biometric packets
├── model/
│   └── AxiomModels.kt                           # Domain models, UniformState, ContinuityInterlock, EegState, UnicriticState, RustCodexCatalog
├── sensor/
│   └── BiometricSensorEngine.kt                 # Android SensorEventListener (Accelerometer/Gyroscope) + 5-band SY4-EEG synthesizer
├── service/
│   ├── AsCoreRgbBiometricStreamService.kt       # `as[core]` Tri-Chromatic RGB Biometric Stream Service & phase-locked waveform synthesizer
│   ├── SpectRoomSensorPathService.kt            # 6-Path SPECT Room Sensor Telemetry Bus (PATH-01..06) with 48-sample rolling buffers
│   ├── LocalOnlyPathsInterconnectService.kt     # 6-Node Local Only Paths Interconnect & `check:pass:true_only` serverless deploy engine
│   └── GlobalVsPersonalDeployService.kt         # Unified `[PaidvsFREE]` Anti-Subscription Decay Engine, Refined Whitepaper & `1+ /Own/Dev/Logic_States`
├── shader/
│   ├── AxiomGlRenderer.kt                       # OpenGL ES 2.0 renderer & aspect-ratio-coupled raymarching uniform matrix controller
│   └── AxiomShaders.kt                          # Vertex & Fragment GLSL source code + AGSL RuntimeShader post-processing program
├── ui/
│   ├── components/
│   │   ├── GlslViewport.kt                      # 3D Hex-Reflex Compose Canvas viewport, top telemetry bar, and status badges
│   │   ├── ShaderEffectModifier.kt              # Hardware-accelerated AGSL / graphicsLayer screen filter modifier
│   │   ├── TunnelHudDeck.kt                     # TUNNEL tab: shader sliders, Continuity Interlock card, Local Only Interconnect card
│   │   ├── BiometricsHudDeck.kt                 # EEG tab: 5-band EEG bars, `as[core]` RGB console, somatic vector HUD
│   │   ├── UnicriticHudDeck.kt                  # UNICRITIC tab: Lyapunov exponent, phase-space portrait, drift inject/stabilize controls
│   │   ├── CodexAndSpectDecks.kt                # CODEX & SPECT tabs: Whitepaper console, Rust/SQLx/OpenAPI codex, SHA-256 verifier, SPECT Room Sensor Dashboard
│   │   ├── GlobalVsPersonalDeployWhitepaperCard.kt # Unified `[PaidvsFREE]` Deploy, Corp Greed Decay, & Side-by-Side MiCA -> `[Personal account:DEV]` Whitepaper
│   │   ├── SpectRoomSensorDashboardCard.kt      # Real-time 6-path oscilloscope, path filter chips, sparkline cards, SPECTRoom entity vault
│   │   ├── LocalOnlyPathsInterconnectCard.kt    # 6-node local topology grid & `check:pass:true_only` serverless deploy gate cards
│   │   ├── ScreenFilterAntStudio.kt             # AntStudio screen filter preset selector & chromatic aberration tuner
│   │   ├── ScreenShareBestWorkDeck.kt           # SHARE tab: 4-target APK relay matrix (DEST-1..4), STEELS & MICA cards
│   │   ├── SteelsEvidenceRegistryCard.kt        # STEELS Statutory & Technical Evidence Registry UI
│   │   └── MicaComplianceCard.kt                # MICA Art. 68 Compliance & Governance matrix UI + Remembered Relay progression strip
│   └── theme/
│       ├── Color.kt                             # Cyber-scientific palette (Neon Cyan, Emerald, Magenta, Amber, Void Dark)
│       ├── Theme.kt                             # Material 3 dark color scheme & typography binding
│       └── Type.kt                              # Monospace telemetry & geometric display typography definitions
└── viewmodel/
    └── AxiomViewModel.kt                        # Central 30 Hz physics/biometric loop, Continuity Interlock, Room persistence actions
```

---

## 3. Core Architectural Subsystems

### 3.1 3D Hex-Reflex Raymarched Viewport & Continuity Interlock
- **Viewport Rendering (`GlslViewport.kt`, `AxiomShaders.kt`, `AxiomGlRenderer.kt`)**:
  - Renders a 60 FPS procedural 3D Hex-Reflex tunnel with dynamic aspect-ratio correction (`min(size.width, size.height) * 0.46f`) so wide-screen landscape and foldable layouts maintain circular geometric symmetry without horizontal stretching.
  - Angular frequency (`u_freq` / `ringCount`) is quantized via `round(u_freq).coerceAtLeast(1f)` to guarantee C0/C1 boundary continuity at `-π / +π` wrap seams.
- **Continuity Interlock (`ContinuityInterlockState` in `AxiomModels.kt` & `AxiomViewModel.kt`)**:
  - Synchronizes three critical planes across tab transitions, orientation changes, and shader preset switches:
    1. **GLSL Uniform Gate**: Validates `u_speed in [0.0, 5.0]`, `u_freq in [1.0, 24.0]`, `u_glow in [0.1, 3.0]`, and `u_distort in [0.0, 2.0]`.
    2. **SY4-EEG Spectral Gate**: Verifies finite, non-NaN 5-band amplitudes and phase coherence.
    3. **SHA-256 Chain Seal**: Links every transition into an unbroken cryptographic chain (`chainTipHash` + `previousChainHash`).
  - **Auto-Remediation**: When out-of-bounds uniforms or continuity drift are injected or detected, the Continuity Interlock automatically clamps uniforms back to canonical bounds (`quantizeFreq(u_freq)`) without interrupting the 3D tunnel frame loop.

### 3.2 `as[core]` Real-Time RGB Biometric Stream Service
- **Service Implementation (`AsCoreRgbBiometricStreamService.kt` & `MockBiometricRepository.kt`)**:
  - Synthesizes tri-chromatic neuro-somatic optical channels at 30 Hz:
    - **R-Channel (660 nm Hemodynamic Cortical Flux)**: Coupled to Beta + Gamma cognitive load and somatic acceleration.
    - **G-Channel (532 nm Autonomic PPG Pulse Wave)**: Coupled to Alpha + Theta coherence and heart-rate variability harmonics.
    - **B-Channel (470 nm Galvanic Phasic Arousal)**: Coupled to Delta baseline + Unicritic Lyapunov drift.
  - Supports 4 operating modes (`COUPLED_BIOMETRIC`, `CARDIAC_PPG_LOCK`, `CHROMA_STRESS_SWEEP`, `SPECTRAL_CALIBRATION`), pause/resume, phase-lock, and instant snapshots into `SPECTRoom`.

### 3.3 6-Path SPECT Room Sensor Telemetry Bus (`SpectRoomSensorPathService.kt`)
Aggregates all hardware and computational subsystems into six addressable real-time sensor paths (`PATH-01` .. `PATH-06`), maintaining a 48-sample waveform buffer per path and a 64-sample composite history:

| Path ID | Code | Subsystem Source | Primary Metric & Range | Coupled Uniform / Target |
| :--- | :--- | :--- | :--- | :--- |
| **PATH-01** | `SY4-EEG-5B` | `BiometricSensorEngine` 5-Band FFT | Dominant Band Power (`0..100 μV²/Hz`) | `u_freq` / `EEG_COHERENCE` |
| **PATH-02** | `ASCORE-RGB` | `AsCoreRgbBiometricStreamService` | Tri-Chromatic Luma (`20..100 %`) | `RGB_LUMA` / `u_glow` |
| **PATH-03** | `SOMATIC-IMU` | Android `TYPE_ACCELEROMETER` + `TYPE_GYROSCOPE` | 3-Axis Somatic G-Load (`0.5..3.5 g`) | `IMU_6DOF` / `u_distort` |
| **PATH-04** | `GLSL-RAY` | `GlslViewport` + `ContinuityInterlock` | Raymarch Frame Throughput (`24..60 FPS`) | `u_speed` + `u_freq` |
| **PATH-05** | `UNICRITIC` | `UnicriticState` Attractor Solver | Lyapunov Drift (`-1.50..+1.50 λ`) | `LYAPUNOV_GATE` |
| **PATH-06** | `SPECT-WAL` | `SpectDatabase` v4 SQLite Sink | SHA-256 Integrity & WAL Throughput (`100 %`) | `SHA256_SEAL` |

### 3.4 Local Only Paths Interconnect (`check:pass:true_only`)
- **Engine (`LocalOnlyPathsInterconnectService.kt`)**:
  - Bridges all 4 PDF-05 legal-axis APK relay targets (`DEST-1 [ADB-127]`, `DEST-2 [LOCAL-APK]`, `DEST-3 [CORE-VAULT]`, `DEST-4 [LOOP-STAGE]`) with `NODE-5 [SPECT-SQLITE]` (`spect_room_records`) and `NODE-6 [ASSET-VAULT]` (`assets/manifests/local_only.json`).
  - Enforces a 3-stage deterministic verification gate before issuing `check:pass:true_only`:
    1. **Zero External Network Egress**: Confirms all endpoint URI prefixes resolve strictly to `127.0.0.1`, `$HOME/.core-relay`, `/sdcard/`, `/data/`, `1+ /Own/Dev/Logic_States`, `sqlite:///`, or `file:///`.
    2. **Cryptographic SHA-256 Path Seal**: Computes a deterministic digest over every active path endpoint, latency, and continuity chain tip.
    3. **Local Room Audit Synchronization**: Persists a verified audit entry into `SpectDatabase` (`audit_logs` and `spect_room_records`).
  - Includes fault-injection (`injectNonLocalEgressFault`) and single-tap remediation (`remediateToLocalOnlyTrue`) to test and prove fail-closed blocking of non-local egress.

### 3.5 Unified Global vs. Personal Deploy (`[PaidvsFREE]`) & Refined Whitepaper (`MiCA Below -> [Personal account:DEV]`)
- **Anti-Subscription Corporate Greed Decay Engine (`GlobalVsPersonalDeployService.kt`)**:
  - Eliminates recurring subscriptions (`recurringSubscriptionFeeUsd = $0.00` forever under `S:OpenSource(nosubs!-asis)Contract!`).
  - Replaces extractive SaaS rent with a **One-Time Decaying Global Bond**:
    $$B(t) = B_0 \cdot e^{-\lambda t}, \quad \text{Recurring}(t) = \$0.00 \;\forall t$$
  - As epoch $t$ or decay constant $\lambda$ advances, the institutional bond remainder $B(t)$ decays directly into the **`[Personal account:DEV]` Open-Source Commons Pool** (`commonsSubsidyUnits = B_0 - B(t)`), guaranteeing that personal developer accounts (`[Personal account:DEV]`) remain 100% sovereign and free forever.
- **Refined Whitepaper by Remembered Relay Logic (Side-by-Side Progression)**:
  - **Column A — Start as of MiCA Below (Statutory Floor)**:
    - `WP-AXIS-I`: MiCA `(EU) 2023/1114` Art. 2(1)-(4) & Recital 22 non-CASP hardware/DLT exemption + `[if D~]` wave clamp floor.
    - `WP-AXIS-II`: `S:OpenSource(nosubs!-asis)Contract!` statutory baseline prohibiting custodial fee extraction & recurring rent traps.
    - `WP-AXIS-III`: PDF-05 4-Axis Legal Relay specification (`CS / EN / DE / LAT`) + DORA `(EU) 2022/2554` Art. 11 ICT continuity rules.
    - `WP-AXIS-IV`: Pre-compliance SHA-256 script verification (`Čištění skriptu SHA-256`) + `EPISTULA / SATIS / LITTERAE` state machine.
  - **Column B — Where We Start Now (`[Personal account:DEV]`)**:
    - `WP-AXIS-I`: `[Personal account:DEV]` sovereign origin mounted at `1+ /Own/Dev/Logic_States` with zero external intermediary.
    - `WP-AXIS-II`: Unified `[PaidvsFREE]` engine where `Recurring = $0.00` forever and the Global bond decays into the `[Personal account:DEV]` commons.
    - `WP-AXIS-III`: Active Remembered Relay (`DEST-1..4` + `NODE-5 [SPECT-SQLITE]` + `NODE-6 [ASSET-VAULT]`) bridged under `check:pass:true_only` (`0` external egress).
    - `WP-AXIS-IV`: Real-time `as[core]` RGB stream + 6-Path SPECT Room Sensor Bus + `1+ /Own/Dev/Logic_States` sealed into Room v4 SQLite.
- **`1+ /Own/Dev/Logic_States` Sovereign State Registry**:
  - Mounts 4 SHA-256-sealed logic state vectors under `file:///data/user/0/com.example/files/Own/Dev/Logic_States`:
    - `LOGIC-01` (`MICA_FLOOR_STATE.state`)
    - `LOGIC-02` (`NOSUBS_DECAY_STATE.state`)
    - `LOGIC-03` (`REMEMBERED_RELAY_STATE.state`)
    - `LOGIC-04` (`PERSONAL_DEV_SOVEREIGN.state`)

---

## 4. Data Persistence Layer (`SPECTRoom` / Room SQLite v4)

Defined in `SpectEntities.kt`, `SpectDao.kt`, `SpectDatabase.kt`, and `SpectRepository.kt`:

1. **`SpectRoomEntity` (`tableName = "spect_room_records"`)**
   - Primary entity representing sealed SPECTRoom sensor snapshots and biometric drift logs.
   - Fields:
     - `id: Int` (PrimaryKey, autoGenerate)
     - `sessionTag: String` (e.g., `SPECT-PATH-01`, `LOCAL-DEPLOY-GATE`, `SPECT-CORE-ALPHA`)
     - `biometricDriftLog: String` (Formatted telemetry log with `λ`, `Δ`, and sensor vector metadata)
     - `driftMagnitude: Float` (Normalized drift magnitude `[0.0 .. 1.0]`)
     - `coherenceRatio: Float` (Spectral/phase coherence ratio `[0.0 .. 1.0]`)
     - `dominantBand: String` (`DELTA`, `THETA`, `ALPHA`, `BETA`, `GAMMA`, or path code)
     - `sha256Digest: String` (64-char SHA-256 hex digest of the telemetry payload)
     - `sha256IntegrityStatus: String` (`VERIFIED`, `DRIFT_WARN`, or `CORRUPTED`)
     - `timestamp: Long` (Epoch milliseconds)

2. **`SpectStatEntity` (`tableName = "spect_stats"`)**
   - Stores summary metrics (`metricName`, `coherenceScore`, `driftIndex`, `dominantBand`, `biometricDriftLog`, `sha256Digest`, `sha256IntegrityStatus`, `timestamp`).
   - Synchronized automatically via `SpectRepository.insertSpectStat(...)` which writes to both `spect_stats` and `spect_room_records`.

3. **`AuditLogEntity` (`tableName = "audit_logs"`)**
   - Immutable SHA-256 script and manifest verification log (`scriptName`, `sha256Hash`, `status`, `timestamp`).

4. **`ComplianceCheckEntity` (`tableName = "compliance_checks"`)**
   - Stores MICA Art. 68 and STEELS statutory compliance state (`articleRef`, `controlName`, `passed`, `notes`, `updatedAt`).

---

## 5. Navigation & HUD Deck Architecture

The UI adapts dynamically across portrait handhelds and wide-screen/tablet viewports (`isWideScreen = maxWidth >= 700.dp`) and exposes primary navigation decks via `HudTab`:

1. **`TUNNEL` (`TunnelHudDeck.kt`)**
   - Real-time GLSL uniform sliders (`u_speed`, `u_freq`, `u_glow`, `u_distort`), preset selector (`AXIOM PRIME`, `NEURO PULSE`, `SINGULARITY`, `CALM VOID`), AntStudio Screen Filter controller, **Continuity Interlock Card**, and **Local Only Paths Interconnect Card**.
2. **`EEG` (`BiometricsHudDeck.kt`)**
   - 5-band SY4-EEG spectrum analyzer (`DELTA`, `THETA`, `ALPHA`, `BETA`, `GAMMA`), **`as[core]` RGB Biometric Stream Console**, and 6-DoF Somatic IMU vector monitor.
3. **`UNICRITIC` (`UnicriticHudDeck.kt`)**
   - Real-time Lyapunov stability gauge (`λ`), 2D phase-space attractor canvas, and drift fault injection / stabilization controls.
4. **`CODEX` (`CodexAndSpectDecks.kt` + `GlobalVsPersonalDeployWhitepaperCard.kt` + `LocalOnlyPathsInterconnectCard.kt`)**
   - **Unified Global vs. Personal Deploy (`[PaidvsFREE]`) & Side-by-Side Remembered-Relay Whitepaper (`Start: MiCA Below -> Now: [Personal account:DEV]` & `1+ /Own/Dev/Logic_States`)**, **Serverless Infra-Only Deploy (`check:pass:true_only`) Console**, **MiCA Pre-Compliance Architecture (`[if D~]`)**, **SteelsSystem `S:OpenSource(nosubs!-asis)Contract!` Evidence Registry**, and interactive **SHA-256 Script Integrity Verifier**.
5. **`SPECT` (`CodexAndSpectDecks.kt` + `SpectRoomSensorDashboardCard.kt`)**
   - **SPECT Room Sensor Paths Real-Time Telemetry Dashboard** featuring a 6-trace oscilloscope (`PATH-01`..`PATH-06`), interactive path filter chips, per-path sparkline cards with individual pause/seal actions, calibration/zero-drift controls, and the persisted `SpectRoomEntity` SQLite vault.

---

## 6. Build & Verification Commands

- **Compile Debug APK**:
  ```bash
  gradle assembleDebug
  ```
- **Run Unit & Robolectric Continuity/Telemetry Tests**:
  ```bash
  gradle :app:testDebugUnitTest
  ```
  Verified test suite in `app/src/test/java/com/example/ExampleUnitTest.kt` covers:
  - SHA-256 deterministic hashing & integrity verification
  - Continuity Interlock out-of-bounds uniform auto-remediation & angular frequency quantization
  - `as[core]` RGB Biometric Stream synthesis & mode switching
  - Local Only Paths Interconnect (`check:pass:true_only`) zero-egress enforcement, fault injection, and remediation
  - 6-Path SPECT Room Sensor Telemetry Bus real-time stepping, filtering, and calibration
  - Unified Global vs. Personal Deploy (`[PaidvsFREE]`) zero-subscription corporate greed decay, side-by-side MiCA to `[Personal account:DEV]` whitepaper comparison, and `1+ /Own/Dev/Logic_States` SHA-256 state registry


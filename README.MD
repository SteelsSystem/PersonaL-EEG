# SY4-EEG BlockMind — AXIOM 3D Hex-Reflex Profiler & Local Telemetry Architecture

[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Linux%20FS%20%7C%20Raspberry%20Pi%204-00e5ff?style=flat-square&logo=android)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-S%3AOpenSource(nosubs!--asis)Contract!-00e676?style=flat-square)](https://github.com/)
[![Egress Gate](https://img.shields.io/badge/Egress%20Gate-check%3Apass%3Atrue__only-00e5ff?style=flat-square)](https://github.com/)
[![Persistence](https://img.shields.io/badge/Persistence-Room%20v4%20SQLite%20WAL-b388ff?style=flat-square)](https://developer.android.com/training/data-storage/room)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20MVVM%20%2B%20AGSL%2FGLSL-ffb300?style=flat-square)](https://developer.android.com/)

**Application ID:** `com.aistudio.sy4eeg.blockmind`  
**Namespace:** `com.example`  
**Target Environment:** Android 14+ (API 34/36), Phone Linux Environments (Termux / PRoot-Distro / Chroot), Raspberry Pi 4 Model B (ARM64 / aarch64), Debian/Ubuntu  
**Deployment Invariant:** `check:pass:true_only` // Zero External Egress // 100% On-Device Enclave  

---
vlcek(zav)cobrasecurity.cz

## 3. High-Level Architecture Overview

The system operates strictly on-device across three functional planes:
1. **Interactive Presentation & HUD Plane**: Jetpack Compose M3 UI, AGSL `RuntimeShader` (API 33+) with fallback canvas pipeline, dynamic aspect-ratio-corrected 3D Hex-Reflex viewport, and the dedicated **SY4-EEG BlockMind Telemetry Dashboard**.
2. **State & Orchestration Plane**: `AxiomViewModel` driving a 30 Hz (33 ms) physics/biometric loop, continuous Tri-Axis Continuity Interlock, automatic out-of-bounds uniform remediation, and SHA-256 chain sealing.
3. **Hardware & Persistence Plane**: Android `SensorManager` IMU integration, `as[core]` tri-chromatic optical stream service, 6-path SPECT Room sensor bus, and dual-table Room v4 SQLite storage (`spect_room_records` and `spect_stats`).

```
+---------------------------------------------------------------------------------------------------+
|                                   PRESENTATION & HUD DECK LAYER                                   |
|  MainActivity -> Sy4EegAxiomApp (Adaptive Canonical Split-Screen / Portrait HUD Overlay)          |
|  +-- GlslViewport (3D Hex-Reflex Raymarched Tunnel + Top Telemetry Bar + Continuity HUD)          |
|  +-- Sy4EegBlockMindDashboardScreen (Full-Screen Multi-Band Neural Oscilloscope & Polarity Radar) |
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

## 4. Key Architectural Subsystems

### 4.1 3D Hex-Reflex Raymarched Viewport & Continuity Interlock
- **Aspect-Independent Raymarcher (`GlslViewport.kt`, `AxiomShaders.kt`, `AxiomGlRenderer.kt`)**:
  - Computes dynamic viewport proportions using `min(width, height) * 0.46f` to prevent distortion on 16:9, ultrawide, and foldable displays.
  - Frequency bounds are quantized (`round(u_freq).coerceAtLeast(1f)`) to eliminate discontinuity lines at radial phase boundaries.
- **Tri-Axis Continuity Interlock (`ContinuityInterlockState`)**:
  - Automatically verifies and locks:
    1. **GLSL Uniform Gate**: Ensures `u_speed`, `u_freq`, `u_glow`, and `u_distort` stay within safe numerical bounds.
    2. **SY4-EEG Spectral Gate**: Guarantees finite (non-NaN, non-infinite) power spectral density across all 5 bands.
    3. **SHA-256 Chain Seal**: Cryptographically hashes and links every state transition (`chainTipHash` -> `previousChainHash`).
  - **Auto-Remediation**: Injected out-of-bounds parameters are corrected in the subsequent frame without interrupting the rendering pipeline.

### 4.2 `as[core]` Real-Time RGB Biometric Stream Service
- **Optical Synthesis Channels (`AsCoreRgbBiometricStreamService.kt`)**:
  - **R-Channel (660 nm Hemodynamic Flux)**: Models Beta + Gamma frequency energy and physical motion acceleration.
  - **G-Channel (532 nm Autonomic PPG Pulse)**: Models Alpha + Theta phase coherence and cardiac rhythm dynamics.
  - **B-Channel (470 nm Galvanic Phasic Arousal)**: Models Delta baseline stability and Lyapunov drift resistance.
- **Operating Modes**: Supports `COUPLED_BIOMETRIC`, `CARDIAC_PPG_LOCK`, `CHROMA_STRESS_SWEEP`, and `SPECTRAL_CALIBRATION`.

### 4.3 6-Path SPECT Room Sensor Telemetry Bus
Aggregates subsystem metrics into addressable real-time paths (`PATH-01` to `PATH-06`):

| Path ID | Short Code | Origin | Primary Metric | Dynamic Coupling |
| :--- | :--- | :--- | :--- | :--- |
| **PATH-01** | `SY4-EEG-5B` | `BiometricSensorEngine` 5-Band FFT | Dominant Band Power (`0..100 μV²/Hz`) | `u_freq` / `EEG_COHERENCE` |
| **PATH-02** | `ASCORE-RGB` | `AsCoreRgbBiometricStreamService` | Tri-Chromatic Luma (`20..100 %`) | `RGB_LUMA` / `u_glow` |
| **PATH-03** | `SOMATIC-IMU`| Android `TYPE_ACCELEROMETER` + `GYROSCOPE` | Somatic G-Load (`0.5..3.5 g`) | `IMU_6DOF` / `u_distort` |
| **PATH-04** | `GLSL-RAY` | `GlslViewport` Pipeline | Frame Throughput (`24..60 FPS`) | `u_speed` + `u_freq` |
| **PATH-05** | `UNICRITIC` | `UnicriticState` Attractor Solver | Lyapunov Drift (`-1.50..+1.50 λ`) | `LYAPUNOV_GATE` |
| **PATH-06** | `SPECT-WAL` | `SpectDatabase` v4 SQLite Sink | SHA-256 Verification & WAL Rate | `SHA256_SEAL` |

### 4.4 Dedicated SY4-EEG BlockMind Telemetry Dashboard
Accessible via the **`SY4 [DASH]`** header action chip or the `EEG/RGB` tab:
- **Top KPI Strip**: Real-time readouts for Dominant Frequency, Phase Coherence, Cognitive Load, BlockMind `[+-]` Polarity, and Osa Node Lock.
- **Multi-Band Neural Oscilloscope**: Renders 5 stacked live simulated EEG traces (`γ Gamma`, `β Beta`, `α Alpha`, `θ Theta`, `δ Delta`) alongside an aggregate master wave overlay.
- **Spectral Power Density Sliders**: Interactive controls for band power (`µV²/Hz`) with one-tap presets (`FOCUS [β]`, `CREATIVE [θ]`, `TRUTH [α]`).
- **BlockMind Polarity & Osa A-X Radar**: Polar phase radar visualizing all 10 `Osa A-X-00..09` nodes, polarity flux states (`BLOCK [-]`, `LOCK [+-]`, `UNBLOCK [+]`), and epistula seals.

### 4.5 Local Only Paths Interconnect (`check:pass:true_only`)
- **Zero-Egress Enforcement**: Validates that all target endpoints resolve to loopback or local filesystem enclaves (`127.0.0.1`, `$HOME/.core-relay`, `/sdcard/`, `/data/`, `sqlite:///`, `file:///`).
- **Fail-Closed Architecture**: Any non-local route triggers an immediate egress fault (`injectNonLocalEgressFault`) which halts external transmission until remediated (`remediateToLocalOnlyTrue`).

### 4.6 Unified Global vs. Personal Deploy (`[PaidvsFREE]`) & Anti-Subscription Model
- **Zero Recurring Fees**: Guaranteed by `S:OpenSource(nosubs!-asis)Contract!` (`recurringSubscriptionFeeUsd = $0.00` forever).
- **Decaying Global Bond Model**:
  $$B(t) = B_0 \cdot e^{-\lambda t}, \quad \text{Recurring}(t) = \$0.00 \;\forall t$$
  As time progresses, institutional bond units decay directly into the **`[Personal account:DEV]` Commons Pool**, ensuring developer sovereign accounts remain permanently free and unfettered.
- **`1+ /Own/Dev/Logic_States` Registry**: Persists four verifiable logic vectors:
  - `LOGIC-01`: MiCA Baseline Floor (`MICA_FLOOR_STATE.state`)
  - `LOGIC-02`: No-Subs Decay Engine (`NOSUBS_DECAY_STATE.state`)
  - `LOGIC-03`: Remembered Relay Topology (`REMEMBERED_RELAY_STATE.state`)
  - `LOGIC-04`: Sovereign Developer Account (`PERSONAL_DEV_SOVEREIGN.state`)

---

## 5. Persistence Architecture (Room SQLite v4 WAL)

The on-device database (`SpectDatabase.kt`) operates in Write-Ahead Logging mode:
- **`SpectRoomEntity` (`spect_room_records`)**: Canonical table storing session tags, biometric logs, normalized drift, coherence ratios, dominant frequency bands, and verified SHA-256 digests.
- **`SpectStatEntity` (`spect_stats`)**: High-speed summary table synchronized on every dual-write operation.
- **`AuditLogEntity` (`audit_logs`)**: Cryptographic audit trail storing script verification records and manifest hashes.
- **`ComplianceCheckEntity` (`compliance_checks`)**: MiCA Art. 68 & STEELS statutory compliance registers.

---

## 6. Verification & Automated Test Suite

Run the full local JVM test suite (no emulator required):

```bash
# Execute unit tests
gradle :app:testDebugUnitTest

# Assemble production debug APK
gradle assembleDebug
```

### Covered Test Dimensions (`app/src/test/java/com/example/ExampleUnitTest.kt`):
- Deterministic SHA-256 script cleaning and digest verification.
- Continuity Interlock uniform clamping and angular frequency quantization.
- `as[core]` RGB biometric stream generation and profile switching.
- Local Only Paths Interconnect zero-egress validation, fault injection, and remediation.
- 6-Path SPECT Room sensor telemetry stepping, filtering, and calibration.
- Unified `[PaidvsFREE]` anti-subscription decay metrics and `1+ /Own/Dev/Logic_States` hash sealing.

---

## 7. License & Sovereign Developer Terms

Distributed under the **`S:OpenSource(nosubs!-asis)Contract!`** framework.  
All developer accounts (`[Personal account:DEV]`) retain 100% data sovereignty, zero telemetry reporting, zero subscription fees, and complete local execution rights down to bare-metal hardware.

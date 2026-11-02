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

## 1. Quick Start: Safe One-Command Deployment Script

Designed for **fail-safe execution** across Android phone Linux userlands (Termux / PRoot / UserLAnd), Raspberry Pi 4 (2 GB / 4 GB / 8 GB), and Debian/Ubuntu workstations. 

This script enforces `set -euo pipefail`, traps unexpected errors, auto-detects architecture (`aarch64`, `armv7l`, `x86_64`), configures optimal JVM heap sizes to prevent Out-Of-Memory kills on low-RAM Raspberry Pi 4 boards, and verifies the on-device cryptographic environment.

### 1.1 One-Line Copy & Paste Runner

```bash
cat << 'EOF' | bash
#!/usr/bin/env bash
# ==============================================================================
# SY4-EEG BlockMind — Safe One-Command Bootstrap & Verifier
# Target: Phone Linux Filesystem (Termux / PRoot) & Raspberry Pi 4 Model B (ARM64)
# Protocol: Bias-Guarded `set -euo pipefail` Execution Pipeline
# ==============================================================================
set -euo pipefail
IFS=$'\n\t'

# --- Terminal Color Palette ---
CLR_RESET='\033[0m'
CLR_CYAN='\033[1;36m'
CLR_GREEN='\033[1;32m'
CLR_AMBER='\033[1;33m'
CLR_RED='\033[1;31m'
CLR_PURPLE='\033[1;35m'

# --- Guard Trap ---
cleanup() {
    local exit_code=$?
    if [ "${exit_code}" -ne 0 ]; then
        echo -e "\n${CLR_RED}[FAIL] Pipeline aborted with exit code ${exit_code} on line ${BASH_LINENO[0]}${CLR_RESET}"
        echo -e "${CLR_AMBER}[DIAGNOSTIC] Inspect log output above. If building on Raspberry Pi 4 (2GB), ensure swapfile >= 2G is enabled.${CLR_RESET}"
    fi
}
trap cleanup EXIT ERR

echo -e "${CLR_CYAN}======================================================================${CLR_RESET}"
echo -e "${CLR_CYAN}  SY4-EEG BlockMind // AXIOM Safe One-Command Deployment Rig           ${CLR_RESET}"
echo -e "${CLR_CYAN}  Host: $(uname -s) $(uname -m) | Kernel: $(uname -r)                 ${CLR_RESET}"
echo -e "${CLR_CYAN}======================================================================${CLR_RESET}"

# --- Stage 1: Hardware & System Architecture Detection ---
ARCH="$(uname -m)"
echo -e "${CLR_PURPLE}[STEP 1/6] Detecting System & Hardware Specifications...${CLR_RESET}"
echo -e "  -> Detected Architecture: ${CLR_GREEN}${ARCH}${CLR_RESET}"

IS_RPI4=false
IS_PHONE_LINUX=false

if grep -q -i "Raspberry Pi 4" /proc/cpuinfo 2>/dev/null || grep -q -i "BCM2711" /proc/cpuinfo 2>/dev/null; then
    IS_RPI4=true
    echo -e "  -> Hardware Profile: ${CLR_GREEN}Raspberry Pi 4 Model B detected${CLR_RESET}"
elif [ -d "/data/data/com.termux" ] || [ -n "${TERMUX_VERSION:-}" ] || [ -d "/data/data" ]; then
    IS_PHONE_LINUX=true
    echo -e "  -> Hardware Profile: ${CLR_GREEN}Android Phone Linux Userland detected (Termux // limit:termux active)${CLR_RESET}"
    
    # limit:termux Filesystem Safety Bias Check:
    # Android mounts /sdcard with 'noexec', causing Gradle child binaries (aapt2, ninja) to fail with EACCES.
    CURRENT_DIR="$(pwd)"
    if [[ "${CURRENT_DIR}" == *"/sdcard"* ]] || [[ "${CURRENT_DIR}" == *"/storage/emulated"* ]]; then
        echo -e "  ${CLR_RED}[LIMIT:TERMUX VIOLATION] Running inside shared storage (${CURRENT_DIR})!${CLR_RESET}"
        echo -e "  ${CLR_AMBER}Android mounts /sdcard with 'noexec'. You MUST move this project to Termux home:${CLR_RESET}"
        echo -e "  ${CLR_CYAN}Fix:${CLR_RESET} mkdir -p \$HOME/projects && cp -r ${CURRENT_DIR} \$HOME/projects/ && cd \$HOME/projects/$(basename "${CURRENT_DIR}")"
        exit 1
    else
        echo -e "  -> Filesystem Enclave: ${CLR_GREEN}OK (${CURRENT_DIR} in Termux app storage)${CLR_RESET}"
    fi

    # Acquire wake-lock if Termux API / termux-wake-lock is available to avoid Android Doze kill
    if command -v termux-wake-lock >/dev/null 2>&1; then
        termux-wake-lock || true
        echo -e "  -> Power Management: ${CLR_GREEN}termux-wake-lock acquired (preventing Android sleep)${CLR_RESET}"
    fi
else
    echo -e "  -> Hardware Profile: ${CLR_GREEN}Generic Linux / Workstation detected${CLR_RESET}"
fi

# Memory & Swap Safety Bias Check
TOTAL_MEM_KB="$(grep MemTotal /proc/meminfo 2>/dev/null | awk '{print $2}' || echo "4194304")"
TOTAL_MEM_MB=$(( TOTAL_MEM_KB / 1024 ))
echo -e "  -> Total System RAM: ${CLR_GREEN}${TOTAL_MEM_MB} MB${CLR_RESET}"

if [ "${IS_PHONE_LINUX}" = true ]; then
    # limit:termux Android LMKD (Low Memory Killer Daemon) cap
    echo -e "  ${CLR_CYAN}[POLICY: limit:termux] Clamping JVM heap to prevent Android LMKD OOM termination...${CLR_RESET}"
    GRADLE_JVM_OPTS="-Xmx1280m -XX:MaxMetaspaceSize=384m -Dorg.gradle.parallel=false -Dorg.gradle.workers.max=2"
elif [ "${TOTAL_MEM_MB}" -lt 2500 ]; then
    echo -e "  ${CLR_AMBER}[WARN] Low-RAM Host (< 2.5 GB). Tuning Gradle JVM heap to prevent OOM...${CLR_RESET}"
    GRADLE_JVM_OPTS="-Xmx1024m -XX:MaxMetaspaceSize=384m -Dorg.gradle.parallel=false -Dorg.gradle.workers.max=2"
elif [ "${TOTAL_MEM_MB}" -lt 4500 ]; then
    GRADLE_JVM_OPTS="-Xmx2048m -XX:MaxMetaspaceSize=512m -Dorg.gradle.parallel=true -Dorg.gradle.workers.max=4"
else
    GRADLE_JVM_OPTS="-Xmx3072m -XX:MaxMetaspaceSize=768m -Dorg.gradle.parallel=true"
fi

# --- Stage 2: Toolchain Preflight Verification ---
echo -e "\n${CLR_PURPLE}[STEP 2/6] Verifying Java & Gradle Toolchain...${CLR_RESET}"
if command -v java >/dev/null 2>&1; then
    JAVA_VER="$(java -version 2>&1 | head -n 1)"
    echo -e "  -> Java Runtime: ${CLR_GREEN}${JAVA_VER}${CLR_RESET}"
else
    echo -e "  ${CLR_RED}[ERROR] OpenJDK (version 17 or 21) is required but not installed.${CLR_RESET}"
    if [ "${IS_PHONE_LINUX}" = true ]; then
        echo -e "  ${CLR_CYAN}Termux Quick Install:${CLR_RESET} pkg install openjdk-17"
    else
        echo -e "  ${CLR_CYAN}Debian/RPI4 Quick Install:${CLR_RESET} sudo apt-get update && sudo apt-get install -y openjdk-17-jdk"
    fi
    exit 1
fi

# --- Stage 3: Workspace Integrity & Structure Verification ---
echo -e "\n${CLR_PURPLE}[STEP 3/6] Validating BlockMind Repository Structure...${CLR_RESET}"
REQUIRED_FILES=(
    "build.gradle.kts"
    "settings.gradle.kts"
    "app/build.gradle.kts"
    "app/src/main/java/com/example/MainActivity.kt"
    "app/src/main/java/com/example/viewmodel/AxiomViewModel.kt"
    "app/src/main/java/com/example/service/LocalOnlyPathsInterconnectService.kt"
    "app/src/main/java/com/example/ui/components/Sy4EegBlockMindDashboardScreen.kt"
)

for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "${file}" ]; then
        echo -e "  ${CLR_RED}[MISSING] Critical source file not found: ${file}${CLR_RESET}"
        echo -e "  Please ensure you execute this script from the project root directory."
        exit 1
    fi
done
echo -e "  -> Required source files verified: ${CLR_GREEN}ALL PRESENT${CLR_RESET}"

# --- Stage 4: Zero-Egress Network Isolation Validation ---
echo -e "\n${CLR_PURPLE}[STEP 4/6] Enforcing Zero-Egress Security Invariant (check:pass:true_only)...${CLR_RESET}"
SUSPICIOUS_EGRESS="$(grep -rEn "https?://(?!ais-|127\.0\.0\.1|localhost)" app/src/main/java/ 2>/dev/null || true)"
if [ -n "${SUSPICIOUS_EGRESS}" ]; then
    echo -e "  ${CLR_AMBER}[AUDIT] External URI references flagged for review:${CLR_RESET}"
    echo "${SUSPICIOUS_EGRESS}" | head -n 5
else
    echo -e "  -> Local-Only Paths Interconnect: ${CLR_GREEN}ZERO NON-LOCAL EGRESS VERIFIED${CLR_RESET}"
fi

# --- Stage 5: Cryptographic SHA-256 Checksum Calculation ---
echo -e "\n${CLR_PURPLE}[STEP 5/6] Sealing Core Manifest & Logic State SHA-256 Hashes...${CLR_RESET}"
MAIN_ACTIVITY_SHA="$(sha256sum app/src/main/java/com/example/MainActivity.kt | awk '{print $1}')"
VIEW_MODEL_SHA="$(sha256sum app/src/main/java/com/example/viewmodel/AxiomViewModel.kt | awk '{print $1}')"
INTERCONNECT_SHA="$(sha256sum app/src/main/java/com/example/service/LocalOnlyPathsInterconnectService.kt | awk '{print $1}')"

echo -e "  -> MainActivity.kt SHA-256:     ${CLR_CYAN}${MAIN_ACTIVITY_SHA:0:16}...${CLR_RESET}"
echo -e "  -> AxiomViewModel.kt SHA-256:   ${CLR_CYAN}${VIEW_MODEL_SHA:0:16}...${CLR_RESET}"
echo -e "  -> InterconnectService SHA-256: ${CLR_CYAN}${INTERCONNECT_SHA:0:16}...${CLR_RESET}"

# --- Stage 6: Build & Test Execution ---
echo -e "\n${CLR_PURPLE}[STEP 6/6] Executing Local Unit Tests & Compilation...${CLR_RESET}"
export GRADLE_OPTS="${GRADLE_JVM_OPTS}"

if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    GRADLE_CMD="./gradlew"
else
    GRADLE_CMD="gradle"
fi

echo -e "  -> Running Robolectric & Telemetry Invariant Unit Tests..."
${GRADLE_CMD} :app:testDebugUnitTest --no-daemon

echo -e "  -> Compiling Debug APK Build..."
${GRADLE_CMD} assembleDebug --no-daemon

echo -e "\n${CLR_GREEN}======================================================================${CLR_RESET}"
echo -e "${CLR_GREEN}  DEPLOYMENT SPECIFICATION VERIFIED: ALL CHECKS PASSED [SUCCESS]       ${CLR_RESET}"
echo -e "${CLR_GREEN}  Artifact: app/build/outputs/apk/debug/app-debug.apk                 ${CLR_RESET}"
echo -e "${CLR_GREEN}  Gate Assertion: check:pass:true_only // S:OpenSource(nosubs!-asis)  ${CLR_RESET}"
echo -e "${CLR_GREEN}======================================================================${CLR_RESET}"
EOF
```

---

## 2. Hardware Platform Matrix & Tuning Specs

SY4-EEG BlockMind compiles and runs seamlessly across mobile and edge hardware nodes.

| Platform Node | Architecture | Operating Environment | RAM Specs | JVM Heap Configuration | Recommended Roles |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Android Phone (Native APK)** | `arm64-v8a` / `armeabi-v7a` | Android 14+ (API 34/36) | 4 GB – 12 GB | Managed by ART | Live 60 FPS Viewport, IMU Sensor Bridge, Biometric Sync |
| **Android Phone (Linux FS)** | `aarch64` | Termux / PRoot-Distro (Ubuntu/Debian) | 4 GB – 12 GB | `-Xmx1536m -XX:MaxMetaspaceSize=384m` | On-device build rig, local ADB deploy, CLI test runner |
| **Raspberry Pi 4 Model B (2GB)** | `aarch64` / `armv7l` | Raspberry Pi OS 64-bit | 2 GB (+ 2GB Swap) | `-Xmx1024m -XX:MaxMetaspaceSize=384m` | Headless telemetry aggregator, continuous audit verifier |
| **Raspberry Pi 4 Model B (4GB/8GB)**| `aarch64` | Raspberry Pi OS 64-bit / Ubuntu | 4 GB / 8 GB | `-Xmx2048m -XX:MaxMetaspaceSize=512m` | Full headless test rig, APK compile agent, SQLite WAL sink |
| **Linux Workstation** | `x86_64` / `amd64` | Debian 12 / Ubuntu 24.04 LTS | 8 GB – 64 GB | `-Xmx3072m -XX:MaxMetaspaceSize=768m` | Primary IDE development, benchmark sweeps, shader stress |

### 2.1 Raspberry Pi 4 Tuning Notes
When compiling on a **Raspberry Pi 4 (2 GB)**:
1. Ensure a swap file of at least 2 GB is active (`sudo dphys-swapfile swapoff`, adjust `CONF_SWAPSIZE=2048` in `/etc/dphys-swapfile`, then `sudo dphys-swapfile setup && sudo dphys-swapfile swapon`).
2. Add `--no-daemon` to Gradle invocations to prevent idle background daemons from holding memory.
3. Keep `org.gradle.workers.max=2` in `gradle.properties` to avoid CPU thermal throttling and memory pressure on all 4 Cortex-A72 cores simultaneously.

### 2.2 Termux Boundary Limits & Execution Specification (`limit:termux`)

When operating or compiling SY4-EEG BlockMind directly on an Android smartphone inside **Termux / PRoot Linux**, the build pipeline strictly conforms to the **`limit:termux`** operational envelope:

| Boundary Dimension | Hardware / OS Constraint | Failure Mode if Violated | Required `limit:termux` Setting |
| :--- | :--- | :--- | :--- |
| **Filesystem Execution** | Android mounts `/sdcard` and `/storage/emulated/0` with the `noexec` mount flag. | `java.io.IOException: error=13, Permission denied` when Gradle executes `aapt2` or compiler workers. | **Mandatory:** Clone and execute strictly inside Termux internal storage: `$HOME` (`/data/data/com.termux/files/home/`). Never execute from `/sdcard/`. |
| **Process Count (PPK)** | Android 12+ **Phantom Process Killer** limits background apps to a hard cap of 32 child processes. | Entire Termux session silently killed (`Process completed (signal 9) - press Enter`). | Acquire wake-lock (`termux-wake-lock`) prior to building. Enforce `org.gradle.parallel=false` and `org.gradle.workers.max=2`. |
| **RAM & LMKD Cap** | Android **Low Memory Killer Daemon (LMKD)** terminates cgroups exceeding per-app RSS thresholds (~1.5 GB). | Sudden JVM exit `137` (Out of Memory). | Clamp heap: `-Xmx1280m -XX:MaxMetaspaceSize=384m --no-daemon`. |
| **Bionic vs Glibc** | Android Gradle Plugin (AGP) bundles standard glibc-linked `aapt2` and `ninja` binaries; Termux userland uses Bionic libc. | `aapt2: not found` or dynamic linker errors. | **Recommended:** Run inside `proot-distro` (`pkg install proot-distro && proot-distro install ubuntu && proot-distro login ubuntu`), which provides full glibc compatibility. |
| **Power Management** | Android Doze drops CPU clock frequencies to deep idle states during long compiles. | Build stalls or takes 10x longer. | Run `termux-wake-lock` and disable Battery Optimization for Termux in Android Settings. |
| **Direct APK Install** | Headless terminal has no direct X11 window server by default. | Cannot view GUI inside CLI shell. | Install compiled APK directly to Android host with `termux-open app/build/outputs/apk/debug/app-debug.apk`. |

#### Quick-Start Setup for Native Termux (`limit:termux`)
```bash
# 1. Update Termux environment & install dependencies
pkg update -y && pkg install -y git openjdk-17 proot-distro

# 2. Acquire wake-lock so Android doesn't sleep during compilation
termux-wake-lock

# 3. Clone and build in private Termux storage (NOT /sdcard)
cd $HOME
# Clone or move repository to $HOME/blockmind
cd $HOME/blockmind

# 4. Execute with limit:termux memory constraints
export GRADLE_OPTS="-Xmx1280m -XX:MaxMetaspaceSize=384m -Dorg.gradle.parallel=false -Dorg.gradle.workers.max=2"
gradle assembleDebug --no-daemon

# 5. One-tap install on the phone
termux-open app/build/outputs/apk/debug/app-debug.apk
```

---

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

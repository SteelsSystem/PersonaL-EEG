Security Policy & Cryptographic Threat Model (SECURITY.md)

Project: Axiom Hex-Reflex // SY4-EEG BlockMind & MetaEye-Void Unified Stack
Security Protocol: AX-IS-T // [TREAT SECURITY THREAT:REDLINE]
Gate Assertion: check:pass:true_only
Contractual Regime: S:OpenSource(nosubs!-asis)Contract! (SPDX-License-Identifier: MIT OR Apache-2.0)
1. Supported Versions & Target Runtimes
Stack / Runtime Target	Version / API Level	Security & Self-Heal Status
Android SY4-EEG BlockMind APK	Android 7.0+ (minSdk 24 – targetSdk 36)	Active (check:pass:true_only + Room SHA-256 Audit)
MetaEye-Void Rust Core & FFI Stubs	Rust 2021 Edition (1.75+)	Active (ZeroizeOnDrop + Fallible mlock + Offline SQLx)
limit:termux (Android Bionic Userland)	Termux 0.118+ (Native Bionic & PRoot)	Active (content://com.termux.files + Flags 268435457)
Standard Linux & Raspberry Pi 4 (aarch64/x86_64)	Kernel 5.10+ (glibc / musl)	Active (Full mlockall + Multi-Core Deterministic Build)
2. Core Security Invariants
2.1 Zero External Egress (check:pass:true_only)

All telemetry, shader raymarching, script verification, and Room SQLite persistence operate across strictly local paths:

    6 Primary Local Destinations (DEST-1 .. DEST-6):

        local://osa-a-x/intake

        local://as-core/rgb-stream

        local://glsl/hex-reflex

        local://room/spect_audit.db

        local://sha256/manifest

        local://apk/staging

    4 Remembered Developer Logic States (1+ /Own/Dev/Logic_States -> L-01 .. L-04):

        Every hop is validated by LocalOnlyPathsInterconnectEngine.isPathStrictlyLocal(...).

        Any injection of http://, https://, ws://, wss://, or ftp:// into the local interconnect immediately triggers check:pass:false [BLOCKED] until remediated.

2.2 Cryptographic Memory Hygiene (MOD-04)

    Heap-Pinned Secret Storage: Session keys and sensitive 32-byte buffers are allocated inside Box<[u8; 32]> derived with #[derive(Zeroize, ZeroizeOnDrop)] so struct moves never leave residual copies on the call stack.

    Compiler Fence Hardening: Every zeroization pass is followed by core::sync::atomic::compiler_fence(Ordering::SeqCst) to prevent dead-store elimination by LLVM.

    Fallible Advisory mlock on Mobile vs. Full mlockall on Linux:

        On standard privileged Linux hosts, mlockall(MCL_CURRENT | MCL_FUTURE) locks pages in physical RAM to prevent swap leakage.

        On unprivileged Android / Termux UIDs (RLIMIT_MEMLOCK ~64 KB without CAP_IPC_LOCK), libc::mlock is executed as a fallible advisory call so the process never panics or aborts while still zeroizing deterministically on drop.

2.3 Covert Gates SAT & AppSAT Whole-Netlist Resistance (MOD-05)

    Rather than isolating protection in a detached point-function sub-circuit (which is vulnerable to SAT and AppSAT oracle pruning), SY4-EEG BlockMind couples the 10-node Osa-A-X-00..09 matrix, as[core] RGB biometric coherence, and the 64-character SHA-256 continuity chain across the entire render and state pipeline.

    Safety Gate: if (safe) { miscalculus = 0.000; sealed.has = true; }.

2.4 Android & Termux Sandbox Security (MOD-01 & MOD-02)

    No Raw file:// Exposure: On Android 7.0+ (API 24+), exposing file:// URIs across package boundaries is prohibited (FileUriExposedException). fixsnake-v2.sh stages APKs into $HOME/storage/shared/Download, translates paths to content://com.termux.files/..., and dispatches via termux-am using decimal flag bitmask 268435457 (0x10000001 = FLAG_ACTIVITY_NEW_TASK | FLAG_GRANT_READ_URI_PERMISSION).

    Executable TMPDIR & U+2044 Symlink Bridge: Temporary build artifacts and proc-macro shared objects are isolated in $HOME/.cargo_tmp (chmod 700, umask 077), and Unicode fraction-slash paths (MetaEye⁄Void, U+2044) are bridged via ~/metaeye_work to prevent shell word-splitting vulnerabilities.

    Zero Hardcoded Secrets: API keys are never hardcoded in source files or committed to version control. Optional cloud AI studio features use the AI Studio Secrets panel (BuildConfig) and automatically fall back to deterministic local 512x512 procedural synthesis under RULE: IF FAILLOAD POSSIBLE.

3. Autonomous Threat Detection & Self-Healing (AX-IS-T)

When the Unicritic Multi-Axis Consensus Engine (GLSL Axis, SY4-EEG Axis, Osa-A-X Axis) detects critical drift or an out-of-bounds shader uniform:

    AX-IS-T // [TREAT SECURITY THREAT:REDLINE] activates visual and telemetry alerts.

    AS TO GLSL: TRUE // IF FALSE -> DENY clamps raymarching steps and uniform bounds to safe sanctuary defaults.

    Tri-Axis Continuity Interlock records the remediation event and parent-child SHA-256 digest in the local Room SQLite database (spect_audit.db).

4. Alignment with Terms & Conditions (TERMS_AND_CONDITIONS.md)

All security guarantees operate under S:OpenSource(nosubs!-asis)Contract!:

    Non-Custodial Safe Harbor: Qualifies as local non-custodial technical tooling under Regulation (EU) 2023/1114 (MiCA) Article 2 & Recital 22.

    Operational Resilience: Autonomous local fallback (RULE: IF FAILLOAD POSSIBLE) and local SHA-256 audit trails align with Regulation (EU) 2022/2554 (DORA) Article 11.

    Non-Clinical Disclaimer: All SY4-EEG and as[core] RGB biometric streams are strictly for software engineering, cryptographic research, and HUD telemetry simulation ("AS IS").

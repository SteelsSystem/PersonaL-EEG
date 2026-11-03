

---

## 8. Design Tactics: `Metallurgia:THEME(STEEL ALLOY ANTIMAG POSITION)` & Confirmed 2FA Device Deploy Output Logic

### 8.1 `Metallurgia:THEME(STEEL ALLOY ANTIMAG POSITION)` Alloy Presets

| Preset ID | Alloy Designation | Relative Permeability ($\mu_r$) | `u_antiMag` Target | `u_magPressure` Target | Anti-Mag Position Vector |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`316L AUSTENITE // ANTIMAG POS`** | AISI 316L Low-Mu Austenitic Steel (`Cr17-Ni12-Mo2`) | `1.003` | `1.45` | `0.65` | `POS[0.00, 0.00, +1.00] // ZERO-FLUX NULL` |
| **`MU-METAL // FARADAY CAGE`** | ASTM A753 Alloy 4 (`Ni77-Fe16-Cu5-Cr2`) | `1.001` | `1.75` | `0.42` | `POS[-0.12, +0.08, +1.00] // SHUNT VECTOR` |
| **`INCONEL 718 // CRYO ANTIMAG`** | UNS N07718 Superalloy (`Ni53-Cr19-Nb5-Mo3`) | `1.001` | `1.60` | `0.55` | `POS[+0.04, -0.04, +1.00] // CRYO-ISOLATION` |
| **`NITRONIC 50 // ARMOR ANTIMAG`** | XM-19 Nitrogen-Strengthened Austenitic (`Cr22-Ni13-Mn5`) | `1.002` | `1.30` | `0.78` | `POS[0.00, +0.02, +1.00] // TORSION LOCK` |

### 8.2 Allowed to Compare 2FA — Confirmed 10-Step Device Deployment Output Logic

When deploying on your device, the installer compares **Factor 1 (Manifest & `S:OpenSource(nosubs!-asis)Contract!` SHA-256 Digest)** against **Factor 2 (Target Runtime Environment + `Metallurgia:THEME(STEEL ALLOY ANTIMAG POSITION)` Attestation)** and prints the following deterministic 10-step terminal trace:

```text
[1/10] Runtime Environment Detected: TERMUX_BIONIC_NATIVE (or LINUX_RPI4_ARM64 / LINUX_STANDARD_HOST)
[2/10] ASCII Symlink Bridge (~/metaeye_work) & Executable TMPDIR (~/.cargo_tmp) Ready
[3/10] Applied Build Profile for TERMUX_BIONIC_NATIVE (CARGO_BUILD_JOBS=1)
[4/10] Provisioned Pure-Rust audiopus_sys C-ABI Stub (MOD-03)
[5/10] Generated fixsnake-v2.sh Content URI Dispatcher (flags=268435457 / 0x10000001)
[6/10] Auditing Local-Only Paths (1+ /Own/Dev/Logic_States)... [0 Bytes Egress]
[7/10] Verifying Contract: S:OpenSource(nosubs!-asis)Contract! (Recurring = $0.00 USD)...
[8/10] Executing 2FA Comparison & Metallurgia Anti-Mag Gate (2FA-<PAIRING_TOKEN>)...
       ├─ Design Tactic               : Metallurgia:THEME(STEEL ALLOY ANTIMAG POSITION)
       ├─ Steel Alloy & Anti-Mag Pos  : 316L AUSTENITE // ANTIMAG POS (mu_r=1.003 • POS[0.00, 0.00, +1.00] // ZERO-FLUX NULL)
       ├─ Factor 1 (Manifest SHA-256) : <64-hex Factor 1 prefix>...
       ├─ Factor 2 (Device+Alloy 2FA) : <64-hex Factor 2 prefix>...
       └─ Allowed to Compare 2FA      : TRUE [MATCH CONFIRMED]
[9/10] Sealing SHA256SUMS.manifest -> <64-hex composite seal>...
[10/10] check:pass:true_only // All 10 Pipeline Steps Verified (TERMUX_BIONIC_NATIVE)
```

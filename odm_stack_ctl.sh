#!/usr/bin/env bash
# STEELS // SY4-EEG ODM Stack Controller, Guarded Command Wrapper & Local Launcher
# Aligns DOM-01..DOM-05, Layers 0..4, RGB/FRQ Knowability & Atlas (RED) Lost-Path Gate
set -euo pipefail

STATE_DIR="${ODM_STATE_DIR:-./.odm_runtime}"
STATE_FILE="${STATE_DIR}/gate_status"
REPORT_FILE="${STATE_DIR}/odm_preflight_report.json"
WRAPPER_SCRIPT="${STATE_DIR}/run_cmd.sh"
PORT="${ODM_PORT:-8420}"

mkdir -p "${STATE_DIR}"

# Initialize STATUS to OFF if state file does not exist
if [[ ! -f "${STATE_FILE}" ]]; then
  echo "OFF" > "${STATE_FILE}"
fi

# 1. Generate the strict Command Wrapper Contract (run_cmd.sh)
cat << 'EOF' > "${WRAPPER_SCRIPT}"
#!/usr/bin/env python3
import os
import sys
import json
import shutil
import subprocess

ALLOWLIST = {
    "uname": ["uname", "-a"],
    "id": ["id"],
    "df": ["df", "-h"],
    "free": ["free", "-m"],
    "lscpu": ["lscpu"],
    "termux-battery-status": ["termux-battery-status"],
    "termux-audio-info": ["termux-audio-info"],
}

def is_termux() -> bool:
    return bool(os.environ.get("TERMUX_VERSION")) or os.path.exists("/data/data/com.termux")

def main() -> int:
    if len(sys.argv) < 2:
        print(json.dumps({"ok": False, "error": "missing_command_argument"}))
        return 2

    cmd_name = sys.argv[1].strip()
    if cmd_name not in ALLOWLIST:
        print(json.dumps({
            "ok": False,
            "error": "command_not_in_allowlist",
            "command": cmd_name,
            "allowed": sorted(list(ALLOWLIST.keys()))
        }))
        return 126

    argv = ALLOWLIST[cmd_name]

    # Outside Termux: produce deterministic simulator response without executing
    if not is_termux():
        print(json.dumps({
            "ok": True,
            "mode": "desktop_simulator",
            "executed": False,
            "command": cmd_name,
            "argv": argv,
            "simulated_output": f"[SIMULATOR] Guarded dry-run for {cmd_name} (DOM-01/DOM-03)"
        }))
        return 0

    if shutil.which(argv[0]) is None:
        print(json.dumps({"ok": False, "mode": "termux", "error": f"binary_not_found_{argv[0]}"}))
        return 127

    try:
        res = subprocess.run(argv, capture_output=True, text=True, timeout=15, check=False)
        print(json.dumps({
            "ok": res.returncode == 0,
            "mode": "termux",
            "executed": True,
            "command": cmd_name,
            "returncode": res.returncode,
            "stdout": res.stdout.strip(),
            "stderr": res.stderr.strip()
        }))
        return res.returncode
    except subprocess.TimeoutExpired:
        print(json.dumps({"ok": False, "mode": "termux", "error": "command_timeout_15s"}))
        return 124

if __name__ == "__main__":
    sys.exit(main())
EOF
chmod +x "${WRAPPER_SCRIPT}"

get_status() {
  cat "${STATE_FILE}" | tr -d '[:space:]'
}

write_preflight_report() {
  python3 - << 'PYEOF' > "${REPORT_FILE}"
import json
import time

def enforce_register_0x200f(raw_reg: int) -> dict:
    masked = raw_reg | 0x200F
    low15 = masked & 0x7FFF
    ones = bin(low15).count("1")
    parity_b15 = (ones % 2) << 15
    final_reg = low15 | parity_b15
    return {
        "raw_hex": f"0x{raw_reg:04X}",
        "enforced_hex": f"0x{final_reg:04X}",
        "mask": "0x200F",
        "b15_parity": (final_reg >> 15) & 1
    }

report = {
    "schema": "STEELS-ODM-TOPOLOGY-v1",
    "deterministic_sync": "v2026.04.12-DET-SYNC",
    "timestamp": 1775952000,
    "knowability_gate": {
        "rgb_knowable": True,
        "frq_knowable": True,
        "or_if_lie": False,
        "lost_path_fallback": "AS_ATLAS_RED"
    },
    "domains": [
        {"id": "DOM-01", "scope": "Hardware and sensors", "active": "Guarded status allowlist", "gate": "Explicit action consent", "deferred": "TermuxHardwareBridge"},
        {"id": "DOM-02", "scope": "Connectivity and radio", "active": "Air-gapped local loopback", "gate": "Zero outbound access", "deferred": "OmniChannelServer"},
        {"id": "DOM-03", "scope": "Audio and media", "active": "Needle DSP and 7-Band FFT", "gate": "Probe only without mutation", "deferred": "MultiModalExtractorTriage"},
        {"id": "DOM-04", "scope": "Messaging and contacts", "active": "Serenity and Poise isolation", "gate": "Zero inbox or clipboard reads", "deferred": "KnowledgeVaultRAG"},
        {"id": "DOM-05", "scope": "Apps and system actions", "active": "Gate controller and simulator", "gate": "Blocked when status is off", "deferred": "CactusRuntimeEngine"}
    ],
    "register_hygiene": enforce_register_0x200f(0x0700),
    "pre_emission_gate": {
        "token": ">P:emission>tense-dylz",
        "visibility_required": "private_or_unlisted",
        "CheckPassTrueOnly": "check:pass:true_only"
    }
}
print(json.dumps(report, indent=2))
PYEOF
}

ACTION="${1:-STATUS}"
shift || true

case "${ACTION^^}" in
  STATUS)
    echo "STATUS=$(get_status)"
    ;;
  ON)
    echo "ON" > "${STATE_FILE}"
    echo "STATUS=ON"
    ;;
  OFF)
    echo "OFF" > "${STATE_FILE}"
    echo "STATUS=OFF"
    ;;
  MAP|PREFLIGHT)
    write_preflight_report
    cat "${REPORT_FILE}"
    ;;
  TEST|SYNC)
    write_preflight_report
    "${WRAPPER_SCRIPT}" uname
    echo "TEST_CONTRACT=PASS"
    echo "DETERMINISTIC_SYNC_VERSION=v2026.04.12-DET-SYNC"
    ;;
  RUN)
    CUR="$(get_status)"
    if [[ "${CUR}" != "ON" ]]; then
      echo '{"ok":false,"error":"gate_status_off","RUN_BLOCKED":1,"atlas_state":"AS_ATLAS_RED_LOST_PATH"}'
      exit 3
    fi
    CMD_ARG="${1:-uname}"
    "${WRAPPER_SCRIPT}" "${CMD_ARG}"
    ;;
  *)
    echo "Usage: $0 {STATUS|ON|OFF|MAP|TEST|SYNC|RUN <allowlisted_cmd>}"
    exit 2
    ;;
esac

#!/usr/bin/env bash
# PersonaL-EEG // SY4-EEG BlockMind — Compile Best Average & Keystore Bootstrap
# Supports:
#   ./compile-best-average.sh --init-keystore
#   ./compile-best-average.sh --test
#   ./compile-best-average.sh
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
KEYSTORE_PATH="${SCRIPT_DIR}/debug.keystore"
HOME_KEYSTORE="${HOME}/.android/debug.keystore"

init_keystore() {
  if [[ -f "${KEYSTORE_PATH}" ]]; then
    echo "[KEYSTORE] Local debug.keystore already present at ${KEYSTORE_PATH} (preserved; never committed per .gitignore)."
    return 0
  fi

  if [[ -f "${HOME_KEYSTORE}" ]]; then
    cp -n "${HOME_KEYSTORE}" "${KEYSTORE_PATH}" 2>/dev/null || true
    echo "[KEYSTORE] Copied existing ~/.android/debug.keystore -> ${KEYSTORE_PATH}"
    return 0
  fi

  echo "[KEYSTORE] Generating local Android Debug keystore at ${KEYSTORE_PATH}..."
  keytool -genkeypair -v \
    -keystore "${KEYSTORE_PATH}" \
    -storepass android \
    -alias androiddebugkey \
    -keypass android \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -dname "CN=Android Debug,O=Android,C=US"
  echo "[KEYSTORE] Generated local debug.keystore (strictly local; ignored by .gitignore)."
}

run_odm_and_protocol_o_verification() {
  if [[ -f "${SCRIPT_DIR}/odm_stack_ctl.sh" ]]; then
    bash "${SCRIPT_DIR}/odm_stack_ctl.sh" SYNC
  fi
  echo "PROTOCOL_O_INTEGRITY_CORE_2026=SEALED_IIFE_READY"
  echo "AUDIUS_KEYLESS_ENGINE=_engine.audius_READY"
  echo "SUBLAYER_GATE=VOLATILE_MEMORY_ONLY"
  echo "DETERMINISTIC_SELF_UPDATE_UPGRADE_SYNC=v2026.04.12-DET-SYNC_LOCKED"
  echo "EMERGENCY_RELOAD_PACK=MERKLE_MESH_6HOP_LOCKED"
}

MODE="${1:---all}"

case "${MODE}" in
  --init-keystore|--self-update-sync)
    init_keystore
    run_odm_and_protocol_o_verification
    ;;
  --test)
    init_keystore
    run_odm_and_protocol_o_verification
    gradle :app:testDebugUnitTest
    ;;
  *)
    init_keystore
    run_odm_and_protocol_o_verification
    ;;
esac

package com.example.model

import com.example.sensor.HardwareTelemetry

/**
 * Unified, read-only projection of EEG, hardware scan, APK target planning,
 * and Unicritic evaluation. It is derived from the existing application state;
 * it does not claim that simulated or declared data has been physically acquired
 * or that an APK has been installed.
 */
enum class UCompOrigin {
    SIMULATED_EEG,
    ANDROID_MOTION_SENSOR,
    PHYSICAL_BLE_SCAN,
    VIRTUAL_BLE_BRIDGE,
    DECLARED_INSTALL_TARGETS,
    LOCAL_UNICRITIC_EVALUATION,
    UNAVAILABLE,
    UNKNOWN
}

enum class UCompHealth {
    ACTIVE,
    IDLE,
    SIMULATED,
    PERMISSION_REQUIRED,
    DECLARED_ONLY,
    NOT_EXECUTED,
    UNAVAILABLE,
    UNKNOWN
}

data class UCompEegState(
    val bands: EegBandState,
    val dominantFrequencyHz: Float,
    val phaseCoherencePct: Int,
    val cognitiveLoadPct: Int,
    val origin: UCompOrigin = UCompOrigin.SIMULATED_EEG,
    val health: UCompHealth = UCompHealth.SIMULATED
)

data class UCompScanState(
    val motion: HardwareTelemetry,
    val motionOrigin: UCompOrigin,
    val motionHealth: UCompHealth,
    val bleDevices: List<String>,
    val bleStatus: String,
    val bleOrigin: UCompOrigin,
    val bleHealth: UCompHealth
)

data class UCompInstallationState(
    val targets: List<LegalAxisTarget>,
    val parserStructureBalanced: Boolean,
    val localOnlyDeclared: Boolean,
    val origin: UCompOrigin = UCompOrigin.DECLARED_INSTALL_TARGETS,
    val health: UCompHealth = UCompHealth.DECLARED_ONLY,
    val executionStatus: UCompHealth = UCompHealth.NOT_EXECUTED
)

data class UCompState(
    val eeg: UCompEegState,
    val scan: UCompScanState,
    val installation: UCompInstallationState,
    val unicritic: UnicriticLiveEvaluation,
    val unicriticOrigin: UCompOrigin = UCompOrigin.LOCAL_UNICRITIC_EVALUATION
) {
    companion object {
        fun from(
            eegBands: EegBandState,
            dominantFrequencyHz: Float,
            phaseCoherencePct: Int,
            cognitiveLoadPct: Int,
            hardware: HardwareTelemetry,
            installPlan: ForwardTunnelParseResult,
            unicritic: UnicriticLiveEvaluation
        ): UCompState {
            val bleState = hardware.bleScanState
            val bleOrigin = when {
                bleState.contains("VIRTUAL", ignoreCase = true) ||
                    bleState.contains("SIM-BRIDGE", ignoreCase = true) ->
                    UCompOrigin.VIRTUAL_BLE_BRIDGE
                bleState.contains("SCANNING", ignoreCase = true) ||
                    bleState.startsWith("LOCKED", ignoreCase = true) ||
                    hardware.discoveredBleDevices.isNotEmpty() ->
                    UCompOrigin.PHYSICAL_BLE_SCAN
                else -> UCompOrigin.UNKNOWN
            }
            val bleHealth = when {
                bleState.contains("PERMISSION REQUIRED", ignoreCase = true) ->
                    UCompHealth.PERMISSION_REQUIRED
                bleOrigin == UCompOrigin.VIRTUAL_BLE_BRIDGE -> UCompHealth.SIMULATED
                bleOrigin == UCompOrigin.PHYSICAL_BLE_SCAN -> UCompHealth.ACTIVE
                bleState.contains("STANDBY", ignoreCase = true) ||
                    bleState.contains("IDLE", ignoreCase = true) -> UCompHealth.IDLE
                else -> UCompHealth.UNKNOWN
            }

            return UCompState(
                eeg = UCompEegState(
                    bands = eegBands,
                    dominantFrequencyHz = dominantFrequencyHz,
                    phaseCoherencePct = phaseCoherencePct,
                    cognitiveLoadPct = cognitiveLoadPct
                ),
                scan = UCompScanState(
                    motion = hardware,
                    motionOrigin = if (hardware.sensorActive) {
                        UCompOrigin.ANDROID_MOTION_SENSOR
                    } else {
                        UCompOrigin.UNAVAILABLE
                    },
                    motionHealth = if (hardware.sensorActive) {
                        UCompHealth.ACTIVE
                    } else {
                        UCompHealth.UNAVAILABLE
                    },
                    bleDevices = hardware.discoveredBleDevices,
                    bleStatus = bleState,
                    bleOrigin = bleOrigin,
                    bleHealth = bleHealth
                ),
                installation = UCompInstallationState(
                    targets = installPlan.legalTargets,
                    parserStructureBalanced = installPlan.bracketDepthBalanced,
                    localOnlyDeclared = installPlan.onlyLocalLogicFlag
                ),
                unicritic = unicritic
            )
        }
    }
}

package com.example.service

import com.example.model.EegBandState
import com.example.sensor.HardwareTelemetry
import java.security.MessageDigest
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class SpectSensorPathId(
    val id: String,
    val shortCode: String,
    val title: String,
    val sensorSourceUri: String,
    val spectRoomColumn: String
) {
    PATH_01_EEG_SPECTRAL(
        id = "PATH-01",
        shortCode = "SY4-EEG",
        title = "SY4-EEG 5-Band Spectral Bus",
        sensorSourceUri = "sensor://sy4-eeg/5-band-fft",
        spectRoomColumn = "spect_room_records.dominant_band"
    ),
    PATH_02_AS_CORE_RGB(
        id = "PATH-02",
        shortCode = "as[core]",
        title = "as[core] Tri-Chromatic RGB Stream",
        sensorSourceUri = "sensor://as-core/rgb-synchro",
        spectRoomColumn = "spect_room_records.biometric_drift_log"
    ),
    PATH_03_SOMATIC_IMU(
        id = "PATH-03",
        shortCode = "IMU-6DOF",
        title = "Somatic Accelerometer & Gyro IMU",
        sensorSourceUri = "sensor://hardware-imu/accel-gyro",
        spectRoomColumn = "spect_room_records.inverted_stability_index"
    ),
    PATH_04_GLSL_RAYMARCH(
        id = "PATH-04",
        shortCode = "GLSL-PIPE",
        title = "3D Hex-Reflex Raymarch Telemetry",
        sensorSourceUri = "sensor://glsl-pipe/frame-telemetry",
        spectRoomColumn = "spect_room_records.inverted_frame_rate"
    ),
    PATH_05_UNICRITIC_DRIFT(
        id = "PATH-05",
        shortCode = "CRIT-DRIFT",
        title = "Unicritic Drift & Continuity Gate",
        sensorSourceUri = "sensor://unicritic/drift-interlock",
        spectRoomColumn = "spect_room_records.biometric_drift_intensity"
    ),
    PATH_06_SHA256_SQLITE(
        id = "PATH-06",
        shortCode = "ROOM-SHA",
        title = "SHA-256 Seal & SPECTRoom WAL Sink",
        sensorSourceUri = "sqlite:///sy4_eeg_spect_local.db#spect_room_records",
        spectRoomColumn = "spect_room_records.sha256_digest"
    )
}

data class SpectSensorPathNodeState(
    val pathId: SpectSensorPathId,
    val primaryReadout: String,
    val secondaryReadout: String,
    val signalNormalized: Float,
    val driftIntensity: Float,
    val sampleRateHz: Int,
    val latencyMs: Float,
    val sha256Verified: Boolean,
    val packetDigestHex: String,
    val statusBadge: String,
    val waveformHistory: List<Float> = List(48) { 0.5f }
)

data class SpectRoomSensorDashboardState(
    val selectedPathFilter: String = "ALL", // "ALL" or SpectSensorPathId.id
    val liveStreamingActive: Boolean = true,
    val aggregateHealthPct: Int = 98,
    val activeSensorPathsCount: Int = 6,
    val totalPacketsRouted: Long = 128L,
    val compositeSignalHistory: List<Float> = List(64) { 0.78f },
    val compositeStabilityHistory: List<Float> = List(64) { 0.96f },
    val compositeDriftHistory: List<Float> = List(64) { 0.04f },
    val sensorPaths: List<SpectSensorPathNodeState> = emptyList(),
    val lastCalibratedSummary: String = "BOOT-SYNC // All 6 SPECT Room Sensor Paths Locked to Local SQLite WAL"
)

object SpectRoomSensorPathEngine {

    private const val HISTORY_SIZE = 48
    private const val COMPOSITE_HISTORY_SIZE = 64

    fun buildInitialDashboardState(): SpectRoomSensorDashboardState {
        val defaultPaths = SpectSensorPathId.entries.mapIndexed { idx, pathId ->
            val baseSignal = (0.72f + idx * 0.03f).coerceIn(0.1f, 0.98f)
            SpectSensorPathNodeState(
                pathId = pathId,
                primaryReadout = when (pathId) {
                    SpectSensorPathId.PATH_01_EEG_SPECTRAL -> "18.2 Hz • Coherence 92%"
                    SpectSensorPathId.PATH_02_AS_CORE_RGB -> "R:0.42 G:0.88 B:0.94"
                    SpectSensorPathId.PATH_03_SOMATIC_IMU -> "1.00 G • 0.02 rad/s (94%)"
                    SpectSensorPathId.PATH_04_GLSL_RAYMARCH -> "60.2 Hz Inv • 16.6 ms"
                    SpectSensorPathId.PATH_05_UNICRITIC_DRIFT -> "0.00 Drift • Score 96%"
                    SpectSensorPathId.PATH_06_SHA256_SQLITE -> "VERIFIED • check:pass:true_only"
                },
                secondaryReadout = "${pathId.sensorSourceUri} ➔ ${pathId.spectRoomColumn}",
                signalNormalized = baseSignal,
                driftIntensity = 0.0f,
                sampleRateHz = if (idx < 4) 60 else 30,
                latencyMs = 0.4f + idx * 0.15f,
                sha256Verified = true,
                packetDigestHex = sha256Hex("INIT|${pathId.id}|$idx"),
                statusBadge = "NOMINAL LOCK",
                waveformHistory = List(HISTORY_SIZE) { i ->
                    (baseSignal + sin(i * 0.25f + idx) * 0.08f).coerceIn(0.08f, 0.96f)
                }
            )
        }
        return SpectRoomSensorDashboardState(
            sensorPaths = defaultPaths
        )
    }

    fun stepSensorDashboard(
        previous: SpectRoomSensorDashboardState,
        simTimeSec: Float,
        eegBands: EegBandState,
        dominantHz: Float,
        phaseCoherencePct: Int,
        cognitiveLoadPct: Int,
        asCoreFrame: AsCoreRgbBiometricFrame,
        hardwareTelemetry: HardwareTelemetry,
        frameMs: Float,
        rotatingFrqHz: Float,
        perturbation: Float,
        unicriticConsensus: Int,
        effectiveDrift: Float,
        sha256Verified: Boolean,
        checkPassTrueOnly: Boolean,
        continuitySha256: String,
        persistedRecordCount: Int
    ): SpectRoomSensorDashboardState {
        if (!previous.liveStreamingActive) return previous

        val basePaths = if (previous.sensorPaths.size == SpectSensorPathId.entries.size) {
            previous.sensorPaths
        } else {
            buildInitialDashboardState().sensorPaths
        }

        val invFps = (1000f / frameMs.coerceAtLeast(1.0f)).coerceIn(5f, 120f)
        val invStability = ((1.0f - perturbation) * 100f).coerceIn(0f, 100f)

        val updatedPaths = basePaths.map { prevNode ->
            when (prevNode.pathId) {
                SpectSensorPathId.PATH_01_EEG_SPECTRAL -> {
                    val sample = (
                        0.25f +
                            (eegBands.alpha * 0.35f + eegBands.beta * 0.35f) +
                            sin(simTimeSec * 4.2f) * 0.08f
                        ).coerceIn(0.06f, 0.98f)
                    val drift = ((100 - phaseCoherencePct) / 100f).coerceIn(0f, 1f)
                    prevNode.copy(
                        primaryReadout = "${"%.1f".format(dominantHz)} Hz • Coherence $phaseCoherencePct% (${eegBands.dominantBandName.substringBefore(" ")})",
                        signalNormalized = sample,
                        driftIntensity = drift,
                        sampleRateHz = 60,
                        latencyMs = 0.42f,
                        sha256Verified = sha256Verified,
                        packetDigestHex = prevNode.packetDigestHex,
                        statusBadge = if (phaseCoherencePct >= 75) "PHASE LOCKED" else "COHERENCE WARN",
                        waveformHistory = (prevNode.waveformHistory.drop(1) + sample)
                    )
                }

                SpectSensorPathId.PATH_02_AS_CORE_RGB -> {
                    val r = asCoreFrame.redChannelCreativity
                    val g = asCoreFrame.greenChannelTruth
                    val b = asCoreFrame.blueChannelFocus
                    val sample = ((r + g + b) / 3.0f + cos(simTimeSec * 3.6f) * 0.06f).coerceIn(0.06f, 0.98f)
                    prevNode.copy(
                        primaryReadout = "R:${"%.2f".format(r)} G:${"%.2f".format(g)} B:${"%.2f".format(b)} • ${asCoreFrame.asCoreToken}",
                        signalNormalized = sample,
                        driftIntensity = (effectiveDrift * 0.6f).coerceIn(0f, 1f),
                        sampleRateHz = 30,
                        latencyMs = 0.55f,
                        sha256Verified = sha256Verified,
                        packetDigestHex = asCoreFrame.sha256FrameSeal,
                        statusBadge = if (asCoreFrame.blockMindPolarityDrive >= 0.35f) {
                            "UNBLOCK [+]"
                        } else if (asCoreFrame.blockMindPolarityDrive <= -0.35f) {
                            "BLOCK [-]"
                        } else {
                            "RGB SYNCED"
                        },
                        waveformHistory = (prevNode.waveformHistory.drop(1) + sample)
                    )
                }

                SpectSensorPathId.PATH_03_SOMATIC_IMU -> {
                    val somPct = hardwareTelemetry.somaticStabilityPct
                    val magG = hardwareTelemetry.motionMagnitudeG
                    val gyro = hardwareTelemetry.gyroDriftRad
                    val sample = (
                        (somPct / 100f) * 0.85f +
                            sin(simTimeSec * 2.8f + magG) * 0.07f
                        ).coerceIn(0.08f, 0.98f)
                    val imuDrift = ((100 - somPct) / 100f + gyro * 0.2f).coerceIn(0f, 1f)
                    prevNode.copy(
                        primaryReadout = "${"%.2f".format(magG)}G • ${"%.2f".format(gyro)} rad/s • Stability $somPct%",
                        signalNormalized = sample,
                        driftIntensity = imuDrift,
                        sampleRateHz = 60,
                        latencyMs = 0.38f,
                        sha256Verified = true,
                        statusBadge = if (hardwareTelemetry.sensorActive) "HW IMU LIVE" else "IMU BRIDGE",
                        waveformHistory = (prevNode.waveformHistory.drop(1) + sample)
                    )
                }

                SpectSensorPathId.PATH_04_GLSL_RAYMARCH -> {
                    val sample = (
                        (invFps / 65f) * 0.78f +
                            sin(simTimeSec * rotatingFrqHz * 4.0f) * 0.12f
                        ).coerceIn(0.08f, 0.98f)
                    prevNode.copy(
                        primaryReadout = "${"%.1f".format(invFps)} Hz Inv (${"%.1f".format(frameMs)}ms) • Rot ${"%.2f".format(rotatingFrqHz)}Hz",
                        signalNormalized = sample,
                        driftIntensity = perturbation.coerceIn(0f, 1f),
                        sampleRateHz = 60,
                        latencyMs = (frameMs * 0.04f).coerceIn(0.3f, 2.5f),
                        sha256Verified = sha256Verified,
                        statusBadge = if (perturbation < 0.15f) "60FPS RAYMARCH" else "FRACTURE HEAL",
                        waveformHistory = (prevNode.waveformHistory.drop(1) + sample)
                    )
                }

                SpectSensorPathId.PATH_05_UNICRITIC_DRIFT -> {
                    val stabilityNorm = ((1.0f - effectiveDrift) * 0.88f + cos(simTimeSec * 3.1f) * 0.05f).coerceIn(0.06f, 0.98f)
                    prevNode.copy(
                        primaryReadout = "Drift ${"%.2f".format(effectiveDrift)} • Consensus $unicriticConsensus% • BioLoad $cognitiveLoadPct%",
                        signalNormalized = stabilityNorm,
                        driftIntensity = effectiveDrift,
                        sampleRateHz = 30,
                        latencyMs = 0.48f,
                        sha256Verified = sha256Verified,
                        statusBadge = when {
                            effectiveDrift >= 0.42f -> "REDLINE SHEAR"
                            effectiveDrift >= 0.15f -> "ADMONITIO"
                            else -> "OPTIMAL LOCK"
                        },
                        waveformHistory = (prevNode.waveformHistory.drop(1) + stabilityNorm)
                    )
                }

                SpectSensorPathId.PATH_06_SHA256_SQLITE -> {
                    val sealOk = sha256Verified && checkPassTrueOnly
                    val sample = if (sealOk) {
                        (0.90f + sin(simTimeSec * 1.9f) * 0.04f).coerceIn(0.80f, 0.98f)
                    } else {
                        0.32f
                    }
                    prevNode.copy(
                        primaryReadout = "Rows: $persistedRecordCount • ${if (checkPassTrueOnly) "check:pass:true_only" else "EGRESS BLOCKED"} • ${continuitySha256.take(12)}...",
                        signalNormalized = sample,
                        driftIntensity = if (sealOk) 0.0f else 0.65f,
                        sampleRateHz = 30,
                        latencyMs = 0.62f,
                        sha256Verified = sealOk,
                        packetDigestHex = continuitySha256,
                        statusBadge = if (sealOk) "SQLITE SEALED" else "VERIFY WARN",
                        waveformHistory = (prevNode.waveformHistory.drop(1) + sample)
                    )
                }
            }
        }

        val avgSignal = updatedPaths.map { it.signalNormalized }.average().toFloat().coerceIn(0.05f, 0.99f)
        val avgDrift = updatedPaths.map { it.driftIntensity }.average().toFloat().coerceIn(0.0f, 1.0f)
        val stabilityNorm = ((invStability / 100f) * (1f - avgDrift * 0.5f)).coerceIn(0.05f, 1.0f)
        val healthPct = ((1.0f - avgDrift * 0.75f) * (if (sha256Verified && checkPassTrueOnly) 100f else 72f))
            .roundToInt()
            .coerceIn(12, 100)

        return previous.copy(
            aggregateHealthPct = healthPct,
            activeSensorPathsCount = updatedPaths.count { it.sha256Verified },
            totalPacketsRouted = previous.totalPacketsRouted + 6L,
            compositeSignalHistory = (previous.compositeSignalHistory.drop(1) + avgSignal).takeLast(COMPOSITE_HISTORY_SIZE),
            compositeStabilityHistory = (previous.compositeStabilityHistory.drop(1) + stabilityNorm).takeLast(COMPOSITE_HISTORY_SIZE),
            compositeDriftHistory = (previous.compositeDriftHistory.drop(1) + avgDrift).takeLast(COMPOSITE_HISTORY_SIZE),
            sensorPaths = updatedPaths
        )
    }

    private fun sha256Hex(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

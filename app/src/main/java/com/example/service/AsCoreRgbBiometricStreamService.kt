package com.example.service

import com.example.data.MockBiometricRepository
import com.example.model.AsIfTunnelState
import com.example.model.ContractPhase
import com.example.model.EegBandState
import com.example.model.RgbCognitiveMode
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

/**
 * `as[core]` stream profiles governing how real-time RGB biometric channels
 * ([R] Creativity, [G] Truth, [B] Focus) are synthesized and coupled to the
 * BlockMind dashboard components (`time + notime = blockmind +-`).
 */
enum class AsCoreStreamProfile(
    val id: String,
    val asCoreTag: String,
    val title: String,
    val subtitle: String,
    val targetRgbMode: RgbCognitiveMode,
    val defaultPolarityTarget: Float
) {
    AS_CORE_EQUILIBRIUM(
        id = "equilibrium",
        asCoreTag = "as[core::equilibrium]",
        title = "as[core] Equilibrium",
        subtitle = "Balanced R•G•B Tri-Chromatic Lock • BlockMind [+-] 0.00",
        targetRgbMode = RgbCognitiveMode.BIOMETRIC_AUTO,
        defaultPolarityTarget = 0.0f
    ),
    AS_CORE_CREATIVITY_RED(
        id = "creativity_r",
        asCoreTag = "as[core::creativity_r]",
        title = "as[core] Red [R]",
        subtitle = "Theta-Gamma Creativity Surge • Unblock Mind [+] Kinetic Flow",
        targetRgbMode = RgbCognitiveMode.CREATIVITY_RED,
        defaultPolarityTarget = 0.72f
    ),
    AS_CORE_TRUTH_GREEN(
        id = "truth_g",
        asCoreTag = "as[core::truth_g]",
        title = "as[core] Green [G]",
        subtitle = "Alpha Crystalline Verification • Epistula Truth Seal",
        targetRgbMode = RgbCognitiveMode.TRUTH_GREEN,
        defaultPolarityTarget = 0.15f
    ),
    AS_CORE_FOCUS_BLUE(
        id = "focus_b",
        asCoreTag = "as[core::focus_b]",
        title = "as[core] Blue [B]",
        subtitle = "Beta-SMR Deep Tunnel Coherence • BlockMind Precision Lock",
        targetRgbMode = RgbCognitiveMode.FOCUS_BLUE,
        defaultPolarityTarget = -0.45f
    ),
    AS_CORE_TRI_WAVE_SWEEP(
        id = "tri_wave",
        asCoreTag = "as[core::tri_wave]",
        title = "as[core] Tri-Wave",
        subtitle = "Harmonic R→G→B Phase Sweep • Dynamic BlockMind [+-] Modulation",
        targetRgbMode = RgbCognitiveMode.BIOMETRIC_AUTO,
        defaultPolarityTarget = 0.0f
    )
}

/**
 * Immutable real-time snapshot emitted by [AsCoreRgbBiometricStreamService] to update
 * all BlockMind dashboard components, Osa A-X- inf00 nodes, and 3D GLSL uniforms.
 */
data class AsCoreRgbBiometricFrame(
    val frameSequenceId: Long = 1L,
    val asCoreToken: String = "as[core::equilibrium#0001]",
    val profile: AsCoreStreamProfile = AsCoreStreamProfile.AS_CORE_EQUILIBRIUM,
    val rgbMode: RgbCognitiveMode = RgbCognitiveMode.FOCUS_BLUE,
    val redChannelCreativity: Float = 0.48f,
    val greenChannelTruth: Float = 0.82f,
    val blueChannelFocus: Float = 0.86f,
    val rgbSynchroVector: FloatArray = floatArrayOf(0.48f, 1.12f, 1.32f),
    val rgbChromaticBalancePct: Int = 94,
    val eegBands: EegBandState = EegBandState(),
    val dominantFrequencyHz: Float = 16.8f,
    val phaseCoherencePct: Int = 94,
    val cognitiveLoadPct: Int = 30,
    val blockMindPolarityDrive: Float = 0.0f,
    val contractPhase: ContractPhase = ContractPhase.EPISTULA,
    val asIfTunnelState: AsIfTunnelState = AsIfTunnelState.LOCKED,
    val rgbWaveformHistoryR: List<Float> = List(48) { 0.48f },
    val rgbWaveformHistoryG: List<Float> = List(48) { 0.82f },
    val rgbWaveformHistoryB: List<Float> = List(48) { 0.86f },
    val sha256FrameSeal: String = "a5c09e00112233445566778899aabbccddeeff00112233445566778899aabbcc",
    val timestampMs: Long = System.currentTimeMillis()
)

/**
 * Service layer contract for mocking and streaming real-time RGB biometric telemetry
 * (`as[core]`) that updates the BlockMind dashboard components.
 */
interface AsCoreRgbBiometricStreamService {
    val isStreamActive: StateFlow<Boolean>
    val activeProfile: StateFlow<AsCoreStreamProfile>
    val cadenceHz: StateFlow<Int>
    val autoDriveBlockMind: StateFlow<Boolean>
    val latestFrame: StateFlow<AsCoreRgbBiometricFrame>

    fun setStreamActive(active: Boolean)
    fun selectProfile(profile: AsCoreStreamProfile)
    fun setCadenceHz(hz: Int)
    fun setAutoDriveBlockMind(enabled: Boolean)
    fun setChannelGains(rGain: Float, gGain: Float, bGain: Float)
    fun getChannelGains(): Triple<Float, Float, Float>
    fun injectChromaticPulse(channel: RgbChannelAxis, intensity: Float = 0.45f)

    /**
     * Computes and emits the next synchronized `as[core]` RGB biometric frame.
     */
    fun stepFrame(
        simTimeSec: Float,
        perturbation: Float = 0f,
        somaticStabilityPct: Int = 96
    ): AsCoreRgbBiometricFrame

    /**
     * Cold reactive [Flow] emitting real-time [AsCoreRgbBiometricFrame] updates at the active cadence.
     */
    fun observeRealtimeStream(overrideIntervalMs: Long? = null): Flow<AsCoreRgbBiometricFrame>
}

enum class RgbChannelAxis(val code: String, val label: String) {
    RED("R", "Creativity [R]"),
    GREEN("G", "Truth [G]"),
    BLUE("B", "Focus [B]")
}

/**
 * Default implementation of [AsCoreRgbBiometricStreamService] (`as[core]`) that synthesizes
 * coupled Red (Creativity / Theta-Gamma), Green (Truth / Alpha), and Blue (Focus / Beta-SMR)
 * biometric streams and drives the BlockMind dashboard components in real time.
 */
class DefaultAsCoreRgbBiometricStreamService(
    private val mockBiometricRepository: MockBiometricRepository = MockBiometricRepository()
) : AsCoreRgbBiometricStreamService {

    private val _isStreamActive = MutableStateFlow(true)
    override val isStreamActive: StateFlow<Boolean> = _isStreamActive.asStateFlow()

    private val _activeProfile = MutableStateFlow(AsCoreStreamProfile.AS_CORE_EQUILIBRIUM)
    override val activeProfile: StateFlow<AsCoreStreamProfile> = _activeProfile.asStateFlow()

    private val _cadenceHz = MutableStateFlow(30)
    override val cadenceHz: StateFlow<Int> = _cadenceHz.asStateFlow()

    private val _autoDriveBlockMind = MutableStateFlow(true)
    override val autoDriveBlockMind: StateFlow<Boolean> = _autoDriveBlockMind.asStateFlow()

    @Volatile
    private var rGain: Float = 1.00f

    @Volatile
    private var gGain: Float = 1.00f

    @Volatile
    private var bGain: Float = 1.00f

    @Volatile
    private var rPulseBoost: Float = 0.0f

    @Volatile
    private var gPulseBoost: Float = 0.0f

    @Volatile
    private var bPulseBoost: Float = 0.0f

    private var frameCounter: Long = 1L

    private val historyR = ArrayDeque<Float>(48).apply { repeat(48) { addLast(0.48f) } }
    private val historyG = ArrayDeque<Float>(48).apply { repeat(48) { addLast(0.82f) } }
    private val historyB = ArrayDeque<Float>(48).apply { repeat(48) { addLast(0.86f) } }

    private val _latestFrame = MutableStateFlow(
        synthesizeFrameInternal(
            simTimeSec = 0f,
            perturbation = 0f,
            somaticStabilityPct = 96,
            seqId = 1L
        )
    )
    override val latestFrame: StateFlow<AsCoreRgbBiometricFrame> = _latestFrame.asStateFlow()

    override fun setStreamActive(active: Boolean) {
        _isStreamActive.value = active
        mockBiometricRepository.setStreamingEnabled(active)
    }

    override fun selectProfile(profile: AsCoreStreamProfile) {
        _activeProfile.value = profile
        mockBiometricRepository.setTargetMode(profile.targetRgbMode)
    }

    override fun setCadenceHz(hz: Int) {
        _cadenceHz.value = hz.coerceIn(5, 60)
    }

    override fun setAutoDriveBlockMind(enabled: Boolean) {
        _autoDriveBlockMind.value = enabled
    }

    override fun setChannelGains(rGain: Float, gGain: Float, bGain: Float) {
        this.rGain = rGain.coerceIn(0.20f, 2.00f)
        this.gGain = gGain.coerceIn(0.20f, 2.00f)
        this.bGain = bGain.coerceIn(0.20f, 2.00f)
    }

    override fun getChannelGains(): Triple<Float, Float, Float> = Triple(rGain, gGain, bGain)

    override fun injectChromaticPulse(channel: RgbChannelAxis, intensity: Float) {
        val clamped = intensity.coerceIn(0.10f, 0.85f)
        when (channel) {
            RgbChannelAxis.RED -> rPulseBoost = (rPulseBoost + clamped).coerceAtMost(0.90f)
            RgbChannelAxis.GREEN -> gPulseBoost = (gPulseBoost + clamped).coerceAtMost(0.90f)
            RgbChannelAxis.BLUE -> bPulseBoost = (bPulseBoost + clamped).coerceAtMost(0.90f)
        }
        mockBiometricRepository.triggerSynapticBurst(clamped * 0.65f)
    }

    override fun stepFrame(
        simTimeSec: Float,
        perturbation: Float,
        somaticStabilityPct: Int
    ): AsCoreRgbBiometricFrame {
        // Decay transient chromatic pulses smoothly
        if (rPulseBoost > 0.004f) rPulseBoost = (rPulseBoost - 0.028f).coerceAtLeast(0f) else rPulseBoost = 0f
        if (gPulseBoost > 0.004f) gPulseBoost = (gPulseBoost - 0.028f).coerceAtLeast(0f) else gPulseBoost = 0f
        if (bPulseBoost > 0.004f) bPulseBoost = (bPulseBoost - 0.028f).coerceAtLeast(0f) else bPulseBoost = 0f

        val nextSeq = ++frameCounter
        val frame = synthesizeFrameInternal(
            simTimeSec = simTimeSec,
            perturbation = perturbation,
            somaticStabilityPct = somaticStabilityPct,
            seqId = nextSeq
        )
        _latestFrame.value = frame
        return frame
    }

    override fun observeRealtimeStream(overrideIntervalMs: Long?): Flow<AsCoreRgbBiometricFrame> = flow {
        var elapsedSec = 0f
        while (true) {
            val interval = overrideIntervalMs ?: (1000L / _cadenceHz.value.coerceIn(5, 60)).coerceAtLeast(16L)
            if (_isStreamActive.value) {
                val frame = stepFrame(
                    simTimeSec = elapsedSec,
                    perturbation = 0f,
                    somaticStabilityPct = 96
                )
                emit(frame)
            }
            delay(interval)
            elapsedSec += interval / 1000f
        }
    }

    private fun synthesizeFrameInternal(
        simTimeSec: Float,
        perturbation: Float,
        somaticStabilityPct: Int,
        seqId: Long
    ): AsCoreRgbBiometricFrame {
        val profile = _activeProfile.value

        // Determine base cognitive mode for this tick
        val resolvedRgbMode = when (profile) {
            AsCoreStreamProfile.AS_CORE_CREATIVITY_RED -> RgbCognitiveMode.CREATIVITY_RED
            AsCoreStreamProfile.AS_CORE_TRUTH_GREEN -> RgbCognitiveMode.TRUTH_GREEN
            AsCoreStreamProfile.AS_CORE_FOCUS_BLUE -> RgbCognitiveMode.FOCUS_BLUE
            AsCoreStreamProfile.AS_CORE_EQUILIBRIUM -> RgbCognitiveMode.BIOMETRIC_AUTO
            AsCoreStreamProfile.AS_CORE_TRI_WAVE_SWEEP -> {
                val phaseIdx = ((simTimeSec / 6.0f).toInt().coerceAtLeast(0)) % 3
                when (phaseIdx) {
                    0 -> RgbCognitiveMode.CREATIVITY_RED
                    1 -> RgbCognitiveMode.TRUTH_GREEN
                    else -> RgbCognitiveMode.FOCUS_BLUE
                }
            }
        }

        mockBiometricRepository.setTargetMode(resolvedRgbMode)
        val basePacket = mockBiometricRepository.nextStepPacket(simTimeSec)

        // Harmonic tri-wave modulations for R (Creativity), G (Truth), and B (Focus)
        val waveR = 0.08f * sin(simTimeSec * 2.1f) + 0.04f * cos(simTimeSec * 4.3f)
        val waveG = 0.07f * cos(simTimeSec * 1.6f) + 0.03f * sin(simTimeSec * 3.2f)
        val waveB = 0.08f * sin(simTimeSec * 2.7f + 1.1f) + 0.04f * cos(simTimeSec * 5.0f)

        val profileBiasR = when (profile) {
            AsCoreStreamProfile.AS_CORE_CREATIVITY_RED -> 0.22f
            AsCoreStreamProfile.AS_CORE_TRI_WAVE_SWEEP -> 0.14f * sin(simTimeSec * 0.9f)
            else -> 0.0f
        }
        val profileBiasG = when (profile) {
            AsCoreStreamProfile.AS_CORE_TRUTH_GREEN -> 0.20f
            AsCoreStreamProfile.AS_CORE_EQUILIBRIUM -> 0.12f
            AsCoreStreamProfile.AS_CORE_TRI_WAVE_SWEEP -> 0.14f * sin(simTimeSec * 0.9f + 2.094f)
            else -> 0.0f
        }
        val profileBiasB = when (profile) {
            AsCoreStreamProfile.AS_CORE_FOCUS_BLUE -> 0.22f
            AsCoreStreamProfile.AS_CORE_TRI_WAVE_SWEEP -> 0.14f * sin(simTimeSec * 0.9f + 4.188f)
            else -> 0.0f
        }

        // Compute normalized [0.05..1.00] R, G, B biometric channel intensities
        val rawR = ((basePacket.bands.theta * 0.62f + basePacket.bands.gamma * 0.38f + waveR + profileBiasR) * rGain + rPulseBoost)
            .coerceIn(0.05f, 1.0f)
        val rawG = ((basePacket.bands.alpha * 0.78f + (1f - perturbation) * 0.22f + waveG + profileBiasG) * gGain + gPulseBoost)
            .coerceIn(0.05f, 1.0f)
        val rawB = ((basePacket.bands.beta * 0.72f + basePacket.bands.alpha * 0.28f + waveB + profileBiasB) * bGain + bPulseBoost)
            .coerceIn(0.05f, 1.0f)

        // Update 5-band EEG state coupled to the R/G/B channel gains & pulses
        val coupledBands = EegBandState(
            delta = (basePacket.bands.delta * (0.90f + 0.10f * rawG)).coerceIn(0.05f, 1.0f),
            theta = (rawR * 0.75f + basePacket.bands.theta * 0.25f).coerceIn(0.05f, 1.0f),
            alpha = (rawG * 0.80f + basePacket.bands.alpha * 0.20f).coerceIn(0.05f, 1.0f),
            beta = (rawB * 0.78f + basePacket.bands.beta * 0.22f).coerceIn(0.05f, 1.0f),
            gamma = ((rawR * 0.52f + rawB * 0.48f) * 0.65f + basePacket.bands.gamma * 0.35f).coerceIn(0.05f, 1.0f)
        )

        // Compute 3-channel shader tint vector [R, G, B] in [0.25..1.55]
        val shaderTint = floatArrayOf(
            (0.32f + rawR * 1.08f + perturbation * 0.25f).coerceIn(0.25f, 1.55f),
            (0.32f + rawG * 1.08f * (1f - perturbation * 0.35f)).coerceIn(0.25f, 1.55f),
            (0.32f + rawB * 1.12f).coerceIn(0.25f, 1.55f)
        )

        // Chromatic balance score: highest when R, G, B are harmonically balanced or locked to Truth/Focus
        val meanRgb = (rawR + rawG + rawB) / 3f
        val dispersion = (abs(rawR - meanRgb) + abs(rawG - meanRgb) + abs(rawB - meanRgb)) / 3f
        val balancePct = ((1.0f - dispersion * 0.85f) * 100f * (1f - perturbation * 0.3f))
            .roundToInt()
            .coerceIn(35, 100)

        // Compute BlockMind Polarity Drive (-1.00 .. +1.00) from R (kinetic flow +) vs B (stasis lock -) stabilized by G
        val computedPolarityDrive = when (profile) {
            AsCoreStreamProfile.AS_CORE_EQUILIBRIUM -> {
                ((rawR - rawB) * 0.35f * (1f - rawG * 0.5f)).coerceIn(-0.28f, 0.28f)
            }
            AsCoreStreamProfile.AS_CORE_CREATIVITY_RED -> {
                (0.48f + rawR * 0.45f - rawB * 0.15f).coerceIn(0.38f, 1.0f)
            }
            AsCoreStreamProfile.AS_CORE_TRUTH_GREEN -> {
                ((rawR - rawB) * 0.25f).coerceIn(-0.22f, 0.22f)
            }
            AsCoreStreamProfile.AS_CORE_FOCUS_BLUE -> {
                (-0.42f - rawB * 0.45f + rawR * 0.15f).coerceIn(-1.0f, -0.36f)
            }
            AsCoreStreamProfile.AS_CORE_TRI_WAVE_SWEEP -> {
                (sin(simTimeSec * 0.85f) * 0.78f + (rawR - rawB) * 0.22f).coerceIn(-1.0f, 1.0f)
            }
        }

        val motionPenalty = (100 - somaticStabilityPct) * 0.32f
        val cogLoadPct = (
            coupledBands.beta * 36f +
                coupledBands.gamma * 30f +
                coupledBands.theta * 18f -
                coupledBands.alpha * 18f +
                perturbation * 35f +
                motionPenalty
            ).roundToInt().coerceIn(8, 99)

        val phaseCoherence = (
            basePacket.phaseCoherencePct * 0.55f +
                rawG * 42f -
                perturbation * 28f
            ).roundToInt().coerceIn(48, 100)

        val dominantHz = when {
            rawR > rawG && rawR > rawB -> 6.8f + rawR * 4.2f
            rawG >= rawB -> 10.4f + rawG * 2.4f
            else -> 16.5f + rawB * 6.5f
        }

        val contractPhase = when {
            perturbation > 0.42f -> ContractPhase.LITTERAE
            perturbation > 0.06f || cogLoadPct > 78 -> ContractPhase.SATIS
            else -> ContractPhase.EPISTULA
        }

        val asIfState = when {
            perturbation > 0.35f -> AsIfTunnelState.NOT_YET
            perturbation > 0.05f || cogLoadPct > 74 -> AsIfTunnelState.CONVERGING
            else -> AsIfTunnelState.LOCKED
        }

        if (historyR.size >= 48) historyR.removeFirst()
        if (historyG.size >= 48) historyG.removeFirst()
        if (historyB.size >= 48) historyB.removeFirst()
        historyR.addLast(rawR)
        historyG.addLast(rawG)
        historyB.addLast(rawB)

        val token = "as[core::${profile.id}#%04d]".format(seqId % 10000L)
        val sealPayload = "$token|R=${"%.3f".format(rawR)}|G=${"%.3f".format(rawG)}|B=${"%.3f".format(rawB)}|POL=${"%.2f".format(computedPolarityDrive)}|PHASE=${contractPhase.name}"
        val shaSeal = MessageDigest.getInstance("SHA-256")
            .digest(sealPayload.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        return AsCoreRgbBiometricFrame(
            frameSequenceId = seqId,
            asCoreToken = token,
            profile = profile,
            rgbMode = if (profile == AsCoreStreamProfile.AS_CORE_EQUILIBRIUM || profile == AsCoreStreamProfile.AS_CORE_TRI_WAVE_SWEEP) {
                RgbCognitiveMode.BIOMETRIC_AUTO
            } else {
                resolvedRgbMode
            },
            redChannelCreativity = rawR,
            greenChannelTruth = rawG,
            blueChannelFocus = rawB,
            rgbSynchroVector = shaderTint,
            rgbChromaticBalancePct = balancePct,
            eegBands = coupledBands,
            dominantFrequencyHz = dominantHz,
            phaseCoherencePct = phaseCoherence,
            cognitiveLoadPct = cogLoadPct,
            blockMindPolarityDrive = computedPolarityDrive,
            contractPhase = contractPhase,
            asIfTunnelState = asIfState,
            rgbWaveformHistoryR = historyR.toList(),
            rgbWaveformHistoryG = historyG.toList(),
            rgbWaveformHistoryB = historyB.toList(),
            sha256FrameSeal = shaSeal
        )
    }
}

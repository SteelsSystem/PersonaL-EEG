package com.example.data

import com.example.model.EegBandState
import com.example.model.RgbCognitiveMode
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

data class MockEegStreamPacket(
    val mode: RgbCognitiveMode = RgbCognitiveMode.FOCUS_BLUE,
    val bands: EegBandState = EegBandState(),
    val dominantFrequencyHz: Float = 16.4f,
    val phaseCoherencePct: Int = 92,
    val cognitiveLoadIndex: Int = 34,
    val rgbSynchroVector: FloatArray = RgbCognitiveMode.FOCUS_BLUE.shaderTint,
    val streamSequenceId: Long = 0L,
    val timestampMs: Long = System.currentTimeMillis()
)

/**
 * Simulates continuous multi-band EEG biometric telemetry streams synchronized with
 * the RGB Creativity [R], Truth [G], Focus [B], and Bio-Sync [RGB] cognitive modes.
 */
class MockBiometricRepository {

    private val _isStreaming = MutableStateFlow(true)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _targetMode = MutableStateFlow(RgbCognitiveMode.FOCUS_BLUE)
    val targetMode: StateFlow<RgbCognitiveMode> = _targetMode.asStateFlow()

    private val _latestPacket = MutableStateFlow(samplePacket(RgbCognitiveMode.FOCUS_BLUE, 0f, 0L))
    val latestPacket: StateFlow<MockEegStreamPacket> = _latestPacket.asStateFlow()

    @Volatile
    private var burstAmplitude: Float = 0f

    private var sequenceCounter: Long = 1L

    fun setStreamingEnabled(enabled: Boolean) {
        _isStreaming.value = enabled
    }

    fun setTargetMode(mode: RgbCognitiveMode) {
        _targetMode.value = mode
    }

    fun triggerSynapticBurst(intensity: Float = 0.35f) {
        burstAmplitude = (burstAmplitude + intensity).coerceIn(0f, 0.65f)
    }

    /**
     * Cold Flow emitting simulated EEG biometric packets at [intervalMs] cadence.
     */
    fun observeEegStream(intervalMs: Long = 64L): Flow<MockEegStreamPacket> = flow {
        var elapsedSec = 0f
        while (true) {
            if (_isStreaming.value) {
                val packet = nextStepPacket(elapsedSec)
                emit(packet)
            }
            delay(intervalMs)
            elapsedSec += intervalMs / 1000f
        }
    }

    /**
     * Advances internal burst decay and produces the next synchronized [MockEegStreamPacket].
     */
    fun nextStepPacket(timeSec: Float): MockEegStreamPacket {
        if (burstAmplitude > 0.005f) {
            burstAmplitude = (burstAmplitude - 0.025f).coerceAtLeast(0f)
        }
        val packet = samplePacket(
            mode = _targetMode.value,
            timeSec = timeSec,
            seqId = sequenceCounter++,
            extraBurst = burstAmplitude
        )
        _latestPacket.value = packet
        return packet
    }

    /**
     * Deterministically synthesizes an EEG biometric packet for the given [mode] and [timeSec].
     */
    fun samplePacket(
        mode: RgbCognitiveMode,
        timeSec: Float,
        seqId: Long = 0L,
        extraBurst: Float = 0f
    ): MockEegStreamPacket {
        // Slow harmonic waves to simulate realistic neuro-oscillations
        val slowWave = sin(timeSec * 0.9f)
        val medWave = cos(timeSec * 1.7f)
        val fastWave = sin(timeSec * 3.1f)

        val effectiveMode = if (mode == RgbCognitiveMode.BIOMETRIC_AUTO) {
            // Cycle smoothly across Focus, Truth, and Creativity every 12 seconds
            val phase = ((timeSec / 12f).toInt()) % 3
            when (phase) {
                0 -> RgbCognitiveMode.FOCUS_BLUE
                1 -> RgbCognitiveMode.TRUTH_GREEN
                else -> RgbCognitiveMode.CREATIVITY_RED
            }
        } else {
            mode
        }

        val bands: EegBandState
        val dominantHz: Float
        val coherence: Int

        when (effectiveMode) {
            RgbCognitiveMode.CREATIVITY_RED -> {
                // High Theta (4-8 Hz) + Gamma (30-100 Hz) associative bursts
                val delta = (0.24f + 0.05f * slowWave).coerceIn(0.05f, 1.0f)
                val theta = (0.82f + 0.10f * medWave + extraBurst * 0.25f).coerceIn(0.05f, 1.0f)
                val alpha = (0.58f + 0.06f * slowWave).coerceIn(0.05f, 1.0f)
                val beta = (0.46f + 0.07f * fastWave).coerceIn(0.05f, 1.0f)
                val gamma = (0.76f + 0.11f * fastWave + extraBurst * 0.30f).coerceIn(0.05f, 1.0f)
                bands = EegBandState(delta, theta, alpha, beta, gamma)
                dominantHz = 6.4f + 1.2f * medWave
                coherence = (86 + (slowWave * 6).roundToInt() - (extraBurst * 18).roundToInt()).coerceIn(55, 99)
            }

            RgbCognitiveMode.TRUTH_GREEN -> {
                // High Alpha (8-13 Hz) calm crystalline verification lock
                val delta = (0.28f + 0.04f * slowWave).coerceIn(0.05f, 1.0f)
                val theta = (0.44f + 0.05f * medWave).coerceIn(0.05f, 1.0f)
                val alpha = (0.90f + 0.07f * slowWave - extraBurst * 0.15f).coerceIn(0.05f, 1.0f)
                val beta = (0.48f + 0.05f * medWave + extraBurst * 0.20f).coerceIn(0.05f, 1.0f)
                val gamma = (0.36f + 0.04f * fastWave).coerceIn(0.05f, 1.0f)
                bands = EegBandState(delta, theta, alpha, beta, gamma)
                dominantHz = 10.6f + 0.8f * slowWave
                coherence = (95 + (slowWave * 3).roundToInt() - (extraBurst * 15).roundToInt()).coerceIn(60, 100)
            }

            RgbCognitiveMode.FOCUS_BLUE, RgbCognitiveMode.BIOMETRIC_AUTO -> {
                // High Beta / SMR (13-30 Hz) + supportive Alpha tunnel coherence
                val delta = (0.16f + 0.03f * slowWave).coerceIn(0.05f, 1.0f)
                val theta = (0.28f + 0.05f * medWave).coerceIn(0.05f, 1.0f)
                val alpha = (0.70f + 0.06f * slowWave).coerceIn(0.05f, 1.0f)
                val beta = (0.86f + 0.09f * fastWave + extraBurst * 0.20f).coerceIn(0.05f, 1.0f)
                val gamma = (0.54f + 0.07f * medWave + extraBurst * 0.25f).coerceIn(0.05f, 1.0f)
                bands = EegBandState(delta, theta, alpha, beta, gamma)
                dominantHz = 18.2f + 2.1f * fastWave
                coherence = (92 + (medWave * 5).roundToInt() - (extraBurst * 16).roundToInt()).coerceIn(58, 99)
            }
        }

        val rTint = (0.35f + bands.theta * 0.85f + extraBurst * 0.35f).coerceIn(0.25f, 1.50f)
        val gTint = (0.35f + bands.alpha * 0.90f).coerceIn(0.25f, 1.50f)
        val bTint = (0.35f + bands.beta * 0.95f).coerceIn(0.25f, 1.50f)

        val rgbVector = if (mode == RgbCognitiveMode.BIOMETRIC_AUTO) {
            floatArrayOf(rTint, gTint, bTint)
        } else {
            // Modulate the mode's base shader tint slightly with the live EEG oscillation
            val base = effectiveMode.shaderTint
            floatArrayOf(
                (base[0] * (0.92f + 0.16f * bands.theta)).coerceIn(0.2f, 1.6f),
                (base[1] * (0.92f + 0.16f * bands.alpha)).coerceIn(0.2f, 1.6f),
                (base[2] * (0.92f + 0.16f * bands.beta)).coerceIn(0.2f, 1.6f)
            )
        }

        val cogLoad = (
            bands.beta * 38f +
                bands.gamma * 32f +
                bands.theta * 18f -
                bands.alpha * 16f +
                extraBurst * 28f
            ).roundToInt().coerceIn(10, 98)

        return MockEegStreamPacket(
            mode = effectiveMode,
            bands = bands,
            dominantFrequencyHz = dominantHz,
            phaseCoherencePct = coherence,
            cognitiveLoadIndex = cogLoad,
            rgbSynchroVector = rgbVector,
            streamSequenceId = seqId
        )
    }
}

package com.example.ui.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.model.UnicriticVerdictLevel
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Configuration payload for [ShaderEffectModifier] that couples Unicritic critical drift
 * with real-time SY4-EEG biometric load intensity under the AX-IS-T Redline security protocol.
 */
data class ShaderEffectState(
    val isCriticalDrift: Boolean,
    val driftIntensity: Float,
    val biometricLoadIntensity: Float,
    val simTime: Float,
    val redlineThreatActive: Boolean = isCriticalDrift,
    val axisTVectorTag: String = "AX-IS-T // [TREAT SECURITY THREAT:REDLINE]"
) {
    /**
     * Combined distortion factor (0.0..1.0) driven by Unicritic drift and EEG biometric load.
     */
    val compositeDistortion: Float
        get() {
            if (!isCriticalDrift && driftIntensity <= 0.02f && !redlineThreatActive) return 0f
            val baseDrift = if (isCriticalDrift || redlineThreatActive) {
                driftIntensity.coerceAtLeast(0.45f)
            } else {
                driftIntensity
            }
            val bioMultiplier = (0.45f + biometricLoadIntensity.coerceIn(0f, 1f) * 0.85f)
            return (baseDrift * bioMultiplier).coerceIn(0f, 1.0f)
        }
}

/**
 * Compose [ShaderEffectModifier] that dynamically distorts the 3D tunnel visualization
 * when the Unicritic module signals critical drift, modulated by the current biometric load
 * intensity (`cognitiveLoadPct / 100f`) and `[TREAT SECURITY THREAT:REDLINE] // AX-IS-T` state.
 *
 * Can be invoked either as `Modifier.shaderEffectModifier(...)` or `ShaderEffectModifier(...)`.
 */
object ShaderEffectModifier {

    /**
     * AGSL RuntimeShader source for Android 13+ (API 33+) GPU pixel displacement & RGB split.
     */
    const val AX_IS_T_AGSL_SOURCE = """
        uniform shader content;
        uniform float2 resolution;
        uniform float time;
        uniform float drift;
        uniform float bioLoad;

        half4 main(float2 fragCoord) {
            float2 uv = fragCoord / max(resolution, float2(1.0));
            float intensity = clamp(drift * (0.45 + bioLoad * 0.85), 0.0, 1.0);
            if (intensity <= 0.01) {
                return content.eval(fragCoord);
            }
            // AX-IS-T horizontal shear wave + radial biometric barrel warp
            float2 centered = uv - 0.5;
            float r2 = dot(centered, centered);
            float wave = sin(uv.y * 28.0 + time * 14.0) * cos(uv.y * 9.0 - time * 6.0);
            float sliceJitter = step(0.82, sin(uv.y * 42.0 + time * 19.0)) * (sin(time * 37.0) * 0.018 * intensity);
            float2 displacedUv = uv;
            displacedUv.x += (wave * 0.014 * intensity) + sliceJitter;
            displacedUv += centered * (r2 * 0.16 * intensity * bioLoad);

            // Chromatic Redline [R] / Cyan [B] channel separation
            float2 chromaShift = float2(0.009 * intensity * (0.5 + bioLoad), 0.002 * intensity * sin(time * 8.0));
            half4 colR = content.eval((displacedUv + chromaShift) * resolution);
            half4 colG = content.eval(displacedUv * resolution);
            half4 colB = content.eval((displacedUv - chromaShift) * resolution);

            // Redline pulse vignette on outer perimeter
            float vignette = smoothstep(0.18, 0.75, r2) * intensity * 0.35;
            return half4(
                clamp(colR.r + vignette, 0.0, 1.0),
                colG.g * (1.0 - vignette * 0.35),
                clamp(colB.b + vignette * 0.25, 0.0, 1.0),
                colG.a
            );
        }
    """

    operator fun invoke(
        isCriticalDrift: Boolean,
        driftIntensity: Float,
        biometricLoadIntensity: Float,
        simTime: Float,
        redlineThreatActive: Boolean = isCriticalDrift,
        enabled: Boolean = true
    ): Modifier = Modifier.shaderEffectModifier(
        isCriticalDrift = isCriticalDrift,
        driftIntensity = driftIntensity,
        biometricLoadIntensity = biometricLoadIntensity,
        simTime = simTime,
        redlineThreatActive = redlineThreatActive,
        enabled = enabled
    )
}

/**
 * Extension modifier that applies dynamic 3D tunnel shader distortion when Unicritic signals
 * critical drift, scaled by the active SY4-EEG biometric load intensity.
 */
fun Modifier.shaderEffectModifier(
    isCriticalDrift: Boolean,
    driftIntensity: Float,
    biometricLoadIntensity: Float,
    simTime: Float,
    redlineThreatActive: Boolean = isCriticalDrift,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed Modifier

    val effectState = ShaderEffectState(
        isCriticalDrift = isCriticalDrift,
        driftIntensity = driftIntensity,
        biometricLoadIntensity = biometricLoadIntensity,
        simTime = simTime,
        redlineThreatActive = redlineThreatActive
    )
    val composite = effectState.compositeDistortion

    this
        .graphicsLayer {
            if (composite > 0.01f) {
                val bio = biometricLoadIntensity.coerceIn(0f, 1f)
                // Phase-stable harmonic oscillation (avoids multiplying growing simTime by time-varying bio)
                val bioPhaseOffset = bio * PI.toFloat() * 1.5f
                val pulse = sin(simTime * 6.0f + bioPhaseOffset)
                val harmonic = cos(simTime * 4.5f - bioPhaseOffset * 0.7f)

                // Dynamic geometric shear/scale/rotation driven by biometric load intensity
                val zoomWarp = 1f + (composite * 0.035f * (0.5f + bio * 0.5f) * abs(pulse))
                scaleX = zoomWarp + sin(simTime * 8.0f + bioPhaseOffset) * composite * 0.012f
                scaleY = zoomWarp + cos(simTime * 6.5f - bioPhaseOffset) * composite * 0.012f
                rotationZ = pulse * harmonic * composite * (1.8f * (0.4f + bio * 0.6f))
                translationX = sin(simTime * 9.0f + bioPhaseOffset) * composite * bio * 10f
                translationY = cos(simTime * 7.5f - bioPhaseOffset) * composite * bio * 6f

                // Attach hardware AGSL RuntimeShader on Android 13+ (API 33+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    size.width > 0f &&
                    size.height > 0f
                ) {
                    runCatching {
                        val runtimeShader = RuntimeShader(ShaderEffectModifier.AX_IS_T_AGSL_SOURCE)
                        runtimeShader.setFloatUniform("resolution", size.width, size.height)
                        runtimeShader.setFloatUniform("time", simTime)
                        runtimeShader.setFloatUniform("drift", driftIntensity.coerceAtLeast(if (isCriticalDrift) 0.55f else 0f))
                        runtimeShader.setFloatUniform("bioLoad", bio)
                        renderEffect = RenderEffect
                            .createRuntimeShaderEffect(runtimeShader, "content")
                            .asComposeRenderEffect()
                    }
                }
            }
        }
        .drawWithContent {
            if (composite <= 0.01f) {
                drawContent()
                return@drawWithContent
            }

            val bio = biometricLoadIntensity.coerceIn(0f, 1f)
            val jitterPx = (composite * (5f + bio * 14f)).dp.toPx()
            val phaseX = sin(simTime * 9.0f + bio * 2.0f) * jitterPx
            val phaseY = cos(simTime * 7.0f - bio * 1.5f) * (jitterPx * 0.35f)

            // 1. Primary 3D tunnel content pass with subtle AX-IS-T torsion transform
            withTransform({
                translate(left = phaseX * 0.28f, top = phaseY * 0.25f)
            }) {
                this@drawWithContent.drawContent()
            }

            // 2. Dynamic AX-IS-T Redline & Biometric Distortion Overlays
            drawAxIsTRedlineDistortionField(
                composite = composite,
                biometricLoad = bio,
                simTime = simTime,
                isCriticalRedline = isCriticalDrift || redlineThreatActive
            )
        }
}

private fun DrawScope.drawAxIsTRedlineDistortionField(
    composite: Float,
    biometricLoad: Float,
    simTime: Float,
    isCriticalRedline: Boolean
) {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return

    // 1. Biometric-modulated horizontal phase displacement slices (AX-IS-T shear bands)
    val sliceCount = (8 + (biometricLoad * 14f).roundToInt()).coerceIn(8, 22)
    for (i in 0 until sliceCount) {
        val normY = ((i.toFloat() / sliceCount) + (simTime * 0.28f + biometricLoad * 0.15f)) % 1f
        val yPos = normY * h
        val sliceHeight = (2.5f + composite * 7f * (i % 3 + 1) * 0.4f).dp.toPx()
        val shearOffset = sin(simTime * (8f + (i % 4) * 1.5f) + i * 1.7f + biometricLoad * 3f) * composite * (20f + biometricLoad * 32f).dp.toPx()

        // Redline [R] chromatic tear
        drawRect(
            color = NeonPink.copy(alpha = (composite * (0.14f + biometricLoad * 0.22f)).coerceIn(0.04f, 0.38f)),
            topLeft = Offset(x = shearOffset.coerceAtLeast(0f), y = yPos),
            size = Size(width = (w - abs(shearOffset)).coerceAtLeast(12f), height = sliceHeight),
            blendMode = BlendMode.Screen
        )

        // Counter-phase Cyan [B] harmonic tear
        if (i % 2 == 0) {
            drawLine(
                color = NeonCyan.copy(alpha = (composite * 0.32f).coerceIn(0.05f, 0.45f)),
                start = Offset(x = (-shearOffset).coerceAtLeast(0f), y = (yPos + sliceHeight)),
                end = Offset(x = w - shearOffset.coerceAtLeast(0f), y = (yPos + sliceHeight)),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }

    // 2. AX-IS-T Orthogonal Crosshair & Radial Distortion Shockwave
    val cx = w * 0.5f + sin(simTime * 9f) * composite * biometricLoad * 18.dp.toPx()
    val cy = h * 0.5f + cos(simTime * 7f) * composite * biometricLoad * 14.dp.toPx()
    val maxRad = minOf(w, h) * (0.28f + 0.18f * composite)

    val shockPath = Path()
    val segments = 64
    for (s in 0..segments) {
        val angle = (s.toFloat() / segments) * (2f * PI.toFloat())
        val eegHarmonic = sin(angle * 6f + simTime * 12f) * composite * biometricLoad * 18.dp.toPx()
        val r = maxRad + eegHarmonic
        val px = cx + cos(angle) * r
        val py = cy + sin(angle) * r
        if (s == 0) shockPath.moveTo(px, py) else shockPath.lineTo(px, py)
    }
    shockPath.close()

    drawPath(
        path = shockPath,
        color = if (isCriticalRedline) {
            NeonPink.copy(alpha = (0.28f + composite * 0.35f).coerceIn(0.1f, 0.7f))
        } else {
            NeonAmber.copy(alpha = (0.22f + composite * 0.25f).coerceIn(0.08f, 0.5f))
        },
        style = Stroke(width = (1.5f + composite * 2.5f).dp.toPx())
    )

    // 3. [TREAT SECURITY THREAT:REDLINE] Perimeter Containment Vignette & Reticle Brackets
    if (isCriticalRedline) {
        val pulseAlpha = (0.16f + abs(sin(simTime * 8f)) * 0.22f * composite).coerceIn(0.08f, 0.42f)
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    NeonPink.copy(alpha = pulseAlpha * 0.45f),
                    NeonPink.copy(alpha = pulseAlpha)
                ),
                center = Offset(cx, cy),
                radius = maxOf(w, h) * 0.65f
            ),
            size = size
        )

        // Corner lockdown brackets for AX-IS-T Redline containment
        val bracketLen = 28.dp.toPx()
        val pad = 14.dp.toPx()
        val strokeW = 2.5.dp.toPx()
        val bracketColor = NeonPink.copy(alpha = (0.55f + abs(sin(simTime * 10f)) * 0.4f).coerceIn(0.4f, 0.95f))

        // Top-Left
        drawLine(bracketColor, Offset(pad, pad), Offset(pad + bracketLen, pad), strokeW)
        drawLine(bracketColor, Offset(pad, pad), Offset(pad, pad + bracketLen), strokeW)
        // Top-Right
        drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad - bracketLen, pad), strokeW)
        drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad, pad + bracketLen), strokeW)
        // Bottom-Left
        drawLine(bracketColor, Offset(pad, h - pad), Offset(pad + bracketLen, h - pad), strokeW)
        drawLine(bracketColor, Offset(pad, h - pad), Offset(pad, h - pad - bracketLen), strokeW)
        // Bottom-Right
        drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad - bracketLen, h - pad), strokeW)
        drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad, h - pad - bracketLen), strokeW)
    }
}

/**
 * Helper to determine whether Unicritic is currently signaling critical drift or Redline threat.
 */
fun isUnicriticCriticalDrift(
    verdictLevel: UnicriticVerdictLevel,
    effectiveDrift: Float,
    redlineThreatOverride: Boolean = false
): Boolean {
    return redlineThreatOverride ||
        verdictLevel == UnicriticVerdictLevel.CRITICUS ||
        effectiveDrift >= 0.35f
}

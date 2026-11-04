package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HudTab
import com.example.model.ShaderPreset
import com.example.ui.theme.HudBorderCyan
import com.example.ui.theme.HudPanelBg
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AxiomUiState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.isActive

@Composable
fun AxiomGlSurfaceViewport(
    state: AxiomUiState,
    onFrameTelemetry: (Float, String) -> Unit,
    onShaderHealLog: (String, String) -> Unit,
    onOrbitalTouch: (Float, Float) -> Unit,
    onOrbitalTouchNormalized: (Float, Float, Float, Float) -> Unit = { x, y, _, _ -> onOrbitalTouch(x, y) },
    modifier: Modifier = Modifier
) {
    var lastFrameNanos by remember { mutableLongStateOf(0L) }

    // Measure real Choreographer frame delta without creating a blocking SurfaceSyncGroup SurfaceView
    LaunchedEffect(state.paused) {
        while (isActive) {
            withFrameNanos { nowNanos ->
                if (lastFrameNanos != 0L) {
                    val dtMs = ((nowNanos - lastFrameNanos) / 1_000_000f).coerceIn(4.0f, 120.0f)
                    onFrameTelemetry(dtMs, "Hardware Canvas GLSL-3D Pipe")
                }
                lastFrameNanos = nowNanos
            }
        }
    }

    val isCritical = isUnicriticCriticalDrift(
        verdictLevel = state.unicriticEvaluation.verdictLevel,
        effectiveDrift = state.effectiveUnicriticDrift,
        redlineThreatOverride = state.redlineThreatActive
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onOrbitalTouchNormalized(
                        offset.x,
                        offset.y,
                        size.width.toFloat().coerceAtLeast(1f),
                        size.height.toFloat().coerceAtLeast(1f)
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    onOrbitalTouchNormalized(
                        change.position.x,
                        change.position.y,
                        size.width.toFloat().coerceAtLeast(1f),
                        size.height.toFloat().coerceAtLeast(1f)
                    )
                }
            }
            .testTag("axiom_gl_surface_view")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    ShaderEffectModifier(
                        isCriticalDrift = isCritical,
                        driftIntensity = state.effectiveUnicriticDrift,
                        biometricLoadIntensity = state.biometricLoadIntensity,
                        simTime = state.simTime,
                        redlineThreatActive = state.redlineThreatActive,
                        enabled = state.unicriticDistortionEnabled
                    )
                )
                .testTag("axiom_tunnel_shader_effect_canvas")
        ) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas

            val tintR = state.effectiveRgbTint.getOrElse(0) { 0.25f }
            val tintG = state.effectiveRgbTint.getOrElse(1) { 1.05f }
            val tintB = state.effectiveRgbTint.getOrElse(2) { 1.40f }

            val primaryTint = Color(
                red = (0.15f * tintR).coerceIn(0f, 1f),
                green = (0.75f * tintG).coerceIn(0f, 1f),
                blue = (0.92f * tintB).coerceIn(0f, 1f),
                alpha = 1f
            )
            val reflexTint = Color(
                red = (0.85f * tintR).coerceIn(0f, 1f),
                green = (0.65f * tintG).coerceIn(0f, 1f),
                blue = (0.25f * tintB).coerceIn(0f, 1f),
                alpha = 1f
            )

            // Aspect-independent orbital camera offset from normalized touch + smooth rotating phase drift
            val camYaw = state.orbitalYawNorm * 1.4f + sin(state.rotationPhaseRad * 0.35f + state.simTime * 0.15f) * 0.14f
            val camPitch = state.orbitalPitchNorm * 1.1f + cos(state.rotationPhaseRad * 0.28f + state.simTime * 0.12f) * 0.10f

            val center = Offset(
                x = w * 0.5f + camYaw * w * 0.16f,
                y = h * 0.5f + camPitch * h * 0.14f
            )

            // Deep space radial background glow
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryTint.copy(alpha = 0.22f),
                        ObsidianBg
                    ),
                    center = center,
                    radius = maxOf(w, h) * 0.72f
                )
            )

            if (!state.glslGateTrue) {
                drawCelestialSanctuaryFallback(
                    state = state,
                    center = center
                )
            } else {
                when (state.activeShader) {
                    ShaderPreset.HEX_REFLEX -> drawHexReflexTunnel(
                        state = state,
                        center = center,
                        primaryTint = primaryTint,
                        reflexTint = reflexTint
                    )
                    ShaderPreset.FRACTAL -> drawMandelbulb3DProjection(
                        state = state,
                        center = center,
                        primaryTint = primaryTint,
                        reflexTint = reflexTint
                    )
                    ShaderPreset.VORONOI -> drawVoronoiLattice3D(
                        state = state,
                        center = center,
                        primaryTint = primaryTint,
                        reflexTint = reflexTint
                    )
                }
            }

            val atlas = state.atlasKnowabilityState
            val jumpZoom = atlas.effectiveJumpZoomFactor.coerceIn(0.65f, 1.55f)
            val cantRad = (atlas.intakeCantAngleDeg.coerceIn(0f, 55f) / 180f) * PI.toFloat()
            val cantTiltCos = cos(cantRad).coerceIn(0.48f, 1.0f)
            val cantTiltSin = sin(cantRad)

            // Open-Aperture Intake Cant O-Ring radii ("The center lines hides too you too much <or if lie>" + "Make intake cant o ring")
            val baseApertureRadius = minOf(w, h) * 0.25f * jumpZoom
            val oRingRx = baseApertureRadius * 1.18f
            val oRingRy = baseApertureRadius * cantTiltCos

            // 7-Color Prismatic Palette for Audio-Reactive Sonic Sound Swap Transfer "translate"
            val prismaticPalette = listOf(
                Color(0xFFFF1744), // Ruby Sub-Bass
                Color(0xFFFF6D00), // Tangerine Bass
                Color(0xFFFFD600), // Solar Gold Low-Mid
                Color(0xFF00E676), // Emerald Mid
                Color(0xFF00E5FF), // Electric Cyan Upper-Mid
                Color(0xFF7C4DFF), // Indigo Violet Presence
                Color(0xFFF50057)  // Magenta Brilliance
            )
            val sevenBands = atlas.sevenBandFft.asList()
            val swapPhaseShift = atlas.sonicSwapPhaseOffset + atlas.sonicSoundSwapMode.phaseStep

            // Audio-Reactive Colorful Sonic Sound Swap Transfer "translate" Prismatic Ribbons around the Open Cant O-Ring
            if (atlas.sonicSoundSwapTranslateActive) {
                sevenBands.forEachIndexed { bIdx, (_, bandAmp) ->
                    val swatchColor = if (atlas.asAtlasRedActive) {
                        AtlasCrimsonRed
                    } else {
                        prismaticPalette[(bIdx + swapPhaseShift) % prismaticPalette.size]
                    }
                    val ribbonPath = Path()
                    val segCount = 48
                    val phaseSpeed = state.rotationPhaseRad * (0.45f + bIdx * 0.08f) + state.simTime * 2.1f
                    val ampMod = (bandAmp * atlas.sonicSwapWaveBurst).coerceIn(0.08f, 1.35f)
                    for (s in 0..segCount) {
                        val u = s.toFloat() / segCount
                        val theta = u * (2f * PI.toFloat()) + bIdx * (PI.toFloat() / 3.5f)
                        val petalHarmonic = sin(theta * (3 + (bIdx % 4)) + phaseSpeed) *
                            (4.dp.toPx() + ampMod * 12.dp.toPx())
                        val rX = oRingRx * (1.03f + bIdx * 0.032f) + petalHarmonic
                        val rY = oRingRy * (1.03f + bIdx * 0.032f) + petalHarmonic * cantTiltCos
                        val cantShiftX = -sin(theta) * cantTiltSin * (baseApertureRadius * 0.16f)
                        val px = center.x + cos(theta) * rX + cantShiftX
                        val py = center.y + sin(theta) * rY
                        if (s == 0) ribbonPath.moveTo(px, py) else ribbonPath.lineTo(px, py)
                    }
                    ribbonPath.close()
                    // Soft iridescent outer glow + crisp harmonic core
                    drawPath(
                        path = ribbonPath,
                        color = swatchColor.copy(alpha = (0.14f + bandAmp * 0.20f).coerceIn(0.10f, 0.42f)),
                        style = Stroke(width = (3.5f + bandAmp * 3.0f).dp.toPx())
                    )
                    drawPath(
                        path = ribbonPath,
                        color = swatchColor.copy(alpha = (0.52f + bandAmp * 0.38f).coerceIn(0.35f, 0.92f)),
                        style = Stroke(width = 1.4.dp.toPx())
                    )
                }
            }

            // Core Tension Harmonic Strings wrapped around the Intake Cant O-Ring perimeter (leaving center 100% open & unobstructed!)
            val stringCount = 6
            val tension = state.coreTensionString.coerceIn(0.25f, 2.5f)
            for (sIdx in 0 until stringCount) {
                val baseAngle = (sIdx.toFloat() / stringCount) * (2f * PI.toFloat()) + state.rotationPhaseRad * 0.35f
                val stringPath = Path()
                val segments = 24
                for (seg in 0..segments) {
                    val u = seg.toFloat() / segments
                    val theta = baseAngle + (u - 0.5f) * 0.72f
                    val standingWave = sin(u * PI.toFloat() * (2f + tension)) *
                        cos(state.rotationPhaseRad * 2.0f + state.simTime * 3.2f + sIdx) *
                        (if (state.coreTensionPassDone) 3.dp.toPx() else 9.dp.toPx())
                    val rX = oRingRx * 1.16f + standingWave
                    val rY = oRingRy * 1.16f + standingWave * cantTiltCos
                    val px = center.x + cos(theta) * rX - sin(theta) * cantTiltSin * 6.dp.toPx()
                    val py = center.y + sin(theta) * rY
                    if (seg == 0) stringPath.moveTo(px, py) else stringPath.lineTo(px, py)
                }
                val stringColor = if (atlas.asAtlasRedActive) {
                    AtlasCrimsonRed.copy(alpha = 0.48f)
                } else {
                    prismaticPalette[(sIdx + swapPhaseShift) % prismaticPalette.size].copy(alpha = 0.52f)
                }
                drawPath(
                    path = stringPath,
                    color = stringColor,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Sweeping Emerald Self-Heal Wave Ring
            if (state.healWave > 0.01f) {
                val healRadius = (state.healWave / 1.45f) * maxOf(w, h) * 0.75f
                drawCircle(
                    color = NeonGreen.copy(alpha = (1f - state.healWave / 1.45f).coerceIn(0f, 0.85f)),
                    radius = healRadius.coerceAtLeast(8f),
                    center = center,
                    style = Stroke(width = 6.dp.toPx())
                )
            }

            // Unicritic Critical Drift Chromatic Shear Bands (kept outside the open center aperture)
            if (state.effectiveUnicriticDrift > 0.02f) {
                val drift = state.effectiveUnicriticDrift
                val bandCount = 8
                for (b in 0 until bandCount) {
                    val phase = (b / bandCount.toFloat()) * h
                    val yPos = (phase + (state.simTime * 190f)) % h
                    if (kotlin.math.abs(yPos - center.y) > oRingRy * 0.82f) {
                        val shearX = sin(state.simTime * 14f + b) * drift * 36.dp.toPx()
                        drawLine(
                            color = NeonPink.copy(alpha = (drift * 0.42f).coerceIn(0.05f, 0.55f)),
                            start = Offset(0f + shearX.coerceAtLeast(0f), yPos),
                            end = Offset(w + shearX.coerceAtMost(0f), yPos),
                            strokeWidth = (2.2f + drift * 3.5f).dp.toPx()
                        )
                    }
                }
            }

            // 3D Perspective INTAKE CANT O-RING ("Make intake cant o ring" + "The center lines hides too you too much <or if lie>")
            // Zero center-crossing lines: the inner core is 100% open so the user can jump in & out and visualise clearly!
            val spineColor = if (atlas.asAtlasRedActive) AtlasCrimsonRed else NeonCyan
            val oRingSteps = 64
            val outerORingPath = Path()
            val innerORingPath = Path()
            for (i in 0..oRingSteps) {
                val theta = (i.toFloat() / oRingSteps) * (2f * PI.toFloat())
                val cosT = cos(theta)
                val sinT = sin(theta)
                // Canted 3D elliptical projection
                val cantShiftX = -sinT * cantTiltSin * (baseApertureRadius * 0.18f)
                val outerX = center.x + cosT * (oRingRx * 1.08f) + cantShiftX
                val outerY = center.y + sinT * (oRingRy * 1.08f)
                val innerX = center.x + cosT * (oRingRx * 0.92f) + cantShiftX * 0.85f
                val innerY = center.y + sinT * (oRingRy * 0.92f)
                if (i == 0) {
                    outerORingPath.moveTo(outerX, outerY)
                    innerORingPath.moveTo(innerX, innerY)
                } else {
                    outerORingPath.lineTo(outerX, outerY)
                    innerORingPath.lineTo(innerX, innerY)
                }
            }
            outerORingPath.close()
            innerORingPath.close()

            // Outer & Inner Canted Torus O-Ring Gasket Lips
            drawPath(
                path = outerORingPath,
                color = spineColor.copy(alpha = if (atlas.asAtlasRedActive) 0.78f else 0.52f),
                style = Stroke(width = if (atlas.asAtlasRedActive) 3.2.dp.toPx() else 2.2.dp.toPx())
            )
            drawPath(
                path = innerORingPath,
                color = (if (atlas.orIfLieDetected) AtlasCrimsonRed else NeonGreen).copy(alpha = 0.65f),
                style = Stroke(width = 1.6.dp.toPx())
            )

            // Left Canted O-Ring Arc: RGB Knowability Visual Discriminator
            val rgbArcPath = Path()
            for (i in 18..46) {
                val theta = (i.toFloat() / oRingSteps) * (2f * PI.toFloat())
                val px = center.x + cos(theta) * oRingRx - sin(theta) * cantTiltSin * (baseApertureRadius * 0.16f)
                val py = center.y + sin(theta) * oRingRy
                if (i == 18) rgbArcPath.moveTo(px, py) else rgbArcPath.lineTo(px, py)
            }
            drawPath(
                path = rgbArcPath,
                color = if (atlas.rgbKnowable) NeonGreen else AtlasCrimsonRed,
                style = Stroke(width = 4.2.dp.toPx())
            )

            // Right Canted O-Ring Arc: FRQ 7-Band Knowability Visual Discriminator
            val frqArcPath = Path()
            for (i in -14..14) {
                val theta = (i.toFloat() / oRingSteps) * (2f * PI.toFloat())
                val px = center.x + cos(theta) * oRingRx - sin(theta) * cantTiltSin * (baseApertureRadius * 0.16f)
                val py = center.y + sin(theta) * oRingRy
                if (i == -14) frqArcPath.moveTo(px, py) else frqArcPath.lineTo(px, py)
            }
            drawPath(
                path = frqArcPath,
                color = if (atlas.frqKnowable) NeonCyan else AtlasCrimsonRed,
                style = Stroke(width = 4.2.dp.toPx())
            )

            // 5 File-Line Thinking Spine Nodes (L0..L4) orbiting the Intake Cant O-Ring perimeter (never blocking center)
            atlas.centerFileLines.forEachIndexed { idx, node ->
                val nodeAngle = (-PI.toFloat() * 0.5f) + (idx.toFloat() / 5f) * (2f * PI.toFloat()) + state.rotationPhaseRad * 0.12f
                val nx = center.x + cos(nodeAngle) * (oRingRx * 1.0f) - sin(nodeAngle) * cantTiltSin * (baseApertureRadius * 0.16f)
                val ny = center.y + sin(nodeAngle) * (oRingRy * 1.0f)
                val nodeCol = when {
                    node.isAtlasRedAlert -> AtlasCrimsonRed
                    node.lineCode == atlas.selectedFileLineCode -> NeonCyan
                    else -> prismaticPalette[(idx + swapPhaseShift) % prismaticPalette.size]
                }
                drawCircle(
                    color = nodeCol.copy(alpha = 0.36f),
                    radius = 7.5.dp.toPx(),
                    center = Offset(nx, ny)
                )
                drawCircle(
                    color = nodeCol,
                    radius = 3.8.dp.toPx(),
                    center = Offset(nx, ny)
                )
            }

            // If As Atlas (RED) or <or if lie> is active, draw a peripheral bearing indicator on the O-Ring rim (not crossing center!)
            if (atlas.asAtlasRedActive) {
                val bearingRad = (atlas.atlasBearingDeg.toFloat() / 180f) * PI.toFloat()
                val rimInner = Offset(
                    center.x + cos(bearingRad) * (oRingRx * 0.88f),
                    center.y + sin(bearingRad) * (oRingRy * 0.88f)
                )
                val rimOuter = Offset(
                    center.x + cos(bearingRad) * (oRingRx * 1.24f),
                    center.y + sin(bearingRad) * (oRingRy * 1.24f)
                )
                drawLine(
                    color = AtlasCrimsonRed,
                    start = rimInner,
                    end = rimOuter,
                    strokeWidth = 3.5.dp.toPx()
                )
            }

            // Subtle scanlines overlay
            val stepPx = 6.dp.toPx()
            var y = 0f
            while (y < h) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.18f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.5f
                )
                y += stepPx
            }
        }

        // Open Intake Cant O-Ring, <or if lie>, Jump In/Out & RGB/FRQ Knowability Floating Readout
        val atlasOverlay = state.atlasKnowabilityState
        val bannerColor = if (atlasOverlay.asAtlasRedActive) AtlasCrimsonRed else NeonCyan
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianBg.copy(alpha = 0.86f))
                .border(1.dp, bannerColor.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("viewport_center_file_line_banner")
        ) {
            Text(
                text = if (atlasOverlay.rgbKnowable) "RGB: KNOWABLE" else "RGB: NOT KNOWABLE!",
                style = MaterialTheme.typography.labelSmall,
                color = if (atlasOverlay.rgbKnowable) NeonGreen else AtlasCrimsonRed,
                fontWeight = FontWeight.Bold,
                fontSize = 7.6.sp
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 7.6.sp
            )
            Text(
                text = if (atlasOverlay.frqKnowable) "FRQ: KNOWABLE (${"%.1f".format(state.dominantFrequencyHz)}Hz)" else "FRQ: NOT KNOWABLE!",
                style = MaterialTheme.typography.labelSmall,
                color = if (atlasOverlay.frqKnowable) NeonCyan else AtlasCrimsonRed,
                fontWeight = FontWeight.Bold,
                fontSize = 7.6.sp
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 7.6.sp
            )
            Text(
                text = if (atlasOverlay.orIfLieDetected) "<or if lie>: LIE!" else "CANT O-RING ${"%.0f".format(atlasOverlay.intakeCantAngleDeg)}°",
                style = MaterialTheme.typography.labelSmall,
                color = if (atlasOverlay.orIfLieDetected) AtlasCrimsonRed else NeonPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 7.6.sp
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 7.6.sp
            )
            Text(
                text = if (atlasOverlay.asAtlasRedActive) {
                    "AS ATLAS (RED) // ON LOST PATH"
                } else {
                    "JUMP ${"%.2f".format(atlasOverlay.effectiveJumpZoomFactor)}x [${atlasOverlay.selectedFileLineCode}]"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (atlasOverlay.asAtlasRedActive) AtlasCrimsonRed else NeonGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 7.6.sp
            )
        }
    }
}

private fun DrawScope.drawHexReflexTunnel(
    state: AxiomUiState,
    center: Offset,
    primaryTint: Color,
    reflexTint: Color
) {
    val w = size.width
    val h = size.height
    val minDim = minOf(w, h)
    val jumpZoom = state.atlasKnowabilityState.effectiveJumpZoomFactor.coerceIn(0.65f, 1.55f)
    // Widescreen panoramic span scaled by Jump In / Out factor so the user can jump in and out and visualise well
    val wideSpan = maxOf(minDim, w * 0.58f) * jumpZoom
    val depthLayers = (state.maxSteps / 14).coerceIn(4, 12)
    val hexScale = state.hexScale.coerceIn(0.6f, 4.0f)
    val drift = state.effectiveUnicriticDrift
    val entropy = state.perturbation
    val dirSign = if (state.rotationDirectionCw) 1f else -1f

    // Hexagonal domain repetition cells arranged in concentric 3D depth rings
    val hexOffsets = listOf(
        0f to 0f,
        1f to 0f,
        -1f to 0f,
        0.5f to 0.866f,
        -0.5f to 0.866f,
        0.5f to -0.866f,
        -0.5f to -0.866f
    )

    for (layer in depthLayers downTo 1) {
        val zProgress = ((layer.toFloat() / depthLayers) - ((state.simTime * 0.32f) % (1f / depthLayers)) + 1f) % 1f
        val perspectiveScale = (0.14f + zProgress * 1.15f) / (hexScale * 0.52f)
        val depthAlpha = (1f - zProgress * 0.72f).coerceIn(0.14f, 0.94f)

        val breathWave = sin(state.simTime * 2.4f + layer * 0.85f)
        val antiMagScale = 1.0f + 0.14f * breathWave * state.antiMagPulse

        // Smooth phase-integrated Rotating Frequency across 3D depth layers
        val helicalTwist = state.rotationPhaseRad + layer * 0.24f * dirSign
        val cosTwist = cos(helicalTwist)
        val sinTwist = sin(helicalTwist)

        hexOffsets.forEachIndexed { cellIdx, (hx, hy) ->
            // Rotate the 2D hexagonal lattice cell coordinates around the tunnel axis
            val rotHx = hx * cosTwist - hy * sinTwist
            val rotHy = hx * sinTwist + hy * cosTwist

            // Magnetic curl offset
            val curlX = sin(state.simTime * 1.2f + layer * 0.6f + cellIdx) * state.magPressure * 9f
            val curlY = cos(state.simTime * 1.1f - layer * 0.5f + cellIdx) * state.magPressure * 9f

            // Fracture perturbation jitter
            val fracX = if (entropy > 0.01f) sin(state.simTime * 11f + layer * 3f + cellIdx) * entropy * 26f else 0f
            val fracY = if (entropy > 0.01f) cos(state.simTime * 9.5f - layer * 2f + cellIdx) * entropy * 26f else 0f

            // Unicritic horizontal phase shear
            val driftX = if (drift > 0.02f) sin(state.simTime * 15f + layer) * drift * 28f else 0f

            val cellSpacing = wideSpan * 0.26f * perspectiveScale
            val cellCenter = Offset(
                x = center.x + rotHx * cellSpacing + curlX + fracX + driftX,
                y = center.y + rotHy * cellSpacing + curlY + fracY
            )
            val cellRadius = (wideSpan * 0.125f * perspectiveScale * antiMagScale).coerceAtLeast(4f)

            // 1. Reflex Radial Light Core inside each hexagonal cell
            val reflexAlpha = (0.16f * state.reflexDensity * depthAlpha).coerceIn(0.03f, 0.65f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        reflexTint.copy(alpha = reflexAlpha),
                        primaryTint.copy(alpha = reflexAlpha * 0.45f),
                        Color.Transparent
                    ),
                    center = cellCenter,
                    radius = (cellRadius * 1.25f).coerceAtLeast(6f)
                ),
                radius = (cellRadius * 1.25f).coerceAtLeast(6f),
                center = cellCenter
            )

            // 2. Anti-Magnet Shifting Boundary Layer Shell (counter-rotating harmonic)
            if (state.antiMagPulse > 0.05f) {
                drawPolygonHex(
                    center = cellCenter,
                    radius = cellRadius * 0.86f,
                    rotationRad = -helicalTwist * 0.65f + cellIdx * 0.2f,
                    color = NeonPink.copy(alpha = (0.28f * state.antiMagPulse * depthAlpha).coerceIn(0.04f, 0.7f)),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // 3. Outer Hexagonal Crystalline Wall (phase-locked to Rotating Frequency)
            val wallColor = if (entropy > 0.25f) {
                NeonAmber.copy(alpha = depthAlpha * 0.8f)
            } else {
                primaryTint.copy(alpha = depthAlpha * 0.78f)
            }
            drawPolygonHex(
                center = cellCenter,
                radius = cellRadius,
                rotationRad = helicalTwist,
                color = wallColor,
                strokeWidth = (1.8f + zProgress * 1.6f).dp.toPx()
            )
        }
    }
}

private fun DrawScope.drawMandelbulb3DProjection(
    state: AxiomUiState,
    center: Offset,
    primaryTint: Color,
    reflexTint: Color
) {
    val jumpZoom = state.atlasKnowabilityState.effectiveJumpZoomFactor.coerceIn(0.65f, 1.55f)
    val wideSpan = maxOf(minOf(size.width, size.height), size.width * 0.56f) * jumpZoom
    val baseRadius = (wideSpan * 0.32f / (state.hexScale * 0.55f).coerceAtLeast(0.4f)).coerceAtLeast(24f)
    val lobes = 8
    val rings = (state.maxSteps / 12).coerceIn(5, 16)

    for (r in rings downTo 1) {
        val ratio = r.toFloat() / rings
        val rad = baseRadius * ratio
        val path = Path()
        val steps = 72
        val ringRot = state.rotationPhaseRad + r * 0.18f
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * (2f * PI.toFloat()) + ringRot
            val powerMod = sin((theta - ringRot) * lobes + state.rotationPhaseRad * 1.5f + r * 0.4f)
            val perturbMod = sin(theta * 19f - state.simTime * 8f) * state.perturbation * 0.25f
            val rMod = rad * (1f + 0.22f * powerMod + perturbMod)
            val px = center.x + cos(theta) * rMod
            val py = center.y + sin(theta) * rMod
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()
        val col = if (r % 2 == 0) primaryTint else reflexTint
        drawPath(
            path = path,
            color = col.copy(alpha = (0.22f * state.reflexDensity * (1f - ratio * 0.5f)).coerceIn(0.06f, 0.75f)),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

private fun DrawScope.drawVoronoiLattice3D(
    state: AxiomUiState,
    center: Offset,
    primaryTint: Color,
    reflexTint: Color
) {
    val jumpZoom = state.atlasKnowabilityState.effectiveJumpZoomFactor.coerceIn(0.65f, 1.55f)
    val w = size.width
    val h = size.height
    val cols = 6
    val rows = 6
    val spacingX = w / cols
    val spacingY = h / rows
    val cosRot = cos(state.rotationPhaseRad * 0.35f)
    val sinRot = sin(state.rotationPhaseRad * 0.35f)

    val nodes = mutableListOf<Offset>()
    for (gx in 0..cols) {
        for (gy in 0..rows) {
            val phase = (gx * 17 + gy * 31).toFloat()
            val ox = sin(state.rotationPhaseRad + phase + state.perturbation * 4f) * spacingX * 0.36f
            val oy = cos(state.rotationPhaseRad * 0.85f - phase * 0.7f) * spacingY * 0.36f
            val rawX = gx * spacingX + ox - w * 0.5f
            val rawY = gy * spacingY + oy - h * 0.5f
            val rx = rawX * cosRot - rawY * sinRot
            val ry = rawX * sinRot + rawY * cosRot
            nodes.add(
                Offset(
                    x = center.x + rx * 0.85f * jumpZoom,
                    y = center.y + ry * 0.85f * jumpZoom
                )
            )
        }
    }

    val maxConnectDist = maxOf(minOf(w, h), w * 0.55f) * 0.32f * jumpZoom
    for (i in nodes.indices) {
        val a = nodes[i]
        drawCircle(
            color = reflexTint.copy(alpha = 0.65f),
            radius = 4.dp.toPx(),
            center = a
        )
        for (j in i + 1 until nodes.size) {
            val b = nodes[j]
            val dx = a.x - b.x
            val dy = a.y - b.y
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
            if (dist < maxConnectDist) {
                val alpha = ((1f - dist / maxConnectDist) * 0.45f * state.reflexDensity).coerceIn(0.04f, 0.7f)
                drawLine(
                    color = primaryTint.copy(alpha = alpha),
                    start = a,
                    end = b,
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }
    }
}

private fun DrawScope.drawPolygonHex(
    center: Offset,
    radius: Float,
    rotationRad: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    for (i in 0 until 6) {
        val angle = rotationRad + i * (PI.toFloat() / 3f)
        val px = center.x + cos(angle) * radius
        val py = center.y + sin(angle) * radius
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

/**
 * Luminous Celestial Aurora & Sacred Golden-Angle Lotus Sanctuary rendered when
 * `As to GLSL == FALSE -> DENY` is active ("I want to see something beautiful").
 */
private fun DrawScope.drawCelestialSanctuaryFallback(
    state: AxiomUiState,
    center: Offset
) {
    val w = size.width
    val h = size.height
    val maxDim = maxOf(w, h)
    val minDim = minOf(w, h)
    val t = state.simTime

    // 1. Deep Celestial Twilight Nebula Halo
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                NeonPurple.copy(alpha = 0.42f),
                NeonCyan.copy(alpha = 0.25f),
                NeonPink.copy(alpha = 0.14f),
                ObsidianBg
            ),
            center = center,
            radius = maxDim * 0.82f
        )
    )

    // 2. Iridescent Aurora Borealis Silk Ribbons
    val auroraPalette = listOf(
        NeonCyan,
        NeonPurple,
        NeonGreen,
        NeonPink,
        NeonAmber
    )
    val ribbonCount = auroraPalette.size
    for (rIdx in 0 until ribbonCount) {
        val baseColor = auroraPalette[rIdx]
        val yBase = h * (0.20f + rIdx * 0.14f)
        val path = Path()
        val segments = 32
        for (s in 0..segments) {
            val u = s.toFloat() / segments
            val px = u * w
            val wave1 = sin(u * PI.toFloat() * 2.5f + t * (0.65f + rIdx * 0.12f) + rIdx) * (minDim * 0.09f)
            val wave2 = cos(u * PI.toFloat() * 4.2f - t * 0.48f + rIdx * 1.7f) * (minDim * 0.045f)
            val py = yBase + wave1 + wave2
            if (s == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        // Soft wide aurora glow + crisp filament core
        drawPath(
            path = path,
            color = baseColor.copy(alpha = 0.18f),
            style = Stroke(width = 22.dp.toPx())
        )
        drawPath(
            path = path,
            color = baseColor.copy(alpha = 0.42f),
            style = Stroke(width = 6.dp.toPx())
        )
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.55f),
            style = Stroke(width = 1.4.dp.toPx())
        )
    }

    // 3. 12-Fold Sacred Geometric Celestial Lotus Mandala at Focal Center
    val petalCount = 12
    val lotusBaseRadius = minDim * 0.28f
    for (layer in 0 until 3) {
        val layerScale = 1.0f - layer * 0.24f
        val layerRot = t * (if (layer % 2 == 0) 0.22f else -0.18f) + layer * 0.26f
        val layerColor = when (layer) {
            0 -> NeonCyan
            1 -> NeonPurple
            else -> NeonAmber
        }
        val lotusPath = Path()
        val steps = 180
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * PI.toFloat() * 2f
            val petalMod = 0.55f + 0.45f * cos(petalCount * 0.5f * theta)
            val breath = 1.0f + 0.06f * sin(t * 1.6f + layer)
            val r = lotusBaseRadius * layerScale * petalMod * breath
            val px = center.x + cos(theta + layerRot) * r
            val py = center.y + sin(theta + layerRot) * r
            if (i == 0) lotusPath.moveTo(px, py) else lotusPath.lineTo(px, py)
        }
        lotusPath.close()
        drawPath(
            path = lotusPath,
            color = layerColor.copy(alpha = 0.24f),
            style = Stroke(width = 5.dp.toPx())
        )
        drawPath(
            path = lotusPath,
            color = layerColor.copy(alpha = 0.82f),
            style = Stroke(width = 1.6.dp.toPx())
        )
    }

    // 4. Golden-Angle (137.50776°) Phyllotaxis Bioluminescent Stardust Spiral
    val goldenAngleRad = (137.50776f * (PI / 180.0)).toFloat()
    val starCount = 96
    for (idx in 1..starCount) {
        val norm = idx.toFloat() / starCount
        val angle = idx * goldenAngleRad + t * 0.16f
        val radius = kotlin.math.sqrt(norm) * (minDim * 0.43f)
        val sx = center.x + cos(angle) * radius
        val sy = center.y + sin(angle) * radius
        val pulse = (0.5f + 0.5f * sin(t * 2.8f + idx * 0.35f)).coerceIn(0.15f, 1.0f)
        val starColor = auroraPalette[idx % auroraPalette.size]

        drawCircle(
            color = starColor.copy(alpha = 0.32f * pulse),
            radius = (4.5f + 3.0f * pulse).dp.toPx(),
            center = Offset(sx, sy)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.88f * pulse),
            radius = 1.8.dp.toPx(),
            center = Offset(sx, sy)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AxiomTopTelemetryHeader(
    state: AxiomUiState,
    onToggleBlockMind: () -> Unit,
    onOpenUnicriticTab: () -> Unit,
    onOpenGlslModal: () -> Unit,
    onToggleHud: () -> Unit,
    onOpenScreenFilterAntModal: () -> Unit = {},
    onOpenScreenShareModal: () -> Unit = {},
    onToggleWidescreenStage: () -> Unit = {},
    onToggleLandscapeLock: () -> Unit = {},
    onOpenSpectSensorDashboard: () -> Unit = {},
    onOpenCodexWhitepaper: () -> Unit = {},
    onOpenProtocolOSublayerGate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val integrityPct = ((1f - state.perturbation) * 100f).roundToInt().coerceIn(0, 100)
    val eval = state.unicriticEvaluation

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HudPanelBg)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.14f))
                        .border(1.dp, eval.verdictLevel.color.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(eval.verdictLevel.color)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "SY4-EEG // AXIOM",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonCyan
                        )
                        StatusBadge(
                            text = state.contractPhase.latinTitle,
                            color = state.contractPhase.badgeColor
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(eval.verdictLevel.color.copy(alpha = 0.18f))
                                .border(1.dp, eval.verdictLevel.color, RoundedCornerShape(4.dp))
                                .clickable { onOpenUnicriticTab() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("badge_top_unicritic")
                        ) {
                            Text(
                                text = "UNICRITIC: ${eval.consensusScore}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = eval.verdictLevel.color,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val deployOk = state.localInterconnectState.checkPassTrueOnly
                        val deployCol = if (deployOk) NeonGreen else NeonPink
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(deployCol.copy(alpha = 0.18f))
                                .border(1.dp, deployCol, RoundedCornerShape(4.dp))
                                .clickable { onOpenSpectSensorDashboard() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("badge_header_check_pass_true_only")
                        ) {
                            Text(
                                text = state.localInterconnectState.assertionToken,
                                style = MaterialTheme.typography.labelSmall,
                                color = deployCol,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonPurple.copy(alpha = 0.18f))
                                .border(1.dp, NeonPurple, RoundedCornerShape(4.dp))
                                .clickable { onOpenSpectSensorDashboard() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("badge_header_spect_sensor_bus")
                        ) {
                            Text(
                                text = "SPECT-BUS: ${state.spectSensorDashboardState.activeSensorPathsCount}/6",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPurple,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonGreen.copy(alpha = 0.18f))
                                .border(1.dp, NeonGreen, RoundedCornerShape(4.dp))
                                .clickable { onOpenCodexWhitepaper() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("badge_header_personal_dev_own_logic")
                        ) {
                            Text(
                                text = "${state.unifiedDeployWhitepaperState.personalDevAccountTag} • ${state.unifiedDeployWhitepaperState.ownDevLogicCanonicalAlias}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonGreen,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val atlasBadgeCol = if (state.atlasKnowabilityState.asAtlasRedActive) AtlasCrimsonRed else NeonCyan
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(atlasBadgeCol.copy(alpha = 0.20f))
                                .border(1.dp, atlasBadgeCol, RoundedCornerShape(4.dp))
                                .clickable { onOpenSpectSensorDashboard() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("badge_header_atlas_knowability")
                        ) {
                            Text(
                                text = if (state.atlasKnowabilityState.asAtlasRedActive) {
                                    state.atlasKnowabilityState.atlasStateBadge
                                } else {
                                    "RGB•FRQ: KNOWABLE"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = atlasBadgeCol,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "${state.asIfTunnelState.label} • ROT FRQ: ${"%.2f".format(state.effectiveRotatingFrequencyHz)} Hz • ${if (state.atlasKnowabilityState.asAtlasRedActive) "AS ATLAS (RED) // ON LOST PATH" else "CENTER FILE-LINE [${state.atlasKnowabilityState.selectedFileLineCode}]"} • ${state.protocolOState.activeProvider.badge}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.atlasKnowabilityState.asAtlasRedActive) AtlasCrimsonRed else state.asIfTunnelState.color,
                        fontSize = 9.sp
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StealthGateOrbButton(
                    orbKeyActive = state.protocolOState.orbKeyActive,
                    onClickOrb = onOpenProtocolOSublayerGate
                )
                HeaderActionChip(
                    label = if (state.widescreenLandscapeLocked) "WIDE [LOCKED]" else "WIDE [16:9]",
                    accent = if (state.widescreenLandscapeLocked || state.widescreenStageEnabled) NeonCyan else TextSecondary,
                    onClick = onToggleLandscapeLock,
                    testTag = "btn_header_widescreen_toggle"
                )
                HeaderActionChip(
                    label = "SHARE [BEST]",
                    accent = NeonGreen,
                    onClick = onOpenScreenShareModal,
                    testTag = "btn_header_screen_share_best_work"
                )
                HeaderActionChip(
                    label = if (state.screenFilterAntMode != ScreenFilterAntMode.OFF) {
                        "ANT-FILTER"
                    } else {
                        "ANT [OFF]"
                    },
                    accent = if (state.screenFilterAntMode != ScreenFilterAntMode.OFF) {
                        state.screenFilterAntMode.tintColor
                    } else {
                        TextSecondary
                    },
                    onClick = onOpenScreenFilterAntModal,
                    testTag = "btn_header_screen_filter_ant"
                )
                HeaderActionChip(
                    label = when {
                        state.unblockMindActive -> "UNBLOCK [+]"
                        state.blockMindStasis -> "BLOCK [-]"
                        else -> "BLOCKMIND [+-]"
                    },
                    accent = when {
                        state.unblockMindActive -> NeonGreen
                        state.blockMindStasis -> NeonPurple
                        else -> NeonCyan
                    },
                    onClick = onToggleBlockMind,
                    testTag = "btn_blockmind_toggle"
                )
                HeaderIconButton(
                    icon = Icons.Default.Code,
                    contentDesc = "Inspect GLSL Shader Code",
                    onClick = onOpenGlslModal,
                    testTag = "btn_open_glsl_code"
                )
                HeaderIconButton(
                    icon = if (state.hudVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDesc = "Toggle HUD Overlay",
                    onClick = onToggleHud,
                    testTag = "btn_toggle_hud"
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianBg.copy(alpha = 0.78f))
                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TelemetryCounterItem(
                label = "FPS",
                value = "${state.currentFps}",
                color = if (state.currentFps >= 45) NeonGreen else NeonAmber
            )
            TelemetryDivider()
            TelemetryCounterItem(
                label = "ROT-FRQ",
                value = "${"%.2f".format(state.effectiveRotatingFrequencyHz)}Hz",
                color = NeonCyan
            )
            TelemetryDivider()
            TelemetryCounterItem(
                label = "DEPLOY",
                value = if (state.localInterconnectState.checkPassTrueOnly) "true_only" else "BLOCKED",
                color = if (state.localInterconnectState.checkPassTrueOnly) NeonGreen else NeonPink
            )
            TelemetryDivider()
            TelemetryCounterItem(
                label = "PAIDvsFREE",
                value = "SUBS:$0 • -${state.unifiedDeployWhitepaperState.decayMetrics.corpGreedDecayPct}%",
                color = NeonGreen
            )
            TelemetryDivider()
            TelemetryCounterItem(
                label = "COG-LOAD",
                value = "${state.cognitiveLoadPct}%",
                color = if (state.cognitiveLoadPct > 75) NeonPink else NeonGreen
            )
            TelemetryDivider()
            TelemetryCounterItem(
                label = "CRITIC",
                value = eval.verdictLevel.title,
                color = eval.verdictLevel.color
            )
            TelemetryDivider()
            TelemetryCounterItem(
                label = "FAILLOAD",
                value = if (state.failloadGuardEnabled) "SAFE #${state.failloadInterceptCount}" else "BYPASS",
                color = if (state.failloadGuardEnabled) NeonGreen else NeonAmber
            )
            TelemetryDivider()
            TelemetryCounterItem(
                label = "KNOWABLE",
                value = when {
                    state.atlasKnowabilityState.asAtlasRedActive -> "ATLAS [RED]"
                    state.atlasKnowabilityState.rgbKnowable && state.atlasKnowabilityState.frqKnowable -> "RGB•FRQ OK"
                    else -> "UNKNOWABLE"
                },
                color = if (state.atlasKnowabilityState.asAtlasRedActive) AtlasCrimsonRed else NeonGreen
            )
        }
    }
}

/**
 * Dedicated 16:9 Widescreen 3D Tunnel Viewport Stage with interactive orbital control,
 * Center File-Line Thinking Spine & RGB/FRQ Knowability + As Atlas (RED) Lost-Path toggles,
 * and quick Rotating Frequency (`ROTATING FRQ`) controls so the 3D tunnel is never covered
 * by HUD cards even on compact portrait screens.
 */
@Composable
fun WidescreenTunnelStageBar(
    state: AxiomUiState,
    onFrameTelemetry: (Float, String) -> Unit,
    onShaderHealLog: (String, String) -> Unit,
    onOrbitalTouchNormalized: (Float, Float, Float, Float) -> Unit,
    onStepRotatingFrq: (Float) -> Unit,
    onToggleRotationDir: () -> Unit,
    onToggleEegSync: () -> Unit,
    onToggleLandscapeLock: () -> Unit,
    onToggleRgbKnowable: () -> Unit = {},
    onToggleFrqKnowable: () -> Unit = {},
    onToggleAtlasLostPath: () -> Unit = {},
    onToggleOrIfLie: () -> Unit = {},
    onCycleIntakeJumpMode: () -> Unit = {},
    onTriggerSonicSoundSwapTranslate: () -> Unit = {},
    onReanchorAtlasAndRestoreKnowability: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(HudPanelBg)
            .border(1.dp, NeonCyan.copy(alpha = 0.75f), RoundedCornerShape(10.dp))
            .testTag("widescreen_tunnel_stage_card")
    ) {
        // 16:9 Widescreen 3D Tunnel Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(172.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
        ) {
            AxiomGlSurfaceViewport(
                state = state,
                onFrameTelemetry = onFrameTelemetry,
                onShaderHealLog = onShaderHealLog,
                onOrbitalTouch = { _, _ -> },
                onOrbitalTouchNormalized = onOrbitalTouchNormalized,
                modifier = Modifier.fillMaxSize()
            )
            ScreenFilterAntViewportOverlay(
                state = state,
                modifier = Modifier.fillMaxSize()
            )

            // Top-left Widescreen Stage & Preset Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg.copy(alpha = 0.78f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "WIDE 16:9 // ${state.activeShader.title.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• ${"%.2f".format(state.effectiveRotatingFrequencyHz)} Hz (${(state.effectiveRotatingFrequencyHz * 60f).roundToInt()} RPM)",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Top-right Full Widescreen Landscape Toggle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (state.widescreenLandscapeLocked) NeonGreen.copy(alpha = 0.25f)
                        else ObsidianBg.copy(alpha = 0.82f)
                    )
                    .border(
                        1.dp,
                        if (state.widescreenLandscapeLocked) NeonGreen else NeonCyan,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onToggleLandscapeLock() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("btn_stage_landscape_lock")
            ) {
                Text(
                    text = if (state.widescreenLandscapeLocked) "LANDSCAPE: LOCKED" else "⇄ FULL WIDE [16:9]",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.widescreenLandscapeLocked) NeonGreen else NeonCyan,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Quick Rotating Frequency Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianBg.copy(alpha = 0.9f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.16f))
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .clickable { onStepRotatingFrq(-0.15f) }
                        .testTag("btn_stage_rot_frq_minus")
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "ROTATING FRQ: ${"%.2f".format(state.effectiveRotatingFrequencyHz)} Hz",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Base ${"%.2f".format(state.rotatingFrequencyHz)} Hz • EEG ${"%.1f".format(state.dominantFrequencyHz)} Hz",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 7.5.sp
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.16f))
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .clickable { onStepRotatingFrq(0.15f) }
                        .testTag("btn_stage_rot_frq_plus")
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonPurple.copy(alpha = 0.2f))
                        .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                        .clickable { onToggleRotationDir() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("btn_stage_rot_dir")
                ) {
                    Text(
                        text = if (state.rotationDirectionCw) "CW ↻" else "CCW ↺",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (state.syncRotatingFrqWithEeg) NeonGreen.copy(alpha = 0.22f)
                            else NeonAmber.copy(alpha = 0.2f)
                        )
                        .border(
                            1.dp,
                            if (state.syncRotatingFrqWithEeg) NeonGreen else NeonAmber,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onToggleEegSync() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("btn_stage_eeg_sync")
                ) {
                    Text(
                        text = if (state.syncRotatingFrqWithEeg) "EEG-FRQ: SYNC" else "FRQ: MANUAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.syncRotatingFrqWithEeg) NeonGreen else NeonAmber,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Open Intake Cant O-Ring, <or if lie>, Jump In/Out, Sonic "translate" & RGB/FRQ Knowability + As Atlas (RED) Quick Strip
        val atlas = state.atlasKnowabilityState
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (atlas.asAtlasRedActive) AtlasCrimsonRed.copy(alpha = 0.16f)
                    else ObsidianBg.copy(alpha = 0.96f)
                )
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .testTag("stage_center_file_line_knowability_bar"),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val rgbCol = if (atlas.rgbKnowable) NeonGreen else AtlasCrimsonRed
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(0.95f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(rgbCol.copy(alpha = 0.18f))
                    .border(1.dp, rgbCol, RoundedCornerShape(5.dp))
                    .clickable { onToggleRgbKnowable() }
                    .padding(vertical = 4.dp, horizontal = 3.dp)
                    .testTag("btn_stage_rgb_knowable")
            ) {
                Text(
                    text = if (atlas.rgbKnowable) "RGB: OK" else "NOT RGB!",
                    style = MaterialTheme.typography.labelSmall,
                    color = rgbCol,
                    fontSize = 7.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val frqCol = if (atlas.frqKnowable) NeonCyan else AtlasCrimsonRed
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(0.95f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(frqCol.copy(alpha = 0.18f))
                    .border(1.dp, frqCol, RoundedCornerShape(5.dp))
                    .clickable { onToggleFrqKnowable() }
                    .padding(vertical = 4.dp, horizontal = 3.dp)
                    .testTag("btn_stage_frq_knowable")
            ) {
                Text(
                    text = if (atlas.frqKnowable) "FRQ: OK" else "NOT FRQ!",
                    style = MaterialTheme.typography.labelSmall,
                    color = frqCol,
                    fontSize = 7.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val lieCol = if (atlas.orIfLieDetected) AtlasCrimsonRed else NeonPurple
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1.0f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(lieCol.copy(alpha = 0.18f))
                    .border(1.dp, lieCol, RoundedCornerShape(5.dp))
                    .clickable { onToggleOrIfLie() }
                    .padding(vertical = 4.dp, horizontal = 3.dp)
                    .testTag("btn_stage_or_if_lie")
            ) {
                Text(
                    text = if (atlas.orIfLieDetected) "<or if lie>: LIE" else "<or if lie>: OK",
                    style = MaterialTheme.typography.labelSmall,
                    color = lieCol,
                    fontSize = 7.0.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1.0f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(NeonCyan.copy(alpha = 0.18f))
                    .border(1.dp, NeonCyan, RoundedCornerShape(5.dp))
                    .clickable { onCycleIntakeJumpMode() }
                    .padding(vertical = 4.dp, horizontal = 3.dp)
                    .testTag("btn_stage_jump_in_out")
            ) {
                Text(
                    text = "JUMP ${"%.2f".format(atlas.effectiveJumpZoomFactor)}x",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 7.0.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1.1f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(NeonAmber.copy(alpha = 0.20f))
                    .border(1.dp, NeonAmber, RoundedCornerShape(5.dp))
                    .clickable { onTriggerSonicSoundSwapTranslate() }
                    .padding(vertical = 4.dp, horizontal = 3.dp)
                    .testTag("btn_stage_sonic_translate")
            ) {
                Text(
                    text = "\"TRANSLATE\" #${atlas.sonicSoundSwapCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAmber,
                    fontSize = 7.0.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val atlasCol = if (atlas.onLostPath) AtlasCrimsonRed else NeonGreen
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1.05f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(atlasCol.copy(alpha = 0.22f))
                    .border(1.dp, atlasCol, RoundedCornerShape(5.dp))
                    .clickable {
                        if (atlas.onLostPath) onReanchorAtlasAndRestoreKnowability() else onToggleAtlasLostPath()
                    }
                    .padding(vertical = 4.dp, horizontal = 3.dp)
                    .testTag("btn_stage_atlas_lost_path")
            ) {
                Text(
                    text = if (atlas.onLostPath) "ATLAS (RED)" else "GOOD STACK",
                    style = MaterialTheme.typography.labelSmall,
                    color = atlasCol,
                    fontSize = 7.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TelemetryCounterItem(
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 8.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TelemetryDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(14.dp)
            .background(SlateBorder)
    )
}

@Composable
fun StatusBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.65f), RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun HeaderActionChip(
    label: String,
    accent: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = accent.copy(alpha = 0.16f),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .border(1.dp, accent.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = ObsidianBg.copy(alpha = 0.8f),
        modifier = Modifier
            .size(36.dp)
            .minimumInteractiveComponentSize()
            .border(1.dp, HudBorderCyan, RoundedCornerShape(6.dp))
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = NeonCyan,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AxiomBottomNavigationDock(
    activeTab: HudTab,
    hudVisible: Boolean,
    onSelectTab: (HudTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(HudPanelBg)
            .border(1.dp, HudBorderCyan)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HudTab.entries.forEach { tab ->
            val selected = hudVisible && activeTab == tab
            val icon = when (tab) {
                HudTab.TUNNEL -> Icons.Default.Hexagon
                HudTab.BIOMETRICS -> Icons.Default.GraphicEq
                HudTab.UNICRITIC -> Icons.Default.Gavel
                HudTab.CODEX -> Icons.Default.Code
                HudTab.SPECT -> Icons.Default.Dns
            }
            val accentColor = when (tab) {
                HudTab.TUNNEL -> NeonCyan
                HudTab.BIOMETRICS -> NeonGreen
                HudTab.UNICRITIC -> NeonPink
                HudTab.CODEX -> NeonAmber
                HudTab.SPECT -> NeonPurple
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selected) accentColor.copy(alpha = 0.22f)
                        else ObsidianBg.copy(alpha = 0.65f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (selected) accentColor else SlateBorder,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectTab(tab) }
                    .padding(vertical = 7.dp, horizontal = 2.dp)
                    .testTag("nav_tab_${tab.name.lowercase()}")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (selected) accentColor else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = tab.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) accentColor else TextMuted,
                            fontSize = 7.sp
                        )
                    }
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) TextPrimary else TextSecondary,
                        fontSize = 8.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

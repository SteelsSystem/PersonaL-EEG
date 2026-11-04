package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AtlasKnowabilityAndOdmEngine
import com.example.service.IntakeCantJumpMode
import com.example.service.OdmAlignmentTarget
import com.example.service.OdmDomainId
import com.example.service.SonicSoundSwapMode
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AxiomUiState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val AtlasCrimsonRed = Color(0xFFFF1744)

private val PrismaticTranslateColors = listOf(
    Color(0xFFFF1744), // Ruby Sub-Bass
    Color(0xFFFF6D00), // Tangerine Bass
    Color(0xFFFFD600), // Solar Gold Low-Mid
    Color(0xFF00E676), // Emerald Mid
    Color(0xFF00E5FF), // Electric Cyan Upper-Mid
    Color(0xFF7C4DFF), // Indigo Violet Presence
    Color(0xFFF50057)  // Magenta Brilliance
)

@Composable
fun AtlasKnowabilityCenterSpineCard(
    state: AxiomUiState,
    onToggleRgbKnowable: () -> Unit,
    onToggleFrqKnowable: () -> Unit,
    onToggleAtlasLostPath: () -> Unit,
    onReanchorAtlasAndRestoreKnowability: () -> Unit,
    onSelectCenterFileLine: (String) -> Unit,
    onToggleOdmGateStatus: () -> Unit,
    onExecuteOdmAllowlistCommand: (String) -> Unit,
    onToggleOrIfLie: () -> Unit = {},
    onSelectIntakeJumpMode: (IntakeCantJumpMode) -> Unit = {},
    onUpdateIntakeCantAngle: (Float) -> Unit = {},
    onTriggerSonicSoundSwapTranslate: () -> Unit = {},
    onSelectSonicSoundSwapMode: (SonicSoundSwapMode) -> Unit = {},
    onSelectOdmTargetAndSelfTeach: (OdmAlignmentTarget) -> Unit = {},
    onSealAtlasOdmToCodex: () -> Unit = {},
    onCopyToast: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val atlas = state.atlasKnowabilityState
    val clipboard = LocalClipboardManager.current
    val borderAccent = when {
        atlas.asAtlasRedActive -> AtlasCrimsonRed
        atlas.rgbKnowable && atlas.frqKnowable -> NeonCyan
        else -> NeonAmber
    }

    HudCard(
        borderColor = borderAccent,
        modifier = modifier.testTag("card_atlas_knowability_center_spine")
    ) {
        // 1. Header: RGB / FRQ Knowability, Intake Cant O-Ring, Sonic "translate" & As Atlas (RED)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (atlas.asAtlasRedActive) {
                        "AS ATLAS (RED) // ON LOST PATH • CANT O-RING SENTINEL"
                    } else {
                        "OPEN INTAKE CANT O-RING // RGB•FRQ•\"TRANSLATE\"•GOOD STACK"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = if (atlas.asAtlasRedActive) AtlasCrimsonRed else NeonCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See RGB/FRQ Knowable • <or if lie> Open O-Ring • Jump In/Out • Sonic \"translate\" • Self-Teach Good Stack",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 8.2.sp
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(borderAccent.copy(alpha = 0.22f))
                    .border(1.dp, borderAccent, RoundedCornerShape(6.dp))
                    .clickable {
                        clipboard.setText(AnnotatedString(atlas.atlasStateBadge))
                        onCopyToast("Copied Status: ${atlas.atlasStateBadge}")
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("badge_atlas_knowability_state")
            ) {
                Text(
                    text = if (atlas.asAtlasRedActive) "AS ATLAS [RED]" else "GOOD STACK [${atlas.goodStackScorePct}%]",
                    style = MaterialTheme.typography.labelSmall,
                    color = borderAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. High-Visibility Discriminators: SEE IF NOT RGB OR FRQ KNOWABLE + <or if lie> + LOST PATH STATUS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // RGB Knowability Visual Box
            val rgbCol = if (atlas.rgbKnowable) NeonGreen else AtlasCrimsonRed
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(rgbCol.copy(alpha = if (atlas.rgbKnowable) 0.12f else 0.26f))
                    .border(1.5.dp, rgbCol, RoundedCornerShape(8.dp))
                    .clickable { onToggleRgbKnowable() }
                    .padding(vertical = 7.dp, horizontal = 5.dp)
                    .testTag("box_see_rgb_knowable")
            ) {
                Text(
                    text = "RGB KNOWABLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 7.4.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (atlas.rgbKnowable) "RGB: KNOWABLE" else "NOT RGB KNOWABLE",
                    style = MaterialTheme.typography.titleSmall,
                    color = rgbCol,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.2.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (atlas.rgbKnowable) {
                        "R:${(state.asCoreFrame.redChannelCreativity * 100).toInt()}% G:${(state.asCoreFrame.greenChannelTruth * 100).toInt()}% B:${(state.asCoreFrame.blueChannelFocus * 100).toInt()}%"
                    } else {
                        "BLIND -> ATLAS (RED)"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (atlas.rgbKnowable) TextPrimary else AtlasCrimsonRed,
                    fontSize = 7.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            // FRQ Knowability Visual Box
            val frqCol = if (atlas.frqKnowable) NeonCyan else AtlasCrimsonRed
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(frqCol.copy(alpha = if (atlas.frqKnowable) 0.12f else 0.26f))
                    .border(1.5.dp, frqCol, RoundedCornerShape(8.dp))
                    .clickable { onToggleFrqKnowable() }
                    .padding(vertical = 7.dp, horizontal = 5.dp)
                    .testTag("box_see_frq_knowable")
            ) {
                Text(
                    text = "FRQ KNOWABLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 7.4.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (atlas.frqKnowable) "FRQ: KNOWABLE" else "NOT FRQ KNOWABLE",
                    style = MaterialTheme.typography.titleSmall,
                    color = frqCol,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.2.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (atlas.frqKnowable) {
                        "${"%.1f".format(state.dominantFrequencyHz)} Hz • ROT ${"%.2f".format(state.effectiveRotatingFrequencyHz)} Hz"
                    } else {
                        "UNRESOLVED -> ATLAS (RED)"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (atlas.frqKnowable) TextPrimary else AtlasCrimsonRed,
                    fontSize = 7.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            // <or if lie> & Path / Atlas (RED) Visual Box
            val pathCol = if (atlas.onLostPath) AtlasCrimsonRed else NeonGreen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(pathCol.copy(alpha = if (atlas.onLostPath) 0.28f else 0.12f))
                    .border(1.5.dp, pathCol, RoundedCornerShape(8.dp))
                    .clickable { onToggleAtlasLostPath() }
                    .padding(vertical = 7.dp, horizontal = 5.dp)
                    .testTag("box_see_atlas_lost_path")
            ) {
                Text(
                    text = "PATH & <or if lie>",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 7.4.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (atlas.onLostPath) "AS ATLAS (RED)" else "O-RING ANCHORED",
                    style = MaterialTheme.typography.titleSmall,
                    color = pathCol,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.2.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (atlas.orIfLieDetected) {
                        "<or if lie>: LIE DETECTED!"
                    } else if (atlas.onLostPath) {
                        "ON LOST PATH (${atlas.atlasBearingDeg}°)"
                    } else {
                        "<or if lie>: FALSE • 6/6 OK"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (atlas.onLostPath) AtlasCrimsonRed else TextPrimary,
                    fontSize = 7.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. OPEN-APERTURE INTAKE CANT O-RING + AUDIO-REACTIVE SONIC SOUND SWAP "TRANSLATE" VISUALIZER
        // ("The center lines hides too you too much <or if lie>" + "Make intake cant o ring" + "jump in and out and visualise well")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianBg)
                .border(
                    1.dp,
                    if (atlas.asAtlasRedActive) AtlasCrimsonRed else NeonCyan.copy(alpha = 0.7f),
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
                .testTag("center_file_line_thinking_spine_box"),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OPEN INTAKE CANT O-RING (${"%.1f".format(atlas.intakeCantAngleDeg)}°) // SONIC \"TRANSLATE\" #${atlas.sonicSoundSwapCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (atlas.asAtlasRedActive) AtlasCrimsonRed else NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.2.sp
                )
                Text(
                    text = "JUMP ${"%.2f".format(atlas.effectiveJumpZoomFactor)}x • CENTER OPEN",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (atlas.asAtlasRedActive) AtlasCrimsonRed else NeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.8.sp
                )
            }

            // Open-Aperture Intake Cant O-Ring + 7-Band Audio-Reactive Colorful Sonic "translate" Canvas (Zero center-crossing lines!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(94.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF060A12))
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                    .clickable { onTriggerSonicSoundSwapTranslate() }
                    .testTag("canvas_center_file_line_spine")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cx = w * 0.5f
                    val cy = h * 0.5f
                    val spineColor = if (atlas.asAtlasRedActive) AtlasCrimsonRed else NeonCyan
                    val cantRad = (atlas.intakeCantAngleDeg.coerceIn(0f, 55f) / 180f) * PI.toFloat()
                    val cantCos = cos(cantRad).coerceIn(0.48f, 1.0f)
                    val cantSin = sin(cantRad)
                    val jumpZoom = atlas.effectiveJumpZoomFactor.coerceIn(0.68f, 1.45f)

                    // Canted O-Ring Radii (Center is 100% open — NO vertical/horizontal crosshair lines blocking the center!)
                    val rx = (h * 0.36f) * 1.38f * (0.85f + 0.15f * jumpZoom)
                    val ry = (h * 0.34f) * cantCos * (0.85f + 0.15f * jumpZoom)

                    // 1. Audio-Reactive 7-Color Prismatic Sonic Sound Swap "translate" Ribbon around the Open O-Ring
                    val bands = atlas.sevenBandFft.asList()
                    val swapShift = atlas.sonicSwapPhaseOffset + atlas.sonicSoundSwapMode.phaseStep
                    bands.forEachIndexed { bIdx, (_, amp) ->
                        val ribbonCol = if (atlas.asAtlasRedActive) {
                            AtlasCrimsonRed
                        } else {
                            PrismaticTranslateColors[(bIdx + swapShift) % PrismaticTranslateColors.size]
                        }
                        val path = Path()
                        val steps = 40
                        for (s in 0..steps) {
                            val theta = (s.toFloat() / steps) * (2f * PI.toFloat())
                            val wave = sin(theta * (3 + (bIdx % 4)) + state.simTime * 3.0f + bIdx) *
                                (2.dp.toPx() + amp * atlas.sonicSwapWaveBurst * 7.dp.toPx())
                            val px = cx + cos(theta) * (rx + wave) - sin(theta) * cantSin * 6.dp.toPx()
                            val py = cy + sin(theta) * (ry + wave * cantCos)
                            if (s == 0) path.moveTo(px, py) else path.lineTo(px, py)
                        }
                        path.close()
                        drawPath(
                            path = path,
                            color = ribbonCol.copy(alpha = (0.38f + amp * 0.45f).coerceIn(0.25f, 0.90f)),
                            style = Stroke(width = (1.3f + amp * 1.4f).dp.toPx())
                        )
                    }

                    // 2. Dual-Lip Canted O-Ring Gasket (Outer & Inner Rim)
                    val outerRing = Path()
                    val innerRing = Path()
                    val ringSteps = 48
                    for (i in 0..ringSteps) {
                        val theta = (i.toFloat() / ringSteps) * (2f * PI.toFloat())
                        val shiftX = -sin(theta) * cantSin * 6.dp.toPx()
                        val ox = cx + cos(theta) * (rx * 1.06f) + shiftX
                        val oy = cy + sin(theta) * (ry * 1.06f)
                        val ix = cx + cos(theta) * (rx * 0.86f) + shiftX * 0.8f
                        val iy = cy + sin(theta) * (ry * 0.86f)
                        if (i == 0) {
                            outerRing.moveTo(ox, oy)
                            innerRing.moveTo(ix, iy)
                        } else {
                            outerRing.lineTo(ox, oy)
                            innerRing.lineTo(ix, iy)
                        }
                    }
                    outerRing.close()
                    innerRing.close()
                    drawPath(
                        path = outerRing,
                        color = spineColor.copy(alpha = 0.82f),
                        style = Stroke(width = 2.2.dp.toPx())
                    )
                    drawPath(
                        path = innerRing,
                        color = (if (atlas.orIfLieDetected) AtlasCrimsonRed else NeonGreen).copy(alpha = 0.72f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // 3. Left Wing of Open O-Ring: RGB Tri-Chromatic Knowability Bars (outside the O-Ring)
                    val rgbValues = if (atlas.rgbKnowable) {
                        listOf(
                            state.asCoreFrame.redChannelCreativity to NeonPink,
                            state.asCoreFrame.greenChannelTruth to NeonGreen,
                            state.asCoreFrame.blueChannelFocus to NeonCyan
                        )
                    } else {
                        listOf(
                            0.92f to AtlasCrimsonRed,
                            0.18f to AtlasCrimsonRed,
                            0.92f to AtlasCrimsonRed
                        )
                    }
                    val leftWingLimit = (cx - rx * 1.18f - 10.dp.toPx()).coerceAtLeast(16.dp.toPx())
                    rgbValues.forEachIndexed { idx, (v, col) ->
                        val barW = (leftWingLimit - 8.dp.toPx()) * v.coerceIn(0.10f, 1.0f)
                        val barY = h * (0.28f + idx * 0.22f)
                        drawLine(
                            color = col,
                            start = Offset(leftWingLimit, barY),
                            end = Offset((leftWingLimit - barW).coerceAtLeast(8.dp.toPx()), barY),
                            strokeWidth = 5.5.dp.toPx()
                        )
                    }

                    // 4. Right Wing of Open O-Ring: 7-Band POT-FFT Sonic "translate" Rainbow Bars (outside the O-Ring)
                    val rightWingStart = (cx + rx * 1.18f + 10.dp.toPx()).coerceAtMost(w - 16.dp.toPx())
                    val maxRightBarW = (w - rightWingStart - 8.dp.toPx()).coerceAtLeast(12.dp.toPx())
                    bands.forEachIndexed { idx, (_, amp) ->
                        val col = if (atlas.frqKnowable) {
                            PrismaticTranslateColors[(idx + swapShift) % PrismaticTranslateColors.size]
                        } else {
                            AtlasCrimsonRed
                        }
                        val barW = maxRightBarW * (if (atlas.frqKnowable) amp else 0.85f * ((idx % 2) * 0.6f + 0.3f))
                        val barY = h * (0.14f + idx * 0.12f)
                        drawLine(
                            color = col,
                            start = Offset(rightWingStart, barY),
                            end = Offset((rightWingStart + barW).coerceAtMost(w - 8.dp.toPx()), barY),
                            strokeWidth = 4.2.dp.toPx()
                        )
                    }
                }

                // Top & Bottom Labels around Open Intake Cant O-Ring (Never covering the center aperture!)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = if (atlas.rgbKnowable) "◀ RGB [KNOWABLE]" else "◀ RGB [NOT KNOWABLE!]",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (atlas.rgbKnowable) NeonGreen else AtlasCrimsonRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                    Text(
                        text = if (atlas.orIfLieDetected) "◯ <or if lie>: LIE! ◯" else "◯ OPEN CANT O-RING ◯",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (atlas.asAtlasRedActive) AtlasCrimsonRed else NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                    Text(
                        text = if (atlas.frqKnowable) "SONIC \"TRANSLATE\" 7-BAND ▶" else "FRQ [NOT KNOWABLE!] ▶",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (atlas.frqKnowable) NeonAmber else AtlasCrimsonRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                }

                Text(
                    text = atlas.sonicTranslateStatusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.4.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 3.dp)
                )
            }

            // Jump In / Jump Out / Auto-Jump & Cant Angle Controls ("jump in and out and visualise well to make sure")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IntakeCantJumpMode.entries.forEach { mode ->
                    val selected = atlas.intakeCantJumpMode == mode
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selected) NeonCyan.copy(alpha = 0.22f) else ObsidianBg)
                            .border(1.dp, if (selected) NeonCyan else SlateBorder, RoundedCornerShape(6.dp))
                            .clickable { onSelectIntakeJumpMode(mode) }
                            .padding(vertical = 5.dp, horizontal = 4.dp)
                            .testTag("btn_intake_jump_${mode.id}")
                    ) {
                        Text(
                            text = mode.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) NeonCyan else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.3.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Intake Cant O-Ring Angle Presets + <or if lie> Truth/Lie Gate Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(15.0f, 24.5f, 38.0f, 48.0f).forEach { deg ->
                    val isSelected = kotlin.math.abs(atlas.intakeCantAngleDeg - deg) < 1.0f
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (isSelected) NeonPurple.copy(alpha = 0.22f) else ObsidianBg)
                            .border(1.dp, if (isSelected) NeonPurple else SlateBorder, RoundedCornerShape(5.dp))
                            .clickable { onUpdateIntakeCantAngle(deg) }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("btn_cant_angle_${deg.toInt()}")
                    ) {
                        Text(
                            text = "CANT ${deg}°",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) NeonPurple else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.4.sp
                        )
                    }
                }

                val lieBtnCol = if (atlas.orIfLieDetected) AtlasCrimsonRed else NeonGreen
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(5.dp))
                        .background(lieBtnCol.copy(alpha = 0.20f))
                        .border(1.dp, lieBtnCol, RoundedCornerShape(5.dp))
                        .clickable { onToggleOrIfLie() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("btn_toggle_or_if_lie")
                ) {
                    Text(
                        text = if (atlas.orIfLieDetected) "<or if lie>: LIE [CLEAR]" else "Test <or if lie>",
                        style = MaterialTheme.typography.labelSmall,
                        color = lieBtnCol,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.4.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Audio-Reactive Colorful Sonic Sound Swap Transfer "translate" Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonAmber.copy(alpha = 0.10f))
                    .border(1.dp, NeonAmber.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SONIC SOUND SWAP TRANSFER \"TRANSLATE\" // AUDIO ⇄ COLORFUL O-RING",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        )
                        Text(
                            text = atlas.sonicSoundSwapMode.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 7.4.sp
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonAmber.copy(alpha = 0.25f))
                            .border(1.dp, NeonAmber, RoundedCornerShape(6.dp))
                            .clickable { onTriggerSonicSoundSwapTranslate() }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                            .testTag("btn_trigger_sonic_translate")
                    ) {
                        Text(
                            text = "⇄ \"TRANSLATE\" #${atlas.sonicSoundSwapCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.8.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SonicSoundSwapMode.entries.forEach { mode ->
                        val isSelected = atlas.sonicSoundSwapMode == mode
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (isSelected) NeonAmber.copy(alpha = 0.22f) else ObsidianBg)
                                .border(1.dp, if (isSelected) NeonAmber else SlateBorder, RoundedCornerShape(5.dp))
                                .clickable { onSelectSonicSoundSwapMode(mode) }
                                .padding(vertical = 4.dp, horizontal = 3.dp)
                                .testTag("btn_sonic_swap_mode_${mode.id}")
                        ) {
                            Text(
                                text = mode.id.uppercase().replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) NeonAmber else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 5 Interactive Center File-Line Nodes (Wrapped around the Open Intake Cant O-Ring)
            atlas.centerFileLines.forEach { node ->
                val isSelected = node.lineCode == atlas.selectedFileLineCode
                val nodeColor = when {
                    node.isAtlasRedAlert -> AtlasCrimsonRed
                    isSelected -> NeonCyan
                    node.isKnowableAndLocked -> NeonGreen
                    else -> NeonAmber
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (node.isAtlasRedAlert) AtlasCrimsonRed.copy(alpha = 0.16f)
                            else if (isSelected) NeonCyan.copy(alpha = 0.14f)
                            else ObsidianBg.copy(alpha = 0.85f)
                        )
                        .border(1.dp, nodeColor.copy(alpha = if (isSelected || node.isAtlasRedAlert) 0.9f else 0.45f), RoundedCornerShape(6.dp))
                        .clickable { onSelectCenterFileLine(node.lineCode) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("center_file_line_${node.lineCode}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(nodeColor)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "[${node.lineCode}] ${node.fileLineRef}",
                                style = MaterialTheme.typography.labelSmall,
                                color = nodeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                            Text(
                                text = "${node.thoughtTitle} ➔ ${node.liveVerdict}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontSize = 8.sp
                            )
                        }
                    }
                    Text(
                        text = if (node.isAtlasRedAlert) "ATLAS RED" else "O-RING OK",
                        style = MaterialTheme.typography.labelSmall,
                        color = nodeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.5.sp
                    )
                }
            }

            // Live Readout Summary Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (atlas.asAtlasRedActive) AtlasCrimsonRed.copy(alpha = 0.16f)
                        else NeonGreen.copy(alpha = 0.10f)
                    )
                    .border(
                        1.dp,
                        if (atlas.asAtlasRedActive) AtlasCrimsonRed else NeonGreen.copy(alpha = 0.6f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = atlas.rgbKnowabilityReadout,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (atlas.rgbKnowable) NeonGreen else AtlasCrimsonRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
                Text(
                    text = atlas.frqKnowabilityReadout,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (atlas.frqKnowable) NeonCyan else AtlasCrimsonRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
                Text(
                    text = atlas.orIfLieStatusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (atlas.orIfLieDetected) AtlasCrimsonRed else NeonPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
                Text(
                    text = atlas.lostPathReason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (atlas.asAtlasRedActive) AtlasCrimsonRed else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Interactive Controls to SEE RGB/FRQ Unknowable & As Atlas (RED) Lost Path
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = onToggleRgbKnowable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (atlas.rgbKnowable) NeonPink.copy(alpha = 0.20f) else AtlasCrimsonRed.copy(alpha = 0.30f),
                    contentColor = if (atlas.rgbKnowable) NeonPink else AtlasCrimsonRed
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, if (atlas.rgbKnowable) NeonPink else AtlasCrimsonRed, RoundedCornerShape(6.dp))
                    .testTag("btn_toggle_rgb_knowable")
            ) {
                Text(
                    text = if (atlas.rgbKnowable) "Test RGB Not Knowable" else "Restore RGB Knowable",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onToggleFrqKnowable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (atlas.frqKnowable) NeonAmber.copy(alpha = 0.20f) else AtlasCrimsonRed.copy(alpha = 0.30f),
                    contentColor = if (atlas.frqKnowable) NeonAmber else AtlasCrimsonRed
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, if (atlas.frqKnowable) NeonAmber else AtlasCrimsonRed, RoundedCornerShape(6.dp))
                    .testTag("btn_toggle_frq_knowable")
            ) {
                Text(
                    text = if (atlas.frqKnowable) "Test FRQ Not Knowable" else "Restore FRQ Knowable",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = onToggleAtlasLostPath,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AtlasCrimsonRed.copy(alpha = if (atlas.onLostPath) 0.34f else 0.20f),
                    contentColor = AtlasCrimsonRed
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, AtlasCrimsonRed, RoundedCornerShape(6.dp))
                    .testTag("btn_trigger_atlas_lost_path")
            ) {
                Text(
                    text = if (atlas.onLostPath) "AS ATLAS (RED) ACTIVE" else "Simulate Lost Path [RED]",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onReanchorAtlasAndRestoreKnowability,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.24f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_reanchor_atlas_knowability")
            ) {
                Text(
                    text = "Re-Anchor Path & Knowability",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. SELF-TEACHING GOOD-STACK AUTODIDACT ("and teach yourself that how if good stack") & ODM STACK TOPOLOGY (Targets A, B, C, D)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianBg.copy(alpha = 0.9f))
                .border(1.dp, if (atlas.isGoodStack) NeonGreen.copy(alpha = 0.7f) else AtlasCrimsonRed, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SELF-TEACHING GOOD-STACK ENGINE // ODM DOM-01..05 • REG ${atlas.registerEnforcedHex}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (atlas.isGoodStack) NeonGreen else AtlasCrimsonRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                    Text(
                        text = atlas.selfTeachFormulaRule,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontSize = 7.4.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (atlas.odmGateStatusOn) NeonGreen.copy(alpha = 0.2f) else AtlasCrimsonRed.copy(alpha = 0.25f))
                        .border(1.dp, if (atlas.odmGateStatusOn) NeonGreen else AtlasCrimsonRed, RoundedCornerShape(4.dp))
                        .clickable { onToggleOdmGateStatus() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("btn_toggle_odm_gate_status")
                ) {
                    Text(
                        text = if (atlas.odmGateStatusOn) "GATE: ON" else "GATE: OFF [BLOCK]",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (atlas.odmGateStatusOn) NeonGreen else AtlasCrimsonRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                }
            }

            // Guided Alignment Roadmap Targets (Target A, Target B, Target C, Target D) -> Self-Teach Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OdmAlignmentTarget.entries.forEach { target ->
                    val isSelected = atlas.selectedOdmTarget == target
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (isSelected) NeonGreen.copy(alpha = 0.22f) else ObsidianBg)
                            .border(1.dp, if (isSelected) NeonGreen else SlateBorder, RoundedCornerShape(5.dp))
                            .clickable { onSelectOdmTargetAndSelfTeach(target) }
                            .padding(vertical = 5.dp, horizontal = 3.dp)
                            .testTag("btn_odm_${target.id}")
                    ) {
                        Text(
                            text = target.id.uppercase().replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) NeonGreen else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.2.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Text(
                text = atlas.selfTeachStatusBanner,
                style = MaterialTheme.typography.bodyMedium,
                color = if (atlas.isGoodStack) NeonGreen else AtlasCrimsonRed,
                fontWeight = FontWeight.Bold,
                fontSize = 7.8.sp
            )

            // 5 Operational Domains Compact Matrix
            OdmDomainId.entries.forEach { dom ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${dom.id} • ${dom.scope}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.6.sp,
                        modifier = Modifier.width(128.dp)
                    )
                    Text(
                        text = "${dom.activeModule} (${dom.gateContract})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 7.4.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "➔ ${dom.deferredTarget}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonAmber,
                        fontSize = 7.2.sp
                    )
                }
            }

            // Guarded 7-Command Allowlist Bar (run_cmd.sh simulator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                AtlasKnowabilityAndOdmEngine.allowlistedCommands.forEach { (cmd, _) ->
                    val selected = atlas.selectedAllowlistCmd == cmd
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (selected) NeonCyan.copy(alpha = 0.22f) else ObsidianBg)
                            .border(1.dp, if (selected) NeonCyan else SlateBorder, RoundedCornerShape(5.dp))
                            .clickable { onExecuteOdmAllowlistCommand(cmd) }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("btn_odm_cmd_$cmd")
                    ) {
                        Text(
                            text = cmd,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) NeonCyan else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.8.sp
                        )
                    }
                }
            }

            Text(
                text = "run_cmd.sh [exit=${atlas.lastCommandExitCode}]: ${atlas.lastCommandOutputJson}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (atlas.lastCommandExitCode == 0) NeonGreen else AtlasCrimsonRed,
                fontSize = 7.5.sp
            )
        }
    }
}

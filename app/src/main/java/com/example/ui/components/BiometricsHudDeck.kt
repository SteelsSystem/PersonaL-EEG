package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ContractPhase
import com.example.model.RgbCognitiveMode
import com.example.service.AsCoreStreamProfile
import com.example.service.RgbChannelAxis
import com.example.ui.theme.HudBorderCyan
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BiometricsHudDeck(
    state: AxiomUiState,
    onSelectRgbMode: (RgbCognitiveMode) -> Unit,
    onToggleBlockMind: () -> Unit,
    onReorderOsaMatrix: () -> Unit,
    onUpdateDelta: (Float) -> Unit,
    onUpdateTheta: (Float) -> Unit,
    onUpdateAlpha: (Float) -> Unit,
    onUpdateBeta: (Float) -> Unit,
    onUpdateGamma: (Float) -> Unit,
    onApplyBiometricPreset: (String) -> Unit,
    onTriggerBleScan: () -> Unit,
    onToggleMockStream: (Boolean) -> Unit = {},
    onTriggerSynapticBurst: () -> Unit = {},
    onSetBlockMindPolarity: (Float) -> Unit = {},
    onStepBlockMindPolarity: (Float) -> Unit = {},
    onSelectAsCoreProfile: (AsCoreStreamProfile) -> Unit = {},
    onToggleAsCoreStream: (Boolean) -> Unit = {},
    onToggleAsCoreAutoDriveBlockMind: (Boolean) -> Unit = {},
    onSetAsCoreCadenceHz: (Int) -> Unit = {},
    onUpdateAsCoreChannelGains: (Float, Float, Float) -> Unit = { _, _, _ -> },
    onInjectAsCorePulse: (RgbChannelAxis) -> Unit = {},
    onOpenBlockMindDashboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val asCore = state.asCoreFrame

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onOpenBlockMindDashboard,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
                .testTag("btn_biometrics_open_blockmind_dashboard")
        ) {
            Text(
                text = "OPEN FULL-SCREEN SY4-EEG BLOCKMIND TELEMETRY DASHBOARD",
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold
            )
        }

        // 0. as[core] REAL-TIME RGB BIOMETRIC STREAM SERVICE LAYER -> BLOCKMIND DASHBOARD DRIVER
        HudCard(
            borderColor = NeonCyan.copy(alpha = 0.85f),
            modifier = Modifier.testTag("card_ascore_rgb_stream_service")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "as[core] // REAL-TIME RGB BIOMETRIC SERVICE",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${asCore.asCoreToken} • ${asCore.profile.subtitle}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = if (state.asCoreStreamActive) "as[core] ${state.asCoreCadenceHz}Hz" else "as[core] PAUSED",
                    color = if (state.asCoreStreamActive) NeonGreen else NeonAmber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Real-Time Tri-Chromatic [R] [G] [B] Oscilloscope Waveform Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(6.dp)
                    .testTag("canvas_ascore_rgb_oscilloscope")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Subtle horizontal grid lines
                    for (g in 1..3) {
                        val gy = h * (g / 4f)
                        drawLine(
                            color = SlateBorder.copy(alpha = 0.45f),
                            start = Offset(0f, gy),
                            end = Offset(w, gy),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    fun drawChannelTrace(samples: List<Float>, traceColor: Color) {
                        if (samples.size < 2) return
                        val path = Path()
                        samples.forEachIndexed { idx, v ->
                            val x = (idx.toFloat() / (samples.size - 1).coerceAtLeast(1)) * w
                            val y = h - (v.coerceIn(0f, 1f) * (h * 0.84f) + h * 0.08f)
                            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(
                            path = path,
                            color = traceColor.copy(alpha = 0.25f),
                            style = Stroke(width = 4.5.dp.toPx())
                        )
                        drawPath(
                            path = path,
                            color = traceColor,
                            style = Stroke(width = 1.8.dp.toPx())
                        )
                    }

                    drawChannelTrace(asCore.rgbWaveformHistoryR, NeonPink)
                    drawChannelTrace(asCore.rgbWaveformHistoryG, NeonGreen)
                    drawChannelTrace(asCore.rgbWaveformHistoryB, NeonCyan)
                }

                // Live Overlay Readout Badges inside Oscilloscope
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "R:${(asCore.redChannelCreativity * 100).toInt()}%  G:${(asCore.greenChannelTruth * 100).toInt()}%  B:${(asCore.blueChannelFocus * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "BALANCE ${asCore.rgbChromaticBalancePct}% • SEAL ${asCore.sha256FrameSeal.take(8)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live Tri-Chromatic Channel Meters + Instant Chromatic Pulse Injectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple(RgbChannelAxis.RED, asCore.redChannelCreativity, NeonPink),
                    Triple(RgbChannelAxis.GREEN, asCore.greenChannelTruth, NeonGreen),
                    Triple(RgbChannelAxis.BLUE, asCore.blueChannelFocus, NeonCyan)
                ).forEach { (axis, level, color) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianBg.copy(alpha = 0.82f))
                            .border(1.dp, color.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                            .clickable { onInjectAsCorePulse(axis) }
                            .padding(vertical = 6.dp, horizontal = 6.dp)
                            .testTag("btn_ascore_pulse_${axis.code.lowercase()}")
                    ) {
                        Text(
                            text = "${axis.label} ${(level * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { level.coerceIn(0f, 1f) },
                            color = color,
                            trackColor = SlateBorder.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚡ +${axis.code} PULSE",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 7.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // as[core] Stream Profile Selector Chips
            Text(
                text = "as[core] STREAM PROFILES (UPDATES BLOCKMIND DASHBOARD)",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AsCoreStreamProfile.entries.forEach { profile ->
                    val active = asCore.profile == profile
                    val accent = when (profile) {
                        AsCoreStreamProfile.AS_CORE_CREATIVITY_RED -> NeonPink
                        AsCoreStreamProfile.AS_CORE_TRUTH_GREEN -> NeonGreen
                        AsCoreStreamProfile.AS_CORE_FOCUS_BLUE -> NeonCyan
                        AsCoreStreamProfile.AS_CORE_EQUILIBRIUM -> NeonPurple
                        AsCoreStreamProfile.AS_CORE_TRI_WAVE_SWEEP -> NeonAmber
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (active) accent.copy(alpha = 0.24f) else ObsidianBg)
                            .border(1.dp, if (active) accent else SlateBorder, RoundedCornerShape(6.dp))
                            .clickable { onSelectAsCoreProfile(profile) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("btn_ascore_profile_${profile.id}")
                    ) {
                        Text(
                            text = profile.asCoreTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (active) accent else TextSecondary,
                            fontSize = 8.5.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive R / G / B Gain Multipliers
            LabeledSliderRow(
                label = "as[core] Red [R] Creativity Gain",
                valueText = "${"%.2f".format(state.asCoreRChannelGain)}x",
                valueColor = NeonPink,
                value = state.asCoreRChannelGain,
                valueRange = 0.20f..2.00f,
                onValueChange = { r ->
                    onUpdateAsCoreChannelGains(r, state.asCoreGChannelGain, state.asCoreBChannelGain)
                },
                testTag = "slider_ascore_r_gain"
            )
            LabeledSliderRow(
                label = "as[core] Green [G] Truth Gain",
                valueText = "${"%.2f".format(state.asCoreGChannelGain)}x",
                valueColor = NeonGreen,
                value = state.asCoreGChannelGain,
                valueRange = 0.20f..2.00f,
                onValueChange = { g ->
                    onUpdateAsCoreChannelGains(state.asCoreRChannelGain, g, state.asCoreBChannelGain)
                },
                testTag = "slider_ascore_g_gain"
            )
            LabeledSliderRow(
                label = "as[core] Blue [B] Focus Gain",
                valueText = "${"%.2f".format(state.asCoreBChannelGain)}x",
                valueColor = NeonCyan,
                value = state.asCoreBChannelGain,
                valueRange = 0.20f..2.00f,
                onValueChange = { b ->
                    onUpdateAsCoreChannelGains(state.asCoreRChannelGain, state.asCoreGChannelGain, b)
                },
                testTag = "slider_ascore_b_gain"
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stream Controls: Auto-Drive BlockMind [+-], Stream Toggle & Cadence (15/30/60 Hz)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onToggleAsCoreAutoDriveBlockMind(!state.asCoreAutoDriveBlockMind) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.asCoreAutoDriveBlockMind) NeonGreen.copy(alpha = 0.24f) else ObsidianBg,
                        contentColor = if (state.asCoreAutoDriveBlockMind) NeonGreen else TextSecondary
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .border(
                            1.dp,
                            if (state.asCoreAutoDriveBlockMind) NeonGreen else SlateBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .testTag("btn_ascore_autodrive_blockmind")
                ) {
                    Text(
                        text = if (state.asCoreAutoDriveBlockMind) "DRIVE BLOCKMIND: ON" else "DRIVE BLOCKMIND: OFF",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onToggleAsCoreStream(!state.asCoreStreamActive) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.asCoreStreamActive) NeonCyan.copy(alpha = 0.22f) else NeonAmber.copy(alpha = 0.22f),
                        contentColor = if (state.asCoreStreamActive) NeonCyan else NeonAmber
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            if (state.asCoreStreamActive) NeonCyan else NeonAmber,
                            RoundedCornerShape(6.dp)
                        )
                        .testTag("btn_ascore_toggle_stream")
                ) {
                    Text(
                        text = if (state.asCoreStreamActive) "STREAM: LIVE" else "STREAM: PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                listOf(15, 30, 60).forEach { hz ->
                    val selectedHz = state.asCoreCadenceHz == hz
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedHz) NeonPurple.copy(alpha = 0.26f) else ObsidianBg)
                            .border(1.dp, if (selectedHz) NeonPurple else SlateBorder, RoundedCornerShape(6.dp))
                            .clickable { onSetAsCoreCadenceHz(hz) }
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .testTag("btn_ascore_cadence_${hz}hz")
                    ) {
                        Text(
                            text = "${hz}Hz",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selectedHz) NeonPurple else TextSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 1. UNBLOCK MIND // BLOCK MIND [+-] & AS-IF Tunnel Correlation Gate
        HudCard(borderColor = state.asIfTunnelState.color.copy(alpha = 0.6f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UNBLOCK MIND // BLOCK MIND [+-]",
                        style = MaterialTheme.typography.titleSmall,
                        color = when {
                            state.unblockMindActive -> NeonGreen
                            state.blockMindStasis -> NeonPurple
                            else -> NeonCyan
                        }
                    )
                    Text(
                        text = "Polarity Flux (${"%+.2f".format(state.blockMindPolarityFlux)}) • ${asCore.asCoreToken} • R:${(asCore.redChannelCreativity * 100).toInt()} G:${(asCore.greenChannelTruth * 100).toInt()} B:${(asCore.blueChannelFocus * 100).toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = when {
                        state.unblockMindActive -> "UNBLOCK [+]"
                        state.blockMindStasis -> "BLOCK [-]"
                        else -> state.asIfTunnelState.label
                    },
                    color = when {
                        state.unblockMindActive -> NeonGreen
                        state.blockMindStasis -> NeonPurple
                        else -> state.asIfTunnelState.color
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cognitive Load Gauge Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cognitive Load Level",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Text(
                    text = "${state.cognitiveLoadPct}% (${state.eegBands.dominantBandName})",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.cognitiveLoadPct > 75) NeonPink else NeonGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { state.cognitiveLoadPct / 100f },
                color = when {
                    state.cognitiveLoadPct > 75 -> NeonPink
                    state.cognitiveLoadPct > 50 -> NeonAmber
                    else -> NeonGreen
                },
                trackColor = ObsidianBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tripartite Contractual State: Litterae, Satis, Epistula
            Text(
                text = "INTERLOCUTIO IN DEFINITO CONTRACTU: LITTERAE • SATIS • EPISTULA",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ContractPhase.entries.forEach { phase ->
                    val active = state.contractPhase == phase
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (active) phase.badgeColor.copy(alpha = 0.22f)
                                else ObsidianBg.copy(alpha = 0.65f)
                            )
                            .border(
                                1.dp,
                                if (active) phase.badgeColor else SlateBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = phase.latinTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (active) phase.badgeColor else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = phase.meaning,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3-Way Polarity Mode Selector: [- BLOCK MIND] | [+- EQUILIBRIUM] | [+ UNBLOCK MIND]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onSetBlockMindPolarity(-1.0f) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.blockMindStasis) NeonPurple.copy(alpha = 0.28f) else ObsidianBg,
                        contentColor = NeonPurple
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, if (state.blockMindStasis) NeonPurple else SlateBorder, RoundedCornerShape(6.dp))
                        .testTag("btn_block_mind_minus")
                ) {
                    Text(
                        text = "- BLOCK MIND",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onSetBlockMindPolarity(0.0f) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!state.blockMindStasis && !state.unblockMindActive) NeonCyan.copy(alpha = 0.24f) else ObsidianBg,
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            if (!state.blockMindStasis && !state.unblockMindActive) NeonCyan else SlateBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .testTag("btn_block_mind_equilibrium")
                ) {
                    Text(
                        text = "+- BALANCE",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onSetBlockMindPolarity(1.0f) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.unblockMindActive) NeonGreen.copy(alpha = 0.28f) else ObsidianBg,
                        contentColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, if (state.unblockMindActive) NeonGreen else SlateBorder, RoundedCornerShape(6.dp))
                        .testTag("btn_unblock_mind_plus")
                ) {
                    Text(
                        text = "+ UNBLOCK MIND",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Continuous Polarity Flux Slider (-1.00 .. +1.00) with [-] and [+] Step Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBg)
                        .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                        .clickable { onStepBlockMindPolarity(-0.25f) }
                        .testTag("btn_step_polarity_minus")
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "BLOCK [-] STASIS",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPurple,
                            fontSize = 8.sp
                        )
                        Text(
                            text = "FLUX: ${"%+.2f".format(state.blockMindPolarityFlux)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "UNBLOCK [+] FLOW",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen,
                            fontSize = 8.sp
                        )
                    }
                    Slider(
                        value = state.blockMindPolarityFlux,
                        onValueChange = onSetBlockMindPolarity,
                        valueRange = -1.0f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = when {
                                state.unblockMindActive -> NeonGreen
                                state.blockMindStasis -> NeonPurple
                                else -> NeonCyan
                            },
                            activeTrackColor = when {
                                state.unblockMindActive -> NeonGreen
                                state.blockMindStasis -> NeonPurple
                                else -> NeonCyan
                            },
                            inactiveTrackColor = ObsidianBg
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .testTag("slider_blockmind_polarity_flux")
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBg)
                        .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                        .clickable { onStepBlockMindPolarity(0.25f) }
                        .testTag("btn_step_polarity_plus")
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onToggleBlockMind,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        state.unblockMindActive -> NeonGreen.copy(alpha = 0.24f)
                        state.blockMindStasis -> NeonPurple.copy(alpha = 0.28f)
                        else -> NeonCyan.copy(alpha = 0.18f)
                    },
                    contentColor = when {
                        state.unblockMindActive -> NeonGreen
                        state.blockMindStasis -> NeonPurple
                        else -> NeonCyan
                    }
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        when {
                            state.unblockMindActive -> NeonGreen
                            state.blockMindStasis -> NeonPurple
                            else -> NeonCyan.copy(alpha = 0.6f)
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .testTag("btn_deck_blockmind")
            ) {
                Text(
                    text = when {
                        state.unblockMindActive -> "UNBLOCK MIND [+] ACTIVE (KINETIC FLOW) — TAP TO CYCLE [+-]"
                        state.blockMindStasis -> "BLOCK MIND [-] ACTIVE (NOTIME STASIS) — TAP TO CYCLE [+-]"
                        else -> "BLOCKMIND [+-] EQUILIBRIUM — TAP TO ENGAGE UNBLOCK MIND [+]"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp
                )
            }
        }

        // 2. RGB Creativity, Truth, and Focus Modes
        HudCard(borderColor = state.rgbMode.accentColor.copy(alpha = 0.6f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RGB CREATIVITY • TRUTH • FOCUS MODES",
                    style = MaterialTheme.typography.titleSmall,
                    color = state.rgbMode.accentColor
                )
                StatusBadge(
                    text = state.rgbMode.id,
                    color = state.rgbMode.accentColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.rgbMode.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RgbCognitiveMode.entries.forEach { mode ->
                    val selected = state.rgbMode == mode
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (selected) mode.accentColor.copy(alpha = 0.24f)
                                else ObsidianBg.copy(alpha = 0.7f)
                            )
                            .border(
                                1.dp,
                                if (selected) mode.accentColor else SlateBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onSelectRgbMode(mode) }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("rgb_mode_${mode.id.lowercase()}")
                    ) {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) mode.accentColor else TextSecondary,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 3. Osa A-X- inf00 Ad Ordinem Redigendum Matrix
        HudCard(borderColor = HudBorderCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "OSA A-X- INF00 AD ORDINEM REDIGENDUM",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan
                    )
                    Text(
                        text = "Sortis Integratio Componit Istud Destionem • 10-Node Order Matrix",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 8.sp
                    )
                }
                val orderedCount = state.osaMatrix.count { it.isOrdered }
                StatusBadge(
                    text = "$orderedCount/10 ORDERED",
                    color = if (orderedCount == 10) NeonGreen else NeonAmber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                maxItemsInEachRow = 5
            ) {
                state.osaMatrix.forEach { cell ->
                    val cellColor = if (cell.isOrdered) NeonGreen else NeonAmber
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianBg.copy(alpha = 0.8f))
                            .border(1.dp, cellColor.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                            .padding(vertical = 5.dp, horizontal = 3.dp)
                    ) {
                        Text(
                            text = cell.code,
                            style = MaterialTheme.typography.labelSmall,
                            color = cellColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = cell.axisLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 7.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onReorderOsaMatrix,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.20f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonGreen.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                    .testTag("btn_reorder_osa_matrix")
            ) {
                Text(
                    text = "AD ORDINEM REDIGENDUM // ALIGN ALL OSA A-X- INF00 NODES",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp
                )
            }
        }

        // 4. Interactive 5-Band EEG Synthesizer & MockBiometricRepository Stream
        HudCard(borderColor = HudBorderCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MOCK BIOMETRIC EEG STREAM & SYNTH",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan
                    )
                    Text(
                        text = "${"%.1f".format(state.dominantFrequencyHz)} Hz • Coherence ${state.phaseCoherencePct}% • ${state.eegBands.dominantBandName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = if (state.mockStreamEnabled) "STREAM: 30Hz LIVE" else "STREAM: MANUAL",
                    color = if (state.mockStreamEnabled) NeonGreen else NeonAmber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onToggleMockStream(!state.mockStreamEnabled) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.mockStreamEnabled) NeonGreen.copy(alpha = 0.22f) else NeonCyan.copy(alpha = 0.20f),
                        contentColor = if (state.mockStreamEnabled) NeonGreen else NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            if (state.mockStreamEnabled) NeonGreen else NeonCyan,
                            RoundedCornerShape(6.dp)
                        )
                        .testTag("btn_toggle_mock_biometric_stream")
                ) {
                    Text(
                        text = if (state.mockStreamEnabled) "Pause Mock Stream" else "Resume Mock Stream",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                }

                Button(
                    onClick = onTriggerSynapticBurst,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink.copy(alpha = 0.22f),
                        contentColor = NeonPink
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonPink.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .testTag("btn_trigger_synaptic_burst")
                ) {
                    Text(
                        text = "⚡ Synaptic Burst",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "DEEP_FOCUS" to "Deep Focus [B]",
                    "CREATIVE_FLOW" to "Creative [R]",
                    "TRUTH_EQUILIBRIUM" to "Truth Lock [G]"
                ).forEach { (presetKey, label) ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianBg)
                            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                            .clickable { onApplyBiometricPreset(presetKey) }
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                            .testTag("eeg_preset_${presetKey.lowercase()}")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LabeledSliderRow(
                label = "Delta Band (0.5–4 Hz • Restorative)",
                valueText = "${(state.eegBands.delta * 100).toInt()} μV",
                valueColor = NeonPurple,
                value = state.eegBands.delta,
                valueRange = 0.05f..1.0f,
                onValueChange = onUpdateDelta,
                testTag = "slider_eeg_delta"
            )
            LabeledSliderRow(
                label = "Theta Band (4–8 Hz • Creativity [R])",
                valueText = "${(state.eegBands.theta * 100).toInt()} μV",
                valueColor = NeonPink,
                value = state.eegBands.theta,
                valueRange = 0.05f..1.0f,
                onValueChange = onUpdateTheta,
                testTag = "slider_eeg_theta"
            )
            LabeledSliderRow(
                label = "Alpha Band (8–13 Hz • Truth [G])",
                valueText = "${(state.eegBands.alpha * 100).toInt()} μV",
                valueColor = NeonGreen,
                value = state.eegBands.alpha,
                valueRange = 0.05f..1.0f,
                onValueChange = onUpdateAlpha,
                testTag = "slider_eeg_alpha"
            )
            LabeledSliderRow(
                label = "Beta Band (13–30 Hz • Focus [B])",
                valueText = "${(state.eegBands.beta * 100).toInt()} μV",
                valueColor = NeonCyan,
                value = state.eegBands.beta,
                valueRange = 0.05f..1.0f,
                onValueChange = onUpdateBeta,
                testTag = "slider_eeg_beta"
            )
            LabeledSliderRow(
                label = "Gamma Band (30–100 Hz • Binding)",
                valueText = "${(state.eegBands.gamma * 100).toInt()} μV",
                valueColor = NeonAmber,
                value = state.eegBands.gamma,
                valueRange = 0.05f..1.0f,
                onValueChange = onUpdateGamma,
                testTag = "slider_eeg_gamma"
            )
        }

        // 5. Hardware Sensors & BLE Neuro-Headset Bridge
        HudCard(borderColor = SlateBorder) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HARDWARE SENSORS & BLE NEURO-BRIDGE",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan
                )
                StatusBadge(
                    text = if (state.hardwareTelemetry.sensorActive) "IMU ONLINE" else "SIMULATED IMU",
                    color = NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "SOMATIC STABILITY",
                    value = "${state.hardwareTelemetry.somaticStabilityPct}%",
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "ACCEL VECTOR",
                    value = "${"%.2f".format(state.hardwareTelemetry.motionMagnitudeG)} G",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "GYRO DRIFT",
                    value = "${"%.2f".format(state.hardwareTelemetry.gyroDriftRad)} rad/s",
                    valueColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.hardwareTelemetry.bleScanState,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                    if (state.hardwareTelemetry.discoveredBleDevices.isNotEmpty()) {
                        Text(
                            text = state.hardwareTelemetry.discoveredBleDevices.joinToString(" • "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeonGreen,
                            fontSize = 9.sp
                        )
                    }
                }
                Button(
                    onClick = onTriggerBleScan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.2f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .testTag("btn_ble_neuro_scan")
                ) {
                    Text(
                        text = "Scan BLE EEG",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

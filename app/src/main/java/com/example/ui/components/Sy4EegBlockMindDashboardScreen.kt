package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OsaMatrixCell
import com.example.model.RgbCognitiveMode
import com.example.service.RgbChannelAxis
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
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Dedicated 'SY4-EEG BlockMind' Telemetry Dashboard Compose screen displaying real-time
 * simulated neural frequency waveforms, BlockMind [+-] polarity phase radar, Osa A-X-00..09
 * node coherence, and multi-sensor somatic telemetry.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Sy4EegBlockMindDashboardScreen(
    state: AxiomUiState = AxiomUiState(),
    onNavigateBack: () -> Unit = {},
    onToggleMockStream: (Boolean) -> Unit = {},
    onTriggerSynapticBurst: () -> Unit = {},
    onToggleBlockMindStasis: () -> Unit = {},
    onSetBlockMindPolarity: (Float) -> Unit = {},
    onSelectRgbMode: (RgbCognitiveMode) -> Unit = {},
    onApplyBiometricPreset: (String) -> Unit = {},
    onUpdateDelta: (Float) -> Unit = {},
    onUpdateTheta: (Float) -> Unit = {},
    onUpdateAlpha: (Float) -> Unit = {},
    onUpdateBeta: (Float) -> Unit = {},
    onUpdateGamma: (Float) -> Unit = {},
    onReorderOsaMatrix: () -> Unit = {},
    onInjectAsCorePulse: (RgbChannelAxis) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val bands = state.eegBands
    val asCore = state.asCoreFrame
    val osaCells = state.osaMatrix.ifEmpty { defaultSimulatedOsaCells() }
    val orderedOsaCount = osaCells.count { it.isOrdered }
    val polarityLabel = when {
        state.unblockMindActive -> "UNBLOCK MIND [+]"
        state.blockMindStasis -> "BLOCK MIND [-]"
        else -> "EQUILIBRIUM [+-]"
    }
    val polarityColor = when {
        state.unblockMindActive -> NeonGreen
        state.blockMindStasis -> NeonPurple
        else -> NeonCyan
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("sy4_eeg_blockmind_dashboard_screen")
    ) {
        // 1. Top Command & Telemetry Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HudPanelBg)
                .border(1.dp, HudBorderCyan)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(8.dp),
                        color = NeonCyan.copy(alpha = 0.14f),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .border(1.dp, NeonCyan.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                            .testTag("btn_blockmind_dashboard_back")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Return to Cockpit",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "SY4-EEG // BLOCKMIND DASHBOARD",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${state.contractPhase.latinTitle} • ${asCore.asCoreToken} • SEAL ${asCore.sha256FrameSeal.take(12)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { onToggleMockStream(!state.mockStreamEnabled) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (state.mockStreamEnabled) NeonGreen.copy(alpha = 0.18f) else NeonAmber.copy(alpha = 0.18f),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .border(
                                1.dp,
                                if (state.mockStreamEnabled) NeonGreen else NeonAmber,
                                RoundedCornerShape(8.dp)
                            )
                            .testTag("btn_dashboard_toggle_stream")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (state.mockStreamEnabled) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle Simulated Telemetry Stream",
                                tint = if (state.mockStreamEnabled) NeonGreen else NeonAmber,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (state.mockStreamEnabled) "STREAM LIVE" else "PAUSED",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (state.mockStreamEnabled) NeonGreen else NeonAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            )
                        }
                    }

                    Surface(
                        onClick = onTriggerSynapticBurst,
                        shape = RoundedCornerShape(8.dp),
                        color = NeonPink.copy(alpha = 0.18f),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .border(1.dp, NeonPink, RoundedCornerShape(8.dp))
                            .testTag("btn_dashboard_synaptic_burst")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Inject Synaptic Burst",
                                tint = NeonPink,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "BURST",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top KPI Strip
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg.copy(alpha = 0.85f))
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DashboardKpiPill("DOMINANT", "${"%.1f".format(state.dominantFrequencyHz)} Hz (${bands.dominantBandName})", NeonCyan)
                DashboardKpiPill("COHERENCE", "${state.phaseCoherencePct}%", NeonGreen)
                DashboardKpiPill(
                    "COG LOAD",
                    "${state.cognitiveLoadPct}%",
                    if (state.cognitiveLoadPct > 75) NeonPink else NeonGreen
                )
                DashboardKpiPill("POLARITY", polarityLabel, polarityColor)
                DashboardKpiPill("OSA A-X", "$orderedOsaCount/10 LOCK", if (orderedOsaCount == 10) NeonGreen else NeonAmber)
                DashboardKpiPill("FPS / GPU", "${state.currentFps} FPS (${"%.1f".format(state.currentFrameMs)}ms)", NeonCyan)
            }
        }

        // 2. Scrollable Responsive Telemetry Grid
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            val isWide = maxWidth >= 680.dp

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Hero Multi-Channel SY4-EEG Neural Oscilloscope Card
                NeuralOscilloscopeCard(
                    state = state,
                    onSelectRgbMode = onSelectRgbMode,
                    onInjectAsCorePulse = onInjectAsCorePulse
                )

                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EegSpectralBandsCard(
                            state = state,
                            onApplyBiometricPreset = onApplyBiometricPreset,
                            onUpdateDelta = onUpdateDelta,
                            onUpdateTheta = onUpdateTheta,
                            onUpdateAlpha = onUpdateAlpha,
                            onUpdateBeta = onUpdateBeta,
                            onUpdateGamma = onUpdateGamma,
                            modifier = Modifier.weight(1f)
                        )
                        BlockMindPolarityAndOsaRadarCard(
                            state = state,
                            osaCells = osaCells,
                            onToggleBlockMindStasis = onToggleBlockMindStasis,
                            onSetBlockMindPolarity = onSetBlockMindPolarity,
                            onReorderOsaMatrix = onReorderOsaMatrix,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    EegSpectralBandsCard(
                        state = state,
                        onApplyBiometricPreset = onApplyBiometricPreset,
                        onUpdateDelta = onUpdateDelta,
                        onUpdateTheta = onUpdateTheta,
                        onUpdateAlpha = onUpdateAlpha,
                        onUpdateBeta = onUpdateBeta,
                        onUpdateGamma = onUpdateGamma,
                        modifier = Modifier.fillMaxWidth()
                    )
                    BlockMindPolarityAndOsaRadarCard(
                        state = state,
                        osaCells = osaCells,
                        onToggleBlockMindStasis = onToggleBlockMindStasis,
                        onSetBlockMindPolarity = onSetBlockMindPolarity,
                        onReorderOsaMatrix = onReorderOsaMatrix,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Simulated Telemetry Packet Stream & Hardware Somatic Bus
                SimulatedTelemetryStreamCard(state = state)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NeuralOscilloscopeCard(
    state: AxiomUiState,
    onSelectRgbMode: (RgbCognitiveMode) -> Unit,
    onInjectAsCorePulse: (RgbChannelAxis) -> Unit
) {
    val bands = state.eegBands
    val asCore = state.asCoreFrame

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HudPanelBg)
            .border(1.dp, NeonCyan.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("card_sy4_neural_oscilloscope")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = "MULTI-BAND SY4-EEG & as[core] RGB OSCILLOSCOPE",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "5-Band Neural Harmonics (Δ/θ/α/β/γ) + Tri-Chromatic Cognitive Envelope",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 8.5.sp
                    )
                }
            }
            StatusBadge(
                text = "${state.asCoreCadenceHz}Hz LOCK",
                color = NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianBg)
                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                .padding(8.dp)
                .testTag("canvas_sy4_eeg_waveform")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) return@Canvas

                // Background glow
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            state.rgbMode.accentColor.copy(alpha = 0.12f),
                            Color.Transparent,
                            NeonCyan.copy(alpha = 0.08f)
                        )
                    )
                )

                // Grid lines
                for (r in 1..4) {
                    val y = h * (r / 5f)
                    drawLine(
                        color = SlateBorder.copy(alpha = 0.45f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                for (c in 1..7) {
                    val x = w * (c / 8f)
                    drawLine(
                        color = SlateBorder.copy(alpha = 0.30f),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw 5 simulated neural band traces stacked cleanly
                val bandConfigs = listOf(
                    Triple(bands.gamma, 28f, NeonPink),
                    Triple(bands.beta, 18f, NeonCyan),
                    Triple(bands.alpha, 10.5f, NeonGreen),
                    Triple(bands.theta, 6f, NeonAmber),
                    Triple(bands.delta, 2.5f, NeonPurple)
                )
                val samplesCount = 96
                bandConfigs.forEachIndexed { laneIdx, (amp, freqFactor, color) ->
                    val laneCenterY = h * ((laneIdx + 0.8f) / 5.6f)
                    val maxSwing = h * 0.085f * amp.coerceIn(0.1f, 1.0f)
                    val path = Path()
                    for (i in 0..samplesCount) {
                        val u = i.toFloat() / samplesCount
                        val x = u * w
                        val phase = state.simTime * (freqFactor * 0.45f) + u * freqFactor * 1.4f + laneIdx
                        val y = laneCenterY + sin(phase) * maxSwing + cos(phase * 1.7f) * (maxSwing * 0.3f)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(
                        path = path,
                        color = color.copy(alpha = 0.85f),
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                }

                // Composite SY4-EEG Master Wave Overlay
                val history = state.eegWaveHistory
                if (history.size >= 2) {
                    val masterPath = Path()
                    history.forEachIndexed { idx, sample ->
                        val x = (idx.toFloat() / (history.size - 1).coerceAtLeast(1)) * w
                        val y = h - ((sample / 100f).coerceIn(0.05f, 0.95f) * h)
                        if (idx == 0) masterPath.moveTo(x, y) else masterPath.lineTo(x, y)
                    }
                    drawPath(
                        path = masterPath,
                        color = Color.White.copy(alpha = 0.35f),
                        style = Stroke(width = 3.5.dp.toPx())
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "γ GAMMA • β BETA • α ALPHA • θ THETA • δ DELTA",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "R:${(asCore.redChannelCreativity * 100).toInt()}% G:${(asCore.greenChannelTruth * 100).toInt()}% B:${(asCore.blueChannelFocus * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // RGB Cognitive Mode Selector + Pulse Injectors
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RgbCognitiveMode.entries.forEach { mode ->
                val selected = state.rgbMode == mode
                Surface(
                    onClick = { onSelectRgbMode(mode) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selected) mode.accentColor.copy(alpha = 0.22f) else ObsidianBg,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .border(
                            1.dp,
                            if (selected) mode.accentColor else SlateBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .testTag("btn_dashboard_rgb_${mode.id.lowercase()}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) mode.accentColor else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    }
                }
            }

            RgbChannelAxis.entries.forEach { axis ->
                val axisColor = when (axis) {
                    RgbChannelAxis.RED -> NeonPink
                    RgbChannelAxis.GREEN -> NeonGreen
                    RgbChannelAxis.BLUE -> NeonCyan
                }
                Surface(
                    onClick = { onInjectAsCorePulse(axis) },
                    shape = RoundedCornerShape(8.dp),
                    color = axisColor.copy(alpha = 0.14f),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .border(1.dp, axisColor.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                        .testTag("btn_dashboard_pulse_${axis.code.lowercase()}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "⚡ +${axis.code} PULSE",
                            style = MaterialTheme.typography.labelSmall,
                            color = axisColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EegSpectralBandsCard(
    state: AxiomUiState,
    onApplyBiometricPreset: (String) -> Unit,
    onUpdateDelta: (Float) -> Unit,
    onUpdateTheta: (Float) -> Unit,
    onUpdateAlpha: (Float) -> Unit,
    onUpdateBeta: (Float) -> Unit,
    onUpdateGamma: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val bands = state.eegBands
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HudPanelBg)
            .border(1.dp, NeonGreen.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("card_sy4_spectral_bands")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "5-BAND SPECTRAL POWER DENSITY",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dominant Band: ${bands.dominantBandName} • Peak ${"%.1f".format(state.dominantFrequencyHz)} Hz",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 8.5.sp
                )
            }
            StatusBadge(text = "COG ${state.cognitiveLoadPct}%", color = NeonCyan)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Triple("DEEP_FOCUS", "FOCUS [β]", NeonCyan),
                Triple("CREATIVE_FLOW", "CREATIVE [θ]", NeonPink),
                Triple("TRUTH_EQUILIBRIUM", "TRUTH [α]", NeonGreen)
            ).forEach { (presetKey, label, color) ->
                Surface(
                    onClick = { onApplyBiometricPreset(presetKey) },
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.15f),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .testTag("btn_dashboard_preset_${presetKey.lowercase()}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        BandSliderRow("DELTA (0.5–4 Hz) // Deep Stasis", bands.delta, NeonPurple, "slider_dashboard_delta", onUpdateDelta)
        BandSliderRow("THETA (4–8 Hz) // Creative Flux", bands.theta, NeonAmber, "slider_dashboard_theta", onUpdateTheta)
        BandSliderRow("ALPHA (8–13 Hz) // Truth Bridge", bands.alpha, NeonGreen, "slider_dashboard_alpha", onUpdateAlpha)
        BandSliderRow("BETA (13–30 Hz) // Tunnel Focus", bands.beta, NeonCyan, "slider_dashboard_beta", onUpdateBeta)
        BandSliderRow("GAMMA (30–90 Hz) // Hyper Sync", bands.gamma, NeonPink, "slider_dashboard_gamma", onUpdateGamma)
    }
}

@Composable
private fun BandSliderRow(
    label: String,
    value: Float,
    color: Color,
    testTag: String,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontSize = 8.5.sp
            )
            Text(
                text = "${(value * 100).roundToInt()}% (${"%.1f".format(value * 42f)} µV²/Hz)",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp
            )
        }
        Slider(
            value = value.coerceIn(0.05f, 1.0f),
            onValueChange = onValueChange,
            valueRange = 0.05f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = SlateBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .testTag(testTag)
        )
    }
}

@Composable
private fun BlockMindPolarityAndOsaRadarCard(
    state: AxiomUiState,
    osaCells: List<OsaMatrixCell>,
    onToggleBlockMindStasis: () -> Unit,
    onSetBlockMindPolarity: (Float) -> Unit,
    onReorderOsaMatrix: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HudPanelBg)
            .border(1.dp, NeonPurple.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("card_sy4_blockmind_radar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BLOCKMIND [+-] & OSA A-X- INF00 RADAR",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "SEALED HAS:${if (state.blockMindSealedHasTrue) "TRUE" else "FALSE"} • Δ=${"%.4f".format(state.blockMindMiscalculusDelta)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.blockMindSealedHasTrue) NeonGreen else NeonAmber,
                    fontSize = 8.5.sp
                )
            }
            Surface(
                onClick = onToggleBlockMindStasis,
                shape = RoundedCornerShape(8.dp),
                color = NeonCyan.copy(alpha = 0.16f),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                    .testTag("btn_dashboard_toggle_stasis")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (state.blockMindStasis) "STASIS [-]" else "ACTIVE [+-]",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Radial Phase Coherence & Osa A-X-00..09 Node Radar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianBg)
                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                .padding(8.dp)
                .testTag("canvas_blockmind_osa_radar")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width * 0.5f
                val cy = size.height * 0.5f
                val maxR = minOf(size.width, size.height) * 0.42f

                for (ring in 1..3) {
                    drawCircle(
                        color = SlateBorder.copy(alpha = 0.5f),
                        radius = maxR * (ring / 3f),
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                val polyPath = Path()
                osaCells.forEachIndexed { idx, cell ->
                    val angle = (idx.toFloat() / osaCells.size) * (2f * PI.toFloat()) - (PI.toFloat() / 2f)
                    val coherenceRadius = maxR * (1f - cell.entropyValue.coerceIn(0f, 0.85f))
                    val px = cx + cos(angle) * coherenceRadius
                    val py = cy + sin(angle) * coherenceRadius
                    if (idx == 0) polyPath.moveTo(px, py) else polyPath.lineTo(px, py)

                    drawCircle(
                        color = if (cell.isOrdered) NeonGreen else NeonAmber,
                        radius = 4.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
                polyPath.close()
                drawPath(
                    path = polyPath,
                    color = NeonCyan.copy(alpha = 0.25f)
                )
                drawPath(
                    path = polyPath,
                    color = NeonCyan,
                    style = Stroke(width = 1.8.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Triple(-0.75f, "BLOCK [-]", NeonPurple),
                Triple(0.0f, "LOCK [+-]", NeonCyan),
                Triple(0.75f, "UNBLOCK [+]", NeonGreen)
            ).forEach { (fluxTarget, label, col) ->
                Surface(
                    onClick = { onSetBlockMindPolarity(fluxTarget) },
                    shape = RoundedCornerShape(8.dp),
                    color = col.copy(alpha = 0.16f),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, col.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .testTag("btn_dashboard_polarity_${label.substringBefore(" ").lowercase()}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = col,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onReorderOsaMatrix,
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                .testTag("btn_dashboard_reorder_osa")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ALIGN OSA A-X-00..09 & SEAL EPISTULA",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SimulatedTelemetryStreamCard(state: AxiomUiState) {
    val hw = state.hardwareTelemetry
    val sensorPaths = state.spectSensorDashboardState.sensorPaths.take(4)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HudPanelBg)
            .border(1.dp, HudBorderCyan, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("card_sy4_simulated_telemetry_stream")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "LIVE SIMULATED TELEMETRY & SOMATIC SENSOR BUS",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "SOMATIC ${hw.somaticStabilityPct}% • ${"%.2f".format(hw.motionMagnitudeG)}G",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        sensorPaths.forEach { pathNode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg.copy(alpha = 0.85f))
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${pathNode.pathId.shortCode} // ${pathNode.pathId.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp
                    )
                    Text(
                        text = "${pathNode.primaryReadout} • ${pathNode.secondaryReadout}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 8.sp
                    )
                }
                LinearProgressIndicator(
                    progress = { (1f - pathNode.driftIntensity).coerceIn(0.1f, 1f) },
                    color = if (pathNode.sha256Verified) NeonGreen else NeonAmber,
                    trackColor = SlateBorder,
                    modifier = Modifier
                        .width(64.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
private fun DashboardKpiPill(label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 8.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 8.5.sp
        )
    }
}

private fun defaultSimulatedOsaCells(): List<OsaMatrixCell> = listOf(
    OsaMatrixCell("A-X-00", "SHA-256 Root", 0.02f, true),
    OsaMatrixCell("A-X-01", "Litterae Ingest", 0.04f, true),
    OsaMatrixCell("A-X-02", "Hex Domain", 0.03f, true),
    OsaMatrixCell("A-X-03", "Sortis Integr.", 0.05f, true),
    OsaMatrixCell("A-X-04", "Reflex Radial", 0.02f, true),
    OsaMatrixCell("A-X-05", "SQLx SPECT", 0.01f, true),
    OsaMatrixCell("A-X-06", "Mag Curl Field", 0.04f, true),
    OsaMatrixCell("A-X-07", "Anti-Mag Pulse", 0.03f, true),
    OsaMatrixCell("A-X-08", "BlockMind Gate", 0.02f, true),
    OsaMatrixCell("A-X-09", "Epistula Seal", 0.01f, true)
)

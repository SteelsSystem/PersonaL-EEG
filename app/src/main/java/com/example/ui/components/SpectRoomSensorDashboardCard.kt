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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SpectRoomEntity
import com.example.service.SpectSensorPathId
import com.example.service.SpectSensorPathNodeState
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SpectRoomSensorPathsDashboard(
    state: AxiomUiState,
    spectRoomRecords: List<SpectRoomEntity>,
    onSelectSensorPathFilter: (String) -> Unit,
    onToggleSensorStream: () -> Unit,
    onCalibrateAndZeroDriftPaths: () -> Unit,
    onSealSensorPathSnapshotToRoom: () -> Unit,
    onDeleteSpectRoomRecord: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dash = state.spectSensorDashboardState
    val filteredPaths = if (dash.selectedPathFilter == "ALL") {
        dash.sensorPaths
    } else {
        dash.sensorPaths.filter { it.pathId.id == dash.selectedPathFilter }
    }
    val healthColor = when {
        dash.aggregateHealthPct >= 88 -> NeonGreen
        dash.aggregateHealthPct >= 65 -> NeonAmber
        else -> NeonPink
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Primary Real-Time SPECT Room Sensor Paths Oscilloscope & Control Console
        HudCard(
            borderColor = NeonCyan,
            modifier = Modifier.testTag("spect_room_sensor_dashboard_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SPECT ROOM SENSOR PATHS // LIVE TELEMETRY DASHBOARD",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "6 Local Sensor Paths ➔ spect_room_records • Real-Time Multi-Trace Bus",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 8.5.sp
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (dash.liveStreamingActive) NeonGreen.copy(alpha = 0.22f)
                            else NeonAmber.copy(alpha = 0.22f)
                        )
                        .border(
                            1.dp,
                            if (dash.liveStreamingActive) NeonGreen else NeonAmber,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onToggleSensorStream() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("btn_toggle_spect_sensor_stream")
                ) {
                    Text(
                        text = if (dash.liveStreamingActive) "STREAM: LIVE [60Hz]" else "STREAM: PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (dash.liveStreamingActive) NeonGreen else NeonAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top KPI Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "SENSOR HEALTH",
                    value = "${dash.aggregateHealthPct}%",
                    valueColor = healthColor,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "ACTIVE PATHS",
                    value = "${dash.activeSensorPathsCount}/6 LOCKED",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "PACKETS ROUTED",
                    value = "${dash.totalPacketsRouted}",
                    valueColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "ROOM RECORDS",
                    value = "${spectRoomRecords.size} ROWS",
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Multi-Trace Real-Time Sensor Path Oscilloscope
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg.copy(alpha = 0.92f))
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .testTag("spect_sensor_oscilloscope_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    if (w <= 0f || h <= 0f) return@Canvas

                    // Horizontal reference grid lines (25%, 50%, 75%)
                    listOf(0.25f, 0.50f, 0.75f).forEach { ratio ->
                        val y = h * ratio
                        drawLine(
                            color = Color.White.copy(alpha = 0.07f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val pathColors = listOf(
                        NeonCyan,
                        NeonGreen,
                        NeonPurple,
                        NeonAmber,
                        NeonPink,
                        Color(0xFF38BDF8)
                    )

                    // Draw each selected sensor path's real-time waveform
                    filteredPaths.forEach { pathNode ->
                        val colorIdx = pathNode.pathId.ordinal % pathColors.size
                        val traceColor = pathColors[colorIdx]
                        val samples = pathNode.waveformHistory
                        if (samples.size >= 2) {
                            val stepX = w / (samples.size - 1).toFloat()
                            val wavePath = Path()
                            samples.forEachIndexed { idx, value ->
                                val x = idx * stepX
                                val y = h - (value.coerceIn(0.02f, 0.98f) * (h * 0.84f) + h * 0.08f)
                                if (idx == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                            }
                            drawPath(
                                path = wavePath,
                                color = traceColor.copy(
                                    alpha = if (dash.selectedPathFilter == "ALL") 0.82f else 0.98f
                                ),
                                style = Stroke(
                                    width = if (dash.selectedPathFilter == "ALL") 1.8.dp.toPx() else 2.6.dp.toPx()
                                )
                            )
                        }
                    }

                    // Draw composite drift floor trace in dashed/thin pink at bottom
                    val driftHist = dash.compositeDriftHistory
                    if (driftHist.size >= 2) {
                        val stepX = w / (driftHist.size - 1).toFloat()
                        val driftPath = Path()
                        driftHist.forEachIndexed { idx, dVal ->
                            val x = idx * stepX
                            val y = h - (dVal.coerceIn(0f, 1f) * (h * 0.75f) + h * 0.04f)
                            if (idx == 0) driftPath.moveTo(x, y) else driftPath.lineTo(x, y)
                        }
                        drawPath(
                            path = driftPath,
                            color = NeonPink.copy(alpha = 0.55f),
                            style = Stroke(width = 1.2.dp.toPx())
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (dash.selectedPathFilter == "ALL") {
                                "OSC // ALL 6 SPECT ROOM SENSOR PATHS (COMPOSITE BUS)"
                            } else {
                                "OSC // FOCUSED: ${dash.selectedPathFilter}"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "check:pass:true_only • WAL SYNC",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen,
                            fontSize = 8.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cyan:EEG • Green:as[core] • Purple:IMU • Amber:GLSL • Pink:Drift",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 7.5.sp
                        )
                        Text(
                            text = "Bio-Drift: ${"%.2f".format(state.effectiveUnicriticDrift)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.effectiveUnicriticDrift > 0.2f) NeonPink else NeonGreen,
                            fontSize = 8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Sensor Path Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filterItems = listOf("ALL" to "ALL 6 PATHS") +
                    SpectSensorPathId.entries.map { it.id to "${it.id} (${it.shortCode})" }

                filterItems.forEach { (filterKey, label) ->
                    val selected = dash.selectedPathFilter == filterKey
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (selected) NeonCyan.copy(alpha = 0.22f)
                                else ObsidianBg
                            )
                            .border(
                                1.dp,
                                if (selected) NeonCyan else SlateBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onSelectSensorPathFilter(filterKey) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("spect_sensor_filter_$filterKey")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) NeonCyan else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live 6 Sensor Path Cards Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                filteredPaths.forEach { node ->
                    SensorPathTelemetryRowCard(
                        node = node,
                        isFocused = dash.selectedPathFilter == node.pathId.id,
                        onClick = {
                            val nextFilter = if (dash.selectedPathFilter == node.pathId.id) "ALL" else node.pathId.id
                            onSelectSensorPathFilter(nextFilter)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calibration & Room Seal Action Bar
            Text(
                text = dash.lastCalibratedSummary,
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen,
                fontSize = 8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onCalibrateAndZeroDriftPaths,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen.copy(alpha = 0.22f),
                        contentColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                        .testTag("btn_calibrate_spect_sensor_paths")
                ) {
                    Text(
                        text = "Calibrate & Zero-Drift Paths",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.3.sp
                    )
                }

                Button(
                    onClick = onSealSensorPathSnapshotToRoom,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.22f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .testTag("btn_seal_sensor_path_to_spect_room")
                ) {
                    Text(
                        text = "+ Seal Sensor Path to Room",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.3.sp
                    )
                }
            }
        }

        // 2. Persisted SPECTRoom Entity Records Vault (spect_room_records table)
        HudCard(
            borderColor = NeonPurple.copy(alpha = 0.75f),
            modifier = Modifier.testTag("card_spect_room_entity_records")
        ) {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SPECTROOM SENSOR PATH VAULT (${spectRoomRecords.size} RECORDS)",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@Entity(tableName = \"spect_room_records\") • Biometric Drift Logs & SHA-256 Status",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 8.5.sp
                    )
                }
                StatusBadge(
                    text = "SHA-256 INDEXED",
                    color = NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                spectRoomRecords.take(6).forEach { rec ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianBg.copy(alpha = 0.88f))
                            .border(
                                1.dp,
                                if (rec.sha256Verified) SlateBorder else NeonPink,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(8.dp)
                            .testTag("spect_room_record_item_${rec.id}"),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rec.sessionTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadge(
                                text = rec.sha256IntegrityStatus,
                                color = if (rec.sha256Verified) NeonGreen else NeonPink
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "✕",
                                color = TextMuted,
                                modifier = Modifier
                                    .clickable { onDeleteSpectRoomRecord(rec.id) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = rec.biometricDriftLog,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (rec.biometricDriftIntensity >= 0.25f) NeonAmber else NeonGreen,
                            fontSize = 8.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${dateFormat.format(Date(rec.timestamp))} • Band: ${rec.dominantBand} • InvFPS: ${"%.1f".format(rec.invertedFrameRate)}Hz • Stability: ${"%.0f".format(rec.invertedStabilityIndex)}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 7.8.sp
                            )
                            Text(
                                text = "SHA: ${rec.sha256Digest.take(12)}...",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPurple,
                                fontSize = 7.8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorPathTelemetryRowCard(
    node: SpectSensorPathNodeState,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    val accent = when {
        !node.sha256Verified || node.driftIntensity >= 0.42f -> NeonPink
        node.driftIntensity >= 0.18f -> NeonAmber
        isFocused -> NeonCyan
        else -> NeonGreen
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isFocused) NeonCyan.copy(alpha = 0.12f)
                else ObsidianBg.copy(alpha = 0.85f)
            )
            .border(
                1.dp,
                if (isFocused) NeonCyan else SlateBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("spect_sensor_path_card_${node.pathId.id}"),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Column {
                    Text(
                        text = "${node.pathId.id} // ${node.pathId.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                    Text(
                        text = node.secondaryReadout,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 7.5.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatusBadge(
                    text = "${node.sampleRateHz}Hz • ${"%.2f".format(node.latencyMs)}ms",
                    color = NeonCyan
                )
                StatusBadge(
                    text = node.statusBadge,
                    color = accent
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.primaryReadout,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
                Text(
                    text = "Drift Δ: ${"%.2f".format(node.driftIntensity)} • SHA-256: ${node.packetDigestHex.take(16)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (node.sha256Verified) NeonGreen else NeonPink,
                    fontSize = 7.8.sp
                )
            }

            // Mini inline sparkline for this sensor path
            Box(
                modifier = Modifier
                    .width(92.dp)
                    .height(26.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, SlateBorder, RoundedCornerShape(4.dp))
                    .padding(2.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val sw = size.width
                    val sh = size.height
                    val samples = node.waveformHistory
                    if (samples.size >= 2) {
                        val step = sw / (samples.size - 1).toFloat()
                        val path = Path()
                        samples.forEachIndexed { idx, v ->
                            val x = idx * step
                            val y = sh - (v.coerceIn(0.05f, 0.95f) * sh)
                            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(
                            path = path,
                            color = accent,
                            style = Stroke(width = 1.6.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

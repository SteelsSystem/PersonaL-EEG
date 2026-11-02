package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.data.UnicriticVerdictEntity
import com.example.model.LogSeverity
import com.example.model.RemediationActionSummary
import com.example.model.UnicriticLiveEvaluation
import com.example.model.UnicriticVerdictLevel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun UnicriticHudDeck(
    state: AxiomUiState,
    verdicts: List<UnicriticVerdictEntity>,
    onInjectCriticalDrift: () -> Unit,
    onExecuteRemediation: () -> Unit,
    onRemediateSingleAxis: (String) -> Unit,
    onToggleDistortion: (Boolean) -> Unit,
    onLogManualVerdict: () -> Unit,
    onDeleteVerdict: (Int) -> Unit,
    onSimulateContinuityAutoRemediate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val eval = state.unicriticEvaluation
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    val orderedNodes = state.osaMatrix.count { it.isOrdered }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("unicritic_hud_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Prominent Unicritic Verdict Badge & Master Consensus Banner
        UnicriticVerdictHeroBanner(
            eval = eval,
            effectiveDrift = state.effectiveUnicriticDrift,
            onInjectCriticalDrift = onInjectCriticalDrift,
            onExecuteRemediation = onExecuteRemediation
        )

        // 1B. Tri-Axis Continuity Interlock & Chained SHA-256 Status Bar
        HudCard(borderColor = if (state.continuityInterlockEnabled) NeonGreen.copy(alpha = 0.7f) else NeonAmber) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CONTINUITY INTERLOCK // CHAIN #${state.continuityChainEpoch}",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.lastContinuityEventSummary,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 8.5.sp
                    )
                }
                StatusBadge(
                    text = "CLAMPED: ${state.autoRemediatedUniformCount}",
                    color = NeonCyan
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = onSimulateContinuityAutoRemediate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.22f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_unicritic_continuity_remediate")
            ) {
                Text(
                    text = "AUTO-REMEDIATE OOB UNIFORMS & LOCK SHA-256 CHAIN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp
                )
            }
        }

        // 2. Multi-Axis Score Cards & Tri-Axis Radar Breakdown
        UnicriticMultiAxisScorePanel(
            state = state,
            eval = eval,
            orderedNodes = orderedNodes,
            onRemediateSingleAxis = onRemediateSingleAxis,
            onToggleDistortion = onToggleDistortion
        )

        // 3. Summary of Recent Remediation Actions
        UnicriticRecentRemediationsSummaryCard(
            remediations = state.recentRemediations,
            healCycles = state.healCycles,
            onExecuteRemediation = onExecuteRemediation
        )

        // 4. Active Unicritic Diagnostic Findings
        HudCard(borderColor = HudBorderCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE DIAGNOSTIC FINDINGS (${eval.findings.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan
                )
                Button(
                    onClick = onLogManualVerdict,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ObsidianBg,
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .testTag("btn_unicritic_log_verdict")
                ) {
                    Text(
                        text = "+ Snapshot Verdict",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                eval.findings.forEach { finding ->
                    val severityColor = when (finding.severity) {
                        LogSeverity.NOMINAL -> NeonGreen
                        LogSeverity.INFO -> NeonCyan
                        LogSeverity.WARNING -> NeonAmber
                        LogSeverity.CRITICAL -> NeonPink
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianBg.copy(alpha = 0.85f))
                            .border(1.dp, severityColor.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = finding.axis.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = severityColor,
                                fontSize = 8.sp
                            )
                            StatusBadge(
                                text = finding.severity.name,
                                color = severityColor
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = finding.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = finding.detail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        if (finding.actionKey != "LOG_VERDICT") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { onRemediateSingleAxis(finding.actionKey) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = severityColor.copy(alpha = 0.2f),
                                        contentColor = severityColor
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .border(1.dp, severityColor, RoundedCornerShape(6.dp))
                                        .testTag("btn_fix_${finding.actionKey.lowercase()}")
                                ) {
                                    Text(
                                        text = "Remediate ${finding.axis.shortTag} Axis",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Persisted Room SQLite Unicritic Audit Verdicts
        HudCard(borderColor = SlateBorder) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOCAL UNICRITIC VERDICT VAULT (${verdicts.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonGreen
                )
                Text(
                    text = "SQLite // unicritic_verdicts",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 8.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                verdicts.forEach { v ->
                    val vColor = when (v.verdictLevel) {
                        "OPTIMAL" -> NeonGreen
                        "ADMONITIO" -> NeonAmber
                        else -> NeonPink
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianBg.copy(alpha = 0.85f))
                            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(
                                    text = "${v.verdictLevel} (${v.consensusScore}%)",
                                    color = vColor
                                )
                                if (v.remediationApplied) {
                                    StatusBadge(text = "REMEDIATED", color = NeonCyan)
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dateFormat.format(Date(v.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 8.sp
                                )
                                Text(
                                    text = "✕",
                                    color = TextMuted,
                                    modifier = Modifier
                                        .clickable { onDeleteVerdict(v.id) }
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = v.primaryFinding,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MiniStatBox(
                                title = "GLSL AXIS",
                                value = "${v.glslAxisScore}%",
                                valueColor = NeonCyan,
                                modifier = Modifier.weight(1f)
                            )
                            MiniStatBox(
                                title = "EEG AXIS",
                                value = "${v.eegAxisScore}%",
                                valueColor = NeonGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MiniStatBox(
                                title = "SHA AXIS",
                                value = "${v.codexAxisScore}%",
                                valueColor = NeonPurple,
                                modifier = Modifier.weight(1f)
                            )
                            MiniStatBox(
                                title = "DRIFT",
                                value = "%.2f".format(v.driftIntensity),
                                valueColor = vColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnicriticVerdictHeroBanner(
    eval: UnicriticLiveEvaluation,
    effectiveDrift: Float,
    onInjectCriticalDrift: () -> Unit,
    onExecuteRemediation: () -> Unit
) {
    val badgeColor = eval.verdictLevel.color
    val icon = when (eval.verdictLevel) {
        UnicriticVerdictLevel.OPTIMAL -> Icons.Default.CheckCircle
        UnicriticVerdictLevel.ADMONITIO -> Icons.Default.Warning
        UnicriticVerdictLevel.CRITICUS -> Icons.Default.Gavel
    }

    HudCard(borderColor = badgeColor.copy(alpha = 0.8f)) {
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeColor.copy(alpha = 0.18f))
                        .border(1.5.dp, badgeColor, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = eval.verdictLevel.title,
                        tint = badgeColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "UNICRITIC CONSENSUS HUD",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan
                    )
                    Text(
                        text = eval.verdictLevel.contractCorrelation,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            // Prominent Verdict Pill Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.22f))
                    .border(1.5.dp, badgeColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("unicritic_verdict_badge")
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = eval.verdictLevel.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "SCORE: ${eval.consensusScore}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontSize = 8.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = eval.verdictLevel.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onInjectCriticalDrift,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPink.copy(alpha = 0.22f),
                    contentColor = NeonPink
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, NeonPink.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                    .testTag("btn_unicritic_inject_drift")
            ) {
                Text(
                    text = "⚡ Inject Critical Drift",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp
                )
            }

            Button(
                onClick = onExecuteRemediation,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.24f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, NeonGreen.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                    .testTag("btn_unicritic_remediate_all")
            ) {
                Text(
                    text = "⟳ Remediate & Seal (${"%.2f".format(effectiveDrift)})",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun UnicriticMultiAxisScorePanel(
    state: AxiomUiState,
    eval: UnicriticLiveEvaluation,
    orderedNodes: Int,
    onRemediateSingleAxis: (String) -> Unit,
    onToggleDistortion: (Boolean) -> Unit
) {
    HudCard(borderColor = HudBorderCyan) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MULTI-AXIS SCORE BREAKDOWN",
                style = MaterialTheme.typography.titleSmall,
                color = NeonCyan
            )
            StatusBadge(
                text = "CONSENSUS: ${eval.consensusScore}%",
                color = eval.verdictLevel.color
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Top Row: Tri-Axis Radar Canvas + 3 Summary Score Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(116.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg.copy(alpha = 0.88f))
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    val cx = size.width * 0.5f
                    val cy = size.height * 0.52f
                    val radius = size.minDimension * 0.44f

                    listOf(0.35f, 0.7f, 1.0f).forEach { rScale ->
                        drawCircle(
                            color = Color.White.copy(alpha = 0.1f),
                            radius = radius * rScale,
                            center = Offset(cx, cy),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    val anglesDeg = listOf(-90.0, 30.0, 150.0)
                    val scores = listOf(
                        eval.glslAxisScore / 100f,
                        eval.eegAxisScore / 100f,
                        eval.codexAxisScore / 100f
                    )

                    anglesDeg.forEach { deg ->
                        val rad = Math.toRadians(deg)
                        val ex = cx + (cos(rad) * radius).toFloat()
                        val ey = cy + (sin(rad) * radius).toFloat()
                        drawLine(
                            color = Color.White.copy(alpha = 0.16f),
                            start = Offset(cx, cy),
                            end = Offset(ex, ey),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val poly = Path()
                    anglesDeg.forEachIndexed { idx, deg ->
                        val rad = Math.toRadians(deg)
                        val norm = scores[idx].coerceIn(0.1f, 1.0f)
                        val px = cx + (cos(rad) * radius * norm).toFloat()
                        val py = cy + (sin(rad) * radius * norm).toFloat()
                        if (idx == 0) poly.moveTo(px, py) else poly.lineTo(px, py)
                    }
                    poly.close()

                    drawPath(
                        path = poly,
                        color = eval.verdictLevel.color.copy(alpha = 0.25f)
                    )
                    drawPath(
                        path = poly,
                        color = eval.verdictLevel.color,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Text(
                    text = "${eval.consensusScore}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = eval.verdictLevel.color,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AxisScoreBar(
                    label = "Axis I: GLSL & Lattice (40% wt)",
                    score = eval.glslAxisScore,
                    color = NeonCyan
                )
                AxisScoreBar(
                    label = "Axis II: SY4-EEG & Somatic (35% wt)",
                    score = eval.eegAxisScore,
                    color = NeonGreen
                )
                AxisScoreBar(
                    label = "Axis III: Codex & SHA-256 (25% wt)",
                    score = eval.codexAxisScore,
                    color = NeonPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Detailed 3-Column Multi-Axis Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MultiAxisDetailCard(
                axisTitle = "AXIS I // GLSL",
                score = eval.glslAxisScore,
                metricLine1 = "${state.currentFps} FPS (${"%.1f".format(state.currentFrameMs)}ms)",
                metricLine2 = "Entropy: ${"%.2f".format(state.perturbation)}",
                accentColor = NeonCyan,
                onQuickFix = { onRemediateSingleAxis("HEAL_LATTICE") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("card_axis_glsl")
            )
            MultiAxisDetailCard(
                axisTitle = "AXIS II // EEG",
                score = eval.eegAxisScore,
                metricLine1 = "Load: ${state.cognitiveLoadPct}%",
                metricLine2 = "Osa: $orderedNodes/10 Ordered",
                accentColor = NeonGreen,
                onQuickFix = { onRemediateSingleAxis("ALIGN_EEG_OSA") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("card_axis_eeg")
            )
            MultiAxisDetailCard(
                axisTitle = "AXIS III // SHA",
                score = eval.codexAxisScore,
                metricLine1 = if (state.sha256Verified) "Digest Verified" else "Hash Mismatch",
                metricLine2 = "${state.cleanedScriptPreview.length}B Sanitized",
                accentColor = NeonPurple,
                onQuickFix = { onRemediateSingleAxis("SEAL_SHA256") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("card_axis_sha")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val isCriticalRedline = isUnicriticCriticalDrift(
            verdictLevel = eval.verdictLevel,
            effectiveDrift = state.effectiveUnicriticDrift,
            redlineThreatOverride = state.redlineThreatActive
        )
        val effectState = ShaderEffectState(
            isCriticalDrift = isCriticalRedline,
            driftIntensity = state.effectiveUnicriticDrift,
            biometricLoadIntensity = state.biometricLoadIntensity,
            simTime = state.simTime,
            redlineThreatActive = state.redlineThreatActive
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianBg.copy(alpha = 0.88f))
                .border(
                    width = 1.dp,
                    color = if (isCriticalRedline) NeonPink else SlateBorder,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
                .testTag("card_shader_effect_modifier_status"),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.axisTProtocolCode,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCriticalRedline) NeonPink else NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
                StatusBadge(
                    text = if (isCriticalRedline) "REDLINE: ACTIVE" else "AX-IS-T: NOMINAL",
                    color = if (isCriticalRedline) NeonPink else NeonGreen
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "BIO LOAD INTENSITY",
                    value = "${(state.biometricLoadIntensity * 100).roundToInt()}% (${"%.2f".format(state.biometricLoadIntensity)})",
                    valueColor = if (state.biometricLoadIntensity > 0.72f) NeonPink else NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "SHADER DISTORTION",
                    value = "%.2f".format(effectState.compositeDistortion),
                    valueColor = if (effectState.compositeDistortion > 0.25f) NeonPink else NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "MODIFIER PIPE",
                    value = if (state.unicriticDistortionEnabled) "AGSL+LAYER" else "BYPASSED",
                    valueColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onToggleDistortion(!state.unicriticDistortionEnabled)
                    }
                ) {
                    Checkbox(
                        checked = state.unicriticDistortionEnabled,
                        onCheckedChange = onToggleDistortion,
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonPink,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("check_unicritic_shader_distortion")
                    )
                    Text(
                        text = "ShaderEffectModifier (Drift × Biometric Load)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontSize = 10.sp
                    )
                }
                StatusBadge(
                    text = "DRIFT: ${"%.2f".format(state.effectiveUnicriticDrift)}",
                    color = if (state.effectiveUnicriticDrift > 0.2f) NeonPink else NeonCyan
                )
            }
        }
    }
}

@Composable
private fun MultiAxisDetailCard(
    axisTitle: String,
    score: Int,
    metricLine1: String,
    metricLine2: String,
    accentColor: Color,
    onQuickFix: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianBg.copy(alpha = 0.85f))
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onQuickFix() }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = axisTitle,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontSize = 8.sp
            )
            Text(
                text = "$score%",
                style = MaterialTheme.typography.titleSmall,
                color = if (score >= 80) NeonGreen else if (score >= 55) NeonAmber else NeonPink,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = metricLine1,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontSize = 9.sp,
            maxLines = 1
        )
        Text(
            text = metricLine2,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 8.sp,
            maxLines = 1
        )
    }
}

@Composable
fun UnicriticRecentRemediationsSummaryCard(
    remediations: List<RemediationActionSummary>,
    healCycles: Int,
    onExecuteRemediation: () -> Unit
) {
    val avgDelta = if (remediations.isNotEmpty()) {
        remediations.map { (it.scoreAfter - it.scoreBefore).coerceAtLeast(0) }.average().roundToInt()
    } else {
        0
    }

    HudCard(borderColor = NeonGreen.copy(alpha = 0.55f)) {
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
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = "Recent Remediation Actions",
                    tint = NeonGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "RECENT REMEDIATION ACTIONS SUMMARY",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonGreen
                )
            }
            StatusBadge(
                text = "${remediations.size} ACTIONS",
                color = NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Summary KPI Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MiniStatBox(
                title = "TOTAL REMEDIATIONS",
                value = "${remediations.size}",
                valueColor = NeonGreen,
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                title = "AVG CONSENSUS GAIN",
                value = "+$avgDelta% PTS",
                valueColor = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                title = "SELF-HEAL CYCLES",
                value = "#$healCycles",
                valueColor = NeonPurple,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.testTag("recent_remediations_list")
        ) {
            remediations.take(5).forEach { item ->
                val gain = (item.scoreAfter - item.scoreBefore).coerceAtLeast(0)
                val tagColor = when (item.axisTag) {
                    "ALL AXES" -> NeonGreen
                    "GLSL AXIS" -> NeonCyan
                    "EEG AXIS" -> NeonAmber
                    else -> NeonPurple
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianBg.copy(alpha = 0.85f))
                        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                        .padding(9.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(text = item.axisTag, color = tagColor)
                            Text(
                                text = item.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 8.sp
                            )
                        }
                        StatusBadge(
                            text = "${item.scoreBefore}% → ${item.scoreAfter}% (+$gain%)",
                            color = NeonGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.actionTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = item.subsystemsRestored,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadge(text = item.outcomeBadge, color = NeonCyan)
                    }
                }
            }
        }
    }
}

@Composable
private fun AxisScoreBar(
    label: String,
    score: Int,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp
            )
            Text(
                text = "$score%",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (score / 100f).coerceIn(0f, 1f) },
            color = color,
            trackColor = ObsidianBg,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

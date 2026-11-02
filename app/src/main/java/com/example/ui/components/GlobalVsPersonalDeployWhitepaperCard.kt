package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.DeployTierMode
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

@Composable
fun GlobalVsPersonalDeployWhitepaperCard(
    state: AxiomUiState,
    onSelectDeployTierMode: (DeployTierMode) -> Unit,
    onUpdateDecayLambda: (Float) -> Unit,
    onStepDecayEpoch: () -> Unit,
    onSelectOwnDevLogicNode: (String) -> Unit,
    onExecuteUnifiedDeployAndSealWhitepaper: () -> Unit,
    onCopyToast: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val wpState = state.unifiedDeployWhitepaperState
    val decay = wpState.decayMetrics
    val selectedNode = wpState.ownDevLogicNodes.find { it.id == wpState.selectedLogicNodeId }
        ?: wpState.ownDevLogicNodes.firstOrNull()

    HudCard(
        borderColor = NeonGreen,
        modifier = modifier.testTag("card_global_vs_personal_deploy_whitepaper")
    ) {
        // 1. Header: Unified Global vs Personal [PaidvsFREE] & Whitepaper Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GLOBAL VS PERSONAL DEPLOY [PaidvsFREE] // WHITEPAPER",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Start: MiCA Below -> Now: ${wpState.personalDevAccountTag} • ${wpState.ownDevLogicCanonicalAlias}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 8.5.sp
                )
            }
            StatusBadge(
                text = "SUBS: $0.00 • DECAY ${decay.corpGreedDecayPct}%",
                color = NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. [PaidvsFREE] Deploy Mode Selector (Personal Account:DEV vs Global Decay Bond)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DeployTierMode.entries.forEach { mode ->
                val isSelected = wpState.activeTierMode == mode
                val accent = if (mode == DeployTierMode.PERSONAL_DEV_FREE) NeonGreen else NeonCyan
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) accent.copy(alpha = 0.22f) else ObsidianBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) accent else SlateBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectDeployTierMode(mode) }
                        .padding(vertical = 8.dp, horizontal = 6.dp)
                        .testTag("btn_deploy_tier_${mode.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mode.badgeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) accent else TextPrimary,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (mode == DeployTierMode.PERSONAL_DEV_FREE) {
                                "Sovereign Local • $0.00 Forever"
                            } else {
                                "1x Bond -> Commons Decay"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 7.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Anti-Subscription Corporate Greed Decay Telemetry
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianBg.copy(alpha = 0.88f))
                .border(1.dp, NeonGreen.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ANTI-SUBSCRIPTION CORPORATE GREED DECAY ENGINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "S:OpenSource(nosubs!-asis)Contract!",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAmber,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "RECURRING SUBS",
                    value = "$0.00 / FOREVER",
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "GREED DECAYED",
                    value = "${decay.corpGreedDecayPct}%",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "COMMONS POOL",
                    value = "+${"%.0f".format(decay.commonsSubsidyUnits)} U",
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "START NOW",
                    value = "DEV [FREE]",
                    valueColor = NeonAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            LinearProgressIndicator(
                progress = { (decay.corpGreedDecayPct / 100f).coerceIn(0.02f, 1.0f) },
                color = NeonGreen,
                trackColor = SlateBorder.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Text(
                text = decay.formulaExpression,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace
            )

            LabeledSliderRow(
                label = "Corporate Bond Decay Rate λ (Epoch #${decay.decayEpoch})",
                valueText = "λ = ${"%.2f".format(decay.decayLambda)}",
                valueColor = NeonGreen,
                value = decay.decayLambda,
                valueRange = 0.05f..1.50f,
                onValueChange = onUpdateDecayLambda,
                testTag = "slider_corp_decay_lambda"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onStepDecayEpoch,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.20f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .testTag("btn_step_corp_decay_epoch")
                ) {
                    Text(
                        text = "STEP DECAY EPOCH +1 (EPOCH #${decay.decayEpoch + 1})",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(wpState.ownDevLogicRootPath))
                        onCopyToast("Copied ${wpState.ownDevLogicCanonicalAlias} path")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple.copy(alpha = 0.20f),
                        contentColor = NeonPurple
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                        .testTag("btn_copy_own_dev_logic_path")
                ) {
                    Text(
                        text = "COPY ${wpState.ownDevLogicCanonicalAlias}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Refined Whitepaper Side-by-Side Comparison: MiCA Baseline [Below] <-> Remembered Relay [Personal account:DEV]
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianBg.copy(alpha = 0.90f))
                .border(1.dp, NeonCyan.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REFINED WHITEPAPER // SIDE-BY-SIDE REMEMBERED RELAY",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(
                    text = "MiCA BELOW -> [Personal account:DEV]",
                    color = NeonAmber
                )
            }

            // Column headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(SlateBorder.copy(alpha = 0.25f))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "START AS OF MiCA BELOW (FLOOR)",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAmber,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "WHERE WE START NOW: [Personal account:DEV]",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            wpState.whitepaperComparisonRows.forEach { row ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBg)
                        .border(
                            1.dp,
                            if (row.verifiedLocked) SlateBorder else NeonPink,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(6.dp)
                        .testTag("whitepaper_row_${row.axisCode}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${row.axisCode} // ${row.dimensionTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = row.ownDevLogicPath,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPurple,
                            fontSize = 7.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonAmber.copy(alpha = 0.08f))
                                .border(1.dp, NeonAmber.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                                .padding(5.dp)
                        ) {
                            Text(
                                text = row.micaBaselineBelow,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontSize = 8.5.sp
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonGreen.copy(alpha = 0.10f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                                .padding(5.dp)
                        ) {
                            Text(
                                text = row.rememberedRelayPersonalDevNow,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontSize = 8.5.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. 1+ /Own/Dev/Logic_States Sovereign State Registry Inspector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianBg.copy(alpha = 0.92f))
                .border(1.dp, NeonPurple.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
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
                        text = "SOVEREIGN LOGIC REGISTRY // ${wpState.ownDevLogicCanonicalAlias}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = wpState.ownDevLogicRootPath,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 7.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                StatusBadge(
                    text = "${wpState.ownDevLogicNodes.count { it.isLockedTrue }}/${wpState.ownDevLogicNodes.size} LOCKED",
                    color = NeonGreen
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                wpState.ownDevLogicNodes.forEach { node ->
                    val isSelected = node.id == selectedNode?.id
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) NeonPurple.copy(alpha = 0.25f) else ObsidianBg)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) NeonPurple else SlateBorder,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onSelectOwnDevLogicNode(node.id) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("chip_own_dev_logic_${node.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${node.id} • ${node.code}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) NeonPurple else TextPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = node.activeValue,
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonGreen,
                                fontSize = 7.5.sp
                            )
                        }
                    }
                }
            }

            selectedNode?.let { node ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBg)
                        .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "${node.rememberedRelaySource} -> ${node.logicPath}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "EXPR: ${node.stateExpression}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "STATE SHA-256: ${node.stateSha256}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        fontSize = 7.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Button(
                onClick = onExecuteUnifiedDeployAndSealWhitepaper,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.24f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_seal_unified_whitepaper_own_dev")
            ) {
                Text(
                    text = "UNIFY [PaidvsFREE] DEPLOY & SEAL WHITEPAPER -> 1+ /Own/Dev/Logic_States",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

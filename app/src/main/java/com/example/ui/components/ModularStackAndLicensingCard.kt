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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.DeployTargetDeviceMode
import com.example.service.EnvComparisonCategoryFilter
import com.example.service.MetallurgiaSteelAlloyPreset
import com.example.service.ModularityBlockSpec
import com.example.ui.theme.AntiMagAusteniteSilver
import com.example.ui.theme.AntiMagCobaltCyan
import com.example.ui.theme.AntiMagFaradayEmerald
import com.example.ui.theme.AntiMagTitaniumGold
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SteelAlloyBrushedSurface
import com.example.ui.theme.SteelAlloyChromeEdge
import com.example.ui.theme.SteelAlloyGunmetal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AxiomUiState

@Composable
fun ModularStackAndLicensingCard(
    state: AxiomUiState,
    onSelectModularityBlock: (String) -> Unit,
    onSelectEnvComparisonFilter: (EnvComparisonCategoryFilter) -> Unit,
    onSelectMarketingPillar: (String) -> Unit,
    onSimulateLicensingDrift: () -> Unit,
    onSelfCorrectAndSealLicensing: () -> Unit,
    onLoadModularityBlockIntoVerifier: (ModularityBlockSpec) -> Unit,
    onSelectTargetDeviceMode: (DeployTargetDeviceMode) -> Unit = {},
    onSelectMetallurgiaPreset: (MetallurgiaSteelAlloyPreset) -> Unit = {},
    onEngageMetallurgiaAntiMagPosition: (MetallurgiaSteelAlloyPreset) -> Unit = {},
    onVerifyAndSeal2faDeployLogic: () -> Unit = {},
    onCopyToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val modState = state.modularStackLicensingState
    val activeAlloy = modState.activeMetallurgiaPreset
    val twoFa = modState.twoFactorComparison
    val activeBlock = modState.modularityBlocks.find { it.id == modState.selectedBlockId }
        ?: modState.modularityBlocks.first()
    val filteredMatrix = modState.comparisonRows.filter { row ->
        modState.selectedEnvFilter == EnvComparisonCategoryFilter.ALL_DIMENSIONS ||
            row.category == modState.selectedEnvFilter
    }
    val activePillar = modState.marketingPillars.find { it.id == modState.selectedMarketingPillarId }
        ?: modState.marketingPillars.first()

    HudCard(
        borderColor = if (modState.licensingCompliantAll) AntiMagCobaltCyan else NeonAmber,
        modifier = modifier.testTag("card_modular_stack_and_licensing")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "METALLURGIA:THEME(STEEL ALLOY ANTIMAG POSITION) // 2FA & MODULARITY",
                    style = MaterialTheme.typography.titleSmall,
                    color = AntiMagCobaltCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Allowed to Compare 2FA: TRUE • Termux vs. All Else • fixsnake-v2 (268435457) • Self-Healing T&C",
                    style = MaterialTheme.typography.labelSmall,
                    color = AntiMagAusteniteSilver,
                    fontSize = 9.sp
                )
            }
            StatusBadge(
                text = if (twoFa.factorsMatchVerified) "2FA MATCH: TRUE" else "2FA DRIFT WARN",
                color = if (twoFa.factorsMatchVerified) AntiMagFaradayEmerald else NeonAmber
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Summary KPI Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MiniStatBox(
                title = "STEEL ALLOY MU_R",
                value = "%.3f".format(activeAlloy.relativePermeabilityMuR),
                valueColor = AntiMagAusteniteSilver,
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                title = "COMPARE 2FA",
                value = if (twoFa.allowedToCompare2fa) "ALLOWED: TRUE" else "DENIED",
                valueColor = AntiMagFaradayEmerald,
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                title = "URI BITMASK",
                value = "268435457",
                valueColor = AntiMagCobaltCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 0A: DESIGN TACTICS: Metallurgia:THEME(STEEL ALLOY ANTIMAG POSITION)
        Text(
            text = "0A. DESIGN TACTICS: Metallurgia:THEME(STEEL ALLOY ANTIMAG POSITION)",
            style = MaterialTheme.typography.labelSmall,
            color = AntiMagAusteniteSilver,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MetallurgiaSteelAlloyPreset.entries.forEach { preset ->
                val selected = preset == activeAlloy
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (selected) SteelAlloyBrushedSurface
                            else SteelAlloyGunmetal.copy(alpha = 0.85f)
                        )
                        .border(
                            1.dp,
                            if (selected) AntiMagCobaltCyan else SteelAlloyChromeEdge,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectMetallurgiaPreset(preset) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("chip_metallurgia_${preset.id}")
                ) {
                    Column {
                        Text(
                            text = preset.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) AntiMagCobaltCyan else AntiMagAusteniteSilver,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "mu_r=${preset.relativePermeabilityMuR} • u_antiMag=${preset.antiMagPulseTarget}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SteelAlloyBrushedSurface.copy(alpha = 0.92f))
                .border(1.dp, SteelAlloyChromeEdge, RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activeAlloy.alloyDesignation,
                    style = MaterialTheme.typography.labelMedium,
                    color = AntiMagAusteniteSilver,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(text = activeAlloy.positionVectorLabel, color = AntiMagCobaltCyan)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = activeAlloy.tacticalNote,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ACTIVE 3D SHADER TELEMETRY: u_antiMag=${"%.2f".format(state.antiMagPulse)} (Target ${activeAlloy.antiMagPulseTarget}) • u_magPressure=${"%.2f".format(state.magPressure)} (Target ${activeAlloy.magPressureTarget}) • u_hexScale=${"%.2f".format(state.hexScale)}",
                style = MaterialTheme.typography.bodySmall,
                color = AntiMagTitaniumGold,
                fontSize = 8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { onEngageMetallurgiaAntiMagPosition(activeAlloy) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AntiMagCobaltCyan.copy(alpha = 0.22f),
                    contentColor = AntiMagCobaltCyan
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AntiMagCobaltCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_engage_metallurgia_antimag_position")
            ) {
                Text(
                    text = "ENGAGE ${activeAlloy.badge} IN 3D VIEWPORT",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 0B: ALLOWED TO COMPARE 2FA // DEVICE DEPLOY OUTPUT LOGIC VERIFIER
        Text(
            text = "0B. ALLOWED TO COMPARE 2FA // CONFIRMED DEVICE DEPLOY OUTPUT LOGIC",
            style = MaterialTheme.typography.labelSmall,
            color = AntiMagFaradayEmerald,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DeployTargetDeviceMode.entries.forEach { mode ->
                val selected = mode == twoFa.targetDeviceMode
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (selected) AntiMagFaradayEmerald.copy(alpha = 0.22f)
                            else ObsidianBg.copy(alpha = 0.78f)
                        )
                        .border(
                            1.dp,
                            if (selected) AntiMagFaradayEmerald else SlateBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectTargetDeviceMode(mode) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("chip_2fa_device_${mode.id}")
                ) {
                    Column {
                        Text(
                            text = mode.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) AntiMagFaradayEmerald else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                        Text(
                            text = mode.runtimeEnvToken,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianBg.copy(alpha = 0.90f))
                .border(1.dp, AntiMagFaradayEmerald.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PAIRING TOKEN: ${twoFa.composite2faPairingToken}",
                    style = MaterialTheme.typography.labelMedium,
                    color = AntiMagFaradayEmerald,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(
                    text = if (twoFa.factorsMatchVerified) "check:pass:true_only" else "REMEDIATE DRIFT",
                    color = if (twoFa.factorsMatchVerified) NeonGreen else NeonPink
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "FACTOR 1 (MANIFEST & CONTRACT SHA-256): ${twoFa.factorOneManifestDigest.take(32)}...",
                style = MaterialTheme.typography.bodySmall,
                color = NeonCyan,
                fontSize = 8.sp
            )
            Text(
                text = "FACTOR 2 (DEVICE + METALLURGIA ANTIMAG): ${twoFa.factorTwoDeviceRuntimeDigest.take(32)}...",
                style = MaterialTheme.typography.bodySmall,
                color = AntiMagAusteniteSilver,
                fontSize = 8.sp
            )
            Text(
                text = "DISPATCH ROUTE: ${twoFa.targetDeviceMode.dispatchSummary}",
                style = MaterialTheme.typography.bodySmall,
                color = NeonGreen,
                fontSize = 8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(SteelAlloyGunmetal)
                    .border(1.dp, SteelAlloyChromeEdge, RoundedCornerShape(4.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = twoFa.expectedTerminalOutputTrace,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary,
                    fontSize = 8.5.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onVerifyAndSeal2faDeployLogic,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiMagFaradayEmerald.copy(alpha = 0.24f),
                        contentColor = AntiMagFaradayEmerald
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .border(1.dp, AntiMagFaradayEmerald, RoundedCornerShape(6.dp))
                        .testTag("btn_verify_seal_2fa_deploy_logic")
                ) {
                    Text(
                        text = "VERIFY & SEAL 2FA OUTPUT LOGIC",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(twoFa.expectedTerminalOutputTrace))
                        onCopyToast("Copied 2FA Device Deploy Output Trace (${twoFa.composite2faPairingToken})")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiMagCobaltCyan.copy(alpha = 0.20f),
                        contentColor = AntiMagCobaltCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, AntiMagCobaltCyan, RoundedCornerShape(6.dp))
                        .testTag("btn_copy_2fa_output_trace")
                ) {
                    Text(
                        text = "COPY 2FA TRACE",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 1: 6 Shared Modularity Blocks (MOD-01 .. MOD-06)
        Text(
            text = "1. UNIFIED MODULARITY BLOCKS (METAEYE-VOID + SY4-EEG BLOCKMIND)",
            style = MaterialTheme.typography.labelSmall,
            color = NeonGreen,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            modState.modularityBlocks.forEach { block ->
                val selected = block.id == activeBlock.id
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (selected) NeonCyan.copy(alpha = 0.22f)
                            else ObsidianBg.copy(alpha = 0.75f)
                        )
                        .border(
                            1.dp,
                            if (selected) NeonCyan else SlateBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectModularityBlock(block.id) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("chip_modularity_block_${block.id.lowercase().replace('-', '_')}")
                ) {
                    Column {
                        Text(
                            text = "${block.id} // ${block.shortTag}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) NeonCyan else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        Text(
                            text = block.sharedStackScope,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Active Modularity Block Detail Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianBg.copy(alpha = 0.88f))
                .border(1.dp, NeonCyan.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "${activeBlock.id}: ${activeBlock.title}",
                style = MaterialTheme.typography.labelMedium,
                color = NeonCyan,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "FAILED ATTEMPT LESSON: ${activeBlock.rootCauseLesson}",
                style = MaterialTheme.typography.bodySmall,
                color = NeonAmber,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "MODULAR REMEDY: ${activeBlock.architecturalRemedy}",
                style = MaterialTheme.typography.bodySmall,
                color = NeonGreen,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(4.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = activeBlock.codeSnippet,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary,
                    fontSize = 9.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onLoadModularityBlockIntoVerifier(activeBlock) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.22f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .testTag("btn_load_modularity_block_verifier")
                ) {
                    Text(
                        text = "LOAD ${activeBlock.id} INTO SHA-256 VERIFIER",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(activeBlock.codeSnippet))
                        onCopyToast("Copied ${activeBlock.id} (${activeBlock.shortTag}) to clipboard")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen.copy(alpha = 0.18f),
                        contentColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                        .testTag("btn_copy_modularity_block")
                ) {
                    Text(
                        text = "COPY BLOCK",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 2: Termux (Bionic) vs. All Standard Environments (10-Dimension Matrix)
        Text(
            text = "2. TERMUX (ANDROID BIONIC) vs. ALL STANDARD ENVIRONMENTS (LINUX / PI 4)",
            style = MaterialTheme.typography.labelSmall,
            color = NeonPurple,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EnvComparisonCategoryFilter.entries.forEach { filter ->
                val selected = filter == modState.selectedEnvFilter
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (selected) NeonPurple.copy(alpha = 0.24f)
                            else ObsidianBg.copy(alpha = 0.7f)
                        )
                        .border(
                            1.dp,
                            if (selected) NeonPurple else SlateBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectEnvComparisonFilter(filter) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("chip_env_filter_${filter.name.lowercase()}")
                ) {
                    Text(
                        text = filter.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) NeonPurple else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        filteredMatrix.forEach { row ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg.copy(alpha = 0.82f))
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                    .padding(7.dp)
            ) {
                Text(
                    text = row.dimension,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "TERMUX (BIONIC): ${row.termuxBionicRule}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonAmber,
                    fontSize = 9.sp
                )
                Text(
                    text = "ALL STANDARD ENVS: ${row.allStandardEnvironmentsRule}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonGreen,
                    fontSize = 9.sp
                )
                Text(
                    text = "FAILURE PREVENTED: ${row.failurePrevented}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Section 3: Marketing Approach Structure Recommender (4 Pillars)
        Text(
            text = "3. MARKETING APPROACH STRUCTURE RECOMMENDATIONS",
            style = MaterialTheme.typography.labelSmall,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            modState.marketingPillars.forEach { pillar ->
                val selected = pillar.id == activePillar.id
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (selected) NeonGreen.copy(alpha = 0.22f)
                            else ObsidianBg.copy(alpha = 0.7f)
                        )
                        .border(
                            1.dp,
                            if (selected) NeonGreen else SlateBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectMarketingPillar(pillar.id) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("chip_marketing_pillar_${pillar.id.lowercase().replace('-', '_')}")
                ) {
                    Text(
                        text = pillar.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) NeonGreen else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianBg.copy(alpha = 0.86f))
                .border(1.dp, NeonGreen.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activePillar.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(text = activePillar.metricAnchor, color = NeonCyan)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "AUDIENCE: ${activePillar.targetAudience}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 9.sp
            )
            Text(
                text = "POSITIONING: ${activePillar.corePositioning}",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontSize = 9.sp
            )
            Text(
                text = "PLAYBOOK: ${activePillar.actionablePlaybook}",
                style = MaterialTheme.typography.bodySmall,
                color = NeonCyan,
                fontSize = 9.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 4: Self-Licensing & Automatic Correction of Licensing T&C for Full Compliance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "4. SELF-LICENSING & AUTOMATIC CORRECTION OF LICENSING T&C",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (modState.licensingCompliantAll) NeonGreen else NeonAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${modState.contractIdentifier} • Self-Heals: #${modState.autoSelfCorrectedCount} • SHA-256: ${modState.licensingSealSha256.take(16)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        modState.licensingClauses.forEach { clause ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg.copy(alpha = 0.85f))
                    .border(
                        1.dp,
                        if (clause.isCompliant) NeonGreen.copy(alpha = 0.45f) else NeonPink,
                        RoundedCornerShape(6.dp)
                    )
                    .padding(7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${clause.clauseCode} — ${clause.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (clause.isCompliant) NeonGreen else NeonPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        text = if (clause.isCompliant) "COMPLIANT" else "DRIFT",
                        color = if (clause.isCompliant) NeonGreen else NeonPink
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (clause.isCompliant) clause.canonicalRule else clause.driftedRule,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (clause.isCompliant) TextPrimary else NeonAmber,
                    fontSize = 9.sp
                )
                Text(
                    text = "AUTO-CORRECTURE: ${clause.selfCorrectionAction}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonCyan,
                    fontSize = 8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = onSimulateLicensingDrift,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonAmber.copy(alpha = 0.18f),
                    contentColor = NeonAmber
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, NeonAmber, RoundedCornerShape(6.dp))
                    .testTag("btn_simulate_licensing_drift")
            ) {
                Text(
                    text = "SIMULATE T&C DRIFT",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onSelfCorrectAndSealLicensing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.24f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1.4f)
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_self_correct_seal_licensing")
            ) {
                Text(
                    text = "SELF-CORRECT & SEAL T&C COMPLIANCE",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

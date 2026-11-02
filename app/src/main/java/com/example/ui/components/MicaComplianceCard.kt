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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CodexEntry
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AxiomUiState

enum class MicaComplianceRegime(
    val id: String,
    val shortLabel: String,
    val title: String,
    val legalBasis: String,
    val accentColor: Color
) {
    TODAY_PRE_REGIME(
        id = "today_pre",
        shortLabel = "TODAY PRE",
        title = "Today / Pre-Regime Non-Custodial Safe Harbor",
        legalBasis = "MiCA Art. 2(1)-(4) & Recital 22 • S:OpenSource(nosubs!-asis)Contract!",
        accentColor = NeonGreen
    ),
    FUTURE_MICA_ARCH(
        id = "fut_mica_arch",
        shortLabel = "FUT MiCA ARCH",
        title = "Future MiCA + DORA Operational Resilience Architecture",
        legalBasis = "EU Reg. 2023/1114 + DORA 2022/2554 + Art. 66 Compute Telemetry",
        accentColor = NeonCyan
    ),
    DUAL_HORIZON_LOCK(
        id = "dual_lock",
        shortLabel = "TODAY + FUT [if D~]",
        title = "Synchronized Today Pre + Future MiCA Architecture (if D~ Gate)",
        legalBasis = "Zero-Token Local-First Safe Harbor + DORA/MiCA Cryptographic Audit",
        accentColor = NeonPurple
    )
}

object MicaComplianceCatalog {
    fun buildVerifiableMicaContract(
        regime: MicaComplianceRegime,
        dWaveDelta: Float,
        dWaveClampedSafe: Boolean,
        gigaOpsLoad: Float
    ): CodexEntry {
        val code = """
            // ============================================================================
            // MICA COMPLIANCE ARCHITECTURE // TODAY PRE + FUT MiCA ARCH [SO IF D~]
            // REGIME MODE : ${regime.title} (${regime.legalBasis})
            // D~ DELTA    : ${"%.3f".format(dWaveDelta)} (CLAMPED_SAFE = $dWaveClampedSafe)
            // ============================================================================
            // 1. TODAY / PRE-REGIME SAFE HARBOR [MiCA Art. 2 & Recital 22]:
            //    - Zero Token Issuance: Not an ART (Title III), Not an EMT (Title IV).
            //    - Non-Custodial Local Tooling: Not a CASP (Title V); ONLY_LOCAL_LOGIC=1.
            //    - Governed by 'S:OpenSource(nosubs!-asis)Contract!' ("AS IS", zero-liability).
            //
            // 2. FUTURE MiCA & DORA ARCHITECTURE [EU 2023/1114 + EU 2022/2554]:
            //    - ICT Risk & Drift Governance: Unicritic 3-Axis Consensus + AX-IS-T Self-Heal.
            //    - Immutable Forensic Provenance: MetaEye-Void SHA-256 + Room SpectDao Audit.
            //    - Art. 66 Compute Sustainability: Real-time GigaOps telemetry (${"%.1f".format(gigaOpsLoad)} GFLOPs).
            //
            // 3. CONDITIONAL 'IF D~' GATE [Decentralization / Drift / Derivative Wave D~]:
            //    - Rule: if (D~ > 0.35) { enforce_fut_mica_dora_audit(); clamp_to_local_safe_harbor(); }
            //    - Current D~ Status: ${if (dWaveClampedSafe) "SEALED COMPLIANT (D~ CLAMPED)" else "MONITORING D~"}
            // ============================================================================
            pub struct MicaFutureAndTodayPreCompliance {
                pub regime_id: &'static str,          // "${regime.id}"
                pub only_local_logic: bool,           // true
                pub zero_custody_zero_token: bool,    // true (Recital 22 Safe Harbor)
                pub dora_ict_drift_governor: bool,    // true (Unicritic + AX-IS-T)
                pub d_tilde_wave_delta: f32,          // ${"%.3f".format(dWaveDelta)}
                pub if_d_tilde_safe_sealed: bool,     // $dWaveClampedSafe
            }
        """.trimIndent()

        return CodexEntry(
            id = "mica-fut-today-pre-d-wave",
            crateName = "mica_compliance_arch",
            registrySource = "EU 2023/1114 (MiCA) + DORA 2022/2554 // Recital 22",
            categoryTag = "MiCA // IF D~",
            summary = "Today Pre-Regime Safe Harbor + Future MiCA/DORA Architecture & Conditional if(D~) Gate",
            osaMapping = "Osa A-X-05 // Legal & Compliance Axis",
            codeSnippet = code
        )
    }
}

@Composable
fun MicaComplianceArchitectureCard(
    state: AxiomUiState,
    onSelectMicaRegime: (MicaComplianceRegime) -> Unit,
    onUpdateDWaveDelta: (Float) -> Unit,
    onExecuteIfDWaveClampAndSeal: () -> Unit,
    onSealMicaContractInVerifier: (CodexEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val regime = state.micaComplianceRegime
    val dWave = state.micaDWaveDelta
    val isClampedSafe = state.micaDWaveSafeSealed && dWave <= 0.35f

    val gateStatusText = when {
        dWave <= 0.35f -> "IF D~ <= 0.35: SAFE HARBOR SEALED"
        dWave <= 0.70f -> "IF D~ (${"%.2f".format(dWave)}): FUT-MiCA AUDIT"
        else -> "IF D~ > 0.70: CLAMP REQUIRED"
    }
    val gateStatusColor = when {
        dWave <= 0.35f -> NeonGreen
        dWave <= 0.70f -> NeonCyan
        else -> NeonAmber
    }

    HudCard(
        borderColor = gateStatusColor.copy(alpha = 0.8f),
        modifier = modifier.testTag("card_mica_fut_today_pre_compliance")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FUT MiCA ARCH & TODAY PRE // [SO IF D~]",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "EU 2023/1114 (MiCA) + DORA 2022/2554 • Recital 22 Safe Harbor & D~ Gate",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            }
            StatusBadge(
                text = gateStatusText,
                color = gateStatusColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 1. Regime Horizon Selector (Today Pre vs. Fut MiCA Arch vs. Dual Synchronized)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MicaComplianceRegime.entries.forEach { item ->
                val selected = regime == item
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (selected) item.accentColor.copy(alpha = 0.22f)
                            else ObsidianBg
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) item.accentColor else SlateBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectMicaRegime(item) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("chip_mica_regime_${item.id}")
                ) {
                    Text(
                        text = item.shortLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) item.accentColor else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Today Pre + Future MiCA Architectural Proof Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianBg)
                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "ACTIVE HORIZON: ${regime.title}",
                style = MaterialTheme.typography.labelSmall,
                color = regime.accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
            Text(
                text = "Legal Basis: ${regime.legalBasis}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 8.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "• TODAY PRE: Non-custodial, zero-token, local-only execution (ONLY_LOCAL_LOGIC=1). Outside CASP/ART/EMT scope under MiCA Art. 2 & Recital 22.",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontSize = 8.5.sp
            )
            Text(
                text = "• FUT MiCA ARCH: DORA-ready ICT drift resilience (Unicritic + AX-IS-T), immutable SHA-256 Merkle provenance (MetaEye-Void), and Art. 66 compute efficiency (${"%.1f".format(state.gigaOpsLoad)} GigaOps).",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontSize = 8.5.sp
            )
            Text(
                text = "• SO IF D~ RULE: if (D~ > 0.35) -> Auto-clamp D~ to 0.000 Non-Custodial Safe Harbor & seal SHA-256 compliance ledger.",
                style = MaterialTheme.typography.bodySmall,
                color = NeonGreen,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Interactive D~ (Decentralization / Drift / Derivative Wave) Slider
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Conditional Wave Parameter D~ (if D~ Gate)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
                Text(
                    text = "D~ = ${"%.3f".format(dWave)} ${if (isClampedSafe) "[COMPLIANT]" else "[EVALUATE]"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = gateStatusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
            Slider(
                value = dWave,
                onValueChange = onUpdateDWaveDelta,
                valueRange = 0.00f..1.00f,
                colors = SliderDefaults.colors(
                    thumbColor = gateStatusColor,
                    activeTrackColor = gateStatusColor,
                    inactiveTrackColor = SlateBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .testTag("slider_mica_d_wave_delta")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Action Buttons: Execute 'if D~' Clamp & Seal in SHA-256 Verifier
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onExecuteIfDWaveClampAndSeal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.22f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_mica_execute_if_d_wave")
            ) {
                Text(
                    text = "IF (D~) -> CLAMP & LOCK SAFE",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    val entry = MicaComplianceCatalog.buildVerifiableMicaContract(
                        regime = regime,
                        dWaveDelta = dWave,
                        dWaveClampedSafe = state.micaDWaveSafeSealed,
                        gigaOpsLoad = state.gigaOpsLoad
                    )
                    onSealMicaContractInVerifier(entry)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan.copy(alpha = 0.22f),
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_mica_seal_sha256_contract")
            ) {
                Text(
                    text = "SEAL MiCA ARCH SHA-256",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 5. Remembered Relay Bridge: Start as of MiCA Below -> Where We Start Now [Personal account:DEV]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianBg.copy(alpha = 0.90f))
                .border(1.dp, NeonGreen.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("mica_to_personal_dev_relay_strip"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "START AS OF MiCA BELOW -> NOW: ${state.unifiedDeployWhitepaperState.personalDevAccountTag}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${state.unifiedDeployWhitepaperState.ownDevLogicCanonicalAlias} • [PaidvsFREE] Subs=$0.00 • Greed Decay ${state.unifiedDeployWhitepaperState.decayMetrics.corpGreedDecayPct}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 7.5.sp
                )
            }
            StatusBadge(
                text = state.unifiedDeployWhitepaperState.activeTierMode.badgeLabel,
                color = NeonGreen
            )
        }
    }
}

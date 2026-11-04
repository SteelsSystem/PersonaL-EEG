package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.service.NetworkProbePreset
import com.example.service.ProtocolOEngineProvider
import com.example.service.ProtocolOFunctionId
import com.example.service.ProtocolOWireEvent
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

/**
 * Nav `◈` Orb trigger with 6px status dot:
 * - Red (`AtlasCrimsonRed`): no key in volatile `_key`
 * - Green (`NeonGreen`): key active in volatile `_key`
 * Zero label or attribute that reveals key material.
 */
@Composable
fun StealthGateOrbButton(
    orbKeyActive: Boolean,
    onClickOrb: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dotColor = if (orbKeyActive) NeonGreen else AtlasCrimsonRed
    Surface(
        onClick = onClickOrb,
        shape = RoundedCornerShape(6.dp),
        color = ObsidianBg.copy(alpha = 0.88f),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .border(1.dp, dotColor.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
            .testTag("btn_nav_stealth_orb_gate")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = "◈",
                style = MaterialTheme.typography.titleSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
                    .testTag("orb_status_dot_6px")
            )
        }
    }
}

/**
 * Protocol O: Integrity Core 2026 & Audius Keyless REST API Interconnect Card.
 * Consolidates `_gate`, `_cert`, `_engine` (`apiRequest` · `pollinations` · `audius` · `smart`),
 * `_util` (`shortHash` · `extractStructure`), `_locale`, `_fn` (7 functions), and `_wire()`.
 */
@Composable
fun ProtocolOIntegrityCoreCard(
    state: AxiomUiState,
    onWireEvent: (ProtocolOWireEvent) -> Unit,
    onSealProtocolOToCodex: () -> Unit = {},
    onCopyToast: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val proto = state.protocolOState
    val atlas = state.atlasKnowabilityState
    val clipboard = LocalClipboardManager.current

    val providerAccent = when (proto.activeProvider) {
        ProtocolOEngineProvider.GEMINI_O_GATE -> NeonCyan
        ProtocolOEngineProvider.AUDIUS_KEYLESS -> NeonAmber
        ProtocolOEngineProvider.POLLINATIONS_FREE -> NeonGreen
    }

    HudCard(
        borderColor = providerAccent,
        modifier = modifier.testTag("card_protocol_o_integrity_core")
    ) {
        // 1. Header: Protocol O: Integrity Core 2026 + ◈ Orb + Engine Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "PROTOCOL O: INTEGRITY CORE 2026 // SEALED IIFE",
                        style = MaterialTheme.typography.titleSmall,
                        color = providerAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (proto.orbKeyActive) NeonGreen else AtlasCrimsonRed)
                    )
                }
                Text(
                    text = "_gate (◈ Volatile) • _engine.smart() + _engine.audius() • _cert • _util.extractStructure • _wire()",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 8.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                StealthGateOrbButton(
                    orbKeyActive = proto.orbKeyActive,
                    onClickOrb = { onWireEvent(ProtocolOWireEvent.OpenSublayerGate) }
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(providerAccent.copy(alpha = 0.20f))
                        .border(1.dp, providerAccent, RoundedCornerShape(6.dp))
                        .clickable { onWireEvent(ProtocolOWireEvent.GenerateVolatileCertificate) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("btn_open_cert_sublayer")
                ) {
                    Text(
                        text = "📜 _cert [${proto.lastShortHash}]",
                        style = MaterialTheme.typography.labelSmall,
                        color = providerAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Sublayer Consolidation Strip + Locale Selector (_locale)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ENGINE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 7.5.sp
                )
                StatusBadge(
                    text = proto.activeProvider.badge,
                    color = providerAccent
                )
                StatusBadge(
                    text = if (proto.orbKeyActive) "◈ O-GATE: ACTIVE" else "◈ ZERO-KEY VOLATILE",
                    color = if (proto.orbKeyActive) NeonGreen else NeonCyan
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                proto.availableLocales.forEach { loc ->
                    val selected = proto.activeLocale == loc
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (selected) NeonCyan.copy(alpha = 0.22f) else ObsidianBg)
                            .border(1.dp, if (selected) NeonCyan else SlateBorder, RoundedCornerShape(4.dp))
                            .clickable { onWireEvent(ProtocolOWireEvent.SelectLocale(loc)) }
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                            .testTag("btn_protocol_o_locale_$loc")
                    ) {
                        Text(
                            text = loc,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) NeonCyan else TextSecondary,
                            fontSize = 7.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Consolidated 7 Sublayer Functions (_fn: 01 probe .. 07 voiceBriefing)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            ProtocolOFunctionId.entries.forEach { fn ->
                val isSelected = proto.activeFunction == fn
                val fnColor = when {
                    fn.directGeminiOnly -> NeonPink
                    fn == ProtocolOFunctionId.NETWORK_PROBE -> NeonAmber
                    else -> NeonCyan
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) fnColor.copy(alpha = 0.22f) else ObsidianBg)
                        .border(1.dp, if (isSelected) fnColor else SlateBorder, RoundedCornerShape(6.dp))
                        .clickable { onWireEvent(ProtocolOWireEvent.SelectSublayerFunction(fn)) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("btn_protocol_o_fn_${fn.id}")
                ) {
                    Text(
                        text = "[${fn.sectionCode}] ${fn.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) fnColor else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.6.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Network Probe C (Section 06) — Optgroups: FREE • GEMINI O-GATE • AUDIUS KEYLESS • CUSTOM
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianBg)
                .border(1.dp, NeonAmber.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NETWORK PROBE C (SECTION 06) // ${proto.selectedProbePreset.optGroup}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                )
                Text(
                    text = "Keyless & Zero-Forward-Hole",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontSize = 7.4.sp
                )
            }

            // Preset selector chips across FREE, GEMINI, AUDIUS KEYLESS, CUSTOM
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                NetworkProbePreset.entries.forEach { preset ->
                    val selected = proto.selectedProbePreset == preset
                    val chipCol = when (preset.provider) {
                        ProtocolOEngineProvider.AUDIUS_KEYLESS -> NeonAmber
                        ProtocolOEngineProvider.GEMINI_O_GATE -> NeonCyan
                        ProtocolOEngineProvider.POLLINATIONS_FREE -> NeonGreen
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (selected) chipCol.copy(alpha = 0.24f) else HudPanelBg)
                            .border(1.dp, if (selected) chipCol else SlateBorder, RoundedCornerShape(5.dp))
                            .clickable { onWireEvent(ProtocolOWireEvent.SelectNetworkProbePreset(preset)) }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("btn_probe_preset_${preset.id}")
                    ) {
                        Text(
                            text = preset.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) chipCol else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.4.sp
                        )
                    }
                }
            }

            // Query Input + _engine.smart() & _engine.audius() Triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = proto.queryInput,
                    onValueChange = { onWireEvent(ProtocolOWireEvent.UpdateQueryInput(it)) },
                    label = {
                        Text(
                            text = "Query / Track / Sublayer Input (${proto.activeFunction.title})",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeonCyan, fontSize = 9.5.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedContainerColor = HudPanelBg,
                        unfocusedContainerColor = HudPanelBg
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_protocol_o_query")
                )

                Button(
                    onClick = { onWireEvent(ProtocolOWireEvent.ExecuteActiveSublayerFunction) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.24f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .testTag("btn_execute_protocol_o_smart")
                ) {
                    Text(
                        text = "RUN _engine.smart()",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                }

                Button(
                    onClick = { onWireEvent(ProtocolOWireEvent.ExecuteAudiusKeylessRecon(proto.queryInput)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonAmber.copy(alpha = 0.26f),
                        contentColor = NeonAmber
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonAmber, RoundedCornerShape(6.dp))
                        .testTag("btn_execute_engine_audius")
                ) {
                    Text(
                        text = "🎵 _engine.audius()",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                }
            }

            // Live Audius Keyless Stream ⇄ Sonic Sound Swap "translate" Interconnect Banner
            val track = proto.audiusLastTrack
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonAmber.copy(alpha = 0.12f))
                    .border(1.dp, NeonAmber.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🎵 AUDIUS · KEYLESS STREAM ⇄ SONIC \"TRANSLATE\" O-RING: ${track.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                    Text(
                        text = "${track.artistHandle} • ${track.bpm} BPM • ${track.genre} • ${track.ddexProtocolRef} • Cant O-Ring ${"%.1f".format(atlas.intakeCantAngleDeg)}°",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 7.2.sp
                    )
                }
                Text(
                    text = "7-BAND SYNC",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.4.sp
                )
            }

            // _util.extractStructure(res) Key Tree Inspector (Rendered BEFORE Raw JSON Dump)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF060A12))
                        .border(1.dp, NeonGreen.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                        .testTag("box_protocol_o_key_tree")
                ) {
                    Text(
                        text = "_util.extractStructure() KEY TREE",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.4.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = proto.extractedKeyTree,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontSize = 7.4.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF060A12))
                        .border(1.dp, NeonCyan.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                        .clickable {
                            clipboard.setText(AnnotatedString(proto.rawJsonOutput))
                            onCopyToast("Copied Protocol O JSON (${proto.lastShortHash})")
                        }
                        .testTag("box_protocol_o_raw_json")
                ) {
                    Text(
                        text = "RAW JSON DUMP (${proto.lastShortHash})",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.4.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = proto.rawJsonOutput.lines().take(10).joinToString("\n"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 7.2.sp
                    )
                }
            }

            Text(
                text = proto.functionOutputSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 7.8.sp
            )

            // Predefined Deterministic Self-Update • Upgrade • Sync (v2026.04.12-DET-SYNC)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonGreen.copy(alpha = 0.08f))
                    .border(1.dp, NeonGreen.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                    .padding(7.dp)
                    .testTag("panel_predefined_deterministic_sync"),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ PREDEFINED DETERMINISTIC SELF-UPDATE • UPGRADE • SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.8.sp
                    )
                    Text(
                        text = "${proto.deterministicSyncVersion} • ${proto.deterministicVectorShortHash}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.4.sp
                    )
                }

                Text(
                    text = proto.deterministicSyncSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontSize = 7.3.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    proto.deterministicUpgradeNodes.forEach { node ->
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(ObsidianBg)
                                .border(1.dp, NeonGreen.copy(alpha = 0.55f), RoundedCornerShape(5.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "[${node.stepId}] ${node.subsystemTitle}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.1.sp
                            )
                            Text(
                                text = "${node.deterministicHash} • SYNCED",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 6.8.sp
                            )
                        }
                    }
                }
            }

            // Emergency Signal-Loss Self-Dependency Reload Slice & Pack + 6-Hop Relay Vector Merkle-Mesh
            val emergPack = proto.emergencyReloadPack
            val emergColor = when {
                emergPack.signalLossDetected && !emergPack.autoReloadArmed -> AtlasCrimsonRed
                emergPack.signalLossDetected -> NeonAmber
                else -> NeonCyan
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(emergColor.copy(alpha = 0.09f))
                    .border(1.dp, emergColor.copy(alpha = 0.72f), RoundedCornerShape(6.dp))
                    .padding(7.dp)
                    .testTag("panel_emergency_self_dependency_reload"),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🛡️ EMERGENCY SELF-DEPENDENCY RELOAD // 6-HOP RELAY VECTOR MESH",
                            style = MaterialTheme.typography.labelSmall,
                            color = emergColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.7.sp
                        )
                        Text(
                            text = "${emergPack.packId} • Merkle-Mesh Root ${emergPack.merkleMeshRootShortHash} (${emergPack.merkleMeshRootSha256.take(16)}...)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 7.0.sp
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (emergPack.autoReloadArmed) NeonGreen.copy(alpha = 0.22f) else AtlasCrimsonRed.copy(alpha = 0.22f))
                            .border(1.dp, if (emergPack.autoReloadArmed) NeonGreen else AtlasCrimsonRed, RoundedCornerShape(5.dp))
                            .clickable { onWireEvent(ProtocolOWireEvent.ToggleEmergencyAutoReloadArmed) }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("btn_toggle_emergency_auto_reload")
                    ) {
                        Text(
                            text = if (emergPack.autoReloadArmed) "AUTO-RELOAD: ARMED" else "AUTO-RELOAD: MANUAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (emergPack.autoReloadArmed) NeonGreen else AtlasCrimsonRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 6.9.sp
                        )
                    }
                }

                Text(
                    text = emergPack.statusBanner,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontSize = 7.2.sp
                )

                // 6-Hop Relay Vector Ring with Bidirectional Merkle-Mesh SHA-256 Joints
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    emergPack.relayVectorHops.forEach { hop ->
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(ObsidianBg)
                                .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(5.dp))
                                .clickable {
                                    clipboard.setText(AnnotatedString(emergPack.packedReloadSliceJson))
                                    onCopyToast("Copied ${emergPack.packId} 6-Hop Merkle-Mesh Slice")
                                }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("chip_relay_vector_${hop.hopId}")
                        ) {
                            Text(
                                text = "[${hop.hopId}] ${hop.vectorLabel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.0.sp
                            )
                            Text(
                                text = "MeshJoint ${hop.meshJointShortHash} • Fwd/Rev SHA256 OK",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonGreen,
                                fontSize = 6.7.sp
                            )
                            Text(
                                text = hop.sliceSpec.take(44),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 6.5.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Button(
                        onClick = { onWireEvent(ProtocolOWireEvent.TriggerEmergencySelfDependencyReload) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen.copy(alpha = 0.24f),
                            contentColor = NeonGreen
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .minimumInteractiveComponentSize()
                            .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                            .testTag("btn_emergency_slice_reload")
                    ) {
                        Text(
                            text = "🛡️ Emergency Slice & Reload (#${emergPack.reloadExecutionCount})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.4.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = { onWireEvent(ProtocolOWireEvent.SimulateSignalLossAndAutoReload) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonAmber.copy(alpha = 0.24f),
                            contentColor = NeonAmber
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .border(1.dp, NeonAmber, RoundedCornerShape(6.dp))
                            .testTag("btn_simulate_signal_loss_auto_reload")
                    ) {
                        Text(
                            text = "🚨 Signal-Loss Drill & Auto-Reload",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.4.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onWireEvent(ProtocolOWireEvent.TriggerPredefinedDeterministicSelfUpdateSync) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.24f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .testTag("btn_protocol_o_deterministic_sync")
                ) {
                    Text(
                        text = "⚡ Self-Update • Upgrade • Sync",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.6.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = { onWireEvent(ProtocolOWireEvent.GenerateVolatileCertificate) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen.copy(alpha = 0.22f),
                        contentColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                        .testTag("btn_generate_volatile_cert")
                ) {
                    Text(
                        text = "📜 Volatile #cert-sublayer",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.6.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onSealProtocolOToCodex,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple.copy(alpha = 0.22f),
                        contentColor = NeonPurple
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(0.95f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                        .testTag("btn_seal_protocol_o_codex")
                ) {
                    Text(
                        text = "Seal to Codex",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.6.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * `#sublayer-gate` — Stealth Full-Screen Credential Gate Modal (`_gate`).
 * Accessible via nav `◈` orb or `Ctrl+G`.
 * - Input field is password-masked (`PasswordVisualTransformation`), cleared on commit or close.
 * - Zero `localStorage` / zero `SharedPreferences` / zero DOM key serialization.
 * - `Purge` wipes `_key` from memory immediately.
 */
@Composable
fun ProtocolOSublayerGateModal(
    state: AxiomUiState,
    onWireEvent: (ProtocolOWireEvent) -> Unit
) {
    val proto = state.protocolOState
    var transientPasswordInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {
            transientPasswordInput = ""
            onWireEvent(ProtocolOWireEvent.CloseSublayerGate)
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .background(HudPanelBg)
                .border(1.5.dp, if (proto.orbKeyActive) NeonGreen else AtlasCrimsonRed, RoundedCornerShape(12.dp))
                .padding(16.dp)
                .testTag("modal_sublayer_gate"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "◈ #sublayer-gate // STEALTH O-GATE",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (proto.orbKeyActive) NeonGreen else AtlasCrimsonRed)
                    )
                }
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    modifier = Modifier
                        .clickable {
                            transientPasswordInput = ""
                            onWireEvent(ProtocolOWireEvent.CloseSublayerGate)
                        }
                        .padding(6.dp)
                        .testTag("btn_close_sublayer_gate")
                )
            }

            Text(
                text = "commit: 9fe2e76 • API Key Forward Hole Eliminated. Key lives exclusively in volatile `let _key = ''` inside sealed memory. Zero persistence, zero DOM echo, cleared on commit or close.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 9.sp
            )

            OutlinedTextField(
                value = transientPasswordInput,
                onValueChange = { transientPasswordInput = it },
                label = {
                    Text(
                        text = "Volatile O-Gate Credential (type=\"password\" • data-lpignore=\"true\")",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeonGreen, fontSize = 11.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = SlateBorder,
                    focusedContainerColor = ObsidianBg,
                    unfocusedContainerColor = ObsidianBg
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_sublayer_gate_password")
            )

            Text(
                text = proto.gateFeedbackMessage,
                style = MaterialTheme.typography.labelSmall,
                color = if (proto.orbKeyActive) NeonGreen else NeonAmber,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val toCommit = transientPasswordInput
                        transientPasswordInput = "" // Cleared immediately on commit!
                        onWireEvent(ProtocolOWireEvent.CommitVolatileGateKey(toCommit))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen.copy(alpha = 0.25f),
                        contentColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                        .testTag("btn_commit_sublayer_gate")
                ) {
                    Text(
                        text = "COMMIT [_gate.commit]",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        transientPasswordInput = ""
                        onWireEvent(ProtocolOWireEvent.PurgeVolatileGateKey)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AtlasCrimsonRed.copy(alpha = 0.28f),
                        contentColor = AtlasCrimsonRed
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, AtlasCrimsonRed, RoundedCornerShape(6.dp))
                        .testTag("btn_purge_sublayer_gate")
                ) {
                    Text(
                        text = "PURGE [_gate.purge]",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * `#cert-sublayer` — Volatile Integrity Certificate Modal (`_cert`).
 * - `_certData` lives strictly in memory and is wiped (`null`) on dismiss.
 * - Includes animated confidence bar, active engine badge (`Gemini · O-Gate`, `Pollinations.AI · FREE`,
 *   or `Audius · KEYLESS`), `shortHash`, ODM Register `0x200F`, and volatile Blob download.
 */
@Composable
fun ProtocolOCertSublayerModal(
    state: AxiomUiState,
    onWireEvent: (ProtocolOWireEvent) -> Unit,
    onCopyToast: (String) -> Unit
) {
    val cert = state.protocolOState.volatileCertificate ?: return
    val clipboard = LocalClipboardManager.current
    val animatedConfidence by animateFloatAsState(
        targetValue = (cert.confidenceScorePct / 100f).coerceIn(0.05f, 1f),
        animationSpec = tween(durationMillis = 650),
        label = "cert_confidence_anim"
    )

    Dialog(
        onDismissRequest = { onWireEvent(ProtocolOWireEvent.DismissVolatileCertificate) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(12.dp))
                .background(HudPanelBg)
                .border(1.5.dp, NeonGreen, RoundedCornerShape(12.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("modal_cert_sublayer"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📜 #cert-sublayer // VOLATILE INTEGRITY SEAL",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${cert.certId} • ${cert.shortHash} • Zero Persistence (Null-ed on Dismiss)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                StatusBadge(
                    text = cert.engineBadge,
                    color = NeonAmber
                )
            }

            // Animated Confidence Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "INTEGRITY CONFIDENCE SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${cert.confidenceScorePct}%",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(SlateBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedConfidence)
                            .background(NeonGreen)
                    )
                }
            }

            // Certificate Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg)
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Engine Badge: ${cert.engineBadge}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ShortHash: ${cert.shortHash} • ODM Reg: ${cert.odmRegisterHex}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
                Text(
                    text = "Knowability: ${cert.rgbFrqKnowableVerdict} • Audius: ${cert.audiusTrackTitle}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen
                )
                Text(
                    text = "SHA-256: ${cert.sha256Seal.take(36)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cert.sealStatement,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontSize = 9.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(cert.volatileBlobJson))
                        onWireEvent(ProtocolOWireEvent.DownloadVolatileCertificateBlob)
                        onCopyToast("Volatile Certificate Blob Exported (Auto-Revoke 5s)")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.25f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .testTag("btn_download_volatile_cert")
                ) {
                    Text(
                        text = "DOWNLOAD BLOB (5s Revoke)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onWireEvent(ProtocolOWireEvent.DismissVolatileCertificate) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink.copy(alpha = 0.25f),
                        contentColor = NeonPink
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonPink, RoundedCornerShape(6.dp))
                        .testTag("btn_dismiss_volatile_cert")
                ) {
                    Text(
                        text = "DISMISS [WIPE _certData]",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

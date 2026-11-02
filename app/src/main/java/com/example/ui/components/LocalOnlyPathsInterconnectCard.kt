package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.LocalInterconnectNode
import com.example.service.LocalOnlyPathsInterconnectEngine
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
fun LocalOnlyPathsInterconnectCard(
    state: AxiomUiState,
    onSelectInterconnectNode: (String) -> Unit,
    onExecuteServerlessLocalDeploy: () -> Unit,
    onInjectEgressFault: () -> Unit,
    onRemediateLocalOnly: () -> Unit,
    onCopyToast: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val deploy = state.localInterconnectState
    val clipboard = LocalClipboardManager.current
    val accentColor = if (deploy.checkPassTrueOnly) NeonGreen else NeonPink
    val selectedNode = deploy.nodes.firstOrNull { it.id == deploy.activeNodeId } ?: deploy.nodes.firstOrNull()

    HudCard(
        borderColor = accentColor,
        modifier = modifier.testTag("local_interconnect_card")
    ) {
        // Header Row: Title + check:pass:true_only Assertion Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LOCAL ONLY PATHS INTERCONNECT // SERVERLESS INFRA",
                    style = MaterialTheme.typography.titleSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Local Only Way of Serverless Infra Deploy • 6-Node Loopback & Vault Mesh",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 8.5.sp
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.22f))
                    .border(1.dp, accentColor, RoundedCornerShape(6.dp))
                    .clickable {
                        clipboard.setText(AnnotatedString(deploy.assertionToken))
                        onCopyToast("Copied Assertion: ${deploy.assertionToken}")
                    }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("badge_tunnel_check_pass_true_only")
            ) {
                Text(
                    text = deploy.assertionToken,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3-Stage Gate Invariants + Epoch Counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MiniStatBox(
                title = "STAGE 1: EGRESS",
                value = if (deploy.zeroEgressVerified) "0.0 KB/S [LOCAL]" else "EGRESS FAULT",
                valueColor = if (deploy.zeroEgressVerified) NeonGreen else NeonPink,
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                title = "STAGE 2: SHA-256",
                value = if (deploy.sha256SealVerified) "SEALED [64-HEX]" else "MISMATCH",
                valueColor = if (deploy.sha256SealVerified) NeonCyan else NeonPink,
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                title = "STAGE 3: ROOM",
                value = if (deploy.roomAuditVerified) "AUDIT #${deploy.roomAuditRecordCount}" else "UNSEALED",
                valueColor = if (deploy.roomAuditVerified) NeonPurple else NeonAmber,
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                title = "DEPLOY EPOCH",
                value = "#${deploy.deployEpoch}",
                valueColor = NeonGreen,
                modifier = Modifier.weight(0.8f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6-Node Local Only Paths Interconnect Mesh (2 columns x 3 rows)
        Text(
            text = "6-NODE LOCAL ONLY PATHS INTERCONNECT MESH (TAP NODE TO INSPECT & VERIFY HOP)",
            style = MaterialTheme.typography.labelSmall,
            color = NeonCyan,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            deploy.nodes.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pair.forEach { node ->
                        InterconnectNodeChip(
                            node = node,
                            isSelected = node.id == deploy.activeNodeId,
                            onClick = { onSelectInterconnectNode(node.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Selected Node Detail & Interconnect SHA-256 Readout
        selectedNode?.let { node ->
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg)
                    .border(
                        1.dp,
                        if (node.isStrictlyLocal) SlateBorder else NeonPink,
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "[${node.id} • ${node.code}] ${node.category.tag} (${node.transportMode})",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (node.isStrictlyLocal) NeonCyan else NeonPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp
                    )
                    Text(
                        text = "${node.latencyMicros} µs • ${if (node.isStrictlyLocal) "LOCAL-ONLY" else "NON-LOCAL FAULT"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (node.isStrictlyLocal) NeonGreen else NeonPink,
                        fontSize = 8.sp
                    )
                }
                Text(
                    text = "PATH: ${node.localPath}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontSize = 8.5.sp
                )
                Text(
                    text = "HOP SHA-256: ${node.hopSha256.take(24)}... | MESH SEAL: ${deploy.interconnectSealSha256.take(20)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAmber,
                    fontSize = 7.8.sp
                )
                Text(
                    text = deploy.statusSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = accentColor,
                    fontSize = 8.3.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Primary Action Buttons
        Button(
            onClick = onExecuteServerlessLocalDeploy,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen.copy(alpha = 0.24f),
                contentColor = NeonGreen
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                .testTag("btn_execute_local_only_deploy")
        ) {
            Text(
                text = "EXECUTE SERVERLESS LOCAL DEPLOY -> check:pass:true_only",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 8.8.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = onInjectEgressFault,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonAmber.copy(alpha = 0.20f),
                    contentColor = NeonAmber
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonAmber, RoundedCornerShape(6.dp))
                    .testTag("btn_inject_egress_fault")
            ) {
                Text(
                    text = "Test Egress Fault [false]",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onRemediateLocalOnly,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan.copy(alpha = 0.22f),
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_remediate_local_only")
            ) {
                Text(
                    text = "Auto-Remediate Local-Only",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InterconnectNodeChip(
    node: LocalInterconnectNode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = if (node.isStrictlyLocal) NeonGreen else NeonPink
    val borderCol = when {
        !node.isStrictlyLocal -> NeonPink
        isSelected -> NeonCyan
        else -> SlateBorder
    }

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) NeonCyan.copy(alpha = 0.16f)
                else ObsidianBg.copy(alpha = 0.85f)
            )
            .border(1.dp, borderCol, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("interconnect_node_${node.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = "${node.id} // ${node.code}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) NeonCyan else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.2.sp
                )
            }
            Text(
                text = node.localPath.takeLast(28),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 7.5.sp
            )
        }
        Text(
            text = if (node.isStrictlyLocal) "PASS" else "BLOCK",
            style = MaterialTheme.typography.labelSmall,
            color = statusColor,
            fontWeight = FontWeight.Bold,
            fontSize = 7.8.sp
        )
    }
}

@Composable
fun ServerlessLocalDeployCodexCard(
    state: AxiomUiState,
    onExecuteServerlessLocalDeploy: () -> Unit,
    onLoadManifestIntoVerifier: () -> Unit,
    onRemediateLocalOnly: () -> Unit,
    onCopyToast: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val deploy = state.localInterconnectState
    val clipboard = LocalClipboardManager.current
    val accent = if (deploy.checkPassTrueOnly) NeonGreen else NeonPink
    val manifestSnippet = LocalOnlyPathsInterconnectEngine.buildServerlessDeployManifestScript(deploy)

    HudCard(
        borderColor = accent,
        modifier = modifier.testTag("card_codex_local_only_deploy")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SERVERLESS INFRA ONLY DEPLOY // check:pass:true_only",
                    style = MaterialTheme.typography.titleSmall,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Local Only Paths Interconnect -> Local Only Way • Zero External Egress",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 8.5.sp
                )
            }
            StatusBadge(
                text = deploy.assertionToken,
                color = accent
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianBg)
                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                .clickable {
                    clipboard.setText(AnnotatedString(deploy.interconnectSealSha256))
                    onCopyToast("Copied Interconnect SHA-256 Seal")
                }
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "ASSERTION: ${deploy.assertionToken} | ONLY_LOCAL_LOGIC=${if (deploy.onlyLocalLogicFlag) 1 else 0} | EPOCH #${deploy.deployEpoch}",
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 8.2.sp
            )
            Text(
                text = "INTERCONNECT SHA-256 SEAL: ${deploy.interconnectSealSha256}",
                style = MaterialTheme.typography.bodyMedium,
                color = NeonGreen,
                fontSize = 8.5.sp
            )
            Text(
                text = manifestSnippet.lines().take(10).joinToString("\n"),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 8.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = onExecuteServerlessLocalDeploy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.22f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_codex_execute_local_deploy")
            ) {
                Text(
                    text = "Verify & Seal check:pass:true_only",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = if (deploy.checkPassTrueOnly) onLoadManifestIntoVerifier else onRemediateLocalOnly,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan.copy(alpha = 0.20f),
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_codex_load_interconnect_manifest")
            ) {
                Text(
                    text = if (deploy.checkPassTrueOnly) "Load Manifest in SHA-256" else "Remediate Local-Only",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

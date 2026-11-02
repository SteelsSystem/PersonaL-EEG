package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ScriptVerificationEntity
import com.example.data.SpectStatEntity
import com.example.model.BenchmarkReport
import com.example.model.CodexEntry
import com.example.model.RustCodexCatalog
import com.example.service.DeployTierMode
import com.example.shader.AxiomShaders
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CodexAndShaHudDeck(
    state: AxiomUiState,
    verifications: List<ScriptVerificationEntity>,
    onSelectCrateFilter: (String) -> Unit,
    onLoadEntryIntoVerifier: (CodexEntry) -> Unit,
    onLoadActiveShaderIntoVerifier: () -> Unit,
    onUpdateRawScript: (String) -> Unit,
    onUpdateExpectedSha: (String) -> Unit,
    onExecuteCleanAndVerify: () -> Unit,
    onOpenOriginalHtmlProfiler: () -> Unit,
    onParseForwardTunnel: (String, String) -> Unit = { _, _ -> },
    onResolveBlockMindMiscalculus: () -> Unit = {},
    onSelectMicaRegime: (MicaComplianceRegime) -> Unit = {},
    onUpdateMicaDWaveDelta: (Float) -> Unit = {},
    onExecuteMicaIfDWaveClampAndSeal: () -> Unit = {},
    onUpdateScreenShareSearchQuery: (String) -> Unit = {},
    onSelectScreenShareCategory: (BestWorkCategoryFilter) -> Unit = {},
    onSelectScreenShareSpotlightItem: (String) -> Unit = {},
    onLaunchBestWorkInViewport: (BestWorkShowcaseItem) -> Unit = {},
    onVerifyAndSealBestWork: (BestWorkShowcaseItem) -> Unit = {},
    onOpenFullScreenShareModal: () -> Unit = {},
    onExecuteServerlessLocalDeploy: () -> Unit = {},
    onLoadDeployManifestIntoVerifier: () -> Unit = {},
    onRemediateLocalOnly: () -> Unit = {},
    onSelectDeployTierMode: (DeployTierMode) -> Unit = {},
    onUpdateDecayLambda: (Float) -> Unit = {},
    onStepDecayEpoch: () -> Unit = {},
    onSelectOwnDevLogicNode: (String) -> Unit = {},
    onExecuteUnifiedDeployAndSealWhitepaper: () -> Unit = {},
    onCopyToast: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val filteredEntries = RustCodexCatalog.entries.filter { entry ->
        val matchesCrate = state.selectedCodexCrateId == "all" || entry.id == state.selectedCodexCrateId
        val matchesQuery = state.codexSearchQuery.isBlank() ||
            entry.crateName.contains(state.codexSearchQuery, ignoreCase = true) ||
            entry.summary.contains(state.codexSearchQuery, ignoreCase = true)
        matchesCrate && matchesQuery
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 0000. Unified Global vs Personal Deploy [PaidvsFREE] & Remembered Relay Whitepaper (MiCA -> [Personal account:DEV])
        GlobalVsPersonalDeployWhitepaperCard(
            state = state,
            onSelectDeployTierMode = onSelectDeployTierMode,
            onUpdateDecayLambda = onUpdateDecayLambda,
            onStepDecayEpoch = onStepDecayEpoch,
            onSelectOwnDevLogicNode = onSelectOwnDevLogicNode,
            onExecuteUnifiedDeployAndSealWhitepaper = onExecuteUnifiedDeployAndSealWhitepaper,
            onCopyToast = onCopyToast
        )

        // 000. Serverless Infra Only Deploy // check:pass:true_only
        ServerlessLocalDeployCodexCard(
            state = state,
            onExecuteServerlessLocalDeploy = onExecuteServerlessLocalDeploy,
            onLoadManifestIntoVerifier = onLoadDeployManifestIntoVerifier,
            onRemediateLocalOnly = onRemediateLocalOnly,
            onCopyToast = onCopyToast
        )

        // 00. As-If Screen Share // Search Best Work Spotlight Card
        AsIfScreenShareBestWorkCard(
            state = state,
            onUpdateSearchQuery = onUpdateScreenShareSearchQuery,
            onSelectCategory = onSelectScreenShareCategory,
            onSelectSpotlightItem = onSelectScreenShareSpotlightItem,
            onLaunchWorkInViewport = onLaunchBestWorkInViewport,
            onVerifyAndSealWork = onVerifyAndSealBestWork,
            onOpenFullScreenShareModal = onOpenFullScreenShareModal,
            onCopyToast = onCopyToast
        )

        // 0A-0. BLOCKMIND MISCALCULUS TO SEALED HAS:TRUE (if safe)
        HudCard(
            borderColor = if (state.blockMindSealedHasTrue) NeonGreen else NeonAmber,
            modifier = Modifier.testTag("card_blockmind_miscalculus_sealed_has_true")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "BLOCKMIND MISCALCULUS -> SEALED HAS:TRUE",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (state.blockMindSealedHasTrue) NeonGreen else NeonAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Safety Gate: if (safe) { miscalculus = 0.000; sealed.has = true; }",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = if (state.blockMindSealedHasTrue) "SEALED HAS:TRUE" else "MISCALCULUS WARN",
                    color = if (state.blockMindSealedHasTrue) NeonGreen else NeonAmber
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "SAFETY GATE",
                    value = if (state.blockMindSafetyVerified) "SAFE: TRUE" else "CHECK REQ",
                    valueColor = if (state.blockMindSafetyVerified) NeonGreen else NeonPink,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "MISCALCULUS Δ",
                    value = "%.4f".format(state.blockMindMiscalculusDelta),
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "SEALED HAS",
                    value = if (state.blockMindSealedHasTrue) "TRUE" else "FALSE",
                    valueColor = if (state.blockMindSealedHasTrue) NeonGreen else NeonAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onResolveBlockMindMiscalculus,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.22f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_resolve_blockmind_miscalculus")
            ) {
                Text(
                    text = "RESOLVE BLOCKMIND MISCALCULUS -> SEALED HAS:TRUE (IF SAFE)",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 0A-1. MiCA Future + Today Pre Compliance Architecture [SO IF D~]
        MicaComplianceArchitectureCard(
            state = state,
            onSelectMicaRegime = onSelectMicaRegime,
            onUpdateDWaveDelta = onUpdateMicaDWaveDelta,
            onExecuteIfDWaveClampAndSeal = onExecuteMicaIfDWaveClampAndSeal,
            onSealMicaContractInVerifier = { entry ->
                onLoadEntryIntoVerifier(entry)
                onExecuteCleanAndVerify()
            }
        )

        // 0A. SteelsSystem Engineering Evidence Registry & 'S:OpenSource(nosubs!-asis)Contract!'
        SteelsSystemEvidenceRegistryCard(
            onLoadContractOrDossierIntoVerifier = { entry ->
                onLoadEntryIntoVerifier(entry)
                onExecuteCleanAndVerify()
            }
        )

        // 0B. Forward Logic Tunnel Parser & PDF-05 Legal Axis Cross-Check
        val parse = state.forwardTunnelParse
        HudCard(borderColor = NeonCyan.copy(alpha = 0.7f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FORWARD LOGIC TUNNEL PARSER // PDF-05",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan
                    )
                    Text(
                        text = "Portfolio Relay v0.1 • 4 Legal Axes • ONLY_LOCAL_LOGIC=1",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = if (parse.bracketDepthBalanced) "OVERLOCK: UNBREACHABLE" else "GRAMMAR WARN",
                    color = if (parse.bracketDepthBalanced) NeonGreen else NeonAmber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Grammar: ${parse.rawGrammar}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonCyan,
                    fontSize = 9.sp
                )
                Text(
                    text = "Overlock: ${parse.overlockLogic}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonGreen,
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                parse.parsedTokens.forEach { token ->
                    Text(
                        text = "▸ $token",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ALL 4 COMPATIBLE APK INSTALLATION TARGETS CROSS-CHECKED",
                style = MaterialTheme.typography.labelSmall,
                color = NeonAmber,
                fontSize = 8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                parse.legalTargets.forEach { target ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianBg.copy(alpha = 0.8f))
                            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "[${target.id} . ${target.code}] (${target.modeNote})",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontSize = 8.sp
                            )
                            Text(
                                text = target.path,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 8.sp
                            )
                        }
                        StatusBadge(text = "CROSS-CHECKED", color = NeonGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onParseForwardTunnel(parse.rawGrammar, parse.overlockLogic) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan.copy(alpha = 0.22f),
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_transfer_forward_tunnel_parser")
            ) {
                Text(
                    text = "TRANSFER FORWARD LOGIC TUNNEL PARSER & SEAL SHA-256",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp
                )
            }
        }

        // 0B. The "Rosetta Onion" // 500-Year Zero-Dependency Self-Decoding Archive
        HudCard(borderColor = NeonGreen.copy(alpha = 0.65f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "THE ROSETTA ONION // 500-YR ARCHIVE",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonGreen
                    )
                    Text(
                        text = "Raw Binary • Prime Numbers • 2D Spatial Grid • Turing VM",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = "13x17 SEMIPRIME",
                    color = NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 2D Spatial Prime Grid Bitmap (13x13 visual slice of the semiprime beacon)
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianBg)
                        .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                ) {
                    val hashBytes = state.computedSha256.padEnd(64, '0')
                    for (r in 0 until 7) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            for (c in 0 until 11) {
                                val charIdx = (r * 11 + c) % hashBytes.length
                                val bitOn = (hashBytes[charIdx].code + r + c) % 2 == 0 ||
                                    (r == 0 || r == 6 || c == 0 || c == 10)
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(6.dp)
                                        .background(
                                            if (bitOn) NeonGreen else SlateBorder.copy(alpha = 0.35f)
                                        )
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "▸ Layer 0: Prime Beacon [2, 3, 5, 7, 11, 13, 17]",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontSize = 8.sp
                    )
                    Text(
                        text = "▸ Layer 1: 2D Spatial Grid Glyph Map (No OS / No zlib)",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        fontSize = 8.sp
                    )
                    Text(
                        text = "▸ Layer 2: Fundamental Turing State Machine Bootstrap",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonAmber,
                        fontSize = 8.sp
                    )
                    Text(
                        text = "▸ Layer 3: SY4-EEG [${state.rgbMode.id}] + Unicritic ${state.unicriticEvaluation.consensusScore}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple,
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val rosettaEntry = RustCodexCatalog.entries.firstOrNull { it.id == "rosetta-onion-500yr" }
                    if (rosettaEntry != null) {
                        onLoadEntryIntoVerifier(rosettaEntry)
                        onExecuteCleanAndVerify()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.22f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_seal_rosetta_onion")
            ) {
                Text(
                    text = "PACKAGE ROSETTA ONION 500-YR CAPSULE & SEAL SHA-256",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp
                )
            }
        }
        // 1. Čištění skriptu SHA-256 Verifikace (Script Cleaner & Verifier)
        HudCard(borderColor = NeonAmber.copy(alpha = 0.65f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ČIŠTĚNÍ SKRIPTU // SHA-256 VERIFIKACE",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonAmber
                    )
                    Text(
                        text = "Deterministic Script Sanitizer & Cryptographic Digest Verifier",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = if (state.sha256Verified) "SHA-256 VERIFIED" else "HASH MISMATCH",
                    color = if (state.sha256Verified) NeonGreen else NeonPink
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onLoadActiveShaderIntoVerifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.18f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .testTag("btn_load_glsl_into_verifier")
                ) {
                    Text(
                        text = "Load Active GLSL",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Button(
                    onClick = onOpenOriginalHtmlProfiler,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple.copy(alpha = 0.20f),
                        contentColor = NeonPurple
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonPurple.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .testTag("btn_open_html_asset_modal")
                ) {
                    Text(
                        text = "WebGL Asset View",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.rawScriptInput,
                onValueChange = onUpdateRawScript,
                label = {
                    Text(
                        text = "Script Source (${state.scriptTitleInput})",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeonCyan, fontSize = 10.sp),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = SlateBorder,
                    focusedContainerColor = ObsidianBg,
                    unfocusedContainerColor = ObsidianBg
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_raw_script")
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.expectedSha256Input,
                onValueChange = onUpdateExpectedSha,
                label = {
                    Text(
                        text = "Expected SHA-256 Digest (Leave blank for auto-seal)",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = NeonGreen, fontSize = 10.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = SlateBorder,
                    focusedContainerColor = ObsidianBg,
                    unfocusedContainerColor = ObsidianBg
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_expected_sha256")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Computed SHA-256 readout box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                    .clickable {
                        clipboard.setText(AnnotatedString(state.computedSha256))
                    }
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "COMPUTED SHA-256 (TAP TO COPY)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 8.sp
                    )
                    Text(
                        text = "Cleaned: ${state.cleanedScriptPreview.length} chars",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontSize = 8.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = state.computedSha256,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onExecuteCleanAndVerify,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.24f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_verify_and_seal_sha256")
            ) {
                Text(
                    text = "CLEAN SCRIPT & RECORD SHA-256 AUDIT SEAL",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        // 2. Rust / OpenAPI / SQLx / Secrecy / Basilisk Codex Browser
        HudCard(borderColor = HudBorderCyan) {
            Text(
                text = "RUST CRATES & CONTRACT CODEX",
                style = MaterialTheme.typography.titleSmall,
                color = NeonCyan
            )
            Text(
                text = "openapi • openapi-gen • sqlx::query! • sqlx-cli • secrecy • basilisk",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filterChips = listOf("all" to "ALL CRATES") +
                    RustCodexCatalog.entries.map { it.id to it.crateName }
                filterChips.forEach { (id, title) ->
                    val selected = state.selectedCodexCrateId == id
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
                            .clickable { onSelectCrateFilter(id) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("codex_filter_$id")
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) NeonCyan else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredEntries.forEach { entry ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.crateName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = NeonCyan
                                )
                                Text(
                                    text = "${entry.registrySource} • ${entry.osaMapping}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonAmber,
                                    fontSize = 8.sp
                                )
                            }
                            StatusBadge(
                                text = entry.categoryTag,
                                color = NeonPurple
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = entry.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = entry.codeSnippet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeonGreen,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { onLoadEntryIntoVerifier(entry) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonAmber.copy(alpha = 0.2f),
                                    contentColor = NeonAmber
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .border(1.dp, NeonAmber.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .testTag("btn_load_codex_${entry.id}")
                            ) {
                                Text(
                                    text = "Clean & Hash in SHA-256 Verifier",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Recent Local SHA-256 Script Verifications (from Room)
        if (verifications.isNotEmpty()) {
            HudCard(borderColor = SlateBorder) {
                Text(
                    text = "LOCAL SHA-256 AUDIT LOG (SQLITE ROOM)",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    verifications.take(5).forEach { v ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ObsidianBg)
                                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = v.scriptTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "SHA-256: ${v.sha256Digest.take(24)}...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeonCyan,
                                    fontSize = 9.sp
                                )
                            }
                            StatusBadge(
                                text = if (v.isVerified) "SEALED" else "WARN",
                                color = if (v.isVerified) NeonGreen else NeonPink
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpectLocalStatsHudDeck(
    state: AxiomUiState,
    spectStats: List<SpectStatEntity>,
    spectRoomRecords: List<com.example.data.SpectRoomEntity> = emptyList(),
    driftLogs: List<com.example.data.UnicriticDriftLogEntity> = emptyList(),
    onToggleInvertedMode: () -> Unit,
    onCaptureSnapshot: () -> Unit,
    onDeleteRecord: (Int) -> Unit,
    onDeleteDriftLog: (Int) -> Unit = {},
    onClearAllRecords: () -> Unit,
    onSelectSensorPathFilter: (String) -> Unit = {},
    onToggleSensorStream: () -> Unit = {},
    onCalibrateSensorPaths: () -> Unit = {},
    onSealSensorPathToRoom: () -> Unit = {},
    onDeleteSpectRoomRecord: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    val liveInvFps = 1000f / state.currentFrameMs.coerceAtLeast(1f)
    val liveInvStability = ((1f - state.perturbation) * 100f).coerceIn(0f, 100f)
    val liveFocusSignal = (state.eegBands.alpha + state.eegBands.beta) * 50f
    val liveSpectRatio = (liveFocusSignal / state.cognitiveLoadPct.coerceAtLeast(10)).coerceIn(0.1f, 9.9f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 0. Real-Time SPECT Room Sensor Paths Telemetry Dashboard
        SpectRoomSensorPathsDashboard(
            state = state,
            spectRoomRecords = spectRoomRecords,
            onSelectSensorPathFilter = onSelectSensorPathFilter,
            onToggleSensorStream = onToggleSensorStream,
            onCalibrateAndZeroDriftPaths = onCalibrateSensorPaths,
            onSealSensorPathSnapshotToRoom = onSealSensorPathToRoom,
            onDeleteSpectRoomRecord = onDeleteSpectRoomRecord
        )

        // 1. Live Inverted Dev Stats: SPECT Header Card
        HudCard(borderColor = NeonPurple.copy(alpha = 0.65f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ONLY LOCAL INVERT DEV STATS // SPECT",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonPurple
                    )
                    Text(
                        text = "Strictly Device-Local Room SQLite Telemetry & Spectral Inversion",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                Button(
                    onClick = onToggleInvertedMode,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple.copy(alpha = 0.22f),
                        contentColor = NeonPurple
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                        .testTag("btn_toggle_spect_invert")
                ) {
                    Text(
                        text = if (state.spectInvertedMode) "MODE: INVERTED" else "MODE: RAW",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = if (state.spectInvertedMode) "INV LATENCY (1/s)" else "RAW FRAME MS",
                    value = if (state.spectInvertedMode) "${"%.1f".format(liveInvFps)} Hz" else "${"%.1f".format(state.currentFrameMs)} ms",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = if (state.spectInvertedMode) "INV ENTROPY" else "RAW ENTROPY",
                    value = if (state.spectInvertedMode) "${"%.1f".format(liveInvStability)}%" else "%.2f".format(state.perturbation),
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "SPECT RATIO",
                    value = "${"%.2f".format(liveSpectRatio)}x",
                    valueColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCaptureSnapshot,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan.copy(alpha = 0.22f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .testTag("btn_capture_spect_snapshot")
                ) {
                    Text(
                        text = "+ Log Inverted SPECT Snapshot",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Button(
                    onClick = onClearAllRecords,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ObsidianBg,
                        contentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                        .testTag("btn_reset_spect_db")
                ) {
                    Text(
                        text = "Reset DB",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // 1B. Persisted Unicritic Drift Logs & RULE: IF FAILLOAD POSSIBLE Audit Trail (SpectDao)
        HudCard(borderColor = NeonGreen.copy(alpha = 0.75f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UNICRITIC DRIFT & FAILLOAD GUARD VAULT (${driftLogs.size})",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonGreen
                    )
                    Text(
                        text = "SpectRoomTelemetryDao • Biometric Verdicts + RULE: IF FAILLOAD POSSIBLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 8.5.sp
                    )
                }
                StatusBadge(
                    text = "FAILLOAD SEALS: ${state.failloadInterceptCount}",
                    color = if (state.failloadGuardEnabled) NeonGreen else NeonAmber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                driftLogs.take(8).forEach { log ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianBg.copy(alpha = 0.85f))
                            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${log.triggerSource} // ${log.verdictLevel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadge(
                                text = if (!log.redlineActive) "SAFE SEALED" else "REDLINE DRIFT",
                                color = if (!log.redlineActive) NeonGreen else NeonPink
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "✕",
                                color = TextMuted,
                                modifier = Modifier
                                    .clickable { onDeleteDriftLog(log.id) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = log.remediationNote,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "${dateFormat.format(Date(log.timestamp))} • Drift ${"%.2f".format(log.driftIntensity)} • BioLoad ${log.biometricLoadPct}% • Coherence ${log.phaseCoherencePct}% • ${log.rgbBiometricMode}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        // 2. Persisted Room SQLite SPECT Records
        HudCard(borderColor = HudBorderCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERSISTED SPECT TELEMETRY VAULT (${spectStats.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan
                )
                Text(
                    text = "sy4_eeg_spect_local.db",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontSize = 8.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                spectStats.forEach { item ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.sessionTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${dateFormat.format(Date(item.timestamp))} • ${item.activeShader} • ${item.spectralDominance}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 8.sp
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(
                                    text = "SHA-256: ${item.sha256IntegrityStatus}",
                                    color = if (item.sha256Verified) NeonGreen else NeonPink
                                )
                                StatusBadge(text = item.contractPhase, color = NeonCyan)
                                Text(
                                    text = "✕",
                                    color = TextMuted,
                                    modifier = Modifier
                                        .clickable { onDeleteRecord(item.id) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // SPECTRoom Biometric Drift Log & SHA-256 Digest Strip
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "DRIFT LOG: ${item.biometricDriftLog}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (item.biometricDriftIntensity >= 0.25f) NeonPink else NeonGreen,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "SHA-256 SEAL: ${item.sha256Digest.take(28)}...",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontSize = 8.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MiniStatBox(
                                title = if (state.spectInvertedMode) "INV FPS" else "RAW MS",
                                value = if (state.spectInvertedMode) {
                                    "${"%.1f".format(item.invertedFrameRate)} Hz"
                                } else {
                                    "${"%.1f".format(item.rawFrameMs)} ms"
                                },
                                valueColor = NeonCyan,
                                modifier = Modifier.weight(1f)
                            )
                            MiniStatBox(
                                title = if (state.spectInvertedMode) "STABILITY" else "ENTROPY",
                                value = if (state.spectInvertedMode) {
                                    "${"%.0f".format(item.invertedStabilityIndex)}%"
                                } else {
                                    "%.2f".format(item.rawEntropy)
                                },
                                valueColor = NeonGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MiniStatBox(
                                title = "COG-LOAD",
                                value = "${item.cognitiveLoadPct}%",
                                valueColor = NeonAmber,
                                modifier = Modifier.weight(1f)
                            )
                            MiniStatBox(
                                title = "SPECT",
                                value = "${"%.2f".format(item.spectCognitiveRatio)}x",
                                valueColor = NeonPurple,
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
fun GlslSourceCodeModal(
    state: AxiomUiState,
    onDismiss: () -> Unit,
    onCopyToast: (String) -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val shaderSource = AxiomShaders.getSourceForPreset(state.activeShader).trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .height(540.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(HudPanelBg)
                .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GLSL FRAGMENT SHADER // LIVE SOURCE",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonCyan
                    )
                    Text(
                        text = "${state.activeShader.title} + RGB Biometric Tint + Self-Heal",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(8.dp)
                        .testTag("btn_close_glsl_modal")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = shaderSource,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonCyan,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(shaderSource))
                        onCopyToast("GLSL Shader Source Copied to Clipboard!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("btn_copy_glsl_source")
                ) {
                    Text(
                        text = "COPY GLSL SHADER",
                        style = MaterialTheme.typography.titleSmall,
                        color = ObsidianBg
                    )
                }
            }
        }
    }
}

@Composable
fun BenchmarkReportModal(
    report: BenchmarkReport,
    onDismiss: () -> Unit,
    onCopyToast: (String) -> Unit
) {
    val clipboard = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .background(HudPanelBg)
                .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BENCHMARK & SELF-HEAL REPORT",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniStatBox(
                    title = "MEDIAN (P50)",
                    value = "${"%.1f".format(report.p50Ms)} ms",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "95TH PCTL",
                    value = "${"%.1f".format(report.p95Ms)} ms",
                    valueColor = NeonAmber,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "99TH PCTL",
                    value = "${"%.1f".format(report.p99Ms)} ms",
                    valueColor = NeonPink,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                ReportRow("Architecture:", report.architecture, NeonCyan)
                ReportRow("Total Frames Sampled:", "${report.totalFrames}", Color.White)
                ReportRow(
                    "Dropped Frames (>33.3ms):",
                    "${report.droppedFrames} (${"%.1f".format(report.droppedPercent)}%)",
                    NeonAmber
                )
                ReportRow(
                    "Self-Heal Lattice Recovery:",
                    "${report.integrityRestoredPct}% Restored",
                    NeonGreen
                )
                ReportRow("Average Framerate:", "${"%.1f".format(report.avgFps)} FPS", NeonGreen)
                ReportRow("Architecture Compute Score:", "${report.computeScore} PTS", NeonCyan)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                Button(
                    onClick = {
                        val summary = "[SY4-EEG // AXIOM REPORT] Arch=${report.architecture} " +
                            "AvgFPS=${"%.1f".format(report.avgFps)} P50=${"%.1f".format(report.p50Ms)}ms " +
                            "P95=${"%.1f".format(report.p95Ms)}ms Score=${report.computeScore}PTS"
                        clipboard.setText(AnnotatedString(summary))
                        onCopyToast("Diagnostic Report Copied to Clipboard!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ObsidianBg,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                ) {
                    Text("Copy Report", style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("CONFIRM", style = MaterialTheme.typography.titleSmall, color = ObsidianBg)
                }
            }
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EmbeddedHtmlProfilerModal(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(0.95f)
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianBg)
                .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HudPanelBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ATTACHED ASSET // WEBGL GLSL PROFILER",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan
                )
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPink.copy(alpha = 0.25f),
                        contentColor = NeonPink
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Close Asset View", style = MaterialTheme.typography.labelSmall)
                }
            }
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = WebViewClient()
                        loadUrl("file:///android_asset/webgl_stress_tester_glsl_profiler_time_c9677a6f.html")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

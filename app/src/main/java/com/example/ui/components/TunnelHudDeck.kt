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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.example.model.LogSeverity
import com.example.model.ShaderPreset
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

@Composable
fun TunnelHudDeck(
    state: AxiomUiState,
    onSelectPreset: (ShaderPreset) -> Unit,
    onInjectFracture: () -> Unit,
    onRepackageHeal: () -> Unit,
    onUpdateHealRate: (Float) -> Unit,
    onUpdateAutoGovernor: (Boolean) -> Unit,
    onUpdateHexScale: (Float) -> Unit,
    onUpdateReflexDensity: (Float) -> Unit,
    onUpdateMagPressure: (Float) -> Unit,
    onUpdateAntiMagPulse: (Float) -> Unit,
    onUpdateMaxSteps: (Int) -> Unit,
    onUpdateRenderScale: (Float) -> Unit,
    onUpdateFxaa: (Boolean) -> Unit,
    onTogglePause: () -> Unit,
    onRunBenchmark: () -> Unit,
    onUpdateCoreTension: (Float) -> Unit = {},
    onCompleteCoreTensionPass: () -> Unit = {},
    onSelectFilterMode: (ScreenFilterAntMode) -> Unit = {},
    onUpdateFilterIntensity: (Float) -> Unit = {},
    onUpdateSwarmDensity: (Int) -> Unit = {},
    onToggleProjectAiImage: (Boolean) -> Unit = {},
    onUpdateAiOverlayAlpha: (Float) -> Unit = {},
    onUpdateAiPrompt: (String) -> Unit = {},
    onGenerateAiImage: () -> Unit = {},
    onEditAiImage: () -> Unit = {},
    onSeedProceduralBaseImage: () -> Unit = {},
    onSelectUserBitmapForEdit: (android.graphics.Bitmap) -> Unit = {},
    onOpenFullScreenStudioModal: () -> Unit = {},
    onUpdateScreenShareSearchQuery: (String) -> Unit = {},
    onSelectScreenShareCategory: (BestWorkCategoryFilter) -> Unit = {},
    onSelectScreenShareSpotlightItem: (String) -> Unit = {},
    onLaunchBestWorkInViewport: (BestWorkShowcaseItem) -> Unit = {},
    onVerifyAndSealBestWork: (BestWorkShowcaseItem) -> Unit = {},
    onOpenFullScreenShareModal: () -> Unit = {},
    onCopyToast: (String) -> Unit = {},
    onTriggerGlslFalseDenySanctuary: () -> Unit = {},
    onRestoreGlslTrueState: () -> Unit = {},
    onToggleFailloadGuard: (Boolean) -> Unit = {},
    onSimulateFailloadAndIntercept: () -> Unit = {},
    onForceFailloadSafeFallback: () -> Unit = {},
    onToggleContinuityInterlock: (Boolean) -> Unit = {},
    onSimulateContinuityAutoRemediate: () -> Unit = {},
    onUpdateRotatingFrequencyHz: (Float) -> Unit = {},
    onToggleSyncRotatingFrqWithEeg: () -> Unit = {},
    onToggleRotationDirection: () -> Unit = {},
    onToggleWidescreenStage: () -> Unit = {},
    onToggleWidescreenLandscapeLock: () -> Unit = {},
    onSelectInterconnectNode: (String) -> Unit = {},
    onExecuteServerlessLocalDeploy: () -> Unit = {},
    onInjectEgressFault: () -> Unit = {},
    onRemediateLocalOnly: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 000-L. LOCAL ONLY PATHS INTERCONNECT // SERVERLESS INFRA DEPLOY (check:pass:true_only)
        LocalOnlyPathsInterconnectCard(
            state = state,
            onSelectInterconnectNode = onSelectInterconnectNode,
            onExecuteServerlessLocalDeploy = onExecuteServerlessLocalDeploy,
            onInjectEgressFault = onInjectEgressFault,
            onRemediateLocalOnly = onRemediateLocalOnly,
            onCopyToast = onCopyToast
        )

        // 000-W. WIDESCREEN VIEWPORT & ROTATING FREQUENCY (ROTATING FRQ) TUNER
        HudCard(
            borderColor = NeonCyan,
            modifier = Modifier.testTag("card_widescreen_rotating_frq")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "WIDE SCREEN & ROTATING FRQ // VORTEX LOCK",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Phase-Integrated 3D Tunnel Rotation (${"%.2f".format(state.effectiveRotatingFrequencyHz)} Hz) • 16:9 Wide Stage",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 8.5.sp
                    )
                }
                StatusBadge(
                    text = if (state.syncRotatingFrqWithEeg) "EEG-FRQ LOCKED" else "MANUAL FRQ",
                    color = if (state.syncRotatingFrqWithEeg) NeonGreen else NeonAmber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "ROTATING FRQ",
                    value = "${"%.2f".format(state.effectiveRotatingFrequencyHz)} Hz",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "VORTEX RPM",
                    value = "${((state.effectiveRotatingFrequencyHz * 60f).toInt())} RPM",
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "DIRECTION",
                    value = if (state.rotationDirectionCw) "CW [↻]" else "CCW [↺]",
                    valueColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "VIEWPORT",
                    value = if (state.widescreenLandscapeLocked) "WIDE 16:9" else "ADAPTIVE",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LabeledSliderRow(
                label = "3D Tunnel Rotating Frequency (Hz)",
                valueText = "${"%.2f".format(state.rotatingFrequencyHz)} Hz",
                valueColor = NeonCyan,
                value = state.rotatingFrequencyHz,
                valueRange = 0.05f..5.00f,
                onValueChange = onUpdateRotatingFrequencyHz,
                testTag = "slider_rotating_frequency_hz"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "0.25 Hz" to 0.25f,
                    "0.65 Hz" to 0.65f,
                    "1.25 Hz" to 1.25f,
                    "2.40 Hz" to 2.40f
                ).forEach { (label, hz) ->
                    val active = kotlin.math.abs(state.rotatingFrequencyHz - hz) < 0.05f
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (active) NeonCyan.copy(alpha = 0.24f) else ObsidianBg)
                            .border(1.dp, if (active) NeonCyan else SlateBorder, RoundedCornerShape(6.dp))
                            .clickable { onUpdateRotatingFrequencyHz(hz) }
                            .padding(vertical = 6.dp)
                            .testTag("btn_preset_rot_frq_${hz}")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (active) NeonCyan else TextSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onToggleWidescreenLandscapeLock,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.widescreenLandscapeLocked) NeonGreen.copy(alpha = 0.24f) else NeonCyan.copy(alpha = 0.20f),
                        contentColor = if (state.widescreenLandscapeLocked) NeonGreen else NeonCyan
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            if (state.widescreenLandscapeLocked) NeonGreen else NeonCyan,
                            RoundedCornerShape(6.dp)
                        )
                        .testTag("btn_widescreen_landscape_lock")
                ) {
                    Text(
                        text = if (state.widescreenLandscapeLocked) "✓ WIDE SCREEN LOCKED" else "⇄ LOCK WIDE SCREEN",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onToggleSyncRotatingFrqWithEeg,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.syncRotatingFrqWithEeg) NeonGreen.copy(alpha = 0.24f) else NeonAmber.copy(alpha = 0.20f),
                        contentColor = if (state.syncRotatingFrqWithEeg) NeonGreen else NeonAmber
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            if (state.syncRotatingFrqWithEeg) NeonGreen else NeonAmber,
                            RoundedCornerShape(6.dp)
                        )
                        .testTag("btn_toggle_eeg_rot_frq_sync")
                ) {
                    Text(
                        text = if (state.syncRotatingFrqWithEeg) "EEG-FRQ SYNC: ON" else "EEG-FRQ SYNC: OFF",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onToggleRotationDirection,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple.copy(alpha = 0.22f),
                        contentColor = NeonPurple
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                        .testTag("btn_toggle_rot_frq_direction")
                ) {
                    Text(
                        text = if (state.rotationDirectionCw) "CW ↻" else "CCW ↺",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 000-0. TRI-AXIS CONTINUITY INTERLOCK // LOCK GLSL GATE + EEG STATE + SHA-256 CHAIN & AUTO-REMEDIATE UNIFORMS
        HudCard(
            borderColor = if (state.continuityInterlockEnabled) NeonGreen else NeonAmber,
            modifier = Modifier.testTag("card_continuity_interlock")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TRI-AXIS CONTINUITY INTERLOCK // CHAIN #${state.continuityChainEpoch}",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (state.continuityInterlockEnabled) NeonGreen else NeonAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Locks GLSL Gate, EEG State & SHA-256 Chain • Auto-Remediates OOB Uniforms",
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
                            if (state.continuityInterlockEnabled) NeonGreen.copy(alpha = 0.22f)
                            else NeonAmber.copy(alpha = 0.22f)
                        )
                        .border(
                            1.dp,
                            if (state.continuityInterlockEnabled) NeonGreen else NeonAmber,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onToggleContinuityInterlock(!state.continuityInterlockEnabled) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("switch_continuity_interlock")
                ) {
                    Text(
                        text = if (state.continuityInterlockEnabled) "CONTINUITY: LOCKED" else "INTERLOCK: PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.continuityInterlockEnabled) NeonGreen else NeonAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "GLSL GATE",
                    value = if (state.glslGateTrue) "LOCKED" else "AURORA",
                    valueColor = if (state.glslGateTrue) NeonGreen else NeonPurple,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "EEG COHERENCE",
                    value = "${state.phaseCoherencePct}%",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "SHA-256 CHAIN",
                    value = "EPOCH #${state.continuityChainEpoch}",
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "AUTO-CLAMPED",
                    value = "${state.autoRemediatedUniformCount} OOB",
                    valueColor = NeonAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "CHAIN LINK: ${state.continuityParentSha256.take(14)}... ➔ ${state.continuityCurrentSha256.take(18)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 8.sp
                )
                Text(
                    text = state.lastContinuityEventSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonGreen,
                    fontSize = 8.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSimulateContinuityAutoRemediate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan.copy(alpha = 0.22f),
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_simulate_continuity_auto_remediate")
            ) {
                Text(
                    text = "SIMULATE OOB UNIFORM EXCURSION -> AUTO-REMEDIATE & CHAIN SHA-256",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp
                )
            }
        }

        // 000-A. RULE: IF FAILLOAD POSSIBLE -> AUTO-FALLBACK TO SAFE LOCAL STATE & LOG TO ROOM
        HudCard(
            borderColor = when {
                state.failloadRiskPct >= 75 -> NeonPink
                state.failloadGuardEnabled -> NeonGreen
                else -> NeonAmber
            },
            modifier = Modifier.testTag("card_rule_if_failload_possible")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RULE: IF FAILLOAD POSSIBLE // UNIFIED GUARD",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (state.failloadGuardEnabled) NeonGreen else NeonAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "GPU Raymarch • Biometric/Unicritic Drift • AI/Asset Load Safe Fallback & Room Audit",
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
                            if (state.failloadGuardEnabled) NeonGreen.copy(alpha = 0.20f)
                            else NeonAmber.copy(alpha = 0.20f)
                        )
                        .border(
                            1.dp,
                            if (state.failloadGuardEnabled) NeonGreen else NeonAmber,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onToggleFailloadGuard(!state.failloadGuardEnabled) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_toggle_failload_guard")
                ) {
                    Text(
                        text = if (state.failloadGuardEnabled) "GUARD: ARMED" else "GUARD: PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.failloadGuardEnabled) NeonGreen else NeonAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "GPU FAILLOAD",
                    value = "${state.failloadGpuRiskPct}%",
                    valueColor = if (state.failloadGpuRiskPct >= 70) NeonPink else NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "BIO FAILLOAD",
                    value = "${state.failloadBiometricRiskPct}%",
                    valueColor = if (state.failloadBiometricRiskPct >= 70) NeonPink else NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "AI/ASSET RISK",
                    value = "${state.failloadAiRiskPct}%",
                    valueColor = if (state.failloadAiRiskPct >= 70) NeonAmber else NeonPurple,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "ROOM SEALS",
                    value = "#${state.failloadInterceptCount}",
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg)
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "RULE LOGIC: if (failload_possible) { clamp_gpu_64(); restore_eeg_coherence(); synth_local_ant_bitmap(); spect_dao.seal(); }",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 8.sp
                )
                Text(
                    text = "LAST ACTION: ${state.failloadLastInterceptReason}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.failloadFallbackActive) NeonGreen else TextSecondary,
                    fontSize = 8.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onSimulateFailloadAndIntercept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonAmber.copy(alpha = 0.22f),
                        contentColor = NeonAmber
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonAmber, RoundedCornerShape(6.dp))
                        .testTag("btn_simulate_failload_intercept")
                ) {
                    Text(
                        text = "Simulate Failload & Auto-Intercept",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onForceFailloadSafeFallback,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen.copy(alpha = 0.22f),
                        contentColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                        .testTag("btn_force_failload_safe_fallback")
                ) {
                    Text(
                        text = "Force Safe Fallback & Room Seal",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 000. AS TO GLSL: TRUE // IF FALSE -> DENY (Syntax & Uniform Bounds Gate + Celestial Sanctuary & Meta-Hygiene Sleep Core)
        HudCard(
            borderColor = if (state.glslGateTrue) NeonCyan else NeonPurple,
            modifier = Modifier.testTag("card_as_to_glsl_true_deny_gate")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AS TO GLSL: TRUE // IF FALSE -> DENY",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (state.glslGateTrue) NeonCyan else NeonPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.glslGateDiagnostic,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = if (state.glslGateTrue) "GLSL: TRUE [ALLOW]" else "FALSE: DENY [AURORA]",
                    color = if (state.glslGateTrue) NeonGreen else NeonPurple
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(
                    text = "SYNTAX: ${if (state.glslSyntaxValid) "VALID" else "FAIL"}",
                    color = if (state.glslSyntaxValid) NeonGreen else NeonAmber
                )
                StatusBadge(
                    text = "META-HYGIENE: THOROUGH [DEV SEALED]",
                    color = NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTriggerGlslFalseDenySanctuary,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple.copy(alpha = 0.24f),
                        contentColor = NeonPurple
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                        .testTag("btn_glsl_false_deny_sanctuary")
                ) {
                    Text(
                        text = "IF FALSE -> DENY (SLEEP SANCTUARY)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp
                    )
                }

                Button(
                    onClick = onRestoreGlslTrueState,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen.copy(alpha = 0.24f),
                        contentColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                        .testTag("btn_glsl_restore_true")
                ) {
                    Text(
                        text = "VERIFY GLSL: TRUE [DEV]",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp
                    )
                }
            }
        }

        // 00. AS-IF SCREEN SHARE // SEARCH BEST WORK (Lou out in as if an screen share to search best work)
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

        // 0. SCREEN FILTER ANT // Local ARCH Image Studio
        ScreenFilterAntStudioCard(
            state = state,
            onSelectFilterMode = onSelectFilterMode,
            onUpdateFilterIntensity = onUpdateFilterIntensity,
            onUpdateSwarmDensity = onUpdateSwarmDensity,
            onToggleProjectAiImage = onToggleProjectAiImage,
            onUpdateAiOverlayAlpha = onUpdateAiOverlayAlpha,
            onUpdateAiPrompt = onUpdateAiPrompt,
            onGenerateAiImage = onGenerateAiImage,
            onEditAiImage = onEditAiImage,
            onSeedProceduralBaseImage = onSeedProceduralBaseImage,
            onSelectUserBitmapForEdit = onSelectUserBitmapForEdit,
            onOpenFullScreenStudioModal = onOpenFullScreenStudioModal
        )

        // Core Tension String Pass Tuner Banner
        HudCard(borderColor = if (state.coreTensionPassDone) NeonGreen.copy(alpha = 0.65f) else NeonAmber) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CORE TENSION STRING PASS",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (state.coreTensionPassDone) NeonGreen else NeonAmber
                    )
                    Text(
                        text = "Harmonic Tunnel String Resonance & Overlock Seal",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
                StatusBadge(
                    text = if (state.coreTensionPassDone) "PASS: DONE [LOCKED]" else "TUNING ACTIVE",
                    color = if (state.coreTensionPassDone) NeonGreen else NeonAmber
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LabeledSliderRow(
                label = "Core String Harmonic Tension",
                valueText = "${"%.2f".format(state.coreTensionString)} N",
                valueColor = if (state.coreTensionPassDone) NeonGreen else NeonAmber,
                value = state.coreTensionString,
                valueRange = 0.25f..2.50f,
                onValueChange = onUpdateCoreTension,
                testTag = "slider_core_tension_string"
            )
            Button(
                onClick = onCompleteCoreTensionPass,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.24f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_lock_core_tension_pass")
            ) {
                Text(
                    text = if (state.coreTensionPassDone) {
                        "✓ CORE TENSION STRING PASS IS DONE (OVERLOCK SEALED)"
                    } else {
                        "TUNE CORE TENSION STRING PASS & MARK DONE"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp
                )
            }
        }
        // 1. Architecture Mode Preset Switcher
        HudCard(borderColor = HudBorderCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SIMULATION ARCHITECTURE",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan
                )
                Text(
                    text = state.activeShader.complexityTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ShaderPreset.entries.forEach { preset ->
                    val selected = state.activeShader == preset
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
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
                            .clickable { onSelectPreset(preset) }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("preset_btn_${preset.key}")
                    ) {
                        Text(
                            text = preset.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) NeonCyan else TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = state.activeShader.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 10.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg.copy(alpha = 0.6f))
                    .padding(8.dp)
            )
        }

        // 2. Repackage & Self-Heal Matrix
        HudCard(borderColor = NeonGreen.copy(alpha = 0.45f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (state.perturbation > 0.02f) NeonAmber else NeonGreen)
                    )
                    Text(
                        text = "REPACKAGE & SELF-HEAL MATRIX",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonGreen
                    )
                }
                Text(
                    text = "Entropy: ${"%.2f".format(state.perturbation)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.perturbation > 0.2f) NeonAmber else NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onInjectFracture,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonAmber.copy(alpha = 0.22f),
                        contentColor = NeonAmber
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonAmber.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                        .testTag("btn_inject_fracture")
                ) {
                    Text(
                        text = "⚡ Inject Fracture",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Button(
                    onClick = onRepackageHeal,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen.copy(alpha = 0.22f),
                        contentColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonGreen.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .testTag("btn_repackage_heal")
                ) {
                    Text(
                        text = "⟳ Repackage & Heal",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LabeledSliderRow(
                label = "Autonomous Self-Heal Rate",
                valueText = "${"%.2f".format(state.healRate)} /s",
                valueColor = NeonGreen,
                value = state.healRate,
                valueRange = 0.05f..2.5f,
                onValueChange = onUpdateHealRate,
                testTag = "slider_heal_rate"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onUpdateAutoGovernor(!state.autoGovernor) }
                ) {
                    Checkbox(
                        checked = state.autoGovernor,
                        onCheckedChange = onUpdateAutoGovernor,
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonGreen,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("check_auto_governor")
                    )
                    Text(
                        text = "Engine Auto-Repair Governor (FPS Guard)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
                StatusBadge(
                    text = if (state.autoGovernor) "ACTIVE" else "MANUAL",
                    color = if (state.autoGovernor) NeonGreen else TextSecondary
                )
            }
        }

        // 3. Dual Telemetry & Lattice Stability Graph + Benchmark
        HudCard(borderColor = HudBorderCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TELEMETRY & SELF-HEAL STREAM",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = "Cyan: ms | Green: Integrity | Pink: EEG",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontSize = 8.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg.copy(alpha = 0.85f))
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val maxMsScale = 42.0f

                    val y60 = h - (16.66f / maxMsScale) * h
                    val y30 = h - (33.33f / maxMsScale) * h

                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(0f, y60),
                        end = Offset(w, y60),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(0f, y30),
                        end = Offset(w, y30),
                        strokeWidth = 1.dp.toPx()
                    )

                    val count = state.frameTimeHistory.size.coerceAtLeast(2)
                    val stepX = w / (count - 1).toFloat()

                    // Trace 1: Frametime ms (Cyan / Pink if spike)
                    val msPath = Path()
                    state.frameTimeHistory.forEachIndexed { i, ms ->
                        val x = i * stepX
                        val y = h - (ms / maxMsScale).coerceIn(0f, 1f) * h
                        if (i == 0) msPath.moveTo(x, y) else msPath.lineTo(x, y)
                    }
                    drawPath(
                        path = msPath,
                        color = if (state.currentFrameMs > 33.3f) NeonPink else NeonCyan,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Trace 2: Lattice Integrity % (Emerald Green)
                    val intPath = Path()
                    state.integrityHistory.forEachIndexed { i, pct ->
                        val x = i * stepX
                        val y = h - (pct / 105f).coerceIn(0f, 1f) * h
                        if (i == 0) intPath.moveTo(x, y) else intPath.lineTo(x, y)
                    }
                    drawPath(
                        path = intPath,
                        color = NeonGreen,
                        style = Stroke(width = 1.8.dp.toPx())
                    )

                    // Trace 3: EEG Composite Wave (Magenta/Purple)
                    val eegPath = Path()
                    state.eegWaveHistory.forEachIndexed { i, wave ->
                        val x = i * stepX
                        val y = h - (wave / 100f).coerceIn(0f, 1f) * h
                        if (i == 0) eegPath.moveTo(x, y) else eegPath.lineTo(x, y)
                    }
                    drawPath(
                        path = eegPath,
                        color = NeonPurple.copy(alpha = 0.7f),
                        style = Stroke(width = 1.3.dp.toPx())
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "33.3ms (30 FPS) / 16.6ms (60 FPS)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 8.sp
                    )
                    Text(
                        text = "Lattice Integrity % & SY4-EEG Waveform",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen.copy(alpha = 0.85f),
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatBox(
                    title = "FPS LOW / HIGH",
                    value = "${state.minFps} / ${state.maxFps}",
                    valueColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "COMPUTE LOAD",
                    value = "${"%.1f".format(state.gigaOpsLoad)} G-Ops/s",
                    valueColor = NeonAmber,
                    modifier = Modifier.weight(1f)
                )
                MiniStatBox(
                    title = "HEAL CYCLES",
                    value = "${state.healCycles}",
                    valueColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRunBenchmark,
                enabled = !state.benchmarkActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPink.copy(alpha = 0.25f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonPink, RoundedCornerShape(6.dp))
                    .testTag("btn_run_benchmark")
            ) {
                Text(
                    text = if (state.benchmarkActive) {
                        "BENCHMARKING... ${"%.1f".format(state.benchmarkElapsedSec)}s / 10.0s"
                    } else {
                        "RUN 10S BENCHMARK & SELF-HEAL TEST"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }

            if (state.benchmarkActive) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { state.benchmarkProgress },
                    color = NeonPink,
                    trackColor = ObsidianBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }

        // 4. Field & Physics Parameters
        HudCard(borderColor = HudBorderCyan) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FIELD & PHYSICS PARAMETERS",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan
                )
                Text(
                    text = "${"%.2f".format(state.renderScale)}x Scale",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LabeledSliderRow(
                label = "Hexagonal Lattice Scale",
                valueText = "%.2f".format(state.hexScale),
                valueColor = NeonCyan,
                value = state.hexScale,
                valueRange = 0.6f..4.0f,
                onValueChange = onUpdateHexScale,
                testTag = "slider_hex_scale"
            )

            LabeledSliderRow(
                label = "Reflex Radial Light Density",
                valueText = "%.2f".format(state.reflexDensity),
                valueColor = NeonAmber,
                value = state.reflexDensity,
                valueRange = 0.1f..3.5f,
                onValueChange = onUpdateReflexDensity,
                testTag = "slider_reflex_density"
            )

            LabeledSliderRow(
                label = "Magnetic Compressive Pressure",
                valueText = "%.2f".format(state.magPressure),
                valueColor = NeonPurple,
                value = state.magPressure,
                valueRange = 0.0f..3.0f,
                onValueChange = onUpdateMagPressure,
                testTag = "slider_mag_pressure"
            )

            LabeledSliderRow(
                label = "Anti-Magnet Blocking Boundary",
                valueText = "%.2f".format(state.antiMagPulse),
                valueColor = NeonPink,
                value = state.antiMagPulse,
                valueRange = 0.0f..2.5f,
                onValueChange = onUpdateAntiMagPulse,
                testTag = "slider_antimag_pulse"
            )

            LabeledSliderRow(
                label = "Raymarch Depth / Iterations",
                valueText = "${state.maxSteps}",
                valueColor = NeonCyan,
                value = state.maxSteps.toFloat(),
                valueRange = 24f..256f,
                onValueChange = { onUpdateMaxSteps(it.toInt()) },
                testTag = "slider_max_steps"
            )

            LabeledSliderRow(
                label = "Internal Render Resolution",
                valueText = "${"%.2f".format(state.renderScale)}x",
                valueColor = NeonCyan,
                value = state.renderScale,
                valueRange = 0.25f..2.0f,
                onValueChange = onUpdateRenderScale,
                testTag = "slider_render_scale"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onUpdateFxaa(!state.fxaaEnabled) }
                ) {
                    Checkbox(
                        checked = state.fxaaEnabled,
                        onCheckedChange = onUpdateFxaa,
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonCyan,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("check_fxaa")
                    )
                    Text(
                        text = "4-Tap Super-Sample AA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = onTogglePause,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ObsidianBg,
                        contentColor = if (state.paused) NeonAmber else TextPrimary
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                        .testTag("btn_pause_resume")
                ) {
                    Text(
                        text = if (state.paused) "Resume" else "Pause",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // 5. Autonomous Repair Diagnostic Log
        HudCard(borderColor = SlateBorder) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUTONOMOUS REPAIR LOG",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonCyan
                )
                Text(
                    text = state.gpuRendererName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 8.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBg.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                state.healLogs.take(6).forEach { log ->
                    val logColor = when (log.severity) {
                        LogSeverity.NOMINAL -> NeonGreen
                        LogSeverity.INFO -> NeonCyan
                        LogSeverity.WARNING -> NeonAmber
                        LogSeverity.CRITICAL -> NeonPink
                    }
                    Text(
                        text = "[${log.timestamp}][${log.tag}] ${log.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = logColor,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HudCard(
    borderColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(HudPanelBg)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        content()
    }
}

@Composable
fun LabeledSliderRow(
    label: String,
    valueText: String,
    valueColor: Color,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelSmall,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = valueColor,
                activeTrackColor = valueColor,
                inactiveTrackColor = ObsidianBg
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .testTag(testTag)
        )
    }
}

@Composable
fun MiniStatBox(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ObsidianBg.copy(alpha = 0.75f))
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 8.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

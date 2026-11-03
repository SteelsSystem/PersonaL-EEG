package com.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.SpectDatabase
import com.example.data.SpectRepository
import com.example.model.HudTab
import com.example.sensor.BiometricSensorEngine
import com.example.ui.components.AsIfScreenShareBestWorkModal
import com.example.ui.components.AxiomBottomNavigationDock
import com.example.ui.components.AxiomGlSurfaceViewport
import com.example.ui.components.AxiomTopTelemetryHeader
import com.example.ui.components.BenchmarkReportModal
import com.example.ui.components.BiometricsHudDeck
import com.example.ui.components.CodexAndShaHudDeck
import com.example.ui.components.EmbeddedHtmlProfilerModal
import com.example.ui.components.GlslSourceCodeModal
import com.example.ui.components.ScreenFilterAntStudioModal
import com.example.ui.components.ScreenFilterAntViewportOverlay
import com.example.ui.components.SpectLocalStatsHudDeck
import com.example.ui.components.Sy4EegBlockMindDashboardScreen
import com.example.ui.components.TunnelHudDeck
import com.example.ui.components.UnicriticHudDeck
import com.example.ui.components.WidescreenTunnelStageBar
import com.example.ui.theme.HudPanelBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianBg
import com.example.viewmodel.AxiomViewModel
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        val database = SpectDatabase.getInstance(applicationContext)
        val repository = SpectRepository(database.spectDao())

        setContent {
            MyApplicationTheme {
                val axiomViewModel: AxiomViewModel = viewModel(
                    factory = AxiomViewModel.Factory(repository)
                )
                Sy4EegAxiomApp(viewModel = axiomViewModel)
            }
        }
    }
}

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)

            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
            if (resId != 0) {
                val siteKey = context.getString(resId)
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG, "recaptcha_site_key resource string is blank.")
                }
            } else {
                Log.e(TAG, "recaptcha_site_key string resource not found in package.")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

@Composable
fun Sy4EegAxiomApp(
    viewModel: AxiomViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spectStats by viewModel.spectStats.collectAsStateWithLifecycle()
    val spectRoomRecords by viewModel.spectRoomRecords.collectAsStateWithLifecycle()
    val verifications by viewModel.scriptVerifications.collectAsStateWithLifecycle()
    val unicriticVerdicts by viewModel.unicriticVerdicts.collectAsStateWithLifecycle()
    val unicriticDriftLogs by viewModel.unicriticDriftLogs.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Synchronize requested screen orientation when user toggles Widescreen Landscape Lock
    LaunchedEffect(state.widescreenLandscapeLocked) {
        val activity = context as? Activity
        activity?.requestedOrientation = if (state.widescreenLandscapeLocked) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    val sensorEngine = remember {
        BiometricSensorEngine(
            context = context,
            onUpdate = { hwTelemetry ->
                viewModel.onHardwareTelemetryUpdate(hwTelemetry)
            }
        )
    }

    DisposableEffect(sensorEngine) {
        sensorEngine.startSensors()
        onDispose {
            sensorEngine.stopSensors()
        }
    }

    val blePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        sensorEngine.toggleBleNeuroScan()
    }

    BackHandler(enabled = state.activeTab != HudTab.TUNNEL || !state.hudVisible) {
        if (!state.hudVisible) {
            viewModel.toggleHudVisibility()
        } else {
            viewModel.selectTab(HudTab.TUNNEL)
        }
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            AxiomTopTelemetryHeader(
                state = state,
                onToggleBlockMind = viewModel::toggleBlockMindStasis,
                onOpenUnicriticTab = { viewModel.selectTab(HudTab.UNICRITIC) },
                onOpenGlslModal = { viewModel.setShowGlslModal(true) },
                onToggleHud = viewModel::toggleHudVisibility,
                onOpenScreenFilterAntModal = { viewModel.setShowScreenFilterAntModal(true) },
                onOpenScreenShareModal = { viewModel.setShowScreenShareBestWorkModal(true) },
                onToggleWidescreenStage = viewModel::toggleWidescreenStage,
                onToggleLandscapeLock = viewModel::toggleWidescreenLandscapeLock,
                onOpenSpectSensorDashboard = { viewModel.selectTab(HudTab.SPECT) },
                onOpenCodexWhitepaper = { viewModel.selectTab(HudTab.CODEX) },
                onOpenBlockMindDashboard = { viewModel.setShowBlockMindDashboard(true) }
            )
        },
        bottomBar = {
            AxiomBottomNavigationDock(
                activeTab = state.activeTab,
                hudVisible = state.hudVisible,
                onSelectTab = viewModel::selectTab
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideWindow = maxWidth >= 600.dp || maxWidth > maxHeight

            // 1. Live 3D OpenGL ES 2.0 Hex-Reflex Tunnel Viewport (Full Background + Widescreen Cockpit)
            AxiomGlSurfaceViewport(
                state = state,
                onFrameTelemetry = viewModel::onGlFrameTelemetry,
                onShaderHealLog = { tag, msg -> viewModel.appendLog(tag, msg) },
                onOrbitalTouch = viewModel::updateOrbitalTouch,
                onOrbitalTouchNormalized = viewModel::updateOrbitalTouch,
                modifier = Modifier.fillMaxSize()
            )

            // 1B. SCREEN FILTER ANT // Pheromone Polarizer & AI Image Projection Overlay
            ScreenFilterAntViewportOverlay(
                state = state,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Floating Tactical HUD Deck Overlay (Widescreen Split-Cockpit on wide screens, 16:9 Stage + Deck on portrait)
            AnimatedVisibility(
                visible = state.hudVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = if (isWideWindow) {
                    Modifier
                        .fillMaxHeight()
                        .widthIn(max = 460.dp)
                        .align(Alignment.CenterEnd)
                } else {
                    Modifier.fillMaxSize()
                }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Pinned 16:9 Widescreen 3D Stage in Portrait/Compact mode so HUD cards never bury the 3D tunnel
                    if (!isWideWindow && state.widescreenStageEnabled) {
                        WidescreenTunnelStageBar(
                            state = state,
                            onFrameTelemetry = viewModel::onGlFrameTelemetry,
                            onShaderHealLog = { tag, msg -> viewModel.appendLog(tag, msg) },
                            onOrbitalTouchNormalized = viewModel::updateOrbitalTouch,
                            onStepRotatingFrq = viewModel::stepRotatingFrequencyHz,
                            onToggleRotationDir = viewModel::toggleRotationDirection,
                            onToggleEegSync = viewModel::toggleSyncRotatingFrqWithEeg,
                            onToggleLandscapeLock = viewModel::toggleWidescreenLandscapeLock,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        when (state.activeTab) {
                            HudTab.TUNNEL -> TunnelHudDeck(
                                state = state,
                                onSelectPreset = viewModel::selectShaderPreset,
                                onInjectFracture = { viewModel.injectFracture(0.85f) },
                                onRepackageHeal = viewModel::triggerRepackageAndHeal,
                                onUpdateHealRate = viewModel::updateHealRate,
                                onUpdateAutoGovernor = viewModel::updateAutoGovernor,
                                onUpdateHexScale = viewModel::updateHexScale,
                                onUpdateReflexDensity = viewModel::updateReflexDensity,
                                onUpdateMagPressure = viewModel::updateMagPressure,
                                onUpdateAntiMagPulse = viewModel::updateAntiMagPulse,
                                onUpdateMaxSteps = viewModel::updateMaxSteps,
                                onUpdateRenderScale = viewModel::updateRenderScale,
                                onUpdateFxaa = viewModel::updateFxaa,
                                onTogglePause = viewModel::togglePause,
                                onRunBenchmark = viewModel::start10sBenchmark,
                                onUpdateCoreTension = viewModel::updateCoreTensionString,
                                onCompleteCoreTensionPass = viewModel::completeCoreTensionPass,
                                onSelectFilterMode = viewModel::selectScreenFilterAntMode,
                                onUpdateFilterIntensity = viewModel::updateScreenFilterAntIntensity,
                                onUpdateSwarmDensity = viewModel::updateScreenFilterAntSwarmDensity,
                                onToggleProjectAiImage = viewModel::setScreenFilterProjectAiImage,
                                onUpdateAiOverlayAlpha = viewModel::updateScreenFilterAiOverlayAlpha,
                                onUpdateAiPrompt = viewModel::updateScreenFilterAiPrompt,
                                onGenerateAiImage = viewModel::generateScreenFilterAiImage,
                                onEditAiImage = viewModel::editScreenFilterAiImage,
                                onSeedProceduralBaseImage = viewModel::seedProceduralScreenFilterBitmap,
                                onSelectUserBitmapForEdit = viewModel::selectUserBitmapForScreenFilterEdit,
                                onOpenFullScreenStudioModal = { viewModel.setShowScreenFilterAntModal(true) },
                                onUpdateScreenShareSearchQuery = viewModel::updateScreenShareSearchQuery,
                                onSelectScreenShareCategory = viewModel::selectScreenShareCategory,
                                onSelectScreenShareSpotlightItem = viewModel::selectScreenShareSpotlightItem,
                                onLaunchBestWorkInViewport = viewModel::launchBestWorkInViewport,
                                onVerifyAndSealBestWork = viewModel::verifyAndSealBestWork,
                                onOpenFullScreenShareModal = { viewModel.setShowScreenShareBestWorkModal(true) },
                                onCopyToast = viewModel::showToast,
                                onTriggerGlslFalseDenySanctuary = viewModel::triggerGlslFalseDenySanctuary,
                                onRestoreGlslTrueState = viewModel::restoreGlslTrueState,
                                onToggleFailloadGuard = viewModel::toggleFailloadGuard,
                                onSimulateFailloadAndIntercept = viewModel::simulateFailloadAndIntercept,
                                onForceFailloadSafeFallback = { viewModel.executeFailloadSafeFallback() },
                                onToggleContinuityInterlock = viewModel::toggleContinuityInterlock,
                                onSimulateContinuityAutoRemediate = viewModel::simulateOutOfBoundsUniformAndAutoRemediate,
                                onUpdateRotatingFrequencyHz = viewModel::updateRotatingFrequencyHz,
                                onToggleSyncRotatingFrqWithEeg = viewModel::toggleSyncRotatingFrqWithEeg,
                                onToggleRotationDirection = viewModel::toggleRotationDirection,
                                onToggleWidescreenStage = viewModel::toggleWidescreenStage,
                                onToggleWidescreenLandscapeLock = viewModel::toggleWidescreenLandscapeLock,
                                onSelectInterconnectNode = viewModel::selectLocalInterconnectNode,
                                onExecuteServerlessLocalDeploy = viewModel::executeServerlessLocalDeploy,
                                onInjectEgressFault = viewModel::injectNonLocalEgressFault,
                                onRemediateLocalOnly = viewModel::remediateToLocalOnlyTrue
                            )

                        HudTab.BIOMETRICS -> BiometricsHudDeck(
                            state = state,
                            onSelectRgbMode = viewModel::selectRgbMode,
                            onToggleBlockMind = viewModel::toggleBlockMindStasis,
                            onReorderOsaMatrix = viewModel::reorderOsaMatrixNow,
                            onUpdateDelta = { viewModel.updateEegBands(delta = it) },
                            onUpdateTheta = { viewModel.updateEegBands(theta = it) },
                            onUpdateAlpha = { viewModel.updateEegBands(alpha = it) },
                            onUpdateBeta = { viewModel.updateEegBands(beta = it) },
                            onUpdateGamma = { viewModel.updateEegBands(gamma = it) },
                            onApplyBiometricPreset = viewModel::applyBiometricPreset,
                            onTriggerBleScan = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !sensorEngine.hasBlePermissions()) {
                                    blePermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.BLUETOOTH_SCAN,
                                            Manifest.permission.BLUETOOTH_CONNECT
                                        )
                                    )
                                } else {
                                    sensorEngine.toggleBleNeuroScan()
                                }
                            },
                            onToggleMockStream = viewModel::toggleMockBiometricStream,
                            onTriggerSynapticBurst = viewModel::triggerMockSynapticBurst,
                            onSetBlockMindPolarity = viewModel::setBlockMindPolarityFlux,
                            onStepBlockMindPolarity = viewModel::stepBlockMindPolarity,
                            onSelectAsCoreProfile = viewModel::selectAsCoreProfile,
                            onToggleAsCoreStream = viewModel::toggleAsCoreStream,
                            onToggleAsCoreAutoDriveBlockMind = viewModel::toggleAsCoreAutoDriveBlockMind,
                            onSetAsCoreCadenceHz = viewModel::setAsCoreCadenceHz,
                            onUpdateAsCoreChannelGains = { r, g, b ->
                                viewModel.updateAsCoreChannelGains(r, g, b)
                            },
                            onInjectAsCorePulse = { axis ->
                                viewModel.injectAsCoreChromaticPulse(axis)
                            },
                            onOpenBlockMindDashboard = { viewModel.setShowBlockMindDashboard(true) }
                        )

                        HudTab.UNICRITIC -> UnicriticHudDeck(
                            state = state,
                            verdicts = unicriticVerdicts,
                            onInjectCriticalDrift = viewModel::injectCriticalDriftStressTest,
                            onExecuteRemediation = viewModel::executeUnicriticRemediation,
                            onRemediateSingleAxis = viewModel::remediateSingleAxis,
                            onToggleDistortion = viewModel::toggleUnicriticDistortion,
                            onLogManualVerdict = viewModel::logManualUnicriticVerdict,
                            onDeleteVerdict = viewModel::deleteUnicriticVerdict,
                            onSimulateContinuityAutoRemediate = viewModel::simulateOutOfBoundsUniformAndAutoRemediate
                        )

                        HudTab.CODEX -> CodexAndShaHudDeck(
                            state = state,
                            verifications = verifications,
                            onSelectCrateFilter = viewModel::selectCodexFilter,
                            onLoadEntryIntoVerifier = viewModel::loadCodexEntryIntoVerifier,
                            onLoadActiveShaderIntoVerifier = viewModel::loadActiveShaderIntoVerifier,
                            onUpdateRawScript = viewModel::updateRawScriptInput,
                            onUpdateExpectedSha = viewModel::updateExpectedSha256Input,
                            onExecuteCleanAndVerify = viewModel::executeScriptCleanAndPersistAudit,
                            onOpenOriginalHtmlProfiler = { viewModel.setShowEmbeddedHtmlProfilerModal(true) },
                            onParseForwardTunnel = viewModel::parseForwardLogicTunnel,
                            onResolveBlockMindMiscalculus = viewModel::resolveBlockMindMiscalculusIfSafe,
                            onSelectMicaRegime = viewModel::selectMicaComplianceRegime,
                            onUpdateMicaDWaveDelta = viewModel::updateMicaDWaveDelta,
                            onExecuteMicaIfDWaveClampAndSeal = viewModel::executeMicaIfDWaveClampAndSeal,
                            onUpdateScreenShareSearchQuery = viewModel::updateScreenShareSearchQuery,
                            onSelectScreenShareCategory = viewModel::selectScreenShareCategory,
                            onSelectScreenShareSpotlightItem = viewModel::selectScreenShareSpotlightItem,
                            onLaunchBestWorkInViewport = viewModel::launchBestWorkInViewport,
                            onVerifyAndSealBestWork = viewModel::verifyAndSealBestWork,
                            onOpenFullScreenShareModal = { viewModel.setShowScreenShareBestWorkModal(true) },
                            onExecuteServerlessLocalDeploy = viewModel::executeServerlessLocalDeploy,
                            onLoadDeployManifestIntoVerifier = viewModel::loadServerlessDeployManifestIntoVerifier,
                            onRemediateLocalOnly = viewModel::remediateToLocalOnlyTrue,
                            onSelectDeployTierMode = viewModel::selectDeployTierMode,
                            onUpdateDecayLambda = viewModel::updateCorporateDecayLambda,
                            onStepDecayEpoch = viewModel::stepCorporateDecayEpoch,
                            onSelectOwnDevLogicNode = viewModel::selectOwnDevLogicNode,
                            onExecuteUnifiedDeployAndSealWhitepaper = viewModel::executeUnifiedDeployAndSealWhitepaper,
                            onSelectModularityBlock = viewModel::selectModularityBlock,
                            onSelectEnvComparisonFilter = viewModel::selectEnvComparisonFilter,
                            onSelectMarketingPillar = viewModel::selectMarketingPillar,
                            onSimulateLicensingDrift = viewModel::simulateLicensingTcDrift,
                            onSelfCorrectAndSealLicensing = viewModel::executeLicensingSelfCorrectionAndSeal,
                            onLoadModularityBlockIntoVerifier = viewModel::loadModularityBlockIntoVerifier,
                            onSelectTargetDeviceMode = viewModel::selectTargetDeviceMode,
                            onSelectMetallurgiaPreset = viewModel::selectMetallurgiaPreset,
                            onEngageMetallurgiaAntiMagPosition = viewModel::engageMetallurgiaAntiMagPosition,
                            onVerifyAndSeal2faDeployLogic = viewModel::verifyAndSeal2faDeployLogic,
                            onCopyToast = viewModel::showToast
                        )

                        HudTab.SPECT -> SpectLocalStatsHudDeck(
                            state = state,
                            spectStats = spectStats,
                            spectRoomRecords = spectRoomRecords,
                            driftLogs = unicriticDriftLogs,
                            onToggleInvertedMode = viewModel::toggleSpectInvertedMode,
                            onCaptureSnapshot = viewModel::captureCurrentSpectSnapshot,
                            onDeleteRecord = viewModel::deleteSpectRecord,
                            onDeleteDriftLog = viewModel::deleteUnicriticDriftLog,
                            onClearAllRecords = viewModel::clearAllSpectRecords,
                            onSelectSensorPathFilter = viewModel::selectSpectSensorPathFilter,
                            onToggleSensorStream = viewModel::toggleSpectSensorPathStream,
                            onCalibrateSensorPaths = viewModel::calibrateSpectRoomSensorPaths,
                            onSealSensorPathToRoom = viewModel::sealActiveSensorPathSnapshotToRoom,
                            onDeleteSpectRoomRecord = viewModel::deleteSpectRoomEntityRecord
                        )
                    }
                    }
                }
            }

            // 3. Toast Notification Banner
            state.toastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(HudPanelBg)
                        .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonCyan
                    )
                }
            }
        }
    }

    if (state.showGlslModal) {
        GlslSourceCodeModal(
            state = state,
            onDismiss = { viewModel.setShowGlslModal(false) },
            onCopyToast = viewModel::showToast
        )
    }

    state.benchmarkReport?.let { report ->
        BenchmarkReportModal(
            report = report,
            onDismiss = viewModel::dismissBenchmarkReport,
            onCopyToast = viewModel::showToast
        )
    }

    if (state.showEmbeddedHtmlProfilerModal) {
        EmbeddedHtmlProfilerModal(
            onDismiss = { viewModel.setShowEmbeddedHtmlProfilerModal(false) }
        )
    }

    if (state.showScreenFilterAntModal) {
        ScreenFilterAntStudioModal(
            state = state,
            onDismiss = { viewModel.setShowScreenFilterAntModal(false) },
            onSelectFilterMode = viewModel::selectScreenFilterAntMode,
            onUpdateFilterIntensity = viewModel::updateScreenFilterAntIntensity,
            onUpdateSwarmDensity = viewModel::updateScreenFilterAntSwarmDensity,
            onToggleProjectAiImage = viewModel::setScreenFilterProjectAiImage,
            onUpdateAiOverlayAlpha = viewModel::updateScreenFilterAiOverlayAlpha,
            onUpdateAiPrompt = viewModel::updateScreenFilterAiPrompt,
            onGenerateAiImage = viewModel::generateScreenFilterAiImage,
            onEditAiImage = viewModel::editScreenFilterAiImage,
            onSeedProceduralBaseImage = viewModel::seedProceduralScreenFilterBitmap,
            onSelectUserBitmapForEdit = viewModel::selectUserBitmapForScreenFilterEdit
        )
    }

    if (state.showScreenShareBestWorkModal) {
        AsIfScreenShareBestWorkModal(
            state = state,
            onDismiss = { viewModel.setShowScreenShareBestWorkModal(false) },
            onUpdateSearchQuery = viewModel::updateScreenShareSearchQuery,
            onSelectCategory = viewModel::selectScreenShareCategory,
            onSelectSpotlightItem = viewModel::selectScreenShareSpotlightItem,
            onLaunchWorkInViewport = viewModel::launchBestWorkInViewport,
            onVerifyAndSealWork = viewModel::verifyAndSealBestWork,
            onCopyToast = viewModel::showToast
        )
    }

    if (state.showBlockMindDashboard) {
        Sy4EegBlockMindDashboardScreen(
            state = state,
            onNavigateBack = { viewModel.setShowBlockMindDashboard(false) },
            onToggleMockStream = viewModel::toggleMockBiometricStream,
            onTriggerSynapticBurst = viewModel::triggerMockSynapticBurst,
            onToggleBlockMindStasis = viewModel::toggleBlockMindStasis,
            onSetBlockMindPolarity = viewModel::setBlockMindPolarityFlux,
            onSelectRgbMode = viewModel::selectRgbMode,
            onApplyBiometricPreset = viewModel::applyBiometricPreset,
            onUpdateDelta = { viewModel.updateEegBands(delta = it) },
            onUpdateTheta = { viewModel.updateEegBands(theta = it) },
            onUpdateAlpha = { viewModel.updateEegBands(alpha = it) },
            onUpdateBeta = { viewModel.updateEegBands(beta = it) },
            onUpdateGamma = { viewModel.updateEegBands(gamma = it) },
            onReorderOsaMatrix = viewModel::reorderOsaMatrixNow,
            onInjectAsCorePulse = { axis -> viewModel.injectAsCoreChromaticPulse(axis) }
        )
    }
}

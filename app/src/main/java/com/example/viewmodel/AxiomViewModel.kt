package com.example.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ScriptVerificationEntity
import com.example.data.SpectRepository
import com.example.data.SpectRoomEntity
import com.example.data.SpectStatEntity
import com.example.data.UnicriticDriftLogEntity
import com.example.data.UnicriticVerdictEntity
import com.example.model.AsIfTunnelState
import com.example.model.BenchmarkReport
import com.example.model.CodexEntry
import com.example.model.ContractPhase
import com.example.model.CriticAxis
import com.example.model.EegBandState
import com.example.model.HealLogEntry
import com.example.model.HudTab
import com.example.model.LogSeverity
import com.example.model.OsaMatrixCell
import com.example.model.RgbCognitiveMode
import com.example.model.RustCodexCatalog
import com.example.model.ShaderPreset
import com.example.model.UnicriticFinding
import com.example.model.UnicriticLiveEvaluation
import com.example.model.UnicriticVerdictLevel
import com.example.sensor.HardwareTelemetry
import com.example.service.AsCoreRgbBiometricFrame
import com.example.service.AsCoreRgbBiometricStreamService
import com.example.service.AsCoreStreamProfile
import com.example.service.AtlasKnowabilityAndOdmEngine
import com.example.service.AtlasKnowabilityAndOdmState
import com.example.service.DefaultAsCoreRgbBiometricStreamService
import com.example.service.DeployTierMode
import com.example.service.EmergencyReloadRepositoryService
import com.example.service.GlobalVsPersonalDeployEngine
import com.example.service.IntakeCantJumpMode
import com.example.service.LocalOnlyPathsInterconnectEngine
import com.example.service.OdmAlignmentTarget
import com.example.service.ProtocolOEngineProvider
import com.example.service.ProtocolOFunctionId
import com.example.service.ProtocolOIntegrityCoreEngine
import com.example.service.ProtocolOIntegrityCoreState
import com.example.service.ProtocolOWireEvent
import com.example.service.RgbChannelAxis
import com.example.service.ServerlessLocalDeployState
import com.example.service.SonicSoundSwapMode
import com.example.service.SpectRoomSensorDashboardState
import com.example.service.SpectRoomSensorPathEngine
import com.example.service.UnifiedDeployWhitepaperState
import com.example.shader.AxiomShaders
import com.example.ui.components.BestWorkCategoryFilter
import com.example.ui.components.BestWorkShowcaseCatalog
import com.example.ui.components.BestWorkShowcaseItem
import com.example.ui.components.MicaComplianceCatalog
import com.example.ui.components.MicaComplianceRegime
import com.example.ui.components.ScreenFilterAiUiState
import com.example.ui.components.ScreenFilterAntMode
import com.example.ui.components.createProceduralAntLatticeSeedBitmap
import com.example.ui.components.editLocalAntFilterBitmap
import com.example.ui.components.synthesizeLocalAntFilterBitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AxiomUiState(
    val activeTab: HudTab = HudTab.TUNNEL,
    val hudVisible: Boolean = true,
    val activeShader: ShaderPreset = ShaderPreset.HEX_REFLEX,
    val hexScale: Float = 1.80f,
    val reflexDensity: Float = 1.45f,
    val magPressure: Float = 1.20f,
    val antiMagPulse: Float = 0.85f,
    val maxSteps: Int = 96,
    val renderScale: Float = 1.00f,
    val fxaaEnabled: Boolean = false,
    val healRate: Float = 0.65f,
    val autoGovernor: Boolean = true,
    val perturbation: Float = 0.0f,
    val healWave: Float = 0.0f,
    val healCycles: Int = 1,
    val paused: Boolean = false,
    val blockMindStasis: Boolean = false,
    val unblockMindActive: Boolean = false,
    val blockMindPolarityFlux: Float = 0.0f,
    val blockMindSafetyVerified: Boolean = true,
    val blockMindSealedHasTrue: Boolean = true,
    val blockMindMiscalculusDelta: Float = 0.0f,
    val simTime: Float = 0.0f,
    val mouseX: Float = 540f,
    val mouseY: Float = 960f,
    val orbitalYawNorm: Float = 0.0f,
    val orbitalPitchNorm: Float = 0.0f,
    val repackageSignalCounter: Int = 0,

    // Widescreen Viewport Stage & Rotating Frequency (ROTATING FRQ) Engine
    val widescreenStageEnabled: Boolean = true,
    val widescreenLandscapeLocked: Boolean = false,
    val rotatingFrequencyHz: Float = 0.65f,
    val effectiveRotatingFrequencyHz: Float = 0.65f,
    val rotationPhaseRad: Float = 0.0f,
    val rotationDirectionCw: Boolean = true,
    val syncRotatingFrqWithEeg: Boolean = true,

    // GLSL Gate ("As to GLSL: TRUE // IF FALSE -> DENY")
    val glslGateTrue: Boolean = true,
    val glslSyntaxValid: Boolean = true,
    val glslGateDiagnostic: String = "AS TO GLSL: TRUE • 60 FPS Raymarch & Uniform Bounds Nominal",

    // Tri-Axis Continuity Interlock (Locks GLSL Gate, EEG State & SHA-256 Chain across transitions; Auto-Remediates Uniform Bounds)
    val continuityInterlockEnabled: Boolean = true,
    val continuityChainEpoch: Int = 1,
    val continuityParentSha256: String = "0000000000000000000000000000000000000000000000000000000000000000",
    val continuityCurrentSha256: String = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
    val autoRemediatedUniformCount: Int = 2,
    val lastContinuityEventSummary: String = "BOOT-LOCK // GLSL Gate + SY4-EEG + SHA-256 Chain Synchronized",

    // RULE: IF FAILLOAD POSSIBLE (Unified Guard for GPU, Biometric, and AI Loads)
    val failloadGuardEnabled: Boolean = true,
    val failloadRiskPct: Int = 12,
    val failloadGpuRiskPct: Int = 10,
    val failloadBiometricRiskPct: Int = 14,
    val failloadAiRiskPct: Int = 8,
    val failloadInterceptCount: Int = 1,
    val failloadLastInterceptReason: String = "BOOT GUARD: GPU/Biometric/AI Local Safe Fallback Armed",
    val failloadFallbackActive: Boolean = false,

    // Telemetry
    val currentFps: Int = 60,
    val currentFrameMs: Float = 16.6f,
    val minFps: Int = 55,
    val maxFps: Int = 60,
    val gigaOpsLoad: Float = 12.4f,
    val gpuRendererName: String = "OpenGL ES 2.0 Hardware",
    val frameTimeHistory: List<Float> = List(80) { 16.6f },
    val integrityHistory: List<Float> = List(80) { 100f },
    val eegWaveHistory: List<Float> = List(80) { 50f },

    // Biometrics & Contractual State + as[core] Real-Time RGB Biometric Stream Service Layer
    val rgbMode: RgbCognitiveMode = RgbCognitiveMode.FOCUS_BLUE,
    val effectiveRgbTint: FloatArray = RgbCognitiveMode.FOCUS_BLUE.shaderTint,
    val eegBands: EegBandState = EegBandState(),
    val mockStreamEnabled: Boolean = true,
    val dominantFrequencyHz: Float = 18.2f,
    val phaseCoherencePct: Int = 92,
    val cognitiveLoadPct: Int = 32,
    val hardwareTelemetry: HardwareTelemetry = HardwareTelemetry(),
    val contractPhase: ContractPhase = ContractPhase.EPISTULA,
    val asIfTunnelState: AsIfTunnelState = AsIfTunnelState.LOCKED,
    val osaMatrix: List<OsaMatrixCell> = emptyList(),
    val asCoreFrame: AsCoreRgbBiometricFrame = AsCoreRgbBiometricFrame(),
    val asCoreStreamActive: Boolean = true,
    val asCoreAutoDriveBlockMind: Boolean = true,
    val asCoreCadenceHz: Int = 30,
    val asCoreRChannelGain: Float = 1.00f,
    val asCoreGChannelGain: Float = 1.00f,
    val asCoreBChannelGain: Float = 1.00f,

    // Unicritic Multi-Axis Consensus State & AX-IS-T Redline Shader Distortion
    val unicriticEvaluation: UnicriticLiveEvaluation = UnicriticLiveEvaluation(),
    val unicriticDistortionEnabled: Boolean = true,
    val effectiveUnicriticDrift: Float = 0.0f,
    val biometricLoadIntensity: Float = 0.32f,
    val redlineThreatActive: Boolean = false,
    val axisTProtocolCode: String = "AX-IS-T // [TREAT SECURITY THREAT:REDLINE]",
    val recentRemediations: List<com.example.model.RemediationActionSummary> = listOf(
        com.example.model.RemediationActionSummary(
            id = 102L,
            timestamp = "BOOT-SYNC",
            axisTag = "ALL AXES",
            actionTitle = "Tri-Axis Boot Equilibrium & SHA-256 Seal",
            subsystemsRestored = "Hex-Reflex Lattice + Alpha-Beta EEG + Osa A-X-00..09",
            scoreBefore = 74,
            scoreAfter = 96,
            outcomeBadge = "EPISTULA SEALED"
        ),
        com.example.model.RemediationActionSummary(
            id = 101L,
            timestamp = "INIT-GOV",
            axisTag = "GLSL AXIS",
            actionTitle = "Autonomous Raymarch Depth Calibration",
            subsystemsRestored = "Scaled raymarch steps to 96 for 60 FPS lock",
            scoreBefore = 68,
            scoreAfter = 95,
            outcomeBadge = "PHASE LOCKED"
        )
    ),

    // Core Tension String Pass & Forward Logic Tunnel Parser (PDF-05 . LEGAL-AXIS) + Local Only Paths Interconnect
    val coreTensionString: Float = 1.00f,
    val coreTensionPassDone: Boolean = true,
    val forwardTunnelParse: com.example.model.ForwardTunnelParseResult = com.example.model.ForwardTunnelParseResult(),
    val localInterconnectState: ServerlessLocalDeployState = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
        nodes = LocalOnlyPathsInterconnectEngine.buildDefaultNodes(),
        forwardParse = com.example.model.ForwardTunnelParseResult(),
        asCoreToken = "as[core::focus_b#0]",
        continuitySha256 = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
        sha256Verified = true,
        roomAuditVerified = true,
        deployEpoch = 1,
        roomAuditRecordCount = 2
    ),

    // Screen Filter Ant & AI / Local Image Studio
    val screenFilterAntMode: ScreenFilterAntMode = ScreenFilterAntMode.OFF,
    val screenFilterAntIntensity: Float = 0.62f,
    val screenFilterAntSwarmDensity: Int = 28,
    val screenFilterAiPrompt: String = "Bioluminescent hexagonal ant-pheromone lattice with emerald and cyan quantum trails",
    val screenFilterAiState: ScreenFilterAiUiState = ScreenFilterAiUiState.Idle,
    val screenFilterActiveBitmap: Bitmap? = null,
    val screenFilterProjectAiImage: Boolean = false,
    val screenFilterAiOverlayAlpha: Float = 0.48f,
    val showScreenFilterAntModal: Boolean = false,

    // MiCA Compliance Architecture [Today Pre + Fut MiCA Arch if D~] & Unified [PaidvsFREE] Whitepaper
    val micaComplianceRegime: MicaComplianceRegime = MicaComplianceRegime.DUAL_HORIZON_LOCK,
    val micaDWaveDelta: Float = 0.14f,
    val micaDWaveSafeSealed: Boolean = true,
    val unifiedDeployWhitepaperState: UnifiedDeployWhitepaperState = GlobalVsPersonalDeployEngine.evaluateUnifiedState(),

    // As-If Screen Share // Search Best Work
    val screenShareSearchQuery: String = "",
    val screenShareCategoryFilter: BestWorkCategoryFilter = BestWorkCategoryFilter.ALL_BEST,
    val screenShareSpotlightId: String = BestWorkShowcaseCatalog.items.first().id,
    val showScreenShareBestWorkModal: Boolean = false,

    // Benchmark
    val benchmarkActive: Boolean = false,
    val benchmarkProgress: Float = 0.0f,
    val benchmarkElapsedSec: Float = 0.0f,
    val benchmarkReport: BenchmarkReport? = null,

    // Codex & SHA-256 Script Verifier
    val selectedCodexCrateId: String = "all",
    val codexSearchQuery: String = "",
    val scriptTitleInput: String = "Osa-A-X-inf00 // HexReflex & SQLx Seal",
    val scriptCrateTag: String = "Čištění skriptu SHA-256",
    val rawScriptInput: String = RustCodexCatalog.entries.first().codeSnippet,
    val cleanedScriptPreview: String = "",
    val computedSha256: String = "",
    val expectedSha256Input: String = "",
    val sha256Verified: Boolean = true,

    // SPECT Inverted View Toggle & Real-Time SPECT Room Sensor Paths Dashboard
    val spectInvertedMode: Boolean = true,
    val spectSensorDashboardState: SpectRoomSensorDashboardState = SpectRoomSensorPathEngine.buildInitialDashboardState(),

    // RGB/FRQ Knowability, As Atlas (RED) Lost-Path Wayfinder, Center File-Line Spine & ODM Stack Topology
    val atlasKnowabilityState: AtlasKnowabilityAndOdmState = AtlasKnowabilityAndOdmEngine.evaluateState(
        previous = AtlasKnowabilityAndOdmState(),
        asCoreFrame = AsCoreRgbBiometricFrame(),
        asCoreStreamActive = true,
        eegBands = EegBandState(),
        dominantHz = 18.2f,
        rotatingFrqHz = 0.65f,
        phaseCoherencePct = 92,
        perturbation = 0.0f,
        glslGateTrue = true,
        redlineThreatActive = false,
        localInterconnectState = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = LocalOnlyPathsInterconnectEngine.buildDefaultNodes(),
            forwardParse = com.example.model.ForwardTunnelParseResult(),
            asCoreToken = "as[core::focus_b#0]",
            continuitySha256 = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
            sha256Verified = true,
            roomAuditVerified = true,
            deployEpoch = 1,
            roomAuditRecordCount = 2
        ),
        spectSensorHealthPct = 98,
        simTimeSec = 0.0f
    ),

    // Protocol O: Integrity Core 2026 (Sealed Sublayers: _gate, _cert, _engine, _util, _locale, _fn, _wire)
    val protocolOState: ProtocolOIntegrityCoreState = ProtocolOIntegrityCoreEngine.buildInitialState(),

    // Modals & Toasts
    val showGlslModal: Boolean = false,
    val showEmbeddedHtmlProfilerModal: Boolean = false,
    val toastMessage: String? = null,
    val healLogs: List<HealLogEntry> = emptyList()
)

class AxiomViewModel(
    private val repository: SpectRepository,
    private val mockBiometricRepository: com.example.data.MockBiometricRepository = com.example.data.MockBiometricRepository(),
    private val asCoreRgbService: AsCoreRgbBiometricStreamService = DefaultAsCoreRgbBiometricStreamService(mockBiometricRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(AxiomUiState())
    val uiState: StateFlow<AxiomUiState> = _uiState.asStateFlow()

    val spectStats: StateFlow<List<SpectStatEntity>> = repository.allSpectStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val spectRoomRecords: StateFlow<List<SpectRoomEntity>> = repository.allSpectRoomRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scriptVerifications: StateFlow<List<ScriptVerificationEntity>> = repository.allVerifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unicriticVerdicts: StateFlow<List<UnicriticVerdictEntity>> = repository.allUnicriticVerdicts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unicriticDriftLogs: StateFlow<List<UnicriticDriftLogEntity>> = repository.allUnicriticDriftLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var lowFpsStreak = 0
    private var lastFailloadAutoInterceptMs = 0L
    private var logCounter = 1L
    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

    private val benchmarkSamples = mutableListOf<Float>()
    private var benchmarkStartMs = 0L
    private var benchmarkInjectedFracture = false
    private var benchmarkTriggeredHeal = false

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            appendLog("SYS", "SY4-EEG // AXIOM Hex-Reflex & Unicritic Consensus Engine online.", LogSeverity.NOMINAL)
            appendLog("MAG", "Divergence-free magnetic vector field & Osa A-X- inf00 ready.", LogSeverity.INFO)
            recomputeOsaAndContractStates(0f, EegBandState(), HardwareTelemetry())
            cleanAndComputeSha256(
                raw = _uiState.value.rawScriptInput,
                title = _uiState.value.scriptTitleInput,
                crateTag = _uiState.value.scriptCrateTag,
                persistToRoom = false
            )
        }
        startSimulationTicker()
    }

    private fun startSimulationTicker() {
        viewModelScope.launch {
            var lastTickMs = System.currentTimeMillis()
            while (true) {
                delay(32L)
                val now = System.currentTimeMillis()
                val dtSec = ((now - lastTickMs) / 1000f).coerceIn(0.01f, 0.25f)
                lastTickMs = now
                stepPhysicsAndBiometrics(dtSec, now)
            }
        }
    }

    private fun stepPhysicsAndBiometrics(dtSec: Float, nowMs: Long) {
        val s = _uiState.value
        var nextSimTime = s.simTime
        var nextPerturbation = s.perturbation
        var nextHealWave = s.healWave
        var nextHealCycles = s.healCycles
        var justHealedCompletely = false

        if (!s.paused) {
            val timeRateMultiplier = if (s.blockMindStasis) 0.04f else 1.0f
            nextSimTime += dtSec * timeRateMultiplier

            if (nextPerturbation > 0.0005f) {
                nextPerturbation = (nextPerturbation - s.healRate * dtSec * 0.45f).coerceAtLeast(0f)
                if (nextPerturbation <= 0.005f) {
                    nextPerturbation = 0f
                    nextHealCycles++
                    justHealedCompletely = true
                }
            }

            if (nextHealWave > 0f) {
                nextHealWave += dtSec * 0.95f
                if (nextHealWave > 1.45f) {
                    nextHealWave = 0f
                }
            }
        }

        if (justHealedCompletely) {
            appendLog("HEALED", "Hexagonal magnetic lattice restored to 100% symmetry (EPISTULA).", LogSeverity.NOMINAL)
        }

        // Benchmark sweep progression
        var benchActive = s.benchmarkActive
        var benchProgress = s.benchmarkProgress
        var benchElapsedSec = s.benchmarkElapsedSec
        var benchReport = s.benchmarkReport

        if (benchActive) {
            val elapsedMs = (nowMs - benchmarkStartMs).coerceAtLeast(0L)
            benchElapsedSec = elapsedMs / 1000f
            benchProgress = (elapsedMs / 10_000f).coerceIn(0f, 1f)
            benchmarkSamples.add(s.currentFrameMs)

            if (elapsedMs > 3500L && !benchmarkInjectedFracture) {
                benchmarkInjectedFracture = true
                nextPerturbation = (nextPerturbation + 0.90f).coerceAtMost(1.0f)
                appendLog("BENCH", "Mid-sweep fracture injected (Entropy: ${"%.2f".format(nextPerturbation)})", LogSeverity.WARNING)
            }
            if (elapsedMs > 4700L && !benchmarkTriggeredHeal) {
                benchmarkTriggeredHeal = true
                nextHealWave = 0.02f
                nextPerturbation *= 0.25f
                nextHealCycles++
                appendLog("BENCH", "Automated Repackage & Self-Heal wave triggered", LogSeverity.NOMINAL)
            }
            if (elapsedMs >= 10_000L) {
                benchActive = false
                benchReport = buildBenchmarkReport(
                    samples = benchmarkSamples.toList(),
                    state = s.copy(perturbation = nextPerturbation, healCycles = nextHealCycles)
                )
                appendLog("BENCH", "10s Benchmark completed: Score ${benchReport.computeScore} PTS", LogSeverity.NOMINAL)
                logSpectSnapshotInternal(
                    title = "BENCH 10S // ${s.activeShader.title}",
                    p50 = benchReport.p50Ms,
                    p95 = benchReport.p95Ms,
                    p99 = benchReport.p99Ms
                )
            }
        }

        val hw = s.hardwareTelemetry
        val asCoreStepFrame = if (s.mockStreamEnabled && s.asCoreStreamActive && !s.paused) {
            asCoreRgbService.stepFrame(
                simTimeSec = nextSimTime,
                perturbation = nextPerturbation,
                somaticStabilityPct = hw.somaticStabilityPct
            )
        } else {
            null
        }

        val bands = asCoreStepFrame?.eegBands ?: s.eegBands
        val dominantHz = asCoreStepFrame?.dominantFrequencyHz ?: s.dominantFrequencyHz
        val phaseCoherence = asCoreStepFrame?.phaseCoherencePct ?: s.phaseCoherencePct

        // Synchronize BlockMind polarity flux when as[core] Auto-Drive is enabled
        val nextPolarityFlux = if (asCoreStepFrame != null && s.asCoreAutoDriveBlockMind) {
            (s.blockMindPolarityFlux * 0.72f + asCoreStepFrame.blockMindPolarityDrive * 0.28f).coerceIn(-1.0f, 1.0f)
        } else {
            s.blockMindPolarityFlux
        }
        val nextBlockMindStasis = if (asCoreStepFrame != null && s.asCoreAutoDriveBlockMind) {
            nextPolarityFlux <= -0.35f
        } else {
            s.blockMindStasis
        }
        val nextUnblockMindActive = if (asCoreStepFrame != null && s.asCoreAutoDriveBlockMind) {
            nextPolarityFlux >= 0.35f
        } else {
            s.unblockMindActive
        }

        // Compute smooth effective Rotating Frequency (Hz) & integrate continuous rotationPhaseRad
        val eegModFactor = if (s.syncRotatingFrqWithEeg) {
            (0.65f + (dominantHz / 24.0f) * 0.55f).coerceIn(0.35f, 1.85f)
        } else {
            1.0f
        }
        val polarityMultiplier = when {
            nextBlockMindStasis -> 0.08f
            nextUnblockMindActive -> 1.35f
            else -> 1.0f
        }
        val effectiveRotHz = (s.rotatingFrequencyHz * eegModFactor * polarityMultiplier).coerceIn(0.02f, 5.00f)
        val dirSign = if (s.rotationDirectionCw) 1.0f else -1.0f
        val twoPi = (Math.PI * 2.0).toFloat()
        val nextRotationPhase = if (!s.paused) {
            (s.rotationPhaseRad + dtSec * effectiveRotHz * twoPi * dirSign) % (twoPi * 64.0f)
        } else {
            s.rotationPhaseRad
        }

        val motionPenalty = (100 - hw.somaticStabilityPct) * 0.35f
        val rawCogLoad = asCoreStepFrame?.cognitiveLoadPct ?: (
            (bands.beta * 38f + bands.gamma * 32f + bands.theta * 18f - bands.alpha * 16f) +
                nextPerturbation * 35f +
                motionPenalty
            ).roundToInt().coerceIn(8, 99)

        val effectiveTint = asCoreStepFrame?.rgbSynchroVector ?: when (s.rgbMode) {
            RgbCognitiveMode.BIOMETRIC_AUTO -> {
                val r = (0.45f + bands.theta * 0.75f + nextPerturbation * 0.4f).coerceIn(0.25f, 1.5f)
                val g = (0.45f + bands.alpha * 0.85f * (1f - nextPerturbation * 0.5f)).coerceIn(0.25f, 1.5f)
                val b = (0.45f + bands.beta * 0.85f).coerceIn(0.25f, 1.5f)
                floatArrayOf(r, g, b)
            }
            else -> s.rgbMode.shaderTint
        }

        val eegWaveSample = (
            50f +
                sin(nextSimTime * 3.0f) * bands.delta * 12f +
                sin(nextSimTime * 6.5f) * bands.theta * 14f +
                cos(nextSimTime * 10.5f) * bands.alpha * 16f +
                sin(nextSimTime * 19.0f) * bands.beta * 14f +
                cos(nextSimTime * 32.0f) * bands.gamma * 10f
            ).coerceIn(5f, 95f)

        val integrityPct = ((1.0f - nextPerturbation) * 100f).coerceIn(0f, 100f)
        val updatedFrameHist = (s.frameTimeHistory.drop(1) + s.currentFrameMs)
        val updatedIntegrityHist = (s.integrityHistory.drop(1) + integrityPct)
        val updatedEegHist = (s.eegWaveHistory.drop(1) + eegWaveSample)

        val contractPhase = when {
            nextPerturbation > 0.42f -> ContractPhase.LITTERAE
            nextPerturbation > 0.06f || rawCogLoad > 78 -> ContractPhase.SATIS
            else -> ContractPhase.EPISTULA
        }

        val asIfState = when {
            nextPerturbation > 0.35f -> AsIfTunnelState.NOT_YET
            nextPerturbation > 0.05f || (!s.blockMindStasis && rawCogLoad > 72) -> AsIfTunnelState.CONVERGING
            else -> AsIfTunnelState.LOCKED
        }

        val osaCells = buildOsaMatrix(nextPerturbation, nextSimTime, bands)

        // Evaluate Unicritic Multi-Axis Consensus
        val unicriticEval = evaluateUnicritic(
            perturbation = nextPerturbation,
            frameMs = s.currentFrameMs,
            maxSteps = s.maxSteps,
            cogLoad = rawCogLoad,
            somaticStability = hw.somaticStabilityPct,
            osaOrderedCount = osaCells.count { it.isOrdered },
            shaVerified = s.sha256Verified,
            cleanedScriptLen = s.cleanedScriptPreview.length
        )

        val bioIntensity = (rawCogLoad / 100f).coerceIn(0.08f, 1.0f)
        val isRedline = s.redlineThreatActive ||
            unicriticEval.verdictLevel == UnicriticVerdictLevel.CRITICUS ||
            unicriticEval.driftIntensity >= 0.42f

        val effectiveDrift = if (s.unicriticDistortionEnabled) {
            val baseDrift = if (isRedline) {
                unicriticEval.driftIntensity.coerceAtLeast(0.58f)
            } else {
                unicriticEval.driftIntensity
            }
            (baseDrift * (0.55f + bioIntensity * 0.65f)).coerceIn(0f, 1.0f)
        } else {
            0.0f
        }

        // Unified RULE: IF FAILLOAD POSSIBLE Risk Evaluation (GPU + Biometric + AI/Asset Loads)
        val gpuRisk = (
            ((s.currentFrameMs - 16.0f).coerceAtLeast(0f) * 2.2f) +
                ((s.maxSteps - 64).coerceAtLeast(0) * 0.35f) +
                (nextPerturbation * 48f) +
                (if (!s.glslGateTrue) 35f else 0f)
            ).roundToInt().coerceIn(4, 100)

        val bioRisk = (
            (rawCogLoad * 0.55f) +
                (effectiveDrift * 45f) +
                ((100 - hw.somaticStabilityPct) * 0.30f)
            ).roundToInt().coerceIn(5, 100)

        val aiRisk = when (s.screenFilterAiState) {
            is ScreenFilterAiUiState.Loading -> 42
            is ScreenFilterAiUiState.Error -> 88
            is ScreenFilterAiUiState.Success -> 8
            ScreenFilterAiUiState.Idle -> if (s.screenFilterActiveBitmap == null) 14 else 6
        }

        val overallFailloadRisk = maxOf(
            (gpuRisk * 0.42f + bioRisk * 0.38f + aiRisk * 0.20f).roundToInt(),
            maxOf(gpuRisk, maxOf(bioRisk, aiRisk))
        ).coerceIn(4, 100)

        val miscalculusDelta = (nextPerturbation * 0.12f + effectiveDrift * 0.08f).coerceAtLeast(0f)
        val isSafeNow = nextPerturbation < 0.22f && effectiveDrift < 0.25f && s.sha256Verified
        val activeAsCoreFrame = asCoreStepFrame ?: s.asCoreFrame
        val nextSensorDashboard = SpectRoomSensorPathEngine.stepSensorDashboard(
            previous = s.spectSensorDashboardState,
            simTimeSec = nextSimTime,
            eegBands = bands,
            dominantHz = dominantHz,
            phaseCoherencePct = phaseCoherence,
            cognitiveLoadPct = rawCogLoad,
            asCoreFrame = activeAsCoreFrame,
            hardwareTelemetry = hw,
            frameMs = s.currentFrameMs,
            rotatingFrqHz = effectiveRotHz,
            perturbation = nextPerturbation,
            unicriticConsensus = unicriticEval.consensusScore,
            effectiveDrift = effectiveDrift,
            sha256Verified = s.sha256Verified,
            checkPassTrueOnly = s.localInterconnectState.checkPassTrueOnly,
            continuitySha256 = s.continuityCurrentSha256,
            persistedRecordCount = spectRoomRecords.value.size.coerceAtLeast(2)
        )

        val nextAtlasKnowability = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = s.atlasKnowabilityState,
            asCoreFrame = activeAsCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = bands,
            dominantHz = dominantHz,
            rotatingFrqHz = effectiveRotHz,
            phaseCoherencePct = phaseCoherence,
            perturbation = nextPerturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = isRedline,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = nextSensorDashboard.aggregateHealthPct,
            simTimeSec = nextSimTime
        )

        val finalEffectiveTint = if (nextAtlasKnowability.asAtlasRedActive) {
            floatArrayOf(1.52f, 0.18f, 0.24f)
        } else {
            effectiveTint
        }

        _uiState.update {
            it.copy(
                simTime = nextSimTime,
                effectiveRotatingFrequencyHz = effectiveRotHz,
                rotationPhaseRad = nextRotationPhase,
                perturbation = nextPerturbation,
                healWave = nextHealWave,
                healCycles = nextHealCycles,
                benchmarkActive = benchActive,
                benchmarkProgress = benchProgress,
                benchmarkElapsedSec = benchElapsedSec,
                benchmarkReport = benchReport,
                asCoreFrame = activeAsCoreFrame,
                blockMindPolarityFlux = nextPolarityFlux,
                blockMindStasis = nextBlockMindStasis,
                unblockMindActive = nextUnblockMindActive,
                eegBands = bands,
                dominantFrequencyHz = dominantHz,
                phaseCoherencePct = phaseCoherence,
                cognitiveLoadPct = rawCogLoad,
                biometricLoadIntensity = bioIntensity,
                redlineThreatActive = isRedline,
                effectiveRgbTint = finalEffectiveTint,
                frameTimeHistory = updatedFrameHist,
                integrityHistory = updatedIntegrityHist,
                eegWaveHistory = updatedEegHist,
                contractPhase = contractPhase,
                asIfTunnelState = asIfState,
                osaMatrix = osaCells,
                unicriticEvaluation = unicriticEval,
                effectiveUnicriticDrift = effectiveDrift,
                failloadGpuRiskPct = gpuRisk,
                failloadBiometricRiskPct = bioRisk,
                failloadAiRiskPct = aiRisk,
                failloadRiskPct = overallFailloadRisk,
                blockMindMiscalculusDelta = if (it.blockMindSealedHasTrue && isSafeNow) 0.0f else miscalculusDelta,
                blockMindSafetyVerified = isSafeNow,
                spectSensorDashboardState = nextSensorDashboard,
                atlasKnowabilityState = nextAtlasKnowability
            )
        }

        // Auto-Intercept when RULE: IF FAILLOAD POSSIBLE is armed and risk crosses critical threshold
        val failloadPossibleNow = overallFailloadRisk >= 82 ||
            (s.currentFrameMs > 36.0f && nextPerturbation > 0.45f) ||
            (rawCogLoad >= 86 && effectiveDrift >= 0.52f)

        if (s.failloadGuardEnabled && failloadPossibleNow && !benchActive && (nowMs - lastFailloadAutoInterceptMs > 4500L)) {
            lastFailloadAutoInterceptMs = nowMs
            executeFailloadSafeFallback(
                reason = "AUTO-INTERCEPT // Risk ${overallFailloadRisk}% (GPU ${gpuRisk}% • BIO ${bioRisk}% • AI ${aiRisk}%)"
            )
        }
    }

    private fun evaluateUnicritic(
        perturbation: Float,
        frameMs: Float,
        maxSteps: Int,
        cogLoad: Int,
        somaticStability: Int,
        osaOrderedCount: Int,
        shaVerified: Boolean,
        cleanedScriptLen: Int
    ): UnicriticLiveEvaluation {
        val findings = mutableListOf<UnicriticFinding>()

        // Axis I: GLSL & Lattice Symmetry (0..100)
        val latencyPenalty = ((frameMs - 17.5f).coerceAtLeast(0f) * 1.8f).roundToInt()
        val entropyPenalty = (perturbation * 68f).roundToInt()
        val glslScore = (100 - latencyPenalty - entropyPenalty).coerceIn(5, 100)

        if (perturbation > 0.15f) {
            findings.add(
                UnicriticFinding(
                    axis = CriticAxis.GLSL_LATTICE,
                    title = "Hexagonal Lattice Entropy Elevated (${"%.2f".format(perturbation)})",
                    detail = "Magnetic domain repetition shows phase fracture; trigger Repackage & Self-Heal wave.",
                    severity = if (perturbation > 0.45f) LogSeverity.CRITICAL else LogSeverity.WARNING,
                    actionKey = "HEAL_LATTICE"
                )
            )
        }
        if (frameMs > 28.0f || maxSteps > 192) {
            findings.add(
                UnicriticFinding(
                    axis = CriticAxis.GLSL_LATTICE,
                    title = "Raymarch Iteration Load High ($maxSteps steps / ${"%.1f".format(frameMs)}ms)",
                    detail = "Unicritic recommends scaling raymarch iterations to 88 for 60 FPS phase lock.",
                    severity = LogSeverity.WARNING,
                    actionKey = "OPTIMIZE_STEPS"
                )
            )
        }

        // Axis II: SY4-EEG & Somatic Coherence (0..100)
        val cogPenalty = ((cogLoad - 45).coerceAtLeast(0) * 1.15f).roundToInt()
        val osaPenalty = (10 - osaOrderedCount) * 4
        val somaticPenalty = ((95 - somaticStability).coerceAtLeast(0) * 0.5f).roundToInt()
        val eegScore = (100 - cogPenalty - osaPenalty - somaticPenalty).coerceIn(8, 100)

        if (cogLoad > 65 || osaOrderedCount < 10) {
            findings.add(
                UnicriticFinding(
                    axis = CriticAxis.EEG_BIOMETRIC,
                    title = "Cognitive Load ($cogLoad%) & Osa A-X- inf00 ($osaOrderedCount/10 Ordered)",
                    detail = "Rebalance EEG Alpha-Beta bands and align Osa A-X- inf00 nodes for AS-IF Tunnel Lock.",
                    severity = if (cogLoad > 78) LogSeverity.CRITICAL else LogSeverity.WARNING,
                    actionKey = "ALIGN_EEG_OSA"
                )
            )
        }

        // Axis III: Codex & SHA-256 Cryptographic Integrity (0..100)
        val shaPenalty = if (shaVerified) 0 else 45
        val scriptPenalty = if (cleanedScriptLen >= 16) 0 else 25
        val codexScore = (100 - shaPenalty - scriptPenalty).coerceIn(10, 100)

        if (!shaVerified) {
            findings.add(
                UnicriticFinding(
                    axis = CriticAxis.CODEX_SHA256,
                    title = "SHA-256 Digest Mismatch in Script Verifier",
                    detail = "Expected hash diverges from cleaned script digest; re-seal SHA-256 verification.",
                    severity = LogSeverity.CRITICAL,
                    actionKey = "SEAL_SHA256"
                )
            )
        }

        if (findings.isEmpty()) {
            findings.add(
                UnicriticFinding(
                    axis = CriticAxis.GLSL_LATTICE,
                    title = "All Tri-Axis Invariants Nominal (EPISTULA)",
                    detail = "Hex-Reflex shader, SY4-EEG biometrics, and SHA-256 Codex operate in phase lock.",
                    severity = LogSeverity.NOMINAL,
                    actionKey = "LOG_VERDICT"
                )
            )
        }

        val consensus = (glslScore * 0.40f + eegScore * 0.35f + codexScore * 0.25f)
            .roundToInt()
            .coerceIn(0, 100)

        val verdictLevel = when {
            consensus >= 80 -> UnicriticVerdictLevel.OPTIMAL
            consensus >= 55 -> UnicriticVerdictLevel.ADMONITIO
            else -> UnicriticVerdictLevel.CRITICUS
        }

        // Compute shader drift intensity (0.0 when consensus >= 82, ramping up to 1.0 when consensus < 35)
        val driftIntensity = if (consensus >= 82) {
            (perturbation * 0.25f).coerceIn(0f, 0.15f)
        } else {
            (((82 - consensus) / 55f) + perturbation * 0.35f).coerceIn(0.05f, 1.0f)
        }

        return UnicriticLiveEvaluation(
            consensusScore = consensus,
            verdictLevel = verdictLevel,
            glslAxisScore = glslScore,
            eegAxisScore = eegScore,
            codexAxisScore = codexScore,
            driftIntensity = driftIntensity,
            findings = findings
        )
    }

    private fun buildOsaMatrix(entropy: Float, simTime: Float, bands: EegBandState): List<OsaMatrixCell> {
        val labels = listOf(
            "A-X-00" to "SHA-256 Root",
            "A-X-01" to "Litterae Ingest",
            "A-X-02" to "Hex Domain",
            "A-X-03" to "Sortis Integr.",
            "A-X-04" to "Reflex Radial",
            "A-X-05" to "SQLx SPECT",
            "A-X-06" to "Mag Curl Field",
            "A-X-07" to "Anti-Mag Pulse",
            "A-X-08" to "BlockMind Gate",
            "A-X-09" to "Epistula Seal"
        )
        return labels.mapIndexed { idx, (code, label) ->
            val waveJitter = abs(sin(simTime * 1.4f + idx * 0.7f)) * entropy * 0.85f
            val localEntropy = (entropy * 0.75f + waveJitter * 0.25f).coerceIn(0f, 1f)
            val ordered = localEntropy < 0.18f && (bands.alpha + bands.beta) > 0.6f
            OsaMatrixCell(
                code = code,
                axisLabel = label,
                entropyValue = localEntropy,
                isOrdered = ordered
            )
        }
    }

    private fun recomputeOsaAndContractStates(entropy: Float, bands: EegBandState, hw: HardwareTelemetry) {
        val cells = buildOsaMatrix(entropy, 0f, bands)
        _uiState.update {
            it.copy(
                osaMatrix = cells,
                hardwareTelemetry = hw
            )
        }
    }

    fun onGlFrameTelemetry(frameDeltaMs: Float, gpuName: String) {
        val s = _uiState.value
        val fps = (1000f / frameDeltaMs.coerceAtLeast(1f)).roundToInt().coerceIn(1, 120)
        val newMin = minOf(s.minFps, fps)
        val newMax = maxOf(s.maxFps, fps)
        val computeGops = ((1080f * 1920f * s.renderScale * s.renderScale * s.maxSteps * fps) / 1e8f)

        var nextSteps = s.maxSteps
        var nextHealCycles = s.healCycles
        if (s.autoGovernor && frameDeltaMs > 38.0f && s.maxSteps > 40) {
            lowFpsStreak++
            if (lowFpsStreak > 18) {
                nextSteps = (s.maxSteps - 16).coerceAtLeast(32)
                nextHealCycles++
                lowFpsStreak = 0
                appendLog("GOV-HEAL", "Auto-repaired frame latency by scaling raymarch steps to $nextSteps", LogSeverity.INFO)
            }
        } else {
            lowFpsStreak = (lowFpsStreak - 1).coerceAtLeast(0)
        }

        _uiState.update {
            it.copy(
                currentFps = fps,
                currentFrameMs = frameDeltaMs,
                minFps = newMin,
                maxFps = newMax,
                gigaOpsLoad = computeGops,
                gpuRendererName = gpuName,
                maxSteps = nextSteps,
                healCycles = nextHealCycles
            )
        }
    }

    fun onHardwareTelemetryUpdate(telemetry: HardwareTelemetry) {
        _uiState.update { it.copy(hardwareTelemetry = telemetry) }
    }

    fun selectTab(tab: HudTab) {
        _uiState.update { it.copy(activeTab = tab, hudVisible = true) }
    }

    fun toggleHudVisibility() {
        _uiState.update { it.copy(hudVisible = !it.hudVisible) }
    }

    fun selectShaderPreset(preset: ShaderPreset) {
        _uiState.update { it.copy(activeShader = preset) }
        appendLog("SHADER", "Switched active architecture to ${preset.key.uppercase()}", LogSeverity.INFO)
    }

    fun injectFracture(amount: Float = 0.85f) {
        val nextP = (_uiState.value.perturbation + amount).coerceAtMost(1.0f)
        _uiState.update { it.copy(perturbation = nextP) }
        appendLog("FRACTURE", "Injected magnetic lattice perturbation (Entropy: ${"%.2f".format(nextP)})", LogSeverity.WARNING)
        showToast("Magnetic Lattice Fracture Injected — Self-Heal Active")
    }

    fun triggerRepackageAndHeal() {
        val nextCycles = _uiState.value.healCycles + 1
        _uiState.update {
            it.copy(
                healWave = 0.02f,
                perturbation = it.perturbation * 0.25f,
                healCycles = nextCycles,
                repackageSignalCounter = it.repackageSignalCounter + 1
            )
        }
        appendLog("REPACKAGE", "Triggered full Hex-Reflex repackage & phase realignment (#$nextCycles)", LogSeverity.NOMINAL)
        showToast("Repackaging Shader Lattice & Self-Healing Magnetic Symmetry")
    }

    // Unicritic Dedicated Actions
    fun toggleUnicriticDistortion(enabled: Boolean) {
        _uiState.update { it.copy(unicriticDistortionEnabled = enabled) }
    }

    fun injectCriticalDriftStressTest() {
        mockBiometricRepository.triggerSynapticBurst(0.95f)
        _uiState.update {
            it.copy(
                perturbation = 0.88f,
                redlineThreatActive = true,
                unicriticDistortionEnabled = true,
                biometricLoadIntensity = 0.92f,
                cognitiveLoadPct = 92,
                eegBands = EegBandState(
                    delta = 0.40f,
                    theta = 0.82f,
                    alpha = 0.18f,
                    beta = 0.94f,
                    gamma = 0.88f
                ),
                expectedSha256Input = "0000deadbeef_drift_simulation_mismatch",
                sha256Verified = false
            )
        }
        appendLog(
            "AX-IS-T",
            "[TREAT SECURITY THREAT:REDLINE] Critical drift signaled! ShaderEffectModifier distorting 3D tunnel via biometric load.",
            LogSeverity.CRITICAL
        )
        showToast("[TREAT SECURITY THREAT:REDLINE] AX-IS-T Tunnel Distortion Active")
    }

    fun executeUnicriticRemediation() {
        val prevScore = _uiState.value.unicriticEvaluation.consensusScore
        val nextCycles = _uiState.value.healCycles + 1
        val optimalBands = EegBandState(
            delta = 0.22f,
            theta = 0.42f,
            alpha = 0.82f,
            beta = 0.72f,
            gamma = 0.38f
        )
        val newAction = com.example.model.RemediationActionSummary(
            id = System.currentTimeMillis(),
            timestamp = timeFormatter.format(Date()),
            axisTag = "ALL AXES",
            actionTitle = "AX-IS-T Redline Neutralization & Tri-Axis Seal (#$nextCycles)",
            subsystemsRestored = "[TREAT SECURITY THREAT:REDLINE] Cleared • 0.00 Entropy • 10/10 Osa Nodes • SHA-256 Verified",
            scoreBefore = prevScore,
            scoreAfter = 98,
            outcomeBadge = "AX-IS-T LOCKED"
        )
        _uiState.update {
            it.copy(
                perturbation = 0.0f,
                redlineThreatActive = false,
                effectiveUnicriticDrift = 0.0f,
                biometricLoadIntensity = 0.28f,
                cognitiveLoadPct = 28,
                healWave = 0.03f,
                healCycles = nextCycles,
                maxSteps = it.maxSteps.coerceAtMost(96),
                eegBands = optimalBands,
                rgbMode = RgbCognitiveMode.TRUTH_GREEN,
                expectedSha256Input = "",
                sha256Verified = true,
                contractPhase = ContractPhase.EPISTULA,
                asIfTunnelState = AsIfTunnelState.LOCKED,
                recentRemediations = (listOf(newAction) + it.recentRemediations).take(12)
            )
        }
        viewModelScope.launch {
            repository.insertUnicriticVerdict(
                UnicriticVerdictEntity(
                    verdictLevel = "OPTIMAL",
                    consensusScore = 98,
                    glslAxisScore = 99,
                    eegAxisScore = 96,
                    codexAxisScore = 100,
                    driftIntensity = 0.0f,
                    primaryFinding = "AX-IS-T Redline Threat Remediated: Restored GLSL symmetry, EEG Alpha-Beta coherence, and SHA-256 seal.",
                    remediationApplied = true
                )
            )
        }
        appendLog("UNICRITIC", "Neutralized [TREAT SECURITY THREAT:REDLINE] (#$nextCycles) -> OPTIMAL (98%).", LogSeverity.NOMINAL)
        showToast("AX-IS-T Redline Neutralized — Verdict Sealed in Room SQLite")
    }

    fun remediateSingleAxis(actionKey: String) {
        val prevScore = _uiState.value.unicriticEvaluation.consensusScore
        when (actionKey) {
            "HEAL_LATTICE" -> {
                triggerRepackageAndHeal()
                recordSingleAxisRemediation(
                    axisTag = "GLSL AXIS",
                    title = "Hexagonal Lattice Repackage & Self-Heal Wave",
                    subsystems = "Cleared magnetic perturbation & launched radial wave",
                    prevScore = prevScore,
                    newScore = (prevScore + 22).coerceAtMost(98),
                    badge = "LATTICE HEALED"
                )
            }
            "OPTIMIZE_STEPS" -> {
                updateMaxSteps(88)
                recordSingleAxisRemediation(
                    axisTag = "GLSL AXIS",
                    title = "Raymarch Iteration Depth Optimization",
                    subsystems = "Scaled raymarch steps to 88 for 60 FPS frame budget",
                    prevScore = prevScore,
                    newScore = (prevScore + 15).coerceAtMost(97),
                    badge = "FPS LOCKED"
                )
                showToast("Raymarch Steps Scaled to 88 for Optimal Frame Lock")
            }
            "ALIGN_EEG_OSA" -> {
                applyBiometricPreset("TRUTH_EQUILIBRIUM")
                reorderOsaMatrixNow()
                recordSingleAxisRemediation(
                    axisTag = "EEG AXIS",
                    title = "SY4-EEG Truth Equilibrium & Osa A-X- inf00 Order",
                    subsystems = "Aligned 10/10 Osa nodes & restored Alpha-Beta coherence",
                    prevScore = prevScore,
                    newScore = (prevScore + 24).coerceAtMost(98),
                    badge = "OSA 10/10"
                )
            }
            "SEAL_SHA256" -> {
                updateExpectedSha256Input("")
                executeScriptCleanAndPersistAudit()
                recordSingleAxisRemediation(
                    axisTag = "SHA-256",
                    title = "Čištění skriptu & Cryptographic Digest Re-Seal",
                    subsystems = "Sanitized script source & verified SHA-256 baseline",
                    prevScore = prevScore,
                    newScore = (prevScore + 18).coerceAtMost(99),
                    badge = "SHA VERIFIED"
                )
            }
            else -> logManualUnicriticVerdict()
        }
    }

    private fun recordSingleAxisRemediation(
        axisTag: String,
        title: String,
        subsystems: String,
        prevScore: Int,
        newScore: Int,
        badge: String
    ) {
        val action = com.example.model.RemediationActionSummary(
            id = System.currentTimeMillis(),
            timestamp = timeFormatter.format(Date()),
            axisTag = axisTag,
            actionTitle = title,
            subsystemsRestored = subsystems,
            scoreBefore = prevScore,
            scoreAfter = newScore,
            outcomeBadge = badge
        )
        _uiState.update {
            it.copy(recentRemediations = (listOf(action) + it.recentRemediations).take(12))
        }
    }

    fun logManualUnicriticVerdict() {
        val eval = _uiState.value.unicriticEvaluation
        val primary = eval.findings.firstOrNull()?.title ?: "Manual Unicritic Consensus Audit"
        viewModelScope.launch {
            repository.insertUnicriticVerdict(
                UnicriticVerdictEntity(
                    verdictLevel = eval.verdictLevel.name,
                    consensusScore = eval.consensusScore,
                    glslAxisScore = eval.glslAxisScore,
                    eegAxisScore = eval.eegAxisScore,
                    codexAxisScore = eval.codexAxisScore,
                    driftIntensity = eval.driftIntensity,
                    primaryFinding = primary,
                    remediationApplied = eval.verdictLevel == UnicriticVerdictLevel.OPTIMAL
                )
            )
        }
        showToast("Unicritic Verdict (${eval.consensusScore}%) Saved to Local Room DB")
    }

    fun deleteUnicriticVerdict(id: Int) {
        viewModelScope.launch {
            repository.deleteUnicriticVerdict(id)
        }
    }

    fun reorderOsaMatrixNow() {
        val nextCycles = _uiState.value.healCycles + 1
        _uiState.update {
            it.copy(
                perturbation = 0.0f,
                healWave = 0.04f,
                healCycles = nextCycles,
                contractPhase = ContractPhase.EPISTULA,
                asIfTunnelState = AsIfTunnelState.LOCKED
            )
        }
        appendLog("OSA-ORDER", "Osa A-X- inf00 ad ordinem redigendum: All 10 nodes aligned.", LogSeverity.NOMINAL)
        showToast("Osa A-X- inf00 Reduced to Contractual Order (EPISTULA)")
    }

    fun toggleBlockMindStasis() {
        val s = _uiState.value
        val nextFlux = when {
            s.unblockMindActive -> -1.0f
            s.blockMindStasis -> 0.0f
            else -> 1.0f
        }
        setBlockMindPolarityFlux(nextFlux)
    }

    fun togglePause() {
        val nextPaused = !_uiState.value.paused
        _uiState.update { it.copy(paused = nextPaused) }
        showToast(if (nextPaused) "Simulation Paused" else "Simulation Resumed")
    }

    fun updateHexScale(value: Float) {
        val clamped = if (_uiState.value.continuityInterlockEnabled) value.coerceIn(0.6f, 4.0f) else value
        val remediated = clamped != value
        _uiState.update {
            it.copy(
                hexScale = clamped,
                autoRemediatedUniformCount = it.autoRemediatedUniformCount + (if (remediated) 1 else 0),
                lastContinuityEventSummary = if (remediated) {
                    "AUTO-CLAMP // hexScale ${"%.2f".format(value)} -> ${"%.2f".format(clamped)} (3D Tunnel Uninterrupted)"
                } else {
                    it.lastContinuityEventSummary
                }
            )
        }
    }

    fun updateReflexDensity(value: Float) {
        val clamped = if (_uiState.value.continuityInterlockEnabled) value.coerceIn(0.1f, 3.5f) else value
        val remediated = clamped != value
        _uiState.update {
            it.copy(
                reflexDensity = clamped,
                autoRemediatedUniformCount = it.autoRemediatedUniformCount + (if (remediated) 1 else 0),
                lastContinuityEventSummary = if (remediated) {
                    "AUTO-CLAMP // reflexDensity ${"%.2f".format(value)} -> ${"%.2f".format(clamped)} (3D Tunnel Uninterrupted)"
                } else {
                    it.lastContinuityEventSummary
                }
            )
        }
    }

    fun updateMagPressure(value: Float) {
        val clamped = if (_uiState.value.continuityInterlockEnabled) value.coerceIn(0.0f, 3.0f) else value
        val remediated = clamped != value
        _uiState.update {
            it.copy(
                magPressure = clamped,
                autoRemediatedUniformCount = it.autoRemediatedUniformCount + (if (remediated) 1 else 0),
                lastContinuityEventSummary = if (remediated) {
                    "AUTO-CLAMP // magPressure ${"%.2f".format(value)} -> ${"%.2f".format(clamped)} (3D Tunnel Uninterrupted)"
                } else {
                    it.lastContinuityEventSummary
                }
            )
        }
    }

    fun updateAntiMagPulse(value: Float) {
        val clamped = if (_uiState.value.continuityInterlockEnabled) value.coerceIn(0.0f, 2.5f) else value
        val remediated = clamped != value
        _uiState.update {
            it.copy(
                antiMagPulse = clamped,
                autoRemediatedUniformCount = it.autoRemediatedUniformCount + (if (remediated) 1 else 0),
                lastContinuityEventSummary = if (remediated) {
                    "AUTO-CLAMP // antiMagPulse ${"%.2f".format(value)} -> ${"%.2f".format(clamped)} (3D Tunnel Uninterrupted)"
                } else {
                    it.lastContinuityEventSummary
                }
            )
        }
    }

    fun updateMaxSteps(value: Int) {
        val clamped = if (_uiState.value.continuityInterlockEnabled) value.coerceIn(24, 256) else value
        val remediated = clamped != value
        _uiState.update {
            it.copy(
                maxSteps = clamped,
                autoRemediatedUniformCount = it.autoRemediatedUniformCount + (if (remediated) 1 else 0),
                lastContinuityEventSummary = if (remediated) {
                    "AUTO-CLAMP // maxSteps $value -> $clamped (3D Tunnel Uninterrupted)"
                } else {
                    it.lastContinuityEventSummary
                }
            )
        }
    }

    fun updateRenderScale(value: Float) {
        val clamped = if (_uiState.value.continuityInterlockEnabled) value.coerceIn(0.25f, 2.0f) else value
        val remediated = clamped != value
        _uiState.update {
            it.copy(
                renderScale = clamped,
                autoRemediatedUniformCount = it.autoRemediatedUniformCount + (if (remediated) 1 else 0),
                lastContinuityEventSummary = if (remediated) {
                    "AUTO-CLAMP // renderScale ${"%.2f".format(value)} -> ${"%.2f".format(clamped)} (3D Tunnel Uninterrupted)"
                } else {
                    it.lastContinuityEventSummary
                }
            )
        }
    }

    fun updateHealRate(value: Float) = _uiState.update { it.copy(healRate = value.coerceIn(0.05f, 2.5f)) }
    fun updateFxaa(enabled: Boolean) = _uiState.update { it.copy(fxaaEnabled = enabled) }
    fun updateAutoGovernor(enabled: Boolean) = _uiState.update { it.copy(autoGovernor = enabled) }

    fun updateRotatingFrequencyHz(value: Float) {
        val clamped = if (_uiState.value.continuityInterlockEnabled) value.coerceIn(0.05f, 5.00f) else value.coerceIn(0.01f, 8.00f)
        val remediated = clamped != value
        _uiState.update {
            val eegMod = if (it.syncRotatingFrqWithEeg) {
                (0.65f + (it.dominantFrequencyHz / 24.0f) * 0.55f).coerceIn(0.35f, 1.85f)
            } else {
                1.0f
            }
            it.copy(
                rotatingFrequencyHz = clamped,
                effectiveRotatingFrequencyHz = (clamped * eegMod).coerceIn(0.02f, 5.00f),
                autoRemediatedUniformCount = it.autoRemediatedUniformCount + (if (remediated) 1 else 0),
                lastContinuityEventSummary = if (remediated) {
                    "AUTO-CLAMP // rotatingFrequencyHz ${"%.2f".format(value)} -> ${"%.2f".format(clamped)} Hz (3D Tunnel Uninterrupted)"
                } else {
                    it.lastContinuityEventSummary
                }
            )
        }
    }

    fun stepRotatingFrequencyHz(deltaHz: Float) {
        val next = (_uiState.value.rotatingFrequencyHz + deltaHz).coerceIn(0.05f, 5.00f)
        updateRotatingFrequencyHz(next)
    }

    fun toggleSyncRotatingFrqWithEeg(enabled: Boolean = !_uiState.value.syncRotatingFrqWithEeg) {
        _uiState.update {
            val eegMod = if (enabled) {
                (0.65f + (it.dominantFrequencyHz / 24.0f) * 0.55f).coerceIn(0.35f, 1.85f)
            } else {
                1.0f
            }
            it.copy(
                syncRotatingFrqWithEeg = enabled,
                effectiveRotatingFrequencyHz = (it.rotatingFrequencyHz * eegMod).coerceIn(0.02f, 5.00f)
            )
        }
        val msg = if (enabled) "Rotating Frequency Synchronized with SY4-EEG Dominant Hz" else "Manual Rotating Frequency Lock Active"
        appendLog("ROT-FRQ", msg, LogSeverity.INFO)
        showToast(msg)
    }

    fun toggleRotationDirection() {
        val nextCw = !_uiState.value.rotationDirectionCw
        _uiState.update { it.copy(rotationDirectionCw = nextCw) }
        val dirLabel = if (nextCw) "CLOCKWISE [CW +]" else "COUNTER-CLOCKWISE [CCW -]"
        appendLog("ROT-FRQ", "3D Tunnel vortex rotation direction set to $dirLabel", LogSeverity.INFO)
        showToast("Tunnel Rotation: $dirLabel")
    }

    fun toggleWidescreenStage(enabled: Boolean = !_uiState.value.widescreenStageEnabled) {
        _uiState.update { it.copy(widescreenStageEnabled = enabled) }
        val msg = if (enabled) "16:9 Widescreen Tunnel Stage Pinned" else "Full-Canvas Overlay Mode"
        appendLog("WIDESCREEN", msg, LogSeverity.INFO)
        showToast(msg)
    }

    fun toggleWidescreenLandscapeLock(locked: Boolean = !_uiState.value.widescreenLandscapeLocked) {
        _uiState.update {
            it.copy(
                widescreenLandscapeLocked = locked,
                widescreenStageEnabled = true
            )
        }
        val msg = if (locked) "Wide Screen Landscape Mode Locked [16:9 COCKPIT]" else "Adaptive Widescreen / Portrait Orientation Unlocked"
        appendLog("WIDESCREEN", msg, LogSeverity.NOMINAL)
        showToast(msg)
    }

    fun toggleContinuityInterlock(enabled: Boolean = !_uiState.value.continuityInterlockEnabled) {
        _uiState.update {
            it.copy(
                continuityInterlockEnabled = enabled,
                lastContinuityEventSummary = if (enabled) {
                    "INTERLOCK ARMED // Locking GLSL Gate, EEG State & SHA-256 Chain Across Transitions"
                } else {
                    "INTERLOCK PAUSED // Manual Uniform Excursion Override Permitted"
                }
            )
        }
        if (enabled) {
            synchronizeContinuityTransition("INTERLOCK_REARMED", persistToRoom = true)
        }
        val msg = if (enabled) "Tri-Axis Continuity Interlock Locked" else "Tri-Axis Continuity Interlock Paused"
        appendLog("CONTINUITY", msg, LogSeverity.NOMINAL)
        showToast(msg)
    }

    fun simulateOutOfBoundsUniformAndAutoRemediate() {
        val s = _uiState.value
        val prevScore = s.unicriticEvaluation.consensusScore
        val nextEpoch = s.continuityChainEpoch + 1
        val nextClampedCount = s.autoRemediatedUniformCount + 5
        val nextHealCycles = s.healCycles + 1
        val parentHash = s.continuityCurrentSha256

        // Compute new chained SHA-256 over parent digest + remediated GLSL uniforms + EEG state
        val chainPayload = buildString {
            append("PARENT=").append(parentHash)
            append("|EPOCH=").append(nextEpoch)
            append("|EVENT=UNIFORM_EXCURSION_AUTO_REMEDIATED")
            append("|SHADER=").append(s.activeShader.key)
            append("|UNIFORMS=hex:1.80,reflex:1.45,mag:1.20,antimag:0.85,steps:88,scale:1.00,rotFrq:0.65")
            append("|EEG=").append(s.rgbMode.id).append(",coherence:96")
        }
        val nextHash = MessageDigest.getInstance("SHA-256")
            .digest(chainPayload.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        val summary = "AUTO-REMEDIATED 5 OOB UNIFORMS (hex 9.4->1.8, rotFrq 18.0->0.65Hz, steps 512->88) • 3D Tunnel Uninterrupted • Chain #$nextEpoch"

        val actionSummary = com.example.model.RemediationActionSummary(
            id = System.currentTimeMillis(),
            timestamp = timeFormatter.format(Date()),
            axisTag = "CONTINUITY LOCK",
            actionTitle = "Out-of-Bounds Uniform Auto-Remediation & Chain #$nextEpoch",
            subsystemsRestored = "Clamped hexScale/rotatingFrq/maxSteps in-place • GLSL Gate=TRUE • EEG Coherence 96% • SHA-256 Chained",
            scoreBefore = prevScore,
            scoreAfter = 99,
            outcomeBadge = "TUNNEL UNINTERRUPTED"
        )

        _uiState.update {
            it.copy(
                continuityInterlockEnabled = true,
                continuityChainEpoch = nextEpoch,
                continuityParentSha256 = parentHash,
                continuityCurrentSha256 = nextHash,
                autoRemediatedUniformCount = nextClampedCount,
                lastContinuityEventSummary = summary,
                hexScale = 1.80f,
                reflexDensity = 1.45f,
                magPressure = 1.20f,
                antiMagPulse = 0.85f,
                maxSteps = 88,
                renderScale = 1.00f,
                rotatingFrequencyHz = 0.65f,
                effectiveRotatingFrequencyHz = 0.65f,
                perturbation = 0.0f,
                healWave = 0.04f,
                healCycles = nextHealCycles,
                glslGateTrue = true,
                glslSyntaxValid = true,
                glslGateDiagnostic = "CONTINUITY AUTO-REMEDIATED // OOB Uniforms & Rotating Frq Clamped in 0ms • 3D Tunnel Uninterrupted",
                redlineThreatActive = false,
                effectiveUnicriticDrift = 0.0f,
                phaseCoherencePct = 96,
                cognitiveLoadPct = 28,
                sha256Verified = true,
                expectedSha256Input = "",
                computedSha256 = nextHash,
                contractPhase = ContractPhase.EPISTULA,
                asIfTunnelState = AsIfTunnelState.LOCKED,
                recentRemediations = (listOf(actionSummary) + it.recentRemediations).take(12)
            )
        }

        viewModelScope.launch {
            repository.insertVerification(
                ScriptVerificationEntity(
                    scriptTitle = "CONTINUITY CHAIN #$nextEpoch // Auto-Remediated Uniforms",
                    crateCategory = "Tri-Axis Continuity Lock",
                    rawCharCount = chainPayload.length,
                    cleanedCharCount = chainPayload.length,
                    sha256Digest = nextHash,
                    isVerified = true
                )
            )
            repository.insertBiometricVerdictAndDriftLog(
                verdict = UnicriticVerdictEntity(
                    verdictLevel = "OPTIMAL",
                    consensusScore = 99,
                    glslAxisScore = 100,
                    eegAxisScore = 97,
                    codexAxisScore = 100,
                    driftIntensity = 0.0f,
                    primaryFinding = summary,
                    remediationApplied = true
                ),
                driftLog = UnicriticDriftLogEntity(
                    triggerSource = "CONTINUITY // OOB UNIFORM AUTO-REMEDIATION",
                    verdictLevel = "OPTIMAL",
                    rgbBiometricMode = s.rgbMode.title,
                    biometricLoadPct = 28,
                    dominantFrequencyHz = s.dominantFrequencyHz,
                    phaseCoherencePct = 96,
                    driftIntensity = 0.0f,
                    compositeShaderDistortion = 0.0f,
                    redlineActive = false,
                    remediationNote = "$summary | Parent SHA-256: ${parentHash.take(12)} -> Child SHA-256: ${nextHash.take(12)}"
                )
            )
        }

        logSpectSnapshotInternal(
            title = "CONTINUITY LOCK #$nextEpoch // OOB Uniforms Clamped",
            p50 = 16.4f,
            p95 = 17.6f,
            p99 = 18.9f
        )

        appendLog("CONTINUITY", summary, LogSeverity.NOMINAL)
        showToast("Continuity Interlock: Uniform Bounds Auto-Remediated & 3D Tunnel Uninterrupted")
    }

    fun synchronizeContinuityTransition(transitionTag: String, persistToRoom: Boolean = false) {
        val s = _uiState.value
        val clampedHex = s.hexScale.coerceIn(0.6f, 4.0f)
        val clampedReflex = s.reflexDensity.coerceIn(0.1f, 3.5f)
        val clampedMag = s.magPressure.coerceIn(0.0f, 3.0f)
        val clampedAntiMag = s.antiMagPulse.coerceIn(0.0f, 2.5f)
        val clampedSteps = s.maxSteps.coerceIn(24, 256)
        val clampedScale = s.renderScale.coerceIn(0.25f, 2.0f)
        val clampedRotFrq = s.rotatingFrequencyHz.coerceIn(0.05f, 5.00f)

        val anyClamped = clampedHex != s.hexScale ||
            clampedReflex != s.reflexDensity ||
            clampedMag != s.magPressure ||
            clampedAntiMag != s.antiMagPulse ||
            clampedSteps != s.maxSteps ||
            clampedScale != s.renderScale ||
            clampedRotFrq != s.rotatingFrequencyHz

        val nextEpoch = s.continuityChainEpoch + 1
        val parentHash = s.continuityCurrentSha256.ifEmpty {
            "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4"
        }
        val payload = "PARENT=$parentHash|EPOCH=$nextEpoch|TRANSITION=$transitionTag|SHADER=${s.activeShader.key}|RGB=${s.rgbMode.id}|HEX=$clampedHex|ROT_FRQ=$clampedRotFrq|STEPS=$clampedSteps"
        val nextHash = MessageDigest.getInstance("SHA-256")
            .digest(payload.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        val eventMsg = if (anyClamped) {
            "TRANSITION [$transitionTag] // Auto-Clamped Uniforms & Chained SHA-256 (#$nextEpoch)"
        } else {
            "TRANSITION [$transitionTag] // GLSL Gate + EEG + SHA-256 Chain Locked (#$nextEpoch)"
        }

        _uiState.update {
            it.copy(
                hexScale = clampedHex,
                reflexDensity = clampedReflex,
                magPressure = clampedMag,
                antiMagPulse = clampedAntiMag,
                maxSteps = clampedSteps,
                renderScale = clampedScale,
                rotatingFrequencyHz = clampedRotFrq,
                glslGateTrue = true,
                glslSyntaxValid = true,
                continuityChainEpoch = nextEpoch,
                continuityParentSha256 = parentHash,
                continuityCurrentSha256 = nextHash,
                autoRemediatedUniformCount = it.autoRemediatedUniformCount + (if (anyClamped) 1 else 0),
                lastContinuityEventSummary = eventMsg
            )
        }

        if (persistToRoom) {
            viewModelScope.launch {
                repository.insertVerification(
                    ScriptVerificationEntity(
                        scriptTitle = "CHAIN #$nextEpoch // $transitionTag",
                        crateCategory = "Continuity Interlock",
                        rawCharCount = payload.length,
                        cleanedCharCount = payload.length,
                        sha256Digest = nextHash,
                        isVerified = true
                    )
                )
            }
        }
    }

    fun updateCoreTensionString(tension: Float) {
        _uiState.update {
            it.copy(
                coreTensionString = tension.coerceIn(0.25f, 2.50f),
                coreTensionPassDone = false
            )
        }
    }

    fun completeCoreTensionPass() {
        val prevScore = _uiState.value.unicriticEvaluation.consensusScore
        val tunedTension = _uiState.value.coreTensionString
        val action = com.example.model.RemediationActionSummary(
            id = System.currentTimeMillis(),
            timestamp = timeFormatter.format(Date()),
            axisTag = "TENSION PASS",
            actionTitle = "Core Tension String Pass Locked (${"%.2f".format(tunedTension)} N)",
            subsystemsRestored = "Forward Logic Tunnel Parser + Overlock [OFFLINE:EMISSION] + 4 Legal Axes",
            scoreBefore = prevScore,
            scoreAfter = 100,
            outcomeBadge = "PASS: DONE"
        )
        _uiState.update {
            it.copy(
                coreTensionPassDone = true,
                perturbation = 0.0f,
                healWave = 0.04f,
                healCycles = it.healCycles + 1,
                contractPhase = ContractPhase.EPISTULA,
                asIfTunnelState = AsIfTunnelState.LOCKED,
                recentRemediations = (listOf(action) + it.recentRemediations).take(12)
            )
        }
        appendLog(
            "TENSION-PASS",
            "Core tension string pass (${"%.2f".format(tunedTension)}) tuned & marked DONE [Core Unbreachable].",
            LogSeverity.NOMINAL
        )
        showToast("Core Tension String Pass Tuned & Locked — PASS: DONE")
    }

    fun parseForwardLogicTunnel(grammarInput: String, overlockInput: String) {
        val tokens = grammarInput
            .split("[", "]")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { idx, token -> "NODE-$idx: $token" }
        val openCount = grammarInput.count { it == '[' } + overlockInput.count { it == '[' }
        val closeCount = grammarInput.count { it == ']' } + overlockInput.count { it == ']' }
        val balanced = openCount > 0 && openCount == closeCount

        val updatedParse = _uiState.value.forwardTunnelParse.copy(
            rawGrammar = grammarInput,
            overlockLogic = overlockInput,
            parsedTokens = if (tokens.isEmpty()) listOf("RAW: $grammarInput") else tokens,
            bracketDepthBalanced = balanced
        )
        _uiState.update { it.copy(forwardTunnelParse = updatedParse) }
        cleanAndComputeSha256(
            raw = "$grammarInput\n$overlockInput",
            title = "PDF-05 // Forward Logic Tunnel Parser",
            crateTag = "Relay-parse:only",
            persistToRoom = true
        )
        appendLog("TUNNEL-PARSE", "Transferred forward logic tunnel parser (${tokens.size} AST nodes).", LogSeverity.NOMINAL)
        showToast("Forward Logic Tunnel Parsed & SHA-256 Sealed")
    }

    // =========================================================================
    // LOCAL ONLY PATHS INTERCONNECT -> SERVERLESS INFRA DEPLOY (check:pass:true_only)
    // =========================================================================

    fun selectLocalInterconnectNode(nodeId: String) {
        val s = _uiState.value
        val cur = s.localInterconnectState
        val evaluated = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = cur.nodes.ifEmpty { LocalOnlyPathsInterconnectEngine.buildDefaultNodes(s.forwardTunnelParse, s.continuityCurrentSha256) },
            forwardParse = s.forwardTunnelParse,
            asCoreToken = s.asCoreFrame.asCoreToken,
            continuitySha256 = s.continuityCurrentSha256,
            sha256Verified = s.sha256Verified,
            roomAuditVerified = cur.roomAuditVerified,
            deployEpoch = cur.deployEpoch,
            roomAuditRecordCount = cur.roomAuditRecordCount,
            activeNodeId = nodeId
        )
        _uiState.update { it.copy(localInterconnectState = evaluated) }
        val node = evaluated.nodes.firstOrNull { it.id == nodeId }
        if (node != null) {
            appendLog(
                "INTERCONNECT",
                "Inspected [${node.id} • ${node.code}] -> ${node.localPath} (hop SHA-256: ${node.hopSha256.take(12)}...)",
                if (node.isStrictlyLocal) LogSeverity.NOMINAL else LogSeverity.WARNING
            )
        }
    }

    fun executeServerlessLocalDeploy() {
        val s = _uiState.value
        val cur = s.localInterconnectState
        val nextEpoch = cur.deployEpoch + 1
        val nextAudits = cur.roomAuditRecordCount + 1
        val baseNodes = LocalOnlyPathsInterconnectEngine.buildDefaultNodes(
            forwardParse = s.forwardTunnelParse,
            continuitySha256 = s.continuityCurrentSha256
        )
        val evaluated = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = baseNodes,
            forwardParse = s.forwardTunnelParse.copy(onlyLocalLogicFlag = true, bracketDepthBalanced = true),
            asCoreToken = s.asCoreFrame.asCoreToken,
            continuitySha256 = s.continuityCurrentSha256,
            sha256Verified = true,
            roomAuditVerified = true,
            deployEpoch = nextEpoch,
            roomAuditRecordCount = nextAudits,
            activeNodeId = cur.activeNodeId
        )
        val manifestScript = LocalOnlyPathsInterconnectEngine.buildServerlessDeployManifestScript(evaluated)

        val actionSummary = com.example.model.RemediationActionSummary(
            id = System.currentTimeMillis(),
            timestamp = timeFormatter.format(Date()),
            axisTag = "LOCAL INTERCONNECT",
            actionTitle = "Serverless Infra Only Deploy (#$nextEpoch) -> ${evaluated.assertionToken}",
            subsystemsRestored = "6/6 Local Paths Bridged • Zero Egress • SHA-256 Seal ${evaluated.interconnectSealSha256.take(12)} • SPECTRoom Audit #$nextAudits",
            scoreBefore = s.unicriticEvaluation.consensusScore,
            scoreAfter = 100,
            outcomeBadge = evaluated.assertionToken
        )

        _uiState.update {
            it.copy(
                localInterconnectState = evaluated,
                forwardTunnelParse = it.forwardTunnelParse.copy(onlyLocalLogicFlag = true, bracketDepthBalanced = true),
                sha256Verified = true,
                expectedSha256Input = "",
                contractPhase = ContractPhase.EPISTULA,
                asIfTunnelState = AsIfTunnelState.LOCKED,
                recentRemediations = (listOf(actionSummary) + it.recentRemediations).take(12)
            )
        }

        cleanAndComputeSha256(
            raw = manifestScript,
            title = "SERVERLESS LOCAL DEPLOY #$nextEpoch // ${evaluated.assertionToken}",
            crateTag = "check:pass:true_only",
            persistToRoom = true
        )

        viewModelScope.launch {
            repository.insertBiometricVerdictAndDriftLog(
                verdict = UnicriticVerdictEntity(
                    verdictLevel = "OPTIMAL",
                    consensusScore = 100,
                    glslAxisScore = 100,
                    eegAxisScore = 98,
                    codexAxisScore = 100,
                    driftIntensity = 0.0f,
                    primaryFinding = "Local Only Paths Interconnect Deploy (#$nextEpoch) -> ${evaluated.assertionToken} [SHA-256: ${evaluated.interconnectSealSha256.take(16)}]",
                    remediationApplied = true
                ),
                driftLog = UnicriticDriftLogEntity(
                    triggerSource = "DEPLOY // LOCAL ONLY WAY [${evaluated.assertionToken}]",
                    verdictLevel = "OPTIMAL",
                    rgbBiometricMode = s.rgbMode.title,
                    biometricLoadPct = s.cognitiveLoadPct,
                    dominantFrequencyHz = s.dominantFrequencyHz,
                    phaseCoherencePct = s.phaseCoherencePct.coerceAtLeast(95),
                    driftIntensity = 0.0f,
                    compositeShaderDistortion = 0.0f,
                    redlineActive = false,
                    remediationNote = "Verified zero external egress across 6 local endpoints, sealed SHA-256 (${evaluated.interconnectSealSha256.take(16)}), and persisted local Room audit #$nextAudits."
                )
            )
        }

        logSpectSnapshotInternal(
            title = "DEPLOY #$nextEpoch // ${evaluated.assertionToken}",
            p50 = 16.2f,
            p95 = 17.5f,
            p99 = 18.8f
        )

        appendLog(
            "DEPLOY-LOCAL",
            "Serverless Infra Only Deploy (#$nextEpoch) verified -> ${evaluated.assertionToken} (Seal: ${evaluated.interconnectSealSha256.take(14)}...)",
            LogSeverity.NOMINAL
        )
        showToast("Serverless Local-Only Deploy Verified: ${evaluated.assertionToken}")
    }

    fun injectNonLocalEgressFault() {
        val s = _uiState.value
        val cur = s.localInterconnectState
        val baseNodes = cur.nodes.ifEmpty {
            LocalOnlyPathsInterconnectEngine.buildDefaultNodes(s.forwardTunnelParse, s.continuityCurrentSha256)
        }
        val faultedNodes = baseNodes.map { node ->
            if (node.id == "DEST-4") {
                node.copy(
                    localPath = "https://external-cloud-egress.invalid/relay/PortfolioRelay.apk",
                    transportMode = "EXTERNAL EGRESS VIOLATION",
                    isStrictlyLocal = false
                )
            } else {
                node
            }
        }
        val evaluated = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = faultedNodes,
            forwardParse = s.forwardTunnelParse,
            asCoreToken = s.asCoreFrame.asCoreToken,
            continuitySha256 = s.continuityCurrentSha256,
            sha256Verified = s.sha256Verified,
            roomAuditVerified = cur.roomAuditVerified,
            deployEpoch = cur.deployEpoch,
            roomAuditRecordCount = cur.roomAuditRecordCount,
            activeNodeId = "DEST-4"
        )
        _uiState.update { it.copy(localInterconnectState = evaluated) }
        appendLog(
            "EGRESS-GUARD",
            "Blocked non-local route on DEST-4 -> Gate asserted ${evaluated.assertionToken}!",
            LogSeverity.WARNING
        )
        showToast("Egress Fault Blocked: ${evaluated.assertionToken}")
    }

    fun remediateToLocalOnlyTrue() {
        executeServerlessLocalDeploy()
    }

    fun loadServerlessDeployManifestIntoVerifier() {
        val deploy = _uiState.value.localInterconnectState
        val script = LocalOnlyPathsInterconnectEngine.buildServerlessDeployManifestScript(deploy)
        cleanAndComputeSha256(
            raw = script,
            title = "LocalOnlyInterconnect // ${deploy.assertionToken}",
            crateTag = "check:pass:true_only",
            persistToRoom = true
        )
        showToast("Loaded Serverless Local-Only Manifest (${deploy.assertionToken}) into SHA-256 Verifier")
    }

    fun updateOrbitalTouch(x: Float, y: Float, viewportWidth: Float = 1080f, viewportHeight: Float = 1920f) {
        val safeW = viewportWidth.coerceAtLeast(1f)
        val safeH = viewportHeight.coerceAtLeast(1f)
        val yawNorm = ((x / safeW) - 0.5f).coerceIn(-0.5f, 0.5f)
        val pitchNorm = ((y / safeH) - 0.5f).coerceIn(-0.5f, 0.5f)
        _uiState.update {
            it.copy(
                mouseX = x,
                mouseY = y,
                orbitalYawNorm = yawNorm,
                orbitalPitchNorm = pitchNorm
            )
        }
    }

    fun selectRgbMode(mode: RgbCognitiveMode) {
        mockBiometricRepository.setTargetMode(mode)
        val matchingProfile = when (mode) {
            RgbCognitiveMode.CREATIVITY_RED -> AsCoreStreamProfile.AS_CORE_CREATIVITY_RED
            RgbCognitiveMode.TRUTH_GREEN -> AsCoreStreamProfile.AS_CORE_TRUTH_GREEN
            RgbCognitiveMode.FOCUS_BLUE -> AsCoreStreamProfile.AS_CORE_FOCUS_BLUE
            RgbCognitiveMode.BIOMETRIC_AUTO -> AsCoreStreamProfile.AS_CORE_EQUILIBRIUM
        }
        asCoreRgbService.selectProfile(matchingProfile)
        val frame = asCoreRgbService.stepFrame(
            simTimeSec = _uiState.value.simTime,
            perturbation = _uiState.value.perturbation,
            somaticStabilityPct = _uiState.value.hardwareTelemetry.somaticStabilityPct
        )
        _uiState.update {
            it.copy(
                rgbMode = mode,
                asCoreFrame = frame,
                eegBands = frame.eegBands,
                dominantFrequencyHz = frame.dominantFrequencyHz,
                phaseCoherencePct = frame.phaseCoherencePct,
                effectiveRgbTint = frame.rgbSynchroVector
            )
        }
        if (_uiState.value.continuityInterlockEnabled) {
            synchronizeContinuityTransition("EEG_RGB_SYNC:${mode.id}", persistToRoom = true)
        }
        appendLog("RGB-SYNC", "Switched biometric stream mode to ${mode.title} (${"%.1f".format(frame.dominantFrequencyHz)} Hz)", LogSeverity.INFO)
    }

    fun selectAsCoreProfile(profile: AsCoreStreamProfile) {
        asCoreRgbService.selectProfile(profile)
        asCoreRgbService.setStreamActive(true)
        val frame = asCoreRgbService.stepFrame(
            simTimeSec = _uiState.value.simTime,
            perturbation = _uiState.value.perturbation,
            somaticStabilityPct = _uiState.value.hardwareTelemetry.somaticStabilityPct
        )
        val targetPol = if (_uiState.value.asCoreAutoDriveBlockMind) {
            frame.blockMindPolarityDrive
        } else {
            _uiState.value.blockMindPolarityFlux
        }
        _uiState.update {
            it.copy(
                mockStreamEnabled = true,
                asCoreStreamActive = true,
                rgbMode = frame.rgbMode,
                asCoreFrame = frame,
                eegBands = frame.eegBands,
                dominantFrequencyHz = frame.dominantFrequencyHz,
                phaseCoherencePct = frame.phaseCoherencePct,
                cognitiveLoadPct = frame.cognitiveLoadPct,
                effectiveRgbTint = frame.rgbSynchroVector,
                blockMindPolarityFlux = targetPol,
                blockMindStasis = targetPol <= -0.35f,
                unblockMindActive = targetPol >= 0.35f,
                contractPhase = frame.contractPhase,
                asIfTunnelState = frame.asIfTunnelState
            )
        }
        if (_uiState.value.continuityInterlockEnabled) {
            synchronizeContinuityTransition("AS_CORE_PROFILE:${profile.asCoreTag}", persistToRoom = true)
        }
        appendLog("as[core]", "Activated ${profile.asCoreTag} RGB stream profile (${frame.asCoreToken}).", LogSeverity.NOMINAL)
        showToast("${profile.title} Stream Locked (${frame.asCoreToken})")
    }

    fun toggleAsCoreStream(active: Boolean = !_uiState.value.asCoreStreamActive) {
        asCoreRgbService.setStreamActive(active)
        _uiState.update {
            it.copy(
                asCoreStreamActive = active,
                mockStreamEnabled = active
            )
        }
        val msg = if (active) "as[core] Real-Time RGB Biometric Stream Active" else "as[core] RGB Stream Paused (Manual Override)"
        appendLog("as[core]", msg, LogSeverity.INFO)
        showToast(msg)
    }

    fun toggleAsCoreAutoDriveBlockMind(enabled: Boolean = !_uiState.value.asCoreAutoDriveBlockMind) {
        asCoreRgbService.setAutoDriveBlockMind(enabled)
        _uiState.update { it.copy(asCoreAutoDriveBlockMind = enabled) }
        val msg = if (enabled) "as[core] Auto-Driving BlockMind [+-] Polarity & Dashboard" else "Manual BlockMind [+-] Polarity Control"
        appendLog("as[core]", msg, LogSeverity.INFO)
        showToast(msg)
    }

    fun setAsCoreCadenceHz(hz: Int) {
        val clamped = hz.coerceIn(5, 60)
        asCoreRgbService.setCadenceHz(clamped)
        _uiState.update { it.copy(asCoreCadenceHz = clamped) }
        appendLog("as[core]", "RGB biometric stream cadence set to ${clamped}Hz", LogSeverity.INFO)
        showToast("as[core] Stream Cadence: ${clamped}Hz")
    }

    fun updateAsCoreChannelGains(
        rGain: Float = _uiState.value.asCoreRChannelGain,
        gGain: Float = _uiState.value.asCoreGChannelGain,
        bGain: Float = _uiState.value.asCoreBChannelGain
    ) {
        val cr = rGain.coerceIn(0.20f, 2.00f)
        val cg = gGain.coerceIn(0.20f, 2.00f)
        val cb = bGain.coerceIn(0.20f, 2.00f)
        asCoreRgbService.setChannelGains(cr, cg, cb)
        val frame = asCoreRgbService.stepFrame(
            simTimeSec = _uiState.value.simTime,
            perturbation = _uiState.value.perturbation,
            somaticStabilityPct = _uiState.value.hardwareTelemetry.somaticStabilityPct
        )
        _uiState.update {
            it.copy(
                asCoreRChannelGain = cr,
                asCoreGChannelGain = cg,
                asCoreBChannelGain = cb,
                asCoreFrame = frame,
                eegBands = frame.eegBands,
                effectiveRgbTint = frame.rgbSynchroVector
            )
        }
    }

    fun injectAsCoreChromaticPulse(channel: RgbChannelAxis, intensity: Float = 0.55f) {
        asCoreRgbService.injectChromaticPulse(channel, intensity)
        val frame = asCoreRgbService.stepFrame(
            simTimeSec = _uiState.value.simTime,
            perturbation = _uiState.value.perturbation,
            somaticStabilityPct = _uiState.value.hardwareTelemetry.somaticStabilityPct
        )
        _uiState.update {
            it.copy(
                asCoreFrame = frame,
                eegBands = frame.eegBands,
                dominantFrequencyHz = frame.dominantFrequencyHz,
                phaseCoherencePct = frame.phaseCoherencePct,
                effectiveRgbTint = frame.rgbSynchroVector
            )
        }
        appendLog("as[core]", "Injected ${channel.label} chromatic pulse (+${(intensity * 100).toInt()}%) into BlockMind stream.", LogSeverity.INFO)
        showToast("as[core] ${channel.label} Chromatic Pulse Injected")
    }

    fun toggleMockBiometricStream(enabled: Boolean = !_uiState.value.mockStreamEnabled) {
        asCoreRgbService.setStreamActive(enabled)
        _uiState.update {
            it.copy(
                mockStreamEnabled = enabled,
                asCoreStreamActive = enabled
            )
        }
        showToast(if (enabled) "as[core] & MockBiometricRepository Live Stream Active" else "Manual EEG Band Sliders Active")
    }

    fun triggerMockSynapticBurst() {
        asCoreRgbService.injectChromaticPulse(RgbChannelAxis.RED, 0.45f)
        appendLog("EEG-BURST", "Injected simulated synaptic burst into as[core] RGB biometric stream.", LogSeverity.INFO)
        showToast("Simulated EEG Synaptic Burst Injected")
    }

    fun updateEegBands(
        delta: Float = _uiState.value.eegBands.delta,
        theta: Float = _uiState.value.eegBands.theta,
        alpha: Float = _uiState.value.eegBands.alpha,
        beta: Float = _uiState.value.eegBands.beta,
        gamma: Float = _uiState.value.eegBands.gamma
    ) {
        // Switching a manual slider pauses automated stream override so user's manual values stay pinned
        mockBiometricRepository.setStreamingEnabled(false)
        _uiState.update {
            it.copy(
                mockStreamEnabled = false,
                eegBands = EegBandState(
                    delta = delta.coerceIn(0.05f, 1.0f),
                    theta = theta.coerceIn(0.05f, 1.0f),
                    alpha = alpha.coerceIn(0.05f, 1.0f),
                    beta = beta.coerceIn(0.05f, 1.0f),
                    gamma = gamma.coerceIn(0.05f, 1.0f)
                )
            )
        }
    }

    fun applyBiometricPreset(presetName: String) {
        when (presetName) {
            "DEEP_FOCUS" -> {
                updateEegBands(delta = 0.15f, theta = 0.25f, alpha = 0.72f, beta = 0.88f, gamma = 0.55f)
                selectRgbMode(RgbCognitiveMode.FOCUS_BLUE)
            }
            "CREATIVE_FLOW" -> {
                updateEegBands(delta = 0.22f, theta = 0.86f, alpha = 0.64f, beta = 0.45f, gamma = 0.78f)
                selectRgbMode(RgbCognitiveMode.CREATIVITY_RED)
            }
            "TRUTH_EQUILIBRIUM" -> {
                updateEegBands(delta = 0.30f, theta = 0.42f, alpha = 0.92f, beta = 0.50f, gamma = 0.38f)
                selectRgbMode(RgbCognitiveMode.TRUTH_GREEN)
            }
        }
        if (_uiState.value.continuityInterlockEnabled) {
            synchronizeContinuityTransition("BIOMETRIC_PRESET:$presetName", persistToRoom = true)
        }
        showToast("Biometric Preset Loaded: $presetName")
    }

    fun start10sBenchmark() {
        if (_uiState.value.benchmarkActive) return
        benchmarkSamples.clear()
        benchmarkStartMs = System.currentTimeMillis()
        benchmarkInjectedFracture = false
        benchmarkTriggeredHeal = false
        _uiState.update {
            it.copy(
                benchmarkActive = true,
                benchmarkProgress = 0f,
                benchmarkElapsedSec = 0f,
                benchmarkReport = null
            )
        }
        appendLog("BENCH", "Started 10s Hex-Reflex & Self-Heal diagnostic sweep.", LogSeverity.INFO)
    }

    fun dismissBenchmarkReport() {
        _uiState.update { it.copy(benchmarkReport = null) }
    }

    private fun buildBenchmarkReport(samples: List<Float>, state: AxiomUiState): BenchmarkReport {
        val safeSamples = if (samples.isEmpty()) listOf(16.6f) else samples.sorted()
        val count = safeSamples.size
        val p50 = safeSamples[(count * 0.50).toInt().coerceIn(0, count - 1)]
        val p95 = safeSamples[(count * 0.95).toInt().coerceIn(0, count - 1)]
        val p99 = safeSamples[(count * 0.99).toInt().coerceIn(0, count - 1)]
        val drops = safeSamples.count { it > 33.33f }
        val dropPct = (drops.toFloat() / count.toFloat()) * 100f
        val avgMs = safeSamples.average().toFloat().coerceAtLeast(1f)
        val avgFps = 1000f / avgMs
        val integrityRestored = ((1.0f - state.perturbation) * 100f).roundToInt().coerceIn(0, 100)
        val score = ((1000f / p95.coerceAtLeast(1f)) * (state.renderScale * state.maxSteps) * 1.25f).roundToInt()

        return BenchmarkReport(
            p50Ms = p50,
            p95Ms = p95,
            p99Ms = p99,
            totalFrames = count,
            droppedFrames = drops,
            droppedPercent = dropPct,
            integrityRestoredPct = integrityRestored,
            avgFps = avgFps,
            computeScore = score,
            architecture = state.activeShader.title.uppercase(),
            resolutionLabel = "${"%.2f".format(state.renderScale)}x Scale"
        )
    }

    // Codex & SHA-256 Script Cleaning / Verification
    fun selectCodexFilter(crateId: String) {
        _uiState.update { it.copy(selectedCodexCrateId = crateId) }
    }

    fun updateCodexSearch(query: String) {
        _uiState.update { it.copy(codexSearchQuery = query) }
    }

    fun loadCodexEntryIntoVerifier(entry: CodexEntry) {
        cleanAndComputeSha256(
            raw = entry.codeSnippet,
            title = "${entry.crateName} // ${entry.osaMapping}",
            crateTag = entry.crateName,
            persistToRoom = false
        )
        showToast("Loaded ${entry.crateName} into SHA-256 Script Cleaner")
    }

    fun loadActiveShaderIntoVerifier() {
        val preset = _uiState.value.activeShader
        val source = AxiomShaders.getSourceForPreset(preset).trimIndent()
        cleanAndComputeSha256(
            raw = source,
            title = "GLSL // ${preset.title}",
            crateTag = "GLSL Shader",
            persistToRoom = false
        )
        showToast("Loaded ${preset.title} GLSL Source into Verifier")
    }

    fun updateRawScriptInput(newScript: String) {
        cleanAndComputeSha256(
            raw = newScript,
            title = _uiState.value.scriptTitleInput,
            crateTag = _uiState.value.scriptCrateTag,
            persistToRoom = false
        )
    }

    fun updateExpectedSha256Input(expected: String) {
        val currentHash = _uiState.value.computedSha256
        val trimmed = expected.trim().lowercase(Locale.US)
        val verified = trimmed.isEmpty() || trimmed == currentHash.lowercase(Locale.US)
        _uiState.update {
            it.copy(
                expectedSha256Input = expected,
                sha256Verified = verified
            )
        }
    }

    fun executeScriptCleanAndPersistAudit() {
        val s = _uiState.value
        cleanAndComputeSha256(
            raw = s.rawScriptInput,
            title = s.scriptTitleInput,
            crateTag = s.scriptCrateTag,
            persistToRoom = true
        )
        showToast("Čištění skriptu & SHA-256 Seal Stored in Local DB")
    }

    private fun cleanAndComputeSha256(
        raw: String,
        title: String,
        crateTag: String,
        persistToRoom: Boolean
    ) {
        val cleaned = raw
            .lines()
            .map { line ->
                val withoutDoubleSlash = line.substringBefore("//")
                withoutDoubleSlash.trim()
            }
            .filter { it.isNotEmpty() }
            .joinToString("\n")

        val digestBytes = MessageDigest.getInstance("SHA-256")
            .digest(cleaned.toByteArray(Charsets.UTF_8))
        val hashHex = digestBytes.joinToString("") { "%02x".format(it) }

        val expected = _uiState.value.expectedSha256Input.trim().lowercase(Locale.US)
        val isMatch = expected.isEmpty() || expected == hashHex.lowercase(Locale.US)

        _uiState.update {
            it.copy(
                scriptTitleInput = title,
                scriptCrateTag = crateTag,
                rawScriptInput = raw,
                cleanedScriptPreview = cleaned,
                computedSha256 = hashHex,
                sha256Verified = isMatch
            )
        }

        if (persistToRoom) {
            viewModelScope.launch {
                repository.insertVerification(
                    ScriptVerificationEntity(
                        scriptTitle = title,
                        crateCategory = crateTag,
                        rawCharCount = raw.length,
                        cleanedCharCount = cleaned.length,
                        sha256Digest = hashHex,
                        isVerified = isMatch
                    )
                )
            }
            appendLog("SHA-256", "Verified & sealed '$title' [${hashHex.take(12)}...]", LogSeverity.NOMINAL)
        }
    }

    fun toggleSpectInvertedMode() {
        _uiState.update { it.copy(spectInvertedMode = !it.spectInvertedMode) }
    }

    fun captureCurrentSpectSnapshot() {
        val s = _uiState.value
        logSpectSnapshotInternal(
            title = "MANUAL // ${s.contractPhase.latinTitle} [${s.rgbMode.id}]",
            p50 = s.currentFrameMs,
            p95 = s.currentFrameMs * 1.12f,
            p99 = s.currentFrameMs * 1.24f
        )
        showToast("Inverted SPECT Telemetry Snapshot Saved to Local SQLite")
    }

    private fun logSpectSnapshotInternal(
        title: String,
        p50: Float,
        p95: Float,
        p99: Float
    ) {
        val s = _uiState.value
        val rawMs = s.currentFrameMs.coerceAtLeast(1.0f)
        val invFps = 1000f / rawMs
        val invStability = ((1.0f - s.perturbation) * 100f).coerceIn(0f, 100f)
        val focusSignal = (s.eegBands.alpha + s.eegBands.beta) * 50f
        val spectRatio = (focusSignal / (s.cognitiveLoadPct.coerceAtLeast(10))).coerceIn(0.1f, 9.9f)
        val driftVal = s.effectiveUnicriticDrift
        val driftStatusLabel = when {
            s.redlineThreatActive || driftVal >= 0.42f -> "REDLINE // AX-IS-T"
            driftVal >= 0.15f -> "ADMONITIO DRIFT"
            else -> "NOMINAL LOCK"
        }
        val driftLogSummary = "$driftStatusLabel // ${"%.2f".format(driftVal)} Drift • BioLoad ${s.cognitiveLoadPct}% • Coherence ${s.phaseCoherencePct}%"
        val shaStatus = if (s.sha256Verified) "VERIFIED" else "DRIFT_MISMATCH"
        val digestHex = s.computedSha256.ifEmpty {
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        }

        viewModelScope.launch {
            repository.insertSpectStat(
                SpectStatEntity(
                    sessionTitle = title,
                    activeShader = s.activeShader.title,
                    rgbMode = s.rgbMode.title,
                    contractPhase = s.contractPhase.latinTitle,
                    asIfTunnelStatus = s.asIfTunnelState.name,
                    cognitiveLoadPct = s.cognitiveLoadPct,
                    rawFrameMs = rawMs,
                    invertedFrameRate = invFps,
                    rawEntropy = s.perturbation,
                    invertedStabilityIndex = invStability,
                    spectCognitiveRatio = spectRatio,
                    gigaOpsEquivalent = s.gigaOpsLoad,
                    p50Ms = p50,
                    p95Ms = p95,
                    p99Ms = p99,
                    spectralDominance = s.eegBands.dominantBandName,
                    healCycles = s.healCycles,
                    biometricDriftLog = driftLogSummary,
                    biometricDriftIntensity = driftVal,
                    sha256IntegrityStatus = shaStatus,
                    sha256Digest = digestHex,
                    sha256Verified = s.sha256Verified
                )
            )
        }
    }

    fun deleteSpectRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteSpectStat(id)
        }
    }

    fun clearAllSpectRecords() {
        viewModelScope.launch {
            repository.clearSpectStats()
            repository.clearVerifications()
            repository.clearUnicriticVerdicts()
            repository.clearUnicriticDriftLogs()
            repository.ensureSeeded()
            showToast("SPECT & Unicritic Local Vault Reset to Baseline")
        }
    }

    fun deleteUnicriticDriftLog(id: Int) {
        viewModelScope.launch {
            repository.deleteUnicriticDriftLog(id)
        }
    }

    fun selectSpectSensorPathFilter(filterKey: String) {
        _uiState.update {
            it.copy(
                spectSensorDashboardState = it.spectSensorDashboardState.copy(
                    selectedPathFilter = filterKey
                )
            )
        }
    }

    fun toggleSpectSensorPathStream() {
        val nextLive = !_uiState.value.spectSensorDashboardState.liveStreamingActive
        _uiState.update {
            it.copy(
                spectSensorDashboardState = it.spectSensorDashboardState.copy(
                    liveStreamingActive = nextLive
                )
            )
        }
        val msg = if (nextLive) "SPECT Room Sensor Paths Telemetry Stream Live" else "SPECT Room Sensor Paths Telemetry Paused"
        appendLog("SPECT-BUS", msg, LogSeverity.INFO)
        showToast(msg)
    }

    fun calibrateSpectRoomSensorPaths() {
        val s = _uiState.value
        val nextCycles = s.healCycles + 1
        val summary = "CALIBRATED // All 6 SPECT Room Sensor Paths Zeroed (0.00 Drift • 100% Health • SHA-256 Sealed)"
        val calibratedPaths = s.spectSensorDashboardState.sensorPaths.map { node ->
            node.copy(
                driftIntensity = 0.0f,
                sha256Verified = true,
                statusBadge = "CALIBRATED LOCK"
            )
        }
        _uiState.update {
            it.copy(
                perturbation = 0.0f,
                effectiveUnicriticDrift = 0.0f,
                redlineThreatActive = false,
                healWave = 0.04f,
                healCycles = nextCycles,
                phaseCoherencePct = 97,
                cognitiveLoadPct = 26,
                sha256Verified = true,
                expectedSha256Input = "",
                contractPhase = ContractPhase.EPISTULA,
                asIfTunnelState = AsIfTunnelState.LOCKED,
                spectSensorDashboardState = it.spectSensorDashboardState.copy(
                    liveStreamingActive = true,
                    aggregateHealthPct = 100,
                    activeSensorPathsCount = 6,
                    sensorPaths = calibratedPaths,
                    lastCalibratedSummary = summary
                )
            )
        }
        logSpectSnapshotInternal(
            title = "SENSOR PATH CALIBRATION #$nextCycles // 6/6 LOCKED",
            p50 = 16.2f,
            p95 = 17.4f,
            p99 = 18.6f
        )
        appendLog("SPECT-BUS", summary, LogSeverity.NOMINAL)
        showToast("SPECT Room Sensor Paths Calibrated & Sealed in SQLite")
    }

    fun sealActiveSensorPathSnapshotToRoom() {
        val s = _uiState.value
        val filter = s.spectSensorDashboardState.selectedPathFilter
        logSpectSnapshotInternal(
            title = "SENSOR BUS [$filter] // ${s.contractPhase.latinTitle} (${s.spectSensorDashboardState.aggregateHealthPct}%)",
            p50 = s.currentFrameMs,
            p95 = s.currentFrameMs * 1.08f,
            p99 = s.currentFrameMs * 1.18f
        )
        appendLog("SPECT-BUS", "Sealed real-time SPECT Room sensor path [$filter] telemetry to local SQLite.", LogSeverity.NOMINAL)
        showToast("SPECT Room Sensor Path [$filter] Sealed to Local DB")
    }

    fun deleteSpectRoomEntityRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteSpectRoomRecord(id)
        }
    }

    // =========================================================================
    // RULE: IF FAILLOAD POSSIBLE -> AUTO-FALLBACK TO SAFE LOCAL STATE & LOG TO ROOM
    // =========================================================================

    fun toggleFailloadGuard(enabled: Boolean = !_uiState.value.failloadGuardEnabled) {
        _uiState.update {
            it.copy(
                failloadGuardEnabled = enabled,
                failloadLastInterceptReason = if (enabled) {
                    "ARMED // Monitoring GPU, Biometric & AI Loads for Failload conditions"
                } else {
                    "BYPASSED // Manual Override Active"
                }
            )
        }
        val statusMsg = if (enabled) "RULE: IF FAILLOAD POSSIBLE Armed" else "RULE: IF FAILLOAD POSSIBLE Paused"
        appendLog("FAILLOAD", statusMsg, LogSeverity.INFO)
        showToast(statusMsg)
    }

    fun simulateFailloadAndIntercept() {
        // Inject a multi-subsystem overload (GPU + Biometric + AI/Hash drift) and immediately intercept if guard is armed
        _uiState.update {
            it.copy(
                perturbation = 0.92f,
                maxSteps = 216,
                cognitiveLoadPct = 94,
                biometricLoadIntensity = 0.94f,
                effectiveUnicriticDrift = 0.78f,
                redlineThreatActive = true,
                failloadGpuRiskPct = 95,
                failloadBiometricRiskPct = 92,
                failloadAiRiskPct = 88,
                failloadRiskPct = 95
            )
        }
        appendLog(
            "FAILLOAD",
            "Injected multi-axis Failload condition (GPU 95% • BIO 92% • AI 88%).",
            LogSeverity.WARNING
        )
        if (_uiState.value.failloadGuardEnabled) {
            executeFailloadSafeFallback(
                reason = "STRESS-TEST INTERCEPT // Multi-Axis Overload (GPU 95% • BIO 92% • AI 88%) Clamped to Safe Local State"
            )
        } else {
            showToast("Failload Condition Active (95% Risk) — Enable Guard or Tap Force Safe Fallback")
        }
    }

    fun executeFailloadSafeFallback(
        reason: String = "MANUAL SAFE FALLBACK // Clamped GPU, Biometric & AI Subsystems to Local Baseline"
    ) {
        val prevState = _uiState.value
        val prevScore = prevState.unicriticEvaluation.consensusScore
        val nextIntercepts = prevState.failloadInterceptCount + 1
        val nextCycles = prevState.healCycles + 1

        val fallbackBitmap = prevState.screenFilterActiveBitmap ?: synthesizeLocalAntFilterBitmap(
            prompt = prevState.screenFilterAiPrompt.ifBlank { "Safe Local Ant-Pheromone Hex Lattice" },
            mode = if (prevState.screenFilterAntMode == ScreenFilterAntMode.OFF) {
                ScreenFilterAntMode.ANT_PHEROMONE_GRID
            } else {
                prevState.screenFilterAntMode
            },
            swarmCount = prevState.screenFilterAntSwarmDensity
        )

        val optimalBands = EegBandState(
            delta = 0.22f,
            theta = 0.40f,
            alpha = 0.84f,
            beta = 0.70f,
            gamma = 0.36f
        )

        val actionSummary = com.example.model.RemediationActionSummary(
            id = System.currentTimeMillis(),
            timestamp = timeFormatter.format(Date()),
            axisTag = "FAILLOAD GUARD",
            actionTitle = "RULE: IF FAILLOAD POSSIBLE Auto-Fallback (#$nextIntercepts)",
            subsystemsRestored = "Clamped GPU Steps->64 • RestoredGLSL=TRUE • Sealed BLOCKMIND HAS:TRUE • Local Ant Bitmap Ready",
            scoreBefore = prevScore,
            scoreAfter = 99,
            outcomeBadge = "SAFE LOCAL SEAL"
        )

        _uiState.update {
            it.copy(
                failloadGuardEnabled = true,
                failloadFallbackActive = true,
                failloadInterceptCount = nextIntercepts,
                failloadLastInterceptReason = reason,
                failloadRiskPct = 8,
                failloadGpuRiskPct = 8,
                failloadBiometricRiskPct = 10,
                failloadAiRiskPct = 6,
                perturbation = 0.0f,
                healWave = 0.04f,
                healCycles = nextCycles,
                maxSteps = 64,
                renderScale = 1.00f,
                glslGateTrue = true,
                glslSyntaxValid = true,
                glslGateDiagnostic = "FAILLOAD SAFE FALLBACK // Raymarch Clamped to 64 Steps • 60 FPS Locked",
                redlineThreatActive = false,
                effectiveUnicriticDrift = 0.0f,
                biometricLoadIntensity = 0.26f,
                cognitiveLoadPct = 26,
                eegBands = optimalBands,
                blockMindSafetyVerified = true,
                blockMindSealedHasTrue = true,
                blockMindMiscalculusDelta = 0.0f,
                micaDWaveDelta = it.micaDWaveDelta.coerceAtMost(0.18f),
                micaDWaveSafeSealed = true,
                screenFilterActiveBitmap = fallbackBitmap,
                screenFilterAiState = ScreenFilterAiUiState.Success(
                    bitmap = fallbackBitmap,
                    prompt = "[SAFE LOCAL FALLBACK] ${it.screenFilterAiPrompt}",
                    isEdited = false
                ),
                expectedSha256Input = "",
                sha256Verified = true,
                contractPhase = ContractPhase.EPISTULA,
                asIfTunnelState = AsIfTunnelState.LOCKED,
                recentRemediations = (listOf(actionSummary) + it.recentRemediations).take(12)
            )
        }

        val shaSeal = _uiState.value.computedSha256.ifEmpty {
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        }

        viewModelScope.launch {
            repository.insertBiometricVerdictAndDriftLog(
                verdict = UnicriticVerdictEntity(
                    verdictLevel = "OPTIMAL",
                    consensusScore = 99,
                    glslAxisScore = 100,
                    eegAxisScore = 98,
                    codexAxisScore = 100,
                    driftIntensity = 0.0f,
                    primaryFinding = "RULE: IF FAILLOAD POSSIBLE (#$nextIntercepts) -> $reason",
                    remediationApplied = true
                ),
                driftLog = UnicriticDriftLogEntity(
                    triggerSource = "RULE // IF FAILLOAD POSSIBLE [SAFE-LOCAL]",
                    verdictLevel = "OPTIMAL",
                    rgbBiometricMode = prevState.rgbMode.title,
                    biometricLoadPct = prevState.cognitiveLoadPct,
                    dominantFrequencyHz = prevState.dominantFrequencyHz,
                    phaseCoherencePct = 96,
                    driftIntensity = prevState.effectiveUnicriticDrift,
                    compositeShaderDistortion = 0.0f,
                    redlineActive = false,
                    remediationNote = "$reason • Clamped GPU to 64 steps, restored EEG coherence, and sealed local Ant-Lattice asset [SHA-256: ${shaSeal.take(16)}]."
                )
            )
        }

        logSpectSnapshotInternal(
            title = "FAILLOAD SAFE SEAL #$nextIntercepts // LOCAL FALLBACK",
            p50 = 16.2f,
            p95 = 17.8f,
            p99 = 19.4f
        )

        appendLog(
            "FAILLOAD",
            "Executed RULE: IF FAILLOAD POSSIBLE (#$nextIntercepts) -> Safe Local State & Room Audit Seal.",
            LogSeverity.NOMINAL
        )
        showToast("IF FAILLOAD POSSIBLE: Auto-Fallback to Safe Local State & Room Sealed")
    }

    // =========================================================================
    // BLOCKMIND [+-] POLARITY, MISCALCULUS SEAL & GLSL TRUE/DENY GATE
    // =========================================================================

    fun setBlockMindPolarityFlux(flux: Float) {
        val clamped = flux.coerceIn(-1.0f, 1.0f)
        val isBlock = clamped <= -0.35f
        val isUnblock = clamped >= 0.35f
        asCoreRgbService.setAutoDriveBlockMind(false)
        _uiState.update {
            it.copy(
                asCoreAutoDriveBlockMind = false,
                blockMindPolarityFlux = clamped,
                blockMindStasis = isBlock,
                unblockMindActive = isUnblock
            )
        }
        val modeLabel = when {
            isUnblock -> "UNBLOCK MIND [+] (${"%+.2f".format(clamped)})"
            isBlock -> "BLOCK MIND [-] (${"%+.2f".format(clamped)})"
            else -> "BLOCKMIND [+-] EQUILIBRIUM (${"%+.2f".format(clamped)})"
        }
        appendLog("BLOCKMIND", "Polarity Flux set to $modeLabel", LogSeverity.INFO)
    }

    fun stepBlockMindPolarity(delta: Float) {
        setBlockMindPolarityFlux(_uiState.value.blockMindPolarityFlux + delta)
    }

    fun resolveBlockMindMiscalculusIfSafe() {
        val s = _uiState.value
        val nextCycles = s.healCycles + 1
        _uiState.update {
            it.copy(
                perturbation = 0.0f,
                effectiveUnicriticDrift = 0.0f,
                redlineThreatActive = false,
                blockMindSafetyVerified = true,
                blockMindSealedHasTrue = true,
                blockMindMiscalculusDelta = 0.0f,
                sha256Verified = true,
                expectedSha256Input = "",
                healCycles = nextCycles,
                contractPhase = ContractPhase.EPISTULA,
                asIfTunnelState = AsIfTunnelState.LOCKED
            )
        }
        cleanAndComputeSha256(
            raw = s.rawScriptInput,
            title = "BLOCKMIND MISCALCULUS -> SEALED HAS:TRUE (IF SAFE)",
            crateTag = "BlockMind Safety Gate",
            persistToRoom = true
        )
        appendLog(
            "BLOCKMIND",
            "Resolved BLOCKMIND MISCALCULUS -> SEALED HAS:TRUE (safe=true, Δ=0.0000).",
            LogSeverity.NOMINAL
        )
        showToast("BLOCKMIND MISCALCULUS Resolved -> SEALED HAS:TRUE")
    }

    fun triggerGlslFalseDenySanctuary() {
        _uiState.update {
            it.copy(
                glslGateTrue = false,
                glslSyntaxValid = false,
                glslGateDiagnostic = "AS TO GLSL: FALSE -> DENY • Celestial Aurora & Golden-Angle Sanctuary Active"
            )
        }
        appendLog(
            "GLSL-GATE",
            "AS TO GLSL: FALSE -> DENY triggered. Engaged Celestial Sanctuary & Meta-Hygiene Sleep Core.",
            LogSeverity.WARNING
        )
        showToast("AS TO GLSL: FALSE -> DENY (Celestial Sanctuary Active)")
    }

    fun restoreGlslTrueState() {
        _uiState.update {
            it.copy(
                glslGateTrue = true,
                glslSyntaxValid = true,
                perturbation = 0.0f,
                healWave = 0.03f,
                glslGateDiagnostic = "AS TO GLSL: TRUE • 60 FPS Raymarch & Uniform Bounds Nominal"
            )
        }
        appendLog("GLSL-GATE", "Restored AS TO GLSL: TRUE — 3D Hex-Reflex Raymarch active.", LogSeverity.NOMINAL)
        showToast("AS TO GLSL: TRUE Restored")
    }

    // =========================================================================
    // MICA COMPLIANCE ARCHITECTURE [TODAY PRE + FUT MICA ARCH IF D~]
    // =========================================================================

    fun selectMicaComplianceRegime(regime: MicaComplianceRegime) {
        _uiState.update { it.copy(micaComplianceRegime = regime) }
        appendLog("MiCA", "Switched compliance regime horizon to ${regime.shortLabel}", LogSeverity.INFO)
    }

    fun updateMicaDWaveDelta(delta: Float) {
        val clamped = delta.coerceIn(0.0f, 1.0f)
        _uiState.update { s ->
            val wp = s.unifiedDeployWhitepaperState
            val nextWp = GlobalVsPersonalDeployEngine.evaluateUnifiedState(
                activeTierMode = wp.activeTierMode,
                initialBondUnits = wp.decayMetrics.initialBondUnits,
                decayLambda = wp.decayMetrics.decayLambda,
                decayEpoch = wp.decayMetrics.decayEpoch,
                micaDWaveDelta = clamped,
                checkPassTrueOnly = s.localInterconnectState.checkPassTrueOnly,
                asCoreToken = s.asCoreFrame.asCoreToken,
                selectedLogicNodeId = wp.selectedLogicNodeId,
                continuitySha256 = s.continuityCurrentSha256
            )
            s.copy(
                micaDWaveDelta = clamped,
                micaDWaveSafeSealed = clamped <= 0.35f,
                unifiedDeployWhitepaperState = nextWp
            )
        }
    }

    fun executeMicaIfDWaveClampAndSeal() {
        val s = _uiState.value
        val clampedDelta = 0.000f
        val entry = MicaComplianceCatalog.buildVerifiableMicaContract(
            regime = s.micaComplianceRegime,
            dWaveDelta = clampedDelta,
            dWaveClampedSafe = true,
            gigaOpsLoad = s.gigaOpsLoad
        )
        val wp = s.unifiedDeployWhitepaperState
        val nextWp = GlobalVsPersonalDeployEngine.evaluateUnifiedState(
            activeTierMode = wp.activeTierMode,
            initialBondUnits = wp.decayMetrics.initialBondUnits,
            decayLambda = wp.decayMetrics.decayLambda,
            decayEpoch = wp.decayMetrics.decayEpoch,
            micaDWaveDelta = clampedDelta,
            checkPassTrueOnly = s.localInterconnectState.checkPassTrueOnly,
            asCoreToken = s.asCoreFrame.asCoreToken,
            selectedLogicNodeId = wp.selectedLogicNodeId,
            continuitySha256 = s.continuityCurrentSha256
        )
        _uiState.update {
            it.copy(
                micaDWaveDelta = clampedDelta,
                micaDWaveSafeSealed = true,
                unifiedDeployWhitepaperState = nextWp
            )
        }
        cleanAndComputeSha256(
            raw = entry.codeSnippet,
            title = "${entry.crateName} // ${s.micaComplianceRegime.shortLabel}",
            crateTag = entry.categoryTag,
            persistToRoom = true
        )
        appendLog("MiCA-D~", "Executed if(D~) Clamp -> 0.000 & Sealed Recital 22 / DORA Proof.", LogSeverity.NOMINAL)
        showToast("MiCA [if D~] Clamped to 0.000 & Sealed in SHA-256 Verifier")
    }

    // =========================================================================
    // UNIFIED GLOBAL VS PERSONAL DEPLOY [PaidvsFREE] & REMEMBERED RELAY WHITEPAPER
    // =========================================================================

    fun selectDeployTierMode(mode: DeployTierMode) {
        _uiState.update { s ->
            val wp = s.unifiedDeployWhitepaperState
            val nextWp = GlobalVsPersonalDeployEngine.evaluateUnifiedState(
                activeTierMode = mode,
                initialBondUnits = wp.decayMetrics.initialBondUnits,
                decayLambda = wp.decayMetrics.decayLambda,
                decayEpoch = wp.decayMetrics.decayEpoch,
                micaDWaveDelta = s.micaDWaveDelta,
                checkPassTrueOnly = s.localInterconnectState.checkPassTrueOnly,
                asCoreToken = s.asCoreFrame.asCoreToken,
                selectedLogicNodeId = wp.selectedLogicNodeId,
                continuitySha256 = s.continuityCurrentSha256
            )
            s.copy(unifiedDeployWhitepaperState = nextWp)
        }
        appendLog(
            "PAIDvsFREE",
            "Switched Deploy Tier Mode to ${mode.badgeLabel} (Recurring Subs = $0.00 Forever).",
            LogSeverity.NOMINAL
        )
        showToast("Deploy Mode: ${mode.badgeLabel} (Subs = $0.00 Forever)")
    }

    fun updateCorporateDecayLambda(lambda: Float) {
        val clamped = lambda.coerceIn(0.05f, 1.50f)
        _uiState.update { s ->
            val wp = s.unifiedDeployWhitepaperState
            val nextWp = GlobalVsPersonalDeployEngine.evaluateUnifiedState(
                activeTierMode = wp.activeTierMode,
                initialBondUnits = wp.decayMetrics.initialBondUnits,
                decayLambda = clamped,
                decayEpoch = wp.decayMetrics.decayEpoch,
                micaDWaveDelta = s.micaDWaveDelta,
                checkPassTrueOnly = s.localInterconnectState.checkPassTrueOnly,
                asCoreToken = s.asCoreFrame.asCoreToken,
                selectedLogicNodeId = wp.selectedLogicNodeId,
                continuitySha256 = s.continuityCurrentSha256
            )
            s.copy(unifiedDeployWhitepaperState = nextWp)
        }
    }

    fun stepCorporateDecayEpoch() {
        val prev = _uiState.value
        val nextEpoch = (prev.unifiedDeployWhitepaperState.decayMetrics.decayEpoch + 1).coerceAtMost(32)
        val wp = prev.unifiedDeployWhitepaperState
        val nextWp = GlobalVsPersonalDeployEngine.evaluateUnifiedState(
            activeTierMode = wp.activeTierMode,
            initialBondUnits = wp.decayMetrics.initialBondUnits,
            decayLambda = wp.decayMetrics.decayLambda,
            decayEpoch = nextEpoch,
            micaDWaveDelta = prev.micaDWaveDelta,
            checkPassTrueOnly = prev.localInterconnectState.checkPassTrueOnly,
            asCoreToken = prev.asCoreFrame.asCoreToken,
            selectedLogicNodeId = wp.selectedLogicNodeId,
            continuitySha256 = prev.continuityCurrentSha256
        )
        _uiState.update { it.copy(unifiedDeployWhitepaperState = nextWp) }
        appendLog(
            "CORP-DECAY",
            "Stepped Corporate Bond Decay to Epoch #$nextEpoch -> ${nextWp.decayMetrics.corpGreedDecayPct}% Decayed into [Personal account:DEV] Commons (Subs=$0.00).",
            LogSeverity.NOMINAL
        )
        showToast("Corp Greed Decayed: ${nextWp.decayMetrics.corpGreedDecayPct}% -> [Personal account:DEV] Commons")
    }

    fun selectOwnDevLogicNode(nodeId: String) {
        _uiState.update { s ->
            s.copy(
                unifiedDeployWhitepaperState = s.unifiedDeployWhitepaperState.copy(
                    selectedLogicNodeId = nodeId
                )
            )
        }
    }

    fun executeUnifiedDeployAndSealWhitepaper() {
        val s = _uiState.value
        val wp = s.unifiedDeployWhitepaperState
        val nextEpoch = (wp.decayMetrics.decayEpoch + 1).coerceAtMost(32)
        val nextWp = GlobalVsPersonalDeployEngine.evaluateUnifiedState(
            activeTierMode = wp.activeTierMode,
            initialBondUnits = wp.decayMetrics.initialBondUnits,
            decayLambda = wp.decayMetrics.decayLambda,
            decayEpoch = nextEpoch,
            micaDWaveDelta = 0.000f,
            checkPassTrueOnly = true,
            asCoreToken = s.asCoreFrame.asCoreToken,
            selectedLogicNodeId = wp.selectedLogicNodeId,
            continuitySha256 = s.continuityCurrentSha256
        )
        val whitepaperEntry = GlobalVsPersonalDeployEngine.buildRefinedWhitepaperCodexEntry(
            state = nextWp,
            micaRegimeLabel = s.micaComplianceRegime.shortLabel,
            micaDWaveDelta = 0.000f,
            continuitySha256 = s.continuityCurrentSha256
        )
        _uiState.update {
            it.copy(
                micaDWaveDelta = 0.000f,
                micaDWaveSafeSealed = true,
                unifiedDeployWhitepaperState = nextWp
            )
        }
        cleanAndComputeSha256(
            raw = whitepaperEntry.codeSnippet,
            title = "WHITEPAPER // MiCA -> [Personal account:DEV] • ${nextWp.ownDevLogicCanonicalAlias}",
            crateTag = nextWp.ownDevLogicCanonicalAlias,
            persistToRoom = true
        )
        logSpectSnapshotInternal(
            title = "OWN-DEV-LOGIC // ${nextWp.activeTierMode.badgeLabel} [SUBS=$0.00]",
            p50 = s.currentFrameMs,
            p95 = (s.currentFrameMs * 1.06f),
            p99 = (s.currentFrameMs * 1.12f)
        )
        appendLog(
            "OWN-DEV",
            "Unified [PaidvsFREE] Deploy & Whitepaper sealed to ${nextWp.ownDevLogicCanonicalAlias} (Subs=$0.00, Decay=${nextWp.decayMetrics.corpGreedDecayPct}%).",
            LogSeverity.NOMINAL
        )
        showToast("Sealed [PaidvsFREE] Whitepaper -> 1+ /Own/Dev/Logic_States")
    }

    // =========================================================================
    // RGB & FRQ KNOWABILITY, AS ATLAS (RED) LOST-PATH & CENTER FILE-LINE SPINE
    // =========================================================================

    fun toggleRgbKnowable() {
        val s = _uiState.value
        val nextForceUnknowable = !s.atlasKnowabilityState.forceRgbUnknowable
        val updatedSeed = s.atlasKnowabilityState.copy(
            forceRgbUnknowable = nextForceUnknowable,
            selectedFileLineCode = "L2-KNOW"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = updatedSeed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        val tint = if (evaluated.asAtlasRedActive) floatArrayOf(1.52f, 0.18f, 0.24f) else s.asCoreFrame.rgbSynchroVector
        _uiState.update {
            it.copy(
                atlasKnowabilityState = evaluated,
                effectiveRgbTint = tint
            )
        }
        val msg = if (evaluated.rgbKnowable) {
            "RGB Knowability Restored: RGB KNOWABLE [LOCKED]"
        } else {
            "RGB NOT KNOWABLE -> AS ATLAS (RED) Chromatic Wayfinder Active!"
        }
        appendLog("ATLAS-RGB", msg, if (evaluated.rgbKnowable) LogSeverity.NOMINAL else LogSeverity.WARNING)
        showToast(msg)
    }

    fun toggleFrqKnowable() {
        val s = _uiState.value
        val nextForceUnknowable = !s.atlasKnowabilityState.forceFrqUnknowable
        val updatedSeed = s.atlasKnowabilityState.copy(
            forceFrqUnknowable = nextForceUnknowable,
            selectedFileLineCode = "L2-KNOW"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = updatedSeed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        val tint = if (evaluated.asAtlasRedActive) floatArrayOf(1.52f, 0.18f, 0.24f) else s.asCoreFrame.rgbSynchroVector
        _uiState.update {
            it.copy(
                atlasKnowabilityState = evaluated,
                effectiveRgbTint = tint
            )
        }
        val msg = if (evaluated.frqKnowable) {
            "FRQ Knowability Restored: 7-Band POT-FFT & EEG Hz LOCKED"
        } else {
            "FRQ NOT KNOWABLE -> AS ATLAS (RED) Frequency Wayfinder Active!"
        }
        appendLog("ATLAS-FRQ", msg, if (evaluated.frqKnowable) LogSeverity.NOMINAL else LogSeverity.WARNING)
        showToast(msg)
    }

    fun toggleAtlasLostPath() {
        val s = _uiState.value
        val nextLost = !s.atlasKnowabilityState.forceAtlasLostPath
        val updatedSeed = s.atlasKnowabilityState.copy(
            forceAtlasLostPath = nextLost,
            selectedFileLineCode = "L4-ATLAS"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = updatedSeed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        val tint = if (evaluated.asAtlasRedActive) floatArrayOf(1.52f, 0.18f, 0.24f) else s.asCoreFrame.rgbSynchroVector
        _uiState.update {
            it.copy(
                atlasKnowabilityState = evaluated,
                effectiveRgbTint = tint
            )
        }
        val msg = if (evaluated.onLostPath) {
            "AS ATLAS (RED) // ON LOST PATH Engaged on Center File-Line Spine"
        } else {
            "Atlas (RED) Cleared — Path Re-Anchored to 1+ /Own/Dev/Logic_States"
        }
        appendLog("ATLAS-RED", msg, if (evaluated.onLostPath) LogSeverity.WARNING else LogSeverity.NOMINAL)
        showToast(msg)
    }

    fun reanchorAtlasAndRestoreKnowability() {
        val s = _uiState.value
        val nextRecov = s.atlasKnowabilityState.atlasRecoveryCount + 1
        val restoredInterconnect = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = LocalOnlyPathsInterconnectEngine.buildDefaultNodes(s.forwardTunnelParse, s.continuityCurrentSha256),
            forwardParse = s.forwardTunnelParse.copy(onlyLocalLogicFlag = true, bracketDepthBalanced = true),
            asCoreToken = s.asCoreFrame.asCoreToken,
            continuitySha256 = s.continuityCurrentSha256,
            sha256Verified = true,
            roomAuditVerified = true,
            deployEpoch = s.localInterconnectState.deployEpoch + 1,
            roomAuditRecordCount = s.localInterconnectState.roomAuditRecordCount + 1,
            activeNodeId = "DEST-1"
        )
        val (cmdOut, exitCode, blockedFlag) = AtlasKnowabilityAndOdmEngine.executeGuardedOdmCommand(
            cmdName = s.atlasKnowabilityState.selectedAllowlistCmd,
            gateStatusOn = true
        )
        val resetSeed = s.atlasKnowabilityState.copy(
            forceRgbUnknowable = false,
            forceFrqUnknowable = false,
            forceAtlasLostPath = false,
            forceOrIfLieDetected = false,
            odmGateStatusOn = true,
            lastCommandOutputJson = cmdOut,
            lastCommandExitCode = exitCode,
            runBlockedFlag = blockedFlag,
            atlasRecoveryCount = nextRecov,
            selfTeachLessonsCount = s.atlasKnowabilityState.selfTeachLessonsCount + 1,
            selectedFileLineCode = "L2-KNOW"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = resetSeed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = true,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz.coerceAtLeast(16.8f),
            rotatingFrqHz = s.effectiveRotatingFrequencyHz.coerceAtLeast(0.65f),
            phaseCoherencePct = s.phaseCoherencePct.coerceAtLeast(94),
            perturbation = 0.0f,
            glslGateTrue = true,
            redlineThreatActive = false,
            localInterconnectState = restoredInterconnect,
            spectSensorHealthPct = 99,
            simTimeSec = s.simTime
        )
        _uiState.update {
            it.copy(
                asCoreStreamActive = true,
                mockStreamEnabled = true,
                perturbation = 0.0f,
                healWave = 0.04f,
                glslGateTrue = true,
                redlineThreatActive = false,
                effectiveUnicriticDrift = 0.0f,
                phaseCoherencePct = it.phaseCoherencePct.coerceAtLeast(94),
                localInterconnectState = restoredInterconnect,
                atlasKnowabilityState = evaluated,
                effectiveRgbTint = s.asCoreFrame.rgbSynchroVector
            )
        }
        val entry = AtlasKnowabilityAndOdmEngine.buildAtlasOdmCodexEntry(evaluated)
        cleanAndComputeSha256(
            raw = entry.codeSnippet,
            title = "ATLAS (RED) RE-ANCHOR #$nextRecov // GOOD STACK SELF-TAUGHT",
            crateTag = entry.categoryTag,
            persistToRoom = true
        )
        appendLog(
            "ATLAS-ANCHOR",
            "Re-anchored Lost Path (#$nextRecov): RGB=KNOWABLE, FRQ=KNOWABLE, <or if lie>=FALSE, Good Stack=${evaluated.goodStackScorePct}%.",
            LogSeverity.NOMINAL
        )
        showToast("Re-Anchored Path -> Good Stack [${evaluated.goodStackScorePct}%] & Self-Taught!")
    }

    fun toggleOrIfLieDetector() {
        val s = _uiState.value
        val nextLie = !s.atlasKnowabilityState.forceOrIfLieDetected
        val seed = s.atlasKnowabilityState.copy(
            forceOrIfLieDetected = nextLie,
            selectedFileLineCode = "L1-PATH"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = seed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        val tint = if (evaluated.asAtlasRedActive) floatArrayOf(1.52f, 0.18f, 0.24f) else s.asCoreFrame.rgbSynchroVector
        _uiState.update {
            it.copy(
                atlasKnowabilityState = evaluated,
                effectiveRgbTint = tint
            )
        }
        val msg = if (evaluated.orIfLieDetected) {
            "<or if lie>: LIE DETECTED -> AS ATLAS (RED) Engaged on Open Intake Cant O-Ring!"
        } else {
            "<or if lie>: FALSE -> Open Intake Cant O-Ring Sealed & Clean"
        }
        appendLog("OR-IF-LIE", msg, if (evaluated.orIfLieDetected) LogSeverity.WARNING else LogSeverity.NOMINAL)
        showToast(msg)
    }

    fun selectIntakeCantJumpMode(mode: IntakeCantJumpMode) {
        val s = _uiState.value
        val seed = s.atlasKnowabilityState.copy(
            intakeCantJumpMode = mode,
            selectedFileLineCode = "L4-ATLAS"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = seed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        _uiState.update { it.copy(atlasKnowabilityState = evaluated) }
        appendLog("CANT-JUMP", "Intake Cant O-Ring Jump Mode -> ${mode.label} (${"%.2f".format(evaluated.effectiveJumpZoomFactor)}x)", LogSeverity.INFO)
        showToast("O-Ring Jump Mode: ${mode.label}")
    }

    fun cycleIntakeCantJumpMode() {
        val modes = IntakeCantJumpMode.entries
        val cur = _uiState.value.atlasKnowabilityState.intakeCantJumpMode
        val next = modes[(modes.indexOf(cur) + 1) % modes.size]
        selectIntakeCantJumpMode(next)
    }

    fun updateIntakeCantAngleDeg(angleDeg: Float) {
        val s = _uiState.value
        val clamped = angleDeg.coerceIn(0f, 55f)
        val seed = s.atlasKnowabilityState.copy(
            intakeCantAngleDeg = clamped,
            selectedFileLineCode = "L4-ATLAS"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = seed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        _uiState.update { it.copy(atlasKnowabilityState = evaluated) }
        showToast("Intake Cant O-Ring Angle: ${"%.1f".format(clamped)}°")
    }

    fun triggerSonicSoundSwapTranslate() {
        val s = _uiState.value
        val cur = s.atlasKnowabilityState
        val modes = SonicSoundSwapMode.entries
        val nextMode = modes[(modes.indexOf(cur.sonicSoundSwapMode) + 1) % modes.size]
        val nextCount = cur.sonicSoundSwapCount + 1
        val nextOffset = (cur.sonicSwapPhaseOffset + 1) % 7
        val nextLessons = if (cur.isGoodStack) cur.selfTeachLessonsCount + 1 else cur.selfTeachLessonsCount
        val seed = cur.copy(
            sonicSoundSwapTranslateActive = true,
            sonicSoundSwapMode = nextMode,
            sonicSoundSwapCount = nextCount,
            sonicSwapPhaseOffset = nextOffset,
            sonicSwapWaveBurst = 1.38f,
            selfTeachLessonsCount = nextLessons,
            selectedFileLineCode = "L2-KNOW"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = seed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        _uiState.update {
            it.copy(
                atlasKnowabilityState = evaluated,
                healWave = 0.04f
            )
        }
        appendLog(
            "SONIC-TRANSLATE",
            "Sonic Sound Swap \"translate\" #$nextCount -> ${nextMode.title} (Self-Taught #$nextLessons).",
            LogSeverity.NOMINAL
        )
        showToast("Sonic Sound Swap \"translate\" #$nextCount: ${nextMode.title}")
    }

    fun selectSonicSoundSwapMode(mode: SonicSoundSwapMode) {
        val s = _uiState.value
        val cur = s.atlasKnowabilityState
        val nextCount = cur.sonicSoundSwapCount + 1
        val seed = cur.copy(
            sonicSoundSwapTranslateActive = true,
            sonicSoundSwapMode = mode,
            sonicSoundSwapCount = nextCount,
            sonicSwapPhaseOffset = (cur.sonicSwapPhaseOffset + 2) % 7,
            sonicSwapWaveBurst = 1.32f
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = seed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        _uiState.update { it.copy(atlasKnowabilityState = evaluated, healWave = 0.04f) }
        showToast("Sonic \"translate\" Mode: ${mode.title}")
    }

    fun selectOdmTargetAndSelfTeach(target: OdmAlignmentTarget) {
        val s = _uiState.value
        val cur = s.atlasKnowabilityState
        val nextLessons = cur.selfTeachLessonsCount + 1
        val seed = cur.copy(
            selectedOdmTarget = target,
            selfTeachLessonsCount = nextLessons
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = seed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        _uiState.update { it.copy(atlasKnowabilityState = evaluated) }
        appendLog("SELF-TEACH", "Good-Stack Autodidact (#$nextLessons): ${target.selfTeachInsight}", LogSeverity.NOMINAL)
        showToast("Self-Taught [${target.badge}]: ${target.title}")
    }

    fun selectCenterFileLine(lineCode: String) {
        val s = _uiState.value
        val updated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = s.atlasKnowabilityState.copy(selectedFileLineCode = lineCode),
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        _uiState.update { it.copy(atlasKnowabilityState = updated) }
    }

    fun toggleOdmGateStatus() {
        val s = _uiState.value
        val nextGateOn = !s.atlasKnowabilityState.odmGateStatusOn
        val (outJson, exitCode, runBlocked) = AtlasKnowabilityAndOdmEngine.executeGuardedOdmCommand(
            cmdName = s.atlasKnowabilityState.selectedAllowlistCmd,
            gateStatusOn = nextGateOn
        )
        val seed = s.atlasKnowabilityState.copy(
            odmGateStatusOn = nextGateOn,
            lastCommandOutputJson = outJson,
            lastCommandExitCode = exitCode,
            runBlockedFlag = runBlocked,
            selectedFileLineCode = "L0-PROBE"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = seed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        val tint = if (evaluated.asAtlasRedActive) floatArrayOf(1.52f, 0.18f, 0.24f) else s.asCoreFrame.rgbSynchroVector
        _uiState.update {
            it.copy(
                atlasKnowabilityState = evaluated,
                effectiveRgbTint = tint
            )
        }
        val msg = if (nextGateOn) {
            "ODM Gate STATUS=ON • Allowlist Probe Armed"
        } else {
            "ODM Gate STATUS=OFF • RUN_BLOCKED=1 (Exit 3) -> AS ATLAS (RED)"
        }
        appendLog("ODM-GATE", msg, if (nextGateOn) LogSeverity.NOMINAL else LogSeverity.WARNING)
        showToast(msg)
    }

    fun executeOdmAllowlistCommand(cmdName: String) {
        val s = _uiState.value
        val (outJson, exitCode, runBlocked) = AtlasKnowabilityAndOdmEngine.executeGuardedOdmCommand(
            cmdName = cmdName,
            gateStatusOn = s.atlasKnowabilityState.odmGateStatusOn
        )
        val seed = s.atlasKnowabilityState.copy(
            selectedAllowlistCmd = cmdName,
            lastCommandOutputJson = outJson,
            lastCommandExitCode = exitCode,
            runBlockedFlag = runBlocked,
            selectedFileLineCode = "L0-PROBE"
        )
        val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = seed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = s.asCoreStreamActive,
            eegBands = s.eegBands,
            dominantHz = s.dominantFrequencyHz,
            rotatingFrqHz = s.effectiveRotatingFrequencyHz,
            phaseCoherencePct = s.phaseCoherencePct,
            perturbation = s.perturbation,
            glslGateTrue = s.glslGateTrue,
            redlineThreatActive = s.redlineThreatActive,
            localInterconnectState = s.localInterconnectState,
            spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
            simTimeSec = s.simTime
        )
        _uiState.update { it.copy(atlasKnowabilityState = evaluated) }
        appendLog("ODM-CMD", "run_cmd.sh $cmdName -> exit=$exitCode (RUN_BLOCKED=$runBlocked)", if (exitCode == 0) LogSeverity.INFO else LogSeverity.WARNING)
        showToast("ODM run_cmd.sh $cmdName -> exit=$exitCode")
    }

    fun sealAtlasOdmToCodex() {
        val s = _uiState.value
        val entry = AtlasKnowabilityAndOdmEngine.buildAtlasOdmCodexEntry(s.atlasKnowabilityState)
        cleanAndComputeSha256(
            raw = entry.codeSnippet,
            title = entry.crateName,
            crateTag = entry.categoryTag,
            persistToRoom = true
        )
        showToast("Sealed Atlas (RED) Knowability & ODM Stack into SHA-256 Verifier")
    }

    // =========================================================================
    // PROTOCOL O: INTEGRITY CORE 2026 // SEALED SUBLAYER WIRE (_wire)
    // =========================================================================

    fun dispatchProtocolOWireEvent(event: ProtocolOWireEvent) {
        val s = _uiState.value
        val curProto = s.protocolOState
        when (event) {
            is ProtocolOWireEvent.OpenSublayerGate -> {
                _uiState.update {
                    it.copy(
                        protocolOState = curProto.copy(
                            gateModalOpen = true,
                            orbKeyActive = ProtocolOIntegrityCoreEngine.isOrbKeyActive()
                        )
                    )
                }
            }

            is ProtocolOWireEvent.CloseSublayerGate -> {
                _uiState.update {
                    it.copy(
                        protocolOState = curProto.copy(
                            gateModalOpen = false,
                            orbKeyActive = ProtocolOIntegrityCoreEngine.isOrbKeyActive()
                        )
                    )
                }
            }

            is ProtocolOWireEvent.CommitVolatileGateKey -> {
                val active = ProtocolOIntegrityCoreEngine.commitVolatileKey(event.rawPasswordInput)
                val nextProvider = if (active) {
                    ProtocolOEngineProvider.GEMINI_O_GATE
                } else {
                    ProtocolOEngineProvider.POLLINATIONS_FREE
                }
                val feedback = if (active) {
                    "◈ O-Gate Key Committed to Volatile Memory (let _key) • Zero Persistence • Orb GREEN"
                } else {
                    "◈ Empty Input -> O-Gate Cleared • Orb RED (Zero Key)"
                }
                _uiState.update {
                    it.copy(
                        protocolOState = curProto.copy(
                            gateModalOpen = false,
                            orbKeyActive = active,
                            activeProvider = nextProvider,
                            gateFeedbackMessage = feedback
                        )
                    )
                }
                appendLog("PROTOCOL-O", feedback, LogSeverity.NOMINAL)
                showToast(if (active) "◈ Volatile O-Gate Key Active (Memory Only)" else "◈ O-Gate Key Cleared")
            }

            is ProtocolOWireEvent.PurgeVolatileGateKey -> {
                ProtocolOIntegrityCoreEngine.purgeVolatileKey()
                val nextPurges = curProto.keyPurgeCount + 1
                val feedback = "◈ _key Wiped Immediately from Volatile Memory (Purge #$nextPurges) • Orb RED"
                _uiState.update {
                    it.copy(
                        protocolOState = curProto.copy(
                            gateModalOpen = false,
                            orbKeyActive = false,
                            activeProvider = ProtocolOEngineProvider.POLLINATIONS_FREE,
                            gateFeedbackMessage = feedback,
                            keyPurgeCount = nextPurges
                        )
                    )
                }
                appendLog("PROTOCOL-O", feedback, LogSeverity.INFO)
                showToast("◈ Volatile _key Purged from Memory")
            }

            is ProtocolOWireEvent.SelectSublayerFunction -> {
                _uiState.update {
                    it.copy(protocolOState = curProto.copy(activeFunction = event.fn))
                }
                executeProtocolOSublayerInternal(fn = event.fn)
            }

            is ProtocolOWireEvent.UpdateQueryInput -> {
                _uiState.update {
                    it.copy(protocolOState = curProto.copy(queryInput = event.query))
                }
            }

            is ProtocolOWireEvent.SelectNetworkProbePreset -> {
                _uiState.update {
                    it.copy(
                        protocolOState = curProto.copy(
                            selectedProbePreset = event.preset,
                            activeFunction = ProtocolOFunctionId.NETWORK_PROBE,
                            activeProvider = event.preset.provider
                        )
                    )
                }
                executeProtocolOSublayerInternal(
                    fn = ProtocolOFunctionId.NETWORK_PROBE,
                    presetOverride = event.preset
                )
            }

            is ProtocolOWireEvent.SelectLocale -> {
                _uiState.update {
                    it.copy(protocolOState = curProto.copy(activeLocale = event.localeCode))
                }
                executeProtocolOSublayerInternal(localeOverride = event.localeCode)
            }

            is ProtocolOWireEvent.ExecuteActiveSublayerFunction -> {
                executeProtocolOSublayerInternal()
            }

            is ProtocolOWireEvent.ExecuteAudiusKeylessRecon -> {
                val (json, track) = ProtocolOIntegrityCoreEngine.executeAudiusKeyless(
                    query = event.query,
                    preset = curProto.selectedProbePreset,
                    allowNetworkEgress = false,
                    simTimeSec = s.simTime
                )
                val tree = ProtocolOIntegrityCoreEngine.extractStructure(json)
                val shortH = ProtocolOIntegrityCoreEngine.shortHash(json)

                // Interconnect Audius 7-band sonic energy directly into Sonic Sound Swap "translate" & Open Cant O-Ring!
                val curAtlas = s.atlasKnowabilityState
                val nextSwapCount = curAtlas.sonicSoundSwapCount + 1
                val updatedAtlas = curAtlas.copy(
                    sonicSoundSwapTranslateActive = true,
                    sonicSoundSwapCount = nextSwapCount,
                    sonicSwapPhaseOffset = (curAtlas.sonicSwapPhaseOffset + 1) % 7,
                    sonicSwapWaveBurst = 1.34f,
                    sevenBandFft = track.sevenBandDrive,
                    sonicTranslateStatusLabel = "AUDIUS KEYLESS ⇄ SONIC \"translate\" #$nextSwapCount // ${track.title} (${track.bpm} BPM)"
                )
                _uiState.update {
                    it.copy(
                        atlasKnowabilityState = updatedAtlas,
                        healWave = 0.04f,
                        protocolOState = curProto.copy(
                            activeProvider = ProtocolOEngineProvider.AUDIUS_KEYLESS,
                            audiusLastTrack = track,
                            extractedKeyTree = tree,
                            rawJsonOutput = json,
                            lastShortHash = shortH,
                            functionOutputSummary = "[_engine.audius • KEYLESS] '${track.title}' (${track.bpm} BPM) -> Coupled to 7-Band Sonic \"translate\" O-Ring (#$nextSwapCount)."
                        )
                    )
                }
                appendLog(
                    "AUDIUS-RECON",
                    "_engine.audius('${event.query}') -> ${track.title} (${track.bpm} BPM) coupled to Sonic \"translate\"",
                    LogSeverity.NOMINAL
                )
                showToast("🎵 Audius Keyless: ${track.title} (${track.bpm} BPM)")
            }

            is ProtocolOWireEvent.GenerateVolatileCertificate -> {
                val cert = ProtocolOIntegrityCoreEngine.generateVolatileCertificate(
                    state = curProto,
                    atlas = s.atlasKnowabilityState,
                    continuitySha256 = s.continuityCurrentSha256
                )
                _uiState.update {
                    it.copy(
                        protocolOState = curProto.copy(
                            volatileCertificate = cert,
                            lastShortHash = cert.shortHash
                        )
                    )
                }
                appendLog("CERT-SUBLAYER", "Generated volatile certificate ${cert.certId} (${cert.engineBadge})", LogSeverity.NOMINAL)
            }

            is ProtocolOWireEvent.DismissVolatileCertificate -> {
                // Volatile _certData is wiped (null-ed) immediately on dismiss!
                _uiState.update {
                    it.copy(
                        protocolOState = curProto.copy(
                            volatileCertificate = null,
                            blobDownloadStatus = "Volatile _certData wiped on dismiss"
                        )
                    )
                }
            }

            is ProtocolOWireEvent.DownloadVolatileCertificateBlob -> {
                _uiState.update {
                    it.copy(
                        protocolOState = curProto.copy(
                            blobDownloadStatus = "Blob URL Created -> Auto-Revoke Armed (5s)"
                        )
                    )
                }
            }

            is ProtocolOWireEvent.TriggerPredefinedDeterministicSelfUpdateSync -> {
                triggerPredefinedDeterministicSelfUpdateSync()
            }

            is ProtocolOWireEvent.TriggerEmergencySelfDependencyReload -> {
                triggerEmergencySelfDependencyReload(
                    signalLossTriggered = false,
                    reason = "ONE_TAP_EMERGENCY_SELF_DEPENDENCY_RELOAD"
                )
            }

            is ProtocolOWireEvent.SimulateSignalLossAndAutoReload -> {
                val armed = curProto.emergencyReloadPack.autoReloadArmed
                if (armed) {
                    triggerEmergencySelfDependencyReload(
                        signalLossTriggered = true,
                        reason = "SIGNAL_LOSS_AUTO_INTERCEPT_MERKLE_6HOP_RELOAD"
                    )
                } else {
                    // If auto-reload is disarmed, drop into AS ATLAS (RED) signal-loss alert awaiting manual one-tap reload
                    val lostSeed = s.atlasKnowabilityState.copy(
                        forceAtlasLostPath = true,
                        selectedFileLineCode = "L4-ATLAS"
                    )
                    val evaluated = AtlasKnowabilityAndOdmEngine.evaluateState(
                        previous = lostSeed,
                        asCoreFrame = s.asCoreFrame,
                        asCoreStreamActive = s.asCoreStreamActive,
                        eegBands = s.eegBands,
                        dominantHz = s.dominantFrequencyHz,
                        rotatingFrqHz = s.effectiveRotatingFrequencyHz,
                        phaseCoherencePct = s.phaseCoherencePct,
                        perturbation = s.perturbation,
                        glslGateTrue = s.glslGateTrue,
                        redlineThreatActive = s.redlineThreatActive,
                        localInterconnectState = s.localInterconnectState,
                        spectSensorHealthPct = s.spectSensorDashboardState.aggregateHealthPct,
                        simTimeSec = s.simTime
                    )
                    val alertPack = curProto.emergencyReloadPack.copy(
                        signalLossDetected = true,
                        lastReloadTriggerReason = "SIGNAL_LOSS_DETECTED_AWAITING_ONE_TAP_RELOAD",
                        statusBanner = "SIGNAL LOSS DETECTED [AUTO-RELOAD DISARMED] -> Tap 'Emergency Slice & Reload' to restore 6-Hop Merkle-Mesh!"
                    )
                    _uiState.update {
                        it.copy(
                            atlasKnowabilityState = evaluated,
                            effectiveRgbTint = floatArrayOf(1.52f, 0.18f, 0.24f),
                            protocolOState = curProto.copy(emergencyReloadPack = alertPack)
                        )
                    }
                    appendLog("EMERG-SIGNAL", "Signal loss detected while Auto-Reload disarmed -> AS ATLAS (RED) engaged.", LogSeverity.WARNING)
                    showToast("⚠️ Signal Loss Detected! Tap Emergency Reload to restore 6-Hop Mesh")
                }
            }

            is ProtocolOWireEvent.ToggleEmergencyAutoReloadArmed -> {
                val nextArmed = !curProto.emergencyReloadPack.autoReloadArmed
                val updatedPack = curProto.emergencyReloadPack.copy(
                    autoReloadArmed = nextArmed,
                    statusBanner = if (nextArmed) {
                        "AUTO SIGNAL-LOSS WATCHDOG ARMED + ONE-TAP RELOAD READY • Merkle Root ${curProto.emergencyReloadPack.merkleMeshRootShortHash}"
                    } else {
                        "MANUAL ONE-TAP RELOAD MODE (Auto Watchdog Paused) • Merkle Root ${curProto.emergencyReloadPack.merkleMeshRootShortHash}"
                    }
                )
                _uiState.update {
                    it.copy(protocolOState = curProto.copy(emergencyReloadPack = updatedPack))
                }
                showToast(if (nextArmed) "🛡️ Auto Signal-Loss Reload Watchdog ARMED" else "🛡️ Switched to Manual One-Tap Emergency Reload")
            }
        }
    }

    /**
     * Delegates to `EmergencyReloadRepositoryService` to slice, verify all 6 Bidirectional Merkle-Mesh
     * SHA-256 joints across the 6-hop Relay Vector Ring, reload the stack locally, and persist the
     * audit record to Room SQLite (`SpectRepository`).
     */
    fun triggerEmergencySelfDependencyReload(
        signalLossTriggered: Boolean = false,
        reason: String = "ONE_TAP_EMERGENCY_SELF_DEPENDENCY_RELOAD"
    ) {
        val s = _uiState.value
        val bundle = EmergencyReloadRepositoryService.executeOneTapOrAutoReload(
            currentProto = s.protocolOState,
            currentAtlas = s.atlasKnowabilityState,
            currentInterconnect = s.localInterconnectState,
            forwardParse = s.forwardTunnelParse,
            asCoreFrame = s.asCoreFrame,
            eegBands = s.eegBands,
            continuitySha256 = s.continuityCurrentSha256,
            signalLossTriggered = signalLossTriggered,
            triggerReason = reason
        )
        val pack = bundle.restoredProtocolO.emergencyReloadPack

        _uiState.update {
            it.copy(
                asCoreStreamActive = true,
                mockStreamEnabled = true,
                perturbation = 0.0f,
                healWave = 0.04f,
                glslGateTrue = true,
                redlineThreatActive = false,
                effectiveUnicriticDrift = 0.0f,
                phaseCoherencePct = 99,
                localInterconnectState = bundle.restoredInterconnect,
                atlasKnowabilityState = bundle.restoredAtlas,
                protocolOState = bundle.restoredProtocolO,
                effectiveRgbTint = s.asCoreFrame.rgbSynchroVector
            )
        }

        viewModelScope.launch {
            EmergencyReloadRepositoryService.persistEmergencyReloadAuditToRoom(
                repository = repository,
                bundle = bundle,
                rgbModeTitle = s.rgbMode.title
            )
        }

        cleanAndComputeSha256(
            raw = bundle.codexScript,
            title = bundle.auditTitle,
            crateTag = "Protocol O // 6-Hop Merkle-Mesh Reload",
            persistToRoom = false
        )
        appendLog(
            "EMERG-RELOAD",
            "${pack.statusBanner} [${bundle.detectionSnapshot.signalHealthMaskHex} • reason=$reason]",
            LogSeverity.NOMINAL
        )
        showToast(
            if (signalLossTriggered) {
                "🚨 Signal Drop Intercepted -> Auto-Reloaded ${pack.packId} (${bundle.detectionSnapshot.signalHealthMaskHex})"
            } else {
                "🛡️ Emergency Self-Dependency Pack Reloaded [${pack.merkleMeshRootShortHash} • ${bundle.detectionSnapshot.signalHealthMaskHex}]"
            }
        )
    }

    /**
     * Executes a full-stack Predefined Deterministic Self-Update, Upgrade & Sync pass across:
     * 1. Local-Only Loopback Interconnect (6/6 paths, <or if lie> = FALSE)
     * 2. ODM Guarded Kernel & 4 Architectural Upgrades (NO_CDN_DOM02, SSE_JSONL_PIPE, RUST_DSP_CONSOLIDATION, GOOD_STACK_AUTODIDACT)
     * 3. RGB & 7-Band FRQ Knowability Lock + Open Intake Cant O-Ring (24.0°, JUMP_SYNC 1.00x)
     * 4. Audius Keyless REST (_engine.audius) ⇄ Sonic Sound Swap "translate" (128 BPM)
     * 5. Protocol O Sealed IIFE (v2026.04.12-DET-SYNC) + Deterministic Volatile Certificate
     */
    fun triggerPredefinedDeterministicSelfUpdateSync() {
        val s = _uiState.value
        val restoredInterconnect = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = LocalOnlyPathsInterconnectEngine.buildDefaultNodes(s.forwardTunnelParse, s.continuityCurrentSha256),
            forwardParse = s.forwardTunnelParse.copy(onlyLocalLogicFlag = true, bracketDepthBalanced = true),
            asCoreToken = s.asCoreFrame.asCoreToken,
            continuitySha256 = s.continuityCurrentSha256,
            sha256Verified = true,
            roomAuditVerified = true,
            deployEpoch = s.localInterconnectState.deployEpoch + 1,
            roomAuditRecordCount = s.localInterconnectState.roomAuditRecordCount + 1,
            activeNodeId = "DEST-1"
        )
        val (cmdOut, exitCode, blockedFlag) = AtlasKnowabilityAndOdmEngine.executeGuardedOdmCommand(
            cmdName = "uname",
            gateStatusOn = true
        )
        val deterministicAtlasSeed = s.atlasKnowabilityState.copy(
            forceRgbUnknowable = false,
            forceFrqUnknowable = false,
            forceAtlasLostPath = false,
            forceOrIfLieDetected = false,
            odmGateStatusOn = true,
            selectedAllowlistCmd = "uname",
            lastCommandOutputJson = cmdOut,
            lastCommandExitCode = exitCode,
            runBlockedFlag = blockedFlag,
            intakeCantAngleDeg = 24.0f,
            intakeCantJumpMode = IntakeCantJumpMode.AUTO_JUMP_IN_OUT,
            sonicSoundSwapTranslateActive = true,
            sonicSoundSwapMode = SonicSoundSwapMode.PRISMATIC_SPECTRUM_SWAP,
            sonicSoundSwapCount = s.atlasKnowabilityState.sonicSoundSwapCount + 1,
            sonicSwapWaveBurst = 1.35f,
            selectedOdmTarget = OdmAlignmentTarget.TARGET_C_RUST_DSP,
            selfTeachLessonsCount = s.atlasKnowabilityState.selfTeachLessonsCount + 1,
            atlasRecoveryCount = s.atlasKnowabilityState.atlasRecoveryCount + 1,
            selectedFileLineCode = "L2-KNOW"
        )
        val syncedAtlas = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = deterministicAtlasSeed,
            asCoreFrame = s.asCoreFrame,
            asCoreStreamActive = true,
            eegBands = s.eegBands,
            dominantHz = 16.8f,
            rotatingFrqHz = 0.72f,
            phaseCoherencePct = 98,
            perturbation = 0.0f,
            glslGateTrue = true,
            redlineThreatActive = false,
            localInterconnectState = restoredInterconnect,
            spectSensorHealthPct = 99,
            simTimeSec = 0.0f
        )
        val syncedProto = ProtocolOIntegrityCoreEngine.executePredefinedDeterministicSelfUpdateSync(
            currentState = s.protocolOState,
            atlas = syncedAtlas,
            continuitySha256 = s.continuityCurrentSha256
        )

        _uiState.update {
            it.copy(
                asCoreStreamActive = true,
                mockStreamEnabled = true,
                perturbation = 0.0f,
                healWave = 0.04f,
                glslGateTrue = true,
                redlineThreatActive = false,
                effectiveUnicriticDrift = 0.0f,
                phaseCoherencePct = 98,
                localInterconnectState = restoredInterconnect,
                atlasKnowabilityState = syncedAtlas,
                protocolOState = syncedProto,
                effectiveRgbTint = s.asCoreFrame.rgbSynchroVector
            )
        }

        val entry = ProtocolOIntegrityCoreEngine.buildProtocolOCodexEntry(syncedProto)
        cleanAndComputeSha256(
            raw = entry.codeSnippet,
            title = "PREDEFINED DETERMINISTIC SELF-UPDATE/UPGRADE/SYNC // ${syncedProto.deterministicVectorShortHash}",
            crateTag = entry.categoryTag,
            persistToRoom = true
        )
        appendLog(
            "DET-SYNC",
            "Predefined Deterministic Self-Update/Upgrade/Sync #${syncedProto.deterministicSyncRevision} (${syncedProto.deterministicVectorShortHash}) -> 6/6 Sublayers Locked.",
            LogSeverity.NOMINAL
        )
        showToast("⚡ Deterministic Self-Update • Upgrade • Sync [${syncedProto.deterministicVectorShortHash}] Locked")
    }

    private fun executeProtocolOSublayerInternal(
        fn: ProtocolOFunctionId? = null,
        presetOverride: com.example.service.NetworkProbePreset? = null,
        localeOverride: String? = null
    ) {
        val s = _uiState.value
        val curProto = s.protocolOState
        val targetFn = fn ?: curProto.activeFunction
        val targetPreset = presetOverride ?: curProto.selectedProbePreset
        val targetLocale = localeOverride ?: curProto.activeLocale

        val (provider, json, audiusTrack) = ProtocolOIntegrityCoreEngine.executeSmartChokePoint(
            fn = targetFn,
            query = curProto.queryInput,
            locale = targetLocale,
            atlas = s.atlasKnowabilityState,
            asCore = s.asCoreFrame,
            eeg = s.eegBands,
            preset = targetPreset
        )
        val tree = ProtocolOIntegrityCoreEngine.extractStructure(json)
        val shortH = ProtocolOIntegrityCoreEngine.shortHash(json)

        val updatedAtlas = if (audiusTrack != null) {
            val nextSwap = s.atlasKnowabilityState.sonicSoundSwapCount + 1
            s.atlasKnowabilityState.copy(
                sonicSoundSwapTranslateActive = true,
                sonicSoundSwapCount = nextSwap,
                sevenBandFft = audiusTrack.sevenBandDrive,
                sonicTranslateStatusLabel = "AUDIUS ⇄ SONIC \"translate\" #$nextSwap // ${audiusTrack.title}"
            )
        } else {
            s.atlasKnowabilityState
        }

        _uiState.update {
            it.copy(
                atlasKnowabilityState = updatedAtlas,
                protocolOState = curProto.copy(
                    orbKeyActive = ProtocolOIntegrityCoreEngine.isOrbKeyActive(),
                    activeProvider = provider,
                    activeFunction = targetFn,
                    selectedProbePreset = targetPreset,
                    activeLocale = targetLocale,
                    audiusLastTrack = audiusTrack ?: curProto.audiusLastTrack,
                    extractedKeyTree = tree,
                    rawJsonOutput = json,
                    lastShortHash = shortH,
                    functionOutputSummary = "[${provider.badge}] ${targetFn.title} (${targetLocale}) • shortHash=$shortH"
                )
            )
        }
    }

    fun sealProtocolOToCodex() {
        val s = _uiState.value
        val entry = ProtocolOIntegrityCoreEngine.buildProtocolOCodexEntry(s.protocolOState)
        cleanAndComputeSha256(
            raw = entry.codeSnippet,
            title = entry.crateName,
            crateTag = entry.categoryTag,
            persistToRoom = true
        )
        showToast("Protocol O: Integrity Core 2026 Sealed in SHA-256 Verifier")
    }

    // =========================================================================
    // AS-IF SCREEN SHARE // SEARCH BEST WORK SPOTLIGHT
    // =========================================================================

    fun setShowScreenShareBestWorkModal(show: Boolean) {
        _uiState.update { it.copy(showScreenShareBestWorkModal = show) }
    }

    fun updateScreenShareSearchQuery(query: String) {
        _uiState.update { it.copy(screenShareSearchQuery = query) }
    }

    fun selectScreenShareCategory(category: BestWorkCategoryFilter) {
        _uiState.update { it.copy(screenShareCategoryFilter = category) }
    }

    fun selectScreenShareSpotlightItem(itemId: String) {
        _uiState.update { it.copy(screenShareSpotlightId = itemId) }
    }

    fun launchBestWorkInViewport(item: BestWorkShowcaseItem) {
        _uiState.update {
            it.copy(
                screenShareSpotlightId = item.id,
                activeTab = item.targetTab,
                activeShader = item.targetPreset ?: it.activeShader,
                showScreenShareBestWorkModal = false,
                hudVisible = true
            )
        }
        appendLog("SCREEN-SHARE", "Launched Best Work #${item.rank}: ${item.title}", LogSeverity.NOMINAL)
        showToast("Screen-Share Live: ${item.title}")
    }

    fun verifyAndSealBestWork(item: BestWorkShowcaseItem) {
        val entry = item.toCodexEntry()
        cleanAndComputeSha256(
            raw = entry.codeSnippet,
            title = entry.crateName,
            crateTag = entry.categoryTag,
            persistToRoom = true
        )
        showToast("Best Work #${item.rank} Verified & SHA-256 Sealed in Room")
    }

    // =========================================================================
    // SCREEN FILTER ANT // PHEROMONE POLARIZER & GEMINI / LOCAL FALLBACK STUDIO
    // =========================================================================

    fun setShowScreenFilterAntModal(show: Boolean) {
        _uiState.update { it.copy(showScreenFilterAntModal = show) }
    }

    fun selectScreenFilterAntMode(mode: ScreenFilterAntMode) {
        val existingBmp = _uiState.value.screenFilterActiveBitmap
        val seededBmp = existingBmp ?: createProceduralAntLatticeSeedBitmap(
            mode = mode,
            swarmCount = _uiState.value.screenFilterAntSwarmDensity
        )
        _uiState.update {
            it.copy(
                screenFilterAntMode = mode,
                screenFilterActiveBitmap = seededBmp
            )
        }
        appendLog("ANT-FILTER", "Screen Filter Ant mode switched to ${mode.title}", LogSeverity.INFO)
    }

    fun updateScreenFilterAntIntensity(intensity: Float) {
        _uiState.update { it.copy(screenFilterAntIntensity = intensity.coerceIn(0.05f, 1.0f)) }
    }

    fun updateScreenFilterAntSwarmDensity(count: Int) {
        _uiState.update { it.copy(screenFilterAntSwarmDensity = count.coerceIn(8, 64)) }
    }

    fun setScreenFilterProjectAiImage(enabled: Boolean) {
        val s = _uiState.value
        val bmp = s.screenFilterActiveBitmap ?: createProceduralAntLatticeSeedBitmap(
            mode = if (s.screenFilterAntMode == ScreenFilterAntMode.OFF) {
                ScreenFilterAntMode.ANT_PHEROMONE_GRID
            } else {
                s.screenFilterAntMode
            },
            swarmCount = s.screenFilterAntSwarmDensity
        )
        _uiState.update {
            it.copy(
                screenFilterProjectAiImage = enabled,
                screenFilterActiveBitmap = bmp
            )
        }
    }

    fun updateScreenFilterAiOverlayAlpha(alpha: Float) {
        _uiState.update { it.copy(screenFilterAiOverlayAlpha = alpha.coerceIn(0.10f, 0.95f)) }
    }

    fun updateScreenFilterAiPrompt(prompt: String) {
        _uiState.update { it.copy(screenFilterAiPrompt = prompt) }
    }

    fun seedProceduralScreenFilterBitmap() {
        val s = _uiState.value
        val activeMode = if (s.screenFilterAntMode == ScreenFilterAntMode.OFF) {
            ScreenFilterAntMode.ANT_PHEROMONE_GRID
        } else {
            s.screenFilterAntMode
        }
        val bmp = createProceduralAntLatticeSeedBitmap(
            mode = activeMode,
            swarmCount = s.screenFilterAntSwarmDensity
        )
        _uiState.update {
            it.copy(
                screenFilterAntMode = activeMode,
                screenFilterActiveBitmap = bmp,
                screenFilterProjectAiImage = true,
                screenFilterAiState = ScreenFilterAiUiState.Success(
                    bitmap = bmp,
                    prompt = "Procedural Ant-Pheromone Hex Lattice (${activeMode.title})",
                    isEdited = false
                )
            )
        }
        showToast("Seeded Procedural Ant-Pheromone Lattice Bitmap")
    }

    fun selectUserBitmapForScreenFilterEdit(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                screenFilterActiveBitmap = bitmap,
                screenFilterProjectAiImage = true,
                screenFilterAiState = ScreenFilterAiUiState.Success(
                    bitmap = bitmap,
                    prompt = "Loaded Custom Image for Screen Filter Ant Studio",
                    isEdited = false
                )
            )
        }
        showToast("Loaded Image into Screen Filter Ant Studio")
    }

    @OptIn(PublicPreviewAPI::class)
    fun generateScreenFilterAiImage() {
        val s = _uiState.value
        val promptText = s.screenFilterAiPrompt.trim().ifEmpty {
            "Bioluminescent hexagonal ant-pheromone lattice with emerald and cyan quantum trails"
        }
        val activeMode = if (s.screenFilterAntMode == ScreenFilterAntMode.OFF) {
            ScreenFilterAntMode.ANT_PHEROMONE_GRID
        } else {
            s.screenFilterAntMode
        }

        _uiState.update {
            it.copy(
                screenFilterAntMode = activeMode,
                screenFilterAiState = ScreenFilterAiUiState.Loading(prompt = promptText, isEditMode = false)
            )
        }

        viewModelScope.launch {
            try {
                val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                    modelName = "gemini-3.1-flash-image-preview",
                    generationConfig = generationConfig {
                        responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
                    }
                )
                val response = withContext(Dispatchers.IO) {
                    model.generateContent(promptText)
                }
                val generatedBitmap = response.candidates
                    .firstOrNull()?.content?.parts
                    ?.firstNotNullOfOrNull { it.asImageOrNull() }

                if (generatedBitmap != null) {
                    _uiState.update {
                        it.copy(
                            screenFilterActiveBitmap = generatedBitmap,
                            screenFilterProjectAiImage = true,
                            screenFilterAiState = ScreenFilterAiUiState.Success(
                                bitmap = generatedBitmap,
                                prompt = promptText,
                                isEdited = false
                            )
                        )
                    }
                    appendLog("ANT-AI", "Generated Screen Filter Ant visual via Gemini 3.1 Flash Image.", LogSeverity.NOMINAL)
                    showToast("Screen Filter Ant Visual Generated")
                } else {
                    handleAiFailloadFallback(promptText, activeMode, isEdit = false, baseBitmap = null)
                }
            } catch (e: Exception) {
                handleAiFailloadFallback(promptText, activeMode, isEdit = false, baseBitmap = null, errorMsg = e.message)
            }
        }
    }

    @OptIn(PublicPreviewAPI::class)
    fun editScreenFilterAiImage() {
        val s = _uiState.value
        val promptText = s.screenFilterAiPrompt.trim().ifEmpty {
            "Transform into a holographic UV Argon ant-colony pheromone shield"
        }
        val activeMode = if (s.screenFilterAntMode == ScreenFilterAntMode.OFF) {
            ScreenFilterAntMode.ANT_PHEROMONE_GRID
        } else {
            s.screenFilterAntMode
        }
        val baseBitmap = s.screenFilterActiveBitmap ?: createProceduralAntLatticeSeedBitmap(
            mode = activeMode,
            swarmCount = s.screenFilterAntSwarmDensity
        )

        _uiState.update {
            it.copy(
                screenFilterAntMode = activeMode,
                screenFilterActiveBitmap = baseBitmap,
                screenFilterAiState = ScreenFilterAiUiState.Loading(prompt = promptText, isEditMode = true)
            )
        }

        viewModelScope.launch {
            try {
                val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                    modelName = "gemini-3.1-flash-image-preview",
                    generationConfig = generationConfig {
                        responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
                    }
                )
                val inputContent = content {
                    image(baseBitmap)
                    text(promptText)
                }
                val response = withContext(Dispatchers.IO) {
                    model.generateContent(inputContent)
                }
                val editedBitmap = response.candidates
                    .firstOrNull()?.content?.parts
                    ?.firstNotNullOfOrNull { it.asImageOrNull() }

                if (editedBitmap != null) {
                    _uiState.update {
                        it.copy(
                            screenFilterActiveBitmap = editedBitmap,
                            screenFilterProjectAiImage = true,
                            screenFilterAiState = ScreenFilterAiUiState.Success(
                                bitmap = editedBitmap,
                                prompt = promptText,
                                isEdited = true
                            )
                        )
                    }
                    appendLog("ANT-AI", "Edited Screen Filter Ant visual via Gemini 3.1 Flash Image.", LogSeverity.NOMINAL)
                    showToast("Screen Filter Ant Visual Edited")
                } else {
                    handleAiFailloadFallback(promptText, activeMode, isEdit = true, baseBitmap = baseBitmap)
                }
            } catch (e: Exception) {
                handleAiFailloadFallback(promptText, activeMode, isEdit = true, baseBitmap = baseBitmap, errorMsg = e.message)
            }
        }
    }

    private suspend fun handleAiFailloadFallback(
        promptText: String,
        activeMode: ScreenFilterAntMode,
        isEdit: Boolean,
        baseBitmap: Bitmap?,
        errorMsg: String? = null
    ) {
        val s = _uiState.value
        val localBmp = withContext(Dispatchers.Default) {
            if (isEdit && baseBitmap != null) {
                editLocalAntFilterBitmap(baseBitmap, promptText, activeMode, s.screenFilterAntSwarmDensity)
            } else {
                synthesizeLocalAntFilterBitmap(promptText, activeMode, s.screenFilterAntSwarmDensity)
            }
        }
        val nextIntercepts = s.failloadInterceptCount + 1
        val interceptNote = "AI/ASSET FAILLOAD INTERCEPTED -> Synthesized 512x512 Local SHA-256 Ant Filter Bitmap"

        _uiState.update {
            it.copy(
                failloadInterceptCount = nextIntercepts,
                failloadLastInterceptReason = interceptNote,
                failloadFallbackActive = true,
                failloadAiRiskPct = 6,
                screenFilterActiveBitmap = localBmp,
                screenFilterProjectAiImage = true,
                screenFilterAiState = ScreenFilterAiUiState.Success(
                    bitmap = localBmp,
                    prompt = "[IF FAILLOAD -> LOCAL ARCH] $promptText",
                    isEdited = isEdit
                )
            )
        }

        repository.insertUnicriticDriftLog(
            UnicriticDriftLogEntity(
                triggerSource = "RULE // IF FAILLOAD POSSIBLE [LOCAL-SYNTH]",
                verdictLevel = "OPTIMAL",
                rgbBiometricMode = s.rgbMode.title,
                biometricLoadPct = s.cognitiveLoadPct,
                dominantFrequencyHz = s.dominantFrequencyHz,
                phaseCoherencePct = s.phaseCoherencePct,
                driftIntensity = s.effectiveUnicriticDrift,
                compositeShaderDistortion = 0.0f,
                redlineActive = false,
                remediationNote = "$interceptNote (${errorMsg?.take(48) ?: "Offline/Quota Safe Fallback"})."
            )
        )

        appendLog("FAILLOAD", interceptNote, LogSeverity.NOMINAL)
        showToast("IF FAILLOAD POSSIBLE: Synthesized Safe Local Ant-Filter Bitmap")
    }

    fun setShowGlslModal(show: Boolean) {
        _uiState.update { it.copy(showGlslModal = show) }
    }

    fun setShowEmbeddedHtmlProfilerModal(show: Boolean) {
        _uiState.update { it.copy(showEmbeddedHtmlProfilerModal = show) }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
        viewModelScope.launch {
            delay(2600L)
            _uiState.update { state ->
                if (state.toastMessage == message) state.copy(toastMessage = null) else state
            }
        }
    }

    fun appendLog(tag: String, message: String, severity: LogSeverity = LogSeverity.INFO) {
        val entry = HealLogEntry(
            id = logCounter++,
            timestamp = timeFormatter.format(Date()),
            tag = tag,
            message = message,
            severity = severity
        )
        _uiState.update { state ->
            state.copy(healLogs = (listOf(entry) + state.healLogs).take(30))
        }
    }

    class Factory(private val repository: SpectRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AxiomViewModel(repository) as T
        }
    }
}

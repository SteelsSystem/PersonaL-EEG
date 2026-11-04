package com.example.service

import com.example.model.CodexEntry
import com.example.model.EegBandState
import java.security.MessageDigest
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Operational Domain Matrix (ODM) 5-Domain Topology (`DOM-01` .. `DOM-05`)
 */
enum class OdmDomainId(
    val id: String,
    val scope: String,
    val activeModule: String,
    val gateContract: String,
    val deferredTarget: String
) {
    DOM_01(
        id = "DOM-01",
        scope = "Hardware & Sensors",
        activeModule = "Guarded status allowlist",
        gateContract = "Explicit action consent",
        deferredTarget = "TermuxHardwareBridge"
    ),
    DOM_02(
        id = "DOM-02",
        scope = "Connectivity & Radio",
        activeModule = "Air-gapped local loopback",
        gateContract = "Zero outbound access",
        deferredTarget = "OmniChannelServer"
    ),
    DOM_03(
        id = "DOM-03",
        scope = "Audio & Media",
        activeModule = "Needle DSP & 7-Band FFT",
        gateContract = "Probe only without mutation",
        deferredTarget = "MultiModalExtractorTriage"
    ),
    DOM_04(
        id = "DOM-04",
        scope = "Messaging & Contacts",
        activeModule = "Serenity & Poise isolation",
        gateContract = "Zero inbox or clipboard reads",
        deferredTarget = "KnowledgeVaultRAG"
    ),
    DOM_05(
        id = "DOM-05",
        scope = "Apps & System Actions",
        activeModule = "Gate controller & simulator",
        gateContract = "Blocked when status is off",
        deferredTarget = "CactusRuntimeEngine"
    )
}

/**
 * 7-Band POT-FFT Structural Smoother bands (`DOM-03` / Layer 2 Needle DSP + Python Smoother)
 */
data class SevenBandFftStructure(
    val subBass: Float = 0.28f,
    val bass: Float = 0.42f,
    val lowMid: Float = 0.56f,
    val mid: Float = 0.78f,
    val upperMid: Float = 0.84f,
    val presence: Float = 0.66f,
    val brilliance: Float = 0.48f,
    val potFftSize: Int = 1024,
    val sonicTensionIndex: Float = 0.34f,
    val reinstakeGuardSafe: Boolean = true
) {
    fun asList(): List<Pair<String, Float>> = listOf(
        "SUB" to subBass,
        "BASS" to bass,
        "L-MID" to lowMid,
        "MID" to mid,
        "U-MID" to upperMid,
        "PRES" to presence,
        "BRIL" to brilliance
    )
}

/**
 * Center File-Line Thinking Spine node ("the layout in center is your file line for thinking the best").
 * Represents one live reasoning line in the 5-layer ODM / SY4-EEG center pipeline.
 */
data class CenterFileLineNode(
    val lineCode: String,
    val fileLineRef: String,
    val thoughtTitle: String,
    val liveVerdict: String,
    val isKnowableAndLocked: Boolean,
    val isAtlasRedAlert: Boolean
)

/**
 * Jump In / Jump Out camera & aperture inspection modes for the Intake Cant O-Ring
 * ("jump in and out and visualise well to make sure").
 */
enum class IntakeCantJumpMode(
    val id: String,
    val label: String,
    val baseZoomFactor: Float
) {
    AUTO_JUMP_IN_OUT("auto_jump", "AUTO JUMP [IN ↔ OUT]", 1.00f),
    JUMP_IN_CORE("jump_in", "JUMP IN [DEEP CORE 1.42x]", 1.42f),
    JUMP_OUT_ORBIT("jump_out", "JUMP OUT [ORBIT 0.76x]", 0.76f)
}

/**
 * Audio-reactive Colorful Sonic Sound Swap Transfer `"translate"` modes
 * ("I love beatiful audioreactive colourful things on sonic sound swap transfer \"translate\"").
 */
enum class SonicSoundSwapMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val phaseStep: Int
) {
    PRISMATIC_SPECTRUM_SWAP(
        id = "prismatic_swap",
        title = "TRANSLATE: PRISMATIC SPECTRUM SWAP",
        subtitle = "7-Band POT-FFT Audio ⇄ 7-Color Iridescent O-Ring Ribbons",
        phaseStep = 0
    ),
    CHROMA_SYNESTHESIA_CASCADE(
        id = "chroma_cascade",
        title = "TRANSLATE: CHROMA SYNESTHESIA CASCADE",
        subtitle = "Harmonic Hz Phase Transfer ⇄ Bioluminescent Petal Waves",
        phaseStep = 2
    ),
    QUANTUM_RING_RESONANCE(
        id = "quantum_resonance",
        title = "TRANSLATE: QUANTUM O-RING RESONANCE",
        subtitle = "Sonic Tension String Transfer ⇄ Dual-Lip Rainbow Torus",
        phaseStep = 4
    )
}

/**
 * Guided Alignment Roadmap Targets (Target A, B, C, D) for the unified ODM Stack.
 */
enum class OdmAlignmentTarget(
    val id: String,
    val badge: String,
    val title: String,
    val selfTeachInsight: String
) {
    TARGET_A_ZERO_CDN(
        id = "target_a",
        badge = "TARGET A // ZERO-CDN CONSOLIDATION",
        title = "Zero-CDN Air-Gapped HTML & GLSL Workbench",
        selfTeachInsight = "Self-Taught [A]: Stripped all external CDNs; DOM-02 air-gapped 0-egress verified."
    ),
    TARGET_B_SSE_DAEMON(
        id = "target_b",
        badge = "TARGET B // LOCK-FREE SSE PIPE",
        title = "Python-to-UI Lock-Free Binary/SSE Telemetry Daemon",
        selfTeachInsight = "Self-Taught [B]: Replaced HTTP fetch polling with lock-free 7-band POT-FFT stream (<16.6ms)."
    ),
    TARGET_C_RUST_DSP(
        id = "target_c",
        badge = "TARGET C // RUST DSP & PyO3",
        title = "Rust needle-discord-metacog dsp_tension.rs & Zeroize",
        selfTeachInsight = "Self-Taught [C]: Ported spectrum_to_structure + evaluate_reinstake_guard to Rust with Zeroize."
    ),
    TARGET_D_TERMUX_ADAPTER(
        id = "target_d",
        badge = "TARGET D // TERMUX EEG BRIDGE",
        title = "TermuxHardwareBridge DOM-01/DOM-03 Consent Adapter",
        selfTeachInsight = "Self-Taught [D]: Guarded 7-cmd allowlist + explicit consent feeds unwrap_ingest_contractframe."
    )
}

/**
 * Unified state for:
 * 1. "I need to SEE if not RGB or FRQ knowable" (`rgbKnowable`, `frqKnowable`, visual diagnostics)
 * 2. "As Atlas (RED) If On Lost Path" (`onLostPath`, `asAtlasRedActive`, `lostPathReason`, `atlasBearingDeg`)
 * 3. "the layout in center is your file line for thinking the best" + "The center lines hides too you too much <or if lie>"
 * 4. "Make intake cant o ring" + "jump in and out and visualise well to make sure" (`intakeCantAngleDeg`, `intakeCantJumpMode`, `effectiveJumpZoomFactor`, `orIfLieDetected`)
 * 5. "I love beatiful audioreactive colourful things on sonic sound swap transfer \"translate\"" (`sonicSoundSwapTranslateActive`, `sonicSoundSwapMode`, `sonicSoundSwapCount`, `sonicSwapPhaseOffset`)
 * 6. "and teach yourself that how if good stack" (`isGoodStack`, `goodStackScorePct`, `selfTeachLessonsCount`, `selfTeachFormulaRule`, `selfTeachStatusBanner`, `selectedOdmTarget`)
 * 7. Complete ODM Stack Topology (`DOM-01..DOM-05`, Layers 0..4, 7-cmd allowlist, Register `0x200F` parity, Pre-Emission Gate `>P:emission>tense-dylz`)
 */
data class AtlasKnowabilityAndOdmState(
    val rgbKnowable: Boolean = true,
    val frqKnowable: Boolean = true,
    val forceRgbUnknowable: Boolean = false,
    val forceFrqUnknowable: Boolean = false,
    val forceAtlasLostPath: Boolean = false,
    val onLostPath: Boolean = false,
    val asAtlasRedActive: Boolean = false,
    val atlasStateBadge: String = "RGB & FRQ KNOWABLE // O-RING SEALED",
    val rgbKnowabilityReadout: String = "RGB KNOWABLE: TRUE [R:48% G:82% B:86% • Balance 94%]",
    val frqKnowabilityReadout: String = "FRQ KNOWABLE: TRUE [18.2 Hz EEG • 0.65 Hz ROT • 7-Band FFT]",
    val lostPathReason: String = "NOMINAL // All 6 Local Interconnect & 6 SPECT Paths Anchored",
    val atlasWayfinderCoordinate: String = "INTAKE CANT O-RING // OPEN APERTURE [24.5° CANT]",
    val atlasBearingDeg: Int = 0,
    val atlasRecoveryCount: Int = 1,

    // Intake Cant O-Ring, Jump In/Out Depth Zoom & <or if lie> Truth/Lie Gate (Zero center-occluding lines)
    val centerLinesHiddenOpenAperture: Boolean = true,
    val intakeCantAngleDeg: Float = 24.5f,
    val intakeCantJumpMode: IntakeCantJumpMode = IntakeCantJumpMode.AUTO_JUMP_IN_OUT,
    val effectiveJumpZoomFactor: Float = 1.00f,
    val intakeORingSealIntegrityPct: Int = 99,
    val forceOrIfLieDetected: Boolean = false,
    val orIfLieDetected: Boolean = false,
    val orIfLieStatusLabel: String = "<or if lie>: FALSE [NO LIE • OPEN CANT O-RING SEALED]",

    // Audio-Reactive Colorful Sonic Sound Swap Transfer "translate" State
    val sonicSoundSwapTranslateActive: Boolean = true,
    val sonicSoundSwapMode: SonicSoundSwapMode = SonicSoundSwapMode.PRISMATIC_SPECTRUM_SWAP,
    val sonicSoundSwapCount: Int = 1,
    val sonicSwapPhaseOffset: Int = 0,
    val sonicSwapWaveBurst: Float = 0.85f,
    val sonicTranslateStatusLabel: String = "SONIC SOUND SWAP \"translate\" // 7-Band Audio ⇄ Prismatic RGB O-Ring [SWAP #1]",

    // Self-Teaching Good-Stack Autodidact ("and teach yourself that how if good stack")
    val isGoodStack: Boolean = true,
    val goodStackScorePct: Int = 99,
    val selfTeachLessonsCount: Int = 4,
    val selectedOdmTarget: OdmAlignmentTarget = OdmAlignmentTarget.TARGET_C_RUST_DSP,
    val selfTeachFormulaRule: String = "if (goodStack = rgbKnowable && frqKnowable && !orIfLie && !onLostPath && reg0x200F_ok) -> SELF_TEACH(translate_7band_to_prismatic_oring)",
    val selfTeachStatusBanner: String = "GOOD STACK [99%] // SELF-TAUGHT #4: 7-Band Sonic \"translate\" ⇄ Open Cant O-Ring Crystallized",

    // Center File-Line Thinking Spine (Wrapped around the Open Intake Cant O-Ring)
    val selectedFileLineCode: String = "L2-KNOW",
    val activeCenterThoughtHeadline: String = "INTAKE CANT O-RING: RGB [KNOWABLE] • FRQ [KNOWABLE] • <or if lie> [CLEAN]",
    val centerFileLines: List<CenterFileLineNode> = emptyList(),

    // ODM Layer 0..4 & DOM-01..DOM-05 State
    val odmGateStatusOn: Boolean = true,
    val selectedAllowlistCmd: String = "uname",
    val lastCommandOutputJson: String = "{\"ok\":true,\"mode\":\"desktop_simulator\",\"executed\":false,\"command\":\"uname\",\"simulated_output\":\"[SIMULATOR] Guarded dry-run for uname (DOM-01/DOM-03)\"}",
    val lastCommandExitCode: Int = 0,
    val runBlockedFlag: Int = 0,
    val registerRawHex: String = "0x200F",
    val registerEnforcedHex: String = "0xA00F",
    val registerParityBitB15: Int = 1,
    val preEmissionGateToken: String = ">P:emission>tense-dylz",
    val preEmissionVerified: Boolean = true,
    val sevenBandFft: SevenBandFftStructure = SevenBandFftStructure(),
    val sha256AtlasOdmSeal: String = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
)

object AtlasKnowabilityAndOdmEngine {

    val allowlistedCommands: List<Pair<String, List<String>>> = listOf(
        "uname" to listOf("uname", "-a"),
        "id" to listOf("id"),
        "df" to listOf("df", "-h"),
        "free" to listOf("free", "-m"),
        "lscpu" to listOf("lscpu"),
        "termux-battery-status" to listOf("termux-battery-status"),
        "termux-audio-info" to listOf("termux-audio-info")
    )

    /**
     * Enforces 16-bit Register Hygiene via mask `0x200F` and recalculates `b15` parity bit on every cycle.
     */
    fun enforceRegister0x200F(rawWord: Int): Triple<String, String, Int> {
        val masked = (rawWord and 0x7FFF) or 0x200F
        val bitCount = Integer.bitCount(masked and 0x7FFF)
        val parityB15 = if (bitCount % 2 != 0) 1 else 0
        val finalWord = (masked and 0x7FFF) or (parityB15 shl 15)
        val rawHex = "0x%04X".format(rawWord and 0xFFFF)
        val enforcedHex = "0x%04X".format(finalWord and 0xFFFF)
        return Triple(rawHex, enforcedHex, parityB15)
    }

    /**
     * Computes 7-Band POT-FFT `StructuralSmoother` activations & `evaluate_reinstake_guard`.
     */
    fun computeSevenBandFft(
        eegBands: EegBandState,
        dominantHz: Float,
        rotatingFrqHz: Float,
        perturbation: Float,
        simTimeSec: Float,
        frqKnowable: Boolean
    ): SevenBandFftStructure {
        if (!frqKnowable) {
            return SevenBandFftStructure(
                subBass = 0.04f,
                bass = 0.04f,
                lowMid = 0.05f,
                mid = 0.04f,
                upperMid = 0.05f,
                presence = 0.04f,
                brilliance = 0.04f,
                potFftSize = 1024,
                sonicTensionIndex = 0.94f,
                reinstakeGuardSafe = false
            )
        }
        val wave = sin(simTimeSec * 2.2f) * 0.05f
        val subBass = (eegBands.delta * 0.82f + 0.12f + wave).coerceIn(0.06f, 0.99f)
        val bass = ((eegBands.delta * 0.45f + eegBands.theta * 0.55f) + wave).coerceIn(0.06f, 0.99f)
        val lowMid = (eegBands.theta * 0.78f + 0.14f - wave * 0.5f).coerceIn(0.06f, 0.99f)
        val mid = (eegBands.alpha * 0.85f + 0.10f + cos(simTimeSec * 1.8f) * 0.04f).coerceIn(0.06f, 0.99f)
        val upperMid = (eegBands.beta * 0.82f + 0.12f).coerceIn(0.06f, 0.99f)
        val presence = ((eegBands.beta * 0.45f + eegBands.gamma * 0.55f) + (rotatingFrqHz * 0.05f)).coerceIn(0.06f, 0.99f)
        val brilliance = (eegBands.gamma * 0.80f + (dominantHz / 40f).coerceIn(0f, 0.25f)).coerceIn(0.06f, 0.99f)

        val tensionIndex = (
            perturbation * 0.55f +
                (upperMid * 0.25f + brilliance * 0.20f) * (1f - mid * 0.35f)
            ).coerceIn(0.05f, 0.99f)
        val reinstakeSafe = tensionIndex < 0.72f && perturbation < 0.40f

        return SevenBandFftStructure(
            subBass = subBass,
            bass = bass,
            lowMid = lowMid,
            mid = mid,
            upperMid = upperMid,
            presence = presence,
            brilliance = brilliance,
            potFftSize = 1024,
            sonicTensionIndex = tensionIndex,
            reinstakeGuardSafe = reinstakeSafe
        )
    }

    /**
     * Simulates the guarded `run_cmd.sh` contract from `odm_stack_ctl.sh`.
     * When `gateStatusOn == false`, any `RUN` invocation halts with exit code 3 and `RUN_BLOCKED=1`.
     */
    fun executeGuardedOdmCommand(
        cmdName: String,
        gateStatusOn: Boolean
    ): Triple<String, Int, Int> {
        val trimmed = cmdName.trim()
        if (!gateStatusOn) {
            val blockedJson =
                """{"ok":false,"error":"gate_status_off","command":"$trimmed","RUN_BLOCKED":1,"exit_code":3,"atlas_fallback":"AS_ATLAS_RED"}"""
            return Triple(blockedJson, 3, 1)
        }
        val match = allowlistedCommands.firstOrNull { it.first == trimmed }
        if (match == null) {
            val denyJson =
                """{"ok":false,"error":"command_not_in_allowlist","command":"$trimmed","exit_code":126}"""
            return Triple(denyJson, 126, 0)
        }
        val argvStr = match.second.joinToString(", ") { "\"$it\"" }
        val okJson =
            """{"ok":true,"mode":"desktop_simulator","executed":false,"command":"$trimmed","argv":[$argvStr],"simulated_output":"[SIMULATOR] Guarded dry-run for $trimmed (DOM-01/DOM-03)"}"""
        return Triple(okJson, 0, 0)
    }

    /**
     * Evaluates RGB & FRQ Knowability, "As Atlas (RED) If On Lost Path", the 5-Line Center File-Line
     * Thinking Spine, and the ODM Stack state.
     */
    fun evaluateState(
        previous: AtlasKnowabilityAndOdmState,
        asCoreFrame: AsCoreRgbBiometricFrame,
        asCoreStreamActive: Boolean,
        eegBands: EegBandState,
        dominantHz: Float,
        rotatingFrqHz: Float,
        phaseCoherencePct: Int,
        perturbation: Float,
        glslGateTrue: Boolean,
        redlineThreatActive: Boolean,
        localInterconnectState: ServerlessLocalDeployState,
        spectSensorHealthPct: Int,
        simTimeSec: Float
    ): AtlasKnowabilityAndOdmState {
        // 1. Evaluate whether RGB is Knowable
        val rPct = (asCoreFrame.redChannelCreativity * 100).roundToInt()
        val gPct = (asCoreFrame.greenChannelTruth * 100).roundToInt()
        val bPct = (asCoreFrame.blueChannelFocus * 100).roundToInt()
        val rgbSignalValid = asCoreFrame.rgbChromaticBalancePct >= 40 &&
            (asCoreFrame.redChannelCreativity + asCoreFrame.greenChannelTruth + asCoreFrame.blueChannelFocus) > 0.20f
        val rgbKnowable = !previous.forceRgbUnknowable && rgbSignalValid

        // 2. Evaluate whether FRQ is Knowable
        val frqSignalValid = dominantHz in 0.5f..60.0f &&
            rotatingFrqHz in 0.02f..5.00f &&
            phaseCoherencePct >= 45
        val frqKnowable = !previous.forceFrqUnknowable && frqSignalValid

        // 3. Evaluate <or if lie> ("The center lines hides too you too much <or if lie>")
        val nonLocalNodes = localInterconnectState.nodes.filter { !it.isStrictlyLocal }
        val interconnectLost = !localInterconnectState.checkPassTrueOnly || nonLocalNodes.isNotEmpty()
        val orIfLieDetected = previous.forceOrIfLieDetected ||
            interconnectLost ||
            !glslGateTrue ||
            !localInterconnectState.sha256SealVerified

        // 4. Evaluate whether the system is On a Lost Path -> "As Atlas (RED) If On Lost Path"
        val odmRunBlocked = previous.runBlockedFlag == 1 || !previous.odmGateStatusOn
        val onLostPath = previous.forceAtlasLostPath ||
            !rgbKnowable ||
            !frqKnowable ||
            orIfLieDetected ||
            interconnectLost ||
            !glslGateTrue ||
            redlineThreatActive ||
            odmRunBlocked

        val asAtlasRedActive = onLostPath

        // 5. Compute Intake Cant O-Ring & Jump In / Out zoom factor ("Make intake cant o ring" + "jump in and out and visualise well to make sure")
        val effectiveJumpZoomFactor = when (previous.intakeCantJumpMode) {
            IntakeCantJumpMode.JUMP_IN_CORE -> 1.42f
            IntakeCantJumpMode.JUMP_OUT_ORBIT -> 0.76f
            IntakeCantJumpMode.AUTO_JUMP_IN_OUT -> (1.02f + 0.34f * sin(simTimeSec * 1.85f)).coerceIn(0.68f, 1.42f)
        }
        val cantDeg = previous.intakeCantAngleDeg.coerceIn(0f, 55f)
        val oRingIntegrity = if (asAtlasRedActive) 42 else 99
        val orIfLieStatusLabel = if (orIfLieDetected) {
            "<or if lie>: LIE DETECTED [SPOOF/FAULT] -> AS ATLAS (RED) O-RING LOCK"
        } else {
            "<or if lie>: FALSE [NO LIE • OPEN CANT O-RING (${"%.1f".format(cantDeg)}°) SEALED]"
        }

        // 6. Build explicit visual diagnostics so the user can SEE immediately if not RGB or FRQ knowable
        val rgbReadout = if (rgbKnowable) {
            "RGB KNOWABLE: TRUE [R:${rPct}% G:${gPct}% B:${bPct}% • Balance ${asCoreFrame.rgbChromaticBalancePct}% • ${if (asCoreStreamActive) "LIVE" else "LOCKED"}]"
        } else {
            "RGB NOT KNOWABLE [!] Tri-Chromatic Signal Blind -> AS ATLAS (RED) Beacon Active"
        }

        val sevenBand = computeSevenBandFft(
            eegBands = eegBands,
            dominantHz = dominantHz,
            rotatingFrqHz = rotatingFrqHz,
            perturbation = perturbation,
            simTimeSec = simTimeSec,
            frqKnowable = frqKnowable
        )

        val frqReadout = if (frqKnowable) {
            "FRQ KNOWABLE: TRUE [EEG ${"%.1f".format(dominantHz)} Hz • ROT ${"%.2f".format(rotatingFrqHz)} Hz • 7-Band POT-FFT ${sevenBand.potFftSize}]"
        } else {
            "FRQ NOT KNOWABLE [!] Spectral Hz Unresolved -> AS ATLAS (RED) Harmonic Fallback"
        }

        val lostReason = when {
            !rgbKnowable && !frqKnowable -> "LOST PATH: BOTH RGB & FRQ NOT KNOWABLE -> AS ATLAS (RED) DUAL BEACON ENGAGED"
            !rgbKnowable -> "LOST PATH: RGB NOT KNOWABLE -> AS ATLAS (RED) CHROMATIC WAYFINDER ENGAGED"
            !frqKnowable -> "LOST PATH: FRQ NOT KNOWABLE -> AS ATLAS (RED) FREQUENCY WAYFINDER ENGAGED"
            orIfLieDetected -> "LOST PATH: <or if lie> DETECTED [SPOOF/OCCLUSION] -> AS ATLAS (RED) OPEN O-RING ENGAGED"
            previous.forceAtlasLostPath -> "LOST PATH: MANUAL ATLAS (RED) LOST-PATH VECTOR TRIGGERED -> WAYFINDING TO LOCAL ANCHOR"
            interconnectLost -> {
                val faultIds = nonLocalNodes.joinToString(",") { it.id }.ifEmpty { "GATE-FALSE" }
                "LOST PATH: INTERCONNECT HOP [$faultIds] LOST (check:pass:false) -> AS ATLAS (RED) ENGAGED"
            }
            odmRunBlocked -> "LOST PATH: ODM GATE STATUS=OFF (RUN_BLOCKED=1, Exit 3) -> AS ATLAS (RED) GUARD"
            !glslGateTrue -> "LOST PATH: GLSL GATE FALSE -> AS ATLAS (RED) SANCTUARY BEACON"
            redlineThreatActive -> "LOST PATH: AX-IS-T REDLINE DRIFT -> AS ATLAS (RED) CONTAINMENT"
            else -> "PATH ANCHORED // Open Intake Cant O-Ring (${"%.1f".format(cantDeg)}°) • RGB & FRQ Knowable • <or if lie>=FALSE"
        }

        val badgeText = when {
            !rgbKnowable && !frqKnowable -> "AS ATLAS (RED) // RGB & FRQ NOT KNOWABLE"
            !rgbKnowable -> "AS ATLAS (RED) // RGB NOT KNOWABLE"
            !frqKnowable -> "AS ATLAS (RED) // FRQ NOT KNOWABLE"
            orIfLieDetected -> "AS ATLAS (RED) // <or if lie> DETECTED"
            asAtlasRedActive -> "AS ATLAS (RED) // ON LOST PATH"
            else -> "RGB & FRQ KNOWABLE // O-RING OPEN"
        }

        val bearingDeg = if (asAtlasRedActive) {
            ((simTimeSec * 45f).roundToInt() % 360 + 360) % 360
        } else {
            0
        }

        val wayfinderCoord = if (asAtlasRedActive) {
            "ATLAS [RED] BEARING %03d° ➔ CANT O-RING %.1f° [JUMP %.2fx]".format(bearingDeg, cantDeg, effectiveJumpZoomFactor)
        } else {
            "INTAKE CANT O-RING %.1f° // JUMP %.2fx [1+ /Own/Dev/Logic_States]".format(cantDeg, effectiveJumpZoomFactor)
        }

        // 7. Enforce 16-bit Register Hygiene (mask 0x200F + b15 parity)
        val dynamicWordSeed = (
            (if (rgbKnowable) 0x0100 else 0x0000) or
                (if (frqKnowable) 0x0200 else 0x0000) or
                (if (!onLostPath) 0x0400 else 0x0800) or
                ((sevenBand.sonicTensionIndex * 15f).roundToInt() and 0x00F0)
            )
        val (rawHex, enforcedHex, parityB15) = enforceRegister0x200F(dynamicWordSeed)

        // 8. Build the 5-Line Center File-Line Thinking Spine wrapped around the Intake Cant O-Ring
        val centerLines = listOf(
            CenterFileLineNode(
                lineCode = "L0-PROBE",
                fileLineRef = "odm_stack_ctl.sh:L22 -> .odm_runtime/run_cmd.sh",
                thoughtTitle = "DOM-01/DOM-05 Guarded 7-Cmd Allowlist Probe",
                liveVerdict = if (previous.odmGateStatusOn && previous.runBlockedFlag == 0) {
                    "ALLOWLIST [${previous.selectedAllowlistCmd}] OK • Exit 0"
                } else {
                    "RUN_BLOCKED=1 (Exit 3) -> ATLAS [RED]"
                },
                isKnowableAndLocked = previous.odmGateStatusOn && previous.runBlockedFlag == 0,
                isAtlasRedAlert = !previous.odmGateStatusOn || previous.runBlockedFlag != 0
            ),
            CenterFileLineNode(
                lineCode = "L1-PATH",
                fileLineRef = "LocalOnlyPathsInterconnectService.kt:L130 -> <or if lie> Guard",
                thoughtTitle = "DOM-02 Air-Gapped Loopback & <or if lie> Detector",
                liveVerdict = if (!orIfLieDetected && !previous.forceAtlasLostPath) {
                    "6/6 PATHS LOCAL • <or if lie>=FALSE"
                } else {
                    "<or if lie> / LOST PATH -> AS ATLAS (RED)"
                },
                isKnowableAndLocked = !orIfLieDetected && !previous.forceAtlasLostPath,
                isAtlasRedAlert = orIfLieDetected || previous.forceAtlasLostPath
            ),
            CenterFileLineNode(
                lineCode = "L2-KNOW",
                fileLineRef = "AsCoreRgbBiometricStreamService.kt:L272 + dsp_tension.rs:L48",
                thoughtTitle = "DOM-03 RGB & 7-Band FRQ Knowability Discrimination",
                liveVerdict = when {
                    rgbKnowable && frqKnowable -> "RGB: KNOWABLE (${asCoreFrame.rgbChromaticBalancePct}%) • FRQ: KNOWABLE (${"%.1f".format(dominantHz)}Hz)"
                    !rgbKnowable && !frqKnowable -> "RGB & FRQ NOT KNOWABLE -> ATLAS [RED]"
                    !rgbKnowable -> "RGB NOT KNOWABLE [BLIND] -> ATLAS [RED]"
                    else -> "FRQ NOT KNOWABLE [UNRESOLVED] -> ATLAS [RED]"
                },
                isKnowableAndLocked = rgbKnowable && frqKnowable,
                isAtlasRedAlert = !rgbKnowable || !frqKnowable
            ),
            CenterFileLineNode(
                lineCode = "L3-REG",
                fileLineRef = "AtlasKnowabilityAndOdmService.kt:L118 -> Reg 0x200F & >P:emission",
                thoughtTitle = "DOM-04 Register 0x200F Parity & Pre-Emission Gate",
                liveVerdict = "Reg $enforcedHex (b15=$parityB15) • ${previous.preEmissionGateToken} • Reinstake=${if (sevenBand.reinstakeGuardSafe) "SAFE" else "HOLD"}",
                isKnowableAndLocked = sevenBand.reinstakeGuardSafe && previous.preEmissionVerified,
                isAtlasRedAlert = !sevenBand.reinstakeGuardSafe
            ),
            CenterFileLineNode(
                lineCode = "L4-ATLAS",
                fileLineRef = "GlslViewport.kt:L285 -> Intake Cant O-Ring (Open Center)",
                thoughtTitle = "Layer 4 Open Cant O-Ring (${"%.1f".format(cantDeg)}°) & Jump In/Out (${"%.2f".format(effectiveJumpZoomFactor)}x)",
                liveVerdict = if (asAtlasRedActive) {
                    "AS ATLAS (RED) O-RING • Bearing %03d° • Jump %.2fx".format(bearingDeg, effectiveJumpZoomFactor)
                } else {
                    "OPEN CANT O-RING ${"%.1f".format(cantDeg)}° • Jump ${"%.2f".format(effectiveJumpZoomFactor)}x • SPECT ${spectSensorHealthPct}%"
                },
                isKnowableAndLocked = !asAtlasRedActive,
                isAtlasRedAlert = asAtlasRedActive
            )
        )

        val selectedNode = centerLines.firstOrNull { it.lineCode == previous.selectedFileLineCode }
            ?: centerLines[2]

        val headline = if (asAtlasRedActive) {
            "AS ATLAS (RED) // ${selectedNode.fileLineRef} ➔ ${selectedNode.liveVerdict}"
        } else {
            "INTAKE CANT O-RING [${selectedNode.lineCode}] // ${selectedNode.fileLineRef} ➔ ${selectedNode.liveVerdict}"
        }

        // 9. Sonic Sound Swap Transfer "translate" Audio-Reactive Prismatic Wave
        val dynamicWaveBurst = (
            previous.sonicSwapWaveBurst * 0.92f +
                (sevenBand.subBass + sevenBand.mid + sevenBand.brilliance) * 0.14f +
                0.18f * (0.5f + 0.5f * sin(simTimeSec * 3.2f + previous.sonicSwapPhaseOffset))
            ).coerceIn(0.25f, 1.45f)

        val sonicTranslateLabel = if (previous.sonicSoundSwapTranslateActive) {
            "SONIC SWAP \"translate\" // ${previous.sonicSoundSwapMode.title} • ${"%.1f".format(dominantHz)}Hz ⇄ 7-Color O-Ring [SWAP #${previous.sonicSoundSwapCount}]"
        } else {
            "SONIC SWAP \"translate\": STANDBY [Tap Translate to Swap 7-Band Audio ⇄ Prismatic O-Ring]"
        }

        // 10. Self-Teaching Good-Stack Autodidact ("and teach yourself that how if good stack")
        val isGoodStack = rgbKnowable &&
            frqKnowable &&
            !orIfLieDetected &&
            !onLostPath &&
            previous.odmGateStatusOn &&
            previous.runBlockedFlag == 0

        val goodStackScorePct = (
            (if (rgbKnowable) 22 else 0) +
                (if (frqKnowable) 22 else 0) +
                (if (!orIfLieDetected) 20 else 0) +
                (if (!onLostPath) 20 else 0) +
                (if (sevenBand.reinstakeGuardSafe) 15 else 5)
            ).coerceIn(0, 99)

        val selfTeachFormula = if (isGoodStack) {
            "if (goodStack = TRUE [${goodStackScorePct}%]) ➔ SELF_TEACH(${previous.selectedOdmTarget.id} + ${previous.sonicSoundSwapMode.id})"
        } else {
            "if (goodStack = FALSE [${goodStackScorePct}%]) ➔ HALT_POISON & ENGAGE AS ATLAS (RED) UNTIL GOOD STACK"
        }

        val selfTeachBanner = if (isGoodStack) {
            "GOOD STACK [${goodStackScorePct}%] // SELF-TAUGHT #${previous.selfTeachLessonsCount}: ${previous.selectedOdmTarget.selfTeachInsight}"
        } else {
            "STACK DEGRADED [${goodStackScorePct}%] // Self-Teach Paused -> Fix ${if (!rgbKnowable) "RGB " else ""}${if (!frqKnowable) "FRQ " else ""}${if (orIfLieDetected) "<or if lie> " else ""}via Re-Anchor"
        }

        val sealPayload = buildString {
            append("ODM_ATLAS_SEAL")
            append("|RGB_KNOW=").append(rgbKnowable)
            append("|FRQ_KNOW=").append(frqKnowable)
            append("|OR_IF_LIE=").append(orIfLieDetected)
            append("|CANT_DEG=").append("%.1f".format(cantDeg))
            append("|JUMP=").append(previous.intakeCantJumpMode.name)
            append("|SONIC_SWAP=").append(previous.sonicSoundSwapMode.id)
            append("|SWAP_CNT=").append(previous.sonicSoundSwapCount)
            append("|GOOD_STACK=").append(isGoodStack)
            append("|SELF_TEACH=").append(previous.selfTeachLessonsCount)
            append("|TARGET=").append(previous.selectedOdmTarget.id)
            append("|LOST_PATH=").append(onLostPath)
            append("|ATLAS_RED=").append(asAtlasRedActive)
            append("|REG=").append(enforcedHex)
            append("|PARITY=").append(parityB15)
            append("|GATE=").append(if (previous.odmGateStatusOn) "ON" else "OFF")
            append("|CMD=").append(previous.selectedAllowlistCmd)
        }
        val sealSha = sha256Hex(sealPayload)

        return previous.copy(
            rgbKnowable = rgbKnowable,
            frqKnowable = frqKnowable,
            onLostPath = onLostPath,
            asAtlasRedActive = asAtlasRedActive,
            orIfLieDetected = orIfLieDetected,
            orIfLieStatusLabel = orIfLieStatusLabel,
            intakeORingSealIntegrityPct = oRingIntegrity,
            effectiveJumpZoomFactor = effectiveJumpZoomFactor,
            sonicSwapWaveBurst = dynamicWaveBurst,
            sonicTranslateStatusLabel = sonicTranslateLabel,
            isGoodStack = isGoodStack,
            goodStackScorePct = goodStackScorePct,
            selfTeachFormulaRule = selfTeachFormula,
            selfTeachStatusBanner = selfTeachBanner,
            atlasStateBadge = badgeText,
            rgbKnowabilityReadout = rgbReadout,
            frqKnowabilityReadout = frqReadout,
            lostPathReason = lostReason,
            atlasWayfinderCoordinate = wayfinderCoord,
            atlasBearingDeg = bearingDeg,
            activeCenterThoughtHeadline = headline,
            centerFileLines = centerLines,
            registerRawHex = rawHex,
            registerEnforcedHex = enforcedHex,
            registerParityBitB15 = parityB15,
            sevenBandFft = sevenBand,
            sha256AtlasOdmSeal = sealSha
        )
    }

    /**
     * Builds a verifiable Codex / Rust-Python ODM + Atlas (RED) Knowability, Sonic Sound Swap "translate" & Self-Teaching Good-Stack contract snippet.
     */
    fun buildAtlasOdmCodexEntry(state: AtlasKnowabilityAndOdmState): CodexEntry {
        val code = """
            // STEELS // ODM STACK TOPOLOGY, <or if lie>, SONIC "translate" & SELF-TEACHING GOOD STACK
            // Center lines removed to never hide the 3D core; wrapped around Canted O-Ring (${state.intakeCantAngleDeg} deg)
            pub const ODM_DOMAINS: [&str; 5] = ["DOM-01", "DOM-02", "DOM-03", "DOM-04", "DOM-05"];
            pub const REGISTER_HYGIENE_MASK: u16 = 0x200F;
            pub const REGISTER_ENFORCED_WORD: &str = "${state.registerEnforcedHex}";
            pub const PRE_EMISSION_GATE: &str = "${state.preEmissionGateToken}";
            pub const RGB_KNOWABLE: bool = ${state.rgbKnowable};
            pub const FRQ_KNOWABLE: bool = ${state.frqKnowable};
            pub const OR_IF_LIE_DETECTED: bool = ${state.orIfLieDetected};
            pub const INTAKE_CANT_O_RING_DEG: f32 = ${"%.1f".format(state.intakeCantAngleDeg)};
            pub const JUMP_MODE: &str = "${state.intakeCantJumpMode.name}";
            pub const SONIC_SWAP_TRANSLATE_MODE: &str = "${state.sonicSoundSwapMode.id}";
            pub const IS_GOOD_STACK: bool = ${state.isGoodStack};
            pub const SELF_TAUGHT_LESSONS: u32 = ${state.selfTeachLessonsCount};
            pub const ACTIVE_ODM_TARGET: &str = "${state.selectedOdmTarget.badge}";
            pub const ON_LOST_PATH: bool = ${state.onLostPath};
            pub const ATLAS_MODE: &str = "${if (state.asAtlasRedActive) "AS_ATLAS_RED_WAYFINDER" else "INTAKE_CANT_O_RING_OPEN_LOCKED"}";
            pub const ATLAS_ODM_SEAL_SHA256: &str = "${state.sha256AtlasOdmSeal}";

            pub fn teach_self_if_good_stack(
                rgb_knowable: bool,
                frq_knowable: bool,
                or_if_lie: bool,
                check_pass_true_only: bool
            ) -> &'static str {
                if !rgb_knowable || !frq_knowable || or_if_lie || !check_pass_true_only {
                    "AS_ATLAS_RED_ON_LOST_PATH_HALT_POISON"
                } else {
                    "GOOD_STACK_SELF_TEACH_SONIC_TRANSLATE_LOCKED"
                }
            }
        """.trimIndent()

        return CodexEntry(
            id = "atlas_odm_knowability",
            crateName = "atlas-odm-knowability // AS ATLAS (RED) O-RING",
            registrySource = "1+ /Own/Dev/Logic_States // Open Intake Cant O-Ring & Self-Teach",
            categoryTag = "RGB/FRQ Knowability & O-Ring",
            summary = "Discriminates RGB & FRQ knowability and <or if lie>, replaces center-occluding lines with an open-aperture Intake Cant O-Ring with Jump In/Out zoom, audio-reactive Sonic Sound Swap \"translate\" ribbons, and self-teaches when if(goodStack) holds.",
            osaMapping = "Osa A-X-02..09 // DOM-01..DOM-05 • Reg ${state.registerEnforcedHex}",
            codeSnippet = code
        )
    }

    private fun sha256Hex(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

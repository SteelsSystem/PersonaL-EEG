package com.example.service

import com.example.model.CodexEntry
import java.security.MessageDigest
import kotlin.math.exp
import kotlin.math.roundToInt

/**
 * Unified Deploy Tier Mode (`[PaidvsFREE]`) enforcing anti-subscription corporate decay
 * and 100% sovereign free `[Personal account:DEV]` execution.
 */
enum class DeployTierMode(
    val id: String,
    val badgeLabel: String,
    val fullTitle: String,
    val contractClause: String,
    val description: String
) {
    PERSONAL_DEV_FREE(
        id = "personal_dev_free",
        badgeLabel = "[Personal account:DEV] • FREE",
        fullTitle = "[Personal account:DEV] // Sovereign Local Node (100% FREE Forever)",
        contractClause = "S:OpenSource(nosubs!-asis)Contract! // [Personal account:DEV] = $0.00/mo",
        description = "Zero recurring fees, zero cloud lock-in, 100% local sovereign execution on 127.0.0.1 + /Own/Dev/Logic_States."
    ),
    GLOBAL_DECAY_BOND(
        id = "global_decay_bond",
        badgeLabel = "GLOBAL [PAID->FREE DECAY BOND]",
        fullTitle = "Global Institutional Deploy // One-Time Decaying Bond -> Open-Source Commons",
        contractClause = "B(t) = B_0 * exp(-λ * t) // Recurring(t) = $0.00 Forever",
        description = "Replaces predatory recurring SaaS rent with a one-time decaying corporate bond that mathematically decays into the [Personal account:DEV] commons."
    )
}

/**
 * Mathematical metrics for the Anti-Subscription Corporate Greed Decay Engine:
 *   B(t) = B_0 * e^(-λ * t), Recurring(t) = 0.00 forever.
 */
data class CorporateDecayMetrics(
    val initialBondUnits: Float = 1000.0f,
    val decayLambda: Float = 0.42f,
    val decayEpoch: Int = 4,
    val currentBondRemainder: Float = 186.37f,
    val commonsSubsidyUnits: Float = 813.63f,
    val corpGreedDecayPct: Int = 81,
    val recurringSubscriptionFeeUsd: Float = 0.00f,
    val noSubsContractLocked: Boolean = true,
    val formulaExpression: String = "B(t) = 1000.0 * e^(-0.42 * 4) = 186.37 -> 81% Decayed to [Personal account:DEV] Commons"
)

/**
 * Sovereign state node stored under `1+ /Own/Dev/Logic_States`.
 */
data class OwnDevLogicStateNode(
    val id: String,
    val code: String,
    val logicPath: String,
    val stateExpression: String,
    val rememberedRelaySource: String,
    val activeValue: String,
    val isLockedTrue: Boolean = true,
    val stateSha256: String
)

/**
 * Side-by-side whitepaper comparison row mapping the MiCA Baseline Floor ("MiCA Below")
 * to where we start now ("Active Remembered Relay -> [Personal account:DEV]").
 */
data class WhitepaperComparisonRow(
    val axisIndex: Int,
    val axisCode: String,
    val dimensionTitle: String,
    val micaBaselineBelow: String,
    val rememberedRelayPersonalDevNow: String,
    val ownDevLogicPath: String,
    val verifiedLocked: Boolean = true
)

/**
 * Complete immutable state for the Unified Global vs Personal `[PaidvsFREE]` Deploy
 * and Refined Remembered-Relay Whitepaper (`MiCA Below -> [Personal account:DEV]`).
 */
data class UnifiedDeployWhitepaperState(
    val activeTierMode: DeployTierMode = DeployTierMode.PERSONAL_DEV_FREE,
    val decayMetrics: CorporateDecayMetrics = CorporateDecayMetrics(),
    val ownDevLogicRootPath: String = "file:///data/user/0/com.example/files/Own/Dev/Logic_States",
    val ownDevLogicCanonicalAlias: String = "1+ /Own/Dev/Logic_States",
    val ownDevLogicNodes: List<OwnDevLogicStateNode> = emptyList(),
    val selectedLogicNodeId: String = "LOGIC-01",
    val whitepaperComparisonRows: List<WhitepaperComparisonRow> = emptyList(),
    val whitepaperSealSha256: String = "",
    val personalDevAccountTag: String = "[Personal account:DEV]",
    val deployAssertionBanner: String = "[PaidvsFREE] UNIFIED • SUBS=$0.00 • [Personal account:DEV] ACTIVE"
)

object GlobalVsPersonalDeployEngine {

    const val OWN_DEV_LOGIC_ROOT_URI = "file:///data/user/0/com.example/files/Own/Dev/Logic_States"
    const val OWN_DEV_LOGIC_ALIAS = "1+ /Own/Dev/Logic_States"

    fun computeSha256Hex(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Evaluates the exponential corporate bond decay curve:
     *   B(t) = B_0 * e^(-λ * t), where Recurring(t) == 0.00 forever.
     */
    fun evaluateCorporateDecay(
        initialBondUnits: Float = 1000.0f,
        decayLambda: Float = 0.42f,
        decayEpoch: Int = 4
    ): CorporateDecayMetrics {
        val clampedB0 = initialBondUnits.coerceAtLeast(100.0f)
        val clampedLambda = decayLambda.coerceIn(0.05f, 1.50f)
        val clampedEpoch = decayEpoch.coerceAtLeast(1)

        val factor = exp((-clampedLambda * clampedEpoch.toFloat()).toDouble()).toFloat()
        val remainder = (clampedB0 * factor).coerceIn(0.0f, clampedB0)
        val subsidy = (clampedB0 - remainder).coerceIn(0.0f, clampedB0)
        val decayPct = ((subsidy / clampedB0) * 100.0f).roundToInt().coerceIn(1, 100)

        return CorporateDecayMetrics(
            initialBondUnits = clampedB0,
            decayLambda = clampedLambda,
            decayEpoch = clampedEpoch,
            currentBondRemainder = remainder,
            commonsSubsidyUnits = subsidy,
            corpGreedDecayPct = decayPct,
            recurringSubscriptionFeeUsd = 0.00f,
            noSubsContractLocked = true,
            formulaExpression = "B(t)=%.0f*e^(-%.2f*%d)=%.2f | Subs=$0.00 | %d%% -> [Personal account:DEV]".format(
                clampedB0,
                clampedLambda,
                clampedEpoch,
                remainder,
                decayPct
            )
        )
    }

    /**
     * Builds the 4 deterministic sovereign state nodes inside `1+ /Own/Dev/Logic_States`.
     */
    fun buildDefaultOwnDevLogicNodes(
        micaDWaveDelta: Float = 0.14f,
        corpGreedDecayPct: Int = 81,
        checkPassTrueOnly: Boolean = true,
        asCoreToken: String = "as[core::focus_b#0]"
    ): List<OwnDevLogicStateNode> {
        val clampedDWave = "%.3f".format(micaDWaveDelta.coerceIn(0f, 1f))
        val rawNodes = listOf(
            Triple(
                "LOGIC-01",
                "MICA_FLOOR_STATE",
                "mica.recital22.exempt=true && if(D~ <= 0.35){ d_wave=$clampedDWave -> 0.000 }"
            ) to Pair(
                "MiCA Art. 2(1)-(4) & Recital 22 Floor",
                "EXEMPT_LOCAL_TRUE (D~=$clampedDWave)"
            ),
            Triple(
                "LOGIC-02",
                "NOSUBS_DECAY_STATE",
                "recurring_fee_usd=0.00 && corp_bond_decay=${corpGreedDecayPct}% && S:OpenSource(nosubs!-asis)Contract!"
            ) to Pair(
                "[PaidvsFREE] Anti-Subscription Decay Engine",
                "SUBS=$0.00 • GREED_DECAY=${corpGreedDecayPct}%"
            ),
            Triple(
                "LOGIC-03",
                "REMEMBERED_RELAY_STATE",
                "relay(DEST-1..4 + NODE-5..6) -> check:pass:${if (checkPassTrueOnly) "true_only" else "false"} && egress=0"
            ) to Pair(
                "Remembered Relay Bus (PDF-05 + SPECTRoom)",
                if (checkPassTrueOnly) "check:pass:true_only [6/6 LOCAL]" else "FAULT_BLOCKED"
            ),
            Triple(
                "LOGIC-04",
                "PERSONAL_DEV_SOVEREIGN",
                "account=[Personal account:DEV] && path=1+ /Own/Dev/Logic_States && bio=$asCoreToken"
            ) to Pair(
                "Where We Start Now: [Personal account:DEV]",
                "ACTIVE_SOVEREIGN • 1+ /Own/Dev/Logic_States"
            )
        )

        return rawNodes.map { (triple, meta) ->
            val (id, code, expr) = triple
            val (source, activeVal) = meta
            val fullPath = "$OWN_DEV_LOGIC_ROOT_URI/$code.state"
            OwnDevLogicStateNode(
                id = id,
                code = code,
                logicPath = fullPath,
                stateExpression = expr,
                rememberedRelaySource = source,
                activeValue = activeVal,
                isLockedTrue = checkPassTrueOnly,
                stateSha256 = computeSha256Hex("$id|$code|$fullPath|$expr|$activeVal")
            )
        }
    }

    /**
     * Builds the Side-by-Side Whitepaper Comparison Matrix between:
     * - Left Column: `START: MiCA BASELINE [BELOW]`
     * - Right Column: `WHERE WE START NOW: REMEMBERED RELAY -> [Personal account:DEV]`
     */
    fun buildWhitepaperComparisonRows(
        micaDWaveDelta: Float = 0.14f,
        checkPassTrueOnly: Boolean = true,
        corpGreedDecayPct: Int = 81,
        activeTierMode: DeployTierMode = DeployTierMode.PERSONAL_DEV_FREE
    ): List<WhitepaperComparisonRow> {
        val dWaveStr = "%.3f".format(micaDWaveDelta.coerceIn(0f, 1f))
        val gateToken = if (checkPassTrueOnly) "check:pass:true_only" else "check:pass:false [BLOCKED]"
        return listOf(
            WhitepaperComparisonRow(
                axisIndex = 1,
                axisCode = "WP-AXIS-I",
                dimensionTitle = "Statutory Foundation vs. Sovereign Origin",
                micaBaselineBelow = "MiCA (EU) 2023/1114 Art. 2(1)-(4) & Recital 22 non-CASP hardware/DLT exemption + [if D~=$dWaveStr] wave clamp floor.",
                rememberedRelayPersonalDevNow = "Where We Start Now: [Personal account:DEV] sovereign origin mounted at 1+ /Own/Dev/Logic_States with zero external intermediary.",
                ownDevLogicPath = "/Own/Dev/Logic_States/MICA_FLOOR_STATE.state",
                verifiedLocked = true
            ),
            WhitepaperComparisonRow(
                axisIndex = 2,
                axisCode = "WP-AXIS-II",
                dimensionTitle = "Economic Contract [PaidvsFREE] Decay",
                micaBaselineBelow = "S:OpenSource(nosubs!-asis)Contract! statutory baseline prohibiting custodial fee extraction & recurring rent traps.",
                rememberedRelayPersonalDevNow = "Unified [${activeTierMode.badgeLabel}]: Recurring=$0.00 forever; Global bond decays (${corpGreedDecayPct}% decayed) into [Personal account:DEV] commons.",
                ownDevLogicPath = "/Own/Dev/Logic_States/NOSUBS_DECAY_STATE.state",
                verifiedLocked = true
            ),
            WhitepaperComparisonRow(
                axisIndex = 3,
                axisCode = "WP-AXIS-III",
                dimensionTitle = "Remembered Relay Topology & Egress Gate",
                micaBaselineBelow = "PDF-05 4-Axis Legal Relay specification (CS / EN / DE /LAT) + DORA (EU) 2022/2554 Art. 11 ICT continuity rules.",
                rememberedRelayPersonalDevNow = "Active Remembered Relay: DEST-1..4 + NODE-5 [SPECT-SQLITE] + NODE-6 [ASSET-VAULT] bridged under $gateToken (0 egress).",
                ownDevLogicPath = "/Own/Dev/Logic_States/REMEMBERED_RELAY_STATE.state",
                verifiedLocked = checkPassTrueOnly
            ),
            WhitepaperComparisonRow(
                axisIndex = 4,
                axisCode = "WP-AXIS-IV",
                dimensionTitle = "Neuro-Somatic & Cryptographic Execution",
                micaBaselineBelow = "Pre-compliance SHA-256 script verification (Čištění skriptu SHA-256) + EPISTULA / SATIS / LITTERAE state machine.",
                rememberedRelayPersonalDevNow = "Real-time as[core] RGB stream + 6-Path SPECT Room Sensor Bus + 1+ /Own/Dev/Logic_States sealed into Room v4 SQLite.",
                ownDevLogicPath = "/Own/Dev/Logic_States/PERSONAL_DEV_SOVEREIGN.state",
                verifiedLocked = checkPassTrueOnly
            )
        )
    }

    /**
     * Evaluates and returns the complete `UnifiedDeployWhitepaperState`.
     */
    fun evaluateUnifiedState(
        activeTierMode: DeployTierMode = DeployTierMode.PERSONAL_DEV_FREE,
        initialBondUnits: Float = 1000.0f,
        decayLambda: Float = 0.42f,
        decayEpoch: Int = 4,
        micaDWaveDelta: Float = 0.14f,
        checkPassTrueOnly: Boolean = true,
        asCoreToken: String = "as[core::focus_b#0]",
        selectedLogicNodeId: String = "LOGIC-01",
        continuitySha256: String = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4"
    ): UnifiedDeployWhitepaperState {
        val decayMetrics = evaluateCorporateDecay(
            initialBondUnits = initialBondUnits,
            decayLambda = decayLambda,
            decayEpoch = decayEpoch
        )
        val logicNodes = buildDefaultOwnDevLogicNodes(
            micaDWaveDelta = micaDWaveDelta,
            corpGreedDecayPct = decayMetrics.corpGreedDecayPct,
            checkPassTrueOnly = checkPassTrueOnly,
            asCoreToken = asCoreToken
        )
        val rows = buildWhitepaperComparisonRows(
            micaDWaveDelta = micaDWaveDelta,
            checkPassTrueOnly = checkPassTrueOnly,
            corpGreedDecayPct = decayMetrics.corpGreedDecayPct,
            activeTierMode = activeTierMode
        )
        val canonicalPayload = buildString {
            append("WHITEPAPER_V2_REMEMBERED_RELAY|")
            append("MODE=${activeTierMode.id}|")
            append("DECAY=${decayMetrics.formulaExpression}|")
            append("PATH=$OWN_DEV_LOGIC_ALIAS|")
            append("CHAIN=$continuitySha256|")
            logicNodes.forEach { node ->
                append("${node.id}:${node.stateSha256}|")
            }
        }
        val sealSha = computeSha256Hex(canonicalPayload)
        val banner = "[PaidvsFREE] ${activeTierMode.badgeLabel} • SUBS=$0.00 • GREED DECAY ${decayMetrics.corpGreedDecayPct}% • $OWN_DEV_LOGIC_ALIAS"

        return UnifiedDeployWhitepaperState(
            activeTierMode = activeTierMode,
            decayMetrics = decayMetrics,
            ownDevLogicRootPath = OWN_DEV_LOGIC_ROOT_URI,
            ownDevLogicCanonicalAlias = OWN_DEV_LOGIC_ALIAS,
            ownDevLogicNodes = logicNodes,
            selectedLogicNodeId = selectedLogicNodeId,
            whitepaperComparisonRows = rows,
            whitepaperSealSha256 = sealSha,
            personalDevAccountTag = "[Personal account:DEV]",
            deployAssertionBanner = banner
        )
    }

    /**
     * Generates a verifiable Rust/TOML Whitepaper & `/Own/Dev/Logic_States` CodexEntry
     * for the CODEX SHA-256 Script Verifier and Room audit log.
     */
    fun buildRefinedWhitepaperCodexEntry(
        state: UnifiedDeployWhitepaperState,
        micaRegimeLabel: String = "TODAY PRE + FUT MiCA [if D~]",
        micaDWaveDelta: Float = 0.14f,
        continuitySha256: String = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4"
    ): CodexEntry {
        val m = state.decayMetrics
        val snippet = """
            // SY4-EEG BLOCKMIND // REFINED WHITEPAPER BY REMEMBERED RELAY LOGIC
            // START AS OF MiCA BELOW -> WHERE WE START NOW: [Personal account:DEV]
            // UNIFIED DEPLOY: [PaidvsFREE] • ANTI-SUBSCRIPTION CORPORATE GREED DECAY
            // SOVEREIGN STATE MOUNT: ${state.ownDevLogicCanonicalAlias} (${state.ownDevLogicRootPath})

            pub mod own_dev_logic_states {
                pub const PERSONAL_ACCOUNT_ORIGIN: &str = "${state.personalDevAccountTag}";
                pub const OWN_DEV_LOGIC_PATH: &str = "${state.ownDevLogicCanonicalAlias}";
                pub const ACTIVE_DEPLOY_MODE: &str = "${state.activeTierMode.id}";
                pub const RECURRING_SUBSCRIPTION_FEE_USD: f32 = 0.00; // ZERO RECURRING SUBS FOREVER
                pub const NOSUBS_CONTRACT: &str = "S:OpenSource(nosubs!-asis)Contract!";
                pub const MICA_REGIME_FLOOR: &str = "$micaRegimeLabel";
                pub const MICA_D_WAVE_DELTA: f32 = ${"%.3f".format(micaDWaveDelta)};
                pub const CORP_BOND_DECAY_PCT: u8 = ${m.corpGreedDecayPct};

                #[derive(Debug, Clone, PartialEq)]
                pub struct PaidVsFreeDecayBond {
                    pub initial_bond_b0: f32,
                    pub decay_lambda: f32,
                    pub decay_epoch_t: u32,
                    pub bond_remainder: f32,
                    pub commons_subsidy: f32,
                    pub recurring_fee_forever: f32,
                }

                /// Enforces B(t) = B_0 * e^(-lambda * t) with Recurring(t) == 0.00 forever.
                /// Decays institutional/global license extraction into the [Personal account:DEV] commons.
                pub fn verify_paid_vs_free_anti_greed_gate() -> PaidVsFreeDecayBond {
                    let bond = PaidVsFreeDecayBond {
                        initial_bond_b0: ${"%.2f".format(m.initialBondUnits)},
                        decay_lambda: ${"%.2f".format(m.decayLambda)},
                        decay_epoch_t: ${m.decayEpoch},
                        bond_remainder: ${"%.2f".format(m.currentBondRemainder)},
                        commons_subsidy: ${"%.2f".format(m.commonsSubsidyUnits)},
                        recurring_fee_forever: 0.00,
                    };
                    assert_eq!(bond.recurring_fee_forever, 0.00, "Recurring subscriptions are strictly forbidden");
                    bond
                }

                /// Side-by-Side Progression: MiCA Baseline [Below] -> Remembered Relay [Personal account:DEV]
                pub fn verify_remembered_relay_progression() -> [&'static str; 4] {
                    [
                        "${state.ownDevLogicNodes.getOrNull(0)?.code ?: "MICA_FLOOR_STATE"} => ${state.ownDevLogicNodes.getOrNull(0)?.activeValue ?: "EXEMPT_LOCAL_TRUE"}",
                        "${state.ownDevLogicNodes.getOrNull(1)?.code ?: "NOSUBS_DECAY_STATE"} => ${state.ownDevLogicNodes.getOrNull(1)?.activeValue ?: "SUBS=$0.00"}",
                        "${state.ownDevLogicNodes.getOrNull(2)?.code ?: "REMEMBERED_RELAY_STATE"} => ${state.ownDevLogicNodes.getOrNull(2)?.activeValue ?: "check:pass:true_only"}",
                        "${state.ownDevLogicNodes.getOrNull(3)?.code ?: "PERSONAL_DEV_SOVEREIGN"} => ${state.ownDevLogicNodes.getOrNull(3)?.activeValue ?: "[Personal account:DEV]"}",
                    ]
                }

                pub const CONTINUITY_CHAIN_TIP: &str = "$continuitySha256";
                pub const WHITEPAPER_SEAL_SHA256: &str = "${state.whitepaperSealSha256}";
            }
        """.trimIndent()

        return CodexEntry(
            id = "whitepaper-mica-relay-personal-dev",
            crateName = "own-dev-logic-states ([PaidvsFREE] Whitepaper)",
            registrySource = "1+ /Own/Dev/Logic_States // [Personal account:DEV]",
            categoryTag = "1+ /Own/Dev/Logic_States",
            summary = "Refined Whitepaper by Remembered Relay Logic: MiCA Baseline [Below] -> [Personal account:DEV] with [PaidvsFREE] Anti-Subscription Corporate Decay (Subs = $0.00 Forever).",
            osaMapping = "Osa A-X-08 // [PaidvsFREE] Sovereign Logic States",
            codeSnippet = snippet
        )
    }
}

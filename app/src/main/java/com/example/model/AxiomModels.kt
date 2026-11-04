package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

enum class HudTab(val label: String, val badge: String) {
    TUNNEL("TUNNEL", "GLSL"),
    BIOMETRICS("EEG/RGB", "SY4"),
    UNICRITIC("UNICRITIC", "CRIT"),
    CODEX("CODEX", "RUST"),
    SPECT("SPECT", "SQL")
}

enum class UnicriticVerdictLevel(
    val title: String,
    val contractCorrelation: String,
    val description: String,
    val color: Color
) {
    OPTIMAL(
        title = "OPTIMAL",
        contractCorrelation = "EPISTULA // SEALED",
        description = "All three axes (GLSL Pipeline, SY4-EEG Biometrics, SHA-256 Codex) operate in crystalline phase lock.",
        color = NeonGreen
    ),
    ADMONITIO(
        title = "ADMONITIO",
        contractCorrelation = "SATIS // DRIFT DETECTED",
        description = "Moderate cross-axis phase divergence detected; autonomous self-healing or Unicritic remediation advised.",
        color = NeonAmber
    ),
    CRITICUS(
        title = "CRITICUS",
        contractCorrelation = "LITTERAE // SHEAR ALERT",
        description = "Critical multi-axis drift! Real-time GLSL chromatic distortion active; execute Unicritic Remediation.",
        color = NeonPink
    )
}

enum class CriticAxis(val label: String, val shortTag: String) {
    GLSL_LATTICE("Axis I: GLSL & Lattice Symmetry", "GLSL"),
    EEG_BIOMETRIC("Axis II: SY4-EEG & Somatic Load", "EEG"),
    CODEX_SHA256("Axis III: Codex & SHA-256 Seal", "SHA")
}

data class UnicriticFinding(
    val axis: CriticAxis,
    val title: String,
    val detail: String,
    val severity: LogSeverity,
    val actionKey: String
)

data class RemediationActionSummary(
    val id: Long,
    val timestamp: String,
    val axisTag: String,
    val actionTitle: String,
    val subsystemsRestored: String,
    val scoreBefore: Int,
    val scoreAfter: Int,
    val outcomeBadge: String
)

data class UnicriticLiveEvaluation(
    val consensusScore: Int = 95,
    val verdictLevel: UnicriticVerdictLevel = UnicriticVerdictLevel.OPTIMAL,
    val glslAxisScore: Int = 96,
    val eegAxisScore: Int = 90,
    val codexAxisScore: Int = 100,
    val driftIntensity: Float = 0.05f,
    val findings: List<UnicriticFinding> = emptyList()
)

enum class ShaderPreset(
    val key: String,
    val title: String,
    val complexityTag: String,
    val description: String
) {
    HEX_REFLEX(
        key = "hex_reflex",
        title = "Hex Reflex Core",
        complexityTag = "INFINITE HEX-LATTICE",
        description = "3D Hexagonal domain repetition with radial 'Reflex' light density, divergence-free magnetic compression, sine-modulated anti-magnetic blocking boundary, and autonomous lattice self-healing."
    ),
    FRACTAL(
        key = "fractal",
        title = "Mandelbulb 3D",
        complexityTag = "POLYNOMIAL SDF",
        description = "Volumetric 3D Mandelbulb distance estimation raymarcher coupled to the magnetic perturbation, RGB biometric mode, and self-heal parameters."
    ),
    VORONOI(
        key = "voronoi",
        title = "Voronoi Lattice",
        complexityTag = "3D CELLULAR",
        description = "Real-time raymarched 3D Voronoi cellular lattice with reactive phase perturbations, biometric tinting, and crystalline recovery."
    )
}

enum class RgbCognitiveMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val shaderTint: FloatArray
) {
    CREATIVITY_RED(
        id = "CREATIVITY",
        title = "Creativity [R]",
        subtitle = "Theta-Gamma Divergent Synthesis • Warm Plasma Tint",
        accentColor = NeonPink,
        shaderTint = floatArrayOf(1.35f, 0.45f, 0.75f)
    ),
    TRUTH_GREEN(
        id = "TRUTH",
        title = "Truth [G]",
        subtitle = "Alpha Equilibrium & Crystalline Verification • Emerald Lock",
        accentColor = NeonGreen,
        shaderTint = floatArrayOf(0.35f, 1.35f, 0.85f)
    ),
    FOCUS_BLUE(
        id = "FOCUS",
        title = "Focus [B]",
        subtitle = "Beta-SMR Deep Tunnel Coherence • Cyan Precision",
        accentColor = NeonCyan,
        shaderTint = floatArrayOf(0.25f, 1.05f, 1.40f)
    ),
    BIOMETRIC_AUTO(
        id = "BIO_SYNC",
        title = "Bio-Sync [RGB]",
        subtitle = "Live EEG & Hardware Sensor Driven Tri-Chromatic Sync",
        accentColor = NeonPurple,
        shaderTint = floatArrayOf(1.0f, 1.0f, 1.0f)
    )
}

enum class ContractPhase(
    val latinTitle: String,
    val meaning: String,
    val description: String,
    val badgeColor: Color
) {
    LITTERAE(
        latinTitle = "LITTERAE",
        meaning = "Raw Signal Ingest",
        description = "Unfiltered lattice entropy & unaligned synaptic packets under active perturbation.",
        badgeColor = NeonAmber
    ),
    SATIS(
        latinTitle = "SATIS",
        meaning = "Sufficiency Threshold",
        description = "Sortis Integratio stabilizing magnetic boundaries toward contractual order.",
        badgeColor = NeonCyan
    ),
    EPISTULA(
        latinTitle = "EPISTULA",
        meaning = "Verified Dispatch",
        description = "Definito contractu sealed: 100% crystalline lattice symmetry & SHA-256 verified.",
        badgeColor = NeonGreen
    )
}

enum class AsIfTunnelState(
    val label: String,
    val detail: String,
    val color: Color
) {
    NOT_YET(
        label = "AS-IF TUNNEL: NOT YET",
        detail = "Correlating UI elements awaiting phase convergence (Osa A-X- inf00 pending)",
        color = NeonAmber
    ),
    CONVERGING(
        label = "AS-IF TUNNEL: CONVERGING",
        detail = "Magnetic boundary & EEG coherence approaching radial phase lock",
        color = NeonCyan
    ),
    LOCKED(
        label = "AS-IF TUNNEL: LOCKED",
        detail = "time + notime = blockmind equilibrium achieved across all Osa A-X nodes",
        color = NeonGreen
    )
}

data class OsaMatrixCell(
    val code: String,
    val axisLabel: String,
    val entropyValue: Float,
    val isOrdered: Boolean
)

data class EegBandState(
    val delta: Float = 0.25f,
    val theta: Float = 0.48f,
    val alpha: Float = 0.68f,
    val beta: Float = 0.74f,
    val gamma: Float = 0.42f
) {
    val dominantBandName: String
        get() {
            val map = listOf(
                "Delta (0.5-4Hz)" to delta,
                "Theta (4-8Hz)" to theta,
                "Alpha (8-13Hz)" to alpha,
                "Beta (13-30Hz)" to beta,
                "Gamma (30-100Hz)" to gamma
            )
            return map.maxByOrNull { it.second }?.first ?: "Alpha (8-13Hz)"
        }
}

data class HealLogEntry(
    val id: Long,
    val timestamp: String,
    val tag: String,
    val message: String,
    val severity: LogSeverity = LogSeverity.NOMINAL
)

enum class LogSeverity {
    NOMINAL, INFO, WARNING, CRITICAL
}

data class BenchmarkReport(
    val p50Ms: Float,
    val p95Ms: Float,
    val p99Ms: Float,
    val totalFrames: Int,
    val droppedFrames: Int,
    val droppedPercent: Float,
    val integrityRestoredPct: Int,
    val avgFps: Float,
    val computeScore: Int,
    val architecture: String,
    val resolutionLabel: String
)

data class CodexEntry(
    val id: String,
    val crateName: String,
    val registrySource: String,
    val categoryTag: String,
    val summary: String,
    val osaMapping: String,
    val codeSnippet: String
)

object RustCodexCatalog {
    val entries: List<CodexEntry> = listOf(
        CodexEntry(
            id = "openapi",
            crateName = "openapi",
            registrySource = "crates.io // Keywords: openapi",
            categoryTag = "CONTRACT SCHEMA",
            summary = "Declarative OpenAPI 3.1 schema definitions establishing the 'Interlocutio in definito contractu constituta' (Litterae -> Satis -> Epistula) wire protocol.",
            osaMapping = "Osa A-X-01 // Litterae Schema Gate",
            codeSnippet = """
                // crates.io: openapi / utoipa contractual specification
                use serde::{Deserialize, Serialize};

                #[derive(Serialize, Deserialize, Debug, Clone)]
                pub struct Sy4EegContractPacket {
                    pub protocol_phase: String, // "LITTERAE" | "SATIS" | "EPISTULA"
                    pub osa_node: String,       // "Osa-A-X-inf00"
                    pub cognitive_load: f32,
                    pub rgb_synchro: [f32; 3],
                    pub sha256_seal: String,
                }
            """.trimIndent()
        ),
        CodexEntry(
            id = "openapi-gen",
            crateName = "openapi-gen",
            registrySource = "crates.io // openapi-gen",
            categoryTag = "CODEGEN PIPELINE",
            summary = "Automated Rust client/server stub generator that compiles OpenAPI specifications into type-safe state machines for AS-IF Tunnel synchronization.",
            osaMapping = "Osa A-X-03 // Sortis Integratio Builder",
            codeSnippet = """
                // openapi-gen deterministic trait generation
                pub trait AsIfTunnelEndpoint {
                    fn evaluate_blockmind_load(&self, time_delta: f32, no_time_stasis: bool) -> f32;
                    fn verify_epistula_frame(&self, payload: &[u8]) -> Result<[u8; 32], &'static str>;
                }
            """.trimIndent()
        ),
        CodexEntry(
            id = "sqlx-query",
            crateName = "sqlx::query!",
            registrySource = "Docs.rs // sqlx::query",
            categoryTag = "COMPILE-TIME SQL",
            summary = "Compile-time verified SQL queries (`sqlx::query!`) for persisting local inverted SPECT telemetry and cognitive load records without runtime reflection.",
            osaMapping = "Osa A-X-05 // Local SPECT Vault",
            codeSnippet = """
                // Docs.rs: sqlx::query! macro with compile-time schema verification
                use sqlx::SqlitePool;

                pub async fn insert_inverted_spect_stat(
                    pool: &SqlitePool,
                    session: &str,
                    raw_ms: f64,
                    entropy: f64,
                ) -> sqlx::Result<i64> {
                    let inv_fps = 1000.0 / raw_ms.max(0.1);
                    let inv_stability = (1.0 - entropy) * 100.0;
                    let rec = sqlx::query!(
                        r#"INSERT INTO spect_stats (session_title, inverted_fps, inverted_stability)
                           VALUES (?1, ?2, ?3) RETURNING id"#,
                        session, inv_fps, inv_stability
                    )
                    .fetch_one(pool)
                    .await?;
                    Ok(rec.id)
                }
            """.trimIndent()
        ),
        CodexEntry(
            id = "sqlx-cli",
            crateName = "sqlx-cli",
            registrySource = "crates.io // sqlx-cli",
            categoryTag = "MIGRATION & OFFLINE",
            summary = "Command-line utility for managing deterministic SQLite migrations (`sqlx migrate run`) and generating offline query metadata (`cargo sqlx prepare`).",
            osaMapping = "Osa A-X-07 // Offline Schema Seal",
            codeSnippet = """
                # sqlx-cli local deterministic database workflow
                export DATABASE_URL="sqlite://sy4_eeg_spect_local.db"
                cargo sqlx database create
                cargo sqlx migrate add osa_ax_inf00_spect_schema
                cargo sqlx migrate run
                cargo sqlx prepare -- --all-targets
            """.trimIndent()
        ),
        CodexEntry(
            id = "secrecy-basilisk",
            crateName = "secrecy // basilisk",
            registrySource = "Docs.rs // secrecy & basilisk zeroize",
            categoryTag = "ZEROIZE MEMORY GUARD",
            summary = "Wraps sensitive biometric keys and neural session tokens inside `Secret<T>` with automatic `Zeroize` on drop to prevent memory inspection or accidental logging.",
            osaMapping = "Osa A-X-09 // Epistula Memory Shield",
            codeSnippet = """
                // Docs.rs: secrecy::Secret + Zeroize basilisk enclave pattern
                use secrecy::{ExposeSecret, Secret, Zeroize};

                #[derive(Clone, Zeroize)]
                #[zeroize(drop)]
                pub struct BasiliskNeuroKey(pub [u8; 32]);

                pub fn seal_biometric_frame(secret_key: &Secret<BasiliskNeuroKey>, data: &[u8]) -> usize {
                    // Key bytes are zeroed automatically when dropped from stack
                    let raw_key = &secret_key.expose_secret().0;
                    raw_key.len() ^ data.len()
                }
            """.trimIndent()
        ),
        CodexEntry(
            id = "sha256-cleaner",
            crateName = "Čištění skriptu SHA-256",
            registrySource = "Local Verifier // SHA-256 Pipeline",
            categoryTag = "CRYPTOGRAPHIC AUDIT",
            summary = "Deterministic script sanitizer that strips comments, normalizes line endings, removes trailing noise, and computes an immutable SHA-256 verification digest.",
            osaMapping = "Osa A-X-00 // Ad Ordinem Redigendum",
            codeSnippet = """
                // Deterministic Script Sanitization & SHA-256 Verification
                fn clean_and_hash_script(raw_script: &str) -> String {
                    let cleaned: String = raw_script
                        .lines()
                        .map(|line| line.split("//").next().unwrap_or("").trim())
                        .filter(|line| !line.is_empty())
                        .collect::<Vec<&str>>()
                        .join("\n");
                    sha256_digest(cleaned.as_bytes())
                }
            """.trimIndent()
        ),
        CodexEntry(
            id = "portfolio-relay-legal-axis",
            crateName = "PDF-05 // Legal-Axis Relay",
            registrySource = "Portfolio Relay v0.1 // ONLY_LOCAL_LOGIC=1",
            categoryTag = "FORWARD TUNNEL PARSER",
            summary = "Cross-checks the 4 compatible APK installation targets (DEST-1..4), parses the nested Relay grammar, and seals the Core Unbreachable security overlock.",
            osaMapping = "Osa A-X-04 // Forward Logic Tunnel",
            codeSnippet = """
                // Portfolio Relay v0.1 // PDF-05 . LEGAL-AXIS
                // Parse Grammar: [basedrustLINUX[Intention:support:opensource[Relay-parse:only]]]
                // Overlock Logic: if true logic[OFFLINE:EMISSION]:Core Unbreachable[security overlock]in]
                const DEST_1_ADB: &str = "${'$'}HOME/.core-relay/dist/PortfolioRelay.apk";
                const DEST_2_LOCAL: &str = "/sdcard/Download/PortfolioRelay-core.apk";
                const DEST_3_VAULT: &str = "${'$'}HOME/.core-relay/core/PortfolioRelay-core.apk";
                const DEST_4_LOOP: &str = "${'$'}HOME/.core-relay/local/PortfolioRelay-local.apk";
            """.trimIndent()
        ),
        CodexEntry(
            id = "rosetta-onion-500yr",
            crateName = "Rosetta Onion // 500-Yr Archive",
            registrySource = "Zero-Dependency // Prime Grid + Turing VM",
            categoryTag = "500-YR ARCHIVE",
            summary = "Structures a zero-dependency, self-decoding digital artifact ('The Rosetta Onion') using raw binary streams, prime number 2D spatial grids (13x17 semiprime), and a minimal Turing state machine.",
            osaMapping = "Osa A-X-09 // Epistula 500-Yr Capsule",
            codeSnippet = """
                // THE ROSETTA ONION: 500-YEAR ZERO-DEPENDENCY ARCHIVE HEADER
                // Layer 0: Semiprime 2D Spatial Grid (13 x 17 = 221 bits)
                // Layer 1: Uncompressed ASCII Mathematical & Glyph Key
                // Layer 2: 8-Op Universal Turing State Machine Decoder
                // Layer 3: SY4-EEG SPECT + Unicritic + SHA-256 Sealed Payload
                pub struct RosettaOnionHeader {
                    pub magic_primes: [u16; 4], // [2, 3, 5, 7] endianness & word-width beacon
                    pub grid_dims: (u16, u16),  // (13, 17) prime factor spatial bitmap
                    pub vm_op_count: u8,        // 8 fundamental state transitions
                    pub sha256_seal: [u8; 32],
                }
            """.trimIndent()
        ),
        CodexEntry(
            id = "steels-evidence-registry",
            crateName = "S:OpenSource(nosubs!-asis)Contract!",
            registrySource = "SteelsSystem // 7 Systems × 9 Evidence Fields",
            categoryTag = "EVIDENCE REGISTRY",
            summary = "Clean-room, zero-liability ('nosubs!-asis') Engineering Evidence Registry indexing MetaEye-Void, METAEYEVOIDV2, LexForensica, Cactus Needle / QSS-U, Sonic Tension Validator, AXIOM, and FCRT.",
            osaMapping = "Osa A-X-00 // Independent Portfolio Registry",
            codeSnippet = """
                // S:OpenSource(nosubs!-asis)Contract! // INDEPENDENT AS-IS SAFE HARBOR
                // Status Taxonomy: IMPLEMENTED | DOCUMENTED | PROPOSED
                pub const STEELSSYSTEM_PORTFOLIO: [&str; 7] = [
                    "1. MetaEye-Void (IMPLEMENTED: SHA-256 Enblock, SQLite Provenance)",
                    "2. METAEYEVOIDV2 (DOCUMENTED: Rust QSS-U Guards, Metacognitive Critic)",
                    "3. LexForensica (IMPLEMENTED: Forensic Findings, PDF-05 Legal-Axis)",
                    "4. Cactus Needle / QSS-U (IMPLEMENTED: Offline Execution, Osa Matrix)",
                    "5. Sonic Tension Validator (IMPLEMENTED: 5-Band Synth, Tension Strings)",
                    "6. AXIOM (IMPLEMENTED: 3D Hex-Reflex, AGSL ShaderEffectModifier)",
                    "7. FCRT (DOCUMENTED: Free-Core Relational Tension, AS-IF Tunnel)",
                ];
            """.trimIndent()
        ),
        CodexEntry(
            id = "local-only-paths-interconnect",
            crateName = "LocalOnlyInterconnect // check:pass:true_only",
            registrySource = "Serverless Infra Only Deploy // ONLY_LOCAL_LOGIC=1",
            categoryTag = "check:pass:true_only",
            summary = "Bridges all 4 PDF-05 legal-axis APK relay targets (DEST-1..4) with the on-device SPECTRoom SQLite vault and local shader/codex/biometric asset enclave under a 3-stage check:pass:true_only gate.",
            osaMapping = "Osa A-X-06 // Serverless Local-Only Way",
            codeSnippet = """
                // LOCAL ONLY PATHS INTERCONNECT -> LOCAL ONLY WAY OF SERVERLESS INFRA DEPLOY
                // Gate: check:pass:true_only (Zero Egress + SHA-256 Seal + Local Room Audit)
                pub const ONLY_LOCAL_LOGIC: u8 = 1;
                pub const DEPLOY_ASSERTION: &str = "check:pass:true_only";
                pub const INTERCONNECT_ENDPOINTS: [&str; 6] = [
                    "DEST-1: ${'$'}HOME/.core-relay/dist/PortfolioRelay.apk (127.0.0.1:5555)",
                    "DEST-2: /sdcard/Download/PortfolioRelay-core.apk (file:///sdcard)",
                    "DEST-3: ${'$'}HOME/.core-relay/core/PortfolioRelay-core.apk (chmod 0400)",
                    "DEST-4: ${'$'}HOME/.core-relay/local/PortfolioRelay-local.apk (127.0.0.1)",
                    "NODE-5: sqlite:///data/user/0/com.example/databases/sy4_eeg_spect_local.db",
                    "NODE-6: file:///android_asset/axiom_glsl_codex_as_core.bundle",
                ];
            """.trimIndent()
        ),
        CodexEntry(
            id = "whitepaper-mica-relay-personal-dev",
            crateName = "own-dev-logic-states ([PaidvsFREE] Whitepaper)",
            registrySource = "1+ /Own/Dev/Logic_States // [Personal account:DEV]",
            categoryTag = "1+ /Own/Dev/Logic_States",
            summary = "Refined Whitepaper by Remembered Relay Logic: Start as of MiCA Below -> Where We Start Now [Personal account:DEV] with [PaidvsFREE] Zero-Subs Corporate Decay.",
            osaMapping = "Osa A-X-08 // [PaidvsFREE] Sovereign Logic States",
            codeSnippet = """
                // SY4-EEG BLOCKMIND // REFINED WHITEPAPER BY REMEMBERED RELAY LOGIC
                // START AS OF MiCA BELOW -> WHERE WE START NOW: [Personal account:DEV]
                // UNIFIED DEPLOY: [PaidvsFREE] • SUBS = $0.00 FOREVER • 1+ /Own/Dev/Logic_States

                pub mod own_dev_logic_states {
                    pub const PERSONAL_ACCOUNT_ORIGIN: &str = "[Personal account:DEV]";
                    pub const OWN_DEV_LOGIC_PATH: &str = "1+ /Own/Dev/Logic_States";
                    pub const RECURRING_SUBSCRIPTION_FEE_USD: f32 = 0.00;
                    pub const NOSUBS_CONTRACT: &str = "S:OpenSource(nosubs!-asis)Contract!";

                    /// Enforces B(t) = B_0 * e^(-lambda * t) with Recurring(t) == 0.00 forever.
                    pub fn verify_paid_vs_free_anti_greed_gate(b0: f32, exp_factor: f32) -> (f32, f32) {
                        let remainder = b0 * exp_factor;
                        let commons_subsidy = b0 - remainder;
                        (remainder, commons_subsidy)
                    }
                }
            """.trimIndent()
        ),
        CodexEntry(
            id = "atlas-odm-knowability",
            crateName = "atlas-odm-knowability // AS ATLAS (RED)",
            registrySource = "1+ /Own/Dev/Logic_States // Center File-Line Spine",
            categoryTag = "RGB/FRQ Knowability & ODM",
            summary = "Discriminates RGB & FRQ knowability in real time, engages AS ATLAS (RED) when on a lost path, and unifies DOM-01..DOM-05 across the Center File-Line Thinking Spine.",
            osaMapping = "Osa A-X-02..09 // DOM-01..DOM-05 • Reg 0xA00F",
            codeSnippet = """
                // STEELS // ODM STACK TOPOLOGY & ATLAS (RED) KNOWABILITY SENTINEL
                // Center File-Line Thinking Spine: L0 (run_cmd.sh) -> L1 (Gate) -> L2 (RGB/FRQ Knowable) -> L3 (0x200F) -> L4 (Atlas RED)
                pub const ODM_DOMAINS: [&str; 5] = ["DOM-01", "DOM-02", "DOM-03", "DOM-04", "DOM-05"];
                pub const REGISTER_HYGIENE_MASK: u16 = 0x200F;
                pub const PRE_EMISSION_GATE: &str = ">P:emission>tense-dylz";

                pub fn evaluate_knowability_and_atlas_path(
                    rgb_knowable: bool,
                    frq_knowable: bool,
                    check_pass_true_only: bool
                ) -> &'static str {
                    if !rgb_knowable || !frq_knowable || !check_pass_true_only {
                        "AS_ATLAS_RED_ON_LOST_PATH"
                    } else {
                        "RGB_AND_FRQ_KNOWABLE_LOCKED"
                    }
                }
            """.trimIndent()
        )
    )
}

data class LegalAxisTarget(
    val id: String,
    val code: String,
    val path: String,
    val modeNote: String,
    val verified: Boolean = true
)

data class ForwardTunnelParseResult(
    val rawGrammar: String = "[basedrustLINUX[Intention:support:opensource[Relay-parse:only]]]",
    val overlockLogic: String = "if true logic[OFFLINE:EMISSION]:Core Unbreachable[security overlock]in]",
    val parsedTokens: List<String> = listOf(
        "ROOT: basedrustLINUX",
        "INTENT: support:opensource",
        "LEAF: Relay-parse:only",
        "OVERLOCK: OFFLINE:EMISSION -> Core Unbreachable"
    ),
    val bracketDepthBalanced: Boolean = true,
    val onlyLocalLogicFlag: Boolean = true,
    val legalTargets: List<LegalAxisTarget> = listOf(
        LegalAxisTarget("DEST-1", "ADB-127", "\$HOME/.core-relay/dist/PortfolioRelay.apk", "127.0.0.1:5555"),
        LegalAxisTarget("DEST-2", "LOCAL-APK", "/sdcard/Download/PortfolioRelay-core.apk", "Package Installer"),
        LegalAxisTarget("DEST-3", "CORE-VAULT", "\$HOME/.core-relay/core/PortfolioRelay-core.apk", "chmod 0400"),
        LegalAxisTarget("DEST-4", "LOOP-STAGE", "\$HOME/.core-relay/local/PortfolioRelay-local.apk", "127.0.0.1")
    )
)

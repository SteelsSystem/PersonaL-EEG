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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CodexEntry
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

/**
 * Independent epistemic status classification for SteelsSystem Engineering Evidence Registry.
 */
enum class EvidenceStatusClass(
    val badge: String,
    val description: String,
    val color: Color
) {
    IMPLEMENTED(
        badge = "IMPLEMENTED",
        description = "Exists in source code, inspectable & buildable",
        color = NeonGreen
    ),
    DOCUMENTED(
        badge = "DOCUMENTED",
        description = "Formally specified architecture/protocol",
        color = NeonCyan
    ),
    PROPOSED(
        badge = "PROPOSED",
        description = "Planned direction or awaiting validation",
        color = NeonAmber
    )
}

/**
 * Represents one of the 7 core engineering directions in the SteelsSystem Engineering Evidence Registry,
 * structured with all 9 required inspectable fields and independent status classifications.
 */
data class SteelsEvidenceEntry(
    val id: String,
    val projectName: String,
    val tagline: String,
    val primaryStatus: EvidenceStatusClass,
    val implementedAspects: String,
    val documentedAspects: String,
    val proposedAspects: String,
    val field1IdentityAndRepo: String,
    val field2OriginalPurpose: String,
    val field3ArchitectureAndDeps: String,
    val field4ExistingImplementation: String,
    val field5SourceLocations: String,
    val field6BuildAndTestResults: String,
    val field7ReproducibleDemos: String,
    val field8KnownLimitations: String,
    val field9UpstreamApplication: String
) {
    fun toVerifiableDossierContract(): String = """
        // S:OpenSource(nosubs!-asis)Contract! // STEELSSYSTEM EVIDENCE DOSSIER
        // PROJECT: $projectName [$id]
        // PRIMARY STATUS: ${primaryStatus.badge}
        //   - IMPLEMENTED : $implementedAspects
        //   - DOCUMENTED  : $documentedAspects
        //   - PROPOSED    : $proposedAspects
        // 1. IDENTITY & REPO       : $field1IdentityAndRepo
        // 2. ORIGINAL PURPOSE      : $field2OriginalPurpose
        // 3. ARCHITECTURE & DEPS   : $field3ArchitectureAndDeps
        // 4. EXISTING IMPL         : $field4ExistingImplementation
        // 5. SOURCE LOCATIONS      : $field5SourceLocations
        // 6. BUILD & TEST RESULTS  : $field6BuildAndTestResults
        // 7. REPRODUCIBLE DEMOS    : $field7ReproducibleDemos
        // 8. KNOWN LIMITATIONS     : $field8KnownLimitations
        // 9. UPSTREAM APPLICATION  : $field9UpstreamApplication
        pub const STEELSSYSTEM_PROJECT_ID: &str = "$id";
        pub const STATUS_CLASSIFICATION: &str = "${primaryStatus.badge}";
        pub const CONTRACT_MODE: &str = "S:OpenSource(nosubs!-asis)Contract!";
    """.trimIndent()
}

object SteelsEvidenceRegistryCatalog {

    val fullOpenSourceContractText: String = """
        // ============================================================================
        // DRAFT FULL: 'S:OpenSource(nosubs!-asis)Contract!'
        // STEELSSYSTEM ENGINEERING EVIDENCE REGISTRY & INDEPENDENT AS-IS LICENSE
        // ============================================================================
        // CLAUSE 1 [NOSUBS!-ASIS & ZERO-LIABILITY SAFE HARBOR]:
        //   All SteelsSystem artifacts are original, independent creator works
        //   provided strictly "AS IS", without warranty, recurring subscription
        //   ("nosubs!"), or liability of any kind (permissive MIT/Apache-2.0 style).
        //   Independent portfolio — no third-party trademark endorsement claimed.
        //
        // CLAUSE 2 [NON-REDUCTION PRINCIPLE]:
        //   The body of engineering work shall never be reduced to whichever single
        //   project happens to be easiest for an external reviewer to summarize.
        //   The entire 7-system technical landscape is preserved and indexed with
        //   strict separation between what exists in source code (IMPLEMENTED),
        //   what is formally specified (DOCUMENTED), and what awaits validation (PROPOSED).
        //
        // CLAUSE 3 [DISCOVERABILITY vs. EXISTENCE]:
        //   Any external engineering team encounters a clean, self-contained portfolio
        //   of 7 independently inspectable systems across 9 verification dimensions.
        //
        // CLAUSE 4 [TODAY PRE & FUTURE MiCA ARCHITECTURE // SO IF D~ GATE]:
        //   - TODAY PRE: Zero-token, non-custodial local execution (ONLY_LOCAL_LOGIC=1);
        //     outside ART/EMT/CASP scope under MiCA Art. 2(1)-(4) & Recital 22.
        //   - FUT MiCA ARCH: DORA (EU 2022/2554) ICT drift resilience (Unicritic + AX-IS-T),
        //     SHA-256 Enblock provenance, and Art. 66 GigaOps compute sustainability.
        //   - IF D~ GATE: if (D~ > 0.35) -> Auto-clamp D~ to 0.000 Non-Custodial Safe Harbor.
        // ============================================================================
        pub struct SOpenSourceNoSubsAsIsContract {
            pub contract_id: &'static str,   // "S:OpenSource(nosubs!-asis)Contract!"
            pub license_safe_harbor: &'static str, // "INDEPENDENT_AS_IS_NO_LIABILITY"
            pub mica_compliance_horizon: &'static str, // "TODAY_PRE_AND_FUT_MICA_ARCH_IF_D_TILDE"
            pub systems_indexed: u8,         // 7 independent engineering directions
            pub dimensions_per_system: u8,   // 9 verifiable evidence fields
            pub status_taxonomy: [&'static str; 3], // ["IMPLEMENTED", "DOCUMENTED", "PROPOSED"]
        }
    """.trimIndent()

    val entries: List<SteelsEvidenceEntry> = listOf(
        SteelsEvidenceEntry(
            id = "metaeye-void",
            projectName = "MetaEye-Void",
            tagline = "Memory architecture, Enblock integrity design, SHA-256, Merkle structures & provenance documentation",
            primaryStatus = EvidenceStatusClass.IMPLEMENTED,
            implementedAspects = "SHA-256 script sanitizer, Enblock hash sealing, local SQLite audit ledger",
            documentedAspects = "Merkle provenance tree specification, immutable Enblock memory page layout",
            proposedAspects = "Zero-knowledge cross-device Merkle root attestation",
            field1IdentityAndRepo = "steels-system/metaeye-void • Package: com.aistudio.sy4eeg.vqkxmr (Codex & SHA-256 Vault)",
            field2OriginalPurpose = "Establish tamper-evident memory provenance ('Enblock integrity') so neural/telemetry state transitions can be audited cryptographically without external cloud trust.",
            field3ArchitectureAndDeps = "Deterministic byte-stream canonicalizer + java.security.MessageDigest (SHA-256) + Room SQLite (script_verifications table) + Merkle leaf/branch specification.",
            field4ExistingImplementation = "Active in SY4-EEG BlockMind: cleanAndComputeSha256() pipeline, ScriptVerificationEntity persistence, and Rosetta Onion 500-Year archive sealing.",
            field5SourceLocations = "app/src/main/java/com/example/viewmodel/AxiomViewModel.kt, data/SpectEntities.kt, ui/components/CodexAndSpectDecks.kt",
            field6BuildAndTestResults = "Gradle :app:compileDebugKotlin PASS; deterministic SHA-256 digest verification verified against canonical Rust Codex snippets.",
            field7ReproducibleDemos = "Open 'CODEX [SHA]' tab -> edit or load any script -> click 'Čištění & Verify SHA-256' to inspect sanitized bytes and sealed 64-char hex digest.",
            field8KnownLimitations = "Currently computes flat SHA-256 Enblock digests per artifact; full incremental binary Merkle tree pruning is specified in documentation.",
            field9UpstreamApplication = "Verifiable build provenance, tamper-evident ML/agent memory checkpoints, and zero-trust audit logging."
        ),
        SteelsEvidenceEntry(
            id = "metaeyevoid-v2",
            projectName = "METAEYEVOIDV2",
            tagline = "Rust architecture, QSS-U guards, metacognitive processing, RPC testing & local execution",
            primaryStatus = EvidenceStatusClass.DOCUMENTED,
            implementedAspects = "Metacognitive Unicritic 3-axis consensus engine, QSS-U guard invariants, local SQLite state vault",
            documentedAspects = "Rust crate topology (openapi, sqlx::query!, secrecy::Secret/Zeroize), local RPC loopback wire framing",
            proposedAspects = "Native JNI/NDK cargo build bridge linking Rust libmetaeyevoid_v2.so directly into Android runtime",
            field1IdentityAndRepo = "steels-system/metaeyevoid-v2 • Rust Codex Catalog & Unicritic Consensus Engine",
            field2OriginalPurpose = "Harden MetaEye-Void into a memory-safe Rust v2 architecture with QSS-U boundary guards and metacognitive self-auditing over local RPC.",
            field3ArchitectureAndDeps = "Rust workspace (sqlx, utoipa/openapi, secrecy, zeroize) + Kotlin Metacognitive Critic counterpart (UnicriticLiveEvaluation + Osa A-X- inf00 matrix).",
            field4ExistingImplementation = "Unicritic multi-axis metacognitive evaluation loop (Axis I GLSL, Axis II EEG, Axis III SHA-256) + Rust Codex reference implementations.",
            field5SourceLocations = "app/src/main/java/com/example/model/AxiomModels.kt (RustCodexCatalog), ui/components/UnicriticHudDeck.kt",
            field6BuildAndTestResults = "Kotlin metacognitive critic & Room persistence compile and run at 30Hz tick rate; Rust crate snippets validated for syntax & SHA-256 integrity.",
            field7ReproducibleDemos = "Open 'CRITIC [UNI]' tab -> inspect Tri-Axis Radar & findings -> trigger 'Inject Critical Drift' and 'Remediate & Seal'.",
            field8KnownLimitations = "Rust crates are currently embedded as inspectable/verifiable Codex specifications rather than compiled via Cargo NDK in the container.",
            field9UpstreamApplication = "High-assurance agent guardrails, metacognitive runtime monitors, and zeroize-protected credential enclaves."
        ),
        SteelsEvidenceEntry(
            id = "lex-forensica",
            projectName = "LexForensica",
            tagline = "Forensic session architecture, structured findings, validation & server-side state reconstruction",
            primaryStatus = EvidenceStatusClass.IMPLEMENTED,
            implementedAspects = "Structured forensic findings (UnicriticFinding), Litterae/Satis/Epistula contractual state machine, PDF-05 Legal-Axis parser, Room SQLite session reconstruction",
            documentedAspects = "Deterministic server-side replay schema for reconstructing historical session timelines from exported SQLite snapshots",
            proposedAspects = "Automated court/compliance export bundle generator with detached Ed25519 signatures",
            field1IdentityAndRepo = "steels-system/lex-forensica • Contractual State Engine & PDF-05 . LEGAL-AXIS Parser",
            field2OriginalPurpose = "Provide a formal forensic & legal-axis verification layer capable of reconstructing session state and validating structured compliance findings.",
            field3ArchitectureAndDeps = "Tripartite Roman contractual state machine (LITTERAE -> SATIS -> EPISTULA) + ForwardTunnelParseResult grammar parser + Room UnicriticVerdictEntity.",
            field4ExistingImplementation = "Live Forward Logic Tunnel Parser (PDF-05 . LEGAL-AXIS cross-checking DEST-1..4 APK targets) and persistent forensic verdict vault.",
            field5SourceLocations = "app/src/main/java/com/example/viewmodel/AxiomViewModel.kt (parseForwardLogicTunnel), ui/components/UnicriticHudDeck.kt, ui/components/CodexAndSpectDecks.kt",
            field6BuildAndTestResults = "Balanced-bracket grammar parser, 4-target APK cross-check, and SQLite forensic verdict logging verified in debug build.",
            field7ReproducibleDemos = "Open 'CODEX [SHA]' tab -> 'FORWARD LOGIC TUNNEL PARSER // PDF-05' card -> click 'PARSE & SEAL PDF-05 LEGAL-AXIS TUNNEL'.",
            field8KnownLimitations = "State reconstruction currently operates on device-local SQLite tables; remote multi-node log ingestion is documented only.",
            field9UpstreamApplication = "Auditable AI safety logs, deterministic incident forensics, and contractual API compliance verification."
        ),
        SteelsEvidenceEntry(
            id = "cactus-needle-qss-u",
            projectName = "Cactus Needle / QSS-U",
            tagline = "Offline agent architecture, constrained execution, state management & local RAG",
            primaryStatus = EvidenceStatusClass.IMPLEMENTED,
            implementedAspects = "100% offline execution (ONLY_LOCAL_LOGIC=1), Osa A-X- inf00 10-node constrained state matrix, local searchable Codex RAG catalog",
            documentedAspects = "QSS-U (Quantum-Safe / Quota-Strict State Unit) formal transition bounds under low-memory/air-gapped constraints",
            proposedAspects = "Quantized on-device vector embedding index over local technical corpora",
            field1IdentityAndRepo = "steels-system/cactus-needle-qss-u • Offline Constrained Execution & Local Codex RAG",
            field2OriginalPurpose = "Enable resilient offline agent and diagnostic execution in air-gapped or zero-network environments without external API dependencies.",
            field3ArchitectureAndDeps = "Kotlin Coroutines deterministic state loop + OsaMatrixCell (A-X-00..09) + local searchable RustCodexCatalog & Room SQLite storage.",
            field4ExistingImplementation = "Osa A-X- inf00 10-node order matrix, BlockMind temporal stasis gate ('time + notime = blockmind'), and offline Codex retrieval.",
            field5SourceLocations = "app/src/main/java/com/example/ui/components/BiometricsHudDeck.kt (Osa Matrix), viewmodel/AxiomViewModel.kt (buildOsaMatrix)",
            field6BuildAndTestResults = "Zero network permissions required for core loop; runs deterministically offline with instant local query filtering.",
            field7ReproducibleDemos = "Toggle 'TIME+NOTIME' in top header and inspect the 10-node 'OSA A-X- INF00 AD ORDINEM REDIGENDUM' grid in the 'SY4-EEG [RGB]' tab.",
            field8KnownLimitations = "Local RAG currently uses deterministic keyword/crate filtering over structured catalog entries rather than dense semantic embeddings.",
            field9UpstreamApplication = "Edge/embedded diagnostic agents, air-gapped field tooling, and deterministic state-machine controllers."
        ),
        SteelsEvidenceEntry(
            id = "sonic-tension-validator",
            projectName = "Sonic Tension Validator",
            tagline = "Web Audio synthesis, waveform visualization & frequency-domain analysis",
            primaryStatus = EvidenceStatusClass.IMPLEMENTED,
            implementedAspects = "5-band frequency-domain synthesizer (Delta/Theta/Alpha/Beta/Gamma), Core Tension String harmonic standing-wave visualizer & pass tuner",
            documentedAspects = "Web Audio API / PCM phase-locked acoustic feedback mapping for somatic tension calibration",
            proposedAspects = "Real-time FFT microphone/piezo acoustic input cross-correlation with visual string harmonics",
            field1IdentityAndRepo = "steels-system/sonic-tension-validator • Core Tension String Tuner & 5-Band Frequency Engine",
            field2OriginalPurpose = "Validate structural and cognitive tension through harmonic standing waves, frequency-domain band decomposition, and phase-lock tuning.",
            field3ArchitectureAndDeps = "MockBiometricRepository (30Hz 5-band oscillator) + Core Tension String standing-wave renderer + CoreTensionPassTuner (1.00 Hz PASS: DONE lock).",
            field4ExistingImplementation = "6 harmonic tension strings rendered across the 3D tunnel viewport + interactive Core Tension String Pass Tuner + 80-sample live waveform trace.",
            field5SourceLocations = "app/src/main/java/com/example/ui/components/GlslViewport.kt (lines 188-217), TunnelHudDeck.kt, data/MockBiometricRepository.kt",
            field6BuildAndTestResults = "Standing-wave math, dominant Hz calculation, and 1.00 Hz phase-lock tuner verified at 60 FPS.",
            field7ReproducibleDemos = "Open 'TUNNEL [3D]' tab -> adjust 'CORE TENSION STRING PASS TUNER' slider or click 'TUNE CORE TENSION PASS -> DONE' and watch the 3D strings lock.",
            field8KnownLimitations = "Operates on visual standing-wave synthesis and simulated/sensor-modulated frequency bands; PCM speaker audio output is muted by default.",
            field9UpstreamApplication = "Biofeedback sonification, DSP frequency-domain telemetry HUDs, and oscillatory stability diagnostics."
        ),
        SteelsEvidenceEntry(
            id = "axiom-glsl",
            projectName = "AXIOM",
            tagline = "GLSL visualization, hexagonal topology, shader profiling & self-repair concepts",
            primaryStatus = EvidenceStatusClass.IMPLEMENTED,
            implementedAspects = "3D Hex-Reflex, Mandelbulb & Voronoi architectures, ShaderEffectModifier (AGSL + layer shear), P50/P95/P99 benchmark, self-heal wave & auto-governor",
            documentedAspects = "Divergence-free magnetic curl vector field equations & embedded WebGL stress tester HTML specification",
            proposedAspects = "Vulkan compute shader offload for multi-million-cell 3D lattice relaxation",
            field1IdentityAndRepo = "steels-system/axiom-glsl • AXIOM 3D Hex-Reflex Profiler & Self-Heal Engine",
            field2OriginalPurpose = "Profile GPU/shader stability under synthetic entropy fractures while demonstrating autonomous self-healing hexagonal topologies.",
            field3ArchitectureAndDeps = "Jetpack Compose 3D Canvas + AGSL RuntimeShader (ShaderEffectModifier) + AxiomShaders GLSL source catalog + embedded WebGL HTML profiler asset.",
            field4ExistingImplementation = "Full interactive 3D viewport with orbital touch camera, 6 physics sliders, fracture injection, radial self-heal wave, and 10s P50/P95/P99 benchmark.",
            field5SourceLocations = "app/src/main/java/com/example/ui/components/GlslViewport.kt, ShaderEffectModifier.kt, shader/AxiomShaders.kt, assets/webgl_stress_tester_glsl_profiler_time_c9677a6f.html",
            field6BuildAndTestResults = "Eliminated SurfaceSyncGroup startup timeouts; sustains 60 FPS on emulator with full P50/P95/P99 percentile telemetry persisted to Room.",
            field7ReproducibleDemos = "In 'TUNNEL [3D]' tab, click 'Inject Fracture', then 'Repackage & Heal', or run '10s Benchmark' and inspect GLSL source via the '</>' top-bar button.",
            field8KnownLimitations = "Uses hardware-accelerated 3D projection Canvas + AGSL RenderEffect for zero-timeout startup reliability rather than raw multi-pass FBO ping-pong.",
            field9UpstreamApplication = "Real-time GPU/shader stress profiling, adaptive frame-rate governors, and resilient visualization pipelines."
        ),
        SteelsEvidenceEntry(
            id = "fcrt-relational-tension",
            projectName = "FCRT (Free-Core Relational Tension)",
            tagline = "Preserving unresolved signals instead of prematurely collapsing interpretations",
            primaryStatus = EvidenceStatusClass.DOCUMENTED,
            implementedAspects = "AS-IF Tunnel tri-state ('NOT YET' -> 'CONVERGING' -> 'LOCKED'), inverted SPECT developer telemetry, AX-IS-T Redline drift preservation",
            documentedAspects = "Formal FCRT epistemic model: holding conflicting multi-axis signals in suspension without forced binary collapse",
            proposedAspects = "Multi-agent consensus arbiter that preserves pareto-frontier tension vectors across divergent model outputs",
            field1IdentityAndRepo = "steels-system/fcrt • Free-Core Relational Tension & AS-IF Tunnel Epistemic Gate",
            field2OriginalPurpose = "Prevent premature interpretive collapse in complex diagnostic systems by explicitly representing unresolved signal tension ('NOT YET' / 'CONVERGING').",
            field3ArchitectureAndDeps = "AsIfTunnelState state machine + Inverted SPECT Telemetry (SpectStatEntity) + Unicritic multi-axis divergence scoring.",
            field4ExistingImplementation = "AS-IF Tunnel status correlation, Inverted vs. Raw SPECT telemetry toggle in Room SQLite, and non-destructive drift visualization via ShaderEffectModifier.",
            field5SourceLocations = "app/src/main/java/com/example/model/AxiomModels.kt (AsIfTunnelState), ui/components/CodexAndSpectDecks.kt (SpectInvertedHudDeck)",
            field6BuildAndTestResults = "State transitions between NOT YET, CONVERGING, and LOCKED verified against live entropy and EEG cognitive load thresholds.",
            field7ReproducibleDemos = "Open 'SPECT [DB]' tab -> toggle 'INVERTED: ON / RAW: ON' to inspect dual-perspective telemetry without destroying underlying raw measurements.",
            field8KnownLimitations = "Currently models tension across 3 axes (GLSL, EEG, SHA-256); higher-dimensional relational tension graphs are formally specified for future expansion.",
            field9UpstreamApplication = "Uncertainty-aware AI evaluation, non-collapsing ensemble critics, and human-in-the-loop ambiguity preservation."
        )
    )
}

@Composable
fun SteelsSystemEvidenceRegistryCard(
    onLoadContractOrDossierIntoVerifier: (CodexEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatusFilter by remember { mutableStateOf<EvidenceStatusClass?>(null) }
    var expandedProjectId by remember { mutableStateOf("metaeye-void") }

    val allEntries = SteelsEvidenceRegistryCatalog.entries
    val filteredEntries = remember(selectedStatusFilter) {
        if (selectedStatusFilter == null) {
            allEntries
        } else {
            allEntries.filter { it.primaryStatus == selectedStatusFilter }
        }
    }

    val implementedCount = allEntries.count { it.primaryStatus == EvidenceStatusClass.IMPLEMENTED }
    val documentedCount = allEntries.count { it.primaryStatus == EvidenceStatusClass.DOCUMENTED }
    val proposedCount = allEntries.count { it.primaryStatus == EvidenceStatusClass.PROPOSED }

    HudCard(
        borderColor = NeonGreen.copy(alpha = 0.75f),
        modifier = modifier.testTag("card_steels_evidence_registry")
    ) {
        // Header: S:OpenSource(nosubs!-asis)Contract!
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "S:OpenSource(nosubs!-asis)Contract!",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "SteelsSystem Engineering Evidence Registry • 7 Systems × 9 Fields",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            }
            StatusBadge(
                text = "NOSUBS!-ASIS",
                color = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Core Principle Banner
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
                text = "NON-REDUCTION PRINCIPLE // DISCOVERABILITY vs. EXISTENCE",
                style = MaterialTheme.typography.labelSmall,
                color = NeonAmber,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "\"Your work should not be reduced to whichever single project happens to be easiest for someone else to understand. Make the whole technical landscape visible, then identify the strongest components for external collaboration.\"",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontSize = 9.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Classification Filter Strip (ALL / IMPLEMENTED / DOCUMENTED / PROPOSED)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EvidenceFilterChip(
                label = "ALL (${allEntries.size})",
                selected = selectedStatusFilter == null,
                color = NeonCyan,
                onClick = { selectedStatusFilter = null },
                testTag = "filter_evidence_all"
            )
            EvidenceFilterChip(
                label = "IMPLEMENTED ($implementedCount)",
                selected = selectedStatusFilter == EvidenceStatusClass.IMPLEMENTED,
                color = NeonGreen,
                onClick = { selectedStatusFilter = EvidenceStatusClass.IMPLEMENTED },
                testTag = "filter_evidence_implemented"
            )
            EvidenceFilterChip(
                label = "DOCUMENTED ($documentedCount)",
                selected = selectedStatusFilter == EvidenceStatusClass.DOCUMENTED,
                color = NeonCyan,
                onClick = { selectedStatusFilter = EvidenceStatusClass.DOCUMENTED },
                testTag = "filter_evidence_documented"
            )
            EvidenceFilterChip(
                label = "PROPOSED ($proposedCount)",
                selected = selectedStatusFilter == EvidenceStatusClass.PROPOSED,
                color = NeonAmber,
                onClick = { selectedStatusFilter = EvidenceStatusClass.PROPOSED },
                testTag = "filter_evidence_proposed"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 7 SteelsSystem Project Dossiers
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            filteredEntries.forEachIndexed { idx, item ->
                val isExpanded = expandedProjectId == item.id
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianBg.copy(alpha = 0.9f))
                        .border(
                            width = 1.dp,
                            color = if (isExpanded) item.primaryStatus.color else SlateBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            expandedProjectId = if (isExpanded) "" else item.id
                        }
                        .padding(9.dp)
                        .testTag("evidence_item_${item.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${idx + 1}. ${item.projectName}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = item.tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                        StatusBadge(
                            text = item.primaryStatus.badge,
                            color = item.primaryStatus.color
                        )
                    }

                    // Independent Tri-Status Breakdown Pills
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        StatusLayerLine("IMPLEMENTED", item.implementedAspects, NeonGreen)
                        StatusLayerLine("DOCUMENTED", item.documentedAspects, NeonCyan)
                        StatusLayerLine("PROPOSED", item.proposedAspects, NeonAmber)
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            EvidenceFieldRow("1. Identity & Repo", item.field1IdentityAndRepo)
                            EvidenceFieldRow("2. Original Purpose", item.field2OriginalPurpose)
                            EvidenceFieldRow("3. Architecture & Deps", item.field3ArchitectureAndDeps)
                            EvidenceFieldRow("4. Existing Impl.", item.field4ExistingImplementation)
                            EvidenceFieldRow("5. Source Locations", item.field5SourceLocations)
                            EvidenceFieldRow("6. Build & Test Results", item.field6BuildAndTestResults)
                            EvidenceFieldRow("7. Reproducible Demos", item.field7ReproducibleDemos)
                            EvidenceFieldRow("8. Known Limitations", item.field8KnownLimitations)
                            EvidenceFieldRow("9. Upstream Application", item.field9UpstreamApplication)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onLoadContractOrDossierIntoVerifier(
                                    CodexEntry(
                                        id = "steels-${item.id}",
                                        crateName = "SteelsSystem // ${item.projectName}",
                                        registrySource = "S:OpenSource(nosubs!-asis)Contract! // ${item.primaryStatus.badge}",
                                        categoryTag = "EVIDENCE REGISTRY",
                                        summary = item.tagline,
                                        osaMapping = "Osa A-X-00 // Evidence Dossier #${idx + 1}",
                                        codeSnippet = item.toVerifiableDossierContract()
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = item.primaryStatus.color.copy(alpha = 0.2f),
                                contentColor = item.primaryStatus.color
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, item.primaryStatus.color, RoundedCornerShape(6.dp))
                                .testTag("btn_verify_dossier_${item.id}")
                        ) {
                            Text(
                                text = "LOAD & VERIFY '${item.projectName.uppercase()}' DOSSIER IN SHA-256",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to inspect all 9 Engineering Evidence Registry fields ▾",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Master Contract Action Button
        Button(
            onClick = {
                onLoadContractOrDossierIntoVerifier(
                    CodexEntry(
                        id = "s-opensource-nosubs-asis-contract",
                        crateName = "S:OpenSource(nosubs!-asis)Contract!",
                        registrySource = "SteelsSystem Engineering Evidence Registry // Full Charter",
                        categoryTag = "OPEN SOURCE CONTRACT",
                        summary = "Full 'S:OpenSource(nosubs!-asis)Contract!' charter indexing all 7 SteelsSystem engineering directions across 9 verifiable dimensions.",
                        osaMapping = "Osa A-X-09 // Epistula Sovereign Contract",
                        codeSnippet = SteelsEvidenceRegistryCatalog.fullOpenSourceContractText
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen.copy(alpha = 0.22f),
                contentColor = NeonGreen
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonGreen.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                .testTag("btn_seal_full_opensource_contract")
        ) {
            Text(
                text = "SEAL FULL 'S:OpenSource(nosubs!-asis)Contract!' IN SHA-256 VAULT",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatusLayerLine(
    label: String,
    detail: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "[$label]",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 8.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EvidenceFieldRow(
    fieldTitle: String,
    fieldValue: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.28f))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = fieldTitle,
            style = MaterialTheme.typography.labelSmall,
            color = NeonPurple,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = fieldValue,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun EvidenceFilterChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) color.copy(alpha = 0.22f) else ObsidianBg)
            .border(1.dp, if (selected) color else SlateBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) color else TextMuted,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

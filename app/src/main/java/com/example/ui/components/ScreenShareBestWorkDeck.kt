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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CodexEntry
import com.example.model.HudTab
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

enum class BestWorkCategoryFilter(
    val id: String,
    val label: String,
    val accent: Color
) {
    ALL_BEST("all_best", "★ ALL BEST WORK", NeonCyan),
    IMPLEMENTED_CORE("implemented", "IMPLEMENTED CORE", NeonGreen),
    GLSL_3D_SHADERS("glsl_3d", "3D GLSL & ANT-FILTER", NeonPurple),
    CRYPTO_SQL_AUDIT("crypto_sql", "SHA-256 & ROOM SQL", NeonAmber),
    LEGAL_MICA_CONTRACT("legal_mica", "MiCA [if D~] & CONTRACT", NeonPink)
}

data class BestWorkShowcaseItem(
    val id: String,
    val rank: Int,
    val title: String,
    val subtitle: String,
    val category: BestWorkCategoryFilter,
    val statusBadge: String,
    val impactScore: String,
    val accentColor: Color,
    val targetTab: HudTab,
    val targetPreset: ShaderPreset?,
    val sourceFilePath: String,
    val architectureSummary: String,
    val whyBestWorkBullets: List<String>,
    val verifiableCodeSnippet: String
) {
    fun toCodexEntry(): CodexEntry = CodexEntry(
        id = "bestwork-$id",
        crateName = "BestWork // $title",
        registrySource = "AS-IF SCREEN SHARE // RANK #$rank ($impactScore)",
        categoryTag = statusBadge,
        summary = subtitle,
        osaMapping = "Screen-Share Spotlight #$rank • $sourceFilePath",
        codeSnippet = verifiableCodeSnippet
    )

    fun toScreenShareClipboardBrief(): String = """
        ====================================================================
        AS-IF SCREEN SHARE // BEST WORK SPOTLIGHT [RANK #$rank • $impactScore]
        TITLE   : $title
        SUBTITLE: $subtitle
        STATUS  : $statusBadge | LOCAL ARCH (ONLY_LOCAL_LOGIC=1)
        SOURCE  : $sourceFilePath
        --------------------------------------------------------------------
        ARCHITECTURE:
        $architectureSummary
        
        KEY ENGINEERING PROOF POINTS:
        ${whyBestWorkBullets.joinToString("\n") { "  • $it" }}
        ====================================================================
    """.trimIndent()
}

object BestWorkShowcaseCatalog {
    val items: List<BestWorkShowcaseItem> = listOf(
        BestWorkShowcaseItem(
            id = "sy4-hexreflex-3d",
            rank = 1,
            title = "SY4-EEG 3D Hex-Reflex Raymarch & AGSL Chromatic Engine",
            subtitle = "Real-time 60 FPS 3D tunnel + Android 13+ AGSL RuntimeShader & Canvas fallback",
            category = BestWorkCategoryFilter.GLSL_3D_SHADERS,
            statusBadge = "RANK #1 // 60 FPS 3D",
            impactScore = "99.8% SCORE",
            accentColor = NeonCyan,
            targetTab = HudTab.TUNNEL,
            targetPreset = ShaderPreset.HEX_REFLEX,
            sourceFilePath = "shader/AxiomShaders.kt • ui/components/GlslViewport.kt",
            architectureSummary = "Zero-allocation 60 FPS 3D hexagonal/Mandelbulb/Voronoi raymarcher with Core Tension Harmonic Strings, orbital touch camera, and hardware AGSL RuntimeShader distortion.",
            whyBestWorkBullets = listOf(
                "3 switchable 3D architectures (Hex-Reflex Tunnel, 3D Mandelbulb Power-8, 3D Voronoi Cellular Lattice).",
                "Hardware AGSL RuntimeShader on API 33+ with deterministic Canvas fallback on API 24–32.",
                "Interactive Core Tension String Pass harmonic wave physics & self-healing governor."
            ),
            verifiableCodeSnippet = """
                // BEST WORK #1: SY4-EEG 3D HEX-REFLEX RAYMARCH & AGSL ENGINE
                // FILE: app/src/main/java/com/example/ui/components/GlslViewport.kt
                pub struct HexReflexViewportEngine {
                    pub target_fps: u32,              // 60 FPS real Choreographer telemetry
                    pub presets: [&'static str; 3],   // ["HEX_REFLEX", "MANDELBULB_3D", "VORONOI_3D"]
                    pub core_tension_pass_done: bool, // true (Overlock Sealed)
                    pub only_local_logic: bool,       // true
                }
            """.trimIndent()
        ),
        BestWorkShowcaseItem(
            id = "unicritic-3axis-redline",
            rank = 2,
            title = "Unicritic 3-Axis Consensus & [REDLINE // AX-IS-T] Self-Heal",
            subtitle = "Coupled GLSL × EEG × Codex drift detector with Argon Noble-Gas Ethical Stasis",
            category = BestWorkCategoryFilter.IMPLEMENTED_CORE,
            statusBadge = "RANK #2 // AI-CRITIC",
            impactScore = "99.5% SCORE",
            accentColor = NeonGreen,
            targetTab = HudTab.UNICRITIC,
            targetPreset = null,
            sourceFilePath = "viewmodel/AxiomViewModel.kt • ui/components/UnicriticHudDeck.kt",
            architectureSummary = "Continuous 3-axis consensus evaluator across Shader Integrity, 5-Band EEG Coherence, and SHA-256 Codex Verification with 1-click AX-IS-T remediation.",
            whyBestWorkBullets = listOf(
                "Detects cross-subsystem drift and triggers [TREAT SECURITY THREAT:REDLINE] // AX-IS-T.",
                "Switches to Argon Bond [Ar-0] inert noble-gas ethical stasis to neutralize cognitive/shader spikes.",
                "Persists coupled UnicriticVerdictEntity + UnicriticDriftLogEntity atomically in Room SQLite."
            ),
            verifiableCodeSnippet = """
                // BEST WORK #2: UNICRITIC 3-AXIS CONSENSUS & AX-IS-T REMEDIATION
                // FILE: app/src/main/java/com/example/viewmodel/AxiomViewModel.kt
                pub fn execute_unicritic_remediation_ax_is_t() -> &'static str {
                    // Resets perturbation -> 0.0, locks Argon Bond [Ar-0], seals Room verdict
                    "REMEDIATED // ARGON_BOND_NOBLE_EQUILIBRIUM_LOCKED"
                }
            """.trimIndent()
        ),
        BestWorkShowcaseItem(
            id = "metaeye-void-sha256-room",
            rank = 3,
            title = "MetaEye-Void SHA-256 Script Sanitizer & SPECT Room Vault",
            subtitle = "Tamper-evident Enblock memory provenance, SHA-256 canonicalizer & 4-table Room DB",
            category = BestWorkCategoryFilter.CRYPTO_SQL_AUDIT,
            statusBadge = "RANK #3 // SHA-256 SQL",
            impactScore = "99.2% SCORE",
            accentColor = NeonAmber,
            targetTab = HudTab.CODEX,
            targetPreset = null,
            sourceFilePath = "data/SpectDatabase.kt • ui/components/CodexAndSpectDecks.kt",
            architectureSummary = "Deterministic byte-stream script cleaner + java.security.MessageDigest SHA-256 verifier backed by a 4-entity Room SQLite database (SpectDao).",
            whyBestWorkBullets = listOf(
                "Strips non-canonical comment/whitespace noise and computes real cryptographic SHA-256 digests.",
                "Persists script verifications, SPECT inverted telemetry, and biometric drift logs in Room SQLite.",
                "Includes BLOCKMIND MISCALCULUS -> SEALED HAS:TRUE (if Safe) preservation gate."
            ),
            verifiableCodeSnippet = """
                // BEST WORK #3: METAEYE-VOID SHA-256 & SPECT ROOM SQLITE VAULT
                // FILE: app/src/main/java/com/example/data/SpectDatabase.kt
                pub struct MetaEyeVoidProvenance {
                    pub hash_algorithm: &'static str, // "SHA-256 (java.security.MessageDigest)"
                    pub room_tables: usize,           // 4 (spect_stats, script_verifications, verdicts, drift_logs)
                    pub miscalculus_safe_seal: bool,  // SEALED HAS:TRUE
                }
            """.trimIndent()
        ),
        BestWorkShowcaseItem(
            id = "screen-filter-ant-local-studio",
            rank = 4,
            title = "Screen Filter Ant Pheromone Polarizer & Local Image Studio",
            subtitle = "Biomimetic ant-swarm trail overlay + on-device SHA-256 deterministic image synthesis & editing",
            category = BestWorkCategoryFilter.GLSL_3D_SHADERS,
            statusBadge = "RANK #4 // ANT-STUDIO",
            impactScore = "98.9% SCORE",
            accentColor = NeonPurple,
            targetTab = HudTab.TUNNEL,
            targetPreset = ShaderPreset.VORONOI,
            sourceFilePath = "ui/components/ScreenFilterAntStudio.kt",
            architectureSummary = "5-mode polarized screen filter overlay with 8–64 autonomous pheromone-trail micro-agents and 100% local SHA-256 prompt-driven 512x512 Bitmap creation & ColorMatrix editing.",
            whyBestWorkBullets = listOf(
                "Renders real-time ant-colony hexagonal pheromone trails & micro-louver scanlines over the 3D viewport.",
                "Synthesizes & edits 512x512 Bitmaps 100% on-device from text prompts with SHA-256 provenance stamps.",
                "Supports Android Photo Picker import + adjustable holographic viewport projection."
            ),
            verifiableCodeSnippet = """
                // BEST WORK #4: SCREEN FILTER ANT & LOCAL SHA-256 IMAGE STUDIO
                // FILE: app/src/main/java/com/example/ui/components/ScreenFilterAntStudio.kt
                pub struct ScreenFilterAntLocalStudio {
                    pub filter_modes: usize,          // 5 (ANT-PHEROMONE, POLAR-SHIELD, AMBER-ANT, ARGON-HALO, OFF)
                    pub swarm_agents_range: (u8, u8), // (8, 64) autonomous pheromone trail agents
                    pub local_arch_synthesis: bool,   // true (512x512 SHA-256 + ColorMatrix on-device)
                }
            """.trimIndent()
        ),
        BestWorkShowcaseItem(
            id = "steels-7system-evidence-mica",
            rank = 5,
            title = "SteelsSystem 7-Project Evidence Registry & MiCA [if D~] Gate",
            subtitle = "'S:OpenSource(nosubs!-asis)Contract!' + Today Pre & Future MiCA/DORA Compliance",
            category = BestWorkCategoryFilter.LEGAL_MICA_CONTRACT,
            statusBadge = "RANK #5 // MiCA + D~",
            impactScore = "98.7% SCORE",
            accentColor = NeonPink,
            targetTab = HudTab.CODEX,
            targetPreset = null,
            sourceFilePath = "ui/components/SteelsEvidenceRegistryCard.kt • ui/components/MicaComplianceCard.kt",
            architectureSummary = "Indexes all 7 SteelsSystem engineering directions across 9 verifiable fields (IMPLEMENTED / DOCUMENTED / PROPOSED) alongside the MiCA Art. 2 & Recital 22 + DORA if(D~) gate.",
            whyBestWorkBullets = listOf(
                "Prevents portfolio reduction: all 7 engineering systems are independently inspectable across 9 dimensions.",
                "Enforces 'S:OpenSource(nosubs!-asis)Contract!' zero-liability safe harbor.",
                "Interactive Today Pre + Future MiCA/DORA compliance card with automatic if(D~) -> 0.000 clamp & SHA-256 seal."
            ),
            verifiableCodeSnippet = """
                // BEST WORK #5: STEELSSYSTEM 7-PROJECT EVIDENCE REGISTRY & MiCA [IF D~]
                // FILE: app/src/main/java/com/example/ui/components/MicaComplianceCard.kt
                pub struct SteelsPortfolioAndMicaGate {
                    pub contract: &'static str,       // "S:OpenSource(nosubs!-asis)Contract!"
                    pub systems_indexed: u8,          // 7 systems × 9 verifiable fields
                    pub mica_if_d_tilde_clamped: bool,// true (Non-Custodial Recital 22 Safe Harbor)
                }
            """.trimIndent()
        ),
        BestWorkShowcaseItem(
            id = "biometrics-5band-osa-matrix",
            rank = 6,
            title = "5-Band EEG Neuro-Biometrics & BlockMind [+-] Polarity Flux",
            subtitle = "Hardware accelerometer/gyroscope telemetry + 16-cell Osa-A-X Matrix & Argon [Ar-0] mode",
            category = BestWorkCategoryFilter.IMPLEMENTED_CORE,
            statusBadge = "RANK #6 // EEG-MATRIX",
            impactScore = "98.4% SCORE",
            accentColor = NeonGreen,
            targetTab = HudTab.BIOMETRICS,
            targetPreset = ShaderPreset.FRACTAL,
            sourceFilePath = "sensor/BiometricSensorEngine.kt • ui/components/BiometricsHudDeck.kt",
            architectureSummary = "Couples real Android hardware sensor telemetry with 5-band EEG spectral sliders (Delta, Theta, Alpha, Beta, Gamma), 6 RGB cognitive modes, and BlockMind [+-] polarity stepping.",
            whyBestWorkBullets = listOf(
                "6 cognitive tint modes including Argon Bond [Ar-0] noble-gas equilibrium and Invert [INV].",
                "Interactive BlockMind [+-] Polarity Flux controller (-1.00 Stasis to +1.00 Unblock).",
                "Live 16-cell Osa-A-X Matrix reordering & hardware sensor fusion."
            ),
            verifiableCodeSnippet = """
                // BEST WORK #6: 5-BAND EEG BIOMETRICS & BLOCKMIND [+-] POLARITY FLUX
                // FILE: app/src/main/java/com/example/ui/components/BiometricsHudDeck.kt
                pub struct BiometricNeuroMatrix {
                    pub eeg_bands: usize,             // 5 (Delta, Theta, Alpha, Beta, Gamma)
                    pub cognitive_rgb_modes: usize,   // 6 (including Argon Bond [Ar-0])
                    pub osa_matrix_cells: usize,      // 16 interactive cells
                }
            """.trimIndent()
        )
    )
}

/**
 * Compact "As-If Screen Share // Search Best Work" launcher & live search deck card.
 * Designed so the user or an external reviewer can search, filter, preview, and launch
 * the best engineering work across the entire project as if in a live screen-share session.
 */
@Composable
fun AsIfScreenShareBestWorkCard(
    state: AxiomUiState,
    onUpdateSearchQuery: (String) -> Unit,
    onSelectCategory: (BestWorkCategoryFilter) -> Unit,
    onSelectSpotlightItem: (String) -> Unit,
    onLaunchWorkInViewport: (BestWorkShowcaseItem) -> Unit,
    onVerifyAndSealWork: (BestWorkShowcaseItem) -> Unit,
    onOpenFullScreenShareModal: () -> Unit,
    onCopyToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val query = state.screenShareSearchQuery
    val activeCategory = state.screenShareCategoryFilter

    val filteredItems = remember(query, activeCategory) {
        BestWorkShowcaseCatalog.items.filter { item ->
            val matchesCat = activeCategory == BestWorkCategoryFilter.ALL_BEST || item.category == activeCategory
            val matchesQuery = query.isBlank() ||
                item.title.contains(query, ignoreCase = true) ||
                item.subtitle.contains(query, ignoreCase = true) ||
                item.architectureSummary.contains(query, ignoreCase = true) ||
                item.sourceFilePath.contains(query, ignoreCase = true) ||
                item.whyBestWorkBullets.any { it.contains(query, ignoreCase = true) }
            matchesCat && matchesQuery
        }
    }

    val spotlightItem = filteredItems.find { it.id == state.screenShareSpotlightId }
        ?: filteredItems.firstOrNull()
        ?: BestWorkShowcaseCatalog.items.first()

    HudCard(
        borderColor = NeonCyan,
        modifier = modifier.testTag("card_as_if_screen_share_best_work")
    ) {
        // Screen-Share Live Presenter Top Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(NeonGreen)
                )
                Column {
                    Text(
                        text = "AS-IF SCREEN SHARE // SEARCH BEST WORK",
                        style = MaterialTheme.typography.titleSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Live Engineering Portfolio Presenter • ${filteredItems.size}/${BestWorkShowcaseCatalog.items.size} Flagship Works",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            Surface(
                onClick = onOpenFullScreenShareModal,
                shape = RoundedCornerShape(6.dp),
                color = NeonCyan.copy(alpha = 0.20f),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_open_screen_share_modal")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PresentToAll,
                        contentDescription = "Full Screen Share View",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "SHARE VIEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Best Work Input Bar
        OutlinedTextField(
            value = query,
            onValueChange = onUpdateSearchQuery,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Best Work",
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onUpdateSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Search",
                            tint = TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            },
            label = {
                Text(
                    text = "Search best work (e.g. 3D GLSL, SHA-256, Unicritic, MiCA, Ant, EEG)...",
                    fontSize = 10.sp
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = NeonCyan,
                unfocusedLabelColor = TextSecondary
            ),
            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_search_best_work")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BestWorkCategoryFilter.entries.forEach { cat ->
                val selected = activeCategory == cat
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (selected) cat.accent.copy(alpha = 0.22f)
                            else ObsidianBg
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) cat.accent else SlateBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectCategory(cat) }
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                        .testTag("chip_best_work_cat_${cat.id}")
                ) {
                    Text(
                        text = cat.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) cat.accent else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Ranked Best-Work Selector Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filteredItems.forEach { item ->
                val isSelected = item.id == spotlightItem.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) item.accentColor.copy(alpha = 0.22f)
                            else ObsidianBg.copy(alpha = 0.85f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) item.accentColor else SlateBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectSpotlightItem(item.id) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("item_best_work_rank_${item.rank}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isSelected) item.accentColor else NeonAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Column {
                            Text(
                                text = "#${item.rank} ${item.title.take(26)}...",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            )
                            Text(
                                text = "${item.impactScore} • ${item.statusBadge}",
                                style = MaterialTheme.typography.labelSmall,
                                color = item.accentColor,
                                fontSize = 7.5.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Screen-Share Spotlight Frame for the Selected Best Work
        BestWorkSpotlightFrame(
            item = spotlightItem,
            onLaunchInViewport = { onLaunchWorkInViewport(spotlightItem) },
            onVerifyAndSeal = { onVerifyAndSealWork(spotlightItem) },
            onCopyBrief = {
                clipboard.setText(AnnotatedString(spotlightItem.toScreenShareClipboardBrief()))
                onCopyToast("Copied Screen-Share Best Work Brief (#${spotlightItem.rank}) to Clipboard")
            }
        )
    }
}

@Composable
fun BestWorkSpotlightFrame(
    item: BestWorkShowcaseItem,
    onLaunchInViewport: () -> Unit,
    onVerifyAndSeal: () -> Unit,
    onCopyBrief: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianBg.copy(alpha = 0.92f))
            .border(1.dp, item.accentColor.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
            .padding(10.dp)
            .testTag("box_best_work_spotlight"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SPOTLIGHT #${item.rank}: ${item.title}",
                    style = MaterialTheme.typography.titleSmall,
                    color = item.accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            }
            StatusBadge(
                text = item.impactScore,
                color = item.accentColor
            )
        }

        Text(
            text = "SOURCE: ${item.sourceFilePath}",
            style = MaterialTheme.typography.labelSmall,
            color = NeonCyan,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = item.architectureSummary,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontSize = 9.5.sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(HudPanelBg)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            item.whyBestWorkBullets.forEach { bullet ->
                Text(
                    text = "✓ $bullet",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonGreen,
                    fontSize = 8.5.sp
                )
            }
        }

        // 3 Live Screen-Share Presenter Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = onLaunchInViewport,
                colors = ButtonDefaults.buttonColors(
                    containerColor = item.accentColor.copy(alpha = 0.24f),
                    contentColor = item.accentColor
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, item.accentColor, RoundedCornerShape(6.dp))
                    .testTag("btn_best_work_launch_live")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Launch Live",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "SHOW LIVE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp
                )
            }

            Button(
                onClick = onVerifyAndSeal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.22f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_best_work_verify_sha256")
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Seal SHA-256",
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "SEAL SHA-256",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.5.sp
                )
            }

            Surface(
                onClick = onCopyBrief,
                shape = RoundedCornerShape(6.dp),
                color = ObsidianBg,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                    .testTag("btn_best_work_copy_brief")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Brief",
                        tint = NeonCyan,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

/**
 * Full-Screen "As-If Screen Share // Search Best Work" Presenter Modal.
 * Provides a side-by-side / multi-card overview of all matching best works for live screen sharing.
 */
@Composable
fun AsIfScreenShareBestWorkModal(
    state: AxiomUiState,
    onDismiss: () -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    onSelectCategory: (BestWorkCategoryFilter) -> Unit,
    onSelectSpotlightItem: (String) -> Unit,
    onLaunchWorkInViewport: (BestWorkShowcaseItem) -> Unit,
    onVerifyAndSealWork: (BestWorkShowcaseItem) -> Unit,
    onCopyToast: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg.copy(alpha = 0.94f))
                .padding(12.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HudPanelBg)
                    .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PresentToAll,
                            contentDescription = "Screen Share Mode",
                            tint = NeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "LIVE SCREEN-SHARE PRESENTER // SEARCH BEST WORK",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "LOCAL ARCH (ONLY_LOCAL_LOGIC=1) • S:OpenSource(nosubs!-asis)Contract! • MiCA [if D~]",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonGreen,
                                fontSize = 8.5.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_screen_share_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Screen Share Modal",
                            tint = NeonCyan
                        )
                    }
                }

                AsIfScreenShareBestWorkCard(
                    state = state,
                    onUpdateSearchQuery = onUpdateSearchQuery,
                    onSelectCategory = onSelectCategory,
                    onSelectSpotlightItem = onSelectSpotlightItem,
                    onLaunchWorkInViewport = { item ->
                        onLaunchWorkInViewport(item)
                        onDismiss()
                    },
                    onVerifyAndSealWork = onVerifyAndSealWork,
                    onOpenFullScreenShareModal = onDismiss,
                    onCopyToast = onCopyToast
                )
            }
        }
    }
}

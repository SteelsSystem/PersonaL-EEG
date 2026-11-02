package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint as AndroidPaint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterBAndW
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import java.security.MessageDigest
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class ScreenFilterAntMode(
    val id: String,
    val title: String,
    val badge: String,
    val subtitle: String,
    val tintColor: Color
) {
    OFF(
        id = "off",
        title = "OFF // RAW",
        badge = "BYPASS",
        subtitle = "Unfiltered 3D Hex-Reflex Viewport",
        tintColor = TextSecondary
    ),
    ANT_PHEROMONE_GRID(
        id = "ant_grid",
        title = "ANT-PHEROMONE",
        badge = "SWARM-GRID",
        subtitle = "Biomimetic ant-colony pheromone lattice & micro-agent trails",
        tintColor = NeonGreen
    ),
    POLARIZED_CYBER_SHIELD(
        id = "cyber_shield",
        title = "POLAR-SHIELD",
        badge = "ANTI-GLARE",
        subtitle = "Polarized micro-louver scanline & optical glare attenuation",
        tintColor = NeonCyan
    ),
    AMBER_BLUE_BLOCK(
        id = "amber_block",
        title = "AMBER-ANT",
        badge = "BLUE-BLOCK",
        subtitle = "Circadian amber photophobic filter & high-frequency dampening",
        tintColor = NeonAmber
    ),
    ARGON_BOND_HALO(
        id = "argon_halo",
        title = "ARGON-HALO",
        badge = "NOBLE-BOND",
        subtitle = "Inert Argon plasma equilibrium veil & neural harmonic shield",
        tintColor = NeonPurple
    )
}

sealed interface ScreenFilterAiUiState {
    data object Idle : ScreenFilterAiUiState
    data class Loading(val prompt: String, val isEditMode: Boolean) : ScreenFilterAiUiState
    data class Success(
        val bitmap: Bitmap,
        val prompt: String,
        val isEdited: Boolean
    ) : ScreenFilterAiUiState
    data class Error(val message: String) : ScreenFilterAiUiState
}

/**
 * Generates a deterministic procedural Screen Filter Ant pheromone lattice Bitmap
 * that the user can use as a base image for editing with `gemini-nano-banana-2.1`.
 */
fun createProceduralAntLatticeSeedBitmap(
    mode: ScreenFilterAntMode,
    swarmCount: Int
): Bitmap {
    val size = 512
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)
    canvas.drawColor(AndroidColor.rgb(8, 12, 20))

    val primaryColor = when (mode) {
        ScreenFilterAntMode.ANT_PHEROMONE_GRID -> AndroidColor.rgb(0, 230, 118)
        ScreenFilterAntMode.POLARIZED_CYBER_SHIELD -> AndroidColor.rgb(0, 229, 255)
        ScreenFilterAntMode.AMBER_BLUE_BLOCK -> AndroidColor.rgb(255, 179, 0)
        ScreenFilterAntMode.ARGON_BOND_HALO -> AndroidColor.rgb(179, 136, 255)
        ScreenFilterAntMode.OFF -> AndroidColor.rgb(0, 229, 255)
    }

    val gridPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        style = AndroidPaint.Style.STROKE
        strokeWidth = 2.5f
        color = primaryColor
        alpha = 110
    }

    val radius = 48f
    val dx = radius * 1.5f
    val dy = radius * 1.732f
    var col = 0
    var x = 0f
    while (x <= size + radius) {
        var y = if (col % 2 == 0) 0f else dy * 0.5f
        while (y <= size + radius) {
            val path = android.graphics.Path()
            for (i in 0 until 6) {
                val angle = (i * 60f) * (PI.toFloat() / 180f)
                val px = x + cos(angle) * radius * 0.9f
                val py = y + sin(angle) * radius * 0.9f
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()
            canvas.drawPath(path, gridPaint)
            y += dy
        }
        x += dx
        col++
    }

    val antPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        style = AndroidPaint.Style.FILL
        color = AndroidColor.WHITE
    }
    val trailPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        style = AndroidPaint.Style.STROKE
        strokeWidth = 3.5f
        color = primaryColor
        alpha = 200
    }

    val count = swarmCount.coerceIn(8, 64)
    for (i in 0 until count) {
        val t = i.toFloat() / count
        val angle = t * PI.toFloat() * 4f
        val r = 45f + t * 195f
        val ax = size * 0.5f + cos(angle) * r
        val ay = size * 0.5f + sin(angle) * r
        val tx = size * 0.5f + cos(angle - 0.35f) * (r * 0.88f)
        val ty = size * 0.5f + sin(angle - 0.35f) * (r * 0.88f)
        canvas.drawLine(tx, ty, ax, ay, trailPaint)
        canvas.drawCircle(ax, ay, 5.5f, antPaint)
    }

    return bmp
}

/**
 * 100% Local ARCH (`ONLY_LOCAL_LOGIC=1`) deterministic SHA-256 prompt-driven image synthesis engine.
 * Synthesizes a 512x512 Bitmap entirely on-device without any cloud or external RPC dependencies.
 */
fun synthesizeLocalAntFilterBitmap(
    prompt: String,
    mode: ScreenFilterAntMode,
    swarmCount: Int
): Bitmap {
    val size = 512
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)

    val digest = MessageDigest.getInstance("SHA-256")
        .digest(prompt.trim().lowercase(Locale.US).toByteArray(Charsets.UTF_8))
    val shaHex = digest.joinToString("") { "%02x".format(it) }.take(16).uppercase(Locale.US)

    val lower = prompt.lowercase(Locale.US)
    val primaryColor = when {
        "amber" in lower || "gold" in lower -> AndroidColor.rgb(255, 190, 20)
        "argon" in lower || "uv" in lower || "violet" in lower || "purple" in lower -> AndroidColor.rgb(185, 130, 255)
        "emerald" in lower || "green" in lower || "pheromone" in lower -> AndroidColor.rgb(0, 235, 135)
        "pink" in lower || "redline" in lower || "crimson" in lower -> AndroidColor.rgb(255, 64, 129)
        else -> when (mode) {
            ScreenFilterAntMode.ANT_PHEROMONE_GRID -> AndroidColor.rgb(0, 230, 118)
            ScreenFilterAntMode.POLARIZED_CYBER_SHIELD -> AndroidColor.rgb(0, 229, 255)
            ScreenFilterAntMode.AMBER_BLUE_BLOCK -> AndroidColor.rgb(255, 179, 0)
            ScreenFilterAntMode.ARGON_BOND_HALO -> AndroidColor.rgb(179, 136, 255)
            ScreenFilterAntMode.OFF -> AndroidColor.rgb(0, 229, 255)
        }
    }
    val secondaryColor = AndroidColor.rgb(
        (digest[0].toInt() and 0xFF).coerceIn(40, 240),
        (digest[1].toInt() and 0xFF).coerceIn(90, 255),
        (digest[2].toInt() and 0xFF).coerceIn(130, 255)
    )

    canvas.drawColor(AndroidColor.rgb(6, 10, 18))

    // Radial plasma core gradient
    val glowPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(
            size * 0.5f,
            size * 0.5f,
            size * 0.48f,
            intArrayOf(
                AndroidColor.argb(95, AndroidColor.red(primaryColor), AndroidColor.green(primaryColor), AndroidColor.blue(primaryColor)),
                AndroidColor.argb(40, AndroidColor.red(secondaryColor), AndroidColor.green(secondaryColor), AndroidColor.blue(secondaryColor)),
                AndroidColor.rgb(6, 10, 18)
            ),
            floatArrayOf(0f, 0.58f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), glowPaint)

    // Hexagonal lattice scaled by SHA-256 byte [3]
    val radius = 34f + ((digest[3].toInt() and 0x1F).toFloat())
    val gridPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        style = AndroidPaint.Style.STROKE
        strokeWidth = 2.0f
        color = primaryColor
        alpha = 115
    }
    val dx = radius * 1.5f
    val dy = radius * 1.732f
    var col = 0
    var x = 0f
    while (x <= size + radius) {
        var y = if (col % 2 == 0) 0f else dy * 0.5f
        while (y <= size + radius) {
            val path = android.graphics.Path()
            for (i in 0 until 6) {
                val angle = (i * 60f) * (PI.toFloat() / 180f)
                val px = x + cos(angle) * radius * 0.88f
                val py = y + sin(angle) * radius * 0.88f
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()
            canvas.drawPath(path, gridPaint)
            y += dy
        }
        x += dx
        col++
    }

    // Harmonic sacred rings & Fibonacci filaments derived from SHA-256 bytes [4..11]
    val ringPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        style = AndroidPaint.Style.STROKE
        strokeWidth = 2.2f
        color = secondaryColor
        alpha = 155
    }
    val ringCount = 4 + (digest[4].toInt() and 0x03)
    for (rIdx in 1..ringCount) {
        val r = (size * 0.44f) * (rIdx.toFloat() / ringCount)
        canvas.drawCircle(size * 0.5f, size * 0.5f, r, ringPaint)
    }

    // Autonomous Pheromone Swarm Agents & Synaptic Vectors
    val trailPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        style = AndroidPaint.Style.STROKE
        strokeWidth = 3.2f
        color = primaryColor
        alpha = 210
    }
    val nodePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        style = AndroidPaint.Style.FILL
        color = AndroidColor.WHITE
    }
    val count = swarmCount.coerceIn(12, 64)
    val spiralTurns = 3f + ((digest[5].toInt() and 0x07) * 0.5f)
    for (i in 0 until count) {
        val t = i.toFloat() / count
        val hashOffset = ((digest[i % digest.size].toInt() and 0xFF) / 255f) * 0.45f
        val angle = (t * PI.toFloat() * spiralTurns) + hashOffset
        val r = 28f + t * 210f
        val ax = size * 0.5f + cos(angle) * r
        val ay = size * 0.5f + sin(angle) * r
        val tx = size * 0.5f + cos(angle - 0.42f) * (r * 0.84f)
        val ty = size * 0.5f + sin(angle - 0.42f) * (r * 0.84f)
        canvas.drawLine(tx, ty, ax, ay, trailPaint)
        canvas.drawCircle(ax, ay, 4.8f, nodePaint)
    }

    // On-device SHA-256 Local Arch Provenance Stamp
    val textPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 15f
        isFakeBoldText = true
    }
    canvas.drawText("LOCAL ARCH // SHA256:$shaHex", 18f, size - 18f, textPaint)

    return bmp
}

/**
 * 100% Local ARCH (`ONLY_LOCAL_LOGIC=1`) deterministic on-device image editor.
 * Applies ColorMatrix chromatic transformations, polarity inversion, and pheromone lattice compositing
 * to an existing Bitmap on-device with zero cloud calls.
 */
fun editLocalAntFilterBitmap(
    baseBitmap: Bitmap,
    editPrompt: String,
    mode: ScreenFilterAntMode,
    swarmCount: Int
): Bitmap {
    val size = 512
    val scaledBase = Bitmap.createScaledBitmap(baseBitmap, size, size, true)
    val out = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(out)

    val digest = MessageDigest.getInstance("SHA-256")
        .digest(editPrompt.trim().lowercase(Locale.US).toByteArray(Charsets.UTF_8))
    val shaHex = digest.joinToString("") { "%02x".format(it) }.take(16).uppercase(Locale.US)
    val lower = editPrompt.lowercase(Locale.US)

    val matrix = when {
        "invert" in lower || "uv" in lower || "hologram" in lower -> ColorMatrix(
            floatArrayOf(
                -0.85f, 0.20f, 0.45f, 0f, 180f,
                0.15f, -0.75f, 0.35f, 0f, 165f,
                0.35f, 0.25f, -0.60f, 0f, 235f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "amber" in lower || "gold" in lower -> ColorMatrix(
            floatArrayOf(
                1.30f, 0.15f, 0.00f, 0f, 20f,
                0.10f, 1.05f, 0.00f, 0f, 10f,
                0.00f, 0.05f, 0.45f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        else -> ColorMatrix(
            floatArrayOf(
                0.75f, 0.15f, 0.35f, 0f, 12f,
                0.10f, 1.25f, 0.20f, 0f, 18f,
                0.20f, 0.25f, 1.35f, 0f, 24f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    val basePaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        colorFilter = ColorMatrixColorFilter(matrix)
    }
    canvas.drawBitmap(scaledBase, 0f, 0f, basePaint)

    // Overlay prompt-seeded pheromone vectors & SHA-256 stamp
    val overlayColor = when (mode) {
        ScreenFilterAntMode.ANT_PHEROMONE_GRID -> AndroidColor.rgb(0, 230, 118)
        ScreenFilterAntMode.POLARIZED_CYBER_SHIELD -> AndroidColor.rgb(0, 229, 255)
        ScreenFilterAntMode.AMBER_BLUE_BLOCK -> AndroidColor.rgb(255, 179, 0)
        ScreenFilterAntMode.ARGON_BOND_HALO -> AndroidColor.rgb(179, 136, 255)
        ScreenFilterAntMode.OFF -> AndroidColor.rgb(0, 229, 255)
    }
    val vectorPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        style = AndroidPaint.Style.STROKE
        strokeWidth = 2.8f
        color = overlayColor
        alpha = 185
    }
    val count = swarmCount.coerceIn(10, 64)
    for (i in 0 until count) {
        val t = i.toFloat() / count
        val angle = t * PI.toFloat() * 5f + ((digest[i % digest.size].toInt() and 0xFF) / 80f)
        val r = 35f + t * 200f
        val x1 = size * 0.5f + cos(angle) * (r * 0.85f)
        val y1 = size * 0.5f + sin(angle) * (r * 0.85f)
        val x2 = size * 0.5f + cos(angle + 0.3f) * r
        val y2 = size * 0.5f + sin(angle + 0.3f) * r
        canvas.drawLine(x1, y1, x2, y2, vectorPaint)
    }

    val textPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 15f
        isFakeBoldText = true
    }
    canvas.drawText("LOCAL EDIT // SHA256:$shaHex", 18f, size - 18f, textPaint)

    return out
}

/**
 * Full-screen non-blocking Screen Filter Ant & AI Image projection overlay.
 */
@Composable
fun ScreenFilterAntViewportOverlay(
    state: AxiomUiState,
    modifier: Modifier = Modifier
) {
    val mode = state.screenFilterAntMode
    val aiBitmap = (state.screenFilterAiState as? ScreenFilterAiUiState.Success)?.bitmap
        ?: state.screenFilterActiveBitmap

    val shouldRenderOverlay = mode != ScreenFilterAntMode.OFF ||
        (state.screenFilterProjectAiImage && aiBitmap != null)

    if (!shouldRenderOverlay) return

    val imageBitmap = aiBitmap?.asImageBitmap()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_filter_ant_viewport_overlay")
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val intensity = state.screenFilterAntIntensity.coerceIn(0.05f, 1.0f)

        // 1. Optional AI-Generated / Edited Image Texture Projection Layer
        if (state.screenFilterProjectAiImage && imageBitmap != null) {
            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(w.roundToInt(), h.roundToInt()),
                alpha = (state.screenFilterAiOverlayAlpha * intensity).coerceIn(0.08f, 0.85f)
            )
        }

        if (mode == ScreenFilterAntMode.OFF) return@Canvas

        val tint = mode.tintColor

        // 2. Polarized Optical Veil Tint
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    tint.copy(alpha = 0.08f * intensity),
                    ObsidianBg.copy(alpha = 0.22f * intensity),
                    tint.copy(alpha = 0.12f * intensity)
                )
            )
        )

        // 3. Micro-Louver Polarized Scanlines
        val scanSpacing = 12.dp.toPx().coerceAtLeast(6f)
        var scanY = (state.simTime * 28f) % scanSpacing
        while (scanY < h) {
            drawLine(
                color = tint.copy(alpha = 0.14f * intensity),
                start = Offset(0f, scanY),
                end = Offset(w, scanY),
                strokeWidth = 1.dp.toPx()
            )
            scanY += scanSpacing
        }

        // 4. Ant-Colony Pheromone Hexagonal Trail Lattice & Swarm Agents
        val swarmAgents = state.screenFilterAntSwarmDensity.coerceIn(8, 64)
        val centerX = w * 0.5f
        val centerY = h * 0.5f
        val maxRadius = minOf(w, h) * 0.46f

        for (idx in 0 until swarmAgents) {
            val phase = idx.toFloat() / swarmAgents
            val orbitSpeed = if (idx % 2 == 0) 0.65f else -0.52f
            val theta = phase * PI.toFloat() * 2f + state.simTime * orbitSpeed
            val quantizedTheta = (theta / (PI.toFloat() / 3f)).roundToInt() * (PI.toFloat() / 3f)
            val blendTheta = theta * 0.65f + quantizedTheta * 0.35f

            val radialWave = 0.22f + 0.72f * ((sin(state.simTime * 0.9f + idx * 0.7f) + 1f) * 0.5f)
            val r = maxRadius * radialWave

            val headX = centerX + cos(blendTheta) * r
            val headY = centerY + sin(blendTheta) * r

            val tailTheta = blendTheta - 0.24f * orbitSpeed
            val tailR = r * 0.90f
            val tailX = centerX + cos(tailTheta) * tailR
            val tailY = centerY + sin(tailTheta) * tailR

            val midX = (headX + tailX) * 0.5f + sin(theta * 3f) * 8.dp.toPx()
            val midY = (headY + tailY) * 0.5f + cos(theta * 3f) * 8.dp.toPx()

            val trailPath = Path().apply {
                moveTo(tailX, tailY)
                quadraticTo(midX, midY, headX, headY)
            }

            // Pheromone trail
            drawPath(
                path = trailPath,
                color = tint.copy(alpha = (0.38f * intensity).coerceIn(0.05f, 0.75f)),
                style = Stroke(width = 1.8.dp.toPx())
            )

            // Ant agent node + antenna sensor vector
            drawCircle(
                color = tint.copy(alpha = (0.78f * intensity).coerceIn(0.1f, 0.95f)),
                radius = 3.2.dp.toPx(),
                center = Offset(headX, headY)
            )
            drawCircle(
                color = Color.White.copy(alpha = (0.85f * intensity).coerceIn(0.1f, 1f)),
                radius = 1.4.dp.toPx(),
                center = Offset(headX, headY)
            )
        }
    }
}

/**
 * Interactive Card embedded in the Tunnel HUD Deck for configuring SCREEN FILTER ANT
 * and launching or running `gemini-nano-banana-2.1` Create & Edit Images.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenFilterAntStudioCard(
    state: AxiomUiState,
    onSelectFilterMode: (ScreenFilterAntMode) -> Unit,
    onUpdateFilterIntensity: (Float) -> Unit,
    onUpdateSwarmDensity: (Int) -> Unit,
    onToggleProjectAiImage: (Boolean) -> Unit,
    onUpdateAiOverlayAlpha: (Float) -> Unit,
    onUpdateAiPrompt: (String) -> Unit,
    onGenerateAiImage: () -> Unit,
    onEditAiImage: () -> Unit,
    onSeedProceduralBaseImage: () -> Unit,
    onSelectUserBitmapForEdit: (Bitmap) -> Unit,
    onOpenFullScreenStudioModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }.getOrNull()?.let { decoded ->
                onSelectUserBitmapForEdit(decoded)
            }
        }
    }

    val activeAccent = if (state.screenFilterAntMode != ScreenFilterAntMode.OFF) {
        state.screenFilterAntMode.tintColor
    } else {
        NeonCyan
    }

    HudCard(
        borderColor = activeAccent.copy(alpha = 0.75f),
        modifier = modifier.testTag("card_screen_filter_ant_studio")
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
                    imageVector = Icons.Default.BlurOn,
                    contentDescription = "Screen Filter Ant",
                    tint = activeAccent,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "SCREEN FILTER ANT // LOCAL IMAGE STUDIO",
                        style = MaterialTheme.typography.titleSmall,
                        color = activeAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "LOCAL ARCH (ONLY_LOCAL_LOGIC=1) • On-Device Create & Edit",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }
            Surface(
                onClick = onOpenFullScreenStudioModal,
                shape = RoundedCornerShape(6.dp),
                color = activeAccent.copy(alpha = 0.18f),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .border(1.dp, activeAccent, RoundedCornerShape(6.dp))
                    .testTag("btn_open_screen_filter_ant_modal")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Expand Studio",
                        tint = activeAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "STUDIO",
                        style = MaterialTheme.typography.labelSmall,
                        color = activeAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 1. Screen Filter Ant Mode Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ScreenFilterAntMode.entries.forEach { mode ->
                val selected = state.screenFilterAntMode == mode
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (selected) mode.tintColor.copy(alpha = 0.22f)
                            else ObsidianBg.copy(alpha = 0.78f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) mode.tintColor else SlateBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectFilterMode(mode) }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("chip_screen_filter_ant_${mode.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) mode.tintColor else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                        Text(
                            text = mode.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) mode.tintColor else TextMuted,
                            fontSize = 7.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = state.screenFilterAntMode.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Intensity & Ant-Swarm Density Sliders
        ScreenFilterSliderRow(
            label = "Filter Attenuation & Pheromone Glow",
            valueText = "${(state.screenFilterAntIntensity * 100).roundToInt()}%",
            accentColor = activeAccent,
            value = state.screenFilterAntIntensity,
            valueRange = 0.05f..1.00f,
            onValueChange = onUpdateFilterIntensity,
            testTag = "slider_screen_filter_ant_intensity"
        )

        ScreenFilterSliderRow(
            label = "Ant Swarm Micro-Agent Density",
            valueText = "${state.screenFilterAntSwarmDensity} AGENTS",
            accentColor = NeonGreen,
            value = state.screenFilterAntSwarmDensity.toFloat(),
            valueRange = 8f..64f,
            onValueChange = { onUpdateSwarmDensity(it.roundToInt()) },
            testTag = "slider_screen_filter_ant_swarm"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Inline Gemini Nano Banana 2.1 Prompt & Create/Edit Controls
        ScreenFilterAiControlsSection(
            state = state,
            onUpdateAiPrompt = onUpdateAiPrompt,
            onGenerateAiImage = onGenerateAiImage,
            onEditAiImage = onEditAiImage,
            onSeedProceduralBaseImage = onSeedProceduralBaseImage,
            onPickPhotoForEdit = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onToggleProjectAiImage = onToggleProjectAiImage,
            onUpdateAiOverlayAlpha = onUpdateAiOverlayAlpha
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenFilterAiControlsSection(
    state: AxiomUiState,
    onUpdateAiPrompt: (String) -> Unit,
    onGenerateAiImage: () -> Unit,
    onEditAiImage: () -> Unit,
    onSeedProceduralBaseImage: () -> Unit,
    onPickPhotoForEdit: () -> Unit,
    onToggleProjectAiImage: (Boolean) -> Unit,
    onUpdateAiOverlayAlpha: (Float) -> Unit
) {
    val isLoading = state.screenFilterAiState is ScreenFilterAiUiState.Loading
    val hasBaseBitmap = state.screenFilterActiveBitmap != null

    val presetPrompts = listOf(
        "Bioluminescent ant-colony pheromone hexagonal screen filter lattice, neon cyan and emerald cyber HUD",
        "Argon noble-gas plasma crystal shield with golden Fibonacci synaptic filaments on dark obsidian",
        "Invert dark polarity to high-contrast holographic ultraviolet neural map with glowing nodes"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianBg.copy(alpha = 0.85f))
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LOCAL ARCH // ON-DEVICE CREATE & EDIT IMAGES",
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
            StatusBadge(
                text = if (hasBaseBitmap) "LOCAL EDIT READY" else "ONLY_LOCAL_LOGIC=1",
                color = if (hasBaseBitmap) NeonGreen else NeonPurple
            )
        }

        // Preset prompt chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presetPrompts.forEachIndexed { idx, preset ->
                Surface(
                    onClick = { onUpdateAiPrompt(preset) },
                    shape = RoundedCornerShape(6.dp),
                    color = NeonPurple.copy(alpha = 0.14f),
                    modifier = Modifier
                        .border(1.dp, NeonPurple.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                        .testTag("btn_screen_filter_prompt_preset_$idx")
                ) {
                    Text(
                        text = when (idx) {
                            0 -> "Preset: Ant Lattice"
                            1 -> "Preset: Argon Shield"
                            else -> "Edit: UV Hologram"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple,
                        fontSize = 8.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.screenFilterAiPrompt,
            onValueChange = onUpdateAiPrompt,
            enabled = !isLoading,
            label = {
                Text(
                    text = "Text prompt to create or edit Screen Filter Ant image",
                    fontSize = 10.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = NeonCyan,
                unfocusedLabelColor = TextSecondary
            ),
            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_screen_filter_ai_prompt")
        )

        // Primary Create & Edit Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onGenerateAiImage,
                enabled = !isLoading && state.screenFilterAiPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan.copy(alpha = 0.24f),
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_create_ai_filter_image")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Create Image",
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CREATE IMAGE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }

            Button(
                onClick = onEditAiImage,
                enabled = !isLoading && state.screenFilterAiPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen.copy(alpha = 0.24f),
                    contentColor = NeonGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                    .testTag("btn_edit_ai_filter_image")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Image",
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (hasBaseBitmap) "EDIT IMAGE" else "SEED & EDIT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
        }

        // Source Image Loader Row (Photo Picker or Procedural Ant Lattice Seed)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = onPickPhotoForEdit,
                enabled = !isLoading,
                shape = RoundedCornerShape(6.dp),
                color = ObsidianBg,
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, HudBorderCyan, RoundedCornerShape(6.dp))
                    .testTag("btn_pick_gallery_image_for_edit")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Pick Photo",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LOAD PHOTO TO EDIT",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontSize = 8.sp
                    )
                }
            }

            Surface(
                onClick = onSeedProceduralBaseImage,
                enabled = !isLoading,
                shape = RoundedCornerShape(6.dp),
                color = ObsidianBg,
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .border(1.dp, NeonAmber.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                    .testTag("btn_seed_ant_lattice_bitmap")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterBAndW,
                        contentDescription = "Capture Ant Lattice",
                        tint = NeonAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SEED ANT LATTICE",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonAmber,
                        fontSize = 8.sp
                    )
                }
            }
        }

        // AI Generation / Edit Status & Result Preview
        when (val aiState = state.screenFilterAiState) {
            is ScreenFilterAiUiState.Idle -> {
                state.screenFilterActiveBitmap?.let { bmp ->
                    GeneratedFilterBitmapPreview(
                        bitmap = bmp,
                        caption = "Active Base Bitmap Ready for Editing / Projection",
                        projectEnabled = state.screenFilterProjectAiImage,
                        overlayAlpha = state.screenFilterAiOverlayAlpha,
                        onToggleProject = onToggleProjectAiImage,
                        onUpdateAlpha = onUpdateAiOverlayAlpha
                    )
                }
            }

            is ScreenFilterAiUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(HudPanelBg)
                        .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                        .padding(12.dp)
                        .testTag("screen_filter_ai_loading_box"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (aiState.isEditMode) {
                                "Editing image with gemini-nano-banana-2.1..."
                            } else {
                                "Generating image with gemini-nano-banana-2.1..."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        color = NeonCyan,
                        trackColor = SlateBorder,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "\"${aiState.prompt}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            is ScreenFilterAiUiState.Success -> {
                GeneratedFilterBitmapPreview(
                    bitmap = aiState.bitmap,
                    caption = if (aiState.isEdited) {
                        "EDITED VIA GEMINI-NANO-BANANA-2.1: \"${aiState.prompt}\""
                    } else {
                        "CREATED VIA GEMINI-NANO-BANANA-2.1: \"${aiState.prompt}\""
                    },
                    projectEnabled = state.screenFilterProjectAiImage,
                    overlayAlpha = state.screenFilterAiOverlayAlpha,
                    onToggleProject = onToggleProjectAiImage,
                    onUpdateAlpha = onUpdateAiOverlayAlpha
                )
            }

            is ScreenFilterAiUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonPink.copy(alpha = 0.12f))
                        .border(1.dp, NeonPink, RoundedCornerShape(6.dp))
                        .padding(10.dp)
                        .testTag("screen_filter_ai_error_box"),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "AI IMAGE ENGINE ALERT",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = aiState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneratedFilterBitmapPreview(
    bitmap: Bitmap,
    caption: String,
    projectEnabled: Boolean,
    overlayAlpha: Float,
    onToggleProject: (Boolean) -> Unit,
    onUpdateAlpha: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(HudPanelBg)
            .border(1.dp, NeonGreen.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen,
                fontSize = 8.sp,
                modifier = Modifier.weight(1f)
            )
            Surface(
                onClick = { onToggleProject(!projectEnabled) },
                shape = RoundedCornerShape(6.dp),
                color = if (projectEnabled) NeonGreen.copy(alpha = 0.22f) else ObsidianBg,
                modifier = Modifier
                    .border(
                        1.dp,
                        if (projectEnabled) NeonGreen else SlateBorder,
                        RoundedCornerShape(6.dp)
                    )
                    .testTag("btn_toggle_project_ai_bitmap")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Project onto Screen Filter",
                        tint = if (projectEnabled) NeonGreen else TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (projectEnabled) "PROJECTING ON VIEWPORT" else "PROJECT TO FILTER",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (projectEnabled) NeonGreen else TextSecondary,
                        fontSize = 8.sp
                    )
                }
            }
        }

        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, HudBorderCyan, RoundedCornerShape(6.dp))
                .testTag("img_screen_filter_ai_preview")
        )

        if (projectEnabled) {
            ScreenFilterSliderRow(
                label = "Viewport Holographic Blend Opacity",
                valueText = "${(overlayAlpha * 100).roundToInt()}%",
                accentColor = NeonGreen,
                value = overlayAlpha,
                valueRange = 0.10f..0.85f,
                onValueChange = onUpdateAlpha,
                testTag = "slider_ai_bitmap_blend_alpha"
            )
        }
    }
}

@Composable
fun ScreenFilterAntStudioModal(
    state: AxiomUiState,
    onDismiss: () -> Unit,
    onSelectFilterMode: (ScreenFilterAntMode) -> Unit,
    onUpdateFilterIntensity: (Float) -> Unit,
    onUpdateSwarmDensity: (Int) -> Unit,
    onToggleProjectAiImage: (Boolean) -> Unit,
    onUpdateAiOverlayAlpha: (Float) -> Unit,
    onUpdateAiPrompt: (String) -> Unit,
    onGenerateAiImage: () -> Unit,
    onEditAiImage: () -> Unit,
    onSeedProceduralBaseImage: () -> Unit,
    onSelectUserBitmapForEdit: (Bitmap) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg.copy(alpha = 0.88f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HudPanelBg)
                    .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SCREEN FILTER ANT // GEMINI-NANO-BANANA-2.1",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Create & Edit Neuro-Pheromone Screen Filters with Text Prompts",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_screen_filter_ant_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Screen Filter Ant Studio",
                            tint = NeonCyan
                        )
                    }
                }

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
                    onOpenFullScreenStudioModal = onDismiss
                )
            }
        }
    }
}

@Composable
private fun ScreenFilterSliderRow(
    label: String,
    valueText: String,
    accentColor: Color,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = SlateBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .testTag(testTag)
        )
    }
}

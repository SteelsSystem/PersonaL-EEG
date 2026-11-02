package com.example.shader

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.model.ShaderPreset
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AxiomGlRenderer(
    private val onFrameTelemetry: (frameDeltaMs: Float, gpuRendererName: String) -> Unit,
    private val onShaderSelfHealLog: (tag: String, msg: String) -> Unit
) : GLSurfaceView.Renderer {

    private val quadVertices = floatArrayOf(
        -1.0f, -1.0f,
         3.0f, -1.0f,
        -1.0f,  3.0f
    )
    private val vertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(quadVertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(quadVertices)
            position(0)
        }

    private val programs = mutableMapOf<ShaderPreset, Int>()
    private val uniformLocations = mutableMapOf<ShaderPreset, UniformLocs>()

    private data class UniformLocs(
        val aPosition: Int,
        val uResolution: Int,
        val uTime: Int,
        val uMouse: Int,
        val uMaxSteps: Int,
        val uHexScale: Int,
        val uReflexDensity: Int,
        val uMagPressure: Int,
        val uAntiMagPulse: Int,
        val uPerturbation: Int,
        val uHealWave: Int,
        val uFxaa: Int,
        val uRgbTint: Int,
        val uUnicriticDrift: Int,
        val uRotFreq: Int,
        val uRotPhase: Int
    )

    @Volatile var activePreset: ShaderPreset = ShaderPreset.HEX_REFLEX
    @Volatile var simTime: Float = 0f
    @Volatile var mouseX: Float = 540f
    @Volatile var mouseY: Float = 960f
    @Volatile var maxSteps: Int = 88
    @Volatile var hexScale: Float = 1.80f
    @Volatile var reflexDensity: Float = 1.45f
    @Volatile var magPressure: Float = 1.20f
    @Volatile var antiMagPulse: Float = 0.85f
    @Volatile var perturbation: Float = 0.0f
    @Volatile var healWave: Float = 0.0f
    @Volatile var fxaaEnabled: Boolean = false
    @Volatile var renderScale: Float = 1.0f
    @Volatile var rgbTintR: Float = 0.25f
    @Volatile var rgbTintG: Float = 1.05f
    @Volatile var rgbTintB: Float = 1.40f
    @Volatile var unicriticDrift: Float = 0.0f
    @Volatile var rotatingFrequencyHz: Float = 0.65f
    @Volatile var rotationPhaseRad: Float = 0.0f
    @Volatile var repackageRequested: Boolean = false

    private var surfaceWidth: Int = 1080
    private var surfaceHeight: Int = 1920
    private var gpuRendererName: String = "OpenGL ES 2.0 Hardware"
    private var lastFrameNano: Long = System.nanoTime()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.02f, 0.027f, 0.043f, 1.0f)
        gpuRendererName = GLES20.glGetString(GLES20.GL_RENDERER) ?: "GLES20 GPU"
        compileAllPrograms()
        lastFrameNano = System.nanoTime()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        surfaceWidth = width.coerceAtLeast(64)
        surfaceHeight = height.coerceAtLeast(64)
        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight)
        if (mouseX == 540f && mouseY == 960f) {
            mouseX = surfaceWidth * 0.5f
            mouseY = surfaceHeight * 0.5f
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        val now = System.nanoTime()
        val dtMs = ((now - lastFrameNano) / 1_000_000f).coerceIn(4.0f, 250.0f)
        lastFrameNano = now

        if (repackageRequested) {
            repackageRequested = false
            compileAllPrograms()
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val preset = activePreset
        val prog = programs[preset] ?: return
        val locs = uniformLocations[preset] ?: return

        GLES20.glUseProgram(prog)

        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(locs.aPosition)
        GLES20.glVertexAttribPointer(locs.aPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val effW = (surfaceWidth * renderScale).coerceAtLeast(64f)
        val effH = (surfaceHeight * renderScale).coerceAtLeast(64f)

        GLES20.glUniform2f(locs.uResolution, effW, effH)
        GLES20.glUniform1f(locs.uTime, simTime)
        GLES20.glUniform2f(locs.uMouse, mouseX * renderScale, (surfaceHeight - mouseY) * renderScale)
        GLES20.glUniform1i(locs.uMaxSteps, maxSteps)
        GLES20.glUniform1f(locs.uHexScale, hexScale)
        GLES20.glUniform1f(locs.uReflexDensity, reflexDensity)
        GLES20.glUniform1f(locs.uMagPressure, magPressure)
        GLES20.glUniform1f(locs.uAntiMagPulse, antiMagPulse)
        GLES20.glUniform1f(locs.uPerturbation, perturbation)
        GLES20.glUniform1f(locs.uHealWave, healWave)
        GLES20.glUniform1i(locs.uFxaa, if (fxaaEnabled) 1 else 0)
        GLES20.glUniform3f(locs.uRgbTint, rgbTintR, rgbTintG, rgbTintB)
        GLES20.glUniform1f(locs.uUnicriticDrift, unicriticDrift)
        GLES20.glUniform1f(locs.uRotFreq, rotatingFrequencyHz)
        GLES20.glUniform1f(locs.uRotPhase, rotationPhaseRad)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

        onFrameTelemetry(dtMs, gpuRendererName)
    }

    private fun compileAllPrograms() {
        programs.values.forEach { if (it != 0) GLES20.glDeleteProgram(it) }
        programs.clear()
        uniformLocations.clear()

        for (preset in ShaderPreset.entries) {
            val fsSource = AxiomShaders.getSourceForPreset(preset)
            val prog = createProgramWithSelfHeal(AxiomShaders.VS_SOURCE, fsSource, preset.key)
            if (prog != 0) {
                programs[preset] = prog
                GLES20.glUseProgram(prog)
                uniformLocations[preset] = UniformLocs(
                    aPosition = GLES20.glGetAttribLocation(prog, "a_position"),
                    uResolution = GLES20.glGetUniformLocation(prog, "u_resolution"),
                    uTime = GLES20.glGetUniformLocation(prog, "u_time"),
                    uMouse = GLES20.glGetUniformLocation(prog, "u_mouse"),
                    uMaxSteps = GLES20.glGetUniformLocation(prog, "u_max_steps"),
                    uHexScale = GLES20.glGetUniformLocation(prog, "u_hex_scale"),
                    uReflexDensity = GLES20.glGetUniformLocation(prog, "u_reflex_density"),
                    uMagPressure = GLES20.glGetUniformLocation(prog, "u_mag_pressure"),
                    uAntiMagPulse = GLES20.glGetUniformLocation(prog, "u_antimag_pulse"),
                    uPerturbation = GLES20.glGetUniformLocation(prog, "u_perturbation"),
                    uHealWave = GLES20.glGetUniformLocation(prog, "u_heal_wave"),
                    uFxaa = GLES20.glGetUniformLocation(prog, "u_fxaa"),
                    uRgbTint = GLES20.glGetUniformLocation(prog, "u_rgb_tint"),
                    uUnicriticDrift = GLES20.glGetUniformLocation(prog, "u_unicritic_drift"),
                    uRotFreq = GLES20.glGetUniformLocation(prog, "u_rot_freq"),
                    uRotPhase = GLES20.glGetUniformLocation(prog, "u_rot_phase")
                )
            }
        }
    }

    private fun createProgramWithSelfHeal(vsSrc: String, fsSrc: String, label: String): Int {
        val vs = compileShader(GLES20.GL_VERTEX_SHADER, vsSrc)
        var fs = compileShader(GLES20.GL_FRAGMENT_SHADER, fsSrc)
        if (fs == 0) {
            onShaderSelfHealLog("REPACKAGE", "Repackaging $label shader with mediump fallback")
            val fallbackFs = fsSrc.replace("precision highp float;", "precision mediump float;")
            fs = compileShader(GLES20.GL_FRAGMENT_SHADER, fallbackFs)
        }
        if (vs == 0 || fs == 0) return 0

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vs)
        GLES20.glAttachShader(program, fs)
        GLES20.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(program)
            return 0
        }
        return program
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }
}

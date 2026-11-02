package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Primary SPECTRoom telemetry entity storing inverted developer statistics alongside
 * biometric drift logs and SHA-256 cryptographic integrity check status.
 */
@Entity(
    tableName = "spect_stats",
    indices = [
        Index(value = ["sha256_verified"]),
        Index(value = ["timestamp"])
    ]
)
data class SpectStatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionTitle: String,
    val activeShader: String,
    val rgbMode: String,
    val contractPhase: String,
    val asIfTunnelStatus: String,
    val cognitiveLoadPct: Int,
    val rawFrameMs: Float,
    val invertedFrameRate: Float,      // 1000f / rawFrameMs (Inverted latency)
    val rawEntropy: Float,
    val invertedStabilityIndex: Float, // (1f - rawEntropy) * 100f
    val spectCognitiveRatio: Float,    // Focus-to-Entropy spectral efficiency
    val gigaOpsEquivalent: Float,
    val p50Ms: Float,
    val p95Ms: Float,
    val p99Ms: Float,
    val spectralDominance: String,
    val healCycles: Int,

    // SPECTRoom Biometric Drift Log Fields
    @ColumnInfo(name = "biometric_drift_log")
    val biometricDriftLog: String = "NOMINAL // 0.00 Drift • Alpha-Beta Phase Lock",
    @ColumnInfo(name = "biometric_drift_intensity")
    val biometricDriftIntensity: Float = 0.0f,

    // SPECTRoom SHA-256 Integrity Check Status Fields
    @ColumnInfo(name = "sha256_integrity_status")
    val sha256IntegrityStatus: String = "VERIFIED", // "VERIFIED" | "DRIFT_MISMATCH" | "UNVERIFIED"
    @ColumnInfo(name = "sha256_digest")
    val sha256Digest: String = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    @ColumnInfo(name = "sha256_verified")
    val sha256Verified: Boolean = true,

    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Dedicated SPECTRoom entity specifically modeling biometric drift logs and
 * SHA-256 integrity verification records in local SQLite storage.
 */
@Entity(
    tableName = "spect_room_records",
    indices = [
        Index(value = ["sha256_integrity_status"]),
        Index(value = ["timestamp"])
    ]
)
data class SpectRoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "session_title")
    val sessionTitle: String,
    @ColumnInfo(name = "biometric_drift_log")
    val biometricDriftLog: String,
    @ColumnInfo(name = "biometric_drift_intensity")
    val biometricDriftIntensity: Float,
    @ColumnInfo(name = "cognitive_load_pct")
    val cognitiveLoadPct: Int,
    @ColumnInfo(name = "dominant_band")
    val dominantBand: String,
    @ColumnInfo(name = "sha256_integrity_status")
    val sha256IntegrityStatus: String,
    @ColumnInfo(name = "sha256_digest")
    val sha256Digest: String,
    @ColumnInfo(name = "sha256_verified")
    val sha256Verified: Boolean,
    @ColumnInfo(name = "inverted_frame_rate")
    val invertedFrameRate: Float,
    @ColumnInfo(name = "inverted_stability_index")
    val invertedStabilityIndex: Float,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "script_verifications")
data class ScriptVerificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scriptTitle: String,
    val crateCategory: String,
    val rawCharCount: Int,
    val cleanedCharCount: Int,
    val sha256Digest: String,
    val isVerified: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "unicritic_verdicts")
data class UnicriticVerdictEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val verdictLevel: String,          // "OPTIMAL" | "ADMONITIO" | "CRITICUS"
    val consensusScore: Int,           // 0..100
    val glslAxisScore: Int,            // 0..100
    val eegAxisScore: Int,             // 0..100
    val codexAxisScore: Int,           // 0..100
    val driftIntensity: Float,         // 0.0..1.0
    val primaryFinding: String,
    val remediationApplied: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "unicritic_drift_logs",
    indices = [
        Index(value = ["redlineActive"]),
        Index(value = ["timestamp"])
    ]
)
data class UnicriticDriftLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val triggerSource: String,
    val verdictLevel: String,
    val rgbBiometricMode: String,
    val biometricLoadPct: Int,
    val dominantFrequencyHz: Float,
    val phaseCoherencePct: Int,
    val driftIntensity: Float,
    val compositeShaderDistortion: Float,
    val redlineActive: Boolean,
    val remediationNote: String,
    val timestamp: Long = System.currentTimeMillis()
)

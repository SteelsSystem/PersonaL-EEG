package com.example.data

import kotlinx.coroutines.flow.Flow

class SpectRepository(private val spectDao: SpectDao) {
    val allSpectStats: Flow<List<SpectStatEntity>> = spectDao.getAllSpectStats()
    val allSpectRoomRecords: Flow<List<SpectRoomEntity>> = spectDao.getAllSpectRoomRecords()
    val allVerifications: Flow<List<ScriptVerificationEntity>> = spectDao.getAllVerifications()
    val allUnicriticVerdicts: Flow<List<UnicriticVerdictEntity>> = spectDao.getAllUnicriticVerdicts()
    val allUnicriticDriftLogs: Flow<List<UnicriticDriftLogEntity>> = spectDao.getAllUnicriticDriftLogs()

    fun getSpectStatsBySha256Verified(verified: Boolean): Flow<List<SpectStatEntity>> {
        return spectDao.getSpectStatsBySha256Verified(verified)
    }

    suspend fun insertSpectStat(stat: SpectStatEntity) {
        spectDao.insertSpectStat(stat)
        spectDao.insertSpectRoomRecord(
            SpectRoomEntity(
                sessionTitle = stat.sessionTitle,
                biometricDriftLog = stat.biometricDriftLog,
                biometricDriftIntensity = stat.biometricDriftIntensity,
                cognitiveLoadPct = stat.cognitiveLoadPct,
                dominantBand = stat.spectralDominance,
                sha256IntegrityStatus = stat.sha256IntegrityStatus,
                sha256Digest = stat.sha256Digest,
                sha256Verified = stat.sha256Verified,
                invertedFrameRate = stat.invertedFrameRate,
                invertedStabilityIndex = stat.invertedStabilityIndex,
                timestamp = stat.timestamp
            )
        )
    }

    suspend fun deleteSpectStat(id: Int) {
        spectDao.deleteSpectStatById(id)
    }

    suspend fun deleteSpectRoomRecord(id: Int) {
        spectDao.deleteSpectRoomRecordById(id)
    }

    suspend fun clearSpectStats() {
        spectDao.clearAllSpectStats()
        spectDao.clearAllSpectRoomRecords()
    }

    suspend fun insertVerification(verification: ScriptVerificationEntity) {
        spectDao.insertVerification(verification)
    }

    suspend fun clearVerifications() {
        spectDao.clearAllVerifications()
    }

    suspend fun insertUnicriticVerdict(verdict: UnicriticVerdictEntity) {
        spectDao.insertUnicriticVerdict(verdict)
    }

    suspend fun deleteUnicriticVerdict(id: Int) {
        spectDao.deleteUnicriticVerdictById(id)
    }

    suspend fun clearUnicriticVerdicts() {
        spectDao.clearAllUnicriticVerdicts()
    }

    suspend fun insertUnicriticDriftLog(driftLog: UnicriticDriftLogEntity) {
        spectDao.insertUnicriticDriftLog(driftLog)
    }

    suspend fun insertBiometricVerdictAndDriftLog(
        verdict: UnicriticVerdictEntity,
        driftLog: UnicriticDriftLogEntity
    ) {
        spectDao.insertBiometricVerdictAndDriftLog(verdict, driftLog)
    }

    suspend fun deleteUnicriticDriftLog(id: Int) {
        spectDao.deleteUnicriticDriftLogById(id)
    }

    suspend fun clearUnicriticDriftLogs() {
        spectDao.clearAllUnicriticDriftLogs()
    }

    suspend fun ensureSeeded() {
        if (spectDao.getSpectCount() == 0) {
            insertSpectStat(
                SpectStatEntity(
                    sessionTitle = "BOOT // Osa A-X- inf00 Baseline",
                    activeShader = "HEX_REFLEX",
                    rgbMode = "FOCUS (BLUE)",
                    contractPhase = "EPISTULA",
                    asIfTunnelStatus = "TUNNEL_LOCKED",
                    cognitiveLoadPct = 28,
                    rawFrameMs = 16.6f,
                    invertedFrameRate = 60.2f,
                    rawEntropy = 0.0f,
                    invertedStabilityIndex = 100.0f,
                    spectCognitiveRatio = 3.57f,
                    gigaOpsEquivalent = 11.4f,
                    p50Ms = 16.5f,
                    p95Ms = 17.8f,
                    p99Ms = 19.2f,
                    spectralDominance = "Alpha-Beta Lock (14.2 Hz)",
                    healCycles = 1,
                    biometricDriftLog = "NOMINAL // 0.00 Drift • EEG Coherence 94% • Osa 10/10",
                    biometricDriftIntensity = 0.0f,
                    sha256IntegrityStatus = "VERIFIED",
                    sha256Digest = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
                    sha256Verified = true
                )
            )
            insertSpectStat(
                SpectStatEntity(
                    sessionTitle = "SWEEP // Reflex Magnetic Compression",
                    activeShader = "HEX_REFLEX",
                    rgbMode = "TRUTH (GREEN)",
                    contractPhase = "SATIS",
                    asIfTunnelStatus = "CONVERGING",
                    cognitiveLoadPct = 46,
                    rawFrameMs = 17.2f,
                    invertedFrameRate = 58.1f,
                    rawEntropy = 0.14f,
                    invertedStabilityIndex = 86.0f,
                    spectCognitiveRatio = 1.87f,
                    gigaOpsEquivalent = 14.8f,
                    p50Ms = 16.9f,
                    p95Ms = 20.4f,
                    p99Ms = 24.1f,
                    spectralDominance = "Theta-Alpha Bridge (9.6 Hz)",
                    healCycles = 3,
                    biometricDriftLog = "ADMONITIO // 0.14 Drift • Somatic Tension +12% • Auto-Heal Active",
                    biometricDriftIntensity = 0.14f,
                    sha256IntegrityStatus = "VERIFIED",
                    sha256Digest = "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae",
                    sha256Verified = true
                )
            )
        }
        if (spectDao.getUnicriticCount() == 0) {
            spectDao.insertUnicriticVerdict(
                UnicriticVerdictEntity(
                    verdictLevel = "OPTIMAL",
                    consensusScore = 96,
                    glslAxisScore = 98,
                    eegAxisScore = 92,
                    codexAxisScore = 100,
                    driftIntensity = 0.04f,
                    primaryFinding = "Tri-axis EPISTULA lock verified across GLSL, SY4-EEG, and SHA-256 Codex.",
                    remediationApplied = true
                )
            )
        }
        if (spectDao.getUnicriticDriftLogCount() == 0) {
            spectDao.insertUnicriticDriftLog(
                UnicriticDriftLogEntity(
                    triggerSource = "BOOT //RULE: IF FAILLOAD POSSIBLE",
                    verdictLevel = "OPTIMAL",
                    rgbBiometricMode = "FOCUS (BLUE)",
                    biometricLoadPct = 28,
                    dominantFrequencyHz = 14.2f,
                    phaseCoherencePct = 94,
                    driftIntensity = 0.00f,
                    compositeShaderDistortion = 0.00f,
                    redlineActive = false,
                    remediationNote = "Unified Failload Guard Armed • GPU + Biometric + AI Safe Fallback Ready"
                )
            )
        }
    }
}

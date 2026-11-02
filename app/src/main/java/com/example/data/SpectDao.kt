package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

import androidx.room.Transaction

/**
 * Room DAO interface for querying and mutating SPECTRoom entities, biometric drift logs,
 * SHA-256 script verifications, and Unicritic audit verdicts.
 */
@Dao
interface SpectDao {
    @Query("SELECT * FROM spect_stats ORDER BY timestamp DESC")
    fun getAllSpectStats(): Flow<List<SpectStatEntity>>

    @Query("SELECT * FROM spect_stats WHERE sha256_verified = :verified ORDER BY timestamp DESC")
    fun getSpectStatsBySha256Verified(verified: Boolean): Flow<List<SpectStatEntity>>

    @Query("SELECT * FROM spect_stats WHERE biometric_drift_intensity >= :minDrift ORDER BY biometric_drift_intensity DESC")
    fun getSpectStatsByBiometricDrift(minDrift: Float): Flow<List<SpectStatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpectStat(stat: SpectStatEntity)

    @Query(
        "UPDATE spect_stats SET sha256_integrity_status = :status, sha256_digest = :digest, sha256_verified = :verified WHERE id = :id"
    )
    suspend fun updateSpectSha256Status(
        id: Int,
        status: String,
        digest: String,
        verified: Boolean
    )

    @Query("DELETE FROM spect_stats WHERE id = :id")
    suspend fun deleteSpectStatById(id: Int)

    @Query("DELETE FROM spect_stats")
    suspend fun clearAllSpectStats()

    @Query("SELECT COUNT(*) FROM spect_stats")
    suspend fun getSpectCount(): Int

    // Dedicated SPECTRoom table queries (biometric drift logs + SHA-256 integrity status)
    @Query("SELECT * FROM spect_room_records ORDER BY timestamp DESC")
    fun getAllSpectRoomRecords(): Flow<List<SpectRoomEntity>>

    @Query("SELECT * FROM spect_room_records WHERE sha256_verified = :verified ORDER BY timestamp DESC")
    fun getSpectRoomRecordsByShaStatus(verified: Boolean): Flow<List<SpectRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpectRoomRecord(record: SpectRoomEntity)

    @Query("DELETE FROM spect_room_records WHERE id = :id")
    suspend fun deleteSpectRoomRecordById(id: Int)

    @Query("DELETE FROM spect_room_records")
    suspend fun clearAllSpectRoomRecords()

    // Script SHA-256 Verifications
    @Query("SELECT * FROM script_verifications ORDER BY timestamp DESC")
    fun getAllVerifications(): Flow<List<ScriptVerificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerification(verification: ScriptVerificationEntity)

    @Query("DELETE FROM script_verifications")
    suspend fun clearAllVerifications()

    // Unicritic Multi-Axis Verdicts
    @Query("SELECT * FROM unicritic_verdicts ORDER BY timestamp DESC")
    fun getAllUnicriticVerdicts(): Flow<List<UnicriticVerdictEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnicriticVerdict(verdict: UnicriticVerdictEntity)

    @Query("DELETE FROM unicritic_verdicts WHERE id = :id")
    suspend fun deleteUnicriticVerdictById(id: Int)

    @Query("DELETE FROM unicritic_verdicts")
    suspend fun clearAllUnicriticVerdicts()

    @Query("SELECT COUNT(*) FROM unicritic_verdicts")
    suspend fun getUnicriticCount(): Int

    // Unicritic Drift Logs (SPECT Room Telemetry)
    @Query("SELECT * FROM unicritic_drift_logs ORDER BY timestamp DESC")
    fun getAllUnicriticDriftLogs(): Flow<List<UnicriticDriftLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnicriticDriftLog(driftLog: UnicriticDriftLogEntity)

    @Query("DELETE FROM unicritic_drift_logs WHERE id = :id")
    suspend fun deleteUnicriticDriftLogById(id: Int)

    @Query("DELETE FROM unicritic_drift_logs")
    suspend fun clearAllUnicriticDriftLogs()

    @Query("SELECT COUNT(*) FROM unicritic_drift_logs")
    suspend fun getUnicriticDriftLogCount(): Int

    @Transaction
    suspend fun insertBiometricVerdictAndDriftLog(
        verdict: UnicriticVerdictEntity,
        driftLog: UnicriticDriftLogEntity
    ) {
        insertUnicriticVerdict(verdict)
        insertUnicriticDriftLog(driftLog)
    }
}

/**
 * Alias interface for callers referencing SpectRoomDao or SpectRoomTelemetryDao directly.
 */
@Dao
interface SpectRoomDao : SpectDao

@Dao
interface SpectRoomTelemetryDao : SpectDao

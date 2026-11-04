package com.example.service

import java.security.MessageDigest

/**
 * Deterministic local-first gate for synchronised records.
 *
 * This class performs no I/O and opens no network connections. Callers persist accepted
 * records locally. A separate transport worker may later send queued local records.
 *
 * Remote records use the same schema/hash/revision checks as local records, plus explicit
 * provenance and authorization verification supplied by trusted non-LLM verifiers.
 */
enum class SyncRecordOrigin { LOCAL, REMOTE }

enum class LocalFirstSyncDecision {
    ACCEPT_LOCAL,
    APPLY_REMOTE_TO_LOCAL,
    DUPLICATE,
    CONFLICT,
    QUARANTINE
}

data class LocalFirstSyncEnvelope(
    val eventId: String,
    val entityId: String,
    val schemaVersion: Int,
    val revision: Long,
    val origin: SyncRecordOrigin,
    val payload: String,
    val payloadSha256: String,
    val sourceRef: String,
    val policyDigest: String,
    val createdAtEpochMs: Long
)

data class LocalFirstSyncEvidence(
    /** True only after a trusted verifier validates the source signature/provenance. */
    val provenanceVerified: Boolean = false,
    /** True only after a deterministic policy/permission check, never an LLM decision. */
    val authorizationVerified: Boolean = false
)

data class LocalFirstSyncResult(
    val decision: LocalFirstSyncDecision,
    val reason: String,
    /** Queue intent only. This class never performs network I/O. */
    val queueForOutboundSync: Boolean = false
)

object LocalFirstSyncGate {
    private const val SHA256_HEX_LENGTH = 64

    fun evaluate(
        envelope: LocalFirstSyncEnvelope,
        currentLocalRevision: Long?,
        expectedSchemaVersion: Int,
        expectedPolicyDigest: String,
        evidence: LocalFirstSyncEvidence,
        onlineAvailable: Boolean
    ): LocalFirstSyncResult {
        if (envelope.eventId.isBlank() || envelope.entityId.isBlank() || envelope.sourceRef.isBlank()) {
            return quarantine("Missing event, entity, or source identity.")
        }
        if (envelope.schemaVersion != expectedSchemaVersion) {
            return quarantine("Schema version mismatch.")
        }
        if (envelope.revision < 0L || envelope.createdAtEpochMs <= 0L) {
            return quarantine("Invalid revision or creation timestamp.")
        }
        if (!isSha256(envelope.payloadSha256) ||
            sha256Hex(envelope.payload) != envelope.payloadSha256.lowercase()
        ) {
            return quarantine("Payload digest missing, malformed, or mismatched.")
        }
        if (!isSha256(expectedPolicyDigest) ||
            envelope.policyDigest.lowercase() != expectedPolicyDigest.lowercase()
        ) {
            return quarantine("Canonical policy digest mismatch.")
        }
        if (!evidence.provenanceVerified || !evidence.authorizationVerified) {
            return quarantine("Required provenance or authorization evidence is unavailable.")
        }

        if (currentLocalRevision != null) {
            if (envelope.revision < currentLocalRevision) {
                return LocalFirstSyncResult(
                    LocalFirstSyncDecision.CONFLICT,
                    "Incoming revision is older than the local revision."
                )
            }
            if (envelope.revision == currentLocalRevision) {
                return LocalFirstSyncResult(
                    LocalFirstSyncDecision.DUPLICATE,
                    "Revision already exists locally; no overwrite is performed."
                )
            }
        }

        return when (envelope.origin) {
            SyncRecordOrigin.LOCAL -> LocalFirstSyncResult(
                decision = LocalFirstSyncDecision.ACCEPT_LOCAL,
                reason = "Accepted into the canonical local path.",
                queueForOutboundSync = onlineAvailable
            )
            SyncRecordOrigin.REMOTE -> LocalFirstSyncResult(
                decision = LocalFirstSyncDecision.APPLY_REMOTE_TO_LOCAL,
                reason = "Remote record passed the same local validation gate and may now be applied locally."
            )
        }
    }

    private fun quarantine(reason: String) = LocalFirstSyncResult(
        decision = LocalFirstSyncDecision.QUARANTINE,
        reason = reason
    )

    private fun isSha256(value: String): Boolean =
        value.length == SHA256_HEX_LENGTH && value.all { it in '0'..'9' || it.lowercaseChar() in 'a'..'f' }

    private fun sha256Hex(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

package com.example.service

import java.security.MessageDigest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalFirstSyncGateTest {
    private val policyDigest = "a".repeat(64)
    private val evidence = LocalFirstSyncEvidence(
        provenanceVerified = true,
        authorizationVerified = true
    )

    private fun envelope(
        origin: SyncRecordOrigin = SyncRecordOrigin.LOCAL,
        revision: Long = 1L,
        payload: String = """{"value":"local"}""",
        policy: String = policyDigest
    ) = LocalFirstSyncEnvelope(
        eventId = "evt-1",
        entityId = "entity-1",
        schemaVersion = 1,
        revision = revision,
        origin = origin,
        payload = payload,
        payloadSha256 = sha256(payload),
        sourceRef = "local-test-source",
        policyDigest = policy,
        createdAtEpochMs = 1_800_000_000_000L
    )

    @Test
    fun localRecordIsAcceptedWithoutNetworkAndDoesNotQueue() {
        val result = LocalFirstSyncGate.evaluate(
            envelope = envelope(),
            currentLocalRevision = null,
            expectedSchemaVersion = 1,
            expectedPolicyDigest = policyDigest,
            evidence = evidence,
            onlineAvailable = false
        )

        assertEquals(LocalFirstSyncDecision.ACCEPT_LOCAL, result.decision)
        assertFalse(result.queueForOutboundSync)
    }

    @Test
    fun localRecordMayBeQueuedButGateDoesNotPerformNetworkIo() {
        val result = LocalFirstSyncGate.evaluate(
            envelope = envelope(),
            currentLocalRevision = null,
            expectedSchemaVersion = 1,
            expectedPolicyDigest = policyDigest,
            evidence = evidence,
            onlineAvailable = true
        )

        assertEquals(LocalFirstSyncDecision.ACCEPT_LOCAL, result.decision)
        assertTrue(result.queueForOutboundSync)
    }

    @Test
    fun validRemoteRecordIsAppliedOnlyAfterLocalGate() {
        val result = LocalFirstSyncGate.evaluate(
            envelope = envelope(origin = SyncRecordOrigin.REMOTE),
            currentLocalRevision = null,
            expectedSchemaVersion = 1,
            expectedPolicyDigest = policyDigest,
            evidence = evidence,
            onlineAvailable = true
        )

        assertEquals(LocalFirstSyncDecision.APPLY_REMOTE_TO_LOCAL, result.decision)
        assertFalse(result.queueForOutboundSync)
    }

    @Test
    fun invalidDigestIsQuarantined() {
        val result = LocalFirstSyncGate.evaluate(
            envelope = envelope().copy(payloadSha256 = "0".repeat(64)),
            currentLocalRevision = null,
            expectedSchemaVersion = 1,
            expectedPolicyDigest = policyDigest,
            evidence = evidence,
            onlineAvailable = false
        )

        assertEquals(LocalFirstSyncDecision.QUARANTINE, result.decision)
    }

    @Test
    fun missingAuthorizationEvidenceIsQuarantined() {
        val result = LocalFirstSyncGate.evaluate(
            envelope = envelope(origin = SyncRecordOrigin.REMOTE),
            currentLocalRevision = null,
            expectedSchemaVersion = 1,
            expectedPolicyDigest = policyDigest,
            evidence = LocalFirstSyncEvidence(provenanceVerified = true),
            onlineAvailable = true
        )

        assertEquals(LocalFirstSyncDecision.QUARANTINE, result.decision)
    }

    @Test
    fun staleRemoteRevisionCannotOverwriteLocalState() {
        val result = LocalFirstSyncGate.evaluate(
            envelope = envelope(origin = SyncRecordOrigin.REMOTE, revision = 3L),
            currentLocalRevision = 4L,
            expectedSchemaVersion = 1,
            expectedPolicyDigest = policyDigest,
            evidence = evidence,
            onlineAvailable = true
        )

        assertEquals(LocalFirstSyncDecision.CONFLICT, result.decision)
    }

    private fun sha256(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

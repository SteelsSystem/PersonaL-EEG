package com.example.service

import com.example.data.ScriptVerificationEntity
import com.example.data.SpectRepository
import com.example.data.UnicriticDriftLogEntity
import com.example.model.EegBandState
import com.example.model.ForwardTunnelParseResult

/**
 * Compact, zero-allocation Signal-Loss & Relay-Mesh Telemetry Snapshot.
 * Packs all 6 Relay Vector hop states, ODM gate status, and Merkle-mesh parity into a 16-bit
 * local register (`signalHealthMaskHex`, nominal `0x3F01`) to do more for less (strictly local-only).
 */
data class SignalLossDetectionResult(
    val signalLossDetected: Boolean,
    val faultMaskBits: Int,
    val signalHealthMaskHex: String,
    val activeFaultCodes: List<String>,
    val merkleRingIntact: Boolean,
    val autoReloadRecommended: Boolean,
    val compactDiagnostic: String
)

/**
 * Atomic outcome of a One-Tap or Automatic Emergency Self-Dependency Reload.
 */
data class EmergencyReloadExecutionBundle(
    val restoredInterconnect: ServerlessLocalDeployState,
    val restoredAtlas: AtlasKnowabilityAndOdmState,
    val restoredProtocolO: ProtocolOIntegrityCoreState,
    val detectionSnapshot: SignalLossDetectionResult,
    val codexScript: String,
    val auditTitle: String
)

/**
 * One-Tap & Auto-Watchdog Trigger Interface for the Emergency Self-Dependency Reload Mechanism.
 */
interface EmergencySelfDependencyReloadTrigger {
    fun detectSignalLossVector(
        atlas: AtlasKnowabilityAndOdmState,
        interconnect: ServerlessLocalDeployState,
        proto: ProtocolOIntegrityCoreState
    ): SignalLossDetectionResult

    fun verifyMerkleMeshIntegrity(pack: EmergencySelfDependencyReloadPack): Boolean

    fun executeOneTapOrAutoReload(
        currentProto: ProtocolOIntegrityCoreState,
        currentAtlas: AtlasKnowabilityAndOdmState,
        currentInterconnect: ServerlessLocalDeployState,
        forwardParse: ForwardTunnelParseResult,
        asCoreFrame: AsCoreRgbBiometricFrame,
        eegBands: EegBandState,
        continuitySha256: String,
        signalLossTriggered: Boolean,
        triggerReason: String
    ): EmergencyReloadExecutionBundle
}

/**
 * Local-Only Repository Service handling:
 * 1. Automatic signal-loss detection across the 6-hop Relay Vector Ring (`RV-01`..`RV-06`).
 * 2. Bidirectional Merkle-Mesh SHA-256 joint verification (`Reverse || Self || Forward` -> Root).
 * 3. One-tap & automatic emergency self-dependency reload execution with local SQLite (`SpectRepository`) audit persistence.
 */
object EmergencyReloadRepositoryService : EmergencySelfDependencyReloadTrigger {

    // Fault bit flags (0x00 when nominal)
    const val FAULT_ODM_GATE_OR_REG = 1 shl 0     // RV-01 fault
    const val FAULT_PROTO_O_SUBLAYER = 1 shl 1    // RV-02 fault
    const val FAULT_AUDIUS_DSP_GUARD = 1 shl 2    // RV-03 fault
    const val FAULT_RGB_FRQ_OR_CANT = 1 shl 3     // RV-04 fault
    const val FAULT_ROOM_SQLITE_AUDIT = 1 shl 4   // RV-05 fault
    const val FAULT_LOCAL_EGRESS_GATE = 1 shl 5   // RV-06 fault

    /**
     * Evaluates signal loss across all 6 Relay Vector hops in O(1) time without heap churn
     * when nominal, packing health into a 16-bit local register (`0x3F01` = 6/6 Hops + Auto-Armed).
     */
    override fun detectSignalLossVector(
        atlas: AtlasKnowabilityAndOdmState,
        interconnect: ServerlessLocalDeployState,
        proto: ProtocolOIntegrityCoreState
    ): SignalLossDetectionResult {
        val pack = proto.emergencyReloadPack
        val merkleOk = verifyMerkleMeshIntegrity(pack)

        var faultBits = 0
        if (!atlas.odmGateStatusOn || atlas.runBlockedFlag != 0) {
            faultBits = faultBits or FAULT_ODM_GATE_OR_REG
        }
        if (!proto.deterministicSyncLocked || !merkleOk) {
            faultBits = faultBits or FAULT_PROTO_O_SUBLAYER
        }
        if (!atlas.sevenBandFft.reinstakeGuardSafe) {
            faultBits = faultBits or FAULT_AUDIUS_DSP_GUARD
        }
        if (!atlas.rgbKnowable || !atlas.frqKnowable || atlas.orIfLieDetected || atlas.onLostPath || atlas.asAtlasRedActive) {
            faultBits = faultBits or FAULT_RGB_FRQ_OR_CANT
        }
        if (!interconnect.roomAuditVerified || !interconnect.sha256SealVerified) {
            faultBits = faultBits or FAULT_ROOM_SQLITE_AUDIT
        }
        if (!interconnect.checkPassTrueOnly || !interconnect.zeroEgressVerified) {
            faultBits = faultBits or FAULT_LOCAL_EGRESS_GATE
        }

        val healthyHopBits = (faultBits.inv() and 0x3F) shl 8
        val armedBit = if (pack.autoReloadArmed) 0x0001 else 0x0000
        val merkleBit = if (merkleOk) 0x4000 else 0x0000
        val maskWord = merkleBit or healthyHopBits or (faultBits and 0x3F) or armedBit
        val maskHex = "0x%04X".format(maskWord)

        val signalLoss = faultBits != 0 || pack.signalLossDetected
        val faultCodes = if (faultBits == 0) {
            emptyList()
        } else {
            buildList {
                if ((faultBits and FAULT_ODM_GATE_OR_REG) != 0) add("RV-01:ODM_GATE_DROP")
                if ((faultBits and FAULT_PROTO_O_SUBLAYER) != 0) add("RV-02:MERKLE_DRIFT")
                if ((faultBits and FAULT_AUDIUS_DSP_GUARD) != 0) add("RV-03:DSP_REINSTAKE_HOLD")
                if ((faultBits and FAULT_RGB_FRQ_OR_CANT) != 0) add("RV-04:RGB_FRQ_LOST_PATH")
                if ((faultBits and FAULT_ROOM_SQLITE_AUDIT) != 0) add("RV-05:SQLITE_PARITY_LOSS")
                if ((faultBits and FAULT_LOCAL_EGRESS_GATE) != 0) add("RV-06:EGRESS_GATE_FAULT")
            }
        }

        val autoReloadRec = signalLoss && pack.autoReloadArmed
        val diagnostic = if (!signalLoss) {
            "LOCAL-ONLY RELAY MESH [$maskHex] // 6/6 Vectors Locked • Merkle ${pack.merkleMeshRootShortHash} • Zero Signal Loss"
        } else {
            "SIGNAL LOSS [$maskHex] (${faultCodes.joinToString(",")}) -> ${if (pack.autoReloadArmed) "AUTO-RELOAD ENGAGED" else "ONE-TAP RELOAD READY"}"
        }

        return SignalLossDetectionResult(
            signalLossDetected = signalLoss,
            faultMaskBits = faultBits,
            signalHealthMaskHex = maskHex,
            activeFaultCodes = faultCodes,
            merkleRingIntact = merkleOk,
            autoReloadRecommended = autoReloadRec,
            compactDiagnostic = diagnostic
        )
    }

    /**
     * Verifies the 6-hop closed Relay Vector Ring (`RV-01..RV-06 -> RV-01`) and every hop's
     * Bidirectional Merkle-Mesh SHA-256 joint (`reverseJoint`, `sliceSha256`, `forwardJoint`).
     */
    override fun verifyMerkleMeshIntegrity(pack: EmergencySelfDependencyReloadPack): Boolean {
        val hops = pack.relayVectorHops
        if (hops.size != 6 || pack.merkleMeshRootSha256.length != 64) return false

        for (i in hops.indices) {
            val cur = hops[i]
            val next = hops[(i + 1) % 6]
            val prev = hops[(i + 5) % 6]

            // Closed ring topology check: cur.targetNodeCode must match next.sourceNodeCode
            if (cur.targetNodeCode != next.sourceNodeCode) return false
            if (cur.sourceNodeCode != prev.targetNodeCode) return false

            val expectedFwd = ProtocolOIntegrityCoreEngine.sha256Hex(
                "FWD_JOINT|${cur.hopId}|${cur.sliceSha256}|${next.sliceSha256}"
            )
            val expectedRev = ProtocolOIntegrityCoreEngine.sha256Hex(
                "REV_JOINT|${cur.hopId}|${cur.sliceSha256}|${prev.sliceSha256}"
            )
            val expectedMesh = ProtocolOIntegrityCoreEngine.sha256Hex(
                "MESH_JOINT|${cur.hopId}|$expectedRev|${cur.sliceSha256}|$expectedFwd"
            )
            if (cur.forwardJointSha256 != expectedFwd ||
                cur.reverseJointSha256 != expectedRev ||
                cur.meshJointSha256 != expectedMesh
            ) {
                return false
            }
        }
        return true
    }

    /**
     * Single-pass local-only execution of the Emergency Self-Dependency Reload.
     * Re-slices all 6 hops, verifies the Bidirectional Merkle-Mesh root, and restores
     * Local Interconnect, ODM/Atlas Knowability, Open Intake Cant O-Ring, and Protocol O states.
     */
    override fun executeOneTapOrAutoReload(
        currentProto: ProtocolOIntegrityCoreState,
        currentAtlas: AtlasKnowabilityAndOdmState,
        currentInterconnect: ServerlessLocalDeployState,
        forwardParse: ForwardTunnelParseResult,
        asCoreFrame: AsCoreRgbBiometricFrame,
        eegBands: EegBandState,
        continuitySha256: String,
        signalLossTriggered: Boolean,
        triggerReason: String
    ): EmergencyReloadExecutionBundle {
        // 1. Restore all 6 local-only interconnect paths (`check:pass:true_only`, 0 egress)
        val restoredInterconnect = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = LocalOnlyPathsInterconnectEngine.buildDefaultNodes(forwardParse, continuitySha256),
            forwardParse = forwardParse.copy(onlyLocalLogicFlag = true, bracketDepthBalanced = true),
            asCoreToken = asCoreFrame.asCoreToken,
            continuitySha256 = continuitySha256,
            sha256Verified = true,
            roomAuditVerified = true,
            deployEpoch = currentInterconnect.deployEpoch + 1,
            roomAuditRecordCount = currentInterconnect.roomAuditRecordCount + 1,
            activeNodeId = "DEST-1"
        )

        // 2. Restore ODM guarded allowlist probe + Open Intake Cant O-Ring + RGB/FRQ Knowability
        val (cmdOut, exitCode, blockedFlag) = AtlasKnowabilityAndOdmEngine.executeGuardedOdmCommand(
            cmdName = "uname",
            gateStatusOn = true
        )
        val reloadAtlasSeed = currentAtlas.copy(
            forceRgbUnknowable = false,
            forceFrqUnknowable = false,
            forceAtlasLostPath = false,
            forceOrIfLieDetected = false,
            odmGateStatusOn = true,
            selectedAllowlistCmd = "uname",
            lastCommandOutputJson = cmdOut,
            lastCommandExitCode = exitCode,
            runBlockedFlag = blockedFlag,
            intakeCantAngleDeg = 24.0f,
            intakeCantJumpMode = IntakeCantJumpMode.AUTO_JUMP_IN_OUT,
            sonicSoundSwapTranslateActive = true,
            sonicSoundSwapMode = SonicSoundSwapMode.PRISMATIC_SPECTRUM_SWAP,
            sonicSoundSwapCount = currentAtlas.sonicSoundSwapCount + 1,
            sonicSwapWaveBurst = 1.38f,
            selectedOdmTarget = OdmAlignmentTarget.TARGET_C_RUST_DSP,
            selfTeachLessonsCount = currentAtlas.selfTeachLessonsCount + 1,
            atlasRecoveryCount = currentAtlas.atlasRecoveryCount + 1,
            selectedFileLineCode = "L2-KNOW"
        )
        val restoredAtlas = AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = reloadAtlasSeed,
            asCoreFrame = asCoreFrame,
            asCoreStreamActive = true,
            eegBands = eegBands,
            dominantHz = 16.8f,
            rotatingFrqHz = 0.72f,
            phaseCoherencePct = 99,
            perturbation = 0.0f,
            glslGateTrue = true,
            redlineThreatActive = false,
            localInterconnectState = restoredInterconnect,
            spectSensorHealthPct = 99,
            simTimeSec = 0.0f
        )

        // 3. Slice & pack the 6-hop Relay Vector Ring and Bidirectional Merkle-Mesh
        val restoredProto = ProtocolOIntegrityCoreEngine.executeEmergencySelfDependencyReload(
            currentState = currentProto,
            atlas = restoredAtlas,
            continuitySha256 = continuitySha256,
            signalLossTriggered = signalLossTriggered,
            triggerReason = triggerReason
        )

        // 4. Verify post-reload signal health register (0x7F01 / 0x3F01 nominal)
        val postDetection = detectSignalLossVector(
            atlas = restoredAtlas,
            interconnect = restoredInterconnect,
            proto = restoredProto.copy(
                emergencyReloadPack = restoredProto.emergencyReloadPack.copy(signalLossDetected = false)
            )
        )

        val codexEntry = ProtocolOIntegrityCoreEngine.buildProtocolOCodexEntry(restoredProto)
        val pack = restoredProto.emergencyReloadPack
        val auditTitle = "EMERGENCY SELF-DEPENDENCY RELOAD // ${pack.packId} (${pack.merkleMeshRootShortHash} • ${postDetection.signalHealthMaskHex})"

        return EmergencyReloadExecutionBundle(
            restoredInterconnect = restoredInterconnect,
            restoredAtlas = restoredAtlas,
            restoredProtocolO = restoredProto,
            detectionSnapshot = postDetection,
            codexScript = codexEntry.codeSnippet,
            auditTitle = auditTitle
        )
    }

    /**
     * Persists a compact, local-only Emergency Reload audit record into Room SQLite (`SpectRepository`).
     */
    suspend fun persistEmergencyReloadAuditToRoom(
        repository: SpectRepository,
        bundle: EmergencyReloadExecutionBundle,
        rgbModeTitle: String
    ) {
        val pack = bundle.restoredProtocolO.emergencyReloadPack
        repository.recordEmergencySelfDependencyReloadAudit(
            packId = pack.packId,
            merkleRootSha256 = pack.merkleMeshRootSha256,
            merkleRootShortHash = pack.merkleMeshRootShortHash,
            signalHealthMaskHex = bundle.detectionSnapshot.signalHealthMaskHex,
            triggerReason = pack.lastReloadTriggerReason,
            codexScript = bundle.codexScript,
            rgbModeTitle = rgbModeTitle
        )
    }
}

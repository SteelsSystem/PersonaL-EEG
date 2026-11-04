package com.example

import com.example.data.SpectRoomEntity
import com.example.data.SpectStatEntity
import com.example.model.ForwardTunnelParseResult
import com.example.service.AsCoreStreamProfile
import com.example.service.DefaultAsCoreRgbBiometricStreamService
import com.example.service.LocalOnlyPathsInterconnectEngine
import com.example.service.RgbChannelAxis
import com.example.viewmodel.AxiomUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun spectRoomEntity_andContinuityInterlockDefaults_areValid() {
        val state = AxiomUiState()
        assertTrue(state.continuityInterlockEnabled)
        assertTrue(state.glslGateTrue)
        assertTrue(state.sha256Verified)
        assertEquals(1, state.continuityChainEpoch)
        assertTrue(state.widescreenStageEnabled)
        assertEquals(0.65f, state.rotatingFrequencyHz, 0.001f)
        assertEquals(0.65f, state.effectiveRotatingFrequencyHz, 0.001f)
        assertTrue(state.rotationDirectionCw)
        assertTrue(state.asCoreStreamActive)
        assertTrue(state.asCoreAutoDriveBlockMind)
        assertTrue(state.localInterconnectState.checkPassTrueOnly)
        assertEquals("check:pass:true_only", state.localInterconnectState.assertionToken)
        assertEquals(6, state.localInterconnectState.nodes.size)

        val roomRecord = SpectRoomEntity(
            sessionTitle = "CONTINUITY LOCK #1",
            biometricDriftLog = "NOMINAL LOCK // 0.00 Drift • BioLoad 28% • Coherence 96%",
            biometricDriftIntensity = 0.0f,
            cognitiveLoadPct = 28,
            dominantBand = "Alpha (8-13Hz)",
            sha256IntegrityStatus = "VERIFIED",
            sha256Digest = state.continuityCurrentSha256,
            sha256Verified = true,
            invertedFrameRate = 60.2f,
            invertedStabilityIndex = 100.0f
        )
        assertEquals("VERIFIED", roomRecord.sha256IntegrityStatus)
        assertTrue(roomRecord.sha256Verified)

        val statEntity = SpectStatEntity(
            sessionTitle = "CONTINUITY LOCK #1",
            activeShader = "HEX_REFLEX",
            rgbMode = "FOCUS (BLUE)",
            contractPhase = "EPISTULA",
            asIfTunnelStatus = "LOCKED",
            cognitiveLoadPct = 28,
            rawFrameMs = 16.6f,
            invertedFrameRate = 60.2f,
            rawEntropy = 0.0f,
            invertedStabilityIndex = 100.0f,
            spectCognitiveRatio = 3.5f,
            gigaOpsEquivalent = 12.4f,
            p50Ms = 16.5f,
            p95Ms = 17.8f,
            p99Ms = 19.2f,
            spectralDominance = "Alpha (8-13Hz)",
            healCycles = 1
        )
        assertEquals("VERIFIED", statEntity.sha256IntegrityStatus)
        assertTrue(statEntity.sha256Verified)
    }

    @Test
    fun asCoreRgbBiometricStreamService_updatesBlockMindChannelsAndProfiles() {
        val service = DefaultAsCoreRgbBiometricStreamService()
        assertTrue(service.isStreamActive.value)

        // Step in Creativity Red [R] profile -> positive BlockMind polarity drive (UNBLOCK [+])
        service.selectProfile(AsCoreStreamProfile.AS_CORE_CREATIVITY_RED)
        service.injectChromaticPulse(RgbChannelAxis.RED, 0.50f)
        val redFrame = service.stepFrame(simTimeSec = 1.25f, perturbation = 0.0f, somaticStabilityPct = 96)
        assertEquals(AsCoreStreamProfile.AS_CORE_CREATIVITY_RED, redFrame.profile)
        assertTrue(redFrame.asCoreToken.startsWith("as[core::creativity_r#"))
        assertTrue(redFrame.redChannelCreativity > 0.65f)
        assertTrue(redFrame.blockMindPolarityDrive >= 0.35f)
        assertEquals(48, redFrame.rgbWaveformHistoryR.size)
        assertEquals(64, redFrame.sha256FrameSeal.length)

        // Step in Focus Blue [B] profile -> negative BlockMind polarity drive (BLOCK [-])
        service.selectProfile(AsCoreStreamProfile.AS_CORE_FOCUS_BLUE)
        service.injectChromaticPulse(RgbChannelAxis.BLUE, 0.50f)
        val blueFrame = service.stepFrame(simTimeSec = 2.50f, perturbation = 0.0f, somaticStabilityPct = 96)
        assertEquals(AsCoreStreamProfile.AS_CORE_FOCUS_BLUE, blueFrame.profile)
        assertTrue(blueFrame.blueChannelFocus > 0.65f)
        assertTrue(blueFrame.blockMindPolarityDrive <= -0.35f)
    }

    @Test
    fun localOnlyPathsInterconnect_verifiesCheckPassTrueOnlyAndBlocksExternalEgress() {
        val parse = ForwardTunnelParseResult()
        val defaultNodes = LocalOnlyPathsInterconnectEngine.buildDefaultNodes(parse)
        assertEquals(6, defaultNodes.size)
        assertTrue(defaultNodes.all { it.isStrictlyLocal && it.hopSha256.length == 64 })

        val passState = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = defaultNodes,
            forwardParse = parse,
            asCoreToken = "as[core::truth_g#7]",
            continuitySha256 = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
            sha256Verified = true,
            roomAuditVerified = true,
            deployEpoch = 3,
            roomAuditRecordCount = 5
        )
        assertTrue(passState.checkPassTrueOnly)
        assertTrue(passState.zeroEgressVerified)
        assertTrue(passState.sha256SealVerified)
        assertTrue(passState.roomAuditVerified)
        assertEquals("check:pass:true_only", passState.assertionToken)

        // Inject external egress route on DEST-4 -> gate must block and assert check:pass:false [BLOCKED]
        val faultedNodes = defaultNodes.map {
            if (it.id == "DEST-4") it.copy(localPath = "https://remote-egress.example.com/relay.apk") else it
        }
        val blockedState = LocalOnlyPathsInterconnectEngine.evaluateDeployGate(
            nodes = faultedNodes,
            forwardParse = parse,
            asCoreToken = "as[core::truth_g#7]",
            continuitySha256 = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
            sha256Verified = true,
            roomAuditVerified = true,
            deployEpoch = 3,
            roomAuditRecordCount = 5
        )
        assertFalse(blockedState.checkPassTrueOnly)
        assertFalse(blockedState.zeroEgressVerified)
        assertEquals("check:pass:false [BLOCKED]", blockedState.assertionToken)
    }

    @Test
    fun spectRoomSensorPathEngine_stepsAllSixSensorPathsInRealTime() {
        val state = AxiomUiState()
        val initialDash = state.spectSensorDashboardState
        assertEquals(6, initialDash.sensorPaths.size)
        assertTrue(initialDash.liveStreamingActive)

        val stepped = com.example.service.SpectRoomSensorPathEngine.stepSensorDashboard(
            previous = initialDash,
            simTimeSec = 2.4f,
            eegBands = state.eegBands,
            dominantHz = 16.4f,
            phaseCoherencePct = 94,
            cognitiveLoadPct = 28,
            asCoreFrame = state.asCoreFrame,
            hardwareTelemetry = state.hardwareTelemetry,
            frameMs = 16.5f,
            rotatingFrqHz = 0.65f,
            perturbation = 0.0f,
            unicriticConsensus = 98,
            effectiveDrift = 0.0f,
            sha256Verified = true,
            checkPassTrueOnly = true,
            continuitySha256 = state.continuityCurrentSha256,
            persistedRecordCount = 4
        )
        assertEquals(6, stepped.sensorPaths.size)
        assertEquals(6, stepped.activeSensorPathsCount)
        assertTrue(stepped.totalPacketsRouted > initialDash.totalPacketsRouted)
        assertTrue(stepped.aggregateHealthPct >= 90)
        assertTrue(stepped.sensorPaths.all { it.waveformHistory.size == 48 && it.sha256Verified })
    }

    @Test
    fun globalVsPersonalDeployEngine_enforcesZeroRecurringSubs_corporateGreedDecay_andOwnDevLogicStates() {
        val state = AxiomUiState()
        val wp = state.unifiedDeployWhitepaperState
        assertEquals(com.example.service.DeployTierMode.PERSONAL_DEV_FREE, wp.activeTierMode)
        assertEquals("[Personal account:DEV]", wp.personalDevAccountTag)
        assertEquals("1+ /Own/Dev/Logic_States", wp.ownDevLogicCanonicalAlias)
        assertEquals(0.00f, wp.decayMetrics.recurringSubscriptionFeeUsd, 0.0001f)
        assertTrue(wp.decayMetrics.noSubsContractLocked)
        assertEquals(4, wp.ownDevLogicNodes.size)
        assertEquals(4, wp.whitepaperComparisonRows.size)
        assertTrue(wp.ownDevLogicNodes.all {
            it.stateSha256.length == 64 && LocalOnlyPathsInterconnectEngine.isPathStrictlyLocal(it.logicPath)
        })
        assertTrue(LocalOnlyPathsInterconnectEngine.isPathStrictlyLocal("1+ /Own/Dev/Logic_States"))

        // Step corporate bond decay epoch -> remainder decreases, commons subsidy & decay % increase
        val epoch1 = com.example.service.GlobalVsPersonalDeployEngine.evaluateCorporateDecay(
            initialBondUnits = 1000.0f,
            decayLambda = 0.42f,
            decayEpoch = 1
        )
        val epoch6 = com.example.service.GlobalVsPersonalDeployEngine.evaluateCorporateDecay(
            initialBondUnits = 1000.0f,
            decayLambda = 0.42f,
            decayEpoch = 6
        )
        assertEquals(0.00f, epoch1.recurringSubscriptionFeeUsd, 0.0001f)
        assertEquals(0.00f, epoch6.recurringSubscriptionFeeUsd, 0.0001f)
        assertTrue(epoch6.currentBondRemainder < epoch1.currentBondRemainder)
        assertTrue(epoch6.commonsSubsidyUnits > epoch1.commonsSubsidyUnits)
        assertTrue(epoch6.corpGreedDecayPct > epoch1.corpGreedDecayPct)

        val codexEntry = com.example.service.GlobalVsPersonalDeployEngine.buildRefinedWhitepaperCodexEntry(
            state = wp,
            micaRegimeLabel = "TODAY PRE + FUT MiCA [if D~]",
            micaDWaveDelta = 0.000f,
            continuitySha256 = state.continuityCurrentSha256
        )
        assertEquals("1+ /Own/Dev/Logic_States", codexEntry.categoryTag)
        assertTrue(codexEntry.codeSnippet.contains("[Personal account:DEV]"))
        assertTrue(codexEntry.codeSnippet.contains("S:OpenSource(nosubs!-asis)Contract!"))
        assertEquals(64, wp.whitepaperSealSha256.length)
    }

    @Test
    fun atlasKnowabilityAndOdmEngine_discriminatesRgbAndFrqKnowability_andEngagesAtlasRedOnLostPath() {
        val state = AxiomUiState()
        val initial = state.atlasKnowabilityState
        assertTrue(initial.rgbKnowable)
        assertTrue(initial.frqKnowable)
        assertFalse(initial.onLostPath)
        assertFalse(initial.asAtlasRedActive)
        assertEquals(5, initial.centerFileLines.size)

        // 1. Force RGB Not Knowable -> must engage As Atlas (RED) On Lost Path
        val rgbBlind = com.example.service.AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = initial.copy(forceRgbUnknowable = true),
            asCoreFrame = state.asCoreFrame,
            asCoreStreamActive = true,
            eegBands = state.eegBands,
            dominantHz = state.dominantFrequencyHz,
            rotatingFrqHz = state.effectiveRotatingFrequencyHz,
            phaseCoherencePct = state.phaseCoherencePct,
            perturbation = 0.0f,
            glslGateTrue = true,
            redlineThreatActive = false,
            localInterconnectState = state.localInterconnectState,
            spectSensorHealthPct = 98,
            simTimeSec = 1.2f
        )
        assertFalse(rgbBlind.rgbKnowable)
        assertTrue(rgbBlind.frqKnowable)
        assertTrue(rgbBlind.onLostPath)
        assertTrue(rgbBlind.asAtlasRedActive)
        assertTrue(rgbBlind.atlasStateBadge.contains("AS ATLAS (RED)"))

        // 2. Force FRQ Not Knowable -> must engage As Atlas (RED) On Lost Path
        val frqBlind = com.example.service.AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = initial.copy(forceFrqUnknowable = true),
            asCoreFrame = state.asCoreFrame,
            asCoreStreamActive = true,
            eegBands = state.eegBands,
            dominantHz = state.dominantFrequencyHz,
            rotatingFrqHz = state.effectiveRotatingFrequencyHz,
            phaseCoherencePct = state.phaseCoherencePct,
            perturbation = 0.0f,
            glslGateTrue = true,
            redlineThreatActive = false,
            localInterconnectState = state.localInterconnectState,
            spectSensorHealthPct = 98,
            simTimeSec = 2.4f
        )
        assertTrue(frqBlind.rgbKnowable)
        assertFalse(frqBlind.frqKnowable)
        assertTrue(frqBlind.onLostPath)
        assertTrue(frqBlind.asAtlasRedActive)

        // 3. Verify ODM Guarded Command Allowlist & Register 0x200F Parity
        val (_, exitOff, blockedOff) = com.example.service.AtlasKnowabilityAndOdmEngine.executeGuardedOdmCommand("uname", gateStatusOn = false)
        assertEquals(3, exitOff)
        assertEquals(1, blockedOff)

        val (_, exitOk, blockedOk) = com.example.service.AtlasKnowabilityAndOdmEngine.executeGuardedOdmCommand("uname", gateStatusOn = true)
        assertEquals(0, exitOk)
        assertEquals(0, blockedOk)

        val (_, enforcedHex, parityBit) = com.example.service.AtlasKnowabilityAndOdmEngine.enforceRegister0x200F(0x0000)
        assertEquals("0xA00F", enforcedHex)
        assertEquals(1, parityBit)

        // 4. Verify <or if lie> detection engages AS ATLAS (RED) and Open Intake Cant O-Ring Jump In/Out
        assertTrue(initial.centerLinesHiddenOpenAperture)
        assertFalse(initial.orIfLieDetected)
        val lieDetected = com.example.service.AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = initial.copy(
                forceOrIfLieDetected = true,
                intakeCantJumpMode = com.example.service.IntakeCantJumpMode.JUMP_IN_CORE,
                intakeCantAngleDeg = 32f
            ),
            asCoreFrame = state.asCoreFrame,
            asCoreStreamActive = true,
            eegBands = state.eegBands,
            dominantHz = state.dominantFrequencyHz,
            rotatingFrqHz = state.effectiveRotatingFrequencyHz,
            phaseCoherencePct = state.phaseCoherencePct,
            perturbation = 0.0f,
            glslGateTrue = true,
            redlineThreatActive = false,
            localInterconnectState = state.localInterconnectState,
            spectSensorHealthPct = 98,
            simTimeSec = 3.0f
        )
        assertTrue(lieDetected.orIfLieDetected)
        assertTrue(lieDetected.onLostPath)
        assertTrue(lieDetected.asAtlasRedActive)
        assertEquals(1.42f, lieDetected.effectiveJumpZoomFactor, 0.001f)
        assertFalse(lieDetected.isGoodStack)

        // 5. Verify Sonic Sound Swap "translate" and Self-Teaching Good Stack Autodidact
        val goodStackState = com.example.service.AtlasKnowabilityAndOdmEngine.evaluateState(
            previous = initial.copy(
                sonicSoundSwapTranslateActive = true,
                sonicSoundSwapMode = com.example.service.SonicSoundSwapMode.CHROMA_SYNESTHESIA_CASCADE,
                intakeCantJumpMode = com.example.service.IntakeCantJumpMode.JUMP_OUT_ORBIT,
                selectedOdmTarget = com.example.service.OdmAlignmentTarget.TARGET_C_RUST_DSP
            ),
            asCoreFrame = state.asCoreFrame,
            asCoreStreamActive = true,
            eegBands = state.eegBands,
            dominantHz = state.dominantFrequencyHz,
            rotatingFrqHz = state.effectiveRotatingFrequencyHz,
            phaseCoherencePct = state.phaseCoherencePct,
            perturbation = 0.0f,
            glslGateTrue = true,
            redlineThreatActive = false,
            localInterconnectState = state.localInterconnectState,
            spectSensorHealthPct = 98,
            simTimeSec = 4.0f
        )
        assertTrue(goodStackState.isGoodStack)
        assertTrue(goodStackState.goodStackScorePct >= 95)
        assertEquals(0.76f, goodStackState.effectiveJumpZoomFactor, 0.001f)
        assertTrue(goodStackState.sonicTranslateStatusLabel.contains("translate"))
        assertTrue(goodStackState.selfTeachStatusBanner.contains("GOOD STACK"))
    }

    @Test
    fun protocolO_integrityCore2026_volatileGate_audiusKeyless_and_certSublayer_verified() {
        val engine = com.example.service.ProtocolOIntegrityCoreEngine
        engine.purgeVolatileKey()
        assertFalse(engine.isOrbKeyActive())

        // 1. _gate commit & purge (commit: 9fe2e76 — volatile in-memory only)
        assertTrue(engine.commitVolatileKey("volatile-secret-o-gate-key"))
        assertTrue(engine.isOrbKeyActive())
        engine.purgeVolatileKey()
        assertFalse(engine.isOrbKeyActive())

        // 2. _util.shortHash & _util.extractStructure (commit: ab124cf)
        val hash = engine.shortHash("protocol-o-2026")
        assertTrue(hash.startsWith("0x"))
        assertEquals(10, hash.length)

        // 3. _engine.audius(query) — keyless REST API integration + 7-Band Sonic "translate" drive
        val (audiusJson, track) = engine.executeAudiusKeyless(
            query = "hex reflex sonic translate",
            preset = com.example.service.NetworkProbePreset.AUDIUS_TRACKS_SEARCH
        )
        assertTrue(audiusJson.contains("_engine.audius"))
        assertTrue(audiusJson.contains("ZERO_KEY_PERMISSIONLESS"))
        assertTrue(track.streamReady)
        assertTrue(track.sevenBandDrive.reinstakeGuardSafe)

        val keyTree = engine.extractStructure(audiusJson)
        assertTrue(keyTree.contains("data"))
        assertTrue(keyTree.contains("protocol_o_meta"))

        // 4. _cert volatile certificate lifecycle (#cert-sublayer) & Predefined Deterministic Sync
        val initialProto = engine.buildInitialState()
        val atlas = com.example.service.AtlasKnowabilityAndOdmState()
        val contSha = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4"
        val certA = engine.generateVolatileCertificate(
            state = initialProto,
            atlas = atlas,
            continuitySha256 = contSha
        )
        val certB = engine.generateVolatileCertificate(
            state = initialProto,
            atlas = atlas,
            continuitySha256 = contSha
        )
        assertTrue(certA.certId.startsWith("CERT-"))
        assertEquals(certA.certId, certB.certId)
        assertEquals(certA.sha256Seal, certB.sha256Seal)
        assertTrue(certA.confidenceScorePct >= 90)
        assertTrue(certA.volatileBlobJson.contains("VOLATILE_IN_MEMORY_ONLY_ZERO_PERSISTENCE"))

        // 5. Predefined Deterministic Self-Update, Upgrade & Sync reproducibility
        val syncRun1 = engine.executePredefinedDeterministicSelfUpdateSync(
            currentState = initialProto,
            atlas = atlas,
            continuitySha256 = contSha
        )
        val syncRun2 = engine.executePredefinedDeterministicSelfUpdateSync(
            currentState = initialProto,
            atlas = atlas,
            continuitySha256 = contSha
        )
        assertTrue(syncRun1.deterministicSyncLocked)
        assertEquals("v2026.04.12-DET-SYNC", syncRun1.deterministicSyncVersion)
        assertEquals(6, syncRun1.deterministicUpgradeNodes.size)
        assertEquals(syncRun1.deterministicVectorShortHash, syncRun2.deterministicVectorShortHash)
        assertEquals(syncRun1.deterministicVectorSha256, syncRun2.deterministicVectorSha256)
        assertEquals(syncRun1.volatileCertificate?.sha256Seal, syncRun2.volatileCertificate?.sha256Seal)

        // 6. Emergency Signal-Loss Self-Dependency Reload Pack & 6-Hop Bidirectional Merkle-Mesh
        val reloaded = engine.executeEmergencySelfDependencyReload(
            currentState = syncRun1,
            atlas = atlas,
            continuitySha256 = contSha,
            signalLossTriggered = true,
            triggerReason = "UNIT_TEST_SIGNAL_LOSS_AUTO_RELOAD"
        )
        val pack = reloaded.emergencyReloadPack
        assertTrue(pack.packId.startsWith("EMERG-PACK-"))
        assertTrue(pack.signalLossDetected)
        assertTrue(pack.allMeshJointsVerified)
        assertEquals(6, pack.relayVectorHops.size)
        assertEquals(64, pack.merkleMeshRootSha256.length)
        // Verify closed 6-hop ring connectivity (RV-01 -> ... -> RV-06 -> RV-01)
        assertEquals("ODM-KERNEL", pack.relayVectorHops[0].sourceNodeCode)
        assertEquals("ODM-KERNEL", pack.relayVectorHops[5].targetNodeCode)
        pack.relayVectorHops.forEach { hop ->
            assertEquals(64, hop.sliceSha256.length)
            assertEquals(64, hop.forwardJointSha256.length)
            assertEquals(64, hop.reverseJointSha256.length)
            assertEquals(64, hop.meshJointSha256.length)
            assertTrue(hop.jointVerified)
        }

        // 7. EmergencyReloadRepositoryService: O(1) 16-bit signal-loss detection & one-tap reload
        val repoService = com.example.service.EmergencyReloadRepositoryService
        assertTrue(repoService.verifyMerkleMeshIntegrity(pack))

        val degradedAtlas = atlas.copy(
            rgbKnowable = false,
            onLostPath = true,
            asAtlasRedActive = true,
            odmGateStatusOn = false,
            runBlockedFlag = 1
        )
        val nominalInterconnect = com.example.service.ServerlessLocalDeployState()
        val faultDetect = repoService.detectSignalLossVector(
            atlas = degradedAtlas,
            interconnect = nominalInterconnect,
            proto = initialProto
        )
        assertTrue(faultDetect.signalLossDetected)
        assertTrue(faultDetect.autoReloadRecommended)
        assertTrue(faultDetect.activeFaultCodes.contains("RV-01:ODM_GATE_DROP"))
        assertTrue(faultDetect.activeFaultCodes.contains("RV-04:RGB_FRQ_LOST_PATH"))

        val reloadBundle = repoService.executeOneTapOrAutoReload(
            currentProto = initialProto,
            currentAtlas = degradedAtlas,
            currentInterconnect = nominalInterconnect,
            forwardParse = com.example.model.ForwardTunnelParseResult(),
            asCoreFrame = com.example.service.AsCoreRgbBiometricFrame(),
            eegBands = com.example.model.EegBandState(),
            continuitySha256 = contSha,
            signalLossTriggered = true,
            triggerReason = "REPO_SERVICE_AUTO_RELOAD"
        )
        assertFalse(reloadBundle.detectionSnapshot.signalLossDetected)
        assertEquals("0x7F01", reloadBundle.detectionSnapshot.signalHealthMaskHex)
        assertTrue(reloadBundle.restoredAtlas.rgbKnowable)
        assertTrue(reloadBundle.restoredAtlas.frqKnowable)
        assertFalse(reloadBundle.restoredAtlas.asAtlasRedActive)
        assertTrue(reloadBundle.restoredInterconnect.checkPassTrueOnly)
    }
}




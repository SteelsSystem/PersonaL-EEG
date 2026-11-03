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
    fun modularStackAndLicensingEngine_verifiesModularityBlocks_termuxMatrix_andSelfCorrectingTc() {
        val state = AxiomUiState()
        val mod = state.modularStackLicensingState
        assertEquals(6, mod.modularityBlocks.size)
        assertEquals(10, mod.comparisonRows.size)
        assertEquals(4, mod.marketingPillars.size)
        assertEquals(6, mod.licensingClauses.size)
        assertTrue(mod.licensingCompliantAll)
        assertEquals(0.00f, mod.recurringFeeUsd, 0.0001f)
        assertEquals("S:OpenSource(nosubs!-asis)Contract!", mod.contractIdentifier)
        assertEquals(64, mod.licensingSealSha256.length)

        // Verify MOD-02 contains content://com.termux.files and 268435457 flag bitmask
        val mod02 = mod.modularityBlocks.first { it.id == "MOD-02" }
        assertTrue(mod02.codeSnippet.contains("content://com.termux.files"))
        assertTrue(mod02.codeSnippet.contains("268435457"))

        // Simulate T&C drift -> non-compliant, then self-correct & seal -> compliant with $0.00 recurring fee
        val drifted = com.example.service.ModularStackAndLicensingEngine.simulateLicensingDrift(mod)
        assertFalse(drifted.licensingCompliantAll)
        assertTrue(drifted.recurringFeeUsd > 0f)

        val healed = com.example.service.ModularStackAndLicensingEngine.selfCorrectAndSealLicensing(drifted)
        assertTrue(healed.licensingCompliantAll)
        assertEquals(0.00f, healed.recurringFeeUsd, 0.0001f)
        assertTrue(healed.licensingClauses.all { it.isCompliant })
        assertEquals(64, healed.licensingSealSha256.length)

        // Verify Allowed to Compare 2FA & Metallurgia:THEME(STEEL ALLOY ANTIMAG POSITION)
        assertEquals("Metallurgia:THEME(STEEL ALLOY ANTIMAG POSITION)", healed.metallurgiaThemeDesignTactic)
        assertTrue(healed.twoFactorComparison.allowedToCompare2fa)
        assertTrue(healed.twoFactorComparison.factorsMatchVerified)
        assertTrue(healed.twoFactorComparison.composite2faPairingToken.startsWith("2FA-"))
        assertTrue(healed.twoFactorComparison.expectedTerminalOutputTrace.contains("Allowed to Compare 2FA      : TRUE [MATCH CONFIRMED]"))
        assertTrue(healed.twoFactorComparison.expectedTerminalOutputTrace.contains("check:pass:true_only"))

        val muMetalState = com.example.service.ModularStackAndLicensingEngine.selectMetallurgiaPreset(
            healed,
            com.example.service.MetallurgiaSteelAlloyPreset.MU_METAL_PERMEABILITY_SHIELD
        )
        assertEquals(1.001f, muMetalState.activeMetallurgiaPreset.relativePermeabilityMuR, 0.0001f)
        assertTrue(muMetalState.twoFactorComparison.expectedTerminalOutputTrace.contains("MU-METAL // FARADAY CAGE"))
    }
}

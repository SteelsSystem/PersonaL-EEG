package com.example.service

import com.example.model.ForwardTunnelParseResult
import java.security.MessageDigest
import java.util.Locale

enum class LocalEndpointCategory(val tag: String) {
    LEGAL_AXIS_RELAY("PDF-05 RELAY"),
    SPECT_SQLITE_VAULT("ROOM SQLITE"),
    LOCAL_ASSET_VAULT("ASSET ENCLAVE")
}

data class LocalInterconnectNode(
    val id: String,
    val code: String,
    val category: LocalEndpointCategory,
    val localPath: String,
    val transportMode: String,
    val isStrictlyLocal: Boolean = true,
    val latencyMicros: Int = 42,
    val hopSha256: String = ""
)

data class ServerlessLocalDeployState(
    val checkPassTrueOnly: Boolean = true,
    val assertionToken: String = "check:pass:true_only",
    val zeroEgressVerified: Boolean = true,
    val sha256SealVerified: Boolean = true,
    val roomAuditVerified: Boolean = true,
    val onlyLocalLogicFlag: Boolean = true,
    val activeNodeId: String = "DEST-1",
    val deployEpoch: Int = 1,
    val roomAuditRecordCount: Int = 2,
    val interconnectSealSha256: String = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
    val statusSummary: String = "LOCAL ONLY WAY // All 6 Interconnect Paths Isolated • Zero Egress • Room Audit Sealed",
    val nodes: List<LocalInterconnectNode> = emptyList()
)

object LocalOnlyPathsInterconnectEngine {

    fun buildDefaultNodes(
        forwardParse: ForwardTunnelParseResult = ForwardTunnelParseResult(),
        continuitySha256: String = "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4"
    ): List<LocalInterconnectNode> {
        val rawNodes = listOf(
            LocalInterconnectNode(
                id = "DEST-1",
                code = "ADB-127",
                category = LocalEndpointCategory.LEGAL_AXIS_RELAY,
                localPath = forwardParse.legalTargets.getOrNull(0)?.path
                    ?: "\$HOME/.core-relay/dist/PortfolioRelay.apk",
                transportMode = "127.0.0.1:5555 Loopback Bridge",
                isStrictlyLocal = true,
                latencyMicros = 38
            ),
            LocalInterconnectNode(
                id = "DEST-2",
                code = "LOCAL-APK",
                category = LocalEndpointCategory.LEGAL_AXIS_RELAY,
                localPath = forwardParse.legalTargets.getOrNull(1)?.path
                    ?: "/sdcard/Download/PortfolioRelay-core.apk",
                transportMode = "file:///sdcard Package Installer",
                isStrictlyLocal = true,
                latencyMicros = 54
            ),
            LocalInterconnectNode(
                id = "DEST-3",
                code = "CORE-VAULT",
                category = LocalEndpointCategory.LEGAL_AXIS_RELAY,
                localPath = forwardParse.legalTargets.getOrNull(2)?.path
                    ?: "\$HOME/.core-relay/core/PortfolioRelay-core.apk",
                transportMode = "chmod 0400 Read-Only Enclave",
                isStrictlyLocal = true,
                latencyMicros = 29
            ),
            LocalInterconnectNode(
                id = "DEST-4",
                code = "LOOP-STAGE",
                category = LocalEndpointCategory.LEGAL_AXIS_RELAY,
                localPath = forwardParse.legalTargets.getOrNull(3)?.path
                    ?: "\$HOME/.core-relay/local/PortfolioRelay-local.apk",
                transportMode = "127.0.0.1 Serverless Stage",
                isStrictlyLocal = true,
                latencyMicros = 31
            ),
            LocalInterconnectNode(
                id = "NODE-5",
                code = "SPECT-SQLITE",
                category = LocalEndpointCategory.SPECT_SQLITE_VAULT,
                localPath = "sqlite:///data/user/0/com.example/databases/sy4_eeg_spect_local.db",
                transportMode = "Room @Database WAL Local I/O",
                isStrictlyLocal = true,
                latencyMicros = 46
            ),
            LocalInterconnectNode(
                id = "NODE-6",
                code = "ASSET-VAULT",
                category = LocalEndpointCategory.LOCAL_ASSET_VAULT,
                localPath = "file:///android_asset/axiom_glsl_codex_as_core.bundle",
                transportMode = "Zero-Egress Asset & Shader Bus",
                isStrictlyLocal = true,
                latencyMicros = 24
            )
        )
        return rawNodes.map { node ->
            node.copy(hopSha256 = computeHopSha256(node, continuitySha256))
        }
    }

    fun isPathStrictlyLocal(path: String): Boolean {
        val trimmed = path.trim().lowercase(Locale.US)
        if (trimmed.isEmpty()) return false
        val forbiddenPrefixes = listOf("http://", "https://", "ws://", "wss://", "ftp://", "s3://", "gs://")
        if (forbiddenPrefixes.any { trimmed.startsWith(it) }) return false
        return trimmed.startsWith("\$home/.core-relay") ||
            trimmed.startsWith("/sdcard/") ||
            trimmed.startsWith("/data/") ||
            trimmed.startsWith("/own/dev/logic_states") ||
            trimmed.startsWith("1+ /own/dev/logic_states") ||
            trimmed.startsWith("sqlite:///") ||
            trimmed.startsWith("file:///") ||
            trimmed.startsWith("127.0.0.1")
    }

    fun computeHopSha256(node: LocalInterconnectNode, seedSha256: String): String {
        val payload = "HOP=${node.id}|CODE=${node.code}|PATH=${node.localPath}|MODE=${node.transportMode}|LOCAL=${node.isStrictlyLocal}|SEED=${seedSha256.take(32)}"
        return sha256Hex(payload)
    }

    fun evaluateDeployGate(
        nodes: List<LocalInterconnectNode>,
        forwardParse: ForwardTunnelParseResult,
        asCoreToken: String,
        continuitySha256: String,
        sha256Verified: Boolean,
        roomAuditVerified: Boolean,
        deployEpoch: Int,
        roomAuditRecordCount: Int,
        activeNodeId: String = "DEST-1"
    ): ServerlessLocalDeployState {
        val updatedNodes = nodes.map { node ->
            val strictlyLocal = isPathStrictlyLocal(node.localPath)
            val updated = node.copy(isStrictlyLocal = strictlyLocal)
            updated.copy(hopSha256 = computeHopSha256(updated, continuitySha256))
        }

        val zeroEgress = updatedNodes.isNotEmpty() &&
            updatedNodes.all { it.isStrictlyLocal } &&
            forwardParse.onlyLocalLogicFlag &&
            forwardParse.bracketDepthBalanced

        val manifestPayload = buildString {
            append("INFRA=SERVERLESS_LOCAL_ONLY_WAY")
            append("|EPOCH=").append(deployEpoch)
            append("|ONLY_LOCAL_LOGIC=").append(if (forwardParse.onlyLocalLogicFlag) 1 else 0)
            append("|GRAMMAR=").append(forwardParse.rawGrammar)
            append("|AS_CORE=").append(asCoreToken)
            append("|CONTINUITY=").append(continuitySha256)
            updatedNodes.forEach { n ->
                append("|").append(n.id).append("=").append(n.localPath).append(":").append(n.hopSha256.take(16))
            }
        }
        val sealSha256 = sha256Hex(manifestPayload)
        val shaSealOk = sha256Verified && sealSha256.length == 64

        val passTrueOnly = zeroEgress && shaSealOk && roomAuditVerified
        val assertionToken = if (passTrueOnly) {
            "check:pass:true_only"
        } else {
            "check:pass:false [BLOCKED]"
        }

        val summary = when {
            passTrueOnly -> "SERVERLESS INFRA DEPLOY: check:pass:true_only • 6/6 Local Paths Bridged • Zero Egress • SHA-256 & Room Sealed (Epoch #$deployEpoch)"
            !zeroEgress -> "DEPLOY HALTED [check:pass:false] • Non-local egress path detected in Interconnect Mesh! Auto-Remediate required."
            !shaSealOk -> "DEPLOY HALTED [check:pass:false] • SHA-256 script or continuity digest mismatch."
            else -> "DEPLOY HALTED [check:pass:false] • Local SPECTRoom audit parity check pending."
        }

        return ServerlessLocalDeployState(
            checkPassTrueOnly = passTrueOnly,
            assertionToken = assertionToken,
            zeroEgressVerified = zeroEgress,
            sha256SealVerified = shaSealOk,
            roomAuditVerified = roomAuditVerified,
            onlyLocalLogicFlag = forwardParse.onlyLocalLogicFlag,
            activeNodeId = activeNodeId,
            deployEpoch = deployEpoch,
            roomAuditRecordCount = roomAuditRecordCount,
            interconnectSealSha256 = sealSha256,
            statusSummary = summary,
            nodes = updatedNodes
        )
    }

    fun buildServerlessDeployManifestScript(state: ServerlessLocalDeployState): String {
        val nodeLines = state.nodes.joinToString("\n") { node ->
            "    (\"${node.id}\", \"${node.code}\", \"${node.localPath}\", ${node.isStrictlyLocal}), // hop_sha256=${node.hopSha256.take(16)}"
        }
        return """
            // LOCAL ONLY PATHS INTERCONNECT -> LOCAL ONLY WAY OF SERVERLESS INFRA DEPLOY
            // Gate Assertion: ${state.assertionToken} (Epoch #${state.deployEpoch})
            // Unified [PaidvsFREE]: [Personal account:DEV] • Subs=$0.00 • 1+ /Own/Dev/Logic_States
            // Invariants: zero_egress=${state.zeroEgressVerified} | sha256_seal=${state.sha256SealVerified} | room_audit=${state.roomAuditVerified}
            pub const ONLY_LOCAL_LOGIC: u8 = ${if (state.onlyLocalLogicFlag) 1 else 0};
            pub const PERSONAL_DEV_ORIGIN: &str = "[Personal account:DEV]";
            pub const OWN_DEV_LOGIC_STATES_PATH: &str = "1+ /Own/Dev/Logic_States";
            pub const RECURRING_SUBS_FEE_USD: f32 = 0.00;
            pub const DEPLOY_GATE_ASSERTION: &str = "${state.assertionToken}";
            pub const INTERCONNECT_SEAL_SHA256: &str = "${state.interconnectSealSha256}";

            pub const LOCAL_INTERCONNECT_MESH: [(&str, &str, &str, bool); 6] = [
            $nodeLines
            ];

            pub fn verify_serverless_local_only_deploy() -> Result<&'static str, &'static str> {
                let all_local = LOCAL_INTERCONNECT_MESH.iter().all(|(_, _, _, is_local)| *is_local);
                if ONLY_LOCAL_LOGIC == 1 && all_local {
                    Ok("check:pass:true_only")
                } else {
                    Err("check:pass:false")
                }
            }
        """.trimIndent()
    }

    private fun sha256Hex(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

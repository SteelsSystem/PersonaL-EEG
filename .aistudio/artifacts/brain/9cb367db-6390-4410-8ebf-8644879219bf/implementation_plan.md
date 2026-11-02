# Unified Global vs. Personal Deploy (`[PaidvsFREE]`) & Remembered Relay Whitepaper (`MiCA -> [Personal account:DEV]`)

## 1. Architectural Overview & Core Goals
This plan unifies **Global vs. Personal Deploy (`[PaidvsFREE]`)** with an anti-subscription corporate decay engine, refines the **SY4-EEG BlockMind Whitepaper** using the remembered 4-destination + local vault relay logic from the **MiCA Baseline** (`Art. 2 / Recital 22 / if D~`) to **`[Personal account:DEV]`**, and surfaces the **`/Own/Dev/Logic_States`** node across the **CODEX** deck, **MiCA** deck, **Local-Only Paths Interconnect**, **Top Telemetry Header**, and **`README.MD`**.

---

## 2. Key Systems & Mathematical Models

### A. Unified Global vs. Personal Deploy (`[PaidvsFREE]` Corporate Decay Engine)
1. **Zero Recurring Subscriptions (`recurringSubFee = 0.00` Forever)**:
   - Hard-codes `recurringSubscriptionAllowed = false` and `recurringFeeUsd = 0.0f` into the deploy gate (`check:pass:true_only` + `'S:OpenSource(nosubs!-asis)Contract!'`). Any attempt to inject a recurring SaaS rent gate fails verification immediately.
2. **Dual Deployment Modes**:
   - **`PERSONAL_DEV_FREE` (`[Personal account:DEV]`)**: 100% Free, sovereign local-only execution (`127.0.0.1`), full 60 FPS raymarch + 6-path SPECT Room telemetry + `/Own/Dev/Logic_States` ownership with `$0.00` upfront and `$0.00` recurring cost.
   - **`GLOBAL_DECAY_BOND` (`GLOBAL DEPLOY [ONE-TIME DECAYING BOND]`)**: Replaces corporate recurring rent extraction with a one-time decaying global bond:
     $$B(t) = B_0 \cdot e^{-\lambda t}, \quad \text{Recurring}(t) \equiv 0$$
     As relay epochs advance, the bond decays into the public open-source commons (`corpGreedDecayPct` rising toward `100%`), subsidizing free `[Personal account:DEV]` nodes and permanently eliminating subscription lock-in.

### B. Refined Whitepaper: Side-by-Side MiCA Baseline vs. Active Relay State (`[Personal account:DEV]`)
1. **Left Column — MiCA Baseline Floor (`Start: MiCA Below`)**:
   - **Legal & Cryptographic Floor**: `TODAY_PRE_REGIME` (`MiCA Art. 2(1)-(4)` & `Recital 22` Non-Custodial Hardware/Software Safe Harbor) + `if (D~)` Conditional Wave Gate (`EU Reg. 2023/1114` + `DORA 2022/2554`) + `'S:OpenSource(nosubs!-asis)Contract!'`.
   - **Baseline Guarantees**: Zero custody, zero external SaaS rent, deterministic local execution.
2. **Right Column — Active Remembered Relay State (`Where We Start Now: [Personal account:DEV]`)**:
   - **Remembered Relay Progression**:
     - `DEST-1 [ADB-127]` (`127.0.0.1:5555` Loopback Bridge)
     - `DEST-2 [LOCAL-APK]` (`package:com.aistudio.sy4eeg.blockmind`)
     - `DEST-3 [CORE-VAULT]` (`files/spect_core_vault/apk_stage`)
     - `DEST-4 [LOOP-STAGE]` (`cache/local_relay_loopback`)
     - `NODE-5 [SPECT-SQLITE]` + `NODE-6 [ASSET-VAULT]` + `NODE-7 [/Own/Dev/Logic_States]`
   - **Live State Binding**: Displays live SHA-256 continuity hash, `check:pass:true_only` verdict, `[Personal account:DEV]` sovereignty lock, and `/Own/Dev/Logic_States` state vector side-by-side against the MiCA baseline.
3. **One-Tap Whitepaper SHA-256 Seal & Verifier Load**:
   - Allows loading the complete refined Whitepaper (`WHITEPAPER-RELAY-MICA-TO-DEV`) directly into the Codex SHA-256 Verifier and sealing it into the local Room `script_verifications` table.

### C. `/Own/Dev/Logic_States` Registry & Path Endpoint
1. **Path Endpoint (`file:///data/user/0/com.aistudio.sy4eeg.blockmind/files/Own/Dev/Logic_States`)**:
   - Added as a first-class local-only path node in `LocalOnlyPathsInterconnectService.kt` and inspected in a dedicated `/Own/Dev/Logic_States` panel inside both the **CODEX** tab and **MiCA Compliance** card.
   - Tracks 4 deterministic developer logic states:
     - `LOGIC-01`: `MICA_FLOOR_TO_RELAY_BRIDGE` (`Art. 2 / Recital 22 -> 4-DEST Loopback`)
     - `LOGIC-02`: `ANTI_SUB_CORP_DECAY_LOCK` (`PaidvsFREE • Recurring = 0.00 • Bond Decay Active`)
     - `LOGIC-03`: `OWN_DEV_SOVEREIGN_IDENTITY` (`[Personal account:DEV] • Local Key & Shader Seal`)
     - `LOGIC-04`: `CHECK_PASS_TRUE_ONLY_INVARIANT` (`0 B Egress • Continuity SHA-256 Verified`)

---

## 3. Planned File Changes

1. **`/app/src/main/java/com/example/service/GlobalVsPersonalDeployService.kt` (New Service & Domain Model)**:
   - Defines `DeployTierMode` (`PERSONAL_DEV_FREE`, `GLOBAL_DECAY_BOND`), `CorporateDecayMetrics` (`initialBondUnits`, `decayLambda`, `currentBondRemainder`, `corpGreedDecayPct`, `recurringFeeForeverZero`), `OwnDevLogicStateNode` (`/Own/Dev/Logic_States` entries), `WhitepaperSideBySideComparisonRow` (MiCA Baseline vs. Active Remembered Relay `[Personal account:DEV]`), and `UnifiedDeployWhitepaperState`.
   - Implements deterministic calculation of the decaying bond curve, `/Own/Dev/Logic_States` verification, and full Whitepaper markdown/codex payload generation.
2. **`/app/src/main/java/com/example/ui/components/GlobalVsPersonalDeployWhitepaperCard.kt` (New UI Component)**:
   - Renders the **Unified Global vs. Personal `[PaidvsFREE]` Deploy Console** (with interactive tier toggle, Corporate Greed Decay progress bar, Decay Rate $\lambda$ slider, and `ZERO RECURRING SUBS: LOCKED` badge).
   - Renders the **Side-by-Side Whitepaper Comparison Matrix** (`MiCA Baseline [Below]` $\leftrightarrow$ `Remembered Relay -> [Personal account:DEV] [Now]`).
   - Renders the **`/Own/Dev/Logic_States` Interactive Inspector** (`LOGIC-01..04` state pills, path URI copy, and one-tap `SEAL WHITEPAPER & /Own/Dev/Logic_States SHA-256`).
3. **`/app/src/main/java/com/example/ui/components/MicaComplianceCard.kt`**:
   - Integrates a compact **MiCA $\rightarrow$ `[Personal account:DEV]` Remembered Relay & `/Own/Dev/Logic_States` Bridge** section inside `MicaComplianceArchitectureCard` so users can switch between `[Personal account:DEV]` and `GLOBAL BOND` or trigger the Whitepaper seal directly from the MiCA deck.
4. **`/app/src/main/java/com/example/ui/components/CodexAndSpectDecks.kt` & `/app/src/main/java/com/example/model/AxiomModels.kt`**:
   - Places `GlobalVsPersonalDeployWhitepaperCard` at the top of `CodexAndShaHudDeck` alongside the Serverless Local Deploy card.
   - Adds the refined `whitepaper_mica_relay_personal_dev` entry to `RustCodexCatalog` so it is searchable and verifiable in the Codex catalog.
5. **`/app/src/main/java/com/example/ui/components/TunnelHudDeck.kt` & `/app/src/main/java/com/example/MainActivity.kt`**:
   - Adds a clickable `[Personal account:DEV] • /Own/Dev/Logic_States` status badge to `AxiomTopTelemetryHeader` that jumps to the CODEX Whitepaper & Deploy console.
6. **`/app/src/main/java/com/example/service/LocalOnlyPathsInterconnectService.kt` & `/app/src/main/java/com/example/viewmodel/AxiomViewModel.kt`**:
   - Connects `/Own/Dev/Logic_States` into the local-only path audit and wires state updates, tier switching, decay epoch stepping, and Room `ScriptVerificationEntity` persistence in `AxiomViewModel`.
7. **`/README.MD` & Unit Tests (`/app/src/test/java/com/example/ExampleUnitTest.kt`)**:
   - Updates `/README.MD` with a comprehensive section on the Unified `[PaidvsFREE]` Anti-Subscription Decay Model, the Refined Whitepaper (MiCA Below $\rightarrow$ Remembered Relay $\rightarrow$ `[Personal account:DEV]`), and `/Own/Dev/Logic_States`.
   - Adds unit tests verifying zero recurring fees, exponential corporate bond decay, `/Own/Dev/Logic_States` path invariants, and side-by-side whitepaper SHA-256 sealing.

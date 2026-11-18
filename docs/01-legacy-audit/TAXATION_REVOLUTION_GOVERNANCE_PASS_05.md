# G0 Pass 05 — Taxation, governance and revolution

**Reviewed:** 2026-11-18. **Evidence:** older `netizen539/civcraft` Java files inspected directly and historical VimeWorld Civ Wiki articles independently read. **Status:** SOURCE-CONFIRMED for linked Java operations, WIKI-DOCUMENTED for later variant, DESIGN-PROPOSED for Reborn recommendations. No user design-freeze approval.

## 1. Taxes: separate payment flows

### Old code

- [Town.depositTaxed, lines 1081–1105](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java#L1081-L1105): income deposited to a town first calculates the civilization's income tax; the town receives a remainder. For occupied towns `war.captured_penalty` further modifies the retained balance. Town forwards the civilization's share to `Civilization.taxPayment`.
- [Civilization.taxPayment, lines 1130–1157](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1130-L1157): records taxes per town, routes `taxAmount * sciencePercentage` to research conversion and remainder to civilization treasury. The configured `coins_per_beaker` ratio may be modified by Great Library buff.
- [DailyTimer](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/threading/timers/DailyTimer.java#L51): daily town tax collection, town upkeep, civilization upkeep, debt progression.
- [SyncTradeTimer](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/threading/timers/SyncTradeTimer.java#L53): transfers share of trade proceeds to town treasury and civilization tax method.

### Later wiki

- [Civilization commands](https://civ-wiki.vimeworld.com/wiki/Команды_цивилизации): `/civ set taxes <процент>` defines income-tax percentage; `/civ set science <процент>` defines tax contribution used to purchase science.
- [Town commands](https://civ-wiki.vimeworld.com/wiki/Команды_города): `/t set taxrate <процент>` and `/t set flattax <сумма>` specify town-level collection; latter is a daily levy on residents. Distinct fees exist for library, bank, smith and grocer services.
- [Government](https://civ-wiki.vimeworld.com/wiki/Государственный_строй): government determines maintenance/tax effects and max taxation, while transitional anarchy increases building maintenance 5× and restricts income operations.

**Model requirement (proposed):** keep separate transaction types for `city personal tax`, `civilization income tax`, `city upkeep`, `civilization upkeep`, `service fee`, `occupation payment`, `research allocation` and `war surcharge`. Store monetary balance in one ledger, create explicit explanatory statement, and prevent double taxation through nested callbacks.

## 2. Revolution: exact old-code conditions

[Legacy CivCommand.revolution_cmd, lines 129–213](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/command/civ/CivCommand.java#L129-L213) verifies:

1. Revolution cannot begin during WarTime or the pre-war declaration window.
2. The selected town must have `motherCiv` and must be the **historical civilization's capital**.
3. Historical civilization must have a recorded conquest date and the configured `revolution_cooldown` must have elapsed.
4. Fee comes from `motherCiv.getRevolutionFee()`; confirmation requires `/civ revolution yes` and the capital town's treasury must cover fee.
5. All towns still retaining `motherCiv` return to the historical civilization. Code clears their motherCiv pointers.
6. Relations to their former current owners transition to `WAR`; each occupying civilization is marked aggressor.
7. Historical civ is marked unconquered and put into active civilization registry; revolution fee is charged.

The method performs a **sequence of mutable town updates before charging the fee**. This is a useful consistency/crash-risk target: Reborn should process sovereign reconstitution as an atomic, restart-safe operation. Method's name and comment suggest global return of eligible towns; verify all exceptions and permissions in the final audit.

[Legacy Town.onDefeat, lines 2085–2103](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java#L2085-L2103): remembers original civ on initial conquest; if recaptured by own historical state, clears the historical pointer.

[Legacy Town.capitulate, lines 2533 onward](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java#L2533): clears remembered native allegiance; captured city then cannot revolt through this mechanism. Capital capitulation calls civilization-wide capitulation.

### Later wiki occupation and city authority

- [War](https://civ-wiki.vimeworld.com/wiki/Война): after conquest, cities transfer under conqueror but remember former state; seven-day automatic capitulation is documented in this version. Peace, neutral and ally diplomacy may end war; liberation can return an uncapi­tulated town to original civilization.
- [Town Hall](https://civ-wiki.vimeworld.com/wiki/Ратуша): conqueror **cannot appoint a mayor before city capitulation** and initially has only limited access/ability to set tax. This is distinct from full civic administration.
- [Civilization commands](https://civ-wiki.vimeworld.com/wiki/Команды_цивилизации): `/civ dip capitulate`, `/civ dip liberate`, diplomacy and tax commands.

**Proposed data invariants:** `Town.currentRulerId`, `Town.nativeCivilizationId`, `Town.occupationStatus`, `Town.mayorId`, `Town.occupationDate`, `Town.capitulationDeadline`, `Town.taxAuthority` are separate. This is a candidate schema, not a finalized set of fields.

## 3. Governance and hierarchy

Wiki [city and plot permissions](https://civ-wiki.vimeworld.com/wiki/Группы_и_права_в_городе) and commands:
- Civilization roles `leaders` and `advisers`; city roles `mayors`, `assistants`, `residents`; distinct plot owner/group/others and build, destroy, interact, item-use flags.
- City/civilization powers are not necessarily equivalent: a conqueror can collect constrained taxes without replacing a mayor until capitulation.
- Anarchy follows a government switch; [current government page](https://civ-wiki.vimeworld.com/wiki/Государственный_строй) says duration depends on number of players, as opposed to [old government page](https://civ-wiki.vimeworld.com/wiki/Old_Государственный_строй)'s fixed 24 hours. **Version conflict, not one universal rule.**
- Political modes affect upkeep, trade, science and growth, so every authority action should check the active policy.

**Proposed Reborn hierarchy:** player and NPC actors share typed permissions. Personal autonomy does not imply right to manage a city; state leader and town mayor authority must be explicit. Never expose direct world edits bypassing claims through automated jobs.

## 4. Audit conflict register

| ID | Conflict or uncertainty | Why it matters |
|---|---|---|
| GOV-001 | Wiki original/old fixed 24h versus later player-count anarchy | Human-player count cannot be substituted with simulated NPC population blindly |
| GOV-002 | Occupying state receives some economic effects, but town hall source restricts governance and mayor installation until capitulation | Separate sovereign owner, taxation rights and local administrator |
| GOV-003 | Configurable daily taxes versus every NPC household being taxable | Different entity population model, needs explicit new balance |
| GOV-004 | Legacy revolution changes many towns then charges; no transactional recovery shown in inspected method | Risk of partial state after failure |
| GOV-005 | Wiki automatic capitulation after seven days relies on real-time/server schedule | Singleplayer save-pause, offline time and configuration require rule |
| GOV-006 | Differences between old and later wiki government modes/bonuses | Version-tag all balance data; do not merge without provenance |

## 5. Design implications for bounded world and patch stages

- **D-013** selectable Overworld bounds 10k/15k/25k affects culture influence, civ founding density, strategic distance, resource-site distribution, revolution fronts and borders.
- Border size is not a reasonable proxy for number of inhabitants or taxes. AI settlement density and economy must have explicit capacity/profiled budgets.
- **P0:** persist world border; maintain world-scoped registries and stable IDs. No full tax/war gameplay required in foundation.
- **P1:** identify NPCs and local settlement; only the minimal approved economic prerequisites for a complete living village.
- **P2:** city/civ treasuries, taxation, office/permissions and state budgets — fully delivered as one closed feature scope.
- **P5:** conquest, capitulation, liberation and revolution with stable history, atomic transfers, finance consistency.
- Later mechanics may be fully specified before P0, but implementation remains blocked by G0–G4.

## 6. Still to audit

1. Extract all `taxrate`, `flattax` and debt constraints and thresholds from Java/configs and wiki.
2. Trace original Town.revolt/civilization war recovery side effects; distinguish manual revolution from other capture variants.
3. Inspect full town and civ role permissions, including transfer/kick/claimleader authority.
4. Define calendar/offline progression for single-player and dedicated server.
5. Confirm war peace/neutral/ally transitions and occupied-city liabilities.
6. Review article edit history for contradictory world-wonder cap and government timing claims.

**Gate G0 OPEN. No mod code written.**

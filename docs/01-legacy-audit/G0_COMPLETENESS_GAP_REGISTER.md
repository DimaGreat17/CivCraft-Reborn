# G0 — Audit Completeness, Gaps and Exit Criteria

**Audit snapshot:** 2026-11-18. **Status: G0 IN PROGRESS / NOT EXIT-READY.**  
**Sources:** actual repository documents and their citations, not a runtime test. Legacy Bukkit source: `netizen539/civcraft` at referenced pinned commit; later VimeWorld rules: historic wiki.  
**Important:** no percentage-of-completion claim is defensible without per-feature coverage verification. An item appearing in a wiki index is not implementation evidence.

## 1. Audited assets — evidence reviewed

| Artifact | What is actually covered | Evidence tier / remaining limitation |
|---|---|---|
| [Legacy audit](./LEGACY_AUDIT.md), [config inventory](./CONFIG_INVENTORY.md) | 57 old tech IDs, 27 building IDs, 7 wonders, 7 governments, 28 trade goods, 7 spy missions, 5 random events, 2 special units, 45 town upgrades/research IDs | CONFIG; individual feature behavior not universally checked |
| [Java audit passes 1–3](./JAVA_CODE_AUDIT_01.md), [2](./JAVA_CODE_AUDIT_02.md), [3](./JAVA_CODE_AUDIT_03.md) | Models, research, warfare, building templates, DB and timers | TARGETED CODE, not full call graph nor runtime tests |
| [Technology graph](./TECHNOLOGY_GRAPH.md) | 57 old tech nodes with prerequisites, values and acyclicity | OLD CONFIG COMPLETE; later-era tree not complete |
| [Building parity](./BUILDING_PARITY_MATRIX.md) | 27 old configured structures + tentative class paths | IDs complete; complete per-building operational semantics NOT complete |
| [Later wiki inventory](./VIMEWORLD_WIKI_AUDIT.md), [cross-generation matrix](./GENERATION_COMPARISON_MATRIX.md) | Historical later systems and 13 cross-generation comparisons | WIKI / SELECTIVE CODE, not full feature-by-feature parity |
| [Era buildings index](./VIME_BUILDINGS_BY_ERA.md) | 73 entries in navigation categories, plus two civic centers separately | WIKI INDEX; not unique runtime IDs, not individual article audit |
| [Nations, religion, wonders](./VIME_NATIONS_RELIGIONS_WONDERS.md) | 7 nations, 7 later religion options, indexed 22 world + 6 national wonders | INDEX + SELECTIVE ARTICLES; individual wonders, exclusive national buildings missing |
| [Later detailed passes 01–04](./VIME_BUILDING_EFFECTS_PASS_01.md), [production](./VIME_PRODUCTION_SCIENCE_LOGISTICS_PASS_02.md), [agriculture](./VIME_AGRICULTURE_EXTRACTION_PASS_03.md), [social](./VIME_SOCIAL_HAPPINESS_GOVERNANCE_PASS_04.md) | Selected building effects, recipes, happiness, policies, city rights | PARTIAL ARTICLES, some unavailable/inconsistent |
| [Taxation and revolution](./TAXATION_REVOLUTION_GOVERNANCE_PASS_05.md), [diplomacy and war](./DIPLOMACY_WAR_CONSEQUENCES_PASS_06.md) | Source-traced treasury, revolts, treaties, liberation, wars | SOURCE + WIKI; full permission/edge-case graph missing |
| [Victory passes 07–09](./VICTORY_CONDITIONS_PASS_07.md), [late game](./VIME_LATEGAME_VICTORY_MISSIONS_PASS_08.md), [culture/domination](./VIME_CULTURE_DIPLOMACY_DOMINATION_PASS_09.md) | Four original condition classes and six later wiki routes, seven space mission totals | Partial version-specific wiki, mission component matrix missing |
| [World/session decisions](../04-architecture/WORLD_SIZE_PRESETS.md), [modes](../02-game-design/WORLD_SESSION_MODES.md) | D-013 sizes 10k/15k/25k; D-014 finite victory or endless sandbox | APPROVED CONCEPT; technical defaults and details not yet frozen |

## G0-A progress update (2026-11-18)

- [STRUCTURE_PARITY_27_VS_VIME.md](./STRUCTURE_PARITY_27_VS_VIME.md): all **27** old configured structure IDs now have an evidence-graded later wiki name mapping or explicit unmatched classification; this closes only the **identity mapping subtask** of GAP-001, not detailed behavioral parity. Of 73 later index entries, 51 lack an *unambiguous exact-name* old counterpart; many are genuinely new, but some may be renamed successors. Do not call them 51 confirmed new structures.
- [VIME_TECH_GRAPH_RECOVERY_01.md](./VIME_TECH_GRAPH_RECOVERY_01.md): direct technology-to-building unlock and civic prerequisite examples collected. The late tree is embedded as a raster, not a readable list of technology-to-technology prerequisites; full graph reconstruction remains blocked until image acquisition and verification. Its wiki page indicates a **2024-09-03** last edit (source tree version 1.9.2), so 2022 wiki-page dating must not be applied globally.

## 2. Priority-ranked open research backlog

**P0 = blocks G0 closure and core GDD dependencies; P1 = must be resolved before the related gameplay scope freezes; P2 = deliberate later-detail audit with explicitly tracked limits.** These are *audit priority levels*, not numbered release patches.

| ID | Priority | Topic | Evidence we currently have | Missing deliverable | Recommended next action |
|---|---|---|---|---|---|
| GAP-001 | P0 | Cross-version feature-parity registry | 27 old structures, 73 wiki navigation positions, 13 broad comparisons | One **per-feature** row: old ID, original code, later wiki page/revision, applicability, confidence, conflict, target KEEP/ADAPT/EXCLUDE, proposed release, acceptance test | Build master parity sheet; no automatic approval |
| GAP-002 | P0 | Full late-era technology graph | Old 57-tech graph; later wiki tech tree as image/navigation | Later IDs/names, exact AND prerequisites, costs, era gating, world-wonder science tech dependencies, missing/cyclic detection | Transcribe source graph with verification; record unreadable edges as UNKNOWN |
| GAP-003 | P0 | NPC/AI vs legacy membership | Old Resident is mostly player membership; new NPC is Reborn concept | Domain constraints: identity, household, food, jobs, culture, housing, reproduction policy, loaded/virtual transition | Draft design *questions*, not pretend it existed historically; subsequent GDD work |
| GAP-004 | P0 | Worldgen/placement + fixed borders | D-013 world sizes, trade-point spacing config, town/culture docs | Seed/biome, starting location distribution, minimum distance, biome viability, resource equality, valid founding under 10k map, Nether/End edge cases | Verify source worldgen constraints and build map feasibility checklist |
| GAP-005 | P0 | IP, dependencies and migration risk | Root GPL-2.0 and some Java copyright notices, schematic and old Bukkit platform | File-by-file license rights, third-party assets and dependencies, transplant vs fresh implementation decision, legal review if copying | Audit ownership; no code/assets copied until resolved |
| GAP-006 | P0 | Persistence / offline time / world simulation | Old SQL/async queue audited; world bounds/modes documented | Saved schema map, authoritative calendar, timers, LOD reconciliation, crash/rollback behavior, cross-dimensional unload, update budgets | Source trace + system contract; defer implementation until design freeze |
| GAP-007 | P1 | Old 27 building effect completeness | Class mappings tentative, configuration costs | All effects, upkeeps, unlocks, upgrades, templates, footprints, limits, construction and permissions per row; four classes unresolved | Focused Java/YAML pass |
| GAP-008 | P1 | Late 73 building and unit entries | Categorized wiki navigation | Per-article footprint, tech, item inputs, effect, limits, owner restrictions, war outcomes, release eligibility | Individual article card audit with provenance |
| GAP-009 | P1 | Wonders | Old 7 IDs, later 22 world + 6 national link lists | Per-wonder upkeep, tech, effect, construction/uniqueness, destruction rules, nation bonus, world-scale prerequisites | Article-level wonder matrix; separate normal/national kinds |
| GAP-010 | P1 | Governments/nations/religions/talents | Seven old government IDs, nine modes later, seven nations and seven religion options | Comprehensive modifier precedence, unlock/transition requirements, upgrade-specific exclusive nation buildings, full talent choices | Define source-versioned catalogs and conflicts |
| GAP-011 | P1 | Agriculture/logistics | Selected farm, warehouse, mine, granary, sawmill, quarry, beekeeping articles | Remaining school/university/factory, apiary/pasture/hive, output recipes, random drop multi-rolls, timer/offline semantics | Continue article audits; avoid invented percentages |
| GAP-012 | P1 | Warfare/permissions/diplomacy | Code traces of capture, rebellion, 5 relations, war state and tax flows | Command actor rights, complete war/occupation state machine, war damage recoverability, treaty expiry, war schedule suitable for singleplayer, full owner transfer effects | Write independent source-traced state-transition matrix |
| GAP-013 | P1 | Victory / late game | Old 4 source conditions, later 6 wiki conditions, 7 space missions | Space recipe component-by-mission matrix, reliable exact historic versions, win clocks, NPC voting, concurrent winners and zero rival cases | Complete evidence then present GDD options; preserve D-014 |
| GAP-014 | P1 | Espionage/special units | YAML 7 spy missions + 2 units | Mission execution path, catches/escapes, permissions, buff impacts, unit persistence, NPC equivalent | Java and later wiki cross-audit |
| GAP-015 | P1 | Wonders/events/exploration | Random events 5 IDs; later world events/ruins/provinces introduced | Province ownership, event rate/magnitude/stacks, ruins/dungeons/artifacts, worldgen distribution | Targeted sources with implementation gap tags |
| GAP-016 | P2 | UI, commands, map integration and admin policy | Found command paths and Dynmap module | Screen/overlay design, admin-only mechanics and world generation visibility under Minecraft/Fabric; dedicated-server admin rules | GDD/architecture UX and admin audit |
| GAP-017 | P2 | Balance and progression | Historical prices and 9 town upgrade levels | Coherent version-specific balance, NPC affordability, rates under 3 map sizes, game duration and economy stabilization | Design-era calibration/tests after catalogs |
| GAP-018 | P2 | Original submodules and incidental server features | `civcraft_dynmap`, `civregister`, `moblib`, arena/perks/anticheat identified | Decide core parity vs excluded server-only systems; third-party dependencies | Explicit KEEP/ADAPT/EXCLUDE review |

## 3. Notable contradictions requiring explicit resolution

| ID | Mismatch | Sources / implication |
|---|---|---|
| WIKI-001 | NPP max 1 in card vs 2 per civilization in text | [Building effects](./VIME_BUILDING_EFFECTS_PASS_01.md): cannot choose a cap automatically |
| WIKI-CONFLICT-002 | Arsenal upgrade heading 1–4 vs table 1–5 | [Production and logistics](./VIME_PRODUCTION_SCIENCE_LOGISTICS_PASS_02.md) |
| WAR-CONFLICT-001 | Old liberation of capital may restore multiple towns; later wiki describes separate return | [Diplomacy pass](./DIPLOMACY_WAR_CONSEQUENCES_PASS_06.md) |
| GOV-001 | Government transition anarchy fixed 24h older vs player-count later | [Taxation pass](./TAXATION_REVOLUTION_GOVERNANCE_PASS_05.md) |
| VERSION-RLG-001..004 | Religion count, effects, rank formula, transition time differ by version | [Late victory pass](./VIME_LATEGAME_VICTORY_MISSIONS_PASS_08.md) |
| VERSION-WIN-001..003 | Culture threshold, Council hold time, Council eligible city type change across versions | [Culture victory pass](./VIME_CULTURE_DIPLOMACY_DOMINATION_PASS_09.md) |
| PARITY-UNKNOWN-001 | Vassal in old war YAML but relation enum VASSAL commented out | [Diplomacy pass](./DIPLOMACY_WAR_CONSEQUENCES_PASS_06.md), old code; not implementation evidence |
| POP-UNKNOWN-001 | Old resident count as human members; Reborn needs many physical NPC | [Social pass](./VIME_SOCIAL_HAPPINESS_GOVERNANCE_PASS_04.md); player-count penalties cannot be reused per NPC blindly |
| WORLD-UNKNOWN-001 | Finite-victory mode at new-world creation vs not-yet-implemented victory systems | [World session design](../02-game-design/WORLD_SESSION_MODES.md); avoid half-working P0 mode |
| MAP-UNKNOWN-001 | 10k/15k/25k border alone does not ensure feasible wonders, trade-resource sites or faction count | [World preset design](../04-architecture/WORLD_SIZE_PRESETS.md); map feasibility + LOD work needed |

## 4. What **not** to claim yet

- That all 73 later-building index entries have been behaviorally verified or uniquely mapped to legacy IDs.
- That the complete later research graph, all wonders or all nation-specific articles have been checked.
- That later VimeWorld rules are proved by the old Bukkit Java source.
- That physical, household-simulating NPCs are an original CivCraft feature.
- That the chosen world-size border is a measured performance improvement without benchmark evidence.
- That 1.0 includes all wiki features without enumerated retention decisions.
- That G0 is complete, G1 has started or a release can be promised.

## 5. G0 exit checklist (must be evidenced)

- [ ] Each **retained** old/YAML and later/wiki gameplay feature has a stable row ID, provenance, behavior evidence or explicit `UNKNOWN` with risk assessment, and comparable version identification.
- [ ] The older and later tech/building/wonder/government/religion catalogs have explicit per-item status, and all dependencies for P0/P1 gameplay are resolved.
- [ ] Cross-version conflicts recorded and assigned an owner decision or defensible design option; no unsignaled merging.
- [ ] Mandatory old-code execution paths audited for construction, government, research, war, economy, persistence, rights and world placement.
- [ ] Server-only/legacy plugin features have clear provisional KEEP/ADAPT/EXCLUDE proposals.
- [ ] External source/asset licensing risks recorded sufficiently to keep implementation clean-room if needed.
- [ ] World size and session-mode choices included as user-approved concepts D-013/D-014, while their *unspecified* details are explicitly open.
- [ ] A prioritized GDD question register is produced, describing choices rather than asserting them.
- [ ] User acknowledges G0 review before G1 is treated as approved; no gameplay code before G0–G4 completion.

**Important gate philosophy:** G0 is source audit, not full approval of all mechanical balances. Evidence gaps can be carried into G1 only with a named risk and deferred scope where they cannot invalidate core architecture; missing critical dependency behavior cannot silently carry forward.

## 6. Recommended execution order from this audit

1. **G0-A:** cross-version exhaustive catalog and later tech graph (GAP-001, GAP-002, GAP-007, GAP-008).
2. **G0-B:** foundational blockers for NPC in bounded worlds (GAP-003–006), including licensing and LOD/time.
3. **G0-C:** per-mechanic later content and conflicts (GAP-009–015).
4. **G0-D:** produce target parity choices, acceptance tests and GDD question register; user signoff, then enter G1.

**Current state: G0 OPEN; prioritized and made measurable, not closed. No gameplay code implemented.**

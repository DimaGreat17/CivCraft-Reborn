# CivCraft legacy Java audit — Phase 2: Civilization, territory, war and persistence

**Audit date:** 2026-11-18
**Upstream snapshot:** `netizen539/civcraft` commit `b64fe5b99da7c531fcdb5799217bf1aac2d760e0` (GitHub source search file links). Files below inspected via the GitHub connector.
**Status:** Code excerpts and method inventory verified; not a complete behavioral trace, integration run or performance benchmark.

## Source-backed mechanics

| ID | Mechanic | Direct code evidence | Legacy result | Reborn recommendation |
|---|---|---|---|---|
| CODE-001 | Civilization researches technologies | [Civilization.java#L1036](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1036) `startTechnologyResearch` checks current research, treasury, known tech and prerequisites | Civilization-scoped research requires funds and valid dependencies | Keep rules; independent ResearchService, data-driven technology defs |
| CODE-002 | Civilization upkeep | [Civilization.java#L815](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L815) `payUpkeep` iterates towns, computes non-capital town upkeep | Maintenance costs depend on state and towns | Keep economic pressure; extract upkeep calculator and ledger |
| CODE-003 | Government transition | [Civilization.java#L1077](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1077) `changeGovernment` prevents repeat and switch from anarchy; checks buff | Government change is rule-checked | Keep transition as explicit state machine |
| CODE-004 | Defeat and sovereignty | [Civilization.java#L1245](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1245) `onDefeat` transfers town defeat handling and relations | Defeat affects city ownership and diplomacy | Keep semantics where approved; explicit capture transaction and events |
| CODE-005 | Score | [Civilization.java#L1419](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1419) `getScore` sums town score + tech score | State score combines town and tech scores | Preserve and extend only by approved rules |
| CODE-006 | Plots as saleable ownership | [TownChunk.java#L463](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/TownChunk.java#L463) `purchase`: checks balance, pays town or current owner, transfers ownership | Plot transactions and private property | Keep with atomic ledger and permission revalidation |
| CODE-007 | Diplomatic relations | [DiplomacyManager.java#L123](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/DiplomacyManager.java#L123) `setRelation`, [#L232](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/DiplomacyManager.java#L232) `isHostileWith` tests WAR/HOSTILE statuses | State-based diplomatic relationships | Keep with policy and expiration service |
| CODE-008 | Biome culture modifiers | [CultureChunk.java#L133](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/CultureChunk.java#L133) `getHappiness/getHammers/getGrowth/getBeakers` use biome plus attribute modifiers | Biomes directly affect multiple city outputs | Keep; test geographic bonus stacking |
| CODE-009 | Culture border calculation | [CultureProcessAsyncTask.java#L45](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/threading/tasks/CultureProcessAsyncTask.java#L45) BFS expansion; [#L222](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/threading/tasks/CultureProcessAsyncTask.java#L222) iterates towns and recalculates overlapping culture | Culture overlay is recalculated across towns | Use incremental region updates and immutable snapshots |
| CODE-010 | War-time block interception | [WarListener.java#L28](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/war/WarListener.java#L28) block break and placement; explosion hook | Combat modifies protection/edit rules at war-time | Keep permissions and rules, server-authoritative validation |
| CODE-011 | War rollback | [WarRegen.java#L258](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/war/WarRegen.java#L258) restores blocks from town war-log file | War damage restoration on reload/end | Rebuild versioned, idempotent world-edit journal |
| CODE-012 | Saved containers during wartime | [WarRegen.java#L191](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/war/WarRegen.java#L191) records block and clears container inventories during destruction flow | Inventory/world consistency is tied to war rollback | Audit save and restore comprehensively before specification |
| CODE-013 | Database update worker | [SQLUpdate.java#L47](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/database/SQLUpdate.java#L47) perpetual while loop, queue `poll()` and immediate `continue` on empty | Queue worker uses busy polling on empty queue | Do not port; blocking queue or bounded flush scheduler |
| CODE-014 | Runtime config loader | [CivSettings.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/config/CivSettings.java) `loadConfigFiles/loadConfigObjects`, lookup helpers | YAML config loaded into legacy settings APIs | Typed/validated definitions with version-aware data loading |

## Coupling and runtime hazards

1. **Monolithic domain objects:** `Civilization.java` (about 1,860 lines), `Town.java` (about 3,171) combine state, policy, payment, SQL persistence and services. Domain/application/storage must be separated.
2. **Busy-spin candidate verified:** SQLUpdate loops indefinitely and immediately retries if queue polling returns null, with no observed wait at the empty-queue branch. This unnecessarily consumes a thread when idle. Whether the deployed plugin suppresses the effect via other lifecycle management has not been measured.
3. **Cross-service globals:** `CivGlobal` used by town, diplomacy, culture, war and related classes; new state must be scoped to the server world/save and injected through services.
4. **World-edit recovery:** WarRegen serializes old-style block state into text files. Modern containers/block entities require transaction-aware snapshots and recovery; do not adapt old numeric block IDs directly.
5. **Culture update complexity:** BFS across towns and neighbor conflict processing can become expensive with many states. Keep a region dirty-set and amortized per-tick budgets; profile before assuming best solution.
6. **Server threading:** Old code's asynchronous culture task accesses global registries. Audit every API call against current Minecraft thread constraints; immutable snapshots off-thread, actual edits on server thread.
7. **Java-version incompatibilities:** Original Bukkit APIs, IDs, entity events and persistence are not valid targets for a direct modern Fabric translation.

## Necessary invariants and acceptance tests

- `research`: exactly one active project per civilization; only valid prerequisites; coins deducted once.
- `treasury`: debits/credits balanced, including failures, resale and restarts.
- `plot ownership`: a chunk has one recognized owner; purchase cannot spend twice under concurrent requests.
- `culture`: overlapping influence resolved deterministically, independent of iteration order.
- `diplomacy`: both participants observe one coherent relation state; expiration and war transitions consistent.
- `war`: war-time checks authoritative; rollback idempotent and container-safe even after crashes.
- `queue`: no busy-wait on empty storage queue; bounded backpressure and shutdown flush.
- `simulation`: neither off-thread access to mutable game world nor forced chunk loading in virtual mode.

## Open verification tasks

- Complete `Civilization.payUpkeep`, `TownChunk.claim` and `DiplomacyManager.setRelation` code paths including all corner cases.
- Audit war block log format, crash atomicity, protection and post-war compensation.
- Map every config structure/tech/mission to its handlers; check disabled or dead code.
- Inspect initialization, scheduler timers, SQL schema and plugin adapters.
- Establish attribution/license clearance before any source code copying.
- Compare actual source version with specific historical CivCraft server builds.

**G0 remains open.** This report is a partial code-based audit; implementation behavior has not been validated on a running legacy server.

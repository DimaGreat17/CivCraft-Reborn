# CivCraft generations — source-backed comparison matrix, pass 1

**Audit date:** 2026-11-18
**Status:** IN PROGRESS; **no design approvals made in this document**.

## Evidence legend

- **OLD-CODE** — seen in Java/config of [netizen539/civcraft](https://github.com/netizen539/civcraft), reference commit `b64fe5b99da7c531fcdb5799217bf1aac2d760e0` where indexed.
- **LATER-WIKI** — documented on [VimeWorld CivCraft Wiki](https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница), whose pages are historical and do not prove live 2026 behavior or source implementation.
- **PROPOSED** — design direction for Reborn, pending GDD approval.
- **UNKNOWN** — not yet supported by enough evidence.

## Cross-generation findings

| ID | System | Old CivCraft (code evidence) | VimeWorld (wiki evidence) | Proposed Reborn requirement | Classification |
|---|---|---|---|---|---|
| CMP-01 | City creation | Town/Civilization models and settler unit configured; exact formation trace remains | [Cities](https://civ-wiki.vimeworld.com/wiki/Города_и_улучшения): settler trained at barracks; first settler 25,000 coins, later +25,000 per settler; approval by civ leader/advisor | Cost curves and founding authority in rule definitions; human and NPC actors use same use case | SHARED + LATER RULE |
| CMP-02 | Terrain bonuses | [CultureChunk.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/CultureChunk.java) biome modifiers for happiness, hammers, growth, science | [Cities](https://civ-wiki.vimeworld.com/wiki/Города_и_улучшения) describes surveying biome and building on suitable ground | Pure spatial scoring based on existing world geometry and biome | SHARED |
| CMP-03 | City levels | `town.yml` contains configured level upgrades | [Upgrades](https://civ-wiki.vimeworld.com/wiki/Дерево_улучшений) 9 levels, claim slots, building slots, maintenance, tech and coin/hammer costs | Validate data-driven level definitions, no hard-coded limits | SHARED + REBALANCED |
| CMP-04 | Research prerequisites | [ConfigTech.isAvailable](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/config/ConfigTech.java#L91) checks all colon-separated prerequisites | [Wiki tech tree](https://civ-wiki.vimeworld.com/wiki/Дерево_технологий) lists later era progression | Independently version all tech/era trees; avoid conflating old 57-tech graph with later progression | SHARED + LATER EXPANSION |
| CMP-05 | Infrastructure adjacency | No matching specific adjacency mechanic confirmed in inspected old Java; needs targeted source search | [Infrastructure](https://civ-wiki.vimeworld.com/wiki/Инфраструктура): +5% per eligible same-town building within horizontal 100-block distance, +60% cottage max and +35% mine max, calculated every 30 seconds | Dependency/dirty-region spatial bonus with stable cache | LATER-WIKI; OLD UNKNOWN |
| CMP-06 | Nations | `governments.yml` and government logic found; national identity independent of government not confirmed | [Nations](https://civ-wiki.vimeworld.com/wiki/Нации): one-time national choice, exclusive effects/structures and wonder synergies | Keep nationality as independent durable identity, not government enum | LATER-WIKI; OLD UNKNOWN |
| CMP-07 | Talents | Not identified in old configuration catalog | [Talents](https://civ-wiki.vimeworld.com/wiki/Таланты_цивилизации): one choice of three at each capital culture level; skipped choices are lost | Data-driven event-triggered choices; use deterministic AI selection | LATER-WIKI |
| CMP-08 | Plot ownership | [TownChunk.purchase](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/TownChunk.java#L463) transfers coins and plot ownership | [Cities](https://civ-wiki.vimeworld.com/wiki/Города_и_улучшения) describes city plot administration and rights | Unified plot rights and transactional ownership regardless of resident type | SHARED |
| CMP-09 | War and capture | [War.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/war/War.java), [TownHall.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/TownHall.java) implement WarTime and control points | [War](https://civ-wiki.vimeworld.com/wiki/Война): 6 normal city / 8 capital control points; capitol defeat captures all towns | Model objective, siege, owner transfer, diplomacy as independent transitions | SHARED + LATER RULE |
| CMP-10 | Historical sovereignty | [Civilization.onDefeat](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1245) transfers native and conquered towns; `Town` has mother-civ field | [War](https://civ-wiki.vimeworld.com/wiki/Война) describes captured but not capitulated towns retaining former allegiance and possible restoration/revolution | Separate `currentOwnerId`, `originCivilizationId` and `occupationStatus` | SHARED with expanded semantics |
| CMP-11 | War damage restoration | [WarRegen.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/war/WarRegen.java) writes restoration log | [War](https://civ-wiki.vimeworld.com/wiki/Война) describes repair of destroyed non-wonder buildings at half cost; lost upgrades persist | Transactional world-edit journal and distinct destroyed/repaired states | SHARED + LATER RULE |
| CMP-12 | Government / religion | Old governments parsed in settings and transition methods | [Nations](https://civ-wiki.vimeworld.com/wiki/Нации) has national government affinities and distinctive religion for some nations | Independent policy, culture, religion and nation effect sources; stacking rules required | SHARED + LATER EXTENSIONS |
| CMP-13 | Victory/endgame | Score and wonders configured, completion logic not fully traced | [War](https://civ-wiki.vimeworld.com/wiki/Война) cites domination; [Space missions](https://civ-wiki.vimeworld.com/wiki/Космические_миссии) adds science-based track | Optional victory tracks in persistent endless sandbox; precise selection unapproved | OLD-PARTIAL + LATER-WIKI |

## Verified city-upgrade table (VimeWorld wiki only)

Historical balance values transcribed from https://civ-wiki.vimeworld.com/wiki/Дерево_улучшений . **Do not silently apply to Reborn.**

| Level | Local wiki name | Claim cap | Building slots | Upkeep | Coins to level | Hammers | Required tech as wiki names it |
|---|---|---:|---:|---:|---:|---:|---|
| 1 | Поселение | 32 | 4 | 500 | — | — | — |
| 2 | Село | 64 | 6 | 750 | 10000 | 600 | Монархия |
| 3 | Деревня | 80 | 8 | 1250 | 20000 | 2000 | Церковь |
| 4 | Большая Деревня | 128 | 12 | 2000 | 30000 | 3000 | Письменность |
| 5 | Городок | 256 | 15 | 3500 | 50000 | 3000 | Деньги |
| 6 | Город | 512 | 17 | 5500 | 125000 | 6000 | Алхимия |
| 7 | Большой Город | 1024 | 20 | 7000 | 250000 | 12000 | Машиностроение |
| 8 | Огромный Город | 2048 | 25 | 8000 | 500000 | 25000 | Научный метод |
| 9 | Метрополис | 4096 | 30 | 25000 | 1000000 | 50000 | Инновации |

## Key rules from later Wiki that change data architecture

### Occupation
From [War](https://civ-wiki.vimeworld.com/wiki/Война):
- 6 control points in a regular town, 8 in capital.
- Capital's capture results in capture of all cities of that civilization.
- Captured towns remain distinct from capitulated towns.
- A wiki-described seven-day auto-capitulation deadline exists.
- Liberation and revolution may restore former sovereignty, research and score.
- Server-specific war dates, command restrictions and economic penalties are historical wiki settings; **not approved** as Reborn defaults.

### Adjacency
From [Infrastructure](https://civ-wiki.vimeworld.com/wiki/Инфраструктура):
- Horizontal distance only, same town, <=100 blocks from center.
- Cottage categories: arena, library, world market, monument, museum, hotel, tavern, theater, mall, university, temple, school.
- Mine categories: nuclear power plant, factory/plant, quarry, blacksmith, gunpowder workshop, warehouse, manufactory.
- Every category-type of nearby qualifying building contributes +5%; wiki caps indicate distinct eligible types. Verify actual stacking and duplicates; no code available.
- Suggested implementation: event-driven spatial indexes and a cache invalidated by building construction, removal, ownership changes and upgrades.

### Nation and talent
From [Nations](https://civ-wiki.vimeworld.com/wiki/Нации) and [Talents](https://civ-wiki.vimeworld.com/wiki/Таланты_цивилизации):
- Nation selection once per civilization, exclusive properties.
- Every capital cultural level allows exactly one of three talent choices; missed selection is lost on next level.
- These require effect origin metadata and persisted one-time choices, not just a set of loose scalar bonuses.

## Priority design consequences (no implementation yet)

1. **Sovereignty first:** design `SettlementIdentity` vs `CurrentOwner` and capture/occupation/revolution domain events before building AI-war.
2. **Economy and research contracts:** tech, city level, cultural talents, nation effects, wonders and infrastructure all modify one shared stat calculation API. Data-driven, origin-tagged modifiers needed.
3. **Performance:** incremental spatial effects and region-level recalculation; no 30-second full-world scan when world scales to thousands of settlements.
4. **Version provenance:** never overwrite old-code facts with VimeWorld values in legacy documents; keep distinct catalogs and source tags.
5. **Release policy:** patch sequencing remains provisional until G0–G4 completed; future patch mechanics may be specified early but must not be implemented in current patch.

## Pending verification

- All late wiki building, wonder and unit entries individually; older-version matching and exact unlocks.
- Full VimeWorld technology graph, religion, province, culture and nation list.
- Confirm legacy `motherCiv` behavior across capture and secession with further Java source traces.
- Distinguish owner permissions from occupied status; decide how player and NPC roles behave.
- Resolve mismatch between generic settlement terms and precise legacy upgradable town levels.
- Gather selected revision IDs/history for crucial wiki pages (home oldid 5784; war oldid 5716 listed on page).

**G0 is open. This matrix is initial comparison evidence, not a final implementation contract.**

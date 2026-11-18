# VimeWorld Civ Wiki — later CivCraft rules and feature inventory

**Evidence date:** 2026-11-18  
**Primary entry:** https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница  
**Status:** WIKI-DOCUMENTED, NOT CODE-VERIFIED. This is a later documented VimeWorld server variant. Treat game mechanics as version-specific; do not assume direct equivalence to the old `netizen539/civcraft` Bukkit source. The visible front page says last edited 2022-04-22; individual wiki pages can reflect other historical revisions.

## Evidence policy
- `LEGACY_CODE`: evidenced in historical source code/config.
- `VIME_WIKI`: described in this wiki, implementation unavailable.
- `REBORN_PROPOSAL`: our proposed design addition, not original behavior.
- `APPROVED`: explicit user decision, never implied from wiki listing.

## Major later-version systems

| ID | System | Wiki evidence | New requirement / research question |
|---|---|---|---|
| VW-001 | Era-based development | [Home](https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница): prehistoric through information-era buildings and improvements | Research tiers, technology unlocks and era-dependent economic production must be versioned |
| VW-002 | Nations | [Нации](https://civ-wiki.vimeworld.com/wiki/Нации): national bonuses, unique government/wonders/buildings | Separate national identity from government, diplomacy and civilization ID |
| VW-003 | Culture talents | [Таланты](https://civ-wiki.vimeworld.com/wiki/Таланты_цивилизации): choose 1 of 3 at new capital culture level; skipped talent lost | Explicit one-time talent decisions, slots, eligibility, choice persistence |
| VW-004 | Infrastructure adjacency | [Инфраструктура](https://civ-wiki.vimeworld.com/wiki/Инфраструктура): each qualified structure within horizontal 100 blocks gives +5% to Cottage or Mine, counted within same town | Spatial proximity effect service and caching; do not constantly rescan all buildings |
| VW-005 | Territory/economy | [Постройка зданий](https://civ-wiki.vimeworld.com/wiki/Постройка_зданий): claimed chunk price depends on existing claims; city officials can build | Permission matrix and tiered land pricing |
| VW-006 | City upgrades | [Дерево улучшений](https://civ-wiki.vimeworld.com/wiki/Дерево_улучшений): 9 settlement levels, claim limits 32 → 4096, building slots 4 → 30, upkeep and technology requirements | Data-driven city progression with claim, slot, upkeep caps |
| VW-007 | War/conquest | [Война](https://civ-wiki.vimeworld.com/wiki/Война): city 6 control blocks, capital 8; capture capital defeats civ; world restoration/repair; liberation/revolution, diplomacy | War/capture/occupation/independence requires explicit state machines |
| VW-008 | Global events | [Мировые события](https://civ-wiki.vimeworld.com/wiki/Мировые_события): weekly random events; global modifiers to economics, science, war, etc. | Policy effects engine: duration, stacking, deterministic start/end |
| VW-009 | National wonders | [Национальные чудеса](https://civ-wiki.vimeworld.com/wiki/Национальные_чудеса): building requirements in all uncaptured home cities, passive effects, do not consume wonder slot | Aggregate cross-city prerequisites and effect invalidation |
| VW-010 | Space missions | [Космические миссии](https://civ-wiki.vimeworld.com/wiki/Космические_миссии): Rocket/Space Shuttle components, science+hammers across civ, mission progression, loss on defeat | Endgame requires multi-stage projects and cross-city production contracts |
| VW-011 | Exploration content | [Руины](https://civ-wiki.vimeworld.com/wiki/Руины): ruin generation, rewards, scrolls, artifacts | World-generation exploration loot system; table-driven drops |
| VW-012 | Province model | [Провинция](https://civ-wiki.vimeworld.com/wiki/Провинция) | Separate article found; detailed rule audit pending |
| VW-013 | Modern content | [Home](https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница): beekeeping, bosses, dungeons, weapons, new structures, defensive air/ship units | Must inventory individually before scope decision |
| VW-014 | Religion | [Home](https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница) religion and special nations | Audit faith systems and relation to temples/religious bonuses |
| VW-015 | Victory | [Home](https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница), [Война](https://civ-wiki.vimeworld.com/wiki/Война), [Космические миссии](https://civ-wiki.vimeworld.com/wiki/Космические_миссии) | Audit different winning conditions and compatibility with endless sandbox |

## Source details with important consequences

### Infrastructure
The wiki describes +5% per eligible infrastructure building within **100 horizontal blocks** between building centers, within the **same town**. It gives possible totals **+60% for Cottage** and **+35% for Mine**, with distance recalculated every **30 seconds** in that server version. In Reborn: design spatial dirty-region invalidation and scheduled recompute, rather than replicating a full scan.

### City levels
The wiki table lists 9 levels with claim caps `32, 64, 80, 128, 256, 512, 1024, 2048, 4096` and structure slot caps `4, 6, 8, 12, 15, 17, 20, 25, 30`. These are **wiki historical balance values**, not approved Reborn gameplay numbers.

### War
Wiki states 6 Town Hall control blocks versus 8 Capitol control blocks. Destroying all Capitol points captures all civilization towns. Defeated civilization can retain underlying national identity to be restored by revolution, and owner may liberate not-yet-capitulated towns. War has server-specific declaration windows and aggressive upkeep penalties; must not reuse real-life schedule blindly in single player.

### Space
Space Mission progress uses science from across the civilization and hammers from uncaptured native towns; defeat removes last mission and current progress. This is a documented **later-server endgame**, not evidence that older source supports it.

### Nations / culture talent
Nation selection and governments are different systems. Talents chosen upon capital culture increases are separate from ordinary tech research. Model independent modifiers with explicit effect source, priority, stacking and lifecycle.

## Reborn compatibility matrix (initial recommendations; NOT approved game design)

| Later wiki mechanic | Candidate treatment |
|---|---|
| Nations | ADAPT — culturally distinct AI rivals without forced character class |
| City talents | KEEP / ADAPT — strategic cultural differentiation |
| Infrastructure | KEEP / ADAPT — city placement becomes meaningful |
| Late eras | REVIEW — preserve history/branching but decide whether technological endpoint fits Minecraft experience |
| Space missions | REVIEW — optional victory track, not required for free sandbox |
| Global events | KEEP / ADAPT — deterministic server configuration |
| Exploration ruins | ADAPT — distribute rare resources and lore |
| War schedules | REPLACE for single player; configurable multiplayer protection |
| National wonders | KEEP / ADAPT — expensive multi-city strategic investment |
| Boss/dungeon/beekeeping/ship systems | REVIEW individually for design cohesion and performance |

## What cannot yet be claimed
- No access to the later VimeWorld implementation source; wiki-described effects have not been independently code-tested.
- The wiki is historical. It cannot be used alone to assert what the current live VimeWorld server runs in 2026.
- Feature descriptions may be incomplete/outdated, have typos or require operational caveats.
- No decision to implement all of the later wiki features has been approved.

## Required follow-up tasks
1. Create detailed page-level catalog for buildings/improvements including cost, upgrades, era, effects.
2. Inventory complete nation list, exclusive mechanics and governments.
3. Extract tech tree by era from text/source (not solely image), preserving dependency constraints.
4. Catalog world events and endgame tracks, victory rules and options.
5. Compare each Vime wiki mechanic to legacy code, label `OLD`, `LATER`, `SHARED`, `UNKNOWN`.
6. Present conflicts to owner for design decision before freezing Reborn GDD.

## Detailed follow-up registers (2026-11-18)

- [Vime buildings and improvements by era](./VIME_BUILDINGS_BY_ERA.md): 73 navigation entries in era/military categories (not validated structure IDs), plus Town Hall and Capitol tracked separately.
- [Nations, religions and wonders](./VIME_NATIONS_RELIGIONS_WONDERS.md): 7 listed nations, 7 religion choices, 22 world-wonder links and 6 national-wonder links. This is a categorized wiki inventory; detailed effect/price audit remains open.
- Technology tree is shown as an image on the wiki. It must not be treated as a validated machine-readable full graph without explicit transcription and review.

**Scope caution:** Some 2021–2022 update notes modify effects and prices. For each individual mechanic use the specific revision of the relevant wiki page or patch notes; an older page and later patch note may not match.

- [Detailed building effects: pass 01](./VIME_BUILDING_EFFECTS_PASS_01.md): Factory, NPP, Laboratory, World Market, National College, Hermitage, East India Company and World Bank. Includes costs, formulas, requirement checks and explicit NPP limit inconsistency.

- [Production, science and logistics: pass 02](./VIME_PRODUCTION_SCIENCE_LOGISTICS_PASS_02.md): warehouse→mines, granary→cottages, sawmill/tool wear, construction work points, science, road travel and arsenal; source uncertainties and upgrade inconsistencies retained.

- [Agriculture, extraction and food: pass 03](./VIME_AGRICULTURE_EXTRACTION_PASS_03.md): farm crops, fish farm, quarry, plantation, resource mine, granary, fish boat, stoneworks, silkworm and beekeeping, plus input/output invariants and remaining evidence gaps.

- [Happiness, governance and social systems: pass 04](./VIME_SOCIAL_HAPPINESS_GOVERNANCE_PASS_04.md): player resident records vs physical NPCs, happiness tiers, civic plot permissions, 9 later wiki government modes, culture influence and world-size implications.

- [Taxation, governance and revolution: pass 05](./TAXATION_REVOLUTION_GOVERNANCE_PASS_05.md): legacy Java trace of taxable deposits, science allocation, daily upkeep and revolution; VimeWorld occupation rights, tax commands and government timing differences.

- [Diplomacy, war and sovereignty: pass 06](./DIPLOMACY_WAR_CONSEQUENCES_PASS_06.md): five source-confirmed diplomacy states, peace/treaty requests, war windows, casual-mode distinction, conquest and liberation; **WAR-CONFLICT-001** records old-code vs later-wiki capital liberation divergence.

- [Victory conditions: pass 07](./VICTORY_CONDITIONS_PASS_07.md): four old-code win conditions plus VimeWorld economic/religious/endgame changes; verified conquest fraction counts **civilizations**, not percentage of physical map area; D-014 finite/endless comparison.

- [Late victory and space mission detailed audit: pass 08](./VIME_LATEGAME_VICTORY_MISSIONS_PASS_08.md): economic/World Bank, religious/Notre-Dame and science/7 shuttle missions; per-version religion conflicts and D-014 session-mode implications.

- [Culture, diplomacy, domination victory: pass 09](./VIME_CULTURE_DIPLOMACY_DOMINATION_PASS_09.md): source code vs late wiki, Burj Al Arab, Council of Eight removal of votes, domination score ×9 and six wonders; version conflicts and NPC eligibility.

- [Individual building source retrieval pass 14](./G0_BUILDING_SOURCE_RETRIEVAL_AND_CARDS_PASS_14.md): 42/73 fetched article sources, 31 failed, and rate/placement/production special cases.
- [Comprehensive G0 review (2026-11-18)](./G0_COMPREHENSIVE_AUDIT_REVIEW_2026-11-18.md): status, evidence counts and explicit blocking omissions.
- [28 wonder construction catalog](./G0_WONDER_CONSTRUCTION_MATRIX_PASS_11.md), [73 building coverage ledger](./G0_73_BUILDING_COVERAGE_LEDGER.md), [province/spy/world events/ruins](./G0_WORLD_SYSTEMS_LATE_PASS_12.md).

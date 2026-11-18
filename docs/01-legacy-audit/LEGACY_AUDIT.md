# CivCraft Legacy Audit — Phase 1: Source and configuration reconnaissance

**Date:** 2026-11-18  
**State:** IN PROGRESS — initial config inventory completed, source-code behavioral audit pending  
**Upstream:** [netizen539/civcraft](https://github.com/netizen539/civcraft), default branch `master`  
**Config inventory:** [CONFIG_INVENTORY.md](./CONFIG_INVENTORY.md)

## 1. Provenance and scope

The upstream README describes an old Bukkit server-side civilization modification, with player-formed camps, towns, civilizations, research, diplomacy, wonders, and wars. The repository also contains multi-server ban/perk components not necessarily related to gameplay.

Root modules visibly include:
- `civcraft/` — main Bukkit project, with `src/`, `data/`, `bin/` and bundled JAR;
- `civcraft_data/` — gameplay configuration, templates, arenas and packaged server JAR;
- `civcraft_dynmap/` — map integration;
- `civregister/` — related registration/infrastructure module;
- `moblib/` — supporting module.

Repository LICENSE is listed as GPL-2.0. Before copying code or bundled assets, review all relevant licenses, author credits and potential third-party dependencies. Do not assume game design descriptions are subject to the same licensing treatment as source code.

Source: https://github.com/netizen539/civcraft .

## 2. Configuration-based findings

All findings below have evidence in the upstream `civcraft_data/data/*.yml` configuration. **CONFIG-VERIFIED is not the same as IMPLEMENTATION-VERIFIED.**

| Legacy ID | Domain | Evidence | Observed / important mechanic | Preliminary Reborn treatment | State |
|---|---|---|---|---|---|
| LEG-001 | Camps | `camp.yml` | Camp HP, regeneration, firepoints/coal upkeep, raids, Sifter and Longhouse upgrades | ADAPT; connect to AI population economy | CONFIG-VERIFIED |
| LEG-002 | Town/civilization | `civ.yml`, `town.yml` | Starting coins, cooldowns, town level upgrades and town research | KEEP/ADAPT | CONFIG-VERIFIED |
| LEG-003 | Buildings/tiles | `structures.yml` | 27 named IDs: Town Hall, Capitol, Bank, Trommel, Store, Library, Grocer, Blacksmith, Granary, Cottage, Monument, Temple, Shipyard, Mine, Trade Outpost, Fishing Boat, Farm, Pasture, Wall, Road, towers, Barracks, Windmill, Market, Stable | KEEP feature categories; adapt underlying mechanics | CONFIG-VERIFIED |
| LEG-004 | Research | `techs.yml` | 57 tech IDs, research beaker costs, coin costs, prerequisite fields; tech-dependent item and potion access | KEEP technology progression, convert obsolete item IDs | CONFIG-VERIFIED |
| LEG-005 | Wonders | `wonders.yml` | 7 unique IDs, building templates, costs, requirements, points, health and regeneration | KEEP, reconcile world uniqueness rules | CONFIG-VERIFIED |
| LEG-006 | Government | `governments.yml` | 7 government IDs; modifiers for trade, cottage economy, upkeep, growth, taxes, culture, hammers and beakers | KEEP and implement AI policies | CONFIG-VERIFIED |
| LEG-007 | Culture/claims | `culture.yml` | Culture levels and biome effects/configured modifiers | KEEP, verify actual claim mechanics in Java | CONFIG-PARTIAL |
| LEG-008 | Trading/goods | `goods.yml`, `market.yml` | 28 trade goods with generation rules, trade multipliers, market commodities/prices | KEEP, integrate logistics and physical inventory | CONFIG-VERIFIED |
| LEG-009 | Happiness | `happiness.yml` | Seven named states from Revolt to Ecstatic; production/science/culture/coin multipliers | KEEP and connect to NPC loyalty | CONFIG-VERIFIED |
| LEG-010 | Espionage | `espionage.yml` | 7 spy mission IDs; exposure, surveillance, failure and consequences configuration | KEEP concept; AI-driven missions require design | CONFIG-VERIFIED |
| LEG-011 | War | `war.yml` | War schedule/cooldown, declaration windows, vassal duration, vassal tax, siege-related controls | KEEP core state machine; add autonomous armies | CONFIG-PARTIAL |
| LEG-012 | Special units | `units.yml` | Configured Spy and Settler; equipment speed/damage modifiers | KEEP / reinterpret for player and NPC | CONFIG-VERIFIED |
| LEG-013 | Random events | `randomevents.yml` | Five event IDs: Slime Plague, Truffles, Productivity, Herbs, Gold Rush | KEEP as event system with explicit rules | CONFIG-VERIFIED |
| LEG-014 | Score and victory | `score.yml`, `wonders.yml` | Score and wonder-point data present | Investigate win condition implementation | CONFIG-PARTIAL |
| LEG-015 | Arena, perks, anticheat | `arena.yml`, `perks.yml`, `nocheat.yml` | Legacy server-specific systems | DEFER decision until code and multiplayer design reviewed | IDENTIFIED |
| LEG-016 | Other gameplay data | `buffs.yml`, `enchantments.yml`, `fishing.yml`, `materials.yml` | Additional mechanical configs available | Audit fully before scope classification | IDENTIFIED |

## 3. Specific values observed — examples, not final balance

- `camp.yml`: `hitpoints: 5000`, `firepoints: 48`, `coal_per_firepoint: 4`, `cost: 2500.0`, `raid_length: 2`.
- `civ.yml`: `starting_coins: 250.0`, `join_civ_cooldown: 24`, `distance_from_spawn: 1000.0`.
- `war.yml`: `cooldown_time: 168`, `time_declare_days: 3`, `vassal_hours: 168`, `vassal_tax: 0.5`.
- `governments.yml`: government changes specify anarchy duration of 24 hours.
- `goods.yml`: generation bounds of 15–40 chunks between trade goods and separate land/water rules.

Values might be historical, disabled or overridden in other builds. Their semantics must be traced through the runtime parser before accepting them as game rules.

## 4. Major discoveries that change the design scope

1. **Happiness and forms of government** must be designed before completing population AI and policy systems.
2. **Spy missions** impact diplomacy, security, town structures and military AI.
3. **Camps have maintenance and raid rules**, not just a small pre-town footprint.
4. **Trade goods are geographically generated** and separate from generic Minecraft blocks.
5. **Random events and score/victory systems** require explicit treatment in the GDD.
6. The original configuration defines **special Spy/Settler units**; do not mistake those definitions for evidence of fully simulated civilian AI NPCs.
7. Some configuration comments explicitly mark particular multipliers as unused. Code inspection is required before claiming their behavior.

## 5. Evidence levels

- **IDENTIFIED**: source/configuration file exists, contents or implementation not exhaustively analyzed.
- **CONFIG-VERIFIED**: exact configuration key or ID observed.
- **CODE-VERIFIED**: controlling Java implementation inspected.
- **TEST-VERIFIED**: reproducible behavior established on a matching legacy runtime.
- **DESIGN-APPROVED**: preservation decision approved for Reborn.

No subsystem should be marked fully audited based only on its YAML file.

## 6. Outstanding source-code audit tasks

1. Inspect `civcraft/src/` Java packages for the configuration loaders, town and civ models, commands and permissions.
2. Cross-reference each building, technology, wonder and trade good ID with runtime classes and handlers.
3. Trace all war and capture paths and find actual relationship between `time_length`, `time_war_repeat` and published WarTime description.
4. Verify which `happiness.yml`, `governments.yml` and other values are actually used.
5. Audit templates, schematic placement, permissions and rollback.
6. Trace storage schema, save/load and server tick tasks.
7. Inspect `civcraft_dynmap`, `civregister` and `moblib` to decide portability.
8. Create feature-by-feature KEEP / ADAPT / REPLACE / EXCLUDE matrix after behavioral audit.
9. Record licensing implications of copying code/art.
10. Version-pin source commit and fork/release differences before considering a design freeze.

## 7. Source links

- https://github.com/netizen539/civcraft
- https://github.com/netizen539/civcraft/tree/master/civcraft_data/data
- https://dev.bukkit.org/projects/civcraft
- https://github.com/netizen539/civcraft/blob/master/LICENSE

**Gate G0 is NOT complete.** This document is Phase 1 reconnaissance only.

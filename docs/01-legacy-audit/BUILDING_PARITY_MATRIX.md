# Building Parity Matrix — CivCraft legacy, Phase 1

**Date:** 2026-11-18. **Coverage:** all 27 structure/tile improvement IDs in [structures.yml](https://github.com/netizen539/civcraft/blob/master/civcraft_data/data/structures.yml).  
**Evidence:** YAML costs/requirements, plus method inventories for named structure subclasses where available. **CODE-METHODS means source method existence, not validated gameplay output.**  
**Status:** SOURCE AUDIT IN PROGRESS. This matrix is not an approved Reborn replacement catalog.

## Source-backed structure register

| Legacy ID | Display name | Coins | Hammers | Required tech | Java evidence | Observed behavior or audit gap | Confidence |
|---|---|---:|---:|---|---|---|---|
| `s_townhall` | Town Hall | 10000 | 600 | `—` | [TownHall](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/TownHall.java) | control points, research bar/respawn | CODE-IDENTIFIED |
| `s_capitol` | Capitol | 50000 | 600 | `—` | [Capitol](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Capitol.java) | capital control point, sign interaction | CODE-IDENTIFIED |
| `s_bank` | Bank | 2250 | 1200 | `tech_religion` | [Bank](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Bank.java) | exchange, interest, fees | CODE-METHODS |
| `s_trommel` | Trommel | 5000 | 800 | `tech_mining` | [Trommel](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Trommel.java) | mineral chance settings | CODE-METHODS |
| `s_store` | Store | 2500 | 900 | `tech_commerce` | [Store](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Store.java) | requires code audit | CONFIG |
| `s_library` | Library | 5000 | 1500 | `tech_productivity` | [Library](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Library.java) | enchantments, item processing | CODE-METHODS |
| `s_grocer` | Grocer | 1500 | 900 | `tech_hunting` | [Grocer](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Grocer.java) | sell food/materials with level and fees | CODE-METHODS |
| `s_blacksmith` | Blacksmith | 32000 | 1500 | `tech_blacksmithing` | [Blacksmith](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Blacksmith.java) | forge, catalysts, smelting | CODE-METHODS |
| `s_granary` | Granary | 2500 | 500 | `tech_pottery` | [Granary](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Granary.java) | requires component-effect tracing | CODE-IDENTIFIED |
| `ti_cottage` | Cottage | 5000 | 1400 | `tech_advanced_masonry` | [Cottage](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Cottage.java) | food consumption and coin generation | CODE-METHODS |
| `s_monument` | Monument | 15000 | 2000 | `tech_religion` | [Monument](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Monument.java) | requires attribute-effect tracing | CODE-IDENTIFIED |
| `s_temple` | Temple | 30000 | 2000 | `tech_priesthood` | [Temple](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Temple.java) | entity sacrifice hook | CODE-METHODS |
| `s_shipyard` | Shipyard | 35000 | 2000 | `tech_sailing` | not yet mapped | requires class mapping | CONFIG |
| `ti_mine` | Mine | 25000 | 900 | `tech_productivity` | [Mine](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Mine.java) | consumption/production processing | CODE-METHODS |
| `ti_trade_outpost` | Trade Outpost | 25000 | 2000 | `tech_trade` | [TradeOutpost](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/TradeOutpost.java) | bonus trade-good handling | CODE-METHODS |
| `ti_fishing_boat` | Fishing Boat | 25000 | 2000 | `tech_trade` | [FishingBoat](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/FishingBoat.java) | trade outpost-style construction | CODE-METHODS |
| `ti_farm` | Farm | 2500 | 800 | `tech_agriculture` | [Farm](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Farm.java) | farm growth and regeneration | CODE-METHODS |
| `ti_pasture` | Pasture | 2500 | 1000 | `tech_hunting` | not yet mapped | requires class mapping | CONFIG |
| `ti_wall` | Wall | 50 | 300 | `tech_advanced_masonry` | not yet mapped | special per-segment tile; no generic schematic | CONFIG |
| `ti_road` | Road | 300 | 500 | `tech_masonry` | not yet mapped | special per-segment tile; no generic schematic | CONFIG |
| `s_arrowtower` | Arrow Tower | 50000 | 2500 | `tech_artillery` | [ArrowTower](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/ArrowTower.java) | projectile defensive stats | CODE-METHODS |
| `s_cannontower` | Cannon Tower | 250000 | 5000 | `tech_advanced_artillery` | [CannonTower](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/CannonTower.java) | cannon projectile component | CODE-IDENTIFIED |
| `s_scouttower` | Scout Tower | 15000 | 1500 | `tech_masonry` | [ScoutTower](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/ScoutTower.java) | scout/report periodic processing | CODE-METHODS |
| `s_barracks` | Barracks | 15000 | 1500 | `tech_armory` | [Barracks](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Barracks.java) | train units, repair equipment | CODE-METHODS |
| `ti_windmill` | Windmill | 25000 | 3000 | `tech_automation` | [Windmill](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Windmill.java) | effect event and processing | CODE-METHODS |
| `s_market` | Market | 10000 | 2600 | `tech_commerce` | [Market](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Market.java) | buy/sell signage and pricing | CODE-METHODS |
| `s_stable` | Stable | 5000 | 1200 | `tech_global_trade` | [Stable](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Stable.java) | services/item cost/fees | CODE-METHODS |

## Cross-system design requirements

1. **Building definitions:** preserve per-structure identity and cataloged currency, hammer and tech requirements until balance decisions are approved.
2. **Runtime effects:** verify every event handler, component, timer, inventory action and building effect before declaring a parity match.
3. **Construction:** model building template, footprint, placement rules, construction progress, repair, demolition and permissions separately from economics.
4. **Residents:** NPC labor, transport and personal interactions are proposed **Reborn additions**, not observed original behavior.
5. **Physical economy:** do not treat cottage/mine scripted outputs as evidence that the original included roaming worker NPCs.
6. **Special tiles:** wall/road use `template: notused` in YAML. Review custom per-segment implementation and cannot infer schematic behavior.
7. **Progression:** note Grocer, Library, Bank and other upgrade mechanics must cross-reference town.yml upgrades and actual Java handlers.
8. **Performance:** no per-building uncontrolled every-tick callbacks; prefer scheduled batched production and event-driven changes.
9. **Data:** validate IDs, prerequisites, versioned layouts, item references and effective costs at load time.
10. **Tests:** each verified building requires construction, resource payment, upkeep, effect, permission, unload/reload and corruption/recovery test cases.

## Completeness gaps

- The rows show the base configured cost and hammer cost only; upkeep, hitpoints, levels, limits, biome effects, item lists and other fields require a second extraction pass.
- `Shipyard`, `Pasture`, `Road`, `Wall` have not yet been mapped to exact gameplay class paths in this audit.
- `Granary`, `Monument`, `Store`, tower logic and the detailed `Temple` effect require more than method listing.
- Wonder structures are tracked separately from the 27 rows; see `wonders.yml`.
- No keep/adapt/exclude conclusion is final before the design review and applicable license clarification.

## Definition of complete

For each building: verified original runtime effect, all configuration fields, persistent state, event behavior, dependency map, proposed Reborn equivalent, NPC tasks (if any), acceptance cases, and explicit approval.

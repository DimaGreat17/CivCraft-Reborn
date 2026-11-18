# CivCraft legacy Java audit — Phase 3: buildings, research, capture, storage

**Date:** 2026-11-18
**Evidence level:** targeted Java source inspection, not runtime verification.
**Upstream:** [netizen539/civcraft](https://github.com/netizen539/civcraft), source-search commit `b64fe5b99da7c531fcdb5799217bf1aac2d760e0`.

## Findings with direct code locations

| Ref | System | Code evidence | Supported finding | Porting implication |
|---|---|---|---|---|
| SRC-301 | Research definitions | [ConfigTech.java lines 40–60](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/config/ConfigTech.java#L40-L60) | Loads technology ID/name, beaker cost, coin cost, requirement string and points from configuration | Typed, validated data catalog; dependency graph tests |
| SRC-302 | Research prerequisites | [ConfigTech.java lines 91–108](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/config/ConfigTech.java#L91-L108) | Colon-separated technology prerequisites must all be known; debug flag bypass exists | Preserve AND semantics, no debug bypass in production |
| SRC-303 | Research progression | [BeakerTimer.java line 40 onward](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/threading/timers/BeakerTimer.java#L40) | Iterates civilizations, requires valid capital/town, checks for Town Hall before beaker production | Civic infrastructure prerequisite; separate research calculator and scheduler |
| SRC-304 | Construction and previews | [Template.java lines 203–230](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/template/Template.java#L203-L230) | Scaffold/preview manipulates Minecraft blocks and sends player-only block previews | Geometry/preview adapters separate from immutable schematic definitions |
| SRC-305 | Construction undo | [Template.java lines 368 onward](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/template/Template.java#L368) | Writes block representations to undo template files | New versioned world edit journal, block-entity-aware rollback |
| SRC-306 | Town Hall capture | [TownHall.java lines 333–396](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/TownHall.java#L333-L396) | Damaged control points can be destroyed; handler checks whether all points are destroyed | Keep multiple capture objectives, state-machine-based outcomes |
| SRC-307 | War Camp capture | [WarCamp.java lines 475–524](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/camp/WarCamp.java#L475-L524) | Camp control points and war-camp destruction lifecycle with structural cleanup | Preserve distinct camp siege mechanics |
| SRC-308 | Commerce infrastructure | [TradeOutpost.java lines 262 onward](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/TradeOutpost.java#L262) | Initializes BonusGoodie for trade outpost, interacts with item frames and Town Hall | Trade-good logistics need unique ownership and physical/abstract consistency |
| SRC-309 | Persistence schema | [SQL.java lines 157–183](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/database/SQL.java#L157-L183) | Registers SQL tables for civilization, town, resident, relations, claims, buildings, wonders, goods, protected blocks, missions, events and camps | Design explicit versioned state stores and migration paths |
| SRC-310 | Persistence abstraction | [SQLObject.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/SQLObject.java) | Domain objects inherit persistence contract | Do not replicate storage inheritance in domain models |
| SRC-311 | Async persistence | [SQL.java lines 377–394](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/database/SQL.java#L377-L394) | Named object SQL update can be scheduled asynchronously, object-based insert/update logic | Immutable save snapshots, bounded queue and error handling |

## Legacy system model (source-derived, partial)

- **Research:** YAML tech entries → ConfigTech parsed objects → Civ research eligibility and payment → beaker timer using capital infrastructure.
- **Building:** structure definition + template geometry → preview and permission/validation → scaffold/build → bound blocks and possible undo/repair.
- **War:** WarTime and relation state gate attacks; control-point damage on Town Hall / War Camp changes siege state; block restoration exists as a separate system.
- **Persistence:** many named objects extend SQLObject, with per-object load/save and shared SQL utilities; a significant amount of state is coordinated with global registries.

These are architecture summaries, not proof of every transition or failure case.

## What to preserve versus redesign

| Subject | Preserve as game mechanics | Replace in implementation |
|---|---|---|
| Technology progression | Currency costs, science/beaker progression, explicit dependencies | Mutable maps with string-encoded dependency graph |
| Building | Schematic footprints, previews, costs, capture hitpoints, repairs | Direct block writes inside domain objects |
| War objectives | Control-point and War Camp mechanics | Mutating town ownership inside rendering/world handlers |
| Trade goods | Location-specific resources and Town Hall/outpost linkage | Ad hoc multi-object mutation of item frames + ledger |
| State saving | Persisted cities, owners, relations, claims, research and events | SQLObject inheritance, unmanaged async writes and unversioned snapshots |

## Checks before any design freeze

1. **Catalog completeness:** cross-reference every configured building with its concrete structure subclass, construction prerequisites and runtime effects.
2. **Technology graph:** extract all 57 IDs and colon-delimited dependencies, detect missing IDs/cycles and distinguish wonder techs.
3. **Capture semantics:** inspect remaining portions of TownHall, Capitol, Town, War and Relation for complete control-point → conquest → vassalage transitions.
4. **Storage:** map persisted fields and on-disk worlds/undo files; enumerate crash cases and migrations.
5. **Performance:** identify high-frequency timers and direct world access from async tasks; profile modern replacement rather than transplant old scheduling assumptions.
6. **IP and license:** separate verified factual behavior from reused source; unresolved rights notices mean no copying upstream code/assets yet.

## Follow-on proposed artifacts

- `BUILDING_PARITY_MATRIX.md`: each of the 27 building/improvement IDs, subclasses, effect, save form and tests.
- `TECHNOLOGY_GRAPH.md`: exact prerequisites, costs, invalid edges and era grouping.
- `CAPTURE_STATE_MACHINE.md`: war participants, windows, war camps, control points, occupation and peace.
- `PERSISTENCE_REQUIREMENTS.md`: versions, data ownership, transaction boundaries, recoverability.

**Gate G0: OPEN.** Concrete findings are now backed by Java references; further full-path inspection is still required.

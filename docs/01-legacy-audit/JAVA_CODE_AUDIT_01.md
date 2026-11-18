# Legacy Java audit — first structural pass

**Upstream examined:** [netizen539/civcraft](https://github.com/netizen539/civcraft), commit `b64fe5b99da7c531fcdb5799217bf1aac2d760e0` as exposed by GitHub file search.
**Evidence:** Selected classes inspected; this is **NOT** a complete flow or behavioral audit.

## Observed classes

| File | Approx. lines | Directly observed responsibilities | Reborn approach |
|---|---:|---|---|
| [Town.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java) | 3,171 | Model state, city membership/groups, upgrades, culture, SQL table creation/loading/saving, global registry access | Split model, application use-cases, repositories, permissions, culture |
| [Civilization.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java) | 1,860 | Large civilization class; full behavior audit remains | Isolate diplomacy, research, treasury and state identity after tracing methods |
| [CivGlobal.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/main/CivGlobal.java) | 2,027 | Global mutable system access, SQL imports, world-scope concerns | World-scoped registries and dependency injection |
| [Camp.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/camp/Camp.java) | 1,378 | Camp SQL, members, firepoints, inventory/furnaces, placement templates, block permissions | Separate camp state, resource upkeep, permission-checked world building |
| [Buildable.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Buildable.java) | 1,540 | Template geometry, validation, build progress, block binding/undo, building costs | Declarative building definitions + staged construction + world adapter |
| [Structure.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Structure.java) | 680 | SQL persistence, components, lifecycle, repair, loading | Building aggregate + strategy components + repository |
| [War.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/war/War.java) | 483 | Static war-state management, WarTime boundaries, defeated town/civ processing, session recovery | Domain war state machine, event-driven transitions, idempotent results |
| [CivCraft.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/main/CivCraft.java) | 353 | Bukkit JavaPlugin bootstrap, listener registration, scheduler startup | Thin Fabric initialization and modular service composition |

## Concrete code findings

1. `Town.init()` constructs SQL `TOWNS` schema and `Town.saveNow()` writes state within the same type that represents the town. This is a persistence/domain coupling.
2. `Town` directly resolves civilization through `CivGlobal.getCivFromId()`, confirming global service coupling.
3. `Camp.processFirepoints()` obtains real furnace inventories; camp economy already interacts with physical containers.
4. `Camp.buildCampFromTemplate()` iterates the template's x/y/z geometry and accesses game blocks. Build functionality is physical, not merely a claim record.
5. `Buildable` declares world-facing `build(Player, Location, Template)` and manages block binding / repair-related lifecycle.
6. `War.setWarTime(false)` triggers restoration and relocation actions; `War.processDefeated()` walks defeated town records.
7. `CivCraft.startTimers()` schedules SQLUpdate async and SyncBuildUpdateTask sync. Scheduling and persistence must be re-examined in modern server lifecycle.

## Architecture risks and decisions (design recommendations, not original behavior)

- Large, multipurpose classes increase regression risk and hinder isolated testing; **do not directly translate to Fabric 1:1**.
- Globals and static mutable game state must be replaced with explicit world-scoped ownership.
- SQL methods and physical block operations must be outside domain aggregates.
- Building and war state transitions must be idempotent under interruption/restart.
- Prefer data-driven definitions for technologies, building templates and costs, with separate execution systems.

## Licensing/rights blocker

The repository root `LICENSE` contains GPL version 2 text. However, inspected Java files (`Town.java`, `Camp.java`, `Civilization.java`, `CivGlobal.java`, etc.) start with AVRGAMING LLC 2013 'All Rights Reserved' proprietary/reproduction-restricted notices. **These are potentially conflicting signals.** Do not assume all code/art may be copied or relicensed by the root LICENSE alone. Clarify provenance and applicable permissions before copying code or assets. Reimplementation of documented game behaviors using original independently written code should be kept distinguishable from code copying. Consult qualified licensing advice if redistribution is planned.

## Next source-code audit targets
- `object/Civilization.java`: treasury, research, diplomatic permissions, score and government.
- `object/Town.java`: claims and culture growth; loading/saving upgrades and memberships.
- `war/WarListener.java`, `war/WarRegen.java`: capture, damage, city ownership and restitution.
- `config/CivSettings.java`: which YAML fields are parsed, defaults, disabled functionality.
- `structure/*`: building subclasses and their actual effects.
- `threading/*`: scheduler cadence, data hazards and world-thread ownership.
- `database/*`: schema, migration, update queue and crash recovery.
- `main/CivGlobal.java`: all hidden global dependencies.

This audit is a preliminary architectural risk map, not a complete diagnosis of performance or correctness of legacy CivCraft.

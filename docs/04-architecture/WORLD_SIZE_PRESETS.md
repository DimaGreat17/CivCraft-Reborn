# World Size Presets and Simulation Bounds — Design Draft

**Date:** 2026-11-18  
**Decision:** D-013: user-approved concept of an explicit world-size choice at new-world creation.  
**Status:** concept approved; implementation details/protection/other dimensions remain proposed until GDD and architecture freeze.

## World creation presets

| Preset ID | Overworld border width | Playable square area (blocks²) | Approx. chunks in full square |
|---|---:|---:|---:|
| `SMALL` | 10,000 blocks | 100,000,000 | 390,625 |
| `MEDIUM` | 15,000 blocks | 225,000,000 | ~878,906 |
| `LARGE` | 25,000 blocks | 625,000,000 | ~2,441,406 |

**Units:** `10,000 × 10,000` are horizontal Minecraft **blocks** X/Z, not chunks or kilometers. Presets represent the border square, not pre-generated or permanently loaded territory.

## User experience

- World-creation screen adds `CivCraft World Size` preset selector: **10k × 10k**, **15k × 15k**, **25k × 25k**.
- Choice is stored in world save with a versioned world configuration.
- Singleplayer and dedicated-server behavior need to match; dedicated server owner can set the preset before first creation through equivalent config.
- Use server-authoritative border; clients only display selection and boundaries.
- Proposed default: **15k × 15k** (not yet approved); user may choose any of the three.
- Prefer square border centered at world spawn or a configurable saved center. **Center is open design decision.** Vanilla world border can enforce the square, subject to mapping and version compatibility.
- Nether and End dimensions: **open design decision**; consider Overworld-only bound plus portal coordinate rules and per-dimension safety, rather than assuming identical square boundaries.
- Existing world selection/edit: changing size later needs confirmation and a migration/consistency strategy; shrinking after settlements exist outside the border is hazardous, so forbid uncontrolled shrink by default.

## Invariants

1. Settlement, culture, claim, autonomous expansion, trade destination, new structures, and strategic planning never select locations outside the currently authoritative boundary.
2. World generation, structure generation, navigation and teleport/portal behavior must respect the border policy where applicable.
3. Area limitation **does not** imply the game has loaded every chunk within it, and **does not itself cap** active NPCs, chunk-load budget, scheduled simulation, world size on disk, pathfinding work, or entity count.
4. No NPC or remote civilization force-loads chunks simply because they lie within the border.
5. Unloaded civilizations use bounded/aggregated simulation; loaded NPCs use explicit AI budgets and LOD transitions.
6. Border settings persist through restart, server reconnect, backup restore and version migration.
7. Production and economic output do not duplicate when AI states cross the loaded/virtual modes or approach borders.
8. Admin commands and direct vanilla border modification require a clear reconciliation policy; never silently desynchronize mod configuration and actual border.
9. If an existing settlement somehow lies outside the configured area, protect data and report actionable diagnostics instead of deleting it.

## Capacity design (PROPOSED)

World size should influence **recommended** caps on civilization density, independent AI update budget and settlement placement spacing, but there is no justified one-to-one formula or hardcoded NPC-count limit yet. Determine caps only after performance tests on representative hardware.

Suggested performance metrics: server MSPT/TPS, loaded entity count, pathfinding operations, simulation queue size/lag, database/save latency, disk usage, new chunk generation spikes, world creation time and multiplayer synchronization load.

## GDD acceptance cases

- New world selection for each preset survives save and restart; actual border width matches selected value.
- Border remains constant when loading a save; cannot place city structures or claim beyond it.
- New settlements generated near edges stay within border and avoid incomplete invalid footprints.
- Portals, teleports, resurrections, military movement and trade routes have defined edge behavior.
- Already-created worlds are never silently cropped or deleted.
- Singleplayer and dedicated server use the same simulation restrictions.
- Runs with no regions loaded besides active player area do not mass-generate border-wide chunks.
- Performance is profiled on all three presets with representative civilization populations.

## Dependency / patch placement

This feature belongs in **P0 (foundation)** because it influences world initialization, persistence, strategic planners, region indexing and settlement generation. It must be complete and tested before P1. The game code must not begin until G0–G4 are closed and approved.

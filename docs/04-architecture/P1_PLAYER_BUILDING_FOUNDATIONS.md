# P1 / 0.2 — Player building foundation and construction contract

**Decision date:** 2026-11-19
**Status:** ACCEPTED GAMEPLAY DESIGN; implementation not yet completed
**Supersedes:** loose core-only founding followed by unrestricted blueprint selection without a per-building physical anchor.
**Scope:** player-initiated construction (NPC-founded construction is a distinct later path).

## Canonical construction sequence

1. Player acquires/crafts a **structure-specific foundation item** for an unlocked building (initially Camp Heart).
2. Player places the real foundation block in a CivCraft-enabled world. This creates a server-side pending construction site linked to the foundation's BlockPos and a stable BuildingId. Placement does **not** spawn the finished prefab, grant resources, create residents, or activate the building.
3. The **complete 3D ghost schematic** opens, initially aligned with foundation. Before confirmation, player can translate the footprint by single blocks and rotate it in 90-degree increments using existing projection/placement UI. The foundation itself remains a real anchor and must stay within the approved footprint; the actual building remains anchored to it.
4. Server checks dimension, unlock tier, land/claims/permissions, world border, radius and collision before accepting final location/orientation. Client-side preview is non-authoritative. On confirmation create a persisted construction order (BuildingId, foundation pos, schematic key/version, transformed footprint and material bill). No physical blocks beyond the foundation are generated.
5. Player physically places ordinary Minecraft blocks following missing-position projection. Server tracks real-world matching, validates structural rules and accepts completion only if all required conditions hold. Client holograms are merely hints; no inventory or structure mutation on client.
6. After server-side completion the building transitions to ACTIVE and enables its real gameplay functions. Invalidated/removed required components suspend functionality and update diagnostics; foundation removal enters safe invalid/cancel recovery without duping resources.
7. On restart/chunk unload, the construction order persists with its identity and transform; UNKNOWN_UNLOADED must never imply completion. Multiuser edits and permissions remain server authoritative.

## Camp Heart as an actual building

- The Camp Heart is now a **complete physical building**, not an isolated Camp Core block with an unrelated four-obsidian cross.
- The Camp Heart foundation is the first available structure item. Placing it opens the full Camp Heart schematic with the same move/rotate/confirm/build pipeline.
- CivCraft founding/camp management activates **after the built Heart validates**. Do not allow `/civcraft camp found` to bypass construction requirements. The canonical building contains the interactive management block (its placed foundation or incorporated control point).
- The physical Heart must have a clearly documented real-block schematic, material bill, version and acceptance rules. Existing 4 obsidian per core placement and heart-cross assumptions are **legacy rules requiring review**; do not silently preserve the old cross as the entire building, and do not silently delete rewards without recording a migration/balance decision.

## Later buildings and stage unlocks

- Player progresses CAMP → SETTLEMENT using server-verified criteria. Catalog visibility follows server-owned progress, but a **physical foundation block is required for each new building**.
- Existing tiers are provisional: pre-camp HEART only; CAMP basic tier; SETTLEMENT advanced tier. Do not unlock high-tier foundations through client state.
- Every structure uses the identical construction protocol, with different schematic, footprint, workstations, activation conditions and foundation item identity.
- A building may be placed in an unfinished state but produces no functionality until activated. Server checks prevent using a pending foundation to bypass tier requirements.
- Before the first Camp Heart is finished, the player builds manually with real Minecraft blocks. Later builder-assisted construction combines **real resource requirements** with a separate **numeric hammer-progress requirement**; the exact way builder progress becomes a physical block structure is to be specified before code implementation. The no-free-resources principle remains binding.

## State and data model

`BuildingInstance` stores stable BuildingId, CampId (nullable for an unregistered Heart), foundation position and block type, owner, dimension, schematic id/version, confirmed origin/rotation, footprint, state `PLACED / POSITIONING / CONSTRUCTION / ACTIVE / INVALID / SUSPENDED_UNLOADED`, revision and validated timestamp. All transitions must be idempotent and persisted. Cancellation/refund behavior must never mint duplicate items.

### Explicit integration changes

- `CampCoreBlock`: no immediate registered Camp on placement; treat as Heart foundation/controller.
- `CampBlueprintProjection`, `BlueprintTransform`, `CivCraftPlacementScreen`: reuse UI, but bind position to a specific pending foundation + server-confirmed construction site.
- `BlueprintNetworking`: server validates request/confirmation and synchronizes durable BuildingId, stage, transformation and progress.
- `CampBuildingScanner`/housing validators: physical validation remains authoritative; freeform vanilla structures may exist, but they do not automatically become **registered special CivCraft buildings** without their appropriate foundation.
- `CampSavedData`: schema migration for existing worlds/old Camp Core and player housing; no destructive automatic conversion. Backups and an explicit legacy path are mandatory.

## Acceptance tests

1. Place Heart foundation: complete ghost appears; no finished structure materializes and no camp is founded.
2. Move ghost N/S/E/W and rotate 4×90°: transform persists after confirmation/reload; real foundation remains fixed.
3. Attempt unauthorized, overlapping, out-of-border or out-of-radius placement: server rejects.
4. Place matching blocks: progression increases, missing ghosts disappear; remove matching blocks: progression decreases.
5. Attempt `camp found` prematurely: rejected. Complete and validate building: camp control/founding becomes available.
6. Place STORAGE/WORKSHOP/FARM foundations after unlock: same pipeline and real functional building registration; advanced foundations blocked until promoted.
7. Wrong world / forged packets / wrong owner / missing foundation: no privilege or stage bypass.
8. Reload unfinished construction, unload chunk, restart server, switch clients: stable ID and geometry, no silent complete or duplication.
9. Remove required structural component from ACTIVE building: state invalidates and functional effects stop safely.
10. Verify hammer progress against independent material availability: 700 required / 400 per hour = 1 hour 45 minutes; no resource bypass or progress duplication.
11. Run real Minecraft 26.3 client visual acceptance: prior render-pass crash selecting STORAGE must not recur; CI dedicated smoke is not graphical evidence.

## Explicit non-goals

No automatic free prefab construction, no instant construction on placing foundation, no client-owned progress, no full NPC construction or civilizations in this slice. Later NPC building should reuse the same persisted construction orders and material transaction checks.

## Clarification: numeric hammers and builder-assisted completion (2026-11-19)

The earlier description of NPCs using better **physical hammer tools** is superseded. **Hammers (молоточки) are numerical construction work points**, akin to science/culture yield, NOT inventory items. A building has both a real material bill and `hammerRequired`. Once the physical Camp Heart has been built manually and a builder/workforce has been assigned, the settlement produces `hammers/hour` to advance its project(s). Example: a storage building requiring 700 hammer points at 400 points/hour takes 1.75 hours = 1h45m, provided material requirements are satisfied. The exact stacking of builders, allocation across simultaneous projects, offline progress and visuals of auto-build are not yet fixed.

The initial Camp Heart remains manual. Physical resources and the full structure requirement are not replaced by numeric hammer points: a finished construction order must still resolve to a valid real-world building. Whether blocks appear progressively during work or only upon hammer completion requires a separate design choice. Avoid implementing NPC pathfinding/hammer-in-hand equipment as the hammer-resource mechanic.


## Confirmed decision — progressive block-by-block construction (2026-11-19)

**This decision supersedes every earlier 'open question' about the visual manner of automatic building.** Both manually constructed and NPC-assisted buildings emerge **progressively as real Minecraft blocks** in the world, never as an instant fully formed prefab at 100%.

- Before the founding Camp Heart is physically complete, the player places the real blocks by hand. The schematic shows remaining positions.
- After eligible builders are assigned, the settlement produces an aggregate numerical `hammersPerHour`, allocated to active construction orders according to a future allocation policy. **Hammers are progress units, not tool items.**
- Each construction order defines a deterministic sequence of required block placements and a total hammer cost. For every next eligible schematic position, the server requires: (a) accumulated work sufficient for that position; (b) matching actual material from authorized stock; (c) a safe/valid, currently loaded, placeable target location and permission. Only then the server consumes/commits its material and places the actual BlockState. Existing correctly placed player blocks count as completed physical work; they must not be overwritten or charged twice.
- The building visually develops in stages (foundation, walls, roof and fittings as appropriate to the actual schema). Missing schematic volumes disappear as real blocks are installed. Exact ordering, per-block hammer cost distribution, manual-versus-produced work accounting, and multi-project scheduling must be specified in follow-up design; **block-by-block manifestation itself is FINAL**.
- Progress pauses rather than completing magically when there is insufficient stock, obstructed placement, missing permission, unloaded chunk or absent work production. The next retry must be safe and idempotent. For multi-block vanilla structures, atomic correct-state placement is required; no duplicate items or orphaned halves.
- `ACTIVE` can occur only when all required blocks physically match the server-validated construction, the material ledger is reconciled, and the required construction work criteria are satisfied.
- Test at 0%, intermediate progress and 100%; manually add/remove a correct block, stop builders, exhaust materials, unload/reload, crash/restart and use two players, proving stable real-world blocks, no instant prefab, no duplication and correct ghost disappearance.

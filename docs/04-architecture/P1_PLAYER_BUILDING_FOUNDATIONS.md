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
- The existing player design decision (real Minecraft block construction, no free prefab spawning) is preserved.

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
10. Run real Minecraft 26.3 client visual acceptance: prior render-pass crash selecting STORAGE must not recur; CI dedicated smoke is not graphical evidence.

## Explicit non-goals

No automatic free prefab construction, no instant construction on placing foundation, no client-owned progress, no full NPC construction or civilizations in this slice. Later NPC building should reuse the same persisted construction orders and material transaction checks.

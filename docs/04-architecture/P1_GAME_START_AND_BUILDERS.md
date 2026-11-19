# P1 — Player entry paths, camp founding, and builder-driven construction

**Decision:** 2026-11-19. **Status:** confirmed gameplay direction; code implementation pending.
**Related:** `P1_PLAYER_BUILDING_FOUNDATIONS.md`, PR #7, issue #8.

## Player's initial situation

Player spawns in a living Minecraft world and chooses organically, without a forced class or character-path selection:
- **Join an inhabited settlement:** discover an already living NPC settlement, seek acceptance as an ordinary resident, contribute through activities, and gradually earn higher responsibilities/status. The final rank titles, qualifications, governing systems and authority succession are NOT locked.
- **Found a new settlement:** craft a special Camp Heart foundation block, find suitable land, place it, move/rotate the hologram, confirm placement and physically build the **entire** Camp Heart in the world. There is initially no NPC workforce.

These paths remain fluid: an existing resident may eventually found their own camp; a founder may choose other political relationships later.

## Construction progression

### Phase A — wilderness / unfinished Camp Heart

- Player crafts the **Camp Heart foundation block**. This physical placement opens a full building schematic, not an instant prefab.
- Player can adjust position/rotation of the schematic before fixing the server-validated construction site. The foundation position stays anchored and participates in the footprint.
- Construction requires **real Minecraft blocks**. The player manually places them. There is no worker assignment, free NPC, abstract completion timer, automatic material deduction that creates fake blocks or automatic fill.
- A physically finished and server-verified Heart enables camp founding/management. The core structure is a functional building, not one isolated block.

### Phase B — built camp, residents and builders

- Once the Camp Heart is completed, the player can invite/recruit actual NPC residents, subject to genuine housing and entry conditions.
- A builder is an assigned NPC worker, not a free building summon. Builder-driven construction becomes available **only after the camp is built** and there is an eligible resident/job assignment.
- Player continues to place a distinct **building-specific foundation** for each structure and positions/rotates its schematic using exactly the same building-site system.
- The player may build manually, assign an eligible builder, or combine both methods on one construction order. The construction order is a single shared source of truth.
- The builder obtains real materials from authorized accessible stock, physically travels to the site and places the next required blocks. It must stop on insufficient materials, invalid rights, occupied spots, disconnected routes, invalid building or unloaded areas. No teleportation or fake production.
- Every placed block consumes one actual corresponding material (subject to vanilla placement representation, including multi-block shapes). Server validates atomic material/placement transaction and progress; crash/restart cannot duplicate or lose items.
- Hammer tool quality affects **placement/work speed**, not resource consumption or instantaneous construction. Actual multiplier, recipes, durability and number-of-builders scaling are balancing decisions yet to be finalized; proposed prototype examples ×1, ×1.5, ×2 are illustrative ONLY.
- Player and builder must not place the same block twice. Pause/resume/reassign assignments, recover from block removal and reconcile reality after restarts.
- Active buildings must still pass physical server-side validation to provide functional benefits.

### Phase C — settlement and further progression

- Promote CAMP -> SETTLEMENT when server-verified requirements are met; additional building-specific foundations/blueprints unlock. Building functions depend on real completed structures.
- NPC settlement world-generation, acceptance/status progression and NPC builder AI are required for the overall game but are **not already implemented by the present PR #7 prototype**.
- Autonomous NPC-created settlements and further state development belong to later milestones and must reuse the same material/physical building rules.

## Construction process model

`FOUNDATION_PLACED -> POSITIONING -> CONSTRUCTION -> PHYSICALLY_COMPLETE -> ACTIVE`.
Blocked states carry precise reason: `NEEDS_MATERIALS`, `NO_WORKER`, `UNREACHABLE`, `INVALID_SITE`, `UNLOADED`, `NO_PERMISSION`; use persisted state/diagnostics rather than inventing completion. Builder status is distinct from building state.

## Necessary acceptance tests

1. New player finds discoverable populated NPC settlement and can join at the basic resident rank; permissions, duties and advancement work according to documented rules (future implementation).
2. New player can independently craft/place Camp Heart foundation and position/rotate the full schematic.
3. No builders can work before the camp is complete; all initial Heart construction is manual.
4. Camp functions only after physical validation of full Heart. Invite residents only through server-authorized operations.
5. A recruited and assigned builder can obtain stock, navigate, place real blocks and stop/resume predictably.
6. Hammer comparison shows measurable placement-rate difference without creating or consuming extra block material.
7. Shared manual/NPC construction, interruption, missing materials, full storage, unloaded chunks, save/restart and multiplayer edits cannot duplicate resources or progress.
8. Stage promotion unlocks additional foundation item/building plans with same placement workflow; no instant prefab.
9. CI checks do not stand in for interactive Minecraft client GPU or AI navigation tests. Known historic schematic GPU crash must be separately checked.

## Deferred choices (not silently locked)

- Precisely how NPC settlements appear at world start (world-generation vs population of selected vanilla villages) and how they coexist with vanilla villages.
- Reputation/status/rank titles, governing privileges, acceptance rules and recruitment criteria.
- Exact initial Camp Heart footprint, required blocks and balance; treatment of the legacy 4 obsidian placement award.
- Hammer types, values, durability, recipes and multiple builder interference; builder movement, pathing and placement animations.
- Whether stock comes from authorized chests, dedicated warehouses or carried supplies, and details of crash-safe item reservation.

## Architectural priority

This replaces the earlier P1 order with **player entry paths + physical Camp Heart -> completed camp -> residents -> builder automation -> settlement advancement**. NPC-founded competing civilizations remain a later path, but the overall design reserves space for them.

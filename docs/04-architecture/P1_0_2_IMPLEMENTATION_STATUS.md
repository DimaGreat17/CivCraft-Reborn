# P1 / 0.2 Living Settlement — implementation status

**2026-11-18 | DEVELOPMENT BUILD, NOT A RELEASE | branch `feat/p1-living-settlement` | PR #7**

This is a real source-code implementation on top of the unmerged P0 branch. It does **not** establish P0 owner acceptance, G4 release freeze, complete P1 scope, or Minecraft client/dedicated gameplay signoff.

## Implemented (first vertical slice)

| Feature | Code and observable behavior | Caveat |
|---|---|---|
| Physical core block | `CampBlocks` registers `civcraft_reborn:camp_core`, crafting recipe, blockstate/model, drop and RU/EN name | Uses vanilla texture, no final custom art |
| Player-founded camp | `/civcraft camp found` near core, checks server WorldBorder, physical foundation, 96-block separation, one owner camp, and consumes **16 oak logs, 16 cobblestone, 8 bread** | Initial test balance, not frozen; future ACL/site and item tags needed |
| World persistence | `CampSavedData` with versioned Codec, stable CampId/owner/dimension/anchor/stage | Real save/load and corruption injection pending |
| Residents | `/civcraft camp recruit` registers **an existing nearby vanilla villager**, requires a reachable bed, free capacity and 4 bread; stable citizen and entity UUID in SavedData | Vanilla AI; no custom jobs/needs/loaded↔virtual yet |
| Housing inspection | `HousingScanner` scans actual beds/solid supporting floor, headroom and nearby roof with bounded BFS; reports rooms, verified cells and D-056 provisional 4 cells/NPC | Conservative prototype: NOT full D-057 PHYSICAL collision/path evidence, nuanced corridors, rights/comfort, multi-floor safety |
| Physical storage/structures | `CampStorageScanner` counts actual food in containers; `CampBuildingScanner` detects workshop (craft table+furnace), farm (farmland+composter), storage | Prototype modules only; no real production workflow or permission-safe shared inventory ledger |
| Camp promotion | `/civcraft camp promote` needs **3 verified rooms, 3 registered citizens, 16 actual stored food, real storage/workshop/farm**; retains CampId, never spawns items/NPC | Provisional requirements; rights, jobs and NPC camp promotion require more work |
| Diagnostics | `/civcraft camp status`, `inspect`; tests for sites, ID uniqueness, stage/identity and food conditions | Not yet graphical GUI |
| CI artifact | `.github/workflows/p1-living-settlement.yml` builds Java tests, boots dedicated server and uploads **development JAR** | Passing compilation/server smoke != playable-feature validation |

## NOT implemented; release blockers

1. NPC-founded group→camp with actual shared gathering/construction costs, NPC autonomous search and migration. NPCs currently cannot independently found camps.
2. Real work-ticket planner, crafting/harvesting/economy operations and exclusive inventory reservation/commit/reconciliation. Vanilla villagers may continue vanilla AI but this is **not** the promised CivCraft job system.
3. Persistent food/REST/SAFETY/HOUSING need transitions and citizen satisfaction, happiness diagnostics.
4. Verified room segmentation, exclusive cell ownership, fully physical slabs/stairs collision, private-only transit, comfort, safe split/merge/reassignment.
5. Proper server rights/ACL, claim/permission boundary, recipe balance, functional MODULES building catalog, UI.
6. Loaded↔Virtual identity, job/resource parity, no forced chunk loads, crash resilience and hostile world interactions.
7. Real NPC camp and player camp coexistence/integration, client & dedicated tests, full 0.2 acceptance suite, performance/profile and persistence migration.
8. Finalize and accept P0; branch P1 is based on **unmerged** PR #6 and not ready for deployment over existing worlds without backup.

## Design invariants honored in this slice

- No free items on founding or promotion, no free villagers on recruitment.
- Server-side checks before spending; world-scoped saved identity.
- Same permanent CampId on promotion.
- Provisional density 4 validated cells/NPC; COMBINED bed+floor rule.
- Full NPC world simulation and complete housing code **cannot** be claimed by this proof of concept.

## Player testing once CI is green

1. Back up existing saves. Install **both client and server** with matching Fabric 26.3 dependencies.
2. Obtain `civcraft_reborn:camp_core` via crafting or creative inventory; place on solid terrain.
3. Bring 16 oak logs, 16 cobblestone and 8 bread, stand within four blocks, run `/civcraft camp found`.
4. Place real beds in enclosed shelters and chests with food, farmland+composter, craft table+furnace.
5. Check `/civcraft camp inspect`, recruit **existing** nearby villagers with additional bread and `/civcraft camp recruit`.
6. Check `/civcraft camp promote` once the requirements show as fulfilled.
7. Quit/restart **a disposable world** and inspect `/civcraft camp status`; send crash logs if validation fails.

**NO RELEASE 0.2:** the codebase is only a first implemented slice. Do not merge or tag as released until all P1 gates are objectively satisfied.

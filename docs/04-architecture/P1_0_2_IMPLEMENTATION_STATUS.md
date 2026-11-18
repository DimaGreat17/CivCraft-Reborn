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

## User-requested creation and management UX update (2026-11-18)

**Choice:** physically constructed camp, **not** auto-generated building and not abstract inventory-only camp. Player crafts/places a real `camp_core`, then gathers real materials and registers the camp; further construction is by ordinary Minecraft blocks and a modular checklist/schematic, not an automatic free prefab.

- Right-clicking the physical camp core opens an **independent CivCraft management screen** with tabs **Лагерь** / **Цивилизация**. The camp section exposes **Основать**, **Проверить**, **Схема строительства**, **Принять жителя**, **Повысить**, **Список поселений**.
- The camp construction action opens a separate **CivCraftBlueprintScreen** with an ordered physical-build guide; its **Проверить построенное** button asks the server to report actual progress. Action buttons invoke **server-authoritative** `/civcraft camp ...` commands. There is no client-trusted mutation of stocks, citizens or camp ownership.
- `/civcraft camp plan` gives a progress-aware construction checklist: 3 actual sheltered bedrooms, 3 citizens, food in real storage, workshop, farm. The player constructs blocks physically. **No translucent world-space hologram/ghost blueprint or snapping has been implemented**.
- The **Цивилизация** tab is intentionally disabled and explicitly labelled future: actual civ government features belong to a later patch, not a fake functional 0.2 menu.
- The old floating Create World widgets are removed. CivCraft world options are now a dedicated screen entered from the vanilla World tab, with an explicit enable switch; a vanilla new world no longer receives the CivCraft border automatically. Existing 0.1 saves remain opt-in by legacy decode.
- Client tab mixin is `environment=client` in `fabric.mod.json`, so dedicated-server launch must not require client GUI resource.

**Acceptance still open:** verify the World tab placement and camp screen interactively on Minecraft 26.3, packet/command execution and permission checks on dedicated servers; CI Java compilation and server startup alone are insufficient.

## Starter rare materials — issued after EVERY physical Camp Core placement (2026-11-18)

**Owner clarification:** the game awards rare materials every time a player physically places a Camp Core in a CivCraft-enabled world. No grant on world creation, login, reconnect or merely holding the item. The prior once-per-player grant limit was incorrect and has been removed.

- `CampCoreBlock.setPlacedBy` awards **4 physical obsidian** each time a player places a Camp Core, from the authoritative server.
- The player places four obsidian north/south/east/west of the core before `/civcraft camp found` succeeds. This does not require an early-game obsidian mining tool.
- If the inventory lacks space, any remaining items drop at the camp location.
- The old receipt SavedData/state/test files were removed as unused.
- **Balance/exploit risk:** breaking and re-placing a Camp Core yields another 4 obsidian, as explicitly requested. A later crafting-cost and abuse review is required; do not claim the starter economy is finalized.
- A first **3D world-space blueprint preview** has now been coded: the player opens the Camp Core management UI → construction schematic → selects a HEART, STORAGE, WORKSHOP, FARM or BEDROOM template. Missing block locations are indicated by cyan client-side dust particles, and their locations disappear when matching real blocks are placed. The construction menu shows per-block progress. This is a particle-based holographic outline, **not yet a solid semi-transparent block mesh**, and has not been interactively client-tested. No snapping/auto-placement or client-authoritative construction is added.

## 3D blueprint preview (development, 2026-11-18)

- `CampBlueprintProjection` maintains a client-only active template bound to the last physical Camp Core clicked. The preview automatically hides on changing dimensions or walking farther than 28 blocks.
- `CampBlueprintProjection.Plan`: HEART (4 obsidian N/S/E/W), STORAGE (chest+barrel), WORKSHOP (craft table+furnace), FARM (4 farmland+composter), BEDROOM (optional 3×3 oak room, roof and any bed).
- `CivCraftBlueprintScreen` selects and hides templates and shows `completed / total` based on **real currently loaded client world block states**. The visual overlay uses cyan dust corner particles for missing cells. **Block correctness, resources, claim permission and promotion still belong to the server** and are checked by existing `/civcraft camp ...` commands, not by the client visual preview.
- Rendering is throttled every five client ticks and limited to 80 step locations per sample. No chunk force-load; preview does not work without a clicked core.
- The BEDROOM geometry is an **optional** example; under D-050/D-053 ordinary houses remain freeform and are validated by actual physical ROOM criteria. Exact template shape must not be misrepresented as mandatory.
- Pending G3 follow-up: fully translucent quads/solid block ghosts, correct rendering through/behind blocks, HUD progress without leaving the world, client multiplayer testing, localization and performance verification for large templates.
- **Acceptance test:** right-click core → choose HEART → see four cyan outlined cells → put obsidian one by one → each correctly placed cell disappears → remove obsidian → corresponding cell reappears → select workshop/farm/bedroom → walk >28 blocks or switch dimension, projection stops → dedicated-server restart and world rights remain unaffected.

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

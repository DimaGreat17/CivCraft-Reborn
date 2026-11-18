# G0 Pass 10 — legacy runtime mechanics, special units, persistence and portability

**Audit date:** 2026-11-18. **Evidence:** direct source inspection of `netizen539/civcraft` pinned historical snapshot `b64fe5b99da7c531fcdb5799217bf1aac2d760e0`. This is a **targeted source audit**, not a running Bukkit server and not a proof that VimeWorld later used the same implementation. All line references below refer to this old code snapshot.

## 1. Previously unmapped structures: code now located

| Old ID | Source | Verified responsibility | Important boundary |
|---|---|---|---|
| `ti_pasture` | [Pasture.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Pasture.java) | Registers pasture chunks and entities, limits breeding, persists entity association, handles death/removal | Not a generic offline food generator |
| `ti_wall` | [Wall.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Wall.java) | Procedural segmented wall construction, protected block checks, preview and repair/undo | `template: notused`: wall cannot be ported as simple one-schematic building |
| `ti_road` | [Road.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/road/Road.java) | Polyline/segment placement, material costs, protection check, undo records, database-backed road blocks and markers | Roads are **actual world geometry**, not evidence of NPC cargo routes |
| `s_shipyard` | [Structure.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Structure.java), [Buildable.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Buildable.java) | No `class Shipyard` surfaced in code search; likely generic/buildable entry, but actual runtime type resolution needs direct config mapping | **NOT VERIFIED** as an independent Java subclass; not correct to claim missing in game |

**Effect:** earlier [Building Parity Matrix](./BUILDING_PARITY_MATRIX.md) should no longer label Pasture/Road/Wall “not mapped”; exact per-building behavior, upgrades and persistence still require deeper confirmation.

## 2. Units are items bound to player actions

- [Settler.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/items/units/Settler.java): `spawn(Inventory,Town)`, `onInteract(PlayerInteractEvent)`, `execute(String playerName)`. Unit is an inventory object initiating a **player-triggered** town placement flow, not a roaming settler NPC.
- [FoundTown.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/items/components/FoundTown.java): explicit code comment says `XXXX THIS IS NOT BEING USED RIGHT NOW. SEE SETTLER INSTEAD`. **Do not use** this apparently similar component as the active implementation.
- [Spy.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/items/units/Spy.java): mission books, player inventory transfer and report-on-death behavior. It is not an autonomous spying humanoid entity.
- [EspionageMissionTask.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/threading/tasks/EspionageMissionTask.java): mission task references `playerName`, `startLocation`, `target`, configured mission; scout-tower distance contributes detection/exposure.

**Reborn design consequence (proposed):** player-controlled special unit actions and physically navigating NPCs require separate actor adapters over the same legal mission/settlement rules. Do not multiply historical “online enemy players” espionage formulas by virtual NPC population without explicit rebalancing.

## 3. Random events: **two distinct mechanisms**

**Old Java events:** [RandomEventTimer.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/randomevents/RandomEventTimer.java) iterates **individual towns**, skips a town with an active event, checks event config chances, selects an event. [RandomEvent.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/randomevents/RandomEvent.java) implements persisted components `requirements/actions/success/failure`, duration and modifiers. Five old configured IDs are in [CONFIG_INVENTORY.md](./CONFIG_INVENTORY.md).

**Source-confirmed defect:** `RandomEventTimer.java` around line 47 invokes `rand.nextInt(1)` for same-chance “coin toss”. In Java `nextInt(1)` **always yields 0**, so the code's intended fairness tie-break is not random. Record as `BUG-LEGACY-001` and do **not** reproduce.

**Later wiki global events:** [Мировые события](https://civ-wiki.vimeworld.com/wiki/Мировые_события) describes a world-wide weekly event: starts Tuesday 12:00 Moscow, lasts three days; chooses one of **11** events, including depression, rats, cultural/scientific booms, reconstruction, government transition, pacifism, world wars, religious fasting, circus and labor enthusiasm. Global events have systemic economic/science/war modifiers. **Wiki server schedule is historical, not a sensible mandatory singleplayer setting**.

**Reborn proposed split:** `TownRandomEvent` / `WorldPolicyEvent` share a typed effect framework and clock API but differ in scope, selection, concurrency and validity. User may choose game-time schedules later in GDD.

## 4. Persistence defects and migration safety

[SQLUpdate.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/database/SQLUpdate.java#L47-L66):
- `while(true)` repeatedly `saveObjects.poll()`, and on `null` immediately `continue`, with **no wait or sleep**: an unbounded busy-spin when the queue is empty.
- Each queued mutable SQLObject calls `saveNow()`; the excerpt does not show a bounded queue, replay log, atomic transaction spanning city/civ/capture changes or crash-safe persistence guarantees.
- Exceptions print stack traces and loop continues; no structured retry/backoff/dead-letter in this class.
- This demonstrates *risk*, not proof of observed data loss.

[SQL.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/database/SQL.java) and prior [Java audit 03](./JAVA_CODE_AUDIT_03.md) show entity tables + SQLObject inheritance. [Road.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/road/Road.java) and [Wall.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/structure/Wall.java) have distinct world-block undo/repair behavior.

**GDD / architecture nonnegotiables (proposed):** world-scoped versioned state, stable IDs, one server-authoritative commit owner, bounded write queue, immutable async read snapshots, no background Bukkit/Minecraft mutable-world access, idempotent monetary/item/capture transactions, explicit block-change journal, recovery after crash in the middle of siege, production, wonder completion or technology unlock.

## 5. World generation and settlement founding

Old [Settler.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/items/units/Settler.java) contains a player-coupled town placement validation path; user action and existing Town Hall proximity are checked. There is **no evidence of autonomous state seeding or world generator that populates all chunks with living NPC civilizations** in these inspected units.

Later wiki [Поселенец](https://civ-wiki.vimeworld.com/wiki/Поселенец) documents:
- first Settler **25,000 coins + 500 hammers**, each subsequent +25,000 coins (hammers unchanged);
- minimum **150 blocks** between new location and existing Town Hall/Capitol;
- culture proximity is **not** required; distance from capital causes unhappiness;
- leader approval required for requested new town.
These are later wiki, not verified old code constants.

Later wiki [Провинция](https://civ-wiki.vimeworld.com/wiki/Провинция) describes a **pre-civilization phase** starting V0.7:
- Province requires capital completion, Architecture, Trade, Legislation and capital culture level 3 to progress to civilization; threshold **25,000 carried coins** for founding province but not spent.
- Province has **one-city maximum**, no wonders or winning, restrictions on medieval techs, and province/civilization war pairing rules with an age-based exception.
- This stage is fundamental to designing NPC civ founding and cannot be omitted as “simple instant state creation” without user-approved replacement.

**World-size application D-013:** 10k/15k/25k X/Z boundaries apply to cities, trade/resource points, migration and strategic expansion. The 150-block founding minimum is a historical **wiki mechanic**, not a validated universally sufficient spacing for procedurally spawned NPC cities. Must reserve proper biome/resource density and viable founding spots.

## 6. License and source-rights warning — hard stop on copying

The [upstream repository](https://github.com/netizen539/civcraft) advertises a root **GPL-2.0** license. However [Spy.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/items/units/Spy.java) and [SQLUpdate.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/database/SQLUpdate.java) contain **AVRGAMING LLC 2013 All Rights Reserved / dissemination prohibited** header notices. This is a concrete conflict between apparent root license and individual file notices.

No definitive legal conclusion is possible from the repository alone; individual file histories, copyright ownership and any later permissions must be examined before copying. **Use independent new code based on documented mechanic facts unless rights are clarified.** Do not copy old Java, templates, schematics, sounds or third-party game assets into Reborn.

## 7. Portability / excluded upstream server modules

- Bukkit/Spigot commands, listeners, SQL globals, Bukkit scheduler and legacy block IDs are *implementation technology*, not a contract for modern Fabric.
- Dynmap plugin, arena, third-party anti-cheat, server chat/donation/UI and registration/perk infrastructure should be classified separately as server-only integrations. Their exact status awaits owner scope choice; **identified, not assumed required gameplay parity**.
- A clean modern mod should preserve gameplay behavior where approved but not import old server architectural assumptions.

## 8. Test checklist before G3 architecture freeze

1. Save/reload under concurrent player and NPC actions, and repeatably recreate queue-empty, queue-full, write-failure situations without spin/data loss.
2. Creation/abandonment of town, province-to-civilization progression, 150-block historical spacing adaptation, biome viability on each size preset.
3. Construct/demolish/repair road/wall with original-block recovery, permissions, chunk unload and crash.
4. Settler/spying capability from players and NPCs with identical resource costs and permissions; no online-player-count confusion.
5. Town event and global event modifier precedence, stacking, interval/cooldown and no duplicate effects.
6. Dependency and license trace for every reused artifact; prefer clean-room implementation.
7. PvP/war status transitions atomic with building ownership and tax ledger.
8. All background AI actions bound by tick/time budgets, and never force-load entire capped map.

**G0 evidence improved, but old runtime test, full license chain and source version parity NOT CLOSED.** No mod code written.

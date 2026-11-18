# Architecture Requirements (Draft)

## Server-owned domains

- `civilization`: state formation, diplomacy, strategies
- `settlement`: towns, claims, buildings and planning
- `economy`: inventories, production and atomic transactions
- `population`: citizens, needs, identity and skills
- `jobs`: orders, assignment, retries and reservation
- `world`: permission-checked block modifications and generation
- `combat`: armies, supplies, battle and capture
- `simulation`: scheduling, LOD, persistence
- `client`: visualization and UI

## World-bound simulation

- New-world size presets: **10k×10k**, **15k×15k**, **25k×25k** Overworld blocks. See [world size presets](./WORLD_SIZE_PRESETS.md).
- World border is server-authoritative; settlement generation, claim, planning and movement stay in bounds.
- Borders do not remove the need for active-entity, pathfinding, chunk generation and background AI budgets.

## Hard invariants

1. One authoritative citizen record per resident.
2. No duplicate production while crossing physical/virtual LOD modes.
3. Resource consumption and transfers are accounted for exactly once.
4. Reserved materials cannot be spent by competing jobs.
5. Every NPC world edit passes claim and permission checks.
6. World edits execute on the Minecraft server thread.
7. Interrupted jobs are resumable or safely rolled back.
8. Remote simulation never force-loads chunks solely to tick citizens.

## Open technical decisions

Persistence API, data migration/versioning, network protocol, serializer, Java/loader/game versions, performance budget and thread boundaries.

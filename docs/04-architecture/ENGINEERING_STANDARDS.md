# CivCraft Reborn — Engineering Standards (v0.1)

**Status:** Proposed engineering policy for review. **Scope:** Future Java/Fabric code, tools, integrations and data formats. No game code implemented yet.

## 1. Goals and non-negotiables
- Readability and testability outrank clever abstractions; optimize based on measurements.
- Domain rules must not depend on Minecraft classes, mixins, UI or persistence frameworks.
- Prefer explicit contracts and composition to inheritance trees or god objects.
- Every significant mechanic has an owner, a public API, invariants, acceptance tests and measurable performance budget.
- Preserve agreed game-design semantics; refactor architecture without silently rewriting rules.
- No legacy code copied until file-level licensing and attribution are reviewed.

## 2. Proposed logical modules (not prematurely separate Gradle projects)

```text
common/domain/             # Civ, settlement, citizen, economy, war identities, invariants
common/application/        # Use cases, commands, decisions, domain events
common/simulation/         # Strategic/offline ticking, schedule, deterministic calculations
common/ports/              # Persistence, clock, random, world access, event publication
fabric/server/             # Chunk lifecycle, entity interactions, server-only adapters
fabric/client/             # HUD, maps, renderer, screens
infrastructure/persistence/# Saves, versioned migrations, atomic persistence
data/                      # Datapack/config-defined technology/building/recipe catalogs
tests/                     # Unit, integration, deterministic replay, stress
```

Modules are conceptual boundaries first; split Gradle modules when measurements or build ownership justify it. Dependency direction: Fabric/infrastructure -> application -> domain; domain does not import Fabric.

## 3. Naming, design and review
- Java: clear English names, conventional UpperCamelCase types, lowerCamelCase functions/fields, CONSTANT_CASE constants.
- One well-defined responsibility per class. Split objects when they combine storage, world mutation, configuration parsing and decision logic.
- Small methods with early returns; no boolean-flag parameter explosion.
- Avoid static mutable global registries for game state; use world-scoped services passed explicitly.
- Prefer immutable `record` snapshots and typed IDs; mutable entities reside behind explicit ownership.
- No `null` as implicit control flow, no silent catch-and-continue, no unchecked unsafe casts.
- Comments explain *why*, invariants and Minecraft-specific constraints; names and tests explain *what*.
- Document non-obvious algorithms, coordinate units, tick units and invariants.
- Avoid hardcoded resource/tech/structure catalogs; use validated, versioned data definitions.
- Public-facing interfaces require Javadoc contract including thread expectations and errors.
- API changes affecting saves/protocols require a migration plan.

## 4. Simulation and performance
- All gameplay mutations are server-authoritative; world/block/chunk/entity writes occur on server thread.
- Expensive planning may execute on immutable snapshots off-thread, then revalidate and commit on server thread.
- Never access unloaded chunk blocks via APIs that silently force-load chunks in an offline simulation.
- Schedule citizen decisions with per-tick budgets and phase staggering. No full citizen scan every tick if not required.
- Use spatial indexes and caches for lookups; invalidate via events with bounded lifecycle.
- Prefer economic batch operations for unloaded settlements; define a single ledger for all LOD modes.
- No resource generation or double application during LOD transitions.
- Avoid chatty disk writes, per-tick object allocation in hot loops and unbounded logs.
- Record TPS/MSPT, active entity count, job queue depth, pathfinding time, LOD transitions, memory and save duration.
- Do not tune speculative micro-optimizations before profiler evidence.

## 5. Persistence and determinism
- Each civilization, settlement, citizen, job and army has a stable identifier.
- Snapshot versioning and migration are mandatory; verify backups and recovery.
- Reservations/transfer operations must be idempotent or transactional.
- Reproducible random inputs/seed and deterministic calculation fixtures where practical.
- World edits require permission checks, main-thread execution and failure handling.
- Restart/reload must neither duplicate nor lose inventory, population or active job ownership.

## 6. Testing and quality gates
- Static formatting, compilation, lint, dependency and resource validation on every PR.
- Unit tests for pure economic/AI rules; state-machine property/invariant tests.
- Integration tests for claims, containers, multiplayer permissions and chunk lifecycle.
- Load/stress tests for large cities, pathfinding, wars and repeated unloading.
- Save/reload and version migration tests.
- Review checklist: no design-registry contradiction; no new hidden mutable global state; no uncontrolled tick loop; no unbounded chunk loading; profiling evidence for performance claims.
- Every task: objective, constraints, acceptance checks, tests, docs and relevant design decision IDs.

## 7. Explicitly deferred choices
Minecraft version and loader pin, Java toolchain, Gradle module structure, serializer, data migration format, event bus, exact performance thresholds and code coverage targets require the technical design gate. No performance promises without benchmarking.

## 8. Change management
Architecture Decision Records (ADRs) capture alternatives, trade-offs, risks and approval. Design changes must update `DECISIONS.md` and all affected contracts/tests before implementation.

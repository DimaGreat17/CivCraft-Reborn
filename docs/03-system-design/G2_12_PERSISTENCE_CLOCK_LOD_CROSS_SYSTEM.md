# G2_12 — Persistence, clock, LOD and cross-system recovery

**CivCraft Reborn · 2026-11-18 · G2 DRAFT / NOT FROZEN**

**Traceability:** [Approved concepts D-001…D-048](../../DECISIONS.md), [Master GDD](../02-game-design/CIVCRAFT_REBORN_MASTER_GDD.md), [G2 shared invariants](./G2_00_CONTRACT_INDEX_AND_INVARIANTS.md). This is **design documentation**, not implementation, operational validation or final approved balance. All operations below are proposed signatures requiring owner review and G3 feasibility.

## 1. Domain aggregates and identity

- `WorldId, AggregateRevision, LogicalGameTime, OperationId, EventId, OutboxEntry, ProjectionLease, ReconcileState, SaveFormatVersion`: tracked as a distinct domain identity or versioned field; UUID/data representation requires G3.

All modifications require server authority and consistent Revision/OperationId. Actors are never granted powers simply because they are NPC; temporary Minecraft entities do not replace durable records.

## 2. Fundamental contracts

### SAVE-01 Persist versioned aggregate and outbox

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Crash after ledger before block mutation reconciles without duplicating. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### SIM-01 Reconcile Loaded to Virtual

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Crash after entity spawn before lease update avoids duplicate NPC. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### SIM-02 Materialize Virtual to Loaded

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Server stops for a week: zero accrued work. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### CLOCK-01 Advance active logical time

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Simultaneous load of two clients yields one projection lease. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### REC-01 Recover pending operation after crash

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Migration of prior save preserves stable IDs. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

## 3. Invariants and policy boundaries

1. game/server stopped => no catch-up production.
2. one authoritative citizen, one cargo lot and one active executor across projections.
3. external Minecraft changes require journal/reconciliation rather than fictitious global ACID transaction.

**Cross-system consistency:** money/stock through [G2-03](./G2_03_ECONOMY_TRANSACTIONS_AND_LOGISTICS.md); citizen projection through [G2-01](./G2_01_CITIZEN_JOBS_AND_MIGRATION.md); town sovereign ownership through [G2-02](./G2_02_SETTLEMENT_AND_POLITY_LIFECYCLE.md). Concurrency, persistence and scheduling through [G2-12](./G2_12_PERSISTENCE_CLOCK_LOD_CROSS_SYSTEM.md) when developed. External Minecraft block/container effects cannot be assumed to participate in one global rollback. Required adapters and compensations must be validated in G3.

## 4. Failure and recovery matrix

| Failure | Response | Post-condition |
|---|---|---|
| Insufficient resources | `INSUFFICIENT_STOCK` or relevant typed denial | No unaudited creation or partial charge |
| Stale revision / concurrent actors | `STALE_REVISION`, retry with fresh read | At most one commit for conflicted target |
| Permission changed mid-process | `PERMISSION_DENIED` or PAUSED depending on completed work | No unauthorized world mutation |
| Crash after domain commit before world effect | `RECONCILIATION_REQUIRED` | Restore intended effect or compensate; never double grant |
| Load/unload during action | Transfer executor lease and snapshot | One NPC/job/cargo, unchanged economic cost |
| Source is UNKNOWN / unverified | `UNSUPPORTED_CONTENT` | Not enabled as functional patch content |
| Outside 10k/15k/25k Overworld border | `BORDER_VIOLATION` | No placement outside configured playable world |

## 5. Acceptance scenarios (design targets, NOT executed tests)

- **G2_12-T01** — Crash after ledger before block mutation reconciles without duplicating.
- **G2_12-T02** — Crash after entity spawn before lease update avoids duplicate NPC.
- **G2_12-T03** — Server stops for a week: zero accrued work.
- **G2_12-T04** — Simultaneous load of two clients yields one projection lease.
- **G2_12-T05** — Migration of prior save preserves stable IDs.

## 6. Explicit pending decisions / blockers

1. **BLOCKER-1:** storage backend and Minecraft safe thread constraints require G3.
2. **BLOCKER-2:** TPS/MSPT/performance budgets not yet benchmarked.
3. **BLOCKER-3:** crash outcomes need executable failure injection on target loader.

Until the relevant blockers are resolved, this contract is a **design skeleton with enforceable intent**, not a ready-to-code implementation contract. Missing precise evidence/values may not be invented or silently accepted. G0 is OPEN; G2/G3/G4 not signed off.

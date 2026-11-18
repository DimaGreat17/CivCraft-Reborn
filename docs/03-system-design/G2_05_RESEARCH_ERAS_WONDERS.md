# G2_05 — Research graph, eras, wonders

**CivCraft Reborn · 2026-11-18 · G2 DRAFT / NOT FROZEN**

**Traceability:** [Approved concepts D-001…D-048](../../DECISIONS.md), [Master GDD](../02-game-design/CIVCRAFT_REBORN_MASTER_GDD.md), [G2 shared invariants](./G2_00_CONTRACT_INDEX_AND_INVARIANTS.md). This is **design documentation**, not implementation, operational validation or final approved balance. All operations below are proposed signatures requiring owner review and G3 feasibility.

## 1. Domain aggregates and identity

- `TechId, SourceVersion, PrerequisiteEdges, ANDJoin, ResearchOrder, ScienceAccount, WonderId, BuildOrder, ActiveEffectId, GraphRevision`: tracked as a distinct domain identity or versioned field; UUID/data representation requires G3.

All modifications require server authority and consistent Revision/OperationId. Actors are never granted powers simply because they are NPC; temporary Minecraft entities do not replace durable records.

## 2. Fundamental contracts

### TECH-01 Validate versioned tech DAG

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Missing parent blocks research rather than inventing edge. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### TECH-02 Begin research

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Graph cycle or orphan source ID fails build validation. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### TECH-03 Commit completion

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Two states cannot complete one unique wonder simultaneously. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### WONDER-01 Research wonder tech

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Destroyed wonder does not keep an unearned effect. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### WONDER-02 Compete for unique construction

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Reload cannot duplicate completed science costs. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

### WONDER-03 Activate/revoke effect

**Trigger/actor:** issued by authorized player/NPC service or scheduled authoritative server event only when target feature is enabled. **Inputs:** stable source/target IDs, OperationId, actor rights, expected aggregate revision, validated source/version and applicable LogicalGameTime. **Guards:** target exists, relevant permissions and dependencies hold, capacity/resources are available and all external changes are legal. **Reservation:** lock or stage relevant subject, resource, slot or effect with traceable ReservationId; avoid time-limited wall-clock assumptions. **Transition:** REQUESTED → VALIDATED → RESERVED → APPLY_PENDING → COMMITTED, otherwise REJECTED/PAUSED/RECONCILE. **Output:** one persistent typed event and read-model revision, with evidence of all costs and rights. **Failure:** reject before side effects when possible; after partial changes reconcile idempotently, never blindly refund or replay items. **Test:** Missing parent blocks research rather than inventing edge. **Specific policy:** finer formulas and external adapter actions remain subordinate to G0 source confidence, selected GDD and G3 architecture.

## 3. Invariants and policy boundaries

1. V1.0.9 user scheme has priority for structural evidence; late wiki additions retain separate version.
2. all upstream prerequisite edges AND-validated for players and NPC; no free science.
3. wonder research, actual construction, active bonus and victory are distinct stages; yellow wonder nodes not sequential.

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

- **G2_05-T01** — Missing parent blocks research rather than inventing edge.
- **G2_05-T02** — Graph cycle or orphan source ID fails build validation.
- **G2_05-T03** — Two states cannot complete one unique wonder simultaneously.
- **G2_05-T04** — Destroyed wonder does not keep an unearned effect.
- **G2_05-T05** — Reload cannot duplicate completed science costs.

## 6. Explicit pending decisions / blockers

1. **BLOCKER-1:** full diagram incomplete: 66 names, 68 candidate edges, 7 isolated.
2. **BLOCKER-2:** wonder slots/bonuses and six inaccessible cards unresolved.
3. **BLOCKER-3:** promotion prerequisites must be implemented with entire minimal viable tech closure.

Until the relevant blockers are resolved, this contract is a **design skeleton with enforceable intent**, not a ready-to-code implementation contract. Missing precise evidence/values may not be invented or silently accepted. G0 is OPEN; G2/G3/G4 not signed off.

# G3 — Technical architecture index and readiness

**2026-11-18 — DOCUMENTED DRAFT, NOT TECHNICALLY VERIFIED.** Target platform Minecraft Java/Fabric **in principle** (D-046). No version, compiler or runtime capability has been validated; no mod code modified.

## Architecture documents

1. [G3-00 — Boundaries, components, flows](./G3_00_ARCHITECTURE_BLUEPRINT.md)
2. [G3-01 — Persistence, journal and crash recovery](./G3_01_PERSISTENCE_TRANSACTION_RECOVERY.md)
3. [G3-02 — NPC projection, simulation scheduler and performance](./G3_02_SIMULATION_SCHEDULER_PERFORMANCE.md)
4. [G3-03 — Networking, security, content and legal provenance](./G3_03_NETWORK_SECURITY_CONTENT_PROVENANCE.md)

## System requirements sources

- [G2 full index](../03-system-design/G2_00_CONTRACT_INDEX_AND_INVARIANTS.md) — G2-01…12 documented, DRAFT.
- [Full Game Design](../02-game-design/CIVCRAFT_REBORN_MASTER_GDD.md) — 33 conceptual approvals, not frozen.
- [G0 audit gaps](../01-legacy-audit/G0_COMPREHENSIVE_AUDIT_REVIEW_2026-11-18.md).
- [G4 pre-code readiness](../05-roadmap/G4_PRE_CODE_READINESS_REVIEW.md) — explicit NO-GO until verification.

## Required G3 signoff evidence

| Check | Status |
|---|---|
| Stable Minecraft Java/Fabric loader/API/Java/mappings combination confirmed via actual tooling | NOT TESTED |
| Client and dedicated-server startup | NOT TESTED |
| Safe chunk/block/entity lifecycle and server-thread mutation integration | NOT TESTED |
| Durable save plus crash injection recovery (world effects vs own ledger) | NOT TESTED |
| Network actor authorization, command replay defence | NOT TESTED |
| Same CitizenId, JobId, cargo across Loaded↔Virtual | NOT TESTED |
| Three Overworld size stress profiles and server metrics | NOT TESTED |
| Reproducible migration tests / backup restores | NOT TESTED |
| Dependency and file-level license review | OPEN |

**No technical gate passes merely because its intended design is recorded.**
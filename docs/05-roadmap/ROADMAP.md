# Roadmap — Design Gates and Sequential Major Patches

**Release policy:** [RELEASE_STRATEGY.md](./RELEASE_STRATEGY.md) — the next major patch is blocked until the previous one is completed, tested, released and accepted.

## Design gates

| Gate | Deliverable | Exit criteria | Status |
|---|---|---|---|
| G0 | Original CivCraft code audit + VimeWorld wiki audit | Verified, provenance-labelled feature inventory and comparison | IN PROGRESS |
| G1 | Game Design Document | Full approved mechanics, options, edge cases | NOT STARTED |
| G2 | System Design Documents | Contracts, data, transitions, tests and dependencies | NOT STARTED |
| G3 | Technical architecture | Storage, LOD, scheduler, networking, perf budgets | NOT STARTED |
| G4 | Design freeze | Contradiction audit, scope lock, explicit approval | NOT STARTED |

**New G0-A wonder evidence:** [22 wonder technologies and their source-image gates](../01-legacy-audit/VIME_WONDER_TECH_BRANCH_PASS_06.md). The C5 branch is nonsequential; wonder research, construction and victory prerequisites remain separate for GDD approval.

## Proposed release sequence

P0 0.1 Foundation → P1 0.2 Living Settlement → P2 0.3 Civilization Systems → P3 0.4 Research & Culture → P4 0.5 Independent Rivals → P5 0.6 Diplomacy & Warfare → P6 0.7–0.9 Legacy Completion → P7 1.0 Stable.

The sequence is preliminary and can be revised *during design* based on dependencies and the legacy feature-parity audit. Once frozen, it is governed by the change-control policy.

## G0 source retrieval follow-up

[Pass 14](../01-legacy-audit/G0_BUILDING_SOURCE_RETRIEVAL_AND_CARDS_PASS_14.md) now distinguishes **42/73 wiki building articles retrievable** versus **31 currently inaccessible**, without claiming the 42 have complete effects audits. Bank-interest wiki table/formula conflict `BANK-CONFLICT-001` registered. G0 remains OPEN.

## Preliminary GDD work (not a phase-gate signoff)

[GDD-01: settlement → province → independent civilization](../02-game-design/SETTLEMENT_PROVINCE_CIVILIZATION_LIFECYCLE_GDD_01.md) was drafted at project-owner request during outstanding G0 audit. This is **PROPOSED**, not frozen. Early major patches have a dependency conflict: provincial promotion requires technologies, while the tentative patch ladder schedules civilization mechanics before the complete research tree. G4 must resolve prerequisite closure without fake stubs; no gameplay code was added.

## G0 comprehensive review (2026-11-18)

- [Full source coverage and gate verdict](../01-legacy-audit/G0_COMPREHENSIVE_AUDIT_REVIEW_2026-11-18.md): **G0 still IN PROGRESS**, all 18 gaps classified.
- [73 late building evidence ledger](../01-legacy-audit/G0_73_BUILDING_COVERAGE_LEDGER.md): 19 selectively article-described, 54 index-only; not complete parity.
- [Late tech graph validation](../01-legacy-audit/G0_TECH_GRAPH_VALIDATION_PASS_13.md): 66 labels, 68 partial edge candidates, 7 isolated; DAG incomplete.
- [28 wonders construction audit](../01-legacy-audit/G0_WONDER_CONSTRUCTION_MATRIX_PASS_11.md): 22 accessible article cards, 6 unavailable.
- [World mechanics and runtime risk passes](../01-legacy-audit/G0_WORLD_SYSTEMS_LATE_PASS_12.md), [source risks](../01-legacy-audit/G0_SOURCE_RUNTIME_AND_RISKS_PASS_10.md).

## Current priority

Finish G0 rather than writing mod code. See [G0 completeness / 18-gap closure register](../01-legacy-audit/G0_COMPLETENESS_GAP_REGISTER.md), [original audit](../01-legacy-audit/LEGACY_AUDIT.md) and [later VimeWorld wiki audit](../01-legacy-audit/VIMEWORLD_WIKI_AUDIT.md). G0-A identity mapping of all 27 old building IDs is done but behavioral parity remains open; partial later technology-to-building gates verified, research-tree image now user-supplied; 66 ordinary labels and 22 wonder entries visually transcribed, but 49 same-track edge candidates recorded; 8 interbranch visual gate groups logged; 19 named cross-branch candidate edges added (15 high confidence, 4 tentative), and combined 68-edge subset checked without cycles; remaining white-ring inputs and costs still require verification. Next: detailed per-feature effects/tech graph source retrieval, worldgen/NPC/permissions/license blockers.

Never mark a gate or patch complete without reproducible evidence and owner approval.

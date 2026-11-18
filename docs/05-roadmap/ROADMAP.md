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

## Current priority

Finish G0 rather than writing mod code. See [G0 completeness / 18-gap closure register](../01-legacy-audit/G0_COMPLETENESS_GAP_REGISTER.md), [original audit](../01-legacy-audit/LEGACY_AUDIT.md) and [later VimeWorld wiki audit](../01-legacy-audit/VIMEWORLD_WIKI_AUDIT.md). G0-A identity mapping of all 27 old building IDs is done but behavioral parity remains open; partial later technology-to-building gates verified, research-tree image now user-supplied; 66 ordinary labels and 22 wonder entries visually transcribed, but 49 same-track edge candidates recorded; 8 interbranch visual gate groups logged; 19 named cross-branch candidate edges added (15 high confidence, 4 tentative), and combined 68-edge subset checked without cycles; remaining white-ring inputs and costs still require verification. Next: detailed per-feature effects/tech graph source retrieval, worldgen/NPC/permissions/license blockers.

Never mark a gate or patch complete without reproducible evidence and owner approval.

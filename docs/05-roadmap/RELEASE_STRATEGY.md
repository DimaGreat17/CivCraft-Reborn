# Release Strategy — Sequential Major Patches

**Decision:** D-012 (approved concept, 2026-11-18).  
**Status:** Release process approved; patch contents and numbering are proposals until the full CivCraft + VimeWorld audit, GDD and architecture review are approved.

## Non-negotiable rule

**Only one development stage may be active at a time. A later major patch cannot enter implementation until the preceding patch is feature-complete, integrated, tested, documented, released and explicitly accepted.**

Fixes and maintenance of the active stage are permitted. Research and design of later stages may proceed, but must not introduce unfinished later-stage mechanics into the current release. No public commitments to exact release dates until scope and capacity are known.

## Work phases

1. **G0–G4 Design preparation (no main mod code):** audit legacy CivCraft code and later VimeWorld wiki separately; reconcile differences, write complete target GDD and system contracts, architecture and approval.
2. **One active patch:** implement only frozen scope, maintain tests and save compatibility.
3. **Release candidate:** all stories complete, performance measured, migrations checked, no blocking bugs.
4. **Acceptance:** user approves the release against its checklist; version/tag/changelog published.
5. **Next patch:** unlock only after acceptance.

## Proposed major-patch ladder (subject to design approval)

| Stage | Tentative release | Objective | Initial exit criteria |
|---|---|---|---|
| P0 | 0.1 Foundation | Mod boots, world-scoped state, registries, config, saves, **world-size preset choice at creation**, border, permissions/test harness | Create/load worlds for 10k/15k/25k without losing settings; enforce border; server-only authority, automated core tests |
| P1 | 0.2 Living Settlement | Player-founded camp → settlement, housing, basic workers, jobs, real stocks and construction | Autonomous test village survives 10 game-days, works correctly in loaded/unloaded simulation and across restart |
| P2 | 0.3 Civilization Systems | Multiple cities, claims/culture, treasury/taxes, economy/trade/logistics, government and happiness basics | Two settlements and a player settlement can interact, trade, claim and persist without duplication |
| P3 | 0.4 Research & Culture | Full selected technology tree, progress, upgrades, eras, cultural talents and infrastructure bonuses | All selected tech dependencies and building unlocks tested, progression complete |
| P4 | 0.5 Independent Rivals | Autonomous founding, expansion, trade, strategic decisions, NPC AI states and nation differences | Multiple autonomous factions grow with no resource cheats; stable long-run simulation |
| P5 | 0.6 Diplomacy & Warfare | Relations, war, militia/armies, supply, control-point capture, restoration, surrender/vassalage | Full conflict and peace cycle, consistent claims and rollback after crash/restart |
| P6 | 0.7–0.9 Legacy Completion | All approved original and VimeWorld legacy mechanics not covered earlier: wonders, espionage, events, special buildings, other eras and late-game if approved | Every retained legacy feature has passed its acceptance test; no missing agreed functions |
| P7 | 1.0 Stable Release | Multiplayer hardening, performance, UI, balance, localization, regression and migrations | Stable single-player/server playtests, load benchmark, acceptance matrix, docs and version tag |

**Important:** P1/P2 contain temporary cut-lines rather than empty or stub dependencies: all foundational prerequisites for a patch must be fully included and tested within that patch. If a system is inseparable, move it as a whole; no partial production features disguised as complete. Patch boundaries and version numbers can change only through a design decision before freeze.

## Definition of Done (each major patch)

- [ ] All approved patch requirements implemented and integrated.
- [ ] No placeholder UI, fake production, silent stub logic or half-working mechanic presented as finished.
- [ ] Unit + integration + save/load + multiplayer-relevant permission tests pass.
- [ ] No known release-blocking bugs; issues tracked and triaged.
- [ ] Performance measured against agreed budgets (TPS/MSPT, entities, pathfinding, memory, save times).
- [ ] Backwards save compatibility and migrations tested, or breaking changes documented and explicitly approved.
- [ ] Changelog, operational documentation and release notes written.
- [ ] Every implementation task linked to a GDD/system requirement and verification evidence.
- [ ] Explicit acceptance recorded; tag and GitHub Release for actual publication.

## Change control and hotfixes

- A new idea belongs in the future patch backlog, **not the active patch**.
- A critical bug found during testing is fixed within the active patch before acceptance.
- Post-release emergency fixes can use a patch/hotfix version without unlocking next major feature stage.
- An approved change to frozen scope requires impact analysis for dependencies, persistence, performance and testing.
- Do not mark patch complete based only on code merged or a demo screenshot.

## Current status

- G0 legacy audit: **IN PROGRESS**.
- VimeWorld later-version wiki review: **IN PROGRESS**.
- P0–P7: **NOT STARTED**, tentative sequencing only.
- No code implementation permitted until the design gates close.

# CivCraft Reborn — Game Design Document Index

**Status: PARTIALLY APPROVED_CONCEPT / NOT FROZEN (2026-11-18).** The owner approved 14 Round A (D-016…D-029), nine Round B (D-030…D-038), and seven Round C (D-039…D-045) concept choices. Three other decisions remain OPEN; G0 and G2–G4 remain incomplete. G0 audit remains open; G1 drafting completed at system-outline level, not approved. No gameplay code implemented.

## Main document

**[CIVCRAFT REBORN — MASTER GDD](./CIVCRAFT_REBORN_MASTER_GDD.md)** — vision, gameplay loops, 13 sections, intersystem dependencies, sources, acceptance and signoff rules.

## Complete GDD section library

| ID | Document | Status |
|---|---|---|
| GDD-01 | [Settlement, province, civilization lifecycle](./SETTLEMENT_PROVINCE_CIVILIZATION_LIFECYCLE_GDD_01.md) | PROPOSED |
| GDD-02 | [NPC population, needs, work and migration](./NPC_POPULATION_AND_BEHAVIOR_GDD_02.md) | PROPOSED |
| GDD-03 | [Production, economy, taxes, logistics](./GDD_03_ECONOMY_PRODUCTION_LOGISTICS.md) | PROPOSED |
| GDD-04 | [World generation, claims, culture and construction](./GDD_04_WORLDGEN_TERRITORY_CONSTRUCTION.md) | PROPOSED |
| GDD-05 | [Technologies, eras and wonders](./GDD_05_TECHNOLOGIES_WONDERS_ERAS.md) | PROPOSED |
| GDD-06 | [Government, rights and culture](./GDD_06_GOVERNMENT_LAW_CULTURE.md) | PROPOSED |
| GDD-07 | [Diplomacy, wars, occupation and restoration](./GDD_07_DIPLOMACY_WAR_CONQUEST.md) | PROPOSED |
| GDD-08 | [Autonomous civilization AI](./GDD_08_AUTONOMOUS_CIVILIZATION_AI.md) | PROPOSED |
| GDD-09 | [Victory, religion and events](./GDD_09_VICTORY_RELIGION_EVENTS_ENDGAME.md) | PROPOSED |
| GDD-10 | [Player experience, UI and multiplayer](./GDD_10_PLAYER_EXPERIENCE_UI_MULTIPLAYER.md) | PROPOSED |
| GDD-11 | [Quality, persistence, LOD and acceptance contracts](./GDD_11_QUALITY_SYSTEM_CONTRACTS.md) | PROPOSED |
| GDD-12 | [Nations, espionage, ruins and special mechanics](./GDD_12_ESPIONAGE_NATIONS_EXPLORATION.md) | PROPOSED |
| GDD-13 | [Feature provenance and scope traceability](./GDD_13_SCOPE_TRACEABILITY_MATRIX.md) | PROPOSED |

## Owner approval rounds

- **[Round A — living world: 14 concept choices](./GDD_REVIEW_ROUND_A_LIVING_WORLD.md)** — **APPROVED_CONCEPT by owner**; numerical tuning remains open.
- **[Round B — technologies, government, war and AI: 9 concept choices](./GDD_REVIEW_ROUND_B_STATE_AND_TECH.md)** — **APPROVED_CONCEPT** by owner; costs, contested historical details and patch numbering remain open.
- **[Round C — victory, religion, nations, espionage, events and UX: 7 concept choices](./GDD_REVIEW_ROUND_C_VICTORY_CONTENT_UX.md)** — **APPROVED_CONCEPT** by owner; historical content conflicts/unknowns explicit.
- **[Round D — technical platform, private enterprise, source rights: 3 choices](./GDD_REVIEW_ROUND_D_PLATFORM_SOCIAL_RIGHTS.md)** — **AWAITING OWNER APPROVAL**; no specific Minecraft version or source-reuse permissions assumed.
- [All 33 outstanding decisions](./GDD_APPROVAL_AND_OPEN_DECISIONS.md) — no decisions silently accepted.

## Supporting design constraints and source catalogs

- [33 owner decisions: 30 APPROVED_CONCEPT, 3 OPEN](./GDD_APPROVAL_AND_OPEN_DECISIONS.md)
- [D-015 interbranch technology research](./TECHNOLOGY_PROGRESSION_RULES.md)
- [D-014 finite victory vs endless sandbox](./WORLD_SESSION_MODES.md)
- [D-013 world size presets](../04-architecture/WORLD_SIZE_PRESETS.md)
- [G0 source audit](../01-legacy-audit/G0_COMPREHENSIVE_AUDIT_REVIEW_2026-11-18.md)
- [Sequential release process](../05-roadmap/RELEASE_STRATEGY.md)

## Completion disclaimer

**Document coverage is not game design freeze.** All 13 system sections are written at design/draft level, but numerical balance, precise mechanics for historically undocumented items and 33 owner choices remain open. The old CivCraft/VimeWorld sources contain contradictions, and the modern source/asset rights have not been resolved. G2/G3 contracts, architecture and G4 signoff must be completed prior to mod coding.

## Review sequence

1. Owner reviews master GDD and groups of open decisions.
2. Update all 13 sections based on approved selections; every frozen rule has a source/design decision and acceptance test.
3. Reconcile critical G0 gaps or explicitly approve restricted scope without missing core prerequisites.
4. Formalize G2 system contracts and G3 tech architecture.
5. G4 final review, patch decomposition, test plans and explicit implementation approval.

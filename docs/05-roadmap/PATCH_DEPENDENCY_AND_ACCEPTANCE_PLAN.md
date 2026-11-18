# Последовательные патчи и зависимости — план для будущего G4 freeze

**2026-11-18 · PROPOSED, NOT APPROVED PATCH CONTENT.** D-012 / D-038 / D-045; [G2 index](../03-system-design/G2_00_CONTRACT_INDEX_AND_INVARIANTS.md).

## Граф зависимостей

`World config / border → stable IDs and authority → saves / ledger → citizens / food / housing / jobs → local production / construction → claims / city / province → validated tech prerequisite closure + Capitol → civilization promotion → strategic AI expansion → diplomacy / war → wonder victory / events`.

Гильдии/частные предприятия требуют оплаты, собственности, найма, налогов и обратимых договоров. Поздние религиозные и дипломатические победы требуют всех реально существующих зданий/исследований и timer evaluator.

## P0 early-release narrowing (2026-11-18)

[Read P0/0.1 scope and exit criteria](./P0_0_1_SCOPE_AND_SIGNOFF_PROPOSAL.md). First patch deliberately excludes citizen/game economy/claims; all enabled functions (world choices, server border and state persistence) must work completely. This is a **candidate for explicit owner review**, not a changed approved release freeze. Minecraft 26.3 is a [toolchain verification candidate](../04-architecture/G3_P0_TOOLCHAIN_FEASIBILITY_PLAN.md), not a confirmed build. Session mode selection is stored and non-working FINITE outcomes must be truthfully disclosed per D-045.

## Предлагаемые релизные контрольные пакеты

| Patch candidate | Scope | Gate/tests |
|---|---|---|
| P0 Foundation | Выбор Overworld 10k/15k/25k, session-mode persisted, server authority, versioned save, event journal, claim stub **запрещён**; полноценные border/permissions для доступных действий | Dedicated/singleplayer create/save/load; border enforced, no false available finite win |
| P1 Living Settlement | **Основание лагеря игроком и автономное основание лагеря NPC на общей модели**; полноценная цепочка group→camp→settlement; еда, жильё, реальные ресурсы, jobs, **чертежи функциональных зданий + свободные дома/декор (D-050)**, Loaded↔Virtual и миграция, не псевдо-NPC. Игрок вправе вместо собственного основания вступить в существующее поселение (D-019). | Проверить оба пути основания лагеря, общий учёт ресурсов/прав, переход camp→settlement, loaded/virtual conservation, survival, crash recovery и производительность |
| P2 Intermediate Polity & Research (candidate revision) | Claims, city money, lawful province, validated early research **полной замкнутой цепочки**, Capitol, promotion to civilization together if feasible | All prerequisite AND gates, state transitions and tax rights tested |
| P3 Expansion & full selected tech | Дополнительные города, логистика, complete selected research DAG, здания, эпохи и культура в пределах утверждённого списка | Full graph validated; two cities and NPC state cost parity |
| P4 Independent Rivals | Autonomous country generation/expansion/trade and long-range AI strategy using real resources | Long-run fair simulation with no resource cheats and no excessive chunks |
| P5 Diplomacy & War | Five relations, agreements, army/supply, lawful siege/occupation/revolution/peace and restitution rule | Complete war→peace→capture→liberation cycle; WAR-CONFLICT-001 resolved |
| P6 Legacy & Endgame | Retained catalog of buildings/wonders, nation/religion/event/spy/ruins, space, fully usable finite victory outcomes and sandbox milestones | Each retained content source/recipe is approved and all win paths pass |
| P7 Stabilization | Multiplayer hardening, migration tests, UI/accessibility, profiling, balance, compatibility | Release acceptance and benchmark suite |

**These are dependency-safe candidates, not actual approved P0–P7 numbering.** P2 early tech and P3 selected later tree separation must be verified; if closure impossible, promote province only as part of P3 and move connected scope, no placeholder.

**P1 player-founded camp detail:** [P1_PLAYER_CAMP_FOUNDING_PROPOSAL.md](./P1_PLAYER_CAMP_FOUNDING_PROPOSAL.md) (D-049 approved concept; further details proposed). 

**P1 construction design:** [P1_BLUEPRINT_AND_FREE_BUILDING_DESIGN.md](./P1_BLUEPRINT_AND_FREE_BUILDING_DESIGN.md) (D-050 concept approved). **Modular blueprint format:** [P1_MODULAR_BLUEPRINT_CONTRACT.md](./P1_MODULAR_BLUEPRINT_CONTRACT.md) (MODULES D-051 concept approved; geometry, catalogue and balancing proposed). **HYBRID material choice:** [P1_HYBRID_MATERIALS_AND_FUNCTIONAL_BLOCKS.md](./P1_HYBRID_MATERIALS_AND_FUNCTIONAL_BLOCKS.md) (D-052 concept approved; exact functional blocks and recipes proposed). **ROOM housing validation:** [P1_ROOM_HOUSING_RECOGNITION_CONTRACT.md](./P1_ROOM_HOUSING_RECOGNITION_CONTRACT.md) (D-053 concept approved: bed, shelter, access, safety; numerical and technical rules proposed). **COMBINED occupancy and comfort:** [P1_COMBINED_HOUSING_CAPACITY_AND_COMFORT.md](./P1_COMBINED_HOUSING_CAPACITY_AND_COMFORT.md) (D-054 concept approved: beds + usable space cap occupancy, comfort is separate; numeric effects proposed). **FLOOR area metric:** [P1_FLOOR_AREA_HOUSING_METRIC.md](./P1_FLOOR_AREA_HOUSING_METRIC.md) (D-055 concept approved: usable floor area, not 3D volume; exact rules proposed). **Preliminary housing area balance:** [P1_FLOOR_HOUSING_BALANCE_4_BLOCKS.md](./P1_FLOOR_HOUSING_BALANCE_4_BLOCKS.md) (D-056: 4 usable block² per NPC for tests, not frozen balance). **PHYSICAL floor qualification:** [P1_PHYSICAL_FLOOR_VALIDATION_CONTRACT.md](./P1_PHYSICAL_FLOOR_VALIDATION_CONTRACT.md) (D-057 concept approved: real collision, walkability and safety; area contribution remains proposed). **CELL floor counting:** [P1_CELL_FLOOR_ACCOUNTING_CONTRACT.md](./P1_CELL_FLOOR_ACCOUNTING_CONTRACT.md) (D-058 concept approved: one validated cell = one block²; shared-zone handling and geometry implementation proposed). **Housing regions and shared areas:** [P1_HOUSING_REGIONS_AND_SHARED_SPACE_PROPOSAL.md](./P1_HOUSING_REGIONS_AND_SHARED_SPACE_PROPOSAL.md) (D-059 PRIVATE_ONLY approved concept: a room counts only its own cells, shared corridors/stairs do not increase housing capacity; segmentation, multilevel topology and repairs remain PROPOSED / NOT FROZEN). **Delegated room segmentation and recovery:** [P1_HOUSING_REGION_SEGMENTATION_AND_RECOVERY_DESIGN.md](./P1_HOUSING_REGION_SEGMENTATION_AND_RECOVERY_DESIGN.md) (D-060…D-063 DESIGN_SELECTED_DELEGATED; not frozen/implemented). **Housing Comfort & Needs:** [P1_HOUSING_COMFORT_AND_NEEDS_DESIGN.md](./P1_HOUSING_COMFORT_AND_NEEDS_DESIGN.md) (D-064…D-066 delegated design selected; no new city-happiness system, no numeric effects frozen). P1 freeze and P0 acceptance remain mandatory.

## Definition of Done per patch

Requirements traced to GDD/G2; no unavailable stub; both singleplayer/dedicated tests; crash/recovery tests; competitive AI parity; saved world compatibility; metrics measured and accepted; source provenance; full user acceptance and release tag before unlocking next major patch.

## G4 approval fields for each patch

`PatchId, ApprovedScope, ExplicitExclusions, SourceRevisionSet, RequiredTechClosure, NumericBalanceRevision, ReferenceHardware, MeasuredTPS/MSPT, SaveFormatVersion, DependencyVersions, LicensesReviewed, TestEvidence, OwnerAcceptance`. Blank = not ready.

## Open release decisions

- P0 finite world mode is selected/persisted, but usable completion is unavailable before late game; clarify exactly which UI/creation choices are available in each release.
- Research graph complete closure and economic catalog uncertain; cannot freeze P2/P3 split today.
- P6 scope may be too large for one complete major patch: splitting into sequential, independently completed sub-patches is allowed before freeze with explicit owner approval. No hidden release dates.
- Full PvP/NPC war gameplay/visuals require dedicated prototypes and mode balance.
- Every patch needs truthful launch criteria and accepted saved-state migrations.

**Current status: PATCH LADDER DRAFT — no coding permission.**
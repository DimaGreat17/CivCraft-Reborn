# Decision Register

Status: `APPROVED_CONCEPT` / `PROPOSED` / `FROZEN` / `NEEDS_AUDIT`.

| ID | Decision | Status | Notes |
|---|---|---|---|
| D-001 | Hybrid freeform sandbox, with no mandatory ruler path | APPROVED_CONCEPT | Player may be citizen, explorer, commander or ruler |
| D-002 | NPC states grow independently with real resource constraints | APPROVED_CONCEPT | No infinite AI resources |
| D-003 | Preserve original CivCraft features after auditing | APPROVED_CONCEPT | Catalog is outstanding |
| D-004 | Physical NPCs nearby; abstract simulation far away | APPROVED_CONCEPT | Round A GDD-009: equal costs, time, cargo; LOD and transition tests pending G2/G3 |
| D-005 | Unified resources, reservations and job tickets | APPROVED_CONCEPT | Round A GDD-008/010/011: one accounting authority; exact schemas and rates pending G2/G3 |
| D-006 | Latest stable Minecraft + Fabric | PROPOSED | Pin only after toolchain audit |
| D-007 | Autonomous village vertical slice | PROPOSED | Implementation milestone, not final scope |
| D-008 | Complete GDD and system specs before core coding | APPROVED_CONCEPT | Design-first workflow |
| D-009 | Code must prioritize readability, extensibility, testability and measured optimization | APPROVED_CONCEPT | Engineering standards documented; technical choices remain proposed |
| D-010 | Separate domain rules from Fabric/Minecraft APIs and persistence | PROPOSED | Architecture gate review required |
| D-011 | Do not copy legacy code/assets before file-level rights review | PROPOSED | Root GPL-2.0 conflicts with notices in inspected Java files; clarify |
| D-012 | Release large patches strictly sequentially; finish, test, publish and accept each before developing the next | APPROVED_CONCEPT | [Release strategy](docs/05-roadmap/RELEASE_STRATEGY.md); exact patch scopes are proposed |
| D-013 | Offer world size at creation: 10,000×10,000, 15,000×15,000, 25,000×25,000 blocks | APPROVED_CONCEPT | [World presets](docs/04-architecture/WORLD_SIZE_PRESETS.md). Border center, dimension handling, default and performance caps remain design-open |
| D-014 | Let player choose finite victory-based world or endless sandbox at world creation | APPROVED_CONCEPT | [Session modes](docs/02-game-design/WORLD_SESSION_MODES.md); victory types, default, post-victory behavior remain design-open |
| D-015 | Preserve interbranch technology dependencies so states cannot rush to the far end of one discipline | APPROVED_CONCEPT | [Technology progression](docs/02-game-design/TECHNOLOGY_PROGRESSION_RULES.md); exact links to be transcribed from owner-supplied tree image |

| D-016 | Initial NPC simulation has unique individuals, food, work and migration; families, births and aging deferred to separately approved design | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-001; numerical tuning and G2/G3 implementation details not frozen |
| D-017 | Core NPC needs are food, housing, rest and safety; water uses access/proximity requirement, not per-unit drink inventory initially | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-002; numerical tuning and G2/G3 implementation details not frozen |
| D-018 | Some independent NPC groups seed once at world creation with accounted resources; later new groups arise via real migration/founding, never free respawn | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-003; numerical tuning and G2/G3 implementation details not frozen |
| D-019 | Player may join NPC society or found own settlement under identical eligibility; player need not personally progress through camp to participate | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-004; numerical tuning and G2/G3 implementation details not frozen |
| D-020 | Preserve Capitol plus Architecture/Trade/Legislation research and capital culture as province promotion categories; exact threshold, money and prerequisite DAG unresolved | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-005; numerical tuning and G2/G3 implementation details not frozen |
| D-021 | Adaptive NPC/settlement seeding by biome viability and resources, with caps determined from profiling rather than fixed values now | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-006; numerical tuning and G2/G3 implementation details not frozen |
| D-022 | Players may hire NPCs and rent/buy housing under the same contracts, ownership checks and permissions as organizations | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-007; numerical tuning and G2/G3 implementation details not frozen |
| D-023 | Settlement planner chooses job portfolio, individuals execute/replan local actions; a worker/job/resource cannot be double-reserved | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-008; numerical tuning and G2/G3 implementation details not frozen |
| D-024 | Nearby deliveries physical, distant deliveries virtual with equal real-world inputs, elapsed game time, cargo limits, risk and ownership | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-009; numerical tuning and G2/G3 implementation details not frozen |
| D-025 | Physical Minecraft item stock is the material basis; construction hammers, science and trade statuses are separate ledgers; no NPC+legacy-timer duplicate production | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-010; numerical tuning and G2/G3 implementation details not frozen |
| D-026 | Use auditable double-entry transactions, bounded explicitly defined money issuance and source-backed earnings; exact prices, taxation, wages, interest and emission rules are not approved | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-011; numerical tuning and G2/G3 implementation details not frozen |
| D-027 | Approved border presets apply to Overworld; historical 150-block settlement spacing is reference only; Nether/End borders and resize/migration remain open | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-012; numerical tuning and G2/G3 implementation details not frozen |
| D-028 | Simulation progresses only during active server/game time (including distant settlements); no catch-up production while game/server stopped; exact clock unit and LOD cadence open | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-026; numerical tuning and G2/G3 implementation details not frozen |
| D-029 | Long crises can produce migration, gradual decline, abandonment/ruins with preserved citizen/settlement/state history; no instant deletion | APPROVED_CONCEPT | Owner approved Round A option A on 2026-11-18, GDD-032; numerical tuning and G2/G3 implementation details not frozen |

## Round A approval scope (2026-11-18)

Owner explicitly approved the recommended **conceptual** option A for 14 items GDD-001…012, GDD-026, GDD-032. Cross-cutting D-004 and D-005 are now approved **at concept level**; D-016…D-029 record one new approval per item. See [Round A source of truth](docs/02-game-design/GDD_REVIEW_ROUND_A_LIVING_WORLD.md) and [approval ledger](docs/02-game-design/GDD_APPROVAL_AND_OPEN_DECISIONS.md). The decision **does not** approve numerical amounts, clocks/cadences, NPC caps, unit costs, tax rates, money issuance rules, historical 150-block spacing, the exact culture threshold, research graph, release patch mapping, technical implementation, or any remaining GDD issues. No design freeze, G0 closeout, or coding authorization.

## Change control

Any approved or frozen decision change must specify why, affected features, save compatibility, acceptance tests, and explicit approval. Do not silently replace earlier decisions.

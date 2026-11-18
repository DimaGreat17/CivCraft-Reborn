# Decision Register

Status: `APPROVED_CONCEPT` / `PROPOSED` / `FROZEN` / `NEEDS_AUDIT`.

| ID | Decision | Status | Notes |
|---|---|---|---|
| D-001 | Hybrid freeform sandbox, with no mandatory ruler path | APPROVED_CONCEPT | Player may be citizen, explorer, commander or ruler |
| D-002 | NPC states grow independently with real resource constraints | APPROVED_CONCEPT | No infinite AI resources |
| D-003 | Preserve original CivCraft features after auditing | APPROVED_CONCEPT | Catalog is outstanding |
| D-004 | Physical NPCs nearby; abstract simulation far away | PROPOSED | Requires transition tests |
| D-005 | Unified resources, reservations and job tickets | PROPOSED | Check war/construction/trade compatibility |
| D-006 | Latest stable Minecraft + Fabric | PROPOSED | Pin only after toolchain audit |
| D-007 | Autonomous village vertical slice | PROPOSED | Implementation milestone, not final scope |
| D-008 | Complete GDD and system specs before core coding | APPROVED_CONCEPT | Design-first workflow |
| D-009 | Code must prioritize readability, extensibility, testability and measured optimization | APPROVED_CONCEPT | Engineering standards documented; technical choices remain proposed |
| D-010 | Separate domain rules from Fabric/Minecraft APIs and persistence | PROPOSED | Architecture gate review required |
| D-011 | Do not copy legacy code/assets before file-level rights review | PROPOSED | Root GPL-2.0 conflicts with notices in inspected Java files; clarify |
| D-012 | Release large patches strictly sequentially; finish, test, publish and accept each before developing the next | APPROVED_CONCEPT | [Release strategy](docs/05-roadmap/RELEASE_STRATEGY.md); exact patch scopes are proposed |
| D-013 | Offer world size at creation: 10,000×10,000, 15,000×15,000, 25,000×25,000 blocks | APPROVED_CONCEPT | [World presets](docs/04-architecture/WORLD_SIZE_PRESETS.md). Border center, dimension handling, default and performance caps remain design-open |
| D-014 | Let player choose finite victory-based world or endless sandbox at world creation | APPROVED_CONCEPT | [Session modes](docs/02-game-design/WORLD_SESSION_MODES.md); victory types, default, post-victory behavior remain design-open |

## Change control

Any approved or frozen decision change must specify why, affected features, save compatibility, acceptance tests, and explicit approval. Do not silently replace earlier decisions.

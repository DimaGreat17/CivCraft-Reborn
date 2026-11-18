# G0 Pass 04 — Happiness, Residents, Governments, Culture and Permissions

**Audit date:** 2026-11-18  
**Evidence classification:** historical [VimeWorld wiki](https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница) versus directly inspected older [Town.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java); no VimeWorld source code access. All future NPC behavior is a **REBORN PROPOSAL**, not a legacy claim.

## 1. Happiness — wiki rules

Source: [Счастье](https://civ-wiki.vimeworld.com/wiki/Счастье).

- Wiki percentage formula: `H / (H + U) × 100`, where H = accumulated happiness points and U = accumulated unhappiness points.
- Producing happiness: biome, monument, temple, arena, stadium, museum, theater, trade ship, world market, select trade resources and random events.
- Producing unhappiness: additional towns (wiki says 1.25 per town), captured towns (2), players, industrial buildings, and capital distance.
- Resident/nonresident scaling listed as `m × 2^n`, where resident `m=0.125`, plot outsider `m=0.075`, and `n` is capped at 4. Interpretation of ordering and counting needs explicit runtime confirmation.
- Distance penalty: `0.01 × distance^p`; `p=0.75` if city and capital culture areas connect, `p=1.05` otherwise.

| Happiness % | State | Science, coins, culture and work-point multiplier |
|---|---|---:|
| 0–19 | Бунт | ×0.20 |
| 20–39 | Беспорядки | ×0.50 |
| 40–49 | Несчастье | ×0.75 |
| 50–69 | Нормальное | ×1.00 |
| 70–84 | Счастье | ×1.15 |
| 85–91 | Ликование | ×1.30 |
| 92–100 | Экстаз | ×1.50 |

**Open detail:** the source gives no treatment for `H=U=0`; avoid divide-by-zero with an explicit approved rule.

## 2. Old Java code: source tracing

Source: [Town.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java), inspected directly.

- [Town.java lines 467–493](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java#L467-L493): residents are `Resident` membership records keyed by name; `addResident` associates a member with the town and resident permission group. **This is not evidence of physical, autonomous NPC population.**
- [Town.java lines 2738–2865](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java#L2738-L2865): `getUnhappiness` aggregates civ-wide unhappiness, resident count × configured coefficient, war modifiers, capital distance, negative structure effects and random events. Caches attribute totals with a timeout.
- [Town.java lines 2850–2875](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java#L2850-L2875): `getHappinessModifier` returns constant `1.0`, while `getHappinessState` obtains configured tier from actual `getHappinessPercentage`. **Do not assume the constant method disables the tier system**; other income/production code uses `getHappinessState().coin_rate`.
- [Town.java lines 2506–2542](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Town.java#L2506-L2542): `motherCiv` stored independently of current owning civ, removed on capitulation, and impacts potential revolt.
- Division of resident/land plot owners and civic roles must remain separate from NPC personal needs.

## 3. Government — wiki rules

Source: [Государственный строй](https://civ-wiki.vimeworld.com/wiki/Государственный_строй).

Wiki lists: Anarchy, Tribalism, Despotism, Monarchy, Theocracy, Communism, Republic, Mercantilism and Technocracy. The latter list is broader than the 7 government IDs in the older inspected `governments.yml` catalog.

The government table distinguishes multipliers for trade, strategic structures, cottage income, city upkeep, plant growth, tax cap, culture, work points, science and trade-ship income. Government transitions temporarily enter anarchy with substantial financial/production penalties. Exact population-based delay is a **player-count** wiki mechanic and should not be copied directly into NPC-populated Reborn without an explicit policy.

**Version note:** some special bonuses are specified in wiki prose rather than the comparison table; full effects require separate government-specific verification.

## 4. City/plot permissions — wiki rules

Source: [Группы и права в городе](https://civ-wiki.vimeworld.com/wiki/Группы_и_права_в_городе).

- A city claims 16×16 horizontal block chunks.
- Default city groups: mayors, assistants, residents; civilization groups: leaders, advisers.
- Each plot separates `owner`, `groups` and `others`.
- Four independent permission flags: Build, Destroy, Interact, Item Use.
- Nonresident players may be included in town groups; membership and land ownership are different.
- **Reborn proposed**: server-authoritative `PermissionService` uses a consistent actor principal `PlayerId / NpcCitizenId / OrganizationId`, never bypassing permission checks for NPC construction or inventory handling.

## 5. Culture — wiki rules

Source: [Культура](https://civ-wiki.vimeworld.com/wiki/Культура).

- Cultured territory is a **zone of influence**, separate from chunk ownership/protection; only owning/claiming the chunk grants the relevant block protections.
- Competing cultures may push the border based on culture strength.
- The wiki lists 11 levels; culture thresholds begin at 0 and reach 6.5 million at level 11, with a listed radius/extent of 8 to 50 chunks.
- Terrain/biome produces modifiers for work points, vegetation/growth, happiness and science.
- **Reborn impact:** culture propagation, plot ownership, legal governance and world border must be separate geometry/state layers.

## 6. Social population — critical old vs new distinction

| Topic | Verified in sources | Reborn proposed direction |
|---|---|---|
| Population | Legacy resident records tied to players/permission groups | Explicit physical NPC identities, demographics, jobs and needs |
| Food | Wiki granary/cottage automated production (previous pass) | Household-level food consumption with reserves and interruption |
| Housing | Cottage provides economy effects; no proof of autonomously simulated family households | Residential capacity, beds, family/migration rules only after GDD approval |
| Health / birth / death | Not established by sources inspected in this pass | Open: demographic simulation and its depth |
| Happiness | City aggregate score / production tiers | Keep aggregate city wellbeing; separate optional per-person morale/needs |
| Government | State-wide economic rules | AI government decision process with exactly same policy enforcement as players |
| Migration / revolts | Revolts linked to conquered-town ownership, not evidence of everyday migration AI | Explicit event-driven migration and revolt if user approves |
| World size | Not inferred from original sources | D-013 user-approved 10k/15k/25k bound at world creation; density and culture range must be border-aware |

**Design safeguard:** Do NOT apply old player-member exponential unhappiness formula per NPC. With hundreds of NPCs, that would have unintended effects and is not historically supported. Population weighting and city-wide penalties require new balancing and specification.

## 7. Design acceptance recommendations (NOT approved implementation)

1. One authoritative citizen identity with separate employment, residence, housing, tax status, owner and legal group memberships.
2. Happy/unhappy contributions recorded with source; modifiers deterministic, capped where required, tested for zero totals.
3. No settlement placement, claim or expansion beyond approved world border; geography-based bonuses and distance use appropriate bounds.
4. Governments are typed policies and use a controlled transition state, not hard-coded if-else across unrelated buildings.
5. Civ-wide modifiers propagate to all cities; city-specific effects remain local; avoid full world recomputation per NPC.
6. AI and players must operate on the same resource/territory/permission rules.
7. Reproduce historical wiki numbers only in version-labelled optional balance packs if selected for GDD, never silently set as new defaults.

## 8. Remaining G0 gaps

- Full population role, local governance and taxation workflow audit; compare original code's resident roles with later wiki.
- Verify all nine VimeWorld government modifiers and switching edge cases.
- Formalize rebellion, capital overthrow and civil war with capture model.
- Separate player-only permission checks from potential NPC actors in a new design.
- Decide NPC population scaling relative to 10k/15k/25k world-size presets.
- Full gameplay design and architecture freeze must precede implementation.

**G0 remains OPEN.**

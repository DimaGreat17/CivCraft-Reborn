# G0 Pass 06 — Diplomacy, war termination, occupation and peace treaties

**Audit date:** 2026-11-18. **Evidence:** original `netizen539/civcraft` Java source inspected with GitHub connector; later VimeWorld Wiki verified via direct page access. Later implementation itself is not available. **Status:** fact audit, not an approved gameplay specification.

## Source provenance
- OLD CODE: [Relation.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Relation.java), [DiplomacyManager.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/DiplomacyManager.java), [CivDiplomacyCommand.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/command/civ/CivDiplomacyCommand.java), [War.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/war/War.java).
- LATER WIKI: [Diplomacy](https://civ-wiki.vimeworld.com/wiki/Дипломатия), [War](https://civ-wiki.vimeworld.com/wiki/Война), [Revolution](https://civ-wiki.vimeworld.com/wiki/Революция), [Civilization commands](https://civ-wiki.vimeworld.com/wiki/Команды_цивилизации). These are historic documentation, not live 2026 runtime proof.

## 1. Diplomacy state set — verified in old source and later wiki

[Relation.java lines 43–51](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Relation.java#L43-L51): `NEUTRAL`, `HOSTILE`, `WAR`, `PEACE`, `ALLY`. `MASTER` and `VASSAL` are **commented out**, hence vassalage is NOT an implemented enum state in inspected snapshot. [DiplomacyManager.java lines 123–169](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/DiplomacyManager.java#L123-L169) maintains a war-count for relation transitions and defaults unknown relations to NEUTRAL. The code contains a commented-out vassal-specific branch.

| State | Later-wiki documented result | Confidence |
|---|---|---|
| Neutral | Default status; neutral city PvP off, border crossing possible | WIKI |
| Peace | Friendly status with no distinct mechanical ceasefire effect versus neutral in cited war article | WIKI |
| Ally | Scout towers/ships exclude allies, espionage within ally culture disallowed | WIKI |
| Hostile | Defenses attack hostile players | WIKI |
| War | PvP and (during war window) structural damage/capture | WIKI and CODE |

## 2. Diplomatic requests and declarations

[Old CivDiplomacyCommand.java lines 241–375](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/command/civ/CivDiplomacyCommand.java#L241-L375) verifies:
- Authorized leader/advisor may request NEUTRAL, PEACE or ALLY. Recipient is asked to accept or reject.
- In old `casual mode`, mutually **requested** WAR is supported; unilateral war declarations are prohibited.
- Normal WAR declaration blocked during WarTime, near next WarTime (with ally assistance exception), and when declarer civilization is in debt.
- HOSTILE declaration is unilateral but cannot substitute for WAR during an ongoing war.
- Declaring to own civilization or attacking admin civilization blocked.
- Ally-request restrictions apply near war windows when either party is already at war.
- War declaration sets aggressor metadata; code has commented-out special logic for attributing aggression when defending an ally, so **correct allied intervention aggressor attribution is not fully verified**.

Wiki [Diplomacy](https://civ-wiki.vimeworld.com/wiki/Дипломатия): `/civ dip request … neutral/peace/ally`, `/civ dip declare … hostile/war`, `/civ dip dissolution`, `/civ dip respond yes/no`. Different relationships have different defense, entry and spying rules.

## 3. War windows and peace

[Later wiki War](https://civ-wiki.vimeworld.com/wiki/Война) explicitly documents:
- War declaration no later than 72 hours before scheduled WarTime, with 24-hour exception during victory countdown.
- All civilization cities treated as war participants, and structure damage is allowed during war event window.
- Six ordinary-town control points, eight capital control points.
- War ends by conquest or diplomacy; ceasefire/neutral, peace, ally agreements are accepted by both parties. Peace vs neutral not mechanically distinct as cessation according to this article.
- Aggressor pays structure-upkeep multiplier **2.5×**, or **5×** when defender has Notre Dame, with no increase for concurrent second war per wiki description.
- Server weekday/clock-time restrictions are **historical server policy** and should not be defaulted to singleplayer Reborn.

## 4. Loss, occupation and liberation

From [later wiki War](https://civ-wiki.vimeworld.com/wiki/Война):
- Defeating a civilization transfers its cities, but they remain occupied rather than immediately assimilated.
- Historical leadership remains on record, and captured towns preserve their former civilization identity.
- Automatic capitulation after 7 days; manual capitulation also described.
- Trade goods are transferred to conqueror under cited war rules.
- A conquered city keeps its local government and conqueror cannot administratively replace or dissolve it immediately.
- `/civ dip liberate <town>` may return an uncapitulated town; liberation of a capital revives the civilization, while remaining cities require separate return actions under the **later wiki** description.
- The [old-code liberation method](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/command/civ/CivDiplomacyCommand.java#L116-L161) **differs importantly**: when liberating a capital, iterates all towns with that mother civ **and** owned by same capitol-owner civ, returning them, rather than necessarily returning exactly one. This is a **verified inter-version behavioral discrepancy**, record as `WAR-CONFLICT-001`.
- Old-code `War.processDefeated` [lines 264–316](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/war/War.java#L264-L316) has a distinct **casual mode** branch where defeat leads to resetting relation to NEUTRAL instead of conquest transfer.
- The [Revolution](https://civ-wiki.vimeworld.com/wiki/Революция) wiki describes time-limited revolution windows, forced government transition to Tribalism/anarchy, and loss of three recently researched techs; the previous report traced old-source command's fee, cooldown, capital requirements and civ-wide liberation. These are different version-specific rules, not silently merged.

## 5. Further later-version diplomacy features

The [Civilization commands](https://civ-wiki.vimeworld.com/wiki/Команды_цивилизации) wiki also lists:
- `/civ dip gift town <town> <civ>`: gift a town; warning that wonders are destroyed.
- `/civ dip gift entireciv <civ>`: gift an entire civilization; similar wonder destruction warning.
- `/market`: purchase town or civilization with unresolved >7-day debt, with wonder destruction warning.
- `/civ vote`: vote for a civilization for diplomatic victory.
- `/civ perm`: role-based special abilities; `/civ group`: leader/adviser lists.

**Important distinction:** a gift/market transfer is *not* the same as conquest, occupation or liberation. These need separate domain operations with separate historical-identity handling.

## 6. Systems breakdown for Reborn (PROPOSED, no approval)

| Domain | Core state | Invariants / commands |
|---|---|---|
| Diplomacy | Unordered pair of civ IDs, state, actor, aggression record, treaty terms, optional expiry | Exactly one coherent bilateral relation, permission-checked transitions, idempotent save |
| Treaties | Offer, reply, validity, rescission, optional concessions | Authorized signatures and no unilateral peace unless rules explicitly allow |
| War | Participants, aggressor, legal war window, war goals, active siege sites, winner | No contradictory wars / ceasefire states; single authoritative war lifecycle |
| Sovereignty | Current owner, native civ, occupied/capitu­lated status, mayor and allowed rights | Differentiate transfer by conquest vs donation vs annexation vs liberation |
| Liberation | Legality, restored civ ID and town target, postconditions | Never lose uncaptured data or transfer towns to wrong owner |
| Economy | War upkeep penalty, war reparations, tributes, gift transfer, trade-good ownership | Atomic ledger and resource movement, no duplicate expenditure |
| Revolution | Eligible historical civ, cooldown/fee, towns and war creation | One atomic operation, no partial state after crash, repeat-safe |
| Victory | Separate diplomatic-victory rule evaluator | No unverified reuse of Vime wiki server schedule |

The word `vassalage` is an optional *future design concept only*: not verified in the five-state original relation enum. Any such addition needs specific GDD approval and must not be counted in parity coverage.

## 7. World and sequential release implications

- D-013: 10k/15k/25k world presets. War front planning and settlement expansion must respect fixed border; remote AI war cannot force-load all battlefield chunks.
- P0: stable ID / persisted world scope / border.
- P2: governance and taxation / permissions, but not a half-implemented war mechanic.
- P4: autonomous AI can evaluate neighbor relations and prepare decisions only to the extent fully supported by active patch scope; do not ship inaccessible war UI/stubs.
- P5: complete diplomacy, war, treaties, occupation, liberation and revolution in one finished, verified stage (unless patch dependency review explicitly reassigns all related responsibilities).
- P6: special additional late-era defenses, conditions, and approved expansions after P5 completion.

## 8. Mandatory tests before G4 design freeze / release

1. Bilateral set relation and request/accept/decline, including expired offers and one-sided disconnection.
2. Unilateral hostility and mutual peace; war state transitions reject invalid calls.
3. Player and NPC leaders use same permission/treasury rules.
4. Casual-mode and mainline war variant require explicit rules selection; cannot implement both by accident.
5. Capture of city versus capital preserves historical identity and clear expected ownership.
6. Defeat, retreat/peace, surrender, manual/auto-capitulation, liberation, revolution, gift-town and entire-civ.
7. Different inter-version capital liberation outcomes are resolved by owner decision.
8. War multipliers apply once; Notre Dame conditions and concurrent war case tested.
9. Restart/crash during state transition recovers without duplicated towns, goods, treasury or war state.
10. Strategic AI and border-aware targets prevent expansion, teleport and siege outside world bounds.

## 9. Open decision and evidence register

- `WAR-CONFLICT-001`: old source liberates capital **plus qualifying historical towns held by same owner**; later wiki says capital returns alone and other towns need separate commands. **Version divergence — do not pick automatically.**
- `WAR-UNKNOWN-002`: later wiki does not validate code-level offer expiry/serialization, allied-aggressor logic, or exact duration of ceasefire.
- `WAR-UNKNOWN-003`: war schedule / real-time 7-day capitulation and revolution weekday window are not suitable as-is for singleplayer pausable saves.
- `WAR-UNKNOWN-004`: vassalage state not implemented in inspected old relation enum. No validated Vime implementation located in reviewed pages.
- `WAR-UNKNOWN-005`: whether losses of wonders on gifted/debt-market town transfers must be carried across into Reborn; user design review.
- `WAR-UNKNOWN-006`: diplomacy victory vote thresholds and timers need separate page audit.

**Gate G0 remains OPEN. No gameplay code written.**

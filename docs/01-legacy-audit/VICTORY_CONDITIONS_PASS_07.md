# G0 Pass 07 — Victory Conditions: legacy source vs VimeWorld evolution

**Audit date:** 2026-11-18. **Status:** SOURCE-TRACED (old Java/config) and WIKI-DOCUMENTED (later VimeWorld). This is **not** the approved Reborn endgame rulebook. The user's D-014 decision (finite victory or endless sandbox selected at world creation) is **approved in principle**.

## Evidence sources

- Old Java source, pinned commit `b64fe5b99da7c531fcdb5799217bf1aac2d760e0`: [endgame directory](https://github.com/netizen539/civcraft/tree/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame)
- Original [civ.yml](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft_data/data/civ.yml), `end_conditions` block.
- Historical VimeWorld [Old Условия Победы](https://civ-wiki.vimeworld.com/wiki/Old_Условия_Победы), [CivCraft V0.9 notes](https://civ-wiki.vimeworld.com/wiki/CivCraft_V0.9), [CivCraft V1.2 notes](https://civ-wiki.vimeworld.com/wiki/CivCraft_V1.2), [CivCraft V1.4 notes](https://civ-wiki.vimeworld.com/wiki/CivCraft_V1.4), [Hall of Winners](https://civ-wiki.vimeworld.com/wiki/Зал_победителей).
- Source unavailable for later-server implementations; wiki and patch notes may document different versions. Keep them version-labelled.

## 1. The four original CODE-CONFIRMED win conditions

| Victory | Config/class | Prerequisite | Hold | Extra final test / cancellation |
|---|---|---|---|---|
| Cultural | `end_culture`, [EndConditionCulture](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndConditionCulture.java) | 3 eligible native towns at culture level >=10; 1 wonder | 21 days | Not conquered; war defeat resets |
| Conquest | `end_conquest`, [EndConditionConquest](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndConditionConquest.java) | Captures >=75% of world town metric, or >=50% if active Chichen Itza; at least 28 days after source-defined start date | 21 days | Cannot be conquered; defeat resets |
| Scientific | `end_science`, [EndConditionScience](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndConditionScience.java) | Research `tech_enlightenment`, active Great Library in eligible native city | 21 days | Have most *accumulated extra beakers* at final check; defeat removes enlightenment and accumulated winning beakers |
| Diplomatic | `end_diplomacy`, [EndConditionDiplomacy](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndConditionDiplomacy.java) | Active Council of Eight in eligible native city | 21 days | Most votes at final check; each registered Resident has vote cooldown configured 24h; war defeat destroys wonder, clears votes and resets |

**Precision note:** `EndConditionConquest` compares `Civilization.getPercentageConquered()` to 0.75/0.50. It does **not** directly compute fraction of traversable blocks in this class. Need inspect that method before claiming denominator represents world area, claims, or number of civilization towns.

### Original shared victory processing

[EndGameCondition.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndGameCondition.java):
- Config loads class names, conditions, `days_held`, starts and failures into session database.
- Holding/progress check increments a days-held counter on repeated calls; exact scheduler cadence must be traced. Not every invocation is automatically one real day.
- On completion, calls `Civilization.declareAsWinner`. This file does not itself describe automated world deletion.
- On failure, removes active progress for appropriate civ. Specific subclasses reset other resources (votes, science, wonders) on war defeat.
- Old code `isActive(civ)` checks whether the shared session key has entries; source likely has a multi-civ attribution hazard, as noted by internal code comments. Treat as a code review question, not proven runtime bug.
- Original `getDaysLeft` reads the first session entry's value as an integer even though `getSessionData` saves `id:daysHeld`; potential serialization mismatch. Verify the caller and actual session DB behavior before claiming failure.

## 2. Version evolution / later VimeWorld evidence

### Old wiki behavior
[Old Условия Победы](https://civ-wiki.vimeworld.com/wiki/Old_Условия_Победы) describes four familiar categories, 3-week sustain period and server map reset when a winner is chosen. This **server reset** is a policy of that instance; not acceptable to copy into Reborn without user approval, and would conflict with D-014 endless mode.

### Later wiki changes
- [V0.9 patch notes](https://civ-wiki.vimeworld.com/wiki/CivCraft_V0.9): Council of Eight victory changed to **4 weeks**, no longer needs votes, may start only after two weeks from world beginning. This contradicts old source's 21 days + vote final check.
- [V0.6 notes](https://civ-wiki.vimeworld.com/wiki/CivCraft_V0.6): economic victory requirements changed to Stock Exchange level 6, replacing prior cottage requirements. This confirms economic victory in a later generation, not code-present in old endgame directory.
- [V1.2 notes](https://civ-wiki.vimeworld.com/wiki/CivCraft_V1.2): Scientific Victory counts only Space Shuttle in civilization **capital**; accumulated science weights native cities 100%, other cities 30%.
- [V1.4 notes](https://civ-wiki.vimeworld.com/wiki/CivCraft_V1.4): introduces **Religious Victory**: needs a religion and Notre Dame checked each day at tax collection, can start after 21 days of server phase, 3-week progress. Economic victory requires maximum World Bank upgrade and hold period.
- [Hall of Winners](https://civ-wiki.vimeworld.com/wiki/Зал_победителей) records results of science, economic, cultural, diplomatic, religious, domination and military categories across historical phases. **These labels are historical event outcomes**, not evidence that seven distinct code classes existed simultaneously in any one build.

## 3. Cross-generation taxonomy (do not conflate)

| Candidate track | Old Java | Later wiki support | Critical unresolved rule |
|---|---|---|---|
| Cultural | YES | YES | Thresholds later increased by updates, eligibility and normal/captured city contributions |
| Conquest / Domination | YES as `Conquest` | Domination and Military labels recorded; wiki war system | Distinguish territorial/world-percentage conquest vs separate 'military' victory in later versions |
| Scientific | YES | YES, Shuttle and research-weighting changes | Is Shuttle a required wonder or endgame mission in target revision; exact science aggregate |
| Diplomatic | YES | YES, Council rule reworked | Votes in old version vs no-votes Council 4-week later version |
| Economic | NO in inspected old endgame classes/config | YES; Stock Exchange earlier, World Bank later | Max tier, holding duration and economic metric |
| Religious | NO in inspected old endgame classes/config | YES since V1.4 | Religion and Notre Dame daily eligibility, start time |
| Space mission | NO in inspected original 4 conditions | YES as later science progression | Whether an independent victory category or scientific prerequisite |

## 4. Key design difference: bounded size != winning condition

D-013 Overworld width presets **10k/15k/25k blocks** set geographic bounds. A conquest requirement of '75% of the world' must **not** be treated as 75% of all block area without defining the measurement; old source uses an abstract `getPercentageConquered()`. AI faction count and density also affect viability, especially on small world.

## 5. Approved game mode D-014 and candidate architecture

World creation:
- `FINITE_VICTORY`: eligible winner triggers a saved, authoritative win resolution and final screen.
- `ENDLESS_SANDBOX`: identical milestones may be recorded in civilization/world history, **but no forced world ending or map wipe**.
- Both independent of D-013 size choice; separate victory milestone evaluation from `SessionConclusionPolicy`.

Proposed typed evaluator interface: `VictoryRule` takes immutable snapshot with civilization ID, territory, cities, research, finance, faith, wonders, timer; returns eligible/candidate/held/failed/confirmed events. One central `VictoryCoordinator` tracks per-civ per-rule, not shared global singleton progress.

### Essential decisions for later GDD
1. Which win conditions are enabled, by default or in configurable list? **Not yet approved.**
2. Should finite victory finish the world, freeze simulation or allow optional continue? User approved 'world limited by final goal'; post-win continuation is **undecided**.
3. Real-time days vs in-game days vs server-active time in 21-day hold windows; offline/singleplayer pause **not decided**.
4. When multiple civilizations satisfy different conditions at same tick, what tie breaker?
5. If no NPC civilization survives or there are zero rivals, should 'most votes/science' auto-win?
6. How to count AI voting vs player Resident vote; avoid hundreds of artificial NPC ballots.
7. World border and initial NPC faction population must make all enabled goals physically achievable.

## 6. Test contract / patch sequencing

- Deterministic start, reset and completion for each victory; persistence over restart.
- Conquest denominator explicitly documented, including native/occupied/lost civilization cases.
- Economy, religion and science calculated from one authoritative ledger (not duplicated virtual NPC outputs).
- All victory paths honor D-014; no unwitting reset in sandbox mode.
- Winner is an existing, eligible civilization; no winner caused by null rivals / missing wonder / disabled track.
- World size presets tested for impossible victory conditions, empty maps and edge settlements.
- P0 foundation may define/persist mode, but **must not advertise fully functional finite victory before supported victory rules ship**; patch dependency resolution is required in G1–G4 due sequential complete-patch policy.

## Open research
- Trace source `getPercentageConquered` and `declareAsWinner` and scheduler to establish precise victory domain.
- Retrieve historical VimeWorld victory article for a specific post-V1.4 snapshot, if available; wiki changelog suggests conflicting versions.
- Audit economic and religious victory pages independently, and Space Mission page in detail.
- Decide whether 'Military Victory' differs from Conquest/Domination in late wiki.
- Complete source licensing audit before any code copying.

**G0 OPEN.** No gameplay code written.

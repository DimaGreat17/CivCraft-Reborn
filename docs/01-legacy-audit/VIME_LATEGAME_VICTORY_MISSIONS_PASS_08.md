# G0 Pass 08 — VimeWorld Late-game Victory, Religion and Space Missions

**Inspection date:** 2026-11-18. **Source confidence:** historical VimeWorld wiki article verified for detailed conditions, and patch 1.4 notes for version-specific differences. No VimeWorld server runtime or later game source was inspected. These values are **not approved Reborn balance**.

## Sources and versions

- [Победа](https://civ-wiki.vimeworld.com/wiki/Победа), article with 6 later victory routes.
- [Космические миссии](https://civ-wiki.vimeworld.com/wiki/Космические_миссии), [permanent oldid=5762](https://civ-wiki.vimeworld.com/index.php?title=Космические_миссии&oldid=5762), last edit 2021-12-27.
- [Религия](https://civ-wiki.vimeworld.com/wiki/Религия).
- [Всемирный банк](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк).
- [CivCraft V1.4 notes, March 2019](https://civ-wiki.vimeworld.com/wiki/CivCraft_V1.4).
- [CivCraft V1.2 notes, December 2018](https://civ-wiki.vimeworld.com/wiki/CivCraft_V1.2).
- [Prior old-code victory audit](./VICTORY_CONDITIONS_PASS_07.md) for original four implemented Java victory types.

**Wiki page != a verified current server or a single consistent version.** Note 2019 patch notes and 2021 wiki pages differ.

## Main victory rules from the later 'Победа' article

| Route | Startup restriction | Required hold | Eligibility & continuity conditions |
|---|---|---|---|
| Religious | 21 days since phase start | 21 days | Religion at every daily tax evaluation, Notre Dame in uncaptured city, >=45,000 religion points on every daily tax check, first place in any religion |
| Economic | 21 days since phase start | 21 days | Max-level World Bank in uncaptured town |
| Diplomatic | 21 days since phase start | 21 days | Council of Eight wonder in uncaptured town and kept from destruction |
| Cultural | 21 days since phase start | 21 days | Burj Al Arab in uncaptured town; >=13,000,000 culture across native cities |
| Scientific | 21 days since phase start | 21 days | Space Shuttle specifically in capital for entire countdown, >=20,000 science points with native cities counted 100% and other cities 30%, final Warp mission to Trestellar complete |
| Domination | Not stated in this section | 14 days | During hold, own score >=9× second-ranked civilization, six world wonders |

[Source: Победа](https://civ-wiki.vimeworld.com/wiki/Победа). Purchased-city category excluded from wins in this historical ruleset.

**Terminology caution:** Domination as 9× competitor score is **not automatically equivalent** to old-code Conquest (percent of civilizations conquered); keep distinct identifiers. “Military victory” in earlier hall-of-fame lists remains a version-specific label, not a proven separate final condition.

## Economic victory: World Bank detail

[Source: Всемирный банк](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк)
- Footprint 4×4, capital cost 1,500,000 coins and 1,000,000 hammers for initial world wonder.
- Requires World Bank technology (Innovations), **Stock Exchange in all uncaptured cities** and Bank level 10 in the host city.
- Wonder limit 1, and page mentions max 2 **normal world wonders per city**.
- Five bank percentage tiers:

| Tier | Percentage | Coins to upgrade | Hammers | Tech named in article |
|---|---:|---:|---:|---|
| 1 | 200% | 1,000,000 | 100,000 | Всемирный банк |
| 2 | 250% | 2,500,000 | 200,000 | Радио |
| 3 | 300% | 5,000,000 | 300,000 | Биология |
| 4 | 350% | 10,000,000 | 500,000 | Криогеника |
| 5 | 400% | 15,000,000 | 1,000,000 | Not named |

- On wartime destruction, bank loses 1 tier and interrupts upgrade.
- **Open:** article does not explicitly specify precise calculation semantics of its bank percentage or every cancellation rule of an in-progress economic victory. Victory page states max tier and ownership.

## Religious victory and religious economy

[Sources: Победа](https://civ-wiki.vimeworld.com/wiki/Победа), [Религия](https://civ-wiki.vimeworld.com/wiki/Религия).
- Religion income is generated from buildings and prophet effects, influenced by happiness and wonders, accumulated centrally for civilization.
- Religion points can be reduced through espionage and warfare.
- Opening a religion uses eligibility conditions tied to science, culture, working city buildings, town count or Aztec nation.
- At tax collection, ranking by religion points changes efficacy of religion buffs. The detailed later religion article lists **7 religion options**.
- Religious victory needs **actual first place** in any religion, >=45,000 points at daily checks, active religion, and Notre Dame hosted in an uncaptured town.
- The 2019 V1.4 release notes originally listed **6 religions**, while the later wiki religion article lists **7** including Paganism. **VERSION-RLG-001**.
- Initial V1.4 notes describe religion buff `Atheism +250 science`; later religion article gives **+160**. **VERSION-RLG-002**.
- The original V1.4 notes show a different religion ranking attenuation formula (`100 - (15 + 5*rank)`) than the later article (`100 - (10 + 5*rank)`). **VERSION-RLG-003**.
- The initial V1.4 notes describe transition max **48 hours**, later religion article max **24 hours**. **VERSION-RLG-004**.
- Do **not** infer a stable unified faith balance from two revisions.

## Seven sequential Space Missions

[Source: Космические миссии](https://civ-wiki.vimeworld.com/wiki/Космические_миссии), oldid 5762.

Space Shuttle must first be crafted in the **Factory/Завод** from materials, then placed as a wonder; mission components also crafted in Factory. Missions start via shuttle after inserting required components into the shuttle container. Progress tracked via book, `/civ space progress` or shuttle UI.

| Order | Mission | Hammers | Science | One-time reward Mercury |
|---|---|---:|---:|---|
| 1 | Launch orbital satellite | 75,000 | 75,000 | 16 T1 |
| 2 | Moon flight | 150,000 | 150,000 | 32 T1 |
| 3 | Mars flight | 225,000 | 225,000 | 16 T2 |
| 4 | Satellite to Sun | 300,000 | 300,000 | 24 T2 + 8 T1 |
| 5 | To edge of Solar System | 375,000 | 375,000 | 32 T2 |
| 6 | Warp jump to Betelgeuse | 750,000 | 750,000 | 10 T3 |
| 7 | Warp jump to Trestellar | 1,125,000 | 1,125,000 | 18 T3 + 4 T2 |

**Total across seven missions: 3,000,000 hammers and 3,000,000 science**, excluding mission-component item requirements and shuttle crafting.

- Hammers contributed by native, non-conquered civilization cities; science contributed from the entire civilization (the *separate victory eligibility* science formula weighs cities differently).
- Space mission progress recalculated **once per minute** in the described version.
- The wiki lists **13 distinct rocket components**: satellite, fuel tanks, engines, landing module, orbital station, residential module, solar reflector, equipment, solar sail, provisions, warp core, warp engine, gondolas. Individual recipe lines appear on source page, but mission/component mapping is rendered as a flattened HTML table and requires a reliable cell-mapped transcription before implementation.
- Loss in war removes the last completed mission and resets current progress.
- Rewards from already-completed missions are **not** paid a second time after war resets.
- Shuttle destruction **interrupts victory** until a new shuttle is built.
- Later Victory page: shuttle must be in **capital**, not another town, throughout countdown.

## Reborn candidate systems — PROPOSED

- `VictoryRuleEvaluator` separated from `WorldSessionConclusionPolicy` for D-014 finite vs endless mode.
- `EndgamePrerequisiteGraph`: stock exchange coverage, host bank tier, wonder city ownership, religion ranks, native city culture, space mission state.
- `MissionDefinition` with `requiredComponents`, `workPoints`, `sciencePoints`, `rewardDefinition`, `rewardClaimed` and `rollbackRule`; use a resource ledger and idempotency keys.
- `VictoryHoldState`: per-civilization per-victory track, explicit timeline, failure causes, revision and persisted evidence.
- Resource authoritativeness for production: physical craft items, abstract science/work points and mission progress cannot share a mutable stack representation.
- AI must obey exactly the same mission/component costs and eligibility rules as player civilizations. No virtual free items/instant mission completion.
- Timers should be specified in **in-game days, active-server time or elapsed real time**; the wiki server's real-time schedule is not automatically suitable for singleplayer.
- 10k/15k/25k bordered worlds can contain all three victory systems without requiring unlimited map area; performance depends on budgets and number of factions, not area setting alone.

## Verification gates

1. Finite and endless modes: same eligibility, but only finite mode resolves world; sandbox logs historical achievement.
2. Economic upgrade: atomic bank upgrade, prerequisites checked after occupation/destruction, hold interrupted according to approved policy.
3. Religious victory: religion active, daily balance/rank, wonder presence, no double counting religion or owner territories.
4. Space missions: seven ordered stages; once-only rewards even after war reset; progress persists through unload/restart; no item duplication.
5. Same-frame conflict between two victory types or factions deterministic, no contradictory winners.
6. A world with few or no rivals avoids auto-win due to rank comparison or absent denominator.
7. Ensure win conditions selected are achievable under each size preset and under NPC-government progression.
8. Source updates reconciled before balance freeze (especially VERSION-RLG-001..004).

## Outstanding verification

- Per-mission 13-component quantity matrix and exact item craft recipes, cross-checked with actual rendered wiki HTML table.
- Exact VimeWorld 2019→2021 sequence for religion/power shifts.
- Complete economic victory event hooks on World Bank destruction, occupation and upgrades.
- Confirm whether high-level science mission victory progression can be started solely by NPC civilization, and required AI planning.
- Verify exact last-changed revisions for World Bank and Victory pages.
- Research full six-victory matrix separately when G0 scope is closed.

**G0 is OPEN. No gameplay code created.**

# G0 Pass 09 — Cultural, Diplomatic and Domination Victory Comparison

**Audited:** 2026-11-18  
**Scope:** three later VimeWorld win tracks compared to original public Java code and archived config.  
**Evidence tiers:** OLD-CODE for Java + YAML, LATER-WIKI for individual wiki article and V0.9 patch notes, PROPOSED for Reborn architecture. Wiki data is historical, not live server-verifiable.

## Primary sources

- [VimeWorld Победа](https://civ-wiki.vimeworld.com/wiki/Победа): final late-wiki conditions and city statuses.
- [VimeWorld Культура](https://civ-wiki.vimeworld.com/wiki/Культура): culture levels and territorial influence.
- [Бурдж Аль Араб](https://civ-wiki.vimeworld.com/wiki/Бурдж_Аль_Араб): cultural victory wonder requirements/cost/effects.
- [CivCraft V0.9](https://civ-wiki.vimeworld.com/wiki/CivCraft_V0.9): Council of Eight changed to no vote + 4 weeks; Burj cultural threshold formerly raised to 12.5 million.
- [Old Culture Java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndConditionCulture.java)
- [Old Diplomacy Java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndConditionDiplomacy.java)
- [Old Conquest Java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndConditionConquest.java)
- [Old config](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft_data/data/civ.yml)
- [Old Civilization score + conquest fraction](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1419)

## Cross-version victory matrix

| Victory | Original code + config (OLD-CODE) | Later VimeWorld wiki (LATER-WIKI) | Difference |
|---|---|---|---|
| Culture | 3 **native/unconquered** towns each at culture level >= 10; >=1 wonder, remain unconquered; hold 21 days | Burj Al Arab in an **uncaptured** town; summed **native-town culture >=13,000,000**; starts after 21 days of phase, hold **21 days** | From several high-level towns to cumulative native-culture target + specific wonder |
| Diplomacy | Council of Eight wonder in native city; registered Resident voting with 24h per-voter cooldown; must have highest vote count at end; hold 21 days | Council of Eight in **uncaptured** town; if destroyed countdown interrupted; starts after 21 days, hold **21 days**, **no vote requirement** in later V0.9+ rules | Diplomacy's vote mechanism removed; modern version is defensive wonder hold |
| Conquest vs Domination | Conquest = 75% of counted civilizations or 50% with active Chichen Itza, start 28 days into original server, hold **21 days** | Domination = **score at least nine times second-place civ** AND **six world wonders** for **14 days** | Not the same victory; counts of conquered states != comparative score and wonders |

Do not treat 'military', 'domination' and 'conquest' as universally interchangeable across all wiki revisions.

## Later culture in detail

[VimeWorld Culture](https://civ-wiki.vimeworld.com/wiki/Культура) describes **11 culture levels**: level thresholds `0, 500, 10k, 20k, 100k, 250k, 500k, 1m, 2m, 4m, 6.5m`; listed influence extents **8, 10, 13, 15, 17, 19, 22, 26, 32, 40, 50 chunks**.

- Culture expands **influence**, not direct private building/interaction rights. Claims must still be recorded separately.
- Overlap may transfer influence to stronger culture; borders and own town footprints require a dedicated geographic policy.
- **Victory count:** later Победа explicitly counts ONLY towns with **Родной** status, and says this status can persist through revolution. Status **Присоединенный** and **Захваченный** are different and must not be silently added to winning score.
- Native-town culture summed to 13,000,000 is not synonymous with 13m total culture of all controlled towns.
- A later amendment [V0.9](https://civ-wiki.vimeworld.com/wiki/CivCraft_V0.9) changed the culture goal to 12.5m at that time; latest inspected Победа page displays **13m**. **VERSION-WIN-001**: versioned balance threshold.

### Burj Al Arab article evidence

From [Burj Al Arab](https://civ-wiki.vimeworld.com/wiki/Бурдж_Аль_Араб):
- 4×4 footprint; hotels in **all cities**; technology 'Burj Al Arab' in Art; **1,200,000 coins + 650,000 hammers**; 5,000 HP / regeneration 6 HP/s; listed limit 1.
- Required world wonder for cultural victory; civilization-wide **+12 happiness**, daily income **+10,000 × (number of civilizations in world − 2)** (wiki excludes itself and spawn/admin civ).
- On small maps or very few civilizations the written formula can yield zero/negative results; do **not** implement blindly. Its application to eliminated/captured civilizations remains unexplained. **WIN-UNKNOWN-001**.
- Wiki notes normal world wonder limit **2 per city**; compare prior national wonder exceptions.

## Later diplomacy in detail

[VimeWorld Победа](https://civ-wiki.vimeworld.com/wiki/Победа) says only a Council of Eight wonder in an uncaptured town, held for 21 days; it is interrupted upon destruction.

[V0.9 patch notes](https://civ-wiki.vimeworld.com/wiki/CivCraft_V0.9) specifically state:
- Council victory **no longer requires votes**, was set to **4 weeks**, and could start **2 weeks** after world start.
- Council gets defensive effects (punch immunity; protection against particular upgrade/talent).
- Upon capturing a city or civilization the Council is destroyed; wonder must be in native or annexed town in that revision.

Thus **VERSION-WIN-002**: 28-day held / 14-day start in V0.9 versus 21-day held / 21-day start in later Victory article. **VERSION-WIN-003**: earlier V0.9 accepts native/annexed for Council; later Победа states uncaptured. Need approval of intended semantics, not a silent 'correction'.

Old [EndConditionDiplomacy](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/endgame/EndConditionDiplomacy.java) stores raw votes from registered Resident records. Those residents are chiefly players, **not NPC inhabitants**. Hundreds of Reborn NPC must not each be granted an identical voting action by accident.

**GDD design question:** a purely wonder-based 'diplomatic victory' has little direct diplomacy. Should Reborn faithfully preserve the later rules, introduce vote approval by NPC-led sovereign states, or have a separate competition? User approval required.

## Later domination in detail

[VimeWorld Победа](https://civ-wiki.vimeworld.com/wiki/Победа): civilization score must remain **at least 9× the second-ranked civ**, and owning **6 world wonders**, continuously during **14 days**.

Old [Civilization.getScore](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1419-L1427) sums each owned town's score and `getTechScore()`. This is **only evidence of old code's score definition**, not proof that later score formula is unchanged; need trace `Town.getScore` and wiki `Очки Развития` for later version.

Old Conquest [getPercentageConquered](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/object/Civilization.java#L1635-L1655) counts active + conquered **civilizations** and counts own civilization + conquered capital civs held by this state in numerator. It is **not** geographical world-share.

If no runner-up civilization exists, or its score is zero/negative, the multiplication rule becomes degenerate. Rule for eligibility/minimum rival score must be defined explicitly. **WIN-UNKNOWN-002**.

War changes ownership, building/wonder destruction and city native status; victory score and timers require recomputation from authoritative snapshots after each relevant event.

## Player choice D-014 and world-size D-013

- **FINITE_VICTORY:** fulfilling any enabled winning track produces single persistent, authoritative terminal result and final screen. Multiple simultaneous wins need tie-resolution.
- **ENDLESS_SANDBOX:** same objective can be recorded as a historical milestone, but *must not* end world or disable autonomous factions. An achievement should not fire repeatedly after every tick.
- 10k / 15k / 25k boundaries are **area limits only**, not guaranteed city capacity. In 10k world, six wonders may be unreachable if AI founding, rare site placement and population caps do not permit sufficient development.
- Physical NPCs, abstract far-away simulation and concrete AI strategy should count score/culture/wonders through the **same** ledgers and rules as player cities.
- No exact mode default or set of enabled victory tracks has been approved.

## GDD test candidates

1. Culture victory sums only native towns, including native allegiance after revolution, ignoring occupied and annexed towns as specified.
2. Culture influence and claimed property remain separate; no off-border cultural claims/constructible footprints.
3. Diplomatic vote systems (if selected) use explicit actor population rules, one-vote accounting and eligibility; old code's player-based voters cannot be applied to each NPC blindly.
4. Destruction/conquest of Council or Burj during countdown invalidates victory only by documented rules selected for Reborn.
5. Domination ratio exactly evaluated against valid second-ranked civilization, with defined behavior on zero opponents and zero second score.
6. Counting six **world wonders** excludes national wonders if final design so specifies.
7. Victory holds survive restart and pause; no time manipulation in saved world from offline wall-clock drift.
8. Endless mode never triggers world shutdown after success; finite mode triggers exactly once.
9. AI uses no invisible vote/scoring/culture advantages and respects chosen border.
10. Data set versioned with named source revision, balancing user decisions and regression tests.

## Source and decision gaps

- `VERSION-WIN-001`: 12.5m V0.9 culture vs 13m later wiki.
- `VERSION-WIN-002`: 28-day Council hold / 14-day startup V0.9 vs later 21/21.
- `VERSION-WIN-003`: Council eligible city statuses differ across revisions.
- `WIN-UNKNOWN-001`: Burj scaling by world civilization count and low-population handling.
- `WIN-UNKNOWN-002`: no second civ / zero rival score for domination.
- `WIN-UNKNOWN-003`: Council individual wiki page not retrievable this pass; do not attribute its numeric costs or HP.
- `WIN-UNKNOWN-004`: whether later 'diplomatic' should mean victory-by-wonder or genuine diplomacy in Reborn.
- `WIN-UNKNOWN-005`: precise later score calculation differs from old code and needs independent source.
- `WIN-UNKNOWN-006`: old-vs-later culture victory eligibility, conquered/native definitions, wonder destruction events need a single versioned target design.

**G0 remains OPEN.** No mod/game code changed.

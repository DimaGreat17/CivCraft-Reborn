# VimeWorld CivCraft — Detailed Building Effects, Pass 01

**Evidence collected:** 2026-11-18. **Source:** individual pages of the historical VimeWorld Civ Wiki. **Evidence tier:** WIKI-ARTICLE-VERIFIED, not source-code or current-server verified. This report contains verbatim numeric facts paraphrased from separate wiki pages and explicitly flagged discrepancies.

## Production and technology buildings

| Building | Page | Footprint | Tech; prerequisite | Coins | Daily upkeep | Hammers | HP | Points | Limits in article |
|---|---|---|---|---:|---:|---:|---:|---:|---|
| Фабрика | [wiki](https://civ-wiki.vimeworld.com/wiki/Фабрика) | 3×2 | Инновации; no building | 100,000 | 15,000 | 15,000 | 1,000 | 5,000 | 2 per civilization |
| АЭС | [wiki](https://civ-wiki.vimeworld.com/wiki/АЭС) | 4×3 | Пластмассы; no building | 200,000 | 35,000 | 35,000 | 1,500 | 10,000 | **Conflict:** card says 1, text says max 2 per civilization |
| Лаборатория | [wiki](https://civ-wiki.vimeworld.com/wiki/Лаборатория) | 1×1 | Криогеника; Школа | 100,000 | 2,000 | 10,000 | 1,000 | 10,000 | 1 (scope not specified in card) |
| Мировой рынок | [wiki](https://civ-wiki.vimeworld.com/wiki/Мировой_рынок) | 1×1 | Коммерция; no building | 10,000 | 1,250 | 2,600 | 200 | 3,500 | 1 (scope not specified in card) |

### Exact documented effects (historical values)

**Фабрика**
- Source calls it a city structure tied to a strategic trade good: уголь (coal).
- Happiness modifier: **+6 unhappiness**.
- Hammers: **+400 + 200 × number of coal goods in city, counting no more than 2**.
- Income per hour: **+20,000 + 4,000 × number of coal goods in city** (article does not explicitly say the income term has the same 2-coal cap).
- Adds infrastructure bonus to Mine.
- Can build only within town claimed area; limit 2 per civilization.

**АЭС**
- Tied to strategic good уран (uranium); occupies city building slot.
- Happiness modifier: **+7 unhappiness**.
- Hammers: **+500 + 300 × number of uranium goods in city, counting max 2**.
- Income per hour: **+30,000 + 7,500 × number of uranium goods in city** (income cap not explicitly stated).
- Tesla Tower and Tesla Ship deal **+25% damage**.
- Destroying station by command or in war causes city to lose **75% of vegetation for 48 hours** (the wiki's referenced underlying 'vegetation/growth' stat semantics must be checked).
- Adds Mine infrastructure bonus; construction inside city protection required.
- **Wiki discrepancy:** infobox `Ограничение: 1` vs bullet `no more than 2 NPP per civilization`. Neither is silently chosen.

**Лаборатория**
- Requires School.
- Science **+200** (page does not specify periodicity in effect row).
- Additional science per resident, capped at **10 residents**; per-resident numeric coefficient is not given.
- Technology research coin cost **−5%**, stacks to maximum **−25%**.
- Important: this is a discount on **coin cost**, not a reduction of required beakers.

**Мировой рынок**
- Global commodity prices connected across server; item demand and supply change price.
- Every eighth purchase increases item price by **128**; every eighth sale lowers by **128**.
- Sale price never drops below **64 coins**.
- Wiki states difference between purchase and sale prices as **100%**; the precise price/spread semantics cannot be safely inferred without running the system.
- Adds **+1 happiness**.

## National and world wonders

| Wonder | Page | Footprint | Prerequisites | Coins | Hammers | HP | Points | Limit |
|---|---|---|---|---:|---:|---:|---:|---:|
| Национальный Колледж | [wiki](https://civ-wiki.vimeworld.com/wiki/Национальный_Колледж) | 4×4 | Производительность; Library in all cities | 650,000 | 450,000 | 2,500 | 112,000 | 1 |
| Эрмитаж | [wiki](https://civ-wiki.vimeworld.com/wiki/Эрмитаж) | 5×5 | Археология; Museum in all cities | 1,000,000 | 600,000 | 2,500 | 125,000 | 1 |
| Ост-Индская компания | [wiki](https://civ-wiki.vimeworld.com/wiki/Ост-Индская_компания) | 4×4 | Деньги; Market in all cities | 1,000,000 | 650,000 | 2,500 | 135,000 | 1 |
| Всемирный банк | [wiki](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк) | 4×4 | Всемирный банк (Инновации); Stock Exchange in all uncaptured cities, Bank level 10 in wonder's city | 1,500,000 | 1,000,000 | 5,000 | 500,000 | 1 |

**Национальный Колледж**
- Does not consume a normal wonder slot.
- Science **+15% per city**.
- Unlocks special enchantments: Невесомость II and Небесная кара I (specific listed prices on page).
- Coins per day **+100 per library in the world**; 'world' versus 'owned' library inclusion should not be assumed.

**Эрмитаж**
- Does not consume normal wonder slot.
- Culture **+30% per city**.
- Coins daily **+100 per monument in world**.

**Ост-Индская компания**
- Does not consume normal wonder slot.
- Income **150,000 × government trade modifier** if Market present in all cities.
- Income from Trade Ship, Bank and Cottage **+10% per city**.
- Wiki does **not** unambiguously specify frequency of its flat 150,000 income; do not label it hourly or daily without verification.

**Всемирный банк**
- World wonder, linked to **economic victory**.
- Describes upgrade tiers with bank percentage values **200%, 250%, 300%, 350%, 400%**.
- On war destruction level decreases by one and current upgrade is interrupted.
- Wiki says normal wonder cap **2 per city** in the article; national wonders explicitly do not consume a normal slot. Interpret world-wonder count together with a separate rules audit.

## Design and architecture implications — proposals, not approved rules

1. **Typed effects:** add typed `StatModifier` with explicit source, target, unit, frequency, threshold and stack/cap behavior.
2. **Trade resources:** separate actual stock items from special geographically controlled 'trade goods'; do not conjure coal/uranium from physical inventory assumptions.
3. **Economy:** price calculation should be deterministic and transactional; price floor/step are validated config values and must preserve consistency across server restarts.
4. **City infrastructure:** urban adjacency and civilization-wide requirements belong to independent calculators/caches, not the building class.
5. **Disasters:** AЭС destruction may emit an event generating a timed, persisted negative city modifier; no unloaded chunk editing required merely to simulate the effect.
6. **Education/research:** separate population-count contribution, flat science gain, percentage cost reductions and technology prerequisites, and define which are computed per tick/day.
7. **National wonder requirement:** when a building is destroyed/captured, all dependant civilization bonuses must be invalidated atomically.
8. **Autonomous NPCs:** their building/job tasks are new Reborn features; the VimeWiki pages do not prove these buildings employ physical NPC workers.
9. **Release strategy:** documentation for late buildings can be completed during G0–G4, but functionality only ships in a fully completed later patch.

## Evidence gaps, contradictions and incomplete retrieval

- Wiki page for **АЭС** contradicts itself on max count (1 vs 2); **OPEN CONFLICT WIKI-001**.
- Wiki School, University, Factory/Plant (`Завод`) and World University articles could not be retrieved in this pass; no numeric rules from those pages are asserted here.
- Effects marked `Наука +200` or `Молоточки +400` need verification of units and production period using shared mechanics guides.
- Later patch notes may supersede old wiki pages; audit revision history before accepting final numbers.
- Wonder-specific buffs and rare upgrade edge cases require separate page/behavior audit.

**G0 remains IN PROGRESS. No target code written.**

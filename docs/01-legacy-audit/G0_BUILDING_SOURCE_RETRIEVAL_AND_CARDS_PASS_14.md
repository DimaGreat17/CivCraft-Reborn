# G0 Pass 14 — Individual Later-VimeWorld Building Sources and High-Impact Card Semantics

**Date:** 2026-11-18. This source access pass **retrieved 42 of the 73 indexed individual VimeWorld building articles**, while **31 article requests failed or timed out** using the available wiki reader. Both [Capitol](https://civ-wiki.vimeworld.com/wiki/Капитолий) and [Town Hall](https://civ-wiki.vimeworld.com/wiki/Ратуша) articles were also obtained. Every indexed structure has a row in [the updated source retrieval ledger](./G0_73_BUILDING_COVERAGE_LEDGER.md), showing prior **selective-detail coverage** separately from access success/failure. An unavailable cache page is **not proof the original historical article is nonexistent**. The 42 retrieved pages are not equivalent to exhaustive effects review, and later VimeWorld runtime remains unavailable.

## A. Directly rechecked high-risk building card findings

| Building | Tech / footprint | Construction, upkeep and notable rule | Primary wiki |
|---|---|---|---|
| Bank / Банк | Religion; 1×1 | 2,250 coins + 1,200 hammers; 100 coins/day. Level 1–10 exchanges rates 40%→200%; foreign customer 5%–15% levy into town treasury. Four separate interest upgrades | [Bank](https://civ-wiki.vimeworld.com/wiki/Банк) |
| Forge / Кузница | Кузница; 1×2 | 32,000 coins + 1,500 hammers; 2,000/day; maximum 4 per civilization. Repairs, ore processing and copper-linked income/hammer boosts | [Forge](https://civ-wiki.vimeworld.com/wiki/Кузница) |
| Market / Рынок | Деньги; 2×2 | 50,000 coins + 2,000 hammers; 1,000/day. Wiki says it **does not use city building slot** and can be built without land protection/claim; trade income `(2500 + sum(base trade income)*4)*governmentTradeMultiplier`, plus bonuses | [Market](https://civ-wiki.vimeworld.com/wiki/Рынок) |
| Radio Tower / Радиовышка | Радио; 1×1 | 200,000 coins + 10,000 hammers; 10,000/day; +200 culture and −50% anarchy time per *resident* (legacy resident != NPC) | [Radio Tower](https://civ-wiki.vimeworld.com/wiki/Радиовышка) |
| Trade Ship / Торговый корабль | Навигация + Верфь; 1×2 | 50,000 coins + 2,000 hammers; 1,000/day. Consumes fish and apiary products stored in Outgoing chests to generate coins, culture, items, crystals in Incoming once/hour; pauses without inputs and loses 128 level-progress points on idle | [Trade Ship](https://civ-wiki.vimeworld.com/wiki/Торговый_корабль) |
| Tesla Tower / Башня Теслы | Электричество; 1×1 | 350,000 coins + 7,500 hammers; 4,500/day; 150-block combat range, attack interval 5 seconds, minimum range 10 blocks, lightning splash; nominal one-building limit | [Tesla Tower](https://civ-wiki.vimeworld.com/wiki/Башня_Теслы) |
| Arrow Tower / Башня лучников | Артиллерия; 1×1 | 50,000 coins + 2,500 hammers; 1,500/day; 100-block range, one shot/second, 5 damage ignoring armor; requires spacing/horizontal geometry | [Arrow Tower](https://civ-wiki.vimeworld.com/wiki/Башня_лучников) |
| Mine / Шахта | Производительность; 1×1 | 25,000 coins + 900 hammers; 1,500/day; hourly warehouse redstone consumption 10/15/20/25/30 yields 100/125/150/200/250 work points by level | [Mine](https://civ-wiki.vimeworld.com/wiki/Шахта) |
| Capitol / Капитолий | No tech; 4×4 | 125,000 coins + 600 hammers; eight control points, 4 built-in arrow towers, culture +50; military/research center | [Capitol](https://civ-wiki.vimeworld.com/wiki/Капитолий) |
| Town Hall / Ратуша | Settler required; 2×2 | 600 hammers; **50,000 coins given for relocation**, not necessarily original founding price; 6 control points; captured mayor cannot simply be replaced before capitulation | [Town Hall](https://civ-wiki.vimeworld.com/wiki/Ратуша) |

**Cost arithmetic stays source-specific:** Do not confuse base build, research, upgrades, relocating Town Hall, and tax/upkeep payments.

## B. Confirmed source-level contradictions and special cases

### BANK-CONFLICT-001 (HIGH)

[Bank](https://civ-wiki.vimeworld.com/wiki/Банк) has an *interest rate table* `0.0075%, 0.01%, 0.015%, 0.025%`, while its worked example explicitly calculates `100,000 × 0.01 = 1,000`, i.e. **1%**, not `0.01%`. Literal percent sign and formula are inconsistent by a factor of 100. Even the prior row 0.0075% may intend a decimal fraction 0.0075=0.75%. **Do NOT pick either silently in Reborn**: record both data interpretations and require owner balance decision or later source code proof.

### SOURCE-WAR-001: physical town hall/capitol control and sovereignty

[Capitol](https://civ-wiki.vimeworld.com/wiki/Капитолий) and [Town Hall](https://civ-wiki.vimeworld.com/wiki/Ратуша) show 8 vs 6 control blocks, with distinct military and occupation consequences; Town Hall explicitly says occupying state may set tribute without replacing the mayor. These must remain separate from ownership transfer/capitulation.

### SOURCE-TRADE-001: item transfer vs trade-site bonus

[Trade Ship](https://civ-wiki.vimeworld.com/wiki/Торговый_корабль) actually consumes food/fish/honey **physical inputs** and generates outputs, while [Trading-resource outposts](./VIME_AGRICULTURE_EXTRACTION_PASS_03.md) generate entitlements/bonuses. They are not identical production entities or an NPC cargo caravan. Ensure input/output chests remain authoritative and avoid item duplication during LOD.

### SOURCE-BUILD-001: buildable outside normal claimed city slots

[Market](https://civ-wiki.vimeworld.com/wiki/Рынок) documents construction without an owned plot and without consuming ordinary city slot, requiring an explicitly authorized special placement policy. **This is not carte blanche for NPCs to bypass claim permissions**.

### SOURCE-GOV-001: old player residence vs NPC population

[Radio Tower](https://civ-wiki.vimeworld.com/wiki/Радиовышка) uses anarchy reduction per “resident”, reflecting later server player roles. A Reborn city with 1000 NPCs cannot inherit that formula without deliberate balancing.

## C. Reproduction and remaining evidence limitations

- The retrieval manifest includes source availability for **all 73 wiki navigation entries**, but not complete source-verified economics, combat, production or versions for each one.
- The unavailable **31** include some critical modern and military pages (e.g. strengthened walls, several military ships, factory, education buildings, beekeeping, and information-age combat laboratory), so detailed effects cannot be guaranteed.
- Some other articles in prior reports were accessible through older cached results; failure today is not evidence of permanent unavailability.
- Exact upgrade costs and all effects for each retrieved page still require dedicated per-item extraction and a chosen target wiki version.
- No public later VimeWorld server source was used; no runtime correctness guarantees or G0 closure.
- [Full G0 review](./G0_COMPREHENSIVE_AUDIT_REVIEW_2026-11-18.md) is the governing assessment. The 19 selectively audited titles measure **prior written detail**, 42 retrieved pages measure **availability**, not completeness.

**G0 OPEN. No Minecraft mod implementation or legacy code copying.**

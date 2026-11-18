# VimeWorld CivCraft — Production, Science and Logistics Audit (Pass 02)

**Date of inspection:** 2026-11-18  
**Source:** Historical VimeWorld CivCraft wiki, individual articles.  
**Evidence level:** WIKI-ARTICLE-VERIFIED unless otherwise flagged. No private VimeWorld source code or executable server was inspected. Numeric values are historical observations, **not approved Reborn balance**.

## 1. Source register / provenance

| ID | Page | Evidence status | Source |
|---|---|---|---|
| VS-201 | Склад (Warehouse) | ARTICLE-VERIFIED; historical revision `oldid=3554` | https://civ-wiki.vimeworld.com/wiki/Склад |
| VS-202 | Шахта (Mine) | ARTICLE-VERIFIED | https://civ-wiki.vimeworld.com/wiki/Шахта |
| VS-203 | Амбар (Granary) | ARTICLE-VERIFIED | https://civ-wiki.vimeworld.com/wiki/Амбар |
| VS-204 | Лесопилка (Sawmill) | ARTICLE-VERIFIED | https://civ-wiki.vimeworld.com/wiki/Лесопилка |
| VS-205 | Дорожная станция (Road Station) | ARTICLE-VERIFIED | https://civ-wiki.vimeworld.com/wiki/Дорожная_станция |
| VS-206 | Арсенал (Arsenal) | ARTICLE-VERIFIED | https://civ-wiki.vimeworld.com/wiki/Арсенал |
| VS-207 | Молоточки (Hammers) | ARTICLE-VERIFIED | https://civ-wiki.vimeworld.com/wiki/Молоточки |
| VS-208 | Наука (Science) | ARTICLE-VERIFIED | https://civ-wiki.vimeworld.com/wiki/Наука |
| VS-209 | Школа / Университет / Завод | INDEX-MENTIONED ONLY, article page retrieval failed | https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница |
| VS-210 | Новости обновлений | ARTICLE-VERIFIED; revisions must be reviewed separately | https://civ-wiki.vimeworld.com/wiki/Новости_обновлений |

## 2. Construction and upkeep inventory

| Building | Era (index) | Base tech | Cost (coins) | Upkeep / day | Hammers to build | HP | Footprint | Count limit shown |
|---|---|---|---:|---:|---:|---:|---|---|
| Склад | Stone | Керамика | 15,000 | 1,500 | 700 | 300 | 1×1 | 1 |
| Шахта | Stone | Производительность | 25,000 | 1,500 | 900 | 300 | 1×1 | Unrestricted |
| Амбар | Stone | Керамика | 2,500 | 1,500 | 500 | 500 | 1×1 | 3 |
| Лесопилка | Stone | Загонная охота | 40,000 | 800 | 1,000 | 500 | 1×1 | 1 |
| Дорожная станция | Renaissance | Машиностроение | 175,000 | 1,500 | 4,750 | 1,000 | 1×2 | 1 |
| Арсенал | Stone | Архитектура + Бараки | 25,000 | 1,000 | 2,500 | 500 | 2×2 | 1 |

**Caution:** The wiki's 'count limit' column does not always state the scope (town vs civilization). Do not assume its scope for Reborn without further confirmation.

## 3. Confirmed production chain: Warehouse → Mines → City hammers

Sources: [Warehouse](https://civ-wiki.vimeworld.com/wiki/Склад), [Mine](https://civ-wiki.vimeworld.com/wiki/Шахта).

- Warehouse has **8 double chests** for redstone; mines withdraw this redstone **hourly** and generate city hammer output and advancement points.
- The mine occupies a settlement building slot. Its upgrade process is time-based; wartime destruction decreases its level by 1.
- Mine level table from the article:

| Level | Redstone / hour | Hammers | Improvement time to next level (wiki) |
|---|---:|---:|---|
| 1 | 10 | 100 | 24 hours |
| 2 | 15 | 125 | 48 hours |
| 3 | 20 | 150 | 96 hours |
| 4 | 25 | 200 | 120 hours |
| 5 | 30 | 250 | 144 hours |

- Mine adds **+0.5 unhappiness** in the article.
- Infrastructure bonus can be earned from buildings within 100 blocks; details belong to infrastructure article.

**Important interpretation boundary:** The cited article calls these outputs `молоточки` (abstract production points), **not inventories of physical hammer items**. Reborn may implement worker NPCs physically, but must preserve this conceptual distinction until the redesign is approved.

**Reborn proposal, not source fact:** A single ResourceReservationService handles redstone payment, city production credit and partial failure on shortages; virtual mines update the same ledger as loaded physical mines.

## 4. Confirmed production chain: Granary → Cottages → Resources, City Coins

Source: [Granary](https://civ-wiki.vimeworld.com/wiki/Амбар).

- Granary has **two double chests** with distinct roles: one takes food inputs, the other receives valuables from cottages.
- Each hour cottages consume suitable food, progress their levels, deposit valuables and pay city treasury. In anarchy the description lists an exception to the normal operation.
- Listed food: bread, carrots, potatoes; raw fish allowed with `Fishing` trade-good effect; raw pork at cottage levels 5–6.
- Upgraded Granary converts iron/gold/diamond/emerald valuables into blocks for the town.
- Granary level-2 upgrade: **500,000 coins**, **50,000 hammers**, prerequisite `Конвейерная сборка`.

**Reborn proposal:** Define input/output inventories, a cottage operation record, transactions, no-duplication invariant and anarchy/insufficient-food pause behavior, with separate world storage adapters.

## 5. Confirmed chain: Sawmill tools → durability → biome-weighted wood products

Source: [Sawmill](https://civ-wiki.vimeworld.com/wiki/Лесопилка).

- Sawmill has **two chests**: axes supplied by players and produced wood.
- **Once per 8 seconds**, it consumes **one durability point** of an axe and rolls output.
- Surrounding biome coefficients determine wood-product roll quantity. Biome coefficient examples: winter forest 1; forest 1; taiga 1.5; jungle 1.5; swamp 0.7; savanna 0.9; mushroom 0.3; field 0.2.
- Reported formula: arithmetic average of biome coefficients + count of biome samples / 500 (exact sample geometry and rounding require follow-up).
- Drop roll bases listed by wiki: ordinary wood 20%, T1 wood 5%, T2 wood 1%.
- Upgrade levels:

| Level | Tech prerequisite | Upgrade coins | Upgrade hammers | Sampling radius | Required axe |
|---|---|---:|---:|---|---|
| 1 | Загонная охота | — | — | 3 chunks | Gold |
| 2 | Налоги | 20,000 | 2,000 | 4 chunks | Stone |
| 3 | Эффективность | 30,000 | 3,000 | 5 chunks | Iron |
| 4 | Инженерия | 40,000 | 4,000 | 6 chunks | Diamond |

**Reborn proposal:** Keep spatial biome sampling deterministic and cached; tick processing only when tool durability and output capacity are available. Reconcile NPC woodcutting vs legacy automatic sawmill yield to avoid duplicate resource production.

## 6. Science and construction-point semantics

Sources: [Science](https://civ-wiki.vimeworld.com/wiki/Наука), [Hammers](https://civ-wiki.vimeworld.com/wiki/Молоточки).

- Hammers are **abstract labor/production points**, accumulate as city production and are used for buildings, wonders and units.
- The wiki states one city’s hammer output is spent over **half an hour**; a city with 100 hammers completes a 1,000-hammer building in **5 hours** in its worked example.
- One ordinary building and one wonder may be constructed concurrently per city in that described version.
- Science points are produced hourly by cities, modified by biome, science buildings, trade resources and other bonuses.
- From taxable income, a civilization may dedicate a chosen percentage to purchasing science. Wiki states **17.5 coins per science**, or **20 under Mercantilism**; discounts may apply.
- A city may convert hammers into science using command `/town chammers`.
- Wiki prints a technology cost multiplier formula: `1 + (tech era + civilization era / 10) * 0.11`. The written precedence is recorded **exactly as shown**; do not silently interpret or rewrite parentheses.
- Catch-up bonus: a civilization behind the leader by 2 eras gets **-10% coin/beaker cost**, 3 eras **-20%**, 4 eras **-30%**, etc.
- On switching active research, the spent technology coin cost is **not refunded**.

**Reborn proposal:** Use separately named `ConstructionWorkPoints`, `ResearchPoints`, `Currency` and physical `ItemStack`/resources. Do not call hammers an inventory item. Design NPC labor as a source of work points only if explicitly agreed, with one-authority accounting and transparent UI.

## 7. Logistics / travel and military supply

Source: [Road Station](https://civ-wiki.vimeworld.com/wiki/Дорожная_станция).
- Within own city claim, player travel speed **+35%**; in culture **+15%** for civilization members, excluding captured cities.
- Travel to own or allied road stations costs **5,000 coins** and has **5-minute cooldown**.
- Described effects concern members/player movement; **no evidence** that this building routes NPC caravans or physically moves material goods.

Source: [Arsenal](https://civ-wiki.vimeworld.com/wiki/Арсенал).
- Requires Barracks; provides consumables/ammunition/potions, equipment enhancement.
- Listed armor post-death durability preservation chance **+7.5%**, and war upkeep reduction **-10%**.
- Arsenal-level progression on page is internally odd: heading says levels `[1-4]` but its table lists **1–5**. **OPEN WIKI-CONFLICT-002**; record source ambiguity instead of selecting a limit.
- Buying arrows/potions is a shop/service effect, not confirmation of NPC production.

## 8. Source contradictions / unknowns

1. **WIKI-CONFLICT-002 — Arsenal upgrade range:** command `[1-4]` while tier table enumerates 1–5.
2. **Science tech multiplier:** parenthesis/operation precedence likely ambiguous in prose; require source confirmation before implementing.
3. **Sawmill sampling:** how 'count of biomes' is calculated and rounding are not fully defined.
4. **Mine and cottage pause behavior:** observed input requirement, but exact shortfall/no-output recovery and simultaneous work order details not verified by runtime.
5. **School, University and Factory/Plant:** main index identifies pages; direct article retrieval failed, so no unverified numeric effects were inferred.
6. **Previous audit conflicts:** WIKI-001 NPP limit contradiction remains unresolved.
7. **Historic revisions:** 2018–2022 VimeWorld patch notes include balance changes, and page revisions may differ; only a targeted version reconstruction can claim one consistent numerical balance set.

## 9. Design implications and next audit

- Distinguish **abstract production currencies** (hammers/beakers) from **physical inputs/outputs** (redstone, food, axes, wood).
- Build production as explicit, resumable recipes with one source of truth and input reservation before output creation.
- Define research and work-point timebase and server catch-up policy for unloaded chunks.
- Prevent warehouse/production scans that forcibly load chunks; cache inventory snapshots and reconcile on load.
- NPCs may transport inputs or perform work, but the legacy wiki described automated production — adding physical NPCs is a Reborn design choice.
- Continue with specific article audit of agriculture, industry, science buildings and exact legacy vs later implementation differences.

**Gate G0 remains IN PROGRESS. No game code created.**

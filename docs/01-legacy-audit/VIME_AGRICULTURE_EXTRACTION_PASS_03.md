# VimeWorld agriculture, extraction and food supply — Article audit pass 03

**Date:** 2026-11-18. **Evidence:** historical publicly available VimeWorld Civ Wiki articles; not VimeWorld runtime code. **Status:** WIKI-ARTICLE-VERIFIED unless explicitly otherwise. Values are historical, not Reborn design decisions.

## 1. Source register

- [Farm](https://civ-wiki.vimeworld.com/wiki/Ферма)
- [Fish Farm](https://civ-wiki.vimeworld.com/wiki/Рыбная_ферма)
- [Quarry](https://civ-wiki.vimeworld.com/wiki/Карьер)
- [Plantation](https://civ-wiki.vimeworld.com/wiki/Плантация)
- [Mine/Pit](https://civ-wiki.vimeworld.com/wiki/Рудник)
- [Granary](https://civ-wiki.vimeworld.com/wiki/Амбар)
- [Fishing Boat](https://civ-wiki.vimeworld.com/wiki/Рыболовное_судно)
- [Stoneworks](https://civ-wiki.vimeworld.com/wiki/Каменоломня)
- [Silkworm Farm](https://civ-wiki.vimeworld.com/wiki/Ферма_шелкопрядов)
- [Beekeeping](https://civ-wiki.vimeworld.com/wiki/Пчеловодство)
- [Trade resources](https://civ-wiki.vimeworld.com/wiki/Торговые_ресурсы)
- [Quick start](https://civ-wiki.vimeworld.com/wiki/Быстрый_старт)

Pages for Pasture, individual Apiary and Hive were not retrievable in this pass; do not infer their detailed costs.

## 2. Building cost ledger (article cards)

| Structure | Tech | Coins | Daily upkeep | Work-point cost | HP | Footprint | Limit as printed |
|---|---|---:|---:|---:|---:|---|---|
| Farm | Фермерство | 2,500 | 500 | 800 | 100 | 1×1 | Unlimited |
| Fish Farm | Механизмы | 10,000 | 5,000 | 5,000 | 100 | 1×1 | Unlimited |
| Quarry | Механизмы | 10,000 | 1,500 | 5,000 | 100 | 1×1 | 1 (scope unclear) |
| Plantation | Ферментация | 25,000 | 1,000 | 1,500 | 500 | 1×1 | Unlimited |
| Mine/Pit trade resource | Эффективность | 50,000 | 2,000 | 2,000 | 500 | 1×1 | Unlimited |
| Granary | Керамика | 2,500 | 1,500 | 500 | 500 | 1×1 | 3 (scope unclear) |
| Fishing Boat | Торговля + Верфь | 25,000 | 500 | 2,000 | 500 | 1×1 | Unlimited |
| Stoneworks | Машиностроение | 250,000 | 20,000 | 10,000 | 1,000 | 2×2 | 1 (scope unclear) |
| Silkworm Farm | Монополия | 35,000 | 5,000 | 2,500 | 800 | 1×2 | 1 (scope unclear) |

**Important names:** `Шахта` = work-point production consuming redstone (documented in previous pass); `Рудник` = trade-resource extraction, not an ore generator; `Карьер` = tool-durability-based item production. These must have different IDs and processing systems in Reborn.

## 3. Farm / food dependency

From [Farm](https://civ-wiki.vimeworld.com/wiki/Ферма):
- Wheat, potatoes, carrots and nether wart may grow **only on Farm chunks** in this documented server variant. This is *not vanilla Minecraft*.
- Crop growth is determined per city by buildings, wonders, government and culture; differs from vanilla growth.
- Needs light level **9** and water within **4 blocks**.
- Crop growth is allocated per **farm chunk**; multiple one-layer farms can outperform one multilayer farm.
- More planted crop instances in a chunk reduce the per-crop selection frequency.
- Pistons, dispensers and droppers operate only on Farm chunks in this server's rule set.
- Farm occupies a city building slot.

From [Granary](https://civ-wiki.vimeworld.com/wiki/Амбар): cottages take food hourly (bread, carrots, potatoes, conditional fish/pork), consume it, deposit valuables in output inventory and generate town income. Anarchy pauses the described ordinary operation. Prior pass covers upgrade mechanics.

**Open Reborn decision:** Whether to preserve restrictions on vanilla farming and machine blocks; these are server-specific rules and could harm an ordinary Minecraft survival experience. Do not mark them as accepted.

## 4. Fish Farm / tool wear / outputs

From [Fish Farm](https://civ-wiki.vimeworld.com/wiki/Рыбная_ферма):
- Input: fishing rods in single chests; output: fish and mercury in double chest.
- Fish type depends on biome and pool level.
- Full output inventory pauses production until capacity is freed.
- Lure adds output per tier and costs corresponding rod durability.
- Pool upgrades: pool 2 **25,000 coins / 5,000 work points / Commerce**; pool 3 **50,000 / 7,500 / Monopoly**; pool 4 **100,000 / 15,000 / Machinery**.
- Fish feed the Trade Ship economy; neither the fishing rod durability loss per ordinary action nor exact catch chances were confirmed in the page.

## 5. Quarry / pickaxe wear / loot

From [Quarry](https://civ-wiki.vimeworld.com/wiki/Карьер):
- Input pickaxes via container, output mineables into a double chest; full output chest pauses production.
- Listed drop chances include dirt **40%**, cobblestone **50%**, other stone **10%**, coal **5%**, iron **2%**, redstone **0.75%**, gold **0.01%**, diamonds/emeralds **0.009%**, tungsten **0.004%**, and an obsidian conditional **75%** during diamond outcomes.
- **These chances total more than 100%** because primary stone and bonus mineral drops appear to be separate rolls; exact probability structure not inferable from the wiki. Do **not** normalize into one drop table without verification.
- With Trommel/Rudopromyvochnaya-machine and Efficiency-enchanted pickaxe: 2 resource outcomes for 2 durability, effectively 2× processing compared to base claim.
- Guarana trade good halves dirt probability.
- Quarry levels 1–4 have reported multipliers **1.00, 1.05, 1.10, 1.15**. Upgrade requirements and exact costs need their own table when implementing.

## 6. Production outputs vs special trade-resource anchors

From [Plantation](https://civ-wiki.vimeworld.com/wiki/Плантация), [Mine/Pit](https://civ-wiki.vimeworld.com/wiki/Рудник), [Fishing Boat](https://civ-wiki.vimeworld.com/wiki/Рыболовное_судно), [Trade Resources](https://civ-wiki.vimeworld.com/wiki/Торговые_ресурсы):
- Plantation: terrestrial trade resource from a fixed world resource point; permission restricted to town mayor, advisors and civilization leaders; claims its chunk automatically, cannot simply unclaim.
- Mine/Pit: mineral/resource-point trade good; identical restrictions around protected claim and eligible players. **Not** a generic mine producing iron/coal as item stacks.
- Fishing Boat: aquatic trade resource from water point; requires Shipyard **in the same city**; automatically claims the chunk, and cannot just unclaim it.
- Trade goods are accounted for at town/civilization level, can be transferred/gifted and return to source outpost at expiration. Server-specific fixed Sunday 17:00 Moscow expiration is **not appropriate as a presumed single-player default**.
- Same-kind goods multiplicative income modifier: `1 + (min(count,4)-1)*0.5`; every individual unit of that good obtains the same modifier, per wiki description. Verify exact order with state/religion/building bonuses.
- Physical special-good pickup/expiration must not be confused with routine warehouse inventories and their item transport.

## 7. Stoneworks, silkworm, bees

- [Stoneworks](https://civ-wiki.vimeworld.com/wiki/Каменоломня): late production building **+7.5% city work points**, and adds Mine infrastructure bonus; it does **not** directly output stone item stacks according to this page.
- [Silkworm Farm](https://civ-wiki.vimeworld.com/wiki/Ферма_шелкопрядов): **8 potatoes every 2 seconds → 1 string**. The page says potatoes are withdrawn by stack or less and consumed virtually: specific implementation semantics unverified, so treat input reservation as a design requirement, not a faithful runtime claim.
- [Beekeeping](https://civ-wiki.vimeworld.com/wiki/Пчеловодство): an Apiary can hold up to **8 hives**, with levels supporting 2/4/6/8. Hives produce honey and additional wax/propolis/pollen at higher levels. Honey types/prices depend on the biome. Hives require proximity to a Farm measured in chunk X/Z; wiki prints squared distance **<=25**, and each hive grants **+25 vegetation up to +200** to its Farm.
- The wiki's description of distance and exact output units per time should be independently clarified: it does **not** define full production tick/latency for every hive output.

## 8. Reborn architectural implications (PROPOSED, not approved GDD)

1. Separate `CropGrowthPolicy`, `MachineRecipe`, `ToolWear`, `StorageCapacity`, `TradeResourceSite`, `TerritoryRights`, `CityWorkPoints`.
2. Resource-processing task: reserve inputs and output capacity → compute deterministic/sourced RNG → commit item+durability changes **once** → persist transaction ID → notify dependent system.
3. NPC harvesting must **not** trigger a second implicit scripted crop or resource yield for the same work; choosing actor/automation semantics requires design approval.
4. City hunger/food demand and storage shortages need explicit consumption and interruption rules; no infinite supplies for AI civilizations.
5. Rules tied to chunks/biomes must work in LOD without forcing chunks to load; cache site characteristics with invalidation on world modifications.
6. Keep outputs traceable: ordinary item (potato/stone/fish), abstract city production (work points), and transferable trade-good entitlement are distinct categories.
7. Model input-slot and output-slot capacity separately and stop on backpressure; test restarts/partial resources/tool breakage.
8. Capture and ownership changes affect whether production and trade-site bonuses remain active.

## 9. Ambiguities for later resolution

- Pasture, Hive and Apiary standalone building details unavailable via direct page retrieval; only Beekeeping overview is verified.
- Probabilities of Quarry bonus drops may be independent rather than exclusive; wiki alone does not establish exact algorithm.
- Fish Farm standard catches/rod durability and hive production time units undocumented on inspected pages.
- Wiki-vs-legacy version rules for farm-only growth and vanilla automation restrictions require a separate design choice.
- This audit does not prove NPCs existed or physically worked in VimeWorld CivCraft.
- Historical values might be superseded by VimeWorld later patch notes.

**G0 remains open.** No mod code is introduced in this audit.

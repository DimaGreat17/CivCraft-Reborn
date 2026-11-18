# Technology Dependency Graph — legacy source data

Source: [techs.yml](https://github.com/netizen539/civcraft/blob/master/civcraft_data/data/techs.yml). Runtime prerequisite semantics inspected in [ConfigTech.isAvailable](https://github.com/netizen539/civcraft/blob/master/civcraft/src/com/avrgaming/civcraft/config/ConfigTech.java#L91). Generated inventory on 2026-11-18. This is a **configuration inventory**, not live balancing validation.

- Technologies: **57**
- Prerequisite edges: **100**
- Unresolved prerequisite references: **0**
- Detected directed cycles: **0**
- Root technologies: **4**
- Dependency semantics: colon-separated, all prerequisites required.

All referenced prerequisites resolve to configured technology IDs.

## Full catalog

| Technology ID | Name | Coins | Beakers | Score | Prerequisites |
|---|---|---:|---:|---:|---|
| `tech_agriculture` | Agriculture | 5000.0 | 500.0 | 125 | — |
| `tech_armory` | Armory | 5000.0 | 500.0 | 125 | — |
| `tech_mining` | Mining | 5000.0 | 500.0 | 125 | — |
| `tech_religion` | Religion | 5000.0 | 500.0 | 125 | — |
| `tech_archery` | Archery | 7500.0 | 1000.0 | 250 | `tech_armory` |
| `tech_blacksmithing` | Blacksmithing | 7500.0 | 1000.0 | 250 | `tech_armory` |
| `tech_metal_casting` | Metal Casting | 7500.0 | 1000.0 | 250 | `tech_armory` |
| `tech_masonry` | Masonry | 7500.0 | 1000.0 | 250 | `tech_mining` |
| `tech_productivity` | Productivity | 7500.0 | 1000.0 | 250 | `tech_mining` |
| `tech_pottery` | Pottery | 7500.0 | 1000.0 | 250 | `tech_agriculture` |
| `tech_hunting` | Hunting | 7500.0 | 1000.0 | 250 | `tech_agriculture` |
| `tech_code_of_laws` | Code of Laws | 7500.0 | 1000.0 | 250 | `tech_religion` |
| `tech_brewing` | Brewing | 7500.0 | 1000.0 | 250 | `tech_religion` |
| `tech_advanced_masonry` | Advanced Masonry | 10000.0 | 2000.0 | 500 | `tech_masonry` |
| `tech_sailing` | Sailing | 10000.0 | 2000.0 | 500 | `tech_productivity` |
| `tech_automation` | Automation | 10000.0 | 2000.0 | 500 | `tech_pottery` |
| `tech_trade` | Trade | 10000.0 | 2000.0 | 500 | `tech_hunting` |
| `tech_writing` | Writing | 10000.0 | 2000.0 | 500 | `tech_code_of_laws` |
| `tech_priesthood` | Priesthood | 10000.0 | 2000.0 | 500 | `tech_brewing` |
| `tech_leather_refinement` | Leather Refinement | 15000.0 | 2500.0 | 625 | `tech_archery`, `tech_advanced_masonry`, `tech_automation` |
| `tech_fletching` | Fletching | 15000.0 | 2500.0 | 625 | `tech_archery`, `tech_advanced_masonry`, `tech_automation` |
| `tech_alloys` | Alloys | 15000.0 | 2500.0 | 625 | `tech_blacksmithing`, `tech_advanced_masonry`, `tech_automation` |
| `tech_sword_smithing` | Sword Smithing | 15000.0 | 2500.0 | 625 | `tech_metal_casting`, `tech_advanced_masonry`, `tech_automation` |
| `tech_artillery` | Artillery | 15000.0 | 2500.0 | 625 | `tech_advanced_masonry` |
| `tech_commerce` | Commerce | 15000.0 | 2500.0 | 625 | `tech_trade` |
| `tech_monarchy` | Monarchy | 15000.0 | 2500.0 | 625 | `tech_writing` |
| `tech_fermentation` | Fermentation | 15000.0 | 2500.0 | 625 | `tech_priesthood` |
| `tech_taxation` | Taxation | 40000.0 | 10000.0 | 2500 | `tech_sailing` |
| `tech_currency` | Currency | 40000.0 | 10000.0 | 2500 | `tech_monarchy` |
| `tech_efficiency` | Efficiency | 75000.0 | 20000.0 | 5000 | `tech_taxation` |
| `tech_global_trade` | Global Trade | 75000.0 | 20000.0 | 5000 | `tech_commerce` |
| `tech_economics` | Economics | 100000.0 | 20000.0 | 5000 | `tech_currency` |
| `tech_malting` | Malting | 75000.0 | 20000.0 | 5000 | `tech_fermentation` |
| `tech_tanning` | Tanning | 75000.0 | 17500.0 | 4375 | `tech_efficiency`, `tech_malting`, `tech_leather_refinement` |
| `tech_tillering` | Tillering | 75000.0 | 17500.0 | 4375 | `tech_efficiency`, `tech_malting`, `tech_fletching` |
| `tech_chemical_bonding` | Chemical Bonding | 75000.0 | 17500.0 | 4375 | `tech_efficiency`, `tech_malting`, `tech_alloys` |
| `tech_folded_steel` | Folded Steel | 75000.0 | 17500.0 | 4375 | `tech_efficiency`, `tech_malting`, `tech_sword_smithing` |
| `tech_advanced_artillery` | Advanced Artillery | 150000.0 | 35000.0 | 8750 | `tech_artillery` |
| `tech_machinery` | Machinery | 150000.0 | 35000.0 | 8750 | `tech_efficiency` |
| `tech_nationalism` | Nationalism | 300000.0 | 35000.0 | 8750 | `tech_economics` |
| `tech_alchemy` | Alchemy | 150000.0 | 35000.0 | 8750 | `tech_malting` |
| `tech_construction` | Construction | 1000000.0 | 70000.0 | 17500 | `tech_nationalism` |
| `tech_advanced_efficiency` | Advanced Efficiency | 150000.0 | 35000.0 | 8750 | `tech_machinery` |
| `tech_innovation` | Innovation | 1500000.0 | 200000.0 | 50000 | `tech_construction` |
| `tech_composites` | Composites | 2500000.0 | 175000.0 | 43750 | `tech_advanced_efficiency`, `tech_innovation`, `tech_tanning` |
| `tech_laminates` | Laminates | 2500000.0 | 175000.0 | 43750 | `tech_advanced_efficiency`, `tech_innovation`, `tech_tillering` |
| `tech_forging` | Forging | 2500000.0 | 175000.0 | 43750 | `tech_advanced_efficiency`, `tech_innovation`, `tech_chemical_bonding` |
| `tech_tempering` | Tempering | 2500000.0 | 175000.0 | 43750 | `tech_advanced_efficiency`, `tech_innovation`, `tech_folded_steel` |
| `tech_scientific_method` | Scientific Method | 2500000.0 | 350000.0 | 87500 | `tech_innovation` |
| `tech_enlightenment` | Enlightenment | 10000000.0 | 1500000.0 | 375000 | `tech_composites`, `tech_laminates`, `tech_forging`, `tech_tempering`, `tech_advanced_artillery`, `tech_advanced_efficiency`, `tech_scientific_method`, `tech_alchemy`, `tech_automation`, `tech_global_trade` |
| `tech_great_pyramid` | Great Pyramid | 75000.0 | 75000.0 | 18750 | `tech_sailing`, `tech_advanced_masonry`, `tech_automation` |
| `tech_hanging_gardens` | Hanging Gardens | 100000.0 | 100000.0 | 25000 | `tech_automation`, `tech_commerce` |
| `tech_colossus` | Colossus | 75000.0 | 75000.0 | 18750 | `tech_advanced_artillery`, `tech_global_trade`, `tech_nationalism` |
| `tech_chichen_itza` | Chichen Itza | 80000.0 | 80000.0 | 40000 | `tech_tanning`, `tech_tillering`, `tech_chemical_bonding`, `tech_folded_steel` |
| `tech_great_library` | Great Library | 50000.0 | 50000.0 | 12500 | `tech_advanced_artillery`, `tech_machinery` |
| `tech_notre_dame` | Notre Dame | 175000.0 | 175000.0 | 43750 | `tech_construction`, `tech_global_trade`, `tech_productivity` |
| `tech_council_of_eight` | Council of Eight | 175000.0 | 175000.0 | 43750 | `tech_alchemy`, `tech_global_trade`, `tech_scientific_method`, `tech_advanced_efficiency` |

## Porting rules

1. Use typed technology definitions and graph validation on data load; report missing edges/cycles clearly.
2. Do not change historical costs unless a design decision explicitly approves new balance.
3. Separate research initiation, treasury payment, beaker accrual and completion into independently tested services.
4. Keep wonder-unlocking technologies available for parity decisions, not silently remove them.
5. Verify old currency/beaker effects against the runtime and update the balance specification accordingly.

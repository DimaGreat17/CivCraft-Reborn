# G0 Pass 12 — late VimeWorld province, settlers, spying, events and exploration

**Reviewed 2026-11-18.** Historical wiki source, plus direct earlier old Java traces. WIKI-DOCUMENTED != later runtime verification. Differences are versioned and are **not** approved as Reborn balance. This pass complements [runtime pass 10](./G0_SOURCE_RUNTIME_AND_RISKS_PASS_10.md).

## 1. Province is a distinct progression state

[Провинция](https://civ-wiki.vimeworld.com/wiki/Провинция), originally introduced V0.7:
- Player needs **25,000 coins on hand** to found province; money is **not deducted**, according to article.
- Promote to full civilization after building **Capitol**, studying **Architecture / Trade / Legislation**, and reaching **culture level 3** in capital. It says civilization status is granted only on Capitol completion; exact event order/edge of tech/culture must be clarified.
- While province: **only one town**, cannot build world wonders or win, and cannot study medieval technologies except artillery-related and monarchy exceptions.
- Normally provinces war with provinces and civs with civs; after 7 days, established civilization may declare on an unpromoted province when civ is also at least 7 days old; province defeat can lead to burning of towns.
- **Reborn implication PROPOSED:** `Camp -> Province -> Civilization` should be considered a **real state progression** with explicit restrictions/transition policy and optional design adaptation, not an unconditional instant creation. NPCs must have same resource-based promotion requirements as players, if this feature is approved.

## 2. Settler, distance and founding

[Поселенец](https://civ-wiki.vimeworld.com/wiki/Поселенец):
- First cost **25k coins / 500 hammers**, subsequent each +25k coins while hammers remain same.
- New settlement must be at least **150 blocks from a Town Hall or Capitol**, using a survey preview. No culture boundary adjacency required; longer capital distance penalizes happiness.
- Leader approval required to add new town; sender must not currently be mayor/assistant of other town.
- Source [old Settler.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/items/units/Settler.java) uses PlayerInteractEvent, not an autonomous pathfinding citizen.

**D-013 question:** 150-block historic spacing must be considered alongside larger settlement footprints, resource availability and seeded AI positions in square 10k/15k/25k worlds. NPC strategic founding should use bounded placement/survey and reserve a valid spot before committing costly construction.

## 3. Espionage: full later-wiki mission table (8 recorded)

[Шпион](https://civ-wiki.vimeworld.com/wiki/Шпион). Spy unit trained at Barracks for **50k coins / 2500 hammers**, requires 10 minutes online for human player use. The mission is a **player-held special unit**, not a physical NPC from wiki.

| Mission | Coins | Duration | Required technology | Effect / cooldown | Enemy online threshold | Outside culture allowed |
|---|---:|---|---|---|---:|---|
| Отравление карьеров | 45 000 | 2 мин | Конвейерная сборка | 3–24 ч, cooldown 36 ч | 1 | да |
| Расследовать город | 10 000 | 30 с | Научный метод | Отчёт: город + наука/религия | 0 | нет |
| Воровство казны | 20 000 | 2 мин 20 с | Национализм | 20% городских денег, близость 50 блоков | 0 | нет |
| Отравление амбаров | 20 000 | 1 мин | Национализм | 2–24 ч, 5% -1 уровень коттеджей, cooldown 36 ч | 1 | да |
| Отравление улья | 17 500 | 1 мин | Национализм | 1–3 ч, cooldown 24 ч | 0 | да |
| Саботаж | 250 000 | 5 мин | Научный метод | Разрушить обычную структуру, не чудо; 50 блоков | 1 | да |
| Пиратство | 5 000 | 1 мин | Конвейерная сборка | Украсть торговый ресурс; 50 блоков | 0 | да |
| Инквизиция | 100 000 | 2 мин | Национализм | −200 религии; территория незахваченного города | 1 | нет |

- Pursuit/exposure starts at 0.1% per second; nearby players within 64 blocks add 0.2%/second; each scout tower/ship adds 0.5%/second. 40% reveals town; 80% coordinates; 90% identity and halves mission success chance (source wiki).
- Cannot spy in allied civilization culture. Online population affects chance, but excludes captured towns; later wiki counts players, not NPCs.
- Spy receives a written mission log; a dead spy drops evidence of sponsor/action.
- **Old YAML 7 spy mission IDs** vs **8 later wiki missions** = `SPY-CONFLICT-001`: not a 1:1 mission set; old wonder sabotage appears absent from this later table whereas hive/quarry/religious poisoning added.
- **Technical risk:** per-second remote NPC exposure and player proximity cannot load far-away chunks. LOD/disconnected agents must have deterministic abstract exposure calculations; no server-wide scan of online players for every spy.

## 4. Global events (11 confirmed wiki entries)

[Мировые события](https://civ-wiki.vimeworld.com/wiki/Мировые_события) states once weekly Tuesday at 12:00 Moscow, lasts 3 days (historic server schedule, not user-approved).

| No. | Global event | Historically listed main effects |
|---:|---|---|
| 1 | Великая депрессия | Продажи банков, коттеджи, рынки, торговля −10%; налог +5% |
| 2 | Жирные крысы | HP крыс +25%; дроп меркурия +50%; урон +4 |
| 3 | Культурный расцвет | Культура +20%; молоточки −10% |
| 4 | Научный расцвет | Наука +15%; цена исследований в монетах +15% |
| 5 | Перестройка | Молоточки +15%; постройки/чудеса в монетах +15% |
| 6 | Перемены | Время перехода государственного строя /2 |
| 7 | Пацифизм | Инициаторы войны +1 несчастья; мирные −1 |
| 8 | Мировые войны | Налог от войны −0.5; военные строения −25% в монетах/молоточках; военная наука −5% монет |
| 9 | Религиозный пост | Религия +25%; культура −10% |
| 10 | Цирк | Счастье +3; наука −5%; молоточки −5% |
| 11 | Блеск трудолюбия | Улучшения −15%, юниты −10%, исследования −5%; счастье −3, религия −25% |

**Important:** Old [RandomEventTimer.java](https://github.com/netizen539/civcraft/blob/b64fe5b99da7c531fcdb5799217bf1aac2d760e0/civcraft/src/com/avrgaming/civcraft/randomevents/RandomEventTimer.java) operates per **town**, selects among **five old config event IDs**, and contains a `Random.nextInt(1)` tie-break defect (see pass 10). Global events are not the same subsystem merely with more entries.

**Open GDD questions:** clock time vs in-game time, stacking/overlap, pause/restart behavior, multiplayer admin controls, order of multipliers, whether NPC civilizations can anticipate public world events, effect origin logging.

## 5. Ruins, scrolls and one-use artifacts

[Руины](https://civ-wiki.vimeworld.com/wiki/Руины):
- Generated in any biome; one “lucky block” per ruin. Can be destroyed.
- Rewards grouped into chance bands **90%, 70%, 50%, 30%, 3.82%, 2.15%, 0.66%** (per listed loot categories, not a proven disjoint probability distribution). Do not normalize these as a single roll without code.
- Reward categories: food/materials, weapons, tools, mercury, rare custom artifacts, enchantment recipes and scrolls.
- Technology scroll (up to 35% advancement for era 1/2), bank upgrade scroll, settler progress, city tier upgrade, culture and temporary work-point bonus. Each has rules for whether pre-existing purchase/ongoing work is necessary; code not available.
- One-use artifacts are another category, not a wonder or technology.

**Reborn design proposal:** loaded ruin generation gated by worldgen index, per-ruin unique reward claim ID, guarded single loot transaction, biome and border placement, no duplicate loot on unload/restart; NPC ruins exploration is optional, requires explicit rule.

## 6. Nations and diplomacy modifiers (later wiki)

[Нации](https://civ-wiki.vimeworld.com/wiki/Нации): nation chosen **once**, cannot change. Nation determines exclusive effects, exclusive buildings, special government transition reduction and unique wonder buffs; same-nation captured towns impose a different unhappiness penalty. Detailed seven-nation count and variants are already catalogued in [VIME_NATIONS_RELIGIONS_WONDERS.md](./VIME_NATIONS_RELIGIONS_WONDERS.md). Avoid treating nation, government, religion, capital city and political sovereignty as one field.

## 7. Dependency implications / tests

- Province eligibility and Settler expense/distance constraints must be checked by the same authority for player and AI.
- Triggers require persistent event ID for capture, failed spy mission, completed trade/resource operation and ruined loot to prevent duplication.
- Do not port time-of-day server events / online-player formulas into pauseable singleplayer without design decisions.
- Policy effect engine needs provenance, percentage precedence, exclusions, scope and expiration.
- World presets must place viable biome distribution, safe spawn and trade/ruin resources without force-loading all 2.4m chunks in a 25k map.
- Ruins, espionage and province defeat can alter diplomacy, economy or city culture; every action must publish a coherent, recoverable state transition.

## 8. Evidence gaps not disguised as complete

- Current VimeWorld mission handler code not publicly verified; numerical probabilities and eight mission effects only documented by wiki.
- Full item-level ruin loot and rare artifact probabilities depend on special item recipes not audited in runtime.
- Distinct list of all historical nation-only buildings and every government modifier still requires separate source-by-source extraction.
- Old source vs Vime wiki worldgen, population and province transitions differ by version.
- No whole-world NPC performance benchmark or 10k/15k/25k procedural generation test exists.

**G0 remains OPEN. No gameplay implementation.**

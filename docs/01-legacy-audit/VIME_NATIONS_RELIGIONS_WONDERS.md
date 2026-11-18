# VimeWorld CivCraft — Nations, Religions, Wonders (Wiki Audit)

**Checked:** 2026-11-18. **Provenance:** [Nations](https://civ-wiki.vimeworld.com/wiki/Нации) (page last edited 2022-02-09), [Religion](https://civ-wiki.vimeworld.com/wiki/Религия), [National Wonders](https://civ-wiki.vimeworld.com/wiki/Национальные_чудеса), [wiki index](https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница) (2022-04-22). **Evidence:** WIKI-DOCUMENTED, not code verified. Numbers below are historical server-version parameters, not approved Reborn balance.

## Seven nations

| Nation | Exclusive nation trait (selected example) | Government affinity | Examples of exclusive buildings |
|---|---|---|---|
| Ацтеки | Hive frame used twice; defensive tower cost reductions; jungle-dependent happiness | Монархия | Алтарь; Плавучие сады |
| Американцы | +3 building slots in each native town | Технократия | Кафе; Кинотеатр |
| Греки | Doubled education bonuses from science buildings | Меркантилизм | Академия; Амфитеатр |
| Египтяне | Desert chunks boost hammers, desert cities grant happiness | Республика | Гробница |
| Турки | Fleet bonuses and cheaper sea buildings | Коммунизм | Мечеть; Крепость |
| Французы | -5% capital culture requirements, -25% change-government time | Теократия | Шато |
| Японцы | River/ocean chunks improve hammers; culture buildings gain era-based bonus | Деспотизм | Додзё; Пагода; Рисовое поле |

Rules explicitly described in the nations page:
- Nation choice is one-time on civilization creation and cannot be changed.
- City inherits the nation's identity and effects.
- National government affinity reduces transition time 25%.
- Capturing a same-nation town incurs less unhappiness than cross-nation capture.
- Exclusive buildings, nation-specific bonus modifiers and wonder synergies are distinct from government effects.

Do not confuse nationality with player ethnicity. This is a **fictional game faction selection**.

## Seven religion choices

Source: [Религия](https://civ-wiki.vimeworld.com/wiki/Религия). The wiki lists distinct benefits and unlocking / losing conditions:

| Religion | Unlock condition in wiki | Principal effect | Loss condition overview |
|---|---|---|---|
| Атеизм | At least 1,500 science | +160 science per town | Enough science lost from uncaptured towns through war |
| Буддизм | At least 100,000 culture in uncaptured cities | +30% culture to towns | ≥25% culture loss in war |
| Ислам | At least six towns | +50% Punch activation; cottage no longer consumes raw pork | Loss of 50% towns after war |
| Католицизм | At least five active trommels in uncaptured towns | +40% trommel efficiency | Active uncaptured trommels fall below foundation condition |
| Православие | At least five towns | -2 unhappiness in towns | Capital unhappiness rises by at least 6 after war |
| Протестантизм | At least five active cottages in uncaptured towns | +60% cottage income | Active uncaptured cottages drop below foundation condition |
| Язычество | Aztec nation | No captured-town unhappiness or captured-town science debuff | Capitol captured |

Additional wiki rules:
- Buildings and prophets produce religion points; income is accumulated for civilization each minute.
- Founding cost: `10000 * (1 + 0.075 * numberOfFoundedTowns)` coins.
- Civilization religion rank modifies benefit strength, calculated at tax time.
- A religion switch has a paid transition and temporary pause of religion-point accrual.
- War and espionage may affect religion points/eligibility.

**Design consequence (PROPOSED):** implement `ReligionDefinition`, `ReligionPolicy`, `FaithPointLedger`, `ReligionTransition`, `ReligionRankEffect` and a generic `RequirementEvaluator` separate from nation and government. Never assume religious bonuses are permanent.

## Wonders: category inventories from wiki navigation

The wiki home page lists **22 world-wonder article links** (counting only items in the world-wonders list, not national wonders):
Ангкор-Ват, Александрийская библиотека, Александрийский маяк, Бурдж Аль Араб, Висячие сады, Всемирный банк, Всемирный университет, Глобус (Театр), Древо Жизни, Замок Химедзи, Замок Нойшванштайн, Колосс Родосский, Космический Шаттл, Нотр-Дам-де-Пари, Парфенон, Пирамида Хеопса, Совет Восьми, Статуя Зевса, Статуя Свободы, Флагман Ingermanland, Чичен-Ица, Эйфелева башня.

The wiki also lists **6 national wonders**:
Колизей, Литейный Завод, Международный Порт, Национальный Колледж, Ост-Индская компания, Эрмитаж.

**Limit:** This is an index-based article inventory; each wonder's effects, tech requirements, unique slot behavior, construction constraints, upgrade paths and nation synergies still require article-by-article auditing.

National wonders [source](https://civ-wiki.vimeworld.com/wiki/Национальные_чудеса) are enabled by building requirements in every native/uncaptured city; requirements must continue to be satisfied for benefits to remain active. They do not consume the ordinary wonder slot.

## Architecture implications — candidate only

- Source-tagged, data-driven nation/government/religion/talent/wonder definitions, not an enum with one big switch.
- Typed modifiers with target, additive/multiplicative semantics, origin, duration, eligible territories, stacking and precedence.
- Distributed prerequisite indexes: a destroyed/occupied building may disable a national-wonder benefit across an entire civilization. Incremental updates preferred.
- Deterministic AI nation and religion eligibility/selection with exactly the same mechanics as players.
- Versioned data and migration: one-time nation choice and temporary faith transition must survive reload.
- No real-world religious representation is implied or endorsed; replicate only the documented game rules subject to explicit scope approval.

## Outstanding
- Open all 28 individual wonder article pages and extract effects/costs/prerequisites.
- Verify religious ranking scaling formula with examples and modifiers; never infer undocumented edge cases.
- Check precise government modifiers via separate wiki page, including update-log corrections.
- Audit full nation-exclusive building articles and compare with old CivCraft source.
- Resolve technical era tree from image-based source, distinguishing unreadable details and unverified connections.

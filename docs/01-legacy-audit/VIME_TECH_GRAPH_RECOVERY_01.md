# G0-A — VimeWorld Technology Graph Recovery, pass 01

**Inspection:** 2026-11-18. **Status:** PARTIAL, IMAGE BLOCKER. This report distinguishes **research-to-building unlock edges**, **building prerequisite edges**, and **technology-to-technology prerequisite edges**. They are not interchangeable.

## 1. Primary graph source and retrieval limitation

The late wiki [Дерево технологий](https://civ-wiki.vimeworld.com/wiki/Дерево_технологий) (page `oldid=5796`, visible last edit **2024-09-03**) embeds a large raster `Tree_1.9.2-rendered-s.png`. The wiki HTML does **not** provide a machine-readable list of technology nodes and dependency arrows. During this pass the image link could not be retrieved for reliable edge transcription. **Therefore no complete later technology graph, number of late technologies, cycle check or exact technology-to-technology prerequisites can yet be certified.**

The earlier original [TECHNOLOGY_GRAPH.md](./TECHNOLOGY_GRAPH.md) **is complete for the old configured graph**: 57 nodes, 100 prerequisite edges, no cycles, 4 roots; its metadata does not extend to the later VimeWorld graph.

## 2. Source-verified technology → unlock relationships (NOT research prerequisites)

| Later technology as named by source | Unlock or gate | Source | Evidence |
|---|---|---|---|
| Фермерство | Ферма | [Ферма](https://civ-wiki.vimeworld.com/wiki/Ферма) | WIKI ARTICLE |
| Механизмы | Рыбная ферма; Карьер | [Рыбная ферма](https://civ-wiki.vimeworld.com/wiki/Рыбная_ферма), [Карьер](https://civ-wiki.vimeworld.com/wiki/Карьер) | WIKI ARTICLE |
| Керамика | Амбар; Склад | [Амбар](https://civ-wiki.vimeworld.com/wiki/Амбар), [Склад](https://civ-wiki.vimeworld.com/wiki/Склад) | WIKI ARTICLE |
| Загонная охота | Лесопилка | [Лесопилка](https://civ-wiki.vimeworld.com/wiki/Лесопилка) | WIKI ARTICLE |
| Производительность | Шахта; Национальный Колледж | [Шахта](https://civ-wiki.vimeworld.com/wiki/Шахта), [Национальный Колледж](https://civ-wiki.vimeworld.com/wiki/Национальный_Колледж) | WIKI ARTICLE |
| Архитектура | Арсенал | [Арсенал](https://civ-wiki.vimeworld.com/wiki/Арсенал) | WIKI ARTICLE |
| Машиностроение | Дорожная станция; Каменоломня | [Дорожная станция](https://civ-wiki.vimeworld.com/wiki/Дорожная_станция), [Каменоломня](https://civ-wiki.vimeworld.com/wiki/Каменоломня) | WIKI ARTICLE |
| Торговля | Рыболовное судно | [Рыболовное судно](https://civ-wiki.vimeworld.com/wiki/Рыболовное_судно) | WIKI ARTICLE |
| Эффективность | Рудник | [Рудник](https://civ-wiki.vimeworld.com/wiki/Рудник) | WIKI ARTICLE |
| Ферментация | Плантация | [Плантация](https://civ-wiki.vimeworld.com/wiki/Плантация) | WIKI ARTICLE |
| Монополия | Ферма шелкопрядов | [Ферма шелкопрядов](https://civ-wiki.vimeworld.com/wiki/Ферма_шелкопрядов) | WIKI ARTICLE |
| Экономика | Фондовая Биржа | [Фондовая Биржа](https://civ-wiki.vimeworld.com/wiki/Фондовая_Биржа) | WIKI ARTICLE; manually reopened 2026-11-18 |
| Инновации / Всемирный банк | Всемирный банк | [Всемирный банк](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк) | WIKI ARTICLE; exact text `Всемирный банк (Инновации)` may indicate separate wonder technology, NOT a verified direct research edge |
| Криогеника | Лаборатория | [Лаборатория](https://civ-wiki.vimeworld.com/wiki/Лаборатория) | WIKI ARTICLE; requires School |
| Нотр-Дамм (Республика) | Нотр-Дам-де-Пари | [Нотр-Дам-де-Пари](https://civ-wiki.vimeworld.com/wiki/Нотр-Дам-де-Пари) | WIKI ARTICLE; category/parenthetical is NOT a verified AND edge |
| Археология | Эрмитаж | [Эрмитаж](https://civ-wiki.vimeworld.com/wiki/Эрмитаж) | WIKI ARTICLE |
| Деньги | Ост-Индская компания | [Ост-Индская компания](https://civ-wiki.vimeworld.com/wiki/Ост-Индская_компания) | WIKI ARTICLE |
| Радио | World Bank upgrade tier 2 | [Всемирный банк](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк) | WIKI ARTICLE |
| Биология | World Bank upgrade tier 3 | [Всемирный банк](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк) | WIKI ARTICLE |
| Криогеника | World Bank upgrade tier 4 | [Всемирный банк](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк) | WIKI ARTICLE |

The table is **not** a list of distinct technology nodes (duplicate rows and gate labels may refer to concept aliases), and does not demonstrate any arrow between technologies.

## 3. Verified non-tech prerequisite examples

| Target | Required building/condition | Source |
|---|---|---|
| Лаборатория | Школа | [Лаборатория](https://civ-wiki.vimeworld.com/wiki/Лаборатория) |
| Арсенал | Бараки | [Арсенал](https://civ-wiki.vimeworld.com/wiki/Арсенал) |
| Рыболовное судно | Верфь in same city | [Рыболовное судно](https://civ-wiki.vimeworld.com/wiki/Рыболовное_судно) |
| Национальный Колледж | Библиотека in all cities | [Национальный Колледж](https://civ-wiki.vimeworld.com/wiki/Национальный_Колледж) |
| Эрмитаж | Музей in all cities | [Эрмитаж](https://civ-wiki.vimeworld.com/wiki/Эрмитаж) |
| Ост-Индская компания | Рынок in all cities | [Ост-Индская компания](https://civ-wiki.vimeworld.com/wiki/Ост-Индская_компания) |
| Всемирный банк | Фондовая биржа in all noncaptured cities + Банк level 10 at wonder site | [Всемирный банк](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк) |

## 4. Why the distinction matters

- Research DAG edge: `requiresTechnology(A,B)` means researching A requires already researching B.
- Building gate edge: `requiresTechnology(Building,Tech)` means constructing a building requires a technology; **does not** establish any research DAG edge.
- Civic precondition: `requiresBuilding(Building,OtherBuilding,scope)` may depend on city or entire civilization and can become invalid after conquest.
- World wonder 'parenthesis' is source text, not a parsed reliable two-node tech dependency.

Constructing later research graph from item unlocks would invent dependencies.

## 5. Recovery task (blocking GAP-002)

1. Obtain the authentic `Tree_1.9.2-rendered-s.png` from the wiki or another legally accessible identical version and record SHA/oldid.
2. Read every node and arrow at full resolution; use uncertainty annotations for overlapping edges or unreadable labels.
3. Create technology IDs preserving source names/version, research prerequisites AND/OR semantics, costs/science, era and unlock references.
4. Validate node count, unknown references and cycles. Cross-check at least all victory-gated and P0–P3 technology paths with individual articles.
5. Any graph edge not readable or confirmed remains `UNKNOWN`, never inferred from chronological location on image.
6. User approves rule adaptations in GDD; source graph is not silently adopted as final Reborn progression.

**G0-A partial progress, GAP-002 OPEN. No mod code changed.**

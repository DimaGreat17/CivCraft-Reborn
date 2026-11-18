# VimeWorld building and improvement taxonomy — Wiki index pass

**Evidence:** [VimeWorld wiki navigation](https://civ-wiki.vimeworld.com/wiki/New_Заглавная_страница) (historical index, edited 2022-04-22). **Status:** WIKI-INDEX-VERIFIED only; full article effects and costs remain NOT AUDITED.

All category labels and items are transcribed from wiki navigation. Main city structures Capitol (Капитолий) and Town Hall (Ратуша) are listed separately. Nation-exclusive buildings are **not** exhaustively represented in this index and are tracked in the nations audit.

## Group summary

| Wiki category | Entries |
|---|---:|
| Военные сооружения / War | 13 |
| Первобытная эра / Buildings | 5 |
| Каменный век / Buildings | 11 |
| Средневековье / Buildings | 7 |
| Эра Ренессанса / Buildings | 8 |
| Паровой век / Buildings | 3 |
| Индустриальная эра / Buildings | 2 |
| Атомная эра / Buildings | 2 |
| Информационная эра / Buildings | 1 |
| Первобытная эра / Improvements | 1 |
| Каменный век / Improvements | 12 |
| Средневековье / Improvements | 3 |
| Эра Ренессанса / Improvements | 3 |
| Паровой век / Improvements | 1 |
| Атомная эра / Improvements | 1 |

**Index totals**: 73 entries across the categories above (including military structures and duplicates across *conceptual* categories if present); plus two city civic centers. These are **navigation entries**, not unique building implementation IDs.

## Военные сооружения / War

1. Башня разведчиков
2. Башня лучников
3. Башня пушкарей
4. Башня Теслы
5. Дирижабль
6. Корабль лучников
7. Корабль пушкарей
8. Корабль Теслы
9. Корабль разведчиков
10. Военный Лагерь
11. Военный Корабль
12. Стена
13. Укрепленная стена

## Первобытная эра / Buildings

1. Банк
2. Бараки
3. Дробилка
4. Монумент
5. Собор

## Каменный век / Buildings

1. Амбар
2. Арсенал
3. Бакалея
4. Библиотека
5. Верфь
6. Кузница
7. Склад
8. Маяк
9. Портал
10. Храм
11. Церковь

## Средневековье / Buildings

1. Арена
2. Канализация
3. Мировой рынок
4. Отель
5. Плавучие сады
6. Рынок
7. Университет

## Эра Ренессанса / Buildings

1. Дорожная станция
2. Пороховая мастерская
3. Рудопромывочная машина
4. Таверна
5. Торговый центр
6. Фондовая Биржа
7. Шато
8. Школа

## Паровой век / Buildings

1. Замок
2. Музей
3. Фабрика

## Индустриальная эра / Buildings

1. Радиовышка
2. Театр

## Атомная эра / Buildings

1. Военная база
2. Лаборатория

## Информационная эра / Buildings

1. Военная лаборатория

## Первобытная эра / Improvements

1. Ферма

## Каменный век / Improvements

1. Карьер
2. Коттедж
3. Лагерь
4. Лесопилка
5. Мельница
6. Монстр-ферма
7. Пасека
8. Пастбище
9. Рыбная ферма
10. Рыболовное судно
11. Улей
12. Шахта

## Средневековье / Improvements

1. Плантация
2. Рудник
3. Торговый корабль

## Эра Ренессанса / Improvements

1. Завод
2. Ферма шелкопрядов
3. Каменоломня

## Паровой век / Improvements

1. Буровая вышка

## Атомная эра / Improvements

1. АЭС

## Architecture and release dependencies (PROPOSED)

- Each structure must have unique stable `structure_id`, optional era, feature category, footprint/shape, resource cost, time/hammer cost, tech prerequisite, maintenance, eligibility rules and runtime effects.
- Separate permanent structures, tile improvements, naval/air war structures, and defense installations. The wiki mixes items under navigation headings.
- Do not treat headings as a complete catalog of eras; technologies must be audited from the separate tree and may unlock earlier/later structures out of strict chronology.
- Building construction must integrate with NPC job tickets and supply chains in Reborn, but worker NPC features are **our extension** and are not asserted as late-wiki content.
- One active development patch at a time; include full dependency closure of each selected feature.
- Add source revision metadata and per-building tests before design freeze.

## Next detail audit

Open each wiki article and extract exact prerequisites, limits, costs, hourly effects and upgrade mechanics; classify old-code parity, later-wiki differences, nation-exclusive behavior and future Reborn changes.

**G0 remains OPEN.**

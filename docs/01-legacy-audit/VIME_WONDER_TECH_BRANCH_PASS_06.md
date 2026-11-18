# G0 — VimeWorld wonder technology branch, source-image transcription (pass 06)

**Date:** 2026-11-18. **Primary evidence:** exact user-provided `image(20261118-070044).png` and its enlarged `tech_wonders.png` crop, visually inspected. Its footer labels `CivCraft V1.0.9 by VimeWorld`; it is not asserted identical to later wiki `Tree_1.9.2-rendered-s.png`. **Status:** image transcription; not verified against VimeWorld server runtime. [Prior visual inventory](./VIME_TECH_GRAPH_VISUAL_TRANSCRIPTION_02.md), [interbranch links](./VIME_TECH_GRAPH_INTERBRANCH_EDGES_PASS_05.md).

## Most important rule — from the diagram legend

**The yellow C5 wonder path is NOT a sequential chain of research prerequisites.** The image legend says (Russian, in meaning): *For researching technologies in this branch, previous ones need not be studied*. Each dotted yellow branch lists a wonder-specific gate beneath its label. Visual proximity on the dotted line does **not** create `Wonder A → Wonder B` edges.

This is distinct from D-015, where ordinary technology branches require previous AND cross-branch prerequisites. D-015 therefore **does not impose sequential wonder-to-wonder progression**. Wonder research can require many ordinary prerequisites without requiring preceding wonders.

## Source-image matrix

**Legend:** cost pair reproduced as the **two numbers shown above the wonder technology**, in the order shown; source footer associates tech study with money and science, but confirm units/rounding before importing. Unclear figures are deliberately omitted. `No printed requirement` means the diagram lacks legible small-text prerequisites, **not** that the wonder is freely available.

| Audit ID | Wonder research label | Ordinary research prerequisite(s) printed beneath | Yellow track | Printed two-number pair | Evidence note |
|---|---|---|---|---|---|
| W-01 | Парфенон | Церковь; Торговля | TOP | 75 000 / 75 000 | IMAGE-READ |
| W-02 | Пирамида Хеопса | Архитектура; Механизмы; Мореплавание | TOP | 75 000 / 75 000 | IMAGE-READ |
| W-03 | Висячие сады | Коммерция; Механизмы | TOP | 75 000 / 75 000 | IMAGE-READ |
| W-04 | Чичен-Ица | Все исследования, открывающие T3 | TOP | 75 000 / 75 000 | IMAGE-READ; RULE-GROUP |
| W-05 | Глобус (Театр) | Национализм; Инженерия | TOP | 100 000 / 100 000 | IMAGE-READ |
| W-06 | Эйфелева башня | Радио | TOP | 150 000 / 150 000 | IMAGE-READ |
| W-07 | Бурдж Аль Араб | Искусство | TOP | 125 000 / 125 000 | IMAGE-READ |
| W-08 | Статуя свободы | Химия композитов; Сплавы металлов | TOP | 250 000 / 250 000 | IMAGE-READ |
| W-09 | Статуя Зевса | Архитектура; Механизмы; Литье металлов | MIDDLE | Неуверенно читается | COST-UNVERIFIED |
| W-10 | Колосс Родосский | Национализм; Баллистика | MIDDLE | Неуверенно читается | COST-UNVERIFIED |
| W-11 | Нотр-Дам-де-Пари | Республика | MIDDLE | 200 000 / 200 000 | IMAGE-READ |
| W-12 | Космический шаттл | Не указано под названием | MIDDLE | Неуверенно читается | NO-PRINTED-REQ |
| W-13 | Всемирный университет | Республика; Биология | MIDDLE | Неуверенно читается | COST-UNVERIFIED |
| W-14 | Совет Восьми | Электричество | MIDDLE | 600 000 / 600 000 | IMAGE-READ |
| W-15 | Древо жизни | Производительность; Загонная охота | BOTTOM | Неуверенно читается | COST-UNVERIFIED |
| W-16 | Ангкор-Ват | Образование; Инженерия | BOTTOM | Неуверенно читается | COST-UNVERIFIED |
| W-17 | Александрийский маяк | Инженерия; Экономика | BOTTOM | 75 000 / 75 000 | IMAGE-READ |
| W-18 | Александрийская библиотека | Инженерия; Алхимия | BOTTOM | 50 000 / 50 000 | IMAGE-READ |
| W-19 | Замок Химэдзи | Булатная сталь; Инженерия | BOTTOM | 150 000 / 150 000 | IMAGE-READ |
| W-20 | Флагман Ingermanland | Национализм | BOTTOM | 100 000 / 100 000 | IMAGE-READ |
| W-21 | Всемирный банк | Инновации | BOTTOM | 150 000 / 150 000 | IMAGE-READ |
| W-22 | Замок Нойшванштайн | Археология | BOTTOM | Неуверенно читается | COST-UNVERIFIED |

## Three distinct prerequisite kinds

1. `ResearchPrerequisite`: `Church` and `Trade` required to **research** `Parthenon`, for example. When two or more prerequisites are printed, interpret as an AND set provisionally, consistent with source legend and D-015; exact runtime verification pending.
2. `WonderConstructionPrerequisite`: city/wonder building rules, footprints, availability, uniqueness, building count, coin/hammers/material inputs and sovereignty constraints as described in [later wonder articles](./VIME_NATIONS_RELIGIONS_WONDERS.md). **Do not conflate research cost pair with the cost of constructing a wonder**.
3. `VictoryPrerequisite`: preserving a wonder in an uncaptured city during a timed win challenge, as separately captured in [victory pass 07](./VICTORY_CONDITIONS_PASS_07.md), [late pass 08](./VIME_LATEGAME_VICTORY_MISSIONS_PASS_08.md), and [pass 09](./VIME_CULTURE_DIPLOMACY_DOMINATION_PASS_09.md). A researched wonder is not proof of active wonder ownership.

## Source-scope and conflicts

- `Чичен-Ица`: source says *all research that unlocks T3*, a **set-expression prerequisite**. Do not replace this with an invented short subset; compute only after a verified list of T3 unlocking technologies is available.
- `Космический шаттл`: printed ordinary prerequisite text not visible in supplied source. **UNKNOWN**, not `NONE`. Shuttle construction and mission chain require separate factory components and endgame project rules.
- `Совет Восьми` needs `Электричество` in the V1.0.9 source diagram; the research requirement is separate from Council diplomatic victory holding.
- `Всемирный банк` has printed `Инновации` at research layer; physical building [World Bank](https://civ-wiki.vimeworld.com/wiki/Всемирный_банк) also requires Stock Exchange coverage and host Bank level 10 in later wiki.
- The old Bukkit YAML lists **7 old wonders**, whereas the source visual C5 branch has **22 entries**. Do not merge their costs, names or gameplay behavior without versioned mapping.
- Labels `Александрийская библиотека` versus old `Great Library` and some national wonder-like names should not be assumed exact one-to-one runtime IDs without articles.
- Several cost numbers are still intentionally `UNVERIFIED`; do not copy historical cost guesses into final Reborn balance.

## Proposed implementation rules — NOT APPROVED FINAL DESIGN

- Store each wonder technology with `ordinaryRequiredTechIds[]` and its source version; do not add predecessor from previous yellow node.
- Keep special requirements such as `all T3 unlocks` as typed predicates with documented provenance.
- Maintain distinct states: `WONDER_RESEARCHED`, `WONDER_BUILT`, `WONDER_ACTIVE`, `WONDER_DESTROYED`, `WONDER_CAPTURED`; avoid assuming an unlocked research grants wonder benefits.
- Apply identical checks to player-led and autonomous NPC-led civilizations; no instant high-tier wonder through arbitrary yellow line.
- Exclude unavailable costs/unknown conditions from hard requirements until the graph and GDD is approved.

## Validation / acceptance criteria

1. Wonder A research never requires Wonder B **solely because it appears earlier on the dotted yellow track**.
2. Every multi-ordinary-technology wonder gate checks all named requirements.
3. `Чичен-Ица` gate evaluated from a verified, versioned T3 technology subset.
4. Research, construction, ownership and victory prerequisite tests are independent and restart-safe.
5. Destroyed/captured wonder invalidates only the benefits/win-condition checks prescribed by approved design; its research should not automatically be erased.
6. The 10k/15k/25k world-size presets do not create impossible wonder resource or construction-site placement by default; verify with map generation.
7. Research cost and building cost are not conflated, and source-image values never silently override VimeWorld article values.
8. Each wonder row eventually has a source coordinate and source revision, plus all validated numbers and names.

## Next source-audit work

- Revisit unreadable wonder cost pairs with another source image version, and reconcile visible numbers with encyclopedia articles.
- Resolve `Космический шаттл` missing text and exact Chichen Itza T3 gate list.
- Cross-check each research-to-wonder pair with individual historical wiki wonder pages.
- Reconcile city construction costs and empire-wide prerequisites in the future GDD, not in the source-catalog layer.

**G0 GAP-002 and GAP-009 remain OPEN. No gameplay code added.**

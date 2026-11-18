# G0 — VimeWorld Technology Graph, directed edges pass 03

**2026-11-18.** **Evidence:** user-provided original diagram `image(20261118-070044).png` and high-resolution crops `tech_left.png`, `tech_middle.png`, `tech_right.png`. This is an *image-traced* adjacency subset rather than a full validated DAG. The supplied source image has been directly inspected; no inferred technology prerequisite may become a shipped requirement without cross-check and GDD approval.

**Important semantics:** A displayed colored horizontal progression can verify that the technology on the right follows an earlier displayed named technology on that track. It does NOT by itself guarantee **no other incoming** requirements. The drawing's **white-ring markers denote additional cross-branch AND prerequisites**. The rows below are an initial edge candidate set based on continuously drawn paths; inspect exact geometry at each joint and source names before turning it into programmatic final requirements. Some additional colored lines join between listed nodes, and some named positions denote milestones rather than rings.

## 1. Traceable same-track dependency candidates

| Trace ID | Predecessor (shown left/branch source) | Successor (shown right/branch target) | Visual basis | Confidence and limitations |
|---|---|---|---|---|
| RED-01 | Арсенал | Стрельба | Main upper red trunk, left crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-02 | Арсенал | Кузница | Red branch descends between Arsenal and Fletching, left crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-03 | Стрельба | Оперение стрел | Upper red line | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-04 | Кузница | Выделка кожи | Second red line | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-05 | Кузница | Литье металлов | Red branch lower, left crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-06 | Литье металлов | Металлургия | Third red line | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-07 | Литье металлов | Ковка мечей | Third-to-fourth red branch | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-08 | Оперение стрел | Составной лук | Top red line, middle crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-09 | Выделка кожи | Дубление | Second red line, middle crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-10 | Металлургия | Химия | Third red line, middle crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-11 | Ковка мечей | Булатная сталь | Fourth red line, middle crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-12 | Составной лук | Ламинация | Top red line, right crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-13 | Дубление | Химия композитов | Second red line, right crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-14 | Химия | Сплавы металлов | Third red line, right crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| RED-15 | Булатная сталь | Легирование | Fourth red line, right crop | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-01 | Шахты | Производительность | Upper blue trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-02 | Шахты | Строительство | Lower blue branch splits after Шахты | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-03 | Производительность | Мореплавание | Upper blue trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-04 | Строительство | Архитектура | Lower blue trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-05 | Налоги | Эффективность | Upper blue trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-06 | Артиллерия | Баллистика | Lower blue trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-07 | Эффективность | Инженерия | Upper blue trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-08 | Инженерия | Конвейерная сборка | Upper blue trunk middle | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-09 | Конвейерная сборка | Электричество | Upper blue trunk middle | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-10 | Электричество | Инновации | Upper blue trunk near colored join | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-11 | Инновации | Радио | Upper blue trunk right | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-12 | Радио | Пластмассы | Upper blue trunk right | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-13 | Инновации | Электродинамика | Blue branch below right trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-14 | Удобрение | Биология | Lower blue trunk right | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| BLUE-15 | Биология | Криогеника | Lower blue trunk right | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| GREEN-01 | Фермерство | Керамика | Upper green trunk left | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| GREEN-02 | Фермерство | Охота | Green branch below left | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| GREEN-03 | Керамика | Механизмы | Upper green trunk left | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| GREEN-04 | Охота | Пчеловодство | Lower green line | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| GREEN-05 | Торговля | Коммерция | Lower green line left | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| GREEN-06 | Коммерция | Мировой рынок | Lower green line left-to-middle | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| GREEN-07 | Мировой рынок | Монополия | Green branch to middle; verify exact separation from green entry at junction | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-01 | Религия | Монархия | Upper purple trunk left | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-02 | Религия | Виноделие | Purple branch left | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-03 | Монархия | Законодательство | Upper purple trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-04 | Виноделие | Церковь | Lower purple trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-05 | Законодательство | Письменность | Upper purple trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-06 | Церковь | Ферментация | Lower purple trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-07 | Письменность | Деньги | Upper purple trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-08 | Ферментация | Пивоварение | Lower purple trunk | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-09 | Деньги | Экономика | Upper purple path through middle | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-10 | Экономика | Национализм | Lower purple path middle | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-11 | Национализм | Археология | Lower purple path to right | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |
| PURPLE-12 | Археология | Искусство | Right purple line | VISUAL-HIGH; direct displayed branch, possible extra AND inputs |

**Count: 49 same-track directed relationship candidates**, intentionally excluding any branch-crossing AND-set reconstruction. These candidates are not asserted as a complete graph and do not prove exact minimal direct prerequisites at every junction. All labels must be reconciled with the original [visual list](./VIME_TECH_GRAPH_VISUAL_TRANSCRIPTION_02.md).

## 2. Confirmed presence of cross-branch constraints, edges pending full trace

The owner approved **D-015: preserve interbranch tech dependencies**. The following are **visual joint groups**, not yet formalized as `A -> B` edges:

1. **Early military branch gate:** vertical green/blue lines end at multiple white-ring red track military nodes around `Оперение стрел`, `Выделка кожи`, `Металлургия`, `Ковка мечей`; the exact upstream blue and green technology depends on which connector is traced.
2. **Middle military gate:** `Составной лук`, `Дубление`, `Химия`, `Булатная сталь` share a colored vertical bus with `Инженерия`, `Алхимия` and `Экономика`.
3. **Late military gate:** `Ламинация`, `Химия композитов`, `Сплавы металлов`, `Легирование` connect to a blue bus leading down towards `Радио`; distinguish node-specific paths from a single shared requirement.
4. **Endgame join:** `Военная наука` and the blue research branch converge on `Технология будущего`. Trace exact direction and parent closure before setting a mandatory conjunction.
5. **Green/purple convergence:** `Мировой рынок`, `Монополия`, `Республика`, `Экономика`, `Машиностроение`, `Научный метод` and `Инновации` interact through multi-color intersections that must be traced at higher resolution.
6. **Yellow wonder tracks:** separate world wonder tech prerequisites; **do not** promote yellow dotted connections into ordinary research edges without checking the exact legend symbols.

## 3. Excluded / still uncertain

- Every cross-color edge at ring markers; no precise per-node AND list published in this pass.
- Currency / science cost per technology, even when partially readable, not copied from earlier versions.
- Unreadable fine-print, possible curved line crossings, and unique numeric IDs.
- Connection topologies involving `Военная наука`, `Технология будущего` and `Республика` that cannot be resolved to a *single* same-track direct edge without visual tracing.
- Anachronistic assumptions such as inferring prerequisites from technology names or historical eras.

## 4. Validation still required (GAP-002)

1. Map diagram pixels to technology markers and continuous connectors for all four primary colors.
2. Enumerate AND prerequisite sets for **every white ring** and mark ambiguities explicitly.
3. Establish full directed edge list with no undefined names; compute topological ordering, strongly connected components and path reachability.
4. Trace 22 wonder yellow dotted paths separately and only use source-confirmed requirements.
5. Verify technology costs + science amounts against image crops.
6. Review all exact edges and balancing decisions before freezing GDD; ensure player and NPC states obey identical rules.

**G0 OPEN; no Minecraft code written.**

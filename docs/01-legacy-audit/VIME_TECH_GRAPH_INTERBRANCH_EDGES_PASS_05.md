# G0 — Interbranch research edges, per-node candidate pass 05

**Date:** 2026-11-18. **Primary evidence:** user-supplied screenshot of a VimeWorld technology graph (2048×1057) and zoomed `tech_left.png`, `tech_middle.png`, `tech_right.png` files available in this conversation. **Verification level:** manually traced visual parent candidates; NOT a full validated source research DAG. The earlier [group trace](./VIME_TECH_GRAPH_INTERBRANCH_PASS_04.md) records the colored common buses and ambiguity.

## Reproducible extraction rules

- `parent → target`: visually connected upstream *named* research and the ring target. This is a **research prerequisite candidate**; true immediate-parent relationship must be confirmed with the full source graph, especially shared common buses.
- `VISUAL-HIGH`: continuous clearly visible parent-color bus and ring at crop resolution. `VISUAL-LIKELY`: green bus visible, but its precise closest named source still needs a connectivity check.
- Requirements are **AND**, if each contributing colored bus proves to touch the white ring. A name being visually nearby does not imply an extra prerequisite.
- All listed rows supplement, and do not replace, red same-track predecessors in [pass 03](./VIME_TECH_GRAPH_EDGES_PASS_03.md).
- `Node IDs` are still provisional names from [visual labels](./VIME_TECH_GRAPH_VISUAL_TRANSCRIPTION_02.md); localization aliases and exact future internal IDs remain undecided.

## Candidate direct input edges

| ID | Upstream research | Locked research | Gate | Confidence | Visual reasoning |
|---|---|---|---|---|---|
| XEDGE-01 | Механизмы | Оперение стрел | X-01 | VISUAL-LIKELY | Early green bus rises from Механизмы to red ring; verify exact last named green anchor |
| XEDGE-02 | Механизмы | Выделка кожи | X-01 | VISUAL-LIKELY | Green common bus to ring |
| XEDGE-03 | Механизмы | Металлургия | X-01 | VISUAL-LIKELY | Green common bus to ring |
| XEDGE-04 | Механизмы | Ковка мечей | X-01 | VISUAL-LIKELY | Green common bus to ring |
| XEDGE-05 | Инженерия | Составной лук | X-02 | VISUAL-HIGH | Blue bus rises directly from Инженерия ring |
| XEDGE-06 | Инженерия | Дубление | X-02 | VISUAL-HIGH | Blue bus rises directly from Инженерия ring |
| XEDGE-07 | Инженерия | Химия | X-02 | VISUAL-HIGH | Blue bus rises directly from Инженерия ring |
| XEDGE-08 | Инженерия | Булатная сталь | X-02 | VISUAL-HIGH | Blue bus rises directly from Инженерия ring |
| XEDGE-09 | Экономика | Составной лук | X-02 | VISUAL-HIGH | Purple bus rises from Экономика ring to common military rings |
| XEDGE-10 | Экономика | Дубление | X-02 | VISUAL-HIGH | Purple bus rises from Экономика ring |
| XEDGE-11 | Экономика | Химия | X-02 | VISUAL-HIGH | Purple bus rises from Экономика ring |
| XEDGE-12 | Экономика | Булатная сталь | X-02 | VISUAL-HIGH | Purple bus rises from Экономика ring |
| XEDGE-13 | Радио | Ламинация | X-07 | VISUAL-HIGH | Blue vertical bus rises from Радио node area to red ring |
| XEDGE-14 | Радио | Химия композитов | X-07 | VISUAL-HIGH | Blue vertical bus rises from Радио node area to red ring |
| XEDGE-15 | Радио | Сплавы металлов | X-07 | VISUAL-HIGH | Blue vertical bus rises from Радио node area to red ring |
| XEDGE-16 | Радио | Легирование | X-07 | VISUAL-HIGH | Blue vertical bus rises from Радио node area to red ring |
| XEDGE-17 | Военная наука | Технология будущего | X-08 | VISUAL-HIGH | Red line runs from Военная наука around right side into final ring |
| XEDGE-18 | Пластмассы | Технология будущего | X-08 | VISUAL-HIGH | Upper blue research line runs into final ring |
| XEDGE-19 | Криогеника | Технология будущего | X-08 | VISUAL-HIGH | Lower blue research line runs into final ring |

**Count:** 19 cross-branch candidate arrows, 15 high confidence, 4 likely. These are not 19 proven final edges; they are hypotheses grounded in visible geometry awaiting source-level review.

## Partial graph structural check (this file's edges only)

- Candidate records: 19.
- Endpoints absent from prior transcribed name list: **0**.
- Directed cycles **among these cross-branch candidate arrows only**: **0**.
- This does **NOT** prove that the combined 49 same-track candidate edges + these crosses + yellow wonder constraints form a valid acyclic complete technology graph. Those sources have their own unresolved aliases and possible incorrect arrows.

## Combined preliminary validation (passes 03 + 05)

Manually parsed the rows of [pass 03](./VIME_TECH_GRAPH_EDGES_PASS_03.md) and this pass and performed a directed cycle check of the **union**: **68 candidate edges**, touching **59 named nodes**, **0 cycles detected**. This only validates the *transcribed subset* and cannot establish that the source graph itself has no cycles or that every node is reachable. A wrong or omitted arrow can change the result.

## Three key AND-gate groups

**X-02, middle-tier military:** all four T3 military nodes are reached by red horizontal progression plus blue bus rooted at Engineering and purple bus rooted at Economics. Before lock-in, trace the exact order of bus meeting and whether minimal direct parents are Engineering and Economics or intermediate nodes.

**X-07, late-tier military:** each T4 military node has its own red predecessor and a blue vertical bus rising from Radio. The crop confirms presence of the technical gate; formal minimal-parent designation remains conditional on full branch traversal.

**X-08, Future Technology:** visible final white ring has three paths: red Military Science, blue Plastics and blue Cryogenics. This appears a three-way AND requirement, not a single-blue advanced progress shortcut. These paths may share earlier technology ancestors.

## Outstanding X-01 and other gate work

- Left early green bus parent identity (Механизмы vs other earlier green node) must be individually reconfirmed; mark XEDGE-01..04 **VISUAL-LIKELY**, not source-certified.
- The **blue line crossing the X-01 gate area** must be checked for actual meeting/white ring status; no guessed blue edges added here.
- Exact purple input to `Научный метод`, `Машиностроение`, `Инновации`, `Археология`, and economics-sector gates still require pixel tracing at their white rings.
- Wonder dotted-line dependencies are excluded pending separate audit; they are not ordinary tech research edges.
- Costs and research-point amounts remain separate transcription work.
- D-015 requires preserving actual AND constraints. Any deliberate balance deviation must be an explicit GDD decision.

**G0 GAP-002 OPEN. No game code changed.**

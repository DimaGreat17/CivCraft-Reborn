# G0: VimeWorld research graph — interbranch AND-gate trace, pass 04

**Reviewed:** 2026-11-18. **Primary original material:** user-provided 2048×1057 technology diagram `image(20261118-070044).png`, and its original-resolution magnified regions `tech_left.png`, `tech_middle.png`, `tech_right.png`. Diagram footer labels it `CivCraft V1.0.9 by VimeWorld`. This is a *visual source interpretation*, NOT later runtime proof.

**User design D-015:** mandatory cross-branch research dependencies **must be preserved**. Do not simplify research to four independent branches. This decision approves the principle, not a hand-invented edge list.

## 1. Reading the diagram without fabricating dependencies

- Red: Вооружение (C1); blue: Технический прогресс (C2); green: Экономика (C3); purple: Общество (C4); yellow dotted: Чудеса (C5).
- On-image legend explicitly calls attention to white-ring nodes requiring ALL upstream technologies along contributing lines.
- A branch **bus** may touch several white-ring nodes. The direct prerequisite to record for each may be the latest named technology on that bus; the complete **transitive prerequisite closure** is larger.
- **A colored line passing a label with a short bar** identifies an ordinary step. It is not safe to infer that this is a new cross-color junction.
- A geometric crossing with no white-ring connection is **not** proof that one branch depends on another.
- Record **topology groups** as visual facts and separate **immediate parent candidates** as unresolved, rather than writing made-up direct graph edges.

## 2. Rechecked cross-branch topology (evidence grading)

| Gate ID | Research nodes / cluster reached | Colored contributing lines actually visible | Source crop | Classification | What remains uncertain |
|---|---|---|---|---|---|
| X-01 | Оперение стрел; Выделка кожи; Металлургия; Ковка мечей | Red horizontal branches meet green vertical bus; blue connection in vicinity of the lower join | `tech_left.png` around white rings at crop x≈778, y≈51–321, and green/blue paths to y≈446–750 | **VISUAL-BUS-CONFIRMED**, exact parent tech still candidate | Whether blue is an additional AND requirement at every ring or only downstream join; identify last named blue/green tech feeding bus |
| X-02 | Составной лук; Дубление; Химия; Булатная сталь | Red horizontal branches meet continuous blue + purple vertical lines | `tech_middle.png` x≈260, y≈55–330 | **VISUAL-BUS-CONFIRMED** | Each white ring's minimal blue and purple prerequisite; confirm which labelled predecessors anchor these buses |
| X-03 | Инженерия and Алхимия | Blue upper/lower research lines share vertical neighborhood with purple progression and white rings | `tech_middle.png` x≈260, y≈460–550 | **VISUAL-JOIN-CONFIRMED** | Exact required purple prior node; branch orientation and where the named nodes lie on the colored paths |
| X-04 | Научный метод; Машиностроение | Purple branches feed white-ring positions on blue progress paths | `tech_middle.png` x≈608, y≈639 and y≈951 | **VISUAL-JOIN-CONFIRMED** | Which earlier societal tech is immediate prerequisite and how blue trunk splits |
| X-05 | Инновации | Blue main progression meets green/purple branches at a white-ring node | `tech_middle.png` x≈1140, y≈462 | **VISUAL-JOIN-CONFIRMED** | Exact source technologies from green and purple loops; avoid treating physical closeness as multiple incoming edges |
| X-06 | Археология | Blue and green incoming curves converge with purple line at a white ring | `tech_middle.png` x≈1140, y≈1082 | **VISUAL-JOIN-CONFIRMED** | Verify immediate blue and green predecessors |
| X-07 | Ламинация; Химия композитов; Сплавы металлов; Легирование | Four red horizontal tiers join one blue vertical bus originating at Радио-area tech track | `tech_right.png` x≈271, y≈60–380, down to blue Радио line at y≈498 | **VISUAL-BUS-CONFIRMED** | Is Радио direct required predecessor for all four, or does the upper-tier bus imply an additional unmarked lower-tier transition? |
| X-08 | Технология будущего | Red path from Военная наука meets two blue paths from Пластмассы and Криогеника at final white ring | `tech_right.png` x≈800, y≈980 with blue arms from y≈498 and y≈732; red arm from x≈540 | **VISUAL-AND-TOPOLOGY-HIGH** | Formalize minimal parents and ensure three contributions; check if other upper-blue prerequisites are transitive only |

**These 8 rows document gate *groups*, NOT 8 directed prerequisites.** A single row can require multiple immediate incoming edges. No tested final directed adjacency matrix exists yet.

## 3. High-confidence inferences — keep separate from visual observation

| Hypothesis | Source support | Status |
|---|---|---|
| Early advanced red tier depends on some C3 economic development | Green bus physically touches all 4 early white red rings | HIGH-confidence structural inference; exact technology still OPEN |
| Mid red tier depends on progress AND society, not solely prior red branch | Blue + purple buses touch 4 white red rings | HIGH-confidence structural inference; exact direct parents still OPEN |
| Late red tier requires C2 progress reaching Radio-region | One blue bus touches late red rings | HIGH-confidence structural inference; exact Radio prerequisite per node still OPEN |
| Future Technology has both late military AND late technical research ancestry | Red and blue paths meet final white circle, with two separate blue arms | HIGH-confidence structural inference; candidate immediate parents: Военная наука / Пластмассы / Криогеника; requires final coordinate trace before freeze |
| Interbranch links should be mandatory `AND`, not automatically `OR` | Visual legend calls for all preceding technologies, reinforced by owner D-015 | Design principle CONFIRMED; exact topology pending |

## 4. Corrections/constraints relative to pass 03

[Pass 03](./VIME_TECH_GRAPH_EDGES_PASS_03.md) listed **49 same-track edge candidates**, not a verified exhaustive graph. In particular:
- Direct predecessor and transitive ancestor must not be conflated; reading a bus as four independent arrows may misrepresent the source.
- Some purple/green nodes (e.g. `Республика`) are placed directly on green paths despite branch nomenclature, so source colors override semantic intuition.
- `Технология будущего` must not be treated as reachable by red `Военная наука` alone; it has other visibly convergent blue paths.
- Per-node coin/research costs, wonders' yellow dotted prerequisites and the full transitive closure remain pending.
- Do not mark any research node as cycle-free/reachable based on this partial diagram trace.

## 5. Planned machine-checkable graph schema (design proposal, not code)

`ResearchNode { id, sourceLabel, sourceVersion, sourceBranch, coinCost, beakerCost, sourceCoordinates, verificationStatus }`

`ResearchPrerequisite { requiredNodeId, unlockedNodeId, gateType=ALL, sourceEvidence, verificationStatus }`

Store **one explicit immediate prerequisite per validated connector**, generate transitive closure computationally, and attach uncertainty to every unverified line. Separate ordinary research from wonder-specific eligibility.

## 6. G0 completion conditions

- Trace and record **every white-ring node** into exact immediate-parent IDs and pixel evidence.
- Transcribe all 66 candidate ordinary research names from [visual pass 02](./VIME_TECH_GRAPH_VISUAL_TRANSCRIPTION_02.md) and normalize naming aliases without silently inventing source IDs.
- Reconcile line directions and costs against actual image and individual wiki article details.
- Validate unknown references, cycles, roots, unreachable nodes and all target victory technology unlocks.
- Test `D-015` progress pacing: reaching top-tier military without supporting tech/industry/society should be impossible.
- Reconcile source diagram `V1.0.9` with wiki page's newer raster `1.9.2` rather than pretending they are identical.
- Finish review and obtain GDD approval before any mod implementation.

**G0 GAP-002 remains OPEN. No gameplay code changed.**

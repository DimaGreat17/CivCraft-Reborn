# G0 Pass 13 — Formal completeness checks of the user-supplied technology graph

**Date:** 2026-11-18. **Input documentation inspected directly:**
- [Visual transcription pass 02](./VIME_TECH_GRAPH_VISUAL_TRANSCRIPTION_02.md) (66 ordinary labels).
- [Same-track edge candidates pass 03](./VIME_TECH_GRAPH_EDGES_PASS_03.md) (49).
- [Cross-branch edge candidates pass 05](./VIME_TECH_GRAPH_INTERBRANCH_EDGES_PASS_05.md) (19).
- [Wonder prerequisites pass 06](./VIME_WONDER_TECH_BRANCH_PASS_06.md) (22 C5 entries).
- [Cross-branch group audit pass 04](./VIME_TECH_GRAPH_INTERBRANCH_PASS_04.md) and owner approved D-015.

**This document validates the existing *transcription*, not the original VimeWorld full research graph.** No technology mechanics were implemented.

## Measured subset results

| Metric | Result | What it proves |
|---|---:|---|
| Ordinary technology labels | **66** | Count of visual transcription entries only |
| Candidate directed ordinary relationships | **68** | 49 main-branch + 19 cross-branch rows |
| Unique named endpoints used | **59** | 7 transcribed names not present in any candidate edge |
| Unknown/dangling endpoint names | **0** | Every edge endpoint matches some transcribed node name |
| Duplicate name labels | **0** | Names are unique in this transcribed set |
| Exact duplicate arrow candidates | **0** | No repeated identical from→to pair |
| Directed cycles in this candidate subset | **0** | Conditional: omissions/wrong arrows can hide or introduce cycles |
| Apparent roots (no candidate inbound edge) | **16** | Mainly symptom of missing edges; **not 16 real source roots** |

## Seven isolated transcription labels (urgent graph repair)

These research labels currently have **no incoming or outgoing edge** in passes 03 + 05:

1. `Навигация` (C2)
2. `Алхимия` (C2)
3. `Научный метод` (C2)
4. `Машиностроение` (C2)
5. `Загонная охота` (C3)
6. `Республика` (C3 visual grouping)
7. `Образование` (C4)

An isolated node is **not evidence that it is optional or has no prerequisites**. Several are in fact prerequisites for C5 wonders or major late buildings. These need image-pixel trace and version confirmation.

## Apparent roots of candidate graph (NOT accepted source roots)

`Арсенал`, `Военная наука`, `Шахты`, `Налоги`, `Артиллерия`, `Навигация`, `Алхимия`, `Научный метод`, `Машиностроение`, `Удобрение`, `Фермерство`, `Торговля`, `Загонная охота`, `Республика`, `Религия`, `Образование`.

In particular **Военная наука** appearing as a root is a manifest missing late-red prerequisite, NOT a game-rule fact. The graph cannot be considered closed until these spurious roots are resolved.

## Endgame / wonder dependency closure tests

- Parthenon requires `Церковь` + `Торговля`, but `Церковь` has a society predecessor and `Торговля` has no recorded ordinary incoming edge. Need true chain.
- Angkor Wat requires `Инженерия` + `Образование`, but Education currently isolated. Source chain incomplete.
- Tree of Life needs `Производительность` + `Загонная охота`, but latter currently isolated.
- Notre Dame requires `Республика`, currently isolated.
- Chichen Itza requires all Tier-3 unlocks, but Tier-3 set is not yet validated.
- Future Technology candidate parents `Военная наука`, `Пластмассы`, `Криогеника`; Military Science must be linked to earlier military research.
- C5 wonders are **nonsequential** according to owner-provided image legend; do not insert yellow predecessor requirements to conceal missing ordinary-branch gates.

## Minimum criteria before proclaiming a valid complete technology graph

1. Validate every white ring in original image; write each **immediate** parent edge with coordinates/evidence.
2. Replace ambiguous aliases without losing original label and branch; resolve every isolated research node.
3. Record all study costs and science points with source snapshot and explicit units; distinguish wonder-research cost vs construction cost.
4. Validate cycles, reachability, and **unintended free roots** on complete adjacency, not just an incomplete subset.
5. Derive Tier-1–Tier-4 sets and enforce same-branch/interbranch D-015 AND requirements for NPC/player.
6. Reconcile owner-supplied diagram footer `V1.0.9` against later wiki's `Tree 1.9.2`; do not combine into a fictitious consistent build.
7. Build automated graph test cases using complete data **after** design freeze, not by importing unverified edges.

## Release/dependency conclusion

**GAP-002 CANNOT BE MARKED CLOSED.** These 7 isolations are *known, testable remaining transcription gaps*. The incomplete graph is an acceptable research artifact but not a reliable P3 release implementation catalog. There is still no physical mod code.

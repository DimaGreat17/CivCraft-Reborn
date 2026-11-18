# Technology Progression — Interbranch Dependencies (D-015)

**Approved concept — 2026-11-18:** Preserve dependency links within AND **between** the technology branches of the VimeWorld CivCraft tree. This intentionally prevents a civilization from racing to the end of just one discipline while neglecting the others. **Exact node/edge mapping and numerical costs are not yet frozen**; source image transcription is part of G0.

## Design intent

- Research is divided into several disciplines. The reference image provided by the project owner shows **Вооружение (red), Технический прогресс (blue), Экономика (green), Общество (purple)**, plus **Чудеса (dashed yellow)**.
- A technology is eligible **only after satisfying every mandatory prerequisite**. Some prerequisites cross color/disciplines. Cross-branch connectors are a *feature* and **must not be simplified away** to make branch-specific rush strategies possible.
- Explicit join markers in the image are important: a crossing is not automatically a dependency. Each confirmed junction must be transcribed as a directed edge or multi-edge AND requirement.
- Era gates and cross-discipline prerequisite closures should prevent access to high-tier military, economy or science without the supporting foundational technology.
- Same eligibility and resource costs apply to player and autonomous NPC civilizations. No AI shortcuts.
- For major sequential releases, new technologies may unlock only when their prerequisite closure is available in released content. No unfinishable tech or buildings exposed to player in an earlier patch.

## Reference and fidelity

The user supplied a **2048×1057** tree diagram on 2026-11-18. It shows colored connectors and multi-branch joins; it is now the visual source for late tree reconstruction. It should not be confused with the original Bukkit `techs.yml` 57-node graph or an unverifiable wiki revision.

### Source reconstruction protocol

1. Transcribe all visible technology names, costs and science requirements; attach image coordinates and a confidence tag.
2. Trace connector edges, noting which arrow/junction visually connects prerequisite to unlocked node.
3. Distinguish ordinary tech AND prerequisites, special wonder-tech requirements, building unlocks and decorative crossings.
4. Record every unclear junction as `UNVERIFIED`; **never invent** an edge for balance reasons.
5. Check DAG for dangling references/cycles, compute prerequisite closure and test early-to-late rush attempts.
6. Present reconstructed graph and any intentional balance changes for explicit GDD review; approved principle **does not** automatically approve each edge or old numeric balance.

## Requirements / acceptance scenarios

- Unlock of any final military node fails if required economy/society/progress ancestor was not researched.
- Unlock of a late industrial/economic node fails if a cross-branch mandatory prerequisite is absent.
- Node becomes available when all prerequisites are satisfied, independent of research order.
- No cyclic chain, unreachable node, or branch dead-end in a completed release.
- A tech cannot be bypassed by AI planner, direct UI, save/load or multiplayer client.
- Existing saves retain researched IDs and active research when compatible trees are migrated; incompatible changes require an explicit migration and owner approval.

## Separate from D-012 patches

The technology graph describes **in-game civilization progress**. D-012 describes **real-world mod release increments**. They are distinct concepts but patches must ship complete reachable segments of the graph.

**Status:** D-015 approved concept; G0 image-backed edge mapping IN PROGRESS; no mod implementation permitted before G0–G4 approvals.

# P0 / 0.1 — implementation status (not release signoff)

**Branch:** `feat/p0-foundation-compatibility`  
**PR:** [#6](https://github.com/DimaGreat17/CivCraft-Reborn/pull/6)  
**Updated:** 2026-11-18  
**Release:** **BLOCKED / NOT MERGED**. Only P0 Foundation implementation is authorized.

## Implemented in the branch

- Java 25 / Minecraft Java 26.3 / Fabric Loader 0.20.1 / Fabric API 0.166.2+26.3 / Loom 1.19.4 / Gradle 9.8.
- Native Minecraft Create World screen gains CivCraft size/mode cycle buttons via Fabric ScreenEvents; FINITE selector explicitly says victory unavailable. **Compiled but interactive GUI verification still required.**
- Selected choices saved atomically in `config/civcraft-reborn-world-defaults.properties` prior to integrated-server world creation; new worlds capture this into world-scoped `SavedData` on first access. Existing worlds are not intentionally mutated through this file.
- Dedicated server retains pre-world defaults file for first creation.
- Typed P0 model with three sizes, two session modes, schema validation and Codec; no victory evaluator.
- WorldBorder centered explicitly at (0,0) and sized by the selected **side length**.
- Read-only server-side `/civcraft worldinfo` command for saved mode, border, schema and victory unavailability.
- Unit tests for config parsing, all six atomic write/read paths, Codec roundtrips and unknown schema.
- `scripts/p0_world_restart_smoke.py`: non-destructive dedicated-world create/stop/restart check. Optional `.github/workflows/p0-world-matrix.yml` generates all six cases. **Not yet executed or confirmed green**.

## Real CI evidence, limited claim

- [Run 44279950983](https://github.com/DimaGreat17/CivCraft-Reborn/actions/runs/44279950983): successful compile, JUnit and **one** dedicated server startup to `Done`.
- [Run 44291344239](https://github.com/DimaGreat17/CivCraft-Reborn/actions/runs/44291344239): successful compile, tests and single-server smoke on earlier UI changes, as inspected through workflow jobs. Later commits require independent CI evidence.
- Neither run is the full six-case save/restart matrix or an actual graphical `runClient` user interaction.

## Known technical/release blockers

1. Confirm new screen controls in **real client** (position, click behavior, resizing, integrated-world first save, correct defaults for new world, not previous saves). The current file-based handoff is transitional, needs end-to-end testing for world isolation, cancellation and error handling.
2. Run all six real worlds through save/stop/restart/rejoin; confirm their mode/size and physical border. Optional matrix harness may require adjusting stdin forwarding depending on Loom `runServer`; failure should remain visible rather than be ignored.
3. Dedicated and singleplayer spawn/border-edge, client spoof and actual server persistence tests, not just JUnit.
4. Crash/corrupt `SavedData` recovery that never silently resets an existing world; schema migration/backup path.
5. Define/reuse stable `WorldId` and seed from vanilla authoritative world metadata with documented validation; no accidental reassignment.
6. Reproducible Gradle wrapper and dependency/licensing review (the CI currently installs Gradle 9.8 itself).
7. Rebase/merge up-to-date `main`, rerun CI, get explicit owner release acceptance.

## Safety/release rule

No NPC, jobs, housing, settlement, claim, economy, tech tree, war, diplomacy or victory gameplay is being implemented in P0. Do not merge PR #6 or declare P0 0.1 finished until blockers pass and owner explicitly accepts it.

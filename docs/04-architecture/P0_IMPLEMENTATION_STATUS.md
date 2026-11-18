# P0 implementation progress (NOT a release signoff)

**Branch:** `feat/p0-foundation-compatibility`  
**PR:** #6  
**Updated:** 2026-11-18

## Previously confirmed CI evidence

- Java 25 / Minecraft Java 26.3 / Fabric Loader 0.20.1 / Fabric API 0.166.2+26.3 / Loom 1.19.4 / Gradle 9.8.
- The prior CI run [44279950983](https://github.com/DimaGreat17/CivCraft-Reborn/actions/runs/44279950983) confirmed compile, JUnit execution and a dedicated server boot to `Done`.
- World options are represented by immutable typed `WorldSettings`; they are intended to be stored per world via Minecraft `SavedData`.
- Startup reads world settings and applies the Overworld border; FINITE is only saved intent, without a victory evaluator.

## New test coverage (commit 840f856)

- Exercised all six preset × session-mode combinations by loading a temporary properties file.
- Verified default configuration in the absence of the properties file.
- Verified that unsupported presets and unrecognized modes fail explicitly, and that null options and future schema versions are rejected.
- These tests were committed; **their new CI run has not yet been confirmed green**. Unit tests cannot demonstrate actual save/restart behavior.

## Outstanding P0 acceptance blockers

1. Actual world-creation GUI and accurate disclosure that finite victory evaluation is unavailable.
2. Six real world save/restart/rejoin tests (not just config parsing), including persisted mode, selected size and actual border geometry.
3. Integrated single-player client smoke and dedicated-server tests, including negative client-mutation attempts.
4. Verify world spawn and border center, structure placement, build/exploration edge behavior.
5. Crash-interrupted write and corrupted/unknown-schema recovery without deleting or silently recreating settings.
6. Record and verify the required WorldId/seed validation and recovery/migration plan.
7. Resolve rights/dependency signoff, evidence files and owner acceptance.

## Guardrails

No NPCs, settlements, currencies, technologies, claims, diplomacy, victory evaluators or legacy gameplay belong in P0. Do not merge PR #6 or claim P0 0.1 released until all required acceptance criteria are satisfied.

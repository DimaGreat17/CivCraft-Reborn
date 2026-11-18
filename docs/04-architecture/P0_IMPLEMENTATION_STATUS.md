# P0 implementation progress (NOT a release signoff)

**Branch:** feat/p0-foundation-compatibility

## Supported / under test

- Compatible Maven/Gradle candidate has passed GitHub Actions compile once after pinning Loom 1.19.4 and Gradle 9.8.
- `WorldPreset`, `SessionMode`, `WorldSettings` are typed and immutable.
- `WorldSettingsSavedData` is designed to persist the options per-world.
- `FoundationServerBootstrap` applies a centered Overworld border on server startup.
- Initial options are read from an optional config file; absence defaults to 10k sandbox.
- FINITE is *recorded*, not misrepresented as a working victory mode.

## Not yet complete

- Exact 26.3 compilation and dedicated-server runtime test of these new sources.
- A genuine world-creation UI allowing choices at world creation (the config file is an interim tool).
- World spawn and border placement edge cases, recovery after a partially written save.
- Persist/restart checks for all six combinations.
- Dedicated-server vs integrated-server tests, permission/security test harness, release acceptance.
- New historical CivCraft mechanics intentionally excluded from P0.

Do not mark 0.1 released or P0 passed until all acceptance tests and user signoff are complete.

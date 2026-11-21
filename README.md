# CivCraft Reborn

A Minecraft Java Edition civilization simulation mod inspired by the historical CivCraft Bukkit plugin.

> **Status:** P0 / 0.1 Foundation is implemented in part on a development branch; **NOT RELEASED**. Minecraft 26.3 / Java 25 / Fabric Loader 0.20.1 / Fabric API 0.166.2+26.3 are pinned for the P0 development build. A CI compile + initial dedicated-server smoke has passed, but the complete client/world-restart/recovery acceptance matrix has not.

## P0 foundation development preview

- Initial 10,000×10,000, 15,000×15,000 and 25,000×25,000 Overworld presets, enforced by the server border centered at (0,0).
- Two recorded world session intentions: `ENDLESS_SANDBOX` and `FINITE_VICTORY`. **Finite victory is NOT implemented**; choosing FINITE cannot end a game.
- Fabric client adds size and mode selectors to Minecraft's Create World screen; selected defaults are captured into per-world `SavedData` on first server access. A dedicated server uses `run/config/civcraft-reborn-world-defaults.properties`.
- Read-only server command: `/civcraft worldinfo` to inspect saved mode, border and schema.
- Gradle compile/Unit tests/one-server smoke: `.github/workflows/p0-compatibility.yml`. Optional six-case real-world restart matrix: `.github/workflows/p0-world-matrix.yml`.
- **Limitations:** new UI has not been interactively client-tested; save/restart matrix and corruption/compatibility/edge tests must pass before merge or release.
- No NPC, towns, economy, technology, claims, wars or victory mechanics are shipped.

Run development code from `feat/p0-foundation-compatibility` with Java 25, Gradle 9.8:
```sh
gradle build
gradle runClient
gradle runServer
```

On dedicated servers set `overworld_side_blocks=10000|15000|25000` and `session_mode=ENDLESS_SANDBOX|FINITE_VICTORY` **before creating a new world**. Changing this file must not be treated as a way to rewrite a previously saved world's settings.

## Vision

A hybrid survival/strategy sandbox: become a citizen, trader, commander, founder, or ruler. Autonomous NPC civilizations develop under the same resource constraints as player settlements.

## Design principles

1. Audit legacy CivCraft features before choosing what to port.
2. NPCs modify the real Minecraft world through authorized actions.
3. AI economies require real resources and logistics.
4. Loaded and unloaded settlements use one authoritative economic ledger.
5. Support single-player and multiplayer.
6. No unapproved scope additions after design freeze.
7. Complete cross-system design before core implementation.

## Documents

- [Project charter](docs/00-vision/PROJECT_CHARTER.md)
- [CivCraft legacy audit](docs/01-legacy-audit/LEGACY_AUDIT.md)
- [Gameplay index](docs/02-game-design/GDD_INDEX.md)
- [Systems index](docs/03-system-design/SYSTEM_INDEX.md)
- [Architecture](docs/04-architecture/ARCHITECTURE.md)
- [Roadmap](docs/05-roadmap/ROADMAP.md)
- [Decision register](DECISIONS.md)

## Source and license policy

Original upstream candidate: https://github.com/netizen539/civcraft . Verify provenance and license obligations before copying original code, configurations, or assets. License for this new project is **TBD**.

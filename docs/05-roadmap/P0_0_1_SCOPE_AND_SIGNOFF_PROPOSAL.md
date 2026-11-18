# P0 / 0.1 Foundation — scope, acceptance и граница перед кодом

**2026-11-18 — PROPOSED / AWAITING OWNER APPROVAL. NO GAMEPLAY CODE AUTHORIZED.** Основа: [D-008/D-012/D-013/D-014/D-045/D-046](../../DECISIONS.md), [G2-11](../03-system-design/G2_11_WORLDGEN_SEED_BORDER_SESSION.md), [G2-12](../03-system-design/G2_12_PERSISTENCE_CLOCK_LOD_CROSS_SYSTEM.md), [G4](./G4_PRE_CODE_READINESS_REVIEW.md).

## A. Игровая цель и проверяемая ценность 0.1

0.1 = **полностью работающее создание/загрузка ограниченного мира с заданной конфигурацией и серверно-авторитетным хранением настроек**. Не является ни «демо NPC», ни частичной цивилизацией. Это технический фундамент, который пользователь может создать, открыть повторно и проверить, но не выдать за игру CivCraft целиком.

### Входит и должно работать к релизу

| ID | Полная функция | Реальная приёмка |
|---|---|---|
| P0-01 | Minecraft Java/Fabric client and dedicated server mod loads | Запуск клиента и headless dedicated server без ошибки; версии зафиксированы |
| P0-02 | World creation config: three Overworld sizes 10,000×10,000 / 15,000×15,000 / 25,000×25,000 | Три пресета доступны при создании мира; размер трактуется как длина стороны, не радиус |
| P0-03 | Session intent: FINITE_VICTORY / ENDLESS_SANDBOX stored per world | 6 комбинаций size×mode записаны и переживают restart |
| P0-04 | Honest finite-mode availability | Пока нет механизма победы, UI прямо говорит «конечная победа пока недоступна», не обещает финал; конкретная политика selectable-but-inactive vs delayed finite creation отдельно утверждается при freeze |
| P0-05 | World border enforced server-side | Overworld ограничен выбранным размером при игре и после перезагрузки; проверка server/API geometry |
| P0-06 | Versioned world-scoped settings | SchemaVersion/WorldId/seed/world-mode/size имеют явные validation rules, backup/migration plan; неверная версия не стирает мир |
| P0-07 | Minimal authoritative read/write and security | Только server permits config mutation; клиент не может повысить роль или менять чужое сохранение |
| P0-08 | Save/quit/reload/restart flow | Create, join, save, stop, restart, rejoin, compare data integrity and worldborder |
| P0-09 | Developer diagnostics/tests for all enabled P0 functions | Сборка, unit/integration smoke, logs, version manifest and negative tests |
| P0-10 | Unambiguous feature disclosure | UI/readme/changelog не показывают NPC/город/войну/технологии/победу как реализованные |

### Явно НЕ входит

NPC entity/population, worldgen NPC camps, camp→settlement progression, claims and political plot rights, currency/production/jobs/housing, City/Province/Civilization, technology tree, wonders, wars, diplomacy, victory evaluators, religious and guild features, AI and virtual simulation engine. **Не делать полуготовые команды/вкладки этих систем и не заявлять placeholder как реализованное содержимое.**

P0 не требует закрывать весь late-game G0 tech/73 buildings/wonder audit, **если scope ограничения подписаны владельцем**. Однако безопасность чужих файлов/библиотек и архитектурная пригодность для будущего состояния мира остаются обязательными.

## B. Порядок реализации после отдельного разрешения

1. Verify exact stable toolchain and compile empty mod baseline in approved repo branch (only after owner authorizes coding).
2. Implement typed saved world config with migration/version guard.
3. Implement the **actual world creation UI flow** and authoritative size/mode storage; fallback config must not silently promise UI.
4. Apply server-side Overworld border and persistence hooks; check valid center and edge semantics.
5. Add explicit early-game finite-mode warning/display policy.
6. Add singleplayer and dedicated-server test suites; negative tests, restart/restore.
7. Verify P0 requirements, fix blockers, tag release, get explicit owner acceptance *before P1*.

## C. Критические open choices для P0 freeze

- **P0-D01**: border center, geometry and spawn behavior for each preset; decide how clients see the area without chunk force-loading.
- **P0-D02**: finite-world policy. Recommended: user can record FINITE as intent, but world-creation GUI explicitly warns no victory evaluator yet; even better if finite worlds aren't offered as *playable victory worlds* until later. Owner must approve exact labeling.
- **P0-D03**: selected exact Minecraft/Fabric/Java/Gradle/API versions after real compatibility test; latest stable candidate != verified build.
- **P0-D04**: save backend/API, migration and recovery behavior; owner approval of small documented format.
- **P0-D05**: reference environment and minimum acceptance for startup, save/restart, worldborder, and test logs.
- **P0-D06**: physical contents of P0 permission model: only server authority for world settings/border, **not** hidden future plot-claim stub.
- **P0-D07**: external dependency licenses approved; old CivCraft Java/assets are not copied.

## D. Proposed P0 acceptance

- **P0-A01:** Create all 6 size/mode combinations in singleplayer and dedicated server; confirm saved values and world border after two restarts.
- **P0-A02:** Server rejects fabricated client changes to world size/session mode.
- **P0-A03:** No claim/game systems advertised as functional when absent; finite mode clearly inactive as victory until actual patch.
- **P0-A04:** Spawn and exploration respect preset geometry; no outside-border construction/path exploitation.
- **P0-A05:** Interrupted save/restart recovers settings without resetting world history or changing mode.
- **P0-A06:** Same WorldId with another server/client shows the same persisted settings.
- **P0-A07:** Unknown save format rejected with recovery guidance, never silent zero-state overwrite.
- **P0-A08:** No unapproved historical source file/asset imported into binary.
- **P0-A09:** Required tests and release evidence stored in repo with commit/versions/hardware.
- **P0-A10:** All P0 scope finished, demo/release accepted explicitly before P1 per D-012.

**Documentation drafting pass, not execution. No world or executable mod was created.**

## E. Release gate

`P0_SCOPE_APPROVED` requires a separate explicit owner decision; `G3_FEASIBILITY_VERIFIED` requires a reproducible actual compile/launch and server test; `P0_CODE_AUTHORIZED` requires owner permission to write code. These three flags **are not set by this file**.

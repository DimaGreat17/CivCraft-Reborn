# P0 / 0.1 Foundation — независимый аудит реализации и план приёмки

**Дата проверки:** 2026-11-18. **Статус:** REVIEWED FROM REPOSITORY; NOT RELEASE ACCEPTED. **Код не менялся.** Проверенные источники: `main` (документация), `feat/p0-foundation-compatibility` (реальный код и CI workflow), PR [#6](https://github.com/DimaGreat17/CivCraft-Reborn/pull/6), [P0 scope](P0_0_1_SCOPE_AND_SIGNOFF_PROPOSAL.md), [G3 toolchain](../04-architecture/G3_P0_TOOLCHAIN_FEASIBILITY_PLAN.md).

## 1. Итог руководителю

**Решение: NO-GO для P0 0.1 release / P1 start.** Есть рабочий задел: самостоятельно написанный Fabric entrypoint, типизированные настройки трёх размеров и двух режимов, `SavedData` и установка серверной границы мира, unit-тесты и CI smoke dedicated-server. Но P0 нельзя объявить готовым из-за отсутствующего полноценного клиентского создания мира и доказательств реального сохранения всех комбинаций, совместимости/безопасности/аварийного восстановления. Ветка P0 не слита.

### Фактически подтверждённый CI

На [run 44279950983](https://github.com/DimaGreat17/CivCraft-Reborn/actions/runs/44279950983) GitHub Actions job `compile` завершён `success`, в том числе шаги `Static manifest validation`, `Compile and unit test foundation`, `Smoke-test dedicated server boot and persisted settings`, `Upload compiled artifacts`. Это реальное свидетельство прохождения шагов в **данном запуске**; текущая HEAD после него и все будущие изменения отдельно не проверялись этим аудитом. CI smoke ищет логи `entrypoint loaded`, `Overworld 10000x10000 blocks` и `Done`; **не** запускает последовательность stop/restart/compare и **не** проверяет все шесть world settings.

### Состояние веток

`main...feat/p0-foundation-compatibility`: `diverged`, **ahead_by 28 / behind_by 89** на момент сравнения. Отображаемый PR #6 — **WIP**, в GitHub описаны незавершённые критерии. Наличие diverged не доказывает merge conflict, но требует актуализации ветки и повторной CI до merge. **Не сливать до исправлений и проверки.**

## 2. Что на самом деле присутствует в исходниках ветки P0

| Компонент | Путь в feature branch | Наблюдаемое состояние |
|---|---|---|
| Fabric и manifest | `build.gradle`, `gradle.properties`, `src/main/resources/fabric.mod.json` | Параметры 26.3, JDK 25, Loader 0.20.1, API 0.166.2+26.3, Loom 1.19.4, Gradle 9.8; CI использует установленный Gradle |
| Entry point | `src/main/java/dev/civcraftreborn/CivCraftReborn.java` | Реальный `ModInitializer`, регистрируется server lifecycle bootstrap |
| Presets/modes | `foundation/WorldPreset.java`, `SessionMode.java` | SMALL=10000, MEDIUM=15000, LARGE=25000; ENDLESS_SANDBOX / FINITE_VICTORY |
| World settings | `foundation/WorldSettings.java` | `record(schemaVersion,preset,mode)`, Codec, reject unknown schema/null; `isVictoryEvaluatorAvailable() == false` |
| First-run defaults | `foundation/FoundationDefaults.java` | Временный файл `config/civcraft-reborn-world-defaults.properties`, применяется при первом `SavedData` |
| Persistence | `foundation/WorldSettingsSavedData.java` | `SavedDataType`, `getDataStorage().computeIfAbsent(TYPE)`, dirty при `fresh` |
| Border | `foundation/FoundationServerBootstrap.java` | `SERVER_STARTED` → `getWorldBorder().setSize(preset.sideBlocks())`, без явного задания центра |
| Tests | `src/test/.../WorldSettingsTest.java` | Проверяют typed options и файл defaults, schema/null и несуществующую victory; не настоящие миры |
| CI | `.github/workflows/p0-compatibility.yml` | build + JUnit + один 110 s dedicated server boot и grep логов |
| Manifest static check | `scripts/validate_p0_manifest.py` | Проверяет фиксированные значения/entrypoint; НЕ runtime |
| Release docs | `docs/04-architecture/P0_IMPLEMENTATION_STATUS.md` | Ветка сама перечисляет 7 крупных незакрытых блокеров |

**Главная архитектурная деталь:** `WorldSettings` содержит только `schemaVersion, preset, mode`; P0-06 также требует `WorldId` и `seed` с проверкой, но в текущем record этого нет. Их может предоставлять vanilla world metadata; это надо **доказать ссылкой на авторитетное поле/контракт**, а не молча добавлять дубли.

## 3. Матрица требований P0-01…10

Обозначения: `PARTIAL` — код есть, доказательства полноты нет; `BLOCKED` — критичная функция отсутствует или не доказана; `EVIDENCED_SUBSET` — узкий положительный CI.

| Req | Требование | Итог | Причина и остающаяся приёмка |
|---|---|---|---|
| P0-01 | Client + dedicated mod load | PARTIAL | CI dedicated PASS в указанном run; клиентский запуск и доказательство текущего binary отсутствуют |
| P0-02 | Выбор 10k/15k/25k на world creation | BLOCKED | Типизированные константы есть; клиентского UI выбора нет, только предварительный файл config |
| P0-03 | Два режима + шесть комбинаций после restart | BLOCKED | Enum и config parsing протестированы, но 6 реальных save→stop→restart→rejoin не выполнены |
| P0-04 | Честное отображение неработающей FINITE победы | BLOCKED | `isVictoryEvaluatorAvailable=false`, серверный WARN, но нет GUI предупреждения и утверждённой пользовательской политики |
| P0-05 | Серверный WorldBorder | PARTIAL | `setSize` присутствует, smoke default 10000 логируется; центр, край, build/explore, spawn и restart остальных presets не проверены |
| P0-06 | Versioned world scoped settings + WorldId/seed, recovery | PARTIAL/BLOCKED | Codec/SavedData/schema guard есть; явной контрактной трассировки WorldId/seed, миграции, backup и recovery нет |
| P0-07 | Authoritative read/write/security | PARTIAL | Bootstrap выполняется на сервере; спуфинг клиента и разрешения не испытаны; модификация config после world create не должна менять мир |
| P0-08 | Create/join/save/stop/restart/rejoin | BLOCKED | В workflow всего один запуск сервера, без проверенного restart сравнения |
| P0-09 | Logs/unit/integration negative tests/evidence | PARTIAL | JUnit, manifest, CI dedicated smoke; нет общей интеграционной матрицы и error injection |
| P0-10 | Truthful feature disclosure | PARTIAL | В PR ясно описано отсутствие NPC/победы; GUI/changelog/конечная конфигурация релиза не готовы |

**Итого:** полностью закрытых P0-01…10 по строгому релизному критерию **0/10**; это не означает отсутствие полезного выполненного кода или провал доступного CI. Требуется комплексная приёмка, а не новый слой P1.

## 4. Приоритет исправлений

### BLOCKER-A — создание мира / FINITE disclosure

Реализовать клиентский интерфейс выбора одного из 3 размеров и режима с понятной видимой маркировкой неактивной конечной победы. Выбранные параметры сериализуются в **конкретный мир** авторитетным сервером при создании, а не используются только из глобального файла defaults. Конкретную пользовательскую политику не маскировать как уже одобренную — вынести на release-gate владельца.

### BLOCKER-B — шесть последовательностей actual-world persistence

Для каждой пары SMALL/MEDIUM/LARGE × ENDLESS/FINITE создать отдельный реальный мир; проверить `SavedData`, runtime WorldBorder, stop/save, два restart, повторное чтение `preset/mode`, неприменимость изменённых глобальных defaults к старому миру. Сохранить журналы с именем/seed/world config, сохранить результаты в repo artifacts. Unit parsing tests недостаточны.

### BLOCKER-C — border center / spawn / edge

Зафиксировать центр WorldBorder как реальную игру-контракт (явный setCenter или доказанный vanilla default). Для каждого размера: world spawn, строение/движение у предела, незаконная попытка взаимодействия вне границы, dedicated vs singleplayer parity. Не путать длину стороны и радиус.

### BLOCKER-D — versioning, recovery, identity

Привязать `WorldId/seed` к vanilla authoritative source либо проектному world settings, задокументировать сравнение при rejoin. Протестировать неизвестную схему, повреждённые NBT/SaveData и прерванную запись с резервной копией: **никакого тихого возврата к defaults** на существующем мире. Миграции schema v1→future должны иметь контракт/путь безопасного отказа.

### BLOCKER-E — security / CI / release

Тест отрицательных попыток изменения world size/mode клиентом (либо документально/тестово доказать отсутствие разрешённого сетевого пути). Интегрированный клиент + dedicated smoke, воспроизводимый Gradle wrapper (в перечне изменённых файлов PR отсутствуют `gradlew`/`gradle/wrapper/*`), manifest версии и лицензии. CI workflow сейчас установлен на Gradle 9.8 отдельно — это менее автономно воспроизводимо, чем wrapper.

### BLOCKER-F — синхронизация PR с main

Feature branch `diverged` от `main`; перед слиянием обновить base, проверить конфликты и rerun CI. Документация на `main` в `README.md` всё ещё утверждает, что «no gameplay code» и версии не закреплены: для незлитого PR это правда о `main`, но при merge README следует обновить честно на status P0 foundation (без «готовой игры»). `docs/04-architecture/G3_P0_TOOLCHAIN_FEASIBILITY_PLAN.md` в `main` также представляет setup как лишь desktop candidate: после получения актуальных результатов внести раздел **VERIFIED SUBSET**, не удаляя исторический plan.

## 5. Рекомендуемая последовательность работ

| Очерёдность | Пакет | Exit criterion |
|---|---|---|
| 1 | Сверка branch/main и актуальный CI latest feature HEAD | Evidence текущей версии и отсутствие конфликтов |
| 2 | World creation UI и честная FINITE маркировка | Игрок создаёт все 6 комбинаций без ручного редактирования config |
| 3 | Интеграционные world save/restart + border tests | Все 6 комбинаций переживают 2 restart; edge cases |
| 4 | Schema/identity/crash tests + client-security negative | Нет сброса настроек мира и spoof |
| 5 | Client/dedicated matrix, reproducible artifacts/licenses | PASS с точными зависимостями и log links |
| 6 | P0 scope/risk review → user acceptance → release tag | P0 официально принят; только после этого возможен P1 G4 |

## 6. Scope ограничения для Codex / реализатора

- **Разрешено в отдельном явно утверждённом P0 development scope:** дополнять только foundation/world-creation/border/persistence/diagnostics/testing; не писать NPC, города, финальную победу, claim, экономику, жильё и AI.
- **Эта запись — аудит, а не распоряжение на реализацию.** Изменять gameplay/менять версию Minecraft или выполнять merge за владельца нельзя.
- После доработки требовать evidence с конкретным ref/commit, а не только описанием в PR body.

## 7. Verdict

**P0 0.1 Foundation: IMPLEMENTATION IN PROGRESS / RELEASE BLOCKED.** Пройденный CI smoke подтверждает ранний фундамент. Но требований на world creation, persisted six-way world matrix, GUI disclosure, full border validation и crash recovery ещё не хватает. PR #6 не объединять, 0.1 не помечать готовой, P1 не начинать. Следующая работа — исправления перечисленных P0 блокеров с последующей проверкой и отдельной приёмкой владельца.

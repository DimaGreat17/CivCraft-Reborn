# G3 / P1 — Housing на Fabric: реализуемость, риски, план технических проверок

**2026-11-18 · DESK REVIEW / NO CODE / NOT BUILD VERIFIED.** Это **план проверки**, а не подтверждённая работоспособность на конкретной версии Minecraft/Fabric. P0 уже имеет отдельный [toolchain feasibility plan](G3_P0_TOOLCHAIN_FEASIBILITY_PLAN.md), где версия игры/Loader/API/Loom/Java остаётся кандидатом. Не фиксировать P1-API сигнатуры до версии и локальной сборки.

## 1. Границы

Требуется единый серверный housing-service: ROOM, PHYSICAL/CELL, сегментация HYBRID_ROOMS, PRIVATE_ONLY, предварительные 4 клетки/NPC, назначение HOME-01, комфорт и диагностика. Сохраняем отдельную domain model (G2-10), Fabric — адаптер к BlockState/миру/сетевой репликации. Не добавлять новые специальные блоки для регистрации домов, не менять P0.

## 2. Подтверждаемые официальной документацией механизмы (только desk review)

| Возможность | Документальное основание | Что НЕ доказано |
|---|---|---|
| `BlockState` и физическая геометрия | Для Yarn существует метод получения `getCollisionShape(...)` → `VoxelShape` в опубликованных версиях API | Точная сигнатура/поведение на пока не закреплённой 26.x toolchain; возможность стояния конкретного NPC |
| Fabric events / custom event | Fabric docs: events; в отдельных местах без API hook нужен mixin или собственный event | Наличие **одного** полного события для всех изменений мира, включая жидкость, поршни и другие моды |
| Saved Data | Fabric docs: сохраняемые данные между сессиями | Конкретная реализация версии, формат, crash-consistency и миграции собственных схем |
| Data Attachments | Fabric docs: persistent attachments к entity/block entity/level/chunk | Соответствие нашей модели глобальных RegionId/операционного журнала, выбранный способ хранения |
| Networking | Fabric docs: клиентские/серверные пакеты и CustomPacketPayload | Точные packet types/permissions/availability после pin версии |
| Block entities | Fabric docs: блоковые сущности хранят и сериализуют состояние | Housing нельзя без нужды привязывать к новому обязательному блоку из-за D-050/D-053 |

Источники: [Fabric Events](https://docs.fabricmc.net/develop/events/), [Fabric Saved Data](https://docs.fabricmc.net/develop/serialization/saved-data), [Fabric Data Attachments](https://docs.fabricmc.net/develop/serialization/data-attachments), [Fabric Networking](https://docs.fabricmc.net/develop/networking), [Fabric Block Entities](https://docs.fabricmc.net/develop/blocks/block-entities), [Yarn BlockState collision shape reference — versioned example](https://maven.fabricmc.net/docs/yarn-25w42a%2Bbuild.2/net/minecraft/block/AbstractBlock.AbstractBlockState.html).

**Ограничение доказательства:** эти справочные страницы поддерживают существование примитивов, но не доказывают корректность полного Housing, ни совместимость API в конкретной выбранной версии. Нельзя заменять запуск dedicated-server ссылками.

## 3. Архитектурные адаптеры (выбранное проектное направление)

```text
Fabric WorldChangeObserver ─┐
Fabric BlockGeometryReader ─┤
Fabric PathProbe/Navigation ┤  => HousingWorldSnapshotAdapter
Fabric ClaimRightsAdapter ──┘            |
                                         v
  HousingRegionDiscovery -> HousingCellAllocation -> HousingValidation
                                         |
                       HousingCapacity + HousingComfort
                                         |
                  HousingAssignments (G2-01 HOME-01)
                                         |
     Persistence/Journal + DiagnosticsSync + Metrics
```

Зависимости внутри domain-engine должны идти через интерфейсы, без обращения к `World` или `ServerLevel` из правил COMBINED/CELL. Архитектура — кандидат G3, согласование окончательной формы API не проводилось.

## 4. Семь технических проверок (SPK-01…07)

| Spike | Что проверить в **изолированной будущей тестовой ветке** | Критерий доказательства |
|---|---|---|
| SPK-01 Collision | Полный блок, плита, лестница, ступени, ковёр, люк, жидкости, модовый VoxelShape, размеры NPC | Корректная CELL/PHYSICAL пригодность в singleplayer и dedicated, равные результаты |
| SPK-02 World changes | Игрок, NPC, поршень, взрыв, огонь, жидкость, чужой мод меняют структуру | Изменение вызывает корректную инвалидизацию; не пропускается опасная модификация |
| SPK-03 Room segmentation | Открытая спальня, две комнаты, общий коридор, два этажа | Стабильная HYBRID_ROOMS классификация и отсутствие двойной площади |
| SPK-04 Save/reload | Сохранение валидного жилья и split/merge, crash между reserve/commit | Никаких двойных BedId/CitizenId/slots, consistent journal |
| SPK-05 Performance | 10/100/1000 кандидатных регионов, массовые изменения блоков, многоярусные дома | Замер p95 check_ms, queue depth, mspt delta и bound на сканирование; численные цели ставятся после hardware benchmark |
| SPK-06 Unloaded/virtual | Дом пересекает незагруженный чанк, перевод NPC loaded↔virtual | Нет force-load/ложного роста capacity, повторная проверка безопасна |
| SPK-07 Security/UX | Клиент подделывает статус/право и запрашивает чужую диагностику | Нет доверия клиенту; нет утечки чужих координат, понятные причины отказа |

**Ни один spike не запущен; ни один результат не измерен.** Для выполнения необходим принятый P0 и утверждённая работа над соответствующим P1/изолированным экспериментом. Эта запись сама по себе не даёт permission на код.

## 5. Риски и решения

| Риск | Влияние | План снижения |
|---|---|---|
| Несуществующий/неполный Fabric event для изменений блоков | Устаревший жилищный кеш | Аудит всех путей изменений, targeted mixins при необходимости; либо консервативное подтверждение по ревизиям |
| Разница коллизии и фактического пути NPC | Завышенная вместимость | Nav probe с реальным габаритом/маршрутом, G3 tests |
| Сквозные/открытые планировки | Неверная классификация комнат | bounded graph + безопасное UNKNOWN, диагностика игроку |
| Выгруженные чанки | Ложные новые слоты или force-load | Snapshot-only + PENDING, запуск после естественной загрузки |
| 1000 комнат пересчитываются одновременно | Просадка TPS | dirty budget, coalescing событий, priority queue, performance gate |
| Частичная запись assignment | Двойные жильцы | Revision CAS, idempotency, journal reconciliation |
| Изменение версии правил | Сломанные saved worlds | Versioned schema and migration tests, no destructive silent eviction |
| Оценка комфорта из декора | Эксплойт ресурсов/морали | Explainable factors, нет товаров/бонусов без отдельного решения |

## 6. Последовательность будущей верификации

1. После **официальной приёмки P0** зафиксировать Minecraft/Fabric/JDK/Loom/Gradle версии и сохранить build evidence.
2. Утвердить **G4 P1 scope**, исключения и тестовую ветку; убедиться, что G0/исторический аудит и лицензии не блокируют нужные части.
3. Реализовать только изолированные SPK проверки в отдельном PR без скрытого запуска полного P1.
4. Записать точные API сигнатуры, mapping, значения, stack traces, серверные логи, количество проверок и измеренные mspt.
5. Принять/переделать G3 контракт, а затем реализовывать P1 последовательно: world adapter → ROOM/CELL → region allocation → HOME-01 → comfort → diagnostics → multiplayer/load tests.

## 7. Формат результата каждого spike

`SpikeId, RepoCommitSHA, Branch, MinecraftVersion, LoaderVersion, FabricAPIVersion, Mappings, JavaVersion, Gradle/Loom, Environment, TestWorld/Seed, Scenario, Expected, Actual, Logs, PerformanceEvidence, Verdict(PASS/FAIL/BLOCKED), Date`.

**Текущий вывод:** технические примитивы в экосистеме существуют, но комплексная система **не верифицирована** в Minecraft. Никакого утверждения «Fabric полностью поддерживает это без mixins» или «тесты прошли» нет.

## Ссылки на контракты

- [P0 toolchain candidate](G3_P0_TOOLCHAIN_FEASIBILITY_PLAN.md)
- [G2-10 integrated housing](../03-system-design/G2_P1_HOUSING_INTEGRATED_DOMAIN_CONTRACT.md)
- [Housing UX and acceptance](../05-roadmap/P1_HOUSING_DIAGNOSTICS_AND_ACCEPTANCE_MATRIX.md)
- [P1 patch plan](../05-roadmap/PATCH_DEPENDENCY_AND_ACCEPTANCE_PLAN.md)

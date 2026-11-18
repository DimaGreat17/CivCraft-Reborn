# G2-10 — Единый доменный контракт жилья NPC (P1 / 0.2)

**Статус: DESIGN_SELECTED_DELEGATED / DRAFT / NOT FROZEN (2026-11-18).** Консолидация D-053…D-066 и HOME-01, НЕ новый релизный freeze, НЕ код и НЕ отдельное утверждение владельцем каждой детали.

## 1. Обязательная трассировка

| Источник | Обязательный принцип |
|---|---|
| D-053 ROOM | Физическая кровать, укрытие, доступ, безопасность, права; нет регистрации дома игроком |
| D-054 COMBINED | Кровати и пригодный пол совместно ограничивают вместимость, комфорт — отдельно |
| D-055 FLOOR | Подсчитываем площадь пола, не объём воздуха |
| D-056 preliminary balance | 4 проверенные клетки пола на NPC — для будущего тестирования |
| D-057 PHYSICAL | Реальная геометрия опоры, проходимость, безопасность, включая пригодные плиты/ступени |
| D-058 CELL | Одна валидная уникальная клетка даёт один блок², без дробной площади |
| D-059 PRIVATE_ONLY | Только собственные клетки жилой зоны; общие коридоры и лестницы без вклада в вместимость |
| D-060…D-063 delegated | HYBRID_ROOMS, однократный владелец клетки, этажность, безопасный merge/split |
| D-064…D-066 delegated | Отдельный объяснимый комфорт, связь только с удовлетворённостью жильём, пересчёт и persistence |
| G2-01 HOME-01 | Уникальный CitizenId, HousingSlotId, резерв, право и атомарное назначение |

## 2. Авторитетные агрегаты (кандидатная схема)

- `Citizen` и `HousingAssignment`: агрегаты G2-01, единственный доменный владелец назначения.
- `HousingRegion`: `RegionId, SettlementId, Dimension, TopologyRevision, State, ExclusiveCellIds[], BedIds[], AccessibleEntryRefs[], RightsRevision`.
- `BedAnchor`: нормализованная физическая кровать; ровно один `BedId` на двойной Minecraft bed, связь с одним `RegionId`.
- `FloorCell`: канонический `FloorCellId` для проверенной поверхности X/Y/Z/SurfaceKey, `PHYSICAL`-доказательство и `ownerRegionId?`.
- `TransitRegion`: общие проходы/лестницы для пути, без кредитования жилья.
- `HousingValidationSnapshot`: `WorldRevision, TopologyRevision, RulesVersion, Reasons[], CheckedGameTime, Completeness`.
- `HousingCapacitySnapshot`: `ValidBedCount, ExclusiveEligibleCells, Capacity, ExistingAssignments, AvailableForNewAssignment`.
- `HousingComfortAssessment`: `Band, FactorEvidence[], RegionRevision`; никакого поля `CityHappinessDelta`.
- `HousingOperationJournal`: `OperationId, RegionId, ExpectedRevision, Type, Outcome, ActiveGameTime`.

Это схема-кандидат, не предписание конкретного формата хранения Java.

## 3. Один путь проверки, одно право на решение

`ObservedBlockChange → DirtyRegionQueue → BoundedWorldSnapshot → ROOM/PHYSICAL validation → HYBRID_ROOMS topology → PRIVATE_ONLY cell attribution → COMBINED capacity → Comfort assessment → HOME-01 reconciliation → server diagnostics`.

### Предусловия

- Мир/задействованные чанки доступны для достоверной проверки; отсутствие данных не может создавать новую площадь.
- Сервер определил законные права поселения и доступа к кроватям.
- Нет конфликта канонических `FloorCellId`, `BedId`, `RegionId` и `HousingSlotId`.
- Граница сканирования ограничена; если не доказана пригодность целой релевантной области, статус `PENDING_VERIFICATION`.

### Результат и инварианты

- `Capacity = min(validBeds, floor(privateVerifiedCells/4))` **с предварительной нормой D-056** и только при валидном ROOM. Для новых назначений при невалидном/неопределённом ROOM доступность равна нулю; существующие жильцы не удаляются.
- `AvailableForNewAssignment = max(0, Capacity - AssignedAndReservedCount)` (кандидат при единственном журнале резервов).
- Клетка принадлежит не более чем одному жилому региону; транзитная клетка не принадлежит жилой комнате.
- Две части кровати дают один BedId и не более одного слота.
- `ComfortBand` не меняет `Capacity`, `REST` или `FOOD` и не предоставляет city-happiness бонус автоматически.
- Игрок/NPC не может подменить серверный `Validated` или `ComfortBand` сетевым сообщением.

## 4. Переходы состояний

| Состояние | Значение | Новые назначения |
|---|---|---|
| `PENDING` | Новый кандидат, требуются полные данные | Нет |
| `VALID` | ROOM, права и пространство проверены | Да, в пределах свободных мест |
| `DIRTY` | Изменение блоков, кеш устарел | Нет до переоценки |
| `INVALID` | Не соответствует ROOM/правам | Нет |
| `CONFLICT` | Два региона претендуют на одну клетку/кровать или несогласованные ревизии | Нет до разрешения |
| `REASSIGNMENT_REQUIRED` | Жильё испорчено или вместимость меньше занятости | Нет, жители сохраняются |

Имена/граф переходов — техническая кандидатная модель, таймеры и гарантия доступности нуждаются в G3-проверке.

## 5. HOME-01 и нетривиальные изменения

1. `ReserveHousing(CitizenId, SlotId, ExpectedRevision, OperationId)` проверяет актуальную валидацию и права; idempotent.
2. `CommitMove` переносит связь со старого слота на новый без двойного проживания.
3. `RegionSplit/Merge` переносит устойчивые BedId/CitizenId, а устаревшие RegionId архивирует/сопоставляет; не оставляет phantom bed/slot.
4. При ухудшении жилья `NeedsEvaluator` получает `HOUSING/REST/SAFETY` события в соответствии с реальным нарушением, не удаляет гражданина.
5. `NeedsEvaluator` рассматривает `ComfortBand` отдельно и только для удовлетворённости жильём; глобальное счастье города остаётся другим доменом.
6. В unloaded/virtual режиме NPC не материализуется ради перепроверки дома. Последнее подтверждённое состояние хранится с ревизией; **для новых заселений** требуется актуальная достоверность.

## 6. Ошибки, последствия, журнал

| Error / ReasonCode | Причина | Действие |
|---|---|---|
| `NO_BED` | Отсутствует пригодная кровать | Не выделять слот |
| `INSUFFICIENT_PRIVATE_CELLS` | Проверенной собственной площади меньше нормы | Подсказать площадь, не использовать коридор |
| `NO_SHELTER` | Укрытие не соответствует условиям ROOM | Указать дефект |
| `NO_ACCESS` | Не существует действительного маршрута | Подсказать блокировку |
| `UNSAFE_SURFACE` | Физически непригодная поверхность | Исключить конкретную клетку |
| `TRANSIT_ONLY` | Общая лестница/коридор | Не прибавлять площадь |
| `RIGHTS_DENIED` | Нет доступа/законного размещения | Не заселять |
| `UNLOADED_BOUNDARY` | Данные мира неполны | `PENDING`, без force-load |
| `CELL_OWNER_CONFLICT` | Одна клетка двум регионам | Без двойного начисления |
| `STALE_TOPOLOGY` | Ревизия изменилась | Перепроверка, запрет stale commit |
| `COMFORT_UNKNOWN` | Нет надёжной оценки качества | Не начислять положительный комфорт |

## 7. Операционные требования (кандидаты)

- Server-thread reads of world state; никакого небезопасного параллельного обращения к live world.
- Snapshots для вычислений могут быть вынесены с main thread **только после подтверждения snapshot-immutability**, commit возвращается на серверный поток с проверкой ревизии.
- Детектировать **все** изменения, важные для геометрии: не только действия игрока, но и физика, жидкость, взрывы, пожар, механизмы, моды и worldgen. Не полагаться на единственный event block break.
- Обход и dirty-очередь ограничены. Никаких force-load и полного обхода всех домов каждый тик.
- Persistence: версия правила/схемы, пересчёт после несовместимых обновлений, журнал идемпотентных операций.
- Метрики: `candidate_rooms`, `verified_regions`, `dirty_queue_depth`, `check_ms_p95`, `checked_cells`, `pending_regions`, `assignment_conflicts`, `comfort_rechecks`, `mspt_delta`.

## 8. Матрица интерфейса и приёмки

Сервер отдаёт `HousingDiagnosticSnapshot` с `RegionId`, status, rights, bed counts, private cell count, capacity, assigned/reserved, comfort, reasons, evidence revision. UI не отправляет «сделать пригодным». Для детальной визуализации клеток требуется permission check и ограниченная выдача данных; не раскрывать чужие приватные координаты.

### Пакет обязательных тестов

- **Критичные функции:** ROOM-01…14; CELL-01…14; AREA4-01…12; HREG-01…24; HSEG-01…15; HC-01…16.
- **Интеграция:** один и тот же дом загружен/выгружен; crash между `ReserveHousing` и `CommitMove`; конкурирующие изменения двери и assignment; смена прав земли; перенос двухблочной кровати; split/merge при полной занятости; повторная миграция после перезапуска.
- **Нагрузочные:** множество NPC/комнат, массовая перестройка, пользовательские модовые блоки, сохранение сервера при большой dirty-очереди.

Тесты выше определяют проектную **приёмку**, но не являются пройденными тестами.

## 9. G3/G4 блокеры

1. Реально доступные Fabric hooks на target Minecraft версии для **полного** наблюдения изменений блоков.
2. Точная физическая проверка проходимости NPC, коллизий плит/ступеней, дверей и модовых блоков.
3. Проверка производительности, лимита комнаты/сканирования и хранения состояний.
4. Сохранения, миграции, правовые ограничения и взаимодействие с G2-01.
5. Игровая калибровка 4 клетки/NPC и комфорта без новых экономических эффектов.
6. Внешние источники CivCraft, аудит G0 и лицензирование, если использован исторический игровой контент.

**Не начинать P1-код** до приёмки P0, утверждённого G4 и технического доказательства проходимости ключевых контрактов.

## Смежные документы

- [G2-01 Citizen/Needs/HOME-01](G2_01_CITIZEN_JOBS_AND_MIGRATION.md)
- [ROOM D-053](../05-roadmap/P1_ROOM_HOUSING_RECOGNITION_CONTRACT.md)
- [CELL D-058](../05-roadmap/P1_CELL_FLOOR_ACCOUNTING_CONTRACT.md)
- [Housing Regions D-060…D-063](../05-roadmap/P1_HOUSING_REGION_SEGMENTATION_AND_RECOVERY_DESIGN.md)
- [Comfort D-064…D-066](../05-roadmap/P1_HOUSING_COMFORT_AND_NEEDS_DESIGN.md)
- [P1 roadmap](../05-roadmap/PATCH_DEPENDENCY_AND_ACCEPTANCE_PLAN.md)

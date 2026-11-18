# G2-01 — Контракты граждан, потребностей, жилья, поручений и миграции

**2026-11-18 · DRAFT / NOT FROZEN**  
**Нормативная концепция:** [D-016–D-029](../../DECISIONS.md), прежде всего D-016, D-017, D-022–D-024, D-028, D-029; [GDD-02](../02-game-design/NPC_POPULATION_AND_BEHAVIOR_GDD_02.md); [GDD-01](../02-game-design/SETTLEMENT_PROVINCE_CIVILIZATION_LIFECYCLE_GDD_01.md). **Контрактные формы ниже — PROPOSED**, не новая воля владельца. Нет числового баланса и реализации.

## 1. Объекты и неизменяемые связи

| Aggregate | Предлагаемый устойчивый набор полей | Ответственный |
|---|---|---|
| `Citizen` | `CitizenId`, `LifeState`, `SettlementId?`, `PolityId?`, `HousingSlotId?`, `SkillTags`, `NeedStates`, `LocationRef`, `Version` | CitizenDomain |
| `HousingSlot` | `SlotId`, `BuildingId`, `OwnerId`, `OccupantId?`, `ReservationId?`, `Habitability`, `Version` | HousingService |
| `NeedState` | `NeedType`, `LastEvaluatedActiveTime`, `Band`, `AccumulationData` | NeedsEvaluator |
| `JobTicket` | `JobId`, `IssuerId`, `CitizenId?`, `TargetRef`, `InputReservationIds[]`, `OutputCapacityReservation`, `Progress`, `State`, `IdempotencyKey`, `Version` | JobService |
| `MigrationRequest` | `MigrationId`, `CitizenId`, `SourceSettlementId`, `DestinationSettlementId`, `DestinationSlotReservationId`, `CargoRef`, `RouteRef`, `State` | MigrationService |
| `CitizenProjection` | `CitizenId`, `EntityRef?`, `ProjectionState`, `LoadedRegionRef?`, `Revision` | SimulationCoordinator |

`Citizen` — личность в домене, **не** Entity Minecraft. Реализация полей и БД требует G3.

**Однозначность:** один активный дом, один текущий исключающий JobId, один материализованный EntityRef; отсутствующий EntityRef сам по себе не является смертью. Состояние запасов пищи принадлежат экономике G2-03, не `NeedState` как бесконечные virtual items.

## 2. Citizen LifeState

`REGISTERED → ALIVE → {MIGRATING | INCAPACITATED | DECEASED | DEPARTED}`.

- `REGISTERED` — создаётся в рамках однократного worldgen seed или в дальнейшем из легальной миграции/демографической механики, если отдельно утверждена. Повтор seed key не создаёт нового CitizenId.
- `ALIVE` — разрешены работа, отдых, еда и законное проживание.
- `MIGRATING` — та же личность во время перемещения; гражданство и фактическое место находятся в переходном состоянии, правила пересчёта формализуются G3.
- `INCAPACITATED` — временное нерабочее состояние как возможный будущий вариант, пока не привязывать к болезни.
- `DECEASED` и `DEPARTED` — терминальные для данной жизненной записи; причины смерти/убыли не вводятся без отдельного GDD. Статус не восстанавливается при загрузке физического чанка.

## 3. Потребности и реакция

**Утверждённые категории:** `FOOD`, `HOUSING`, `REST`, `SAFETY`; вода в базовой версии — доступность источника, не расходуемые литры (D-017). Числовые шкалы и минимальные задержки — `BALANCE_TBD`. Привязка к реальному wall-clock при выключенном сервере запрещена (D-028).

Предлагаемый автомат потребности: `SATISFIED → WARNING → CRITICAL → RECOVERING → SATISFIED`; переходы — на авторитетном `LogicalGameTime`, не при каждом рендер-кадре. Событие `NeedCritical` формирует запрос на пищу/жильё/отдых/безопасность. **Не** допускается необратимая смерть или отъезд сразу после одного WARNING. Критичность может прерывать текущую работу через корректную паузу и освобождение ресурсов.

### Контракт NEED-01 — получить питание

- **Trigger:** поселение/житель фиксирует потребность в порции.
- **Actor:** CitizenController либо городской сервис питания; право потреблять соответствует реальному договору/владельцу.
- **Preconditions:** Citizen ALIVE; доступный пищевой StockLot и действующий маршрут выдачи; нет другой активной выдачи с тем же `OperationId`.
- **Reserve:** настоящая доступная порция/выдача из G2-03; резерв не делает порцию съеденной.
- **Commit:** по подтверждённой выдаче/потреблению `FoodConsumed` уменьшает настоящий баланс, изменяет потребность и пишет событие ровно один раз.
- **Failure:** пустой склад → `INSUFFICIENT_STOCK`, житель остаётся голодным, создаётся снабженческий запрос; пропавший маршрут/разрушенная кухня → пауза/перепланирование, без списания в пустоту.
- **Tests:** NPC-04, NPC-06, NPC-07, ECO-02, ECO-06.

### Контракт REST-01 — восстановление сил

- **Trigger:** отдых в свободный/экстренный период.
- **Preconditions:** Citizen ALIVE, в доступном подходящем месте сна/укрытии по выбранному правилу; активное время запущено.
- **Progress:** только активное игровое время, согласование с загруженной или виртуальной симуляцией.
- **Commit:** `RestCompleted` ровно один раз для интервала; увеличение работоспособности без копирования материальных ресурсов.
- **Failure:** недоступный дом/война → временное укрытие и состояние `REST_UNAVAILABLE`, без мигновенного исчезновения NPC.
- **Tests:** NPC-01, NPC-08, NPC-10.

## 4. Жильё

### Контракт HOME-01 — поселить

`REQUESTED → SLOT_RESERVED → ASSIGNED` либо `FAILED/CANCELLED`.

**Input:** `CitizenId`, `SlotId`, `ActorId`, `ExpectedRevision`, `OperationId`.  
**Checks:** слот пригоден, живущий гражданин вправе там находиться, собственник согласен, у слота нет занятого/зарезервированного другого пользователя.  
**Commit:** освобождение старого `HousingSlotId` и занятие нового — единая согласованная запись, предотвращающая одновременно два места.  
**Failure:** уничтоженное здание/два претендента → одна операция побеждает; другая получает `CAPACITY_RESERVED`, исходное жильё не теряется.  
**Test:** NPC-11; два игрока и NPC одновременно заселяют одно место — не больше одного успешного результата.

**Внешний мир:** физическое существование здания и точного блока спального места должно быть подтверждено Minecraft adapter; orphan slots не превращаются в бесконечную вместимость.

## 5. Поручения и профессии

Профессия/навыки — свойство человека; `JobTicket` — конкретная работа; место работы и права — отдельные сущности. Планировщик города назначает работы, NPC выполняет локальные действия (D-023).

### Job state machine (PROPOSED)

`CREATED → VALIDATED → RESERVED → ASSIGNED → IN_PROGRESS → READY_TO_COMMIT → COMPLETED`

Дополнительные исходы: `PAUSED` (можно восстановить), `CANCELLED` (до необратимого этапа), `FAILED` (явная ошибка с компенсацией). Никакое состояние не самообеспечивает создание предметов; выход фиксирует G2-03.

### Контракт JOB-01 — сформировать заявку

- **Actor:** город/законный наниматель; player и NPC проходят одни правила (D-022).
- **Checks:** разрешённый участок, реальный доступ к исходным материалам, технология/рецепт в статусе VALIDATED, работоспособность места, свободная ёмкость выхода, разрешение тратить средства.
- **Reserve:** входные предметы, инструмент/износ (если рецепт требует), рабочее место, доступный работник и выходная ёмкость через однозначные `ReservationId`.
- **Failure:** `INSUFFICIENT_STOCK`, `PERMISSION_DENIED`, `DEPENDENCY_MISSING`, `UNSUPPORTED_CONTENT`, `NO_WORKER`; никаких скрытых замен отсутствующих ресурсов.

### Контракт JOB-02 — исполнить и завершить

- **Trigger:** только RESERVED/ASSIGNED + активное время; NPC entity или виртуальный executor, **но не оба**.
- **Progress:** учесть рабочее время, маршрут к месту/складу, действительный инструмент и безопасность.
- **Commit:** единственный `WorkCompletionId` создаёт один выпуск и фиксирует расходы/износ; если внешний Minecraft-мир не обновился, отметить `RECONCILIATION_REQUIRED`.
- **Failure:** умер работник → stop/reassign и учёт удерживаемых товаров; исчез инструмент → `PAUSED`; уничтожена стройка → сохранение оплаченных расходов и дальнейшая компенсация по правилам, но не полный бесплатный возврат.
- **Tests:** NPC-02/03/08/13/19, ECO-03/06.

### Job ownership invariant

В каждый момент один CitizenId не может исполнять две несовместимые активные заявки. `PAUSED` удерживает только те резервы, которые действительно нужны для возобновления, с явной политикой release/expiry (G3).

## 6. Миграция

**Утверждено:** миграция — закономерный возможный результат долгого кризиса и реальный источник новых общин; не возникает бесплатных NPC (D-018/D-029).

### Контракт MOVE-01 — запрос на переезд

- **Trigger:** добровольное решение NPC/приглашение принимающей общины/затяжной кризис; одно разовое WARNING не запускает принудительный исход.
- **Inputs:** source/destination/citizen/property/permission/available housing.
- **Preconditions:** Citizen ALIVE, source owns current resident link, destination допускает жителя, свободен слот, маршрут реально допустим; если нет безопасной площадки — fail/another destination, не исчезновение.
- **Reserve:** слот в принимающем городе, допустимая вместимость грузового пути, действительные предметы; владение товарами не меняется без сделки.
- **Commit (two-phase domain candidate):** `MIGRATION_RESERVED → IN_TRANSIT → ARRIVED → MEMBERSHIP_TRANSFERRED`, после прибытия один CitizenId обновляет поселение, дом и гражданские права. Для маршрута используем G2-03 shipment/time.
- **Failure:** путь заблокирован/принимающий город потерял право/дом разрушен → return/replan или `MIGRATION_PAUSED` с явным держателем груза; никаких новых CitizenId.
- **Tests:** NPC-12/18/20, LC-14.

**Нерешено:** восстановление после уничтожения источника во время перехода, правовой статус человека в пути, точное право мигрировать через враждебную границу — сопряжение с G2-06/07.

## 7. Loaded / Virtual contracts

- `ProjectionMode = LOADED | VIRTUAL | TRANSITION`; переключение не создаёт нового CitizenId и не обнуляет JobId.
- `LOADED → VIRTUAL`: снять финальный подтверждённый snapshot позиции/груза/прогресса и дематериализовать entity **без** создания дополнительных предметов.
- `VIRTUAL → LOADED`: материализовать ровно одну entity из авторитетного состояния, согласовав предметы и задания. Виртуальная очередь больше не является одновременно активным исполнителем.
- Все неполные внешние операции после падения — не повторная выдача, а `RECONCILE`.
- Дальние работы учитывают те же затраты времени и физические ограничения; world force-loading запрещён.

## 8. Event catalogue (кандидаты)

`CitizenRegistered`, `NeedCritical`, `FoodConsumed`, `HousingReserved`, `HousingAssigned`, `JobCreated`, `JobReserved`, `JobProgressed`, `JobCompleted`, `JobFailed`, `MigrationRequested`, `MigrationArrived`, `CitizenDeceased`, `CitizenDeparted`, `CitizenProjectionChanged`. Любое событие имеет `EventId`, owner aggregate, revision, logical time, причина, инициатор.

## 9. Тестовый пакет G2-01

| ID | Сценарий | Проверка |
|---|---|---|
| G2-N01 | Два игрока/NPC бронируют последний койко-слот | Один успех, другой предсказуемый отказ |
| G2-N02 | Два задания на одного работника | Несовместимая заявка отклоняется |
| G2-N03 | Пища уничтожена после резервирования | Потребление не начислено бесплатно; корректное возмещение/ошибка |
| G2-N04 | Сохранение в состоянии JOB READY_TO_COMMIT | Повтор после краша не даёт второго предмета |
| G2-N05 | Выгрузка NPC с инструментом и предметом | Один CitizenId и один груз после materialization |
| G2-N06 | Истощение еды в далёком городе | Срабатывают поставки/кризис, не бесплатный спавн запасов |
| G2-N07 | Одновременная миграция в последнее жильё | Один успешный transfer; второй не теряет старое членство |
| G2-N08 | Игрок нанимает NPC не имея полномочий | Серверный отказ без удержания зарплаты |
| G2-N09 | Разрушение дома в момент смены региона | Нет orphan citizen или двойной записи жилья |
| G2-N10 | Выключенный сервер и повторная загрузка | Ни один NPC не отработал скрытое время |
| G2-N11 | Нападение на переносимый груз NPC | Единственный учтённый исход груза и работы |
| G2-N12 | Долгий кризис и восстановление | Город постепенно перестраивает задания; не удаляет историю |

## D-053 — ROOM housing concept approval (2026-11-18)

Housing eligibility for freely constructed Minecraft houses is now **APPROVED_CONCEPT** as an automatic, server-authoritative room check: a usable bed, shelter, physical accessibility and safety. This is a recognition prerequisite, **not** a free NPC spawn or an approved numerical score. One physical bed must not become multiple slots through duplicate discovery events; actual occupancy policy, room geometry, allocation rights, danger thresholds, and unloaded-chunk processing remain DRAFT. New housing design: [P1 ROOM recognition](../05-roadmap/P1_ROOM_HOUSING_RECOGNITION_CONTRACT.md). G2-01 housing reservations, migrations and Loaded/Virtual conservation remain in force.

## D-054 — COMBINED housing capacity and comfort (2026-11-18)

**APPROVED_CONCEPT:** доступная вместимость ограничивается количеством пригодных кроватей **и** пригодным пространством; комфорт учитывается отдельно и влияет на удовлетворённость NPC, но не создаёт дополнительных мест. Равенство HousingSlotId и CitizenId, безопасная переоценка при разрушении и запрет двойной записи сохраняются. Конкретные пространственные пороги, формулы и эффекты — PROPOSED; [детальная спецификация](../05-roadmap/P1_COMBINED_HOUSING_CAPACITY_AND_COMFORT.md).

## D-055 — FLOOR space metric (2026-11-18)

**APPROVED_CONCEPT:** для D-054 COMBINED используется подтверждённая пригодная площадь пола, а не весь 3D-объём комнаты. Кровати и ROOM-условия обязательны; вертикальная проходимость остаётся частью доступа. Норма площади, геометрические допуски и расчёт многоэтажности PROPOSED. [Спецификация FLOOR](../05-roadmap/P1_FLOOR_AREA_HOUSING_METRIC.md).

## D-056 — provisional FLOOR housing density (2026-11-18)

**APPROVED_PRELIMINARY_BALANCE:** 4 пригодных блока² пола на каждого NPC, совместно с кроватью и всеми проверками ROOM. Это параметр для P1-тестов, а не финальный баланс. Комфорт не увеличивает вместимость. [Примеры и тесты D-056](../05-roadmap/P1_FLOOR_HOUSING_BALANCE_4_BLOCKS.md).

## D-057 — PHYSICAL usable floor (2026-11-18)

**APPROVED_CONCEPT:** считать пол пригодным по реальным коллизиям, физической проходимости и безопасности для NPC; плиты/ступени не запрещены по названию блока. Норма D-056 4 блока²/NPC остаётся предварительной, конкретный вклад частичных поверхностей ещё не утверждён. [Контракт PHYSICAL](../05-roadmap/P1_PHYSICAL_FLOOR_VALIDATION_CONTRACT.md).

## D-058 — CELL accounting of physically usable floor (2026-11-18)

**APPROVED_CONCEPT:** одна уникальная физически пригодная клетка пола, успешно прошедшая PHYSICAL/ROOM, даёт ровно 1 блок²; частичные поверхности могут участвовать, но без дробного кредита. D-056 сохраняет предварительную норму 4 таких клеток/NPC. Общая площадь нескольких жилищ не может засчитываться дважды; правила распределения комнат и геометрия поверхностей PROPOSED. [CELL contract](../05-roadmap/P1_CELL_FLOOR_ACCOUNTING_CONTRACT.md).

## P1 Housing Regions and Shared Space (PROPOSED, 2026-11-18)

Design candidate: [Housing Regions and Shared Space](../05-roadmap/P1_HOUSING_REGIONS_AND_SHARED_SPACE_PROPOSAL.md). It proposes distinct living regions, transition corridors and stairs, unique floor-cell attribution, multi-floor access and safe split/merge reconciliation. **Owner approved PRIVATE_ONLY as D-059:** individual rooms receive credit only for their own validated cells; shared hallways and stairs are transit and grant no residential capacity. The other two variants are not selected. D-060…D-063 are **DESIGN_SELECTED_DELEGATED**: HYBRID_ROOMS segmentation, deterministic shared/private floor attribution, transit-only common stairs, and safe merge/split with resident reassignment. Exact algorithms, hazards and timelines are not FROZEN; see [delegated region design](../05-roadmap/P1_HOUSING_REGION_SEGMENTATION_AND_RECOVERY_DESIGN.md). Existing HOME-01 reservations and D-053…D-058 take precedence.

## 10. BLOCKED / TODO

`NPC-G2-B1`: конкретные показатели потребностей и таймеры (BALANCE).  
`NPC-G2-B2`: политики недееспособности/смерти/сроков миграции (в GDD нет чисел).  
`NPC-G2-B3`: физические Minecraft container ↔ доменный склад, допустимые пути, simulation tick и threading (G3).  
`NPC-G2-B4`: правила жилья и занятости в оккупированном городе (G2-06/07).  
`NPC-G2-B5`: нет утверждённой демографии/семей — нельзя выпускать как реализованную способность.

**Статус: G2-01 DRAFT. Не код, не полномасштабный готовый G2.**

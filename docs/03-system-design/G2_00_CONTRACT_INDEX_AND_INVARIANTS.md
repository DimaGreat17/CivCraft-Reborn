# G2-00 — Реестр системных контрактов, терминология и инварианты

**CivCraft Reborn · 2026-11-18 · DRAFT / NOT FROZEN**  
**Основание:** [33 концептуальных решения](../../DECISIONS.md); [Master GDD](../02-game-design/CIVCRAFT_REBORN_MASTER_GDD.md); [GDD-01](../02-game-design/SETTLEMENT_PROVINCE_CIVILIZATION_LIFECYCLE_GDD_01.md); [GDD-02](../02-game-design/NPC_POPULATION_AND_BEHAVIOR_GDD_02.md); [GDD-03](../02-game-design/GDD_03_ECONOMY_PRODUCTION_LOGISTICS.md). Создание спецификаций по явному поручению владельца не закрывает G0/G1/G2/G3/G4 и не разрешает реализацию игрового кода.

## 1. Реестр G2

| ID | Контракт | Статус |
|---|---|---|
| G2-01 | [Citizen / Need / Housing / Job / Migration](./G2_01_CITIZEN_JOBS_AND_MIGRATION.md) | DRAFT |
| G2-02 | [Site / Settlement / Province / Civilization lifecycle](./G2_02_SETTLEMENT_AND_POLITY_LIFECYCLE.md) | DRAFT |
| G2-03 | [Item ledger / Production / Shipment / Money](./G2_03_ECONOMY_TRANSACTIONS_AND_LOGISTICS.md) | DRAFT |
| G2-04 | [Territory/claim/permissions/construction](./G2_04_TERRITORY_PERMISSIONS_CONSTRUCTION.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |
| G2-05 | [Technology graph/eras/wonders](./G2_05_RESEARCH_ERAS_WONDERS.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |
| G2-06 | [Government/culture/legal transitions](./G2_06_GOVERNMENT_CULTURE_CITIZENSHIP.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |
| G2-07 | [Diplomacy/war/capture/revolution](./G2_07_DIPLOMACY_WAR_CAPTURE.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |
| G2-08 | [AI strategic planning/knowledge budget](./G2_08_STRATEGIC_AI_AND_KNOWLEDGE.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |
| G2-09 | [Victory/events/religion/espionage/ruins](./G2_09_VICTORY_EVENTS_NATIONS_ESPIONAGE.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |
| G2-10 | [Player UI/network/ACL](./G2_10_UI_MULTIPLAYER_COMMAND_AUTH.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |
| G2-11 | [Worldgen, seeding, session/border rules](./G2_11_WORLDGEN_SEED_BORDER_SESSION.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |
| G2-12 | [Cross-system persistence, scheduling, reconciliation and simulation budgets](./G2_12_PERSISTENCE_CLOCK_LOD_CROSS_SYSTEM.md) | DRAFT / NOT FROZEN; source and G3 blockers remain |

## 2. Источник истины и правила точности

**APPROVED_CONCEPT:** верхнеуровневые условия D-001…D-048; только действующие APPROVED_CONCEPT из DECISIONS имеют нормативный статус, остальные предложения ниже — `G2_DRAFT`. Никакие сроки, суммы, жители, процентные ставки, размеры строительных областей, частоты рабочих тиков, версии Minecraft/Fabric здесь не утверждены.

**PROPOSED_CONTRACT:** ниже описана *форма* проверки, которую ещё предстоит согласовать. Пример: `reserve → commit` — архитектурный кандидат, не доказательство конкретной ACID БД; весь мир Minecraft и внешние контейнеры не участвуют в одной реальной SQL-транзакции автоматически.

**SOURCE_UNKNOWN:** неизвестные старые рецепты, узлы исследований, чудеса, источники эмиссии и правила захвата должны быть либо закрыты по G0, либо исключены из конкретного патча **явным** решением владельца. Не придумывать legacy детали.

## 3. Термины и модель домена

| Термин | Смысл | Что не означает |
|---|---|---|
| `ActorId` | Идентификатор игрока, NPC или уполномоченной организации в команде | Права по самому факту ID |
| `CitizenId` | Постоянная идентичность NPC | Minecraft entity ID |
| `SettlementId` | Город/лагерь с реальными инфраструктурой и гражданами | Политический суверенитет |
| `SiteId` | Географическая площадка | Владелец города |
| `PolityId` | Политическая провинция/цивилизация | Казна конкретного гражданина |
| `JobId` | Единственное оплачиваемое/исполняемое поручение | Профессия или конкретная entity |
| `OperationId` | Идемпотентный ключ доменного действия | Minecraft tick |
| `StockLotId` | Учтённая партия физических предметов | Только число в GUI |
| `ShipmentId` | Перевозка реального груза | Создание новых предметов |
| `AssetOwnerId` | Законный собственник ресурсов/строения | Текущий захватчик без договора |
| `LogicalGameTime` | Монотонное активное время мира | Время выключенного сервера |

## 4. Контракт каждой команды

Каждая изменяющая мир команда обязана иметь:
`CommandId / Actor / Target / ExpectedRevision / Preconditions / PermissionCheck / ResourceReservation / TimeOrProgress / DomainEvents / CommitBoundary / Result / RetryPolicy / FailureCompensation / AuditEvidence`.

Для внешних эффектов (изменение блоков Minecraft, entity spawn, физические контейнеры) потребуется reconciliation/операционный журнал. **Псевдоконтракт atomic commit не означает, что чужие API поддерживают общий rollback.** При невозможности транзакционного эффекта — предварительно готовить обратимую операцию либо идемпотентное восстановление, запрещая повторную выплату/добычу.

## 5. Сквозные инварианты

- **INV-01:** один `CitizenId` соответствует не более чем одной физической entity-проекции в активном мире.
- **INV-02:** погибший гражданин не возрождается при materialization; ID никогда не переиспользуется.
- **INV-03:** один `HousingSlotId` занят максимум одним жителем; бронирование является определённым состоянием.
- **INV-04:** одно место выполнения и один работник одновременно не имеют несовместимых действующих `JobId`.
- **INV-05:** один `StockLotId` имеет единственное местонахождение/владельца и ровно одну доступную величину на каждый снимок.
- **INV-06:** доступные и зарезервированные количества не бывают отрицательными; отмена/повтор не удваивают товары.
- **INV-07:** `Hammers`, `Science`, `Coins`, `ItemStack`, `TradeStatus` типизированы раздельно; конверсия только по явному источнику.
- **INV-08:** каждый перенос собственности и денег имеет симметричную бухгалтерскую запись, включая отдельно разрешённые mint/burn-счета.
- **INV-09:** создание поселения/цивилизации фиксируется единожды; культура/политический владелец/историческая родина различны.
- **INV-10:** только сервер авторитетен; любой запрос NPC/игрока проходит одинаковые права/расходы.
- **INV-11:** Loaded/Virtual меняют вычислительное представление, а не цены, источник товара или время труда.
- **INV-12:** остановленный мир не производит новые ресурсы и не начисляет прогресс.
- **INV-13:** WorldBorder (Overworld) и protected claims проверяются до операций размещения/строительства.
- **INV-14:** никакая операция не разрешает неизвестную технологию или непроверенный legacy recipe.
- **INV-15:** каждый сбой имеет наблюдаемый `ErrorCode`, безопасный повтор и запись причины.

## 6. Типы отказов и синхронизация

`STALE_REVISION` — повторить команду после получения актуального состояния; `PERMISSION_DENIED` — без изменения; `INSUFFICIENT_STOCK` — без списания; `CAPACITY_RESERVED` — ждать/искать другой слот; `INVALID_TARGET` — невозможная площадка/гражданин; `DEPENDENCY_MISSING` — блокированное требование; `UNSUPPORTED_CONTENT` — неизвестный неподтверждённый рецепт; `CONFLICTING_OPERATION` — операция уже активна; `RECONCILIATION_REQUIRED` — не завершено согласование внешнего мира.

**Обязательное различие:** доменная операция может быть зафиксирована, а Minecraft-блок не обновлён из-за сбоя. Задача G3 — гарантировать предсказуемое восстановление без двойной выдачи материалов и неправомерного удаления блоков.

## 7. Границы первого набора

G2-01…12 задают **проектные системные контракты с открытыми зависимостями**, но **не** финальные рецепты, баланс, все технологии и политические алгоритмы. Не считать готовым релиз P1: нет определения точного входа worldgen, complete ACL / construction, тестов Fabric, migration format и performance budgets.

**P1 housing addendum (not a new numbered G2 system):** [G2-P1 integrated housing contract](G2_P1_HOUSING_INTEGRATED_DOMAIN_CONTRACT.md) consolidates D-053…D-066 and HOME-01. This addendum does **not** change G2 coverage 12/12 or complete/approve P1. Supporting [diagnostics](../05-roadmap/P1_HOUSING_DIAGNOSTICS_AND_ACCEPTANCE_MATRIX.md) and [Fabric test plan](../04-architecture/G3_P1_HOUSING_FABRIC_FEASIBILITY_AND_SPIKE_PLAN.md) are unimplemented DRAFTs.

## 8. Definition of Done G2

Полный G2 можно предложить к утверждению после покрытия G2-01…12, отсечения/подтверждения всех required G0 деталей для выбранного патча, сквозного согласования операций и тестов, уточнения G3 API/persistence/LOD и explicit owner signoff. Данный индекс — рабочий baseline; статус **G2 DRAFT DOCUMENT COVERAGE 12/12, NOT FROZEN OR TECHNICALLY VALIDATED**.

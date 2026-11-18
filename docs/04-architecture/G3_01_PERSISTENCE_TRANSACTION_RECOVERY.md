# G3-01 — Сохранения, денежная книга и восстановление

**2026-11-18 — PROPOSED / NOT VERIFIED ON FABRIC.** [G2-03](../03-system-design/G2_03_ECONOMY_TRANSACTIONS_AND_LOGISTICS.md), [G2-12](../03-system-design/G2_12_PERSISTENCE_CLOCK_LOD_CROSS_SYSTEM.md). Storage backend/serializers не утверждены.

## Устойчивые сущности

`WorldRecord` (world config, time, version), `Citizen`, `Settlement`, `Polity`, `Claim`, `SiteReservation`, `StockLot`, `MoneyAccount`, `JournalEntry`, `JobTicket`, `Shipment`, `Building`, `TechnologyProgress`, `War`, `Event`, `OperationLog`, `ProjectionLease`. Все имеют stable ID, revision, owner, logical time; no ID reuse.

## Журнал операций

Proposal status: `INTENT_RECORDED → DOMAIN_RESERVED → EXTERNAL_APPLY_PENDING → APPLIED_CONFIRMED → LEDGER_COMMITTED → COMPLETE`, with `RECONCILE`, `CANCELLED`, `FAILED_FINAL`. Не предполагать глобальную ACID транзакцию между Minecraft world и собственной БД. Операция на границе памяти/внешнего мира имеет durable intent, уникальный `OperationId`, ожидаемую ревизию, hash/expected target state, applied marker, правила компенсации.

**Типовой сбой**: после списания древесины server падает до смены блока строительства. Recovery читает durable operation log: если блок уже поставлен — подтверждает единственный effect; если ещё нет и есть действительные права — завершает; если состояние конфликтует — ручная/автоматическая компенсация по явно утверждённой политике. **Нельзя** просто ещё раз начислить продукцию.

## Денежная книга

При переводе 100 условных монет используются две связанные записи дебет/кредит. 100 — **иллюстрация формы**, не экономический параметр. Исходящий баланс не может стать отрицательным после резервов; `MoneyAccount` и `JournalEntry` сохраняются согласованно. Mint/burn — отдельный системный счёт с разрешённым эмиссионным policy, которой пока нет. Налоги, зарплаты и торговля проходят тот же transfer.

## Модели сохранения и миграции

`saveFormatVersion`, `contentVersion`, `sourceVersion`, `migrationId`, `checksum`, `WorldId`. Каждая миграция идемпотентна, имеет backup/restore и тест «предыдущий релиз → новый релиз → загрузка → сравнение инвариантов». При неизвестной версии запрещено переписывать сохранение как пустое. Backups с минимальной политикой retention не утверждены, бюджет диска нужно измерить.

## Конкурентность и порядок

- Один авторитетный world-state writer для Minecraft API, технически выбранный после G3 feasibility.
- Domain object optimistic revision/CAS или механика блокировок на выбранном storage; конфликт `STALE_REVISION`.
- Фоновые планировщики возвращают requests/results, не изменяют блоки напрямую.
- У каждого `JobId/ShipmentId` один исполнитель и отдельная логика retry/timeout.
- Не копировать busy-spin старого `SQLUpdate.java`.

## Recovery check cases

REC-01 после денег до товара; REC-02 после изменения блока до journal ack; REC-03 при миграции жителя; REC-04 при разгрузке каравана; REC-05 после завершения исследования и перед GUI; REC-06 при смерти entity; REC-07 после захвата столицы; REC-08 при смене версии данных; REC-09 при двух игроках у одного сундука; REC-10 при одновременных суверенных переходах.

**OPEN:** Fabric storage APIs, backend choice, atomicity boundaries, physical chest truth, cross-save migrations and recovery feasibility. Гарантии не прошли реальную проверку.
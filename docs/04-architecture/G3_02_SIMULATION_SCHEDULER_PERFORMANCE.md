# G3-02 — NPC LOD, планировщик и производительность

**2026-11-18 — PROPOSED / NOT BENCHMARKED.** D-002/D-004/D-023/D-024/D-028/D-037; [G2-01](../03-system-design/G2_01_CITIZEN_JOBS_AND_MIGRATION.md), [G2-08](../03-system-design/G2_08_STRATEGIC_AI_AND_KNOWLEDGE.md), [G2-12](../03-system-design/G2_12_PERSISTENCE_CLOCK_LOD_CROSS_SYSTEM.md).

## Цели

Один и тот же гражданин выполняет работу в физической Loaded области или в Virtual без одновременных исполнителей. Game clock движется только при работающем сервере; пауза/выключение не дают накопленных дней симуляции. WorldBorder 10k/15k/25k — география, не автоматически производительность.

## Состояния LOD

`LOADED`: entity + pathfinding/animation + domain CitizenId и JobId.  
`TRANSITION_TO_VIRTUAL`: остановить физического исполнителя, зафиксировать позицию, cargo и долю прогресса, передать lease.  
`VIRTUAL`: domain NPC и групповые операции по ограниченному числу шагов с тем же затратами труда/груза/времени.  
`TRANSITION_TO_LOADED`: приостановить виртуальное выполнение, восстановить физическую projection из authoritative snapshot, перепроверить контейнеры/блоки/задания; выдать lease только одному executor.

**Critical:** переход chunk unload сам по себе не гарантирует, что мир/сундуки уже доступны; физические side-effects требуют safe world context. При конфликте `RECONCILIATION_REQUIRED`, не дублировать.

## Уровни планирования

`CitizenController` — короткие локальные задачи; `SettlementPlanner` — снабжение, жильё, строительство, приоритетная очередь Jobs; `PolityPlanner` — ресурсы между городами, культура, tech, экспансия, diplomacy при доступном G2. Обновлять по событиям и ограниченной периодике, не пересчитывать всю карту/всех NPC на каждый tick.

## Бюджеты — параметры для проверки, НЕ выбранные цифры

`maxPhysicalNpcEntities`, `maxVirtualCitizenStepsPerTick`, `maxPlanningMillisPerTick`, `maxConcurrentPathfinds`, `maxChunkInteractions`, `maxJobQueueSize`, `maxShipmentTransitions`, `saveDurationBudget`, `msptP95`, `tpsFloor`, `memoryBudget`. Ни одно значение не фиксируется до экспериментов.

## Бенчмарк матрица

| Сценарий | Карты | Нагрузка |
|---|---|---|
| Singleplayer nearby camp | 10k, 15k, 25k | NPC сон/еда/строительство рядом |
| Dedicated server city | 10k, 15k, 25k | Отдельные игроки, job collisions, world save |
| Sparse distant states | 10k, 15k, 25k | Полностью виртуальные поселения, без force-load |
| Massive migration | 10k, 15k, 25k | Одновременные переселения и резервирование домов |
| Trade routes | 10k, 15k, 25k | Караваны рядом/вдали, переходы проекции |
| War + crisis | 10k, 15k, 25k | События, снабжение армии, смена суверенитета |
| Soak test | 10k, 15k, 25k | Длинная работа сервера с периодическими перезапусками |

Для каждой комбинации замерить MSPT (p50/p95/p99), TPS, память/GC, число entities, loaded chunks, частоту pathfinding, latency of save, throughput events/jobs, number of reconciliation errors, resource conservation. Target pass/fail thresholds согласовываются **после** измерений и возможностей ПК/серверов.

## Performance failure policies

- Нет бюджета на физический путь → отложить неприоритетное Job, не ускорить virtual job бесплатно.
- Нет времени на стратегический ИИ → перенос планирования в очередь, а не пропуск потребления пищи без следа.
- Очень дальний мир → не загружать блоки для дальних государственных событий.
- При restarts → persistent clock and job progress no catch-up.
- При игроке, телепортирующемся между дальними городами → one projection lease for all citizen/cargo IDs.

**OPEN:** реально существующий Fabric server thread API, scheduler model, sync fences, измеренные budgets, максимальные размеры поселений/государств. Не называть архитектуру производительной до замеров.
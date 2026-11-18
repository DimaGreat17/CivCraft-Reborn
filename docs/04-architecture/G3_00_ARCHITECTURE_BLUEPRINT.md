# G3-00 — Архитектурный проект CivCraft Reborn

**2026-11-18 — ARCHITECTURE PROPOSAL / NO TECH FEASIBILITY SIGNOFF.** Основание: [D-046](../../DECISIONS.md), [G2-00](../03-system-design/G2_00_CONTRACT_INDEX_AND_INVARIANTS.md), [Engineering Standards](./ENGINEERING_STANDARDS.md).

## Цель

Minecraft **Java Edition + Fabric** как целевой путь. Точную стабильную совместимую Minecraft/Java/Fabric/API/mappings версию **не фиксировать** до воспроизводимой проверки; архитектура ниже — предлагаемая, а не запущенный мод. Сервер авторитетен. Важнейшая гарантия: одно состояние граждан, денег и имущества, независимо от расстояния до игрока.

## C4-lite: контексты

| Context | Отвечает | Не должен делать |
|---|---|---|
| Client UI | Read models, запросы, карта, объяснение отказа | Мутировать world, treasury, claims |
| Network Protocol | Typed commands, permissions, version/check validation, rate limiting | Доверять client-provided roles/prices |
| Application Services | Use-cases, access checks, orchestration, rollback/reconcile statuses | Содержать Fabric-specific блоки |
| Domain Core | Citizens, settlements, polity, economy, technology, diplomacy, culture, victory | Импортировать Minecraft/Fabric API |
| Inventory & Monetary Ledger | Stock lots, account transfers, reservations, effects | Создавать внешние предметы без adapter commit |
| Scheduler / AI | Snapshots, jobs, budgets, strategic planning | Изменять live world из фоновых потоков |
| Persistence | Snapshots, journal, schema version, recovery, projection leases | Тихо удалять неизвестные поля |
| Minecraft World Adapter | Block events, entity projection, claims, legal container interactions | Принимать решения AI или source-unknown recipe |
| Content Data | Versioned technology/wonder/structure/government definitions | Незаметно смешивать V1.0.9 и позднюю wiki |
| Telemetry/Test Harness | MSPT/TPS, tracing, failure injection and regression | Выдавать документацию за прошедшие тесты |

## Главные потоки

**Job:** Client/AI command → authorization → domain snapshot → reservation → scheduler → world projection action (when loaded) or virtual effect (when unloaded) → durable journal/outbox → inventory commit → read-model event. Когда Minecraft block operation неподтверждена, outbox остаётся PENDING и повтор безопасен.

**Town:** world/site suitability → site lock → material/human reserve → physical foundation placement → persisted settlement ID → release reservations. Два одновременных актора не основывают два города на том же site.

**Loaded/Virtual:** CitizenId всегда один. Minecraft Entity — лишь временная проекция. Общий ledger и JobId обрабатываются одним lease-holder.

**No infrastructure claims:** ни одна БД или модуль ещё не выбраны и не измерены; схемы в G2 — кандидаты для G3 прототипирования без игрового контента.

## Direction of dependencies

`client → network → application → domain interfaces ← adapters (world, persistence, scheduler)`. Domain не зависит от adapter; Minecraft события превращаются в валидируемые команды/наблюдения. Content definitions прикрепляются к версии источника, выбранной механике и подтверждённым prerequisites.

## Requirements before freeze

- Скомпилировать **минимальный технический feasibility spike** на проверенной стабильной платформе только после отдельного разрешения на тестовый код или в изолированном архитектурном прототипе; без выдачи его за разработку игрового мода.
- Реально измерить Minecraft chunk lifecycle, block/container API, entity ownership, dedicated server and concurrency.
- Утвердить storage choice и восстановление после crash, и legal provenance всех внешних assets.
- Покрыть G2-01…12 подлинными API contract tests, а не только markdown чек-листом.

**Вердикт:** схема модульной архитектуры подготовлена, G3 NOT VALIDATED.
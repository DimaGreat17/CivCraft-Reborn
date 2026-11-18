# G4 — Финальная проверка CivCraft Reborn перед написанием кода

**2026-11-18 · REVIEW COMPLETE AS DOCUMENT / G4 NO-GO FOR CODE.** Проверка проектной готовности: [DECISIONS](../../DECISIONS.md), [Master GDD](../02-game-design/CIVCRAFT_REBORN_MASTER_GDD.md), [G2 index](../03-system-design/G2_00_CONTRACT_INDEX_AND_INVARIANTS.md), [G3 architecture](../04-architecture/G3_00_ARCHITECTURE_BLUEPRINT.md), [G0 audit](../01-legacy-audit/G0_COMPREHENSIVE_AUDIT_REVIEW_2026-11-18.md).

## 1. Что действительно завершено

| Артефакт | Доказательство | Verdict |
|---|---|---|
| Концептуальная политика | D-001…D-048, 33/33 GDD concept choices owner-approved | **PASS CONCEPT** |
| Иерархия GDD | Master GDD + GDD-01…13 | **PASS DOCUMENT COVERAGE; NOT FROZEN** |
| G2 контрактные разделы | G2-00 index, G2-01…12 written | **PASS DOCUMENT COVERAGE; DRAFT ONLY** |
| G3 архитектурные разделы | G3-00…03 proposal | **PASS ARCHITECTURE OUTLINE; NOT FEASIBILITY-PROVEN** |
| G0 историческая полнота | 18-gap audit / 73 building ledger / tech graph | **FAIL SIGNOFF — evidence gaps** |
| Проверка загрузки модов | Ни одна цельная сборка на выбранной современной версии не делалась | **NOT TESTED** |
| Стресс-проверка симуляции | Нет настоящих TPS/MSPT/heap/loaded chunks измерений | **NOT TESTED** |
| Подробный баланс | Не утверждены затраты, скорости, плотность, временные пороги | **OPEN BY DESIGN** |
| Права на исходные материалы | GPL root vs proprietary file notices | **UNRESOLVED; no legacy copying** |
| Утверждённые рамки P0–P7 | Patch ladder only proposed, P2→P3 dependency contradiction | **NOT FROZEN** |
| Право начинать код | Нет явного G4 authorization | **NO-GO** |

## 2. Контрольный список до первой строки игрового кода

- [x] Проектный концепт A/B/C/D обсуждён и подтверждён.
- [x] Каждая большая subsystem имеет GDD-каркас.
- [x] 12 системных областей имеют документные G2-контракты и acceptance scenarios.
- [x] G3 описывает модули, безопасность, LOD, persistence и measurement strategy.
- [ ] Закрыть **критические G0 gaps для выбранного scope P0/P1** либо оформить явное ограничение scope и подтверждение владельца. Особенно проблемы полного tech graph, building recipe provenance, licensing.
- [ ] Утвердить, какой полный **вертикальный срез** войдёт в первый патч, без пустой UI-заглушки конечной победы и без «разорванной» провинции.
- [ ] Определить и проверить конкретные версии Minecraft Java, Fabric loader/API, mappings, Java SDK и совместимости зависимостей.
- [ ] Утвердить storage backend, транзакционные границы Minecraft blocks/containers, save migration/recovery; проверить failure injection.
- [ ] Измерить reference hardware и договориться о значимых TPS/MSPT/NPC/chunk/heap budgets.
- [ ] Для **каждого** механизма активного патча утвердить численные балансы и source versions, не выдумывать неизвестную историческую формулу.
- [ ] Проверить permission matrix и LOD convergence на клиентах/выделенном сервере, с тестами после crash/restart.
- [ ] Провести межконтрактный аудит и исправить найденные расхождения (см. ниже).
- [ ] Утвердить лицензии на внешние файлы / clean-room implementation policy соблюдается file-by-file.
- [ ] Подписать G1/G2/G3/G4 freeze и явно разрешить реализацию **конкретного** патча.

## 3. Критические противоречия и проектные риски

| ID | Severity | Issue | Required decision/evidence |
|---|---|---|---|
| PRE-01 | BLOCKER for research | **66 tech labels/68 candidate edges with 7 isolated** ≠ full verified graph | Проверить исходное изображение и зависимости, статус для конкретного релиза |
| PRE-02 | BLOCKER for full legacy parity | 73 building names, only subset fully described; 6 wonder articles unavailable | Source-backed catalog per retained feature or explicit exclusion from defined patch |
| PRE-03 | BLOCKER for copyright reuse | Old repo root GPL-2.0 vs AVRGAMING all-rights-reserved notices | File-level legal assessment; no direct copying |
| PRE-04 | BLOCKER for province promotion patch | Capitol and research prerequisites required, P2 planned before research P3 | Ensure validated early tech + promotion complete in same patch, or move entire promotion |
| PRE-05 | BLOCKER for P0 finite functionality | World-mode choice at world creation but wins implemented later | Advertise unavailable state and decide creation policy until fully functioning victory patch |
| PRE-06 | BLOCKER for wartime implementation | Liberation of capital returns one town (late wiki) vs many (old runtime) | Owner selects precise policy in future war scope |
| PRE-07 | BLOCKER for economic proof | Bank interest 0.01% vs examples implying 1%, plus missing issuance/rates | Version-specific source verdict and owner-approved balance values |
| PRE-08 | BLOCKER for correct world effects | G2 reservation/commit assumes world-side reconciliation without validated safe APIs | Prototype save/side-effects and recovery without duplicating items |
| PRE-09 | BLOCKER for scalable NPCs | No actual simulation load benchmark | Measure Loaded/Virtual parity and budgets on 3 Overworld sizes |
| PRE-10 | BLOCKER for player business feature | guild roles/ownership/legal contracting and taxation still detailed-TBD | Do not ship private enterprise until full contract chain |
| PRE-11 | HIGH | G2-04…12 contracts are standardized drafts, not implementation-complete technical specifications | Enrich with exact state diagrams, command schemas and domain-specific failure tests before freeze |
| PRE-12 | HIGH | G2-01/02/03 contain conceptual semantics but no concrete storage schema/versioned data migration | G3 validated persistence and staged side effects |
| PRE-13 | HIGH | Historical numbers previously described in GDD can be mistaken for accepted thresholds | Every number tagged SOURCE_REFERENCE vs BALANCE_APPROVED |
| PRE-14 | HIGH | Conflicting source dates/versions (old Java, Vime 1.0.9 visual, later 1.9.2 wiki) | Store SOURCE_VERSION and confidence, no silent merge |

## 4. Pre-code readiness verdict

**NO-GO for implementation of CivCraft Reborn gameplay.** Это не означает, что проект не спроектирован. Все основные подсистемы описаны и 33 концептуальных решения приняты, но **полная проектная готовность отличается от документного покрытия**. Реализация может начаться только по отдельному прямому разрешению владельца **после** проверки указанного выбранного патча и gates.

G0 остаётся OPEN. G1 полный freeze OPEN. G2 покрытие документов 12/12, контрактная точность и утверждение OPEN. G3 проектная архитектура написана, техническая проверка OPEN. G4 проверка проведена, **не подписана**.

## 5. Что разрешено делать сейчас

- Дальше закрывать G0 source evidence и формировать approved KEEP/ADAPT/EXCLUDE по патчам.
- Выбирать test target versions и разрабатывать **план** feasibility testing без изменения игрового кода.
- Договориться о balance tables и нагрузочном reference hardware.
- Исправлять документацию и явные противоречия только в пределах уже утверждённых принципов.
- Вынести на отдельное согласование **строго ограниченный P0 scope**. Не начинать даже пустой scaffold без права на написание кода, если user требует остановиться перед кодом.

**Граница выполненной работы по этому запросу: документация сформирована до этапа pre-code review. Следующая необходимая операция — decision / source resolution / feasibility, не скрытое написание кода.**
# GDD-13 — Перечень систем, подтверждение источников и полнота проектирования

**PROPOSED · 2026-11-18.** Этот документ — **матрица охвата систем GDD**, а не полный каталог каждой исторической единицы контента. Обязательная исходная матрица: [27↔73 структуры](../01-legacy-audit/STRUCTURE_PARITY_27_VS_VIME.md), [73 строения](../01-legacy-audit/G0_73_BUILDING_COVERAGE_LEDGER.md), [28 чудес](../01-legacy-audit/G0_WONDER_CONSTRUCTION_MATRIX_PASS_11.md), [66/68 технологии](../01-legacy-audit/G0_TECH_GRAPH_VALIDATION_PASS_13.md), [полный аудит G0](../01-legacy-audit/G0_COMPREHENSIVE_AUDIT_REVIEW_2026-11-18.md).

## Round C — утверждённый принцип работы с каталогом (2026-11-18)

**APPROVED_CONCEPT — [D-044](../../DECISIONS.md), GDD-028.** Полный исторический перечень сохраняется с атрибутами `SOURCE_VERSION`, `EVIDENCE_STATUS`, `KEEP/ADAPT/EXCLUDE_CANDIDATE` и `UNKNOWN`. **Недоступные сведения не считаются подтверждёнными, не подменяются выдуманными эффектами и не исключаются молча.** Окончательный выбор того, что войдёт в каждый патч, требует явного согласования scope и закрытия критических блокеров G0. Для поздней версии 73 здания и 28 чудес — каталог охвата, **не** доказательство всех эффектов. Полный GDD-13 **NOT FROZEN**.

## Маркировка

`RETAIN_CANDIDATE` — механика исторически присутствует и предлагается к сохранению; `ADAPT_CANDIDATE` — требуется изменение под NPC/Fabric; `NEW_CANDIDATE` — специальная механика Reborn, не доказанная старым source; `DEFER_SCOPE` — не утверждена для ближайших патчей. **Ни один кандидат не является утверждённым KEEP/ADAPT/EXCLUDE до явного решения владельца.**

| Система | Связанный GDD | Статус кандидата | Исторические данные и что остаётся |
|---|---|---|---|
| Лагерь, поселение, провинция, цивилизация | [01](./SETTLEMENT_PROVINCE_CIVILIZATION_LIFECYCLE_GDD_01.md) | ADAPT_CANDIDATE | Province/wiki, Camp старой версии; новые переходы требуют GDD |
| Физические работающие граждане и миграция | [02](./NPC_POPULATION_AND_BEHAVIOR_GDD_02.md) | NEW_CANDIDATE | Нет исходного NPC runtime |
| Добыча, хозяйство, производство, монеты | [03](./GDD_03_ECONOMY_PRODUCTION_LOGISTICS.md) | ADAPT_CANDIDATE | Выборочные статьи; 54 здания без глубоких эффектов |
| Территория, культура и права | [04](./GDD_04_WORLDGEN_TERRITORY_CONSTRUCTION.md) | ADAPT_CANDIDATE | Исторические PlotClaim и культура, геометрия |
| Дороги, стены, построение сооружений | [04](./GDD_04_WORLDGEN_TERRITORY_CONSTRUCTION.md) | ADAPT_CANDIDATE | Old Road/Wall Java traced, новая генерация |
| Четыре ветки технологий и AND-связи | [05](./GDD_05_TECHNOLOGIES_WONDERS_ERAS.md) | RETAIN_CANDIDATE | 7 изолированных узлов, стоимости/ребра неполны |
| 22 мировых и 6 национальных чудес | [05](./GDD_05_TECHNOLOGIES_WONDERS_ERAS.md) | RETAIN_CANDIDATE | 6 отдельных карточек недоступны, эффекты не полностью сверены |
| Управление, должности, строй и налоги | [06](./GDD_06_GOVERNMENT_LAW_CULTURE.md) | ADAPT_CANDIDATE | 7 старых/9 поздних строев, конфликт таймеров |
| Дипломатия, договоры, войны, революции | [07](./GDD_07_DIPLOMACY_WAR_CONQUEST.md) | ADAPT_CANDIDATE | Old Java + поздняя wiki, конфликт освобождения |
| Независимый ИИ поселений и государств | [08](./GDD_08_AUTONOMOUS_CIVILIZATION_AI.md) | NEW_CANDIDATE | Центральная цель Reborn, дизайн и измерения впереди |
| Культурная/экономическая/научная и др. победы | [09](./GDD_09_VICTORY_RELIGION_EVENTS_ENDGAME.md) | ADAPT_CANDIDATE | Поздние 6 целей и старые 4 code-условия |
| Мировые и городские события | [09](./GDD_09_VICTORY_RELIGION_EVENTS_ENDGAME.md) / [12](./GDD_12_ESPIONAGE_NATIONS_EXPLORATION.md) | ADAPT_CANDIDATE | 5 локальных + 11 глобальных, разные таймеры |
| Игровой интерфейс, многопользовательские права | [10](./GDD_10_PLAYER_EXPERIENCE_UI_MULTIPLAYER.md) | NEW_CANDIDATE | Новый мод, платформа ещё не зафиксирована |
| Loaded/Virtual, сохранения, измерение TPS/MSPT | [11](./GDD_11_QUALITY_SYSTEM_CONTRACTS.md) | NEW_CANDIDATE | Надо G2/G3 и профилирование |
| Нации, религии, шпионаж, руины и артефакты | [12](./GDD_12_ESPIONAGE_NATIONS_EXPLORATION.md) | ADAPT_CANDIDATE | Вики/Java, вопросы версий и шансов |
| Интеграции публичного Bukkit-сервера (регистрация, Dynmap, anti-cheat, perks) | [10](./GDD_10_PLAYER_EXPERIENCE_UI_MULTIPLAYER.md) | DEFER_SCOPE | Не считать обязательным контентом Fabric без пользовательского решения |
| Настройки мира 10k/15k/25k и два режима | [04](./GDD_04_WORLDGEN_TERRITORY_CONSTRUCTION.md)/[09](./GDD_09_VICTORY_RELIGION_EVENTS_ENDGAME.md) | APPROVED_CONCEPT | D-013 и D-014; остальные детали открыты |

## Критерии финального полного каталога

Каждая технология, постройка, улучшение, чудо, правительство, нация, религия, миссия, ивент и юнит получает ID, source revision, source reference, игровое поведение, cost/prereqs, owner, limits, release scope, KEEP/ADAPT/EXCLUDE, owner approval, tests. **Не** маркировать всё «готово», если у 54 зданий ещё нет проверенных эффектов.

## Сквозные зависимости

`WORLD → NPC ID + Settlement → Housing + Food + JobTickets → Economy + Production → Claims + Province → Government + Research → Civilization AI → Diplomacy + Army → Wonders + Victory`.

Эта цепочка — dependency **анализ**, не закон «обязательно выпускать ровно такими патчами». G4 должен зафиксировать минимальные вертикальные срезы так, чтобы нужные prerequisites не были пустыми заглушками.

## Открытый рисковый реестр

- Лицензия старого кода и файловых ассетов, необходимость clean-room.
- Временные правила и state persistence при остановленном сервере.
- Полный технологический DAG и этапы провинции.
- Экономические коэффициенты из противоречивых версий.
- Производительность NPC в 25k² без force-loading.
- Локальная и глобальная генерация торговли, чудес, событий, NPC-городов.

**Статус: план документирования создан; целевой состав механик не утверждён.**
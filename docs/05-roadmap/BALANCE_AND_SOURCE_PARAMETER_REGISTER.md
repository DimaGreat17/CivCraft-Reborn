# Реестр параметров баланса и происхождения

**2026-11-18 · PARAMETER LIST ONLY. Ни одно численное значение не утверждено.**  
Различать `SOURCE_REFERENCE` (вплоть до противоречивого примера), `RESEARCH_PENDING`, `BALANCE_PROPOSAL`, `BALANCE_APPROVED`, `IMPLEMENTED_TESTED`. Число из исторической wiki без explicit design approval нельзя считать `BALANCE_APPROVED`.

| Parameter ID | Область | Требуется для | Status |
|---|---|---|---|
| BAL-WORLD-01 | Стартовая плотность лагерей на 10k/15k/25k | P1 worldgen | RESEARCH_PENDING |
| BAL-WORLD-02 | Дистанция между центрами, площадь town claim | P1/P2 | Исторические 150 блоков SOURCE_REFERENCE_ONLY |
| BAL-NPC-01 | Начальная численность, запас еды и строительных материалов | P1 | RESEARCH_PENDING |
| BAL-NPC-02 | Частота еды, усталость, восстановление и уровень кризиса | P1 | RESEARCH_PENDING |
| BAL-NPC-03 | Время миграции, предельный риск, длительность дефицита | P1 | RESEARCH_PENDING |
| BAL-FOOD-01 | Добыча урожая, снабжение и consume/production | P1 | RESEARCH_PENDING |
| BAL-JOB-01 | Труд, инструменты и выпуск по профессиям | P1 | RESEARCH_PENDING |
| BAL-BUILD-01 | Предметная и Hammers-стоимость построек | P1/P2 | G0 SOURCE PARTIAL |
| BAL-CULT-01 | Культурный рост и уровень для повышения провинции | P2 | Исторический III уровень SOURCE_REFERENCE_ONLY |
| BAL-TECH-01 | Tech DAG, Science/Coin prices и unlock effects | P2/P3 | G0 graph incomplete |
| BAL-MONEY-01 | Эмиссия, торговый спрос, зарплаты, налоги, содержание | P2/P3 | RESEARCH_PENDING |
| BAL-BANK-01 | Процентный доход банка | Later | G0 0.01% vs example implying 1%; CONFLICT |
| BAL-WAR-01 | Подготовка, осады, штрафы, армия/снабжение | P5 | RESEARCH_PENDING |
| BAL-WIN-01 | Шесть победных порогов и таймеров удержания | P6 | RESEARCH_PENDING |
| BAL-REL-01 | Нации, религии, бонусы | P6 | G0 version conflict |
| BAL-SPY-01 | 8 missions probabilities/cost/risk | P6 | G0 partial |
| PERF-01 | MSPT/TPS, RAM, save time, loaded entities and chunks | Each patch | UNMEASURED |
| PERF-02 | Virtual simulation cadence and max citizens | P1+ | UNMEASURED |

## Принятие числа

1. Указать `ParameterId` и источник с версией, тестируемую гипотезу, границы допустимого.
2. Создать data-only balance table с новой ревизией; никаких чисел из неподтверждённых wiki как definitive.
3. Прогнать баланс в малой общине, долгом NPC-мире и трёх размерах карты.
4. Владелец утверждает конкретную ревизию `BALANCE_APPROVED`.
5. G4 привязывает ревизию к утверждённому патчу и acceptance tests.

**До этого у BAL-WORLD/BAL-NPC/BAL-TECH/BAL-MONEY и прочих чисел статус OPEN.**
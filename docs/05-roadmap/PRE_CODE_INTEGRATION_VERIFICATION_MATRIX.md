# CivCraft Reborn — интеграционный план проверки перед игровым кодом

**2026-11-18 · TEST PLAN / NOT EXECUTED**  
[Все G2](../03-system-design/G2_00_CONTRACT_INDEX_AND_INVARIANTS.md), [G3](../04-architecture/G3_00_ARCHITECTURE_BLUEPRINT.md), [G4](./G4_PRE_CODE_READINESS_REVIEW.md).

## Matrix — система × критическая граница

| Test | Cross-system path | Предусловия | Проверяемый инвариант | Фактический результат |
|---|---|---|---|---|
| INT-01 | Worldgen → Site → Citizen → Stock | Инициализация мира, crash и restart | Все seed grants, места и NPC созданы **единожды** | NOT RUN |
| INT-02 | Citizen Need → Food Stock → Job Planner | Нехватка еды и запасов у лагеря | Нет бесплатного питания, есть план снабжения | NOT RUN |
| INT-03 | Housing → Migration → Membership | Два переселенца на последнее место | Один слот, один новый resident, нет потери другого | NOT RUN |
| INT-04 | Job → Producer → Physical Inventory | Worker + legacy-produced building | Один производственный pipeline, нет второго таймера | NOT RUN |
| INT-05 | Money → Payroll → NPC Household | Частный найм, выплата за законную работу | Дебет и кредит одной суммы, нет двойной зарплаты | NOT RUN |
| INT-06 | Shipment → unloaded chunk → physical caravan | Player loads cargo midpoint | Один ShipmentId/StockLot и то же elapsed time | NOT RUN |
| INT-07 | Site → Claim → Build Blocks | Два государства основывают город рядом | Нет crossing border или двойного TownId | NOT RUN |
| INT-08 | Province → Tech DAG → Capitol → Civ | Неполная технология/постройка | Не допускается promotion без полного closure | BLOCKED G0 |
| INT-09 | Culture → Claim Ownership | Накопление culture в чужом городе | Cultural influence не даёт чужой build permission | NOT RUN |
| INT-10 | City Capture → Polity → Jobs → Taxes | Capture при активной работе | Native/current owner и деньги сохраняются | NOT RUN |
| INT-11 | War → Liberation → Native Town | Liberation after surrendered capital | Source-version rule explicitly selected, no double transfer | BLOCKED WAR-CONFLICT-001 |
| INT-12 | Wonder Research → Build → Victory | Одно чудо, два претендента | Ровно один active wonder и один bonus | BLOCKED CONTENT |
| INT-13 | Space Mission → Reward → Restart | Crash during mission | Награда один раз, progress unaffected by duplicate event | BLOCKED CONTENT |
| INT-14 | FINITE → Victory / SANDBOX → milestone | Hold conditions met | Только конечный режим останавливает партию | BLOCKED BALANCE |
| INT-15 | Client → ACL → Account/Claim | Поддельный сетевой payload | Сервер проверяет Actor/session, no unauthorized write | NOT RUN |
| INT-16 | Save → Resume → Migration | Change saveFormatVersion | Identities/stock/history preserved, no silent reset | NOT RUN |
| INT-17 | Paused World → Virtual Settlement | Server stopped for real day/week | No accumulated income/food/war progress | NOT RUN |
| INT-18 | 10k/15k/25k → Worldgen/LOD | Multiple NPC states over long play | No forced chunks; measured MSPT/TPS/RAM within accepted limits | NOT RUN |
| INT-19 | Private Shop → NPC Hire → Taxes | Player outside government opens legal store | Contracts + payment + property rights consistent | NOT RUN |
| INT-20 | Ruin → Inventory → Reload | Same ruin claimed again | No duplicated historical loot | BLOCKED CONTENT |

## Fail injection sequence (future technical validation)

- Kill server at `RESERVED`, `APPLY_PENDING`, `LEDGER_COMMITTED` and `COMPLETE` for job, town founding, payment, shipment and research.
- Reload each saved state twice, with no extra worldgen grants or new StockLots, and compare authoritative book and physical world effects.
- On loaded/virtual boundary, move/kill NPC or destroy receiving building while a shipment is active; assert only one executor and legal cargo state.
- Run two clients concurrently for tax, claim, treasury, guild inventory and village appointment.
- Repeat all on world sizes 10k, 15k, 25k, and with FINITE/SANDBOX selection persisted.

## Evidence package needed for G4 pass

For each test, attach: target version/commit, input fixture world seed, screenshot/log/instrumentation, expected vs actual, duration and hardware, failure injection, known limitations, reviewer and approval. **This file records planned tests, not passing tests.**

## Blocking work by evidence

**Historical source:** verified tech parent DAG and early closure; each enabled building/wonder recipe; selected liberation behavior; currency/interest discrepancies.  
**Engineering:** target Fabric/Java compatibility, safe block/container APIs, versioned save/recovery implementation.  
**Gameplay:** numeric balancing, person counts, resource sustainability, army rules, win durations.  
**Performance:** actual tested TPS/MSPT and simulation budgets; no fabricated thresholds.  
**Legal:** original code/assets and third-party dependency license review.

**Status: TEST PLAN READY; TESTS NOT EXECUTED. G4 NO-GO.**
# CivCraft Reborn — G0 Comprehensive Audit Review

**Date:** 2026-11-18. **Audience:** Project owner, lead developer and future GDD authors. **Verdict: G0 NOT READY FOR SIGNOFF.** The remaining materials have been audited at inventory/selected-source level and risks documented; **the complete behavioral parity audit requested by the owner cannot be truthfully certified**, because key evidence and detailed per-item reviews are still missing. Do not label unverified historical code/wiki facts as accepted Reborn gameplay.

## 1. Executive result

### New artifacts in the current comprehensive research pass

1. [Pass 10: old Java runtime/technical risks](./G0_SOURCE_RUNTIME_AND_RISKS_PASS_10.md): Pasture/Road/Wall class mapping, active Settler and Spy implementations, local vs global events, persistence queue bug and license conflict.
2. [Pass 11: all 28 wonders' construction matrix](./G0_WONDER_CONSTRUCTION_MATRIX_PASS_11.md): 22 world + 6 national names, **22/28** individual article cards accessible, 6 individual pages inaccessible.
3. [Pass 12: late mechanics, province, espionage, events, ruins](./G0_WORLD_SYSTEMS_LATE_PASS_12.md): province promotion and war restrictions; settler placement and costs, all 8 spy mission table rows, all 11 global events, exploration loot categories.
4. [Pass 13: graph validation](./G0_TECH_GRAPH_VALIDATION_PASS_13.md): 66 visual labels, 49 + 19 = **68** directed edge candidates, **7 isolated technology labels**, zero cycles in **incomplete subset**.
5. [G0 73-entry later building ledger](./G0_73_BUILDING_COVERAGE_LEDGER.md): explicit 73/73 inventory rows; 19 with selective article coverage and 54 still index-only.
6. [Previously completed detailed audits](./VIMEWORLD_WIKI_AUDIT.md), [old source index](./LEGACY_AUDIT.md), [18-gap register](./G0_COMPLETENESS_GAP_REGISTER.md), [original technology graph](./TECHNOLOGY_GRAPH.md).

No executable Minecraft mod code was written.

## Supplement — individual article retrieval after initial comprehensive review

[Pass 14](./G0_BUILDING_SOURCE_RETRIEVAL_AND_CARDS_PASS_14.md) checked each of 73 wiki-index building article URLs: **42 retrieved, 31 failed/timed out**. This does **not** change the earlier **19 individually written selective-effect summaries versus 54 not yet detailed**; those are different coverage measures. It adds verified examples for bank, forge, market, radio tower, trade ship, defense towers and city centers, including **BANK-CONFLICT-001**: bank rate table has percent signs inconsistent with its worked formula by a factor of 100. The [building ledger](./G0_73_BUILDING_COVERAGE_LEDGER.md) has a separate fetch column. Original G0 NO-GO verdict remains unchanged.

## 2. Evidence coverage summary

| Evidence grouping | Verified/listed | Remaining limitation |
|---|---|---|
| Original config | 57 technologies, 27 structures, 7 wonders, 7 governments, 28 trade goods, 7 espionage missions, 5 town events, 2 special units, 45 town upgrades | Data catalog is not exhaustive runtime proof |
| Original research DAG | 57 nodes, 100 prerequisite edges, 0 cycles | Old generation only |
| Later image tech tree | 66 named ordinary entries + 22 wonder entries; 68 transcribed edge candidates, 7 isolated | **UNVALIDATED** complete DAG, study costs partly unverified, source image differs from wiki later revision |
| Later structures | 73 navigation titles + Town Hall/Capitol | **54 index-only**, 19 selective article detail, no complete per-feature parity |
| Wonders | 22 world + 6 national catalog names | 22 individually opened construction cards, 6 unavailable; effects not exhaustive |
| Provinces/settlers | Wiki promotion + location rules and old Settler code mapped | New AI founding and worldgen never existed in verified old code |
| Warfare/governance | Town/civ and diplomacy Java state paths, wiki differences | Full rights/treaty/capture transition graph incomplete |
| Economy/agriculture | Numerous selective wiki production recipes; resource categories separated | Remaining recipes, upgraded buildings, dynamic price/effects, NPC transport |
| Espionage | 7 old config IDs; 8 later wiki missions fully listed | Later runtime unavailable; actor player count differs from NPC agents |
| Events | 5 old local events; 11 later global event entries | Conflicting scope and clock; old same-chance RNG code bug |
| Victory | Four old Java endgame classes + six later wiki goals | Late science recipe matrix and exact timebase / concurrent victory unresolved |
| Engineering/rights | Old SQL busy-spin identified; old code header license conflict identified | Modern fabric implementation, persistence tests and rights review not done |

## 3. Status of ALL 18 registered gaps

`BLOCKED` means G0 needs more evidence, `GDD-NOVEL` means not a historical parity task at all and must be designed in G1, `DEFER-TO-G1/G3` means a design/tech decision rather than source inspection.

| ID | Workstream | Present state | Evidence obtained | Explicit next criterion |
|---|---|---|---|---|
| GAP-001 | Old↔later parity | PARTIAL | 27/27 old structure IDs mapped to later navigation or unknown; 73/73 later entries tracked; exact effects incomplete | Full per-feature KEEP/ADAPT/EXCLUDE with proof |
| GAP-002 | Late technology graph | BLOCKED | 66 visual nodes, 68 candidate edges; 7 isolated; old 57/100 graph complete; 22 C5 labels | Trace all rings and missing edges; verify costs and tier requirements |
| GAP-003 | Physical NPC and AI | GDD-NOVEL | Old residents/settler/spy tied to players; no evidence of autonomous villages in inspected source | Author new identity, household, job, needs, LOD and AI contracts; explicit design choice |
| GAP-004 | Worldgen/size/placement | PARTIAL | Province stage and settler min 150-block rule documented; D-013 sizes approved | Population/biome/site generation, dimensions, seed determinism and map feasibility |
| GAP-005 | Source rights/license | BLOCKING-RISK | Upstream GPL-2.0 advertised; individual AVRGAMING proprietary notices exist | Review file rights and avoid copying old code/assets unless clarified |
| GAP-006 | Persistence, clocks, LOD | SOURCE-RISK | Old SQL busy-spin; global object saves, old wall/road undo; world mode D-014 | Write G2/G3 contracts and restart tests, data migrations, server clock |
| GAP-007 | Old structures | PARTIAL | Pasture/Road/Wall actual Java classes identified; Shipyard subclass still unconfirmed | Per-structure data field effects and construction handlers |
| GAP-008 | Late buildings | PARTIAL | 73 index entries; 19 selectively described individual wiki pages; 54 index-only | 54+ article detail audits or explicit scope decisions |
| GAP-009 | Wonders | PARTIAL | 22 world and 6 national names; 22 article construction cards accessible; 6 pages unavailable | All individual effects and 6 missing cards, national slot/uniqueness/version policy |
| GAP-010 | Government/nations/religions/talents | PARTIAL | 7 old gov; 9 later variants, 7 nations/religions, nation page confirms exclusive effects | Complete modifiers, transition timing, exclusive build and talent precedence |
| GAP-011 | Production/logistics | PARTIAL | Farm, quarry, fish, mine, granary, warehouse/sawmill source articles | Remaining production and transport recipes and offline throughput |
| GAP-012 | War/rights/diplomacy | PARTIAL | Five relations, tax flow, war, occupation/revolution Java paths traced | Exact complete transition/permission matrix and multiplayer race tests |
| GAP-013 | Victory/space | PARTIAL | Four old code conditions, six later wiki tracks, seven mission totals | Full 13-component space recipe matrix, timers, source revisions, zero-rival cases |
| GAP-014 | Espionage/units | PARTIAL | Old spy/settler item classes found; eight later spy mission rules catalogued | Runtime later source, NPC agent adaptation and encounter/surveillance balance |
| GAP-015 | Events/exploration/province | PARTIAL | Old five town events; 11 global events; ruins and province described | Detailed artifact drops, global schedule/stacking, bosses, dungeons and provinces |
| GAP-016 | UI/admin/integrations | DEFER-TO-G1/G3 | Player commands and legacy dynmap/platform modules identified | Define selected modern Fabric UI/admin rules |
| GAP-017 | Balance/testing | DEFER-TO-G1/G4 | Historical costs and many source formulas recorded | Select version and run balancing under 10k/15k/25k and both session modes |
| GAP-018 | Legacy server auxiliary systems | SCOPING-OPEN | Dynmap, arena/perks, anticheat and registration modules identified | User KEEP/ADAPT/EXCLUDE decision; ensure rights |

## 4. Historical version differences / high-impact contradictions

- **Technology diagram:** owner-supplied image footer appears V1.0.9; wiki page references a newer tree 1.9.2. 68 edge candidates cannot be represented as **final VimeWorld 1.9.2 graph**.
- **Wonder research:** yellow dotted wonder branch nonsequential, but each wonder has ordinary research requirements. Do not chain the 22 wonders simply by image order.
- **Wonder slot limit:** most opened individual cards give 2 normal world wonders per city; another later/older page gives 3. Different version/special rules; requires explicit version decision.
- **V0.9 diplomatic victory:** 4-week Council hold and no votes; later victory article 21 days; old Java code uses player Resident votes. Cannot merge.
- **Conquest vs Domination:** old captured-civ fraction and later 9× score + six wonders are independent conditions.
- **Liberation:** old Java capital liberation may restore associated cities; wiki later describes separate town liberation.
- **RNG:** old same-chance event tie-break `nextInt(1)` is deterministically wrong, not a mechanic to copy.
- **Gov/religion:** contradictory historical transition duration and buff formulas, must version.
- **NPCs:** old Settler/Spy are player-held units; actual roaming family/job NPC simulation is new, not a parity finding.
- **Licensing:** GPL-2.0 root vs Java per-file AVRGAMING all-rights-reserved notices. Avoid copying code/assets until legal clarity.
- **D-013/D-014:** 'endless' means no endgame terminal event, **not infinite terrain**; all worlds bounded by chosen 10k/15k/25k blocks.

## 5. G0 gate audit — honest verdict

| Exit gate | Assessment | Blocking reason |
|---|---|---|
| Complete old config naming catalog | PASS INVENTORY | But individual runtime behavior partial |
| Complete old tech dependency graph | PASS OLD SOURCE CONFIG | No runtime execution needed to establish graph edges, but not later version |
| Complete later technology prerequisites | FAIL | Seven isolated nodes, ambiguous white rings/costs, version mismatch |
| Complete later building behavior matrix | FAIL | 54 index-only entries; individual effects/limits not captured |
| Complete wonder effects & building prereqs | PARTIAL | Six inaccessible cards; most article effects not fully recorded |
| Province, settlements, spies, events, war, victory concepts source-traced | PARTIAL | Source differences known; some late mechanics/wiki-only |
| NPC and worldgen design approved | NOT G0 SOURCE FACT | Move to G1; cannot treat as legacy-feature evidence |
| Full legal ability to port source/assets | BLOCKED | Conflicting rights notices |
| User parity KEEP/ADAPT/EXCLUDE choices | NOT APPROVED | Needs GDD decision |
| No gameplay code before G0–G4 | PASS | Only documents changed |

**Verdict:** G0 cannot be responsibly closed or used to unlock gameplay coding. If the goal is to finish source evidence as far as sources permit, archive known inaccessible pages/uncertainties, then request explicit owner agreement on any version choice or deferral *without* falsely claiming technical features verified.

## 6. GDD handoff, without prematurely claiming decisions

### Approved in decision register
- D-001: hybrid free-role sandbox.
- D-002: independent NPC civilizations with real resource constraints.
- D-003: retain audited original feature categories.
- D-008: complete design before coding.
- D-009: readable, extensible and tested code.
- D-012: one **fully completed and accepted major patch at a time**.
- D-013: world size at creation 10k/15k/25k.
- D-014: world mode at creation finite victory or endless sandbox.
- D-015: preserve cross-branch technology AND prerequisites, prevent one-branch rush.

### Important *unapproved* choices for G1/G2
1. Choose intended target ruleset when old Java, 2018 V0.9, 2021 wiki and owner diagram conflict.
2. Province as mandatory early state for human and AI, or adapt founding while retaining progression gating.
3. NPC population scale, family/needs depth, AI pathfinding LOD, resource and job ownership.
4. P0 new-world finite victory UI must not be advertised as a complete functional endgame until victory systems actually implemented; patch dependency boundary must move responsibly.
5. Winner outcome after finite game (world frozen / optional continue) and effective hold-time clock.
6. Government/tax/happiness formula recalibration from human resident count to NPC count.
7. Capture/revolution/liberation/occupation target version.
8. Unit espionage, loot, events and nation-exclusive structures retained/excluded per release with full prerequisites.
9. Worldgen guarantees resource and habitat viability for all three world sizes.
10. Source asset license approach: independent clean-room implementation versus formal permission and compliant derivative.

## 7. Next evidence actions (do not equate with development patches)

- **Critical:** finish graph white-ring edge trace, costs, source version and seven isolated labels; verify all 66 ordinary node reachability.
- **Critical:** individually inspect 54 index-only buildings (or obtain approved scope exclusion), source exact details for six inaccessible wonders and missing wonder effects.
- **Critical:** finish source-to-source rights investigation; only independent code until resolved.
- **Required:** full wiki special modules (nation buildings, religion, boss, artifact, loot, government), then cover any GDD-retained modules with acceptance tests.
- **G1 when actually approved:** produce authoritative design, economy/war/AI/time, scope and test contracts. No automatic progression because a markdown inventory exists.

## 8. Stewardship

This is a complete **review of the outstanding audit workstreams**, not a proof that all original and late-version gameplay behavior is comprehensively known. Every `UNKNOWN` carries a test and source-acquisition action. **G0 remains IN PROGRESS and the next release stage does not start.**

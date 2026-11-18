# World Session Mode — Finite Victory or Endless Sandbox

**Decision D-014:** APPROVED_CONCEPT on 2026-11-18. A new CivCraft Reborn world has an explicit choice of session mode. Details below marked **PROPOSED** are not frozen or implemented.

## Selectable modes at world creation

| Saved value | UI title (RU) | Player-facing behavior | Status |
|---|---|---|---|
| `FINITE_VICTORY` | Мир с конечной целью | The world has explicit victory conditions. Meeting an enabled victory condition resolves the session and presents a final result. | APPROVED_CONCEPT |
| `ENDLESS_SANDBOX` | Бесконечная песочница | No victory event terminates the world. Civilizations continue developing, fighting, trading and changing. Milestones may be recorded, but never force a session ending. | APPROVED_CONCEPT |

This choice is **independent** of D-013 geographic size presets: 10k×10k, 15k×15k, 25k×25k blocks. An endless sandbox still has a bounded map; “endless” refers to duration and open-ended gameplay, not an infinite terrain area.

## Requirements inherited from user decision

1. The two modes must be available on the new-world creation screen.
2. The chosen mode must be saved as part of world-specific configuration and read on every load.
3. Finite mode must support a genuinely completed victory outcome, not merely a notification.
4. Endless mode must not automatically stop the world when a civilization reaches a victory milestone.
5. World simulations use identical economy, AI, resource and sovereignty rules except for clearly declared victory-specific behavior.
6. Both single-player and multiplayer worlds need server-authoritative outcome handling.
7. A player may continue participating as an ordinary citizen/explorer even if not controlling the leading civilization; role freedom remains an approved project goal.

## Candidate state model — PROPOSED

`WorldRules { worldSizePreset, sessionMode, enabledVictoryTypes, rulesVersion }`
`VictoryEvent { type, civilizationId, worldTime, evidenceSnapshot }`
`GameOutcome { phase: PLAYING | RESOLVED, winnerCivilizationId?, victoryType?, resolvedTime? }`

- Finite: once a valid victory event is accepted, transition `PLAYING → RESOLVED` exactly once, persist result, show end summary; **whether the player can continue in read-only or post-victory free play remains undecided**.
- Endless: valid milestone emits a recorded achievement/history event while leaving `GameOutcome.phase = PLAYING`; AI and world ticking continue.
- Multiple simultaneous victors and tie-break semantics are **open decisions**.
- The user has **not** selected victory types, thresholds, time limits, whether multiple conditions can be enabled, or a default session mode. These depend on the ongoing legacy/VimeWorld victory audit.
- Mode changes on existing worlds are **not approved**; require explicit conversion rules and migration policy before UI is offered.
- Separate permanent achievements/historical events from single-session `GameOutcome`. No double-firing after restart.

## Release dependency

- **P0 / 0.1 Foundation:** final UI/world creation selection, versioned persistence and authoritative mode policy. Do not ship a misleading nonfunctional “win world” screen; if the finite session mode requires late-game victory systems, its availability and completion dependency must be addressed in G1–G4, potentially by shifting a complete implementation to the patch that introduces victory. This does **not** permit placeholders marked as completed.
- **Later approved victory patch:** full victory-rule implementations and endgame flows. Release schedule and exact victory types are pending audit and design freeze.
- In **ENDLESS_SANDBOX**, any delivered victory implementation must honor the non-termination invariant.

## Acceptance-test outline

- Both choices selectable independently of all three world-size presets.
- Save/reload, backup and multiplayer reconnect preserve choice; clients cannot switch authoritative settings.
- In finite mode a verified win has exactly one final resolution, deterministic when multiple events compete.
- In sandbox mode identical achievement occurs but civilization/world keeps simulating after it.
- Victory disabled or unavailable condition cannot accidentally terminate world.
- Boundary size never implicitly determines duration/mode.

**G0–G4 gates remain open. No Minecraft mod code added.**

# P1 Camp Heart v1 — manual gameplay acceptance

**Development PR:** #9 into feat/p1-living-settlement. **Do not merge/release without successful Minecraft 26.3 client acceptance.**
**World rule:** create a NEW disposable CivCraft-enabled world; back up any old world first. Existing historical 4-obsidian camps are not automatically migrated.

## First-camp player flow

1. Start a Fabric 26.3 client with matching development mod and server dependencies, enable a CivCraft world.
2. Craft the existing camp core item (renamed **«Основание лагеря»**; identifier remains `civcraft_reborn:camp_core`).
3. Place it on flat, cleared supported ground. The full cyan Heart hologram should appear automatically with the real foundation inside it. At this stage NO registered camp exists.
4. Press **G** (rebindable in Controls) to open the **Лагерь / Цивилизация** interface or press Esc and click the dedicated **CivCraft** button. Right-click on the foundation MUST NOT open this general menu.
5. Under `Лагерь → Схема строительства → Сердце лагеря`, move the footprint up to ±2 blocks around the fixed core and rotate ±90°. The actual core cannot teleport. Display and server should agree; invalid overlaps/terrain/unloaded chunks must be refused.
6. Click **«Подтвердить площадку лагеря»**. Server stores owner/world/transform. Further reposition must be refused.
7. Open **«Материалы и остаток»** to see required block resources (a door is one item but two block positions). Material list is advisory; player constructs by placing physical Minecraft blocks manually.
8. Verify completed hologram cells disappear, including door halves and oriented roof stairs. Wrong blocks don't count; removing a correct block makes its ghost reappear.
9. Run `/civcraft camp heart inspect` (nearby). Expected **162/162** for the accepted complete structure.
10. Open the menu and click **«Основать лагерь»** (or `/civcraft camp found`): before 162/162 it must fail; afterward create ONE permanent CampId with no second material debit.
11. Verify the new camp menu works from a distance. Recruitment requires actual nearby living villagers and validated housing; no free NPCs are spawned.
12. Leave/rejoin and restart the server. Confirm the site transform and ownership, construction progress, CampId, and the ability to repair after damaging any structural required block.
13. Check multiplayer rights and foreign core attempts, finite world border, other dimensions, unloaded edge chunks, and GPU stability when showing/hiding/switching STORAGE and HEART projections.

## Known limitations in this draft

- Clicking the dedicated CivCraft button is implemented **in the pause menu**; the persistent clickable in-world HUD button is NOT yet implemented. The G hotkey works as a separate input path.
- Preexisting worlds with a legacy core/obsidian-cross foundation require a migration or conversion command.
- A legacy reward of 4 obsidian per foundation placement remains in code, despite the new 7x7 Heart not requiring obsidian. Owner balance decision is outstanding.
- Numerical `hammers/hour`, shared production allocation, and fully foundation-anchored NPC construction of other buildings are NOT part of this first-camp vertical slice; legacy NPC builder code still needs redesign.
- Exact Minecraft GPU rendering and controls cannot be certified by headless CI.
- On first-pass client UX, error feedback on a rejected move may temporarily require reopening the placement screen to resynchronize; server remains authoritative.

## Build validation

Review GitHub Actions P1 compile-and-server and P0 checks for the latest PR head. Require Java 25, unit tests, dedicated bootstrap and actual Minecraft 26.3 client graphics acceptance. A green CI run is necessary but not sufficient.

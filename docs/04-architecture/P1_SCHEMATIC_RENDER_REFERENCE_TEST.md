# P1 / 0.2 — Litematica reference test and native blueprint GPU renderer

**2026-11-18 · In development, NOT release accepted.**

## 1. Two separate profiles

### Real CivCraft player release (no Litematica dependency)

- Only Minecraft 26.3 + Fabric 0.20.1 + Fabric API 0.166.2+26.3 + CivCraft.
- CivCraft's `CivCraftGhostBlockRenderer` owns native rendering of translucent *whole block volume* holograms through Fabric `LevelExtractionEvents` / `LevelRenderEvents`.
- The client-only `CampBlueprintProjection` is the shared exact coordinate/expected-material model for HEART, STORAGE, WORKSHOP, FARM and optional BEDROOM.
- Ghost faces disappear after the right block is placed; there is no automatic building and no free material grant from the preview. Server commands still authorize actual camp founding and resources.
- Current native ghost renders **solid translucent colored voxel cubes**, not yet Minecraft block textures or accurate slab/stair/bed baked model geometry. Performance/gameplay screenshot and client testing are required.

### Reference comparison (test-only, required pair)

Run: `gradle verifySchematicTestProfile -PschematicTest=true`, then `gradle runClient -PschematicTest=true`.

The Gradle flag declares both **mandatory test-runtime** dependencies:
- Litematica **0.29.1** for Minecraft 26.3, published by its maintainer on [Modrinth](https://modrinth.com/mod/litematica/version/0.29.1); pinned project/version ID `bEpr0Arc:ZqV316KY`.
- MaLiLib **0.30.2** for Minecraft 26.3, published on [Modrinth](https://modrinth.com/mod/malilib/version/0.30.2); pinned project/version ID `GcWjdA9I:JX9bESec`.

Without the flag neither mod is required or bundled into the CivCraft JAR. They are **not** declared in the published `fabric.mod.json` as regular or transitive runtime requirements. Gradle dependency resolution in this test profile must succeed before a Litematica comparison is accepted. Compatibility with both installed simultaneously still requires an actual `runClient` test.

Official Litematica source: [maruohon/litematica](https://github.com/maruohon/litematica), LGPL-3.0; its license and dependency chain need an explicit compliance review before copying, modifying or combining any source. **No Litematica source is copied or adapted into CivCraft**, and native rendering code is written against Minecraft/Fabric drawing APIs, based on [Fabric's rendering documentation](https://docs.fabricmc.net/develop/rendering/world).

## 2. Visual comparison tasks

| Test | Reference Litematica behavior | Native CivCraft expected result |
|---|---|---|
| GHOST-01 | Render complete block preview in a 3D world | Missing positions have 6 semi-transparent faces, not particles |
| GHOST-02 | Correct positions/materials | Place exact block: preview cell disappears; remove it: preview returns |
| GHOST-03 | Render range controls | Move outside 28-block preview radius: CivCraft ghost hides |
| GHOST-04 | Safe unmodified world | Rendering never modifies block state or grants items |
| GHOST-05 | Actual preview placement | Core pos anchors coordinates, layout remains stable |
| GHOST-06 | Multiple constructions | Switch heart/warehouse/workshop/farm/room without old ghosts |
| GHOST-07 | World boundary | No forced loading outside local loaded chunks |
| GHOST-08 | Dedicated server | Client-only renderer and Litematica do not load on server |
| GHOST-09 | FPS | Compare a room + 80+ cells on the same graphics settings; log fps and frame times |
| GHOST-10 | Texture fidelity | **OPEN**: Minecraft textures, directional blockstate / slabs / stairs / bed shape — not yet native |
| GHOST-11 | Shader compatibility | **OPEN**, capture vanilla and shader pipeline screenshots |
| GHOST-12 | Permissions | Client preview does not authorize camp/city ownership or server blocks |

## 3. Native render internals and constraints

- Immutable list of up to 256 missing positions is extracted on the client/level extraction event. The draw event consumes positions only, no live world reads, avoiding extraction/draw race.
- GPU rendering uses a staged vertex buffer with an alpha-tinted debug filled pipeline. The client closes the buffer with `CivCraftGhostRendererCleanupMixin` when GameRenderer closes.
- Local preview from right-clicked Camp Core. A test world with only one placed core is sufficient; no need to register the camp before seeing its physical construction outline.
- All full translucent cubes are currently **approximate block volumes** and are not promised as high-fidelity Litematica baked blockstate renders. Accurate models/textures and player-facing rotation/placement control remain required improvements.

## 4. What evidence proves readiness

1. `gradle build` CI passes on current branch HEAD.
2. Dedicated server smoke passes with *no Litematica*.
3. `gradle verifySchematicTestProfile -PschematicTest=true` resolves test-profile requirements (configuration declaration alone does not verify the remote artifact).
4. Interactive client with reference mods installs and launches; after right-click Camp Core compare screenshots/video for GHOST-01…12.
5. Only after cross-platform/client acceptance change the implementation status from DRAFT; do not ship reference dependencies in CivCraft release.

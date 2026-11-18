# G3 / P0 — Проверка технической совместимости Minecraft/Fabric

**2026-11-18 — DESK RESEARCH, BUILD NOT EXECUTED.** Target platform Minecraft Java + Fabric (D-046) but the exact toolchain is **unapproved**.

## 1. Verified-by-public-reference candidate

At the time of this desk review, **Minecraft Java Edition 26.3** appears to be the latest stable release. Fabric published its official **Fabric for Minecraft 26.3** developer notice (2026-09-15) and recommends Loom 1.17 and Gradle 9.6.0 **as of publication**. Its notice listed stable Fabric Loader 0.19.5 **at publication**, not necessarily today's latest. Independent release trackers identify Java 25 as the minimum for Minecraft 26.x. Current Fabric API builds should be selected via Fabric's version selector **for the exact Minecraft version**, not inferred from the loader number.

**Public reference URLs (research, not local build verification):**
- https://www.fabricmc.net/2026/09/15/263.html
- https://fabricmc.net/develop/
- https://minecraft.wiki/w/Minecraft%3A_Java_Edition
- https://jkit.tools/en/radar/minecraft-versions
- https://www.curseforge.com/minecraft/mc-mods/fabric-api

**Proposed candidate test, NOT a locked version:** Minecraft Java 26.3; JDK 25; official Fabric Loader stable for 26.3 at test time; Fabric API exact matching 26.3; Loom and Gradle following official 26.3 guidance. Exact identifiers and SHA checksums must be recorded from official Fabric metadata and public distribution before `P0-D03 APPROVED`.

## 2. Mandatory compatibility verification

1. Fetch **exact** Minecraft game release manifest and Fabric loader/API metadata; record URLs, versions, hashes, dates and licensing.
2. Verify Java runtime JDK requirement, Gradle wrapper compatibility and Fabric Loom. Do not assume Fabric Loader 0.19.5 remains most recent.
3. Generate independent mod dev workspace with original template (not imported CivCraft Java).
4. Run compile + unit tests + client launch and dedicated server launch; retain stdout/stderr, save paths, screenshots.
5. Save and reopen world in client and dedicated; test server-side WorldBorder on 10k/15k/25k.
6. Test script can access stable world events, safe server execution, config and migration hooks; no direct Minecraft API from background workers.
7. Exercise command spoofing/replay and read-only client config. Explicitly verify world creation UI is realistically supported by selected Fabric APIs; if not, seek new architecture decision.
8. Run baseline MSPT/TPS, memory, startup time and save duration on known hardware; record actual values, then propose non-arbitrary pass/fail thresholds.
9. Test dependency licensing and maintain file provenance; no old Bukkit code or schematic imports.
10. Pin reproducible lockfile/Gradle wrapper and compare against second machine/clean server.

## 3. Decision table

| Candidate | Source-supported | Build-tested | Approval |
|---|---|---|---|
| Minecraft 26.3 | Yes (as of 2026-11-18 desk review) | NO | Candidate only |
| Java 25 | Reported minimum for current Minecraft 26.x | NO | Candidate only |
| Fabric Loader | Official notice lists 0.19.5 as of 2026-09-15 | NO | Choose current compatible stable at test |
| Fabric API | Matching 26.3 build reported by distribution, select officially | NO | Candidate only |
| Loom / Gradle | Official Fabric notice: 1.17 / 9.6.0 as of article | NO | Candidate only |
| Client/dedicated runtime | Unknown | NO | Must test |

## 4. Evidence required before platform freeze

Repository SHA, target game version, JDK vendor/version, Loader version, API version, Loom, Gradle wrapper, mappings, exact dependency coordinates, operating system, test scripts, results, time, hardware and permission/license review. No future tool call can be inferred to have run merely because this plan exists.

**Result: P0 candidate identified, but G3 remains NOT VERIFIED, no mod code written.**

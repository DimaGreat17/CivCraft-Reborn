# P0 compatibility execution — Fabric 26.3

**Branch:** `feat/p0-foundation-compatibility`  
**Purpose:** Verify a minimal original mod entrypoint before adding any gameplay modules.  
**Status:** UNVERIFIED UNTIL WORKFLOW PASSES.

## Official upstream references

- https://github.com/FabricMC/fabric-example-mod/tree/26.3
- https://fabricmc.net/develop/
- https://docs.fabricmc.net/develop/loom/
- https://www.fabricmc.net/2026/09/15/263.html

## Test candidate

Minecraft Java `26.3`, Fabric Loader `0.20.1`, Fabric API `0.166.2+26.3`, Loom `1.19.4`, JDK `25`, Gradle `9.8.0`. The initial choices came from the official example-mod 26.3 branch; CI on 2026-11-18 showed resolved Loom 1.19.4 **requires Gradle >=9.8.0**, therefore these exact Loom/Gradle values supersede the earlier snapshot/9.6 pair. The corrected pair still needs a passing build. **This does not prove the exact combination works on this repository until CI compiles it and a real server starts.**

## Steps and pass gates

1. Static validation `python3 scripts/validate_p0_manifest.py`.
2. Java 25/Gradle/Fabric dependency resolution + `gradle build --no-daemon` (CI).
3. Confirm an installable Fabric mod JAR exists.
4. Run client and dedicated-server smoke tests on the exact binary with Fabric Loader and API.
5. Verify world creation, server border, persistent config and crash recovery only after adding P0 code.

**Current stage:** initial compilation succeeded with pinned Loom/Gradle. Foundation server-settings implementation has since been added and is under CI; world-creation UI, runtime server smoke and six-option restart verification remain pending. No NPC, city or economy has been implemented.

## Rights

This mod entrypoint/build configuration is independently authored; old CivCraft Java and historical assets are not copied. Root project license remains TBD. Fabric API and other dependencies retain their own licensing terms.

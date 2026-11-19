#!/usr/bin/env python3
"""Regression guard for the exact 2026-11-18 Renderpearl render-pass crash.

CI cannot run a GPU-backed Minecraft client, but it can prevent the old
unsafe StagedVertexBuffer.upload() / nested RenderPass implementation from
returning unnoticed. Real GPU/client verification is still mandatory.
"""
from pathlib import Path

root = Path(__file__).resolve().parents[1]
renderer = (root / "src/client/java/dev/civcraftreborn/client/CivCraftGhostBlockRenderer.java").read_text()
initializer = (root / "src/client/java/dev/civcraftreborn/client/CivCraftRebornClient.java").read_text()
projection = (root / "src/client/java/dev/civcraftreborn/client/CampBlueprintProjection.java").read_text()

assert 'LevelRenderEvents.COLLECT_SUBMITS.register' in renderer, "Ghost rendering must collect native submits"
assert '.submitCustomGeometry(' in renderer, "Expected deferred Minecraft-owned custom textured quad geometry"
assert 'vertices.putBakedQuad(' in renderer, "Expected actual baked model quads, not primitive cubes"
assert 'quad.setColor(' in renderer, "Explicit per-face alpha required, tintLayers are insufficient"
assert 'new QuadInstance()' in renderer, "Expected explicit per-quad vertex color"
assert 'RenderTypes.entityTranslucent(' in renderer, "Expected translucent block-atlas model pipeline"
assert 'getBlockStateModelSet()' in renderer, "Must use resource-pack-resolved BlockState models"
assert 'CivCraftGhostBlockRenderer.register();' in initializer, "Native renderer not enabled"
assert 'CivCraftGhostBlockRenderer::render' not in renderer, "Old render callback found"
# Only inspect actual Java call expressions, not documentation.
for line in renderer.splitlines():
    code = line.strip()
    if code.startswith(('*', '//')):
        continue
    for forbidden in (
        'StagedVertexBuffer.', '.upload()', '.createRenderPass(',
        '.createCommandEncoder()', 'BUFFER.upload(',
    ):
        assert forbidden not in code, f"Forbidden in-pass GPU operation: {forbidden} in {code}"
assert 'SAFE_OUTLINE' not in projection, "Duplicate particle fallback still active"
print("PASS: per-face alpha for textured ghosts via vanilla deferred submits; illegal GPU operations absent")

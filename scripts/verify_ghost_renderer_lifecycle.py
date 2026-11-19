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
assert '.submitBlockModel(' in renderer, "Expected Minecraft-owned textured block-model submit"
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
print("PASS: native textured BlockState submits; illegal GPU operations absent")

package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;

/**
 * Translucent full-volume block previews using Minecraft 26.3's native feature
 * submission path. In particular, we DO NOT create a nested RenderPass or call
 * StagedVertexBuffer.upload() from any level-render callback.
 *
 * Geometry is submitted during COLLECT_SUBMITS and Minecraft uploads/draws it
 * with its own properly ordered RenderType feature renderer. This resolves the
 * precise render-pass lifecycle violation in the user's 2026-11-18 crash.
 *
 * Shapes are intentionally ghosted cubes (all six faces), not yet exact baked
 * models for stairs, beds, slabs or directional properties.
 */
public final class CivCraftGhostBlockRenderer {
    private static final int MAX_GHOST_BLOCKS = 256;
    private static final float INSET = 0.018f;
    private static final float ALPHA = 0.34f;
    private record Ghost(BlockPos pos, boolean obstructed) {}

    private CivCraftGhostBlockRenderer() {}

    public static void register() {
        LevelRenderEvents.COLLECT_SUBMITS.register(CivCraftGhostBlockRenderer::submit);
    }

    private static void submit(LevelRenderContext context) {
        Minecraft client = Minecraft.getInstance();
        CampBlueprintProjection.Plan plan = CampBlueprintProjection.selected();
        BlockPos anchor = CampBlueprintProjection.anchor();
        if (plan == null || anchor == null || client.level == null
                || client.player == null || !CampBlueprintProjection.unlocked(plan)
                || (plan != CampBlueprintProjection.Plan.HEART
                    && client.player.blockPosition().distSqr(anchor) > 28 * 28)) {
            return;
        }

        // World data is read while Minecraft is collecting submits, never
        // from the deferred GPU execution callback.
        List<Ghost> missing = new ArrayList<>();
        for (CampBlueprintProjection.Step step : CampBlueprintProjection.steps(plan)) {
            BlockPos position = CampBlueprintProjection.worldPosition(plan, step);
            if (position == null || !client.level.hasChunkAt(position)
                    || CampBlueprintProjection.matches(plan, step)) {
                continue;
            }
            // Red marks occupied incorrect cells that the player must clear.
            // Cyan marks empty positions waiting for the required block.
            boolean obstructed=!client.level.getBlockState(position).isAir();
            missing.add(new Ghost(position.immutable(),obstructed));
            if (missing.size() >= MAX_GHOST_BLOCKS) break;
        }
        if (missing.isEmpty()) return;

        Vec3 camera = context.levelState().cameraRenderState.pos;
        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        // submitCustomGeometry records the immutable geometry callback. The
        // vanilla feature dispatcher handles buffer upload and render-pass
        // lifetime; do NOT upload manually inside COLLECT_SUBMITS or render.
        List<Ghost> positions = List.copyOf(missing);
        context.submitNodeCollector().submitCustomGeometry(
                poseStack, RenderTypes.debugFilledBox(), (pose, vertices) -> {
                    Matrix4fc matrix = pose.pose();
                    for (Ghost ghost : positions) {
                        BlockPos position=ghost.pos();
                        cube(vertices, matrix, position.getX(), position.getY(),
                                position.getZ(),ghost.obstructed());
                    }
                });
        poseStack.popPose();
    }

    private static void cube(VertexConsumer vertices, Matrix4fc matrix,
            int x, int y, int z,boolean obstructed) {
        float x0=x+INSET, y0=y+INSET, z0=z+INSET;
        float x1=x+1-INSET, y1=y+1-INSET, z1=z+1-INSET;
        // Six full ghost faces; red cells are occupied by incorrect terrain.
        float red=obstructed?1.0f:0.19f;
        float green=obstructed?0.18f:0.81f;
        float blue=obstructed?0.14f:1.0f;
        face(vertices,matrix,red,green,blue,x0,y0,z1,x1,y0,z1,x1,y1,z1,x0,y1,z1);
        face(vertices,matrix,red,green,blue,x1,y0,z0,x0,y0,z0,x0,y1,z0,x1,y1,z0);
        face(vertices,matrix,red,green,blue,x0,y0,z0,x0,y0,z1,x0,y1,z1,x0,y1,z0);
        face(vertices,matrix,red,green,blue,x1,y0,z1,x1,y0,z0,x1,y1,z0,x1,y1,z1);
        face(vertices,matrix,red,green,blue,x0,y1,z1,x1,y1,z1,x1,y1,z0,x0,y1,z0);
        face(vertices,matrix,red,green,blue,x0,y0,z0,x1,y0,z0,x1,y0,z1,x0,y0,z1);
    }

    private static void face(VertexConsumer vertices, Matrix4fc matrix,
            float red,float green,float blue,
            float ax,float ay,float az,float bx,float by,float bz,
            float cx,float cy,float cz,float dx,float dy,float dz) {
        vertices.addVertex(matrix,ax,ay,az).setColor(red,green,blue,ALPHA);
        vertices.addVertex(matrix,bx,by,bz).setColor(red,green,blue,ALPHA);
        vertices.addVertex(matrix,cx,cy,cz).setColor(red,green,blue,ALPHA);
        vertices.addVertex(matrix,dx,dy,dz).setColor(red,green,blue,ALPHA);
    }

    /** Kept for backward-compatible GameRenderer close mixin. No GPU-owned buffers. */
    public static void close() {
        // All renderer-owned GPU resources were removed; vanilla owns buffers.
    }
}

package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.vertex.VertexFormat;

import dev.civcraftreborn.CivCraftReborn;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Native, independent full-voxel translucent building preview.
 *
 * No Litematica classes are imported or copied. Minecraft's 26.3 rendering
 * pipeline extracts immutable visible positions, then draws alpha-tinted
 * full block faces during the translucent terrain render pass.
 *
 * This is a whole-block preview: stair, slab and bed silhouettes are still
 * represented by bounding cubes pending block-model render support.
 */
public final class CivCraftGhostBlockRenderer {
    private static final RenderPipeline GHOST_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(
                            CivCraftReborn.MOD_ID, "pipeline/camp_ghost_blocks"))
                    .withDepthStencilState(Optional.empty())
                    .build());

    private static final StagedVertexBuffer BUFFER =
            new StagedVertexBuffer(() -> "CivCraft building ghost blocks",
                    RenderType.SMALL_BUFFER_SIZE);
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1, 1, 1, 1);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final org.joml.Matrix4f TEXTURE_MATRIX = new org.joml.Matrix4f();
    private static final int MAX_BLOCKS = 256;

    private static List<BlockPos> visibleBlocks = List.of();

    private CivCraftGhostBlockRenderer() {}

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(CivCraftGhostBlockRenderer::extract);
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(CivCraftGhostBlockRenderer::render);
    }

    private static void extract(LevelExtractionContext context) {
        Minecraft client = Minecraft.getInstance();
        CampBlueprintProjection.Plan plan = CampBlueprintProjection.selected();
        BlockPos anchor = CampBlueprintProjection.anchor();
        if (client.level == null || plan == null || anchor == null
                || client.player == null
                || client.player.blockPosition().distSqr(anchor) > 28 * 28) {
            visibleBlocks = List.of();
            return;
        }

        List<BlockPos> missing = new ArrayList<>();
        for (CampBlueprintProjection.Step step : CampBlueprintProjection.steps(plan)) {
            BlockPos pos = CampBlueprintProjection.worldPosition(plan, step);
            if (!client.level.hasChunkAt(pos) || CampBlueprintProjection.matches(plan, step)) continue;
            missing.add(pos.immutable());
            if (missing.size() >= MAX_BLOCKS) break;
        }
        // All mutable client-world reads finish in extraction, not the GPU drawing pass.
        visibleBlocks = List.copyOf(missing);
    }

    private static void render(LevelRenderContext context) {
        List<BlockPos> blocks = visibleBlocks;
        if (blocks.isEmpty()) return;

        VertexFormat format = GHOST_PIPELINE.getVertexFormatBinding(0);
        if (format == null) return;

        PrimitiveTopology topology = GHOST_PIPELINE.getPrimitiveTopology();
        StagedVertexBuffer.Draw batch = BUFFER.appendDraw(format, topology,
                topology == PrimitiveTopology.QUADS
                        ? RenderSystem.getProjectionType().vertexSorting() : null);

        var builder = BUFFER.getVertexBuilder(batch);
        PoseStack matrices = context.poseStack();
        Vec3 eye = context.levelState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-eye.x, -eye.y, -eye.z);
        Matrix4fc pose = matrices.last().pose();
        for (BlockPos pos : blocks) {
            writeCube(builder, pose, pos.getX(), pos.getY(), pos.getZ());
        }
        matrices.popPose();

        BUFFER.upload();
        StagedVertexBuffer.ExecuteInfo info = BUFFER.getExecuteInfo(batch);
        if (info != null) draw(Minecraft.getInstance(), info);
        BUFFER.endFrame();
    }

    private static void writeCube(VertexConsumer vertices, Matrix4fc pose,
            int x, int y, int z) {
        float x0=x+0.02f, y0=y+0.02f, z0=z+0.02f;
        float x1=x+0.98f, y1=y+0.98f, z1=z+0.98f;
        float r=0.17f, g=0.8f, b=1f, a=0.27f;
        // Each block is rendered as six full translucent surfaces, not dots.
        face(vertices,pose,x0,y0,z1,x1,y0,z1,x1,y1,z1,x0,y1,z1,r,g,b,a);
        face(vertices,pose,x1,y0,z0,x0,y0,z0,x0,y1,z0,x1,y1,z0,r,g,b,a);
        face(vertices,pose,x0,y0,z0,x0,y0,z1,x0,y1,z1,x0,y1,z0,r,g,b,a);
        face(vertices,pose,x1,y0,z1,x1,y0,z0,x1,y1,z0,x1,y1,z1,r,g,b,a);
        face(vertices,pose,x0,y1,z1,x1,y1,z1,x1,y1,z0,x0,y1,z0,r,g,b,a);
        face(vertices,pose,x0,y0,z0,x1,y0,z0,x1,y0,z1,x0,y0,z1,r,g,b,a);
    }

    private static void face(VertexConsumer vertices, Matrix4fc matrix,
            float ax,float ay,float az,float bx,float by,float bz,
            float cx,float cy,float cz,float dx,float dy,float dz,
            float r,float g,float b,float alpha) {
        vertices.addVertex(matrix,ax,ay,az).setColor(r,g,b,alpha);
        vertices.addVertex(matrix,bx,by,bz).setColor(r,g,b,alpha);
        vertices.addVertex(matrix,cx,cy,cz).setColor(r,g,b,alpha);
        vertices.addVertex(matrix,dx,dy,dz).setColor(r,g,b,alpha);
    }

    private static void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info) {
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(),
                        COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);
        RenderTarget target = client.gameRenderer.mainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (color == null) return;

        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder()
                .createRenderPass(() -> "CivCraft blueprint projection", color,
                        Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(RenderSystem.getCompiledPipeline(GHOST_PIPELINE));
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.setVertexBuffer(0, info.vertexBuffer().slice());
            pass.setIndexBuffer(info.indexBuffer(), info.indexType());
            pass.drawIndexed(info.indexCount(), 1,
                    info.firstIndex(), info.baseVertex(), 0);
        }
    }

    public static void close() {
        BUFFER.close();
    }
}

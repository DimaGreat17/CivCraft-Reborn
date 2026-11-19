package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * World-space ghost made from the exact textured baked quads of each desired
 * BlockState. Every quad, including un-tinted wood/stone sprites, receives
 * an explicit translucent color through QuadInstance.
 *
 * IMPORTANT: submitBlockModel(tintLayers) DOES NOT apply alpha to untinted
 * model faces. That old path made the hologram look fully opaque regardless
 * of the tint array alpha values.
 *
 * Geometry is submitted through vanilla COLLECT_SUBMITS; no manual GPU
 * uploads or nested render passes are created (Renderpearl-safe lifecycle).
 */
public final class CivCraftGhostBlockRenderer {
    private static final int MAX_GHOST_BLOCKS=256;
    private static final int LIGHT=0x00F000F0;

    // Alpha is applied to EVERY quad (not just JSON tintindex faces).
    // 0x60 = ~38% opacity; 0x90 = ~56% opacity.
    private static final int NORMAL_COLOR=0x60FFFFFF;
    private static final int OBSTRUCTED_COLOR=0x90FF7777;

    private record GhostQuads(BlockPos pos,List<BakedQuad> quads,boolean obstructed) {}
    private CivCraftGhostBlockRenderer(){}

    public static void register() {
        LevelRenderEvents.COLLECT_SUBMITS.register(CivCraftGhostBlockRenderer::submit);
    }

    private static void submit(LevelRenderContext context) {
        Minecraft client=Minecraft.getInstance();
        var plan=CampBlueprintProjection.selected();
        BlockPos anchor=CampBlueprintProjection.anchor();
        if(plan==null||anchor==null||client.level==null||client.player==null
                ||!CampBlueprintProjection.unlocked(plan)
                ||(plan!=CampBlueprintProjection.Plan.HEART
                    &&client.player.blockPosition().distSqr(anchor)>28*28))
            return;

        // Snapshot all world-dependent information on the extraction thread,
        // before the deferred draw callback executes.
        var models=client.getModelManager().getBlockStateModelSet();
        List<GhostQuads> needed=new ArrayList<>();
        for(var step:CampBlueprintProjection.steps(plan)) {
            BlockPos position=CampBlueprintProjection.worldPosition(plan,step);
            if(position==null||!client.level.hasChunkAt(position)
                    ||CampBlueprintProjection.matches(plan,step))continue;
            BlockState desired=CampBlueprintProjection.expectedState(plan,step);
            // Authored blueprints may optionally REQUIRE empty air cells.
            // Show an occupied required-air cell as a red translucent glass
            // marker so the player knows exactly which block to remove.
            BlockState visual=desired.isAir()
                    ? Blocks.RED_STAINED_GLASS.defaultBlockState():desired;
            List<BlockStateModelPart> parts=new ArrayList<>();
            models.get(visual).collectParts(
                    RandomSource.create(visual.getSeed(position)),parts);

            List<BakedQuad> quads=new ArrayList<>();
            for(BlockStateModelPart part:parts) {
                quads.addAll(part.getQuads(null));
                for(Direction direction:Direction.values())
                    quads.addAll(part.getQuads(direction));
            }
            if(quads.isEmpty())continue;
            boolean obstructed=!client.level.getBlockState(position).isAir();
            needed.add(new GhostQuads(position.immutable(),List.copyOf(quads),obstructed));
            if(needed.size()>=MAX_GHOST_BLOCKS)break;
        }
        if(needed.isEmpty())return;

        Vec3 camera=context.levelState().cameraRenderState.pos;
        PoseStack stack=context.poseStack();
        stack.pushPose();
        stack.translate(-camera.x,-camera.y,-camera.z);
        List<GhostQuads> snapshot=List.copyOf(needed);
        context.submitNodeCollector().submitCustomGeometry(
                stack,RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS),
                (pose,vertices)->{
                    QuadInstance quad=new QuadInstance();
                    quad.setLightCoords(LIGHT);
                    quad.setOverlayCoords(OverlayTexture.NO_OVERLAY);
                    for(GhostQuads ghost:snapshot) {
                        // Vertex positions are in local model coordinates.
                        // Translate using a copy of the extracted matrix, not
                        // client world state or render-pass commands.
                        PoseStack local=new PoseStack();
                        local.last().pose().set(pose.pose());
                        local.translate(ghost.pos().getX(),ghost.pos().getY(),ghost.pos().getZ());
                        quad.setColor(ghost.obstructed()?OBSTRUCTED_COLOR:NORMAL_COLOR);
                        for(BakedQuad baked:ghost.quads())
                            vertices.putBakedQuad(local.last(),baked,quad);
                    }
                });
        stack.popPose();
    }

    public static void close() {
        // Buffers and GPU passes belong to the Minecraft feature dispatcher.
    }
}

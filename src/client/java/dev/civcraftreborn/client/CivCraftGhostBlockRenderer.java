package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Actual vanilla textured BlockState models for the missing blueprint cells.
 * No custom GPU upload or nested RenderPass; all models are submitted through
 * Minecraft's normal COLLECT_SUBMITS feature queue.
 *
 * The translucent entity block-atlas render type preserves textures and
 * the exact shape of stairs, doors, slabs, panes, logs and ordinary blocks.
 */
public final class CivCraftGhostBlockRenderer {
    private static final int MAX_GHOST_BLOCKS=256;
    private static final int LIGHT=0x00F000F0;
    // 60% opacity: blueprint blocks are easier to identify against the world.
    private static final int[] NORMAL_TINT={0x99FFFFFF,0x99FFFFFF,0x99FFFFFF,0x99FFFFFF};
    // 70% opacity: occupied incorrect cells must remain clearly distinguishable.
    private static final int[] OBSTRUCTED_TINT={0xB3FF6666,0xB3FF6666,0xB3FF6666,0xB3FF6666};

    private record Ghost(BlockPos pos,BlockState expected,boolean obstructed) {}
    private CivCraftGhostBlockRenderer(){}

    public static void register(){
        LevelRenderEvents.COLLECT_SUBMITS.register(CivCraftGhostBlockRenderer::submit);
    }

    private static void submit(LevelRenderContext context){
        Minecraft client=Minecraft.getInstance();
        var plan=CampBlueprintProjection.selected();
        BlockPos anchor=CampBlueprintProjection.anchor();
        if(plan==null || anchor==null || client.level==null || client.player==null
                || !CampBlueprintProjection.unlocked(plan)
                || (plan!=CampBlueprintProjection.Plan.HEART
                    && client.player.blockPosition().distSqr(anchor)>28*28))
            return;

        List<Ghost> needed=new ArrayList<>();
        for(var step:CampBlueprintProjection.steps(plan)){
            BlockPos position=CampBlueprintProjection.worldPosition(plan,step);
            if(position==null||!client.level.hasChunkAt(position)
                    || CampBlueprintProjection.matches(plan,step))continue;
            needed.add(new Ghost(position.immutable(),
                    CampBlueprintProjection.expectedState(plan,step),
                    !client.level.getBlockState(position).isAir()));
            if(needed.size()>=MAX_GHOST_BLOCKS)break;
        }
        if(needed.isEmpty())return;

        // The vanilla block model lookup resolves models from active resource
        // packs; each state contributes its real quads and texture coordinates.
        var models=client.getModelManager().getBlockStateModelSet();
        var renderType=RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
        Vec3 camera=context.levelState().cameraRenderState.pos;
        PoseStack pose=context.poseStack();
        pose.pushPose();
        pose.translate(-camera.x,-camera.y,-camera.z);
        for(Ghost ghost:needed){
            List<BlockStateModelPart> parts=new ArrayList<>();
            models.get(ghost.expected()).collectParts(
                    RandomSource.create(ghost.expected().getSeed(ghost.pos())),parts);
            if(parts.isEmpty())continue;
            pose.pushPose();
            pose.translate(ghost.pos().getX(),ghost.pos().getY(),ghost.pos().getZ());
            context.submitNodeCollector().submitBlockModel(pose,renderType,
                    List.copyOf(parts),ghost.obstructed()?OBSTRUCTED_TINT:NORMAL_TINT,
                    LIGHT,OverlayTexture.NO_OVERLAY,0);
            pose.popPose();
        }
        pose.popPose();
    }

    public static void close(){
        // The vanilla feature system owns buffer uploads and cleanup.
    }
}

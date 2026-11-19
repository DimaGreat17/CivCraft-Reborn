package dev.civcraftreborn.client;

import dev.civcraftreborn.settlement.CampBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import dev.civcraftreborn.settlement.BlueprintStatusRequest;
import dev.civcraftreborn.settlement.BlueprintStatusResponse;

/**
 * The physical camp core opens a management interface on right-click.
 * On a server the command actions remain fully authoritative.
 */
public final class CivCraftRebornClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CampBlueprintProjection.register();
        CivCraftGhostBlockRenderer.register();
        ClientPlayNetworking.registerGlobalReceiver(BlueprintStatusResponse.TYPE,
                (payload, context) -> context.client().execute(() ->
                        CampBlueprintProjection.setServerStage(payload.core(), payload.stage())));
        // Registered renderer uses vanilla feature submit batching, not a
        // nested GPU pass or StagedVertexBuffer.upload().
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!world.isClientSide() ||
                    !world.getBlockState(hit.getBlockPos()).is(CampBlocks.CAMP_CORE)) {
                return InteractionResult.PASS;
            }
            CampBlueprintProjection.useCore(hit.getBlockPos(),
                    world.dimension().identifier().toString());
            ClientPlayNetworking.send(new BlueprintStatusRequest(hit.getBlockPos()));
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.gui.setScreen(new CivCraftManagementScreen(minecraft.gui.screen()));
            return InteractionResult.SUCCESS;
        });
    }
}

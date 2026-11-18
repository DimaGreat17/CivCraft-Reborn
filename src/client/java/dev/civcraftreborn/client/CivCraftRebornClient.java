package dev.civcraftreborn.client;

import dev.civcraftreborn.settlement.CampBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;

/**
 * The physical camp core opens a management interface on right-click.
 * On a server the command actions remain fully authoritative.
 */
public final class CivCraftRebornClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CampBlueprintProjection.register();
        CivCraftGhostBlockRenderer.register();
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!world.isClientSide() ||
                    !world.getBlockState(hit.getBlockPos()).is(CampBlocks.CAMP_CORE)) {
                return InteractionResult.PASS;
            }
            CampBlueprintProjection.useCore(hit.getBlockPos(),
                    world.dimension().identifier().toString());
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.gui.setScreen(new CivCraftManagementScreen(minecraft.gui.screen()));
            return InteractionResult.SUCCESS;
        });
    }
}

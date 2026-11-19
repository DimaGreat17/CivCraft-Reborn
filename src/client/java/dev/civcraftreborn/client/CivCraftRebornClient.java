package dev.civcraftreborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.civcraftreborn.settlement.CampBlocks;
import dev.civcraftreborn.settlement.BlueprintStatusRequest;
import dev.civcraftreborn.settlement.BlueprintStatusResponse;
import dev.civcraftreborn.settlement.HeartPlacementResponse;
import dev.civcraftreborn.settlement.HeartPlacedNotice;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

/**
 * CivCraft management is opened from a dedicated configurable key mapping.
 * Right-clicking the physical Camp Heart NEVER opens the management screen.
 */
public final class CivCraftRebornClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY=KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("civcraft_reborn","controls"));
    private static KeyMapping openMenu;

    @Override
    public void onInitializeClient() {
        CampBlueprintProjection.register();
        CivCraftGhostBlockRenderer.register();
        ClientPlayNetworking.registerGlobalReceiver(BlueprintStatusResponse.TYPE,
                (payload, context) -> context.client().execute(() ->
                        CampBlueprintProjection.setServerStage(payload.core(), payload.stage())));

        ClientPlayNetworking.registerGlobalReceiver(HeartPlacementResponse.TYPE,
                (payload, context) -> context.client().execute(() ->
                        CampBlueprintProjection.setHeartPlacement(payload.core(),
                                payload.shiftX(),payload.shiftZ(),payload.turns(),
                                payload.confirmed())));

        ClientPlayNetworking.registerGlobalReceiver(HeartPlacedNotice.TYPE,
                (payload,context) -> context.client().execute(() -> {
                    Minecraft client=context.client();
                    if(client.level==null)return;
                    CampBlueprintProjection.useCore(payload.core(),
                            client.level.dimension().identifier().toString());
                    CampBlueprintProjection.setServerStage(payload.core(),-1);
                    CampBlueprintProjection.show(CampBlueprintProjection.Plan.HEART);
                    // No screen intrusion: the world-space hologram appears
                    // automatically and can be adjusted from the CivCraft menu.
                }));

        openMenu=KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.civcraft_reborn.open_menu",InputConstants.Type.KEYBOARD,
                InputConstants.KEY_G,CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(openMenu.consumeClick()) {
                if(client.player==null||client.level==null||client.gui.screen()!=null)continue;
                prepareNearbyFoundation(client);
                client.gui.setScreen(new CivCraftManagementScreen(null));
            }
        });
    }

    private static void prepareNearbyFoundation(Minecraft client) {
        BlockPos center=client.player.blockPosition();
        BlockPos nearest=null;
        double distance=Double.POSITIVE_INFINITY;
        // Local bounded scan; never request or force-load remote chunks.
        for(BlockPos pos:BlockPos.betweenClosed(
                center.offset(-12,-5,-12),center.offset(12,5,12))) {
            if(!client.level.hasChunkAt(pos))continue;
            if(!client.level.getBlockState(pos).is(CampBlocks.CAMP_CORE))continue;
            double d=pos.distSqr(center);
            if(d<distance) {nearest=pos.immutable();distance=d;}
        }
        if(nearest!=null){
            CampBlueprintProjection.useCore(nearest,
                    client.level.dimension().identifier().toString());
            ClientPlayNetworking.send(new BlueprintStatusRequest(nearest));
        }
    }
}

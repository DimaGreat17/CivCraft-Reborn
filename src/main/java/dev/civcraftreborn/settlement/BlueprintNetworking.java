package dev.civcraftreborn.settlement;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import dev.civcraftreborn.foundation.WorldSettingsSavedData;

/** Never trust client-supplied stage, permission or nearby settlement ID. */
public final class BlueprintNetworking {
    private BlueprintNetworking() {}

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(
                BlueprintStatusRequest.TYPE, BlueprintStatusRequest.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(
                BlueprintStatusResponse.TYPE, BlueprintStatusResponse.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BlueprintStatusRequest.TYPE,
                (request, context) -> {
                    ServerPlayer player = context.player();
                    BlockPos core = request.core();
                    // Same dimension and actual placed block, nearby, with no force-loading.
                    if (!WorldSettingsSavedData.get(context.server()).settings().civcraftWorld()
                            || player.blockPosition().distSqr(core) > 16 * 16
                            || !player.level().hasChunkAt(core)
                            || !player.level().getBlockState(core).is(CampBlocks.CAMP_CORE)) {
                        ServerPlayNetworking.send(player, new BlueprintStatusResponse(core, -1));
                        return;
                    }

                    String dimension = player.level().dimension().identifier().toString();
                    int stage = CampSavedData.get(context.server())
                            .at(dimension, core.getX(), core.getY(), core.getZ())
                            .filter(c -> c.owner().equals(player.getUUID().toString()))
                            .map(c -> c.stage() == SettlementStage.SETTLEMENT ? 1 : 0)
                            .orElse(-1);
                    ServerPlayNetworking.send(player, new BlueprintStatusResponse(core, stage));
                });
    }
}

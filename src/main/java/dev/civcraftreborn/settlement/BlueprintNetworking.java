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
        PayloadTypeRegistry.serverboundPlay().register(
                HeartSiteRequest.TYPE, HeartSiteRequest.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(
                BlueprintStatusResponse.TYPE, BlueprintStatusResponse.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(
                HeartPlacementResponse.TYPE, HeartPlacementResponse.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(
                HeartProgressResponse.TYPE, HeartProgressResponse.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(
                HeartPlacedNotice.TYPE, HeartPlacedNotice.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(
                HeartProjectCancelled.TYPE, HeartProjectCancelled.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(HeartSiteRequest.TYPE,
                (request,context) -> {
                    ServerPlayer player=context.player();
                    if(!WorldSettingsSavedData.get(context.server()).settings().civcraftWorld())return;
                    String dimension=player.level().dimension().identifier().toString();
                    var site=CampHeartSavedData.get(context.server())
                            .ownedBy(player.getUUID(),dimension).stream().findFirst();
                    if(site.isEmpty()) {
                        ServerPlayNetworking.send(player,new HeartProjectCancelled(BlockPos.ZERO));
                        return;
                    }
                    site.ifPresent(s -> {
                        int stage=CampSavedData.get(context.server())
                                .ownedBy(player.getUUID())
                                .filter(c -> c.dimension().equals(dimension))
                                .map(c -> c.stage()==SettlementStage.SETTLEMENT?1:0)
                                .orElse(-1);
                        ServerPlayNetworking.send(player,new BlueprintStatusResponse(s.core(),stage));
                        ServerPlayNetworking.send(player,new HeartPlacementResponse(s.core(),
                                s.shiftX(),s.shiftY(),s.shiftZ(),s.turns(),s.confirmed(),s.blueprintId()));
                        var progress=CampHeartTemplate.inspect(s.blueprintId(),
                                (net.minecraft.server.level.ServerLevel)player.level(),
                                s.core(),s.shiftX(),s.shiftY(),s.shiftZ(),s.turns());
                        ServerPlayNetworking.send(player,new HeartProgressResponse(s.core(),
                                progress.matched(),progress.total(),progress.loaded()));
                    });
                });

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
                    CampHeartSavedData.get(context.server()).byCore(dimension,core)
                            .filter(s->s.owner().equals(player.getUUID().toString()))
                            .ifPresent(site->ServerPlayNetworking.send(player,
                                    new HeartPlacementResponse(core,site.shiftX(),site.shiftY(),
                                            site.shiftZ(),site.turns(),site.confirmed(),site.blueprintId())));
                });
    }
}

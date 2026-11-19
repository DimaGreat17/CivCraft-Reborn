package dev.civcraftreborn.settlement;

import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/**
 * Cancel only UNCONFIRMED construction when its physical starter is destroyed.
 * A confirmed site has already intentionally consumed its foundation and must
 * not be cancelled. World mutations by players, explosives and other causes
 * are all covered by inspecting the real block state on the server.
 */
public final class CampHeartProjectLifecycle {
    private CampHeartProjectLifecycle() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CampHeartProjectLifecycle::tick);
    }

    static boolean shouldCancel(boolean confirmed,boolean chunkLoaded,boolean starterPresent) {
        return !confirmed && chunkLoaded && !starterPresent;
    }

    private static void tick(MinecraftServer server) {
        if(server.getTickCount()%10!=0)return;
        CampHeartSavedData saved=CampHeartSavedData.get(server);
        for(CampHeartSite site:saved.all()) {
            if(site.confirmed())continue;
            ServerLevel level=null;
            for(ServerLevel candidate:server.getAllLevels()) {
                if(candidate.dimension().identifier().toString().equals(site.dimension())) {
                    level=candidate;
                    break;
                }
            }
            if(level==null)continue;
            boolean loaded=level.hasChunkAt(site.core());
            boolean stillPresent=loaded&&level.getBlockState(site.core()).is(CampBlocks.CAMP_CORE);
            if(!shouldCancel(site.confirmed(),loaded,stillPresent))continue;

            // Deleting a pending project is an idempotent server-side mutation.
            saved.remove(site.dimension(),site.core());
            var owner=server.getPlayerList().getPlayer(UUID.fromString(site.owner()));
            if(owner!=null) {
                ServerPlayNetworking.send(owner,new HeartProjectCancelled(site.core()));
                owner.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "CivCraft: construction plan cancelled — its starter block was broken."));
            }
        }
    }
}

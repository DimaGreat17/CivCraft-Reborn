package dev.civcraftreborn.settlement;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import net.minecraft.network.chat.Component;
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
    private static final Set<String> READY_ANNOUNCED=new HashSet<>();
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
            ServerLevel level=null;
            for(ServerLevel candidate:server.getAllLevels()) {
                if(candidate.dimension().identifier().toString().equals(site.dimension())) {
                    level=candidate;
                    break;
                }
            }
            if(level==null)continue;
            if(site.confirmed()) {
                var owner=server.getPlayerList().getPlayer(UUID.fromString(site.owner()));
                if(owner==null || !owner.level().dimension().identifier().toString().equals(site.dimension()))
                    continue;
                // Read-only full world check; a blueprint is ready only if every
                // mandatory physical block exists with the expected state.
                var check=CampHeartTemplate.inspect(level,site.core(),site.shiftX(),
                        site.shiftY(),site.shiftZ(),site.turns());
                if(check.complete() && CampSavedData.get(server)
                        .ownedBy(owner.getUUID()).isEmpty()) {
                    if(READY_ANNOUNCED.add(site.id()))
                        owner.sendSystemMessage(Component.literal(
                                "CivCraft: Camp Heart COMPLETE (162/162). "
                                +"Open CivCraft -> Основать лагерь to register your camp."));
                } else READY_ANNOUNCED.remove(site.id());
                continue;
            }
            READY_ANNOUNCED.remove(site.id());
            boolean loaded=level.hasChunkAt(site.core());
            boolean stillPresent=loaded&&level.getBlockState(site.core()).is(CampBlocks.CAMP_CORE);
            if(!shouldCancel(site.confirmed(),loaded,stillPresent))continue;

            // Deleting a pending project is an idempotent server-side mutation.
            saved.remove(site.dimension(),site.core());
            READY_ANNOUNCED.remove(site.id());
            var owner=server.getPlayerList().getPlayer(UUID.fromString(site.owner()));
            if(owner!=null) {
                ServerPlayNetworking.send(owner,new HeartProjectCancelled(site.core()));
                owner.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "CivCraft: construction plan cancelled — its starter block was broken."));
            }
        }
    }
}

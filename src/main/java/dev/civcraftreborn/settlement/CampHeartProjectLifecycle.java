package dev.civcraftreborn.settlement;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import dev.civcraftreborn.CivCraftReborn;

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
    private static final Set<String> MISSING_SCHEMAS_WARNED=new HashSet<>();
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
                // Completion belongs to the building, not to the player being
                // online or standing in a particular dimension. SavedData
                // therefore reflects the physical world even on a server.
                CampHeartTemplate.Check check;
                try {
                    check=CampHeartTemplate.inspect(site.blueprintId(),level,site.core(),
                            site.shiftX(),site.shiftY(),site.shiftZ(),site.turns());
                } catch(IllegalStateException missingBlueprint) {
                    // Do not silently switch a persisted project to another
                    // definition: it could require rebuilding a different house.
                    // Skip this site without crashing server ticks or erasing it.
                    if(MISSING_SCHEMAS_WARNED.add(site.id()))
                        CivCraftReborn.LOGGER.error(
                                "Construction {} is frozen: packaged blueprint {} missing; restore resource",
                                site.id(),site.blueprintId(),missingBlueprint);
                    continue;
                }
                if(!check.loaded())continue; // Never mark damaged because of unloaded chunks.
                boolean complete=check.complete();
                if(complete != site.completed()) {
                    saved.replace(site,site.withCompletion(complete));
                    var owner=server.getPlayerList().getPlayer(UUID.fromString(site.owner()));
                    if(owner!=null) {
                        owner.sendSystemMessage(Component.literal(
                                complete
                                    ? "CivCraft: строительство сердца лагеря ЗАВЕРШЕНО ("
                                        +check.total()+"/"+check.total()+"). "
                                        +"Теперь нажмите «Основать лагерь» в меню CivCraft."
                                    : "CivCraft: постройка сердца лагеря повреждена ("
                                        +check.matched()+"/"+check.total()+"). "
                                        +"Проверьте /civcraft camp heart inspect."));
                    }
                }
                continue;
            }
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

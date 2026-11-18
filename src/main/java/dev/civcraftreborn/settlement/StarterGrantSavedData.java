package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * World-scoped receipt: placing another core or reconnecting never repeats the
 * first core construction grant. Server-only; clients cannot write receipts.
 */
public final class StarterGrantSavedData extends SavedData {
    private static final SavedDataType<StarterGrantSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID, "starter_grants"),
            StarterGrantSavedData::fresh,
            StarterGrantState.CODEC.xmap(StarterGrantSavedData::load, StarterGrantSavedData::snapshot),
            null);

    private final List<String> claimed = new ArrayList<>();

    private StarterGrantSavedData(List<String> received) {
        claimed.addAll(received);
    }

    private static StarterGrantSavedData fresh() {
        StarterGrantSavedData data = new StarterGrantSavedData(List.of());
        data.setDirty();
        return data;
    }

    private static StarterGrantSavedData load(StarterGrantState state) {
        return new StarterGrantSavedData(state.claimedPlayers());
    }

    private StarterGrantState snapshot() {
        return new StarterGrantState(StarterGrantState.CURRENT_SCHEMA, List.copyOf(claimed));
    }

    public static StarterGrantSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public boolean hasClaimed(UUID player) {
        return claimed.contains(player.toString());
    }

    /** Only register once, on the server thread, on actual successful block placement. */
    public boolean registerPlacement(UUID player) {
        if (hasClaimed(player)) return false;
        claimed.add(player.toString());
        setDirty();
        return true;
    }
}

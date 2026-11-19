package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/** SavedData is the only authoritative registry of camp IDs and their stage. */
public final class CampSavedData extends SavedData {
    private static final SavedDataType<CampSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID, "settlements"),
            CampSavedData::fresh,
            CampRegistryState.CODEC.xmap(CampSavedData::load, CampSavedData::snapshot),
            null
    );

    private final List<CampRecord> camps = new ArrayList<>();

    private CampSavedData(List<CampRecord> records) {
        camps.addAll(records);
    }

    private static CampSavedData fresh() {
        CampSavedData data = new CampSavedData(List.of());
        data.setDirty();
        return data;
    }

    private static CampSavedData load(CampRegistryState state) {
        return new CampSavedData(state.camps());
    }

    private CampRegistryState snapshot() {
        return new CampRegistryState(CampRegistryState.CURRENT_SCHEMA, List.copyOf(camps));
    }

    public static CampSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public List<CampRecord> all() {
        return List.copyOf(camps);
    }

    public Optional<CampRecord> ownedBy(UUID player) {
        return camps.stream().filter(c -> c.owner().equals(player.toString())).findFirst();
    }

    public Optional<CampRecord> at(String dimension, int x, int y, int z) {
        return camps.stream().filter(c -> c.dimension().equals(dimension)
                && c.x() == x && c.y() == y && c.z() == z).findFirst();
    }

    public Optional<CampRecord> closest(String dimension, int x, int z) {
        return camps.stream().filter(c -> c.dimension().equals(dimension)).min(
                (a,b) -> Long.compare(distanceSquared(a,x,z),distanceSquared(b,x,z)));
    }

    private static long distanceSquared(CampRecord c, int x, int z) {
        long dx = (long)c.x()-x, dz=(long)c.z()-z;
        return dx*dx+dz*dz;
    }

    public void add(CampRecord camp) {
        if (camps.stream().anyMatch(c -> c.id().equals(camp.id())
                || (c.dimension().equals(camp.dimension()) && c.x() == camp.x()
                && c.y() == camp.y() && c.z() == camp.z()))) {
            throw new IllegalStateException("Duplicate settlement");
        }
        camps.add(camp);
        setDirty();
    }

    public void replace(CampRecord previous, CampRecord updated) {
        if (!previous.id().equals(updated.id())) throw new IllegalArgumentException("ID changed");
        int index = camps.indexOf(previous);
        if (index < 0) throw new IllegalStateException("Stale camp revision");
        camps.set(index, updated);
        setDirty();
    }

    public void remove(CampRecord camp) {
        if (camps.remove(camp)) setDirty();
    }
}

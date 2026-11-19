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

/** Loaded entity and saved CitizenId are separate projections of one person. */
public final class CitizenSavedData extends SavedData {
    private static final SavedDataType<CitizenSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID, "citizens"),
            CitizenSavedData::fresh,
            CitizenRegistryState.CODEC.xmap(CitizenSavedData::load, CitizenSavedData::snapshot),
            null);

    private final List<CitizenRecord> citizens = new ArrayList<>();

    private CitizenSavedData(List<CitizenRecord> records) {
        citizens.addAll(records);
    }

    private static CitizenSavedData fresh() {
        CitizenSavedData data = new CitizenSavedData(List.of());
        data.setDirty();
        return data;
    }

    private static CitizenSavedData load(CitizenRegistryState state) {
        return new CitizenSavedData(state.citizens());
    }

    private CitizenRegistryState snapshot() {
        return new CitizenRegistryState(CitizenRegistryState.CURRENT_SCHEMA, List.copyOf(citizens));
    }

    public static CitizenSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public List<CitizenRecord> all() {
        return List.copyOf(citizens);
    }

    public Optional<CitizenRecord> byEntity(UUID uuid) {
        return citizens.stream().filter(c -> c.entityUuid().equals(uuid.toString())).findFirst();
    }

    public List<CitizenRecord> atCamp(CampRecord camp) {
        return citizens.stream().filter(c -> c.campId().equals(camp.id())).toList();
    }

    public boolean claimedBed(CampRecord camp, int x, int y, int z) {
        return citizens.stream().anyMatch(c -> c.campId().equals(camp.id())
                && c.bedX() == x && c.bedY() == y && c.bedZ() == z);
    }

    public void add(CitizenRecord citizen) {
        if (citizens.stream().anyMatch(c ->
                c.id().equals(citizen.id()) || c.entityUuid().equals(citizen.entityUuid()))) {
            throw new IllegalStateException("Citizen entity already registered");
        }
        citizens.add(citizen);
        setDirty();
    }
}

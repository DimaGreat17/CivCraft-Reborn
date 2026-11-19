package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class CitizenJobSavedData extends SavedData {
    private static final SavedDataType<CitizenJobSavedData> TYPE=new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"citizen_jobs"),
            CitizenJobSavedData::fresh,
            CitizenJobRegistryState.CODEC.xmap(CitizenJobSavedData::restore,
                    CitizenJobSavedData::snapshot),null);
    private final List<CitizenJobAssignment> assignments=new ArrayList<>();
    private CitizenJobSavedData(List<CitizenJobAssignment> data) { assignments.addAll(data); }
    private static CitizenJobSavedData fresh() {
        var d=new CitizenJobSavedData(List.of());d.setDirty();return d;
    }
    private static CitizenJobSavedData restore(CitizenJobRegistryState s) {
        return new CitizenJobSavedData(s.assignments());
    }
    private CitizenJobRegistryState snapshot() {
        return new CitizenJobRegistryState(1,List.copyOf(assignments));
    }
    public static CitizenJobSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    public List<CitizenJobAssignment> all() { return List.copyOf(assignments); }
    public Optional<CitizenJobAssignment> forCitizen(String id) {
        return assignments.stream().filter(a->a.citizenId().equals(id)).findFirst();
    }
    public void assign(CitizenJobAssignment value) {
        assignments.removeIf(a->a.citizenId().equals(value.citizenId()));
        assignments.add(value);
        setDirty();
    }
    public void unassign(String citizenId) {
        if(assignments.removeIf(a->a.citizenId().equals(citizenId)))setDirty();
    }
}

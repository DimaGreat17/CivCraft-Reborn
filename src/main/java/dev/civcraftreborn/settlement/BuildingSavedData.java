package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class BuildingSavedData extends SavedData {
    private static final SavedDataType<BuildingSavedData> TYPE=new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"buildings"),
            BuildingSavedData::fresh,
            BuildingRegistryState.CODEC.xmap(BuildingSavedData::restore,BuildingSavedData::snapshot),
            null);
    private final List<BuildingRecord> buildings = new ArrayList<>();
    private BuildingSavedData(List<BuildingRecord> records) { buildings.addAll(records); }
    private static BuildingSavedData fresh() {
        BuildingSavedData d=new BuildingSavedData(List.of());d.setDirty();return d;
    }
    private static BuildingSavedData restore(BuildingRegistryState s) {
        return new BuildingSavedData(s.buildings());
    }
    private BuildingRegistryState snapshot() { return new BuildingRegistryState(1,buildings); }
    public static BuildingSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    public List<BuildingRecord> atCamp(CampRecord camp) {
        return buildings.stream().filter(b->b.campId().equals(camp.id())).toList();
    }
    public void add(BuildingRecord building) {
        if(buildings.stream().anyMatch(b->b.id().equals(building.id())||
                (b.campId().equals(building.campId())&&b.x()==building.x()
                        &&b.y()==building.y()&&b.z()==building.z())))
            throw new IllegalStateException("Duplicate building identity or origin");
        buildings.add(building);
        setDirty();
    }
}

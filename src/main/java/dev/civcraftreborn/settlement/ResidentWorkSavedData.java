package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/** Server-owned durable citizen to facility assignments. */
public final class ResidentWorkSavedData extends SavedData {
    private static final SavedDataType<ResidentWorkSavedData> TYPE=new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"resident_jobs"),
            ResidentWorkSavedData::fresh,
            ResidentWorkState.CODEC.xmap(ResidentWorkSavedData::restore,ResidentWorkSavedData::snapshot),
            null);
    private final List<ResidentAssignment> assignments=new ArrayList<>();
    private ResidentWorkSavedData(List<ResidentAssignment> data){assignments.addAll(data);}
    private static ResidentWorkSavedData fresh(){
        ResidentWorkSavedData s=new ResidentWorkSavedData(List.of());s.setDirty();return s;
    }
    private static ResidentWorkSavedData restore(ResidentWorkState s){
        return new ResidentWorkSavedData(s.assignments());
    }
    private ResidentWorkState snapshot(){return new ResidentWorkState(1,assignments);}
    public static ResidentWorkSavedData get(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    public List<ResidentAssignment> all(){return List.copyOf(assignments);}
    public Optional<ResidentAssignment> byCitizen(String citizenId){
        return assignments.stream().filter(a->a.citizenId().equals(citizenId)).findFirst();
    }
    public void assign(ResidentAssignment assignment){
        assignments.removeIf(a->a.citizenId().equals(assignment.citizenId()));
        assignments.add(assignment);setDirty();
    }
}

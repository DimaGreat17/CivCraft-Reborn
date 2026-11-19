package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class ConstructionSavedData extends SavedData {
    private static final SavedDataType<ConstructionSavedData> TYPE=new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"construction_orders"),
            ConstructionSavedData::fresh,
            ConstructionRegistryState.CODEC.xmap(ConstructionSavedData::restore,
                    ConstructionSavedData::snapshot),null);
    private final List<ConstructionOrder> orders=new ArrayList<>();
    private ConstructionSavedData(List<ConstructionOrder> records){orders.addAll(records);}
    private static ConstructionSavedData fresh(){
        ConstructionSavedData s=new ConstructionSavedData(List.of());s.setDirty();return s;
    }
    private static ConstructionSavedData restore(ConstructionRegistryState s){
        return new ConstructionSavedData(s.orders());
    }
    private ConstructionRegistryState snapshot(){return new ConstructionRegistryState(1,orders);}
    public static ConstructionSavedData get(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    public List<ConstructionOrder> all(){return List.copyOf(orders);}
    public List<ConstructionOrder> atCamp(CampRecord c){
        return orders.stream().filter(o->o.campId().equals(c.id())).toList();
    }
    public boolean citizenBusy(String citizenId){
        return orders.stream().anyMatch(o->o.citizenId().equals(citizenId));
    }
    public void add(ConstructionOrder order){
        if(citizenBusy(order.citizenId()))throw new IllegalStateException("Builder is already busy");
        if(orders.stream().anyMatch(o->o.id().equals(order.id())||
                (o.campId().equals(order.campId())&&o.x()==order.x()
                        &&o.y()==order.y()&&o.z()==order.z())))
            throw new IllegalStateException("Work order already exists");
        orders.add(order);setDirty();
    }
    public void complete(ConstructionOrder order){
        if(orders.remove(order))setDirty();
    }
}

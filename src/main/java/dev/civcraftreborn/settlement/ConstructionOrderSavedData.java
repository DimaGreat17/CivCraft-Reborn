package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class ConstructionOrderSavedData extends SavedData {
    private static final SavedDataType<ConstructionOrderSavedData> TYPE=new SavedDataType<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"construction_orders"),
            ConstructionOrderSavedData::fresh,
            ConstructionOrderState.CODEC.xmap(ConstructionOrderSavedData::restore,
                    ConstructionOrderSavedData::snapshot),null);
    private final List<ConstructionOrder> orders=new ArrayList<>();
    private ConstructionOrderSavedData(List<ConstructionOrder> items) { orders.addAll(items); }
    private static ConstructionOrderSavedData fresh() {
        var d=new ConstructionOrderSavedData(List.of());d.setDirty();return d;
    }
    private static ConstructionOrderSavedData restore(ConstructionOrderState s) {
        return new ConstructionOrderSavedData(s.orders());
    }
    private ConstructionOrderState snapshot() {
        return new ConstructionOrderState(1,List.copyOf(orders));
    }
    public static ConstructionOrderSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    public List<ConstructionOrder> all() { return List.copyOf(orders); }
    public Optional<ConstructionOrder> byId(String id) {
        return orders.stream().filter(o->o.id().equals(id)).findFirst();
    }
    public List<ConstructionOrder> atCamp(CampRecord camp) {
        return orders.stream().filter(o->o.campId().equals(camp.id())).toList();
    }
    public void add(ConstructionOrder order) {
        if(orders.stream().anyMatch(o->o.id().equals(order.id())||
                (o.campId().equals(order.campId())&&o.x()==order.x()
                        &&o.y()==order.y()&&o.z()==order.z())))
            throw new IllegalStateException("Duplicate construction worksite");
        orders.add(order);setDirty();
    }
    public void finish(String id) {
        if(orders.removeIf(o->o.id().equals(id)))setDirty();
    }
}

package dev.civcraftreborn.settlement;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ConstructionRegistryState(int schema,List<ConstructionOrder> orders) {
    public static final Codec<ConstructionRegistryState> CODEC=RecordCodecBuilder.create(i->
            i.group(Codec.INT.fieldOf("schema").forGetter(ConstructionRegistryState::schema),
                    ConstructionOrder.CODEC.listOf().fieldOf("orders")
                            .forGetter(ConstructionRegistryState::orders))
                    .apply(i,ConstructionRegistryState::new));
    public ConstructionRegistryState {
        if(schema!=1)throw new IllegalArgumentException("Unsupported work order schema");
        orders=List.copyOf(orders);
        if(orders.stream().map(ConstructionOrder::id).distinct().count()!=orders.size())
            throw new IllegalArgumentException("Duplicate construction orders");
    }
}

package dev.civcraftreborn.settlement;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ConstructionOrderState(int schema,List<ConstructionOrder> orders) {
    public static final Codec<ConstructionOrderState> CODEC=RecordCodecBuilder.create(i ->
            i.group(Codec.INT.fieldOf("schema").forGetter(ConstructionOrderState::schema),
                    ConstructionOrder.CODEC.listOf().fieldOf("orders")
                            .forGetter(ConstructionOrderState::orders))
                    .apply(i,ConstructionOrderState::new));
    public ConstructionOrderState {
        if(schema!=1)throw new IllegalArgumentException("Unsupported construction schema");
        orders=List.copyOf(orders);
        if(orders.stream().map(ConstructionOrder::id).distinct().count()!=orders.size())
            throw new IllegalArgumentException("Duplicate construction IDs");
    }
}

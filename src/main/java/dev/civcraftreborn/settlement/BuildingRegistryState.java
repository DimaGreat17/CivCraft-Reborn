package dev.civcraftreborn.settlement;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BuildingRegistryState(int schema, List<BuildingRecord> buildings) {
    public static final Codec<BuildingRegistryState> CODEC=RecordCodecBuilder.create(i ->
            i.group(Codec.INT.fieldOf("schema").forGetter(BuildingRegistryState::schema),
                    BuildingRecord.CODEC.listOf().fieldOf("buildings")
                            .forGetter(BuildingRegistryState::buildings))
                    .apply(i,BuildingRegistryState::new));
    public BuildingRegistryState {
        if(schema!=1)throw new IllegalArgumentException("Unsupported buildings schema");
        buildings=List.copyOf(buildings);
        if(buildings.stream().map(BuildingRecord::id).distinct().count()!=buildings.size())
            throw new IllegalArgumentException("Duplicate building IDs");
    }
}

package dev.civcraftreborn.settlement;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CitizenJobRegistryState(int schema, List<CitizenJobAssignment> assignments) {
    public static final Codec<CitizenJobRegistryState> CODEC=RecordCodecBuilder.create(i ->
            i.group(Codec.INT.fieldOf("schema").forGetter(CitizenJobRegistryState::schema),
                    CitizenJobAssignment.CODEC.listOf().fieldOf("assignments")
                            .forGetter(CitizenJobRegistryState::assignments))
                    .apply(i,CitizenJobRegistryState::new));
    public CitizenJobRegistryState {
        if(schema!=1)throw new IllegalArgumentException("Unsupported citizen jobs schema");
        assignments=List.copyOf(assignments);
        if(assignments.stream().map(CitizenJobAssignment::citizenId).distinct().count()!=assignments.size())
            throw new IllegalArgumentException("Citizen has multiple active jobs");
    }
}

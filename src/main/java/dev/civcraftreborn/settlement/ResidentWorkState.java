package dev.civcraftreborn.settlement;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ResidentWorkState(int schema, List<ResidentAssignment> assignments) {
    public static final Codec<ResidentWorkState> CODEC=RecordCodecBuilder.create(i->
            i.group(Codec.INT.fieldOf("schema").forGetter(ResidentWorkState::schema),
                    ResidentAssignment.CODEC.listOf().fieldOf("assignments")
                            .forGetter(ResidentWorkState::assignments))
                    .apply(i,ResidentWorkState::new));
    public ResidentWorkState {
        if(schema!=1)throw new IllegalArgumentException("Unsupported resident jobs schema");
        assignments=List.copyOf(assignments);
        if(assignments.stream().map(ResidentAssignment::citizenId).distinct().count()
                !=assignments.size())throw new IllegalArgumentException("Duplicate citizen roles");
    }
}

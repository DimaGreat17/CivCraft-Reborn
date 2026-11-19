package dev.civcraftreborn.settlement;

import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * One citizen, one active role. Target is a BuildingId for professionals, or a
 * ConstructionOrderId for a builder. No phantom citizens can be created here.
 */
public record CitizenJobAssignment(String citizenId, CitizenJob role, String targetId) {
    public static final Codec<CitizenJobAssignment> CODEC=RecordCodecBuilder.create(i ->
            i.group(
                Codec.STRING.fieldOf("citizen_id").forGetter(CitizenJobAssignment::citizenId),
                Codec.STRING.xmap(CitizenJob::valueOf,CitizenJob::name)
                        .fieldOf("role").forGetter(CitizenJobAssignment::role),
                Codec.STRING.fieldOf("target_id").forGetter(CitizenJobAssignment::targetId)
            ).apply(i,CitizenJobAssignment::new));

    public CitizenJobAssignment {
        UUID.fromString(citizenId);
        UUID.fromString(targetId);
        if(role==null || role==CitizenJob.UNEMPLOYED)
            throw new IllegalArgumentException("Active job must have a nonempty occupation");
    }
}

package dev.civcraftreborn.settlement;

import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** A citizen cannot change jobs without an explicit authoritative reassignment. */
public record ResidentAssignment(String citizenId, String buildingId,
        ResidentWorkRole role) {
    public static final Codec<ResidentAssignment> CODEC=RecordCodecBuilder.create(i->
            i.group(
                    Codec.STRING.fieldOf("citizen_id").forGetter(ResidentAssignment::citizenId),
                    Codec.STRING.fieldOf("building_id").forGetter(ResidentAssignment::buildingId),
                    Codec.STRING.xmap(ResidentWorkRole::valueOf,ResidentWorkRole::name)
                            .fieldOf("role").forGetter(ResidentAssignment::role)
            ).apply(i,ResidentAssignment::new));
    public ResidentAssignment {
        UUID.fromString(citizenId);
        if(!buildingId.isEmpty())UUID.fromString(buildingId);
        if(role==null)throw new IllegalArgumentException("Role required");
        if(role!=ResidentWorkRole.BUILDER&&buildingId.isBlank())
            throw new IllegalArgumentException("Building required for non-builder role");
    }
}

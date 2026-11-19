package dev.civcraftreborn.settlement;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CitizenRegistryState(int schema, List<CitizenRecord> citizens) {
    public static final int CURRENT_SCHEMA = 1;
    public static final Codec<CitizenRegistryState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("schema").forGetter(CitizenRegistryState::schema),
                    CitizenRecord.CODEC.listOf().fieldOf("citizens").forGetter(CitizenRegistryState::citizens)
            ).apply(instance, CitizenRegistryState::new));

    public CitizenRegistryState {
        if (schema != CURRENT_SCHEMA) throw new IllegalArgumentException("Unknown citizen schema");
        citizens = List.copyOf(citizens);
        if (citizens.stream().map(CitizenRecord::id).distinct().count() != citizens.size()
                || citizens.stream().map(CitizenRecord::entityUuid).distinct().count() != citizens.size()) {
            throw new IllegalArgumentException("Citizen or world entity registered twice");
        }
    }
}

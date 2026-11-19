package dev.civcraftreborn.settlement;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CampRegistryState(int schema, List<CampRecord> camps) {
    public static final int CURRENT_SCHEMA = 1;
    public static final Codec<CampRegistryState> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("schema").forGetter(CampRegistryState::schema),
            CampRecord.CODEC.listOf().fieldOf("camps").forGetter(CampRegistryState::camps)
        ).apply(instance, CampRegistryState::new)
    );

    public CampRegistryState {
        if (schema != CURRENT_SCHEMA) {
            throw new IllegalArgumentException("Unsupported settlement save version " + schema);
        }
        camps = List.copyOf(camps);
        if (camps.stream().map(CampRecord::id).distinct().count() != camps.size()) {
            throw new IllegalArgumentException("Duplicate settlement ID");
        }
    }
}

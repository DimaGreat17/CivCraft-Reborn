package dev.civcraftreborn.settlement;

import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Persistent, server-owned settlement identity and anchor, no synthetic resources. */
public record CampRecord(
        String id, String owner, String name,
        String dimension, int x, int y, int z,
        SettlementStage stage, long createdGameTime) {
    public static final Codec<CampRecord> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(CampRecord::id),
            Codec.STRING.fieldOf("owner").forGetter(CampRecord::owner),
            Codec.STRING.fieldOf("name").forGetter(CampRecord::name),
            Codec.STRING.fieldOf("dimension").forGetter(CampRecord::dimension),
            Codec.INT.fieldOf("x").forGetter(CampRecord::x),
            Codec.INT.fieldOf("y").forGetter(CampRecord::y),
            Codec.INT.fieldOf("z").forGetter(CampRecord::z),
            Codec.STRING.xmap(SettlementStage::valueOf, SettlementStage::name)
                .fieldOf("stage").forGetter(CampRecord::stage),
            Codec.LONG.fieldOf("created_game_time").forGetter(CampRecord::createdGameTime)
        ).apply(instance, CampRecord::new)
    );

    public CampRecord {
        UUID.fromString(id);
        UUID.fromString(owner);
        if (name == null || name.isBlank() || name.length() > 48) {
            throw new IllegalArgumentException("Invalid settlement name");
        }
        if (dimension == null || dimension.isBlank()) throw new IllegalArgumentException("Dimension required");
        if (stage == null || createdGameTime < 0) throw new IllegalArgumentException("Invalid camp state");
    }

    public CampRecord promoted() {
        return new CampRecord(id, owner, name, dimension, x, y, z,
                SettlementStage.SETTLEMENT, createdGameTime);
    }
}

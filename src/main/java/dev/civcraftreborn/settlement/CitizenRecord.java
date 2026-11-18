package dev.civcraftreborn.settlement;

import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** A known physical Minecraft NPC entity is the only source of a new citizen. */
public record CitizenRecord(String id, String entityUuid, String campId,
        int bedX, int bedY, int bedZ, long joinedGameTime) {
    public static final Codec<CitizenRecord> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("id").forGetter(CitizenRecord::id),
                    Codec.STRING.fieldOf("entity_uuid").forGetter(CitizenRecord::entityUuid),
                    Codec.STRING.fieldOf("camp_id").forGetter(CitizenRecord::campId),
                    Codec.INT.fieldOf("bed_x").forGetter(CitizenRecord::bedX),
                    Codec.INT.fieldOf("bed_y").forGetter(CitizenRecord::bedY),
                    Codec.INT.fieldOf("bed_z").forGetter(CitizenRecord::bedZ),
                    Codec.LONG.fieldOf("joined_game_time").forGetter(CitizenRecord::joinedGameTime)
            ).apply(instance, CitizenRecord::new));

    public CitizenRecord {
        UUID.fromString(id);
        UUID.fromString(entityUuid);
        UUID.fromString(campId);
        if (joinedGameTime < 0) throw new IllegalArgumentException("Invalid join time");
    }

    public static CitizenRecord recruit(UUID villager, CampRecord camp, int x, int y, int z,
            long gameTime) {
        return new CitizenRecord(UUID.randomUUID().toString(), villager.toString(),
                camp.id(), x, y, z, gameTime);
    }
}

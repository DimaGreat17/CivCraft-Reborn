package dev.civcraftreborn.settlement;

import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Stable identity of an actual verified, player-built facility. */
public record BuildingRecord(String id, String campId, String dimension,
        FunctionalBuildingTemplates.Type type, int x, int y, int z, int rotation) {
    public static final Codec<BuildingRecord> CODEC=RecordCodecBuilder.create(i ->
            i.group(
                    Codec.STRING.fieldOf("id").forGetter(BuildingRecord::id),
                    Codec.STRING.fieldOf("camp_id").forGetter(BuildingRecord::campId),
                    Codec.STRING.fieldOf("dimension").forGetter(BuildingRecord::dimension),
                    Codec.STRING.fieldOf("type").xmap(
                            FunctionalBuildingTemplates.Type::valueOf,
                            FunctionalBuildingTemplates.Type::name)
                            .fieldOf("type").forGetter(BuildingRecord::type),
                    Codec.INT.fieldOf("x").forGetter(BuildingRecord::x),
                    Codec.INT.fieldOf("y").forGetter(BuildingRecord::y),
                    Codec.INT.fieldOf("z").forGetter(BuildingRecord::z),
                    Codec.INT.fieldOf("rotation").forGetter(BuildingRecord::rotation)
            ).apply(i,BuildingRecord::new));
    public BuildingRecord {
        UUID.fromString(id);
        UUID.fromString(campId);
        if(dimension==null||dimension.isBlank()||type==null||rotation<0||rotation>3)
            throw new IllegalArgumentException("Invalid building metadata");
    }
    public static BuildingRecord create(CampRecord camp,
            FunctionalBuildingTemplates.Type type, int x,int y,int z,int rotation) {
        return new BuildingRecord(UUID.randomUUID().toString(),camp.id(),
                camp.dimension(),type,x,y,z,rotation);
    }
}

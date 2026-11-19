package dev.civcraftreborn.settlement;

import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Immutable work request against physical world blocks and a real citizen. */
public record ConstructionOrder(String id, String campId, String citizenId,
        String dimension, FunctionalBuildingTemplates.Type type, int x, int y,
        int z, int rotation, long createdTime) {
    public static final Codec<ConstructionOrder> CODEC=RecordCodecBuilder.create(i->
            i.group(
                    Codec.STRING.fieldOf("id").forGetter(ConstructionOrder::id),
                    Codec.STRING.fieldOf("camp_id").forGetter(ConstructionOrder::campId),
                    Codec.STRING.fieldOf("citizen_id").forGetter(ConstructionOrder::citizenId),
                    Codec.STRING.fieldOf("dimension").forGetter(ConstructionOrder::dimension),
                    Codec.STRING.xmap(FunctionalBuildingTemplates.Type::valueOf,
                            FunctionalBuildingTemplates.Type::name)
                            .fieldOf("type").forGetter(ConstructionOrder::type),
                    Codec.INT.fieldOf("x").forGetter(ConstructionOrder::x),
                    Codec.INT.fieldOf("y").forGetter(ConstructionOrder::y),
                    Codec.INT.fieldOf("z").forGetter(ConstructionOrder::z),
                    Codec.INT.fieldOf("rotation").forGetter(ConstructionOrder::rotation),
                    Codec.LONG.fieldOf("created_time").forGetter(ConstructionOrder::createdTime)
            ).apply(i,ConstructionOrder::new));
    public ConstructionOrder {
        UUID.fromString(id);UUID.fromString(campId);UUID.fromString(citizenId);
        if(type==null||dimension==null||dimension.isBlank()||
                rotation<0||rotation>3||createdTime<0)
            throw new IllegalArgumentException("Invalid construction order");
    }
    public static ConstructionOrder issue(CampRecord camp,CitizenRecord builder,
            FunctionalBuildingTemplates.Type type,int x,int y,int z,
            int rotation,long gameTime) {
        return new ConstructionOrder(UUID.randomUUID().toString(),camp.id(),
                builder.id(),camp.dimension(),type,x,y,z,rotation,gameTime);
    }
}

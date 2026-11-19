package dev.civcraftreborn.settlement;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CampHeartSiteState(int schema,List<CampHeartSite> sites) {
    public static final Codec<CampHeartSiteState> CODEC=RecordCodecBuilder.create(i->i.group(
            Codec.INT.fieldOf("schema").forGetter(CampHeartSiteState::schema),
            CampHeartSite.CODEC.listOf().fieldOf("sites").forGetter(CampHeartSiteState::sites)
    ).apply(i,CampHeartSiteState::new));
    public CampHeartSiteState {
        if(schema!=1)throw new IllegalArgumentException("Unknown heart site schema: "+schema);
        sites=List.copyOf(sites);
    }
}

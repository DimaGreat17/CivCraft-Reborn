package dev.civcraftreborn.settlement;

import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

/** Persistent placement of one first-camp foundation; project foundation coordinates are retained separately from the actual building. */
public record CampHeartSite(String id,String owner,String dimension,
        int x,int y,int z,int shiftX,int shiftZ,int turns,boolean confirmed) {
    public static final Codec<CampHeartSite> CODEC=RecordCodecBuilder.create(i->i.group(
            Codec.STRING.fieldOf("id").forGetter(CampHeartSite::id),
            Codec.STRING.fieldOf("owner").forGetter(CampHeartSite::owner),
            Codec.STRING.fieldOf("dimension").forGetter(CampHeartSite::dimension),
            Codec.INT.fieldOf("x").forGetter(CampHeartSite::x),
            Codec.INT.fieldOf("y").forGetter(CampHeartSite::y),
            Codec.INT.fieldOf("z").forGetter(CampHeartSite::z),
            Codec.INT.fieldOf("shift_x").forGetter(CampHeartSite::shiftX),
            Codec.INT.fieldOf("shift_z").forGetter(CampHeartSite::shiftZ),
            Codec.INT.fieldOf("turns").forGetter(CampHeartSite::turns),
            Codec.BOOL.fieldOf("confirmed").forGetter(CampHeartSite::confirmed)
    ).apply(i,CampHeartSite::new));
    public CampHeartSite {
        UUID.fromString(id);
        UUID.fromString(owner);
        if(dimension==null||dimension.isBlank()||
                !CampHeartTemplate.transformValid(shiftX,shiftZ,turns))
            throw new IllegalArgumentException("Invalid Camp Heart site");
    }
    public static CampHeartSite placed(UUID owner,String dimension,BlockPos core) {
        return new CampHeartSite(UUID.randomUUID().toString(),owner.toString(),dimension,
                core.getX(),core.getY(),core.getZ(),12,0,0,false);
    }
    public BlockPos core(){return new BlockPos(x,y,z);}
    public CampHeartSite withPlacement(int newX,int newZ,int newTurns) {
        if(confirmed)throw new IllegalStateException("Construction site is already confirmed");
        return new CampHeartSite(id,owner,dimension,x,y,z,newX,newZ,newTurns,false);
    }
    public CampHeartSite confirm() {
        return new CampHeartSite(id,owner,dimension,x,y,z,shiftX,shiftZ,turns,true);
    }
}

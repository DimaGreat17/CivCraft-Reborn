package dev.civcraftreborn.settlement;

import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

/** Persistent placement of one first-camp foundation; project foundation coordinates are retained separately from the actual building. */
public record CampHeartSite(String id,String owner,String dimension,
        int x,int y,int z,int shiftX,int shiftY,int shiftZ,int turns,boolean confirmed,boolean completed) {
    public static final Codec<CampHeartSite> CODEC=RecordCodecBuilder.create(i->i.group(
            Codec.STRING.fieldOf("id").forGetter(CampHeartSite::id),
            Codec.STRING.fieldOf("owner").forGetter(CampHeartSite::owner),
            Codec.STRING.fieldOf("dimension").forGetter(CampHeartSite::dimension),
            Codec.INT.fieldOf("x").forGetter(CampHeartSite::x),
            Codec.INT.fieldOf("y").forGetter(CampHeartSite::y),
            Codec.INT.fieldOf("z").forGetter(CampHeartSite::z),
            Codec.INT.fieldOf("shift_x").forGetter(CampHeartSite::shiftX),
            Codec.INT.optionalFieldOf("shift_y",0).forGetter(CampHeartSite::shiftY),
            Codec.INT.fieldOf("shift_z").forGetter(CampHeartSite::shiftZ),
            Codec.INT.fieldOf("turns").forGetter(CampHeartSite::turns),
            Codec.BOOL.fieldOf("confirmed").forGetter(CampHeartSite::confirmed),
            Codec.BOOL.optionalFieldOf("completed",false).forGetter(CampHeartSite::completed)
    ).apply(i,CampHeartSite::new));
    public CampHeartSite {
        UUID.fromString(id);
        UUID.fromString(owner);
        if(dimension==null||dimension.isBlank()||
                !CampHeartTemplate.transformValid(shiftX,shiftY,shiftZ,turns))
            throw new IllegalArgumentException("Invalid Camp Heart site");
    }
    public static CampHeartSite placed(UUID owner,String dimension,BlockPos core) {
        return new CampHeartSite(UUID.randomUUID().toString(),owner.toString(),dimension,
                core.getX(),core.getY(),core.getZ(),12,0,0,0,false,false);
    }
    public BlockPos core(){return new BlockPos(x,y,z);}
    public CampHeartSite withPlacement(int newX,int newZ,int newTurns) {
        return withPlacement(newX,shiftY,newZ,newTurns);
    }
    public CampHeartSite withPlacement(int newX,int newY,int newZ,int newTurns) {
        if(confirmed)throw new IllegalStateException("Construction site is already confirmed");
        return new CampHeartSite(id,owner,dimension,x,y,z,newX,newY,newZ,newTurns,false,false);
    }
    /** Authoritative dynamic completion state; persisted and revoked if a block breaks. */
    public CampHeartSite withCompletion(boolean value) {
        if(value && !confirmed)throw new IllegalStateException("Cannot complete an unconfirmed site");
        return new CampHeartSite(id,owner,dimension,x,y,z,shiftX,shiftY,shiftZ,turns,confirmed,value);
    }
    public CampHeartSite confirm() {
        return new CampHeartSite(id,owner,dimension,x,y,z,shiftX,shiftY,shiftZ,turns,true,false);
    }
}

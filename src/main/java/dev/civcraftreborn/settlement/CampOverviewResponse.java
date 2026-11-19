package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Read-only, server-verified settlement dashboard. Never grants or consumes resources. */
public record CampOverviewResponse(boolean exists,int stage,int residents,
        int rooms,int beds,int food,int storages,boolean workshop,
        boolean farm,boolean complete) implements CustomPacketPayload {
    public static final Type<CampOverviewResponse> TYPE=new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"camp_overview"));
    public static final StreamCodec<RegistryFriendlyByteBuf,CampOverviewResponse> CODEC=
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,CampOverviewResponse::exists,
                    ByteBufCodecs.INT,CampOverviewResponse::stage,
                    ByteBufCodecs.INT,CampOverviewResponse::residents,
                    ByteBufCodecs.INT,CampOverviewResponse::rooms,
                    ByteBufCodecs.INT,CampOverviewResponse::beds,
                    ByteBufCodecs.INT,CampOverviewResponse::food,
                    ByteBufCodecs.INT,CampOverviewResponse::storages,
                    ByteBufCodecs.BOOL,CampOverviewResponse::workshop,
                    ByteBufCodecs.BOOL,CampOverviewResponse::farm,
                    ByteBufCodecs.BOOL,CampOverviewResponse::complete,
                    CampOverviewResponse::new);

    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static CampOverviewResponse absent(){
        return new CampOverviewResponse(false,-1,0,0,0,0,0,false,false,false);
    }
}

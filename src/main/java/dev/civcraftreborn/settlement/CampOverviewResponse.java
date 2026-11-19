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
            new StreamCodec<>() {
                @Override public CampOverviewResponse decode(RegistryFriendlyByteBuf buf) {
                    return new CampOverviewResponse(buf.readBoolean(),buf.readInt(),buf.readInt(),
                            buf.readInt(),buf.readInt(),buf.readInt(),buf.readInt(),
                            buf.readBoolean(),buf.readBoolean(),buf.readBoolean());
                }
                @Override public void encode(RegistryFriendlyByteBuf buf,CampOverviewResponse value) {
                    buf.writeBoolean(value.exists());
                    buf.writeInt(value.stage());
                    buf.writeInt(value.residents());
                    buf.writeInt(value.rooms());
                    buf.writeInt(value.beds());
                    buf.writeInt(value.food());
                    buf.writeInt(value.storages());
                    buf.writeBoolean(value.workshop());
                    buf.writeBoolean(value.farm());
                    buf.writeBoolean(value.complete());
                }
            };
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static CampOverviewResponse absent(){
        return new CampOverviewResponse(false,-1,0,0,0,0,0,false,false,false);
    }
}

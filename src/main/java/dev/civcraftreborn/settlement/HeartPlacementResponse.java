package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Read-only server snapshot of the owned Camp Heart construction placement. */
public record HeartPlacementResponse(BlockPos core,int shiftX,int shiftY,int shiftZ,
        int turns,boolean confirmed,String blueprintId) implements CustomPacketPayload {
    public static final Type<HeartPlacementResponse> TYPE=new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"heart_placement"));
    public static final StreamCodec<RegistryFriendlyByteBuf,HeartPlacementResponse> CODEC=
            new StreamCodec<>() {
                @Override public HeartPlacementResponse decode(RegistryFriendlyByteBuf buf) {
                    return new HeartPlacementResponse(buf.readBlockPos(),buf.readInt(),
                            buf.readInt(),buf.readInt(),buf.readInt(),buf.readBoolean(),
                            buf.readUtf());
                }
                @Override public void encode(RegistryFriendlyByteBuf buf,HeartPlacementResponse value) {
                    buf.writeBlockPos(value.core());
                    buf.writeInt(value.shiftX());
                    buf.writeInt(value.shiftY());
                    buf.writeInt(value.shiftZ());
                    buf.writeInt(value.turns());
                    buf.writeBoolean(value.confirmed());
                    buf.writeUtf(value.blueprintId());
                }
            };
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
}

package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Read-only server snapshot of the owned Camp Heart construction placement. */
public record HeartPlacementResponse(BlockPos core,int shiftX,int shiftZ,
        int turns,boolean confirmed) implements CustomPacketPayload {
    public static final Type<HeartPlacementResponse> TYPE=new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"heart_placement"));
    public static final StreamCodec<RegistryFriendlyByteBuf,HeartPlacementResponse> CODEC=
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,HeartPlacementResponse::core,
                    ByteBufCodecs.INT,HeartPlacementResponse::shiftX,
                    ByteBufCodecs.INT,HeartPlacementResponse::shiftZ,
                    ByteBufCodecs.INT,HeartPlacementResponse::turns,
                    ByteBufCodecs.BOOL,HeartPlacementResponse::confirmed,
                    HeartPlacementResponse::new);
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
}

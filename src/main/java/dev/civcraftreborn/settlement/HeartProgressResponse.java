package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Read-only physical progress snapshot, always validated on the server. */
public record HeartProgressResponse(BlockPos core,int matched,int total,
        boolean loaded) implements CustomPacketPayload {
    public static final Type<HeartProgressResponse> TYPE=new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"heart_progress"));
    public static final StreamCodec<RegistryFriendlyByteBuf,HeartProgressResponse> CODEC=
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,HeartProgressResponse::core,
                    ByteBufCodecs.INT,HeartProgressResponse::matched,
                    ByteBufCodecs.INT,HeartProgressResponse::total,
                    ByteBufCodecs.BOOL,HeartProgressResponse::loaded,
                    HeartProgressResponse::new);
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
}

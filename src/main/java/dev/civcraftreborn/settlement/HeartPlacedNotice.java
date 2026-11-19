package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Sent to the placing player so the full Heart hologram appears immediately. */
public record HeartPlacedNotice(BlockPos core) implements CustomPacketPayload {
    public static final Type<HeartPlacedNotice> TYPE=new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"heart_placed"));
    public static final StreamCodec<RegistryFriendlyByteBuf,HeartPlacedNotice> CODEC=
            StreamCodec.composite(BlockPos.STREAM_CODEC,HeartPlacedNotice::core,
                    HeartPlacedNotice::new);
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
}

package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * The server deleted the pending construction project. BlockPos.ZERO is
 * reserved for a "no project for this player" response to project discovery.
 */
public record HeartProjectCancelled(BlockPos core) implements CustomPacketPayload {
    public static final Type<HeartProjectCancelled> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"heart_project_cancelled"));
    public static final StreamCodec<RegistryFriendlyByteBuf,HeartProjectCancelled> CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC,HeartProjectCancelled::core,
                    HeartProjectCancelled::new);
    @Override public Type<? extends CustomPacketPayload> type(){ return TYPE; }
}

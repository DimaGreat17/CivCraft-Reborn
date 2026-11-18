package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BlueprintStatusRequest(BlockPos core) implements CustomPacketPayload {
    public static final Type<BlueprintStatusRequest> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID, "blueprint_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintStatusRequest> CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, BlueprintStatusRequest::core,
                    BlueprintStatusRequest::new);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Server-validated capabilities for one physical Camp Core, not client claims. */
public record BlueprintStatusResponse(BlockPos core, int stage) implements CustomPacketPayload {
    // -1 = no authorized camp, 0 = CAMP, 1 = SETTLEMENT.
    public static final Type<BlueprintStatusResponse> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID, "blueprint_status"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintStatusResponse> CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, BlueprintStatusResponse::core,
                    ByteBufCodecs.INT, BlueprintStatusResponse::stage,
                    BlueprintStatusResponse::new);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

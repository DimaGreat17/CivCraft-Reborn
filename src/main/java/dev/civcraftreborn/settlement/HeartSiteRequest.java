package dev.civcraftreborn.settlement;

import dev.civcraftreborn.CivCraftReborn;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Request the sender's own construction project, independent of starter position. */
public record HeartSiteRequest() implements CustomPacketPayload {
    public static final Type<HeartSiteRequest> TYPE=new Type<>(
            Identifier.fromNamespaceAndPath(CivCraftReborn.MOD_ID,"heart_site_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf,HeartSiteRequest> CODEC=
            StreamCodec.unit(new HeartSiteRequest());
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
}

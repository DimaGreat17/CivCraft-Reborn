package dev.civcraftreborn.client;

import net.fabricmc.api.ClientModInitializer;

/**
 * Client entry point. World-tab controls live inside the vanilla tab's own
 * GridLayout (see CreateWorldWorldTabMixin), not as free-floating overlays.
 */
public final class CivCraftRebornClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Registered declaratively by the client-only mixin.
    }
}

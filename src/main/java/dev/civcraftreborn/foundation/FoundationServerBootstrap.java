package dev.civcraftreborn.foundation;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import dev.civcraftreborn.CivCraftReborn;

/**
 * Applies settings on the authoritative server after the world is available.
 * The mode is persisted, but victory completion is deliberately not implemented in P0.
 */
public final class FoundationServerBootstrap {
    private FoundationServerBootstrap() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(FoundationServerBootstrap::applySettings);
    }

    private static void applySettings(MinecraftServer server) {
        WorldSettings settings = WorldSettingsSavedData.get(server).settings();
        server.overworld().getWorldBorder().setSize(settings.preset().sideBlocks());

        CivCraftReborn.LOGGER.info(
                "Foundation world settings: Overworld {}x{} blocks, mode {}",
                settings.preset().sideBlocks(),
                settings.preset().sideBlocks(),
                settings.mode());

        if (settings.mode() == SessionMode.FINITE_VICTORY) {
            CivCraftReborn.LOGGER.warn(
                    "FINITE_VICTORY is saved as world intent only. "
                    + "No CivCraft victory evaluator exists in patch 0.1.");
        }
    }
}

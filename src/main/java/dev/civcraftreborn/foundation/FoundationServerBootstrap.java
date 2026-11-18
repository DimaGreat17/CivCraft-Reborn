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
        // A side length is not a radius. Pin center so all presets span [-side/2, +side/2].
        server.overworld().getWorldBorder().setCenter(0.0, 0.0);
        server.overworld().getWorldBorder().setSize(settings.preset().sideBlocks());

        CivCraftReborn.LOGGER.info(
                "Foundation world settings: Overworld {}x{} blocks, mode {} (border center 0,0)",
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

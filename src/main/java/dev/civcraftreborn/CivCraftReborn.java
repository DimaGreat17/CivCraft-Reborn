package dev.civcraftreborn;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compatibility-only entrypoint. No gameplay systems are registered here yet.
 */
public final class CivCraftReborn implements ModInitializer {
    public static final String MOD_ID = "civcraft_reborn";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("CivCraft Reborn 0.1 compatibility entrypoint loaded");
    }
}

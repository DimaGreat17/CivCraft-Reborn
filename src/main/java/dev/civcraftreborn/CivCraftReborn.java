package dev.civcraftreborn;

import net.fabricmc.api.ModInitializer;
import dev.civcraftreborn.foundation.FoundationServerBootstrap;
import dev.civcraftreborn.foundation.FoundationCommands;
import dev.civcraftreborn.settlement.CampBlocks;
import dev.civcraftreborn.settlement.BlueprintNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compatibility-only entrypoint. No gameplay systems are registered here yet.
 */
public final class CivCraftReborn implements ModInitializer {
    public static final String MOD_ID = "civcraft_reborn";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        FoundationServerBootstrap.register();
        FoundationCommands.register();
        CampBlocks.initialize();
        BlueprintNetworking.register();
        LOGGER.info("CivCraft Reborn 0.1 Foundation entrypoint loaded");
    }
}

package dev.civcraftreborn.client;

import dev.civcraftreborn.CivCraftReborn;
import dev.civcraftreborn.foundation.FoundationDefaults;
import dev.civcraftreborn.foundation.SessionMode;
import dev.civcraftreborn.foundation.WorldPreset;
import dev.civcraftreborn.foundation.WorldSettings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;

/**
 * Adds P0-specific options to Minecraft's native world-creation screen without
 * replacing its normal seed, gamemode and data-pack functionality.
 *
 * The selected values are written before the integrated server starts. On first
 * server access the world-scoped SavedData captures them permanently; subsequent
 * changes to defaults cannot mutate an existing world's settings.
 */
public final class CivCraftRebornClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (!(screen instanceof CreateWorldScreen)) {
                return;
            }

            final WorldSettings[] selected = {FoundationDefaults.load()};
            final int buttonWidth = Math.min(190, Math.max(110, width / 3));
            final int x = width - buttonWidth - 8;

            Button size = Button.builder(
                    Component.literal(sizeText(selected[0])),
                    button -> {
                        selected[0] = new WorldSettings(WorldSettings.CURRENT_SCHEMA,
                                nextPreset(selected[0].preset()), selected[0].mode());
                        saveChoice(button, selected[0], true);
                    }).bounds(x, 35, buttonWidth, 20).build();

            Button mode = Button.builder(
                    Component.literal(modeText(selected[0])),
                    button -> {
                        selected[0] = new WorldSettings(WorldSettings.CURRENT_SCHEMA,
                                selected[0].preset(), nextMode(selected[0].mode()));
                        saveChoice(button, selected[0], false);
                    }).bounds(x, 59, buttonWidth, 20).build();

            Screens.getWidgets(screen).add(size);
            Screens.getWidgets(screen).add(mode);

            // The explicit warning appears on the selector itself, not only in logs.
            if (selected[0].mode() == SessionMode.FINITE_VICTORY) {
                CivCraftReborn.LOGGER.info(
                        "Finite mode selected for a new world: victory conditions are not available in P0");
            }
        });
    }

    private static String sizeText(WorldSettings settings) {
        return "CivCraft: " + settings.preset().sideBlocks() + " x "
                + settings.preset().sideBlocks();
    }

    private static String modeText(WorldSettings settings) {
        return settings.mode() == SessionMode.FINITE_VICTORY
                ? "FINITE (victory unavailable)" : "ENDLESS sandbox";
    }

    private static WorldPreset nextPreset(WorldPreset preset) {
        WorldPreset[] options = WorldPreset.values();
        return options[(preset.ordinal() + 1) % options.length];
    }

    private static SessionMode nextMode(SessionMode mode) {
        return mode == SessionMode.ENDLESS_SANDBOX
                ? SessionMode.FINITE_VICTORY : SessionMode.ENDLESS_SANDBOX;
    }

    private static void saveChoice(Button button, WorldSettings settings, boolean isSize) {
        FoundationDefaults.saveWorldCreationChoice(settings);
        button.setMessage(Component.literal(isSize ? sizeText(settings) : modeText(settings)));
    }
}

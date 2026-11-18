package dev.civcraftreborn.foundation;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Temporary pre-world server configuration source.
 * A first-run world-creation GUI is required before the P0 release can be signed off.
 * Once a world exists, its SavedData owns the settings; changes to this file do not rewrite it.
 */
public final class FoundationDefaults {
    private static final String CONFIG_NAME = "civcraft-reborn-world-defaults.properties";

    private FoundationDefaults() {
    }

    public static WorldSettings load() {
        return fromFile(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME));
    }

    static WorldSettings fromFile(Path path) {
        if (!Files.exists(path)) {
            return WorldSettings.defaults();
        }

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read CivCraft Reborn defaults at " + path, e);
        }

        try {
            int size = Integer.parseInt(properties.getProperty("overworld_side_blocks", "10000").trim());
            SessionMode mode = SessionMode.valueOf(
                    properties.getProperty("session_mode", "ENDLESS_SANDBOX").trim());
            return new WorldSettings(WorldSettings.CURRENT_SCHEMA, WorldPreset.fromSide(size), mode);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid CivCraft Reborn world defaults at " + path
                    + ". Expected overworld_side_blocks=10000|15000|25000 and "
                    + "session_mode=FINITE_VICTORY|ENDLESS_SANDBOX.", e);
        }
    }
}

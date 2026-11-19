package dev.civcraftreborn.foundation;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Bootstraps settings for a NEW world. The world-scoped SavedData takes over on first access.
 * Existing worlds never reread these defaults. The file remains useful for dedicated servers.
 */
public final class FoundationDefaults {
    private static final String CONFIG_NAME = "civcraft-reborn-world-defaults.properties";

    private FoundationDefaults() {
    }

    public static WorldSettings load() {
        return fromFile(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME));
    }

    /**
     * Persist the choice from the vanilla Create World screen before world generation begins.
     * The same file format may also be written by a dedicated-server administrator.
     */
    public static void saveWorldCreationChoice(WorldSettings settings) {
        toFile(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME), settings);
    }

    static void toFile(Path path, WorldSettings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("World settings cannot be null");
        }

        Properties properties = new Properties();
        properties.setProperty("overworld_side_blocks", Integer.toString(settings.preset().sideBlocks()));
        properties.setProperty("session_mode", settings.mode().name());
        properties.setProperty("civcraft_world", Boolean.toString(settings.civcraftWorld()));

        try {
            Files.createDirectories(path.toAbsolutePath().getParent());
            Path temporary = Files.createTempFile(path.toAbsolutePath().getParent(), ".civcraft-world-", ".tmp");
            try {
                try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                    properties.store(writer, "CivCraft Reborn defaults for newly created worlds");
                }
                try {
                    Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                    Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write CivCraft Reborn world defaults at " + path, e);
        }
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
            String enabledValue = properties.getProperty("civcraft_world", "false").trim();
            if (!enabledValue.equalsIgnoreCase("true") && !enabledValue.equalsIgnoreCase("false")) {
                throw new IllegalArgumentException("civcraft_world must be true or false");
            }
            return new WorldSettings(WorldSettings.CURRENT_SCHEMA, WorldPreset.fromSide(size),
                    mode, Boolean.parseBoolean(enabledValue));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid CivCraft Reborn world defaults at " + path
                    + ". Expected overworld_side_blocks=10000|15000|25000 and "
                    + "session_mode=FINITE_VICTORY|ENDLESS_SANDBOX.", e);
        }
    }
}

package dev.civcraftreborn.foundation;

import static org.junit.jupiter.api.Assertions.*;

import com.mojang.serialization.JsonOps;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class WorldSettingsTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void supportsExactlyThreeSideLengths() {
        assertEquals(WorldPreset.SMALL, WorldPreset.fromSide(10_000));
        assertEquals(WorldPreset.MEDIUM, WorldPreset.fromSide(15_000));
        assertEquals(WorldPreset.LARGE, WorldPreset.fromSide(25_000));
        assertThrows(IllegalArgumentException.class, () -> WorldPreset.fromSide(12_000));
    }

    @Test
    void allSixWorldOptionsAreAcceptedFromConfiguration() throws IOException {
        Path configuration = temporaryDirectory.resolve("world.properties");

        for (WorldPreset preset : WorldPreset.values()) {
            for (SessionMode mode : SessionMode.values()) {
                Files.writeString(configuration,
                        "overworld_side_blocks=" + preset.sideBlocks() + "\n"
                        + "session_mode=" + mode.name() + "\n",
                        StandardCharsets.UTF_8);

                WorldSettings settings = FoundationDefaults.fromFile(configuration);

                assertEquals(WorldSettings.CURRENT_SCHEMA, settings.schemaVersion());
                assertEquals(preset, settings.preset());
                assertEquals(mode, settings.mode());
                assertFalse(settings.isVictoryEvaluatorAvailable(),
                        "P0 must not advertise victory for " + mode);
            }
        }
    }

    @Test
    void missingConfigurationUsesDocumentedDefaults() {
        WorldSettings settings = FoundationDefaults.fromFile(
                temporaryDirectory.resolve("does-not-exist.properties"));
        assertEquals(WorldPreset.SMALL, settings.preset());
        assertEquals(SessionMode.ENDLESS_SANDBOX, settings.mode());
    }

    @Test
    void invalidSizeAndModeFailInsteadOfSilentlyResetting() throws IOException {
        Path configuration = temporaryDirectory.resolve("invalid.properties");

        Files.writeString(configuration,
                "overworld_side_blocks=12000\nsession_mode=ENDLESS_SANDBOX\n",
                StandardCharsets.UTF_8);
        assertThrows(IllegalStateException.class,
                () -> FoundationDefaults.fromFile(configuration));

        Files.writeString(configuration,
                "overworld_side_blocks=10000\nsession_mode=UNRECOGNIZED\n",
                StandardCharsets.UTF_8);
        assertThrows(IllegalStateException.class,
                () -> FoundationDefaults.fromFile(configuration));
    }

    @Test
    void defaultsDoNotAdvertiseVictory() {
        WorldSettings settings = WorldSettings.defaults();
        assertEquals(SessionMode.ENDLESS_SANDBOX, settings.mode());
        assertFalse(settings.isVictoryEvaluatorAvailable());
    }

    @Test
    void unsupportedSchemaNeverBecomesAnEmptyWorld() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorldSettings(99, WorldPreset.SMALL, SessionMode.ENDLESS_SANDBOX));
    }

    @Test
    void nullOptionsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new WorldSettings(WorldSettings.CURRENT_SCHEMA, null, SessionMode.ENDLESS_SANDBOX));
        assertThrows(IllegalArgumentException.class,
                () -> new WorldSettings(WorldSettings.CURRENT_SCHEMA, WorldPreset.SMALL, null));
    }
    @Test
    void selectedWorldCreationChoiceRoundTripsToDisk() {
        Path configuration = temporaryDirectory.resolve("civcraft/world-defaults.properties");
        for (WorldPreset preset : WorldPreset.values()) {
            for (SessionMode mode : SessionMode.values()) {
                WorldSettings selected = new WorldSettings(WorldSettings.CURRENT_SCHEMA, preset, mode);
                FoundationDefaults.toFile(configuration, selected);
                assertEquals(selected, FoundationDefaults.fromFile(configuration));
            }
        }
    }

    @Test
    void atomicWriteDoesNotLeaveTemporaryFiles() throws IOException {
        Path folder = temporaryDirectory.resolve("world-config");
        Path config = folder.resolve("world-defaults.properties");
        FoundationDefaults.toFile(config, WorldSettings.defaults());
        try (var entries = Files.list(folder)) {
            assertEquals(java.util.List.of(config), entries.toList());
        }
    }

    @Test
    void invalidSelectionCannotReplaceExistingChoice() throws IOException {
        Path config = temporaryDirectory.resolve("world-defaults.properties");
        WorldSettings selected = new WorldSettings(
                WorldSettings.CURRENT_SCHEMA, WorldPreset.LARGE, SessionMode.FINITE_VICTORY);
        FoundationDefaults.toFile(config, selected);
        String before = Files.readString(config);
        assertThrows(IllegalArgumentException.class, () -> FoundationDefaults.toFile(config, null));
        assertEquals(before, Files.readString(config));
        assertEquals(selected, FoundationDefaults.fromFile(config));
    }


    @Test
    void allSixChoicesRoundTripThroughTheActualSaveCodec() {
        for (WorldPreset preset : WorldPreset.values()) {
            for (SessionMode mode : SessionMode.values()) {
                WorldSettings original = new WorldSettings(WorldSettings.CURRENT_SCHEMA, preset, mode);
                var encoded = WorldSettings.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();
                var decoded = WorldSettings.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();
                assertEquals(original, decoded);
            }
        }
    }

    @Test
    void futureSchemaIsNotSilentlyReplacedByDefaultsDuringDecode() {
        JsonObject encoded = new JsonObject();
        encoded.addProperty("schema_version", 99);
        encoded.addProperty("overworld_side_blocks", 10000);
        encoded.addProperty("session_mode", "ENDLESS_SANDBOX");
        assertThrows(RuntimeException.class, () ->
                WorldSettings.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow());
    }


    @Test
    void newWorldDefaultsRemainVanillaUnlessCivCraftSelected() {
        assertFalse(WorldSettings.defaults().civcraftWorld());
    }

    @Test
    void explicitCivCraftChoiceIsSavedAndReloaded() {
        Path file = temporaryDirectory.resolve("civcraft-selection.properties");
        WorldSettings civcraft = new WorldSettings(WorldSettings.CURRENT_SCHEMA,
                WorldPreset.LARGE, SessionMode.FINITE_VICTORY, true);
        FoundationDefaults.toFile(file, civcraft);
        assertEquals(civcraft, FoundationDefaults.fromFile(file));

        WorldSettings vanilla = new WorldSettings(WorldSettings.CURRENT_SCHEMA,
                WorldPreset.SMALL, SessionMode.ENDLESS_SANDBOX, false);
        FoundationDefaults.toFile(file, vanilla);
        assertEquals(vanilla, FoundationDefaults.fromFile(file));
    }

    @Test
    void preOptInSaveWithoutFieldKeepsItsOriginalCivCraftWorld() {
        var legacy = new com.google.gson.JsonObject();
        legacy.addProperty("schema_version", 1);
        legacy.addProperty("overworld_side_blocks", 15000);
        legacy.addProperty("session_mode", "ENDLESS_SANDBOX");
        var restored = WorldSettings.CODEC.parse(JsonOps.INSTANCE, legacy).getOrThrow();
        assertTrue(restored.civcraftWorld());
        assertEquals(WorldPreset.MEDIUM, restored.preset());
    }


}

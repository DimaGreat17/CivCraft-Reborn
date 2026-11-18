package dev.civcraftreborn.foundation;

import static org.junit.jupiter.api.Assertions.*;

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
}

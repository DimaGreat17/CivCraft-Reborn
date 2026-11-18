package dev.civcraftreborn.foundation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

final class WorldSettingsTest {
    @Test
    void supportsExactlyThreeSideLengths() {
        assertEquals(WorldPreset.SMALL, WorldPreset.fromSide(10_000));
        assertEquals(WorldPreset.MEDIUM, WorldPreset.fromSide(15_000));
        assertEquals(WorldPreset.LARGE, WorldPreset.fromSide(25_000));
        assertThrows(IllegalArgumentException.class, () -> WorldPreset.fromSide(12_000));
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
}

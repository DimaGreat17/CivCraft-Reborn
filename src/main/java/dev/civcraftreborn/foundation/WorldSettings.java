package dev.civcraftreborn.foundation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Immutable settings for one world. Older saved worlds (before the CivCraft
 * opt-in checkbox) are interpreted as CivCraft worlds to preserve their border.
 */
public record WorldSettings(int schemaVersion, WorldPreset preset, SessionMode mode,
                            boolean civcraftWorld) {
    public static final int CURRENT_SCHEMA = 1;
    private static final Codec<WorldPreset> PRESET_CODEC =
            Codec.INT.xmap(WorldPreset::fromSide, WorldPreset::sideBlocks);
    private static final Codec<SessionMode> MODE_CODEC =
            Codec.STRING.xmap(SessionMode::valueOf, SessionMode::name);

    public static final Codec<WorldSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("schema_version").forGetter(WorldSettings::schemaVersion),
                    PRESET_CODEC.fieldOf("overworld_side_blocks").forGetter(WorldSettings::preset),
                    MODE_CODEC.fieldOf("session_mode").forGetter(WorldSettings::mode),
                    // Missing on legacy worlds: they had CivCraft border enabled.
                    Codec.BOOL.optionalFieldOf("civcraft_world", true)
                            .forGetter(WorldSettings::civcraftWorld)
            ).apply(instance, WorldSettings::new)
    );

    /** Compatibility for previous call sites and old P0 unit tests. */
    public WorldSettings(int schemaVersion, WorldPreset preset, SessionMode mode) {
        this(schemaVersion, preset, mode, true);
    }

    public WorldSettings {
        if (schemaVersion != CURRENT_SCHEMA) {
            throw new IllegalArgumentException(
                    "Unsupported CivCraft Reborn world settings version " + schemaVersion
                    + ". Do not overwrite; a versioned migration is required.");
        }
        if (preset == null || mode == null) {
            throw new IllegalArgumentException("World preset and session mode are required");
        }
    }

    /** New vanilla worlds are not CivCraft worlds until explicitly selected. */
    public static WorldSettings defaults() {
        return new WorldSettings(CURRENT_SCHEMA, WorldPreset.SMALL,
                SessionMode.ENDLESS_SANDBOX, false);
    }

    public boolean isVictoryEvaluatorAvailable() {
        return false;
    }
}

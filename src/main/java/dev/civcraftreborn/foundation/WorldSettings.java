package dev.civcraftreborn.foundation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Versioned, per-world immutable foundation settings. */
public record WorldSettings(int schemaVersion, WorldPreset preset, SessionMode mode) {
    public static final int CURRENT_SCHEMA = 1;
    private static final Codec<WorldPreset> PRESET_CODEC =
            Codec.INT.xmap(WorldPreset::fromSide, WorldPreset::sideBlocks);
    private static final Codec<SessionMode> MODE_CODEC =
            Codec.STRING.xmap(SessionMode::valueOf, SessionMode::name);

    public static final Codec<WorldSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("schema_version").forGetter(WorldSettings::schemaVersion),
                    PRESET_CODEC.fieldOf("overworld_side_blocks").forGetter(WorldSettings::preset),
                    MODE_CODEC.fieldOf("session_mode").forGetter(WorldSettings::mode)
            ).apply(instance, WorldSettings::new)
    );

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

    public static WorldSettings defaults() {
        return new WorldSettings(CURRENT_SCHEMA, WorldPreset.SMALL, SessionMode.ENDLESS_SANDBOX);
    }

    public boolean isVictoryEvaluatorAvailable() {
        // FINITE_VICTORY is persisted as an intention only in the P0 Foundation patch.
        return false;
    }
}

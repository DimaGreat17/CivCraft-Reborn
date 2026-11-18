package dev.civcraftreborn.foundation;

/** Length of each side of the playable Overworld, measured in blocks. */
public enum WorldPreset {
    SMALL(10_000),
    MEDIUM(15_000),
    LARGE(25_000);

    private final int sideBlocks;

    WorldPreset(int sideBlocks) {
        this.sideBlocks = sideBlocks;
    }

    public int sideBlocks() {
        return sideBlocks;
    }

    public static WorldPreset fromSide(int sideBlocks) {
        for (WorldPreset preset : values()) {
            if (preset.sideBlocks == sideBlocks) {
                return preset;
            }
        }
        throw new IllegalArgumentException("Unsupported Overworld side length: " + sideBlocks);
    }
}

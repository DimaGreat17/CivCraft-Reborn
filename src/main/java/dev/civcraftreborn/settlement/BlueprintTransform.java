package dev.civcraftreborn.settlement;

/** Pure integer building footprint rotation; independent of Minecraft client. */
public final class BlueprintTransform {
    private BlueprintTransform() {}

    public record Point(int x, int z) {}

    public static Point rotate(int x, int z, int width, int depth, int turns) {
        if (width < 1 || depth < 1 || x < 0 || z < 0 || x >= width || z >= depth) {
            throw new IllegalArgumentException("Invalid blueprint footprint point");
        }
        return switch (Math.floorMod(turns, 4)) {
            case 1 -> new Point(depth - 1 - z, x);
            case 2 -> new Point(width - 1 - x, depth - 1 - z);
            case 3 -> new Point(z, width - 1 - x);
            default -> new Point(x,z);
        };
    }

    public static boolean inCampRadius(int relativeX, int relativeZ, int radius) {
        return Math.abs(relativeX) <= radius && Math.abs(relativeZ) <= radius;
    }
}

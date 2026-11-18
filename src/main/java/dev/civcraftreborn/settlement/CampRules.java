package dev.civcraftreborn.settlement;

import java.util.Collection;
import java.util.UUID;

/**
 * Deterministic server-side preconditions shared by player and future NPC founders.
 * NPC founders must supply an inventory reserve through the same construction path.
 */
public final class CampRules {
    public static final int MIN_DISTANCE = 96;
    public static final int CAMP_RADIUS = 24;
    public static final int REQUIRED_LOGS = 16;
    public static final int REQUIRED_STONE = 16;
    public static final int REQUIRED_FOOD = 8;
    private CampRules() {}

    public static boolean canFoundAt(Collection<CampRecord> existing, String dimension,
            int x, int z, boolean insideWorldBorder, boolean physicallySupported) {
        if (!insideWorldBorder || !physicallySupported) return false;
        for (CampRecord record : existing) {
            if (!record.dimension().equals(dimension)) continue;
            long dx = (long) x - record.x();
            long dz = (long) z - record.z();
            if (dx * dx + dz * dz < (long) MIN_DISTANCE * MIN_DISTANCE) return false;
        }
        return true;
    }

    public static CampRecord found(UUID founder, String dimension, int x, int y, int z,
            String name, long gameTime) {
        return new CampRecord(UUID.randomUUID().toString(), founder.toString(),
                name, dimension, x, y, z, SettlementStage.CAMP, gameTime);
    }

    /** No promotion bonus: the same persistent ID and stored resources remain intact. */
    public static CampRecord promote(CampRecord existing, int validHomes, int residents,
            int foodItems, boolean storageExists) {
        if (existing.stage() != SettlementStage.CAMP || validHomes < 3 || residents < 3
                || foodItems < 16 || !storageExists) {
            throw new IllegalStateException("Requires 3 rooms, 3 living citizens, 16 real food items and storage");
        }
        return existing.promoted();
    }
}

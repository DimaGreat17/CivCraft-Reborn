package dev.civcraftreborn.settlement;

import java.util.Set;

/**
 * Authoritative stage requirements for the optional building catalog.
 * No plan unlock ever creates blocks, citizens, resources or settlement rights.
 */
public final class BlueprintAccess {
    private BlueprintAccess() {}

    public static final Set<String> BASIC = Set.of(
            "HEART", "STORAGE", "WORKSHOP", "FARM", "BEDROOM", "SMALL_HOUSE");
    public static final Set<String> SETTLEMENT = Set.of(
            "BARRACKS", "GRANARY", "SMITHY", "WATCHTOWER", "WELL");

    public static boolean allowed(String planId, SettlementStage stage) {
        if (planId == null || stage == null) return false;
        return BASIC.contains(planId) || (stage == SettlementStage.SETTLEMENT
                && SETTLEMENT.contains(planId));
    }

    public static boolean allowedBeforeFounding(String planId) {
        return "HEART".equals(planId);
    }
}

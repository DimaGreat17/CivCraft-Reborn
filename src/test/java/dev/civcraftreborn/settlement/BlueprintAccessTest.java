package dev.civcraftreborn.settlement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class BlueprintAccessTest {
    @Test
    void beforeFoundingOnlyPhysicalHeartMayBeProjected() {
        assertTrue(BlueprintAccess.allowedBeforeFounding("HEART"));
        assertFalse(BlueprintAccess.allowedBeforeFounding("STORAGE"));
        assertFalse(BlueprintAccess.allowedBeforeFounding("WATCHTOWER"));
    }

    @Test
    void newlyFoundedCampHasOnlyBasicStructures() {
        for (String id : BlueprintAccess.BASIC) {
            assertTrue(BlueprintAccess.allowed(id, SettlementStage.CAMP), id);
        }
        for (String id : BlueprintAccess.SETTLEMENT) {
            assertFalse(BlueprintAccess.allowed(id, SettlementStage.CAMP), id);
        }
    }

    @Test
    void promotionUnlocksDevelopedSettlementBuildings() {
        for (String id : BlueprintAccess.SETTLEMENT) {
            assertTrue(BlueprintAccess.allowed(id, SettlementStage.SETTLEMENT), id);
        }
        for (String id : BlueprintAccess.BASIC) {
            assertTrue(BlueprintAccess.allowed(id, SettlementStage.SETTLEMENT), id);
        }
    }

    @Test
    void unknownOrMissingPlansAreNeverAllowed() {
        assertFalse(BlueprintAccess.allowed("GOVERNMENT", SettlementStage.SETTLEMENT));
        assertFalse(BlueprintAccess.allowed(null, SettlementStage.CAMP));
        assertFalse(BlueprintAccess.allowed("FARM", null));
    }
}

package dev.civcraftreborn.settlement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

final class CampRulesTest {
    @Test
    void foundationDemandsValidSiteAndRealSeparation() {
        CampRecord camp = CampRules.found(UUID.randomUUID(), "minecraft:overworld",
                10, 64, 10, "Original", 100);
        assertTrue(CampRules.canFoundAt(List.of(camp), "minecraft:overworld",
                110, 10, true, true));
        assertFalse(CampRules.canFoundAt(List.of(camp), "minecraft:overworld",
                30, 10, true, true));
        assertFalse(CampRules.canFoundAt(List.of(), "minecraft:overworld",
                0, 0, false, true));
        assertFalse(CampRules.canFoundAt(List.of(), "minecraft:overworld",
                0, 0, true, false));
        assertTrue(CampRules.canFoundAt(List.of(camp), "minecraft:the_nether",
                30, 10, true, true));
    }

    @Test
    void promotionKeepsSettlementIdAndDoesNotAddAnyResources() {
        CampRecord camp = CampRules.found(UUID.randomUUID(), "minecraft:overworld",
                0, 64, 0, "Camp", 0);
        assertThrows(IllegalStateException.class, () -> CampRules.promote(camp, 2, 3, 16, true));
        assertThrows(IllegalStateException.class, () -> CampRules.promote(camp, 3, 3, 15, true));
        assertThrows(IllegalStateException.class, () -> CampRules.promote(camp, 3, 3, 16, false));
        CampRecord promoted = CampRules.promote(camp, 3, 3, 16, true);
        assertEquals(SettlementStage.SETTLEMENT, promoted.stage());
        assertEquals(camp.id(), promoted.id());
        assertEquals(camp.owner(), promoted.owner());
        assertEquals(camp.createdGameTime(), promoted.createdGameTime());
        assertThrows(IllegalStateException.class, () -> CampRules.promote(promoted, 3, 3, 16, true));
    }

    @Test
    void registryRefusesDuplicatePersistentIdentities() {
        CampRecord first = CampRules.found(UUID.randomUUID(), "minecraft:overworld",
                0, 64, 0, "A", 1);
        assertThrows(IllegalArgumentException.class, () ->
                new CampRegistryState(CampRegistryState.CURRENT_SCHEMA, List.of(first, first)));
        assertThrows(IllegalArgumentException.class, () ->
                new CampRegistryState(999, List.of(first)));
    }

    @Test
    void campRecordValidatesOwnerAndName() {
        UUID owner = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () ->
                new CampRecord(UUID.randomUUID().toString(), owner.toString(), "",
                        "minecraft:overworld", 0, 64, 0, SettlementStage.CAMP, 1));
        assertThrows(IllegalArgumentException.class, () ->
                new CampRecord(UUID.randomUUID().toString(), "broken-owner", "Test",
                        "minecraft:overworld", 0, 64, 0, SettlementStage.CAMP, 1));
    }
}

package dev.civcraftreborn.settlement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class CampWorkPersistenceTest {
    private static String uuid() { return UUID.randomUUID().toString(); }

    @Test
    void jobsAreOnePerRealCitizenAndNeedFacilityExceptBuilders() {
        String citizen=uuid(), building=uuid();
        ResidentAssignment assigned=new ResidentAssignment(citizen,building,ResidentWorkRole.FARMER);
        assertEquals(citizen,assigned.citizenId());
        assertEquals(building,assigned.buildingId());
        assertThrows(IllegalArgumentException.class,
                ()->new ResidentAssignment(citizen,"",ResidentWorkRole.FARMER));
        assertDoesNotThrow(()->new ResidentAssignment(citizen,"",ResidentWorkRole.BUILDER));
        assertThrows(IllegalArgumentException.class,
                ()->new ResidentWorkState(1,List.of(assigned,assigned)));
        assertThrows(IllegalArgumentException.class,
                ()->new ResidentWorkState(9,List.of()));
    }

    @Test
    void ordersHavePersistentCampCitizenSiteAndIdentity() {
        ConstructionOrder order=new ConstructionOrder(uuid(),uuid(),uuid(),
                "minecraft:overworld",FunctionalBuildingTemplates.Type.STORAGE,
                3,64,-5,1,120);
        assertEquals(120,order.createdTime());
        assertEquals(1,order.rotation());
        assertEquals(FunctionalBuildingTemplates.Type.STORAGE,order.type());
        assertThrows(IllegalArgumentException.class,
                ()->new ConstructionRegistryState(1,List.of(order,order)));
        assertThrows(IllegalArgumentException.class,
                ()->new ConstructionRegistryState(5,List.of(order)));
    }

    @Test
    void roleMatchesOnlyItsFunctionalBuilding() {
        assertTrue(ResidentWorkRole.FARMER.compatible(FunctionalBuildingTemplates.Type.FARM));
        assertFalse(ResidentWorkRole.FARMER.compatible(FunctionalBuildingTemplates.Type.SMITHY));
        assertTrue(ResidentWorkRole.CRAFTSMAN.compatible(FunctionalBuildingTemplates.Type.WORKSHOP));
        assertTrue(ResidentWorkRole.STOREKEEPER.compatible(FunctionalBuildingTemplates.Type.GRANARY));
        assertFalse(ResidentWorkRole.STOREKEEPER.compatible(FunctionalBuildingTemplates.Type.FARM));
    }
}

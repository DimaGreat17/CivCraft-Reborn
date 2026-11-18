package dev.civcraftreborn.settlement;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class FunctionalBuildingsTest {
    @Test
    void everyBuildingHasPhysicalFootprintAndMultipleStructuralCells() {
        for(var type:FunctionalBuildingTemplates.Type.values()) {
            var cells=FunctionalBuildingTemplates.cells(type);
            assertTrue(cells.size()>=35,type+" must be a complete building, not 2 utility blocks");
            assertTrue(cells.stream().anyMatch(c->c.y()==-1),type+" needs a real floor");
            Set<String> coords=new HashSet<>();
            for(var c:cells)
                assertTrue(coords.add(c.x()+":"+c.y()+":"+c.z()),type+" duplicate cell");
        }
    }

    @Test
    void supportBuildingsContainActualFunctionalFixtures() {
        assertTrue(FunctionalBuildingTemplates.cells(FunctionalBuildingTemplates.Type.FARM)
                .stream().anyMatch(c->c.block()==net.minecraft.world.level.block.Blocks.WATER));
        assertTrue(FunctionalBuildingTemplates.cells(FunctionalBuildingTemplates.Type.STORAGE)
                .stream().anyMatch(c->c.block()==net.minecraft.world.level.block.Blocks.CHEST));
        assertTrue(FunctionalBuildingTemplates.cells(FunctionalBuildingTemplates.Type.WORKSHOP)
                .stream().anyMatch(c->c.block()==net.minecraft.world.level.block.Blocks.FURNACE));
        assertTrue(FunctionalBuildingTemplates.cells(FunctionalBuildingTemplates.Type.BARRACKS)
                .stream().anyMatch(FunctionalBuildingTemplates.Cell::anyBed));
        assertTrue(FunctionalBuildingTemplates.cells(FunctionalBuildingTemplates.Type.WATCHTOWER)
                .stream().anyMatch(c->c.block()==net.minecraft.world.level.block.Blocks.BELL));
    }

    @Test
    void roleRequiresCorrespondingRealWorkplace() {
        assertTrue(ResidentWorkRole.FARMER.compatible(FunctionalBuildingTemplates.Type.FARM));
        assertFalse(ResidentWorkRole.FARMER.compatible(FunctionalBuildingTemplates.Type.WORKSHOP));
        assertTrue(ResidentWorkRole.CRAFTSMAN.compatible(FunctionalBuildingTemplates.Type.SMITHY));
        assertTrue(ResidentWorkRole.STOREKEEPER.compatible(FunctionalBuildingTemplates.Type.GRANARY));
        assertTrue(ResidentWorkRole.WATCHMAN.compatible(FunctionalBuildingTemplates.Type.WATCHTOWER));
        assertTrue(ResidentWorkRole.WATER_CARRIER.compatible(FunctionalBuildingTemplates.Type.WELL));
    }

    @Test
    void oneWorkerOneJobInvariant() {
        String c=UUID.randomUUID().toString();
        String b=UUID.randomUUID().toString();
        assertThrows(IllegalArgumentException.class,()->new ResidentWorkState(1,
                java.util.List.of(new ResidentAssignment(c,b,ResidentWorkRole.FARMER),
                        new ResidentAssignment(c,b,ResidentWorkRole.FARMER))));
    }

    @Test
    void buildOrdersHaveRealCitizenAndStableCampIdentity() {
        CampRecord camp=CampRules.found(UUID.randomUUID(),"minecraft:overworld",
                0,64,0,"Camp",0);
        CitizenRecord citizen=CitizenRecord.recruit(UUID.randomUUID(),camp,2,65,2,10);
        var order=ConstructionOrder.issue(camp,citizen,
                FunctionalBuildingTemplates.Type.STORAGE,5,64,5,1,20);
        assertEquals(camp.id(),order.campId());
        assertEquals(citizen.id(),order.citizenId());
        assertEquals(1,order.rotation());
        assertThrows(IllegalArgumentException.class,()->new ConstructionOrder(order.id(),
                camp.id(),citizen.id(),camp.dimension(),order.type(),
                5,64,5,7,20));
    }
}

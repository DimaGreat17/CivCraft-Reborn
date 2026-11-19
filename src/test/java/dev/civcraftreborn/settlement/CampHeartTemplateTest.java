package dev.civcraftreborn.settlement;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.StairBlock;

/** No test here claims real client rendering acceptance. */
final class CampHeartTemplateTest {
    @BeforeAll
    static void bootstrap(){
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void exactBuildingContains162NonOverlappingPhysicalCells(){
        var cells=CampHeartTemplate.cells();
        assertEquals(162,cells.size());
        var positions=new HashSet<String>();
        for(var c:cells){
            assertTrue(positions.add(c.x()+":"+c.y()+":"+c.z()));
            assertTrue(c.x()>=0&&c.x()<7&&c.y()>=0&&c.y()<6
                    &&c.z()>=0&&c.z()<7);
        }
        assertEquals(1,cells.stream().filter(CampHeartTemplate.Cell::coreMarker).count());
    }

    @Test
    void quarterTurnsRotateBuildingIndependentlyOfStarter(){
        BlockPos anchor=new BlockPos(50,70,50);
        var coreCell=CampHeartTemplate.cells().stream()
                .filter(CampHeartTemplate.Cell::coreMarker).findFirst().orElseThrow();
        var stair=CampHeartTemplate.cells().stream()
                .filter(c->c.state().is(Blocks.SPRUCE_STAIRS)).findFirst().orElseThrow();
        for(int turn=0;turn<4;turn++){
            assertEquals(anchor.offset(3,0,3),CampHeartTemplate.position(anchor,3,3,turn,coreCell));
            assertNotNull(CampHeartTemplate.expected(anchor,0,0,turn,stair)
                    .getValue(StairBlock.FACING));
        }
        assertEquals(Direction.NORTH,CampHeartTemplate.expected(anchor,0,0,1,stair)
                .getValue(StairBlock.FACING));
    }

    @Test
    void remoteFootprintDoesNotIncludeOrRequirePhysicalStarter(){
        BlockPos anchor=new BlockPos(0,64,0);
        var coreCell=CampHeartTemplate.cells().stream()
                .filter(CampHeartTemplate.Cell::coreMarker).findFirst().orElseThrow();
        assertEquals(Blocks.OAK_PLANKS,
                CampHeartTemplate.expected(anchor,64,0,0,coreCell).getBlock());
        for(var cell:CampHeartTemplate.cells()) {
            assertNotEquals(anchor,CampHeartTemplate.position(anchor,64,0,0,cell));
            assertFalse(CampHeartTemplate.expected(anchor,64,0,0,cell).isAir());
        }
    }

    @Test
    void doorOccupiesTwoPhysicalCellsWithCorrectHalves(){
        var door=CampHeartTemplate.cells().stream()
                .filter(c->c.state().is(Blocks.OAK_DOOR)).toList();
        assertEquals(2,door.size());
        assertNotEquals(door.get(0).state().getValue(DoorBlock.HALF),
                door.get(1).state().getValue(DoorBlock.HALF));
    }

    @Test
    void removingStarterCancelsOnlyPendingLoadedProjects() {
        assertTrue(CampHeartProjectLifecycle.shouldCancel(false,true,false));
        assertFalse(CampHeartProjectLifecycle.shouldCancel(false,true,true));
        assertFalse(CampHeartProjectLifecycle.shouldCancel(false,false,false));
        assertFalse(CampHeartProjectLifecycle.shouldCancel(true,true,false));
    }

    @Test
    void pendingSiteValidatesTransformsAndPreservesIdOnConfirm(){
        var site=CampHeartSite.placed(UUID.randomUUID(),"minecraft:overworld",
                new BlockPos(0,64,0));
        assertFalse(site.confirmed());
        var moved=site.withPlacement(96,-64,3).confirm();
        assertTrue(moved.confirmed());
        assertEquals(site.id(),moved.id());
        assertEquals(site.core(),moved.core());
        assertThrows(IllegalStateException.class,()->moved.withPlacement(0,0,0));
        assertThrows(IllegalArgumentException.class,
                ()->site.withPlacement(129,0,0));
    }
}

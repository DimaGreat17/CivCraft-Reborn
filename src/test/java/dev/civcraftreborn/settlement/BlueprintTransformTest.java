package dev.civcraftreborn.settlement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class BlueprintTransformTest {
    @Test
    void rectangularFootprintRotatesClockwiseAroundTopLeft() {
        assertEquals(new BlueprintTransform.Point(0,0),
                BlueprintTransform.rotate(0,0,5,3,0));
        assertEquals(new BlueprintTransform.Point(2,0),
                BlueprintTransform.rotate(0,0,5,3,1));
        assertEquals(new BlueprintTransform.Point(4,2),
                BlueprintTransform.rotate(0,0,5,3,2));
        assertEquals(new BlueprintTransform.Point(0,4),
                BlueprintTransform.rotate(0,0,5,3,3));
    }

    @Test
    void fourQuarterTurnsRestoreEveryCellExactly() {
        for (int w=1; w<=7; w++) {
            for (int h=1; h<=7; h++) {
                for (int x=0; x<w; x++) {
                    for (int z=0; z<h; z++) {
                        assertEquals(new BlueprintTransform.Point(x,z),
                                BlueprintTransform.rotate(x,z,w,h,4));
                    }
                }
            }
        }
    }

    @Test
    void invalidFootprintsCannotBeProjected() {
        assertThrows(IllegalArgumentException.class,
                () -> BlueprintTransform.rotate(3,0,3,3,1));
        assertThrows(IllegalArgumentException.class,
                () -> BlueprintTransform.rotate(0,-1,3,3,1));
    }

    @Test
    void buildingOffsetCannotEscapeCampRadius() {
        assertTrue(BlueprintTransform.inCampRadius(24,-24,24));
        assertFalse(BlueprintTransform.inCampRadius(25,0,24));
        assertFalse(BlueprintTransform.inCampRadius(0,-25,24));
    }
}

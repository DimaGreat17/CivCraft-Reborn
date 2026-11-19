package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The original 7x7x6 Camp Heart schematic, shared by authoritative server
 * validation and client projection. The foundation starts a construction project; it is NOT part of the finished building.
 * The project stores a separately positioned and rotated structure footprint.
 * All position and facing transforms are deterministic.
 */
public final class CampHeartTemplate {
    public static final int SIZE = 7;
    public static final int HEIGHT = 6;
    private CampHeartTemplate() {}

    public static final String LEGACY_BLUEPRINT_ID="camp_heart_v1";
    public record Cell(int x,int y,int z,BlockState state,boolean coreMarker) {}
    public record Check(int matched,int total,boolean loaded) {
        public boolean complete() { return loaded && matched==total; }
    }
    /** A world cell that still needs a physical change; never force-load chunks. */
    public record Missing(BlockPos pos,BlockState expected,BlockState actual) {}

    // v1 stays identical for existing projects. The source of each block and
    // every property is now the same JSON format produced from user NBT.
    private static final CampBlueprintCatalog.Definition DEFINITION=
            CampBlueprintCatalog.loadBundled(LEGACY_BLUEPRINT_ID);
    private static final List<Cell> CELLS=DEFINITION.cells().stream()
            .map(c -> new Cell(c.x(),c.y(),c.z(),c.state(),
                    c.x()==3 && c.y()==0 && c.z()==3))
            .toList();

    public static List<Cell> cells() { return CELLS; }

    /** NW corner of footprint after placement; the physical core never moves. */
    public static BlockPos origin(BlockPos core,int shiftX,int shiftZ) {
        return origin(core,shiftX,0,shiftZ);
    }

    public static BlockPos origin(BlockPos core,int shiftX,int shiftY,int shiftZ) {
        return core.offset(-3+shiftX,shiftY,-3+shiftZ);
    }

    public static BlockPos position(BlockPos core,int shiftX,int shiftZ,
            int turns,Cell cell) {
        return position(core,shiftX,0,shiftZ,turns,cell);
    }

    public static BlockPos position(BlockPos core,int shiftX,int shiftY,int shiftZ,
            int turns,Cell cell) {
        var rotated=BlueprintTransform.rotate(cell.x(),cell.z(),SIZE,SIZE,turns);
        return origin(core,shiftX,shiftY,shiftZ).offset(rotated.x(),cell.y(),rotated.z());
    }

    public static BlockState expected(BlockPos core,int shiftX,int shiftZ,
            int turns,Cell cell) {
        return expected(core,shiftX,0,shiftZ,turns,cell);
    }

    public static BlockState expected(BlockPos core,int shiftX,int shiftY,int shiftZ,
            int turns,Cell cell) {
        BlockState state=cell.state();
        // No requirement for the foundation block to occupy the building footprint.
        if(state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            state=state.setValue(HorizontalDirectionalBlock.FACING,
                    rotate(state.getValue(HorizontalDirectionalBlock.FACING),turns));
        }
        return state;
    }

    private static Direction rotate(Direction dir,int turns) {
        for(int i=0;i<Math.floorMod(turns,4);i++)dir=dir.getClockWise();
        return dir;
    }

    public static boolean matching(BlockState actual,BlockState required) {
        if(!actual.is(required.getBlock()))return false;
        // Glass panes receive derived neighbour connection properties; allow
        // those to vary, but validate directional blocks and doors exactly.
        if(required.hasProperty(HorizontalDirectionalBlock.FACING)
                &&!actual.getValue(HorizontalDirectionalBlock.FACING)
                    .equals(required.getValue(HorizontalDirectionalBlock.FACING)))return false;
        if(required.hasProperty(DoorBlock.HALF)
                &&actual.getValue(DoorBlock.HALF)!=required.getValue(DoorBlock.HALF))return false;
        if(required.hasProperty(SlabBlock.TYPE)
                &&actual.getValue(SlabBlock.TYPE)!=required.getValue(SlabBlock.TYPE))return false;
        return true;
    }

    public static Check inspect(ServerLevel level,BlockPos core,int shiftX,int shiftZ,int turns) {
        return inspect(level,core,shiftX,0,shiftZ,turns);
    }

    public static Check inspect(ServerLevel level,BlockPos core,int shiftX,int shiftY,
            int shiftZ,int turns) {
        int matched=0;
        for(Cell cell:CELLS) {
            BlockPos pos=position(core,shiftX,shiftY,shiftZ,turns,cell);
            if(!level.hasChunkAt(pos))return new Check(matched,CELLS.size(),false);
            if(matching(level.getBlockState(pos),expected(core,shiftX,shiftY,shiftZ,turns,cell)))matched++;
        }
        return new Check(matched,CELLS.size(),true);
    }

    /**
     * Return a bounded sample of problems, including precise world coordinates
     * and BlockStates (for stairs, door facing and upper/lower slab). Inspect
     * is read-only and deliberately scans only already loaded chunks.
     */
    public static List<Missing> firstMissing(ServerLevel level,BlockPos core,
            int shiftX,int shiftY,int shiftZ,int turns,int limit) {
        if(limit<=0)return List.of();
        List<Missing> problems=new ArrayList<>();
        for(Cell cell:CELLS) {
            BlockPos pos=position(core,shiftX,shiftY,shiftZ,turns,cell);
            if(!level.hasChunkAt(pos))continue;
            BlockState actual=level.getBlockState(pos);
            BlockState required=expected(core,shiftX,shiftY,shiftZ,turns,cell);
            if(!matching(actual,required)) {
                problems.add(new Missing(pos,required,actual));
                if(problems.size()>=limit)break;
            }
        }
        return List.copyOf(problems);
    }

    public static boolean transformValid(int dx,int dz,int turns) {
        return transformValid(dx,0,dz,turns);
    }

    public static boolean transformValid(int dx,int dy,int dz,int turns) {
        return dx>=-128&&dx<=128&&dy>=-64&&dy<=64
                &&dz>=-128&&dz<=128&&turns>=0&&turns<4;
    }
}

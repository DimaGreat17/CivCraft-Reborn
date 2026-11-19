package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private static final Map<String,CampBlueprintCatalog.Definition> DEFINITIONS=
            new ConcurrentHashMap<>();
    private static final Map<String,List<Cell>> TEMPLATES=new ConcurrentHashMap<>();
    public static CampBlueprintCatalog.Definition definition(String id) {
        return DEFINITIONS.computeIfAbsent(id,CampBlueprintCatalog::loadBundled);
    }
    public static List<Cell> cells(String id) {
        return TEMPLATES.computeIfAbsent(id,key -> {
            var def=definition(key);
            return def.cells().stream()
                .map(c -> new Cell(c.x(),c.y(),c.z(),c.state(),
                    c.x()==def.width()/2 && c.y()==0 && c.z()==def.depth()/2))
                .toList();
        });
    }

    public static List<Cell> cells() { return cells(LEGACY_BLUEPRINT_ID); }

    /** NW corner of footprint after placement; the physical core never moves. */
    public static BlockPos origin(BlockPos core,int shiftX,int shiftZ) {
        return origin(core,shiftX,0,shiftZ);
    }

    public static BlockPos origin(BlockPos core,int shiftX,int shiftY,int shiftZ) {
        return origin(LEGACY_BLUEPRINT_ID,core,shiftX,shiftY,shiftZ);
    }

    public static BlockPos origin(String blueprint,BlockPos core,int shiftX,int shiftY,int shiftZ) {
        var def=definition(blueprint);
        return core.offset(-def.width()/2+shiftX,shiftY,-def.depth()/2+shiftZ);
    }

    public static BlockPos position(BlockPos core,int shiftX,int shiftZ,
            int turns,Cell cell) {
        return position(core,shiftX,0,shiftZ,turns,cell);
    }

    public static BlockPos position(BlockPos core,int shiftX,int shiftY,int shiftZ,
            int turns,Cell cell) {
        return position(LEGACY_BLUEPRINT_ID,core,shiftX,shiftY,shiftZ,turns,cell);
    }

    public static BlockPos position(String blueprint,BlockPos core,int shiftX,int shiftY,int shiftZ,
            int turns,Cell cell) {
        var def=definition(blueprint);
        var rotated=BlueprintTransform.rotate(cell.x(),cell.z(),def.width(),def.depth(),turns);
        return origin(blueprint,core,shiftX,shiftY,shiftZ)
                .offset(rotated.x(),cell.y(),rotated.z());
    }

    public static BlockState expected(BlockPos core,int shiftX,int shiftZ,
            int turns,Cell cell) {
        return expected(core,shiftX,0,shiftZ,turns,cell);
    }

    public static BlockState expected(BlockPos core,int shiftX,int shiftY,int shiftZ,
            int turns,Cell cell) {
        return expected(LEGACY_BLUEPRINT_ID,core,shiftX,shiftY,shiftZ,turns,cell);
    }

    public static BlockState expected(String blueprint,BlockPos core,int shiftX,int shiftY,int shiftZ,
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

    /**
     * Compare all player-controlled orientation/placement properties, but
     * ignore neighbor-derived connections and transient redstone/open states.
     * Vanilla stairs shape and glass-pane arms are computed by Minecraft and
     * must not block a valid structure from reaching 100%.
     */
    public static boolean matching(BlockState actual,BlockState required) {
        if(!actual.is(required.getBlock()))return false;
        var placementProperties=java.util.Set.of(
                "facing","axis","half","type","hinge","face","rotation","part");
        for(var property:required.getProperties()) {
            if(placementProperties.contains(property.getName())
                    &&!actual.getValue(property).equals(required.getValue(property)))
                return false;
        }
        return true;
    }

    public static Check inspect(ServerLevel level,BlockPos core,int shiftX,int shiftZ,int turns) {
        return inspect(level,core,shiftX,0,shiftZ,turns);
    }

    public static Check inspect(ServerLevel level,BlockPos core,int shiftX,int shiftY,
            int shiftZ,int turns) {
        return inspect(LEGACY_BLUEPRINT_ID,level,core,shiftX,shiftY,shiftZ,turns);
    }

    public static Check inspect(String blueprint,ServerLevel level,BlockPos core,int shiftX,
            int shiftY,int shiftZ,int turns) {
        List<Cell> required=cells(blueprint);
        int matched=0;
        for(Cell cell:required) {
            BlockPos pos=position(blueprint,core,shiftX,shiftY,shiftZ,turns,cell);
            if(!level.hasChunkAt(pos))return new Check(matched,required.size(),false);
            if(matching(level.getBlockState(pos),
                    expected(blueprint,core,shiftX,shiftY,shiftZ,turns,cell)))matched++;
        }
        return new Check(matched,required.size(),true);
    }

    /**
     * Return a bounded sample of problems, including precise world coordinates
     * and BlockStates (for stairs, door facing and upper/lower slab). Inspect
     * is read-only and deliberately scans only already loaded chunks.
     */
    public static List<Missing> firstMissing(ServerLevel level,BlockPos core,
            int shiftX,int shiftY,int shiftZ,int turns,int limit) {
        return firstMissing(LEGACY_BLUEPRINT_ID,level,core,shiftX,shiftY,shiftZ,turns,limit);
    }
    public static List<Missing> firstMissing(String blueprint,ServerLevel level,BlockPos core,
            int shiftX,int shiftY,int shiftZ,int turns,int limit) {
        if(limit<=0)return List.of();
        List<Missing> problems=new ArrayList<>();
        for(Cell cell:cells(blueprint)) {
            BlockPos pos=position(blueprint,core,shiftX,shiftY,shiftZ,turns,cell);
            if(!level.hasChunkAt(pos))continue;
            BlockState actual=level.getBlockState(pos);
            BlockState required=expected(blueprint,core,shiftX,shiftY,shiftZ,turns,cell);
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

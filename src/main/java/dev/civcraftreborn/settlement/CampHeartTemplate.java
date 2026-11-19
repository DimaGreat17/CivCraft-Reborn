package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * The original 7x7x6 Camp Heart schematic, shared by authoritative server
 * validation and client projection. The manually placed core remains a fixed
 * physical anchor, while the footprint may move at most two blocks around it.
 * All position and facing transforms are deterministic.
 */
public final class CampHeartTemplate {
    public static final int SIZE = 7;
    public static final int HEIGHT = 6;
    private CampHeartTemplate() {}

    // Each row is north (-Z) to south (+Z); columns west (-X) to east (+X).
    // C is the default floor anchor. When a site is shifted, C becomes planks
    // and the existing core replaces the floor cell at its real world position.
    private static final String[][] LAYERS = {
        {"SSSSSSS", "SPPPPPS", "SPPPPPS", "SPPCPPS", "SPPPPPS", "SPPPPPS", "SSSSSSS"},
        {"LPPPPPL", "P.....P", "P.....P", "P.....P", "P.....P", "P.....P", "LPPDPPL"},
        {"LPPWPPL", "P.....P", "P.....P", "W.....W", "P.....P", "P.....P", "LPPDPPL"},
        {"<PPPPP>", "<.....>", "<.....>", "<.....>", "<.....>", "<.....>", "<PPPPP>"},
        {".<PPP>.", ".<...>.", ".<...>.", ".<...>.", ".<...>.", ".<...>.", ".<PPP>."},
        {"..<=>..", "..<=>..", "..<=>..", "..<=>..", "..<=>..", "..<=>..", "..<=>.."}
    };

    public record Cell(int x,int y,int z,BlockState state,boolean coreMarker) {}
    public record Check(int matched,int total,boolean loaded) {
        public boolean complete() { return loaded && matched == total; }
    }
    private static final List<Cell> CELLS = create();

    private static List<Cell> create() {
        List<Cell> cells = new ArrayList<>();
        for (int y=0;y<HEIGHT;y++) for (int z=0;z<SIZE;z++) {
            if (LAYERS[y][z].length()!=SIZE) throw new IllegalStateException("Bad Heart row");
            for (int x=0;x<SIZE;x++) {
                char symbol=LAYERS[y][z].charAt(x);
                if (symbol!='.') cells.add(new Cell(x,y,z,block(symbol,y),symbol=='C'));
            }
        }
        return List.copyOf(cells);
    }

    private static BlockState block(char symbol,int y) {
        return switch(symbol) {
            case 'S' -> Blocks.COBBLESTONE.defaultBlockState();
            case 'P' -> Blocks.OAK_PLANKS.defaultBlockState();
            case 'C' -> Blocks.OAK_PLANKS.defaultBlockState(); // lazy physical core marker
            case 'L' -> Blocks.OAK_LOG.defaultBlockState();
            case 'W' -> Blocks.GLASS_PANE.defaultBlockState();
            case 'D' -> Blocks.OAK_DOOR.defaultBlockState()
                    .setValue(DoorBlock.FACING,Direction.SOUTH)
                    .setValue(DoorBlock.HALF,y==1?DoubleBlockHalf.LOWER:DoubleBlockHalf.UPPER);
            case '<' -> Blocks.SPRUCE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING,Direction.WEST);
            case '>' -> Blocks.SPRUCE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING,Direction.EAST);
            case '=' -> Blocks.SPRUCE_SLAB.defaultBlockState()
                    .setValue(SlabBlock.TYPE,SlabType.TOP);
            default -> throw new IllegalArgumentException("Unknown symbol: "+symbol);
        };
    }

    public static List<Cell> cells() { return CELLS; }

    /** NW corner of footprint after placement; the physical core never moves. */
    public static BlockPos origin(BlockPos core,int shiftX,int shiftZ) {
        return core.offset(-3+shiftX,0,-3+shiftZ);
    }

    public static BlockPos position(BlockPos core,int shiftX,int shiftZ,
            int turns,Cell cell) {
        var rotated=BlueprintTransform.rotate(cell.x(),cell.z(),SIZE,SIZE,turns);
        return origin(core,shiftX,shiftZ).offset(rotated.x(),cell.y(),rotated.z());
    }

    public static BlockState expected(BlockPos core,int shiftX,int shiftZ,
            int turns,Cell cell) {
        BlockState state=cell.state();
        BlockPos target=position(core,shiftX,shiftZ,turns,cell);
        if(target.equals(core)) return CampBlocks.CAMP_CORE.defaultBlockState();
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
        int matched=0;
        for(Cell cell:CELLS) {
            BlockPos pos=position(core,shiftX,shiftZ,turns,cell);
            if(!level.hasChunkAt(pos))return new Check(matched,CELLS.size(),false);
            if(matching(level.getBlockState(pos),expected(core,shiftX,shiftZ,turns,cell)))matched++;
        }
        return new Check(matched,CELLS.size(),true);
    }

    public static boolean transformValid(int dx,int dz,int turns) {
        return dx>=-2&&dx<=2&&dz>=-2&&dz<=2&&turns>=0&&turns<4;
    }
}

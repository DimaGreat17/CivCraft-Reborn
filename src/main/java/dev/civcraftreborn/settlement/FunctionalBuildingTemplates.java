package dev.civcraftreborn.settlement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * SERVER and CLIENT share actual FULL structure blueprints, never just 2
 * functional blocks. A building does not exist as a CivCraft facility until
 * every non-optional structural/material cell is physically completed.
 *
 * Origin is the north-west floor corner (Y is feet level, foundation at -1).
 * Exact block matching is intentionally conservative for the first playable
 * slice. It can later become a flexible room/module specification.
 */
public final class FunctionalBuildingTemplates {
    public enum Type { STORAGE, WORKSHOP, FARM, BEDROOM, SMALL_HOUSE,
        BARRACKS, GRANARY, SMITHY, WATCHTOWER, WELL }
    public record Cell(int x, int y, int z, Block block, boolean anyBed) {
        public Cell(int x, int y, int z, Block block) { this(x,y,z,block,false); }
        public BlockPos position(BlockPos origin) { return origin.offset(x,y,z); }
    }
    public record Check(int total, int matched, boolean loaded) {
        public boolean complete() { return loaded && total > 0 && total == matched; }
    }

    private FunctionalBuildingTemplates() {}

    private static final Map<Type,List<Cell>> CACHE = new EnumMap<>(Type.class);

    public static List<Cell> cells(Type type) {
        if(type==null)return List.of();
        return CACHE.computeIfAbsent(type,FunctionalBuildingTemplates::build);
    }

    private static List<Cell> build(Type type) {
        List<Cell> c = new ArrayList<>();
        switch (type) {
            case STORAGE -> {
                shell(c,5,5,3,Blocks.OAK_PLANKS,Blocks.OAK_LOG,Blocks.SPRUCE_PLANKS);
                put(c,1,0,2,Blocks.CHEST);
                put(c,3,0,2,Blocks.BARREL);
                put(c,1,0,3,Blocks.BARREL);
            }
            case WORKSHOP -> {
                shell(c,6,5,3,Blocks.COBBLESTONE,Blocks.OAK_LOG,Blocks.OAK_PLANKS);
                put(c,1,0,2,Blocks.CRAFTING_TABLE);
                put(c,4,0,2,Blocks.FURNACE);
                put(c,4,0,3,Blocks.BARREL);
            }
            case FARM -> {
                // Full 7x7 bounded agricultural plot with water support
                // constructed by the player, not a 4-farmland fake facility.
                for(int x=0;x<7;x++) for(int z=0;z<7;z++) {
                    if(x==0||x==6||z==0||z==6) {
                        put(c,x,-1,z,Blocks.OAK_PLANKS);
                        if(!(x==3&&z==0)) put(c,x,0,z,Blocks.OAK_FENCE);
                    } else if(x==3&&z==3) {
                        put(c,x,-1,z,Blocks.WATER); // hydration source
                    } else {
                        put(c,x,-1,z,Blocks.FARMLAND);
                    }
                }
                put(c,3,0,0,Blocks.OAK_FENCE_GATE);
                put(c,1,0,1,Blocks.COMPOSTER);
            }
            case BEDROOM -> {
                shell(c,5,5,3,Blocks.OAK_PLANKS,Blocks.OAK_LOG,Blocks.OAK_PLANKS);
                c.add(new Cell(2,0,3,Blocks.AIR,true));
            }
            case SMALL_HOUSE -> {
                shell(c,5,5,4,Blocks.OAK_PLANKS,Blocks.OAK_LOG,Blocks.OAK_PLANKS);
                c.add(new Cell(2,0,3,Blocks.AIR,true));
                put(c,1,0,2,Blocks.CHEST);
                put(c,3,0,2,Blocks.CRAFTING_TABLE);
            }
            case BARRACKS -> {
                shell(c,9,7,4,Blocks.SPRUCE_PLANKS,Blocks.SPRUCE_LOG,
                        Blocks.SPRUCE_PLANKS);
                for(int x=2;x<=6;x+=2) c.add(new Cell(x,0,4,Blocks.AIR,true));
                put(c,4,0,5,Blocks.BARREL);
                put(c,4,0,2,Blocks.CRAFTING_TABLE);
            }
            case GRANARY -> {
                shell(c,7,7,4,Blocks.OAK_PLANKS,Blocks.SPRUCE_LOG,
                        Blocks.SPRUCE_PLANKS);
                for(int x=1;x<=5;x+=2)for(int z=2;z<=4;z+=2) {
                    put(c,x,0,z,(x+z)%2==0?Blocks.CHEST:Blocks.BARREL);
                }
            }
            case SMITHY -> {
                shell(c,7,6,4,Blocks.COBBLESTONE,Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS);
                put(c,1,0,2,Blocks.CRAFTING_TABLE);
                put(c,5,0,2,Blocks.BLAST_FURNACE);
                put(c,5,0,3,Blocks.FURNACE);
                put(c,3,0,4,Blocks.SMITHING_TABLE);
                put(c,2,0,4,Blocks.GRINDSTONE);
                put(c,1,0,4,Blocks.BARREL);
            }
            case WATCHTOWER -> {
                shell(c,5,5,6,Blocks.COBBLESTONE,Blocks.SPRUCE_LOG,
                        Blocks.SPRUCE_PLANKS);
                // Real upper watch platform, perimeter railing and signal bell.
                for(int x=0;x<5;x++)for(int z=0;z<5;z++)
                    if(x==0||x==4||z==0||z==4)
                        put(c,x,7,z,Blocks.OAK_FENCE);
                put(c,2,7,2,Blocks.BELL);
                for(int y=0;y<6;y++)put(c,1,y,1,Blocks.LADDER);
            }
            case WELL -> {
                // Built stone lined well with four wooden posts and real water.
                for(int x=0;x<5;x++)for(int z=0;z<5;z++) {
                    if(x==2&&z==2)put(c,x,-1,z,Blocks.WATER);
                    else put(c,x,-1,z,Blocks.COBBLESTONE);
                    if(x>=1&&x<=3&&z>=1&&z<=3
                            &&(x==1||x==3||z==1||z==3))
                        put(c,x,0,z,Blocks.COBBLESTONE);
                    if(x>=1&&x<=3&&z>=1&&z<=3)
                        put(c,x,4,z,Blocks.OAK_PLANKS);
                }
                for(int x:new int[]{1,3})for(int z:new int[]{1,3})
                    for(int y=1;y<=3;y++)put(c,x,y,z,Blocks.OAK_FENCE);
            }
        }
        Map<String,Cell> distinct = new LinkedHashMap<>();
        for(Cell cell:c) distinct.put(cell.x()+":"+cell.y()+":"+cell.z(),cell);
        return List.copyOf(distinct.values());
    }

    private static void put(List<Cell> c,int x,int y,int z,Block b) {
        c.add(new Cell(x,y,z,b));
    }

    private static void shell(List<Cell> c,int w,int d,int height,
            Block floor,Block post,Block roof) {
        for(int x=0;x<w;x++) for(int z=0;z<d;z++) {
            put(c,x,-1,z,floor);
            put(c,x,height,z,roof);
            if(x==0||x==w-1||z==0||z==d-1) {
                for(int y=0;y<height;y++) {
                    if(z==0 && x==w/2 && y<2) continue; // actual doorway
                    boolean corner=(x==0||x==w-1)&&(z==0||z==d-1);
                    put(c,x,y,z,corner?post:Blocks.OAK_PLANKS);
                }
            }
        }
    }

    public static Check inspect(ServerLevel level, BlockPos origin, Type type, int turns) {
        List<Cell> cells = cells(type);
        int width = cells.stream().mapToInt(Cell::x).max().orElse(0)+1;
        int depth = cells.stream().mapToInt(Cell::z).max().orElse(0)+1;
        int matched=0;
        for(Cell cell:cells) {
            BlueprintTransform.Point point=BlueprintTransform.rotate(
                    cell.x(),cell.z(),width,depth,turns);
            BlockPos pos=origin.offset(point.x(),cell.y(),point.z());
            if(!level.hasChunkAt(pos)) return new Check(cells.size(),matched,false);
            var state=level.getBlockState(pos);
            if(cell.anyBed()? state.getBlock() instanceof BedBlock : state.is(cell.block()))
                matched++;
        }
        return new Check(cells.size(),matched,true);
    }
}

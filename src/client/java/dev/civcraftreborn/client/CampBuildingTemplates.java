package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * CivCraft-authored starter settlement templates, inspired by generic medieval
 * village architecture; NOT converted/repackaged third-party .schem files.
 * All cells are actual Minecraft blocks, never automatically placed.
 *
 * Templates are optional visual guidance. D-050 allows freeform player housing;
 * a ghost's exact decorative shape does not replace server ROOM/MODULES rules.
 */
public final class CampBuildingTemplates {
    private CampBuildingTemplates() {}

    public static List<CampBlueprintProjection.Step> forPlan(CampBlueprintProjection.Plan plan) {
        List<CampBlueprintProjection.Step> cells = new ArrayList<>();
        switch (plan) {
            case SMALL_HOUSE -> cottage(cells);
            case BARRACKS -> barracks(cells);
            case GRANARY -> granary(cells);
            case SMITHY -> smithy(cells);
            case WATCHTOWER -> watchtower(cells);
            case WELL -> well(cells);
            default -> { return List.of(); }
        }
        Map<String, CampBlueprintProjection.Step> unique = new LinkedHashMap<>();
        for (CampBlueprintProjection.Step cell : cells) {
            String key = cell.dx() + ":" + cell.dy() + ":" + cell.dz();
            unique.put(key, cell);
        }
        return List.copyOf(unique.values());
    }

    private static void put(List<CampBlueprintProjection.Step> cells, int x,int y,int z,Block block) {
        cells.add(new CampBlueprintProjection.Step(x,y,z,block));
    }

    /** Four corner posts, solid floor, open door and roof; simple 5×5 survival house. */
    private static void cottage(List<CampBlueprintProjection.Step> c) {
        int x0=5, z0=5, x1=9, z1=9;
        house(c,x0,z0,x1,z1,4,Blocks.OAK_PLANKS,Blocks.OAK_LOG,Blocks.OAK_PLANKS,7,z0);
        put(c,6,0,7,Blocks.CHEST);
        put(c,8,0,7,Blocks.CRAFTING_TABLE);
        c.add(new CampBlueprintProjection.Step(7,0,8,Blocks.AIR,true));
    }

    /** Open room with multiple real beds; not a free citizen generator. */
    private static void barracks(List<CampBlueprintProjection.Step> c) {
        int x0=-10,z0=6,x1=-4,z1=11;
        house(c,x0,z0,x1,z1,4,Blocks.COBBLESTONE,Blocks.SPRUCE_LOG,Blocks.SPRUCE_PLANKS,-7,z0);
        for(int x=-9;x<=-5;x+=2) {
            c.add(new CampBlueprintProjection.Step(x,0,9,Blocks.AIR,true));
        }
        put(c,-7,0,10,Blocks.BARREL);
    }

    /** Elevated compact barn with storage; food must still be physically stored. */
    private static void granary(List<CampBlueprintProjection.Step> c) {
        house(c,11,-9,15,-5,4,Blocks.OAK_PLANKS,Blocks.SPRUCE_LOG,Blocks.SPRUCE_PLANKS,13,-9);
        put(c,12,0,-7,Blocks.CHEST);
        put(c,14,0,-7,Blocks.BARREL);
        put(c,12,0,-6,Blocks.BARREL);
        put(c,14,0,-6,Blocks.CHEST);
    }

    /** Cobblestone working forge; stone and fuel remain normal resources. */
    private static void smithy(List<CampBlueprintProjection.Step> c) {
        house(c,-14,-7,-9,-3,4,Blocks.COBBLESTONE,Blocks.OAK_LOG,Blocks.STONE_BRICKS,-11,-7);
        put(c,-13,0,-5,Blocks.FURNACE);
        put(c,-12,0,-5,Blocks.BLAST_FURNACE);
        put(c,-10,0,-5,Blocks.CRAFTING_TABLE);
        put(c,-10,0,-4,Blocks.BARREL);
    }

    /** A narrow wooden watch post, built one block at a time; no defense bonus in P1. */
    private static void watchtower(List<CampBlueprintProjection.Step> c) {
        int x0=12,z0=8,x1=16,z1=12;
        for(int x=x0;x<=x1;x++) for(int z=z0;z<=z1;z++) {
            put(c,x,-1,z,Blocks.COBBLESTONE);
            put(c,x,6,z,Blocks.SPRUCE_PLANKS);
        }
        for(int x : new int[]{x0,x1}) for(int z : new int[]{z0,z1})
            for(int y=0;y<=6;y++) put(c,x,y,z,Blocks.SPRUCE_LOG);
        for(int y=0;y<=5;y++) put(c,14,y,10,Blocks.LADDER);
        for(int x=x0;x<=x1;x++) for(int z=z0;z<=z1;z++)
            if(x==x0||x==x1||z==z0||z==z1) put(c,x,7,z,Blocks.OAK_FENCE);
    }

    /** A decorative well: not a source of infinite virtual water resources. */
    private static void well(List<CampBlueprintProjection.Step> c) {
        int cx=1, cz=-9;
        for(int x=cx-1;x<=cx+1;x++) for(int z=cz-1;z<=cz+1;z++) {
            if(x==cx && z==cz) continue; // leave center physically open
            put(c,x,0,z,Blocks.COBBLESTONE);
        }
        for(int z : new int[]{cz-1,cz+1}) {
            put(c,cx-1,1,z,Blocks.OAK_FENCE);
            put(c,cx+1,1,z,Blocks.OAK_FENCE);
        }
        for(int x=cx-1;x<=cx+1;x++) for(int z=cz-1;z<=cz+1;z++)
            put(c,x,3,z,Blocks.OAK_PLANKS);
    }

    /**
     * Rectangular room with a 1-wide real doorway and continuous horizontal
     * roof; no magic claim, capacity or spawn effect. x/z include both edges.
     */
    private static void house(List<CampBlueprintProjection.Step> c,
            int x0,int z0,int x1,int z1,int height,
            Block floor,Block posts,Block roof,int doorX,int doorZ) {
        for(int x=x0;x<=x1;x++) for(int z=z0;z<=z1;z++) {
            put(c,x,-1,z,floor);
            put(c,x,height,z,roof);
        }
        for(int y=0;y<height;y++) {
            for(int x=x0;x<=x1;x++) for(int z=z0;z<=z1;z++) {
                if(x!=x0 && x!=x1 && z!=z0 && z!=z1) continue;
                if(x==doorX && z==doorZ && y<2) continue;
                boolean corner=(x==x0||x==x1)&&(z==z0||z==z1);
                put(c,x,y,z,corner?posts:Blocks.OAK_PLANKS);
            }
        }
    }
}

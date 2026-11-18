package dev.civcraftreborn.settlement;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Functional buildings require actual COMPLETE, registered structures.
 * A workbench, chest or handful of farmland by themselves never count.
 */
public final class CampBuildingScanner {
    private CampBuildingScanner() {}

    public record Facilities(boolean workshop, boolean granary, boolean farm,
            int storageBlocks, int craftingTables, int furnaces, int farmland,
            int composters, boolean complete) {}

    public static Facilities scan(ServerLevel world, CampRecord camp) {
        if (!world.dimension().identifier().toString().equals(camp.dimension()))
            return new Facilities(false,false,false,0,0,0,0,0,false);
        var server=world.getServer();
        boolean workshop=false,storage=false,farm=false;
        int chests=0,tables=0,furnaces=0,farmland=0,composters=0;
        boolean complete=true;
        for(BuildingRecord building : BuildingSavedData.get(server).atCamp(camp)) {
            var check=FunctionalBuildingTemplates.inspect(world,
                    new BlockPos(building.x(),building.y(),building.z()),
                    building.type(),building.rotation());
            if (!check.loaded()) complete=false;
            if (!check.complete()) continue; // No benefit for destroyed building
            switch(building.type()) {
                case STORAGE -> { storage=true;chests+=3; }
                case WORKSHOP -> { workshop=true;tables++;furnaces++; }
                case FARM -> { farm=true;farmland+=24;composters++; }
                case SMALL_HOUSE -> { tables++;chests++; }
                case BEDROOM -> {}
            }
        }
        return new Facilities(workshop,storage,farm,chests,tables,furnaces,
                farmland,composters,complete);
    }
}

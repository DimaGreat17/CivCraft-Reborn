package dev.civcraftreborn.settlement;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/**
 * Initial physical-module recognition for starter structures. Buildings are not
 * spawned: each module must be a real Minecraft block in an accessible camp.
 *
 * Final flexible MODULES blueprint validation is a separate, stricter P1 gate.
 */
public final class CampBuildingScanner {
    private CampBuildingScanner() {}

    public record Facilities(boolean workshop, boolean granary, boolean farm,
            int storageBlocks, int craftingTables, int furnaces, int farmland,
            int composters, boolean complete) {}

    public static Facilities scan(ServerLevel world, CampRecord camp) {
        BlockPos center = new BlockPos(camp.x(), camp.y(), camp.z());
        int craftingTables=0, furnaces=0, storage=0, farmland=0, composters=0;
        boolean complete = true;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-12,-3,-12),
                center.offset(12,3,12))) {
            if (!world.hasChunkAt(pos)) {
                complete = false;
                continue;
            }
            var b = world.getBlockState(pos).getBlock();
            if (b == Blocks.CRAFTING_TABLE) craftingTables++;
            if (b == Blocks.FURNACE || b == Blocks.BLAST_FURNACE) furnaces++;
            if (b == Blocks.CHEST || b == Blocks.BARREL) storage++;
            if (b == Blocks.FARMLAND) farmland++;
            if (b == Blocks.COMPOSTER) composters++;
        }
        // Do not grant a facility for an unloaded or partially verified region.
        return new Facilities(complete && craftingTables>0 && furnaces>0,
                complete && storage>0, complete && farmland>=4 && composters>0,
                storage,craftingTables,furnaces,farmland,composters,complete);
    }
}

package dev.civcraftreborn.settlement;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

/** Only physically stored food is counted, with no virtual stock creation. */
public final class CampStorageScanner {
    private CampStorageScanner() {}

    public record Stock(int food, int storageBlocks, boolean complete) {}

    public static Stock scan(ServerLevel level, CampRecord camp) {
        BlockPos center = new BlockPos(camp.x(), camp.y(), camp.z());
        int food = 0, storageBlocks = 0;
        boolean complete = true;
        for (BlockPos candidate : BlockPos.betweenClosed(center.offset(-12, -3, -12),
                center.offset(12, 3, 12))) {
            if (!level.hasChunkAt(candidate)) {
                complete = false;
                continue;
            }
            if (!(level.getBlockEntity(candidate) instanceof Container container)) continue;
            storageBlocks++;
            // Food is not redeemed here; promotion checks that the stock still
            // exists. Consumers must remove it from the exact same container.
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (isSupportedFood(stack.getItem())) food += stack.getCount();
            }
        }
        return new Stock(food, storageBlocks, complete);
    }

    public static boolean isSupportedFood(Item item) {
        return item == Items.BREAD || item == Items.BAKED_POTATO
                || item == Items.COOKED_BEEF || item == Items.COOKED_PORKCHOP
                || item == Items.CARROT;
    }
}

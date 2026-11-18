package dev.civcraftreborn.settlement;

import dev.civcraftreborn.foundation.WorldSettingsSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * The one-time construction material kit is supplied *after the player places*
 * a physical camp core. No resources are awarded on login or world creation.
 * The obsidian is for building the camp heart around the placed core, not for
 * crafting the core itself (which would cause a progression deadlock).
 */
public final class CampCoreBlock extends Block {
    public static final int STARTER_OBSIDIAN = 4;

    public CampCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
            @Nullable LivingEntity placer, ItemStack placedStack) {
        super.setPlacedBy(level, pos, state, placer, placedStack);
        if (level.isClientSide() || !(placer instanceof ServerPlayer player)) return;

        if (!WorldSettingsSavedData.get(player.level().getServer()).settings().civcraftWorld()) {
            return;
        }

        StarterGrantSavedData grants = StarterGrantSavedData.get(player.getServer());
        if (!grants.registerPlacement(player.getUUID())) return;

        ItemStack obsidian = new ItemStack(Items.OBSIDIAN, STARTER_OBSIDIAN);
        if (!player.getInventory().add(obsidian) && !obsidian.isEmpty()) {
            // Inventory insertion can be partial; only drop the remainder.
            net.minecraft.world.Containers.dropItemStack(level,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, obsidian);
        }
        player.sendSystemMessage(Component.literal(
                "CivCraft: you placed your first Camp Core. Received 4 obsidian "
                + "to build the physical camp heart. This grant is once per player per world."));
    }
}

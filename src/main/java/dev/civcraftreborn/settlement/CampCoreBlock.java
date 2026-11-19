package dev.civcraftreborn.settlement;

import dev.civcraftreborn.foundation.WorldSettingsSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.Nullable;

/**
 * Single-use physical construction starter, NOT an embedded central Camp block.
 * Once the player confirms a separate building site, the starter is consumed.
 * No obsidian/material kit is generated on each placement.
 */
public final class CampCoreBlock extends Block {
    public CampCoreBlock(Properties properties) { super(properties); }

    @Override
    public void setPlacedBy(Level level,BlockPos pos,BlockState state,
            @Nullable LivingEntity placer,ItemStack placedStack) {
        super.setPlacedBy(level,pos,state,placer,placedStack);
        if(level.isClientSide()||!(placer instanceof ServerPlayer player))return;
        if(!WorldSettingsSavedData.get(player.level().getServer()).settings().civcraftWorld())return;

        String dimension=player.level().dimension().identifier().toString();
        var sites=CampHeartSavedData.get(player.level().getServer());
        var old=sites.byCore(dimension,pos);
        if(old.isPresent()&&old.get().confirmed()) {
            player.sendSystemMessage(Component.literal(
                    "CivCraft: This foundation location already belongs to a confirmed project."));
            return;
        }
        if(!sites.ownedBy(player.getUUID(),dimension).isEmpty()
                && (old.isEmpty() || !old.get().owner().equals(player.getUUID().toString()))) {
            player.sendSystemMessage(Component.literal(
                    "CivCraft: Finish your existing camp founding project first."));
            return;
        }
        if(old.isPresent())sites.remove(dimension,pos);
        sites.add(CampHeartSite.placed(player.getUUID(),dimension,pos));
        ServerPlayNetworking.send(player,new HeartPlacedNotice(pos));
        player.sendSystemMessage(Component.literal(
                "CivCraft: Choose a separate 7x7 Camp Heart building site via G. "
                +"The foundation is consumed on confirmation; no building is tied to this block."));
    }
}

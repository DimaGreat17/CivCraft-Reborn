package dev.civcraftreborn.settlement;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/** Player-founded camps: real resources, physical anchor, server-side checks. */
public final class CampCommands {
    private CampCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("camp")
                .then(Commands.literal("found").executes(ctx -> found(ctx.getSource())))
                .then(Commands.literal("status").executes(ctx -> status(ctx.getSource())))
                .then(Commands.literal("inspect").executes(ctx -> inspect(ctx.getSource())))
                .then(Commands.literal("promote").executes(ctx -> promote(ctx.getSource())));
    }

    private static int found(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            var level = player.level();
            CampSavedData store = CampSavedData.get(source.getServer());
            if (store.ownedBy(player.getUUID()).isPresent()) {
                return fail(source, "You already own a camp; its ID and resources persist.");
            }

            Optional<BlockPos> anchor = findCore(player);
            if (anchor.isEmpty()) {
                return fail(source, "Place a CivCraft Camp Core within 4 blocks before founding.");
            }
            BlockPos pos = anchor.get();
            String dimension = level.dimension().identifier().toString();
            boolean withinBorder = level.getWorldBorder().isWithinBounds(pos);
            boolean supported = level.getBlockState(pos.below()).isSolid();
            if (!CampRules.canFoundAt(store.all(), dimension, pos.getX(), pos.getZ(),
                    withinBorder, supported)) {
                return fail(source, "Invalid site: collision, border, terrain or nearby settlement.");
            }
            if (!has(player, Items.OAK_LOG, CampRules.REQUIRED_LOGS)
                    || !has(player, Items.COBBLESTONE, CampRules.REQUIRED_STONE)
                    || !has(player, Items.BREAD, CampRules.REQUIRED_FOOD)) {
                return fail(source, "Founding requires 16 oak logs, 16 cobblestone and 8 bread in inventory.");
            }

            // On the authoritative server thread, validate all resource availability
            // BEFORE mutation; never mint items, NPCs, land or settlement money.
            CampRecord camp = CampRules.found(player.getUUID(), dimension,
                    pos.getX(), pos.getY(), pos.getZ(),
                    player.getName().getString() + "'s Camp", level.getGameTime());
            remove(player, Items.OAK_LOG, CampRules.REQUIRED_LOGS);
            remove(player, Items.COBBLESTONE, CampRules.REQUIRED_STONE);
            remove(player, Items.BREAD, CampRules.REQUIRED_FOOD);
            store.add(camp);
            source.sendSuccess(() -> Component.literal(
                    "Camp founded at " + pos.toShortString() + " (ID " + camp.id()
                    + "). No residents or resources are spawned for free."), false);
            return 1;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source, "Only a player can found a camp.");
        }
    }

    private static int inspect(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            var camps = CampSavedData.get(source.getServer());
            var entry = camps.ownedBy(player.getUUID());
            if (entry.isEmpty()) return fail(source, "You do not own a registered camp.");
            CampRecord camp = entry.get();
            var housing = HousingScanner.scan(player.serverLevel(), camp);
            var stock = CampStorageScanner.scan(player.serverLevel(), camp);
            String message = "Camp " + camp.name() + " (" + camp.stage() + "): "
                    + housing.roomCount() + " rooms, " + housing.bedCount() + " valid beds, "
                    + housing.privateFloorCells() + " usable floor cells, "
                    + housing.capacity() + " housing slots (4 cells/NPC), "
                    + stock.food() + " real food items in " + stock.storageBlocks()
                    + " storages. Complete=" + (housing.complete() && stock.complete());
            source.sendSuccess(() -> Component.literal(message), false);
            return 1;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source, "Only camp owners can inspect their own housing.");
        }
    }

    private static int promote(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            CampSavedData registry = CampSavedData.get(source.getServer());
            CampRecord camp = registry.ownedBy(player.getUUID()).orElse(null);
            if (camp == null) return fail(source, "Found a camp before promoting it.");
            var housing = HousingScanner.scan(player.serverLevel(), camp);
            var food = CampStorageScanner.scan(player.serverLevel(), camp);
            if (!housing.complete() || !food.complete()) {
                return fail(source, "Some housing or storage chunks could not be verified.");
            }
            CampRecord promoted;
            try {
                promoted = CampRules.promote(camp, housing.roomCount(),
                        CitizenSavedData.get(source.getServer()).atCamp(camp).size(),
                        food.food(), food.storageBlocks() > 0);
            } catch (IllegalStateException e) {
                return fail(source, e.getMessage());
            }
            // Promotion changes only the settlement stage; existing beds,
            // real food and resident identities are never duplicated.
            registry.replace(camp, promoted);
            source.sendSuccess(() -> Component.literal(
                    "Camp promoted to SETTLEMENT without free resources or new IDs."), false);
            return 1;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source, "Only camp owners can promote settlements.");
        }
    }

    private static int status(CommandSourceStack source) {
        CampSavedData store = CampSavedData.get(source.getServer());
        source.sendSuccess(() -> Component.literal("Registered settlements: " + store.all().size()), false);
        for (CampRecord camp : store.all()) {
            source.sendSuccess(() -> Component.literal(
                    camp.name() + " [" + camp.stage() + "] "
                    + camp.dimension() + " " + camp.x() + "," + camp.y()
                    + "," + camp.z() + " ID=" + camp.id()), false);
        }
        return store.all().size();
    }

    private static Optional<BlockPos> findCore(ServerPlayer player) {
        BlockPos center = player.blockPosition();
        var level = player.level();
        for (BlockPos candidate : BlockPos.betweenClosed(
                center.offset(-4, -2, -4), center.offset(4, 2, 4))) {
            if (level.getBlockState(candidate).is(CampBlocks.CAMP_CORE)) {
                return Optional.of(candidate.immutable());
            }
        }
        return Optional.empty();
    }

    private static boolean has(ServerPlayer player, Item item, int amount) {
        int total = 0;
        var inv = player.getInventory();
        for (int index = 0; index < inv.getContainerSize(); index++) {
            ItemStack stack = inv.getItem(index);
            if (stack.is(item)) total += stack.getCount();
            if (total >= amount) return true;
        }
        return false;
    }

    private static void remove(ServerPlayer player, Item item, int amount) {
        int remaining = amount;
        var inv = player.getInventory();
        for (int index = 0; index < inv.getContainerSize() && remaining > 0; index++) {
            ItemStack stack = inv.getItem(index);
            if (!stack.is(item)) continue;
            int consumed = Math.min(stack.getCount(), remaining);
            stack.shrink(consumed);
            remaining -= consumed;
        }
        if (remaining != 0) throw new IllegalStateException("Unexpected inventory mutation during founding");
        inv.setChanged();
    }

    private static int fail(CommandSourceStack source, String reason) {
        source.sendFailure(Component.literal(reason));
        return 0;
    }
}

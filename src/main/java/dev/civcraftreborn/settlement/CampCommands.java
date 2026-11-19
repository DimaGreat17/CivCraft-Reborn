package dev.civcraftreborn.settlement;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;

import java.util.Optional;
import dev.civcraftreborn.foundation.WorldSettingsSavedData;

/** Player-founded camps: real resources, physical anchor, server-side checks. */
public final class CampCommands {
    private CampCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("camp")
                .then(Commands.literal("found").executes(ctx -> found(ctx.getSource())))
                .then(CampHeartCommands.command())
                .then(Commands.literal("status").executes(ctx -> status(ctx.getSource())))
                .then(Commands.literal("inspect").executes(ctx -> inspect(ctx.getSource())))
                .then(Commands.literal("plan").executes(ctx -> plan(ctx.getSource())))
                .then(Commands.literal("promote").executes(ctx -> promote(ctx.getSource())))
                .then(Commands.literal("recruit").executes(ctx -> recruit(ctx.getSource())))
                .then(CampBuildingCommands.command())
                .then(CampResidentJobCommands.command())
                .then(CampConstructionCommands.command())
                .then(CampMigrationCommands.command());
    }

    private static int found(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            var level = player.level();
            if (!WorldSettingsSavedData.get(source.getServer()).settings().civcraftWorld()) {
                return fail(source, "Create a CivCraft world to found a camp.");
            }
            CampSavedData store = CampSavedData.get(source.getServer());
            if (store.ownedBy(player.getUUID()).isPresent()) {
                return fail(source, "You already own a camp; its ID and resources persist.");
            }

            String dimension = level.dimension().identifier().toString();
            CampHeartSite site = CampHeartSavedData.get(source.getServer())
                    .ownedBy(player.getUUID(),dimension).stream().filter(CampHeartSite::confirmed)
                    .findFirst().orElse(null);
            if(site == null) {
                return fail(source, "Place your own Camp Heart foundation and confirm its site.");
            }
            BlockPos pos = site.core();
            if (!site.confirmed()) {
                return fail(source, "Confirm your Camp Heart placement before building.");
            }
            var finished = CampHeartTemplate.inspect((net.minecraft.server.level.ServerLevel)level,
                    pos,site.shiftX(),site.shiftY(),site.shiftZ(),site.turns());
            if (!finished.complete()) {
                var problems=CampHeartTemplate.firstMissing((net.minecraft.server.level.ServerLevel)level,
                        pos,site.shiftX(),site.shiftY(),site.shiftZ(),site.turns(),1);
                String hint=problems.isEmpty()?"":" First mismatch at "+
                        problems.getFirst().pos().toShortString()+": expected="+
                        problems.getFirst().expected()+" actual="+problems.getFirst().actual();
                return fail(source, "Camp Heart incomplete: "
                        + finished.matched() + "/" + finished.total()
                        + (finished.loaded() ? "." : " (unloaded chunks).")
                        + hint + " Run /civcraft camp heart inspect for more details.");
            }
            BlockPos buildingCenter=CampHeartTemplate.origin(pos,site.shiftX(),site.shiftY(),site.shiftZ())
                    .offset(3,0,3);
            boolean withinBorder = level.getWorldBorder().isWithinBounds(buildingCenter);
            // A fully verified 162-cell structure is already physical.
            // Requiring extra solid terrain below its center would reject
            // legitimate raised buildings even when every required cell matches.
            boolean supported = finished.complete();
            if (!CampRules.canFoundAt(store.all(), dimension,
                    CampHeartTemplate.origin(pos,site.shiftX(),site.shiftY(),site.shiftZ()).getX()+3,
                    CampHeartTemplate.origin(pos,site.shiftX(),site.shiftY(),site.shiftZ()).getZ()+3,
                    withinBorder, supported)) {
                return fail(source, "Invalid site: collision, border, terrain or nearby settlement.");
            }
            // Materials have already been placed into the complete Heart by hand.
            // Never charge the player for the same wood and stone twice.
            CampRecord camp = CampRules.found(player.getUUID(), dimension,
                    buildingCenter.getX(),buildingCenter.getY(),buildingCenter.getZ(),
                    player.getName().getString() + "'s Camp", level.getGameTime());
            store.add(camp);
            source.sendSuccess(() -> Component.literal(
                    "Camp founded at " + buildingCenter.toShortString() + " (ID " + camp.id()
                    + "). No residents or resources are spawned for free."), false);
            return 1;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source, "Only a player can found a camp.");
        }
    }

    private static int plan(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            CampRecord camp = CampSavedData.get(source.getServer())
                    .ownedBy(player.getUUID()).orElse(null);
            if (camp == null) {
                return fail(source, "Place a physical Camp Core and found your camp first.");
            }
            var rooms = HousingScanner.scan(player.level(), camp);
            var stock = CampStorageScanner.scan(player.level(), camp);
            var facilities = CampBuildingScanner.scan(player.level(), camp);
            int residents = CitizenSavedData.get(source.getServer()).atCamp(camp).size();

            source.sendSuccess(() -> Component.literal(
                    "First craft and place the Camp Heart foundation, confirm its footprint "
                    + "using /civcraft camp heart confirm, then build all schematic blocks by hand."), false);
            source.sendSuccess(() -> Component.literal(
                    "1. Sheltered bedrooms: " + rooms.roomCount() + "/3. "
                    + "Each NPC needs a physical bed and at least 4 valid private floor cells."), false);
            source.sendSuccess(() -> Component.literal(
                    "2. Residents: " + residents + "/3. Recruit existing villagers, no free spawns."), false);
            source.sendSuccess(() -> Component.literal(
                    "3. Granary: " + stock.food() + "/16 actual food in "
                    + stock.storageBlocks() + " loaded containers."), false);
            source.sendSuccess(() -> Component.literal(
                    "4. Workshop: crafting table + furnace = " + facilities.workshop()), false);
            source.sendSuccess(() -> Component.literal(
                    "5. Farm: at least 4 farmland + composter = " + facilities.farm()), false);
            source.sendSuccess(() -> Component.literal(
                    "This is a modular build schematic, NOT spawned structures. "
                    + "Use /civcraft camp inspect to validate and /civcraft camp promote to advance."), false);
            return 1;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source, "Only camp owners can review their construction plan.");
        }
    }

    private static int recruit(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            CampSavedData store = CampSavedData.get(source.getServer());
            CampRecord camp = store.ownedBy(player.getUUID()).orElse(null);
            if (camp == null) return fail(source, "Found your camp first.");
            if (!player.level().dimension().identifier().toString().equals(camp.dimension())) {
                return fail(source, "You must be in the camp's dimension.");
            }
            BlockPos center = new BlockPos(camp.x(), camp.y(), camp.z());
            if (player.blockPosition().distSqr(center) > 24 * 24) {
                return fail(source, "Return to your camp to recruit.");
            }
            var housing = HousingScanner.scan(player.level(), camp);
            if (!housing.complete()) return fail(source, "Housing is pending full verification.");
            CitizenSavedData citizens = CitizenSavedData.get(source.getServer());
            int registered = citizens.atCamp(camp).size();
            if (housing.capacity() <= registered) {
                return fail(source, "No free, verified housing capacity (4 real floor cells/NPC).");
            }
            if (!has(player, Items.BREAD, 4)) {
                return fail(source, "Each recruited resident requires 4 real bread in your inventory.");
            }

            var candidates = player.level().getEntitiesOfClass(Villager.class,
                    new AABB(center).inflate(12.0),
                    villager -> villager.isAlive() && citizens.byEntity(villager.getUUID()).isEmpty());
            if (candidates.isEmpty()) {
                return fail(source, "No unregistered living villager nearby. NPCs are never spawned for free.");
            }
            var freeBed = HousingScanner.verifiedBedHeads(player.level(), camp).stream()
                    .filter(bed -> !citizens.claimedBed(camp,
                            bed.getX(), bed.getY(), bed.getZ()))
                    .findFirst();
            if (freeBed.isEmpty()) {
                return fail(source, "No unassigned, reachable, sheltered bed available.");
            }

            Villager villager = candidates.getFirst();
            BlockPos bed = freeBed.get();
            CitizenRecord record = CitizenRecord.recruit(villager.getUUID(), camp,
                    bed.getX(), bed.getY(), bed.getZ(), player.level().getGameTime());
            // No world entity creation, no duplicate population and no virtual free items.
            remove(player, Items.BREAD, 4);
            citizens.add(record);
            villager.getNavigation().moveTo(bed.getX() + 0.5, bed.getY() + 1,
                    bed.getZ() + 0.5, 0.9);
            source.sendSuccess(() -> Component.literal(
                    "Registered physical villager as citizen " + record.id()
                    + " with a real bed. This villager keeps vanilla AI and needs."), false);
            return 1;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source, "Only players can recruit existing villagers.");
        }
    }

    private static int inspect(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            var camps = CampSavedData.get(source.getServer());
            var entry = camps.ownedBy(player.getUUID());
            if (entry.isEmpty()) return fail(source, "You do not own a registered camp.");
            CampRecord camp = entry.get();
            var housing = HousingScanner.scan(player.level(), camp);
            var stock = CampStorageScanner.scan(player.level(), camp);
            var facilities = CampBuildingScanner.scan(player.level(), camp);
            String message = "Camp " + camp.name() + " (" + camp.stage() + "): "
                    + housing.roomCount() + " rooms, " + housing.bedCount() + " valid beds, "
                    + housing.privateFloorCells() + " usable floor cells, "
                    + housing.capacity() + " housing slots (4 cells/NPC), "
                    + stock.food() + " real food items in " + stock.storageBlocks()
                    + " storages, workshop=" + facilities.workshop()
                    + ", farm=" + facilities.farm()
                    + ". Complete=" + (housing.complete() && stock.complete() && facilities.complete());
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
            var housing = HousingScanner.scan(player.level(), camp);
            var food = CampStorageScanner.scan(player.level(), camp);
            var facilities = CampBuildingScanner.scan(player.level(), camp);
            if (!housing.complete() || !food.complete() || !facilities.complete()) {
                return fail(source, "Some housing or storage chunks could not be verified.");
            }
            CampRecord promoted;
            try {
                promoted = CampRules.promote(camp, housing.roomCount(),
                        CitizenSavedData.get(source.getServer()).atCamp(camp).size(),
                        food.food(), food.storageBlocks() > 0 && facilities.granary(),
                        facilities.workshop(), facilities.farm());
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

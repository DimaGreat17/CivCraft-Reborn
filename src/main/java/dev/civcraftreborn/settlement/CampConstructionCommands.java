package dev.civcraftreborn.settlement;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;

/**
 * A construction order NEVER bypasses manual building: both player and NPC
 * fill exactly the same material cells, and the NPC consumes actual items
 * from physical camp storage. To issue an order, stand at the plan origin or
 * submit exact coordinates from the CivCraft placement screen.
 */
public final class CampConstructionCommands {
    private CampConstructionCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("construction")
                .then(Commands.literal("list").executes(c -> list(c.getSource())))
                .then(Commands.literal("order")
                        .then(Commands.argument("type",StringArgumentType.word())
                                .then(Commands.argument("rotation",IntegerArgumentType.integer(0,3))
                                        .then(Commands.argument("x",IntegerArgumentType.integer())
                                                .then(Commands.argument("y",IntegerArgumentType.integer())
                                                        .then(Commands.argument("z",IntegerArgumentType.integer())
                                                                .executes(c -> issue(c.getSource(),
                                                                        StringArgumentType.getString(c,"type"),
                                                                        IntegerArgumentType.getInteger(c,"rotation"),
                                                                        IntegerArgumentType.getInteger(c,"x"),
                                                                        IntegerArgumentType.getInteger(c,"y"),
                                                                        IntegerArgumentType.getInteger(c,"z")))))))));
    }

    private static int issue(CommandSourceStack source,String name,int rotation,int x,int y,int z) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=CampSavedData.get(source.getServer())
                    .ownedBy(player.getUUID()).orElse(null);
            if(camp==null)return fail(source,"Found a camp first.");
            if(!player.level().dimension().identifier().toString().equals(camp.dimension()))
                return fail(source,"Order must belong to this camp's world.");
            if(player.blockPosition().distSqr(new BlockPos(camp.x(),camp.y(),camp.z()))>24*24)
                return fail(source,"Stand inside your camp to issue an order.");

            FunctionalBuildingTemplates.Type type;
            try {type=FunctionalBuildingTemplates.Type.valueOf(name.toUpperCase(java.util.Locale.ROOT));}
            catch(IllegalArgumentException e){return fail(source,"Not a supported fully buildable blueprint.");}
            if(!BlueprintAccess.allowed(type.name(),camp.stage()))
                return fail(source,"Blueprint is not unlocked.");

            BlockPos origin=new BlockPos(x,y,z);
            var cells=FunctionalBuildingTemplates.cells(type);
            int w=cells.stream().mapToInt(FunctionalBuildingTemplates.Cell::x).max().orElse(0)+1;
            int d=cells.stream().mapToInt(FunctionalBuildingTemplates.Cell::z).max().orElse(0)+1;
            for(var cell:cells) {
                var p=BlueprintTransform.rotate(cell.x(),cell.z(),w,d,rotation);
                BlockPos absolute=origin.offset(p.x(),cell.y(),p.z());
                if(Math.abs(absolute.getX()-camp.x())>CampRules.CAMP_RADIUS||
                        Math.abs(absolute.getZ()-camp.z())>CampRules.CAMP_RADIUS||
                        !player.level().getWorldBorder().isWithinBounds(absolute))
                    return fail(source,"Construction crosses camp or world border.");
                if(!player.level().hasChunkAt(absolute))
                    return fail(source,"Load all chunks of the requested building.");
            }

            var citizens=CitizenSavedData.get(source.getServer()).atCamp(camp);
            var jobs=ResidentWorkSavedData.get(source.getServer());
            var orders=ConstructionSavedData.get(source.getServer());
            CitizenRecord builder=citizens.stream()
                    .filter(c->jobs.byCitizen(c.id()).map(a->
                            a.role()==ResidentWorkRole.BUILDER).orElse(false))
                    .filter(c->!orders.citizenBusy(c.id()))
                    .filter(c->player.level().getEntity(java.util.UUID.fromString(c.entityUuid()))
                            instanceof Villager)
                    .findFirst().orElse(null);
            if(builder==null)return fail(source,
                    "Recruit a resident and use /civcraft camp job assign BUILDER first.");

            ConstructionOrder order=ConstructionOrder.issue(camp,builder,type,
                    x,y,z,rotation,player.level().getGameTime());
            orders.add(order);
            source.sendSuccess(()->Component.literal("Builder assigned to "+type+
                    " at "+origin.toShortString()+". Supply blocks in nearby chests; "+
                    "the worker moves and consumes one item for each physical block."),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Only players can issue building orders.");
        }catch(IllegalStateException e) {return fail(source,e.getMessage());}
    }

    private static int list(CommandSourceStack source) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=CampSavedData.get(source.getServer())
                    .ownedBy(player.getUUID()).orElse(null);
            if(camp==null)return fail(source,"No owned camp.");
            for(ConstructionOrder order:ConstructionSavedData.get(source.getServer()).atCamp(camp)) {
                var check=FunctionalBuildingTemplates.inspect(player.level(),
                        new BlockPos(order.x(),order.y(),order.z()),
                        order.type(),order.rotation());
                source.sendSuccess(()->Component.literal(order.type()+" "+
                        check.matched()+"/"+check.total()+" built, builder "+
                        order.citizenId()),false);
            }
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Only players can inspect orders.");
        }
    }

    private static int fail(CommandSourceStack s,String message) {
        s.sendFailure(Component.literal(message));return 0;
    }
}

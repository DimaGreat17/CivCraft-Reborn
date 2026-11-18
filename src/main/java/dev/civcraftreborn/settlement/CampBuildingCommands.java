package dev.civcraftreborn.settlement;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Complete building acceptance; standing on the north-west floor-origin at
 * block Y (foundation is Y-1), the player checks the real structure.
 * Registration does not auto-place any blocks or grant free bonuses.
 */
public final class CampBuildingCommands {
    private CampBuildingCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("building")
                .then(Commands.literal("list").executes(c -> list(c.getSource())))
                .then(Commands.literal("register")
                        .then(Commands.argument("type",StringArgumentType.word())
                                .then(Commands.argument("rotation",IntegerArgumentType.integer(0,3))
                                        .executes(c -> register(c.getSource(),
                                                StringArgumentType.getString(c,"type"),
                                                IntegerArgumentType.getInteger(c,"rotation"),null))
                                        .then(Commands.argument("x",IntegerArgumentType.integer())
                                                .then(Commands.argument("y",IntegerArgumentType.integer())
                                                        .then(Commands.argument("z",IntegerArgumentType.integer())
                                                                .executes(c->register(c.getSource(),
                                                                        StringArgumentType.getString(c,"type"),
                                                                        IntegerArgumentType.getInteger(c,"rotation"),
                                                                        new BlockPos(IntegerArgumentType.getInteger(c,"x"),
                                                                                IntegerArgumentType.getInteger(c,"y"),
                                                                                IntegerArgumentType.getInteger(c,"z"))))))))));
    }

    private static int register(CommandSourceStack source,String requested,int rotation,
            BlockPos requestedOrigin) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=CampSavedData.get(source.getServer())
                    .ownedBy(player.getUUID()).orElse(null);
            if(camp==null) return fail(source,"First found your camp.");
            if(!player.level().dimension().identifier().toString().equals(camp.dimension()))
                return fail(source,"Building must be registered in its camp dimension.");

            FunctionalBuildingTemplates.Type type;
            try { type=FunctionalBuildingTemplates.Type.valueOf(requested.toUpperCase(java.util.Locale.ROOT)); }
            catch(IllegalArgumentException e) { return fail(source,"Unknown building type."); }

            if(!BlueprintAccess.allowed(type.name(),camp.stage()))
                return fail(source,"Building type is locked at the current camp stage.");

            BlockPos origin=requestedOrigin==null?player.blockPosition():requestedOrigin;
            if(player.blockPosition().distSqr(origin)>24*24)
                return fail(source,"You must be present near the physical construction site.");
            if(Math.abs(origin.getX()-camp.x())>CampRules.CAMP_RADIUS||
                    Math.abs(origin.getZ()-camp.z())>CampRules.CAMP_RADIUS)
                return fail(source,"Stand at the building floor origin within camp bounds.");

            var cells=FunctionalBuildingTemplates.cells(type);
            int width=cells.stream().mapToInt(FunctionalBuildingTemplates.Cell::x).max().orElse(0)+1;
            int depth=cells.stream().mapToInt(FunctionalBuildingTemplates.Cell::z).max().orElse(0)+1;
            for(var cell:cells) {
                var relative=BlueprintTransform.rotate(cell.x(),cell.z(),width,depth,rotation);
                BlockPos pos=origin.offset(relative.x(),cell.y(),relative.z());
                if(Math.abs(pos.getX()-camp.x())>CampRules.CAMP_RADIUS||
                        Math.abs(pos.getZ()-camp.z())>CampRules.CAMP_RADIUS||
                        !player.level().getWorldBorder().isWithinBounds(pos))
                    return fail(source,"Structure crosses the camp or world boundary.");
            }

            var check=FunctionalBuildingTemplates.inspect(player.level(),origin,type,rotation);
            if(!check.complete()) {
                return fail(source,"Incomplete/unloaded building: "+check.matched()+"/"+
                        check.total()+" physically verified cells.");
            }
            BuildingRecord record=BuildingRecord.create(camp,type,origin.getX(),
                    origin.getY(),origin.getZ(),rotation);
            BuildingSavedData.get(source.getServer()).add(record);
            source.sendSuccess(()->Component.literal("Registered fully built "+type+
                    " at "+origin.toShortString()+" ID "+record.id()),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Only players can register buildings.");
        }catch(IllegalStateException e) {return fail(source,e.getMessage());}
    }

    private static int list(CommandSourceStack source) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=CampSavedData.get(source.getServer())
                    .ownedBy(player.getUUID()).orElse(null);
            if(camp==null)return fail(source,"No owned camp.");
            int count=0;
            for(BuildingRecord record:BuildingSavedData.get(source.getServer()).atCamp(camp)) {
                if(!record.dimension().equals(player.level().dimension().identifier().toString()))
                    continue;
                var check=FunctionalBuildingTemplates.inspect(player.level(),
                        new BlockPos(record.x(),record.y(),record.z()),
                        record.type(),record.rotation());
                source.sendSuccess(()->Component.literal(record.type()+" "+record.id()+
                        ": "+check.matched()+"/"+check.total()+
                        (check.complete()?" WORKING":" DAMAGED/UNLOADED")),false);
                count++;
            }
            if(count==0)source.sendSuccess(()->Component.literal(
                    "No registered full structures. Stand at a building's floor origin and use /civcraft camp building register STORAGE 0"),false);
            return count;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Only camp owners can list their buildings.");
        }
    }
    private static int fail(CommandSourceStack s,String text) {
        s.sendFailure(Component.literal(text));return 0;
    }
}

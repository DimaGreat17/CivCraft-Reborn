package dev.civcraftreborn.settlement;

import java.util.Locale;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Owner-controlled, server-owned work assignment. Builders require a real,
 * previously recruited villager. No command can spawn resources or citizens.
 */
public final class CampWorkCommands {
    private CampWorkCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("work")
                .then(Commands.literal("status").executes(c -> status(c.getSource())))
                .then(Commands.literal("build")
                        .then(Commands.argument("citizen_id",StringArgumentType.word())
                                .then(Commands.argument("type",StringArgumentType.word())
                                        .then(Commands.argument("rotation",IntegerArgumentType.integer(0,3))
                                                .executes(c -> build(c.getSource(),
                                                        StringArgumentType.getString(c,"citizen_id"),
                                                        StringArgumentType.getString(c,"type"),
                                                        IntegerArgumentType.getInteger(c,"rotation"))))))
                .then(Commands.literal("assign")
                        .then(Commands.argument("citizen_id",StringArgumentType.word())
                                .then(Commands.argument("profession",StringArgumentType.word())
                                        .then(Commands.argument("building_id",StringArgumentType.word())
                                                .executes(c -> assign(c.getSource(),
                                                        StringArgumentType.getString(c,"citizen_id"),
                                                        StringArgumentType.getString(c,"profession"),
                                                        StringArgumentType.getString(c,"building_id"))))))
                .then(Commands.literal("unassign")
                        .then(Commands.argument("citizen_id",StringArgumentType.word())
                                .executes(c -> unassign(c.getSource(),
                                        StringArgumentType.getString(c,"citizen_id")))));
    }

    private static CampRecord owned(CommandSourceStack source,ServerPlayer player) {
        return CampSavedData.get(source.getServer()).ownedBy(player.getUUID()).orElse(null);
    }
    private static CitizenRecord ownedCitizen(CommandSourceStack source,
            CampRecord camp,String citizenId) {
        return CitizenSavedData.get(source.getServer()).atCamp(camp).stream()
                .filter(c->c.id().equals(citizenId)).findFirst().orElse(null);
    }

    private static int build(CommandSourceStack source,String citizenId,String raw,int rotation) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=owned(source,player);
            if(camp==null)return fail(source,"Found a camp first.");
            if(!player.level().dimension().identifier().toString().equals(camp.dimension()))
                return fail(source,"Visit your camp before issuing a building order.");
            CitizenRecord citizen=ownedCitizen(source,camp,citizenId);
            if(citizen==null)return fail(source,"Citizen must be recruited into this camp.");
            FunctionalBuildingTemplates.Type type;
            try {type=FunctionalBuildingTemplates.Type.valueOf(raw.toUpperCase(Locale.ROOT));}
            catch(IllegalArgumentException e){return fail(source,"Unknown starter building type.");}
            if(!BlueprintAccess.allowed(type.name(),camp.stage()))
                return fail(source,"Building is locked.");
            BlockPos origin=player.blockPosition();
            if(Math.abs(origin.getX()-camp.x())>CampRules.CAMP_RADIUS||
                    Math.abs(origin.getZ()-camp.z())>CampRules.CAMP_RADIUS)
                return fail(source,"Stand at the construction floor origin within your camp.");
            int width=FunctionalBuildingTemplates.cells(type).stream()
                    .mapToInt(FunctionalBuildingTemplates.Cell::x).max().orElse(0)+1;
            int depth=FunctionalBuildingTemplates.cells(type).stream()
                    .mapToInt(FunctionalBuildingTemplates.Cell::z).max().orElse(0)+1;
            for(var cell:FunctionalBuildingTemplates.cells(type)) {
                var rotated=BlueprintTransform.rotate(cell.x(),cell.z(),width,depth,rotation);
                BlockPos target=origin.offset(rotated.x(),cell.y(),rotated.z());
                if(Math.abs(target.getX()-camp.x())>CampRules.CAMP_RADIUS||
                        Math.abs(target.getZ()-camp.z())>CampRules.CAMP_RADIUS||
                        !player.level().getWorldBorder().isWithinBounds(target))
                    return fail(source,"Building footprint must fit inside camp boundaries.");
            }

            if(CitizenJobSavedData.get(source.getServer()).forCitizen(citizenId).isPresent())
                return fail(source,"Citizen is already assigned to another job. Unassign first.");
            ConstructionOrder order=ConstructionOrder.issue(camp,citizen,type,
                    origin.getX(),origin.getY(),origin.getZ(),rotation,
                    player.level().getGameTime());
            ConstructionOrderSavedData.get(source.getServer()).add(order);
            CitizenJobSavedData.get(source.getServer()).assign(
                    new CitizenJobAssignment(citizenId,CitizenJob.BUILDER,order.id()));
            source.sendSuccess(()->Component.literal("Construction order "+order.id()+
                    " issued for "+type+". Builder will use real items from nearby containers; "+
                    "special blocks such as beds and farmland need manual placement."),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Player only command.");
        }catch(IllegalStateException e){return fail(source,e.getMessage());}
    }

    private static int assign(CommandSourceStack source,String citizenId,String raw,String buildingId) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=owned(source,player);
            if(camp==null)return fail(source,"Found your camp first.");
            CitizenRecord citizen=ownedCitizen(source,camp,citizenId);
            if(citizen==null)return fail(source,"Citizen not registered in this camp.");
            CitizenJob job;
            try { job=CitizenJob.valueOf(raw.toUpperCase(Locale.ROOT)); }
            catch(IllegalArgumentException e){return fail(source,"Unsupported profession.");}
            if(job==CitizenJob.UNEMPLOYED||job==CitizenJob.BUILDER)
                return fail(source,"Use work unassign or work build for this profession.");
            BuildingRecord target=BuildingSavedData.get(source.getServer()).atCamp(camp).stream()
                    .filter(b->b.id().equals(buildingId)).findFirst().orElse(null);
            if(target==null||!job.supports(target.type()))
                return fail(source,"No matching completed building for this profession.");
            var level=player.level();
            if(!level.dimension().identifier().toString().equals(target.dimension()))
                return fail(source,"Visit the correct building dimension.");
            if(!FunctionalBuildingTemplates.inspect(level,
                    new BlockPos(target.x(),target.y(),target.z()),target.type(),
                    target.rotation()).complete())
                return fail(source,"The building is incomplete, unloaded or damaged.");
            CitizenJobSavedData.get(source.getServer()).assign(
                    new CitizenJobAssignment(citizen.id(),job,target.id()));
            source.sendSuccess(()->Component.literal("Citizen "+citizen.id()+
                    " assigned as "+job+" to building "+target.id()),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e){
            return fail(source,"Player only command.");
        }
    }

    private static int unassign(CommandSourceStack source,String citizenId) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=owned(source,player);
            if(camp==null||ownedCitizen(source,camp,citizenId)==null)
                return fail(source,"Not an owned camp citizen.");
            CitizenJobSavedData.get(source.getServer()).unassign(citizenId);
            source.sendSuccess(()->Component.literal("Work assignment cleared."),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e){
            return fail(source,"Player only command.");
        }
    }

    private static int status(CommandSourceStack source) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=owned(source,player);
            if(camp==null)return fail(source,"No owned camp.");
            var jobs=CitizenJobSavedData.get(source.getServer());
            for(CitizenRecord citizen:CitizenSavedData.get(source.getServer()).atCamp(camp)) {
                String line=jobs.forCitizen(citizen.id())
                        .map(a->a.role()+" @ "+a.targetId()).orElse("UNEMPLOYED");
                source.sendSuccess(()->Component.literal(citizen.id()+" = "+line),false);
            }
            for(ConstructionOrder order:ConstructionOrderSavedData.get(source.getServer()).atCamp(camp)){
                source.sendSuccess(()->Component.literal("BUILD "+order.id()+" "+order.type()+
                        " builder="+order.citizenId()),false);
            }
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e){
            return fail(source,"Player only command.");
        }
    }
    private static int fail(CommandSourceStack source,String msg){
        source.sendFailure(Component.literal(msg));return 0;
    }
}

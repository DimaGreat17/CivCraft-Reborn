package dev.civcraftreborn.settlement;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.phys.AABB;

/**
 * Assign nearest REAL, previously recruited villager to a physically verified
 * building. A reassignment is saved and the vanilla profession is updated.
 * The NPC remains a Minecraft entity, never a spawned virtual placeholder.
 */
public final class CampResidentJobCommands {
    private CampResidentJobCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("job")
                .then(Commands.literal("list").executes(c -> list(c.getSource())))
                .then(Commands.literal("assign")
                        .then(Commands.argument("role", StringArgumentType.word())
                                .executes(c -> assign(c.getSource(),
                                        StringArgumentType.getString(c,"role")))));
    }

    private static int assign(CommandSourceStack source,String name) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=CampSavedData.get(source.getServer())
                    .ownedBy(player.getUUID()).orElse(null);
            if(camp==null)return fail(source,"Found a camp first.");
            if(!player.level().dimension().identifier().toString().equals(camp.dimension()))
                return fail(source,"You must be in your camp.");
            if(player.blockPosition().distSqr(new BlockPos(camp.x(),camp.y(),camp.z()))>24*24)
                return fail(source,"Return to your camp to assign residents.");

            ResidentWorkRole role;
            try {role=ResidentWorkRole.valueOf(name.toUpperCase(java.util.Locale.ROOT));}
            catch(IllegalArgumentException e) {
                return fail(source,"Supported roles: BUILDER FARMER CRAFTSMAN STOREKEEPER WATCHMAN WATER_CARRIER");
            }
            ResidentWorkSavedData jobs=ResidentWorkSavedData.get(source.getServer());
            var residents=CitizenSavedData.get(source.getServer()).atCamp(camp);
            var candidates=player.level().getEntitiesOfClass(Villager.class,
                    new AABB(player.blockPosition()).inflate(8.0),
                    villager->villager.isAlive()&&residents.stream().anyMatch(
                            r->r.entityUuid().equals(villager.getUUID().toString())));
            if(candidates.isEmpty())return fail(source,"No recruited, living villager within 8 blocks.");

            Villager villager=candidates.getFirst();
            CitizenRecord citizen=residents.stream()
                    .filter(r->r.entityUuid().equals(villager.getUUID().toString()))
                    .findFirst().orElseThrow();

            BuildingRecord facility=null;
            if(role!=ResidentWorkRole.BUILDER) {
                for(BuildingRecord record:BuildingSavedData.get(source.getServer()).atCamp(camp)) {
                    if(!role.compatible(record.type()))continue;
                    if(!record.dimension().equals(camp.dimension()))continue;
                    if(!FunctionalBuildingTemplates.inspect(player.level(),
                            new BlockPos(record.x(),record.y(),record.z()),
                            record.type(),record.rotation()).complete())continue;
                    boolean occupied=jobs.all().stream().anyMatch(a->
                            a.buildingId().equals(record.id())&&!a.citizenId().equals(citizen.id()));
                    if(occupied)continue;
                    facility=record;
                    break;
                }
                if(facility==null)return fail(source,
                        "No free, fully completed, registered building for "+role+".");
            }

            jobs.assign(new ResidentAssignment(citizen.id(),
                    facility==null?"":facility.id(),role));

            // Match vanilla presentation and POI-based job behavior to assigned
            // Minecraft occupation; builder and warehouse steward are CivCraft roles.
            if(role==ResidentWorkRole.FARMER) {
                villager.setVillagerData(villager.getVillagerData().withProfession(
                        player.level().registryAccess(),VillagerProfession.FARMER));
            }else if(role==ResidentWorkRole.CRAFTSMAN) {
                villager.setVillagerData(villager.getVillagerData().withProfession(
                        player.level().registryAccess(),VillagerProfession.TOOLSMITH));
            }else if(role==ResidentWorkRole.WATCHMAN) {
                villager.setVillagerData(villager.getVillagerData().withProfession(
                        player.level().registryAccess(),VillagerProfession.WEAPONSMITH));
            }else if(role==ResidentWorkRole.WATER_CARRIER) {
                villager.setVillagerData(villager.getVillagerData().withProfession(
                        player.level().registryAccess(),VillagerProfession.FISHERMAN));
            }
            if(facility!=null) {
                villager.getNavigation().moveTo(facility.x()+2.5,facility.y(),
                        facility.z()+2.5,0.75);
            }

            String buildingId=facility==null?"construction orders":facility.id();
            source.sendSuccess(()->Component.literal("Assigned "+role+" to citizen "+citizen.id()+
                    " (workplace "+buildingId+")."),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e){
            return fail(source,"A player is required.");
        }
    }

    private static int list(CommandSourceStack source) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=CampSavedData.get(source.getServer())
                    .ownedBy(player.getUUID()).orElse(null);
            if(camp==null)return fail(source,"No owned camp.");
            var people=CitizenSavedData.get(source.getServer()).atCamp(camp);
            var jobs=ResidentWorkSavedData.get(source.getServer());
            for(CitizenRecord citizen:people) {
                var assignment=jobs.byCitizen(citizen.id());
                source.sendSuccess(()->Component.literal("Citizen "+citizen.id()+": "+
                        assignment.map(a->a.role()+" @ "+a.buildingId()).orElse("UNASSIGNED")),false);
            }
            return people.size();
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e){
            return fail(source,"A player is required.");
        }
    }

    private static int fail(CommandSourceStack source,String message) {
        source.sendFailure(Component.literal(message));return 0;
    }
}

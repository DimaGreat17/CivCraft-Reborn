package dev.civcraftreborn.settlement;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import dev.civcraftreborn.foundation.WorldSettingsSavedData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Authoritative construction-site positioning. Pending placement requires a real
 * foundation block. Confirmed construction is independent of the starter. The foundation only authorizes starting a project. The finished building may
 * stand separately at any valid loaded site in a bounded 128-block preview area.
 */
public final class CampHeartCommands {
    private CampHeartCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("heart")
                .then(Commands.literal("inspect").executes(c->inspect(c.getSource())))
                .then(Commands.literal("confirm").executes(c->confirm(c.getSource())))
                .then(Commands.literal("move")
                        .then(Commands.argument("x",IntegerArgumentType.integer(-128,128))
                        .then(Commands.argument("z",IntegerArgumentType.integer(-128,128))
                        .executes(c->change(c.getSource(),
                                IntegerArgumentType.getInteger(c,"x"),
                                IntegerArgumentType.getInteger(c,"z"),null)))))
                .then(Commands.literal("rotate")
                        .then(Commands.argument("turns",IntegerArgumentType.integer(0,3))
                        .executes(c->change(c.getSource(),null,null,
                                IntegerArgumentType.getInteger(c,"turns")))));
    }

    private static CampHeartSite current(ServerPlayer player){
        String dim=player.level().dimension().identifier().toString();
        return CampHeartSavedData.get(player.level().getServer())
                .ownedBy(player.getUUID(),dim).stream()
                .filter(s -> CampSavedData.get(player.level().getServer())
                        .ownedBy(player.getUUID()).isEmpty()
                        || CampSavedData.get(player.level().getServer())
                        .ownedBy(player.getUUID())
                        .map(c -> c.x()==CampHeartTemplate.origin(s.core(),s.shiftX(),s.shiftZ()).getX()+3
                            && c.z()==CampHeartTemplate.origin(s.core(),s.shiftX(),s.shiftZ()).getZ()+3)
                        .orElse(false))
                .findFirst().orElse(null);
    }

    private static boolean validWorld(ServerPlayer player){
        return WorldSettingsSavedData.get(player.level().getServer()).settings().civcraftWorld();
    }

    private static boolean clear(ServerLevel level,CampHeartSite site){
        BlockPos core=site.core();
        if(!level.hasChunkAt(core)||
                !level.getBlockState(core).is(CampBlocks.CAMP_CORE))return false;
        for(var cell:CampHeartTemplate.cells()){
            BlockPos pos=CampHeartTemplate.position(core,site.shiftX(),site.shiftZ(),
                    site.turns(),cell);
            if(!level.hasChunkAt(pos)||!level.getWorldBorder().isWithinBounds(pos))
                return false;
            var state=level.getBlockState(pos);
            var expected=CampHeartTemplate.expected(core,site.shiftX(),
                    site.shiftZ(),site.turns(),cell);
            if(!CampHeartTemplate.matching(state,expected)&&!state.isAir())return false;
            // The starter foundation must never be swallowed into the building.
            if(pos.equals(core))return false;
            if(cell.y()==0&&!level.getBlockState(pos.below()).isSolid())return false;
        }
        return true;
    }

    private static int change(CommandSourceStack source,Integer dx,Integer dz,Integer rotation){
        try {
            ServerPlayer player=source.getPlayerOrException();
            if(!validWorld(player))return fail(source,"Not a CivCraft-enabled world");
            CampHeartSite old=current(player);
            if(old==null)return fail(source,"Place your Camp Heart foundation first");
            if(old.confirmed())return fail(source,"Construction position is already confirmed");
            int x=dx==null?old.shiftX():dx;
            int z=dz==null?old.shiftZ():dz;
            int turns=rotation==null?old.turns():rotation;
            CampHeartSite updated=old.withPlacement(x,z,turns);
            if(!clear((ServerLevel)player.level(),updated)) {
                sync(player,old);
                return fail(source,"Blocked, unsupported or unloaded footprint; approach the proposed site and clear/level it");
            }
            CampHeartSavedData.get(source.getServer()).replace(old,updated);
            sync(player,updated);
            source.sendSuccess(()->Component.literal("Camp Heart preview: shift ("
                    +x+","+z+"), rotation "+(turns*90)+" degrees; not confirmed"),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Players only");
        }
    }

    private static int confirm(CommandSourceStack source) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            if(!validWorld(player))return fail(source,"Not a CivCraft-enabled world");
            CampHeartSite site=current(player);
            if(site==null)return fail(source,"No active Camp Heart building project");
            if(site.confirmed())return fail(source,"Already confirmed");
            if(!clear((ServerLevel)player.level(),site))
                return fail(source,"Cannot confirm: footprint is obstructed/unsupported/unloaded");
            String dim=site.dimension();
            BlockPos center=CampHeartTemplate.origin(site.core(),site.shiftX(),site.shiftZ()).offset(3,0,3);
            if(!CampRules.canFoundAt(CampSavedData.get(source.getServer()).all(),dim,
                    center.getX(),center.getZ(),true,true))
                return fail(source,"Too close to another settlement");
            CampHeartSavedData.get(source.getServer()).replace(site,site.confirm());
            // Construction starter is consumed once its task is confirmed.
            // The future building neither contains nor depends on this block.
            player.level().removeBlock(site.core(),false);
            sync(player,site.confirm());
            source.sendSuccess(()->Component.literal(
                    "Camp Heart construction site confirmed. Build the whole hologram manually; "
                    +"check /civcraft camp heart inspect and then found the camp."),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Players only");
        }
    }

    private static int inspect(CommandSourceStack source) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampHeartSite site=current(player);
            if(site==null)return fail(source,"Place a Camp Heart foundation nearby");
            if(!site.confirmed()&&(!player.level().hasChunkAt(site.core())||
                    !player.level().getBlockState(site.core()).is(CampBlocks.CAMP_CORE)))
                return fail(source,"Camp Heart starter foundation is missing");
            var progress=CampHeartTemplate.inspect((ServerLevel)player.level(),
                    site.core(),site.shiftX(),site.shiftZ(),site.turns());
            source.sendSuccess(()->Component.literal("Camp Heart: "+
                    progress.matched()+"/"+progress.total()+" required block positions, "
                    +(site.confirmed()?"site confirmed":"position not confirmed")+
                    (progress.loaded()?"":" (chunks not loaded)")+
                    ". Missing cells must be built manually."),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Players only");
        }
    }

    private static void sync(ServerPlayer player,CampHeartSite site){
        ServerPlayNetworking.send(player,new HeartPlacementResponse(site.core(),
                site.shiftX(),site.shiftZ(),site.turns(),site.confirmed()));
    }

    private static int fail(CommandSourceStack source,String reason){
        source.sendFailure(Component.literal(reason));
        return 0;
    }
}

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
                                IntegerArgumentType.getInteger(c,"x"),null,
                                IntegerArgumentType.getInteger(c,"z"),null)))))
                .then(Commands.literal("height")
                        .then(Commands.argument("y",IntegerArgumentType.integer(-64,64))
                        .executes(c->change(c.getSource(),null,
                                IntegerArgumentType.getInteger(c,"y"),null,null))))
                .then(Commands.literal("rotate")
                        .then(Commands.argument("turns",IntegerArgumentType.integer(0,3))
                        .executes(c->change(c.getSource(),null,null,null,
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
                        .map(c -> c.x()==CampHeartTemplate.origin(s.core(),s.shiftX(),s.shiftY(),s.shiftZ()).getX()+3
                            && c.z()==CampHeartTemplate.origin(s.core(),s.shiftX(),s.shiftY(),s.shiftZ()).getZ()+3)
                        .orElse(false))
                .findFirst().orElse(null);
    }

    private static boolean validWorld(ServerPlayer player){
        return WorldSettingsSavedData.get(player.level().getServer()).settings().civcraftWorld();
    }

    /**
     * A construction plan may intentionally overlap ordinary terrain, trees or
     * other mismatched blocks. They must be removed/replaced DURING building,
     * never silently destroyed at preview/confirmation time.
     *
     * Only objective constraints can reject a location: the starter still
     * exists, no overlap with the starter itself, all plan cells are loaded
     * and inside the world border. Settlement separation is checked on confirm.
     */
    private static boolean siteAvailable(ServerLevel level,CampHeartSite site) {
        BlockPos foundation=site.core();
        if(!level.hasChunkAt(foundation)
                || !level.getBlockState(foundation).is(CampBlocks.CAMP_CORE))return false;
        for(var cell:CampHeartTemplate.cells()) {
            BlockPos pos=CampHeartTemplate.position(foundation,site.shiftX(),
                    site.shiftY(),site.shiftZ(),site.turns(),cell);
            if(level.isOutsideBuildHeight(pos)
                    || !level.hasChunkAt(pos)
                    || !level.getWorldBorder().isWithinBounds(pos)
                    || pos.equals(foundation))return false;
        }
        return true;
    }

    private static int change(CommandSourceStack source,Integer dx,Integer dy,Integer dz,Integer rotation){
        try {
            ServerPlayer player=source.getPlayerOrException();
            if(!validWorld(player))return fail(source,"Not a CivCraft-enabled world");
            CampHeartSite old=current(player);
            if(old==null)return fail(source,"Place your Camp Heart foundation first");
            if(old.confirmed())return fail(source,"Construction position is already confirmed");
            int x=dx==null?old.shiftX():dx;
            int y=dy==null?old.shiftY():dy;
            int z=dz==null?old.shiftZ():dz;
            int turns=rotation==null?old.turns():rotation;
            CampHeartSite updated=old.withPlacement(x,y,z,turns);
            if(!siteAvailable((ServerLevel)player.level(),updated)) {
                sync(player,old);
                return fail(source,"Site outside loaded chunks, world border or overlapping the starter; approach the site");
            }
            CampHeartSavedData.get(source.getServer()).replace(old,updated);
            sync(player,updated);
            source.sendSuccess(()->Component.literal("Camp Heart preview: shift ("
                    +x+","+y+","+z+"), rotation "+(turns*90)+" degrees; not confirmed"),false);
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
            if(!siteAvailable((ServerLevel)player.level(),site))
                return fail(source,"Cannot confirm: site is unloaded, outside world border or overlaps starter");
            String dim=site.dimension();
            BlockPos center=CampHeartTemplate.origin(site.core(),site.shiftX(),site.shiftY(),site.shiftZ()).offset(3,0,3);
            if(!CampRules.canFoundAt(CampSavedData.get(source.getServer()).all(),dim,
                    center.getX(),center.getZ(),true,true))
                return fail(source,"Too close to another settlement");
            CampHeartSavedData.get(source.getServer()).replace(site,site.confirm());
            // Construction starter is consumed once its task is confirmed.
            // The future building neither contains nor depends on this block.
            player.level().removeBlock(site.core(),false);
            sync(player,site.confirm());
            source.sendSuccess(()->Component.literal(
                    "Camp Heart site confirmed. Existing blocks are NOT removed: clear or "
                    +"replace obstacles as you build until all 162 blocks match. "
                    +"Check /civcraft camp heart inspect before founding."),false);
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
                    site.core(),site.shiftX(),site.shiftY(),site.shiftZ(),site.turns());
            source.sendSuccess(()->Component.literal("Camp Heart: "+
                    progress.matched()+"/"+progress.total()+" blocks; "+
                    (site.confirmed()?(progress.complete()
                            ?"FINISHED — ready to found":"CONSTRUCTION INCOMPLETE")
                            :"POSITION NOT CONFIRMED")+
                    (progress.loaded()?"":"; some chunks are NOT loaded")),false);
            if(progress.complete() && site.confirmed()) {
                source.sendSuccess(()->Component.literal(
                        "READY! The structure is complete. Open CivCraft -> Основать лагерь "
                        +"or use /civcraft camp found to register your camp."),false);
            } else {
                var missing=CampHeartTemplate.firstMissing((ServerLevel)player.level(),
                        site.core(),site.shiftX(),site.shiftY(),site.shiftZ(),site.turns(),8);
                if(!missing.isEmpty()) {
                    source.sendSuccess(()->Component.literal(
                            "First "+missing.size()+" mismatched block positions (world coordinates):"),false);
                    for(var entry:missing) {
                        source.sendSuccess(()->Component.literal(
                                entry.pos().toShortString()+" expected="+
                                entry.expected().toString()+" actual="+entry.actual().toString()),false);
                    }
                }
                if(!site.confirmed())
                    source.sendSuccess(()->Component.literal(
                            "Confirm the blueprint site before founding your camp."),false);
            }
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Players only");
        }
    }

    private static void sync(ServerPlayer player,CampHeartSite site){
        ServerPlayNetworking.send(player,new HeartPlacementResponse(site.core(),
                site.shiftX(),site.shiftY(),site.shiftZ(),site.turns(),site.confirmed()));
    }

    private static int fail(CommandSourceStack source,String reason){
        source.sendFailure(Component.literal(reason));
        return 0;
    }
}

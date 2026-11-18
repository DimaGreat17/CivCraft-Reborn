package dev.civcraftreborn.settlement;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

/**
 * Opt-in physical immigration for a remote camp that has no vanilla village
 * nearby. A migrant is an actual persistent world villager, arriving from the
 * edge of the camp, not a free virtual population entry.
 *
 * Each invitation costs 12 *real* bread and needs an unused *real* bed and
 * server-verified room capacity. No permanent housing => no migrant.
 */
public final class CampMigrationCommands {
    private static final int INVITATION_BREAD=12;
    private CampMigrationCommands() {}

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("invite").executes(c -> invite(c.getSource()));
    }

    private static int invite(CommandSourceStack source) {
        try {
            ServerPlayer player=source.getPlayerOrException();
            CampRecord camp=CampSavedData.get(source.getServer())
                    .ownedBy(player.getUUID()).orElse(null);
            if(camp==null)return fail(source,"Create a physical camp before inviting migrants.");
            if(!player.level().dimension().identifier().toString().equals(camp.dimension()))
                return fail(source,"Invite migrants while standing in your own camp.");
            BlockPos center=new BlockPos(camp.x(),camp.y(),camp.z());
            if(player.blockPosition().distSqr(center)>24*24)
                return fail(source,"Return to your camp's center to invite residents.");
            var citizens=CitizenSavedData.get(source.getServer());
            var shelter=HousingScanner.scan(player.level(),camp);
            if(!shelter.complete()||shelter.capacity()<=citizens.atCamp(camp).size())
                return fail(source,"Need one new fully verified sheltered bed/room slot.");

            BlockPos bed=HousingScanner.verifiedBedHeads(player.level(),camp).stream()
                    .filter(b->!citizens.claimedBed(camp,b.getX(),b.getY(),b.getZ()))
                    .findFirst().orElse(null);
            if(bed==null)return fail(source,"No free real bed for the migrant.");

            int bread=0;
            var inv=player.getInventory();
            for(int slot=0;slot<inv.getContainerSize();slot++)
                if(inv.getItem(slot).is(Items.BREAD))bread+=inv.getItem(slot).getCount();
            if(bread<INVITATION_BREAD)
                return fail(source,"Inviting a migrant requires 12 bread (food & travel supplies).");

            BlockPos entry=null;
            for(int dx:new int[]{12,-12,0,0}) {
                int dz=dx==0?(entry==null?12:-12):0;
                for(int dy=-3;dy<=3;dy++) {
                    BlockPos candidate=center.offset(dx,dy,dz);
                    if(!player.level().hasChunkAt(candidate)||!player.level().getWorldBorder()
                            .isWithinBounds(candidate))continue;
                    if(player.level().isEmptyBlock(candidate)
                            &&player.level().isEmptyBlock(candidate.above())
                            &&player.level().getBlockState(candidate.below()).isSolid()) {
                        entry=candidate;
                        break;
                    }
                }
                if(entry!=null)break;
            }
            if(entry==null)return fail(source,"No loaded, unobstructed ground for migrant arrival.");

            Villager villager=EntityType.VILLAGER.create(player.level(),EntitySpawnReason.EVENT);
            if(villager==null)return fail(source,"Could not create migrant entity.");
            villager.moveTo(entry.getX()+0.5,entry.getY(),entry.getZ()+0.5,0.0f,0.0f);
            villager.setPersistenceRequired();
            if(!player.level().addFreshEntity(villager))
                return fail(source,"Migrant arrival was rejected by the world.");

            // Commit real payment ONLY after successful physical entity arrival.
            int needed=INVITATION_BREAD;
            for(int i=0;i<inv.getContainerSize()&&needed>0;i++) {
                ItemStack stack=inv.getItem(i);
                if(!stack.is(Items.BREAD))continue;
                int amount=Math.min(needed,stack.getCount());
                stack.shrink(amount);needed-=amount;
            }
            inv.setChanged();
            CitizenRecord citizen=CitizenRecord.recruit(villager.getUUID(),camp,
                    bed.getX(),bed.getY(),bed.getZ(),player.level().getGameTime());
            citizens.add(citizen);
            villager.getNavigation().moveTo(bed.getX()+0.5,bed.getY()+1,
                    bed.getZ()+0.5,0.7);

            source.sendSuccess(()->Component.literal(
                    "A real migrant arrived at your camp for 12 bread: "+citizen.id()+
                    ". Assign a profession via the resident management screen."),false);
            return 1;
        }catch(com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return fail(source,"Only a camp owner may invite migrants.");
        }
    }

    private static int fail(CommandSourceStack source,String reason) {
        source.sendFailure(Component.literal(reason));return 0;
    }
}

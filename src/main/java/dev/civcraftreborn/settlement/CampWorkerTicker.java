package dev.civcraftreborn.settlement;

import java.util.List;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * Real, loaded-only NPC work. No forced chunk loading, no virtual materials,
 * no instant free buildings. One placement attempt per work order per 40 ticks.
 *
 * In this first slice builders use physically stored vanilla block items in
 * containers near the Camp Core, physically travel to the worksite and place
 * only safe standalone blocks. Multipart/shape-dependent/terrain blocks still
 * need human help. An incomplete or damaged building has no facility effect.
 */
public final class CampWorkerTicker {
    private CampWorkerTicker() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CampWorkerTicker::tick);
    }

    private static void tick(MinecraftServer server) {
        if(server.getTickCount()%40!=0)return;
        var orders=ConstructionOrderSavedData.get(server);
        var jobs=CitizenJobSavedData.get(server);
        var citizens=CitizenSavedData.get(server);
        var camps=CampSavedData.get(server);

        int processed=0;
        for(ConstructionOrder order:orders.all()) {
            if(++processed>12) break; // bounded world-tick budget
            CampRecord camp=camps.all().stream()
                    .filter(c->c.id().equals(order.campId())).findFirst().orElse(null);
            if(camp==null)continue;
            var assignment=jobs.forCitizen(order.citizenId()).orElse(null);
            if(assignment==null||assignment.role()!=CitizenJob.BUILDER
                    ||!assignment.targetId().equals(order.id()))continue;
            CitizenRecord citizen=citizens.atCamp(camp).stream()
                    .filter(c->c.id().equals(order.citizenId())).findFirst().orElse(null);
            if(citizen==null)continue;
            for(ServerLevel level:server.getAllLevels()) {
                if(level.dimension().identifier().toString().equals(order.dimension())) {
                    stepBuilder(level,camp,citizen,order,jobs,orders);
                    break;
                }
            }
        }
    }

    private static void stepBuilder(ServerLevel level,CampRecord camp,CitizenRecord citizen,
            ConstructionOrder order,CitizenJobSavedData jobs,
            ConstructionOrderSavedData orders) {
        if(!level.hasChunkAt(new BlockPos(camp.x(),camp.y(),camp.z())) ||
                !level.getBlockState(new BlockPos(camp.x(),camp.y(),camp.z()))
                        .is(CampBlocks.CAMP_CORE))return;
        var entity=level.getEntity(UUID.fromString(citizen.entityUuid()));
        if(!(entity instanceof Villager villager)||!villager.isAlive())return;
        BlockPos origin=new BlockPos(order.x(),order.y(),order.z());
        var plan=FunctionalBuildingTemplates.cells(order.type());
        int width=plan.stream().mapToInt(FunctionalBuildingTemplates.Cell::x).max().orElse(0)+1;
        int depth=plan.stream().mapToInt(FunctionalBuildingTemplates.Cell::z).max().orElse(0)+1;

        for(var cell:plan) {
            var rotated=BlueprintTransform.rotate(cell.x(),cell.z(),
                    width,depth,order.rotation());
            BlockPos target=origin.offset(rotated.x(),cell.y(),rotated.z());
            if(Math.abs(target.getX()-camp.x())>CampRules.CAMP_RADIUS||
                    Math.abs(target.getZ()-camp.z())>CampRules.CAMP_RADIUS||
                    !level.hasChunkAt(target)||!level.getWorldBorder().isWithinBounds(target))
                return;
            var actual=level.getBlockState(target);
            if(cell.anyBed() ? actual.getBlock() instanceof net.minecraft.world.level.block.BedBlock
                    : actual.is(cell.block()))continue;

            // No ability to modify unrelated structures, overwrite terrain,
            // place special multipart beds, or generate farmland out of air.
            if(cell.anyBed()||cell.block()==Blocks.FARMLAND||
                    cell.block()==Blocks.AIR||!actual.isAir())return;

            Item item=cell.block().asItem();
            if(item==net.minecraft.world.item.Items.AIR)return;
            Container source=findPhysicalStock(level,camp,item);
            if(source==null)return; // lack of real materials suspends work

            if(villager.distanceToSqr(target.getX()+0.5,target.getY()+0.5,
                    target.getZ()+0.5)>3.5*3.5) {
                villager.getNavigation().moveTo(target.getX()+0.5,target.getY()+1,
                        target.getZ()+0.5,0.8);
                return;
            }
            int slot=-1;
            for(int i=0;i<source.getContainerSize();i++) {
                if(source.getItem(i).is(item)){slot=i;break;}
            }
            if(slot<0)return;
            ItemStack stack=source.getItem(slot);
            stack.shrink(1);
            source.setChanged();
            boolean placed=level.setBlock(target,cell.block().defaultBlockState(),3);
            if(!placed) {
                stack.grow(1);source.setChanged();
            }
            return; // exactly one physical placement per cycle
        }
        // Every physical blueprint cell matched, including manually installed
        // bed/farmland. Register no bonus until full verification succeeds.
        var check=FunctionalBuildingTemplates.inspect(level,origin,order.type(),order.rotation());
        if(!check.complete())return;
        var store=BuildingSavedData.get(level.getServer());
        boolean registered=store.atCamp(camp).stream().anyMatch(b->
                b.x()==order.x()&&b.y()==order.y()&&b.z()==order.z());
        if(!registered)store.add(BuildingRecord.create(camp,order.type(),
                order.x(),order.y(),order.z(),order.rotation()));
        orders.finish(order.id());
        jobs.unassign(citizen.id());
    }

    private static Container findPhysicalStock(ServerLevel level,CampRecord camp,Item item) {
        BlockPos center=new BlockPos(camp.x(),camp.y(),camp.z());
        int scanned=0;
        for(BlockPos pos:BlockPos.betweenClosed(center.offset(-10,-2,-10),
                center.offset(10,3,10))) {
            if(++scanned>3000)break;
            if(!level.hasChunkAt(pos))continue;
            if(!(level.getBlockEntity(pos) instanceof Container container))continue;
            for(int slot=0;slot<container.getContainerSize();slot++) {
                if(container.getItem(slot).is(item)&&!container.getItem(slot).isEmpty())
                    return container;
            }
        }
        return null;
    }
}

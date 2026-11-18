package dev.civcraftreborn.settlement;

import java.util.List;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;

/**
 * Physical NPC builder. One missing blueprint cell per 20 ticks maximum.
 * Needs a registered living citizen, a valid camp, nearby loaded chunks,
 * navigable reach, and real materials in a chest/barrel inside that camp.
 *
 * No infinite item generation, chunk force loads, block replacement, or
 * building completion by decree. Missing specialized materials pause work.
 */
public final class CampConstructionSystem {
    private CampConstructionSystem() {}
    private static long tick;
    private static final int STOCK_RADIUS=12;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CampConstructionSystem::tick);
    }

    private static void tick(MinecraftServer server) {
        if(++tick%20!=0)return;
        ConstructionSavedData orders=ConstructionSavedData.get(server);
        for(ConstructionOrder order:orders.all()) {
            for(ServerLevel level:server.getAllLevels()) {
                if(level.dimension().identifier().toString().equals(order.dimension())) {
                    progress(server,level,order,orders);
                    break;
                }
            }
        }
    }

    private static void progress(MinecraftServer server, ServerLevel level,
            ConstructionOrder order,ConstructionSavedData orders) {
        CampRecord camp=CampSavedData.get(server).all().stream()
                .filter(c->c.id().equals(order.campId())
                        &&c.dimension().equals(order.dimension()))
                .findFirst().orElse(null);
        if(camp==null)return;
        CitizenRecord citizen=CitizenSavedData.get(server).atCamp(camp).stream()
                .filter(c->c.id().equals(order.citizenId()))
                .findFirst().orElse(null);
        if(citizen==null)return;
        if(!ResidentWorkSavedData.get(server).byCitizen(citizen.id())
                .map(a->a.role()==ResidentWorkRole.BUILDER).orElse(false))return;

        if(!level.hasChunkAt(new BlockPos(order.x(),order.y(),order.z())))return;
        var entity=level.getEntity(UUID.fromString(citizen.entityUuid()));
        if(!(entity instanceof Villager builder)||!builder.isAlive())return;

        BlockPos origin=new BlockPos(order.x(),order.y(),order.z());
        var check=FunctionalBuildingTemplates.inspect(level,origin,order.type(),order.rotation());
        if(check.complete()) {
            boolean registered=BuildingSavedData.get(server).atCamp(camp).stream()
                    .anyMatch(b->b.x()==order.x()&&b.y()==order.y()&&b.z()==order.z());
            if(!registered) {
                BuildingSavedData.get(server).add(BuildingRecord.create(camp,order.type(),
                        order.x(),order.y(),order.z(),order.rotation()));
            }
            orders.complete(order);
            return;
        }
        if(!check.loaded())return;
        List<FunctionalBuildingTemplates.Cell> cells=FunctionalBuildingTemplates.cells(order.type());
        int w=cells.stream().mapToInt(FunctionalBuildingTemplates.Cell::x).max().orElse(0)+1;
        int d=cells.stream().mapToInt(FunctionalBuildingTemplates.Cell::z).max().orElse(0)+1;

        for(var cell:cells) {
            var rotated=BlueprintTransform.rotate(cell.x(),cell.z(),w,d,order.rotation());
            BlockPos target=origin.offset(rotated.x(),cell.y(),rotated.z());
            if(!level.hasChunkAt(target)
                    ||!level.getWorldBorder().isWithinBounds(target))return;
            var state=level.getBlockState(target);
            if(cell.anyBed()
                    ? state.getBlock() instanceof BedBlock : state.is(cell.block()))continue;

            // Worker does not destroy/replace player blocks. A wrong block is
            // a conflict requiring manual correction, never griefed by AI.
            if(!level.isEmptyBlock(target)
                    && !(cell.block()==Blocks.FARMLAND&&state.is(Blocks.DIRT)))continue;

            if(builder.blockPosition().distSqr(target)>9) {
                builder.getNavigation().moveTo(target.getX()+0.5,target.getY(),
                        target.getZ()+0.5,0.8);
                return;
            }

            BlockPos core=new BlockPos(camp.x(),camp.y(),camp.z());
            Item required=cell.anyBed()?null:
                    cell.block()==Blocks.WATER?Items.WATER_BUCKET:
                    cell.block()==Blocks.FARMLAND?Items.DIRT:
                    cell.block().asItem();

            for(BlockPos storagePos:BlockPos.betweenClosed(
                    core.offset(-STOCK_RADIUS,-3,-STOCK_RADIUS),
                    core.offset(STOCK_RADIUS,3,STOCK_RADIUS))) {
                if(!level.hasChunkAt(storagePos))continue;
                if(!(level.getBlockEntity(storagePos) instanceof Container container))continue;
                for(int slot=0;slot<container.getContainerSize();slot++) {
                    ItemStack stack=container.getItem(slot);
                    if(stack.isEmpty())continue;
                    if(cell.anyBed()) {
                        if(!(stack.getItem() instanceof BlockItem item
                                &&item.getBlock() instanceof BedBlock))continue;
                    }else if(!stack.is(required))continue;

                    // Farmland requires a real hoe kept in physical camp stock.
                    if(cell.block()==Blocks.FARMLAND && !hasHoe(container))continue;
                    if(!place(level,target,cell,stack))return;
                    // Only consume after physical successful world mutation.
                    if(cell.block()==Blocks.WATER) {
                        stack.shrink(1);
                        if(stack.isEmpty())container.setItem(slot,new ItemStack(Items.BUCKET));
                        else net.minecraft.world.Containers.dropItemStack(level,
                                storagePos.getX()+0.5,storagePos.getY()+1,
                                storagePos.getZ()+0.5,new ItemStack(Items.BUCKET));
                    }else {
                        stack.shrink(1);
                    }
                    container.setChanged();
                    return; // Never create a structure in one instant.
                }
            }
            // No supply for this step; other cells cannot bypass it.
            return;
        }
    }

    private static boolean hasHoe(Container container) {
        for(int slot=0;slot<container.getContainerSize();slot++) {
            Item item=container.getItem(slot).getItem();
            if(item==Items.WOODEN_HOE||item==Items.STONE_HOE||item==Items.IRON_HOE
                    ||item==Items.GOLDEN_HOE||item==Items.DIAMOND_HOE
                    ||item==Items.NETHERITE_HOE)return true;
        }
        return false;
    }

    private static boolean place(ServerLevel world,BlockPos target,
            FunctionalBuildingTemplates.Cell cell,ItemStack source) {
        if(cell.anyBed()) {
            if(!(source.getItem() instanceof BlockItem item
                    &&item.getBlock() instanceof BedBlock bed))return false;
            // Head at template target, foot immediately north. Both positions
            // must be loaded, air and supported. Vanilla multipart states.
            BlockPos foot=target.relative(Direction.NORTH);
            if(!world.hasChunkAt(foot)||!world.isEmptyBlock(foot)
                    ||world.getBlockState(foot.below()).isAir()
                    ||world.getBlockState(target.below()).isAir())return false;
            var base=bed.defaultBlockState().setValue(BedBlock.FACING,Direction.SOUTH);
            var footState=base.setValue(BedBlock.PART,BedPart.FOOT);
            var headState=base.setValue(BedBlock.PART,BedPart.HEAD);
            if(!world.setBlock(foot,footState,3))return false;
            if(!world.setBlock(target,headState,3)) {
                world.removeBlock(foot,false);
                return false;
            }
            return true;
        }
        return world.setBlock(target,cell.block().defaultBlockState(),3);
    }
}

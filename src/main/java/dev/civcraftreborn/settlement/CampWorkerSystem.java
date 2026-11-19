package dev.civcraftreborn.settlement;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;

/**
 * First actual livelihood loop for recruited physical villagers:
 *
 * FARMER: moves into a complete farm, harvests ripe wheat with its real drops,
 * consumes a stored seed to replant.
 * CRAFTSMAN: walks to a fully built workshop, physically transfers raw ore and
 * coal from nearby real stock into the vanilla furnace. Vanilla smelting
 * processes the recipe and fuel (no virtual products).
 * STOREKEEPER: physically carries furnace products in his persistent vanilla
 * villager inventory to a completed storage building.
 * BUILDER: handled by CampConstructionSystem and real construction orders.
 *
 * No unregistered, unloaded or destroyed facility produces any game effect.
 * Work pauses instead of creating goods when stocks or workers are missing.
 */
public final class CampWorkerSystem {
    private static long tick;
    private static final Map<String,Long> LAST_ALARM = new HashMap<>();
    private static final int RADIUS=12;
    private CampWorkerSystem() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CampWorkerSystem::run);
    }

    private static void run(MinecraftServer server) {
        if(++tick%60!=0)return;
        var roles=ResidentWorkSavedData.get(server);
        var citizens=CitizenSavedData.get(server);
        var buildings=BuildingSavedData.get(server);
        var camps=CampSavedData.get(server);
        for(ResidentAssignment assignment:roles.all()) {
            if(assignment.role()==ResidentWorkRole.BUILDER)continue;
            CitizenRecord citizen=citizens.all().stream()
                    .filter(c->c.id().equals(assignment.citizenId()))
                    .findFirst().orElse(null);
            if(citizen==null)continue;
            CampRecord camp=camps.all().stream()
                    .filter(c->c.id().equals(citizen.campId())).findFirst().orElse(null);
            if(camp==null)continue;
            BuildingRecord workplace=buildings.atCamp(camp).stream()
                    .filter(b->b.id().equals(assignment.buildingId()))
                    .findFirst().orElse(null);
            if(workplace==null||!assignment.role().compatible(workplace.type()))continue;

            for(ServerLevel level:server.getAllLevels()) {
                if(!level.dimension().identifier().toString().equals(workplace.dimension()))
                    continue;
                if(!FunctionalBuildingTemplates.inspect(level,
                        new BlockPos(workplace.x(),workplace.y(),workplace.z()),
                        workplace.type(),workplace.rotation()).complete())break;

                var entity=level.getEntity(UUID.fromString(citizen.entityUuid()));
                if(!(entity instanceof Villager villager)||!villager.isAlive())break;
                switch(assignment.role()) {
                    case FARMER -> farm(level,camp,workplace,villager);
                    case CRAFTSMAN -> craft(level,camp,workplace,villager);
                    case STOREKEEPER -> store(level,camp,workplace,villager,buildings.atCamp(camp));
                    case WATCHMAN -> watch(level,camp,workplace,villager);
                    case WATER_CARRIER -> water(level,camp,workplace,villager);
                    case BUILDER -> {}
                }
                break;
            }
        }
    }

    private static BlockPos target(BuildingRecord b, int x, int y, int z) {
        var cells=FunctionalBuildingTemplates.cells(b.type());
        int width=cells.stream().mapToInt(FunctionalBuildingTemplates.Cell::x).max().orElse(0)+1;
        int depth=cells.stream().mapToInt(FunctionalBuildingTemplates.Cell::z).max().orElse(0)+1;
        var rotation=BlueprintTransform.rotate(x,z,width,depth,b.rotation());
        return new BlockPos(b.x()+rotation.x(),b.y()+y,b.z()+rotation.z());
    }

    private static boolean approach(Villager villager, BlockPos pos) {
        if(villager.blockPosition().distSqr(pos)<=9)return true;
        villager.getNavigation().moveTo(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,0.75);
        return false;
    }

    private static void farm(ServerLevel level,CampRecord camp,
            BuildingRecord building,Villager villager) {
        for(int x=1;x<=5;x++)for(int z=1;z<=5;z++) {
            BlockPos crops=target(building,x,0,z);
            if(!level.hasChunkAt(crops)||!level.getBlockState(crops).is(Blocks.WHEAT))
                continue;
            if(level.getBlockState(crops).getValue(CropBlock.AGE)<7)continue;
            if(!approach(villager,crops))return;

            // Do not clear a crop if there is no real seed to replant it.
            Located seed=findStock(level,camp,Items.WHEAT_SEEDS);
            if(seed==null)return;
            if(!level.destroyBlock(crops,true,villager))return;
            seed.stack().shrink(1);
            seed.container().setChanged();
            level.setBlock(crops,Blocks.WHEAT.defaultBlockState(),3);
            return;
        }
    }

    private static void craft(ServerLevel level,CampRecord camp,
            BuildingRecord workshop,Villager villager) {
        BlockPos furnacePos=workshop.type()==FunctionalBuildingTemplates.Type.SMITHY
                ?target(workshop,5,0,3):target(workshop,4,0,2);
        if(!level.hasChunkAt(furnacePos)
                ||!(level.getBlockEntity(furnacePos) instanceof Container furnace))return;
        if(!approach(villager,furnacePos))return;
        if(!furnace.getItem(0).isEmpty()||!furnace.getItem(1).isEmpty())return;

        Located material=null;
        for(Item ore:List.of(Items.RAW_IRON,Items.RAW_COPPER,Items.RAW_GOLD)) {
            material=findStock(level,camp,ore);
            if(material!=null)break;
        }
        Located coal=findStock(level,camp,Items.COAL);
        if(material==null||coal==null)return;

        // Validate the two physical sources before altering any container.
        Item item=material.stack().getItem();
        furnace.setItem(0,new ItemStack(item,1));
        furnace.setItem(1,new ItemStack(Items.COAL,1));
        material.stack().shrink(1);
        coal.stack().shrink(1);
        material.container().setChanged();
        coal.container().setChanged();
        furnace.setChanged();
        // Actual vanilla furnace tick later consumes coal and converts ore.
    }

    private static void store(ServerLevel level,CampRecord camp,BuildingRecord warehouse,
            Villager villager,List<BuildingRecord> buildings) {
        var cargo=villager.getInventory();
        BlockPos storage=target(warehouse,1,0,2);
        if(!level.hasChunkAt(storage)
                ||!(level.getBlockEntity(storage) instanceof Container chest))return;

        // Deliver actual carried ingots first. No long-distance teleport.
        for(int slot=0;slot<cargo.getContainerSize();slot++) {
            ItemStack carried=cargo.getItem(slot);
            if(carried.isEmpty()||!isWorkshopOutput(carried.getItem()))continue;
            if(!approach(villager,storage))return;
            if(insert(chest,carried)) {
                cargo.setChanged();chest.setChanged();
            }
            return;
        }

        for(BuildingRecord building:buildings) {
            if(building.type()!=FunctionalBuildingTemplates.Type.WORKSHOP
                    &&building.type()!=FunctionalBuildingTemplates.Type.SMITHY)continue;
            if(!FunctionalBuildingTemplates.inspect(level,
                    new BlockPos(building.x(),building.y(),building.z()),
                    building.type(),building.rotation()).complete())continue;
            BlockPos furnacePos=building.type()==FunctionalBuildingTemplates.Type.SMITHY
                    ?target(building,5,0,3):target(building,4,0,2);
            if(!level.hasChunkAt(furnacePos)
                    ||!(level.getBlockEntity(furnacePos) instanceof Container furnace))continue;
            ItemStack output=furnace.getItem(2);
            if(output.isEmpty()||!isWorkshopOutput(output.getItem()))continue;
            if(!approach(villager,furnacePos))return;
            if(insert(cargo,output)) {
                cargo.setChanged();furnace.setChanged();
            }
            return;
        }
    }

    /**
     * Watchtower has a real defensive purpose: a recruited, stationed watchman
     * inspects loaded hostiles near the tower and warns actual camp players.
     * No monster is spawned/removed and no free guard/goolem is generated.
     */
    private static void watch(ServerLevel level,CampRecord camp,
            BuildingRecord tower,Villager villager) {
        BlockPos post=target(tower,2,0,2);
        if(!approach(villager,post))return;
        if(level.getEntitiesOfClass(Monster.class,new AABB(post).inflate(20.0),
                hostile->hostile.isAlive()).isEmpty())return;
        long now=level.getGameTime();
        long previous=LAST_ALARM.getOrDefault(camp.id(),-10000L);
        if(now-previous<600)return;
        LAST_ALARM.put(camp.id(),now);
        for(var player:level.players()) {
            if(player.blockPosition().distSqr(post)<48*48) {
                player.sendSystemMessage(Component.literal(
                        "CivCraft: watchman at "+camp.name()+" spotted hostile creatures!"));
            }
        }
    }

    /**
     * Real water-carrier loop: picks one stored empty bucket, walks to the
     * physical water source in a verified WELL, fills it, returns it to storage.
     * Water itself follows normal vanilla source-water behavior.
     */
    private static void water(ServerLevel level,CampRecord camp,
            BuildingRecord well,Villager villager) {
        var bag=villager.getInventory();
        for(int slot=0;slot<bag.getContainerSize();slot++) {
            ItemStack stack=bag.getItem(slot);
            if(stack.is(Items.WATER_BUCKET)) {
                Located destination=findStorage(level,camp);
                if(destination==null)return;
                if(!approach(villager,destination.pos()))return;
                if(insert(destination.container(),stack)) {
                    bag.setChanged();
                    destination.container().setChanged();
                }
                return;
            }
        }
        for(int slot=0;slot<bag.getContainerSize();slot++) {
            if(!bag.getItem(slot).is(Items.BUCKET))continue;
            BlockPos source=target(well,2,-1,2);
            if(!level.hasChunkAt(source)||!level.getBlockState(source).is(Blocks.WATER))
                return;
            if(!approach(villager,source.above()))return;
            bag.setItem(slot,new ItemStack(Items.WATER_BUCKET,1));
            bag.setChanged();
            return;
        }
        Located empty=findStock(level,camp,Items.BUCKET);
        if(empty==null)return;
        if(!approach(villager,empty.pos()))return;
        ItemStack one=new ItemStack(Items.BUCKET,1);
        if(insert(bag,one)) {
            empty.stack().shrink(1);
            bag.setChanged();
            empty.container().setChanged();
        }
    }

    private static Located findStorage(ServerLevel level,CampRecord camp) {
        BlockPos core=new BlockPos(camp.x(),camp.y(),camp.z());
        for(BlockPos p:BlockPos.betweenClosed(core.offset(-RADIUS,-3,-RADIUS),
                core.offset(RADIUS,3,RADIUS))) {
            if(!level.hasChunkAt(p))continue;
            var block=level.getBlockState(p);
            if(!block.is(Blocks.CHEST)&&!block.is(Blocks.BARREL))continue;
            if(level.getBlockEntity(p) instanceof Container container)
                return new Located(p.immutable(),container,ItemStack.EMPTY);
        }
        return null;
    }

    private static boolean isWorkshopOutput(Item item) {
        return item==Items.IRON_INGOT||item==Items.COPPER_INGOT||item==Items.GOLD_INGOT;
    }

    /**
     * Transfer one existing stack into another physical inventory, preserving
     * item components. Returns true only if at least one item actually moved.
     */
    private static boolean insert(Container destination,ItemStack source) {
        for(int slot=0;slot<destination.getContainerSize();slot++) {
            ItemStack existing=destination.getItem(slot);
            if(existing.isEmpty()) {
                ItemStack copy=source.copy();
                int moved=Math.min(source.getCount(),Math.min(copy.getMaxStackSize(),
                        destination.getMaxStackSize()));
                copy.setCount(moved);
                destination.setItem(slot,copy);
                source.shrink(moved);
                return moved>0;
            }
            if(ItemStack.isSameItemSameComponents(existing,source)) {
                int room=Math.min(existing.getMaxStackSize(),destination.getMaxStackSize())
                        -existing.getCount();
                if(room>0) {
                    int moved=Math.min(room,source.getCount());
                    existing.grow(moved);
                    source.shrink(moved);
                    return moved>0;
                }
            }
        }
        return false;
    }

    private record Located(BlockPos pos,Container container,ItemStack stack) {}

    /** Stock only from actually loaded camp containers, no virtual inventory. */
    private static Located findStock(ServerLevel level,CampRecord camp,Item item) {
        BlockPos core=new BlockPos(camp.x(),camp.y(),camp.z());
        for(BlockPos p:BlockPos.betweenClosed(core.offset(-RADIUS,-3,-RADIUS),
                core.offset(RADIUS,3,RADIUS))) {
            if(!level.hasChunkAt(p))continue;
            if(!(level.getBlockEntity(p) instanceof Container chest))continue;
            for(int slot=0;slot<chest.getContainerSize();slot++) {
                ItemStack stack=chest.getItem(slot);
                if(stack.is(item)&&!stack.isEmpty())return new Located(p.immutable(),chest,stack);
            }
        }
        return null;
    }
}

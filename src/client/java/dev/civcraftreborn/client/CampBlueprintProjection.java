package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import dev.civcraftreborn.settlement.BlueprintAccess;
import dev.civcraftreborn.settlement.CampHeartTemplate;
import dev.civcraftreborn.settlement.CampRules;
import dev.civcraftreborn.settlement.BlueprintTransform;
import dev.civcraftreborn.settlement.FunctionalBuildingTemplates;
import dev.civcraftreborn.settlement.SettlementStage;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Client-only visual projection. The server owns all permissions, settlement
 * progression, resource spending, actual buildings and citizens.
 *
 * Non-heart templates can be placed anywhere within the 24-block camp radius,
 * rotated in 90-degree increments and previewed without placing any blocks.
 */
public final class CampBlueprintProjection {
    public enum Plan {
        HEART("Сердце лагеря"),
        STORAGE("Простой склад"),
        WORKSHOP("Мастерская"),
        FARM("Ферма"),
        BEDROOM("Жилая комната"),
        SMALL_HOUSE("Дом поселенца"),
        BARRACKS("Общий барак"),
        GRANARY("Амбар"),
        SMITHY("Кузница"),
        WATCHTOWER("Сторожевая башня"),
        WELL("Колодец");

        private final String title;
        Plan(String title) { this.title = title; }
        public String title() { return title; }
    }

    public record Step(int dx, int dy, int dz, Block block, boolean anyBed) {
        public Step(int dx, int dy, int dz, Block block) {
            this(dx, dy, dz, block, false);
        }
        /** Legacy raw-template coordinate. Use worldPosition(plan,step) for previews. */
        public BlockPos position(BlockPos anchor) {
            return anchor.offset(dx, dy, dz);
        }
        public boolean matches(Level level, BlockPos anchor) {
            return matchesAt(level, position(anchor));
        }
        public boolean matchesAt(Level level, BlockPos pos) {
            if (!level.hasChunkAt(pos)) return false;
            var state = level.getBlockState(pos);
            return anyBed ? state.getBlock() instanceof BedBlock : state.is(block);
        }
    }

    public record MaterialLine(String title, int required, int alreadyBuilt,
            int inInventory, int stillNeeded) {}

    private record Bounds(int minX, int minZ, int width, int depth) {}
    private static final Map<Plan, List<Step>> TEMPLATE_CACHE = new EnumMap<>(Plan.class);
    private static final Map<Plan, Bounds> BOUNDS_CACHE = new EnumMap<>(Plan.class);

    private static Bounds bounds(Plan plan) {
        return BOUNDS_CACHE.computeIfAbsent(plan, p -> {
            List<Step> template = steps(p);
            int minX=template.stream().mapToInt(Step::dx).min().orElse(0);
            int maxX=template.stream().mapToInt(Step::dx).max().orElse(0);
            int minZ=template.stream().mapToInt(Step::dz).min().orElse(0);
            int maxZ=template.stream().mapToInt(Step::dz).max().orElse(0);
            return new Bounds(minX,minZ,maxX-minX+1,maxZ-minZ+1);
        });
    }

    private static BlockPos coreAnchor;
    private static String dimension;
    private static Plan activePlan;
    // -2 pending response, -1 no owned camp, 0 CAMP, 1 SETTLEMENT.
    private static int serverStage = -2;
    private static int offsetX = 12;
    private static int offsetY = 0;
    private static int offsetZ = 0;
    private static boolean heartConfirmed;
    private static int heartMatched=-1;
    private static int heartTotal=0;
    private static boolean heartChunksLoaded=false;
    private static int rotation = 0;

    private CampBlueprintProjection() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(CampBlueprintProjection::tick);
    }

    public static void useCore(BlockPos core, String dimensionId) {
        // Never reuse old authorization for another core or another world.
        coreAnchor = core.immutable();
        dimension = dimensionId;
        activePlan = null;
        serverStage = -2;
        offsetX = 12;
        offsetY = 0;
        offsetZ = 0;
        rotation = 0;
        heartConfirmed = false;
        clearHeartProgress();
    }

    public static void setHeartPlacement(BlockPos core,int shiftX,int shiftY,int shiftZ,
            int turns,boolean confirmed) {
        if(coreAnchor==null||!coreAnchor.equals(core))return;
        if(!CampHeartTemplate.transformValid(shiftX,shiftY,shiftZ,turns))return;
        offsetX=shiftX;
        offsetY=shiftY;
        offsetZ=shiftZ;
        rotation=turns;
        heartConfirmed=confirmed;
    }
    private static void clearHeartProgress() {
        heartMatched=-1;
        heartTotal=0;
        heartChunksLoaded=false;
    }

    public static void setHeartProgress(BlockPos core,int matched,int total,boolean loaded) {
        if(coreAnchor==null||!coreAnchor.equals(core))return;
        if(total<1 || matched<0 || matched>total)return;
        heartMatched=matched;
        heartTotal=total;
        heartChunksLoaded=loaded;
    }
    public static boolean heartReadyToFound() {
        return heartConfirmed && heartChunksLoaded && heartTotal>0
                && heartMatched==heartTotal && serverStage==-1;
    }
    public static String heartProgressLabel() {
        if(coreAnchor==null)return "Нет активной стройки";
        if(heartMatched<0)return "Проверяем состояние постройки…";
        if(!heartConfirmed)return "Площадка не подтверждена";
        if(!heartChunksLoaded)return "Не все чанки постройки загружены";
        return heartMatched+"/"+heartTotal+
                (heartMatched==heartTotal?" — ЗАВЕРШЕНО":" — строительство");
    }

    public static boolean heartConfirmed(){return heartConfirmed;}

    public static void setServerStage(BlockPos core, int stage) {
        if (coreAnchor == null || !coreAnchor.equals(core)) return;
        serverStage = stage >= -1 && stage <= 1 ? stage : -1;
        if (activePlan != null && !unlocked(activePlan)) hide();
    }

    public static int serverStage() { return serverStage; }

    public static boolean unlocked(Plan plan) {
        if (serverStage == -2) return false;
        return serverStage == -1
                ? BlueprintAccess.allowedBeforeFounding(plan.name())
                : BlueprintAccess.allowed(plan.name(),
                        serverStage == 1 ? SettlementStage.SETTLEMENT : SettlementStage.CAMP);
    }

    public static boolean show(Plan plan) {
        if (coreAnchor == null || !unlocked(plan)) return false;
        if (activePlan != plan && plan != Plan.HEART) {
            offsetX = 3;
            offsetY = 0;
            offsetZ = 3;
            rotation = 0;
        }
        activePlan = plan;
        return true;
    }

    public static void hide() { activePlan = null; }
    /** Forget an invalidated construction project, not merely its visible ghost. */
    public static void clearProject(BlockPos foundation) {
        if(coreAnchor==null || (!BlockPos.ZERO.equals(foundation)
                && !coreAnchor.equals(foundation)))return;
        coreAnchor=null;
        dimension=null;
        activePlan=null;
        serverStage=-2;
        offsetX=12;
        offsetY=0;
        offsetZ=0;
        rotation=0;
        heartConfirmed=false;
        clearHeartProgress();
    }
    public static Plan selected() { return activePlan; }
    public static BlockPos anchor() { return coreAnchor; }
    public static int rotationDegrees() { return rotation * 90; }
    public static int offsetX() { return offsetX; }
    public static int offsetY() { return offsetY; }
    /** NW floor-level origin used by server construction and acceptance. */
    public static BlockPos placedOrigin() {
        return coreAnchor == null ? null : coreAnchor.offset(offsetX,offsetY,offsetZ);
    }

    public static int offsetZ() { return offsetZ; }

    public static List<Step> steps(Plan plan) {
        if (plan == null) return List.of();
        return TEMPLATE_CACHE.computeIfAbsent(plan, CampBlueprintProjection::buildSteps);
    }

    private static List<Step> buildSteps(Plan plan) {
        if (plan == Plan.HEART) {
            return CampHeartTemplate.cells().stream()
                    .map(cell -> new Step(cell.x(),cell.y(),cell.z(),
                            cell.state().getBlock())).toList();
        }
        // All other 10 structures use the SAME full blueprint model for
        // client rendering, server validation and actual NPC construction.
        FunctionalBuildingTemplates.Type type =
                FunctionalBuildingTemplates.Type.valueOf(plan.name());
        return FunctionalBuildingTemplates.cells(type).stream()
                .map(c -> new Step(c.x(),c.y(),c.z(),c.block(),c.anyBed()))
                .toList();
    }

    /**
     * Rotate the complete footprint about its top-left corner; all dimensions
     * remain non-negative after rotation. Heart is anchored to the real core.
     */
    public static BlockPos worldPosition(Plan plan, Step step) {
        if (coreAnchor == null) return null;
        if (plan == Plan.HEART) {
            var p=BlueprintTransform.rotate(step.dx(),step.dz(),7,7,rotation);
            return coreAnchor.offset(-3+offsetX+p.x(),offsetY+step.dy(),-3+offsetZ+p.z());
        }
        Bounds bounds = bounds(plan);
        int x=step.dx()-bounds.minX(), z=step.dz()-bounds.minZ();
        BlueprintTransform.Point point = BlueprintTransform.rotate(
                x,z,bounds.width(),bounds.depth(),rotation);
        return coreAnchor.offset(offsetX + point.x(), offsetY + step.dy(), offsetZ + point.z());
    }

    private static boolean validPlacement() {
        if (activePlan == null || coreAnchor == null) return false;
        if(activePlan==Plan.HEART)
            return CampHeartTemplate.transformValid(offsetX,offsetY,offsetZ,rotation);
        for (Step step : steps(activePlan)) {
            BlockPos pos = worldPosition(activePlan, step);
            if (!BlueprintTransform.inCampRadius(pos.getX()-coreAnchor.getX(),
                    pos.getZ()-coreAnchor.getZ(), CampRules.CAMP_RADIUS)
                    || (activePlan != Plan.HEART && pos.equals(coreAnchor))) return false;
        }
        return true;
    }

    public static boolean move(int dx, int dz) {
        if (activePlan == null || (activePlan == Plan.HEART && heartConfirmed)) return false;
        offsetX += dx;
        offsetZ += dz;
        if (validPlacement() && (activePlan != Plan.HEART ||
                CampHeartTemplate.transformValid(offsetX,offsetZ,rotation))) return true;
        offsetX -= dx;
        offsetZ -= dz;
        return false;
    }

    /** Raise or lower the unconfirmed blueprint by one or more block layers. */
    public static boolean moveVertical(int deltaY) {
        if(activePlan==null || coreAnchor==null
                || (activePlan==Plan.HEART && heartConfirmed))return false;
        int oldY=offsetY;
        offsetY+=deltaY;
        if(validPlacement())return true;
        offsetY=oldY;
        return false;
    }

    public static boolean rotate(int quarterTurns) {
        if (activePlan == null || (activePlan == Plan.HEART && heartConfirmed)) return false;
        int previous = rotation;
        rotation = Math.floorMod(rotation + quarterTurns, 4);
        if (validPlacement()) return true;
        rotation = previous;
        return false;
    }

    /**
     * Exact predicted BlockState, shared by visual model and server validation.
     * Essential for correctly oriented stairs, doors and upper slabs.
     */
    public static BlockState expectedState(Plan plan,Step step) {
        if(plan==Plan.HEART && coreAnchor!=null) {
            for(var cell:CampHeartTemplate.cells()) {
                if(cell.x()==step.dx() && cell.y()==step.dy() && cell.z()==step.dz())
                    return CampHeartTemplate.expected(coreAnchor,offsetX,offsetZ,rotation,cell);
            }
        }
        return step.block().defaultBlockState();
    }

    public static boolean matches(Plan plan, Step step) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || coreAnchor == null) return false;
        BlockPos pos = worldPosition(plan, step);
        if(pos==null||!client.level.hasChunkAt(pos))return false;
        if(plan==Plan.HEART)
            return CampHeartTemplate.matching(client.level.getBlockState(pos),expectedState(plan,step));
        return step.matchesAt(client.level,pos);
    }

    public static int completed(Plan plan) {
        int count = 0;
        for (Step step : steps(plan)) if (matches(plan, step)) count++;
        return count;
    }

    /** Counts every required block; distinguishes already placed from bag stock. */
    public static List<MaterialLine> materials(Plan plan) {
        record Count(Block block, boolean bed, String name) {}
        Map<String, int[]> amounts = new LinkedHashMap<>();
        Map<String, Count> materials = new LinkedHashMap<>();
        for (Step step : steps(plan)) {
            // A vanilla door consumes one item but occupies two block states.
            if(plan==Plan.HEART&&step.block()==Blocks.OAK_DOOR&&step.dy()==2)continue;
            String key = step.anyBed() ? "ANY_BED" : step.block().getName().getString();
            amounts.computeIfAbsent(key, k -> new int[2])[0]++;
            materials.putIfAbsent(key, new Count(step.block(), step.anyBed(),
                    step.anyBed() ? "Любая кровать"
                            : step.block() == Blocks.FARMLAND
                                    ? "Земля для грядок (мотыга)"
                                    : step.block() == Blocks.WATER
                                            ? "Ведро воды"
                                            : step.block().getName().getString()));
            boolean built=matches(plan,step);
            if(plan==Plan.HEART&&step.block()==Blocks.OAK_DOOR&&step.dy()==1)
                built=built&&matches(plan,new Step(step.dx(),2,step.dz(),Blocks.OAK_DOOR));
            if (built) amounts.get(key)[1]++;
        }
        Minecraft client = Minecraft.getInstance();
        List<MaterialLine> lines = new ArrayList<>();
        for (var entry : amounts.entrySet()) {
            Count material = materials.get(entry.getKey());
            int neededInWorld = entry.getValue()[0] - entry.getValue()[1];
            int inventory = 0;
            if (client.player != null) {
                var inv = client.player.getInventory();
                for (int slot=0; slot<inv.getContainerSize(); slot++) {
                    ItemStack stack = inv.getItem(slot);
                    if (material.bed()) {
                        if (stack.getItem() instanceof BlockItem blockItem
                                && blockItem.getBlock() instanceof BedBlock)
                            inventory += stack.getCount();
                    } else if (stack.is(material.block() == Blocks.FARMLAND
                            ? Blocks.DIRT.asItem() : material.block() == Blocks.WATER
                                    ? net.minecraft.world.item.Items.WATER_BUCKET
                                    : material.block().asItem())) {
                        inventory += stack.getCount();
                    }
                }
            }
            lines.add(new MaterialLine(material.name(), entry.getValue()[0],
                    entry.getValue()[1], inventory, Math.max(0, neededInWorld - inventory)));
        }
        return List.copyOf(lines);
    }

    private static void tick(Minecraft client) {
        if (coreAnchor == null || client.level == null || client.player == null) {
            hide();
            serverStage = -2;
            return;
        }
        if (!client.level.dimension().identifier().toString().equals(dimension)) {
            hide();
            serverStage = -2;
            return;
        }
        if (activePlan != null && !unlocked(activePlan)) hide();

    }
}

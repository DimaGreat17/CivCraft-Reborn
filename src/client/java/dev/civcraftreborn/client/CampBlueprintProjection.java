package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Opt-in client-only 3D hologram prototype. It never sets blocks and grants
 * no inventory. Native translucent full-voxel ghosts show missing blocks and disappear
 * when the corresponding real block is built. The server remains authoritative.
 */
public final class CampBlueprintProjection {
    public enum Plan {
        HEART("Сердце лагеря"),
        STORAGE("Склад"),
        WORKSHOP("Мастерская"),
        FARM("Ферма"),
        BEDROOM("Жилая комната"),
        SMALL_HOUSE("Дом поселенца"),
        BARRACKS("Общий барак"),
        GRANARY("Амбар и склад"),
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
        public BlockPos position(BlockPos anchor) {
            return anchor.offset(dx, dy, dz);
        }
        public boolean matches(net.minecraft.world.level.Level level, BlockPos anchor) {
            BlockPos pos = position(anchor);
            if (!level.hasChunkAt(pos)) return false;
            var state = level.getBlockState(pos);
            return anyBed ? state.getBlock() instanceof BedBlock : state.is(block);
        }
    }


    private static BlockPos coreAnchor;
    private static String dimension;
    private static Plan activePlan;

    private CampBlueprintProjection() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(CampBlueprintProjection::tick);
    }

    public static void useCore(BlockPos core, String dimensionId) {
        if (coreAnchor == null || !coreAnchor.equals(core) || !dimensionId.equals(dimension)) {
            activePlan = null;
        }
        coreAnchor = core.immutable();
        dimension = dimensionId;
    }

    public static void show(Plan plan) {
        if (coreAnchor != null) activePlan = plan;
    }

    public static void hide() {
        activePlan = null;
    }

    public static Plan selected() { return activePlan; }

    public static BlockPos anchor() { return coreAnchor; }

    public static List<Step> steps(Plan plan) {
        List<Step> parts = new ArrayList<>();
        if (plan == Plan.HEART) {
            parts.add(new Step(0, 0, -1, Blocks.OBSIDIAN));
            parts.add(new Step(0, 0, 1, Blocks.OBSIDIAN));
            parts.add(new Step(-1, 0, 0, Blocks.OBSIDIAN));
            parts.add(new Step(1, 0, 0, Blocks.OBSIDIAN));
        } else if (plan == Plan.STORAGE) {
            parts.add(new Step(4, 0, 0, Blocks.CHEST));
            parts.add(new Step(4, 0, 1, Blocks.BARREL));
        } else if (plan == Plan.WORKSHOP) {
            parts.add(new Step(-4, 0, 0, Blocks.CRAFTING_TABLE));
            parts.add(new Step(-4, 0, 1, Blocks.FURNACE));
        } else if (plan == Plan.FARM) {
            for (int x = -1; x <= 0; x++) {
                for (int z = 5; z <= 6; z++) parts.add(new Step(x, -1, z, Blocks.FARMLAND));
            }
            parts.add(new Step(1, 0, 5, Blocks.COMPOSTER));
        } else if (plan == Plan.BEDROOM) {
            // Illustrative voluntary room template, not mandatory shape under ROOM.
            for (int x = 4; x <= 6; x++) for (int z = 4; z <= 6; z++) {
                parts.add(new Step(x, -1, z, Blocks.OAK_PLANKS));
                parts.add(new Step(x, 3, z, Blocks.OAK_PLANKS));
            }
            for (int y = 0; y <= 2; y++) for (int t = 4; t <= 6; t++) {
                if (!(y == 0 && t == 5)) parts.add(new Step(t, y, 4, Blocks.OAK_PLANKS));
                parts.add(new Step(t, y, 6, Blocks.OAK_PLANKS));
                parts.add(new Step(4, y, t, Blocks.OAK_PLANKS));
                parts.add(new Step(6, y, t, Blocks.OAK_PLANKS));
            }
            parts.add(new Step(5, 0, 5, Blocks.AIR, true));
        }
        // New original starter plans are geometry-defined, not redistributed .schem files.
        if (plan.ordinal() >= Plan.SMALL_HOUSE.ordinal()) {
            return CampBuildingTemplates.forPlan(plan);
        }
        // Corner walls from orthogonal faces can overlap. One world cell = one step.
        Map<String, Step> unique = new LinkedHashMap<>();
        for (Step step : parts) {
            unique.put(step.dx() + ":" + step.dy() + ":" + step.dz(), step);
        }
        return List.copyOf(unique.values());
    }

    public static int completed(Plan plan) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || coreAnchor == null) return 0;
        int count = 0;
        for (Step step : steps(plan)) if (step.matches(client.level, coreAnchor)) count++;
        return count;
    }

    private static void tick(Minecraft client) {
        if (activePlan == null || coreAnchor == null || client.level == null
                || client.player == null) return;
        if (!client.level.dimension().identifier().toString().equals(dimension)) {
            hide();
            return;
        }
        if (client.player.blockPosition().distSqr(coreAnchor) > 28 * 28) {
            hide();
            return;
        }

    }
}

package dev.civcraftreborn.client;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Opt-in client-only 3D hologram prototype. It never sets blocks and grants
 * no inventory. Green dots mark real completed steps; cyan dotted cube edges
 * mark missing blocks. Server-side validation remains authoritative.
 */
public final class CampBlueprintProjection {
    public enum Plan {
        HEART("Сердце лагеря"),
        STORAGE("Склад"),
        WORKSHOP("Мастерская"),
        FARM("Ферма"),
        BEDROOM("Жилая комната");

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

    private static final DustParticleOptions MISSING =
            new DustParticleOptions(0x41CCFF, 0.75f);
    private static final DustParticleOptions COMPLETE =
            new DustParticleOptions(0x51E89C, 0.58f);

    private static BlockPos coreAnchor;
    private static String dimension;
    private static Plan activePlan;
    private static int tickCounter;

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
            parts.add(new Step(5, 0, 5, Blocks.WHITE_BED, true));
        }
        return List.copyOf(parts);
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
        // Low particle budget even for the larger house preview.
        if (++tickCounter % 5 != 0) return;
        int emitted = 0;
        for (Step step : steps(activePlan)) {
            BlockPos pos = step.position(coreAnchor);
            if (!client.level.hasChunkAt(pos)) continue;
            boolean built = step.matches(client.level, coreAnchor);
            DustParticleOptions particle = built ? COMPLETE : MISSING;
            // Four opposite cube corners give a legible 3D silhouette.
            for (int c = 0; c < 4; c++) {
                double x = pos.getX() + ((c & 1) == 0 ? 0.05 : 0.95);
                double y = pos.getY() + ((c & 2) == 0 ? 0.08 : 0.92);
                double z = pos.getZ() + ((c & 1) == 0 ? 0.95 : 0.05);
                client.level.addParticle(particle, x, y, z, 0, 0, 0);
            }
            if (++emitted >= 80) break;
        }
    }
}

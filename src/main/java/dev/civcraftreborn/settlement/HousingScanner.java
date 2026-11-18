package dev.civcraftreborn.settlement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

/**
 * Conservative bounded room scanner. Rooms are defined by actually supported,
 * sheltered, traversable cells. Doors separate private rooms from shared halls.
 * Unknown, unloaded or ambiguous space cannot create a new housing slot.
 *
 * This is the initial 0.2 implementation, NOT the fully validated PHYSICAL
 * pathfinder for nonstandard blocks and multi-floor spaces.
 */
public final class HousingScanner {
    private static final int MAX_CELLS_PER_ROOM = 256;
    private static final int MAX_BEDS_PER_CAMP = 48;
    private static final int[] HEIGHT_OFFSETS = {0, 1, -1};

    private HousingScanner() {}

    public record Room(Set<BlockPos> privateCells, Set<BlockPos> bedHeads,
            boolean completelyVerified) {
        public int capacity() {
            return completelyVerified
                    ? Math.min(bedHeads.size(), privateCells.size() / 4) : 0;
        }
    }

    public record HousingSummary(int roomCount, int bedCount,
            int privateFloorCells, int capacity, boolean complete) {}

    public static HousingSummary scan(ServerLevel level, CampRecord camp) {
        if (!level.dimension().identifier().toString().equals(camp.dimension())) {
            return new HousingSummary(0, 0, 0, 0, false);
        }
        BlockPos center = new BlockPos(camp.x(), camp.y(), camp.z());
        List<Room> rooms = new ArrayList<>();
        int beds = 0;
        boolean complete = true;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-CampRules.CAMP_RADIUS, -5, -CampRules.CAMP_RADIUS),
                center.offset(CampRules.CAMP_RADIUS, 5, CampRules.CAMP_RADIUS))) {
            if (!level.hasChunkAt(pos)) {
                complete = false;
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof BedBlock)
                    || state.getValue(BedBlock.PART) != BedPart.HEAD) continue;
            beds++;
            if (beds > MAX_BEDS_PER_CAMP) {
                complete = false;
                break;
            }
            Room room = investigate(level, pos.immutable(), center);
            if (!room.completelyVerified()) complete = false;
            if (room.privateCells().isEmpty()) continue;
            boolean merged;
            do {
                merged = false;
                for (int i = 0; i < rooms.size(); i++) {
                    Room other = rooms.get(i);
                    if (!disjoint(room.privateCells(), other.privateCells())) {
                        Set<BlockPos> cells = new HashSet<>(room.privateCells());
                        cells.addAll(other.privateCells());
                        Set<BlockPos> heads = new HashSet<>(room.bedHeads());
                        heads.addAll(other.bedHeads());
                        room = new Room(Set.copyOf(cells), Set.copyOf(heads),
                                room.completelyVerified() && other.completelyVerified());
                        rooms.remove(i);
                        merged = true;
                        break;
                    }
                }
            } while (merged);
            rooms.add(room);
        }

        int countedBeds = 0, floor = 0, capacity = 0;
        for (Room room : rooms) {
            countedBeds += room.bedHeads().size();
            floor += room.privateCells().size();
            capacity += room.capacity();
        }
        return new HousingSummary(rooms.size(), countedBeds, floor,
                complete ? capacity : 0, complete);
    }

    private static boolean disjoint(Set<BlockPos> a, Set<BlockPos> b) {
        for (BlockPos pos : a) if (b.contains(pos)) return false;
        return true;
    }

    private static Room investigate(ServerLevel level, BlockPos bedHead, BlockPos center) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighbour = bedHead.relative(dir);
            if (walkable(level, neighbour)) queue.add(neighbour);
        }

        boolean complete = true;
        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            if (visited.contains(pos)) continue;
            if (!insideLocalLimit(pos, center)) continue;
            if (!level.hasChunkAt(pos)) {
                complete = false;
                continue;
            }
            if (!walkable(level, pos)) continue;
            if (!sheltered(level, pos)) continue;
            visited.add(pos.immutable());
            if (visited.size() >= MAX_CELLS_PER_ROOM) {
                complete = false;
                break;
            }

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                for (int changeY : HEIGHT_OFFSETS) {
                    BlockPos next = pos.relative(dir).offset(0, changeY, 0);
                    if (!visited.contains(next) && insideLocalLimit(next, center)) {
                        queue.addLast(next);
                    }
                }
            }
        }
        if (visited.isEmpty()) return new Room(Set.of(), Set.of(), complete);
        return new Room(Set.copyOf(visited), Set.of(bedHead), complete);
    }

    private static boolean insideLocalLimit(BlockPos p, BlockPos c) {
        return Math.abs(p.getX() - c.getX()) <= CampRules.CAMP_RADIUS
                && Math.abs(p.getZ() - c.getZ()) <= CampRules.CAMP_RADIUS
                && Math.abs(p.getY() - c.getY()) <= 6;
    }

    private static boolean walkable(ServerLevel level, BlockPos feet) {
        if (!level.hasChunkAt(feet) || !level.hasChunkAt(feet.below())) return false;
        BlockState state = level.getBlockState(feet);
        BlockState above = level.getBlockState(feet.above());
        BlockState floor = level.getBlockState(feet.below());
        if (state.getBlock() instanceof DoorBlock) return false;
        if (!state.getFluidState().isEmpty() || !above.getFluidState().isEmpty()) return false;
        return state.getCollisionShape(level, feet).isEmpty()
                && above.getCollisionShape(level, feet.above()).isEmpty()
                && !floor.getCollisionShape(level, feet.below()).isEmpty();
    }

    private static boolean sheltered(ServerLevel level, BlockPos feet) {
        // A roof is mandatory. Headroom is checked by walkable().
        for (int y = 2; y <= 5; y++) {
            BlockPos above = feet.above(y);
            if (!level.hasChunkAt(above)) return false;
            if (!level.getBlockState(above).getCollisionShape(level, above).isEmpty()) return true;
        }
        return false;
    }
}

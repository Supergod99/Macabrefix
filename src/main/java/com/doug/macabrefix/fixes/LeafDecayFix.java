package com.doug.macabrefix.fixes;

import com.curseforge.macabre.init.MacabreModBlocks;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public final class LeafDecayFix {
    private static final int LEAF_SEED_HORIZONTAL_RADIUS = 8;
    private static final int LEAF_SEED_UP_RADIUS = 32;
    private static final int LEAF_SEED_DOWN_RADIUS = 8;
    private static final int SOAKED_SUPPORT_RADIUS = 6;
    private static final int SOAKED_CLUSTER_HORIZONTAL_RADIUS = 24;
    private static final int SOAKED_CLUSTER_UP_RADIUS = 40;
    private static final int SOAKED_CLUSTER_DOWN_RADIUS = 12;
    private static final int MAX_SOAKED_LEAVES_PER_BREAK = 8192;

    private static final List<Supplier<? extends Block>> LOG_BLOCKS = List.of(
            MacabreModBlocks.SOAKED_LOG,
            MacabreModBlocks.SOAKED_WOOD,
            MacabreModBlocks.TEETHING_LOG,
            MacabreModBlocks.TEETHING_WOOD,
            MacabreModBlocks.WASTED_LOG,
            MacabreModBlocks.WASTED_WOOD,
            MacabreModBlocks.DRIED_LOG,
            MacabreModBlocks.DRIED_WOOD,
            MacabreModBlocks.DEAD_LOG,
            MacabreModBlocks.MEDIUM_DEAD_LOG,
            MacabreModBlocks.SLIM_DEAD_LOG);

    private static final List<Supplier<? extends Block>> VANILLA_DECAY_LEAVES = List.of(
            MacabreModBlocks.DRIED_LEAVES,
            MacabreModBlocks.TEETHING_LEAVES,
            MacabreModBlocks.WASTED_LEAVES);

    private LeafDecayFix() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(EventPriority.LOWEST, LeafDecayFix::onBlockBreak);
    }

    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || event.isCanceled() || !isMacabreLog(event.getState())) {
            return;
        }

        BlockPos brokenLogPos = event.getPos();
        List<BlockPos> soakedLeafSeeds = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(
                brokenLogPos.offset(-LEAF_SEED_HORIZONTAL_RADIUS, -LEAF_SEED_DOWN_RADIUS, -LEAF_SEED_HORIZONTAL_RADIUS),
                brokenLogPos.offset(LEAF_SEED_HORIZONTAL_RADIUS, LEAF_SEED_UP_RADIUS, LEAF_SEED_HORIZONTAL_RADIUS))) {
            if (!level.hasChunkAt(pos)) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (isVanillaDecayLeaf(state)) {
                awakenVanillaLeafDecay(level, pos.immutable(), state);
            } else if (isSoakedLeaf(state)) {
                soakedLeafSeeds.add(pos.immutable());
            }
        }

        decayUnsupportedSoakedLeaves(level, findConnectedSoakedLeaves(level, brokenLogPos, soakedLeafSeeds), brokenLogPos);
    }

    private static void awakenVanillaLeafDecay(ServerLevel level, BlockPos pos, BlockState state) {
        BlockState decayState = state;
        if (state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) {
            decayState = state.setValue(LeavesBlock.PERSISTENT, false);
            level.setBlock(pos, decayState, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
        }
        level.scheduleTick(pos, decayState.getBlock(), 1);
    }

    private static Set<BlockPos> findConnectedSoakedLeaves(ServerLevel level, BlockPos origin, List<BlockPos> seeds) {
        Set<BlockPos> soakedLeaves = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>(seeds);

        while (!queue.isEmpty() && soakedLeaves.size() < MAX_SOAKED_LEAVES_PER_BREAK) {
            BlockPos pos = queue.removeFirst();
            if (soakedLeaves.contains(pos) || !isWithinSoakedClusterBounds(origin, pos) || !level.hasChunkAt(pos)) {
                continue;
            }

            if (!isSoakedLeaf(level.getBlockState(pos))) {
                continue;
            }

            soakedLeaves.add(pos);
            for (Direction direction : Direction.values()) {
                queue.add(pos.relative(direction));
            }
        }

        return soakedLeaves;
    }

    private static void decayUnsupportedSoakedLeaves(ServerLevel level, Set<BlockPos> soakedLeaves, BlockPos brokenLogPos) {
        for (BlockPos pos : soakedLeaves) {
            if (hasNearbySupportLog(level, pos, brokenLogPos)) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (isSoakedLeaf(state)) {
                Block.dropResources(state, level, pos);
                level.removeBlock(pos, false);
            }
        }
    }

    private static boolean hasNearbySupportLog(ServerLevel level, BlockPos leafPos, BlockPos brokenLogPos) {
        for (BlockPos pos : BlockPos.betweenClosed(
                leafPos.offset(-SOAKED_SUPPORT_RADIUS, -SOAKED_SUPPORT_RADIUS, -SOAKED_SUPPORT_RADIUS),
                leafPos.offset(SOAKED_SUPPORT_RADIUS, SOAKED_SUPPORT_RADIUS, SOAKED_SUPPORT_RADIUS))) {
            if (pos.equals(brokenLogPos) || !level.hasChunkAt(pos)) {
                continue;
            }

            if (isMacabreLog(level.getBlockState(pos))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWithinSoakedClusterBounds(BlockPos center, BlockPos pos) {
        return Math.abs(center.getX() - pos.getX()) <= SOAKED_CLUSTER_HORIZONTAL_RADIUS
                && pos.getY() >= center.getY() - SOAKED_CLUSTER_DOWN_RADIUS
                && pos.getY() <= center.getY() + SOAKED_CLUSTER_UP_RADIUS
                && Math.abs(center.getZ() - pos.getZ()) <= SOAKED_CLUSTER_HORIZONTAL_RADIUS;
    }

    private static boolean isWithinRadius(BlockPos center, BlockPos pos, int radius) {
        return Math.abs(center.getX() - pos.getX()) <= radius
                && Math.abs(center.getY() - pos.getY()) <= radius
                && Math.abs(center.getZ() - pos.getZ()) <= radius;
    }

    private static boolean isMacabreLog(BlockState state) {
        return isSuppliedBlock(state, LOG_BLOCKS);
    }

    private static boolean isVanillaDecayLeaf(BlockState state) {
        return state.getBlock() instanceof LeavesBlock && isSuppliedBlock(state, VANILLA_DECAY_LEAVES);
    }

    private static boolean isSoakedLeaf(BlockState state) {
        return state.is(MacabreModBlocks.SOAKED_LEAVES.get());
    }

    private static boolean isSuppliedBlock(BlockState state, List<Supplier<? extends Block>> blocks) {
        Block block = state.getBlock();
        for (Supplier<? extends Block> supplier : blocks) {
            if (block == supplier.get()) {
                return true;
            }
        }
        return false;
    }
}

package me.usainsrht.utimber.model;

import me.usainsrht.utimber.TimberUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static me.usainsrht.utimber.TimberUtil.isLeaf;
import static me.usainsrht.utimber.TimberUtil.isLog;

public class TreeDetector {

    Set<Block> visited;
    Set<Block> logs;
    Set<Block> leaves;
    Tree tree;
    Block baseBlock;
    DetectedTree detectedTree;
    BlockFace largeLogBaseFace;
    Set<BlockFace> otherLogsBlockFaces;

    public TreeDetector(Block baseBlock, Tree tree) {
        this.baseBlock = baseBlock;
        this.visited = new HashSet<>();
        this.logs = new HashSet<>();
        this.leaves = new HashSet<>();
        this.tree = tree;
    }

    Tree.Part visit(Block block) {
        if (visited.contains(block)) return null;
        visited.add(block);
        if (isLog(block, tree)) {
            logs.add(block);
            return Tree.Part.LOG;
        } else if (isLeaf(block, tree)) {
            leaves.add(block);
            return Tree.Part.LEAF;
        }
        return null;
    }

    public void start() {
        visit(baseBlock);
        if (tree.largeLog) {
            // determine where the base log in 2x2 large log
            Tree.Part[] relativeBlocks = new Tree.Part[4];
            relativeBlocks[0] = visit(baseBlock.getRelative(BlockFace.EAST));
            relativeBlocks[1] = visit(baseBlock.getRelative(BlockFace.SOUTH));
            relativeBlocks[2] = visit(baseBlock.getRelative(BlockFace.WEST));
            relativeBlocks[3] = visit(baseBlock.getRelative(BlockFace.NORTH));
            otherLogsBlockFaces = new HashSet<>();
            if (relativeBlocks[0] == Tree.Part.LOG && relativeBlocks[1] == Tree.Part.LOG) {
                largeLogBaseFace = BlockFace.NORTH_WEST;
                otherLogsBlockFaces.add(BlockFace.SOUTH);
                otherLogsBlockFaces.add(BlockFace.EAST);
                otherLogsBlockFaces.add(BlockFace.SOUTH_EAST);
            } else if (relativeBlocks[1] == Tree.Part.LOG && relativeBlocks[2] == Tree.Part.LOG) {
                largeLogBaseFace = BlockFace.NORTH_EAST;
                otherLogsBlockFaces.add(BlockFace.SOUTH);
                otherLogsBlockFaces.add(BlockFace.WEST);
                otherLogsBlockFaces.add(BlockFace.SOUTH_WEST);
            } else if (relativeBlocks[2] == Tree.Part.LOG && relativeBlocks[3] == Tree.Part.LOG) {
                largeLogBaseFace = BlockFace.SOUTH_EAST;
                otherLogsBlockFaces.add(BlockFace.NORTH);
                otherLogsBlockFaces.add(BlockFace.WEST);
                otherLogsBlockFaces.add(BlockFace.NORTH_WEST);
            } else if (relativeBlocks[3] == Tree.Part.LOG && relativeBlocks[0] == Tree.Part.LOG) {
                largeLogBaseFace = BlockFace.SOUTH_WEST;
                otherLogsBlockFaces.add(BlockFace.NORTH);
                otherLogsBlockFaces.add(BlockFace.EAST);
                otherLogsBlockFaces.add(BlockFace.NORTH_EAST);
            } else {
                largeLogBaseFace = null;
            }
        }
        checkLogs(baseBlock.getRelative(BlockFace.UP));
    }

    public void checkLogs(Block block) {
        Block currentBlock = block;

        while (visit(currentBlock) == Tree.Part.LOG) {;
            if (tree.maxLogHeight > 0 && TimberUtil.getYDiff(currentBlock, baseBlock) >= tree.maxLogHeight) {
                break;
            }

            if (largeLogBaseFace != null) {
                for (BlockFace face : otherLogsBlockFaces) {
                    visit(currentBlock.getRelative(face));
                }
            }

            currentBlock = currentBlock.getRelative(BlockFace.UP);
        }

        checkLeaves(block);
    }

    public void checkLeaves(Block block) {
        BlockFace[] faces = new BlockFace[]{
                BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN
        };
        for (BlockFace blockFace : faces) {
            Block relative = block.getRelative(blockFace);
            if (visit(relative) == Tree.Part.LEAF) {
                checkLeaves(relative);
            }
        }
    }

    @Nullable
    public DetectedTree result() {
        return detectedTree != null ? detectedTree : (detectedTree = new DetectedTree(logs, leaves, tree));
    }

}

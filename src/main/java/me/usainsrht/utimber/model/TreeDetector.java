package me.usainsrht.utimber.model;

import me.usainsrht.utimber.TimberUtil;
import me.usainsrht.utimber.UTimber;
import org.bukkit.Bukkit;
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

    public Tree.Part visit(Block block) {
        return visit(block, null);
    }

    public Tree.Part visit(Block block, Tree.Part addIf) {
        if (visited.contains(block)) return null;
        if (isLog(block, tree)) {
            if (addIf == Tree.Part.LOG) {
                visited.add(block);
                logs.add(block);
            }
            return Tree.Part.LOG;
        } else if (isLeaf(block, tree)) {
            if (addIf == Tree.Part.LEAF) {
                visited.add(block);
                leaves.add(block);
            }
            return Tree.Part.LEAF;
        }
        return null;
    }

    public void start() {
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
                if (UTimber.instance.debug) Bukkit.broadcastMessage("Large log tree but not a 2x2 log base " + tree.name);
                largeLogBaseFace = null;
            }
        }
        checkLogs(baseBlock);
    }

    public static int[][] branchSearchOffsets = new int[][]{
            {1,1,1}, {1,1,0}, {1,1,-1},
            {0,1,1}, {0,1,0}, {0,1,-1},
            {-1,1,1}, {-1,1,0}, {-1,1,-1},

            {1,0,1}, {1,0,0}, {1,0,-1},
            {0,0,1},          {0,0,-1},
            {-1,0,1}, {-1,0,0}, {-1,0,-1},

            /*{1,-1,1}, {1,-1,0}, {1,-1,-1},
            {0,-1,1}, {0,-1,0}, {0,-1,-1},
            {-1,-1,1}, {-1,-1,0}, {-1,-1,-1},*/
    };
    public void checkLogs(Block block) {

        if (!checkLog(block)) {
            if (block != baseBlock) {
                checkLeaves(block, block);
            }
            return;
        }

        for (int[] offsets : tree.logDistanceX > 0 ? branchSearchOffsets : new int[][]{{0,1,0}}) {
            Block relative = block.getRelative(offsets[0], offsets[1], offsets[2]);
            checkLogs(relative);
            if (tree.separateLeaves) {
                checkLeaves(block, relative);
            }
        }
    }

    public boolean checkLog(Block block) {
        if (TimberUtil.getXDiff(baseBlock, block) > tree.logDistanceX) {
            return false;
        }
        if (TimberUtil.getYDiff(baseBlock, block) > tree.logDistanceY) {
            return false;
        }
        return visit(block, Tree.Part.LOG) == Tree.Part.LOG;
    }

    public static int[][] leaveSearchOffsetsDiagonal = new int[][]{
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1},
            {1, 1, 1}, {1, 1, -1}, {1, -1, 1}, {1, -1, -1},
            {-1, 1, 1}, {-1, 1, -1}, {-1, -1, 1}, {-1, -1, -1}
    };
    public static int[][] leaveSearchOffsetsCardinal = new int[][]{
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
    };
    public void checkLeaves(Block log, Block leaf) {
        for (int[] offsets : tree.diagonalLeaves ? leaveSearchOffsetsDiagonal : leaveSearchOffsetsCardinal) {
            Block relative = leaf.getRelative(offsets[0], offsets[1], offsets[2]);
            if (TimberUtil.getXDiff(relative, log) > tree.leafDistanceX) {
                continue;
            }
            if (TimberUtil.getYDiff(relative, log) > tree.leafDistanceY) {
                continue;
            }
            if (visit(relative, Tree.Part.LEAF) == Tree.Part.LEAF) {
                checkLeaves(log, relative);
            }
        }
    }

    @Nullable
    public DetectedTree result() {
        if (logs.size() < tree.minLogs) {
            if (UTimber.instance.debug) Bukkit.broadcastMessage("min logs not met: " + logs.size() + " < " + tree.minLogs + " " + tree.name);
            return null;
        }
        if (leaves.size() < tree.minLeaves) {
            if (UTimber.instance.debug) Bukkit.broadcastMessage("min leaves not met: " + leaves.size() + " < " + tree.minLeaves + " " + tree.name);
            return null;
        }
        return detectedTree != null ? detectedTree : (detectedTree = new DetectedTree(logs, leaves, tree));
    }

}

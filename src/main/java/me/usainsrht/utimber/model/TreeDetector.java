package me.usainsrht.utimber.model;

import me.usainsrht.utimber.TimberUtil;
import me.usainsrht.utimber.UTimber;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;

import static me.usainsrht.utimber.TimberUtil.isLeaf;
import static me.usainsrht.utimber.TimberUtil.isLog;

public class TreeDetector {

    Set<Block> visitedLogs = new HashSet<>();
    Set<Block> visitedLeaves = new HashSet<>();
    Set<Block> logs = new HashSet<>();
    Set<Block> leaves = new HashSet<>();
    Tree tree;
    Block baseBlock;
    DetectedTree detectedTree;
    BlockFace largeLogBaseFace;
    Set<BlockFace> otherLogsBlockFaces;
    //List<Block> debugVisits = new ArrayList<>();

    public TreeDetector(Block baseBlock, Tree tree) {
        this.baseBlock = baseBlock;
        this.tree = tree;
    }

    public Tree.Part visit(Block block) {
        return visit(block, null);
    }

    public Tree.Part visit(Block block, Tree.Part addIf) {
        if (addIf == Tree.Part.LOG) {
            if (visitedLogs.contains(block)) return null;
            //debugVisits.add(block);
            visitedLogs.add(block);
            if (isLog(block, tree)) {
                logs.add(block);
                return Tree.Part.LOG;
            }
        } else if (addIf == Tree.Part.LEAF) {
            if (visitedLeaves.contains(block)) return null;
            //debugVisits.add(block);
            visitedLeaves.add(block);
            if (isLeaf(block, tree)) {
                leaves.add(block);
                return Tree.Part.LEAF;
            }
        } else {
            if (isLog(block, tree)) return Tree.Part.LOG;
            if (isLeaf(block, tree)) return Tree.Part.LEAF;
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
            {0,1,0}, /*one block above, put as first element for optimization*/

            {1,0,1}, {1,0,0}, {1,0,-1},
            {0,0,1},          {0,0,-1},
            {-1,0,1}, {-1,0,0}, {-1,0,-1},

            {1,1,1}, {1,1,0}, {1,1,-1},
            {0,1,1},          {0,1,-1},
            {-1,1,1}, {-1,1,0}, {-1,1,-1},

            /*{1,-1,1}, {1,-1,0}, {1,-1,-1},
            {0,-1,1}, {0,-1,0}, {0,-1,-1},
            {-1,-1,1}, {-1,-1,0}, {-1,-1,-1},*/
    };
    public static int[][] branchSearchOffsetsDown = new int[][]{
            {0,1,0}, /*one block above, put as first element for optimization*/

            {1,0,1}, {1,0,0}, {1,0,-1},
            {0,0,1},          {0,0,-1},
            {-1,0,1}, {-1,0,0}, {-1,0,-1},

            {1,1,1}, {1,1,0}, {1,1,-1},
            {0,1,1},          {0,1,-1},
            {-1,1,1}, {-1,1,0}, {-1,1,-1},

            {1,-1,1}, {1,-1,0}, {1,-1,-1},
            {0,-1,1}, {0,-1,0}, {0,-1,-1},
            {-1,-1,1}, {-1,-1,0}, {-1,-1,-1}
    };

    public void checkLogs(Block block) {

        if (!checkLog(block)) {
            if (block != baseBlock) {
                visit(block, Tree.Part.LEAF);
                checkLeaves(block, block);
            }
            return;
        }

        int[][] offsetsList;
        if (tree.logDistanceX > 0) {
            offsetsList = tree.downwardLogs ? branchSearchOffsetsDown : branchSearchOffsets;
        } else {
            offsetsList = tree.downwardLogs ? new int[][]{{0,1,0}, {0,-1,0}} : new int[][]{{0,1,0}};
        }

        for (int[] offsets : offsetsList) {
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
            {1, -1, 0},{-1, -1, 0},{0, -1, 1},{0, -1, -1},
            {1, 1, 1}, {1, 1, -1}, {1, -1, 1}, {1, -1, -1},
            {-1, 1, 1}, {-1, 1, -1}, {-1, -1, 1}, {-1, -1, -1}
    };
    public static int[][] leaveSearchOffsetsCardinal = new int[][]{
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
    };
    public void checkLeaves(Block log, Block leaf) {
        //gets leaves with a clear block gap
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

    public DetectedTree result() {
        if (logs.size() < tree.minLogs) {
            if (UTimber.instance.debug) Bukkit.broadcastMessage("min logs not met: " + logs.size() + " < " + tree.minLogs + " " + tree.name);
            return null;
        }
        if (leaves.size() < tree.minLeaves) {
            if (UTimber.instance.debug) Bukkit.broadcastMessage("min leaves not met: " + leaves.size() + " < " + tree.minLeaves + " " + tree.name);
            return null;
        }
        /*Bukkit.broadcastMessage("total visits: " + debugVisits.size() + " logs " + logs.size() + " leaves " + leaves.size() + " " + tree.name);
        int i = 0;
        for (Block b : debugVisits) {
            Bukkit.getScheduler().runTaskLater(UTimber.instance, () -> {
                //long amount = IntStream.range(0, debugVisits.size()).filter(index -> Objects.equals(debugVisits.get(index), b)).count();
                Material[] jetGlass = {
                        Material.BLUE_STAINED_GLASS,
                        Material.LIGHT_BLUE_STAINED_GLASS,
                        Material.CYAN_STAINED_GLASS,
                        Material.GREEN_STAINED_GLASS,
                        Material.LIME_STAINED_GLASS,
                        Material.YELLOW_STAINED_GLASS,
                        Material.ORANGE_STAINED_GLASS,
                        Material.RED_STAINED_GLASS,
                        Material.MAGENTA_STAINED_GLASS,
                        Material.PURPLE_STAINED_GLASS,
                        Material.PINK_STAINED_GLASS,
                        Material.WHITE_STAINED_GLASS,
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.GRAY_STAINED_GLASS,
                        Material.BLACK_STAINED_GLASS,
                        Material.BROWN_STAINED_GLASS
                };
                b.setType(b.getType() == Material.AIR
                        ? jetGlass[0]
                        : IntStream.range(0, jetGlass.length)
                        .filter(index -> jetGlass[index] == b.getType())
                        .mapToObj(index -> index + 1 < jetGlass.length ? jetGlass[index + 1] : Material.GOLD_BLOCK)
                        .findFirst()
                        .orElse(b.getType()));
            }, (long)(i++ * 0.5));
        }*/
        return detectedTree != null ? detectedTree : (detectedTree = new DetectedTree(logs, leaves, tree, otherLogsBlockFaces));
    }

}

package me.usainsrht.utimber.model;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Set;

public class DetectedTree {

    public Set<Block> logs;
    public Set<Block> leaves;
    public Tree tree;
    public Set<BlockFace> otherLogsBlockFaces;

    public DetectedTree(Set<Block> logs, Set<Block> leaves, Tree tree, Set<BlockFace> otherLogsBlockFaces) {
        this.logs = logs;
        this.leaves = leaves;
        this.tree = tree;
        this.otherLogsBlockFaces = otherLogsBlockFaces;
    }

}

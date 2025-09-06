package me.usainsrht.utimber.model;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nullable;
import java.util.Set;

public class DetectedTree {

    public Set<Block> logs;
    public Set<Block> leaves;
    public Tree tree;
    public @Nullable Set<BlockFace> otherLogsBlockFaces;

    public DetectedTree(Set<Block> logs, Set<Block> leaves, Tree tree, @Nullable Set<BlockFace> otherLogsBlockFaces) {
        this.logs = logs;
        this.leaves = leaves;
        this.tree = tree;
        this.otherLogsBlockFaces = otherLogsBlockFaces;
    }

}

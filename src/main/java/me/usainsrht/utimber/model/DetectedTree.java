package me.usainsrht.utimber.model;

import org.bukkit.block.Block;

import java.util.Set;

public class DetectedTree {

    public Set<Block> logs;
    public Set<Block> leaves;
    public Tree tree;

    public DetectedTree(Set<Block> logs, Set<Block> leaves, Tree tree) {
        this.logs = logs;
        this.leaves = leaves;
        this.tree = tree;
    }

}

package me.usainsrht.utimber;

import me.usainsrht.utimber.model.Tree;
import me.usainsrht.utimber.model.DetectedTree;
import org.bukkit.Material;
import org.bukkit.block.Block;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class TreeDetector {

    @Nullable
    public static Tree detectTree(Block baseBlock) {

        Tree tree = UTimber.instance.trees.stream().filter(t -> t.logs.contains(baseBlock.getType())).findFirst().orElse(null);

        return tree;
    }

    public static DetectedTree detectTree(Block baseBlock, Tree tree) {

        Set<Block> visited = new HashSet<>();
        Set<Block> logs = new HashSet<>();
        Set<Block> leaves = new HashSet<>();

        new Object() {
            void visit(Block block) {
                if (visited.contains(block)) return;
                visited.add(block);
                if (isLog(block, tree)) {
                    logs.add(block);
                } else if (isLeaf(block, tree)) {
                    leaves.add(block);
                }
            }
        };

        return new DetectedTree(logs, leaves, tree);
    }

    public void destroyTree(DetectedTree detectedTree) {
        detectedTree.logs.forEach(block -> block.setType(Material.AIR));
        detectedTree.leaves.forEach(block -> block.setType(Material.AIR));
        detectedTree.logs.stream().findFirst().ifPresent(block -> {
            block.getWorld().playSound(block.getLocation(), "block.chest.open", 10f, 0.1f);
        });
    }

    public static boolean isLog(Block block, Tree tree) {
        return tree.logs.contains(block.getType());
    }

    public static boolean isLeaf(Block block, Tree tree) {
        return tree.leaves.contains(block.getType());
    }

    public static boolean isTimberTool(Material material) {
        if (material == null || material.isAir()) return false;

        return UTimber.instance.getConfig().getStringList("tools").stream().anyMatch(tool -> tool.equalsIgnoreCase(material.name()));
    }

}

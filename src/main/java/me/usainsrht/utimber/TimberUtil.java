package me.usainsrht.utimber;

import me.usainsrht.utimber.model.Tree;
import me.usainsrht.utimber.model.DetectedTree;
import me.usainsrht.utimber.model.TreeDetector;
import org.bukkit.Material;
import org.bukkit.block.Block;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class TimberUtil {

    @Nullable
    public static Tree detectTree(Block baseBlock) {

        Tree tree = UTimber.instance.trees.stream().filter(t -> t.logs.contains(baseBlock.getType())).findFirst().orElse(null);

        return tree;
    }

    public static DetectedTree detectTree(Block baseBlock, Tree tree) {

        TreeDetector detector = new TreeDetector(baseBlock, tree);

        detector.start();

        return detector.result();
    }

    public static void destroyTree(DetectedTree detectedTree) {
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

    public static int getYDiff(Block a, Block b) {
        return Math.abs(a.getY() - b.getY());
    }

    public static int getXDiff(Block a, Block b) {
        return Math.abs(a.getX() - b.getX());
    }

}

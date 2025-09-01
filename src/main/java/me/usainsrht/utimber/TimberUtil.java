package me.usainsrht.utimber;

import me.usainsrht.utimber.model.DetectedTree;
import me.usainsrht.utimber.model.Tree;
import me.usainsrht.utimber.model.TreeDetector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class TimberUtil {

    @Nullable
    public static Tree detectTree(Block baseBlock) {

        Tree tree = UTimber.instance.trees.stream()
                .filter(t -> t.logs.contains(baseBlock.getType()))
                .filter(t -> {
                    if (t.largeLog) {
                        Block north = baseBlock.getRelative(BlockFace.NORTH);
                        Block south = baseBlock.getRelative(BlockFace.SOUTH);
                        Block east = baseBlock.getRelative(BlockFace.EAST);
                        Block west = baseBlock.getRelative(BlockFace.WEST);

                        if (isLog(north, t) && isLog(east, t)) return true;
                        if (isLog(north, t) && isLog(west, t)) return true;
                        if (isLog(south, t) && isLog(east, t)) return true;
                        if (isLog(south, t) && isLog(west, t)) return true;

                        return false;
                    }
                    return true;
                })
                .max(Comparator.comparing(t -> t.largeLog))
                .orElse(null);

        return tree;
    }

    public static DetectedTree detectTree(Block baseBlock, Tree tree) {

        TreeDetector detector = new TreeDetector(baseBlock, tree);

        detector.start();

        return detector.result();
    }

    public static void destroyTree(DetectedTree detectedTree, ItemStack tool, Entity entity) {
        Vector random = new Vector(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), 0.00, ThreadLocalRandom.current().nextDouble(-0.02, 0.02));
        detectedTree.logs.forEach(block -> {
            //block.breakNaturally();
            if (UTimber.instance.debug) block.setType(Material.BROWN_STAINED_GLASS);
            else {
                spawnFallingBlock(block, random, block.getDrops(tool, entity));
                block.setType(Material.AIR);
            }
        });
        detectedTree.leaves.forEach(block -> {
            //block.breakNaturally();
            if (UTimber.instance.debug) block.setType(Material.LIME_STAINED_GLASS);
            else {
                spawnFallingBlock(block, random, block.getDrops(tool, entity));
                block.setType(Material.AIR);
            }
        });

        detectedTree.logs.stream().findFirst().ifPresent(block -> {
            block.getWorld().playSound(block.getLocation(), "block.chest.open", 10f, 0.1f);
        });

        Bukkit.getScheduler().runTaskLater(UTimber.instance, () -> {
            detectedTree.logs.stream().findFirst().ifPresent(block -> {
                block.getWorld().playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1f, 1f);
            });
            detectedTree.leaves.stream().findFirst().ifPresent(block -> {
                block.getWorld().playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1f, 1f);
            });

        }, 20L);
    }

    public static void spawnFallingBlock(Block block, Vector vector, Collection<ItemStack> drops) {
        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().clone().add(0.5,0,0.5), block.getBlockData());
        fallingBlock.setHurtEntities(UTimber.instance.getConfig().getBoolean("tree_falling_damage", false));
        fallingBlock.setDropItem(false);
        fallingBlock.setGravity(false);
        fallingBlock.setMetadata("utimber", new FixedMetadataValue(UTimber.instance, drops));
        Bukkit.getScheduler().runTaskTimer(UTimber.instance, task -> {
            fallingBlock.setVelocity(vector);
            if (fallingBlock.getTicksLived() > 20) {
                fallingBlock.setGravity(true);
                task.cancel();
            }
        }, 1L, 1L);
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
        return Math.max(
                Math.abs(a.getX() - b.getX()),
                Math.abs(a.getZ() - b.getZ())
        );
    }

}

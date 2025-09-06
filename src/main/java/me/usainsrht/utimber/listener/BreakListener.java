package me.usainsrht.utimber.listener;

import me.usainsrht.utimber.TimberUtil;
import me.usainsrht.utimber.UTimber;
import me.usainsrht.utimber.model.DetectedTree;
import me.usainsrht.utimber.model.Tree;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.Objects;

public class BreakListener implements Listener {

    UTimber plugin;

    public BreakListener() {
        plugin = UTimber.instance;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {

        Player player = e.getPlayer();

        if (plugin.getConfig().getBoolean("crouch_disable", true) && player.isSneaking()) {
            return;
        }

        if (plugin.getConfig().getBoolean("require_tool", false) && !TimberUtil.isTimberTool(player.getInventory().getItemInMainHand().getType())) {
            return;
        }

        Block block = e.getBlock();

        long start = System.currentTimeMillis();

        List<Tree> trees = TimberUtil.detectTree(block);
        if (trees == null ||trees.isEmpty()) return;

        DetectedTree detectedTree = trees.stream()
                .map(tree -> TimberUtil.detectTree(block, tree))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (detectedTree == null) return;

        TimberUtil.destroyTree(detectedTree, player.getInventory().getItemInMainHand(), player);

        if (plugin.getConfig().getBoolean("damage_tool_by_log_count", false) && player.getGameMode() != GameMode.CREATIVE) {
            ItemStack tool = player.getInventory().getItemInMainHand();
            if (tool != null && !tool.getType().isAir() && tool.getItemMeta() instanceof Damageable damageable) {
                int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);
                int durabilityLoss = Math.max(1, detectedTree.logs.size() / (unbreakingLevel+1) );
                damageable.setDamage(damageable.getDamage() + durabilityLoss);
                if (damageable.getDamage() >= tool.getType().getMaxDurability() && !damageable.isUnbreakable()) {
                    player.getInventory().setItemInMainHand(null);
                    player.getWorld().playSound(player.getLocation(), "entity.item.break", 1f, 1f);
                } else tool.setItemMeta(damageable);
            }
        }

        if (plugin.debug) player.sendMessage(detectedTree.tree.name + " logs: " + detectedTree.logs.size() + ", leaves: " + detectedTree.leaves.size() + " (" + (System.currentTimeMillis() - start) + "ms)");

        //check replant setting and block if applicable
        if (plugin.getConfig().getBoolean("replant_sapling", true) && detectedTree.tree.sapling != null && !detectedTree.tree.sapling.equals(Material.AIR)) {
            Block below = block.getRelative(BlockFace.DOWN);
            if (plugin.getConfig().getStringList("replantable_blocks").stream().anyMatch(material -> below.getType().toString().equalsIgnoreCase(material))) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    e.getBlock().setType(detectedTree.tree.sapling);
                    if (detectedTree.otherLogsBlockFaces != null) {
                        for (BlockFace face : detectedTree.otherLogsBlockFaces) {
                            Block relative = e.getBlock().getRelative(face);
                            relative.setType(detectedTree.tree.sapling);
                        }
                    }
                }, 2L);

            }
        }


    }

}

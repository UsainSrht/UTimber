package me.usainsrht.utimber.listener;

import me.usainsrht.utimber.TimberUtil;
import me.usainsrht.utimber.UTimber;
import me.usainsrht.utimber.model.DetectedTree;
import me.usainsrht.utimber.model.Tree;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

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

        Tree tree = TimberUtil.detectTree(block);
        if (tree == null) return;

        DetectedTree detectedTree = TimberUtil.detectTree(block, tree);
        if (detectedTree == null) return;

        TimberUtil.destroyTree(detectedTree, player.getInventory().getItemInMainHand(), player);

        if (plugin.debug) player.sendMessage(detectedTree.tree.name + " logs: " + detectedTree.logs.size() + ", leaves: " + detectedTree.leaves.size() + " (" + (System.currentTimeMillis() - start) + "ms)");

        //check replant setting and block if applicable
        if (plugin.getConfig().getBoolean("replant_sapling", true) && detectedTree.tree.sapling != null && !detectedTree.tree.sapling.equals(Material.AIR)) {
            Block below = block.getRelative(BlockFace.DOWN);
            if (plugin.getConfig().getStringList("replantable_blocks").stream().anyMatch(material -> below.getType().toString().equalsIgnoreCase(material))) {
                e.getBlock().setType(detectedTree.tree.sapling);
            }
        }


    }

}

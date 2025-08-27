package me.usainsrht.utimber.listener;

import me.usainsrht.utimber.TimberUtil;
import me.usainsrht.utimber.UTimber;
import me.usainsrht.utimber.model.DetectedTree;
import me.usainsrht.utimber.model.Tree;
import org.bukkit.block.Block;
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

        Tree tree = TimberUtil.detectTree(block);
        if (tree == null) return;

        DetectedTree detectedTree = TimberUtil.detectTree(block, tree);
        if (detectedTree == null) return;

        TimberUtil.destroyTree(detectedTree);

        //check replant setting and block if applicable

    }

}

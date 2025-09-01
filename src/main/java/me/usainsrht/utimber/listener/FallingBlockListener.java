package me.usainsrht.utimber.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FallingBlockListener implements Listener {

    @EventHandler
    public void onFallingBlock(EntityChangeBlockEvent e) {
        if (e.getEntityType() != org.bukkit.entity.EntityType.FALLING_BLOCK) return;

        Entity entity = e.getEntity();

        if (!entity.hasMetadata("utimber")) return;

        entity.getWorld().playSound(e.getBlock().getLocation(), ((FallingBlock) entity).getBlockData().getSoundGroup().getBreakSound(), 1f, 1f);
        entity.getMetadata("utimber").forEach(metadata -> {
            if (metadata.value() == null) return;
            ((List<ItemStack>) metadata.value()).forEach(item -> {
                entity.getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
            });
        });

        e.setCancelled(true);
        entity.remove();


    }

}

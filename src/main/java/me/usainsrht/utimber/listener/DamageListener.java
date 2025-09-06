package me.usainsrht.utimber.listener;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.FALLING_BLOCK) return;

        if (!(e.getEntity() instanceof Item item)) return;

        if (!e.getDamager().hasMetadata("utimber")) return;

        e.setCancelled(true);
    }

}

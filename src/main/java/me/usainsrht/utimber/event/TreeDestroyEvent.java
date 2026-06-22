package me.usainsrht.utimber.event;

import me.usainsrht.utimber.model.DetectedTree;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class TreeDestroyEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final DetectedTree detectedTree;
    private final ItemStack tool;
    private boolean cancelled;

    public TreeDestroyEvent(Player player, DetectedTree detectedTree, ItemStack tool) {
        this.player = player;
        this.detectedTree = detectedTree;
        this.tool = tool;
    }

    public Player getPlayer() {
        return player;
    }

    public DetectedTree getDetectedTree() {
        return detectedTree;
    }

    public ItemStack getTool() {
        return tool;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

package me.Fupery.ArtMap.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when the player opens a help menu
 */
public class PlayerOpenMenuEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public PlayerOpenMenuEvent(Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

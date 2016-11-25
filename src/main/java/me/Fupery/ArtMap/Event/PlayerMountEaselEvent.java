package me.Fupery.ArtMap.Event;

import me.Fupery.ArtMap.Easel.Easel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerMountEaselEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Easel easel;
    private boolean cancelled;

    public PlayerMountEaselEvent(Player who, Easel easel) {
        super(who);
        this.easel = easel;
        cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Easel getEasel() {
        return easel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}

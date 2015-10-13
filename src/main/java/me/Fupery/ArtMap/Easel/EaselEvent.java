package me.Fupery.ArtMap.Easel;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EaselEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    Easel easel;
    ClickType click;
    Player player;
    private boolean cancelled;

    public EaselEvent(Easel easel, ClickType click, Player player) {
        this.easel = easel;
        this.click = click;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Easel getEasel() {
        return easel;
    }

    public ClickType getClick() {
        return click;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static enum ClickType {
        LEFT_CLICK, RIGHT_CLICK, SHIFT_RIGHT_CLICK
    }
}

package me.Fupery.ArtMap.Event;

import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerCraftArtMaterialEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ArtMaterial material;
    private final ItemStack[] ingredients;
    private boolean cancelled = false;
    private final CraftItemEvent event;

    public PlayerCraftArtMaterialEvent(CraftItemEvent event, ArtMaterial material) {
        super(((Player) event.getWhoClicked()));
        this.event = event;
        this.material = material;
        this.ingredients = event.getInventory().getMatrix();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ArtMaterial getMaterial() {
        return material;
    }

    public void setCraftEventCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    public ItemStack getResult() {
        return event.getCurrentItem();
    }

    public void setResult(ItemStack result) {
        event.setCurrentItem(result);
    }

    public ItemStack[] getIngredients() {
        return ingredients;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

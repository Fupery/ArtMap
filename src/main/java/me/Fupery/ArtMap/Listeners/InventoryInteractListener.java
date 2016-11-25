package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

class InventoryInteractListener implements RegisteredListener {

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        checkPreviewing(event.getPlayer(), event);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        checkPreviewing(((Player) event.getWhoClicked()), event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (ArtMap.getPreviewing().containsKey(event.getPlayer())) {
            event.getItemDrop().remove();
            Preview.stop(event.getPlayer());
        }
        if (isKitDrop(event.getPlayer(), event.getItemDrop().getItemStack(), event)) {
            event.getItemDrop().remove();
        }
    }

    private void checkPreviewing(Player player, Cancellable event) {
        if (ArtMap.getPreviewing().containsKey(player)) {
            event.setCancelled(true);
            Preview.stop(player);
        }
    }

    private boolean isKitDrop(Player player, ItemStack itemStack, Cancellable event) {
        if (ArtMap.getArtistHandler().containsPlayer(player)) {
            if (ItemUtils.hasKey(itemStack, ArtItem.KIT_KEY)) return true;
        }
        return false;
    }

    @Override
    public void unregister() {
        PlayerItemHeldEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
    }
}

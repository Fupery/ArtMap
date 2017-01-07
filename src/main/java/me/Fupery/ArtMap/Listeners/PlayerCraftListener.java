package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Colour.ArtDye;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Event.PlayerCraftArtMaterialEvent;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

class PlayerCraftListener implements RegisteredListener {

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {
        ItemStack result = event.getCurrentItem();
        // Disallow players from copying ArtMap maps in the crafting table
        if (result.getType() == Material.MAP) {
            MapArt art = ArtMap.getArtDatabase().getArtwork(result.getDurability());
            if (art != null) {
                if (event.getWhoClicked().getUniqueId().equals(art.getArtistPlayer().getUniqueId())) {
                    Player player = (Player) event.getWhoClicked();
                    ItemStack artworkItem = art.getMapItem();
                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        onShiftClick(artworkItem, player, event);
                    } else {
                        result.setItemMeta(artworkItem.getItemMeta());
                    }
                } else {
                    Lang.NO_CRAFT_PERM.send(event.getWhoClicked());
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
            //return the old dye from a crafted paintbucket
        } else if (ArtMaterial.PAINT_BUCKET.isValidMaterial(result)) {
            PlayerCraftArtMaterialEvent craftEvent = new PlayerCraftArtMaterialEvent(event, ArtMaterial.PAINT_BUCKET);
            Bukkit.getPluginManager().callEvent(craftEvent);
            if (craftEvent.isCancelled()) return;
            boolean kitItem = false;

            //check if any items involved are from an ArtKit - if so, tag the results of the craft
            for (ItemStack ingredient : event.getInventory().getMatrix()) {
                if (ItemUtils.hasKey(ingredient, ArtItem.KIT_KEY)) {
                    kitItem = true;
                    break;
                }
            }
            for (ItemStack ingredient : event.getInventory().getMatrix()) {
                if (ArtMaterial.PAINT_BUCKET.isValidMaterial(ingredient)) {
                    ArtDye dye = ArtItem.DyeBucket.getColour(ArtMap.getColourPalette(), ingredient);
                    if (dye == null) continue;
                    ItemStack previousDye = dye.toItem();
                    if (kitItem) previousDye = ItemUtils.addKey(previousDye, ArtItem.KIT_KEY);
                    ItemUtils.giveItem((Player) event.getWhoClicked(), previousDye);
                }
            }
            if (kitItem) {
                ItemStack newResult = ItemUtils.addKey(result, ArtItem.KIT_KEY);
                event.setCurrentItem(newResult);
            }
        }
    }

    private void onShiftClick(ItemStack artworkItem, Player player, CraftItemEvent event) {
        event.setCancelled(true);

        int i = 0;
        ItemStack[] items = event.getInventory().getMatrix();
        for (ItemStack item : items) {

            if (item != null) {
                i += item.getAmount();
            }
        }
        event.getInventory().setMatrix(new ItemStack[items.length]);
        artworkItem.setAmount(i);
        ItemUtils.giveItem(player, artworkItem);
    }

    @Override
    public void unregister() {
        CraftItemEvent.getHandlerList().unregister(this);
    }
}
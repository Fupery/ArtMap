package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// Disallows players from copying ArtMap maps in the crafting table
public class PlayerCraftListener implements Listener {

    private final ArtMap plugin;

    public PlayerCraftListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {

        event.getWhoClicked().sendMessage("boop");

        ItemStack result = event.getCurrentItem();

        if (result.getType() == Material.MAP && result.hasItemMeta()) {

            MapArt art = MapArt.getArtwork(plugin, result.getDurability());

            if (art != null) {

                if (event.getWhoClicked().getUniqueId().equals(art.getPlayer().getUniqueId())) {

                    int carbonCopies = 0;

                    for (ItemStack item : event.getInventory().getMatrix()) {

                        if (item != null && item.hasItemMeta()) {
                            ItemMeta itemMeta = item.getItemMeta();

                            if (item.getType() == Material.EMPTY_MAP
                                    && itemMeta.hasDisplayName()
                                    && itemMeta.getDisplayName().equals(ArtItem.carbonPaperKey)) {
                                carbonCopies++;
                            }
                        }
                    }

                    if (carbonCopies == 0) {

                        Player player = (Player) event.getWhoClicked();

                        ItemStack artworkItem = art.getMapItem();

                        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                            onShiftClick(artworkItem, player, event);

                        } else {
                            result.setItemMeta(artworkItem.getItemMeta());
                        }

                    } else {

                        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                            event.setResult(Event.Result.DENY);
                            return;
                        }
//                        ItemMeta carbonMeta = Recipe.getActivatedCarbonPaper();
//                        List<String> lore = carbonMeta.getLore();
//                        lore.set(0, "§r" + result.getItemMeta().getDisplayName());
//
//                        ItemMeta resultMeta = result.getItemMeta();
//                        resultMeta.setLore(lore);
//                        resultMeta.setDisplayName(carbonMeta.getDisplayName());
//
//                        result.setItemMeta(resultMeta);
//                        result.setDurability((short) 0);
//                        result.setType(Material.PAPER);
//                        result.setAmount(1);
                        result = ArtMaterial.fillCarbonPaper(art);
                    }

                } else {
                    event.getWhoClicked().sendMessage(ArtMap.Lang.NO_CRAFT_PERM.message());
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }
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
        ItemStack leftOver = player.getInventory().addItem(artworkItem).get(0);

        if (leftOver != null && leftOver.getAmount() > 0) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
        }
    }
}
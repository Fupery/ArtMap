package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

public class EaselInteractListener implements Listener {

    private final ArtMap plugin;

    public EaselInteractListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEaselInteract(EaselEvent event) {

        Player player = event.getPlayer();
        final Easel easel = event.getEasel();

        if (!player.hasPermission("artmap.artist")) {
            player.sendRawMessage(ArtMap.Lang.ELSE_USING.message());
            return;
        }

        if (easel.isPainting()) {
            player.sendMessage(ArtMap.Lang.NO_PERM.message());
            return;
        }

        switch (event.getClick()) {

            case LEFT_CLICK:

                player.sendMessage(ArtMap.Lang.EASEL_HELP.message());
                return;

            case RIGHT_CLICK:

                //If the easel has a canvas, player rides the easel
                if (easel.getFrame().getItem().getType() != Material.AIR) {
                    easel.rideEasel(player);
                    return;
                }

                ItemStack itemInHand = player.getItemInHand();

                //Player must use a canvas on the easel
                if (itemInHand.getType() != Material.PAPER) {
                    player.sendMessage(ArtMap.Lang.NEED_CANVAS.message());
                    return;
                }

                ItemMeta itemInHandMeta = itemInHand.getItemMeta();

                //Check if canvas has correct metadata
                if (!itemInHandMeta.hasDisplayName()) {
                    player.sendMessage(ArtMap.Lang.NOT_A_CANVAS.message());
                    return;
                }

                MapView mapView = null;
                ArtMaterial material = ArtMaterial.getCraftItemType(itemInHand);

                //Check if carbon paper links to a valid artwork
                if (material == ArtMaterial.CARBON_PAPER_FILLED) {

                    if (itemInHandMeta.hasLore()) {
                        String loreID = itemInHandMeta.getLore().get(0);
                        int a = loreID.indexOf("[") + 1, b = loreID.lastIndexOf("]");

                        if (a < 0 || b < 0) {
                            return;
                        }
                        String title = loreID.substring(a, b);
                        MapArt art = MapArt.getArtwork(plugin, title);

                        if (art != null) {
                            mapView = MapArt.cloneArtwork(plugin,
                                    player.getWorld(), art.getMapID());
                        }
                    }

                    if (mapView != null) {
                        easel.mountCanvas(mapView);

                        if (easel.getItem() != null) {
                            ItemStack removed = itemInHand.clone();
                            removed.setAmount(1);
                            player.getInventory().removeItem(removed);
                        }
                        return;
                    }
                    player.sendMessage(ArtMap.Lang.NEED_TO_COPY.message());
                    return;

                    //Mount the canvas
                } else if (material == ArtMaterial.CARBON_PAPER) {
                    player.sendMessage(ArtMap.Lang.NEED_TO_COPY.message());
                    return;

                } else if (material == ArtMaterial.CANVAS) {
                    mapView = MapArt.generateMapID(plugin, player.getWorld());
                    plugin.getNmsInterface().setWorldMap(mapView, MapArt.blankMap);
                    easel.mountCanvas(mapView);

                    if (easel.getItem() != null) {
                        ItemStack removed = itemInHand.clone();
                        removed.setAmount(1);
                        player.getInventory().removeItem(removed);
                    }
                    return;
                }
                break;

            case SHIFT_RIGHT_CLICK:

                MapArt art = null;

                if (easel.hasItem()) {
                    ItemStack easelItem = easel.getItem();

                    if (ArtMaterial.MAP_ART.isValidMaterial(easelItem)) {
                        art = MapArt.getArtwork(plugin, easelItem.getItemMeta().getDisplayName());
                    }

                    if (art != null) {

                        if (!art.getPlayer().getUniqueId().equals(player.getUniqueId())
                                && !player.hasPermission("artmap.admin")) {

                            player.sendMessage(String.format(ArtMap.Lang.NOT_YOUR_EASEL.message(),
                                    art.getPlayer().getName()));
                            return;
                        }

                    } else {
                        final short id = easelItem.getDurability();

                        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                            @Override
                            public void run() {
                                MapArt.recycleID(plugin, id);
                            }
                        });
                    }
                    easel.removeItem();
                }
                easel.breakEasel();
                break;
        }
    }
}

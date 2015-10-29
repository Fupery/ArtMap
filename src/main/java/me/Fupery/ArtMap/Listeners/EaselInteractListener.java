package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

public class EaselInteractListener implements Listener {

    ArtMap plugin;

    public EaselInteractListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEaselInteract(EaselEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("artmap.artist")) {

            final Easel easel = event.getEasel();

            if (!easel.isPainting()) {

                switch (event.getClick()) {

                    case LEFT_CLICK:

                        player.sendMessage(ArtMap.Lang.EASEL_HELP.message());
                        return;

                    case RIGHT_CLICK:

                        if (easel.getFrame().getItem().getType() == Material.AIR) {
                            ItemStack item = player.getItemInHand();

                            if (item.getType() == Material.PAPER) {

                                ItemMeta meta = player.getItemInHand().getItemMeta();

                                if (meta.hasDisplayName()) {

                                    MapView mapView = null;

                                    if (meta.getDisplayName().equals(Recipe.carbonPaperTitle)) {

                                        if (meta.hasLore()) {
                                            String title = meta.getLore().get(0).substring(2);
                                            MapArt art = MapArt.getArtwork(plugin, title);

                                            if (art != null) {
                                                mapView = MapArt.cloneArtwork(plugin,
                                                        player.getWorld(), art.getMapID());
                                            }
                                        }

                                        if (mapView != null) {
                                            easel.mountCanvas(mapView);

                                            if (easel.getItem() != null) {
                                                ItemStack removed = item.clone();
                                                removed.setAmount(1);
                                                player.getInventory().removeItem(removed);
                                            }
                                            return;
                                        }
                                        player.sendMessage(ArtMap.Lang.NEED_TO_COPY.message());
                                        return;

                                    } else if (meta.getDisplayName().equals(Recipe.canvasTitle)) {
                                        mapView = MapArt.generateMapID(plugin, player.getWorld());
                                        plugin.getNmsInterface().setWorldMap(mapView, MapArt.blankMap);
                                        easel.mountCanvas(mapView);

                                        if (easel.getItem() != null) {
                                            ItemStack removed = item.clone();
                                            removed.setAmount(1);
                                            player.getInventory().removeItem(removed);
                                        }
                                        return;
                                    }

                                } else {
                                    player.sendMessage(ArtMap.Lang.NOT_A_CANVAS.message());
                                }

                            } else {
                                player.sendMessage(ArtMap.Lang.NEED_CANVAS.message());
                            }

                        } else {
                            easel.rideEasel(player);
                            return;
                        }
                        break;

                    case SHIFT_RIGHT_CLICK:

                        MapArt art = null;

                        if (easel.hasItem()) {

                            ItemStack itemStack = easel.getItem();

                            if (itemStack.hasItemMeta()) {
                                ItemMeta meta = itemStack.getItemMeta();

                                if (meta.getLore().get(0).equals(MapArt.artworkTag)) {

                                    art = MapArt.getArtwork(plugin, meta.getDisplayName());
                                }
                            }

                            if (art != null) {

                                if (!art.getPlayer().getUniqueId().equals(player.getUniqueId())
                                        && !player.hasPermission("artmap.admin")) {

                                    player.sendMessage(String.format(ArtMap.Lang.NOT_YOUR_EASEL.message(),
                                            art.getPlayer().getName()));
                                    return;
                                }

                            } else {
                                final short id = itemStack.getDurability();

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

            } else {
                player.sendRawMessage(ArtMap.Lang.ELSE_USING.message());
            }

        } else {
            player.sendMessage(ArtMap.Lang.NO_PERM.message());
        }
    }
}

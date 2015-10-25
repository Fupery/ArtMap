package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import static me.Fupery.ArtMap.Utils.Formatting.*;

public class EaselInteractListener implements Listener {

    ArtMap plugin;

    public EaselInteractListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEaselInteract(EaselEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("artmap.artist")) {

            Easel easel = event.getEasel();

            if (!easel.isPainting()) {

                switch (event.getClick()) {

                    case LEFT_CLICK:

                        player.sendMessage(playerMessage(easelHelp));
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
                                                mapView = MapArt.cloneArtwork(plugin, player.getWorld(), art.getMapID());
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
                                        player.sendMessage(playerError(needToCopy));
                                        return;

                                    } else if (meta.getDisplayName().equals(Recipe.canvasTitle)) {
                                        mapView = Bukkit.createMap(player.getWorld());
                                        plugin.getNmsInterface().setWorldMap(mapView, plugin.getBlankMap());
                                        easel.mountCanvas(mapView);

                                        if (easel.getItem() != null) {
                                            ItemStack removed = item.clone();
                                            removed.setAmount(1);
                                            player.getInventory().removeItem(removed);
                                        }
                                        return;
                                    }

                                } else {
                                    player.sendMessage(playerError(notACanvas));
                                }

                            } else {
                                player.sendMessage(playerError(needCanvas));
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

                                    player.sendMessage(String.format(notYourEasel, art.getPlayer().getName()));
                                }
                            }
                        }
                        easel.breakEasel();
                        break;
                }

            } else {
                player.sendRawMessage(playerError(elseUsing));
            }

        } else {
            player.sendMessage(playerError(noperm));
        }
    }
}

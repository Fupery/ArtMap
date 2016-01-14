package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import java.util.concurrent.ConcurrentHashMap;

public class EaselInteractListener implements Listener {

    public static final ConcurrentHashMap<Location, Easel> easels = new ConcurrentHashMap<>();
    private final ArtMap plugin;

    public EaselInteractListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEaselInteract(EaselEvent event) {

        Player player = event.getPlayer();
        final Easel easel = event.getEasel();

        if (!player.hasPermission("artmap.artist")) {
            player.sendRawMessage(ArtMap.Lang.NO_PERM.message());
            return;
        }

        if (easel.isPainting()) {
            player.sendMessage(ArtMap.Lang.ELSE_USING.message());
            return;
        }

        switch (event.getClick()) {

            case LEFT_CLICK:

                player.sendMessage(ArtMap.Lang.EASEL_HELP.message());
                return;

            case RIGHT_CLICK:

                //If the easel has a canvas, player rides the easel
                if (easel.getItem().getType() == Material.MAP) {
                    easel.rideEasel(player, plugin);
                    return;

                } else if (easel.getItem().getType() != Material.AIR) {
                    easel.removeItem();
                    return;
                }

                ItemStack itemInHand = player.getItemInHand();

                //Player must use a canvas on the easel
                if (itemInHand.getType() != Material.PAPER) {
                    player.sendMessage(ArtMap.Lang.NEED_CANVAS.message());
                    return;
                }

                ItemMeta itemInHandMeta = itemInHand.getItemMeta();

                //Check if canvas has valid metadata
                if (!itemInHandMeta.hasLore()) {
                    player.sendMessage(ArtMap.Lang.NOT_A_CANVAS.message());
                    return;
                }

                MapView mapView;
                ArtMaterial material = ArtMaterial.getCraftItemType(itemInHand);

                //Check if carbon paper links to a valid artwork
                if (material == ArtMaterial.CARBON_PAPER_FILLED) {
                    mapView = getMapView(itemInHandMeta.getLore().get(0), player.getWorld());

                    if (mapView != null) {
                        mountMap(easel, mapView, player);

                    } else {
                        player.sendMessage(ArtMap.Lang.NEED_TO_COPY.message());
                    }

                    //Mount the canvas
                } else if (material == ArtMaterial.CARBON_PAPER) {
                    player.sendMessage(ArtMap.Lang.NEED_TO_COPY.message());
                    return;

                } else if (material == ArtMaterial.CANVAS) {
                    mapView = ArtMap.getArtDatabase().generateMapID(player.getWorld());
                    ArtMap.nmsInterface.setWorldMap(mapView, MapArt.blankMap);
                    mountMap(easel, mapView, player);
                }
                break;

            case SHIFT_RIGHT_CLICK:

                if (easel.hasItem()) {
                    final short id = easel.getItem().getDurability();

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            ArtMap.getArtDatabase().recycleID(id);
                        }
                    });
                    easel.removeItem();
                }
                easel.breakEasel(plugin);
        }
    }

    private MapView getMapView(String id, World world) {
        int a = id.indexOf("[") + 1, b = id.lastIndexOf("]");

        if (a < 0 || b < 0) {
            return null;
        }
        String title = id.substring(a, b);
        MapArt art = ArtMap.getArtDatabase().getArtwork(title);

        if (art != null) {
            return MapArt.cloneArtwork(world, art.getMapID());
        }
        return null;
    }

    private void mountMap(Easel easel, MapView mapView, Player player) {
        easel.mountCanvas(mapView);

        if (easel.getItem() != null) {
            ItemStack removed = player.getItemInHand().clone();
            removed.setAmount(1);
            player.getInventory().removeItem(removed);
        }
    }
}

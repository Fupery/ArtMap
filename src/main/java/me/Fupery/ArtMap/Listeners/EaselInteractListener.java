package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.IO.ArtDatabase;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Preview;
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

                MapView mapView;
                ArtMaterial material = ArtMaterial.getCraftItemType(player.getItemInHand());

                if (material == ArtMaterial.CANVAS) {
                    mapView = ArtMap.getArtDatabase().generateMapID(player.getWorld());
                    ArtMap.nmsInterface.setWorldMap(mapView, MapArt.blankMap);
                    mountMap(easel, mapView, player);
                    return;

                } else if (material == ArtMaterial.MAP_ART) {
                    MapArt art = ArtMap.getArtDatabase().getArtwork(player.getItemInHand().getDurability());

                    if (art != null) {

                        if (!player.getUniqueId().equals(art.getPlayer().getUniqueId())) {
                            player.sendMessage(ArtMap.Lang.NO_CRAFT_PERM.message());
                            return;
                        }

                        if (ArtMap.previewing.containsKey(player)) {
                            ArtMap.previewing.get(player).stopPreviewing();
                            return;
                        }
                        mapView = MapArt.cloneArtwork(player.getWorld(), art.getMapID());
                        mountMap(easel, mapView, player);
                        return;
                    }
                }
                player.sendMessage(ArtMap.Lang.NEED_CANVAS.message());
                return;

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

    private void mountMap(Easel easel, MapView mapView, Player player) {
        easel.mountCanvas(mapView);

        if (easel.getItem() != null) {
            ItemStack removed = player.getItemInHand().clone();
            removed.setAmount(1);
            player.getInventory().removeItem(removed);
        }
    }
}

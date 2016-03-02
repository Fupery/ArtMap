package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.GenericMapRenderer;
import me.Fupery.ArtMap.Utils.Lang;
<<<<<<< HEAD
import org.bukkit.Bukkit;
=======
import me.Fupery.ArtMap.Utils.Reflection;
>>>>>>> master
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.concurrent.ConcurrentHashMap;

public class EaselInteractListener implements Listener {

    public static final ConcurrentHashMap<Location, Easel> easels = new ConcurrentHashMap<>();

    @EventHandler
    public void onEaselInteract(EaselEvent event) {

        Player player = event.getPlayer();
        final Easel easel = event.getEasel();
        MapView mapView;

        if (!player.hasPermission("artmap.artist")) {
            player.sendRawMessage(Lang.NO_PERM.message());
            return;
        }

        if (easel.isPainting()) {
            player.sendMessage(Lang.ELSE_USING.message());
            return;
        }

        switch (event.getClick()) {

            case LEFT_CLICK:

                player.sendMessage(Lang.EASEL_HELP.message());
                return;

            case RIGHT_CLICK:

                //If the easel has a canvas, player rides the easel
                if (easel.getItem().getType() == Material.MAP) {
                    easel.rideEasel(player);
                    return;

                } else if (easel.getItem().getType() != Material.AIR) {
                    easel.removeItem();
                    return;
                }

                ArtMaterial material = ArtMaterial.getCraftItemType(player.getItemInHand());

                if (material == ArtMaterial.CANVAS) {
                    mapView = ArtMap.getArtDatabase().generateMapID(player.getWorld());
                    Reflection.setWorldMap(mapView, MapArt.blankMap);
                    mountMap(easel, mapView, player);
                    return;

                } else if (material == ArtMaterial.MAP_ART) {
                    MapArt art = ArtMap.getArtDatabase().getArtwork(player.getItemInHand().getDurability());

                    if (art != null) {

                        if (!player.getUniqueId().equals(art.getPlayer().getUniqueId())) {
                            player.sendMessage(Lang.NO_CRAFT_PERM.message());
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
                player.sendMessage(Lang.NEED_CANVAS.message());
                return;

            case SHIFT_RIGHT_CLICK:

                if (easel.hasItem()) {
                    final short id = easel.getItem().getDurability();
                    ArtMap.nmsInterface.setWorldMap(Bukkit.getMap(id), MapArt.blankMap);

                    mapView = Bukkit.getMap(id);
                    for (MapRenderer renderer : mapView.getRenderers()) {
                        mapView.removeRenderer(renderer);
                    }

                    mapView.addRenderer(new GenericMapRenderer(MapArt.blankMap));
                    ArtMap.runTaskAsync(new Runnable() {
                        @Override
                        public void run() {
                            ArtMap.getArtDatabase().recycleID(id);
                        }
                    });
                    easel.removeItem();
                }
                easel.breakEasel();
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

package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.GenericMapRenderer;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
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

    public static final ConcurrentHashMap<Location, Easel> easels = new ConcurrentHashMap<>();// FIXME: 25/07/2016 why static?

    @EventHandler
    public void onEaselInteract(EaselEvent event) {

        Player player = event.getPlayer();
        final Easel easel = event.getEasel();
        final MapView mapView;

        if (!player.hasPermission("artmap.artist")) {
            ArtMap.getLang().sendMsg("NO_PERM", player);
            return;
        }

        if (easel.isPainting()) {
            ArtMap.getLang().ACTION_BAR_MESSAGES.EASEL_USED.send(player);
            player.spigot().playEffect(easel.getLocation().clone().add(0.5, 0.5, 0.5),
                    Effect.CRIT, 8, 10, 0.3f, 0.4f, 0.3f, 0.02f, 5, 2); // fixme: 23/07/2016 fix location
            return;
        }

        switch (event.getClick()) {

            case LEFT_CLICK:

                ArtMap.getLang().ACTION_BAR_MESSAGES.EASEL_PUNCH.send(player);
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
                    Reflection.setWorldMap(mapView, MapArt.BLANK_MAP);
                    mountMap(easel, mapView, player);
                    return;

                } else if (material == ArtMaterial.MAP_ART) {
                    MapArt art = ArtMap.getArtDatabase().getArtwork(player.getItemInHand().getDurability());

                    if (art != null) {

                        if (!player.getUniqueId().equals(art.getPlayer().getUniqueId())) {
                            ArtMap.getLang().sendMsg("NO_CRAFTING_PERM", player);
                            return;
                        }

                        if (ArtMap.getPreviewing().containsKey(player)) {
                            ArtMap.getPreviewing().get(player).stopPreviewing();
                            return;
                        }
                        mapView = MapArt.cloneArtwork(player.getWorld(), art.getMapID());
                        mountMap(easel, mapView, player);
                        return;
                    }
                }
                ArtMap.getLang().ACTION_BAR_MESSAGES.EASEL_NO_CANVAS.send(player);
                player.spigot().playEffect(easel.getLocation().clone().add(0.5, 0.5, 0.5),
                        Effect.CRIT, 8, 10, 0.3f, 0.4f, 0.3f, 0.02f, 5, 2);// fixme: 23/07/2016 fix location
                return;

            case SHIFT_RIGHT_CLICK:

                if (easel.hasItem()) {
                    final short id = easel.getItem().getDurability();
                    mapView = Bukkit.getMap(id);

                    for (MapRenderer renderer : mapView.getRenderers()) {
                        mapView.removeRenderer(renderer);
                    }

                    mapView.addRenderer(new GenericMapRenderer(MapArt.BLANK_MAP));
                    ArtMap.getTaskManager().ASYNC.run(() -> {
                        Reflection.setWorldMap(mapView, MapArt.BLANK_MAP);
                        ArtMap.getArtDatabase().recycleID(id);
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

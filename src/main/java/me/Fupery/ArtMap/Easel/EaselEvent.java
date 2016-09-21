package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.MapManager;
import me.Fupery.ArtMap.Painting.GenericMapRenderer;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Reflection;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.concurrent.ConcurrentHashMap;

public final class EaselEvent {
    public static final ConcurrentHashMap<Location, Easel> easels = new ConcurrentHashMap<>();// FIXME: 25/07/2016 why static?
    private final Easel easel;
    private final ClickType click;
    private final Player player;

    public EaselEvent(Easel easel, ClickType click, Player player) {
        this.easel = easel;
        this.click = click;
        this.player = player;
    }

    public void callEvent() {
        final MapView mapView;
        if (!player.hasPermission("artmap.artist")) {
            Lang.NO_PERM.send(player);
            return;
        }
        if (easel.isPainting()) {
            Lang.ActionBar.ELSE_USING.send(player);
            SoundCompat.ENTITY_ARMORSTAND_BREAK.play(player);
            easel.playEffect(Effect.CRIT);
            return;
        }
        switch (click) {
            case LEFT_CLICK:
                Lang.ActionBar.EASEL_HELP.send(player);
                return;
            case RIGHT_CLICK:
                //If the easel has a canvas, player rides the easel
                if (easel.getItem().getType() == Material.MAP) {
                    easel.rideEasel(player);
                    return;
                    //remove items that were added while instance is unloaded etc.
                } else if (easel.getItem().getType() != Material.AIR) {
                    easel.removeItem();
                    return;
                }
                ArtMaterial material = ArtMaterial.getCraftItemType(player.getItemInHand());

                if (material == ArtMaterial.CANVAS) {
                    mapView = ArtMap.getMapManager().generateMapID(player.getWorld());
                    Reflection.setWorldMap(mapView, MapManager.BLANK_MAP);
                    easel.mountCanvas(mapView);
                    consumeCurrentItem(player);
                    return;

                } else if (material == ArtMaterial.MAP_ART) {
                    ArtMap.getTaskManager().ASYNC.run(() -> editArtwork(player.getItemInHand()));
                    return;
                }
                Lang.ActionBar.NEED_CANVAS.send(player);
                SoundCompat.ENTITY_ARMORSTAND_BREAK.play(player);
                easel.playEffect(Effect.CRIT);
                return;

            case SHIFT_RIGHT_CLICK:
                if (easel.hasItem()) {
                    if (ArtMaterial.MAP_ART.isValidMaterial(easel.getItem())) {

                    }
                    final short id = easel.getItem().getDurability();
                    mapView = Bukkit.getMap(id);
                    for (MapRenderer renderer : mapView.getRenderers()) {
                        mapView.removeRenderer(renderer);
                    }
                    mapView.addRenderer(new GenericMapRenderer(MapManager.BLANK_MAP));
                    ArtMap.getTaskManager().ASYNC.run(() -> {
                        Reflection.setWorldMap(mapView, MapManager.BLANK_MAP);
                        ArtMap.getMapManager().recycleID(id);
                    });
                    easel.removeItem();
                }
                easel.breakEasel();
                easel.playEffect(Effect.CLOUD);
        }
    }

    private void editArtwork(ItemStack playerMainHandItem) {
        MapArt art = ArtMap.getArtDatabase().getArtwork(playerMainHandItem.getDurability());

        if (art != null) {
            if (!player.getUniqueId().equals(art.getArtistPlayer().getUniqueId())) {
                Lang.ActionBar.NO_EDIT_PERM.send(player);
                easel.playEffect(Effect.CRIT);
                SoundCompat.ENTITY_ARMORSTAND_BREAK.play(player);
                return;
            }
            if (ArtMap.getPreviewing().containsKey(player)) {
                ArtMap.getPreviewing().get(player).stopPreviewing();
                return;
            }
            MapView mapView = MapManager.cloneArtwork(player.getWorld(), art.getMapId());
            easel.editArtwork(mapView, art.getTitle());
            consumeCurrentItem(player);
        } else {
            Lang.NEED_CANVAS.send(player);
            SoundCompat.ENTITY_ARMORSTAND_BREAK.play(player);
            easel.playEffect(Effect.CRIT);
        }
    }

    private void consumeCurrentItem(Player player) {
        ItemStack removed = player.getItemInHand().clone();
        removed.setAmount(1);
        player.getInventory().removeItem(removed);
    }

    public enum ClickType {
        LEFT_CLICK, RIGHT_CLICK, SHIFT_RIGHT_CLICK
    }
}

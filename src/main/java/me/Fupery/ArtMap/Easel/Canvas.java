package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Canvas {

    private final short mapId;

    public Canvas(Map map) {
        this.mapId = map.getMapId();
    }

    private Canvas(short mapId) {
        this.mapId = mapId;
    }

    static Canvas getCanvas(ItemStack item) {
        if (item == null || item.getType() != Material.MAP) return null;
        short mapId = item.getDurability();
        if (item.hasItemMeta() && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().get(0).equals(ArtItem.COPY_KEY)) {
            final String originalName = item.getItemMeta().getLore().get(1);
            return new CanvasCopy(mapId, originalName);
        } else {
            return new Canvas(mapId);
        }
    }

    ItemStack getEaselItem() {
        return new ItemStack(Material.MAP, 1, mapId);
    }

    private ItemStack getDropItem() {
        return ArtMaterial.CANVAS.getItem();
    }

    void dropItem(Location location) {
        location.getWorld().dropItemNaturally(location, getDropItem());
    }

    short getMapId() {
        return mapId;
    }

    static class CanvasCopy extends Canvas {

        private final String originalName;

        public CanvasCopy(Map map, String originalName) {
            super(map);
            this.originalName = originalName;
        }

        private CanvasCopy(short mapId, String originalName) {
            super(mapId);
            this.originalName = originalName;
        }

        @Override
        ItemStack getEaselItem() {
            ItemStack item = super.getEaselItem();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList(ArtItem.COPY_KEY, originalName));
            item.setItemMeta(meta);
            return item;
        }

        @Override
        void dropItem(Location location) {
            ArtMap.getScheduler().ASYNC.run(() -> {
                MapArt original = ArtMap.getArtDatabase().getArtwork(originalName);
                ArtMap.getScheduler().SYNC.run(() -> {
                    if (original != null) {
                        location.getWorld().dropItemNaturally(location, original.getMapItem());
                    } else {
                        location.getWorld().dropItemNaturally(location, super.getDropItem());
                    }
                });
            });
        }
    }
}

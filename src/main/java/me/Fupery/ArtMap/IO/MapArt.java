package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static me.Fupery.ArtMap.Utils.Formatting.listLine;

public class MapArt {

    public static final String artworkTag = "§aPlayer Artwork";
    public static final String artworks = "artworks";
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static final String artistID = "artist";
    private static final String mapID = "mapID";
    private static final String dateID = "date";


    private short mapIDValue;
    private String title;
    private OfflinePlayer player;
    private String date;

    public MapArt(short mapIDValue, String title, OfflinePlayer player) {
        this.mapIDValue = mapIDValue;
        this.title = title;
        this.player = player;
        this.date = dateFormat.format(new Date());
    }

    private MapArt(short mapIDValue, String title, OfflinePlayer player, String date) {
        this.mapIDValue = mapIDValue;
        this.title = title;
        this.player = player;
        this.date = date;
    }

    public static MapArt getArtwork(ArtMap plugin, String title) {

        if (plugin.getMaps() != null) {
            ConfigurationSection mapList = plugin.getMaps().getConfigurationSection(artworks);
            ConfigurationSection map = mapList.getConfigurationSection(title);

            if (map != null) {
                int mapIDValue = map.getInt(mapID);
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(map.getString(artistID)));
                String date = map.getString(dateID);
                return new MapArt(((short) mapIDValue), title, player, date);
            }
        }
        return null;
    }

    public static MapArt getArtwork(ArtMap plugin, ItemStack itemStack) {

        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();

            if (meta.getLore().get(0).equals(artworkTag)) {

                return getArtwork(plugin, meta.getDisplayName());
            }
        }
        return null;
    }

    public static boolean deleteArtwork(ArtMap plugin, String title) {

        if (plugin.getMaps() != null) {
            ConfigurationSection mapList = plugin.getMaps().getConfigurationSection(artworks);
            ConfigurationSection map = mapList.getConfigurationSection(title);

            if (map != null) {
                int mapIDValue = map.getInt(mapID);
                //clear map data
                MapView mapView = Bukkit.getMap((short) mapIDValue);
                plugin.getNmsInterface().setWorldMap(mapView, new byte[128 * 128]);

                for (MapRenderer renderer : mapView.getRenderers()) {
                    mapView.removeRenderer(renderer);
                }
                //remove map from list
                mapList.set(title, null);
                plugin.updateMaps();
                return true;
            }
        }
        return false;
    }

    public static String[] listArtworks(ArtMap plugin, String artist) {
        ArrayList<String> returnList;

        if (plugin.getMaps() != null) {
            ConfigurationSection mapList = plugin.getMaps().getConfigurationSection("artworks");

            Set<String> list = mapList.getKeys(false);
            returnList = new ArrayList<>();

            int i = 0;
            for (String title : list) {
                MapArt art = getArtwork(plugin, title);

                if (art != null) {

                    if (!artist.equals("all")) {

                        if (!art.getPlayer().getName().equalsIgnoreCase(artist)) {
                            continue;
                        }
                    }
                    returnList.add(listLine(title, art.player.getName(), art.date, art.mapIDValue));
                    i++;
                }
            }
            return returnList.toArray(new String[returnList.size()]);
        }
        return null;
    }

    public static MapView cloneArtwork(ArtMap plugin, World world, short mapID) {
        MapView oldMapView = Bukkit.getServer().getMap(mapID);
        MapView newMapView = Bukkit.getServer().createMap(world);
        byte[] oldMap = plugin.getNmsInterface().getMap(oldMapView);
        plugin.getNmsInterface().setWorldMap(newMapView, oldMap);
        return newMapView;
    }

    public ItemStack getMapItem() {

        ItemStack map = new ItemStack(Material.MAP, 1, mapIDValue);

        Date d = new Date();

        ItemMeta meta = map.getItemMeta();

        meta.setDisplayName(title);

        meta.setLore(Arrays.asList(
                artworkTag,
                ChatColor.GOLD + "by " + ChatColor.YELLOW + player.getName(),
                dateFormat.format(d)));
        map.setItemMeta(meta);

        return map;
    }

    public MapArt saveArtwork(ArtMap plugin) {

        if (plugin.getMaps() != null) {
            ConfigurationSection mapList = plugin.getMaps().getConfigurationSection(artworks);
            ConfigurationSection map = mapList.createSection(title);
            map.set(mapID, mapIDValue);
            map.set(artistID, player.getUniqueId().toString());
            map.set(dateID, date);
            plugin.updateMaps();
            return new MapArt(mapIDValue, title, player, dateID);
        }
        return null;
    }

    public short getMapID() {
        return mapIDValue;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }
}

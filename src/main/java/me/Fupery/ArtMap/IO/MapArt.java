package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static me.Fupery.ArtMap.Utils.Formatting.listLine;

public class MapArt {

    public static final String artworks = "artworks";
    public static final byte[] blankMap = getBlankMap();
    public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static final String recycled_keys = "recycled_keys";
    private static final String artistID = "artist";
    private static final String mapID = "mapID";
    private static final String dateID = "date";
    private final short mapIDValue;
    private final String title;
    private final OfflinePlayer player;
    private final String date;

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

    public static MapArt getArtwork(ArtMap plugin, short mapData) {

        if (plugin.getMaps() != null) {
            ConfigurationSection mapList = plugin.getMaps().getConfigurationSection(artworks);
            Set<String> keys = mapList.getKeys(false);

            for (String title : keys) {

                ConfigurationSection map = mapList.getConfigurationSection(title);
                short data = (short) map.getInt(mapID);

                if (mapData == data) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(map.getString(artistID)));
                    String date = map.getString(dateID);
                    return new MapArt(mapData, title, player, date);
                }
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

    public static void recycleID(ArtMap plugin, short id) {
        FileConfiguration maps = plugin.getMaps();

        if (maps != null) {
            ConfigurationSection keys = maps.getConfigurationSection(recycled_keys);

            List<Short> shortList;

            if (keys == null) {
                keys = maps.createSection(recycled_keys);
            }
            shortList = keys.getShortList("keys");

            if (shortList == null) {
                shortList = new ArrayList<>();
            }
            shortList.add(id);
            keys.set("keys", shortList);
            plugin.updateMaps();
        }
    }

    public static MapView generateMapID(ArtMap plugin, World world) {
        FileConfiguration maps = plugin.getMaps();
        MapView mapView = null;

        if (maps != null) {
            ConfigurationSection keys = maps.getConfigurationSection(recycled_keys);

            if (keys != null) {

                List<Short> shortList = keys.getShortList("keys");

                if (shortList != null && shortList.size() > 0) {
                    short id = shortList.get(0);

                    if (getArtwork(plugin, id) == null) {
                        mapView = Bukkit.getMap(id);
                        shortList.remove(0);
                        keys.set("keys", shortList);
                        plugin.updateMaps();
                    }
                }
            }
        }

        if (mapView == null) {
            mapView = Bukkit.createMap(world);
        }
        return mapView;
    }

    private static byte[] getBlankMap() {
        byte[] mapOutput = new byte[128 * 128];

        for (int i = 0; i < mapOutput.length; i++) {
            mapOutput[i] = ArtDye.WHITE.getData();
        }
        return mapOutput;
    }

    public ItemStack getMapItem() {
        return ArtMaterial.getMapArt(mapIDValue, title, player);
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

    public String getTitle() {
        return title;
    }
}

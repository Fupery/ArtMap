package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ArtDatabase {

    public static final String ARTWORKS_TAG = "artworks";
    private static final String RECYCLED_KEYS_TAG = "recycled_keys";
    private static final String ARTIST_TAG = "artist";
    private static final String MAP_TAG = "mapID";
    private static final String DATE_TAG = "date";
    private static UUID[] artistList = null; // FIXME: 25/07/2016 cache artists better
    private static boolean artistsUpToDate = false;
    private static File mapData;
    private ConfigurationSection artworks;
    private ConfigurationSection recycled_keys;

    private ArtDatabase() {
        loadConfiguration();
    }

    public static ArtDatabase buildDatabase() {
        mapData = new File(ArtMap.instance().getDataFolder(), "mapList.yml");

        if (!mapData.exists()) {

            try {

                if (!mapData.createNewFile()) {
                    return null;
                }

            } catch (IOException e) {
                return null;
            }
        }
        return new ArtDatabase();
    }

    public MapArt getArtwork(String title) {
        ConfigurationSection map = artworks.getConfigurationSection(title);

        if (map != null) {
            int mapIDValue = map.getInt(MAP_TAG);
            OfflinePlayer player = (map.contains(ARTIST_TAG)) ?
                    Bukkit.getOfflinePlayer(UUID.fromString(map.getString(ARTIST_TAG))) :
                    null;
            String date = map.getString(DATE_TAG);
            return new MapArt(((short) mapIDValue), title, player, date);
        }
        return null;
    }

    public MapArt getArtwork(short mapData) {

        Set<String> keys = artworks.getKeys(false);

        for (String title : keys) {

            ConfigurationSection map = artworks.getConfigurationSection(title);
            short data = (short) map.getInt(MAP_TAG);

            if (mapData == data) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(map.getString(ARTIST_TAG)));
                String date = map.getString(DATE_TAG);
                return new MapArt(mapData, title, player, date);
            }
        }
        return null;
    }

    public boolean containsArtwork(MapArt art, boolean ignoreMapID) {
        MapArt art2 = getArtwork(art.getTitle());
        return art2 != null && art2.equals(art, ignoreMapID);
    }

    public boolean containsMapID(short mapID) {
        return getArtwork(mapID) != null;
    }

    public synchronized boolean deleteArtwork(String title) {
        ConfigurationSection map = artworks.getConfigurationSection(title);

        if (map != null) {
            int mapIDValue = map.getInt(MAP_TAG);
            //clear map data
            MapView mapView = Bukkit.getMap((short) mapIDValue);
            Reflection.setWorldMap(mapView, new byte[128 * 128]);

            for (MapRenderer renderer : mapView.getRenderers()) {
                mapView.removeRenderer(renderer);
            }
            //remove map from list
            artworks.set(title, null);
            updateMaps();
            artistsUpToDate = false;
            return true;
        }
        return false;
    }

    public MapArt[] listMapArt(String artist) {
        ArrayList<MapArt> returnList;

        Set<String> list = artworks.getKeys(false);
        returnList = new ArrayList<>();

        int i = 0;
        for (String title : list) {
            MapArt art = getArtwork(title);

            if (art != null && art.isValid()) {
                if (!artist.equals("all")
                        && !art.getPlayer().getName().equalsIgnoreCase(artist)) {
                    continue;
                }
                returnList.add(art);
                i++;
            }
        }
        return returnList.toArray(new MapArt[returnList.size()]);
    }

    public UUID[] listArtists(UUID player) {
        if (!artistsUpToDate) {
            ArrayList<UUID> returnList;

            Set<String> list = artworks.getKeys(true);
            returnList = new ArrayList<>();

            if (player != null) {
                returnList.add(0, player);
            }
            for (String title : list) {
                MapArt art = getArtwork(title);

                if (art != null && art.isValid() && !returnList.contains(art.getPlayer().getUniqueId())) {
                    returnList.add(art.getPlayer().getUniqueId());
                }
            }
            artistList = returnList.toArray(new UUID[returnList.size()]);
            artistsUpToDate = true;
        }
        return artistList;
    }

    public synchronized void addArtwork(MapArt art) {
        ConfigurationSection map = artworks.createSection(art.getTitle());
        map.set(MAP_TAG, art.getMapID());
        map.set(ARTIST_TAG, art.getPlayer().getUniqueId().toString());
        map.set(DATE_TAG, art.getDate());
        updateMaps();
        artistsUpToDate = false;
    }

    public synchronized void addArtworks(MapArt... artworks) {
        for (MapArt art : artworks) {
            ConfigurationSection map = this.artworks.createSection(art.getTitle());
            map.set(MAP_TAG, art.getMapID());
            map.set(ARTIST_TAG, art.getPlayer().getUniqueId().toString());
            map.set(DATE_TAG, art.getDate());
        }
        updateMaps();
        artistsUpToDate = false;
    }

    public void recycleID(short id) {
        List<Short> shortList;

        shortList = recycled_keys.getShortList("keys");

        if (shortList == null) {
            shortList = new ArrayList<>();
        }
        shortList.add(id);
        recycled_keys.set("keys", shortList);
        updateMaps();
    }

    public MapView generateMapID(World world) {
        MapView mapView = null;
        List<Short> shortList = recycled_keys.getShortList("keys");

        if (shortList != null && shortList.size() > 0) {
            short id = shortList.get(0);

            if (getArtwork(id) == null) {
                mapView = Bukkit.getMap(id);
                shortList.remove(0);
                recycled_keys.set("keys", shortList);
                updateMaps();
            }
        }

        if (mapView == null) {
            mapView = Bukkit.createMap(world);
        }

        return mapView;
    }

    private synchronized void loadConfiguration() {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(mapData);
        artworks = configuration.getConfigurationSection(ARTWORKS_TAG);
        recycled_keys = configuration.getConfigurationSection(RECYCLED_KEYS_TAG);

        if (artworks == null) {
            artworks = configuration.createSection(ARTWORKS_TAG);
        }
        if (recycled_keys == null) {
            recycled_keys = configuration.createSection(RECYCLED_KEYS_TAG);
        }
    }

    private synchronized void updateMaps() {
        ArtMap.getTaskManager().ASYNC.run(() -> {
            try {
                FileConfiguration configuration = new YamlConfiguration();
                configuration.set(ARTWORKS_TAG, artworks);
                configuration.set(RECYCLED_KEYS_TAG, recycled_keys);
                configuration.save(mapData);
                loadConfiguration();

            } catch (IOException e) {
                ArtMap.instance().getLogger().info(String.format(ArtMap.getLang().getMsg("MAPDATA_ERROR"),
                        mapData.getAbsolutePath(), e));
            }
        });
    }
}

package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ColourMap.f32x32;
import me.Fupery.ArtMap.IO.Database.MapTable;
import me.Fupery.ArtMap.Painting.GenericMapRenderer;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MapManager {

    public static final byte[] BLANK_MAP = getBlankMap();
    private static final String RECYCLED_KEYS_TAG = "recycled_keys";
    private final File file;
    private final BukkitRunnable AUTOSAVE = new BukkitRunnable() {
        @Override
        public void run() {
            for (UUID uuid : ArtMap.getArtistHandler().getArtists()) {
                ArtMap.getArtistHandler().getCurrentSession(uuid).persistMap();
            }
        }
    };
    private PriorityQueue<Short> queue = new PriorityQueue<>();

    public MapManager(ArtMap plugin) {
        file = new File(plugin.getDataFolder(), "keys.yml");
        loadKeys();
        int delay = ArtMap.getConfiguration().ARTWORK_AUTO_SAVE;
        AUTOSAVE.runTaskTimerAsynchronously(plugin, delay, delay);
    }

    public void stop() {
        saveKeys();
        AUTOSAVE.cancel();
    }

    public static byte[] decompressMap(byte[] mapData) {
        return mapData == null ? new byte[MapSize.MAX.size] : new f32x32().readBLOB(mapData);
    }

    public static byte[] compressMap(MapView mapView) {
        byte[] map = Reflection.getMap(mapView);
        byte[] compressed;
        try {
            compressed = new f32x32().generateBLOB(map);
        } catch (IOException e) {
            ErrorLogger.log(e, "Compression error!");
            return new byte[0];
        }
        return compressed;
    }

    public static CompressedMap compressMap(short mapId, byte[] map) {
        byte[] compressed;
        try {
            compressed = new f32x32().generateBLOB(map);
        } catch (IOException e) {
            ErrorLogger.log(e, "Compression error!");
            return null;
        }
        return new CompressedMap(mapId, Arrays.hashCode(map), compressed);
    }

    private static byte[] getBlankMap() {
        byte[] mapOutput = new byte[MapSize.MAX.size];
        Arrays.fill(mapOutput, ArtMap.getColourPalette().getDefaultColour().getColour());
        return mapOutput;
    }

    public byte[] readMap(short mapId) {
        return Reflection.getMap(getMap(mapId));
    }

    public Map cloneArtwork(World world, short mapID) {
        MapView oldMapView = getMap(mapID);
        MapView newMapView = Bukkit.getServer().createMap(world);
        byte[] oldMap = Reflection.getMap(oldMapView);
        Reflection.setWorldMap(newMapView, oldMap);
        return new Map(mapID, newMapView);
    }

    public void setMap(MapView mapView, byte[] map) {
        Reflection.setWorldMap(mapView, map);
        setRenderer(mapView, new GenericMapRenderer(map));//todo sync?
    }

    private void setRenderer(MapView mapView, MapRenderer renderer) {
        mapView.getRenderers().forEach(mapView::removeRenderer);
        if (renderer != null) mapView.addRenderer(renderer);
    }

    public MapView createMap() {
        Short id = queue.poll();
        MapView mapView;
        if (id != null && ArtMap.getArtDatabase().getArtwork(id) == null) {
            mapView = getMap(id);
        } else {
            mapView = Bukkit.createMap(Bukkit.getWorld(ArtMap.getConfiguration().WORLD));
        }
        Reflection.setWorldMap(mapView, MapManager.BLANK_MAP);
        return mapView;
    }

    public void cacheMap(short mapId, byte[] map) {
        ArtMap.getTaskManager().ASYNC.run(() -> {
            MapTable table = ArtMap.getArtDatabase().getMapTable();
            CompressedMap compressedMap = compressMap(mapId, map);
            if (table.containsMap(mapId)) table.updateMap(compressedMap);
            else table.addMap(compressedMap);
        });
    }

    public void restoreMap(short mapId) {
        MapView mapView = getMap(mapId);
        byte[] oldMap = readMap(mapId);
        int oldMapHash = Arrays.hashCode(oldMap);
        MapTable mapTable = ArtMap.getArtDatabase().getMapTable();
        ArtMap.getTaskManager().ASYNC.run(() -> {
            if (mapTable.containsMap(mapId) && mapTable.getHash(mapId) != oldMapHash) {
                CompressedMap map = mapTable.getMap(mapId);
                setMap(mapView, map.decompressMap());
            }
        });
    }

    public void deleteMap(short mapId) {
        MapView mapView = getMap(mapId);
        Reflection.setWorldMap(mapView, new byte[MapSize.MAX.size]);
        setRenderer(mapView, null);
    }

    public void recycleMap(short mapId) {
        MapView mapView = getMap(mapId);
        setRenderer(mapView, new GenericMapRenderer(MapManager.BLANK_MAP));
        ArtMap.getTaskManager().ASYNC.run(() -> {
            ArtMap.getArtDatabase().getMapTable().deleteMap(mapId);
            Reflection.setWorldMap(mapView, MapManager.BLANK_MAP);
            queue.offer(mapId);
        });
    }

    public MapView getMap(short mapId) {
        return Bukkit.getMap(mapId);
    }

    private void loadKeys() {
        validateFile();
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<Short> shortList = yaml.getShortList(RECYCLED_KEYS_TAG);
        for (Short aShort : shortList) {
            queue.offer(aShort);
        }
        yaml.set(RECYCLED_KEYS_TAG, null);
    }

    private void saveKeys() {
        validateFile();
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<Short> shortList = new ArrayList<>();
        for (Short aShort : queue) {
            shortList.add(aShort);
        }
        yaml.set(RECYCLED_KEYS_TAG, shortList);
        try {
            yaml.save(file);
        } catch (IOException e) {
            ErrorLogger.log(e);
        }
    }

    private void validateFile() {
        if (!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            ErrorLogger.log(e);
        }
    }

    public enum MapSize {
        MAX(128 * 128), STANDARD(32 * 32);
        final int size;

        MapSize(int length) {
            this.size = length;
        }

        public int size() {
            return size;
        }
    }
}

package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.ArtDye;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class MapManager {

    public static final byte[] BLANK_MAP = getBlankMap();
    private static final String RECYCLED_KEYS_TAG = "recycled_keys";
    private final File file;
    private PriorityQueue<Short> queue = new PriorityQueue<>();

    public MapManager(ArtMap plugin) {
        file = new File(plugin.getDataFolder(), "keys.yml");
        loadKeys();
    }


    private static byte[] getBlankMap() {
        byte[] mapOutput = new byte[128 * 128];
        Arrays.fill(mapOutput, ArtDye.WHITE.getData());
        return mapOutput;
    }

    public static MapView cloneArtwork(World world, short mapID) {
        MapView oldMapView = Bukkit.getServer().getMap(mapID);
        MapView newMapView = Bukkit.getServer().createMap(world);
        byte[] oldMap = Reflection.getMap(oldMapView);
        Reflection.setWorldMap(newMapView, oldMap);
        return newMapView;
    }

    public void recycleID(short id) {
        queue.offer(id);
    }

    public MapView generateMapID(World world) {
        Short id = queue.poll();
        if (id != null && ArtMap.getArtDatabase().getArtwork(id) == null) {
            return Bukkit.getMap(id);
        } else {
            return Bukkit.createMap(world);
        }
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

    public void saveKeys() {
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
        private final int size;

        MapSize(int length) {
            this.size = length;
        }

        public int size() {
            return size;
        }
    }
}

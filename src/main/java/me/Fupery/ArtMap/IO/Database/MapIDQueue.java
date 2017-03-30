package me.Fupery.ArtMap.IO.Database;

import me.Fupery.ArtMap.IO.ErrorLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class MapIDQueue {
    private static final String RECYCLED_KEYS_TAG = "recycled_keys";
    private final File file;
    private PriorityQueue<Short> queue = new PriorityQueue<>();

    MapIDQueue(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "keys.yml");
    }

    Short poll() {
        return queue.poll();
    }

    void offer(Short id) {
        queue.offer(id);
    }

    void loadIds() {
        validateFile();
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<Short> shortList = yaml.getShortList(RECYCLED_KEYS_TAG);
        for (Short aShort : shortList) {
            queue.offer(aShort);
        }
        yaml.set(RECYCLED_KEYS_TAG, null);
    }

    void saveIds() {
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
            if (!file.createNewFile()) {
                throw new IOException("Cannot create file.");
            }
        } catch (IOException e) {
            ErrorLogger.log(e);
        }
    }
}

package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class YamlReader {
    private ArtMap plugin;
    private String fileName;

    public YamlReader(ArtMap plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    public YamlReader(String fileName) {
        plugin = ArtMap.instance();
        this.fileName = fileName;
    }

    public FileConfiguration readFromResources() {
        return YamlConfiguration.loadConfiguration(plugin.getTextResourceFile(fileName));
    }

    public FileConfiguration readFromDataFolder() {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!plugin.getDataFolder().exists() || !file.exists()) return null;
        return YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration tryDataFolder() {
        FileConfiguration config = readFromDataFolder();
        if (config != null) return config;
        File file = new File(plugin.getDataFolder(), fileName);
        if (!ArtMap.instance().writeResource(fileName, file)) return readFromResources();
        else return YamlConfiguration.loadConfiguration(file);
    }
}

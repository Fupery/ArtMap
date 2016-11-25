package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
        try {
            if (!file.createNewFile()) return readFromResources();
            Files.copy(plugin.getResource(fileName), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().info(String.format("Failed to build %s file", fileName));
            return readFromResources();
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}

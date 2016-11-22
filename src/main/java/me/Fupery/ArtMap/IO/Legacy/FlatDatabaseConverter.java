package me.Fupery.ArtMap.IO.Legacy;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class FlatDatabaseConverter {

    private JavaPlugin plugin;

    public FlatDatabaseConverter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean convertDatabase() {
        String dbFileName = "mapList.yml";
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        if (!databaseFile.exists()) return false;
        plugin.getLogger().info("Old 'mapList.yml' database found! Converting to new format ...");
        plugin.getLogger().info("(This may take a while, but only needs to run once)");
        HashMap<MapArt, MapView> artworks = readArtworks(databaseFile);

        if (artworks != null && artworks.size() > 0) {
            ArtMap.getTaskManager().ASYNC.run(() -> ArtMap.getArtDatabase().addArtworks(artworks));
        }

        File disabledDatabaseFile = new File(plugin.getDataFolder(), dbFileName + ".off");
        if (!databaseFile.renameTo(disabledDatabaseFile)) {
            plugin.getLogger().info("Error disabling mapList.yml! Delete this file manually.");
            return false;
        }
        plugin.getLogger().info(String.format("Conversion completed! %s artworks converted. " +
                "mapList.yml has been disabled.", artworks.size()));
        return true;
    }

    public HashMap<MapArt, MapView> readArtworks(File databaseFile) {
        HashMap<MapArt, MapView> artworkList = new HashMap<>();
        FileConfiguration database = YamlConfiguration.loadConfiguration(databaseFile);
        ConfigurationSection artworks = database.getConfigurationSection("artworks");

        if (artworks == null) return artworkList;

        for (String title : artworks.getKeys(false)) {
            ConfigurationSection map = artworks.getConfigurationSection(title);
            if (map != null) {
                short mapIDValue = (short) map.getInt("mapID");
                OfflinePlayer player = (map.contains("artist")) ?
                        Bukkit.getOfflinePlayer(UUID.fromString(map.getString("artist"))) : null;
                String date = map.getString("date");
                MapView mapView = Bukkit.getMap(mapIDValue);
                if (mapView == null) {
                    plugin.getLogger().info(String.format("    Ignoring '%s' (failed to access map data) ...", title));
                    continue;
                }
                if (player == null || !player.hasPlayedBefore()) {
                    plugin.getLogger().info(String.format("    Ignoring '%s' (artist UUID is invalid) ...", title));
                    continue;
                }
                MapArt artwork = new MapArt(mapIDValue, title, player, date);
                if (ArtMap.getArtDatabase().containsArtwork(artwork, true)) {
                    plugin.getLogger().info(String.format("    Ignoring '%s' (already exists in database) ...", title));
                } else {
                    plugin.getLogger().info(String.format("    Converting '%s' ...", title));
                    artworkList.put(artwork, mapView);
                }
            }
        }
        return artworkList;
    }
}

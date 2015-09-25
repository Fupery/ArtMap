package me.Fupery.Artiste.IO;

import me.Fupery.Artiste.Artiste;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class MapArt {

    short mapID;
    String title;
    OfflinePlayer player;

    public MapArt(short mapID, String title, OfflinePlayer player) {
        this.mapID = mapID;
        this.title = title;
        this.player = player;
    }

    public static MapArt saveArtwork(Artiste plugin, short mapID, String title, OfflinePlayer player) {

        if (plugin.getMaps() != null) {
            ConfigurationSection mapList = plugin.getMaps().getConfigurationSection("artworks");
            ConfigurationSection map = mapList.createSection(title);
            map.set("id", mapID);
            map.set("artist", player.getUniqueId().toString());
            plugin.updateMaps();
            return new MapArt(mapID, title, player);
        }
        return null;
    }

    public static MapArt getArtwork(Artiste plugin, String title) {

        if (plugin.getMaps() != null) {
            ConfigurationSection mapList = plugin.getMaps().getConfigurationSection("artworks");
            ConfigurationSection map = mapList.getConfigurationSection(title);
            int mapID = map.getInt("id");
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(map.getString("artist")));
            return new MapArt(((short) mapID), title, player);
        }
        return null;
    }

    public static boolean deleteArtwork(Artiste plugin, String title) {

        if (plugin.getMaps() != null) {
            ConfigurationSection mapList = plugin.getMaps().getConfigurationSection("artworks");
            ConfigurationSection map = mapList.getConfigurationSection(title);

            if (map != null) {
                int mapID = map.getInt("id");
                //clear map data
                WorldMap worldMap = new WorldMap(Bukkit.getMap(((short) mapID)));
                worldMap.delete();
                //remove map from list
                mapList.set(title, null);
                plugin.updateMaps();
            }
        }
        return false;
    }
}

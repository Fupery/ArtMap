package me.Fupery.Artiste.IO;

import me.Fupery.Artiste.Artiste;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class MapArt {

    public static String getTitle(Artiste plugin, short mapID) {
        String tag = ((Short) mapID).toString();

        FileConfiguration config = plugin.getIDList();

        if (config.contains(tag)) {
            ConfigurationSection key = config.getConfigurationSection(tag);
            return key.getString("title");
        }
        return null;
    }

    public static UUID getArtist(Artiste plugin, short mapID) {
        String tag = ((Short) mapID).toString();

        FileConfiguration config = plugin.getIDList();

        if (config.contains(tag)) {
            ConfigurationSection key = config.getConfigurationSection(tag);
            return UUID.fromString(key.getString("artist"));
        }
        return null;
    }
}

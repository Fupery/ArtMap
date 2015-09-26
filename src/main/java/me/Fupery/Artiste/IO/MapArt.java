package me.Fupery.Artiste.IO;

import me.Fupery.Artiste.Artiste;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class MapArt {

    public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static final String artworkTag = "§aPlayer Artwork";

    private short mapID;
    private String title;
    private OfflinePlayer player;

    public MapArt(short mapID, String title, OfflinePlayer player) {
        this.mapID = mapID;
        this.title = title;
        this.player = player;
    }

    public ItemStack getMapItem() {

        ItemStack map = new ItemStack(Material.MAP, 1, mapID);

        Date d = new Date();

        ItemMeta meta = map.getItemMeta();

        meta.setDisplayName(title);

        meta.setLore(Arrays.asList(
                artworkTag,
                ChatColor.GOLD + "by " + ChatColor.YELLOW + player.getName(),
                dateFormat.format(d)));
        map.setItemMeta(meta);

        return map;
    }

    public MapArt saveArtwork(Artiste plugin) {

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

    public short getMapID() {
        return mapID;
    }

    public String getTitle() {
        return title;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }
}

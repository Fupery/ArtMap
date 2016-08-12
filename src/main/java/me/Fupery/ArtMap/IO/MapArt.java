package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ArtDye;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapArt {
    public static final byte[] BLANK_MAP = getBlankMap();
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private final short mapIDValue;
    private final String title;
    private final OfflinePlayer player;
    private final String date;

    public MapArt(short mapIDValue, String title, OfflinePlayer player) {
        this.mapIDValue = mapIDValue;
        this.title = title;
        this.player = player;
        this.date = DATE_FORMAT.format(new Date());
    }

    public MapArt(short mapIDValue, String title, OfflinePlayer player, String date) {
        this.mapIDValue = mapIDValue;
        this.title = title;
        this.player = player;
        this.date = date;
    }

    public static MapView cloneArtwork(World world, short mapID) {
        MapView oldMapView = Bukkit.getServer().getMap(mapID);
        MapView newMapView = Bukkit.getServer().createMap(world);
        byte[] oldMap = Reflection.getMap(oldMapView);
        Reflection.setWorldMap(newMapView, oldMap);
        return newMapView;
    }

    private static byte[] getBlankMap() {
        byte[] mapOutput = new byte[128 * 128];

        for (int i = 0; i < mapOutput.length; i++) {
            mapOutput[i] = ArtDye.WHITE.getData();
        }
        return mapOutput;
    }

    public boolean isValid() {
        return title != null && title.length() > 2 && title.length() <= 16
                && player != null && player.hasPlayedBefore();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MapArt && equals(((MapArt) obj), false);
    }

    public boolean equals(MapArt art, boolean ignoreMapID) {
        return (title.equals(art.title) && date.equals(art.date))
                && player.getUniqueId().equals(art.player.getUniqueId())
                && (mapIDValue == art.mapIDValue || ignoreMapID);
    }

    public ItemStack getMapItem() {
        return ArtMaterial.getMapArt(mapIDValue, title, player, date);
    }

    public void saveArtwork() {
        ArtMap.getArtDatabase().addArtwork(this);
    }

    public String getDate() {
        return date;
    }

    public short getMapID() {
        return mapIDValue;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public String getTitle() {
        return title;
    }
}

package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MapArt {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private final short id;
    private final String title;
    private final UUID artist;
    private final String date;

    public MapArt(short mapIDValue, String title, OfflinePlayer artist) {
        this(mapIDValue, title, artist, DATE_FORMAT.format(new Date()));
    }

    public MapArt(short id, String title, UUID artist, String date) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.date = date;
    }

    public MapArt(short mapIDValue, String title, OfflinePlayer artist, String date) {
        this.id = mapIDValue;
        this.title = title;
        this.artist = artist.getUniqueId();
        this.date = date;
    }

    public OfflinePlayer getArtistPlayer() {
        return Bukkit.getOfflinePlayer(artist);
    }

    public boolean isValid() {
        return title != null && title.length() > 2 && title.length() <= 16
                && getArtistPlayer() != null && getArtistPlayer().hasPlayedBefore();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MapArt && equals(((MapArt) obj), false);
    }

    public boolean equals(MapArt art, boolean ignoreMapID) {
        return (title.equals(art.title) && date.equals(art.date))
                && artist.equals(art.artist)
                && (id == art.id || ignoreMapID);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(77, 123);
        builder.append(title);
        builder.append(id);
        return builder.toHashCode();
    }

    public ItemStack getMapItem() {
        return ArtMaterial.getMapArt(id, title, getArtistPlayer(), date);
    }

    public void saveArtwork() {
        MapView mapView = Bukkit.getMap(id);
        ArtMap.getTaskManager().ASYNC.run(() -> ArtMap.getArtDatabase().addArtwork(this, mapView));
    }

    public short getMapId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public UUID getArtist() {
        return artist;
    }

    public String getDate() {
        return date;
    }

    public MapArt updateMapId(short newID) {
        return new MapArt(newID, title, artist, date);
    }
}

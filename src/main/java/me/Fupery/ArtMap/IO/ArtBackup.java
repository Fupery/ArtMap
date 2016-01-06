package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.io.*;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ArtBackup implements Serializable {
    public static final long serialVersionUID = 41217749L;

    private final short mapIDValue;
    private final String title;
    private final UUID player;
    private final String date;
    byte[] colors;

    public ArtBackup(ArtMap plugin, MapArt art) {
        this.mapIDValue = art.getMapID();
        this.title = art.getTitle();
        this.player = art.getPlayer().getUniqueId();
        this.date = art.getDate();
        this.colors = plugin.getNmsInterface().getMap(Bukkit.getMap(art.getMapID()));
    }

    public static ArtBackup read(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
        ArtBackup backup = ((ArtBackup) ois.readObject());
        ois.close();
        return backup;
    }

    public void save(ArtMap plugin, boolean overwrite) {
        MapView oldMapView = Bukkit.getMap(mapIDValue);
        MapView mapView = overwrite ? oldMapView : Bukkit.createMap(oldMapView.getWorld());
        plugin.getNmsInterface().setWorldMap(mapView, colors);
        new MapArt(mapIDValue, title, Bukkit.getOfflinePlayer(player), date).saveArtwork(plugin);
    }

    public void write(File file) throws IOException {
        if (!file.createNewFile()) {
            throw new IOException("Could not create backup file!");
        }
        ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
        oos.writeObject(this);
        oos.flush();
        oos.close();
    }
}

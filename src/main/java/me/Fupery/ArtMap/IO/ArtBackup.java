package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.GenericMapRenderer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

import java.io.*;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ArtBackup implements Serializable {
    public static final long serialVersionUID = 41217749L;
    private final String title;
    private final UUID player;
    private final String date;
    byte[] map;
    private short mapID;

    public ArtBackup(MapArt art) {
        this.mapID = art.getMapID();
        this.title = art.getTitle();
        this.player = art.getPlayer().getUniqueId();
        this.date = art.getDate();
        this.map = ArtMap.nmsInterface.getMap(Bukkit.getMap(art.getMapID()));
    }

    public static ArtBackup read(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
        ArtBackup backup = ((ArtBackup) ois.readObject());
        ois.close();
        return backup;
    }

    public void save(World world, boolean overwrite) {
        MapView mapView = Bukkit.getMap(mapID);

        if (mapView == null || overwrite) {
            mapView = Bukkit.createMap(world);
        }
        mapView.addRenderer(new GenericMapRenderer(map));
        ArtMap.nmsInterface.setWorldMap(mapView, map);
        new MapArt(mapID, title, Bukkit.getOfflinePlayer(player), date).saveArtwork();
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

    public MapArt getMapArt() {
        return new MapArt(mapID, title, Bukkit.getOfflinePlayer(player), date);
    }

    public byte[] getMap() {
        return map;
    }

    public short getMapID() {
        return mapID;
    }

    public void setMapID(short mapID) {
        this.mapID = mapID;
    }
}

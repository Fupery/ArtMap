package me.Fupery.ArtMap.IO.Database;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.ErrorLogger;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.MapId;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class Database {
    private final ArtTable artworks;
    private final MapTable maps;
    private final SQLiteDatabase database;

    public Database(SQLiteDatabase database, ArtTable artworks, MapTable maps) {
        this.database = database;
        this.artworks = artworks;
        this.maps = maps;
    }

    public static Database build(JavaPlugin plugin) {
        SQLiteDatabase database;
        ArtTable artworks;
        MapTable maps;
        database = new SQLiteDatabase(new File(plugin.getDataFolder(), "ArtMap.db"));
        if (!database.initialize(artworks = new ArtTable(database), maps = new MapTable(database))) return null;
        Database db = new Database(database, artworks, maps);
        try {
            db.loadArtworks();
        } catch (Exception e) {
            ErrorLogger.log(e, "Error Loading ArtMap Database");
            return null;
        }
        return db;
    }

    public MapArt getArtwork(String title) {
        return artworks.getArtwork(title);
    }

    public MapArt getArtwork(short id) {
        return artworks.getArtwork(id);
    }

    public boolean saveArtwork(MapArt art) {
        MapView mapView = ArtMap.getMapManager().getMap(art.getMapId());
        ArtMap.getTaskManager().ASYNC.run(() -> {
            artworks.addArtwork(art);
            CompressedMap map = CompressedMap.compress(mapView);
            if (maps.containsMap(art.getMapId())) maps.updateMap(map);
            else maps.addMap(map);
        });
        return true;
    }

    public boolean deleteArtwork(MapArt art) {
        if (artworks.deleteArtwork(art.getTitle())) {
            maps.deleteMap(art.getMapId());
            ArtMap.getTaskManager().SYNC.run(() -> ArtMap.getMapManager().deleteMap(art.getMapId()));
            return true;
        } else return false;
    }

    private void loadArtworks() {
        assert Bukkit.isPrimaryThread(); //todo error logging etc.
        List<MapId> ids = maps.getMapIds();
        for (MapId map : ids) {
            MapView mapView = ArtMap.getMapManager().getMap(map.getId());
            if (mapView != null) {
                byte[] storedMap = Reflection.getMap(mapView);
                if (!(Arrays.hashCode(storedMap) == map.getHash())) {
                    fixMap(map.getId(), mapView); //todo logging
                }
            } else {//map doesn't exist!
                //todo spicier magic may be needed here to bring the map from the dead

                //if less than max
                //
//                mapView = Bukkit.createMap(Bukkit.getWorld(ArtMap.getConfiguration().WORLD)); //todo lmao
//                short newMapID = mapView.getId();
//                fixMap(map.getId(), mapView);
//                maps.updateMapId(map.getId(), newMapID);
            }
        }
    }

    private void fixMap(short mapId, MapView mapView) {
        CompressedMap map = maps.getMap(mapId);
        byte[] mapData = map.decompressMap();
        ArtMap.getMapManager().setMap(mapView, mapData);
    }

    public ArtTable getArtTable() {
        return artworks;
    }

    public MapTable getMapTable() {
        return maps;
    }
}

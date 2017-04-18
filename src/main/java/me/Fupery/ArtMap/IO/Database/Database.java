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
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class Database {
    private final ArtTable artworks;
    private final MapTable maps;
    private final SQLiteDatabase database;
    private final MapIDQueue idQueue;
    private final BukkitRunnable AUTO_SAVE = new BukkitRunnable() {
        @Override
        public void run() {
            for (UUID uuid : ArtMap.getArtistHandler().getArtists()) {
                ArtMap.getArtistHandler().getCurrentSession(uuid).persistMap();
            }
        }
    };

    public Database(JavaPlugin plugin, SQLiteDatabase database, ArtTable artworks, MapTable maps) {
        this.database = database;
        this.artworks = artworks;
        this.maps = maps;
        idQueue = new MapIDQueue(plugin);
        int delay = ArtMap.getConfiguration().ARTWORK_AUTO_SAVE;
        AUTO_SAVE.runTaskTimerAsynchronously(plugin, delay, delay);
        idQueue.loadIds();
    }

    public static Database build(JavaPlugin plugin) {
        SQLiteDatabase database;
        ArtTable artworks;
        MapTable maps;
        database = new SQLiteDatabase(new File(plugin.getDataFolder(), "Art.db"));
        if (!database.initialize(artworks = new ArtTable(database), maps = new MapTable(database))) return null;
        Database db = new Database(plugin, database, artworks, maps);
        try {
            db.loadArtworks(plugin);
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
        MapView mapView = getMap(art.getMapId());
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
            ArtMap.getTaskManager().SYNC.run(() -> art.getMap().setMap(new byte[Map.Size.MAX.value]));
            return true;
        } else return false;
    }

    private void loadArtworks(JavaPlugin plugin) {
        assert Bukkit.isPrimaryThread(); //todo error logging etc.
        List<MapId> ids = maps.getMapIds();
        for (MapId mapId : ids) {
            Map map = new Map(mapId.getId());
            if (map.exists()) {
                byte[] storedMap = map.getData();
                if (!(Arrays.hashCode(storedMap) == mapId.getHash())) {
                    map.setMap(maps.getMap(mapId.getId()).decompressMap());
                }
            } else {//this map file doesn't exist!
                //spicy map necromancy
                short topMapId = Reflection.getNextMapId();
                if (topMapId == -1 || topMapId > mapId.getId()) continue;
                File mapFile = new File(Map.getMapDataFolder(), "map_" + mapId.getId() + ".dat");
                if (!mapFile.exists()) try {
                    if (mapFile.createNewFile()) Files.copy(plugin.getResource("blank.dat"),
                            mapFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                map.setMap(maps.getMap(mapId.getId()).decompressMap());
                //create new blank map in map folder

                //if less than max
                //
//                mapView = Bukkit.createMap(Bukkit.getWorld(ArtMap.getConfiguration().WORLD)); //todo lmao
//                short newMapID = mapView.getMapId();
//                fixMap(map.getMapId(), mapView);
//                maps.updateMapId(map.getMapId(), newMapID);
            }
        }
    }

    public ArtTable getArtTable() {
        return artworks;
    }

    public MapTable getMapTable() {
        return maps;
    }

    public void close() {
        idQueue.saveIds();
        AUTO_SAVE.cancel();
    }

    public MapView createMap() {
        Short id = idQueue.poll();
        MapView mapView;
        if (id != null && getArtwork(id) == null) {
            mapView = getMap(id);
        } else {
            mapView = Bukkit.createMap(Bukkit.getWorld(ArtMap.getConfiguration().WORLD));
        }
        Reflection.setWorldMap(mapView, Map.BLANK_MAP);
        return mapView;
    }

    public void cacheMap(Map map, byte[] data) {
        ArtMap.getTaskManager().ASYNC.run(() -> {
            CompressedMap compressedMap = CompressedMap.compress(map.getMapId(), data);
            if (maps.containsMap(map.getMapId())) maps.updateMap(compressedMap);
            else maps.addMap(compressedMap);
        });
    }

    public void restoreMap(Map map) {
        int oldMapHash = Arrays.hashCode(map.getData());
        ArtMap.getTaskManager().ASYNC.run(() -> {
            if (maps.containsMap(map.getMapId())
                    && maps.getHash(map.getMapId()) != oldMapHash) {
                map.setMap(map.getData());
            }
        });
    }

    public void recycleMap(Map map) {
        map.setMap(Map.BLANK_MAP);
        ArtMap.getTaskManager().ASYNC.run(() -> {
            maps.deleteMap(map.getMapId());
            idQueue.offer(map.getMapId());
        });
    }

    public MapView getMap(short mapId) {
        return Bukkit.getMap(mapId);
    }
}

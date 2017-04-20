package me.Fupery.ArtMap.IO.Database;

import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.ErrorLogger;
import me.Fupery.ArtMap.IO.MapId;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MapTable extends SQLiteTable {
    public MapTable(SQLiteDatabase database) {
        super(database, "maps", "CREATE TABLE IF NOT EXISTS maps (" +
                "id   INT   NOT NULL UNIQUE," +
                "hash INT   NOT NULL," +
                "map  BLOB  NOT NULL," +
                "PRIMARY KEY (id)" +
                ");");
    }

    public void addMap(CompressedMap map) {
        new QueuedStatement() {
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, map.getId());
                statement.setInt(2, map.getHash());
                statement.setBytes(3, map.getCompressedMap());
            }
        }.execute("INSERT INTO " + TABLE + " (id, hash, map) VALUES(?,?,?);");
    }

    void updateMapId(int oldMapId, int newMapId) {
        new QueuedStatement() {
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, newMapId);
                statement.setInt(2, oldMapId);
            }
        }.execute("UPDATE " + TABLE + " SET id=? WHERE id=?;");
    }

    public boolean deleteMap(short mapId) {
        return new QueuedStatement() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }
        }.execute("DELETE FROM " + TABLE + " WHERE id=?;");
    }

    public boolean containsMap(short mapId) {
        return new QueuedQuery<Boolean>() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            @Override
            protected Boolean read(ResultSet set) throws SQLException {
                return set.next();
            }
        }.execute("SELECT hash FROM " + TABLE + " WHERE id=?;");
    }

    public void updateMap(CompressedMap map) {
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, map.getHash());
                statement.setBytes(2, map.getCompressedMap());
                statement.setInt(3, map.getId());
            }
        }.execute("UPDATE " + TABLE + " SET hash=?, map=? WHERE id=?;");
    }

    public CompressedMap getMap(short mapId) {
        return new QueuedQuery<CompressedMap>() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            protected CompressedMap read(ResultSet set) throws SQLException {
                if (!set.next()) return null;
                short id = (short) set.getInt("id");
                int hash = set.getInt("hash");
                byte[] map = set.getBytes("map");
                return new CompressedMap(id, hash, map);
            }
        }.execute("SELECT * FROM " + TABLE + " WHERE id=?;");
    }

    public Integer getHash(short mapId) {
        return new QueuedQuery<Integer>() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            protected Integer read(ResultSet set) throws SQLException {
                return (set.next()) ? set.getInt("hash") : null;
            }
        }.execute("SELECT hash FROM " + TABLE + " WHERE id=?;");
    }


    List<MapId> getMapIds() {
        return new QueuedQuery<List<MapId>>() {

            protected void prepare(PreparedStatement statement) throws SQLException {
            }

            protected List<MapId> read(ResultSet set) throws SQLException {
                List<MapId> mapHashes = new ArrayList<>();
                while (set.next()) {
                    mapHashes.add(new MapId((short) set.getInt("id"), set.getInt("hash")));
                }
                return mapHashes;
            }
        }.execute("SELECT id, hash FROM " + TABLE + ";");
    }

    /**
     * @param maps A list of maps to add to the database
     * @return A list of maps that could not be added
     */
    public List<CompressedMap> addMaps(List<CompressedMap> maps) {
        List<CompressedMap> failed = new ArrayList<>();
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                for (CompressedMap map : maps) {
                    try {
                        statement.setInt(1, map.getId());
                        statement.setInt(2, map.getHash());
                        statement.setBytes(3, map.getCompressedMap());
                    } catch (Exception e) {
                        failed.add(map);
                        ErrorLogger.log(e, String.format("Error writing map %s to database!", map.getId()));
                        continue;
                    }
                    statement.addBatch();
                }
            }
        }.executeBatch("INSERT INTO " + TABLE + " (id, hash, map) VALUES(?,?,?);");
        return failed;
    }
}

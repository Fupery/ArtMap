package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.IO.ColourMap.f32x32;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class SQLiteDatabase implements ArtDatabase {

    static final String sqlError = "[ArtMap] Database error, check error.log for more info!";
    private final String TABLE = "artworks";
    private final String ALL_BUT_MAP = "title, id, artist, date";
    private final File dbFile;
    private Connection connection;

    public SQLiteDatabase(JavaPlugin plugin) {
        dbFile = new File(plugin.getDataFolder(), "ArtMap.db");
        initialize();
    }

    Connection getConnection() {
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("[ArtMap] File write error: 'ArtMap.db' - Check error.log for details");
                ErrorLogger.log(e);// TODO: 23/08/2016
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException e) {
            connection = null;
            Bukkit.getLogger().warning(sqlError);
            ErrorLogger.log(e);// TODO: 23/08/2016
        }
        return connection;
    }

    public void initialize() {
        connection = getConnection();
        Statement buildTableStatement = null;
        try {
            buildTableStatement = connection.createStatement();
            buildTableStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "title   varchar(32)       NOT NULL UNIQUE," +
                    "id      INT               NOT NULL UNIQUE," +
                    "artist  varchar(32)       NOT NULL," +
                    "date    varchar(32)       NOT NULL," +
                    "map     BLOB       NOT NULL," +
                    "PRIMARY KEY (title)" +
                    ");");
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE);
            ResultSet rs = ps.executeQuery();
        } catch (SQLException e) {
            Bukkit.getLogger().warning(sqlError);
            ErrorLogger.log(e);// TODO: 23/08/2016
        } finally {
            if (buildTableStatement != null) try {
                buildTableStatement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().warning(sqlError);
                ErrorLogger.log(e);// TODO: 23/08/2016
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                Bukkit.getLogger().warning(sqlError);
                ErrorLogger.log(e);// TODO: 23/08/2016
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public MapArt getArtwork(String title) {
        return new QueuedQuery<MapArt>() {

            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }

            @Override
            MapArt read(ResultSet set) throws SQLException {
                return (set.next()) ? readArtwork(set) : null;
            }
        }.execute("SELECT " + ALL_BUT_MAP + " FROM " + TABLE + " WHERE title=?;");
    }

    @Override
    public MapArt getArtwork(short mapData) {
        return new QueuedQuery<MapArt>() {

            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapData);
            }

            @Override
            MapArt read(ResultSet set) throws SQLException {
                return (set.next()) ? readArtwork(set) : null;
            }
        }.execute("SELECT " + ALL_BUT_MAP + " FROM " + TABLE + " WHERE id=?;");
    }

    private MapArt readArtwork(ResultSet set) throws SQLException {
        String title = set.getString("title");
        int id = set.getInt("id");
        UUID artist = UUID.fromString(set.getString("artist"));
        String date = set.getString("date");
        return new MapArt((short) id, title, artist, date);
    }

    @Override
    public boolean containsArtwork(MapArt art, boolean ignoreMapID) {
        return new QueuedQuery<Boolean>() {
            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, art.getTitle());
            }

            @Override
            Boolean read(ResultSet set) throws SQLException {
                return set.next();
            }
        }.execute("SELECT title FROM " + TABLE + " WHERE title=?;")
                && (ignoreMapID || containsMapID(art.getMapId()));
    }

    @Override
    public boolean containsMapID(short mapID) {
        return new QueuedQuery<Boolean>() {
            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapID);
            }

            @Override
            Boolean read(ResultSet set) throws SQLException {
                return set.next();
            }
        }.execute("SELECT id FROM " + TABLE + " WHERE id=?;");
    }

    @Override
    public boolean deleteArtwork(String title) {
        return new QueuedStatement() {
            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }
        }.execute("DELETE FROM " + TABLE + " WHERE title=?;");
    }

    @Override
    public MapArt[] listMapArt(UUID artist) {
        return new QueuedQuery<MapArt[]>() {
            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, artist.toString());
            }

            @Override
            MapArt[] read(ResultSet results) throws SQLException {
                ArrayList<MapArt> artworks = new ArrayList<>();
                while (results.next()) {
                    artworks.add(readArtwork(results));
                }
                return artworks.toArray(new MapArt[artworks.size()]);
            }
        }.execute("SELECT " + ALL_BUT_MAP + " FROM " + TABLE + " WHERE artist = ? ORDER BY title;");
    }

    @Override
    public UUID[] listArtists(UUID player) {
        return new QueuedQuery<UUID[]>() {
            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, player.toString());
            }

            @Override
            UUID[] read(ResultSet results) throws SQLException {
                ArrayList<UUID> artists = new ArrayList<>();
                artists.add(0, player);
                try {
                    while (results.next()) {
                        artists.add(UUID.fromString(results.getString("artist")));
                    }
                } catch (SQLException e) {
                    Bukkit.getLogger().warning(sqlError);
                    ErrorLogger.log(e);// TODO: 23/08/2016
                }
                return artists.toArray(new UUID[artists.size()]);
            }
        }.execute("SELECT DISTINCT artist FROM " + TABLE + " WHERE artist!=? ORDER BY artist;");
    }

    public void updateMapID(MapArt art) {
        new QueuedStatement() {
            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, art.getMapId());
                statement.setString(2, art.getTitle());
            }
        }.execute("UPDATE " + TABLE + " SET id=? WHERE title=?;");
    }

    public byte[] getMap(String title) {
        return new QueuedQuery<byte[]>() {

            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }

            @Override
            byte[] read(ResultSet set) throws SQLException {
                byte[] blob = set.getBytes("map");
//                Bukkit.getLogger().info(f32x32.arrayToString(blob));//todo remove logging
//                Bukkit.getLogger().info(f32x32.arrayToString(new f32x32().readBLOB(blob)));//todo remove logging
                return blob == null ? null : new f32x32().readBLOB(blob);
            }
        }.execute("SELECT map FROM " + TABLE + " WHERE title=?;");
    }

    @Override
    public void addArtwork(MapArt art) {
        byte[] map = Reflection.getMap(Bukkit.getMap(art.getMapId()));
        byte[] compressed;
        try {
            compressed = new f32x32().generateBLOB(map);
        } catch (IOException e) {
            Bukkit.getLogger().info("[ArtMap] Compression error, check error.log for more info!");
            ErrorLogger.log(e);
            return;
        }
        Bukkit.getLogger().info("MAP:" + compressed.length);//todo remove logging
        new QueuedStatement() {
            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, art.getTitle());
                statement.setInt(2, art.getMapId());
                statement.setString(3, art.getArtist().toString());
                statement.setString(4, art.getDate());
                statement.setBytes(5, compressed);
            }
        }.execute("INSERT INTO " + TABLE + " (title, id, artist, date, map) VALUES(?,?,?,?,?);");
    }

    @Override
    public void addArtworks(MapArt... artworks) {
        for (MapArt artwork : artworks) {
            addArtwork(artwork);
        }// FIXME: 23/08/2016
    }

    private abstract class QueuedStatement extends QueuedQuery<Boolean> {

        @Override
        Boolean read(ResultSet set) throws SQLException {
            return false;//unused
        }

        @Override
        Boolean execute(String query) {
            Connection connection = null;
            PreparedStatement statement = null;
            boolean result = false;
            try {
                connection = getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                result = (statement.executeUpdate() != 0);
            } catch (Exception e) {
                Bukkit.getLogger().warning(SQLiteDatabase.sqlError);
                ErrorLogger.log(e);// TODO: 23/08/2016
            } finally {
                close(connection, statement);
            }
            return result;
        }
    }

    private abstract class QueuedQuery<T> {

        abstract void prepare(PreparedStatement statement) throws SQLException;

        abstract T read(ResultSet set) throws SQLException;

        void close(Connection connection, PreparedStatement statement) {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        T execute(String query) {
            Connection connection = null;
            PreparedStatement statement = null;
            T result = null;
            try {
                connection = getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                result = read(statement.executeQuery());
            } catch (Exception e) {
                Bukkit.getLogger().warning(SQLiteDatabase.sqlError);
                ErrorLogger.log(e);// TODO: 23/08/2016
            } finally {
                close(connection, statement);
            }
            return result;
        }
    }
}

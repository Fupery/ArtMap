package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.IO.ColourMap.f32x32;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ArtDatabase {

    private static final String sqlError = "Database error, check error.log for more info!";
    private final String TABLE = "artworks";
    private final String ALL_BUT_MAP = "title, id, artist, date";
    private final File dbFile;
    private Connection connection;

    public ArtDatabase(JavaPlugin plugin) {
        dbFile = new File(plugin.getDataFolder(), "ArtMap.db");
        initialize();
    }

    private Connection getConnection() {
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                ErrorLogger.log(e, "File write error: 'ArtMap.db' - Check error.log for details");
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException e) {
            connection = null;
            ErrorLogger.log(e, sqlError);
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
            ps.executeQuery();
        } catch (SQLException e) {
            ErrorLogger.log(e, sqlError);
        } finally {
            if (buildTableStatement != null) try {
                buildTableStatement.close();
            } catch (SQLException e) {
                ErrorLogger.log(e, sqlError);
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                ErrorLogger.log(e, sqlError);
            }
        }
    }


    public void close() {
    }


    public MapArt getArtwork(String title) {
        return new QueuedQuery<MapArt>() {


            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }


            MapArt read(ResultSet set) throws SQLException {
                return (set.next()) ? readArtwork(set) : null;
            }
        }.execute("SELECT " + ALL_BUT_MAP + " FROM " + TABLE + " WHERE title=?;");
    }


    public MapArt getArtwork(short mapData) {
        return new QueuedQuery<MapArt>() {


            void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapData);
            }


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


    public boolean containsArtwork(MapArt art, boolean ignoreMapID) {
        return new QueuedQuery<Boolean>() {

            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, art.getTitle());
            }


            Boolean read(ResultSet set) throws SQLException {
                return set.next();
            }
        }.execute("SELECT title FROM " + TABLE + " WHERE title=?;")
                && (ignoreMapID || containsMapID(art.getMapId()));
    }


    public boolean containsMapID(short mapID) {
        return new QueuedQuery<Boolean>() {

            void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapID);
            }


            Boolean read(ResultSet set) throws SQLException {
                return set.next();
            }
        }.execute("SELECT id FROM " + TABLE + " WHERE id=?;");
    }


    public boolean deleteArtwork(String title) {
        return new QueuedStatement() {

            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }
        }.execute("DELETE FROM " + TABLE + " WHERE title=?;");
    }


    public MapArt[] listMapArt(UUID artist) {
        return new QueuedQuery<MapArt[]>() {

            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, artist.toString());
            }


            MapArt[] read(ResultSet results) throws SQLException {
                ArrayList<MapArt> artworks = new ArrayList<>();
                while (results.next()) {
                    artworks.add(readArtwork(results));
                }
                return artworks.toArray(new MapArt[artworks.size()]);
            }
        }.execute("SELECT " + ALL_BUT_MAP + " FROM " + TABLE + " WHERE artist = ? ORDER BY title;");
    }


    public UUID[] listArtists(UUID player) {
        return new QueuedQuery<UUID[]>() {

            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, player.toString());
            }
            UUID[] read(ResultSet results) throws SQLException {
                ArrayList<UUID> artists = new ArrayList<>();
                artists.add(0, player);
                try {
                    while (results.next()) {
                        artists.add(UUID.fromString(results.getString("artist")));
                    }
                } catch (SQLException e) {
                    ErrorLogger.log(e, sqlError);
                }
                return artists.toArray(new UUID[artists.size()]);
            }
        }.execute("SELECT DISTINCT artist FROM " + TABLE + " WHERE artist!=? ORDER BY artist;");
    }

    public void updateMapID(MapArt art) {
        new QueuedStatement() {

            void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, art.getMapId());
                statement.setString(2, art.getTitle());
            }
        }.execute("UPDATE " + TABLE + " SET id=? WHERE title=?;");
    }

    public byte[] getMap(String title) {
        return new QueuedQuery<byte[]>() {
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }
            byte[] read(ResultSet set) throws SQLException {
                byte[] blob = set.getBytes("map");
                return MapManager.decompressMap(blob);
            }
        }.execute("SELECT map FROM " + TABLE + " WHERE title=?;");
    }


    public void addArtwork(MapArt art, MapView mapView) {
        new QueuedStatement() {
            void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, art.getTitle());
                statement.setInt(2, art.getMapId());
                statement.setString(3, art.getArtist().toString());
                statement.setString(4, art.getDate());
                statement.setBytes(5, MapManager.compressMap(mapView));
            }
        }.execute("INSERT INTO " + TABLE + " (title, id, artist, date, map) VALUES(?,?,?,?,?);");
    }


    public void addArtworks(HashMap<MapArt, MapView> artworks) {
        new QueuedStatement() {
            @Override
            void prepare(PreparedStatement statement) throws SQLException {
                for (MapArt art : artworks.keySet()) {
                    statement.setString(1, art.getTitle());
                    statement.setInt(2, art.getMapId());
                    statement.setString(3, art.getArtist().toString());
                    statement.setString(4, art.getDate());
                    statement.setBytes(5, MapManager.compressMap(artworks.get(art)));
                    statement.addBatch();
                }
            }
        }.executeBatch("INSERT INTO " + TABLE + " (title, id, artist, date, map) VALUES(?,?,?,?,?);");
    }

    private abstract class QueuedStatement extends QueuedQuery<Boolean> {

        Boolean read(ResultSet set) throws SQLException {
            return false;//unused
        }

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
                ErrorLogger.log(e, ArtDatabase.sqlError);
            } finally {
                close(connection, statement);
            }
            return result;
        }

        int[] executeBatch(String query) {
            Connection connection = null;
            PreparedStatement statement = null;
            int[] result = new int[0];
            try {
                connection = getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                result = statement.executeBatch();
            } catch (Exception e) {
                ErrorLogger.log(e, ArtDatabase.sqlError);
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
                ErrorLogger.log(e, sqlError);
            } finally {
                close(connection, statement);
            }
            return result;
        }
    }
}

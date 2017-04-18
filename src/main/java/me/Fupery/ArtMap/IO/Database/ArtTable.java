package me.Fupery.ArtMap.IO.Database;

import me.Fupery.ArtMap.IO.ErrorLogger;
import me.Fupery.ArtMap.IO.MapArt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ArtTable extends SQLiteTable {

    ArtTable(SQLiteDatabase database) {
        super(database, "artworks", "CREATE TABLE IF NOT EXISTS artworks (" +
                "title   varchar(32)       NOT NULL UNIQUE," +
                "id      INT               NOT NULL UNIQUE," +
                "artist  varchar(32)       NOT NULL," +
                "date    varchar(32)       NOT NULL," +
                "PRIMARY KEY (title)" +
                ");");
    }

    public MapArt getArtwork(String title) {
        return new QueuedQuery<MapArt>() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }

            protected MapArt read(ResultSet set) throws SQLException {
                return (set.next()) ? readArtwork(set) : null;
            }
        }.execute("SELECT * FROM " + TABLE + " WHERE title=?;");
    }


    public MapArt getArtwork(short mapData) {
        return new QueuedQuery<MapArt>() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapData);
            }

            protected MapArt read(ResultSet set) throws SQLException {
                return (set.next()) ? readArtwork(set) : null;
            }
        }.execute("SELECT * FROM " + TABLE + " WHERE id=?;");
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

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, art.getTitle());
            }


            protected Boolean read(ResultSet set) throws SQLException {
                return set.next();
            }
        }.execute("SELECT title FROM " + TABLE + " WHERE title=?;")
                && (ignoreMapID || containsMapID(art.getMapId()));
    }


    public boolean containsMapID(short mapID) {
        return new QueuedQuery<Boolean>() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapID);
            }

            protected Boolean read(ResultSet set) throws SQLException {
                return set.next();
            }
        }.execute("SELECT id FROM " + TABLE + " WHERE id=?;");
    }


    public boolean deleteArtwork(String title) {
        return new QueuedStatement() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }
        }.execute("DELETE FROM " + TABLE + " WHERE title=?;");
    }

    public boolean deleteArtwork(short mapId) {
        return new QueuedStatement() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }
        }.execute("DELETE FROM " + TABLE + " WHERE id=?;");
    }


    public MapArt[] listMapArt(UUID artist) {
        return new QueuedQuery<MapArt[]>() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, artist.toString());
            }

            protected MapArt[] read(ResultSet results) throws SQLException {
                ArrayList<MapArt> artworks = new ArrayList<>();
                while (results.next()) {
                    artworks.add(readArtwork(results));
                }
                return artworks.toArray(new MapArt[artworks.size()]);
            }
        }.execute("SELECT * FROM " + TABLE + " WHERE artist = ? ORDER BY title;");
    }


    public UUID[] listArtists(UUID player) {
        return new QueuedQuery<UUID[]>() {

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, player.toString());
            }

            protected UUID[] read(ResultSet results) throws SQLException {
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

            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, art.getMapId());
                statement.setString(2, art.getTitle());
            }
        }.execute("UPDATE " + TABLE + " SET id=? WHERE title=?;");
    }

    public void addArtwork(MapArt art) {
        new QueuedStatement() {
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, art.getTitle());
                statement.setInt(2, art.getMapId());
                statement.setString(3, art.getArtist().toString());
                statement.setString(4, art.getDate());
            }
        }.execute("INSERT INTO " + TABLE + " (title, id, artist, date) VALUES(?,?,?,?);");
    }

    /**
     * @param artworks A list of artworks to add to the database
     * @return A list of artworks that could not be added
     */
    public List<MapArt> addArtworks(List<MapArt> artworks) {
        List<MapArt> failed = new ArrayList<>();
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                for (MapArt art : artworks) {
                    try {
                        statement.setString(1, art.getTitle());
                        statement.setInt(2, art.getMapId());
                        statement.setString(3, art.getArtist().toString());
                        statement.setString(4, art.getDate());
                    } catch (Exception e) {
                        failed.add(art);
                        ErrorLogger.log(e, String.format("Error writing %s to database!", art.getTitle()));
                        continue;
                    }
                    statement.addBatch();
                }
            }
        }.executeBatch("INSERT INTO " + TABLE + " (title, id, artist, date, map) VALUES(?,?,?,?,?);");
        return failed;
    }

}

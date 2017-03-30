package me.Fupery.ArtMap.IO.Legacy;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Database.SQLiteDatabase;
import me.Fupery.ArtMap.IO.Database.SQLiteTable;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class OldDatabaseConverter {

    private JavaPlugin plugin;

    public OldDatabaseConverter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean convertDatabase() {
        SQLiteDatabase database = new OldDatabase(plugin);


        String dbFileName = "mapList.yml";
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        if (!databaseFile.exists()) return false;
        plugin.getLogger().info("Old 'mapList.yml' database found! Converting to new format ...");
        plugin.getLogger().info("(This may take a while, but only needs to run once)");
        HashMap<MapArt, MapView> artworks = readArtworks(databaseFile);

        if (artworks != null && artworks.size() > 0) {
            ArtMap.getTaskManager().ASYNC.run(() -> ArtMap.getArtDatabase().getArtTable().addArtworks(artworks));
        }

        File disabledDatabaseFile = new File(plugin.getDataFolder(), dbFileName + ".off");
        if (!databaseFile.renameTo(disabledDatabaseFile)) {
            plugin.getLogger().info("Error disabling mapList.yml! Delete this file manually.");
            return false;
        }
        plugin.getLogger().info(String.format("Conversion completed! %s artworks converted. " +
                "mapList.yml has been disabled.", artworks.size()));
        return true;
    }

    public HashMap<MapArt, MapView> readArtworks(File databaseFile) {
        HashMap<MapArt, MapView> artworkList = new HashMap<>();
        FileConfiguration database = YamlConfiguration.loadConfiguration(databaseFile);
        ConfigurationSection artworks = database.getConfigurationSection("artworks");

        if (artworks == null) return artworkList;

        for (String title : artworks.getKeys(false)) {
            ConfigurationSection map = artworks.getConfigurationSection(title);
            if (map != null) {
                short mapIDValue = (short) map.getInt("mapID");
                OfflinePlayer player = (map.contains("artist")) ?
                        Bukkit.getOfflinePlayer(UUID.fromString(map.getString("artist"))) : null;
                String date = map.getString("date");
                MapView mapView = Bukkit.getMap(mapIDValue);
                if (mapView == null) {
                    plugin.getLogger().info(String.format("    Ignoring '%s' (failed to access map data) ...", title));
                    continue;
                }
                if (player == null || !player.hasPlayedBefore()) {
                    plugin.getLogger().info(String.format("    Ignoring '%s' (artist UUID is invalid) ...", title));
                    continue;
                }
                MapArt artwork = new MapArt(mapIDValue, title, player, date);
                if (ArtMap.getArtDatabase().getArtTable().containsArtwork(artwork, true)) {
                    plugin.getLogger().info(String.format("    Ignoring '%s' (already exists in database) ...", title));
                } else {
                    plugin.getLogger().info(String.format("    Converting '%s' ...", title));
                    artworkList.put(artwork, mapView);
                }
            }
        }
        return artworkList;
    }

    class OldDatabase extends SQLiteDatabase {

        public OldDatabase(JavaPlugin plugin) {
            super(new File(plugin.getDataFolder(), "ArtMap.db"));
        }

        boolean connect() {
            OldDatabaseTable table = new OldDatabaseTable(this);
            if (!initialize(table)) return false;
        }

        @Override
        protected Connection getConnection() {
            return super.getConnection();
        }

        class OldDatabaseTable extends SQLiteTable {
            protected OldDatabaseTable(SQLiteDatabase database) {
                super(database, "artworks", "SELECT * FROM artworks");
            }

            @Override
            protected boolean create() {
                return new QueuedQuery<Boolean>() {
                    @Override
                    protected void prepare(PreparedStatement statement) throws SQLException {

                    }

                    @Override
                    protected Boolean read(ResultSet set) throws SQLException {
                        return set.next();
                    }
                }.execute("SELECT * FROM artworks LIMIT 1");
            }
        }
    }
}

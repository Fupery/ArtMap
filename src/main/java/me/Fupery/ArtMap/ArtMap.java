package me.Fupery.ArtMap;

import me.Fupery.ArtMap.Command.ArtMapCommandExecutor;
import me.Fupery.ArtMap.IO.ArtDatabase;
import me.Fupery.ArtMap.Listeners.*;
import me.Fupery.ArtMap.NMS.InvalidVersion;
import me.Fupery.ArtMap.NMS.NMSInterface;
import me.Fupery.ArtMap.NMS.VersionHandler;
import me.Fupery.ArtMap.Protocol.ArtistHandler;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.Preview;
import me.Fupery.DataTables.DataTables;
import me.Fupery.DataTables.PixelTable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ArtMap extends JavaPlugin {

    public static final NMSInterface nmsInterface = new VersionHandler().getNMSInterface();
    public static final ArtistHandler artistHandler = new ArtistHandler();
    public static final ConcurrentHashMap<Player, Preview> previewing = new ConcurrentHashMap<>();
    private static ArtDatabase artDatabase;
    private List<String> titleFilter;
    private int mapResolutionFactor;
    private PixelTable pixelTable;

    public static ArtDatabase getArtDatabase() {
        return artDatabase;
    }

    public static ArtMap plugin() {
        return (ArtMap) Bukkit.getPluginManager().getPlugin("ArtMap");
    }

    public static void runTask(Runnable runnable) {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ArtMap");
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void runTaskAsync(Runnable runnable) {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ArtMap");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void onEnable() {

        if (nmsInterface instanceof InvalidVersion) {
            String version = ((InvalidVersion) nmsInterface).getVersion();
            getLogger().warning(String.format(
                    Lang.INVALID_VERSION.rawMessage(), version));
            getPluginLoader().disablePlugin(this);
            return;
        }
        saveDefaultConfig();

        mapResolutionFactor = getConfig().getInt("mapResolutionFactor");

        FileConfiguration filter =
                YamlConfiguration.loadConfiguration(getTextResource("titleFilter.yml"));

        titleFilter = filter.getStringList("blacklisted");

        artDatabase = ArtDatabase.buildDatabase();

        if (artDatabase == null) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        int factor = getConfig().getInt("mapResolutionFactor");

        if (factor % 16 == 0 && factor <= 128) {
            mapResolutionFactor = 128 / factor;

        } else {
            mapResolutionFactor = 4;
            getLogger().warning(Lang.INVALID_RESOLUTION.rawMessage());
        }

        if (!loadTables()) {
            getLogger().warning(Lang.INVALID_DATA_TABLES.rawMessage());
            getPluginLoader().disablePlugin(this);
            return;
        }

        ArtMaterial.setupRecipes();

        getCommand("artmap").setExecutor(new ArtMapCommandExecutor());

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerInteractListener(), this);
        manager.registerEvents(new PlayerInteractEaselListener(), this);
        manager.registerEvents(new PlayerQuitListener(), this);
        manager.registerEvents(new ChunkUnloadListener(), this);
        manager.registerEvents(new PlayerCraftListener(), this);
        manager.registerEvents(new InventoryInteractListener(), this);
        manager.registerEvents(new EaselInteractListener(), this);
        manager.registerEvents(new MenuListener(), this);
    }

    @Override
    public void onDisable() {
        artistHandler.clearPlayers();
        artistHandler.getProtocol().close();

        if (previewing.size() > 0) {

            for (Player player : previewing.keySet()) {
                Preview.stop(player);
            }
        }
    }

    private boolean loadTables() {

        try {
            pixelTable = DataTables.loadTable(mapResolutionFactor);
        } catch (DataTables.InvalidResolutionFactorException e) {
            pixelTable = null;
            e.printStackTrace();
        }
        return (pixelTable != null);
    }

    public int getMapResolutionFactor() {
        return mapResolutionFactor;
    }

    public List<String> getTitleFilter() {
        return titleFilter;
    }

    public PixelTable getPixelTable() {
        return pixelTable;
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }

}
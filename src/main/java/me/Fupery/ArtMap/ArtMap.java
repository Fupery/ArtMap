package me.Fupery.ArtMap;

import me.Fupery.ArtMap.Command.CommandHandler;
import me.Fupery.ArtMap.HelpMenu.HelpMenu;
import me.Fupery.ArtMap.IO.ArtDatabase;
import me.Fupery.ArtMap.Listeners.*;
import me.Fupery.ArtMap.Protocol.ArtistHandler;
import me.Fupery.ArtMap.Protocol.Channel.ChannelCacheManager;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.*;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.DataTables.DataTables;
import me.Fupery.DataTables.PixelTable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ArtMap extends JavaPlugin {

    private static ArtistHandler artistHandler;
    private static ConcurrentHashMap<Player, Preview> previewing;
    private static VersionHandler bukkitVersion;
    private static TaskManager taskManager;
    private static ArtDatabase artDatabase;
    private static ChannelCacheManager cacheManager;
    private static Lang lang;
    private final int mapResolutionFactor = 4;// TODO: 20/07/2016 consider adding other resolutions
    private List<String> titleFilter;
    private PixelTable pixelTable;
    private WeakReference<HelpMenu> helpMenu;

    public static ArtDatabase getArtDatabase() {
        return artDatabase;
    }

    public static ArtMap plugin() {
        return (ArtMap) Bukkit.getPluginManager().getPlugin("ArtMap");
    }

    public static HelpMenu getHelpMenu() {
        ArtMap plugin = plugin();
        if (plugin.helpMenu.get() == null) {
            plugin.helpMenu = new WeakReference<>(new HelpMenu());
        }
        return plugin.helpMenu.get();
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static ArtistHandler getArtistHandler() {
        return artistHandler;
    }

    public static ConcurrentHashMap<Player, Preview> getPreviewing() {
        return previewing;
    }

    public static VersionHandler getBukkitVersion() {
        return bukkitVersion;
    }

    public static ChannelCacheManager getCacheManager() {
        return cacheManager;
    }

    public static Lang getLang() {
        return lang;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        taskManager = new TaskManager(this);
        previewing = new ConcurrentHashMap<>();
        artistHandler = new ArtistHandler();
        bukkitVersion = VersionHandler.getVersion();
        artDatabase = ArtDatabase.buildDatabase();
        cacheManager = new ChannelCacheManager();
        FileConfiguration langFile = YamlConfiguration.loadConfiguration(getTextResource("lang.yml"));
        lang = new Lang(getConfig().getString("language"), langFile);

        if (artDatabase == null) {
            getPluginLoader().disablePlugin(this);
            getLogger().warning(lang.getMsg("CANNOT_BUILD_DATABASE"));
            return;
        }
        if (!loadTables()) {
            getLogger().warning(lang.getMsg("INVALID_DATA_TABLES"));
            getPluginLoader().disablePlugin(this);
            return;
        }
        FileConfiguration filter = YamlConfiguration.loadConfiguration(getTextResource("titleFilter.yml"));
        titleFilter = filter.getStringList("blacklisted");

        getCommand("artmap").setExecutor(new CommandHandler());

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerInteractListener(), this);
        manager.registerEvents(new PlayerInteractEaselListener(), this);
        manager.registerEvents(new PlayerQuitListener(), this);
        manager.registerEvents(new ChunkUnloadListener(), this);
        manager.registerEvents(new PlayerCraftListener(), this);
        manager.registerEvents(new InventoryInteractListener(), this);
        manager.registerEvents(new EaselInteractListener(), this);

        helpMenu = new WeakReference<>(null);

        Stats.init(this);
        ArtMaterial.setupRecipes();
    }

    @Override
    public void onDisable() {
        artistHandler.stop();

        if (previewing.size() > 0) {

            for (Player player : previewing.keySet()) {
                Preview.stop(player);
            }
        }
        taskManager = null;
        previewing = null;
        artistHandler = null;
        bukkitVersion = null;
        artDatabase = null;
        cacheManager = null;
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
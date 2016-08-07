package me.Fupery.ArtMap;

import me.Fupery.ArtMap.Command.CommandHandler;
import me.Fupery.ArtMap.HelpMenu.HelpMenu;
import me.Fupery.ArtMap.IO.ArtDatabase;
import me.Fupery.ArtMap.Listeners.*;
import me.Fupery.ArtMap.Protocol.ArtistHandler;
import me.Fupery.ArtMap.Protocol.Channel.ChannelCacheManager;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.*;
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
    private PixelTableManager pixelTable;
    private WeakReference<HelpMenu> helpMenu;
    private boolean hasRegisteredListeners = false;

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
        bukkitVersion = new VersionHandler();
        artDatabase = ArtDatabase.buildDatabase();
        cacheManager = new ChannelCacheManager();
        FileConfiguration langFile = YamlConfiguration.loadConfiguration(getTextResource("lang.yml"));
        boolean disableActionBar = getConfig().getBoolean("disableActionBar");
        lang = new Lang(getConfig().getString("language"), langFile, disableActionBar);

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

        if (!hasRegisteredListeners) {
            PluginManager manager = getServer().getPluginManager();
            manager.registerEvents(new PlayerInteractListener(), this);
            manager.registerEvents(new PlayerInteractEaselListener(), this);
            manager.registerEvents(new PlayerQuitListener(), this);
            manager.registerEvents(new ChunkUnloadListener(), this);
            manager.registerEvents(new PlayerCraftListener(), this);
            manager.registerEvents(new InventoryInteractListener(), this);
            manager.registerEvents(new EaselInteractListener(), this);
            if (bukkitVersion.getVersion() != VersionHandler.BukkitVersion.v1_8) {
                manager.registerEvents(new PlayerSwapHandListener(), this);
                manager.registerEvents(new PlayerDismountListener(), this);
            }
            hasRegisteredListeners = true;
        }
        helpMenu = new WeakReference<>(null);
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
        reloadConfig();
        taskManager = null;
        previewing = null;
        artistHandler = null;
        bukkitVersion = null;
        artDatabase = null;
        cacheManager = null;
        lang = null;
    }

    private boolean loadTables() {
        return ((pixelTable = PixelTableManager.buildTables(mapResolutionFactor)) != null);
    }

    public int getMapResolutionFactor() {
        return mapResolutionFactor;
    }

    public List<String> getTitleFilter() {
        return titleFilter;
    }

    public PixelTableManager getPixelTable() {
        return pixelTable;
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }
}
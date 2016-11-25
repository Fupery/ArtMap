package me.Fupery.ArtMap;

import me.Fupery.ArtMap.Colour.BasicPalette;
import me.Fupery.ArtMap.Colour.Palette;
import me.Fupery.ArtMap.Command.CommandHandler;
import me.Fupery.ArtMap.Compatability.CompatibilityManager;
import me.Fupery.ArtMap.Config.Configuration;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.ArtDatabase;
import me.Fupery.ArtMap.IO.Legacy.FlatDatabaseConverter;
import me.Fupery.ArtMap.IO.MapManager;
import me.Fupery.ArtMap.IO.PixelTableManager;
import me.Fupery.ArtMap.IO.Protocol.Channel.ChannelCacheManager;
import me.Fupery.ArtMap.IO.Protocol.ProtocolHandler;
import me.Fupery.ArtMap.Listeners.EventManager;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Painting.ArtistHandler;
import me.Fupery.ArtMap.Recipe.RecipeLoader;
import me.Fupery.ArtMap.Utils.Preview;
import me.Fupery.ArtMap.Utils.TaskManager;
import me.Fupery.ArtMap.Utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

public class ArtMap extends JavaPlugin {

    private static SoftReference<ArtMap> pluginInstance = null;
    private MenuHandler menuHandler;
    private ArtistHandler artistHandler;
    private ConcurrentHashMap<Player, Preview> previewing; //todo why is this here?
    private VersionHandler bukkitVersion;
    private TaskManager taskManager;
    private ArtDatabase artDatabase;
    private ChannelCacheManager cacheManager;
    private MapManager mapManager;
    private RecipeLoader recipeLoader;
    private CompatibilityManager compatManager;
    private ProtocolHandler protocolHandler;
    private PixelTableManager pixelTable;
    private Configuration config;
    private EventManager eventManager;
    private Palette palette;

    public static ArtDatabase getArtDatabase() {
        return instance().artDatabase;
    }

    public static ArtMap instance() {
        if (pluginInstance == null || pluginInstance.get() == null) {
            pluginInstance = new SoftReference<>((ArtMap) Bukkit.getPluginManager().getPlugin("ArtMap"));
        }
        return pluginInstance.get();
    }

    public static TaskManager getTaskManager() {
        return instance().taskManager;
    }

    public static ArtistHandler getArtistHandler() {
        return instance().artistHandler;
    }

    public static ConcurrentHashMap<Player, Preview> getPreviewing() {
        return instance().previewing;
    }

    public static VersionHandler getBukkitVersion() {
        return instance().bukkitVersion;
    }

    public static ChannelCacheManager getCacheManager() {
        return instance().cacheManager;
    }

    public static RecipeLoader getRecipeLoader() {
        return instance().recipeLoader;
    }

    public static CompatibilityManager getCompatManager() {
        return instance().compatManager;
    }

    public static MenuHandler getMenuHandler() {
        return instance().menuHandler;
    }

    public static MapManager getMapManager() {
        return instance().mapManager;
    }

    public static Configuration getConfiguration() {
        return instance().config;
    }

    public static ProtocolHandler getProtocolManager() {
        return instance().protocolHandler;
    }

    public static Palette getColourPalette() {
        return instance().palette;
    }

    public void setColourPalette(Palette palette) {
        this.palette = palette;
    }

    public static PixelTableManager getPixelTable() {
        return instance().pixelTable;
    }

    @Override
    public void onEnable() {
        pluginInstance = new SoftReference<>(this);
        saveDefaultConfig();
        compatManager = new CompatibilityManager(this);
        config = new Configuration(this, compatManager);
        palette = new BasicPalette();
        taskManager = new TaskManager(this);
        mapManager = new MapManager(this);
        protocolHandler = new ProtocolHandler();
        artistHandler = new ArtistHandler();
        bukkitVersion = new VersionHandler();
        cacheManager = new ChannelCacheManager();
        previewing = new ConcurrentHashMap<>();
        artDatabase = ArtDatabase.buildDatabase(this);
        eventManager = new EventManager(this, bukkitVersion);
        new FlatDatabaseConverter(this).convertDatabase();
        Lang.load(this, config);
        if ((pixelTable = PixelTableManager.buildTables(this)) == null) {
            getLogger().warning(Lang.INVALID_DATA_TABLES.get());
            getPluginLoader().disablePlugin(this);
            return;
        }
        getCommand("artmap").setExecutor(new CommandHandler());
        recipeLoader = new RecipeLoader(this, config);
        recipeLoader.loadRecipes();
        menuHandler = new MenuHandler(this);
    }

    @Override
    public void onDisable() {
        artistHandler.stop();
        menuHandler.closeAll();
        eventManager.unregisterAll();
        mapManager.saveKeys();
        if (previewing.size() > 0) previewing.keySet().forEach(Preview::stop);
        recipeLoader.unloadRecipes();
        reloadConfig();
        pluginInstance = null;
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }
}
package me.Fupery.ArtMap;

import me.Fupery.ArtMap.Colour.BasicPalette;
import me.Fupery.ArtMap.Colour.Palette;
import me.Fupery.ArtMap.Command.CommandHandler;
import me.Fupery.ArtMap.Compatability.CompatibilityManager;
import me.Fupery.ArtMap.Config.Configuration;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.Database.Database;
import me.Fupery.ArtMap.IO.Legacy.FlatDatabaseConverter;
import me.Fupery.ArtMap.IO.Legacy.OldDatabaseConverter;
import me.Fupery.ArtMap.IO.PixelTableManager;
import me.Fupery.ArtMap.IO.Protocol.Channel.ChannelCacheManager;
import me.Fupery.ArtMap.IO.Protocol.ProtocolHandler;
import me.Fupery.ArtMap.Listeners.EventManager;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Painting.ArtistHandler;
import me.Fupery.ArtMap.Preview.PreviewManager;
import me.Fupery.ArtMap.Recipe.RecipeLoader;
import me.Fupery.ArtMap.Utils.TaskManager;
import me.Fupery.ArtMap.Utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.lang.ref.SoftReference;

public class ArtMap extends JavaPlugin {

    private static SoftReference<ArtMap> pluginInstance = null;
    private MenuHandler menuHandler;
    private ArtistHandler artistHandler;
    private VersionHandler bukkitVersion;
    private TaskManager taskManager;
    private Database database;
    private ChannelCacheManager cacheManager;
    private RecipeLoader recipeLoader;
    private CompatibilityManager compatManager;
    private ProtocolHandler protocolHandler;
    private PixelTableManager pixelTable;
    private Configuration config;
    private EventManager eventManager;
    private PreviewManager previewManager;
    private Palette palette;

    public static Database getArtDatabase() {
        return instance().database;
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

    public static PreviewManager getPreviewManager() {
        return instance().previewManager;
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
        protocolHandler = new ProtocolHandler();
        artistHandler = new ArtistHandler();
        bukkitVersion = new VersionHandler();
        cacheManager = new ChannelCacheManager();
        if ((database = Database.build(this)) == null) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        eventManager = new EventManager(this, bukkitVersion);
        new FlatDatabaseConverter(this).convertDatabase();
        new OldDatabaseConverter(this).convertDatabase();
        Lang.load(this, config);
        if ((pixelTable = PixelTableManager.buildTables(this)) == null) {
            getLogger().warning(Lang.INVALID_DATA_TABLES.get());
            getPluginLoader().disablePlugin(this);
            return;
        }
        recipeLoader = new RecipeLoader(this, config);
        recipeLoader.loadRecipes();
        previewManager = new PreviewManager();
        menuHandler = new MenuHandler(this);
        getCommand("artmap").setExecutor(new CommandHandler());
    }

    @Override
    public void onDisable() {
        previewManager.endAllPreviews();
        artistHandler.stop();
        menuHandler.closeAll();
        eventManager.unregisterAll();
        database.close();
        recipeLoader.unloadRecipes();
        reloadConfig();
        pluginInstance = null;
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }
}
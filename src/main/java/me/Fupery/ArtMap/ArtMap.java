package me.Fupery.ArtMap;

import me.Fupery.ArtMap.Command.ArtMapCommandExecutor;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.InventoryMenu.HelpMenu.ArtistMenu;
import me.Fupery.ArtMap.InventoryMenu.HelpMenu.ArtworkMenu;
import me.Fupery.ArtMap.InventoryMenu.HelpMenu.HelpMenu;
import me.Fupery.ArtMap.InventoryMenu.HelpMenu.RecipeMenu;
import me.Fupery.ArtMap.InventoryMenu.InventoryMenu;
import me.Fupery.ArtMap.Listeners.*;
import me.Fupery.ArtMap.NMS.InvalidVersion;
import me.Fupery.ArtMap.NMS.NMSInterface;
import me.Fupery.ArtMap.NMS.VersionHandler;
import me.Fupery.ArtMap.Protocol.ArtistHandler;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Preview;
import me.Fupery.DataTables.DataTables;
import me.Fupery.DataTables.PixelTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ArtMap extends JavaPlugin {

    private File mapList;
    private FileConfiguration maps;
    private List<String> titleFilter;
    private ConcurrentHashMap<Location, Easel> easels;
    private ConcurrentHashMap<Player, Preview> previewing;
    private ConcurrentHashMap<Player, InventoryMenu> openMenus;
    private ArtistHandler artistHandler;
    private int mapResolutionFactor;
    private PixelTable pixelTable;
    private NMSInterface nmsInterface;

    @Override
    public void onEnable() {

        if (loadPluginData()) {
            maps = YamlConfiguration.loadConfiguration(mapList);

            if (maps.getConfigurationSection(MapArt.artworks) == null) {
                maps.createSection(MapArt.artworks);
                updateMaps();
            }
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

        nmsInterface = new VersionHandler(this).getNMSInterface();

        if (nmsInterface instanceof InvalidVersion) {
            String version = ((InvalidVersion) nmsInterface).getVersion();
            getLogger().warning(String.format(
                    Lang.INVALID_VERSION.rawMessage(), version));
            getPluginLoader().disablePlugin(this);
            return;
        }

        easels = new ConcurrentHashMap<>();
        previewing = new ConcurrentHashMap<>();
        openMenus = new ConcurrentHashMap<>();

        ArtMaterial.setupRecipes();

        this.getCommand("artmap").setExecutor(new ArtMapCommandExecutor(this));

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ArtCraftListener(this), this);
        manager.registerEvents(new PlayerInteractListener(this), this);
        manager.registerEvents(new PlayerInteractEaselListener(this), this);
        manager.registerEvents(new PlayerQuitListener(this), this);
        manager.registerEvents(new ChunkUnloadListener(this), this);
        manager.registerEvents(new PlayerCraftListener(this), this);
        manager.registerEvents(new InventoryInteractListener(this), this);
        manager.registerEvents(new EaselInteractListener(this), this);
        manager.registerEvents(new MenuListener(this), this);
    }

    @Override
    public void onDisable() {

        if (artistHandler != null) {
            artistHandler.clearPlayers();
        }

        if (previewing != null && previewing.size() > 0) {

            for (Player player : previewing.keySet()) {
                Preview.stop(this, player);
            }
        }
    }

    private boolean loadPluginData() {

        saveDefaultConfig();

        mapResolutionFactor = getConfig().getInt("mapResolutionFactor");

        mapList = new File(getDataFolder(), "mapList.yml");

        FileConfiguration filter =
                YamlConfiguration.loadConfiguration(getTextResource("titleFilter.yml"));

        titleFilter = filter.getStringList("blacklisted");

        try {

            if (!mapList.exists()) {

                if (!mapList.createNewFile()) {
                    return false;
                }
            }

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public FileConfiguration getMaps() {
        return maps;
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

    public void updateMaps() {
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

            @Override
            public void run() {

                try {
                    maps.save(mapList);
                    maps = YamlConfiguration.loadConfiguration(mapList);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getMapResolutionFactor() {
        return mapResolutionFactor;
    }

    public boolean isPreviewing(Player player) {
        return previewing.containsKey(player);
    }

    public ArtistHandler getArtistHandler() {
        return artistHandler;
    }

    public void setArtistHandler(ArtistHandler artistHandler) {
        this.artistHandler = artistHandler;
    }

    public boolean hasOpenMenu(Player player) {
        return openMenus.containsKey(player);
    }

    public InventoryMenu getMenu(Player player) {
        return openMenus.get(player);
    }

    public void addMenu(Player player, InventoryMenu menu) {
        openMenus.put(player, menu);
    }

    public void removeMenu(Player player) {
        openMenus.remove(player);
    }

    public ConcurrentHashMap<Location, Easel> getEasels() {
        return easels;
    }

    public List<String> getTitleFilter() {
        return titleFilter;
    }

    public PixelTable getPixelTable() {
        return pixelTable;
    }

    public NMSInterface getNmsInterface() {
        return nmsInterface;
    }

    public ConcurrentHashMap<Player, Preview> getPreviewing() {
        return previewing;
    }

    public enum Lang {
        HELP(false), NO_CONSOLE(true), INVALID_POS(true), NO_PERM(true), ELSE_USING(true),
        SAVE_USAGE(false), NOT_RIDING_EASEL(true), SAVE_SUCCESS(false), EASEL_HELP(false),
        NEED_CANVAS(true), NOT_A_CANVAS(true), NOT_YOUR_EASEL(true), NEED_TO_COPY(true),
        BREAK_CANVAS(false), PAINTING(false), DELETED(false), MAP_NOT_FOUND(true),
        NO_CRAFT_PERM(true), BAD_TITLE(true), TITLE_USED(true), PREVIEWING(false),
        UNKNOWN_ERROR(true), EMPTY_HAND_PREVIEW(true), INVALID_VERSION(true),
        INVALID_RESOLUTION(true), INVALID_DATA_TABLES(true), RECIPE_HEADER(false);

        public static final String prefix = "§b[ArtMap] ";
        boolean isErrorMessage;
        String message;

        Lang(boolean isErrorMessage) {
            this.isErrorMessage = isErrorMessage;
            ArtMap plugin = getPlugin(ArtMap.class);
            String language = plugin.getConfig().getString("language");
            FileConfiguration langFile =
                    YamlConfiguration.loadConfiguration(plugin.getTextResource("lang.yml"));

            if (!langFile.contains(language)) {
                language = "english";
            }
            ConfigurationSection lang = langFile.getConfigurationSection(language);

            if (lang.get(name()) != null) {
                message = lang.getString(name());

            } else {
                Bukkit.getLogger().warning(String.format("%sError loading %s from lang.yml", prefix, name()));
            }
        }

        public String message() {
            ChatColor colour = (isErrorMessage) ? ChatColor.RED : ChatColor.GOLD;
            return prefix + colour + message;
        }

        public String rawMessage() {
            return message;
        }
        public enum Array {
            HELP_GETTING_STARTED, HELP_RECIPES, HELP_COMMANDS, HELP_LIST, HELP_CLOSE;

            String[] messages;

            Array() {
                ArtMap plugin = getPlugin(ArtMap.class);
                String language = plugin.getConfig().getString("language");
                FileConfiguration langFile =
                        YamlConfiguration.loadConfiguration(plugin.getTextResource("lang.yml"));

                if (!langFile.contains(language)) {
                    language = "english";
                }
                ConfigurationSection lang = langFile.getConfigurationSection(language);

                if (lang.get(name()) != null) {
                    List<String> strings = lang.getStringList(name());
                    messages = strings.toArray(new String[strings.size()]);

                } else {
                    Bukkit.getLogger().warning(String.format("Error loading %s from lang.yml", name()));
                }
            }

            public String[] messages() {
                return messages;
            }
        }
    }
}
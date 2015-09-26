package me.Fupery.Artiste;

import me.Fupery.Artiste.Artist.ArtistHandler;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.Easel.Recipe;
import me.Fupery.Artiste.IO.WorldMap;
import me.Fupery.Artiste.Listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Artiste extends JavaPlugin {

    public static String entityTag = "Easel";

    private File mapList;
    private FileConfiguration maps;
    private List<String> titleFilter;
    private int backgroundID;
    private ConcurrentHashMap<Player, String> nameQueue;
    private ConcurrentHashMap<Location, Easel> easels;
    private ArtistHandler artistHandler;

    @Override
    public void onEnable() {

        Recipe.setupRecipes(this);

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerInteractListener(this), this);
        manager.registerEvents(new PlayerInteractEaselListener(this), this);
        manager.registerEvents(new CanvasListener(this), this);
        manager.registerEvents(new PlayerQuitListener(this), this);
        manager.registerEvents(new ChunkUnloadListener(this), this);
        manager.registerEvents(new PlayerCraftListener(this), this);

        this.getCommand("artmap").setExecutor(new Commands(this));

        easels = new ConcurrentHashMap<>();

        if (setupRegistry()) {
            maps = YamlConfiguration.loadConfiguration(mapList);

            if (maps.getConfigurationSection("artworks") == null) {
                maps.createSection("artworks");
                updateMaps();
            }
        }

        nameQueue = new ConcurrentHashMap<>();

        backgroundID = getConfig().getInt("backgroundID");
    }

    @Override
    public void onDisable() {

        if (artistHandler != null) {
            artistHandler.getProtocol().close();
        }
        FileConfiguration config = getConfig();

        if (backgroundID != config.getInt("backgroundID")) {
            config.set("backgroundID", backgroundID);
            saveDefaultConfig();
        }
    }

    private boolean setupRegistry() {

        saveDefaultConfig();

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

    public ArtistHandler getArtistHandler() {
        return artistHandler;
    }

    public void setArtistHandler(ArtistHandler artistHandler) {
        this.artistHandler = artistHandler;
    }

    public ConcurrentHashMap<Player, String> getNameQueue() {
        return nameQueue;
    }

    public ConcurrentHashMap<Location, Easel> getEasels() {
        return easels;
    }

    public List<String> getTitleFilter() {
        return titleFilter;
    }

    public int getBackgroundID() {
        return backgroundID;
    }

    private void setBackgroundID(int id) {
        backgroundID = id;
        getConfig().set("backgroundID", id);
        saveDefaultConfig();
    }

    public void setupBackgroundID(World world) {
        MapView mapView = Bukkit.createMap(world);
        WorldMap map = new WorldMap(mapView);
        map.setBlankMap();
        setBackgroundID(mapView.getId());
    }
}
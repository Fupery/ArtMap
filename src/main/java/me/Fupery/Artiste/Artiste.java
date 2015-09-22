package me.Fupery.Artiste;

import me.Fupery.Artiste.Artist.ArtistHandler;
import me.Fupery.Artiste.Easel.Recipe;
import me.Fupery.Artiste.IO.WorldMap;
import me.Fupery.Artiste.Listeners.CanvasListener;
import me.Fupery.Artiste.Listeners.PlayerInteractEaselListener;
import me.Fupery.Artiste.Listeners.PlayerInteractListener;
import me.Fupery.Artiste.Listeners.PlayerQuitListener;
import me.Fupery.Artiste.Utils.TrigTable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Artiste extends JavaPlugin {

    public static String entityTag = "Easel";

    private File mapList;
    private FileConfiguration maps;
    private TrigTable trigTable;
    private int backgroundID;
    private ConcurrentHashMap<Player, String> nameQueue;
    private ArtistHandler artistHandler;

    @Override
    public void onEnable() {

        Recipe.addCanvas();
        Recipe.addEasel();
        Recipe.addBucket();

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerInteractListener(this), this);
        manager.registerEvents(new PlayerInteractEaselListener(this), this);
        manager.registerEvents(new CanvasListener(this), this);
        manager.registerEvents(new PlayerQuitListener(this), this);

        this.getCommand("artmap").setExecutor(new Commands(this));

        if (setupRegistry()) {
            maps = YamlConfiguration.loadConfiguration(mapList);
        }

//        trigTable = new TrigTable(40, ((float) .6155), ((short) 4));

        nameQueue = new ConcurrentHashMap<>();

        backgroundID = getConfig().getInt("backgroundID");
    }

    @Override
    public void onDisable() {
        FileConfiguration config = getConfig();

        if (backgroundID != config.getInt("backgroundID")) {
            config.set("backgroundID", backgroundID);
            saveDefaultConfig();
        }
    }

    private boolean setupRegistry() {

        saveDefaultConfig();

        mapList = new File(getDataFolder(), "mapList.yml");

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

    public TrigTable getTrigTable() {
        return trigTable;
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
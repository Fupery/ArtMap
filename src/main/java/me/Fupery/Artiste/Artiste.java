package me.Fupery.Artiste;

import me.Fupery.Artiste.Artist.ArtistPipeline;
import me.Fupery.Artiste.Utils.TrigTable;
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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Artiste extends JavaPlugin {

    public static String entityTag = "Easel";

    private File mapList;
    private File easelList;
    private FileConfiguration maps;
    private TrigTable trigTable;
    private int backgroundID;
    private ConcurrentHashMap<Location, Boolean> easels;
    private ConcurrentHashMap<Player, ArtistPipeline> activePipelines;
    private ConcurrentHashMap<Player, String> nameQueue;

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
        loadEasels();

        activePipelines = new ConcurrentHashMap<>();
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
        easelList = new File(getDataFolder(), "easels.dat");

        if (!mapList.exists()) {

            if (!mapList.mkdir()) {
                return false;
            }
        }
        if (!easelList.exists()) {

            if (!easelList.mkdir()) {
                return false;
            }
        }
        return true;
    }

    public void saveEasels() {

        try {
            if (!easelList.exists()) {
                easelList.createNewFile();
            }
            ObjectOutputStream out = new ObjectOutputStream(
                    new GZIPOutputStream(new FileOutputStream(easelList)));
            out.writeObject(easels);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean loadEasels() {
        ObjectInputStream in;

        try {

            if (easelList.exists()) {
                in = new ObjectInputStream(new GZIPInputStream(
                        new FileInputStream(easelList)));
                easels = (ConcurrentHashMap) in.readObject();
                in.close();
                return true;

            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    public FileConfiguration getMaps() {
        return maps;
    }

    public TrigTable getTrigTable() {
        return trigTable;
    }

    public ConcurrentHashMap<Location, Boolean> getEasels() {
        return easels;
    }

    public ConcurrentHashMap<Player, ArtistPipeline> getActivePipelines() {
        return activePipelines;
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
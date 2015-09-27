package me.Fupery.Artiste;

import me.Fupery.Artiste.Artist.ArtistHandler;
import me.Fupery.Artiste.Command.CommandListener;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.Easel.Recipe;
import me.Fupery.Artiste.IO.MapArt;
import me.Fupery.Artiste.IO.WorldMap;
import me.Fupery.Artiste.Listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
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
    private ConcurrentHashMap<Player, MapPreview> previewing;
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
        manager.registerEvents(new InventoryInteractListener(this), this);

        this.getCommand("artmap").setExecutor(new CommandListener(this));

        easels = new ConcurrentHashMap<>();
        previewing = new ConcurrentHashMap<>();

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

        if (previewing != null && previewing.size() > 0) {

            for (Player player : previewing.keySet()) {
                stopPreviewing(player);
            }
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

    public void startPreviewing(Player player, MapArt art) {

        if (previewing.containsKey(player)) {
            stopPreviewing(player);
        }
        ItemStack item = art.getMapItem();
        MapPreview preview = new MapPreview(this, player);
        preview.runTaskLaterAsynchronously(this, 300);
        player.setItemInHand(item);
        previewing.put(player, preview);
    }

    public void stopPreviewing(Player player) {

        if (previewing.containsKey(player)) {
            player.setItemInHand(new ItemStack(Material.AIR));
            previewing.get(player).cancel();
            previewing.remove(player);
        }
    }

    public boolean isPreviewing(Player player) {
        return previewing.containsKey(player);
    }

    private class MapPreview extends BukkitRunnable {
        Artiste plugin;
        Player player;

        MapPreview(Artiste plugin, Player player) {
            this.plugin = plugin;
            this.player = player;
        }

        public void run() {
            stopPreviewing(player);
        }
    }
}
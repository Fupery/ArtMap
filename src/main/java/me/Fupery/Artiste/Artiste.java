package me.Fupery.Artiste;

import me.Fupery.Artiste.Listeners.PlayerInteractEaselListener;
import me.Fupery.Artiste.Listeners.PlayerInteractListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Artiste extends JavaPlugin {

    public static String entityTag = "Easel";

    private File idList;

    @Override
    public void onEnable() {
        Recipe.addCanvas();
        Recipe.addEasel();
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerInteractListener(this), this);
        manager.registerEvents(new PlayerInteractEaselListener(this), this);

        setupRegistry();
    }

    @Override
    public void onDisable() {

    }

    private boolean setupRegistry() {

        saveDefaultConfig();
        idList = new File(getDataFolder(), "IDList.yml");

        if (!idList.exists()) {

            if (!idList.mkdir()) {
                return false;
            }
        }
        return true;
    }

    public FileConfiguration getIDList() {
        return YamlConfiguration.loadConfiguration(idList);
    }
}

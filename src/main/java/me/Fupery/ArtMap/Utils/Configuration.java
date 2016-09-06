package me.Fupery.ArtMap.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {
    public final String LANGUAGE;
    public final boolean SWEAR_FILTER;
    public final boolean DISABLE_ACTION_BAR;
    public final boolean CUSTOM_LANG;
    public final boolean CUSTOM_RECIPES;
    public final boolean FORCE_ART_KIT;
    public final boolean HIDE_PREFIX;
    public final boolean LAZY_MAP_ID;

    public Configuration(JavaPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        this.LANGUAGE = configuration.getString("language");
        this.SWEAR_FILTER = configuration.getBoolean("swearFilter");
        this.DISABLE_ACTION_BAR = configuration.getBoolean("disableActionBar");
        this.CUSTOM_LANG = configuration.getBoolean("customLang");
        this.CUSTOM_RECIPES = configuration.getBoolean("customRecipes");
        this.FORCE_ART_KIT = configuration.getBoolean("forceArtKit");
        this.HIDE_PREFIX = configuration.getBoolean("hidePrefix");
        this.LAZY_MAP_ID = configuration.getBoolean("lazyMapId");
    }
}

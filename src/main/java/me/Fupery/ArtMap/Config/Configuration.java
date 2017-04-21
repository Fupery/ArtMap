package me.Fupery.ArtMap.Config;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Compatability.CompatibilityManager;
import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {
    public final String LANGUAGE;
    public final String WORLD;
    public final int ARTWORK_AUTO_SAVE;
    public final boolean SWEAR_FILTER;
    public final boolean DISABLE_ACTION_BAR;
    public final boolean CUSTOM_RECIPES;
    public final boolean FORCE_ART_KIT;
    public final boolean HIDE_PREFIX;

    public Configuration(ArtMap plugin, CompatibilityManager manager) {
        FileConfiguration configuration = plugin.getConfig();
        this.LANGUAGE = configuration.getString("language");
        String world = configuration.getString("world");
        this.WORLD = world != null ? world : "world";
        int autoSave = configuration.getInt("artworkAutoSave");
        this.ARTWORK_AUTO_SAVE = autoSave >= 30 ? autoSave : 300;
        this.SWEAR_FILTER = configuration.getBoolean("swearFilter");
        this.DISABLE_ACTION_BAR = configuration.getBoolean("disableActionBar");
        this.CUSTOM_RECIPES = configuration.getBoolean("customRecipes");
        this.FORCE_ART_KIT = configuration.getBoolean("forceArtKit");
        this.HIDE_PREFIX = configuration.getBoolean("hidePrefix");
    }
}

package me.Fupery.ArtMap.Compatability;

import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityManager implements RegionHandler {
    private List<RegionHandler> regionHandlers;
    private ReflectionHandler reflectionHandler;

    public CompatibilityManager(JavaPlugin plugin) {
        regionHandlers = new ArrayList<>();
        loadRegionHandler(WorldGuardCompat.class);
        loadRegionHandler(FactionsCompat.class);
        loadRegionHandler(GriefPreventionCompat.class);
        loadRegionHandler(RedProtectCompat.class);
        loadRegionHandler(LandlordCompat.class);
        reflectionHandler = loadReflectionHandler();
        if (!(reflectionHandler instanceof VanillaReflectionHandler))
            plugin.getLogger().info(String.format("%s reflection handler enabled.",
                    reflectionHandler.getClass().getSimpleName().replace("Compat", "")));
        for (RegionHandler regionHandler : regionHandlers) {
            plugin.getLogger().info(String.format("%s hooks enabled.",
                    regionHandler.getClass().getSimpleName().replace("Compat", "")));
        }
    }

    public boolean isPluginLoaded(String pluginName) {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        if (player.hasPermission("artmap.admin")) return true; //admins can override
        for (RegionHandler regionHandler : regionHandlers) {
            if (!regionHandler.checkBuildAllowed(player, location)) return false;
        }
        return true;
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        if (checkBuildAllowed(player, entity.getLocation())) return true; //builders can override
        for (RegionHandler regionHandler : regionHandlers) {
            if (!regionHandler.checkInteractAllowed(player, entity, click)) return false;
        }
        return true;
    }

    public ReflectionHandler getReflectionHandler() {
        return reflectionHandler;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    private ReflectionHandler loadReflectionHandler() {
        ReflectionHandler denizenHandler = new DenizenCompat();
        if (denizenHandler.isLoaded()) return denizenHandler;
        ReflectionHandler iDisguiseHandler = new iDisguiseCompat();
        if (iDisguiseHandler.isLoaded()) return iDisguiseHandler;
        return new VanillaReflectionHandler();
    }

    private void loadRegionHandler(Class<? extends RegionHandler> handlerClass) {
        try {
            RegionHandler handler = handlerClass.newInstance();
            if (handler.isLoaded()) regionHandlers.add(handler);
        } catch (Exception | NoClassDefFoundError e) {
            return;
        }
    }

    @Override
    public String toString() {
        String string = "Plugin compatability hooks: ";
        for (RegionHandler regionHandler : regionHandlers) {
            string += regionHandler.getClass().getSimpleName() + " [LOADED:" + regionHandler.isLoaded() + "], ";
        }
        string += "Reflection Handler: " + reflectionHandler.getClass();
        return string;
    }
}

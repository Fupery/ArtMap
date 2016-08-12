package me.Fupery.ArtMap.Compatability;

import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityManager implements RegionHandler {
    private List<RegionHandler> regionHandlers;
    private ReflectionHandler reflectionHandler;

    public CompatibilityManager() {
        regionHandlers = new ArrayList<>();
        loadRegionHandler(WGCompat.class);
        loadRegionHandler(FactionsCompat.class);
        loadRegionHandler(GriefPreventionCompat.class);
        loadRegionHandler(RedProtectCompat.class);
        reflectionHandler = loadReflectionHandler();
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
        ReflectionHandler handler;
        try {
            handler = new DenizenCompat();
            if (!handler.isLoaded()) handler = new VanillaReflectionHandler();
        } catch (Exception | NoClassDefFoundError e) {
            handler = new VanillaReflectionHandler();
        }
        return handler;
    }

    private void loadRegionHandler(Class<? extends RegionHandler> handlerClass) {
        try {
            RegionHandler handler = handlerClass.newInstance();
            if (handler.isLoaded()) regionHandlers.add(handler);
        } catch (Exception | NoClassDefFoundError e) {
            //fail silently
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

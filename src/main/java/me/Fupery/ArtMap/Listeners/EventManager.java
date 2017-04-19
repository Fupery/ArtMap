package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.Utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class EventManager {
    private final Set<RegisteredListener> listeners;

    public EventManager(JavaPlugin plugin, VersionHandler version) {
        listeners = new HashSet<>();
        listeners.add(new PlayerInteractListener());
        listeners.add(new PlayerInteractEaselListener());
        listeners.add(new PlayerQuitListener());
        listeners.add(new ChunkUnloadListener());
        listeners.add(new PlayerCraftListener());
        listeners.add(new InventoryInteractListener());
//        listeners.add(new MapInitializeListener());
        if (version.getVersion() != VersionHandler.BukkitVersion.v1_8) {
            listeners.add(new PlayerSwapHandListener());
            listeners.add(new PlayerDismountListener());
        }
        PluginManager manager = Bukkit.getServer().getPluginManager();
        for (RegisteredListener listener : listeners) manager.registerEvents(listener, plugin);
    }

    public void unregisterAll() {
        listeners.forEach(RegisteredListener::unregister);
    }
}

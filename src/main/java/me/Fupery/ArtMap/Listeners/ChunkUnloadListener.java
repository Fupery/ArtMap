package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {

    private final ArtMap plugin;

    public ChunkUnloadListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {

        if (EaselInteractListener.easels.size() > 0) {
            Bukkit.getScheduler().runTask(plugin, new Runnable() {

                @Override
                public void run() {

                    for (Location location : EaselInteractListener.easels.keySet()) {

                        if (location.getChunk().equals(event.getChunk())) {
                            EaselInteractListener.easels.remove(location);
                        }
                    }
                }
            });
        }
    }
}

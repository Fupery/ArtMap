package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {

    private Artiste plugin;

    public ChunkUnloadListener(Artiste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {

        if (plugin.getEasels().size() > 0) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    Location location = null;

                    for (Location l : plugin.getEasels().keySet()) {

                        if (l.getChunk().equals(event.getChunk())) {
                            location = l;
                        }
                    }

                    if (location != null) {
                        plugin.getEasels().remove(location);
                    }
                }
            });
        }
    }
}

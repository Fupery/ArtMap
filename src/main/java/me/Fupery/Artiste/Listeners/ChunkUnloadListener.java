package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
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
    public void onChunkUnload(ChunkUnloadEvent event) {

        if (plugin.getActiveEasels() != null && plugin.getActiveEasels().size() > 0) {
            Object[] easels = plugin.getActiveEasels().keySet().toArray();

            for (int i = 0; i < easels.length; i++) {

                if (((Location) easels[i]).getChunk().equals(event.getChunk())) {
                    plugin.getActiveEasels().remove(((Location) easels[i]));
                }
            }
        }
    }
}

package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {

        if (EaselInteractListener.easels.size() > 0) {
            ArtMap.getTaskManager().SYNC.run(() -> {

                for (Location location : EaselInteractListener.easels.keySet()) {

                    if (location.getChunk().equals(event.getChunk())) {
                        EaselInteractListener.easels.remove(location);
                    }
                }
            });
        }
    }
}

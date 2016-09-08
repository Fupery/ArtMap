package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {

        if (EaselEvent.easels.size() > 0) {
            ArtMap.getTaskManager().SYNC.run(() -> {

                for (Location location : EaselEvent.easels.keySet()) {

                    if (location.getChunk().equals(event.getChunk())) {
                        EaselEvent.easels.remove(location);
                    }
                }
            });
        }
    }
}

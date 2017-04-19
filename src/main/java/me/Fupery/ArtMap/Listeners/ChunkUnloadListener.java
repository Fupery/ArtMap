package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.EaselMap;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

class ChunkUnloadListener implements RegisteredListener {

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {
        EaselMap easels = ArtMap.getEasels();

        if (!easels.isEmpty()) {
            ArtMap.getScheduler().SYNC.run(() -> {

                for (Location location : easels.keySet()) {

                    if (location.getChunk().equals(event.getChunk())) {
                        easels.remove(location);
                    }
                }
            });
        }
    }

    @Override
    public void unregister() {
        ChunkUnloadEvent.getHandlerList().unregister(this);
    }
}

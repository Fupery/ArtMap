package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.MapInitializeEvent;

public class MapInitializeListener implements RegisteredListener {

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        if (!ArtMap.getArtDatabase().getArtTable().containsMapID(event.getMap().getId())) return;
        //todo check the stuff
    }


    @Override
    public void unregister() {
        MapInitializeEvent.getHandlerList().unregister(this);
    }
}

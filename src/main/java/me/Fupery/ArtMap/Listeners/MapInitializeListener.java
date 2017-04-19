package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Database.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;

public class MapInitializeListener implements RegisteredListener {

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        short mapId = event.getMap().getId();
        Bukkit.getLogger().info("Map initialize: " + mapId);//TODO remove logging
        ArtMap.getScheduler().ASYNC.run(() -> {
            if (!ArtMap.getArtDatabase().getMapTable().containsMap(mapId)) return;
            Bukkit.getLogger().info("Contains map!");//TODO remove logging

            ArtMap.getScheduler().SYNC.run(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Bukkit.getLogger().info("   Player online: " + player.getDisplayName());//TODO remove logging

                    ItemStack item = player.getItemInHand();
                    if (item.getType() == Material.MAP && mapId == event.getMap().getId()) {
                        Bukkit.getLogger().info("   ItemMatches!");//TODO remove logging

                        item.setDurability(Bukkit.createMap(player.getWorld()).getId());
                        player.setItemInHand(item);
                    }
                }
            });
            Map map = new Map(mapId);
            ArtMap.getArtDatabase().restoreMap(map);
        });
    }


    @Override
    public void unregister() {
        MapInitializeEvent.getHandlerList().unregister(this);
    }
}

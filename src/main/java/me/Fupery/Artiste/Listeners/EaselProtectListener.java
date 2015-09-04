package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import static me.Fupery.Artiste.Easel.getEasel;

public class EaselProtectListener implements Listener {

    Artiste plugin;

    public EaselProtectListener(Artiste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (getEasel(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {

        if (getEasel(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }
}

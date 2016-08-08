package me.Fupery.ArtMap.Compatability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CompatibilityManager implements InteractPermissionHandler {
    private InteractPermissionHandler[] interactHandlers;

    public CompatibilityManager() {
        interactHandlers = new InteractPermissionHandler[]{
                new WGCompat()
        };
    }

    @Override
    public boolean checkActionAllowed(Player player, Location location, InteractAction action) {
        for (InteractPermissionHandler interactHandler : interactHandlers) {
            if (!interactHandler.checkActionAllowed(player, location, action)) return false;
        }
        return true;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }
}

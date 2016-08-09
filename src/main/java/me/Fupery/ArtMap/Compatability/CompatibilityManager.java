package me.Fupery.ArtMap.Compatability;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CompatibilityManager implements InteractPermissionHandler {
    private InteractPermissionHandler[] interactHandlers;

    public CompatibilityManager() {
        InteractPermissionHandler wgCompat;
        try {
            wgCompat = new WGCompat();
        } catch (Exception | NoClassDefFoundError e) {
            interactHandlers = new InteractPermissionHandler[0];
            return;
        }
        interactHandlers = new InteractPermissionHandler[]{wgCompat};
    }

    @Override
    public boolean checkActionAllowed(Player player, Location location, InteractAction action) {
        if (player.hasPermission("artmap.admin")) return true;
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

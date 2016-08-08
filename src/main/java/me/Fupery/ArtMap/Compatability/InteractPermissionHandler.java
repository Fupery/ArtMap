package me.Fupery.ArtMap.Compatability;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface InteractPermissionHandler extends CompatibilityHandler {
    boolean checkActionAllowed(Player player, Location location, InteractAction action);

    enum InteractAction {
        INTERACT, USE, BUILD
    }
}

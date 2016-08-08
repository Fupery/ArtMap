package me.Fupery.ArtMap.Compatability;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WGCompat implements InteractPermissionHandler {
    private Object worldGuardPlugin;

    WGCompat() {
        try {
            worldGuardPlugin = WGBukkit.getPlugin();
        } catch (Exception e) {
            worldGuardPlugin = null;
        }
    }

    @Override
    public boolean checkActionAllowed(Player player, Location location, InteractAction action) {
        if (!isLoaded()) return true;
        WorldGuardPlugin wg = ((WorldGuardPlugin) worldGuardPlugin);
        if (action == InteractAction.BUILD) {
            return wg.canBuild(player, location);
        }
        ApplicableRegionSet set = wg.getRegionContainer().createQuery().getApplicableRegions(location);
        LocalPlayer localPlayer = wg.wrapPlayer(player);
        if (action == InteractAction.INTERACT) {
            return set.testState(localPlayer, DefaultFlag.INTERACT);
        } else if (action == InteractAction.USE) {
            return set.testState(localPlayer, DefaultFlag.USE);
        } else {
            return true;
        }
    }

    @Override
    public boolean isLoaded() {
        return worldGuardPlugin != null;
    }
}

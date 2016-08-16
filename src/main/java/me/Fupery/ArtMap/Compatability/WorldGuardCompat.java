package me.Fupery.ArtMap.Compatability;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class WorldGuardCompat implements RegionHandler {
    private Object worldGuardPlugin;

    WorldGuardCompat() {
        this.worldGuardPlugin = WGBukkit.getPlugin();
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        WorldGuardPlugin wg = ((WorldGuardPlugin) worldGuardPlugin);
        ApplicableRegionSet set = wg.getRegionContainer().createQuery().getApplicableRegions(location);
        return wg.canBuild(player, location) && (player.hasPermission("artmap.region.member") ||
                ((set.size() > 0 && set.isOwnerOfAll(wg.wrapPlayer(player)))
                || set.testState(wg.wrapPlayer(player), DefaultFlag.BUILD)));
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        WorldGuardPlugin wg = ((WorldGuardPlugin) worldGuardPlugin);
        ApplicableRegionSet set = wg.getRegionContainer().createQuery().getApplicableRegions(entity.getLocation());
        return (set.size() > 0 && set.isMemberOfAll(wg.wrapPlayer(player)))
                || set.testState(wg.wrapPlayer(player), DefaultFlag.INTERACT);
    }

    @Override
    public boolean isLoaded() {
        return worldGuardPlugin != null;
    }
}

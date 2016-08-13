package me.Fupery.ArtMap.Compatability;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import me.Fupery.ArtMap.Easel.EaselEvent;
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

        return (wg.canBuild(player, location) && player.hasPermission("artmap.region.member"))
                || set.isOwnerOfAll(wg.wrapPlayer(player));
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        WorldGuardPlugin wg = ((WorldGuardPlugin) worldGuardPlugin);
        ApplicableRegionSet set = wg.getRegionContainer().createQuery().getApplicableRegions(entity.getLocation());
        return set.isMemberOfAll(wg.wrapPlayer(player)) || set.testState(wg.wrapPlayer(player), DefaultFlag.INTERACT);
    }

    @Override
    public boolean isLoaded() {
        return worldGuardPlugin != null;
    }
}

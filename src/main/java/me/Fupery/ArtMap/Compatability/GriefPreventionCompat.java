package me.Fupery.ArtMap.Compatability;

import me.Fupery.ArtMap.Easel.EaselEvent;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GriefPreventionCompat implements RegionHandler {

    private final boolean loaded;

    public GriefPreventionCompat() {
        GriefPrevention.instance.getName();
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        return (GriefPrevention.instance.allowBuild(player, location) == null);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(entity.getLocation(), false, null);
        return (claim.allowAccess(player) == null);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}

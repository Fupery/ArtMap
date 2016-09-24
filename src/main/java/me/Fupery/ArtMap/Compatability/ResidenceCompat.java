package me.Fupery.ArtMap.Compatability;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

class ResidenceCompat implements RegionHandler {

    private final boolean loaded;

    public ResidenceCompat() {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Residence")) {
            loaded = false;
            return;
        }
        Residence.getAPI();
        FlagPermissions.addFlag("artmap-place");
        FlagPermissions.addFlag("artmap-use");
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        ClaimedResidence residence = Residence.getResidenceManager().getByLoc(location);
        if (residence == null) return true;
        FlagPermissions perms = Residence.getPermsByLoc(location);
        return perms.playerHas(player.getName(), location.getWorld().getName(), "artmap-place", false);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        ClaimedResidence residence = Residence.getResidenceManager().getByLoc(entity.getLocation());
        if (residence == null) return true;
        FlagPermissions perms = Residence.getPermsByLoc(entity.getLocation());
        return perms.playerHas(player.getName(), entity.getWorld().getName(), "artmap-use", false);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}

package me.Fupery.ArtMap.Compatability;

import br.net.fabiozumbi12.RedProtect.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Region;
import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class RedProtectCompat implements RegionHandler {

    private final boolean loaded;

    RedProtectCompat() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("RedProtect");
        loaded = plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        Region currentRegion = RedProtectAPI.getRegion(location);
        return currentRegion == null || currentRegion.canBuild(player);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        Region currentRegion = RedProtectAPI.getRegion(entity.getLocation());
        return currentRegion == null || currentRegion.canSign(player);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}

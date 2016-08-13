package me.Fupery.ArtMap.Compatability;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landFlags.Build;
import com.jcdesimp.landlord.landFlags.UseContainers;
import com.jcdesimp.landlord.landManagement.FlagManager;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LandlordCompat implements RegionHandler {

    private final String BUILD_FLAG_KEY;
    private final String INTERACT_FLAG_KEY;
    final boolean loaded;

    public LandlordCompat() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Landlord");
        BUILD_FLAG_KEY = Build.class.getSimpleName();
        INTERACT_FLAG_KEY = UseContainers.class.getSimpleName();
        loaded = plugin.isEnabled();
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        OwnedLand land = OwnedLand.getApplicableLand(location);
        FlagManager manager = Landlord.getInstance().getFlagManager();
        if (land == null || !manager.getRegisteredFlags().containsKey(BUILD_FLAG_KEY)) {
            return true;
        }
        Landflag flag = manager.getRegisteredFlags().get(BUILD_FLAG_KEY);
        return land.hasPermTo(player, flag);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        OwnedLand land = OwnedLand.getApplicableLand(entity.getLocation());
        FlagManager manager = Landlord.getInstance().getFlagManager();
        if (land == null || !manager.getRegisteredFlags().containsKey(INTERACT_FLAG_KEY)) {
            return true;
        }
        Landflag flag = manager.getRegisteredFlags().get(INTERACT_FLAG_KEY);
        return land.hasPermTo(player, flag);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}

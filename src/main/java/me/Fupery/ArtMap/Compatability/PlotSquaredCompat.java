package me.Fupery.ArtMap.Compatability;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.flag.BooleanFlag;
import com.intellectualcrafters.plot.object.Plot;
import me.Fupery.ArtMap.Easel.EaselEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

class PlotSquaredCompat implements RegionHandler {

    private final boolean loaded;
    private final BooleanFlag place = new BooleanFlag("artmap-place");
    private final BooleanFlag use = new BooleanFlag("artmap-use");

    public PlotSquaredCompat() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) {
            loaded = false;
            return;
        }
        PlotAPI api = new PlotAPI();
        api.addFlag(place);
        api.addFlag(use);
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        PlotAPI API = new PlotAPI();
        Plot plot = API.getPlot(location);

        return plot == null
                || plot.isAdded(player.getUniqueId())
                || (!plot.isDenied(player.getUniqueId()) && plot.getFlag(place, false));
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        PlotAPI API = new PlotAPI();
        Plot plot = API.getPlot(entity.getLocation());
        return plot == null
                || plot.isAdded(player.getUniqueId())
                || (!plot.isDenied(player.getUniqueId()) && plot.getFlag(use, false));
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}

package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class CommandDelete extends AsyncCommand {

    CommandDelete() {
        super(null, "/artmap delete <title>", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);

        if (art == null) {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
            return;
        }
        if (sender instanceof Player
                && !(art.getArtistPlayer().getUniqueId().equals(((Player) sender).getUniqueId())
                || sender.hasPermission("artmap.admin"))) {
            msg.message = Lang.NO_PERM.get();
            return;
        }
        if (ArtMap.getArtDatabase().deleteArtwork(args[1])) {
            ArtMap.getTaskManager().SYNC.run(() -> {
                MapView mapView = Bukkit.getMap(art.getMapId());
                Reflection.setWorldMap(mapView, new byte[128 * 128]);
                for (MapRenderer renderer : mapView.getRenderers()) {
                    mapView.removeRenderer(renderer);
                }
            });
            msg.message = String.format(Lang.DELETED.get(), args[1]);
        } else {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
        }
    }
}

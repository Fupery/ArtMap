package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

class CommandRestore extends AsyncCommand {
    CommandRestore() {
        super("artmap.admin", "/artmap restore <title>", false);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);
        if (art == null) {
            sender.sendMessage(String.format(Lang.MAP_NOT_FOUND.get(), args[1]));
        } else {
            byte[] map = ArtMap.getArtDatabase().getMap(art.getTitle());
            ArtMap.getTaskManager().SYNC.run(() -> {
                MapView mapView = Bukkit.getMap(art.getMapId());
                if (mapView == null) {
                    mapView = Bukkit.createMap(((Player) sender).getWorld());
                    final MapView finalMapView = mapView;
                    ArtMap.getTaskManager().ASYNC.run(() -> {
                        ArtMap.getArtDatabase().updateMapID(art.updateMapId(finalMapView.getId()));
                    });
                    msg.message = Lang.MAP_ID_MISSING.get();
                }
                int id = mapView.getId();
                ArtMap.getMapManager().overrideMap(mapView, map);
                sender.sendMessage(String.format(Lang.RESTORED_SUCCESSFULY.get(), art.getTitle(), id));
            });
        }
    }
}

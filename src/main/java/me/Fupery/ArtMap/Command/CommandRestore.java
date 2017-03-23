package me.Fupery.ArtMap.Command;

import org.bukkit.command.CommandSender;

class CommandRestore extends AsyncCommand {//todo remove

    CommandRestore() {
        super("artmap.admin", "/artmap restore <title>", false);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
//        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);
//        if (art == null) {
//            sender.sendMessage(String.format(Lang.MAP_NOT_FOUND.get(), args[1]));
//        } else {
//            byte[] map = ArtMap.getArtDatabase().getMap(art.getMapId());
//            ArtMap.getTaskManager().SYNC.run(() -> {
//                MapView mapView = Bukkit.getMap(art.getMapId());
//                if (mapView == null) {
//                    mapView = Bukkit.createMap(((Player) sender).getWorld());
//                    final MapView finalMapView = mapView;
//                    ArtMap.getTaskManager().ASYNC.run(() -> {
//                        ArtMap.getArtDatabase().updateMapId(art.updateMapId(finalMapView.getId()));
//                    });
//                    msg.message = Lang.MAP_ID_MISSING.get();
//                }
//                int id = mapView.getId();
//                ArtMap.getMapManager().setMap(mapView, map);
//                sender.sendMessage(String.format(Lang.RESTORED_SUCCESSFULY.get(), art.getTitle(), id));
//            });
//        }
    }
}

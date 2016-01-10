package me.Fupery.ArtMap.Command;


import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ArtBackup;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.GenericMapRenderer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CommandRestore extends ArtMapCommand {

    CommandRestore(ArtMap plugin) {
        super("op", "/artmap restore <backupname>", true);
        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        //Check world is valid
        World world = Bukkit.getWorld(args[1]);

        if (world == null) {
            sender.sendMessage(String.format(ArtMap.Lang.NO_WORLD.message(), args[1]));
        }
        //Check if overwrite flag is provided
        boolean overwrite = (args.length > 2 && args[2].equals("-o"));

        //Check if backups folder is valid
        File backupsFolder = new File(plugin.getDataFolder(), "backups");

        if (!backupsFolder.exists() && !backupsFolder.mkdir()) {
            msg.message = String.format(ArtMap.Lang.RESTORE_ERROR.message(), backupsFolder.getName());
            return false;
        }

        File backup = new File(backupsFolder, args[0]);

        if (!backup.exists()) {
            msg.message = String.format(ArtMap.Lang.RESTORE_ERROR.message(), backup.getAbsolutePath());
            return false;
        }

        File[] files = backup.listFiles();

        if (files == null) {
            msg.message = String.format(ArtMap.Lang.RESTORE_ERROR.message(), backup.getAbsolutePath());
            return false;
        }
        ArrayList<MapArt> artworks = new ArrayList<>();
        int i = 0; //Number of succesfully restored artworks
        int k = 0; //Number of skipped artworks

        //Restore files in backup folder
        for (File file : files) {

            if (!file.getName().endsWith(".mapArt")) {
                continue;
            }
            ArtBackup backupFile;
            try {
                backupFile = ArtBackup.read(file);
            } catch (IOException | ClassNotFoundException e) {
                sender.sendMessage(String.format("Error reading %s: %s", file.getName(), e.getMessage()));
                continue;
            }
            //MapID handling
            MapView mapView = null;
            short mapID = backupFile.getMapID();

            if (!ArtMap.getArtDatabase().containsArtwork(backupFile.getMapArt(), true)) {

                if (!ArtMap.getArtDatabase().containsMapID(mapID)) {

                    MapView oldMapView = Bukkit.getMap(mapID);

                    if (oldMapView != null && ArtMap.nmsInterface.isMapArt(oldMapView)) {
                        mapView = oldMapView;
                    }
                }

            } else {

                if (!overwrite) {
                    k++;
                    continue;
                }
                mapView = Bukkit.getMap(mapID);
            }
            if (mapView == null) {
                mapView = Bukkit.createMap(world);
                backupFile.setMapID(mapView.getId());
            }
            for (MapRenderer r : mapView.getRenderers()) {
                mapView.removeRenderer(r);
            }
            mapView.addRenderer(new GenericMapRenderer(backupFile.getMap()));
            ArtMap.nmsInterface.setWorldMap(mapView, backupFile.getMap());
            artworks.add(backupFile.getMapArt());
            i++;
        }
        ArtMap.getArtDatabase().addArtworks(artworks.toArray(new MapArt[artworks.size()]));
        sender.sendMessage(String.format(ArtMap.Lang.RESTORE_SUCCESS.message(), i, backup.getAbsoluteFile()));

        if (!overwrite) {
            sender.sendMessage(String.format(ArtMap.Lang.RESTORE_ALREADY_FOUND.message(), k));
        }
        return true;
    }
}

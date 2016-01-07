package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ArtBackup;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.GenericMapRenderer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class ConsoleCommandExecutor implements CommandExecutor {

    private static final DateFormat backupFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    protected int minArgs;
    ArtMap plugin;

    public ConsoleCommandExecutor(ArtMap plugin, int minArgs) {
        this.plugin = plugin;
        this.minArgs = minArgs;
    }

    @Override
    public boolean onCommand(final CommandSender sender,
                             Command command, String s, final String[] args) {
        if (!sender.hasPermission("op")) {
            sender.sendMessage(ArtMap.Lang.NO_PERM.message());
            return true;
        }
        if (args.length < minArgs) {
            return false;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                onCommand(sender, args);
            }
        });

        return true;
    }

    protected abstract void onCommand(CommandSender sender, String[] args);

    public static class BackupExecutor extends ConsoleCommandExecutor {

        public BackupExecutor(ArtMap plugin) {
            super(plugin, 0);
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            File backupsFolder = new File(plugin.getDataFolder(), "backups");

            if (!backupsFolder.exists() && !backupsFolder.mkdir()) {
                sender.sendMessage(ArtMap.Lang.BACKUP_ERROR.message());
                return;
            }
            File backup = new File(backupsFolder, backupFormat.format(new Date()));

            if (!backup.exists() && !backup.mkdir()) {
                sender.sendMessage(ArtMap.Lang.BACKUP_ERROR.message());
                return;
            }

            MapArt[] artworks = ArtMap.getArtDatabase().listMapArt("all");

            if (artworks == null) {
                sender.sendMessage(ArtMap.Lang.NO_ARTWORKS.message());
                return;
            }

            for (MapArt art : artworks) {
                File mapBackup = new File(backup, art.getTitle() + ".mapArt");

                ArtBackup artBackup = new ArtBackup(art);
                try {
                    artBackup.write(mapBackup);
                } catch (IOException e) {
                    sender.sendMessage(String.format("Error writing %s: %s", art.getTitle(), e.getMessage()));
                    return;
                }
            }
            sender.sendMessage(String.format(ArtMap.Lang.BACKUP_SUCCESS.message(), backup.getAbsoluteFile()));
        }
    }

    public static class RestoreExecutor extends ConsoleCommandExecutor {

        public RestoreExecutor(ArtMap plugin) {
            super(plugin, 2);
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
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
                sender.sendMessage(String.format(ArtMap.Lang.RESTORE_ERROR.message(), backupsFolder.getName()));
                return;
            }

            File backup = new File(backupsFolder, args[0]);

            if (!backup.exists()) {
                sender.sendMessage(String.format(ArtMap.Lang.RESTORE_ERROR.message(), backup.getAbsolutePath()));
                return;
            }

            File[] files = backup.listFiles();

            if (files == null) {
                sender.sendMessage(String.format(ArtMap.Lang.RESTORE_ERROR.message(), backup.getAbsolutePath()));
                return;
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
                MapArt art = backupFile.getMapArt();
                short mapID = backupFile.getMapIDValue();

                if (!ArtMap.getArtDatabase().containsArtwork(art, true)) {

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
                }
                for (MapRenderer r : mapView.getRenderers()) {
                    mapView.removeRenderer(r);
                }
                mapView.addRenderer(new GenericMapRenderer(backupFile.getMap()));
                ArtMap.nmsInterface.setWorldMap(mapView, backupFile.getMap());
                artworks.add(art);
                i++;
            }
            ArtMap.getArtDatabase().addArtworks(artworks.toArray(new MapArt[artworks.size()]));
            sender.sendMessage(String.format(ArtMap.Lang.RESTORE_SUCCESS.message(), i, backup.getAbsoluteFile()));

            if (!overwrite) {
                sender.sendMessage(String.format(ArtMap.Lang.RESTORE_ALREADY_FOUND.message(), k));
            }
        }
    }
}

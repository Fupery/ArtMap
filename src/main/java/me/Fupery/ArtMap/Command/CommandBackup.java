package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ArtBackup;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandBackup extends Command {

    private static final DateFormat backupFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    CommandBackup() {
        super("op", "/artmap backup", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        File backupsFolder = new File(ArtMap.plugin().getDataFolder(), "backups");

        if (!backupsFolder.exists() && !backupsFolder.mkdir()) {
            msg.message = Lang.BACKUP_ERROR.message();
            return;
        }
        File backup = new File(backupsFolder, backupFormat.format(new Date()));

        if (!backup.exists() && !backup.mkdir()) {
            msg.message = Lang.BACKUP_ERROR.message();
            return;
        }

        MapArt[] artworks = ArtMap.getArtDatabase().listMapArt("all");

        if (artworks == null) {
            msg.message = Lang.NO_ARTWORKS.message();
            return;
        }

        for (MapArt art : artworks) {
            File mapBackup = new File(backup, art.getTitle() + ".mapArt");

            ArtBackup artBackup = new ArtBackup(art);
            try {
                artBackup.write(mapBackup);
            } catch (IOException e) {
                msg.message = String.format("Error writing %s: %s", art.getTitle(), e.getMessage());
                return;
            }
        }
        sender.sendMessage(String.format(Lang.BACKUP_SUCCESS.message(), backup.getAbsoluteFile()));
    }
}

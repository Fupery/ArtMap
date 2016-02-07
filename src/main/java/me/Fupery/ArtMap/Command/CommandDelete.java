package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDelete extends Command {

    CommandDelete() {
        super(null, "/artmap delete <title>", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);

        if (art != null && sender instanceof Player
                && !(art.getPlayer().getName().equalsIgnoreCase(sender.getName())
                || sender.hasPermission("artmap.admin"))) {
            msg.message = Lang.NO_PERM.message();
            return;
        }

        if (ArtMap.getArtDatabase().deleteArtwork(args[1])) {
            msg.message = String.format(Lang.DELETED.message(), args[1]);
            return;

        } else {
            msg.message = String.format(Lang.MAP_NOT_FOUND.message(), args[1]);
            return;
        }
    }
}

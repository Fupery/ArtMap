package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDelete extends ArtMapCommand {

    CommandDelete(ArtMap plugin) {
        super(null, "/artmap delete <title>", true);
        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);

        if (art != null && sender instanceof Player
                && !(art.getPlayer().getName().equalsIgnoreCase(sender.getName())
                || sender.hasPermission("artmap.admin"))) {
            msg.message = ArtMap.Lang.NO_PERM.message();
            return false;
        }

        if (ArtMap.getArtDatabase().deleteArtwork(args[1])) {
            msg.message = String.format(ArtMap.Lang.DELETED.message(), args[1]);
            return true;

        } else {
            msg.message = String.format(ArtMap.Lang.MAP_NOT_FOUND.message(), args[1]);
            return false;
        }
    }
}

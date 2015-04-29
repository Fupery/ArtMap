package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.Command.Error;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Delete extends MapArtCommand {

	public Delete(CommandListener listener) {
		super(listener);
		usage = "delete <title>";
	}

	protected boolean run() {

		success = ChatColor.GOLD + "Artwork " + ChatColor.AQUA + title
				+ ChatColor.GOLD + " has been removed";

		if (art instanceof Artwork) {

			((Artwork) art).delete(sender);

		}
		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)

			return error;

		if (sender instanceof Player && !sender.hasPermission("artiste.admin")) {

			if (((Player) sender).getUniqueId() != art.getArtist())

				return Error.noPermission;

		}

		artist = StartClass.artistList.get(art.getArtist());

		if (!artist.delArtwork(title))

			return error = String.format(Error.noMap, title);

		return error;
	}

}

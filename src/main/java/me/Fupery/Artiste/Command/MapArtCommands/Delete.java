package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.Command.Utils.Error;

import org.bukkit.ChatColor;

// Fix title success message, permission
public class Delete extends MapArtCommand {

	public Delete(CommandListener listener) {
		super(listener);
		usage = "delete <title>";

		authorRequired = !sender.hasPermission("Artiste.admin");
		playerRequired = authorRequired;
		artistRequired = authorRequired;
	}

	protected boolean run() {

		success = ChatColor.GOLD + "Artwork " + ChatColor.AQUA + title
				+ ChatColor.GOLD + " has been removed";

		if (art instanceof Artwork) {

			((Artwork) art).delete(sender);

			StartClass.artistList.get(art.getArtist()).delArtwork(title);

		}
		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)

			return error;

		artist = StartClass.artistList.get(art.getArtist());

		if (!artist.delArtwork(title))

			return error = String.format(Error.noMap, title);

		return error;
	}

}

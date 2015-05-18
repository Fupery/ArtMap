package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.Command.Utils.Error;

import org.bukkit.ChatColor;

// Fix title success message, permission
public class Delete extends MapArtCommand {

	public void initialize() {
		
		usage = "delete <title>";

		authorRequired = !sender.hasPermission("Artiste.admin");
		playerRequired = authorRequired;
		artistRequired = authorRequired;
	}

	public boolean run() {

		success = ChatColor.GOLD + "Artwork " + ChatColor.AQUA + title
				+ ChatColor.GOLD + " has been removed";

		if (art instanceof Artwork) {

			((Artwork) art).delete(sender);

			Artiste.artistList.get(art.getArtist()).delArtwork(title);

		}
		return true;
	}

	@Override
	public String conditions() {
		artist = Artiste.artistList.get(art.getArtist());

		if (art.getArtist() == null || !artist.delArtwork(title))

			return error = String.format(Error.noMap, title);

		return error;
	}
}

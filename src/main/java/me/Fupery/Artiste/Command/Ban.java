package me.Fupery.Artiste.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.Artist;

public class Ban extends AbstractCommand {
	//TODO - handling for offline players

	private Artist artist;

	public void initialize() {

		usage = "<ban|unban>";
		adminRequired = true;
		minArgs = 2;
		maxArgs = 2;
	}

	public boolean run() {

		switch (args[0].toLowerCase()) {

		case "ban":

			success = ChatColor.GOLD
					+ String.format(
							"%s has been banned from creating artworks",
							args[1]);
			artist.setBanned(true);
			return true;

		case "unban":

			success = ChatColor.GOLD
					+ String.format("%s has been given artwork privileges",
							args[1]);
			artist.setBanned(false);
			return true;

		default:

			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public String conditions() {
		
		OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(args[1]);

		if (p == null)

			return error = String.format("Player '%s' not found.", args[1]);

		artist = Artiste.artistList.get(p.getUniqueId());

		if (artist == null) {

			artist = new Artist(p.getUniqueId());

			Artiste.artistList.put(artist.getArtistID(), artist);
		}

		return error;
	}
}

package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.MapArt.Artwork;

import org.bukkit.entity.Player;

public class Delete extends MapArtCommand {

	public Delete(CommandListener listener) {
		super(listener);
		usage = "delete <title>";
	}

	protected boolean run() {

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (player.getUniqueId() != art.getArtist()
					&& !(player.hasPermission("artiste.override")))
				return false;
		}
		if (art instanceof Artwork) {

			((Artwork) art).delete(sender);
			return true;
		}
		return true;
	}

}

package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.Command.Utils.Error;
import me.Fupery.Artiste.MapArt.MapCategory;
import me.Fupery.Artiste.MapArt.PrivateMap;

import org.bukkit.ChatColor;

public class Publish extends MapArtCommand {

	private MapCategory category;

	public void initialize() {

		usage = "publish <title> <category|list>";
		authorRequired = true;
		playerRequired = true;
		minArgs = 3;
		maxArgs = 3;
		if (args.length > 1) {
			success = String.format("%s has been submitted for approval!",
					ChatColor.AQUA + args[1] + ChatColor.GOLD);
		}
	}

	public boolean run() {
		((PrivateMap) art).setQueued(true, category);
		return true;
	}

	@Override
	public String conditions() {

		if (!sender.hasPermission("artiste.playerTier2")) {

			return Error.noPubPermission;
		}

		switch (type) {

		case PRIVATE:

			if (((PrivateMap) art).isDenied())

				return Error.alreadyDenied;

			return null;

		case QUEUED:

			error = String.format(Error.alreadyQueued, args[1]);
			break;

		case PUBLIC:

			error = String.format(Error.alreadyPub, args[1]);
			break;

		default:
			error = usage;
		}
		if (args[2].equalsIgnoreCase("list")) {
			return error = Error.categories;
		} else {
			category = MapCategory.getCategory(args[2]);
		}
		return error;
	}
}

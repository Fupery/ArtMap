package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.Command.Utils.Error;
import me.Fupery.Artiste.MapArt.PrivateMap;

import org.bukkit.ChatColor;

public class Publish extends MapArtCommand {

	public void initialize() {

		usage = "publish <title>";
		authorRequired = true;
		playerRequired = true;
		if (args.length == 2)

			success = String.format("%s has been submitted for approval!",
					ChatColor.AQUA + args[1] + ChatColor.GOLD);
	}

	public boolean run() {
		((PrivateMap) art).setQueued(true);
		return true;
	}

	@Override
	public String conditions() {

		if (!sender.hasPermission("artiste.publicMapCreation")) {

			return Error.noPubPermission;
		}

		switch (type) {

		case PRIVATE:

			if (((PrivateMap) art).isDenied())

				return error = "This artwork has already been denied!";

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
		return error;
	}
}

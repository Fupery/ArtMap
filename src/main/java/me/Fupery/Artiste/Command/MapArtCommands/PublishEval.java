package me.Fupery.Artiste.Command.MapArtCommands;

import org.bukkit.ChatColor;

import me.Fupery.Artiste.MapArt.PrivateMap;
import me.Fupery.Artiste.Command.Utils.Error;
import me.Fupery.Artiste.MapArt.PublicMap;

public class PublishEval extends MapArtCommand {

	private boolean approve;

	public void initialize() {

		usage = "<approve|deny> <title>";
		adminRequired = true;
	}

	public boolean run() {

		if (!getCmd())
			return false;

		PrivateMap map = (PrivateMap) art;

		if (approve && !map.isDenied()) {
			new PublicMap(map);
			success = ChatColor.GOLD + "Successfully published "
					+ ChatColor.AQUA + map.getTitle() + ChatColor.GOLD
					+ " as a public artwork";
		} else {
			map.deny();
			success = ChatColor.GOLD + "Denied " + ChatColor.AQUA
					+ map.getTitle();
		}
		return true;
	}

	@Override
	public String conditions() {

		switch (type) {

		case PRIVATE:

			error = String.format(Error.notQueued, args[1]);
			break;

		case QUEUED:

			return null;

		case PUBLIC:

			error = String.format(Error.alreadyPub, args[1]);

		default:
			error = String.format(Error.notQueued, args[1]);
		}

		return error;
	}

	private boolean getCmd() {
		switch (commandType) {
		case APPROVE:
			approve = true;
			break;
		case DENY:
			approve = false;
			break;
		default:
			return false;
		}
		return true;
	}

}

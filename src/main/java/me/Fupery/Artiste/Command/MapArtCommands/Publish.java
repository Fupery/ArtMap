package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.MapArt.PrivateMap;
import org.bukkit.ChatColor;

public class Publish extends MapArtCommand {

	public Publish(CommandListener listener) {

		super(listener);

		usage = "publish <title>";
		success = String.format("%s has been submitted for approval!",
				ChatColor.AQUA + args[1] + ChatColor.GOLD);
		authorRequired = true;
		playerRequired = true;
	}

	protected boolean run() {
		((PrivateMap) art).setQueued(true);
		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)
			
			return error;

		if (!sender.hasPermission("artiste.publicMapCreation")) {

			return Error.noPubPermission;
		}

		switch (type) {

		case PRIVATE:
			
			if(( (PrivateMap) art).isDenied())
				
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

package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.MapArt.PrivateMap;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.MapArt.PublicMap;

public class PublishEval extends MapArtCommand {

	private boolean approve;

	protected PublishEval(CommandListener listener) {

		super(listener);
		usage = "<approve|deny> <title>";
		adminRequired = true;
	}

	protected boolean run() {

		if (!getCmd())
			return false;
		
		PrivateMap map = (PrivateMap) art;

		if (approve && !map.isDenied())

			new PublicMap(sender, map);
		
		else
			
			map.deny();

		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		switch (type) {

		case PRIVATE:

			error = String.format(Error.notQueued, args[1]);
			break;

		case QUEUED:

			return null;

		case PUBLIC:

			error = String.format(Error.alreadyPub, args[1]);

		default:
			error = usage;
		}

		return error;
	}

	private boolean getCmd() {
		switch (args[0].toLowerCase()) {
		case "approve":
			approve = true;
			break;
		case "deny":
			approve = false;
			break;
		default:
			return false;
		}
		return true;
	}

}

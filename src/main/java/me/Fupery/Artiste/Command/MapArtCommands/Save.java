package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.Command.CanvasCommands.CanvasCommand;
import me.Fupery.Artiste.MapArt.PrivateMap;

public class Save extends CanvasCommand {

	public Save(CommandListener listener) {
		super(listener);
		usage = "save <title>";
		canvasRequired = true;
		claimRequired = true;
		artistRequired = true;
		playerRequired = true;
	}

	protected boolean run() {

		new PrivateMap(sender, args[1]);
		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		int t = args[1].length();

		if (t > 16 || t < 3 || args[1].equalsIgnoreCase("public")
				|| args[1].equalsIgnoreCase("private"))

			return Error.invalSave;

		if (StartClass.artList.get(args[1]) != null)

			return Error.alreadySaved;

		return null;
	}

}

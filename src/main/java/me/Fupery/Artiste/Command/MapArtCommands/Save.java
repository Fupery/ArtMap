package me.Fupery.Artiste.Command.MapArtCommands;

import org.bukkit.ChatColor;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.Command.CanvasCommands.CanvasCommand;
import me.Fupery.Artiste.MapArt.PrivateMap;
import me.Fupery.Artiste.Tasks.MapReflection;

public class Save extends CanvasCommand {

	String title;

	public Save(CommandListener listener) {
		super(listener);
		usage = "save <title>";

		minArgs = 2;
		maxArgs = 2;

		canvasRequired = true;
		claimRequired = true;
		artistRequired = true;
		playerRequired = true;

		if (args.length == 2)
			title = args[1];
	}

	protected boolean run() {

		success = ChatColor.GOLD + "Successfully saved " + ChatColor.AQUA
				+ args[1] + ChatColor.GOLD + " as a private artwork";

		new PrivateMap(sender, title);
		new MapReflection(title);
		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)

			return error;

		int t = title.length();

		if (t > 16 || t < 3 || title.equalsIgnoreCase("public")
				|| title.equalsIgnoreCase("private"))

			return Error.invalSave;

		if (StartClass.artList.get(title) != null)

			return Error.alreadySaved;

		if (!artist.addArtwork(title))

			return Error.maxArt;

		return error;
	}

}

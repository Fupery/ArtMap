package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Artwork;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Edit extends MapArtCommand {

	private boolean claimRequired;
	private Canvas canvas;

	public Edit(CommandListener listener) {
		super(listener);

		usage = "edit <title>";

		playerRequired = true;
		claimRequired = true;
		canvasRequired = true;

		if (art != null)

			success = ChatColor.GOLD + "Loading " + ChatColor.AQUA
					+ ((Artwork) art).getTitle() + ChatColor.GOLD
					+ "to the canvas";
	}

	protected boolean run() {

		art.edit();
		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)
			
			return error;

		this.canvas = StartClass.canvas;

		if (canvas != null)

			if (claimRequired && canvas.getOwner() != (Player) sender)

				error = Error.notOwner;

		return error;
	}

}

package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Command.Utils.Error;
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
		authorRequired = true;

		if (art != null)

			success = ChatColor.GOLD + "Loading " + ChatColor.AQUA
					+ ((Artwork) art).getTitle() + ChatColor.GOLD
					+ "to the canvas";
	}

	protected boolean run() {
		
		if (art instanceof Artwork) {
			Artwork a = (Artwork) art;
			a.edit(a.getMap());

			return true;
		} else
			return false;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)

			return error;

		this.canvas = StartClass.canvas;

		if (canvas != null)

			if (claimRequired && canvas.getOwner() != (Player) sender)

				error = Error.notOwner;

		if (StartClass.plugin.getConfig().getInt("coolOffTime") > 0
				&& canvas.isCoolingOff())

			error = Error.coolOff;

		return error;
	}

}

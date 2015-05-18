package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Command.Utils.Error;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.Artwork;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Edit extends MapArtCommand {

	private boolean claimRequired;
	private Canvas canvas;

	public void initialize() {

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

	public boolean run() {
		
		if (art instanceof Artwork) {
			Artwork a = (Artwork) art;
			a.edit(a.getMap());

			return true;
		} else
			return false;
	}

	@Override
	public String conditions() {

		this.canvas = Artiste.canvas;

		if (canvas != null)

			if (claimRequired && canvas.getOwner() != (Player) sender)

				error = Error.notOwner;

		if (Artiste.plugin.getConfig().getInt("coolOffTime") > 0
				&& canvas.isCoolingOff())

			error = Error.coolOff;

		return error;
	}
}

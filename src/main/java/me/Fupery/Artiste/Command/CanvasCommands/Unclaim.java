package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.MapArt.Buffer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Unclaim extends CanvasCommand {

	public void initialize() {

		claimRequired = true;
		artistRequired = true;
		usage = "unclaim";
		success = ChatColor.GOLD + "You have unclaimed the canvas, "
				+ "your work will be saved for later!";
	}

	public boolean run() {
		unclaim();
		return true;
	}

	public static void unclaim() {

		Canvas c = Artiste.canvas;
		if (c == null) {
			return;
		}
		Player p = c.getOwner();

		if (p == null) {
			return;
		}
		Artist artist = Artiste.artistList.get(p.getUniqueId());
		artist.setBuffer(new Buffer());

		if (Artiste.canvas != null) {
			Artiste.canvas.clear(p);
		}
		if (Artiste.claimTimer != null) {
			Artiste.claimTimer.cancel();
		}
	}
}

package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Buffer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Unclaim extends CanvasCommand {

	public Unclaim(CommandListener listener) {

		super(listener);
		claimRequired = true;
		artistRequired = true;
		usage = "unclaim";

		success = ChatColor.GOLD + "You have unclaimed the canvas, "
				+ "your work will be saved for later!";
	}

	protected boolean run() {

		if (artist.getBuffer() != null) {

			artist.clearBuffer();
		}
		artist.setBuffer(new Buffer(sender));

		canvas.clear(sender);

		Bukkit.getServer().getScheduler().cancelTasks(StartClass.plugin);

		return true;
	}

}

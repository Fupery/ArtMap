package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Buffer;
import me.Fupery.Artiste.Tasks.ClaimTimer;
import me.Fupery.Artiste.Tasks.TimeRemaining;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Claim extends CanvasCommand {

	public Claim(CommandListener listener) {

		super(listener);
		playerRequired = true;
		artistRequired = true;
		usage = "claim";
	}

	public boolean run() {

		int claimTime = StartClass.config.getInt("claimTime");

		Player player = (Player) sender;

		canvas.setOwner(player);
		Buffer m = artist.getBuffer();

		if (m != null) {

			m.edit();
			artist.clearBuffer();
		}
		if (claimTime > 0) {

			success = ChatColor.GOLD + "Canvas claimed for " + ChatColor.YELLOW
					+ claimTime + ChatColor.GOLD + " minutes!";

			int ticks = claimTime * 60 * 20;

			if (claimTime > 5) {
				int warning = ticks - (5 * 60 * 20);
				new TimeRemaining(sender).runTaskLater(StartClass.plugin,
						warning);

			}
			new ClaimTimer(sender, StartClass.plugin).runTaskLater(
					StartClass.plugin, ticks);

		} else
			success = ChatColor.GOLD + "Canvas claimed!";

		return true;
	}

	protected String evaluate() {
		error = super.evaluate();
		Player p = canvas.getOwner();

		if (p == (Player) sender) {

			error = "You have already claimed the canvas!";
		}
		if (p != (Player) sender && p != null)

			error = "Someone else is using the canvas!";

		return error;
	}

}

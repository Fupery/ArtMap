package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Command.Utils.Error;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public class Reset extends CanvasCommand {

	private DyeColor colour;

	public void initialize() {

		coolOffRequired = (!sender.hasPermission("Artiste.admin") && Artiste.plugin
				.getConfig().getInt("coolOffTime") > 0);

		claimRequired = !sender.hasPermission("Artiste.admin");

		usage = "reset <colour|list>";
		maxArgs = 2;

	}

	public boolean run() {

		canvas.reset(sender, colour);
		canvas.startCoolOff();
		return true;
	}

	@Override
	public String conditions() {

		if (args.length > 1) {

			for (DyeColor c : DyeColor.values()) {
				if (args[1].equalsIgnoreCase(c.toString())) {

					colour = c;
					break;
				}
			}
		} else
			colour = DyeColor.WHITE;

		if (colour != null) {

			success = ChatColor.GOLD + "Resetting Canvas to " + ChatColor.AQUA
					+ colour.toString() + ChatColor.GOLD + " wool";
			return null;

		} else
			error = Error.colours;
		return error;
	}
}

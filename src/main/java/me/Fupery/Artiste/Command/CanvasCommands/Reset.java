package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Command.Error;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public class Reset extends CanvasCommand {

	private DyeColor colour;

	public Reset(CommandListener listener) {

		super(listener);

		coolOffRequired = (!sender.hasPermission("Artiste.admin") && StartClass.plugin
				.getConfig().getInt("coolOffTime") > 0);
		claimRequired = !sender.hasPermission("Artiste.admin");
		usage = "reset <colour|list>";
		maxArgs = 2;

	}

	protected boolean run() {

		canvas.reset(sender, colour);
		canvas.startCoolOff();
		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)

			return error;

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

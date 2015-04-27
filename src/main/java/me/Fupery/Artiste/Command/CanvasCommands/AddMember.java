package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.CommandListener;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class AddMember extends CanvasCommand {

	public AddMember(CommandListener listener) {

		super(listener);
		usage = "addMember <playername>";
		claimRequired = true;
		minArgs = 2;
		maxArgs = 2;
	}

	@SuppressWarnings("deprecation")
	protected boolean run() {

		String player = args[1];

		Server s = sender.getServer();

		if (s.getOnlinePlayers().contains(player)) {

			Player p = s.getPlayer(player);

			canvas.addMember(p);

			success = String.format("%s%s has been added as an artist!",
					ChatColor.AQUA + player, ChatColor.GOLD);

		} else
			error = "That player isn't online!";

		return false;
	}

}

package me.Fupery.Artiste.Command.CanvasCommands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class AddMember extends CanvasCommand {

	Server s;
	String player;

	public void initialize() {
		
		usage = "<addMember|delMember> <playername>";
		claimRequired = true;
		minArgs = 2;
		maxArgs = 2;
	}

	public boolean run() {

		Player p = s.getPlayer(player);

		switch (commandType) {

		case ADDMEMBER:
			canvas.addMember(p);
			success = String.format("%s%s has been added as an artist!",
					ChatColor.AQUA + player, ChatColor.GOLD);
			break;

		case DELMEMBER:
			canvas.delMember(p);
			success = String.format("%s%s has been removed.", ChatColor.AQUA
					+ player, ChatColor.GOLD);
			break;

		default:
			return false;
		}
		return true;
	}

	@Override
	public String conditions() {

		String player = args[1];

		Server s = sender.getServer();

		if (canvas.getOwner().getName() == player) {

			return "You have already claimed the canvas!";
		}
		if (!s.getOnlinePlayers().contains(player)) {

			error = "That player isn't online!";
		}
		return error;
	}
}

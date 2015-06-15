package me.Fupery.Artiste.Command.CanvasCommands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class AddMember extends CanvasCommand {

	private Player player;

	public void initialize() {
		usage = "<addMember|delMember> <playername>";
		claimRequired = true;
		minArgs = 2;
		maxArgs = 2;
	}

	public boolean run() {

		switch (commandType) {

		case ADDMEMBER:
			canvas.addMember(player);
			success = String.format("%s%s has been added as an artist!",
					ChatColor.AQUA + player.getName(), ChatColor.GOLD);
			break;

		case DELMEMBER:
			canvas.delMember(player);
			success = String.format("%s%s has been removed.", ChatColor.AQUA
					+ player.getName(), ChatColor.GOLD);
			break;

		default:
			return false;
		}
		return true;
	}

	@Override
	public String conditions() {

		Server s = sender.getServer();

		player = s.getPlayer(args[1]);

		if (canvas.getOwner() == player) {

			return "You have already claimed the canvas!";
		}
		if (!s.getOnlinePlayers().contains(player)) {

			error = "That player isn't online!";
		}
		return error;
	}
}

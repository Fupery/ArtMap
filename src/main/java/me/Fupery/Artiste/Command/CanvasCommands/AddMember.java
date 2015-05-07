package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.CommandListener;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class AddMember extends CanvasCommand {

	Server s;
	String player;

	public AddMember(CommandListener listener) {

		super(listener);
		usage = "<addMember|delMember> <playername>";
		claimRequired = true;
		minArgs = 2;
		maxArgs = 2;
	}

	@SuppressWarnings("deprecation")
	protected boolean run() {

		Player p = s.getPlayer(player);

		switch (args[0]) {

		case "addMember":

			canvas.addMember(p);
			success = String.format("%s%s has been added as an artist!",
					ChatColor.AQUA + player, ChatColor.GOLD);
			break;

		case "deleteMember":

			canvas.delMember(p);

			success = String.format("%s%s has been removed.", ChatColor.AQUA
					+ player, ChatColor.GOLD);
			break;

		default:

			return false;
		}

		return true;
	}

	protected String evaluate() {
		
		error = super.evaluate();

		if (error != null)

			return error;

		String player = args[1];

		Server s = sender.getServer();
		
		if(canvas.getOwner().getName() == player)
			
			return error = "You have already claimed the canvas!";

		if (!s.getOnlinePlayers().contains(player))

			error = "That player isn't online!";

		return error;
	}

}

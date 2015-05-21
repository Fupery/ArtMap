package me.Fupery.Artiste.Command.CanvasCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Info extends CanvasCommand {

	public void initialize() {

		usage = "info";
	}

	public boolean run() {

		if (sender.hasPermission("artiste.admin")) {

			sender.sendMessage(new String[] {

					ChatColor.GOLD + "Canvas Pos1: " + ChatColor.DARK_AQUA
							+ canvas.getPos1().toString(),

					ChatColor.GOLD + "Canvas Pos2: " + ChatColor.DARK_AQUA
							+ canvas.getPos2().toString() });
		}

		if (canvas.getOwner() != null) {

			sender.sendMessage(ChatColor.GOLD + "Claimed by: "
					+ ChatColor.DARK_AQUA + canvas.getOwner().getName());

			String members = ChatColor.GOLD + "Members: ";

			if (!canvas.getMembers().isEmpty())

				for (UUID p : canvas.getMembers()) {

					members += (ChatColor.GRAY + Bukkit.getPlayer(p).getName()
							+ ChatColor.GOLD + ", ");

				}

			sender.sendMessage(members);

		} else

			sender.sendMessage(ChatColor.GOLD
					+ "The canvas is not currently claimed!");

		return true;
	}

}

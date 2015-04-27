package me.Fupery.Artiste.Command;

import org.bukkit.ChatColor;

import me.Fupery.Artiste.CommandListener;

public class Help extends AbstractCommand {

	private static ChatColor a = ChatColor.DARK_AQUA, b = ChatColor.AQUA,
			c = ChatColor.GOLD;

	public Help(CommandListener listener) {

		super(listener);

		maxArgs = 2;

		usage = "help <page-number|admin>";

	}

	protected boolean run() {

		int p;
		String page = null;

		if (args.length > 1) {

			if (args[1].equalsIgnoreCase("admin"))

				p = 3;

			else
				try {

					p = Integer.parseInt(args[1]);

				} catch (Exception e) {

					return false;
				}
		} else
			p = 1;

		switch (p) {

		case 1:

			sender.sendMessage(page1);

			page = ((Integer) p++).toString();
			break;

		case 2:

			sender.sendMessage(page2);
			if (sender.hasPermission("Artiste.admin"))

				page = "admin";

			successMsg = false;
			break;

		case 3:

			if (!sender.hasPermission("Artiste.admin")) {
				error = Error.noPermission;
				return false;
			}

			sender.sendMessage(page3);
			successMsg = false;
			break;
		default:
			return false;
		}
		if (page != null)

			success = String.format(ChatColor.DARK_PURPLE
					+ "/artmap help %s[%s]%s for more", ChatColor.LIGHT_PURPLE,
					page, ChatColor.DARK_AQUA);

		else
			success = null;

		return true;
	}

	public static final String[] page1 = new String[] {

			c + "¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬ ArtMap Help [1] ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬",
			c + "•" + a + "  /artmap claim" + c + " |" + b
					+ " Claim the canvas" + c + " |",
			c + "•" + a + "  /artmap unclaim" + c + " |" + b
					+ " Save your progress and unclaim" + c + " |",
			c + "•" + a + "  /artmap addmember <playername>" + c + " |" + b
					+ " Add a player" + c + " |",
			c + "•" + a + "  /artmap reset [colour|list]" + c + " |" + b
					+ " Reset the canvas to a colour" + c + " |",
			c + "•" + a + "  /artmap save <title> " + c + " |" + b
					+ " Save your artwork" + c + " |"

	};
	public static final String[] page2 = new String[] {

			c + "¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬ ArtMap Help [2] ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬",
			c + "•" + a + "  /artmap publish <title> " + c + " |" + b
					+ " Make your artwork public" + c + " |",
			c + "•" + a + "  /artmap list [public|private] [p]" + c + " |" + b
					+ " List available artworks" + c + " |",
			c + "•" + a + "  /artmap edit <title>" + c + " |" + b
					+ " Load a saved artwork to the canvas" + c + " |",
			c + "•" + a + "  /artmap buy <title>" + c + " |" + b
					+ " Purchase an artwork as a map item" + c + " |",
			c + "•" + a + "  /artmap delete <title>" + c + " |" + b
					+ " Delete an artwork" + c + " |", };
	public static final String[] page3 = new String[] {

			c + "¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬ ArtMap Help [3] ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬",
			c + "•" + a + "  /artmap define <x><y><z>" + c + " |" + b
					+ " Define the canvas" + c + " |",
			c + "•" + a + "  /artmap remove" + c + " |" + b
					+ " Remove the canvas object" + c + " |",
			c + "•" + a + "  /artmap info" + c + " |" + b
					+ " Return info on the canvas" + c + " |",
			c + "•" + a + "  /artmap list [queued] [p]" + c + " |" + b
					+ " List publish requests" + c + " |",
			c + "•" + a + "  /artmap approve" + c + " |" + b
					+ " Approve a requested publish" + c + " |",
			c + "•" + a + "  /artmap deny" + c + " |" + b
					+ " Deny a requested publish" + c + " |", };
}

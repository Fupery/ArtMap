package me.Fupery.Artiste.Command;

import org.bukkit.ChatColor;

import static me.Fupery.Artiste.Utils.Formatting.*;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Command.Utils.AbstractCommand;
import me.Fupery.Artiste.Command.Utils.Error;

public class Help extends AbstractCommand {

	public void initialize() {

		maxArgs = 2;
		usage = "help <page-number|admin>";
		disablePrefix = true;
	}

	public boolean run() {

		int p;
		String page = null;

		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("admin")) {
				p = 3;
			} else {
				try {
					p = Integer.parseInt(args[1]);
				} catch (Exception e) {
					return false;
				}
			}
		} else {
			p = 1;
		}

		sender.sendMessage(header(((Integer) p).toString()));

		switch (p) {

		case 1:
			sender.sendMessage(page1);
			page = "2";
			break;

		case 2:

			sender.sendMessage(page2);
			if (sender.hasPermission("artiste.admin")) {
				page = "admin";
			}
			break;

		case 3:

			if (!sender.hasPermission("artiste.admin")) {
				error = Error.noPermission;
				return false;
			}
			sender.sendMessage(page3);
			break;

		default:
			return false;
		}
		if (page != null) {

			success = String.format(ChatColor.DARK_PURPLE
					+ "/artmap help %s[%s]%s for more", ChatColor.LIGHT_PURPLE,
					page, ChatColor.DARK_AQUA);

		} else {
			success = null;
		}
		if (Artiste.canvas == null && sender.hasPermission("artiste.admin")) {
			sender.sendMessage(startup);
		}
		return true;
	}

	public static final String[] startup = new String[] {
			ChatColor.GOLD
					+ "Use /artmap define <x><y><z> to generate a new canvas",
			ChatColor.YELLOW
					+ "The location you provide will define the NW corner of the canvas" };

	public static final String[] page1 = new String[] {
			helpFormat("claim", "Claim the canvas"),
			helpFormat("unclaim", "Unclaim the canvas & save your work"),
			helpFormat("addmember <playername>", "Add a player"),
			helpFormat("delmember <playername>", "Remove a player"),
			helpFormat("reset [colour|list]", "Reset the canvas to a colour"),
			helpFormat("save <title>", "Save your artwork") };

	public static final String[] page2 = new String[] {
			helpFormat("publish <title>", "Make your artwork public"),
			helpFormat("list [public|private] [p]", "List available artworks"),
			helpFormat("edit <title>", "Load a saved artwork to the canvas"),
			helpFormat("buy <title>", "Purchase an artwork as a map item"),
			helpFormat("delete <title>", "Delete an artwork") };

	public static final String[] page3 = new String[] {
			helpFormat("define <x><y><z>", "Define the canvas"),
			helpFormat("remove", "Remove the canvas"),
			helpFormat("list [queued] [p]", "List all publish requests"),
			helpFormat("<approve|deny>", "Handle a publish request"),
			helpFormat("<ban|unban>", "Ban a player from making art"), };

	private static String header(String page) {
		return String.format("%s --- ArtMap Help [%s] ---", colourA, page);
	}

	private static String helpFormat(String usage, String description) {
		return String.format("%s•  %s/artmap %s %s| %s%s %s|", colourA,
				colourB, usage, colourA, colourC, description, colourA);
	}
}

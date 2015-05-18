package me.Fupery.Artiste.Command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.AbstractMapArt.validMapType;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.MapArt.PublicMap;
import me.Fupery.Artiste.Command.Utils.Error;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

//fix errors for no canvas
public class List extends AbstractCommand {

	private validMapType type;
	private Set<String> keys;
	private ArrayList<AbstractMapArt> list;
	private AbstractMapArt art;
	private int pages;

	public void initialize() {
		
		maxArgs = 3;
		playerRequired = true;

		usage = "list <private|public> [pg]";

	}

	@Override
	public String conditions() {

		if (Artiste.artList.isEmpty())

			error = String.format(Error.noArtwork, "");
		
		if (args.length == 3) {
			try {
				pages = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				pages = 0;
			}
		} else
			pages = 0;

		type = resolveType();

		return error;
	}

	public boolean run() {

		keys = Artiste.artList.keySet();

		if (!sort() || list.size() < 1) {

			error = String.format(Error.noArtwork, type.toString()
					.toLowerCase());
			return false;

		}

		int line;
		int dS = 7; // max lines to send

		if ((pages * dS) < list.size())

			line = pages * dS;

		else {
			line = 0;
			pages = 0;
		}

		Integer l = (Integer) (pages + 1);
		String buys = "";

		sender.sendMessage(String.format(ChatColor.GOLD
				+ "Showing %s%s artworks", header(), ChatColor.GOLD));

		sender.sendMessage(ChatColor.DARK_PURPLE + "--------"
				+ ChatColor.LIGHT_PURPLE + "[pg " + pages + "]"
				+ ChatColor.DARK_PURPLE + "--------");

		for (int i = line; i < list.size() && i < (pages + dS); i++) {

			art = list.get(i);

			Artwork a = (Artwork) art;

			if (a != null)
				if (a.getType() == validMapType.PUBLIC)
					buys = ChatColor.DARK_PURPLE + "  "
							+ ((PublicMap) a).getBuys() + " buys";

			sender.sendMessage(ChatColor.GOLD + "•  " + ChatColor.AQUA
					+ a.getTitle() + ChatColor.GOLD + " by "
					+ ChatColor.DARK_AQUA
					+ Bukkit.getPlayer(a.getArtist()).getName() + buys);

		}
		if (list.size() > (dS + line))
			sender.sendMessage(String.format(

			ChatColor.DARK_PURPLE + "/artmap list "
					+ type.toString().toLowerCase() + ChatColor.LIGHT_PURPLE
					+ " [%s] " + ChatColor.DARK_AQUA + "to see more", l));
		list = null;

		return true;

	}

	private boolean sort() {

		if (keys.size() == 0)

			return false;

		Iterator<String> compiler = keys.iterator();

		list = new ArrayList<AbstractMapArt>();

		while (compiler.hasNext()) {

			AbstractMapArt m = Artiste.artList.get(compiler.next());

			if (m.getType() == type) {

				if (m.getType() == validMapType.PRIVATE) {

					if (Bukkit.getOfflinePlayer(m.getArtist()) == (OfflinePlayer) sender) {

						list.add(m);
					}
				} else
					list.add(m);
			}
		}
		return true;
	}

	private validMapType resolveType() {
		if (args.length < 2)
			return validMapType.PRIVATE;
		switch (args[1].toLowerCase()) {
		case "private":
			return validMapType.PRIVATE;
		case "public":
			return validMapType.PUBLIC;
		case "template":
			return validMapType.TEMPLATE;
		case "queued":
			return validMapType.QUEUED;
		default:
			return validMapType.PRIVATE;
		}
	}

	private String header() {
		if (type.name().equalsIgnoreCase("private"))
			return ChatColor.AQUA + sender.getName() + "'s";
		else
			return type.name().toLowerCase();
	}



}

package me.Fupery.Artiste.Utils;

import me.Fupery.Artiste.Artiste;

import org.bukkit.ChatColor;


/** Convenience class to standardize text colours */
public class Formatting {

	public static ChatColor colourA = ChatColor.GOLD,
			colourB = ChatColor.DARK_AQUA, colourC = ChatColor.AQUA,
			colourD = ChatColor.DARK_PURPLE, colourE = ChatColor.LIGHT_PURPLE;

	public static String prefix = ChatColor.AQUA + "[ArtMap] ";

	public static ChatColor evalColour(String title) {

		switch (Artiste.artList.get(title).getType()) {
		case PRIVATE:
			return ChatColor.AQUA;
		case PUBLIC:
			return ChatColor.YELLOW;
		case QUEUED:
			return ChatColor.GREEN;
		default:
			return ChatColor.DARK_AQUA;
		}
	}
}

package me.Fupery.Artiste.Command;

import org.bukkit.ChatColor;

/**
 * Convenience class for storing error strings
 * <p>
 * may get replaced, no longer necessary
 */

public class Error {

	private static ChatColor red = ChatColor.RED;

	public static final String noEcon = "MapArt Economy functions disabled - No Vault dependency found!",
			noPermission = red + "You don't have permission!",
			noPubPermission = red
					+ "You don't have permission to create public artworks!",
			notOwner = red + "You must claim the canvas first!",
			noEdit = red + "You can only edit your own artworks!",
			noDef = red + "No canvas has been defined!",
			alreadyDef = red + "The canvas has already been defined!",
			noConsole = red + "Only players may use this command!",
			noArtwork = red + "There are no %s artworks to display!",
			define = red + "Invalid co-ordinates.",
			invalSave = red + "Invalid Name!",
			alreadySaved = red + "That title is already taken!",
			maxArt = red + "You can't claim any more artworks!",
			noMapSize = red + "Map %s corrupted: No mapSize set.",
			notQueued = red + "Map %s is not queued for publication!",
			alreadyQueued = red + "Map %s is already queued for publication!",
			noMap = red + "Map %s not found!", alreadyPub = red
					+ "Map %s has already been published!";

	public static final String colours = red
			+ "BLACK, BLUE, BROWN, CYAN, GRAY, GREEN, LIGHT_BLUE, LIME, "
			+ "MAGENTA, ORANGE, PINK, PURPLE, RED, SILVER, WHITE, YELLOW";

}

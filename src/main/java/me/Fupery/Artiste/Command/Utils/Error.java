package me.Fupery.Artiste.Command.Utils;

import org.bukkit.ChatColor;

/**
 * Convenience class for storing error strings
 * <p>
 * may get replaced, no longer necessary
 */

public class Error {

	public static final String usage = ChatColor.RED
			+ "/artmap help for more commands",
			noEcon = "MapArt Economy functions disabled - No Vault dependency found!",
			noPermission = "You don't have permission!",
			noPubPermission = "You don't have permission to create public artworks!",
			notOwner = "You must claim the canvas first!",
			noEdit = "You can only edit your own artworks!",
			noDef = "No canvas has been defined!",
			alreadyDef = "The canvas has already been defined!",
			noConsole = "Only players may use this command!",
			noArtwork = "There are no %s artworks to display!",
			define = "Invalid co-ordinates.", invalSave = "Invalid Name!",
			alreadySaved = "That title is already taken!",
			maxArt = "You can't claim any more artworks!",
			noMapSize = "Map %s corrupted: No mapSize set.",
			notQueued = "Map %s is not queued for publication!",
			alreadyQueued = "Map %s is already queued for publication!",
			alreadyDenied = "This artwork has been denied!",
			noMap = "Map %s not found!",
			alreadyPub = "Map %s has already been published!",
			coolOff = "Chill out, you can't reset the map that often!",
			noCraft = ChatColor.RED + "Player maps cannot be copied!";

	public static final String colours = "BLACK, BLUE, BROWN, CYAN, GRAY, GREEN, LIGHT_BLUE, LIME, "
			+ "MAGENTA, ORANGE, PINK, PURPLE, RED, SILVER, WHITE, YELLOW";

}

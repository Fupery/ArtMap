package me.Fupery.Artiste.Command;

import org.bukkit.ChatColor;

public class Error {
	
	private static ChatColor red = ChatColor.RED;
	
	public static final String
	noEcon = "MapArt Economy functions disabled - No Vault dependency found!",
	noPermission = red + "You don't have permission!",
	noPubPermission = red + "You don't have permission to create public artworks!",
	notOwner = red + "You must claim the canvas first!",
	noEdit = red + "You can only edit your own artworks!",
	noDef = red + "No canvas has been defined!",
	alreadyDef = red + "The canvas has already been defined!",
	noConsole = red + "Only players may use this command!",
	noArtwork = red + "There are no %s artworks to display!",
	define = red + "Invalid co-ordinates.",
	invalSave = red + "Invalid Name!",
	alreadySaved = red + "Thas title is already taken!",
	maxArt = red + "You can't claim any more artworks!",
	noMapSize = red + "Map ' %s ' corrupted: No mapSize set.",
	notQueued = red + "Map ' %s ' is not queued for publication!",
	noMap = red + "Map ' %s ' not found!"
	;
	
	
	public static final String colours =  red + 
	"BLACK, BLUE, BROWN, CYAN, GRAY, GREEN, LIGHT_BLUE, LIME, "
	+ "MAGENTA, ORANGE, PINK, PURPLE, RED, SILVER, WHITE, YELLOW";
	
	public static String usage(String command){
		
		String s = red + "/artmap ";
		
		switch(command.toLowerCase()){
		
		case "define"   : return s + "define <x><y><z>";
		case "remove"   : return s + "remove";
		case "info"     : return s + "info";
		case "claim"    : return s + "claim";
		case "unclaim"  : return s + "unclaim";
		case "addmember": return s + "addmember <playername>";
		case "reset"    : return s + "reset <colour|list>";
		case "save"     : return s + "save <title>";
		case "publish"  : return s + "publish <title>";
		case "edit"     : return s + "edit <title>";
		case "buy"      : return s + "buy <title>";
		case "list"     : return s + "list <private|public|all>";
		default : return s + "help for a full list of commands";
		}
	}
	
}

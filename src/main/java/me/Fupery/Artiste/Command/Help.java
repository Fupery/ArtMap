package me.Fupery.Artiste.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help {
	
	public Help(CommandSender sender){
		//placeholders to allow for quick colour/formatting changes
		ChatColor a,b,c;
		a = ChatColor.DARK_AQUA; //command usage
		b = ChatColor.AQUA;      //command description
		c = ChatColor.GOLD;      //title & highlights
		
			sender.sendMessage(new String[]{
	            c + "¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬ ArtMap Help ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬", 
	            c + "•" + a + "  /artmap claim" + c+" |" + b + " Claim the canvas"+ c+" |",
	            c + "•" + a + "  /artmap unclaim" + c+" |" + b + " Save your progress and unclaim"+ c+" |",
	            c + "•" + a + "  /artmap addmember <playername>" + c+" |" + b + " Add a player"+ c+" |",
	            c + "•" + a + "  /artmap reset [colour|list]" + c+" |" + b + " Reset the canvas to a colour"+ c+" |",
	            c + "•" + a + "  /artmap save <title> " + c+" |" + b + " Save your artwork"+ c+" |",
	            c + "•" + a + "  /artmap publish <title> " + c+" |" + b + " Make your artwork public"+ c+" |",
	            c + "•" + a + "  /artmap list [public|private] [p]" + c+" |" + b + " List available artworks"+ c+" |",
	            c + "•" + a + "  /artmap edit <title>" + c+" |" + b + " Load a saved artwork to the canvas"+ c+" |",
	            c + "•" + a + "  /artmap buy <title>" + c+" |" + b + " Purchase an artwork as a map item"+ c+" |",
	            c + "•" + a + "  /artmap delete <title>" + c+" |" + b + " Delete an artwork"+ c+" |",
	 
	    	}); //end player help string
		if(!(sender instanceof Player) || sender.hasPermission("artiste.staff")){
			sender.sendMessage(new String[]{
		            c + "¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬ Admin  Help ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬", 
		            c + "•" + a + "  /artmap define <x><y><z>" + c+" |" + b + " Define the canvas"+ c+" |",
		            c + "•" + a + "  /artmap remove" + c+" |" + b + " Remove the canvas object"+ c+" |",
		            c + "•" + a + "  /artmap info" + c+" |" + b + " Return info on the canvas"+ c+" |",
		            c + "•" + a + "  /artmap list [queued] [p]" + c+" |" + b + " List publish requests"+ c+" |",
		            c + "•" + a + "  /artmap approve" + c+" |" + b + " Approve a requested publish"+ c+" |",
		            
		    	}); // end admin command help
		}
	}
}

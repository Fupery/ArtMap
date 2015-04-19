package me.Fupery.Artiste.Command;

import me.Fupery.Artiste.Canvas;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;

public class Reset {
	
	public static void reset(CommandSender sender, String[] args){
		
		DyeColor colour = null;
		
		if(args.length > 1){
		
			for(DyeColor c : DyeColor.values()){
				if(args[1].equalsIgnoreCase(c.toString())){
				
					colour = c;
					break;
				}
			}
		}else colour = DyeColor.WHITE;
		
		
		if(colour != null) 
			
			Canvas.findCanvas().reset(sender, colour);
		
		else sender.sendMessage(Error.colours);
	}
}

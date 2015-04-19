package me.Fupery.Artiste.Command;

import me.Fupery.Artiste.Canvas;

import org.bukkit.command.CommandSender;

public class Define {
	//Eval methods filter inputs for errors
	public static boolean define(CommandSender sender, String[] args){
		
		double p1, p2, p3;
		
		try {
			p1 = Double.parseDouble(args[1]);
			p2 = Double.parseDouble(args[2]);
			p3 = Double.parseDouble(args[3]); 
			
		} catch (NumberFormatException e){
			
			sender.sendMessage(Error.define);
			return false;
		}
		
		Canvas.defineCanvas(sender, p1, p2, p3);
		return true;
	}
}

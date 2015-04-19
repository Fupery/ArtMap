package me.Fupery.Artiste.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.Artiste.Canvas;

public class Handler {
	
	public CommandSender sender;
	public String[] args;
	
	public Handler(CommandSender sender, String[] args){
		this.sender = sender;
		this.args = args;
	}
	
	public boolean evalCmd(int minArgs, int maxArgs, 
		boolean canvasRequired, boolean claimRequired, boolean adminRequired){
		
		//argument length check
		if(args.length < minArgs || args.length > maxArgs){
			sender.sendMessage(Error.usage(args[0]));
			return false;
		}
		//console check
		if(!(sender instanceof Player)){
			
			if(!adminRequired){
				
				sender.sendMessage(Error.noConsole);
				return false;
				
			} else return true;
		}
		Player player = (Player) sender;
		
		//admin command check
		if(adminRequired && !player.hasPermission("artiste.admin")){
			
			sender.sendMessage(Error.noPermission);
			return false;
		}
		//canvas check
		Canvas c = Canvas.findCanvas();
		
		if(canvasRequired)
			
			if(c == null){
				sender.sendMessage(Error.noDef);
				return false;
			}
		else
			if(c != null && adminRequired &&
			args[0] == "define"){
				
				sender.sendMessage(Error.alreadyDef);
				return false;
			}
		
		//canvas owner check
		if(claimRequired && player != c.getOwner()){
			
			sender.sendMessage(Error.notOwner);
			return false;
		}
				
		if(!(player.hasPermission("artiste.claim")) )
			if(canvasRequired){
				
				player.sendMessage(Error.noPermission);
				return false;
		}
		return true;
	}
}

package me.Fupery.Artiste.Tasks;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeRemaining extends BukkitRunnable{
	 
	public CommandSender sender;
	
	public TimeRemaining(CommandSender sender){
		this.sender = sender;
	}

	@Override
	public void run() {
		
		sender.sendMessage(ChatColor.AQUA + "[ArtMap] " +
		ChatColor.GOLD + "5 Minutes remaining with the canvas!");
	}

}

package me.Fupery.Artiste.Tasks;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.StartClass;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ClaimTimer extends BukkitRunnable{
	public CommandSender sender;
	private StartClass plugin;
	
	public ClaimTimer(CommandSender sender, StartClass plugin){
		this.sender = sender;
		this.plugin = plugin;
	}

	@Override
	public void run() {
		Canvas c = Canvas.findCanvas();
		
		sender.sendMessage(ChatColor.AQUA + "[ArtMap]" +
		ChatColor.GOLD + "Your time using the canvas is up!");
		
		c.unclaim(sender, plugin);
	}

}

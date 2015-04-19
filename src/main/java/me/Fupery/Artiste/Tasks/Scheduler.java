package me.Fupery.Artiste.Tasks;

import me.Fupery.Artiste.Canvas;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class Scheduler extends BukkitRunnable {
private CommandSender sender;
private String process;
	@Override
	public void run() {
		switch(this.process){
			case "define" : ;
			break;
			case "reset" :
					
			break;
			case "publish" : ;
			break;
			case "edit" : ;
			break;
			case "test" : ;
			default : break;
		}
	}
	public void setProcess(String process){
		this.process = process;
	}
	public void initializeLoop(CommandSender sender, Canvas c, int x, Location l, Location f){
		this.setSender(sender);
	}
	public CommandSender getSender() {
		return sender;
	}
	public void setSender(CommandSender sender) {
		this.sender = sender;
	}
}

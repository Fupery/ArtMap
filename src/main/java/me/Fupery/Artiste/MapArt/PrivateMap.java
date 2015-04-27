package me.Fupery.Artiste.MapArt;

import java.util.UUID;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.Tasks.MapReflection;
import me.Fupery.Artiste.Command.Error;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class PrivateMap extends Artwork {
	
	private static final long serialVersionUID = -1668200457941039767L;
	private boolean queued;
	private boolean denied;

	@SuppressWarnings("deprecation")
	public PrivateMap(CommandSender sender, String title) {
		
		UUID id = ((Player) sender).getUniqueId();
		Artist a = StartClass.artistList.get(id);
		Canvas c = StartClass.canvas;
		
		a.evalMaxArtworks();
			
		if(a.canPublish()){
			
			mapSize = c.getSize();
			map = new DyeColor[(mapSize*mapSize)+mapSize-1];
			artist = ((Player) sender).getUniqueId();
			this.title = title.toLowerCase();
			
			this.mapId = Bukkit.createMap(Bukkit.getWorld(StartClass.canvas.worldname)).getId();
			
			save();
			
			StartClass.artList.put(title.toLowerCase(), this);
			a.increment();
			
			new MapReflection(title);
			
			sender.sendMessage(ChatColor.GOLD + "Successfully saved " + ChatColor.AQUA +
			title + ChatColor.GOLD + " as a private artwork");
			
		}else sender.sendMessage(Error.maxArt);
	}

	public boolean isQueued() {
		return queued;
	}

	public void setQueued(boolean queued) {
		this.queued = queued;
	}

	public boolean isDenied() {
		return denied;
	}

	public void deny() {
		this.denied = true;
	}
	
}

package me.Fupery.Artiste.MapArt;

import java.util.UUID;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.Command.Error;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class PrivateMap extends Artwork {
	
	private static final long serialVersionUID = -1668200457941039767L;
	private boolean queued;

	public PrivateMap(CommandSender sender, String title) {
		
		UUID id = ((Player) sender).getUniqueId();
		Artist a = StartClass.artistList.get(id);
		Canvas c = Canvas.findCanvas();
		
		a.evalMaxArtworks();
			
		if(a.canPublish()){
			
			mapSize = c.getSize();
			map = new DyeColor[(mapSize*mapSize)+mapSize-1];
			artist = ((Player) sender).getUniqueId();
			this.title = title.toLowerCase();
			
			save();
			
			MapView m = Bukkit.getServer().createMap(((Player) sender).getWorld());
			
			if(!m.getRenderers().isEmpty()){
				for(MapRenderer r : m.getRenderers()){
					m.removeRenderer(r); }
			}
			
			StartClass.artList.put(title.toLowerCase(), this);
			a.increment();
			
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

	
}

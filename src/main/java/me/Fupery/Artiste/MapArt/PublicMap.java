package me.Fupery.Artiste.MapArt;


import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Command.Error;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PublicMap extends Artwork{

	private static final long serialVersionUID = 6321856941144893696L;
	private int buys;

	public PublicMap(CommandSender sender, PrivateMap pm) {
		
			mapSize = pm.getMapSize();
			map = pm.getMap();
			artist = pm.getArtist();
			this.title = pm.getTitle();					
			buys = 0;
			sender.sendMessage(ChatColor.GOLD + "Successfully published " + ChatColor.AQUA +
			title + ChatColor.GOLD + " as a public artwork");
				
			StartClass.artList.put(title.toLowerCase(), this);
	}
		

	public int getBuys() {
		return buys;
	}

	public void setBuys(int buys) {
		this.buys = buys;
	}

}

package me.Fupery.Artiste.MapArt;

import java.io.Serializable;
import java.util.UUID;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.IO.Artist;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Artwork extends AbstractMapArt implements Serializable {

	private static final long serialVersionUID = -1256331201936323289L;
	protected String title;
	protected short mapId;
	
	public void delete(CommandSender sender) {
		
		UUID id = artist;
		Artist a = StartClass.artistList.get(id);
		
		sender.sendMessage(ChatColor.GOLD + "Artwork " + ChatColor.AQUA +
		this.title + ChatColor.GOLD + " has been removed");
		StartClass.artList.remove(title);
			a.decrement();
	}

	public void buy(CommandSender sender){
			
		Player player = (Player) sender;
		
		ItemStack i = new ItemStack(Material.MAP, 1, mapId);
		
		player.getInventory().addItem(i);
		
		if(this instanceof PublicMap)
			((PublicMap) this ).incrementBuys();
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public short getMapId() {
		return mapId;
	}

	public void setMapId(short mapId) {
		this.mapId = mapId;
	}

}

package me.Fupery.Artiste.MapArt;

import java.io.Serializable;
import java.util.UUID;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.Tasks.ArtRenderer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

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

	@SuppressWarnings("deprecation")
	public void buy(CommandSender sender){
			
		Player player = (Player) sender;
				
		AbstractMapArt map = StartClass.artList.get(title);
				
		MapView m = Bukkit.getMap(mapId);
				
		ArtRenderer r = new ArtRenderer(map, sender);
				
		r.initialize(m);
		m.addRenderer(r);
				
		ItemStack i = new ItemStack(Material.MAP, 1, mapId);
		player.getInventory().addItem(i);
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

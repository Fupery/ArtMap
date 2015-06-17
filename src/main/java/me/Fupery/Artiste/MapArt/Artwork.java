package me.Fupery.Artiste.MapArt;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.ArtIO;
import me.Fupery.Artiste.IO.WorldMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Artwork extends AbstractMapArt implements Serializable {

	private static final long serialVersionUID = -1256331201936323289L;
	protected String title;
	protected short mapId;
	protected MapCategory category;

	@SuppressWarnings("deprecation")
	public void delete(CommandSender sender) {

		new WorldMap(Bukkit.getMap(mapId)).delete();
		Artiste.artList.remove(title);
		ArtIO.deleteMap(title);
	}

	public void buy(CommandSender sender) {

		Player player = (Player) sender;
		ItemStack i = new ItemStack(Material.MAP, 1, mapId);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(String.format("'%s'", title));
		im.setLore(Arrays.asList(ChatColor.GOLD + "by " + ChatColor.YELLOW
				+ Bukkit.getOfflinePlayer(artist).getName()));
		i.setItemMeta(im);
		player.getInventory().addItem(i);

		if (this instanceof PublicMap) {
			((PublicMap) this).incrementBuys();
		}
	}

	public DyeColor[] getMap() {

		DyeColor[] d;
		try {
			d = ArtIO.loadMap(title);
		} catch (ClassNotFoundException | IOException e) {
			d = null;
		}
		return d;
	}

	public boolean setMap(DyeColor[] map) {

		try {
			ArtIO.saveMap(map, title);
			return true;
		} catch (IOException e) {
			return false;
		}
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

	public MapCategory getCategory() {
		return category;
	}

	public void setCategory(MapCategory category) {
		this.category = category;
	}

}

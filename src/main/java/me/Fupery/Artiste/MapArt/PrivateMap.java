package me.Fupery.Artiste.MapArt;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.WorldMap;
import me.Fupery.Artiste.Tasks.ColourConvert;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateMap extends Artwork {

	private static final long serialVersionUID = -1668200457941039767L;
	private boolean queued, denied;

	@SuppressWarnings("deprecation")
	public PrivateMap(CommandSender sender, String title) {

		this.artist = ((Player) sender).getUniqueId();
		this.title = title.toLowerCase();
		this.mapSize = Artiste.canvas.getSize();
		this.mapId = Bukkit
				.createMap(Bukkit.getWorld(Artiste.canvas.worldname)).getId();
		setMap(save());
		Artiste.artList.put(title.toLowerCase(), this);
		byte[] m = ColourConvert.byteConvert(getMap(), mapSize);
		new WorldMap(Bukkit.getMap(mapId)).setMap(m);
	}

	public boolean isQueued() {
		return queued;
	}

	public void setQueued(boolean queued, MapCategory category) {
		this.queued = queued;
		this.category = category;
	}

	public boolean isDenied() {
		return denied;
	}

	public void deny() {
		category = null;
		queued = false;
		denied = true;
	}

}

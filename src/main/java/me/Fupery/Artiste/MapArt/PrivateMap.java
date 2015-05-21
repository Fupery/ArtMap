package me.Fupery.Artiste.MapArt;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.IO.MapReflection;

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

		Artist a = Artiste.artistList.get(artist);

		this.mapId = Bukkit
				.createMap(Bukkit.getWorld(Artiste.canvas.worldname)).getId();

		setMap(save());

		Artiste.artList.put(title.toLowerCase(), this);
		a.addArtwork(title);

		new MapReflection(title).override();

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
		this.queued = false;
		this.denied = true;
	}

}

package me.Fupery.Artiste.MapArt;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.Tasks.MapReflection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateMap extends Artwork {

	private static final long serialVersionUID = -1668200457941039767L;
	private boolean queued;
	private boolean denied;

	@SuppressWarnings("deprecation")
	public PrivateMap(CommandSender sender, String title) {

		this.artist = ((Player) sender).getUniqueId();
		this.title = title.toLowerCase();
		this.mapSize = StartClass.canvas.getSize();

		Artist a = StartClass.artistList.get(artist);

		this.mapId = Bukkit.createMap(
				Bukkit.getWorld(StartClass.canvas.worldname)).getId();

		setMap(save());

		StartClass.artList.put(title.toLowerCase(), this);
		a.addArtwork(title);

		MapReflection r = new MapReflection(title);

		r.colorsOverride();
		r.dimensionOverride();

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

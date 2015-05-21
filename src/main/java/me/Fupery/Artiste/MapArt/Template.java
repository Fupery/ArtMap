package me.Fupery.Artiste.MapArt;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.MapReflection;

public class Template extends Artwork {

	private static final long serialVersionUID = 1455694818054458542L;

	@SuppressWarnings("deprecation")
	public Template(String title, DyeColor[] map) {

		boolean d = title.equalsIgnoreCase("default");

		if (Artiste.canvas != null) {

			this.mapSize = (d) ? 64 : Artiste.canvas.getSize();

			this.title = title;

			mapId = Bukkit.getServer()
					.createMap(Bukkit.getWorld(Artiste.canvas.worldname))
					.getId();

			setMap(map);

			Artiste.artList.put(title, this);

			new MapReflection(title).override();
		}
	}

	@SuppressWarnings("deprecation")
	public Template(String title) {

		if (Artiste.canvas != null) {

			this.mapSize = Artiste.canvas.getSize();
			
			World w = Bukkit.getWorld(Artiste.canvas.worldname);

			this.title = title;
			
			this.artist = w.getUID();

			mapId = Bukkit.getServer()
					.createMap(w)
					.getId();

			if (getMap() == null)

				setMap(save());

			Artiste.artList.put(title, this);

			new MapReflection(title).override();
		}
	}
}

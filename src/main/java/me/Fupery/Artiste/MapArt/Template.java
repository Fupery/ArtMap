package me.Fupery.Artiste.MapArt;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Tasks.MapReflection;

public class Template extends Artwork {

	private static final long serialVersionUID = 1455694818054458542L;

	@SuppressWarnings("deprecation")
	public Template(String title, DyeColor[] map) {

		boolean d = title.equalsIgnoreCase("default");

		if (StartClass.canvas != null) {

			this.mapSize = (d) ? 64 : StartClass.canvas.getSize();

			this.title = title;

			mapId = Bukkit.getServer()
					.createMap(Bukkit.getWorld(StartClass.canvas.worldname))
					.getId();

			setMap(map);

			StartClass.artList.put(title, this);

			MapReflection r = new MapReflection(title);

			r.colorsOverride();
			r.dimensionOverride();
		}
	}

	@SuppressWarnings("deprecation")
	public Template(String title) {

		if (StartClass.canvas != null) {

			this.mapSize = StartClass.canvas.getSize();

			this.title = title;

			mapId = Bukkit.getServer()
					.createMap(Bukkit.getWorld(StartClass.canvas.worldname))
					.getId();

			if (getMap() == null)

				setMap(save());

			StartClass.artList.put(title, this);

			MapReflection r = new MapReflection(title);

			r.colorsOverride();
			r.dimensionOverride();
		}
	}
}

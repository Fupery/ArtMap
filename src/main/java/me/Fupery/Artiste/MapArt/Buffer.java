package me.Fupery.Artiste.MapArt;

import java.io.Serializable;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.StartClass;

import org.bukkit.DyeColor;

public class Buffer extends AbstractMapArt implements Serializable {

	private static final long serialVersionUID = 6593038229966932735L;

	private DyeColor[] map;

	public Buffer() {

		Canvas c = StartClass.canvas;
		mapSize = c.getSize();

		map = save();
	}

	public DyeColor[] getMap() {
		
		return map;
	}

	public void setMap(DyeColor[] map) {

		this.map = map;
	}

}

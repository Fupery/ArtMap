package me.Fupery.Artiste.MapArt;

import java.io.Serializable;
import java.util.UUID;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Tasks.SetCanvas;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class AbstractMapArt implements Serializable {

	private static final long serialVersionUID = 3778228012180388020L;
	protected UUID artist;
	protected int mapSize;

	@SuppressWarnings("deprecation")
	public DyeColor[] save() {

		DyeColor[] map = new DyeColor[(mapSize * mapSize) + mapSize - 1];
		Canvas c = Artiste.canvas;
		if (c != null) {
			Location l = c.getPos1().clone();
			int i = 0;

			for (int x = c.getPos1().getBlockX(); x <= c.getPos2().getBlockX(); x++, i++) {
				for (int z = c.getPos1().getBlockZ(); z <= c.getPos2()
						.getBlockZ(); z++, i++) {

					l.setX(x);
					l.setZ(z);
					Block b = l.getBlock();

					if (b.getType() == Material.WOOL) {
						map[i] = DyeColor.getByData(b.getData());
					} else {
						map[i] = null;
					}
				}
			}
		}
		return map;
	}

	public void edit(DyeColor[] map) {
		new SetCanvas(map).runTask(Artiste.plugin);
	}

	public UUID getArtist() {
		return artist;
	}

	public void setArtist(UUID artist) {
		this.artist = artist;
	}

	public int getMapSize() {
		return mapSize;
	}

	public void setMapSize(int mapSize) {
		this.mapSize = mapSize;
	}

	public ValidMapType getType() {

		if (this instanceof PrivateMap) {
			if (((PrivateMap) this).isQueued()) {
				return ValidMapType.QUEUED;
			} else {
				return ValidMapType.PRIVATE;
			}
		} else if (this instanceof PublicMap) {
			return ValidMapType.PUBLIC;
		} else if (this instanceof Buffer) {
			return ValidMapType.BUFFER;
		} else if (this instanceof Template) {
			return ValidMapType.TEMPLATE;
		}
		return null;
	}
}

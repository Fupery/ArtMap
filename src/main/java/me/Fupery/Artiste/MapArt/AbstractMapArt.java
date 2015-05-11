package me.Fupery.Artiste.MapArt;

import java.io.Serializable;
import java.util.UUID;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.StartClass;

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

		Canvas c = StartClass.canvas;
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

					} else
						map[i] = null;
				}
			}
		}
		return map;
	}

	@SuppressWarnings("deprecation")
	public void edit(DyeColor[] map) {

		Canvas c = StartClass.canvas;

		if (c != null && map != null && this.mapSize == c.getSize()) {

			Location l = c.getPos1().clone();

			int i = 0;
			for (int x = c.getPos1().getBlockX(); x <= c.getPos2().getBlockX(); x++, i++) {

				for (int z = c.getPos1().getBlockZ(); z <= c.getPos2()
						.getBlockZ(); z++, i++) {

					l.setX(x);
					l.setZ(z);
					Block b = l.getBlock();

					if (map[i] != null) {

						DyeColor d = map[i];

						if (b.getType() != Material.WOOL)
							b.setType(Material.WOOL);
						if (b.getData() != d.getData())
							b.setData(d.getData());
					}
				}
			}
		}
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

	public validMapType getType() {

		if (this instanceof PrivateMap) {

			if (((PrivateMap) this).isQueued())

				return validMapType.QUEUED;

			else
				return validMapType.PRIVATE;

		} else if (this instanceof PublicMap)

			return validMapType.PUBLIC;

		else if (this instanceof Buffer)

			return validMapType.BUFFER;
		
		else if (this instanceof Template)
			
			return validMapType.TEMPLATE;

		return null;
	}

	public enum validMapType {
		PRIVATE, QUEUED, PUBLIC, BUFFER, TEMPLATE
	}
}

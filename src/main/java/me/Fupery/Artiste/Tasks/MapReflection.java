package me.Fupery.Artiste.Tasks;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.Load;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.Artwork;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

public class MapReflection {

	private Logger log;
	private MapView m;
	private byte[] mapOutput;
	private World world;

	@SuppressWarnings("deprecation")
	public MapReflection(String title) {

		if (Artiste.canvas != null)

			this.world = Bukkit.getWorld(Artiste.canvas.worldname);

		this.log = Bukkit.getLogger();

		AbstractMapArt a = Artiste.artList.get(title);

		if (a != null && a instanceof Artwork) {

			Artwork art = (Artwork) a;
			MapView m = Bukkit.getMap(art.getMapId());

			this.m = m;
			this.mapOutput = new ColourConvert().byteConvert(art.getMap(),
					art.getMapSize());
		} else
			log.warning("invalid title");
	}

	public boolean colorsOverride() {

		Field worldMap;
		Field field;
		byte[] colors;

		try {

			worldMap = m.getClass().getDeclaredField("worldMap");

			worldMap.setAccessible(true);

			Object o = worldMap.get(m);

			field = o.getClass().getDeclaredField("colors");

			field.setAccessible(true);

			colors = (byte[]) field.get(o);

			field.set(o, mapOutput);

		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e1) {

			colors = null;
			log.info(e1.getMessage());

		}

		return (colors != null);
	}

	public boolean dimensionOverride() {

		Field worldMap;
		Field field;
		byte dimension;

		try {

			worldMap = m.getClass().getDeclaredField("worldMap");

			worldMap.setAccessible(true);

			Object o = worldMap.get(m);

			field = o.getClass().getDeclaredField("map");

			field.setAccessible(true);

			dimension = field.getByte(o);

			field.setByte(o, (byte) 5);

		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e1) {

			dimension = -5;
			log.warning(e1.getMessage());
		}

		return (dimension != -5);
	}

	@SuppressWarnings("deprecation")
	public boolean worldMapOverride(short inputId) {

		if (world == null)

			return false;

		Field worldMap;
		Field overrideMap;
		short outputId;

		AbstractMapArt a = Artiste.artList.get("default");

		if (a == null)

			a = Load.setupDefault();

		outputId = ((Artwork) a).getMapId();

		MapView mapIn = Bukkit.getMap(inputId);

		MapView mapOut = Bukkit.getMap(outputId);

		if (mapIn == null || mapOut == null) {

			log.info("map delete error - 'default' map may be corrupted");
			return false;
		}

		try {
			worldMap = mapIn.getClass().getDeclaredField("worldMap");

			worldMap.setAccessible(true);

			overrideMap = mapOut.getClass().getDeclaredField("worldMap");

			overrideMap.setAccessible(true);

			worldMap.set(mapIn, overrideMap.get(mapOut));

		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {

			e.printStackTrace();
			return false;
		}

		return true;
	}
}

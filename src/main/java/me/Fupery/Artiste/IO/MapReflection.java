package me.Fupery.Artiste.IO;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.Tasks.ColourConvert;

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

	public boolean override() {

		byte dimension;

		try {

			Field worldMapField = m.getClass().getDeclaredField("worldMap");

			worldMapField.setAccessible(true);

			Object worldMap = worldMapField.get(m);

			Field dimensionField = worldMap.getClass().getDeclaredField("map");

			dimensionField.setAccessible(true);

			dimension = dimensionField.getByte(worldMap);

			dimensionField.setByte(worldMap, (byte) 5);

			Field colorsField = worldMap.getClass().getDeclaredField("colors");

			colorsField.setAccessible(true);

			colorsField.set(worldMap, mapOutput);

		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e1) {

			dimension = -5;
			log.warning(e1.getMessage());
		}

		return (dimension != -5);
	}
	
	public boolean delete(){

		try {

			Field worldMapField = m.getClass().getDeclaredField("worldMap");

			worldMapField.setAccessible(true);

			Object worldMap = worldMapField.get(m);

			Field colorsField = worldMap.getClass().getDeclaredField("colors");

			colorsField.setAccessible(true);

			colorsField.set(worldMap, new byte[128*128]);

		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e1) {

			log.warning(e1.getMessage());
		}

		return true;
	}

	@Deprecated
	public boolean worldMapOverride(short inputId) {

		if (world == null)

			return false;

		AbstractMapArt a = Artiste.artList.get("default");

		if (a == null)

			a = Load.setupDefault();

		short outputId = ((Artwork) a).getMapId();

		log.info(((Short) inputId).toString());

		log.info(((Short) outputId).toString());

		MapView mapA = Bukkit.getMap(inputId);

		MapView mapB = Bukkit.getMap(outputId);

		if (mapA == null || mapB == null) {

			log.info("map delete error - 'default' map may be corrupted");
			return false;
		}

		try {
			Field worldMapField = mapA.getClass().getDeclaredField("worldMap");

			worldMapField.setAccessible(true);

			Object worldMapA = worldMapField.get(mapA);

			Field idField = worldMapA.getClass().getDeclaredField("id");

			idField.setAccessible(true);

			String s = "map_" + (((Short) outputId).toString());

			idField.set(mapA, s);

		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {

			e.printStackTrace();
			return false;
		}

		return true;
	}
}

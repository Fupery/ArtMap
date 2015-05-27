package me.Fupery.Artiste.IO;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

public class WorldMap {

	private MapView mapView;
	private net.minecraft.server.v1_8_R2.WorldMap worldMap;

	public WorldMap(MapView mapView) {

		this.setMapView(mapView);

		try {
			Field wm = mapView.getClass().getDeclaredField("worldMap");
			wm.setAccessible(true);
			worldMap = (net.minecraft.server.v1_8_R2.WorldMap) wm.get(mapView);
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			worldMap = null;
		}
	}

	public boolean setMap(byte[] mapOutput) {

		byte dimension;
		try {
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
			Bukkit.getLogger().warning(e1.getMessage());
		}
		return (dimension != -5);
	}

	public boolean delete() {
		
		try {
			Field colorsField = worldMap.getClass().getDeclaredField("colors");
			colorsField.setAccessible(true);
			colorsField.set(worldMap, new byte[128 * 128]);
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e1) {
			Bukkit.getLogger().warning(e1.getMessage());
		}
		return true;
	}

	public MapView getMapView() {
		return mapView;
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}
}

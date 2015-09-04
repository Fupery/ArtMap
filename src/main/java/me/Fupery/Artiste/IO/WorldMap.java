package me.Fupery.Artiste.IO;

import java.awt.*;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

public class WorldMap {

    private MapView mapView;
    private Object worldMap;

    public WorldMap(MapView mapView) {

        this.setMapView(mapView);

        try {
            Field wm = mapView.getClass().getDeclaredField("worldMap");
            wm.setAccessible(true);
            worldMap = wm.get(mapView);
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
                | IllegalArgumentException | IllegalAccessException e) {
            dimension = -5;
            Bukkit.getLogger().warning(e.getMessage());
        }
        return (dimension != -5);
    }

    public boolean setBlankMap() {

        byte[] mapOutput = new byte[128 * 128];
        for (int i = 0; i < mapOutput.length; i ++) {
            mapOutput[i] = MapPalette.matchColor(255, 255, 255);
        }

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
                | IllegalArgumentException | IllegalAccessException e) {
            dimension = -5;
            Bukkit.getLogger().warning(e.getMessage());
        }
        return (dimension != -5);
    }

    public boolean delete() {

        try {
            Field colorsField = worldMap.getClass().getDeclaredField("colors");
            colorsField.setAccessible(true);
            colorsField.set(worldMap, new byte[128 * 128]);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            Bukkit.getLogger().warning(e.getMessage());
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
package me.Fupery.Artiste.Tasks;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.Artwork;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

public class MapReflection {
	
	private Logger log;
	private MapView m;
	private int mapSize;
	private byte[] mapOutput;
	
	@SuppressWarnings("deprecation")
	public MapReflection(String title){
	
		this.log = Bukkit.getLogger();
		
		AbstractMapArt a = StartClass.artList.get(title);
	
		if(a != null && a instanceof Artwork){
			
			Artwork art = (Artwork) a;
			MapView m = Bukkit.getMap(art.getMapId());
			
			this.m = m;
			this.mapSize = art.getMapSize();
			this.mapOutput = convert(art.getMap());
			
			log.info(((Boolean)colorsOverride()).toString());
			
			log.info(((Boolean)dimensionOverride()).toString());
			
		}else log.warning("invalid title");
	}
	
	public boolean colorsOverride(){
		
	Field worldMap;
	Field field;
	byte[] colors;
	
	
	try {
		
		worldMap = m.getClass().getDeclaredField("worldMap");
		
		worldMap.setAccessible(true);
		
		Object o = worldMap.get(m);

		log.info(o.toString());
		
		field = o.getClass().getDeclaredField("colors");
		
		field.setAccessible(true);
		
		colors = (byte[]) field.get(o);

		log.info(colors.toString());
		
		field.set(o, mapOutput);
		
		log.info(o.getClass().getDeclaredField("colors").toString());
		
	} catch (NoSuchFieldException | SecurityException | 
			IllegalArgumentException | IllegalAccessException e1) {
		
		colors = null;
		log.warning(e1.getMessage());
		
		e1.printStackTrace();
	} 
	
	return (colors != null);
	}
	public boolean dimensionOverride(){
		
		Field worldMap;
		Field field;
		byte dimension;
		
		
		try {
			
			worldMap = m.getClass().getDeclaredField("worldMap");
			
			worldMap.setAccessible(true);
			
			Object o = worldMap.get(m);

			log.info(o.toString());
			
			field = o.getClass().getDeclaredField("map");
			
			field.setAccessible(true);
			
			dimension = field.getByte(o);

			log.info(((Byte) dimension).toString());
			
			field.setByte(o, (byte) 5);
			
			log.info(o.getClass().getDeclaredField("map").toString());
			
		} catch (NoSuchFieldException | SecurityException | 
				IllegalArgumentException | IllegalAccessException e1) {
			
			dimension = -5;
			log.warning(e1.getMessage());
			
			e1.printStackTrace();
		} 
		
		return (dimension != -5);
		}
		
	private byte[] convert(DyeColor[] map){
		
		mapOutput = new byte[128*128];
		
		if(mapSize == 0){
			mapSize = StartClass.config.getInt("mapSize");
			log.info("no mapSize found!");
		}

		int i = 0;
		int f = 128 / mapSize; 
		
		for (int x = 0; x < 128 ; x += f , i++) {
			
			for (int z = 0;  z < 128; z += f, i++ ) {
				
				DyeColor d = map[i];
				
				byte col = colourConvert(d);
				
				for (int ix = x; ix < (x + f); ix++) {
					
					for (int iz = z;  iz < (z + f); iz++) {
						
						mapOutput[ix + iz * 128] = col;
					
					}
				}
			}	
		} return mapOutput;
	}
	
	@SuppressWarnings("deprecation")
	private byte colourConvert(DyeColor color){
		log.info(color.name());
		Color c = color.getColor();
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		byte code = MapPalette.matchColor(r, g, b);
		return code;
	}
	
}

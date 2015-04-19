 package me.Fupery.test;

import java.io.File;
import java.io.IOException;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.MapArt.Artwork;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.map.MapPalette;

public class MapConvert {
	
	private DyeColor[] map;
	private byte[] mapOutput;
	private short id;
	private int mapSize;
	private CommandSender sender;
	private String name;
	
	public MapConvert(CommandSender sender, Artwork map){
		
		this.map = map.getMap();
		id = map.getMapId();
		name = "map_" + id;
		mapSize = map.getMapSize();
		this.sender = sender;
		
		//File f = mapOverride(setMap());
		
		//NBTTagCompound data = new NBTTagCompound("data");
		//data.put("width",     new NBTTagShort( (short) 128));
		//data.put("width",     new NBTTagShort( (short) 128));
		//data.put("height",    new NBTTagShort( (short) 128));
		//data.put("scale",     new NBTTagByte( (byte) 3));
		//data.put("dimension", new NBTTagByte( (byte) 2));
		//data.put("xCenter",   new NBTTagInt(448));
		//data.put("zCenter",   new NBTTagInt(448));
		//data.put("colors",    new NBTTagByteArray(mapOutput));
		
		//NBTContainerFileGZip c = new NBTContainerFileGZip(f);
		try {
			//data.writeGZip(new FileOutputStream(f));
			//c.writeTag(data);
			sender.sendMessage("Map Save Successful!");
		} catch (Exception e) {
			sender.sendMessage("Writing error " + e);
		}
		//sender.sendMessage(data.toString());
						  
	}
	
	private byte[] setMap(){
		
		mapOutput = new byte[128*128];
			
		int i = 0;
		int s = mapSize;
		int f = 128 / s; 
		
		//x & z represent position
		for (int x = 0; x < 128 ; x += f , i++) {
			
			for (int z = 0;  z < 128; z += f, i++ ) {
				
				DyeColor d = this.map[i];
				
				byte col = colourConvert(d);
				//this for loop scales smaller maps up -
				//ix + iz represent displacement from x&y
				//to fill gaps by interpolating pixels
				for (int ix = x; ix < (x + f); ix++) {
					
					for (int iz = z;  iz <= (z + f); iz++) {
						
					mapOutput[(ix + iz ) * s] = col;
					
					}
				}
			}
				
		}
		return mapOutput;
	}
	
	private File mapOverride(byte[] output){
		Canvas c = Canvas.findCanvas();
		String mapFile = name + ".dat";
		
		File wf = Bukkit.getWorld(c.worldname).getWorldFolder();		
		File dir = new File(wf.getAbsolutePath() + File.separator + "data");
		File f = null;
		sender.sendMessage("Searching for maps at " + dir.getAbsolutePath());
		for(String s : dir.list()){
			if(s.equalsIgnoreCase(mapFile)){
				sender.sendMessage("File " + s.toString() + " found. Overwriting file ...");
				f = new File(dir.getAbsolutePath(), s);
			}
		}
		if(f != null) f.delete();
		
		File file = new File(dir.getAbsolutePath(), mapFile);
		try {
			
			file.createNewFile();
			sender.sendMessage("Creating new save file ...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return file;
		
	}
	
	private byte colourConvert(DyeColor color){
		Color c = color.getColor();
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		@SuppressWarnings("deprecation")
		byte code = MapPalette.matchColor(r, g, b);
		return code;
	}
}

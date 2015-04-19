package me.Fupery.Artiste.MapArt;

import java.io.Serializable;
import java.util.UUID;

import me.Fupery.Artiste.Canvas;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class AbstractMapArt implements Serializable{

	private static final long serialVersionUID = 3778228012180388020L;
	protected UUID artist;
	protected int mapSize;
	protected DyeColor[] map;
	
	@SuppressWarnings("deprecation")
	public void save() {
		
		Canvas c = Canvas.findCanvas();
		if(c != null){
			
			Location l = c.getPos1().clone();
			int i = 0;
			
			for (int x = c.getPos1().getBlockX(); x <= c.getPos2().getBlockX(); x++, i++ ) {
				
				for (int z = c.getPos1().getBlockZ();  z <= c.getPos2().getBlockZ(); z++, i++ ) {
					
					l.setX(x); l.setZ(z);
					Block b = l.getBlock();
					
					if (b.getType() == Material.WOOL ){
						
						this.map[i] = DyeColor.getByData(b.getData() );
						
					} else this.map[i] = null;
				}
			}// end for loops
			
		}
	}
	
	@SuppressWarnings("deprecation")
	public void edit(){
		
		Canvas c = Canvas.findCanvas();
		
		if(c!= null){
			
			//sender.sendMessage(ChatColor.GOLD+"Loading "+ title);
			
			if (map != null) {
				
				Location l = c.getPos1().clone();
				
				int i = 0;
				for (int x = c.getPos1().getBlockX(); x <= c.getPos2().getBlockX(); x++, i++ ) {
					
					for (int z = c.getPos1().getBlockZ();  z <= c.getPos2().getBlockZ(); z++, i++ ) {
						
						l.setX(x); l.setZ(z);
						Block b = l.getBlock();
						
						if(map[i] != null){
							
							DyeColor d = map[i];
							
							if (b.getType() != Material.WOOL ) b.setType(Material.WOOL);
							if (b.getData() != d.getData()) b.setData(d.getData());
						}
					}
				}// end for loops
			}
		}
		
	}
	
	public UUID getArtist() {
		return artist;
	}
	public void setArtist(UUID artist) {
		this.artist = artist;
	}
	public DyeColor[] getMap() {
		return map;
	}
	public void setMap(DyeColor[] map) {
		this.map = map;
	}

	public int getMapSize() {
		return mapSize;
	}

	public void setMapSize(int mapSize) {
		this.mapSize = mapSize;
	}

	public boolean isPublicMap() {
		if(this instanceof PublicMap)
			return true;
		else return false;
	}
	public validMapType getType(){
		
		if(this instanceof PrivateMap){
			
			if(( (PrivateMap) this).isQueued())
				
				return validMapType.PRIVATE_QUEUED;
			
			else return validMapType.PRIVATE; 
		}
		else if(this instanceof PublicMap)
			
			return validMapType.PUBLIC;
		
		else if(this instanceof Buffer)
			
				return validMapType.BUFFER;
		return null;
	}
	public enum validMapType{
		PRIVATE, PRIVATE_QUEUED, PUBLIC, BUFFER, TEMPLATE
	}
}

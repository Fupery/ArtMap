package me.Fupery.Artiste.Tasks;


import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.AbstractMapArt;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**Renders to a MapView for the given artwork,
 * only Artwork objects may be rendered */
public class ArtRenderer extends MapRenderer {
	
	public int mapSize;
	public AbstractMapArt mapArt;
	public DyeColor[] map;
	public CommandSender sender;
	public boolean update = true;
	
	public ArtRenderer(AbstractMapArt mapArt, CommandSender sender) {
		this.sender = sender;
		this.map = mapArt.getMap();
		this.mapSize = mapArt.getMapSize();
		this.mapArt = mapArt;
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if(this.update){
			if(mapSize == 0)
				mapSize = StartClass.config.getInt("mapSize");

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
							
						canvas.setPixel(ix, iz, col);
						
						}
					}
				}	
			}this.update = false;
		}
	}
	private byte colourConvert(DyeColor color){
			Color c = color.getColor();
			int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
			@SuppressWarnings("deprecation")
			byte code = MapPalette.matchColor(r, g, b);
			return code;
	}
}


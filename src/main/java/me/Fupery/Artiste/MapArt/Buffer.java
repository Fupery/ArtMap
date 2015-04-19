package me.Fupery.Artiste.MapArt;


import java.io.Serializable;

import me.Fupery.Artiste.Canvas;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Buffer extends AbstractMapArt implements Serializable{

	private static final long serialVersionUID = 6593038229966932735L;

	public Buffer(CommandSender sender) {
		
		Canvas c = Canvas.findCanvas();
		artist = ( (Player) sender).getUniqueId();
		mapSize = c.getSize();
		map = new DyeColor[(mapSize*mapSize)+mapSize-1];
		
		save();
	}

}

package me.Fupery.Artiste.Event;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Canvas;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventUtils {

	// checks if player is in the canvas area
	public static boolean checkPos(Event evt) {

		Canvas c = Artiste.canvas;
		Block block;

		if (evt instanceof PlayerInteractEvent)

			block = ((PlayerInteractEvent) evt).getClickedBlock();

		else if (evt instanceof BlockFromToEvent)

			block = ((BlockFromToEvent) evt).getBlock();

		else
			block = null;

		if (c == null || block == null)

			return false;

		Location p1 = c.getPos1();
		Location p2 = c.getPos2();

		Location b = block.getLocation();

		if (b.getBlockX() >= p1.getBlockX() && b.getBlockX() <= p2.getBlockX()
				&& b.getBlockZ() >= p1.getBlockZ()
				&& b.getBlockZ() <= p2.getBlockZ()
				&& b.getBlockY() >= p1.getBlockY()
				&& b.getBlockY() <= p2.getBlockY())

			return true;

		else

			return false;

	}
}

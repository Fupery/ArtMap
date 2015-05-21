package me.Fupery.Artiste.IO;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Serializable version of Bukkit.Location represents 3d position in a world
 */
public class CanvasLocation implements Serializable {

	private static final long serialVersionUID = -4480459398117341329L;
	private int x, y, z;
	private UUID world;

	/**
	 * Converts Bukkit.Location object to serializable value
	 * 
	 * @param location
	 */
	public CanvasLocation(Location location) {

		x = location.getBlockX();
		y = location.getBlockY();
		z = location.getBlockZ();

		world = location.getWorld().getUID();
	}

	public Location getLocation() {
		Location l = new Location(Bukkit.getWorld(world), x, y, z);
		return l;
	}

	public World getWorld() {
		return Bukkit.getWorld(world);

	}

}

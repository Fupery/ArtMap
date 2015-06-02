package me.Fupery.Artiste.Command.CanvasCommands;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Command.Utils.Error;
import me.Fupery.Artiste.Tasks.SetCanvas;
import me.Fupery.Artiste.Artiste;

public class Define extends CanvasCommand {

	private double p1, p2, p3;
	private int size;
	private Location canvasPos, endPos;

	public void initialize() {

		usage = "define <x><y><z>";
		success = ChatColor.GOLD + "No Obstruction found";

		canvasRequired = false;
		adminRequired = true;
		playerRequired = true;

		minArgs = 4;
		maxArgs = 4;

		size = Artiste.config.getInt("canvasSize");
		if (size == 0)
			size = 64;
	}

	@Override
	public String conditions() {

		if (Artiste.canvas != null) {
			return error = Error.alreadyDef;
		}
		double pos1, pos2, pos3;

		try {
			pos1 = Double.parseDouble(args[1]);
			pos2 = Double.parseDouble(args[2]);
			pos3 = Double.parseDouble(args[3]);

		} catch (NumberFormatException e) {
			return error = Error.define;
		}
		p1 = pos1;
		p2 = pos2;
		p3 = pos3;

		if (!evalSize(size)) {
			error = "Invalid map size set in config!";
		}
		int mapSize = (size - 1);
		Player player = (Player) sender;
		World w = player.getWorld();
		WorldBorder wb = w.getWorldBorder();
		canvasPos = new Location(w, p1, p2, p3);

		if (wb != null) {
			if ((wb.getSize() - (canvasPos.getBlockX() + size)) <= 0
					|| (wb.getSize() - (canvasPos.getBlockZ() + size) <= 0)) {

				error = "Canvas obstructed by World Border";
			}
		}
		endPos = new Location(w, p1 + mapSize, p2, p3 + mapSize);
		Location l = canvasPos.clone();

		for (; l.getBlockX() <= (endPos.getBlockX()); l.add(1, 0, 0)) {

			for (; l.getBlockZ() <= endPos.getBlockZ(); l.add(0, 0, 1)) {

				if (w.getHighestBlockYAt(l) > canvasPos.getBlockY()

				&& w.getHighestBlockYAt(l) < (canvasPos.getBlockY() + 2)) {

					Material m = w.getHighestBlockAt(l).getType();

					if (m != Material.WOOL && m != Material.WATER) {
						Integer x = l.getBlockX(), y = l.getBlockY(), z = l
								.getBlockZ();
						error = String.format(
								"Canvas obstructed at %s, %s, %s", x, y, z);
					}
				}
			}
		}
		return error;
	}

	public boolean run() {
		Artiste.canvas = new Canvas(canvasPos, endPos, size);
		new SetCanvas(DyeColor.WHITE).runTask(Artiste.plugin);
		return true;
	}

	public static boolean evalSize(int mapSize) {
		switch (mapSize) {
		case 16:
			return true;
		case 32:
			return true;
		case 64:
			return true;
		case 128:
			return true;
		default:
			return false;
		}
	}

}

package me.Fupery.Artiste.Tasks;

import java.util.ArrayList;
import java.util.HashMap;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Canvas;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.Fupery.Artiste.Utils.NMSUtils.*;

/**
 * Resets the canvas to wool blocks via NMS
 */
public class SetCanvas extends BukkitRunnable {
	int mapSize;
	ArrayList<Chunk> update;
	HashMap<Player, Location> players;
	Canvas canvas;
	DyeColor[] map;
	DyeColor base;

	public SetCanvas(DyeColor[] map) {
		canvas = Artiste.canvas;
		mapSize = canvas.getSize();
		this.map = map;
		base = DyeColor.WHITE;
	}

	public SetCanvas(DyeColor colour) {
		canvas = Artiste.canvas;
		mapSize = canvas.getSize();
		this.map = null;
		base = colour;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {

		Canvas c = Artiste.canvas;
		update = new ArrayList<Chunk>();
		if (c != null && this.mapSize == c.getSize()) {
			Location l = c.getPos1().clone();
			int i = 0;

			for (int x = c.getPos1().getBlockX(); x <= c.getPos2().getBlockX(); x++, i++) {
				for (int z = c.getPos1().getBlockZ(); z <= c.getPos2()
						.getBlockZ(); z++, i++) {

					l.setX(x);
					l.setZ(z);
					Block b = l.getBlock();
					DyeColor d;
					d = (map != null && map[i] != null) ? map[i] : base;
					if (b.getType() != Material.WOOL) {
						setBlock(b, d.getData());
						Chunk ch = b.getChunk();
						if (!update.contains(ch)) {
							update.add(ch);
						}
					}
					if (b.getData() != d.getData())

						b.setData(d.getData());
				}
			}
		}
		if (!update.isEmpty()) {
			updatePlayers();
		}
	}

	public void updatePlayers() {

		int r = Bukkit.getServer().getViewDistance() + 4;
		players = new HashMap<Player, Location>();
		
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			players.put(p, p.getLocation());
		}

		if (players.size() == 0) {
			return;
		}
		for (Chunk c : update) {
			int cx = c.getX();
			int cz = c.getZ();
			for (Player p : players.keySet()) {
				int px, pz, pr, i, iab;

				px = players.get(p).getBlockX();
				pz = players.get(p).getBlockZ();

				i = ((px - cx) ^ 2) + ((pz - cz) ^ 2);
				iab = (i < 0) ? -i : i;
				pr = ((int) (Math.floor(Math.sqrt(iab))));
				if (pr <= r) {
					queueChunk(p, cx, cz);
				}
			}
		}
	}
}

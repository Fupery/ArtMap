package me.Fupery.Artiste.Event;

import java.util.ArrayList;
import java.util.UUID;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Artiste;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

/** Handles player interactions with the canvas */
public class PlayerPaintListener implements Listener {

	PlayerPaintEvent event;
	Canvas c;

	@EventHandler
	public void onPaint(PlayerPaintEvent event) {
		this.event = event;
		c = Artiste.canvas;

		if (!evalOwner())
			return;

		switch (event.getItem().getType()) {

		case INK_SACK:

			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)

				draw();
			return;

		case WATER_BUCKET:

			if (event.getAction() == Action.RIGHT_CLICK_AIR) {

				splash();
				Bukkit.getLogger().info("bucket");
			}
			return;

		default:
			return;
		}
	}

	private boolean evalOwner() {

		Player p = event.getPlayer();
		ArrayList<UUID> mem = c.getMembers();

		if (p == c.getOwner())
			return true;

		for (UUID m : mem)
			if (p == Bukkit.getPlayer(m))
				return true;

		return false;
	}

	// Sets the right-clicked block to the color of the dye used
	@SuppressWarnings("deprecation")
	private void draw() {

		Block b = event.getBlock();
		ItemStack i = event.getItem();

		if (b.getType() == Material.WOOL) {

			if (b.getData() != (15 - i.getDurability()))

				b.setData((byte) (15 - i.getDurability()));
		}
	}

	@SuppressWarnings("deprecation")
	private void splash() {

		Block b = event.getBlock();
		byte d = b.getData();

		for (int f = 0; f < 7; f++)

			new Splash(f, b, d).runTaskLater(Artiste.plugin, (5 * f));

	}
}

class Splash extends BukkitRunnable {

	int f, cx, cz;
	Block b;
	byte d;

	Splash(int f, Block b, byte d) {

		this.f = f;
		this.d = d;
		this.b = b;

		Location l = b.getLocation();

		cx = l.getBlockX();
		cz = l.getBlockZ();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {

		Location l = b.getLocation();

		for (int x = 0; x <= cx + f; x++)

			for (int z = 0; z <= cz + f; z++) {

				l.add(x, 0, z);

				if (abs(x) + abs(z) <= f)

					l.getBlock().setData(d);
			}

	}

	int abs(int i) {
		return i = (i < 0) ? -i : i;
	}
}

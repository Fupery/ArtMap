package me.Fupery.Artiste.Tasks;

import java.util.ArrayList;
import java.util.UUID;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Artiste;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

/** Handles player interactions with the canvas */
public class EasyDraw implements Listener {

	@EventHandler
	public void onRightClick(PlayerInteractEvent evt) {

		Canvas c = Artiste.canvas;

		if (checkPos(evt, c) && checkItem(evt)) {

			if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {

				if (evalOwner(evt, c)) {

					setWool(evt, c);
				}
			}
		}
	}

	// Sets the right-clicked block to the color of the dye used
	@SuppressWarnings("deprecation")
	private static void setWool(PlayerInteractEvent evt, Canvas c) {

		Block b = evt.getClickedBlock();
		ItemStack i = evt.getItem();

		if (b.getType() == Material.WOOL) {

			if (b.getData() != (15 - i.getDurability()))

				b.setData((byte) (15 - i.getDurability()));
		}
	}

	// Checks if Player is in bounds of the canvas
	private static boolean checkPos(PlayerInteractEvent evt, Canvas c) {

		if (c != null && evt.getClickedBlock() != null) {

			Location p1 = c.getPos1();
			Location p2 = c.getPos2();

			Location b = evt.getClickedBlock().getLocation();

			if (b.getBlockX() >= p1.getBlockX()
					&& b.getBlockX() <= p2.getBlockX()
					&& b.getBlockZ() >= p1.getBlockZ()
					&& b.getBlockZ() <= p2.getBlockZ()
					&& b.getBlockY() >= p1.getBlockY()
					&& b.getBlockY() <= p2.getBlockY()) {

				return true;
			} else
				return false;
		}
		return false;
	}

	private static boolean checkItem(PlayerInteractEvent evt) {

		if (evt.getItem() != null) {

			if (evt.getItem().getType() == Material.INK_SACK)

				return true;

		}
		return false;
	}

	private static boolean evalOwner(PlayerInteractEvent evt, Canvas c) {

		Player p = evt.getPlayer();
		ArrayList<UUID> mem = c.getMembers();

		if (p == c.getOwner())
			return true;

		for (UUID m : mem)
			if (p == Bukkit.getPlayer(m))
				return true;

		return false;
	}

}

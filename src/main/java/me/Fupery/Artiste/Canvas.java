package me.Fupery.Artiste;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import me.Fupery.Artiste.IO.CanvasLocation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents 3d position of the ArtMap canvas, contains static methods to
 * interact with the canvas in-game.
 */
public class Canvas implements Serializable {

	private static final long serialVersionUID = -1867329677480940178L;

	private UUID owner;
	private ArrayList<UUID> member;

	private int size;
	private boolean canClaim;
	private boolean cooloff;

	private CanvasLocation pos1;
	private CanvasLocation pos2;
	public String worldname;

	public Canvas(Location position1, Location position2, int size) {

		owner = null;
		member = new ArrayList<UUID>();
		this.size = size;
		canClaim = true;
		cooloff = false;
		setPos1(position1);
		setPos2(position2);

		worldname = position1.getWorld().getName();
	}

	public void removeCanvas(CommandSender sender) {

		owner = null;
		member.clear();
		setPos1(null);
		setPos2(null);
		StartClass.canvas = null;

		sender.sendMessage(ChatColor.DARK_AQUA + "Canvas removed successfully!");
	}

	public void clear(CommandSender sender) {

		owner = null;
		this.member.clear();
		this.cooloff = false;
		this.reset(sender, DyeColor.WHITE);
	}

	// TODO - implement task scheduling

	@SuppressWarnings("deprecation")
	public void reset(CommandSender sender, DyeColor colour) {

		Location f = this.getPos2().clone();

		Location l = this.getPos1().clone();

		for (int x = this.getPos1().getBlockX(); x <= (f.getBlockX()); x++) {

			for (int z = this.getPos1().getBlockZ(); z <= (f.getBlockZ()); z++) {

				l.setX(x);
				l.setZ(z);

				Block b = l.getBlock();

				if (b.getType() != Material.WOOL)

					b.setType(Material.WOOL);

				if (b.getData() != colour.getData())

					b.setData(colour.getData());
			}
		}
	}

	public Location getPos1() {
		Location l = this.pos1.getLocation();
		return l;
	}

	public void setPos1(Location pos1) {
		this.pos1 = (pos1 == null) ? null : new CanvasLocation(pos1);
	}

	public Location getPos2() {
		Location l = this.pos2.getLocation();
		return l;
	}

	public void setPos2(Location pos2) {
		this.pos2 = (pos2 == null) ? null : new CanvasLocation(pos2);
	}

	public Player getOwner() {
		Player p = Bukkit.getPlayer(this.owner);
		return p;
	}

	public void setOwner(Player owner) {
		UUID id;
		id = (owner == null) ? null : owner.getUniqueId();
		this.owner = id;
	}

	public int getSize() {
		return size;
	}

	public ArrayList<UUID> getMembers() {
		return this.member;
	}

	public void addMember(Player player) {
		member.add(player.getUniqueId());
	}

	public void delMember(Player player) {
		member.remove(player.getUniqueId());
	}

	public boolean canClaim() {
		return canClaim;
	}

	public void setCanClaim(boolean canClaim) {
		this.canClaim = canClaim;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public boolean isCoolingOff() {
		return cooloff;
	}

	public void startCoolOff() {

		int delay = StartClass.plugin.getConfig().getInt("coolOffTime");

		if (delay > 0) {

			ResetTimer t = new ResetTimer();

			t.runTaskLater(StartClass.plugin, delay * 60 * 20);

			cooloff = true;
		} else
			cooloff = false;
	}

	class ResetTimer extends BukkitRunnable {

		@Override
		public void run() {

			StartClass.canvas.cooloff = false;
		}
	}
}

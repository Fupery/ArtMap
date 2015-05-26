package me.Fupery.Artiste;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import me.Fupery.Artiste.IO.CanvasLocation;
import me.Fupery.Artiste.Tasks.SetCanvas;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
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
	private boolean canClaim, cooloff;

	private CanvasLocation pos1, pos2;
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
		Artiste.canvas = null;
	}

	public void clear(CommandSender sender) {

		owner = null;
		this.member.clear();
		this.cooloff = false;
		new SetCanvas(DyeColor.WHITE).run();
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

		int delay = Artiste.plugin.getConfig().getInt("coolOffTime");

		if (delay > 0) {

			ResetTimer t = new ResetTimer();

			t.runTaskLater(Artiste.plugin, delay * 60 * 20);

			cooloff = true;
		} else
			cooloff = false;
	}

	class ResetTimer extends BukkitRunnable {

		@Override
		public void run() {

			Artiste.canvas.cooloff = false;
		}
	}
}

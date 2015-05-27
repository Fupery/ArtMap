package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.Buffer;
import me.Fupery.Artiste.Command.Utils.Error;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Claim extends CanvasCommand {

	// TODO - move TimerA,B to their own task class

	public void initialize() {

		playerRequired = true;
		artistRequired = true;
		usage = "claim";
	}

	public boolean run() {

		int claimTime = Artiste.config.getInt("claimTime");
		Player player = (Player) sender;
		canvas.setOwner(player);
		Buffer m = artist.getBuffer();

		if (m != null) {
			m.edit(m.getMap());
		}
		if (claimTime > 0) {
			success = ChatColor.GOLD + "Canvas claimed for " + ChatColor.YELLOW
					+ claimTime + ChatColor.GOLD + " minutes!";
			int ticks = claimTime * 60 * 20;
			TimerA timer = new TimerA(sender);
			Artiste.claimTimer = timer;
			timer.runTaskLater(Artiste.plugin, ticks);

			if (claimTime > 5) {
				int warning = ticks - (5 * 60 * 20);
				((TimerA) Artiste.claimTimer).setNotify(new TimerB()
						.runTaskLater(Artiste.plugin, warning));
			}
		} else {
			success = ChatColor.GOLD + "Canvas claimed!";
		}
		return true;
	}

	@Override
	public String conditions() {

		Player p = canvas.getOwner();

		if (p == (Player) sender) {
			error = "You have already claimed the canvas!";
		}
		if (p != (Player) sender && p != null) {
			error = "Someone else is using the canvas!";
		}
		if (sender instanceof Player
				&& !((Player) sender).hasPermission("artiste.playerTier1")) {
			error = Error.noPermission;
		}
		return error;
	}

	class TimerA extends BukkitRunnable {

		private CommandSender sender;
		private BukkitTask notify;

		public TimerA(CommandSender sender) {
			this.sender = sender;
		}

		@Override
		public void run() {
			sender.sendMessage(ChatColor.AQUA + "[ArtMap]" + ChatColor.GOLD
					+ "Your time using the canvas is up!");
			Unclaim.unclaim();
		}

		@Override
		public void cancel() {
			notify.cancel();
			super.cancel();
		}
		public void setNotify(BukkitTask bukkitTask) {
			this.notify = bukkitTask;
		}
	}

	class TimerB extends BukkitRunnable {

		@Override
		public void run() {
			sender.sendMessage(ChatColor.AQUA + "[ArtMap] " + ChatColor.GOLD
					+ "5 Minutes remaining with the canvas!");
		}
	}
}

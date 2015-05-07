package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Buffer;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Claim extends CanvasCommand {

	// TODO - move TimerA,B to their own task class

	public Claim(CommandListener listener) {

		super(listener);
		playerRequired = true;
		artistRequired = true;
		usage = "claim";
	}

	public boolean run() {

		int claimTime = StartClass.config.getInt("claimTime");

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
			StartClass.claimTimer = timer;
			timer.runTaskLater(StartClass.plugin, ticks);

			if (claimTime > 5) {

				int warning = ticks - (5 * 60 * 20);

				((TimerA) StartClass.claimTimer).setNotify(new TimerB()
						.runTaskLater(StartClass.plugin, warning));

			}

		} else
			success = ChatColor.GOLD + "Canvas claimed!";

		return true;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)

			return error;

		Player p = canvas.getOwner();

		if (p == (Player) sender) {

			error = "You have already claimed the canvas!";
		}
		if (p != (Player) sender && p != null)

			error = "Someone else is using the canvas!";

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

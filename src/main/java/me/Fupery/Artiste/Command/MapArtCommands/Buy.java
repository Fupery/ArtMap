package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Artwork;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Buy extends MapArtCommand {
	double cost;
	Economy econ;

	public Buy(CommandListener listener) {
		super(listener);
		playerRequired = true;
		usage = "buy <title>";
		this.cost = StartClass.config.getDouble("artworkPrice");
		this.econ = StartClass.econ;
	}

	protected boolean run() {

		if (art instanceof Artwork) {

			if (checkCurrency()) {
				((Artwork) art).buy(sender);
			}
		}
		return true;
	}

	private boolean checkCurrency() {

		Player player = (Player) sender;

		if (cost <= 0) {

			return true;
		}

		if (!StartClass.economyOn) {

			if (player.getInventory().contains(Material.EMERALD, 5)) {

				player.getInventory().removeItem(
						new ItemStack(Material.EMERALD, 5));

				sender.sendMessage(ChatColor.AQUA + "[Artiste] "
						+ ChatColor.GOLD + "Purchased for 5 emeralds!");

				return true;

			} else {

				sender.sendMessage(ChatColor.AQUA + "[Artiste] "
						+ ChatColor.GOLD + "It costs 5 emeralds to buy a map.");

				return false;
			}

		} else {

			EconomyResponse r = econ.withdrawPlayer(player, cost);

			if (r.transactionSuccess()) {

				sender.sendMessage(ChatColor.AQUA + "[Artiste] "
						+ ChatColor.GOLD + "Purchased for " + r.amount);

				return true;

			} else {

				sender.sendMessage(ChatColor.AQUA + "[Artiste] "
						+ ChatColor.RED
						+ "You don't have enough money to buy this artwork!");

				return false;
			}
		}
	}

}

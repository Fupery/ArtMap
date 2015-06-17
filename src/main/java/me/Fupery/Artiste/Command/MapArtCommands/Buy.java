package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.MapArt.ValidMapType;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Buy extends MapArtCommand {
	double cost;
	Economy econ;

	public void initialize() {

		playerRequired = true;
		usage = "buy <title>";

		this.cost = Artiste.config.getDouble("artworkPrice");
		this.econ = Artiste.econ;

		if (args.length == 2) {

			AbstractMapArt a = Artiste.artList.get(args[1]);

			if (a != null)
				authorRequired = !(a.getType() == ValidMapType.PUBLIC);
		}
	}

	public boolean run() {

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

		if (!Artiste.economyOn) {

			if (player.getInventory().contains(Material.EMERALD, 5)) {

				player.getInventory().removeItem(
						new ItemStack(Material.EMERALD, 5));

				success = ChatColor.GOLD + "Purchased for 5 emeralds!";

				return true;

			} else {

				success = ChatColor.GOLD + "It costs 5 emeralds to buy a map.";

				return false;
			}

		} else {

			EconomyResponse r = econ.withdrawPlayer(player, cost);

			if (r.transactionSuccess()) {

				success = ChatColor.GOLD + "Purchased for " + r.amount;

				return true;

			} else {

				success = ChatColor.RED
						+ "You don't have enough money to buy this artwork!";

				return false;
			}
		}
	}
}

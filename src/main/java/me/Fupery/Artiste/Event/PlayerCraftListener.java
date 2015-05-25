package me.Fupery.Artiste.Event;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import me.Fupery.Artiste.Command.Utils.Error;

/** Disallows players from copying Artiste maps in the crafting table */
public class PlayerCraftListener implements Listener {

	@EventHandler
	public void onPlayerCraftEvent(CraftItemEvent event) {

		ItemStack items[];

		items = event.getInventory().getMatrix();

		for (ItemStack i : items)

			if (i != null
					&& i.getItemMeta() != null
					&& i.getItemMeta().getLore() != null
					&& i.getItemMeta().getLore().get(0)
							.contains(ChatColor.GOLD + "by")) {

				event.setResult(Event.Result.DENY);

				for (HumanEntity e : event.getViewers())

					e.sendMessage(Error.noCraft);

				return;
			}
	}
}
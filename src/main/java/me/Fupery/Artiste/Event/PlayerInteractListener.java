package me.Fupery.Artiste.Event;

import me.Fupery.Artiste.Artiste;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.Fupery.Artiste.Event.EventUtils.checkPos;

public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (Artiste.canvas != null && checkPos(event)
				&& event.getItem() != null
				&& event.getClickedBlock().getType() == Material.WOOL)

			Bukkit.getServer()
					.getPluginManager()
					.callEvent(
							new PlayerPaintEvent(event.getPlayer(), event
									.getAction(), event.getClickedBlock(),
									event.getItem()));

	}
}

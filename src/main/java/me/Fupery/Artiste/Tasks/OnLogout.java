package me.Fupery.Artiste.Tasks;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Command.CanvasCommands.Unclaim;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnLogout implements Listener {

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		
		Canvas c = Artiste.canvas;
		
		if (c != null) {

			Player player = event.getPlayer();

			if (c.getOwner() != null && c.getOwner() == player)

				Unclaim.unclaim();
		}
	}
}

package me.Fupery.Artiste.Event;

import java.util.UUID;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Command.CanvasCommands.Unclaim;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLogoutListener implements Listener {

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Canvas c = Artiste.canvas;
		if (c != null) {
			Player player = event.getPlayer();
			if (c.getOwner() != null && c.getOwner() == player) {
				Unclaim.unclaim();
			}
			if (c.getMembers() != null && isMember(player)) {
				c.delMember(player);
			}
		}
	}

	private boolean isMember(Player p) {
		for (UUID id : Artiste.canvas.getMembers()) {
			if (p.getUniqueId().compareTo(id) == 0) {
				return true;
			}
		}
		return false;
	}
}

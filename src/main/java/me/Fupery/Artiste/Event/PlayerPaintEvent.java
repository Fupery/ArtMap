package me.Fupery.Artiste.Event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public final class PlayerPaintEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Action action;
	private ItemStack item;
	private Location location;
	private Block block;

	public PlayerPaintEvent(Player player, Action action, Block block,
			ItemStack item) {

		this.player = player;
		this.action = action;
		this.item = item;
		this.block = block;
		this.location = block.getLocation();

	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public ItemStack getItem() {
		return item;
	}

	public Player getPlayer() {
		return player;
	}

	public Action getAction() {
		return action;
	}

	public Location getLocation() {
		return location;
	}

	public Block getBlock() {
		return block;
	}
}
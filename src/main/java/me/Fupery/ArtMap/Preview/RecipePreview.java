package me.Fupery.ArtMap.Preview;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class RecipePreview extends TimedPreview {

    private final Inventory preview;

    public RecipePreview(Inventory preview) {
        this.preview = preview;
    }

    @Override
    public boolean start(Player player) {
        super.start(player);
        player.openInventory(preview);
        return true;
    }

    @Override
    public boolean end(Player player) {
        super.end(player);
        preview.clear();
        player.closeInventory();
        return true;
    }

    @Override
    public boolean isEventAllowed(UUID player, Event event) {
        return false;
    }
}

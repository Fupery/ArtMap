package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Preview extends BukkitRunnable {
    protected ArtMap plugin;
    protected Player player;

    Preview(ArtMap plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public static void artwork(ArtMap plugin, Player player, MapArt art) {
        checkCurrentPreviews(plugin, player);
        ItemStack item = art.getMapItem();
        Preview preview = new ItemPreview(plugin, player, item);
        preview.runTaskLaterAsynchronously(plugin, 300);
        player.setItemInHand(item);
        plugin.getPreviewing().put(player, preview);
    }

    public static void inventory(ArtMap plugin, Player player, Inventory previewInventory) {
        checkCurrentPreviews(plugin, player);
        Preview preview = new RecipePreview(plugin, player, previewInventory);
        preview.runTaskLaterAsynchronously(plugin, 300);
        player.openInventory(previewInventory);
        plugin.getPreviewing().put(player, preview);
    }

    public static void stop(ArtMap plugin, Player player) {

        if (plugin.getPreviewing().containsKey(player)) {
            plugin.getPreviewing().get(player).stopPreviewing();
        }
    }

    private static void checkCurrentPreviews(ArtMap plugin, Player player) {
        if (plugin.getPreviewing().containsKey(player)) {
            plugin.getPreviewing().get(player).stopPreviewing();
        }
    }

    public void stopPreviewing() {
        cancel();
        run();
        plugin.getPreviewing().remove(player);
    }
}

class ItemPreview extends Preview {

    private ItemStack preview;

    ItemPreview(ArtMap plugin, Player player, ItemStack preview) {
        super(plugin, player);
        this.preview = preview;
    }

    @Override
    public void run() {
        player.playSound(player.getLocation(), Sound.CLICK, (float) 0.5, -2);
        player.getInventory().removeItem(preview);
        plugin.getPreviewing().remove(player);
    }
}

class RecipePreview extends Preview {

    Inventory recipeWindow;

    RecipePreview(ArtMap plugin, Player player, Inventory recipeWindow) {
        super(plugin, player);
        this.recipeWindow = recipeWindow;
    }

    @Override
    public void run() {
        recipeWindow.clear();
        player.closeInventory();
    }
}

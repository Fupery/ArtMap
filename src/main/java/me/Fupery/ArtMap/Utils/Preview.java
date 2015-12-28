package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

public abstract class Preview extends BukkitRunnable {
    public static final ConcurrentHashMap<Player, Preview> previewing = new ConcurrentHashMap<>();
    final Player player;

    Preview(Player player) {
        this.player = player;
    }

    public static void artwork(ArtMap plugin, Player player, MapArt art) {
        checkCurrentPreviews(player);
        ItemStack item = art.getMapItem();
        Preview preview = new ItemPreview(player, item);
        preview.runTaskLaterAsynchronously(plugin, 300);
        player.setItemInHand(item);
        Preview.previewing.put(player, preview);
    }

    public static void inventory(ArtMap plugin, Player player, Inventory previewInventory) {
        checkCurrentPreviews(player);
        Preview preview = new RecipePreview(player, previewInventory);
        preview.runTaskLaterAsynchronously(plugin, 300);
        player.openInventory(previewInventory);
        Preview.previewing.put(player, preview);
    }

    public static void stop(Player player) {

        if (Preview.previewing.containsKey(player)) {
            Preview.previewing.get(player).stopPreviewing();
        }
    }

    private static void checkCurrentPreviews(Player player) {
        if (Preview.previewing.containsKey(player)) {
            Preview.previewing.get(player).stopPreviewing();
        }
    }

    public void stopPreviewing() {
        cancel();
        run();
        Preview.previewing.remove(player);
    }
}

class ItemPreview extends Preview {

    private final ItemStack preview;

    ItemPreview(Player player, ItemStack preview) {
        super(player);
        this.preview = preview;
    }

    @Override
    public void run() {
        player.playSound(player.getLocation(), Sound.CLICK, (float) 0.5, -2);
        player.getInventory().removeItem(preview);
        Preview.previewing.remove(player);
    }
}

class RecipePreview extends Preview {

    private final Inventory recipeWindow;

    RecipePreview(Player player, Inventory recipeWindow) {
        super(player);
        this.recipeWindow = recipeWindow;
    }

    @Override
    public void run() {
        recipeWindow.clear();
        player.closeInventory();
    }
}

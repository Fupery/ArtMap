package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Preview extends BukkitRunnable {
    final Player player;

    Preview(Player player) {
        this.player = player;
    }

    public static void artwork(Player player, MapArt art) {
        checkCurrentPreviews(player);
        ItemStack item = art.getMapItem();
        Preview preview = new ItemPreview(player, item);
        preview.runTaskLaterAsynchronously(ArtMap.plugin(), 300);
        player.setItemInHand(item);
        ArtMap.previewing.put(player, preview);
    }

    public static void inventory(Player player, Inventory previewInventory) {
        checkCurrentPreviews(player);
        Preview preview = new RecipePreview(player, previewInventory);
        preview.runTaskLaterAsynchronously(ArtMap.plugin(), 300);
        player.openInventory(previewInventory);
        ArtMap.previewing.put(player, preview);
    }

    public static void stop(Player player) {

        if (ArtMap.previewing.containsKey(player)) {
            ArtMap.previewing.get(player).stopPreviewing();
        }
    }

    private static void checkCurrentPreviews(Player player) {
        if (ArtMap.previewing.containsKey(player)) {
            ArtMap.previewing.get(player).stopPreviewing();
        }
    }

    public void stopPreviewing() {
        cancel();
        run();
        ArtMap.previewing.remove(player);
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
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, -2);
        player.getInventory().removeItem(preview);
        ArtMap.previewing.remove(player);
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

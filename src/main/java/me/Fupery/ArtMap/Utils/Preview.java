package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Preview extends BukkitRunnable {
    final Player player;

    Preview(Player player) {
        this.player = player;
    }
    // TODO: 25/07/2016 maybe chill on the statics
    // TODO: 25/07/2016 responsiveness

    public static void artwork(Player player, MapArt art) {
        checkCurrentPreviews(player);
        ItemStack item = art.getMapItem();
        Preview preview = new ItemPreview(player, item);
        preview.runTaskLaterAsynchronously(ArtMap.instance(), 300);
        player.setItemInHand(item);
        ArtMap.getPreviewing().put(player, preview);
    }

    public static void inventory(Player player, Inventory previewInventory) {
        checkCurrentPreviews(player);
        Preview preview = new RecipePreview(player, previewInventory);
        preview.runTaskLaterAsynchronously(ArtMap.instance(), 300);
        player.openInventory(previewInventory);
        ArtMap.getPreviewing().put(player, preview);
    }

    public static void stop(Player player) {

        if (ArtMap.getPreviewing().containsKey(player)) {
            ArtMap.getPreviewing().get(player).stopPreviewing();
        }
    }

    private static void checkCurrentPreviews(Player player) {
        if (ArtMap.getPreviewing().containsKey(player)) {
            ArtMap.getPreviewing().get(player).stopPreviewing();
        }
    }

    public void stopPreviewing() {
        cancel();
        run();
        ArtMap.getPreviewing().remove(player);
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
        if (player.getItemOnCursor().equals(preview)) player.setItemOnCursor(null);
        player.getInventory().removeItem(preview);// TODO: 5/08/2016
        ArtMap.getPreviewing().remove(player);
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

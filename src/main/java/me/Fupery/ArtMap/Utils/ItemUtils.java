package me.Fupery.ArtMap.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    public static void giveItem(Player player, ItemStack item) {
        ItemStack leftOver = player.getInventory().addItem(item).get(0);
        if (leftOver != null && leftOver.getAmount() > 0)
            player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
    }
}

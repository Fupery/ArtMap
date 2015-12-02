package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandPreview extends ArtMapCommand {

    CommandPreview(ArtMap plugin) {
        super(null, "/artmap preview <title>", false);
        this.plugin = plugin;
    }

    public static boolean previewArtwork(ArtMap plugin, Player player, MapArt art) {

        if (player.hasPermission("artmap.admin")) {
            ItemStack currentItem = player.getItemInHand();
            player.setItemInHand(art.getMapItem());

            if (currentItem != null) {
                ItemStack leftOver = player.getInventory().addItem(currentItem).get(0);

                if (leftOver != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                }
            }

        } else {

            if (plugin.isPreviewing(player)) {
                plugin.getPreviewing().get(player).stopPreviewing();
            }

            if (player.getItemInHand().getType() != Material.AIR) {
                return false;
            }

            Preview.artwork(plugin, player, art);
        }
        return true;
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        Player player = (Player) sender;

        MapArt art = MapArt.getArtwork(plugin, args[1]);

        if (art == null) {
            msg.message = String.format(ArtMap.Lang.MAP_NOT_FOUND.message(), args[1]);
            return false;
        }

        if (!previewArtwork(plugin, player, art)) {
            msg.message = ArtMap.Lang.EMPTY_HAND_PREVIEW.message();
            return false;
        }
        msg.message = String.format(ArtMap.Lang.PREVIEWING.message(), args[1]);
        return true;
    }
}

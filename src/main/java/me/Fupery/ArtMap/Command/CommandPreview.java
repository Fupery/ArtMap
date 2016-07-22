package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandPreview extends Command {

    CommandPreview() {
        super(null, "/artmap preview <title>", false);
    }

    public static boolean previewArtwork(final Player player, final MapArt art) {

        if (player.hasPermission("artmap.admin")) {
            ArtMap.getTaskManager().SYNC.run(() -> {
                ItemStack currentItem = player.getItemInHand();
                player.setItemInHand(art.getMapItem());

                if (currentItem != null) {
                    ItemStack leftOver = player.getInventory().addItem(currentItem).get(0);

                    if (leftOver != null) {
                        player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                    }
                }
            });

        } else {

            if (ArtMap.getPreviewing().containsKey(player)) {
                ArtMap.getPreviewing().get(player).stopPreviewing();
            }

            if (player.getItemInHand().getType() != Material.AIR) {
                return false;
            }

            Preview.artwork(player, art);
        }
        return true;
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        Player player = (Player) sender;

        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);

        if (art == null) {
            msg.message = String.format(Lang.MAP_NOT_FOUND.message(), args[1]);
            return;
        }

        if (!previewArtwork(player, art)) {
            msg.message = Lang.EMPTY_HAND_PREVIEW.message();
            return;
        }
        msg.message = String.format(Lang.PREVIEWING.message(), args[1]);
    }
}

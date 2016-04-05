package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselPart;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.LocationTag;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSave extends Command {

    CommandSave() {
        super("artmap.artist", "/artmap save <title>", false);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        final String title = args[1];

        final Player player = (Player) sender;

        if (!new TitleFilter(title).check()) {
            msg.message = Lang.BAD_TITLE.message();
            return;
        }

        MapArt art = ArtMap.getArtDatabase().getArtwork(title);

        if (art != null) {
            msg.message = Lang.TITLE_USED.message();
            return;
        }

        if (!ArtMap.artistHandler.containsPlayer(player)) {
            player.sendMessage(Lang.NOT_RIDING_EASEL.message());
            return;
        }


        ArtMap.runTask(new Runnable() {
            @Override
            public void run() {
                Easel easel = null;

                Entity seat = player.getVehicle();

                if (seat != null) {

                    if (seat.hasMetadata("easel")) {
                        String tag = seat.getMetadata("easel").get(0).asString();
                        Location location = LocationTag.getLocation(seat.getWorld(), tag);

                        easel = Easel.getEasel(location, EaselPart.SIGN);
                    }
                }

                if (easel == null) {
                    player.sendMessage(Lang.NOT_RIDING_EASEL.message());
                    return;
                }
                ArtMap.artistHandler.removePlayer(player);

                MapArt art = new MapArt(easel.getItem().getDurability(), title, player);
                art.saveArtwork();

                easel.getFrame().setItem(new ItemStack(Material.AIR));
                ItemStack leftOver = player.getInventory().addItem(art.getMapItem()).get(0);

                if (leftOver != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                }
                SoundCompat.ENTITY_EXPERIENCE_ORB_TOUCH.play(player, 1, 0);
                player.sendMessage(String.format(Lang.SAVE_SUCCESS.message(), title));
            }
        });
    }
}

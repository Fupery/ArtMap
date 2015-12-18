package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.ArtMap.Listeners.EaselInteractListener;
import me.Fupery.ArtMap.Utils.LocationTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSave extends ArtMapCommand {

    CommandSave(ArtMap plugin) {
        super("artmap.artist", "/artmap save <title>", false);
        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        final String title = args[1];

        final Player player = (Player) sender;

        if (!new TitleFilter(plugin, title).check()) {
            msg.message = ArtMap.Lang.BAD_TITLE.message();
            return false;
        }

        MapArt art = MapArt.getArtwork(plugin, title);

        if (art != null) {
            msg.message = ArtMap.Lang.TITLE_USED.message();
            return false;
        }

        if (!plugin.getArtistHandler().containsPlayer(player)) {
            player.sendMessage(ArtMap.Lang.NOT_RIDING_EASEL.message());
            return false;
        }


        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                Easel easel = null;

                Entity seat = player.getVehicle();

                if (seat != null) {

                    if (seat.hasMetadata("easel")) {
                        String tag = seat.getMetadata("easel").get(0).asString();
                        Location location = LocationTag.getLocation(seat.getWorld(), tag);

                        easel = EaselInteractListener.easels.get(location);
                    }
                }

                if (easel == null) {
                    player.sendMessage(ArtMap.Lang.NOT_RIDING_EASEL.message());
                    return;
                }

                MapArt art = new MapArt(easel.getItem().getDurability(),
                        title, player);

                //Makes sure that frame is empty before saving
                for (int i = 0; i < 3; i++) {

                    easel.getFrame().setItem(new ItemStack(Material.AIR));

                    if (!easel.hasItem()) {
                        art.saveArtwork(plugin);
                        easel.getFrame().setItem(new ItemStack(Material.AIR));
                        plugin.getArtistHandler().removePlayer(player);
                        ItemStack leftOver = player.getInventory().addItem(art.getMapItem()).get(0);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 0);

                        if (leftOver != null) {
                            player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                        }
                        player.sendMessage(String.format(ArtMap.Lang.SAVE_SUCCESS.message(), title));
                        return;
                    }
                }
                player.sendMessage(ArtMap.Lang.UNKNOWN_ERROR.message());
            }
        });
        return true;
    }
}

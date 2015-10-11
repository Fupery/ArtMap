package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.*;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.Recipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;

import static me.Fupery.ArtMap.Utils.Formatting.*;

public class EaselInteractListener implements Listener {

    ArtMap plugin;

    public EaselInteractListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEaselInteract(EaselEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("artmap.artist")) {

            Easel easel = event.getEasel();

            if (!easel.isPainting()) {

                switch (event.getClick()) {

                    case LEFT_CLICK:

                        player.sendMessage(playerMessage(easelHelp));
                        return;

                    case RIGHT_CLICK:

                        if (easel.getFrame().getItem().getType() == Material.AIR) {

                            if (player.getItemInHand().getType() == Material.PAPER) {

                                ItemMeta meta = player.getItemInHand().getItemMeta();

                                if (meta.hasDisplayName()
                                        && meta.getDisplayName().equals(Recipe.canvasTitle)) {
                                    easel.mountCanvas(player, player.getItemInHand());
                                    return;

                                } else {
                                    player.sendMessage(playerError(notACanvas));
                                }

                            } else {
                                player.sendMessage(playerError(needCanvas));
                            }

                        } else {
                            easel.rideEasel(player);
                            return;
                        }
                        break;

                    case SHIFT_RIGHT_CLICK:

                        if (easel.hasItem()) {
                            MapArt art = MapArt.getArtwork(plugin, easel.getItem());

                            if (art != null) {

                                if (!art.getPlayer().getUniqueId().equals(player.getUniqueId())
                                        && !player.hasPermission("artmap.admin")) {

                                    player.sendMessage(String.format(notYourEasel, art.getPlayer().getName()));
                                }
                            }
                        }
                        easel.breakEasel();
                        break;
                }

            } else {
                player.sendRawMessage(playerError(elseUsing));
            }

        } else {
            player.sendMessage(playerError(noperm));
        }
    }
}

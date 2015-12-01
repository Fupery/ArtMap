package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {

    private final ArtMap plugin;

    public PlayerInteractListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    private static BlockFace getFacing(Player player) {
        int yaw = ((int) player.getLocation().getYaw()) % 360;

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 135 && yaw < 225) {
            return BlockFace.SOUTH;

        } else if (yaw >= 225 && yaw < 315) {
            return BlockFace.WEST;

        } else if (yaw >= 315 || yaw < 45) {
            return BlockFace.NORTH;

        } else if (yaw >= 45 && yaw < 135) {
            return BlockFace.EAST;

        } else return BlockFace.NORTH;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (ArtMaterial.EASEL.isValidMaterial(event.getItem())) {

            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                event.setCancelled(true);

                if (event.getBlockFace().equals(BlockFace.UP)) {

                    event.setCancelled(true);

                    Location easelLocation
                            = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
                    BlockFace facing = getFacing(event.getPlayer());

                    if (easelLocation.getBlock().getType() == Material.AIR) {

                        if (!Easel.checkForEasel(plugin, easelLocation)) {

                            Easel easel = Easel.spawnEasel(plugin, easelLocation, facing);
                            Player player = event.getPlayer();
                            ItemStack item = player.getItemInHand().clone();
                            item.setAmount(1);

                            player.getInventory().removeItem(item);

                            if (easel != null) {
                                return;
                            }
                        }
                    }
                    event.getPlayer().sendMessage(ArtMap.Lang.INVALID_POS.message());
                }
            }

        }

//        if (event.getItem() != null && event.getMaterial().equals(Material.ARMOR_STAND)) {
//
//            ItemMeta meta = event.getItem().getItemMeta();
//
//            if (meta != null && meta.hasDisplayName()) {
//
//                if (meta.getDisplayName().equals(ArtItem.easelKey)) {
//
//                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
//                        event.setCancelled(true);
//
//                        if (event.getBlockFace().equals(BlockFace.UP)) {
//
//                            event.setCancelled(true);
//
//                            Location easelLocation
//                                    = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
//                            BlockFace facing = getFacing(event.getPlayer());
//
//                            if (easelLocation.getBlock().getType() == Material.AIR) {
//
//                                if (!Easel.checkForEasel(plugin, easelLocation)) {
//
//                                    Easel easel = Easel.spawnEasel(plugin, easelLocation, facing);
//                                    Player player = event.getPlayer();
//                                    ItemStack item = player.getItemInHand().clone();
//                                    item.setAmount(1);
//
//                                    player.getInventory().removeItem(item);
//
//                                    if (easel != null) {
//                                        return;
//                                    }
//                                }
//                            }
//                            event.getPlayer().sendMessage(ArtMap.Lang.INVALID_POS.message());
//                        }
//                    }
//                }
//            }
//        }
    }

    @EventHandler
    public void onInventoryCreativeEvent(final InventoryCreativeEvent event) {

        if (event.getClick() == ClickType.CREATIVE) {

            final ItemStack item = event.getCursor();
            final Player player = (Player) event.getWhoClicked();

            if (item != null && item.getType() == Material.MAP) {

                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {

                        ItemMeta meta = item.getItemMeta();

                        if (!meta.hasLore()) {

                            MapArt art = MapArt.getArtwork(plugin, item.getDurability());

                            if (art != null) {

                                ItemStack correctLore = art.getMapItem();
                                player.getInventory().setItem(event.getSlot(), correctLore);
                            }
                        }
                    }
                });
            }
        }
    }
}

package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Lang;
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
        Location easelLocation;
        BlockFace facing;

        if (!ArtMaterial.EASEL.isValidMaterial(event.getItem())) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        event.setCancelled(true);

        if (!event.getBlockFace().equals(BlockFace.UP)) {
            return;
        }
        easelLocation = event.getClickedBlock().getLocation().clone().add(0, 2, 0);

        if (easelLocation.getBlock().getType() != Material.AIR) {
            return;
        }

        if (!Easel.checkForEasel(easelLocation)) {

            Easel easel = Easel.spawnEasel(easelLocation, getFacing(event.getPlayer()));
            Player player = event.getPlayer();
            ItemStack item = player.getItemInHand().clone();
            item.setAmount(1);

            player.getInventory().removeItem(item);

            if (easel != null) {
                return;
            }
        }
        event.getPlayer().sendMessage(Lang.INVALID_POS.message());
    }

    @EventHandler
    public void onInventoryCreativeEvent(final InventoryCreativeEvent event) {

        final ItemStack item = event.getCursor();

        if (event.getClick() != ClickType.CREATIVE || event.getClickedInventory() == null) {
            return;
        }
        if (item != null && item.getType() == Material.MAP) {

            ArtMap.runTaskAsync(new Runnable() {
                @Override
                public void run() {

                    ItemMeta meta = item.getItemMeta();

                    if (!meta.hasLore()) {

                        MapArt art = ArtMap.getArtDatabase().getArtwork(item.getDurability());

                        if (art != null) {

                            ItemStack correctLore = art.getMapItem();
                            event.getClickedInventory().setItem(event.getSlot(), correctLore);
                        }
                    }
                }
            });
        }
    }
}

package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Compatability.CompatibilityManager;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEffect;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class PlayerInteractListener implements RegisteredListener {

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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (ArtMap.getPreviewManager().endPreview(event.getPlayer())) event.setCancelled(true);

        if (!ArtMaterial.EASEL.isValidMaterial(event.getItem())
                || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        event.setCancelled(true);

        if (!event.getBlockFace().equals(BlockFace.UP)) {
            return;
        }
        Player player = event.getPlayer();
        Location baseLocation = event.getClickedBlock().getLocation().clone().add(.5, 1.25, .5);
        Location easelLocation = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
        CompatibilityManager compat = ArtMap.getCompatManager();
        if (!player.hasPermission("artmap.artist")
                || !compat.checkBuildAllowed(player, baseLocation)
                || !compat.checkBuildAllowed(player, easelLocation)) {
            Lang.ActionBar.NO_PERM_ACTION.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
            return;
        }
        BlockFace facing = getFacing(player);
        Location frameBlock = new LocationHelper(easelLocation).shiftTowards(facing);

        if (easelLocation.getBlock().getType() != Material.AIR
                || baseLocation.getBlock().getType() != Material.AIR
                || frameBlock.getBlock().getType() != Material.AIR
                || Easel.checkForEasel(easelLocation)) {
            Lang.ActionBar.INVALID_POS.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
            return;
        }
        Easel easel = Easel.spawnEasel(easelLocation, facing);
        ItemStack item = player.getItemInHand().clone();
        item.setAmount(1);

        player.getInventory().removeItem(item);

        if (easel == null) {
            Lang.ActionBar.INVALID_POS.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
        }
    }

    @EventHandler
    public void onInventoryCreativeEvent(final InventoryCreativeEvent event) {
        final ItemStack item = event.getCursor();

        if (event.getClick() != ClickType.CREATIVE || event.getClickedInventory() == null
                || item == null || item.getType() != Material.MAP) {
            return;
        }

        ArtMap.getScheduler().ASYNC.run(() -> {

            ItemMeta meta = item.getItemMeta();

            if (!meta.hasLore()) {

                MapArt art = ArtMap.getArtDatabase().getArtwork(item.getDurability());

                if (art != null) {

                    ItemStack correctLore = art.getMapItem();
                    event.getClickedInventory().setItem(event.getSlot(), correctLore);
                }
            }
        });
    }

    @Override
    public void unregister() {
        PlayerInteractEvent.getHandlerList().unregister(this);
        InventoryCreativeEvent.getHandlerList().unregister(this);
    }
}

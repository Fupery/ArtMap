package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.Easel.EaselPart;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEaselListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {

        Player player = event.getPlayer();

        callEaselEvent(player, event.getRightClicked(), event,
                isSneaking(player));
        checkPreviewing(player, event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();
        callEaselEvent(player, event.getRightClicked(), event,
                isSneaking(player));

        checkPreviewing(player, event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        callEaselEvent(event.getDamager(), event.getEntity(), event,
                EaselEvent.ClickType.LEFT_CLICK);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {

        if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY) {

            callEaselEvent(event.getRemover(), event.getEntity(), event,
                    EaselEvent.ClickType.LEFT_CLICK);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (checkSignBreak(event.getBlock(), event)) {
            if (!checkIsPainting(event.getPlayer(), event)) {
                ArtMap.getLang().ACTION_BAR_MESSAGES.EASEL_PUNCH.send(event.getPlayer());
            }
        }
        checkIsPainting(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        checkSignBreak(event.getBlock(), event);
    }

    private void callEaselEvent(Entity clicker, Entity clicked,
                                Cancellable event, EaselEvent.ClickType click) {

        EaselPart part = EaselPart.getPartType(clicked);

        if (part != null && part != EaselPart.SEAT) {

            Easel easel = Easel.getEasel(clicked.getLocation(), part);

            if (easel != null) {
                boolean wasCancelled = event.isCancelled();
                event.setCancelled(true);

                if (clicker instanceof Player) {
                    Player player = (Player) clicker;

                    if (!checkIsPainting(player, event) && !wasCancelled) {
                        Bukkit.getServer().getPluginManager().callEvent(
                                new EaselEvent(easel, click, player));
                    }
                }
            }
        }
    }

    private EaselEvent.ClickType isSneaking(Player player) {
        return (player.isSneaking()) ? EaselEvent.ClickType.SHIFT_RIGHT_CLICK :
                EaselEvent.ClickType.RIGHT_CLICK;
    }

    private boolean checkIsPainting(Player player, Cancellable event) {

        if (player.isInsideVehicle() && ArtMap.getArtistHandler().containsPlayer(player)) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    private void checkPreviewing(Player player, Cancellable event) {

        if (ArtMap.getPreviewing().containsKey(player)) {

            if (player.getItemInHand().getType() == Material.MAP) {

                Preview.stop(player);
                event.setCancelled(true);
            }
        }
    }

    private boolean checkSignBreak(Block block, Cancellable event) {

        if (block.getType() == Material.WALL_SIGN) {
            Sign sign = ((Sign) block.getState());

            if (sign.getLine(3).equals(EaselPart.ARBITRARY_SIGN_ID)) {

                if (EaselInteractListener.easels.containsKey(block.getLocation())
                        || Easel.checkForEasel(block.getLocation())) {
                    event.setCancelled(true);
                    return true;
                }
            }
        }
        return false;
    }
}

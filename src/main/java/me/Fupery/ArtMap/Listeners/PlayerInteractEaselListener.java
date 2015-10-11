package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.Easel.PartType;
import me.Fupery.ArtMap.Utils.Formatting;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import static me.Fupery.ArtMap.Easel.Easel.getEasel;
import static me.Fupery.ArtMap.Utils.Formatting.breakCanvas;
import static me.Fupery.ArtMap.Utils.Formatting.playerError;

public class PlayerInteractEaselListener implements Listener {

    private ArtMap plugin;

    public PlayerInteractEaselListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {

        Player player = event.getPlayer();

        callEaselEvent(player, event.getRightClicked(), event,
                isSneaking(player));

        checkPreviewing(player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();
        callEaselEvent(player, event.getRightClicked(), event,
                isSneaking(player));

        checkPreviewing(player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {

            Player player = ((Player) event.getDamager());
            callEaselEvent(player, event.getEntity(), event,
                    EaselEvent.ClickType.LEFT_CLICK);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {

        if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY
                && event.getRemover() instanceof Player) {

            Player player = ((Player) event.getRemover());
            callEaselEvent(player, event.getEntity(), event,
                    EaselEvent.ClickType.LEFT_CLICK);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkSignBreak(event.getBlock(), event);
        checkIsPainting(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        checkSignBreak(event.getBlock(), event);
    }

    private void callEaselEvent(Player player, Entity clicked,
                                Cancellable event, EaselEvent.ClickType click) {

        PartType part = PartType.getPartType(clicked);

        if (part != null && part != PartType.SEAT) {

            Easel easel = getEasel(plugin, clicked.getLocation(), part);

            if (easel != null) {
                boolean wasCancelled = event.isCancelled();
                event.setCancelled(true);

                if (!checkIsPainting(player, event) && !wasCancelled) {
                    plugin.getServer().getPluginManager().callEvent(
                            new EaselEvent(easel, click, player));
                }
            }
        }
    }

    private EaselEvent.ClickType isSneaking(Player player) {
        return (player.isSneaking()) ? EaselEvent.ClickType.SHIFT_RIGHT_CLICK :
                EaselEvent.ClickType.RIGHT_CLICK;
    }

    private boolean checkIsPainting(Player player, Cancellable event) {

        if (player.isInsideVehicle() && plugin.getArtistHandler() != null
                && plugin.getArtistHandler().containsPlayer(player)) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    private void checkPreviewing(Player player, Cancellable event) {

        if (plugin.isPreviewing(player)) {

            if (player.getItemInHand().getType() == Material.MAP) {

                plugin.stopPreviewing(player);
                event.setCancelled(true);
            }
        }
    }

    private void checkSignBreak(Block block, Cancellable event) {

        if (block.getType() == Material.WALL_SIGN) {
            Sign sign = ((Sign) block.getState());

            if (sign.getLine(3).equals(Easel.arbitrarySignID)) {

                if (plugin.getEasels().containsKey(block.getLocation())
                        || Easel.checkForEasel(plugin, block.getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

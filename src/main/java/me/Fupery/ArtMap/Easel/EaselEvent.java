package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EaselEvent {
    private final Easel easel;
    private final ClickType click;
    private final Player player;

    public EaselEvent(Easel easel, ClickType click, Player player) {
        this.easel = easel;
        this.click = click;
        this.player = player;
    }

    public void callEvent() {
        if (!player.hasPermission("artmap.artist")) {
            Lang.NO_PERM.send(player);
            return;
        }
        if (easel.isBeingUsed()) {
            Lang.ActionBar.ELSE_USING.send(player);
            easel.playEffect(EaselEffect.USE_DENIED);
            return;
        }
        switch (click) {
            case LEFT_CLICK:
                Lang.ActionBar.EASEL_HELP.send(player);
                return;
            case RIGHT_CLICK:
                //If the easel has a canvas, player rides the easel
                if (easel.getItem().getType() == Material.MAP) {
                    ArtMap.getArtistHandler().addPlayer(player, easel,
                            new Map(easel.getItem().getDurability()), EaselPart.getYawOffset(easel.getFacing()));
                    return;
                    //remove items that were added while instance is unloaded etc.
                } else if (easel.getItem().getType() != Material.AIR) {
                    easel.removeItem();
                    return;
                }
                ItemStack itemInHand = player.getItemInHand();
                ArtMaterial material = ArtMaterial.getCraftItemType(itemInHand);

                if (material == ArtMaterial.CANVAS) {
                    Map map = ArtMap.getArtDatabase().createMap();
                    easel.mountCanvas(new Canvas(map));
                    consumeItem(player, itemInHand);
                    return;

                } else if (material == ArtMaterial.MAP_ART) {
                    ArtMap.getScheduler().ASYNC.run(() -> editArtwork(player.getItemInHand()));
                    return;
                }
                Lang.ActionBar.NEED_CANVAS.send(player);
                easel.playEffect(EaselEffect.USE_DENIED);
                return;

            case SHIFT_RIGHT_CLICK:
                if (easel.hasItem()) {
                    ArtMap.getArtDatabase().recycleMap(new Map(easel.getItem().getDurability()));
                    easel.removeItem();
                }
                easel.breakEasel();
        }
    }

    private void editArtwork(ItemStack playerMainHandItem) {
        MapArt art = ArtMap.getArtDatabase().getArtwork(playerMainHandItem.getDurability());
        ArtMap.getScheduler().SYNC.run(() -> {
            if (art != null) {
                if (!player.getUniqueId().equals(art.getArtistPlayer().getUniqueId())) {
                    Lang.ActionBar.NO_EDIT_PERM.send(player);
                    easel.playEffect(EaselEffect.USE_DENIED);
                    return;
                }
                if (ArtMap.getPreviewManager().endPreview(player)) return;
                Map map = art.getMap().cloneArtwork();
                easel.mountCanvas(new Canvas.CanvasCopy(map, art.getTitle()));
                consumeItem(player, playerMainHandItem);
            } else {
                Lang.ActionBar.NEED_CANVAS.send(player);
                easel.playEffect(EaselEffect.USE_DENIED);
            }
        });
    }

    private void consumeItem(Player player, ItemStack item) {
        ItemStack removed = item.clone();
        removed.setAmount(1);
        player.getInventory().removeItem(removed);
    }

    public enum ClickType {
        LEFT_CLICK, RIGHT_CLICK, SHIFT_RIGHT_CLICK
    }
}

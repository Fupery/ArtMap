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
        if (ArtMap.getPreviewManager().endPreview(player)) return;

        switch (click) {
            case LEFT_CLICK:
                Lang.ActionBar.EASEL_HELP.send(player);
                return;
            case RIGHT_CLICK:
                if (easel.getItem().getType() == Material.MAP) {
                    //If the easel has a canvas, player rides the easel
                    ArtMap.getArtistHandler().addPlayer(player, easel,
                            new Map(easel.getItem().getDurability()), EaselPart.getYawOffset(easel.getFacing()));
                    return;
                } else if (easel.getItem().getType() != Material.AIR) {
                    //remove items that were added while instance is unloaded etc.
                    easel.removeItem();
                    return;
                }
                ItemStack itemInHand = player.getItemInHand();
                ArtMaterial material = ArtMaterial.getCraftItemType(itemInHand);

                if (material == ArtMaterial.CANVAS) {
                    //Mount a canvas on the easel
                    Map map = ArtMap.getArtDatabase().createMap();
                    map.update(player);
                    mountCanvas(itemInHand, new Canvas(map));

                } else if (material == ArtMaterial.MAP_ART) {
                    //Edit an artwork on the easel
                    ArtMap.getScheduler().ASYNC.run(() -> {
                        MapArt art = ArtMap.getArtDatabase().getArtwork(itemInHand.getDurability());
                        ArtMap.getScheduler().SYNC.run(() -> {
                            if (art != null) {
                                if (!player.getUniqueId().equals(art.getArtistPlayer().getUniqueId())) {
                                    Lang.ActionBar.NO_EDIT_PERM.send(player);
                                    easel.playEffect(EaselEffect.USE_DENIED);
                                    return;
                                }
                                Canvas canvas = new Canvas.CanvasCopy(art.getMap().cloneMap(), art.getTitle());
                                mountCanvas(itemInHand, canvas);
                            } else {
                                Lang.ActionBar.NEED_CANVAS.send(player);
                                easel.playEffect(EaselEffect.USE_DENIED);
                            }
                        });
                    });
                } else {
                    Lang.ActionBar.NEED_CANVAS.send(player);
                    easel.playEffect(EaselEffect.USE_DENIED);
                }
                return;

            case SHIFT_RIGHT_CLICK:
                if (easel.hasItem()) {
                    ArtMap.getArtDatabase().recycleMap(new Map(easel.getItem().getDurability()));
                    easel.removeItem();
                }
                easel.breakEasel();
        }
    }

    private void mountCanvas(ItemStack itemInHand, Canvas canvas) {
        easel.mountCanvas(canvas);
        ItemStack removed = itemInHand.clone();
        removed.setAmount(1);
        player.getInventory().removeItem(removed);
    }

    public enum ClickType {
        LEFT_CLICK, RIGHT_CLICK, SHIFT_RIGHT_CLICK
    }
}

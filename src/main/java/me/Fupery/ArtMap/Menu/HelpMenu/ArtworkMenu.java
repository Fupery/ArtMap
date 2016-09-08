package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.Preview;
import me.Fupery.ArtMap.Utils.VersionHandler;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ArtworkMenu extends ListMenu implements ChildMenu {
    private final UUID artist;
    private ArtistMenu parent;

    public ArtworkMenu(ArtistMenu parent, UUID artist, int page) {
        super(processTitle(artist), page);
        this.parent = parent;
        this.artist = artist;
    }

    private static String processTitle(UUID artist) {
        String name = Bukkit.getOfflinePlayer(artist).getName();
        String processedName;
        String title = "§1%s's art";

        if (name.length() >= 22) {
            processedName = name.substring(0, 21);

        } else if (name.length() >= 14) {
            processedName = name;

        } else {
            processedName = String.format(title, name);
        }
        return processedName;
    }

    public static boolean isPreviewItem(ItemStack item) {
        return item != null && item.getType() == Material.MAP && item.hasItemMeta()
                && item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).equals(ArtItem.PREVIEW_KEY);
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return parent;
    }

    @Override
    protected Button[] getListItems() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(artist);
        if (player == null || !player.hasPlayedBefore()) return new Button[0];
        MapArt[] artworks = ArtMap.getArtDatabase().listMapArt(player.getUniqueId());
        Button[] buttons;
        boolean adminButton = parent.getViewer().hasPermission("artmap.admin");

        if (artworks != null && artworks.length > 0) {
            buttons = new Button[artworks.length];

            for (int i = 0; i < artworks.length; i++) {
                buttons[i] = new PreviewButton(this, artworks[i], adminButton);
            }

        } else {
            buttons = new Button[0];
        }
        return buttons;
    }

    @Override
    public void onMenuCloseEvent(Player viewer, MenuCloseReason reason) {
        if (reason == MenuCloseReason.SPECIAL) return;
        if (ArtMap.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
            ItemStack offHand = viewer.getInventory().getItemInOffHand();
            if (isPreviewItem(offHand)) viewer.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    private static class PreviewButton extends Button {

        private final MapArt artwork;
        private final ArtworkMenu artworkMenu;

        private PreviewButton(ArtworkMenu menu, MapArt artwork, boolean adminButton) {
            super(Material.MAP);
            ItemMeta meta = artwork.getMapItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.add(HelpMenu.CLICK);
            if (adminButton) lore.add(lore.size(), ChatColor.GOLD + ArtMap.getLang().getMsg("ADMIN_RECIPE"));
            meta.setLore(lore);
            setItemMeta(meta);
            this.artwork = artwork;
            this.artworkMenu = menu;
        }

        @Override
        public void onClick(Player player, ClickType clickType) {

            if (clickType == ClickType.LEFT) {
                if (ArtMap.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {

                    ItemStack offHand = player.getInventory().getItemInOffHand();
                    if (offHand.getType() == Material.AIR || isPreviewItem(offHand)) {
                        SoundCompat.BLOCK_CLOTH_FALL.play(player);
                        ItemStack preview = artwork.getMapItem();
                        ItemMeta meta = preview.getItemMeta();
                        List<String> lore = getItemMeta().getLore();
                        lore.set(0, ArtItem.PREVIEW_KEY);
                        meta.setLore(lore);
                        preview.setItemMeta(meta);
                        ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.SPECIAL);
                        player.getInventory().setItemInOffHand(preview);
                        ArtMap.getMenuHandler().openMenu(player, this.artworkMenu);
                    } else {
                        ArtMap.getLang().sendMsg("EMPTY_HAND_PREVIEW", player);
                    }
                } else {
                    ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.DONE);

                    ArtMap.getTaskManager().SYNC.run(() -> {
                        if (ArtMap.getPreviewing().containsKey(player)) {
                            ArtMap.getPreviewing().get(player).stopPreviewing();
                        }
                        SoundCompat.BLOCK_CLOTH_FALL.play(player);
                        if (player.getItemInHand().getType() != Material.AIR) {
                            ArtMap.getLang().sendMsg("EMPTY_HAND_PREVIEW", player);
                            return;
                        }
                        Preview.artwork(player, artwork);
                    });

                }
            } else if (clickType == ClickType.RIGHT && player.hasPermission("artmap.artist")) {
                SoundCompat.BLOCK_CLOTH_FALL.play(player);
                ItemStack leftOver = player.getInventory().addItem(artwork.getMapItem()).get(0);
                if (leftOver != null) ArtMap.getTaskManager().SYNC.run(() ->
                        player.getWorld().dropItemNaturally(player.getLocation(), leftOver));
            }
        }
    }
}

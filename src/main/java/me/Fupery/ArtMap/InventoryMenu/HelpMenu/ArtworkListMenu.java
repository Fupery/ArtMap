package me.Fupery.ArtMap.InventoryMenu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Command.CommandPreview;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.InventoryMenu.InventoryMenu;
import me.Fupery.ArtMap.InventoryMenu.ListMenu;
import me.Fupery.ArtMap.InventoryMenu.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

class ArtworkListMenu extends ListMenu {

    UUID artist;

    ArtworkListMenu(InventoryMenu parent, UUID artist, int page) {
        super(parent, processTitle(artist), page, generateButtons(parent.getPlugin(), artist));
        this.artist = artist;
    }

    static MenuButton[] generateButtons(ArtMap plugin, UUID artist) {
        MapArt[] artworks = MapArt.listMapArt(plugin, Bukkit.getOfflinePlayer(artist).getName());
        MenuButton[] buttons;

        if (artworks != null && artworks.length > 0) {
            buttons = new MenuButton[artworks.length];

            for (int i = 0; i < artworks.length; i++) {
                buttons[i] = new ArtworkListMenu.PreviewButton(artworks[i]);
            }

        } else {
            buttons = new MenuButton[0];
        }
        return buttons;
    }

    private static String processTitle(UUID artist) {
        String name = Bukkit.getOfflinePlayer(artist).getName();
        String processedName;
        String title = "§1%s's art";

        if (name.length() >= 25) {
            processedName = name.substring(0, 24);

        } else if (name.length() >= 17) {
            processedName = name;

        } else {
            processedName = String.format(title, name);
        }
        return processedName;
    }

    private static class PreviewButton extends MenuButton {

        MapArt artwork;

        public PreviewButton(MapArt artwork) {
            super(Material.MAP);
            ItemMeta meta = artwork.getMapItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.add(HelpMenu.click);
            meta.setLore(lore);
            setItemMeta(meta);
            this.artwork = artwork;
        }

        @Override
        public void onClick(Player player) {
            player.closeInventory();
            CommandPreview.previewArtwork(getMenu().getPlugin(), player, artwork);
        }
    }
}

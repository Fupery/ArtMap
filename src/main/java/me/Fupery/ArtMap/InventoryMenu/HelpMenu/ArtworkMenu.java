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

public class ArtworkMenu extends ListMenu {

    private UUID artist;

    public ArtworkMenu(InventoryMenu parent, UUID artist) {
        super(parent, "Player Artworks");
        this.artist = artist;
    }

    static MenuButton[] generateButtons(ArtMap plugin, UUID artist) {
        MapArt[] artworks = MapArt.listMapArt(plugin, Bukkit.getOfflinePlayer(artist).getName());
        MenuButton[] buttons;

        if (artworks != null && artworks.length > 0) {
            buttons = new MenuButton[artworks.length];

            for (int i = 0; i < artworks.length; i++) {
                buttons[i] = new ArtworkMenu.PreviewButton(artworks[i]);
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

        if (name.length() >= 22) {
            processedName = name.substring(0, 21);

        } else if (name.length() >= 14) {
            processedName = name;

        } else {
            processedName = String.format(title, name);
        }
        return processedName;
    }

    @Override
    public void open(ArtMap plugin, Player player) {
        listItems = generateButtons(plugin, artist);
        title = processTitle(artist);
        super.open(plugin, player);
    }

    private static class PreviewButton extends MenuButton {

        final MapArt artwork;

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
        public void onClick(ArtMap plugin, Player player) {
            player.closeInventory();
            CommandPreview.previewArtwork(plugin, player, artwork);
        }
    }
}

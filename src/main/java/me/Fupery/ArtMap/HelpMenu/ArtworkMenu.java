package me.Fupery.ArtMap.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Command.CommandPreview;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.InventoryMenu.API.InventoryMenu;
import me.Fupery.InventoryMenu.API.ListMenu;
import me.Fupery.InventoryMenu.API.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class ArtworkMenu extends ListMenu {

    private final UUID artist;

    public ArtworkMenu(InventoryMenu parent, UUID artist) {
        super(parent, ArtMap.getLang().getMsg("MENU_ARTWORKS"), ArtMap.getLang().getMsg("BUTTON_CLOSE"));
        this.artist = artist;
    }

    static MenuButton[] generateButtons(UUID artist) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(artist);
        if (player == null || !player.hasPlayedBefore()) return new MenuButton[0];
        MapArt[] artworks = ArtMap.getArtDatabase().listMapArt(player.getName());
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
    public void open(JavaPlugin plugin, Player player) {
        listItems = generateButtons(artist);
        title = processTitle(artist);
        super.open(plugin, player);
    }

    @Override
    public MenuButton[] generateListButtons() {
        return listItems;
    }

    private static class PreviewButton extends MenuButton {

        final MapArt artwork;

        public PreviewButton(MapArt artwork) {
            super(Material.MAP);
            ItemMeta meta = artwork.getMapItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.add(HelpMenu.CLICK);
            meta.setLore(lore);
            setItemMeta(meta);
            this.artwork = artwork;
        }

        @Override
        public void onClick(JavaPlugin javaPlugin, Player player) {
            player.closeInventory();
            CommandPreview.previewArtwork(player, artwork);
        }
    }
}

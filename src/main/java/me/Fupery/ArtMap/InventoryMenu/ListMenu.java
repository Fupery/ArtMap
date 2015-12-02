package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Command.CommandPreview;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class ListMenu extends InventoryMenu {

    protected static final int maxButtons = 25;
    protected MenuButton[] listItems;
    int page;

    ListMenu(InventoryMenu parent, String title, int page, MenuButton... listItems) {
        super(parent.plugin, parent, parent.getPlayer(), title, InventoryType.CHEST);
        this.page = page;
        this.listItems = listItems;
        paginateButtons();
    }

    protected void paginateButtons() {
        MenuButton[] buttons = new MenuButton[maxButtons + 2];

        buttons[0] = (page == 0) ? new CloseButton() : new PageButton(false);

        int start = page * maxButtons;
        int pageLength = listItems.length - start;
        int end = (pageLength >= maxButtons) ? maxButtons : pageLength;

        System.arraycopy(listItems, start, buttons, 1, end);

        buttons[maxButtons + 1] = (listItems.length > (maxButtons + start)) ? new PageButton(true) : null;
        clearButtons();
        addButtons(buttons);
    }

    protected void changePage(boolean forward) {

        if (page == 0 && !forward) {
            getParent().open();
        }
        page += forward ? 1 : -1;
        paginateButtons();
        getPlayer().updateInventory();
    }

    static class PageButton extends MenuButton {

        boolean forward;

        public PageButton(boolean forward) {
            super(forward ? Material.EMERALD : Material.BARRIER,
                    forward ? "§a§l➡" : "§c§l⬅");
            this.forward = forward;
        }

        @Override
        public void run() {
            if (getMenu() instanceof ListMenu) {
                ((ListMenu) menu).changePage(forward);
            }
        }
    }

}

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
                buttons[i] = new PreviewButton(artworks[i]);
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
        public void run() {
            getMenu().getPlayer().closeInventory();
            CommandPreview.previewArtwork(getMenu().getPlugin(), getPlayer(), artwork);
        }
    }
}

class ArtistListMenu extends ListMenu {

    ArtistListMenu(InventoryMenu parent, int page) {
        super(parent, "§1Click an Artist", page);
        listItems = generateButtons(this);
        paginateButtons();
    }

    private MenuButton[] generateButtons(InventoryMenu menu) {
        UUID[] artists = MapArt.listArtists(menu.getPlugin(), menu.getPlayer().getUniqueId());
        MenuButton[] buttons;

        if (artists != null && artists.length > 0) {
            buttons = new MenuButton[artists.length];

            for (int i = 0; i < artists.length; i++) {

                buttons[i] = new LinkedButton(
                        new ArtworkListMenu(menu, artists[i], 0), Material.SKULL_ITEM);

                OfflinePlayer player = Bukkit.getOfflinePlayer(artists[i]);
                buttons[i].setDurability((short) 3);
                SkullMeta meta = (SkullMeta) (buttons[i].getItemMeta());

                meta.setOwner(player.getName());
                meta.setDisplayName(player.getName());
                meta.setLore(Collections.singletonList(HelpMenu.click));
                buttons[i].setItemMeta(meta);
            }

        } else {
            buttons = new MenuButton[0];
        }
        return buttons;
    }
}


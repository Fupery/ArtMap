package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.InventoryMenu.HelpMenu.HelpMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class MenuButton extends ItemStack {

    protected InventoryMenu menu;

    public MenuButton(Material type, String... text) {
        super(type);
        ItemMeta meta = getItemMeta();
        menu = null;

        if (text.length > 0) {
            meta.setDisplayName(text[0]);

            if (text.length > 1) {
                String[] lore = new String[text.length - 1];
                System.arraycopy(text, 1, lore, 0, text.length - 1);
                meta.setLore(Arrays.asList(lore));
            }
        }
        setItemMeta(meta);
    }

    public abstract void onClick(Player player);

    public InventoryMenu getMenu() {
        return menu;
    }

    void setMenu(InventoryMenu menu) {
        this.menu = menu;
    }

    public static class LinkedButton extends MenuButton {

        InventoryMenu linkedMenu;

        public LinkedButton(InventoryMenu linkedMenu, Material type, String... text) {
            super(type, text);
            this.linkedMenu = linkedMenu;
        }

        @Override
        public void onClick(Player player) {

            if (linkedMenu instanceof PlayerDataSensitiveMenu) {
                ((PlayerDataSensitiveMenu) linkedMenu).initializeMenu(player);
            }
            linkedMenu.open(player);
        }
    }

    public static class StaticButton extends MenuButton {

        public StaticButton(Material type, String... text) {
            super(type, text);
        }

        @Override
        public void onClick(Player player) {
        }
    }

    public static class CloseButton extends MenuButton {

        public CloseButton() {
            super(Material.BARRIER, HelpMenu.close);
        }

        @Override
        public void onClick(Player player) {

            if (menu.hasParent()) {
                menu.getParent().open(player);

            } else {
                player.closeInventory();
            }
        }
    }
}


package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class MenuButton extends ItemStack {

    public MenuButton(Material type, String... text) {
        super(type);
        ItemMeta meta = getItemMeta();

        if (text != null && text.length > 0) {
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

    public static class LinkedButton extends MenuButton {

        final InventoryMenu linkedMenu;

        public LinkedButton(InventoryMenu linkedMenu, Material type, String... text) {
            super(type, text);
            this.linkedMenu = linkedMenu;
        }

        @Override
        public void onClick(Player player) {
            linkedMenu.open(player);
        }

        public InventoryMenu getLinkedMenu() {
            return linkedMenu;
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

        final InventoryMenu menu;

        public CloseButton(InventoryMenu menu) {
            super(Material.BARRIER, Lang.Array.HELP_CLOSE.messages());
            this.menu = menu;
        }

        @Override
        public void onClick(final Player player) {
            ArtMap.runTask(new Runnable() {
                @Override
                public void run() {
                    menu.close(player);

                    if (menu.parent != null) {
                        menu.parent.open(player);
                    }
                }
            });
        }
    }
}


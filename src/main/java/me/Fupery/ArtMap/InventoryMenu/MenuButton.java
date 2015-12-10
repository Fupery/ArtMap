package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
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

    public abstract void onClick(ArtMap plugin, Player player);

    public static class LinkedButton extends MenuButton {

        final InventoryMenu linkedMenu;

        public LinkedButton(InventoryMenu linkedMenu, Material type, String... text) {
            super(type, text);
            this.linkedMenu = linkedMenu;
        }

        @Override
        public void onClick(ArtMap plugin, Player player) {
            linkedMenu.open(plugin, player);
        }

    }

    public static class StaticButton extends MenuButton {

        public StaticButton(Material type, String... text) {
            super(type, text);
        }

        @Override
        public void onClick(ArtMap plugin, Player player) {
        }
    }

    public static class CloseButton extends MenuButton {

        InventoryMenu menu;

        public CloseButton(InventoryMenu menu) {
            super(Material.BARRIER, ArtMap.Lang.Array.HELP_CLOSE.messages());
            this.menu = menu;
        }

        @Override
        public void onClick(final ArtMap plugin, final Player player) {
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    menu.close(plugin, player);

                    if (menu.parent != null) {
                        menu.parent.open(plugin, player);
                    }
                }
            });
        }
    }
}


package me.Fupery.ArtMap.InventoryMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;

public abstract class MenuButton extends ItemStack implements Runnable {

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

    protected Player getPlayer() {
        return (menu != null) ? menu.getPlayer() : null;
    }

    public InventoryMenu getMenu() {
        return menu;
    }

    void setMenu(InventoryMenu menu) {
        this.menu = menu;
    }

    @Override
    public void setData(MaterialData data) {
        super.setData(data);
    }

    @Override
    public void setDurability(short durability) {
        super.setDurability(durability);
    }
}

class LinkedButton extends MenuButton {

    InventoryMenu linkedMenu;

    public LinkedButton(InventoryMenu linkedMenu, Material type, String... text) {
        super(type, text);
        this.linkedMenu = linkedMenu;
    }

    @Override
    public void run() {
        linkedMenu.open();
    }
}

class StaticButton extends MenuButton {

    public StaticButton(Material type, String... text) {
        super(type, text);
    }

    @Override
    public void run() {
    }
}

class CloseButton extends MenuButton {

    public CloseButton() {
        super(Material.BARRIER, HelpMenu.close);
    }

    @Override
    public void run() {

        if (menu.hasParent()) {
            menu.getPlayer().openInventory(menu.getParent().getInventory());

        } else {
            menu.getPlayer().closeInventory();
        }
    }
}

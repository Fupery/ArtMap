package me.Fupery.ArtMap.InventoryMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public abstract class ListMenu extends InventoryMenu {

    protected static final int maxButtons = 25;
    protected MenuButton[] listItems;
    int page;

    protected ListMenu(InventoryMenu parent, String title, int page, MenuButton... listItems) {
        super(parent.plugin, parent, title, InventoryType.CHEST);
        this.page = page;
        this.listItems = listItems;
        paginateButtons();
    }

    protected void paginateButtons() {
        MenuButton[] buttons = new MenuButton[maxButtons + 2];

        buttons[0] = (page == 0) ? new MenuButton.CloseButton() : new PageButton(false);

        int start = page * maxButtons;
        int pageLength = listItems.length - start;
        int end = (pageLength >= maxButtons) ? maxButtons : pageLength;

        System.arraycopy(listItems, start, buttons, 1, end);

        buttons[maxButtons + 1] = (listItems.length > (maxButtons + start)) ? new PageButton(true) : null;
        clearButtons();
        addButtons(buttons);
    }

    protected void changePage(Player player, boolean forward) {

        if (page == 0 && !forward) {
            getParent().open(player);
        }
        page += forward ? 1 : -1;
        paginateButtons();
        updateInventory(player);
    }

    static class PageButton extends MenuButton {

        boolean forward;

        public PageButton(boolean forward) {
            super(forward ? Material.EMERALD : Material.BARRIER,
                    forward ? "§a§l➡" : "§c§l⬅");
            this.forward = forward;
        }

        @Override
        public void onClick(Player player) {
            if (getMenu() instanceof ListMenu) {
                ((ListMenu) menu).changePage(player, forward);
            }
        }
    }

}


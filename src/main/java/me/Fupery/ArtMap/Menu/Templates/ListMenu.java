package me.Fupery.ArtMap.Menu.Templates;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.API.StoragePattern;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.CloseButton;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

public abstract class ListMenu implements MenuTemplate {

    private final StoragePattern pattern;
    private final String heading;
    protected int page;

    public ListMenu(String heading, StoragePattern pattern, int page) {
        this.heading = heading;
        this.pattern = pattern;
        this.page = page;
    }

    @Override
    public String getHeading() {
        return heading;
    }

    @Override
    public InventoryType getType() {
        return InventoryType.CHEST;
    }

    @Override
    public StoragePattern getPattern() {
        return pattern;
    }

    @Override
    public void onMenuOpenEvent(CacheableMenu menu, Player viewer) {
    }

    @Override
    public void onMenuRefreshEvent(CacheableMenu menu, Player viewer) {
    }

    @Override
    public void onMenuClickEvent(CacheableMenu menu, Player viewer, int slot, ClickType click) {
    }

    @Override
    public void onMenuCloseEvent(CacheableMenu menu, Player viewer, MenuCloseReason reason) {
    }

    @Override
    public Button[] getButtons(Player viewer) {
        Button[] listItems = getListItems(viewer);
        int maxButtons = 25;
        Button[] buttons = new Button[maxButtons + 2];

        if (page < 1) {
            buttons[0] = new CloseButton();

        } else {
            buttons[0] = new PageButton(this, false);

            if (page > 0) {
                buttons[0].setAmount(page - 1);
            }
        }
        if (listItems == null || listItems.length < 1) return buttons;

        int start = page * maxButtons;
        int pageLength = listItems.length - start;

        if (pageLength > 0) {
            int end = (pageLength >= maxButtons) ? maxButtons : pageLength;

            System.arraycopy(listItems, start, buttons, 1, end);

            if (listItems.length > (maxButtons + start)) {
                buttons[maxButtons + 1] = new PageButton(this, true);

                if (page < 64) {
                    buttons[maxButtons + 1].setAmount(page + 1);
                }

            } else {
                buttons[maxButtons + 1] = null;
            }
        }
        return buttons;
    }

    @Override
    protected abstract ListMenu clone();

    protected abstract Button[] getListItems(Player viewer);

    protected static class PageButton extends Button {

        boolean forward;
        ListMenu menuTemplate;

        protected PageButton(ListMenu menu, boolean forward) {
            super(forward ? Material.EMERALD : Material.BARRIER, forward ? "§a§l➡" : "§c§l⬅");
            this.forward = forward;
            this.menuTemplate = menu;
        }

        @Override
        public void onClick(CacheableMenu menu, Player player, ClickType clickType) {
            if (forward) SoundCompat.UI_BUTTON_CLICK.play(player);
            else SoundCompat.UI_BUTTON_CLICK.play(player);
            ListMenu newPage = menuTemplate.clone();
            newPage.page = menuTemplate.page + (forward ? 1 : -1);
            ArtMap.getMenuHandler().openMenu(player, newPage);
        }
    }
}

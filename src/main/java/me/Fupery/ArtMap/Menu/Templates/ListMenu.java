package me.Fupery.ArtMap.Menu.Templates;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.CacheableMenu;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.API.StoragePattern;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.CloseButton;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.InventoryMenu.API.MenuButton;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ListMenu implements MenuTemplate {

    private final StoragePattern pattern;
    private final String heading;
    private final int page;

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
        Bukkit.getScheduler().runTaskLater(ArtMap.plugin(), new Runnable() {
            @Override
            public void run() {
                //Your code here!
            }
        }, 3600);//The time to wait, in ticks

        return InventoryType.CHEST;
    }

    @Override
    public StoragePattern getPattern() {
        return null;
    }

    @Override
    public void onMenuOpenEvent(CacheableMenu menu, Player player) {
        getButtons();
    }

    @Override
    public void onMenuRefreshEvent(CacheableMenu menu, Player player) {
    }

    @Override
    public void onMenuClickEvent(CacheableMenu menu, Player player, int slot, ClickType click) {
    }

    @Override
    public void onMenuCloseEvent(CacheableMenu menu, Player player, MenuCloseReason reason) {
    }

    private Button[] paginateButtons(Button[] listItems) {
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

        int start = page * maxButtons;
        int pageLength = listItems.length - start;
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
        return buttons;
    }

    public abstract MenuButton[] generateListButtons();

    protected void changePage(Player player, int page, boolean forward) {
        page += forward ? 1 : -1;
        MenuButton[] buttons = paginateButtons(page, listItems);
        clearButtons();
        addButtons(buttons);
        updateInventory(plugin, player);
    }

    static class PageButton extends Button {

        boolean forward;
        ListMenu menu;

        public PageButton(ListMenu menu, boolean forward) {
            super(forward ? Material.EMERALD : Material.BARRIER, forward ? "§a§l➡" : "§c§l⬅");
            this.forward = forward;
            this.menu = menu;
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            int page = getAmount();
            page += forward ? -1 : 1;
            SoundCompat.UI_BUTTON_CLICK.play(player);
            menu.changePage(player, page, forward);
        }
    }
}

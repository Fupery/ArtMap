package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Menu.API.CloseButton;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;
import com.github.Fupery.InvMenu.API.Button.Button;
import com.github.Fupery.InvMenu.API.Button.StaticButton;
import com.github.Fupery.InvMenu.API.Handler.CacheableMenu;
import com.github.Fupery.InvMenu.API.Handler.MenuHandler;
import com.github.Fupery.InvMenu.API.Templates.BasicMenu;
import com.github.Fupery.InvMenu.API.Templates.ChildMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RecipeMenu extends BasicMenu implements ChildMenu {

    private boolean adminMenu;

    public RecipeMenu(MenuHandler handler, boolean adminMenu) {
        super(handler, ChatColor.DARK_BLUE + Lang.MENU_RECIPE.get(), InventoryType.HOPPER);
        this.adminMenu = adminMenu;
    }

    @Override
    public Button[] getButtons() {
        return new Button[]{
                new StaticButton(Material.SIGN, Lang.Array.INFO_RECIPES.get()),
                new RecipeButton(ArtMaterial.EASEL),
                new RecipeButton(ArtMaterial.CANVAS),
                new RecipeButton(ArtMaterial.PAINT_BUCKET),
                new CloseButton(this)
        };
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenus().MENU.HELP.get(viewer);
    }


    private class RecipeButton extends Button {

        final ArtMaterial recipe;

        public RecipeButton(ArtMaterial material) {
            super(material.getType());
            this.recipe = material;
            ItemMeta meta = material.getItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.set(lore.size() - 1, ChatColor.GREEN + Lang.RECIPE_BUTTON.get());
            if (adminMenu) lore.add(lore.size(), ChatColor.GOLD + Lang.ADMIN_RECIPE.get());
            meta.setLore(lore);
            setItemMeta(meta);
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            if (adminMenu) {
                if (clickType == ClickType.LEFT) {
                    getHandler().openMenu(player, new RecipePreview(getHandler(), recipe));
                } else if (clickType == ClickType.RIGHT) {
                    ArtMap.getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, recipe.getItem()));
                }
            } else {
                getHandler().openMenu(player, new RecipePreview(getHandler(), recipe));
            }
        }
    }
}

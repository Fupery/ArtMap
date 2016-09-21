package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Menu.API.BasicMenu;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.CloseButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RecipeMenu extends BasicMenu implements ChildMenu {

    private boolean adminMenu;

    public RecipeMenu(boolean adminMenu) {
        super(ChatColor.DARK_BLUE + Lang.MENU_RECIPE.get(), InventoryType.HOPPER);
        this.adminMenu = adminMenu;
    }

    @Override
    public Button[] getButtons() {
        return new Button[]{
                new StaticButton(Material.SIGN, Lang.Array.INFO_RECIPES.get()),
                new RecipeButton(ArtMaterial.EASEL),
                new RecipeButton(ArtMaterial.CANVAS),
                new RecipeButton(ArtMaterial.PAINT_BUCKET),
                new CloseButton()
        };
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenuHandler().MENU.HELP.get(viewer);
    }


    private class RecipeButton extends Button {

        final ArtMaterial recipe;

        public RecipeButton(ArtMaterial recipe) {
            super(recipe.getItem().getType());
            this.recipe = recipe;
            ItemMeta meta = recipe.getItem().getItemMeta();
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
                    ArtMap.getMenuHandler().openMenu(player, new RecipePreview(recipe));
                } else if (clickType == ClickType.RIGHT) {
                    ItemStack leftOver = player.getInventory().addItem(recipe.getItem()).get(0);
                    if (leftOver != null) ArtMap.getTaskManager().SYNC.run(() ->
                            player.getWorld().dropItemNaturally(player.getLocation(), leftOver));
                }
            } else {
                ArtMap.getMenuHandler().openMenu(player, new RecipePreview(recipe));
            }
        }
    }
}

package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.API.StoragePattern;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.CloseButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Templates.BasicMenu;
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
    private final MenuTemplate parent;

    public RecipeMenu(MenuTemplate parent) {
        super(ChatColor.DARK_BLUE + ArtMap.getLang().getMsg("MENU_RECIPE"),
                InventoryType.HOPPER, StoragePattern.JUST_IN_TIME);
        this.parent = parent;
    }

    @Override
    public Button[] getButtons(Player viewer) {
        boolean adminButton = viewer.hasPermission("artmap.admin");
        return new Button[]{
                new StaticButton(Material.SIGN, ArtMap.getLang().getArray("INFO_RECIPES")),
                new RecipeButton(ArtMaterial.EASEL, adminButton),
                new RecipeButton(ArtMaterial.CANVAS, adminButton),
                new RecipeButton(ArtMaterial.PAINT_BUCKET, adminButton),
                new CloseButton()
        };
    }

    @Override
    public MenuTemplate getParent() {
        return parent;
    }

    private static class RecipeButton extends Button {

        final ArtMaterial recipe;
        final boolean adminButton;

        public RecipeButton(ArtMaterial recipe, boolean adminButton) {
            super(recipe.getItem().getType());
            this.recipe = recipe;
            ItemMeta meta = recipe.getItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.set(lore.size() - 1, ChatColor.GREEN + ArtMap.getLang().getMsg("RECIPE_BUTTON"));
            if (adminButton) lore.add(lore.size(), ChatColor.GOLD + ArtMap.getLang().getMsg("ADMIN_RECIPE"));
            meta.setLore(lore);
            setItemMeta(meta);
            this.adminButton = adminButton;
        }

        @Override
        public void onClick(CacheableMenu menu, Player player, ClickType clickType) {
            if (adminButton) {
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

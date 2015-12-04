package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RecipeMenu extends InventoryMenu {

    private static MenuButton buttons[] = generateButtons();

    RecipeMenu(InventoryMenu parent) {
        super(parent.plugin, parent, "§1Choose a Recipe", InventoryType.HOPPER, buttons);
    }

    private static MenuButton[] generateButtons() {
        MenuButton[] buttons = new MenuButton[5];
        buttons[0] = new RecipeButton(ArtMaterial.EASEL);
        buttons[1] = new RecipeButton(ArtMaterial.CANVAS);
        buttons[2] = new RecipeButton(ArtMaterial.CARBON_PAPER);
        buttons[3] = new RecipeButton(ArtMaterial.PAINT_BUCKET);
        buttons[4] = new CloseButton();
        return buttons;
    }

    public static Inventory recipePreview(Player player, ArtMaterial recipe) {
        ItemStack[] ingredients = recipe.getPreview();

        Inventory inventory = Bukkit.createInventory(player, InventoryType.WORKBENCH,
                String.format(ArtMap.Lang.RECIPE_HEADER.rawMessage(),
                        recipe.name().toLowerCase()));

        for (int i = 0; i < ingredients.length; i++) {
            inventory.setItem(i + 1, ingredients[i]);
        }
        inventory.setItem(0, recipe.getItem());
        return inventory;
    }

    private static class RecipeButton extends MenuButton {

        ArtMaterial recipe;

        public RecipeButton(ArtMaterial recipe) {
            super(recipe.getItem().getType());
            this.recipe = recipe;
            ItemMeta meta = recipe.getItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.set(3, HelpMenu.click + " Recipe");
            meta.setLore(lore);
            setItemMeta(meta);
        }

        @Override
        public void onClick(Player player) {
            player.closeInventory();

            if (player.hasPermission("artmap.admin")) {
                Preview.inventory(getMenu().getPlugin(), player,
                        recipePreview(player, recipe));
                player.updateInventory();

            } else {
                ItemStack leftOver = player.getInventory().addItem(recipe.getItem()).get(0);

                if (leftOver != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                }
            }
        }
    }
}

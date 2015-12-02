package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.Command.CommandRecipe;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RecipeMenu extends InventoryMenu {

    private static MenuButton buttons[] = generateButtons();

    RecipeMenu(InventoryMenu parent) {
        super(parent.plugin, parent, parent.getPlayer(),
                "§1Choose a Recipe", InventoryType.HOPPER, buttons);
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

    private static class RecipeButton extends MenuButton {

        ArtMaterial recipe;

        public RecipeButton(ArtMaterial recipe) {
            super(recipe.getItem().getType());
            this.recipe = recipe;
            ItemMeta meta = recipe.getItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.set(3, HelpMenu.click);
            meta.setLore(lore);
            setItemMeta(meta);
        }

        @Override
        public void run() {
            getMenu().getPlayer().closeInventory();
            Preview.inventory(getMenu().getPlugin(), getPlayer(),
                    CommandRecipe.recipePreview(getPlayer(), recipe));
        }
    }
}

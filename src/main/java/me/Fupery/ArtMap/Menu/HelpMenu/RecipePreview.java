package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Menu.API.BasicMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class RecipePreview extends BasicMenu {

    private final ArtMaterial recipe;

    public RecipePreview(ArtMaterial recipe) {
        super(String.format(Lang.RECIPE_HEADER.get(), recipe.name().toLowerCase()),
                InventoryType.WORKBENCH);
        this.recipe = recipe;
    }

    @Override
    public void onMenuOpenEvent(Player viewer) {
        viewer.updateInventory();
    }

    @Override
    public Button[] getButtons() {
        ItemStack[] preview = recipe.getPreview();
        Button[] buttons = new Button[preview.length + 1];
        buttons[0] = new StaticButton(recipe.getItem());

        for (int i = 0; i < preview.length; i++) {
            buttons[i + 1] = preview[i] != null ? new StaticButton(preview[i]) : null;
        }
        return buttons;
    }
}

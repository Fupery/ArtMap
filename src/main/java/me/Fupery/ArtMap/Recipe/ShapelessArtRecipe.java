package me.Fupery.ArtMap.Recipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;

public class ShapelessArtRecipe implements Recipe {

    private ItemStack result;
    private ArrayList<ArtMaterial> ingredients;

    public ShapelessArtRecipe(ItemStack result) {
        this.result = result;
        ingredients = new ArrayList<>();
    }

    @Override
    public ItemStack getResult() {
        return result;
    }

    public void addIngredient(ArtMaterial ingredient) {
        ingredients.add(ingredient);
    }

    public ArtMaterial getIngredient(int index) {
        return ingredients.get(index);
    }

    public ArrayList<ArtMaterial> getIngredients() {
        return ingredients;
    }
}

package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Recipe {

    CANVAS(new ItemCanvas(), RecipeItem.canvasKey),
    CARBON_PAPER(new ItemCarbonPaper(false), RecipeItem.carbonPaperKey),
    EASEL(new ItemEasel(), RecipeItem.easelKey),
    MAP_ART()
    PAINT_BUCKET(new PaintBucket(ArtDye.BLACK), RecipeItem.paintBucketKey);

    private String itemKey;

    RecipeItem recipeItem;

    Recipe(RecipeItem recipeItem, String itemKey) {
        this.recipeItem = recipeItem;
        this.itemKey = itemKey;
    }

    public static ItemStack getActivatedCarbonPaper() {
        return new ItemCarbonPaper(true);
    }

    public static void setupRecipes() {

        for (Recipe recipe : Recipe.values()) {
            recipe.recipeItem.addRecipe();
        }
    }

    public static Recipe getItemType(ItemStack item) {

        for (Recipe recipe : values()) {

            if (recipe.isItem(item)) {
                return recipe;
            }
        }
        return null;
    }

    public RecipeItem getResult() {
        return recipeItem;
    }

    public String getItemKey() {
        return itemKey;
    }

    public boolean isItem(ItemStack itemStack) {
        ItemStack recipeItem = getResult().getRecipe().getResult();

        if (itemStack.getType() == recipeItem.getType()
                && itemStack.hasItemMeta()) {

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.hasLore() && itemMeta.getLore().get(0).equals(itemKey)) {
                return true;
            }
        }
        return false;
    }
}


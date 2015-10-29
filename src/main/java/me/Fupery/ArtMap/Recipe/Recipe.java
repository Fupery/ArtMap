package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.inventory.ItemStack;

public enum Recipe {

    CANVAS(new ItemCanvas()), CARBON_PAPER(new ItemCarbonPaper(false)),
    EASEL(new ItemEasel()), PAINT_BUCKET(new PaintBucket(ArtDye.BLACK));

    public static final String canvasTitle = "Canvas";
    public static final String paintBucketTitle = "PaintBucket";
    public static final String carbonPaperTitle = "Carbon Paper";

    RecipeItem recipeItem;

    Recipe(RecipeItem recipeItem) {
        this.recipeItem = recipeItem;
    }

    public static ItemStack getActivatedCarbonPaper() {
        return new ItemCarbonPaper(true);
    }

    public RecipeItem getResult() {
        return recipeItem;
    }

    public void setupRecipe() {
        recipeItem.addRecipe();
    }
}


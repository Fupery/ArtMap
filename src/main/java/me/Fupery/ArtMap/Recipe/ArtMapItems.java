package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Item.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

import static me.Fupery.ArtMap.Config.Lang.Array.RECIPE_EASEL;
import static me.Fupery.ArtMap.Config.Lang.Array.RECIPE_PAINTBUCKET;
import static me.Fupery.ArtMap.Config.Lang.RECIPE_CANVAS_NAME;
import static me.Fupery.ArtMap.Config.Lang.RECIPE_EASEL_NAME;
import static me.Fupery.ArtMap.Config.Lang.RECIPE_PAINTBUCKET_NAME;

public enum ArtMapItems {
    EASEL(new CustomItem(Material.ARMOR_STAND, "§b§oArtMap Easel")
            .name(RECIPE_EASEL_NAME)
            .tooltip(RECIPE_EASEL)
            .recipe(loadRecipe("easel"))
    ),
    CANVAS(new CustomItem(Material.PAPER, "§b§oArtMap Canvas")
            .name(RECIPE_CANVAS_NAME)
            .tooltip(RECIPE_EASEL)
            .recipe(loadRecipe("canvas"))
    ),
    MAP_ART(new CustomItem(Material.MAP, "§b§oPlayer Artwork")
    ),
    PAINT_BUCKET(new CustomItem(Material.BUCKET, "§b§oPaint Bucket") {
        @Override
        public void addRecipe() {
            super.addRecipe();
        }
    }
            .name(RECIPE_PAINTBUCKET_NAME)
            .tooltip(RECIPE_PAINTBUCKET)
    );

    private CustomItem item;

    ArtMapItems(CustomItem item) {
        this.item = item;
    }

    public static final String ARTWORK_TAG = ;
    public static final String CANVAS_KEY = ;
    public static final String EASEL_KEY = ;
    public static final String PAINT_BUCKET_KEY = "§b§oPaint Bucket";
    public static final String KIT_KEY = "§b§oArtKit Item";
    public static final String PREVIEW_KEY = "§b§oPreview Artwork";
    public static final String COPY_KEY = "§b§oArtwork Copy";

    private static Recipe loadRecipe(String itemName) {
        try {
            return ArtMap.getRecipeLoader().getRecipe(new ItemEasel(), itemName.toUpperCase());
        } catch (RecipeLoader.InvalidRecipeException e) {
            e.printStackTrace();
            return null;
        }
    }
}

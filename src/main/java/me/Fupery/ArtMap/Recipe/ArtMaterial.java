package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public enum ArtMaterial {

    MAP_ART(new ItemMapArt((short) 0, "Artwork", null)),
    CANVAS(new ItemCanvas()),
    CARBON_PAPER(new ItemCarbonPaper()),
    CARBON_PAPER_FILLED(new ItemCarbonPaperFilled()),
    EASEL(new ItemEasel()),
    PAINT_BUCKET(new PaintBucket(ArtDye.BLACK));

    private final ArtItem artItem;
    private final Recipe recipe;

    ArtMaterial(ArtItem artItem) {
        this.artItem = artItem;
        this.recipe = artItem.getRecipe();
    }

    public static void setupRecipes() {

        for (ArtMaterial material : values()) {
            material.artItem.addRecipe(material);
        }
    }

    public static ArtMaterial getCraftItemType(ItemStack item) {

        for (ArtMaterial material : values()) {

            if (material.isValidMaterial(item)) {
                return material;
            }
        }
        return null;
    }

    public static ItemStack fillCarbonPaper(MapArt artwork) {
        ItemCarbonPaperFilled carbonPaper = (ItemCarbonPaperFilled) CARBON_PAPER_FILLED.getItem().clone();
        ItemMeta meta = carbonPaper.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(0, lore.get(0).replace("Filled", artwork.getTitle()));
        meta.setLore(lore);
        carbonPaper.setItemMeta(meta);
        return carbonPaper;
    }

    public static ArtMaterial[] values(boolean vanillaRecipesOnly) {
        ArrayList<ArtMaterial> recipes = new ArrayList<>();

        for (ArtMaterial material : values()) {

            if (material.getRecipe() == null) {
                continue;
            }
            boolean isArtRecipe = material.getRecipe() instanceof ShapelessArtRecipe;

            if (vanillaRecipesOnly) {
                isArtRecipe = !isArtRecipe;
            }
            if (isArtRecipe) {
                recipes.add(material);
            }
        }
        return recipes.toArray(new ArtMaterial[recipes.size()]);
    }

    public static ItemMapArt getMapArt(short id, String title, OfflinePlayer player) {
        return new ItemMapArt(id, title, player);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public boolean isValidMaterial(ItemStack itemStack) {

        if (itemStack != null
                && itemStack.getType() == artItem.getType()
                && itemStack.hasItemMeta()) {

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.hasLore() && itemMeta.getLore().get(0).contains(artItem.getLoreID())) {
                return true;
            }
        }
        return false;
    }

    public ItemStack getItem() {
        return artItem;
    }

    public ItemStack[] getPreview() {
        return artItem.getPreview();
    }
}

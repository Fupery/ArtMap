package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;


public enum ArtMaterial {

    MAP_ART(new ItemMapArt((short) 0, "Artwork", null, null)),
    CANVAS(new ItemCanvas()),
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

    public static ItemMapArt getMapArt(short id, String title, OfflinePlayer player, String date) {
        return new ItemMapArt(id, title, player, date);
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

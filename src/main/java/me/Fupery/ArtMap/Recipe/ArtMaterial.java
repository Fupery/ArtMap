package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Colour.ArtDye;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import static me.Fupery.ArtMap.Config.Lang.Array.RECIPE_EASEL;
import static me.Fupery.ArtMap.Config.Lang.RECIPE_CANVAS_NAME;
import static me.Fupery.ArtMap.Config.Lang.RECIPE_EASEL_NAME;

public enum ArtMaterial {

    EASEL, CANVAS, MAP_ART, PAINT_BUCKET;

    private CustomItem artItem;

    public static void setupRecipes() {
        EASEL.artItem = new ArtItem.CraftableItem("EASEL", Material.ARMOR_STAND, ArtItem.EASEL_KEY)
                .name(RECIPE_EASEL_NAME)
                .tooltip(RECIPE_EASEL);

        CANVAS.artItem = new ArtItem.CraftableItem("CANVAS", Material.PAPER, ArtItem.CANVAS_KEY)
                .name(RECIPE_CANVAS_NAME)
                .tooltip(RECIPE_EASEL);

        MAP_ART.artItem = new ArtItem.ArtworkItem((short) -1, "Artwork", null, null);

        PAINT_BUCKET.artItem = new ArtItem.DyeBucket(null) {
            @Override
            public void addRecipe() {
                for (ArtDye d : ArtMap.getColourPalette().getDyes()) {
                    new ArtItem.DyeBucket(d).addRecipe();
                }
            }
        };
        for (ArtMaterial material : values()) material.artItem.addRecipe();
    }

    public static ArtMaterial getCraftItemType(ItemStack item) {
        for (ArtMaterial material : values()) {
            if (material.artItem.checkItem(item)) return material;
        }
        return null;
    }

    public static ArtItem.ArtworkItem getMapArt(short id, String title, OfflinePlayer player, String date) {
        return new ArtItem.ArtworkItem(id, title, player, date);
    }

    public Material getType() {
        return artItem.getMaterial();
    }

    public short getDurability() {
        return artItem.getDurability();
    }

    public ItemStack getItem() {
        return artItem.toItemStack();
    }

    public boolean isValidMaterial(ItemStack item) {
        return artItem.checkItem(item);
    }

    public Recipe getRecipe() {
        return artItem.getBukkitRecipe();
    }

    public ItemStack[] getPreview() {
        return artItem.getRecipe().getPreview();
    }
}

package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Item.CustomItem;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import static me.Fupery.ArtMap.Config.Lang.Array.RECIPE_EASEL;
import static me.Fupery.ArtMap.Config.Lang.RECIPE_CANVAS_NAME;
import static me.Fupery.ArtMap.Config.Lang.RECIPE_EASEL_NAME;

public enum ArtMaterial {

    EASEL(new ArtItem.CraftableItem("EASEL", Material.ARMOR_STAND, "§b§oArtMap Easel")
            .name(RECIPE_EASEL_NAME).tooltip(RECIPE_EASEL)),

    CANVAS(new ArtItem.CraftableItem("CANVAS", Material.PAPER, "§b§oArtMap Canvas")
            .name(RECIPE_CANVAS_NAME).tooltip(RECIPE_EASEL)),

    MAP_ART(new ArtItem.ArtworkItem((short) 0, "Artwork", null, null)),

    PAINT_BUCKET(new ArtItem.DyeBucket(ArtMap.getColourPalette().WHITE) {
        @Override
        public void addRecipe() {
            for (Palette.Dye d : ArtMap.getColourPalette().getDyes()) {
                new ArtItem.DyeBucket(d).addRecipe();
            }
        }
    });

    private final CustomItem artItem;

    ArtMaterial(CustomItem artItem) {
        this.artItem = artItem;
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

    public static void setupRecipes() {
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

    public Recipe getRecipe() {
        return artItem.getBukkitRecipe();
    }

    public ItemStack[] getPreview() {
        return artItem.getRecipe().getPreview();
    }
}

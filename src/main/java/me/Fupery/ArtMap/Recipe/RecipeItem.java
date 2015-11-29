package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

abstract public class RecipeItem extends ItemStack {

    public static final String canvasKey = "Canvas";
    public static final String carbonPaperKey = "Carbon Paper";
    public static final String easelKey = "Easel";
    public static final String paintBucketKey = "PaintBucket";

    public RecipeItem(Material material) {
        super(material);
    }

    public abstract org.bukkit.inventory.Recipe getRecipe();

    public void addRecipe() {
        Bukkit.getServer().addRecipe(getRecipe());
    }

    public ItemStack[] getPreview() {
        ItemStack[] ingredients = new ItemStack[9];

        org.bukkit.inventory.Recipe itemRecipe = getRecipe();

        if (itemRecipe instanceof ShapedRecipe) {
            ShapedRecipe shaped = (ShapedRecipe) itemRecipe;
            String[] shape = shaped.getShape();
            Map<Character, ItemStack> map = shaped.getIngredientMap();

            int i = 0;
            for (String s : shape) {

                for (char c : s.toCharArray()) {

                    ingredients[i] = (map.containsKey(c))
                            ? map.get(c) : new ItemStack(Material.AIR);
                    i++;
                }
            }

        } else if (itemRecipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) itemRecipe;
            List<ItemStack> list = shapeless.getIngredientList();

            for (int i = 0; i < list.size(); i++) {
                ingredients[i] = list.get(i);
            }
        }
        return ingredients;
    }
}

class ItemEasel extends RecipeItem {

    ItemEasel() {
        super(Material.ARMOR_STAND);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(easelKey);
        meta.setLore(Arrays.asList("Used to edit artworks", "Right click to place"));
        setItemMeta(meta);
    }

    @Override
    public org.bukkit.inventory.Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new ItemEasel());
        recipe.shape("*s*", "tft", "lal");
        recipe.setIngredient('s', Material.STICK);
        recipe.setIngredient('t', Material.STRING);
        recipe.setIngredient('f', Material.ITEM_FRAME);
        recipe.setIngredient('l', Material.LEATHER);
        recipe.setIngredient('a', Material.ARMOR_STAND);
        return recipe;
    }
}

class ItemCanvas extends RecipeItem {

    ItemCanvas() {
        super(Material.PAPER);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(canvasKey);
        meta.setLore(Arrays.asList("Use with an Easel", "to create artworks"));
        setItemMeta(meta);
    }

    @Override
    public org.bukkit.inventory.Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new ItemCanvas());
        recipe.shape("lel", "epe", "lel");
        recipe.setIngredient('l', Material.LEATHER);
        recipe.setIngredient('p', Material.EMPTY_MAP);
        recipe.setIngredient('e', Material.EMERALD);
        return recipe;
    }
}

class ItemCarbonPaper extends RecipeItem {

    public ItemCarbonPaper(boolean active) {
        super(active ? Material.PAPER : Material.EMPTY_MAP);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(carbonPaperKey);
        String[] lore = active ?
                new String[]{"§r[Filled]", "Place on an easel", "to edit artwork"} :
                new String[]{"§r[Blank]", "Craft with an artwork", "to create editable copy"};
        meta.setLore(Arrays.asList(lore));
        setItemMeta(meta);
    }

    @Override
    public org.bukkit.inventory.Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new ItemCarbonPaper(false));
        recipe.shape("lel", "epe", "lel");
        recipe.setIngredient('l', Material.COAL);
        recipe.setIngredient('p', Material.PAPER);
        recipe.setIngredient('e', Material.DIAMOND);
        return recipe;
    }
}
class ItemMapArt extends RecipeItem {

    public ItemMapArt() {
        super(Material.MAP);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName("Artwork Copy");
        meta.setLore(Arrays.asList(MapArt.artworkTag, "Use with an Easel", "to create artworks"));
        setItemMeta(meta);
    }

    @Override
    public Recipe getRecipe() {
        return null;
    }
}

class PaintBucket extends RecipeItem {

    PaintBucket(ArtDye colour) {
        super(Material.BUCKET);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(colour.getDisplay() + paintBucketKey);
        meta.setLore(Arrays.asList("§r" + colour.name(), "Combine with dyes to",
                "get different colours", "Use with an Easel and", "Canvas to fill colours"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addUnsafeEnchantment(Enchantment.LUCK, 1);
        setItemMeta(meta);
    }

    @Override
    public org.bukkit.inventory.Recipe getRecipe() {
        return getRecipe(ArtDye.BLACK);
    }

    private org.bukkit.inventory.Recipe getRecipe(ArtDye d) {
        ShapelessRecipe recipe = new ShapelessRecipe(new PaintBucket(d));
        recipe.addIngredient(1, Material.BUCKET);
        recipe.addIngredient(1, d.getRecipeItem());
        return recipe;
    }

    @Override
    public void addRecipe() {
        for (ArtDye d : ArtDye.values()) {
            Bukkit.getServer().addRecipe(getRecipe(d));
        }
    }
}

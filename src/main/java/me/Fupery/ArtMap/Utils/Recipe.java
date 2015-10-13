package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

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

abstract class RecipeItem extends ItemStack {

    public RecipeItem(Material material) {
        super(material);
    }

    public abstract void addRecipe();
}

class ItemEasel extends RecipeItem {

    ItemEasel() {
        super(Material.ARMOR_STAND);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ArtMap.entityTag);
        meta.setLore(Arrays.asList("Used to edit artworks", "Right click to place"));
        setItemMeta(meta);
    }

    @Override
    public void addRecipe() {
        ShapedRecipe easel = new ShapedRecipe(new ItemEasel());
        easel.shape("*s*", "tft", "lal");
        easel.setIngredient('s', Material.STICK);
        easel.setIngredient('t', Material.STRING);
        easel.setIngredient('f', Material.ITEM_FRAME);
        easel.setIngredient('l', Material.LEATHER);
        easel.setIngredient('a', Material.ARMOR_STAND);
        Bukkit.getServer().addRecipe(easel);
    }
}

class ItemCanvas extends RecipeItem {

    ItemCanvas() {
        super(Material.PAPER);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(Recipe.canvasTitle);
        meta.setLore(Arrays.asList("Use with an Easel", "to create artworks"));
        setItemMeta(meta);
    }

    @Override
    public void addRecipe() {
        ShapedRecipe canvas = new ShapedRecipe(new ItemCanvas());
        canvas.shape("lel", "epe", "lel");
        canvas.setIngredient('l', Material.LEATHER);
        canvas.setIngredient('p', Material.EMPTY_MAP);
        canvas.setIngredient('e', Material.EMERALD);
        Bukkit.getServer().addRecipe(canvas);
    }
}

class ItemCarbonPaper extends RecipeItem {

    public ItemCarbonPaper(boolean active) {
        super(active ? Material.PAPER : Material.EMPTY_MAP);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(Recipe.carbonPaperTitle);
        String[] lore = active ?
                new String[]{"§r[Filled]", "Place on an easel", "to edit artwork"} :
                new String[]{"§r[Blank]", "Craft with an artwork", "to create editable copy"};
        meta.setLore(Arrays.asList(lore));
        setItemMeta(meta);
    }

    @Override
    public void addRecipe() {
        ShapedRecipe blankCarbon = new ShapedRecipe(new ItemCarbonPaper(false));
        blankCarbon.shape("lel", "epe", "lel");
        blankCarbon.setIngredient('l', Material.COAL);
        blankCarbon.setIngredient('p', Material.PAPER);
        blankCarbon.setIngredient('e', Material.DIAMOND);
        Bukkit.getServer().addRecipe(blankCarbon);

        ShapelessRecipe carbon = new ShapelessRecipe(new ItemCarbonPaper(true));
        carbon.addIngredient(Material.MAP);
        carbon.addIngredient(1, Material.PAPER);
        Bukkit.getServer().addRecipe(carbon);
    }
}

class PaintBucket extends RecipeItem {

    PaintBucket(ArtDye colour) {
        super(Material.BUCKET);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(colour.getDisplay() + Recipe.paintBucketTitle);
        meta.setLore(Arrays.asList("§r" + colour.name(), "Combine with dyes to",
                "get different colours", "Use with an Easel and", "Canvas to fill colours"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addUnsafeEnchantment(Enchantment.LUCK, 1);
        setItemMeta(meta);
    }

    @Override
    public void addRecipe() {
        for (ArtDye d : ArtDye.values()) {
            ShapelessRecipe paintBucket = new ShapelessRecipe(new PaintBucket(d));
            paintBucket.addIngredient(1, Material.BUCKET);
            paintBucket.addIngredient(1, d.getRecipeItem());
            Bukkit.getServer().addRecipe(paintBucket);
        }
    }
}


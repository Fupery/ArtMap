package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.HashMap;

public enum Recipe {

    CANVAS(new ItemCanvas()), EASEL(new ItemEasel()), PAINT_BUCKET(new PaintBucket(DyeColor.BLACK));

    public static final String canvasTitle = "Canvas";
    public static final String paintBucketTitle = "PaintBucket";
    public static final String voidPaint = "§rVoidPaint";

    RecipeItem recipeItem;


    Recipe(RecipeItem recipeItem) {
        this.recipeItem = recipeItem;
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

class PaintBucket extends RecipeItem {

    private static HashMap<DyeColor, ChatColor> colourWheel;

    static {
        colourWheel = new HashMap<>();
        colourWheel.put(DyeColor.BLACK, ChatColor.WHITE);
        colourWheel.put(DyeColor.BLUE, ChatColor.BLUE);
        colourWheel.put(DyeColor.BROWN, ChatColor.DARK_RED);
        colourWheel.put(DyeColor.CYAN, ChatColor.DARK_AQUA);
        colourWheel.put(DyeColor.GRAY, ChatColor.DARK_GRAY);
        colourWheel.put(DyeColor.GREEN, ChatColor.DARK_GREEN);
        colourWheel.put(DyeColor.LIGHT_BLUE, ChatColor.BLUE);
        colourWheel.put(DyeColor.LIME, ChatColor.GREEN);
        colourWheel.put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE);
        colourWheel.put(DyeColor.ORANGE, ChatColor.GOLD);
        colourWheel.put(DyeColor.PINK, ChatColor.LIGHT_PURPLE);
        colourWheel.put(DyeColor.PURPLE, ChatColor.DARK_PURPLE);
        colourWheel.put(DyeColor.WHITE, ChatColor.WHITE);
        colourWheel.put(DyeColor.YELLOW, ChatColor.YELLOW);
        colourWheel.put(DyeColor.SILVER, ChatColor.GRAY);
        colourWheel.put(DyeColor.RED, ChatColor.RED);
    }

    PaintBucket(DyeColor colour) {
        super(Material.BUCKET);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(colourWheel.get(colour) + Recipe.paintBucketTitle);
        meta.setLore(Arrays.asList("§r" + colour.name(), "Combine with dyes to",
                "get different colours", "Use with an Easel and", "Canvas to fill colours"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addUnsafeEnchantment(Enchantment.LUCK, 1);
        setItemMeta(meta);
    }

    //void bucket fills with empty pixels
    PaintBucket() {
        super(Material.BUCKET);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + Recipe.paintBucketTitle);
        meta.setLore(Arrays.asList(Recipe.voidPaint, "Combine with dyes to",
                "get different colours", "Use with an Easel and", "Canvas to fill colours"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addUnsafeEnchantment(Enchantment.LUCK, 1);
        setItemMeta(meta);
    }

    @Override
    public void addRecipe() {
        for (DyeColor d : DyeColor.values()) {
            ShapelessRecipe paintBucket = new ShapelessRecipe(new PaintBucket(d));
            paintBucket.addIngredient(1, Material.BUCKET);
            paintBucket.addIngredient(1,
                    new MaterialData(Material.INK_SACK, (byte) (15 - d.ordinal())));
            Bukkit.getServer().addRecipe(paintBucket);
        }
        ShapelessRecipe voidBucket = new ShapelessRecipe(new PaintBucket());
        voidBucket.addIngredient(1, Material.BUCKET);
        voidBucket.addIngredient(1, Material.EYE_OF_ENDER);
        Bukkit.getServer().addRecipe(voidBucket);
    }
}


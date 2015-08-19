package me.Fupery.Artiste;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Recipe {

    public static void addEasel() {
        ShapedRecipe easel = new ShapedRecipe(new ItemEasel());
        easel.shape("*s*", "tft", "lal");
        easel.setIngredient('s', Material.STICK);
        easel.setIngredient('t', Material.STRING);
        easel.setIngredient('f', Material.ITEM_FRAME);
        easel.setIngredient('l', Material.LEATHER);
        easel.setIngredient('a', Material.ARMOR_STAND);
        Bukkit.getServer().addRecipe(easel);
    }

    public static void addCanvas() {
        ShapedRecipe canvas = new ShapedRecipe(new ItemCanvas());
        canvas.shape("lll", "lpl", "lll");
        canvas.setIngredient('l', Material.LEATHER);
        canvas.setIngredient('p', Material.PAINTING);
        Bukkit.getServer().addRecipe(canvas);
    }
}

class ItemEasel extends ItemStack {

    ItemEasel() {
        super(Material.ARMOR_STAND);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(Artiste.entityTag);
        meta.setLore(Arrays.asList("Used to edit artworks", "Right click to place"));
        setItemMeta(meta);
    }
}

class ItemCanvas extends ItemStack {

    ItemCanvas() {
        super(Material.PAINTING);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName("Canvas");
        meta.setLore(Arrays.asList("Use with an Easel", "to create artworks"));
        setItemMeta(meta);
    }
}

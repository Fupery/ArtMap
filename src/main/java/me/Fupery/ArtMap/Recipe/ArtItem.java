package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

abstract public class ArtItem extends ItemStack {

    public static final String artworkTag = "§b§oPlayer Artwork";
    public static final String canvasKey = "§b§oArtMap Canvas";
    public static final String easelKey = "§b§oArtMap Easel";
    public static final String paintBucketKey = "§b§oPaint Bucket";
    public static final String help = "§a/artmap §2for help";

    ArtItem(Material material) {
        super(material);
    }

    ArtItem(Material material, int amount, short durability) {
        super(material, amount, durability);
    }

    abstract org.bukkit.inventory.Recipe getRecipe();

    abstract String getLoreID();

    void addRecipe(ArtMaterial material) {
        Bukkit.getServer().addRecipe(material.getRecipe());
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

class ItemEasel extends ArtItem {

    ItemEasel() {
        super(Material.ARMOR_STAND);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName("§e•§6§lEasel§e•");
        meta.setLore(Arrays.asList(getLoreID(), "§7Use to edit artworks", "§7Right click to place", help));
        setItemMeta(meta);
    }

    @Override
    org.bukkit.inventory.Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new ItemEasel());
        recipe.shape("*s*", "tft", "lal");
        recipe.setIngredient('s', Material.STICK);
        recipe.setIngredient('t', Material.STRING);
        recipe.setIngredient('f', Material.ITEM_FRAME);
        recipe.setIngredient('l', Material.LEATHER);
        recipe.setIngredient('a', Material.ARMOR_STAND);
        return recipe;
    }

    @Override
    String getLoreID() {
        return easelKey;
    }
}

class ItemCanvas extends ArtItem {

    ItemCanvas() {
        super(Material.PAPER);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName("§e•§6§lCanvas§e•");
        meta.setLore(Arrays.asList(getLoreID(), "§7Use on an §aEasel", "§7to start painting", help));
        setItemMeta(meta);
    }

    @Override
    org.bukkit.inventory.Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new ItemCanvas());
        recipe.shape("lel", "epe", "lel");
        recipe.setIngredient('l', Material.LEATHER);
        recipe.setIngredient('p', Material.EMPTY_MAP);
        recipe.setIngredient('e', Material.EMERALD);
        return recipe;
    }

    @Override
    String getLoreID() {
        return canvasKey;
    }
}

class ItemMapArt extends ArtItem {

    ItemMapArt(short id, String title, OfflinePlayer player, String date) {
        super(Material.MAP, 1, id);
        Date d = new Date();
        String name = player != null ? player.getName() : "Player";

        ItemMeta meta = getItemMeta();

        meta.setDisplayName(title);

        meta.setLore(Arrays.asList(
                getLoreID(),
                ChatColor.GOLD + "by " + ChatColor.YELLOW + name,
                ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + date));
        setItemMeta(meta);
    }

    @Override
    Recipe getRecipe() {
        return null;
    }

    @Override
    String getLoreID() {
        return artworkTag;
    }
}

class PaintBucket extends ArtItem {

    ArtDye colour;

    PaintBucket(ArtDye colour) {
        super(Material.BUCKET);
        this.colour = colour;
        ItemMeta meta = getItemMeta();
        meta.setDisplayName("§e•" + colour.getDisplay() + "§lPaintBucket§e•");
        meta.setLore(Arrays.asList(getLoreID(),
                "§7Use with an §aEasel §7and", "§aCanvas §7to fill colours", help));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        addUnsafeEnchantment(Enchantment.LUCK, 1);
        setItemMeta(meta);
    }

    @Override
    org.bukkit.inventory.Recipe getRecipe() {
        return getRecipe(ArtDye.BLACK);
    }

    @Override
    String getLoreID() {
        return paintBucketKey + " §7[" + colour.name() + "]";
    }

    private org.bukkit.inventory.Recipe getRecipe(ArtDye d) {
        ShapelessRecipe recipe = new ShapelessRecipe(new PaintBucket(d));
        recipe.addIngredient(1, Material.BUCKET);
        recipe.addIngredient(1, d.getRecipeItem());
        return recipe;
    }

    @Override
    void addRecipe(ArtMaterial material) {
        for (ArtDye d : ArtDye.values()) {
            Bukkit.getServer().addRecipe(getRecipe(d));
        }
    }
}

package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.ArtDye;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

abstract public class ArtItem extends ItemStack {

    public static final String ARTWORK_TAG = "§b§oPlayer Artwork";
    public static final String CANVAS_KEY = "§b§oArtMap Canvas";
    public static final String EASEL_KEY = "§b§oArtMap Easel";
    public static final String PAINT_BUCKET_KEY = "§b§oPaint Bucket";
    public static final String HELP = ArtMap.getLang().getMsg("RECIPE_HELP");

    ArtItem(Material material) {
        super(material);
    }

    ArtItem(Material material, int amount, short durability) {
        super(material, amount, durability);
    }

    static List<String> getToolTipLore(String ID, String[] toolTip) {
        ArrayList<String> lore = new ArrayList<>(Arrays.asList(toolTip));
        lore.add(0, ID);
        lore.add(HELP);
        return lore;
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
        String itemName = "§e•§6§l" + ArtMap.getLang().getMsg("RECIPE_EASEL_NAME") + "§e•";
        meta.setDisplayName(itemName);
        meta.setLore(getToolTipLore(getLoreID(), ArtMap.getLang().getArray("RECIPE_EASEL")));
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
        return EASEL_KEY;
    }
}

class ItemCanvas extends ArtItem {

    ItemCanvas() {
        super(Material.PAPER);
        ItemMeta meta = getItemMeta();
        String itemName = "§e•§6§l" + ArtMap.getLang().getMsg("RECIPE_CANVAS_NAME") + "§e•";
        meta.setDisplayName(itemName);
        meta.setLore(getToolTipLore(getLoreID(), ArtMap.getLang().getArray("RECIPE_CANVAS")));
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
        return CANVAS_KEY;
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
        return ARTWORK_TAG;
    }
}

class PaintBucket extends ArtItem {

    private final ArtDye colour;

    PaintBucket(ArtDye colour) {
        super(Material.BUCKET);
        this.colour = colour;
        ItemMeta meta = getItemMeta();
        String itemName = "§e•" + colour.getDisplay() + "§l"
                + ArtMap.getLang().getMsg("RECIPE_PAINTBUCKET_NAME") + "§e•";
        meta.setDisplayName(itemName);
        meta.setLore(getToolTipLore(getLoreID(), ArtMap.getLang().getArray("RECIPE_PAINTBUCKET")));
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
        return PAINT_BUCKET_KEY + " §7[" + colour.name() + "]";
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

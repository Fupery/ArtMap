package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.ref.WeakReference;
import java.util.*;

abstract public class ArtItem extends ItemStack {

    public static final String ARTWORK_TAG = "§b§oPlayer Artwork";
    public static final String CANVAS_KEY = "§b§oArtMap Canvas";
    public static final String EASEL_KEY = "§b§oArtMap Easel";
    public static final String PAINT_BUCKET_KEY = "§b§oPaint Bucket";
    public static final String KIT_KEY = "§b§oArtKit Item";
    public static final String HELP = ArtMap.getLang().getMsg("RECIPE_HELP");
    private static WeakReference<ItemStack[]> kitReference = new WeakReference<>(getKit());

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

    public static ItemStack[] getKit() {
        if (kitReference != null && kitReference.get() != null) return kitReference.get().clone();
        ItemStack[] itemStack = new ItemStack[36];
        Arrays.fill(itemStack, new ItemStack(Material.AIR));
        for (int i = 0; i < 25; i++) {
            ArtDye dye = ArtDye.values()[i];
            Material material = dye.getRecipeItem().getItemType();
            String name = dye.getDisplay() + dye.name().toLowerCase();
            itemStack[i] = getKitItem(name, material, dye.getRecipeItem().getData());
            itemStack[i].setAmount(16);
        }
        itemStack[25] = getKitItem("§lFeather", Material.FEATHER);
        itemStack[26] = getKitItem("§7§lCoal", Material.COAL);
        itemStack[27] = getKitItem("§6§lCompass", Material.COMPASS);
        ItemStack bucket = new PaintBucket(ArtDye.BLACK);
        bucket.setAmount(16);
        ItemMeta bucketMeta = bucket.getItemMeta();
        List<String> bucketLore = bucketMeta.getLore();
        bucketLore.add(ArtItem.KIT_KEY);
        bucketMeta.setLore(bucketLore);
        bucket.setItemMeta(bucketMeta);
        itemStack[28] = bucket;
        kitReference = new WeakReference<>(itemStack);
        return kitReference.get();
    }

    private static ItemStack getKitItem(String name, Material material, byte durability) {
        ItemStack item = new ItemStack(material, 1, durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(ArtItem.KIT_KEY));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getKitItem(String name, Material material) {
        return getKitItem(name, material, (byte) 0);
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
        try {
            return ArtMap.getRecipeLoader().getRecipe(new ItemEasel(), "EASEL");
        } catch (RecipeLoader.InvalidRecipeException e) {
            e.printStackTrace();
            return null;
        }
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
        try {
            return ArtMap.getRecipeLoader().getRecipe(new ItemCanvas(), "CANVAS");
        } catch (RecipeLoader.InvalidRecipeException e) {
            e.printStackTrace();
            return null;
        }
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

package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Colour.ArtDye;
import me.Fupery.ArtMap.Colour.Palette;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import static me.Fupery.ArtMap.Config.Lang.Array.RECIPE_PAINTBUCKET;
import static me.Fupery.ArtMap.Config.Lang.RECIPE_PAINTBUCKET_NAME;
import static org.bukkit.ChatColor.*;

public class ArtItem {

    public static final String ARTWORK_TAG = "§b§oPlayer Artwork";
    public static final String CANVAS_KEY = "§b§oArtMap Canvas";
    public static final String EASEL_KEY = "§b§oArtMap Easel";
    public static final String PAINT_BUCKET_KEY = "§b§oPaint Bucket";
    public static final String KIT_KEY = "§8[ArtKit]";
    public static final String PREVIEW_KEY = "§b§oPreview Artwork";
    public static final String COPY_KEY = "§b§oArtwork Copy";
    private static WeakReference<ItemStack[]> kitReference = new WeakReference<>(getArtKit());

    public static ItemStack[] getArtKit() {
        if (kitReference != null && kitReference.get() != null) return kitReference.get().clone();
        Palette palette = ArtMap.getColourPalette();
        ItemStack[] itemStack = new ItemStack[36];
        Arrays.fill(itemStack, new ItemStack(Material.AIR));

        for (int i = 0; i < 25; i++) {
            ArtDye dye = palette.getDyes()[i];
            itemStack[i] = new KitItem(dye.getMaterial(), dye.getDurability(), dye.name()).toItemStack();
        }
        itemStack[25] = new KitItem(Material.FEATHER, "§lFeather").toItemStack();
        itemStack[26] = new KitItem(Material.COAL, "§7§lCoal").toItemStack();
        itemStack[27] = new KitItem(Material.COMPASS, "§6§lCompass").toItemStack();
        itemStack[28] = ItemUtils.addKey(new DyeBucket(palette.getDefaultColour()).toItemStack(), KIT_KEY);
        kitReference = new WeakReference<>(itemStack);
        return kitReference.get();
    }

    static class CraftableItem extends CustomItem {

        public CraftableItem(String itemName, Material material, String uniqueKey) {
            super(material, uniqueKey);
            try {
                recipe(ArtMap.getRecipeLoader().getRecipe(itemName.toUpperCase()));
            } catch (RecipeLoader.InvalidRecipeException e) {
                e.printStackTrace();
            }
        }

        @Override
        public CustomItem name(Lang name) {
            return super.name("§e•§6§l" + name.get() + "§e•");
        }
    }

    public static class DyeBucket extends CustomItem {
        DyeBucket(ArtDye dye) {
            super(Material.BUCKET, bucketKey(dye));
            if (dye == null) dye = ArtMap.getColourPalette().getDefaultColour();
            name(bucketName(dye));
            tooltip(RECIPE_PAINTBUCKET.get());
            flag(ItemFlag.HIDE_ENCHANTS);
            enchant(Enchantment.LUCK, 1);
            recipe(new SimpleRecipe.Shapeless()
                    .add(Material.BUCKET)
                    .add(dye.getMaterial(), dye.getDurability()));
        }

        public static ArtDye getColour(Palette palette, ItemStack bucket) {
            if (bucket.getType() == Material.BUCKET && bucket.hasItemMeta() && bucket.getItemMeta().hasLore()) {
                ItemMeta meta = bucket.getItemMeta();
                String key = meta.getLore().get(0);

                for (ArtDye dye : palette.getDyes()) {
                    if (key.equals(bucketKey(dye))) {
                        return dye;
                    }
                }
            }
            return null;
        }

        private static String bucketKey(ArtDye dye) {
            return dye == null ? PAINT_BUCKET_KEY : PAINT_BUCKET_KEY + " §7[" + dye.rawName() + "]";
        }

        private static String bucketName(ArtDye dye) {
            return String.format("§e•%s§l%s§e•", dye.getDisplayColour(), RECIPE_PAINTBUCKET_NAME.get());
        }
    }

    public static class ArtworkItem extends CustomItem {
        public ArtworkItem(short id, String title, OfflinePlayer player, String date) {
            super(Material.MAP, ARTWORK_TAG, id);
            String name = player != null ? player.getName() : "Player";
            name(title);
            tooltip(GOLD + "by " + YELLOW + name, DARK_GREEN + "" + ITALIC + date);
        }
    }

    public static class KitItem extends CustomItem {
        KitItem(Material material, String name) {
            super(material, KIT_KEY, name);
        }

        KitItem(Material material, int durability, String name) {
            super(material, KIT_KEY, durability);
            name(name);
        }
    }
}

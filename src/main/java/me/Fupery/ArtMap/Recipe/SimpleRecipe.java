package me.Fupery.ArtMap.Recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class SimpleRecipe {

    public abstract Recipe toBukkitRecipe(ItemStack result);

    public abstract ItemStack[] getPreview();

    public static class Shaped extends SimpleRecipe {

        private HashMap<Character, SimpleItem> items = new HashMap<>();
        private String[] shape;

        public Shaped shape(String... rows) {
            if (rows.length != 3) throw new RecipeException("A recipe shape must have exactly 3 rows.");
            for (String row : rows) if (row.length() != 3) throw new RecipeException("Recipe row length must be 3.");
            shape = rows;
            return this;
        }

        public Shaped set(char key, Material material, int durability) {
            return set(key, material, durability);
        }

        public Shaped set(char key, Material material) {
            return set(key, material, -1);
        }

        @Override
        public Recipe toBukkitRecipe(ItemStack result) {
            ShapedRecipe recipe = new ShapedRecipe(result);
            recipe.shape(shape);
            for (Character c : items.keySet()) {
                SimpleItem item = items.get(c);
                recipe.setIngredient(c, item.material, item.durability);
            }
            return recipe;
        }

        @Override
        public ItemStack[] getPreview() {
            ItemStack[] preview = emptyCraftingTable();
            int i = 0;
            for (String s : shape) {
                for (char c : s.toCharArray()) {
                    if (items.containsKey(c)) preview[i] = items.get(c).toItemStack();
                    i++;
                }
            }
            return preview;
        }
    }

    public static class Shapeless extends SimpleRecipe {

        private ArrayList<SimpleItem> items = new ArrayList<>();

        public Shapeless add(Material material, int durability, int count) {
            items.add(new SimpleItem(material, durability, count));
            return this;
        }

        public Shapeless add(Material material, int durability) {
            return add(material, durability, 1);
        }

        public Shapeless add(Material material) {
            return add(material, -1, 1);
        }

        @Override
        public Recipe toBukkitRecipe(ItemStack result) {
            ShapelessRecipe recipe = new ShapelessRecipe(result);
            for (SimpleItem item : items) {
                recipe.addIngredient(item.count, item.material, item.durability);
            }
            return recipe;
        }

        @Override
        public ItemStack[] getPreview() {
            ItemStack[] preview = emptyCraftingTable();
            for (int i = 0; i < 9; i++) {
                SimpleItem item = items.get(i);
                preview[i] = item.toItemStack();
            }
            return preview;
        }
    }

    private class RecipeException extends RuntimeException {
        private RecipeException(String message) {
            super(message);
        }
    }
    private static ItemStack[] emptyCraftingTable() {
        ItemStack[] preview = new ItemStack[9];
        Arrays.fill(preview, Material.AIR);
        return preview;
    }

    private static class SimpleItem {
        private final Material material;
        private final short durability;
        private final int count;

        SimpleItem(Material material, int durability, int count) {
            this.material = material;
            this.durability = (short) durability;
            this.count = count;
        }

        ItemStack toItemStack() {
            return new ItemStack(material, count, durability);
        }
    }

}

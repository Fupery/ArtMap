package me.Fupery.ArtMap.Recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SimpleRecipe {

    public abstract Recipe toBukkitRecipe(ItemStack result);

    public abstract ItemStack[] getPreview();

    public static class Shaped extends SimpleRecipe {

        private HashMap<Character, WrappedMaterial> items = new HashMap<>();
        private String[] shape;

        public Shaped shape(String... rows) {
            if (rows.length != 3) throw new RecipeException("A recipe shape must have exactly 3 rows.");
            for (String row : rows) if (row.length() != 3) throw new RecipeException("Recipe row length must be 3.");
            shape = rows;
            return this;
        }

        public Shaped set(char key, Material material, int durability) {
            items.put(key, new WrappedMaterial(material, durability, 1));
            return this;
        }

        public Shaped set(char key, Material material) {
            return set(key, material, -1);
        }

        public Shaped set(char key, WrappedMaterial material) {
            items.put(key, material);
            return this;
        }

        @Override
        public Recipe toBukkitRecipe(ItemStack result) {
            ShapedRecipe recipe = new ShapedRecipe(result);
            recipe.shape(shape);
            for (Character c : items.keySet()) {
                WrappedMaterial item = items.get(c);
                recipe.setIngredient(c, item.getMaterial(), item.getDurability());
            }
            return recipe;
        }

        @Override
        public ItemStack[] getPreview() {
            ItemStack[] preview = new ItemStack[9];
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

        private ArrayList<WrappedMaterial> items = new ArrayList<>();

        public Shapeless add(Material material, int durability, int amount) {
            items.add(new WrappedMaterial(material, durability, amount));
            return this;
        }

        public Shapeless add(Material material, int durability) {
            return add(material, durability, 1);
        }

        public Shapeless add(Material material) {
            return add(material, -1, 1);
        }

        public Shapeless add(WrappedMaterial material) {
            return add(material.getMaterial(), material.getDurability(), material.getAmount());
        }

        @Override
        public Recipe toBukkitRecipe(ItemStack result) {
            ShapelessRecipe recipe = new ShapelessRecipe(result);
            for (WrappedMaterial item : items) {
                recipe.addIngredient(item.getAmount(), item.getMaterial(), item.getDurability());
            }
            return recipe;
        }

        @Override
        public ItemStack[] getPreview() {
            ItemStack[] preview = new ItemStack[9];
            for (int i = 0; i < 9 && i < items.size(); i++) {
                WrappedMaterial item = items.get(i);
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

}

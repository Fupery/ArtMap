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

        private HashMap<Character, Ingredient> items = new HashMap<>();
        private String[] shape;

        public Shaped shape(String... rows) {
            if (rows.length != 3) throw new RecipeException("A recipe shape must have exactly 3 rows.");
            for (String row : rows) if (row.length() != 3) throw new RecipeException("Recipe row length must be 3.");
            shape = rows;
            return this;
        }

        public Shaped set(char key, Material material, int durability) {
            items.put(key, new Ingredient.WrappedMaterial(material, durability, 1));
            return this;
        }

        public Shaped set(char key, Material material) {
            return set(key, material, -1);
        }

        public Shaped set(char key, Ingredient ingredient) {
            items.put(key, ingredient);
            return this;
        }

        @Override
        public Recipe toBukkitRecipe(ItemStack result) {
            ShapedRecipe recipe = new ShapedRecipe(result);
            recipe.shape(shape);
            for (Character c : items.keySet()) {
                Ingredient item = items.get(c);
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

        private ArrayList<Ingredient> items = new ArrayList<>();

        public Shapeless add(Material material, int durability, int amount) {
            items.add(new Ingredient.WrappedMaterial(material, durability, amount));
            return this;
        }

        public Shapeless add(Material material, int durability) {
            return add(material, durability, 1);
        }

        public Shapeless add(Material material) {
            return add(material, -1, 1);
        }

        public Shapeless add(Ingredient ingredient) {
            return add(ingredient.getMaterial(), ingredient.getDurability(), ingredient.getAmount());
        }

        @Override
        public Recipe toBukkitRecipe(ItemStack result) {
            ShapelessRecipe recipe = new ShapelessRecipe(result);
            for (Ingredient item : items) {
                recipe.addIngredient(item.getAmount(), item.getMaterial(), item.getDurability());
            }
            return recipe;
        }

        @Override
        public ItemStack[] getPreview() {
            ItemStack[] preview = new ItemStack[9];
            for (int i = 0; i < 9 && i < items.size(); i++) {
                Ingredient item = items.get(i);
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

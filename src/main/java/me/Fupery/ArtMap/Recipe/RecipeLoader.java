package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RecipeLoader {
    private final FileConfiguration recipeFile;

    public RecipeLoader(ArtMap plugin, Configuration configuration) {
        this.recipeFile = loadRecipeFile(plugin, configuration.CUSTOM_RECIPES);
    }

    private static FileConfiguration loadRecipeFile(ArtMap plugin, boolean customRecipes) {
        String fileName = "recipe.yml";
        FileConfiguration defaultValues = YamlConfiguration.loadConfiguration(plugin.getTextResourceFile(fileName));
        if (!customRecipes) {
            return defaultValues;
        } else {
            File file = new File(plugin.getDataFolder(), fileName);
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) return defaultValues;
                    Files.copy(plugin.getResource(fileName), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    plugin.getLogger().info(String.format("Failed to build %s file", fileName));
                    return defaultValues;
                }
            }
            return YamlConfiguration.loadConfiguration(file);
        }
    }

    public void unloadRecipes() {
        Iterator<Recipe> i = Bukkit.getServer().recipeIterator();
        while (i.hasNext()) {
            Recipe recipe = i.next();
            ItemStack result = recipe.getResult();
            ItemMeta meta = result.getItemMeta();
            if (meta == null || !meta.hasLore()) continue;
            List<String> lore = meta.getLore();
            for (String string : new String[]{ArtItem.CANVAS_KEY, ArtItem.EASEL_KEY, ArtItem.PAINT_BUCKET_KEY}) {
                if (lore.get(0).equals(string)) {
                    i.remove();
                }
            }
        }
    }

    Recipe getRecipe(ItemStack result, String recipeName) throws InvalidRecipeException {
        ConfigurationSection recipeData = recipeFile.getConfigurationSection(recipeName);
        if (recipeData == null) return null;
        ConfigurationSection recipeMaterials = recipeData.getConfigurationSection("MATERIALS");

        if (recipeMaterials == null || recipeMaterials.getKeys(false).size() < 2)
            throw new InvalidRecipeException(recipeName, "Recipe cannot have less than two materials");

        List<String> shape = recipeData.getStringList("SHAPE");
        boolean hasShape = shape != null && shape.size() != 0;
        HashMap<Character, WrappedMaterial> materials = readRecipeMaterials(recipeName, recipeMaterials);

        Recipe recipe = hasShape ? new ShapedRecipe(result) : new ShapelessRecipe(result);

        if (hasShape) {
            validateRecipeShape(recipeName, shape);
            ((ShapedRecipe) recipe).shape(shape.get(0), shape.get(1), shape.get(2));
        }

        for (Character key : materials.keySet()) {
            WrappedMaterial material = materials.get(key);
            if (hasShape) {
                ((ShapedRecipe) recipe).setIngredient(key, material.getData());
            } else {
                ((ShapelessRecipe) recipe).addIngredient(material.amount, material.getData());
            }
        }
        return recipe;
    }

    private void validateRecipeShape(String recipeName, List<String> shape) throws InvalidRecipeException {
        if (shape.size() != 3) throw new InvalidRecipeException(recipeName, "Recipe shape must have 3 lines");
        for (String line : shape) {
            if (line.length() != 3) {
                throw new InvalidRecipeException(recipeName, "Recipe shape must have 3 characters per line.");
            }
        }
    }

    private HashMap<Character, WrappedMaterial> readRecipeMaterials(String recipeName, ConfigurationSection materialList)
            throws InvalidRecipeException {
        HashMap<Character, WrappedMaterial> materials = new HashMap<>();
        for (String key : materialList.getKeys(false)) {
            if (key.length() > 1)
                throw new InvalidMaterialKeyException(recipeName, key, "is not a valid material key");
            if (materials.containsKey(key.charAt(0)))
                throw new InvalidMaterialKeyException(recipeName, key, "cannot be used as a key more than once");

            String materialName = materialList.isConfigurationSection(key) ?
                    materialList.getString(key + ".MATERIAL") :
                    materialList.getString(key);
            Material material;
            int durability = 0;
            int amount = 1;
            try {
                material = Material.valueOf(materialName);
            } catch (IllegalArgumentException e) {
                throw new InvalidMaterialKeyException(recipeName, materialName, "is not a valid material");
            }
            if (materialList.contains(key + ".DURABILITY")) durability = materialList.getInt(key + ".DURABILITY");
            if (materialList.contains(key + ".AMOUNT")) amount = materialList.getInt(key + ".AMOUNT");
            materials.put(key.charAt(0), new WrappedMaterial(material, amount, durability));
        }
        return materials;
    }

    private static class WrappedMaterial {
        private final Material material;
        private final int amount;
        private final byte durability;

        private WrappedMaterial(Material material, int amount, int durability) {
            this.material = material;
            this.amount = amount;
            this.durability = (byte) durability;
        }

        @SuppressWarnings("deprecation")
        MaterialData getData() {
            return new MaterialData(material, durability);
        }
    }

    static class InvalidRecipeException extends Exception {
        private InvalidRecipeException(String recipeName, String message) {
            super("Error loading recipe for " + recipeName + ": " + message);
        }
    }

    private static class InvalidMaterialKeyException extends InvalidRecipeException {
        private InvalidMaterialKeyException(String recipeName, String key, String message) {
            super(recipeName, String.format("'%s' %s", key, message));
        }
    }
}

package me.Fupery.ArtMap.Utils.Item;

import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Recipe.SimpleRecipe;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomItem {
    private String name = null;
    private final String key;
    private final Material material;
    private short durability = 0;
    private String[] tooltip = new String[0];
    private ItemFlag[] itemFlags = new ItemFlag[0];
    private HashMap<Enchantment, Integer> enchants = new HashMap<>();
    private int amount = 1;
    private SimpleRecipe recipe = null;

    public CustomItem(Material material, String uniqueKey) {
        this.material = material;
        this.key = uniqueKey;
    }

    public CustomItem(Material material, String uniqueKey, int durability) {
        this.material = material;
        this.key = uniqueKey;
        this.durability = (short) durability;
    }

    public CustomItem(Material material, String key, String name) {
        this.material = material;
        this.key = key;
        this.name = name;
    }

    public CustomItem(Material material, String key, String... tooltip) {
        this.material = material;
        this.key = key;
        this.tooltip = tooltip;
    }

    public CustomItem(Material material, String key, String name, String... tooltip) {
        this.material = material;
        this.key = key;
        this.name = name;
        this.tooltip = tooltip;
    }

    public CustomItem name(String name) {
        this.name = name;
        return this;
    }

    public CustomItem name(Lang name) {
        this.name = name.get();
        return this;
    }

    public CustomItem tooltip(String... tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public CustomItem tooltip(Lang.Array tooltip) {
        this.tooltip = tooltip.get();
        return this;
    }

    public CustomItem durability(int durability) {
        this.durability = (short) durability;
        return this;
    }

    public CustomItem amount(int amount) {
        this.amount = amount;
        return this;
    }

    public CustomItem enchant(Enchantment enchantment, int level) {
        enchants.put(enchantment, level);
        return this;
    }

    public CustomItem flag(ItemFlag... itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    public CustomItem recipe(SimpleRecipe recipe) {
        this.recipe = recipe;
        return this;
    }

    public Recipe getBukkitRecipe() {
        return recipe.toBukkitRecipe(toItemStack());
    }

    public SimpleRecipe getRecipe() {
        return recipe;
    }

    public void addRecipe() {
        if (recipe != null) Bukkit.addRecipe(getBukkitRecipe());
    }

    public Material getMaterial() {
        return material;
    }

    public short getDurability() {
        return durability;
    }

    public int getAmount() {
        return amount;
    }

    public boolean checkItem(ItemStack itemStack) {
        if (itemStack != null
                && itemStack.getType() == material
                && itemStack.getDurability() == durability
                && itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasLore() && itemMeta.getLore().get(0).contains(key)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material, amount, durability);
        ItemMeta meta = item.getItemMeta();
        if (name != null) meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(key);
        if (tooltip.length > 0) Collections.addAll(lore, tooltip);
        meta.setLore(lore);
        if (itemFlags.length > 0) meta.addItemFlags(itemFlags);
        if (enchants.size() > 0) {
            for (Enchantment e : enchants.keySet()) meta.addEnchant(e, enchants.get(e), true);
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(14, 293);
        builder.append(key);
        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CustomItem)) return false;
        CustomItem item = (CustomItem) obj;
        return key.equals(item.key);
    }
}

package me.Fupery.ArtMap.Recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class WrappedMaterial {
    private final Material material;
    private final short durability;
    private final int amount;

    WrappedMaterial(Material material, int durability, int amount) {
        this.material = material;
        this.durability = (short) durability;
        this.amount = amount;
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

    ItemStack toItemStack() {
        return new ItemStack(material, amount, durability);
    }
}

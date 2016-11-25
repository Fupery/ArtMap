package me.Fupery.ArtMap.Recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface Ingredient {

    Material getMaterial();

    short getDurability();

    int getAmount();

    ItemStack toItemStack();

    class WrappedMaterial implements Ingredient {
        private final Material material;
        private final short durability;
        private final int amount;
        private final ItemMeta meta;

        WrappedMaterial(Material material, int durability, int amount) {
            this.material = material;
            this.durability = (short) durability;
            this.amount = amount;
            meta = null;
        }

        @Override
        public Material getMaterial() {
            return material;
        }

        @Override
        public short getDurability() {
            return durability;
        }

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public ItemStack toItemStack() {
            return new ItemStack(material, amount, durability);
        }
    }

    class WrappedItem implements Ingredient {

        private final ItemStack item;

        public WrappedItem(ItemStack item) {
            this.item = item;
        }

        @Override
        public Material getMaterial() {
            return item.getType();
        }

        @Override
        public short getDurability() {
            return item.getDurability();
        }

        @Override
        public int getAmount() {
            return item.getAmount();
        }

        @Override
        public ItemStack toItemStack() {
            return item.clone();
        }
    }
}

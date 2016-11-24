package me.Fupery.ArtMap.Recipe;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import static org.bukkit.Material.*;

public final class Palette {
    public final Dye
            BLACK = new Dye("Black", 119, ChatColor.DARK_GRAY, INK_SACK, 0),
            RED = new Dye("Red", 17, ChatColor.RED, INK_SACK, 1),
            GREEN = new Dye("Green", 109, ChatColor.DARK_GREEN, INK_SACK, 2),
            BROWN = new Dye("Brown", 105, ChatColor.DARK_RED, INK_SACK, 3),
            BLUE = new Dye("Blue", 101, ChatColor.DARK_BLUE, INK_SACK, 4),
            PURPLE = new Dye("Purple", 97, ChatColor.DARK_PURPLE, INK_SACK, 5),
            CYAN = new Dye("Cyan", 93, ChatColor.DARK_AQUA, INK_SACK, 6),
            SILVER = new Dye("Silver", 32, ChatColor.GRAY, INK_SACK, 7),
            GRAY = new Dye("Gray", 85, ChatColor.DARK_GRAY, INK_SACK, 8),
            PINK = new Dye("Pink", 81, ChatColor.LIGHT_PURPLE, INK_SACK, 9),
            LIME = new Dye("Lime", 77, ChatColor.GREEN, INK_SACK, 10),
            YELLOW = new Dye("Yellow", 74, ChatColor.YELLOW, INK_SACK, 11),
            LIGHT_BLUE = new Dye("Light Blue", 69, ChatColor.BLUE, INK_SACK, 12),
            MAGENTA = new Dye("Magenta", 64, ChatColor.LIGHT_PURPLE, INK_SACK, 13),
            ORANGE = new Dye("Orange", 61, ChatColor.GOLD, INK_SACK, 14),
            WHITE = new Dye("White", 58, ChatColor.WHITE, INK_SACK, 15),
            CREAM = new Dye("Cream", 10, ChatColor.GOLD, PUMPKIN_SEEDS),
            COFFEE = new Dye("Coffee", 41, ChatColor.DARK_RED, MELON_SEEDS),
            GRAPHITE = new Dye("Graphite", 87, ChatColor.DARK_GRAY, FLINT),
            GUNPOWDER = new Dye("Gunpowder", 89, ChatColor.GRAY, SULPHUR),
            MAROON = new Dye("Maroon", 142, ChatColor.DARK_RED, NETHER_STALK),
            AQUA = new Dye("Aqua", 125, ChatColor.AQUA, PRISMARINE_CRYSTALS),
            GRASS = new Dye("Grass", 5, ChatColor.DARK_GREEN, SEEDS),
            GOLD = new Dye("Gold", 121, ChatColor.GOLD, GOLD_NUGGET),
            VOID = new Dye("Void", 0, ChatColor.DARK_GREEN, EYE_OF_ENDER);

    private final Dye[] dyes = new Dye[]{BLACK, RED, GREEN, BROWN, BLUE, PURPLE, CYAN,
            SILVER, GRAY, PINK, LIME, YELLOW, LIGHT_BLUE, MAGENTA, ORANGE, WHITE,
            CREAM, COFFEE, GRAPHITE, GUNPOWDER, MAROON, AQUA, GRASS, GOLD, VOID};

    /**
     * @param item The itemstack to check
     * @return the corresponding dye colour, or null if the item is not a valid dye.
     */
    public Dye getDye(ItemStack item) {
        for (Dye dye : dyes) {
            if (item.getType() == dye.material) {
                if (dye.durability != -1) {
                    if (item.getDurability() != dye.durability) {
                        continue;
                    }
                }
                return dye;
            }
        }
        return null;
    }

    public Dye[] getDyes() {
        return dyes;
    }

    /**
     * Durability value of -1 indicates that items of any durability will be accepted
     */
    public static class Dye {
        private final String name;
        private final byte colour;
        private final ChatColor chatColour;
        private Material material;
        private short durability;

        Dye(String name, int colour, ChatColor chatColor, Material material, int durability) {
            this.name = name;
            this.colour = (byte) colour;
            this.chatColour = chatColor;
            this.material = material;
            this.durability = (short) durability;
        }

        Dye(String name, int colour, ChatColor chatColour, Material material) {
            this(name, colour, chatColour, material, -1);
        }

        public byte getColour() {
            return colour;
        }

        void setDyeItem(Material material, int durability) {
            this.material = material;
            this.durability = (short) durability;
        }

        void setDyeItem(Material material) {
            setDyeItem(material, -1);
        }

        public String name() {
            return chatColour + name;
        }

        public String rawName() {
            return name.toUpperCase();
        }

        public ChatColor getDisplayColour() {
            return chatColour;
        }

        public Material getMaterial() {
            return material;
        }

        public short getDurability() {
            return durability == -1 ? 0 : durability;
        }

        MaterialData getMaterialData() {
            return new MaterialData(material, ((byte) getDurability()));
        }
    }
}

package me.Fupery.ArtMap.Recipe;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ArtDye {
    private final String name;
    private final byte colour;
    private final ChatColor chatColour;
    private Material material;
    private short durability;

    /**
     * Durability value of -1 indicates that items of any durability will be accepted
     */
    ArtDye(String name, int colour, ChatColor chatColor, Material material, int durability) {
        this.name = name;
        this.colour = (byte) colour;
        this.chatColour = chatColor;
        this.material = material;
        this.durability = (short) durability;
    }

    ArtDye(String name, int colour, ChatColor chatColour, Material material) {
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
        return durability;
    }

    public ItemStack getItem() {
        return new ItemStack(material, 1, getDurability());
    }
}

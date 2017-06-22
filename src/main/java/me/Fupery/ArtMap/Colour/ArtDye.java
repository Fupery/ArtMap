package me.Fupery.ArtMap.Colour;

import me.Fupery.ArtMap.Painting.Pixel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ArtDye {
    private final String name;
    private final ChatColor chatColour;
    private Material material;
    private short durability;

    /**
     * Durability value of -1 indicates that items of any durability will be accepted
     */
    protected ArtDye(String name, ChatColor chatColor, Material material, int durability) {
        this.name = name;
        this.chatColour = chatColor;
        this.material = material;
        this.durability = (short) durability;
    }

    protected ArtDye(String name, ChatColor chatColour, Material material) {
        this(name, chatColour, material, -1);
    }

    public abstract void apply(Pixel pixel);

    public abstract byte getDyeColour(byte currentPixelColour);

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

    public ItemStack toItem() {
        ItemStack item = new ItemStack(material, 1, getDurability());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(chatColour + name);
        item.setItemMeta(meta);
        return item;
    }
}

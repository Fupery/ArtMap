package me.Fupery.ArtMap.Colour;

import me.Fupery.ArtMap.Painting.Pixel;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class BasicDye extends ArtDye {

    private final byte colour;

    /**
     * Durability value of -1 indicates that items of any durability will be accepted
     */
    protected BasicDye(String name, int colour, ChatColor chatColor, Material material, int durability) {
        super(name, chatColor, material, durability);
        this.colour = (byte) colour;

    }

    protected BasicDye(String name, int colour, ChatColor chatColour, Material material) {
        this(name, colour, chatColour, material, -1);
    }

    public byte getColour() {
        return colour;
    }

    @Override
    public void apply(Pixel pixel) {
        pixel.setColour(getColour());
    }

    @Override
    public byte getDyeColour(byte currentPixelColour) {
        return getColour();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicDye)) return false;
        BasicDye dye = (BasicDye) obj;
        return dye.colour == colour;
    }
}

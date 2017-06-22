package me.Fupery.ArtMap.Colour;

import me.Fupery.ArtMap.Painting.Pixel;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ShadingDye extends ArtDye {

    private final boolean darken;

    protected ShadingDye(String name, boolean darken, ChatColor chatColour, Material material) {
        super(name, chatColour, material);
        this.darken = darken;
    }

    @Override
    public void apply(Pixel pixel) {
        pixel.setColour(getDyeColour(pixel.getColour()));
    }

    @Override
    public byte getDyeColour(byte currentPixelColour) {
        if (currentPixelColour < 4) {
            return currentPixelColour;
        }
        byte shade = currentPixelColour;
        byte shift;

        while (shade >= 4) {
            shade -= 4;
        }

        if (darken) {

            if (shade > 0 && shade < 3) {
                shift = -1;

            } else if (shade == 0) {
                shift = 3;

            } else {
                return currentPixelColour;
            }

        } else {

            if (shade < 2 && shade >= 0) {
                shift = 1;

            } else if (shade == 3) {
                shift = -3;

            } else {
                return currentPixelColour;
            }
        }
        return (byte) (currentPixelColour + shift);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShadingDye)) return false;
        ShadingDye dye = (ShadingDye) obj;
        return dye.darken == darken;
    }
}

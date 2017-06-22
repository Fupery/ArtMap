package me.Fupery.ArtMap.Colour;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static me.Fupery.ArtMap.Config.Lang.*;
import static org.bukkit.Material.*;

public class BasicPalette implements Palette {
    public final ArtDye
            //Basic Dyes
            BLACK = new BasicDye(DYE_BLACK.get(), 119, ChatColor.DARK_GRAY, INK_SACK, 0),
            RED = new BasicDye(DYE_RED.get(), 17, ChatColor.RED, INK_SACK, 1),
            GREEN = new BasicDye(DYE_GREEN.get(), 109, ChatColor.DARK_GREEN, INK_SACK, 2),
            BROWN = new BasicDye(DYE_BROWN.get(), 105, ChatColor.DARK_RED, INK_SACK, 3),
            BLUE = new BasicDye(DYE_BLUE.get(), 101, ChatColor.DARK_BLUE, INK_SACK, 4),
            PURPLE = new BasicDye(DYE_PURPLE.get(), 97, ChatColor.DARK_PURPLE, INK_SACK, 5),
            CYAN = new BasicDye(DYE_CYAN.get(), 93, ChatColor.DARK_AQUA, INK_SACK, 6),
            SILVER = new BasicDye(DYE_SILVER.get(), 32, ChatColor.GRAY, INK_SACK, 7),
            GRAY = new BasicDye(DYE_GRAY.get(), 85, ChatColor.DARK_GRAY, INK_SACK, 8),
            PINK = new BasicDye(DYE_PINK.get(), 81, ChatColor.LIGHT_PURPLE, INK_SACK, 9),
            LIME = new BasicDye(DYE_LIME.get(), 77, ChatColor.GREEN, INK_SACK, 10),
            YELLOW = new BasicDye(DYE_YELLOW.get(), 74, ChatColor.YELLOW, INK_SACK, 11),
            LIGHT_BLUE = new BasicDye(DYE_LIGHT_BLUE.get(), 69, ChatColor.BLUE, INK_SACK, 12),
            MAGENTA = new BasicDye(DYE_MAGENTA.get(), 64, ChatColor.LIGHT_PURPLE, INK_SACK, 13),
            ORANGE = new BasicDye(DYE_ORANGE.get(), 61, ChatColor.GOLD, INK_SACK, 14),
            WHITE = new BasicDye(DYE_WHITE.get(), 58, ChatColor.WHITE, INK_SACK, 15),
            CREAM = new BasicDye(DYE_CREAM.get(), 10, ChatColor.GOLD, PUMPKIN_SEEDS),
            COFFEE = new BasicDye(DYE_COFFEE.get(), 41, ChatColor.DARK_RED, MELON_SEEDS),
            GRAPHITE = new BasicDye(DYE_GRAPHITE.get(), 87, ChatColor.DARK_GRAY, FLINT),
            GUNPOWDER = new BasicDye(DYE_GUNPOWDER.get(), 89, ChatColor.GRAY, SULPHUR),
            MAROON = new BasicDye(DYE_MAROON.get(), 142, ChatColor.DARK_RED, NETHER_STALK),
            AQUA = new BasicDye(DYE_AQUA.get(), 125, ChatColor.AQUA, PRISMARINE_CRYSTALS),
            GRASS = new BasicDye(DYE_GRASS.get(), 5, ChatColor.DARK_GREEN, SEEDS),
            GOLD = new BasicDye(DYE_GOLD.get(), 121, ChatColor.GOLD, GOLD_NUGGET),
            VOID = new BasicDye(DYE_VOID.get(), 0, ChatColor.DARK_GREEN, EYE_OF_ENDER),
            COAL = new ShadingDye(DYE_COAL.get(), true, ChatColor.DARK_GRAY, Material.COAL),
            FEATHER = new ShadingDye(DYE_FEATHER.get(), false, ChatColor.WHITE, Material.FEATHER);

    private final ArtDye[] dyes = new ArtDye[]{BLACK, RED, GREEN, BROWN, BLUE, PURPLE, CYAN,
            SILVER, GRAY, PINK, LIME, YELLOW, LIGHT_BLUE, MAGENTA, ORANGE, WHITE,
            CREAM, COFFEE, GRAPHITE, GUNPOWDER, MAROON, AQUA, GRASS, GOLD, VOID};

    private final ArtDye[] tools = new ArtDye[]{COAL, FEATHER};

    @Override
    public ArtDye getDye(ItemStack item) {
        for (ArtDye[] dyeList : new ArtDye[][]{dyes, tools}) {
            for (ArtDye dye : dyeList) {
                if (item.getType() == dye.getMaterial()) {
                    if (dye.getDurability() != -1) {
                        if (item.getDurability() != dye.getDurability()) {
                            continue;
                        }
                    }
                    return dye;
                }
            }
        }
        return null;
    }

    @Override
    public ArtDye[] getDyes(DyeType dyeType) {
        if (dyeType == DyeType.DYE) return dyes;
        else if (dyeType == DyeType.TOOL) return tools;
        else if (dyeType == DyeType.ALL) return concatenate(dyes, tools);
        else return null;
    }

    public ArtDye[] concatenate(ArtDye[] a, ArtDye[] b) {
        int aLength = a.length;
        int bLength = b.length;
        ArtDye[] c = new ArtDye[aLength + bLength];
        System.arraycopy(a, 0, c, 0, aLength);
        System.arraycopy(b, 0, c, aLength, bLength);
        return c;
    }

    @Override
    public BasicDye getDefaultColour() {
        return ((BasicDye) WHITE);
    }

}

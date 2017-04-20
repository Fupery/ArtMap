package me.Fupery.ArtMap.Colour;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import static me.Fupery.ArtMap.Config.Lang.*;
import static org.bukkit.Material.*;

public class BasicPalette implements Palette {
    public final ArtDye
            BLACK = new ArtDye(DYE_BLACK.get(), 119, ChatColor.DARK_GRAY, INK_SACK, 0),
            RED = new ArtDye(DYE_RED.get(), 17, ChatColor.RED, INK_SACK, 1),
            GREEN = new ArtDye(DYE_GREEN.get(), 109, ChatColor.DARK_GREEN, INK_SACK, 2),
            BROWN = new ArtDye(DYE_BROWN.get(), 105, ChatColor.DARK_RED, INK_SACK, 3),
            BLUE = new ArtDye(DYE_BLUE.get(), 101, ChatColor.DARK_BLUE, INK_SACK, 4),
            PURPLE = new ArtDye(DYE_PURPLE.get(), 97, ChatColor.DARK_PURPLE, INK_SACK, 5),
            CYAN = new ArtDye(DYE_CYAN.get(), 93, ChatColor.DARK_AQUA, INK_SACK, 6),
            SILVER = new ArtDye(DYE_SILVER.get(), 32, ChatColor.GRAY, INK_SACK, 7),
            GRAY = new ArtDye(DYE_GRAY.get(), 85, ChatColor.DARK_GRAY, INK_SACK, 8),
            PINK = new ArtDye(DYE_PINK.get(), 81, ChatColor.LIGHT_PURPLE, INK_SACK, 9),
            LIME = new ArtDye(DYE_LIME.get(), 77, ChatColor.GREEN, INK_SACK, 10),
            YELLOW = new ArtDye(DYE_YELLOW.get(), 74, ChatColor.YELLOW, INK_SACK, 11),
            LIGHT_BLUE = new ArtDye(DYE_LIGHT_BLUE.get(), 69, ChatColor.BLUE, INK_SACK, 12),
            MAGENTA = new ArtDye(DYE_MAGENTA.get(), 64, ChatColor.LIGHT_PURPLE, INK_SACK, 13),
            ORANGE = new ArtDye(DYE_ORANGE.get(), 61, ChatColor.GOLD, INK_SACK, 14),
            WHITE = new ArtDye(DYE_WHITE.get(), 58, ChatColor.WHITE, INK_SACK, 15),
            CREAM = new ArtDye(DYE_CREAM.get(), 10, ChatColor.GOLD, PUMPKIN_SEEDS),
            COFFEE = new ArtDye(DYE_COFFEE.get(), 41, ChatColor.DARK_RED, MELON_SEEDS),
            GRAPHITE = new ArtDye(DYE_GRAPHITE.get(), 87, ChatColor.DARK_GRAY, FLINT),
            GUNPOWDER = new ArtDye(DYE_GUNPOWDER.get(), 89, ChatColor.GRAY, SULPHUR),
            MAROON = new ArtDye(DYE_MAROON.get(), 142, ChatColor.DARK_RED, NETHER_STALK),
            AQUA = new ArtDye(DYE_AQUA.get(), 125, ChatColor.AQUA, PRISMARINE_CRYSTALS),
            GRASS = new ArtDye(DYE_GRASS.get(), 5, ChatColor.DARK_GREEN, SEEDS),
            GOLD = new ArtDye(DYE_GOLD.get(), 121, ChatColor.GOLD, GOLD_NUGGET),
            VOID = new ArtDye(DYE_VOID.get(), 0, ChatColor.DARK_GREEN, EYE_OF_ENDER);

    private final ArtDye[] dyes = new ArtDye[]{BLACK, RED, GREEN, BROWN, BLUE, PURPLE, CYAN,
            SILVER, GRAY, PINK, LIME, YELLOW, LIGHT_BLUE, MAGENTA, ORANGE, WHITE,
            CREAM, COFFEE, GRAPHITE, GUNPOWDER, MAROON, AQUA, GRASS, GOLD, VOID};

    @Override
    public ArtDye getDye(ItemStack item) {
        for (ArtDye dye : dyes) {
            if (item.getType() == dye.getMaterial()) {
                if (dye.getDurability() != -1) {
                    if (item.getDurability() != dye.getDurability()) {
                        continue;
                    }
                }
                return dye;
            }
        }
        return null;
    }

    @Override
    public ArtDye[] getDyes() {
        return dyes;
    }

    @Override
    public ArtDye getDefaultColour() {
        return WHITE;
    }

}

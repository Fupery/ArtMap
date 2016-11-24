package me.Fupery.ArtMap.Colour;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.*;

public class BasicPalette implements Palette {
    public final ArtDye
            BLACK = new ArtDye("Black", 119, ChatColor.DARK_GRAY, INK_SACK, 0),
            RED = new ArtDye("Red", 17, ChatColor.RED, INK_SACK, 1),
            GREEN = new ArtDye("Green", 109, ChatColor.DARK_GREEN, INK_SACK, 2),
            BROWN = new ArtDye("Brown", 105, ChatColor.DARK_RED, INK_SACK, 3),
            BLUE = new ArtDye("Blue", 101, ChatColor.DARK_BLUE, INK_SACK, 4),
            PURPLE = new ArtDye("Purple", 97, ChatColor.DARK_PURPLE, INK_SACK, 5),
            CYAN = new ArtDye("Cyan", 93, ChatColor.DARK_AQUA, INK_SACK, 6),
            SILVER = new ArtDye("Silver", 32, ChatColor.GRAY, INK_SACK, 7),
            GRAY = new ArtDye("Gray", 85, ChatColor.DARK_GRAY, INK_SACK, 8),
            PINK = new ArtDye("Pink", 81, ChatColor.LIGHT_PURPLE, INK_SACK, 9),
            LIME = new ArtDye("Lime", 77, ChatColor.GREEN, INK_SACK, 10),
            YELLOW = new ArtDye("Yellow", 74, ChatColor.YELLOW, INK_SACK, 11),
            LIGHT_BLUE = new ArtDye("Light Blue", 69, ChatColor.BLUE, INK_SACK, 12),
            MAGENTA = new ArtDye("Magenta", 64, ChatColor.LIGHT_PURPLE, INK_SACK, 13),
            ORANGE = new ArtDye("Orange", 61, ChatColor.GOLD, INK_SACK, 14),
            WHITE = new ArtDye("White", 58, ChatColor.WHITE, INK_SACK, 15),
            CREAM = new ArtDye("Cream", 10, ChatColor.GOLD, PUMPKIN_SEEDS),
            COFFEE = new ArtDye("Coffee", 41, ChatColor.DARK_RED, MELON_SEEDS),
            GRAPHITE = new ArtDye("Graphite", 87, ChatColor.DARK_GRAY, FLINT),
            GUNPOWDER = new ArtDye("Gunpowder", 89, ChatColor.GRAY, SULPHUR),
            MAROON = new ArtDye("Maroon", 142, ChatColor.DARK_RED, NETHER_STALK),
            AQUA = new ArtDye("Aqua", 125, ChatColor.AQUA, PRISMARINE_CRYSTALS),
            GRASS = new ArtDye("Grass", 5, ChatColor.DARK_GREEN, SEEDS),
            GOLD = new ArtDye("Gold", 121, ChatColor.GOLD, GOLD_NUGGET),
            VOID = new ArtDye("Void", 0, ChatColor.DARK_GREEN, EYE_OF_ENDER);

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

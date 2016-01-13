package me.Fupery.ArtMap.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public enum ArtDye {
    //Bukkit Dye Colours
    BLACK(119, Material.INK_SACK, ChatColor.DARK_GRAY, 0),
    RED(17, Material.INK_SACK, ChatColor.RED, 1),
    GREEN(109, Material.INK_SACK, ChatColor.DARK_GREEN, 2),
    BROWN(105, Material.INK_SACK, ChatColor.DARK_RED, 3),
    BLUE(101, Material.INK_SACK, ChatColor.DARK_BLUE, 4),
    PURPLE(97, Material.INK_SACK, ChatColor.DARK_PURPLE, 5),
    CYAN(93, Material.INK_SACK, ChatColor.DARK_AQUA, 6),
    SILVER(32, Material.INK_SACK, ChatColor.GRAY, 7),
    GRAY(85, Material.INK_SACK, ChatColor.DARK_GRAY, 8),
    PINK(81, Material.INK_SACK, ChatColor.LIGHT_PURPLE, 9),
    LIME(77, Material.INK_SACK, ChatColor.GREEN, 10),
    YELLOW(74, Material.INK_SACK, ChatColor.YELLOW, 11),
    LIGHT_BLUE(69, Material.INK_SACK, ChatColor.BLUE, 12),
    MAGENTA(64, Material.INK_SACK, ChatColor.LIGHT_PURPLE, 13),
    ORANGE(61, Material.INK_SACK, ChatColor.GOLD, 14),
    WHITE(58, Material.INK_SACK, ChatColor.WHITE, 15),
    //skin tones
    CREAM(10, Material.PUMPKIN_SEEDS, ChatColor.GOLD),
    COFFEE(41, Material.MELON_SEEDS, ChatColor.DARK_RED),
    //extra colours
    GRAPHITE(87, Material.FLINT, ChatColor.DARK_GRAY),
    GUNPOWDER(89, Material.SULPHUR, ChatColor.GRAY),
    MAROON(142, Material.NETHER_STALK, ChatColor.DARK_RED),
    AQUA(125, Material.PRISMARINE_CRYSTALS, ChatColor.AQUA),
    GRASS(5, Material.SEEDS, ChatColor.DARK_GREEN),
    GOLD(121, Material.GOLD_NUGGET, ChatColor.GOLD),
    //transparent colour
    VOID(0, Material.EYE_OF_ENDER, ChatColor.DARK_GREEN);

    //Instance fields
    private final byte data;
    private final Material material;
    private final int durability;
    private final ChatColor display;

    ArtDye(int data, Material material, ChatColor display) {
        this.data = ((byte) data);
        this.material = material;
        this.display = display;
        this.durability = -1;
    }

    ArtDye(int data, Material material, ChatColor display, int durability) {
        this.data = ((byte) data);
        this.material = material;
        this.display = display;
        this.durability = durability;
    }

    public static ArtDye getArtDye(ItemStack item) {

        for (ArtDye dye : ArtDye.values()) {

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

    public byte getData() {
        return data;
    }

    public ChatColor getDisplay() {
        return display;
    }

    public MaterialData getRecipeItem() {
        return (durability == -1) ?
                new MaterialData(material) :
                new MaterialData(material, (byte) durability);
    }
}

package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class HelpMenu extends MainMenu {

    public HelpMenu(final ArtMap plugin, final Player player) {
        super(plugin, "§1Choose a help topic", player, InventoryType.HOPPER);
        addButtons(generateButtons(this));
    }
    private static MenuButton[] generateButtons(InventoryMenu menu) {
        MenuButton[] buttons = new MenuButton[5];
        buttons[0] = new StaticButton(Material.SIGN, gettingStarted);
        buttons[1] = new LinkedButton(new RecipeMenu(menu), Material.WORKBENCH, recipes);
        buttons[2] = new StaticButton(Material.BOOK_AND_QUILL, commands);
        buttons[3] = new LinkedButton(new ArtistListMenu(menu, 0), Material.PAINTING, list);
        buttons[4] = new CloseButton();
        return buttons;
    }

    public static final String[] gettingStarted =  new String[] {
            "                §6§l⊱§e§lGetting Started§6§l⊰",
            "               §3❉§b§oWelcome to ArtMap!§3❉",
            "§7To get started painting, you will need to craft",
            "§7yourself an §6Easel §7and §6Canvas§7. Once you place",
            "§7your easel, click the canvas with §etools§7 to paint.",
            "   §8░░░░§e❃§lTools §r§e⤸§8░░░░░░░░░░░░░░░░",
            "§a❂§2Dye§a﴿",
            "§2§oLeft-Click§r§7 to draw a point, §2§oRight-Click§r§7 for lines",
            "",
            "                                         §e﴾§6PaintBucket§e❂",
            "     §7Craft a §o§6PaintBucket§7 with dye to fill an area",
            "",
            "§b❂§3Coal&Feather§b﴿",
            "§7Click with §3Coal§7 or §bFeather§7 to §3darken§7 or §blighten"
    };
    public static final String[] recipes =  new String[] {
            "   §6§l⊱§e§lRecipes§6§l⊰",
            "§3✸§b§oClick to View§3✸",
    };
    public static final String[] commands =  new String[] {
            "               §6§l⊱§e§lArtMap Commands§6§l⊰",
            "                §3❀§b§o/artmap§3 for help§3❀",
            "  §8░░░░░░░░░░░░░░░░░░░░░░░░░░░ ",
            "§6•§f/artmap save <title>    §6➯   §7save your artwork",
            "§6•§f/artmap delete <title>  §6➯ §7delete your artwork",
            "§6•§f/artmap preview <title> §6➯  §7preview an artwork",
            "§2You can also preview art in the list menu§e§l➚"
    };
    public static final String[] list =  new String[] {
            "          §6§l⊱§e§lArtwork List§6§l⊰",
            "      §3✺§b§oView Player Artworks§3✺",
            "§7View your artwork or view other",
            "§7players' work grouped by artist.",
            "           §2Click to browse",

    };
    public static final String[] close =  new String[] {
            "    §6§l⊱§e§lBack§6§l⊰",
            "§4﴾§cClose Menu§4﴿"
    };

    public static String click = "§aClick to View";
}

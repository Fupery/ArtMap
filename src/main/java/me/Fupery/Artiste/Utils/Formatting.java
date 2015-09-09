package me.Fupery.Artiste.Utils;

import org.bukkit.ChatColor;

public class Formatting {
    public static String
            prefix = ChatColor.AQUA + "[Artmap] ",
            noperm = "You don't have permission to do this.",
            emptyHand = "Use an empty hand to retrieve your artwork.",
            elseUsing = "Someone else is using this canvas!",
            saveUsage = "Use " + ChatColor.YELLOW +  "/artmap save <title> " + ChatColor.GOLD + "to save your artwork.",
            punchCanvas = "Now left-click your artwork to save.",
            painting = "Right click the canvas with dye or a paint bucket to paint.",
            deleted = "Sucessfully deleted."

    ;

    public static String playerMessage(String string) {
        return prefix + ChatColor.GOLD + string;
    }
    public static String playerError(String string) {
        return prefix + ChatColor.RED + string;
    }
}

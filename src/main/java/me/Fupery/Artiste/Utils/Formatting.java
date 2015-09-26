package me.Fupery.Artiste.Utils;

import org.bukkit.ChatColor;

public class Formatting {
    public static String
            prefix = ChatColor.AQUA + "[Artmap] ",
            playerOnly = "This command can only be used by players.",
            invalidPos = "You can't place an easel here",
            noperm = "You don't have permission to do this.",
            emptyHand = "Use an empty hand to retrieve your artwork.",
            elseUsing = "Someone else is using this canvas!",
            saveUsage = "Use " + ChatColor.YELLOW + "/artmap save <title> " + ChatColor.GOLD + "to save your artwork.",
            punchCanvas = "Now Left-Click your artwork to save '%s'.",
            breakCanvas = "Shift + Right-Click the easel to break it.",
            painting = "Right-Click the canvas with dye or a paint bucket to paint.",
            deleted = "Sucessfully deleted '%s'.",
            mapNotFound = "Artwork '%s' could not be found.",
            noCraftPerm = "You can't copy other players' artworks",
            craftHelp = "",
            badTitle = "Invalid Title. Titles must be between 3 and 16 characters, and consist of only letters and numbers.";

    public static String playerMessage(String string) {
        return prefix + ChatColor.GOLD + string;
    }

    public static String playerError(String string) {
        return prefix + ChatColor.RED + string;
    }
}

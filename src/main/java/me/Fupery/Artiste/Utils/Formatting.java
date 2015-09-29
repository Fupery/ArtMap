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
            saveUsage = "Use " + ChatColor.YELLOW + "/artmap save <title> "
                    + ChatColor.GOLD + "to save your artwork.",
            punchCanvas = "Now Left-Click your artwork to save '%s'.",
            breakCanvas = "Shift + Right-Click the easel to break it.",
            painting = "Right-Click the canvas with dye or a paint bucket to paint.",
            deleted = "Sucessfully deleted '%s'.",
            mapNotFound = "Artwork '%s' could not be found.",
            noDupeCanvas = "You can't copy canvases.",
            noCraftPerm = "You can't copy other players' artworks",
            craftHelp = "",
            noArtworksFound = "No artworks were found by '%s'.",
            listHeader = "Artworks by '%s':",
            listLineHover = ChatColor.YELLOW + "Click to preview '%s'",
            listFooterPage = ChatColor.LIGHT_PURPLE + "Showing pg [%s/%s] ",
            listFooterButton = ChatColor.GREEN + "[Next Page]",
            listFooterNxt = ChatColor.YELLOW + "Click to view the next page",
            seperator = ChatColor.DARK_AQUA + "-------------------------",
            helpHeader = "Help:",
            helpMessage = ChatColor.GOLD + "Craft an Easel and Canvas to create artworks.\n" +
                    "Use Dyes and PaintBuckets to paint on your canvas.",
            badTitle = "Invalid Title. Titles must be between 3 and 16 characters, " +
                    "and consist of only letters and numbers.",
            titleUsed = "Sorry, this title is already being used.",
            previewing = "Previewing artwork '%s'.",
            emptyHandPreview = "You need to have an empty hand to preview.";

    public static String playerMessage(String string) {
        return prefix + ChatColor.GOLD + string;
    }

    public static String playerError(String string) {
        return prefix + ChatColor.RED + string;
    }

    public static String listLine(String title, String playername, String date, short id) {
        return String.format(ChatColor.GOLD + "- %s %sby %s %s %s(%s)", ChatColor.WHITE +
                        "" + ChatColor.ITALIC + title, ChatColor.GOLD, ChatColor.YELLOW + playername,
                ChatColor.DARK_PURPLE + date, ChatColor.DARK_AQUA, +id);
    }

    public static String helpLine(String usage, String helpMsg) {
        return ChatColor.GOLD + "  - " + ChatColor.AQUA + usage +
                ChatColor.GOLD + "  |  " + ChatColor.DARK_AQUA + helpMsg;
    }

    public static String extractListTitle(String listLine) {
        char[] chars = listLine.toCharArray();
        boolean readingTitle = false;
        boolean colourCode = false;
        String title = "";

        for (char c : chars) {

            if (c == '§') {
                colourCode = true;
                readingTitle = false;
                continue;
            }
            if (colourCode) {
                colourCode = false;
                continue;
            }

            if (readingTitle
                    && c == ' ') {
                break;
            }

            if (Character.isAlphabetic(c)
                    || Character.isDigit(c)
                    || c == '_'
                    ) {
                title += c;

                if (!readingTitle) {
                    readingTitle = true;
                }
            }
        }
        return title;
    }
}

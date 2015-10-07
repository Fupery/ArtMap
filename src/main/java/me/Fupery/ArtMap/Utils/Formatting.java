package me.Fupery.ArtMap.Utils;

import org.bukkit.ChatColor;

public class Formatting {
    public static String playerOnly = "This command can only be used by players.";
    public static String invalidPos = "You can't place an easel here";
    public static String noperm = "You don't have permission to do this.";
    public static String emptyHand = "Use an empty hand to retrieve your artwork.";
    public static String elseUsing = "Someone else is using this canvas!";
    public static String saveUsage = "Use " + ChatColor.YELLOW + "/artmap save <title> "
            + ChatColor.GOLD + "to save your artwork.";
    public static String punchCanvas = "Now Left-Click your artwork to save '%s'.";
    public static String breakCanvas = "Shift + Right-Click the easel to break it.";
    public static String painting = "Right-Click the canvas with dye or a paint bucket to paint.";
    public static String deleted = "Sucessfully deleted '%s'.";
    public static String mapNotFound = "Artwork '%s' could not be found.";
    public static String noDupeCanvas = "You can't copy canvases.";
    public static String noCraftPerm = "You can't copy other players' artworks";
    public static String craftHelp = "";
    public static String noArtworksFound = "No artworks were found by '%s'.";
    public static String listHeader = "Artworks by '%s':";
    public static String listLineHover = ChatColor.YELLOW + "Click to preview '%s'";
    public static String listFooterPage = ChatColor.LIGHT_PURPLE + "Showing pg [%s/%s] ";
    public static String listFooterButton = ChatColor.GREEN + "[Next Page]";
    public static String listFooterNxt = ChatColor.YELLOW + "Click to view the next page";
    public static String seperator = ChatColor.DARK_AQUA + "-------------------------";
    public static String helpHeader = "Help:";
    public static String helpMessage = ChatColor.GOLD + "Craft an Easel and Canvas to create artworks.\n" +
            "Use Dyes and PaintBuckets to paint on your canvas.";
    public static String badTitle = "Invalid Title. Titles must be between 3 and 16 characters, " +
            "and consist of only letters and numbers.";
    public static String titleUsed = "Sorry, this title is already being used.";
    public static String previewing = "Previewing artwork '%s'.";
    public static String emptyHandPreview = "You need to have an empty hand to preview.";
    private static String
            prefix = ChatColor.AQUA + "[ArtMap] ";

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

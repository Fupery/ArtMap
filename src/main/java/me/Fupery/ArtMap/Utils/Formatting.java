package me.Fupery.ArtMap.Utils;

import org.bukkit.ChatColor;

public class Formatting {
    public static String noConsole = "This command can only be used by players.";
    public static String invalidPos = "You can't place an easel here";
    public static String noperm = "You don't have permission to do this.";
    public static String elseUsing = "Someone else is using this canvas!";
    public static String saveUsage = "Use " + ChatColor.YELLOW + "/artmap save <title> "
            + ChatColor.GOLD + "to save your artwork.";
    public static String notRidingEasel = "You must be sitting at your easel to save.";
    public static String saveSuccess = "Successfully saved '%s'!";
    public static String easelHelp = "Right-Click to start painting, Shift + Right-Click to break.";
    public static String needCanvas = "You need to place a canvas on the easel to paint.";
    public static String notACanvas = "You can only place ArtMap canvases on the easel.";
    public static String notYourEasel = "You cannot destroy %s's artwork!";
    public static String needToCopy = "You must craft this carbon paper with an artwork to edit.";
    public static String breakCanvas = "Shift + Right-Click the easel to break it.";
    public static String painting = "Use dyes or a paint bucket to paint. " +
            "Left-Click to draw pixels, Right-Click and drag to draw lines.";
    public static String deleted = "Sucessfully deleted '%s'.";
    public static String mapNotFound = "Artwork '%s' could not be found.";
    public static String noCraftPerm = "You can't copy other players' artworks";
    public static String getItem = ChatColor.YELLOW + "Click to get one %s";
    public static String recipeButton = ChatColor.GREEN + "[%s]";
    public static String noArtworksFound = "No artworks were found by '%s'.";
    public static String listHeader = "Artworks by '%s':";
    public static String listLineHover = ChatColor.YELLOW + "Click to preview '%s'";
    public static String listFooterPage = ChatColor.LIGHT_PURPLE + "Showing pg [%s/%s] ";
    public static String listFooterButton = ChatColor.GREEN + "[Next Page]";
    public static String listFooterNxt = ChatColor.YELLOW + "Click to view the next page";
    public static String seperator = ChatColor.DARK_AQUA + "-------------------------";
    public static String helpHeader = "Help:";
    public static String helpMessage = ChatColor.GOLD + "Craft an Easel and Canvas to create artworks.\n" +
            ChatColor.GOLD + "Use Dyes and PaintBuckets to paint on your canvas.";
    public static String badTitle = "Invalid Title. Titles must be between 3 and 16 characters, " +
            "and consist of only letters and numbers.";
    public static String titleUsed = "Sorry, this title is already being used.";
    public static String previewing = "Previewing artwork '%s'.";
    public static String unknownError = "Oops! Something strange has happened. " +
            "Check that your canvas isn't missing parts, and try again.";
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

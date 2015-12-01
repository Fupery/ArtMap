package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.ChatColor;

public class Formatting {

    public static String playerError(String string) {
        return ArtMap.Lang.prefix + ChatColor.RED + string;
    }

    public static String listLine(String title, String playername, String date, short id) {
        return String.format(ChatColor.GOLD + "- %s %sby %s %s %s(%s)", ChatColor.WHITE +
                        "" + ChatColor.ITALIC + title, ChatColor.GOLD, ChatColor.YELLOW + playername,
                ChatColor.DARK_PURPLE + date, ChatColor.DARK_AQUA, +id);
    }

    public static String helpLine(String usage, String helpMsg) {
        return ChatColor.GOLD + "  - " + ChatColor.AQUA + usage +
                ChatColor.GOLD + "  |  " + ChatColor.GRAY + helpMsg;
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

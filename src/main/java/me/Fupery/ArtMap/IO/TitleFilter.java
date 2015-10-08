package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;

public class TitleFilter {

    private ArtMap plugin;
    private String title;
    private char[] chars;
    private String adjTitle;

    public TitleFilter(ArtMap plugin, String title) {
        this.plugin = plugin;
        this.title = title;
        chars = title.toCharArray();
        adjTitle = replaceCharacters();
    }

    public boolean check() {

        if (checkLength() && checkValidType()) {

            for (String reject : plugin.getTitleFilter()) {

                if (title.toLowerCase().contains(reject)
                        || adjTitle.contains(reject)) {
                    return false;
                }
            }

        } else {
            return false;
        }
        return true;
    }

    private boolean checkLength() {
        return (title.length() >= 3 && title.length() <= 16);
    }

    //ensure characters are only alphabetic, numerical, or underscores
    private boolean checkValidType() {

        for (char c : chars) {

            if (Character.isDigit(c)
                    || Character.isAlphabetic(c)
                    || c == '_') {
                continue;
            }
            return false;
        }
        return true;
    }

    //removes repeat characters & underscores
    private String replaceCharacters() {

        String adjString = "";
        char repeatChar = '$';
        char currentChar;

        for (int i = 0; i < chars.length; i++) {

            if (chars[i] == '1') {
                currentChar = 'i';

            } else if (chars[i] == '3') {
                currentChar = 'e';

            } else if (chars[i] == '0') {
                currentChar = 'o';

            } else if (chars[i] == '5') {
                currentChar = 's';

            } else if (Character.toLowerCase(chars[i]) == 'z') {
                currentChar = 's';

            } else if (chars[i] == '_') {
                continue;

            } else {
                currentChar = Character.toLowerCase(chars[i]);
            }

            if (repeatChar != currentChar) {
                repeatChar = currentChar;
                adjString += currentChar;
            }
        }
        return adjString;
    }
}
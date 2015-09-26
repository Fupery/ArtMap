package me.Fupery.Artiste.Utils;

import me.Fupery.Artiste.Artiste;

public class TitleFilter {

    Artiste plugin;
    String title;
    char[] chars;
    String adjTitle;

    public TitleFilter(Artiste plugin, String title) {
        this.plugin = plugin;
        this.title = title;
        chars = title.toCharArray();
        adjTitle = replaceCharacters();
    }

    public boolean check() {

        if (checkLength() && checkValidType()) {

            for (String reject : plugin.getTitleFilter()) {

                if (title.contains(reject)
                        || adjTitle.contains(reject)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkLength() {
        return (title.length() >= 3 && title.length() <= 16);
    }

    private boolean checkValidType() {

        for (char c : chars) {

            if (!Character.isDigit(c)
                    || !Character.isAlphabetic(c)
                    || !(c == '_')) {
                return false;
            }
        }
        return true;
    }

    private String replaceCharacters() {

        String adjString = "";

        for (int i = 0; i < chars.length; i ++) {

            if (chars[i] == '1') {
                adjString += 'i';

            } else if (chars[i] == '3') {
                adjString += 'e';

            } else if (chars[i] == '0') {
                adjString += 'o';

            } else if (chars[i] == '5') {
                adjString += 's';

            } else if (Character.toLowerCase(chars[i]) == 'z') {
                adjString += 's';

            } else if (chars[i] == '_') {
                //remove underscores

            } else {
                adjString += Character.toLowerCase(chars[i]);
            }
        }
        return adjString;
    }
}

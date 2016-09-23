package me.Fupery.ArtMap.Utils.Formatting;

import java.util.Arrays;

public class AlignmentModifier implements Modifier {

    private final Alignment alignment;
    private final int lineLength;

    public AlignmentModifier(Alignment alignment, int lineLength) {
        this.alignment = alignment;
        this.lineLength = lineLength;
    }

    @Override
    public String apply(String rawString) {
        String string = rawString.trim();
        switch (alignment) {
            case LEFT:
                return string;
            case RIGHT:
                return whitespace(lineLength - string.length()) + string;
            case CENTRE:
                double space = ((double) lineLength) / 2D;
                String head = whitespace((int) Math.ceil(space));
                String tail = whitespace((int) Math.floor(space));
                return head + string + tail;
            case JUSTIFIED:
                String[] words = string.split("(( |\\t)((?=.*<)|(?!.*>))|<|>)+");
                int spaceAvailable = lineLength - cumulativeLength(words);
                int spacesPerGap = (int) (spaceAvailable / words.length - 1D);
                String justifiedString = "";
                for (String word : words) {
                    int trailingSpaces = spaceAvailable;
                    if (spaceAvailable >= spacesPerGap) spaceAvailable -= (trailingSpaces = spacesPerGap);
                    justifiedString += (word + whitespace(trailingSpaces));
                }
                return justifiedString;
            default:
                return rawString;
        }
    }

    private static int cumulativeLength(String[] strings) {
        int minimumLength = 0;
        for (String word : strings) minimumLength += word.length();
        return minimumLength;
    }

    private static String whitespace(int length) {
        return String.valueOf(repeatChar('_', length));
    }

    private static char[] repeatChar(char c, int times) {
        char[] chars = new char[times];
        Arrays.fill(chars, c);
        return chars;
    }

    private enum Alignment {
        LEFT, RIGHT, CENTRE, JUSTIFIED
    }
}

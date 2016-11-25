package me.Fupery.ArtMap.Utils.Formatting;

public class TooltipUtils {

    private String[] tooltip;

    public TooltipUtils(String[] tooltip) {
        this.tooltip = tooltip;
    }

    public void justify() {
        int lineLength = 0;
        for (String line : tooltip) if (line.length() > lineLength) lineLength = line.length();

    }
}

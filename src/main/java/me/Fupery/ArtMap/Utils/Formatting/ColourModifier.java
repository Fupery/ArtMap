package me.Fupery.ArtMap.Utils.Formatting;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ColourModifier implements Modifier {

    private final ChatColor baseColour;
    private HashMap<Character, ChatColor> boundColours = new HashMap<>();

    public ColourModifier(ChatColor baseColour) {
        this.baseColour = baseColour;
    }

    public ColourModifier bind(char key, ChatColor colour) {
        boundColours.put(key, colour);
        return this;
    }

    public ColourModifier bind(ChatColor colour) {
        boundColours.put(null, colour);
        return this;
    }

    @Override
    public String apply(String line) {
        String baseColourCode = "§s" + baseColour.getChar();
        String string = baseColourCode + line;
        for (Character character : boundColours.keySet()) {
            String colourCode = "§" + boundColours.get(character).getChar();
            String key = character != null ? "$" + character : "$(\\w|\\d)?";
            Pattern pattern = Pattern.compile(String.format("\\%s\\{[^\\}]{1,20}\\}", key));
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                String group = matcher.group(1);
                String formattedGroup = group.replace(key, colourCode).replaceAll("\\}", baseColourCode);
                string = string.replaceAll(group, formattedGroup);
            }
        }
        return string;
    }
}

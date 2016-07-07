package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public enum Lang {
    HELP(false), NO_CONSOLE(true), PLAYER_NOT_FOUND(true), INVALID_POS(true), NO_PERM(true), ELSE_USING(true),
    SAVE_USAGE(false), NOT_RIDING_EASEL(true), SAVE_SUCCESS(false), EASEL_HELP(false), NEED_CANVAS(true),
    NOT_YOUR_EASEL(true), NO_WORLD(true), BREAK_CANVAS(false), PAINTING(false), DELETED(false),
    MAP_NOT_FOUND(true), MAPDATA_ERROR(true), NO_CRAFT_PERM(true), NO_ARTWORKS(true), BAD_TITLE(true),
    TITLE_USED(true), PREVIEWING(false), EMPTY_HAND_PREVIEW(true), BACKUP_SUCCESS(false), BACKUP_ERROR(true),
    RESTORE_ERROR(true), RESTORE_ALREADY_FOUND(false), RESTORE_SUCCESS(false), INVALID_DATA_TABLES(true),
    CANNOT_BUILD_DATABASE(true), RECIPE_HEADER(false);

    public static final String prefix = "§b[ArtMap] ";
    final boolean isErrorMessage;
    String message;

    Lang(boolean isErrorMessage) {
        this.isErrorMessage = isErrorMessage;
        ArtMap plugin = JavaPlugin.getPlugin(ArtMap.class);
        String language = plugin.getConfig().getString("language");
        FileConfiguration langFile =
                YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));

        if (!langFile.contains(language)) {
            language = "english";
        }
        ConfigurationSection lang = langFile.getConfigurationSection(language);

        if (lang.get(name()) != null) {
            message = lang.getString(name());

        } else {
            Bukkit.getLogger().warning(String.format("%sError loading %s from lang.yml", prefix, name()));
        }
    }

    public String message() {
        ChatColor colour = (isErrorMessage) ? ChatColor.RED : ChatColor.GOLD;
        return prefix + colour + message;
    }

    public String rawMessage() {
        return message;
    }

    public enum Array {
        HELP_GETTING_STARTED, HELP_RECIPES, HELP_TOOLS, HELP_LIST, HELP_CLOSE, HELP_DYES, CONSOLE_HELP,
        INFO_DYES, INFO_RECIPES, INFO_TOOLS, TOOL_DYE, TOOL_PAINTBUCKET, TOOL_COAL, TOOL_FEATHER, TOOL_COMPASS;

        String[] messages;

        Array() {
            ArtMap plugin = JavaPlugin.getPlugin(ArtMap.class);
            String language = plugin.getConfig().getString("language");
            FileConfiguration langFile =
                    YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));

            if (!langFile.contains(language)) {
                language = "english";
            }
            ConfigurationSection lang = langFile.getConfigurationSection(language);

            if (lang.get(name()) != null) {
                List<String> strings = lang.getStringList(name());
                messages = strings.toArray(new String[strings.size()]);

            } else {
                Bukkit.getLogger().warning(String.format("Error loading %s from lang.yml", name()));
            }
        }

        public String[] messages() {
            return messages;
        }
    }
}

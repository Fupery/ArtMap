package me.Fupery.ArtMap.Config;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

class LangLoader {
    private JavaPlugin plugin;
    private ConfigurationSection defaults;
    private ConfigurationSection lang;

    LangLoader(ArtMap plugin, Configuration configuration) {
        this.plugin = plugin;
        String language = configuration.LANGUAGE;
        plugin.getLogger().info(String.format("Loading '%s' language file", language.toLowerCase()));
        defaults = YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));
        lang = null;

        if (language.equalsIgnoreCase("custom")) {
            File customLang = new File(plugin.getDataFolder(), "lang.yml");
            if (!customLang.exists()) {
                try {
                    if (customLang.createNewFile()) Files.copy(plugin.getResource("lang.yml"),
                            customLang.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    logLangError("Failed to build plugins/ArtMap/lang.yml file!");
                }
            }
            lang = YamlConfiguration.loadConfiguration(customLang);

        } else if (!language.equalsIgnoreCase("english")) {
            String languageFileName = String.format("lang%s.yml", File.separator + language);
            Reader langReader = plugin.getTextResourceFile(languageFileName);
            if (langReader != null) {
                lang = YamlConfiguration.loadConfiguration(plugin.getTextResourceFile(languageFileName));
            } else {
                logLangError(String.format("Error loading lang.yml! '%s' is not a valid language.", language));
            }
        }
        if (lang == null) {
            lang = defaults;
        }
        if (configuration.HIDE_PREFIX) Lang.PREFIX = "";
    }

    String loadString(String key) {
        if (!lang.contains(key)) {
            logLangError(String.format("Error loading key from lang.yml: '%s'", key));
            if (defaults == null || !defaults.contains(key)) return null;
            return defaults.getString(key);
        }
        return lang.getString(key);
    }

    String[] loadArray(String key) {
        List<String> messages = lang.getStringList(key);
        if (messages == null) {
            logLangError(String.format("Error loading key from lang.yml: '%s'", key));
            if (defaults == null || !defaults.contains(key)) return new String[]{"[" + key + "] NOT FOUND"};
            messages = defaults.getStringList(key);
        }
        return messages == null ? null : messages.toArray(new String[messages.size()]);
    }

    String[] loadRegex(String key) {
        List<String> msg = lang.getStringList(key);
        if (msg != null) return msg.toArray(new String[msg.size()]);
        return new String[0];
    }

    private void logLangError(String reason) {
        plugin.getLogger().warning(reason + " Default values will be used.");
    }
}

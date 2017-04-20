package me.Fupery.ArtMap.Config;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ErrorLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.List;

class LangLoader {
    private JavaPlugin plugin;
    private FileConfiguration defaults;
    private FileConfiguration lang;
    private boolean usingCustomLang = false;
    private HashMap<String, String> missingStrings = new HashMap<>();

    LangLoader(ArtMap plugin, Configuration configuration) {
        this.plugin = plugin;
        String language = configuration.LANGUAGE;
        plugin.getLogger().info(String.format("Loading '%s' language file", language.toLowerCase()));
        defaults = YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));
        lang = null;

        if (language.equalsIgnoreCase("custom")) {
            usingCustomLang = true;
            File customLang = getCustomLangFile();
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
            usingCustomLang = false;
        }
        if (configuration.HIDE_PREFIX) Lang.PREFIX = "";
    }

    private File getCustomLangFile() {
        File customLang = new File(plugin.getDataFolder(), "lang.yml");
        if (!customLang.exists()) ArtMap.instance().writeResource("lang.yml", customLang);
        return customLang;
    }

    void save() {
        if (!usingCustomLang) return;
        File langFile = getCustomLangFile();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(langFile, true));
            for (String key : missingStrings.keySet()) {
                writer.newLine();
                writer.write(key + ": " + missingStrings.get(key));
            }
            writer.close();
        } catch (IOException e) {
            ErrorLogger.log(e, "Cannot save default keys to lang,yml.");
        }
    }

    String loadString(String key) {
        if (!lang.contains(key)) {
            logLangError(String.format("Error loading key from lang.yml: '%s'", key));
            if (defaults == null || !defaults.contains(key)) return null;
            String defaultString = defaults.getString(key);
            missingStrings.put(key, defaultString);
            return defaultString;
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

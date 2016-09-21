package me.Fupery.ArtMap.Config;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Protocol.Out.WrappedPacket;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public enum Lang implements LangSet<String> {

    COMMAND_SAVE, COMMAND_DELETE, COMMAND_PREVIEW, COMMAND_RESTORE, COMMAND_BACKUP, HELP, SAVE_SUCCESS,
    DELETED, PREVIEWING, BACKUP_SUCCESS, RESTORE_SUCCESS, RESTORE_ALREADY_FOUND, RECIPE_HEADER, NEED_CANVAS,
    NO_CONSOLE, PLAYER_NOT_FOUND, NO_PERM, NOT_RIDING_EASEL, NOT_YOUR_EASEL, BREAK_CANVAS, MAP_NOT_FOUND, NO_ARTWORKS,
    NO_CRAFT_PERM, BAD_TITLE, TITLE_USED, EMPTY_HAND_PREVIEW, MAPDATA_ERROR, BACKUP_ERROR, NO_WORLD,
    RESTORE_ERROR, INVALID_DATA_TABLES, CANNOT_BUILD_DATABASE, MAP_ID_MISSING, RESTORED_SUCCESSFULY, MENU_RECIPE,
    MENU_ARTIST, MENU_ARTWORKS, MENU_DYES, MENU_HELP, MENU_TOOLS, BUTTON_CLICK, BUTTON_CLOSE, BUTTON_BACK,
    RECIPE_BUTTON, ADMIN_RECIPE, RECIPE_HELP, RECIPE_EASEL_NAME, RECIPE_CANVAS_NAME, RECIPE_PAINTBUCKET_NAME;

    public static String PREFIX = "§b[ArtMap] ";
    private String message = null;

    public static void load(ConfigurationSection defaultLang, FileConfiguration langFile, Configuration configuration) {
        LangLoader loader = new LangLoader(defaultLang, langFile, configuration);// TODO: 21/09/2016
        //Load basic messages
        for (Lang key : Lang.values()) {
            key.message = loader.loadString(key.name());
        }
        //Load action bar messages
        for (ActionBar key : ActionBar.values()) {
            String messageString = loader.loadString(key.name());
            if (configuration.DISABLE_ACTION_BAR) {
                String formattedMessage = PREFIX + messageString.replaceAll("§l", "").replaceAll("§3", "§6")
                        .replaceAll("§4", "§c").replaceAll("§b", "§6");
                key.message = new WrappedPacket<String>(formattedMessage) {
                    @Override
                    public void send(Player player) {
                        player.sendMessage(this.rawPacket);
                    }
                };
            } else {
                String formattedMessage = key.isError ?
                        "§c§l✷ " + messageString + " §c§l✷" : "§6✾ " + messageString + " §6✾";
                key.message = ArtMap.getProtocolManager().PACKET_SENDER.buildChatPacket(formattedMessage);
            }
        }
        //Load array messages
        for (Array key : Array.values()) {
            key.messages = loader.loadArray(key.name());
        }
    }

    @Override
    public void send(CommandSender sender) {
        if (message != null) sender.sendMessage(message);
    }

    @Override
    public String get() {
        return message;
    }

    public enum ActionBar implements LangSet<WrappedPacket> {
        EASEL_HELP(false), NEED_CANVAS(true), PAINTING(false), SAVE_USAGE(false), ELSE_USING(true),
        NO_PERM_ACTION(true), NO_EDIT_PERM(true), INVALID_POS(true);

        private WrappedPacket message = null;
        private boolean isError;

        ActionBar(boolean isErrorMessage) {
            isError = false;
        }

        @Override
        public void send(CommandSender sender) {
            if (message != null && sender instanceof Player)
                message.send((Player) sender);// FIXME: 21/09/2016 loading, sending
        }

        @Override
        public WrappedPacket get() {
            return message;
        }
    }

    public enum Array implements LangSet<String[]> {
        HELP_GETTING_STARTED, HELP_RECIPES, HELP_TOOLS, HELP_DYES, HELP_LIST, HELP_CLOSE, INFO_DYES, INFO_RECIPES,
        INFO_TOOLS, TOOL_DYE, TOOL_PAINTBUCKET, TOOL_COAL, TOOL_FEATHER, TOOL_COMPASS, RECIPE_EASEL, RECIPE_CANVAS,
        RECIPE_PAINTBUCKET, CONSOLE_HELP;

        private String[] messages = null;

        @Override
        public void send(CommandSender sender) {
            if (messages != null) sender.sendMessage(messages);
        }

        @Override
        public String[] get() {
            return messages;
        }
    }

    private static class LangLoader {
        private ConfigurationSection defaults;
        private ConfigurationSection lang;

        private LangLoader(ConfigurationSection defaultLang, FileConfiguration langFile, Configuration configuration) {
            String language = configuration.LANGUAGE;
            if (!langFile.contains(language)) language = "english";
            this.defaults = defaultLang;
            lang = langFile.getConfigurationSection(language);
            if (configuration.HIDE_PREFIX) PREFIX = "";
            if (lang == null) Bukkit.getLogger().warning("Error loading lang.yml!");
        }

        private String loadString(String key) {
            if (!lang.contains(key)) {
                Bukkit.getLogger().warning(String.format(
                        "[ArtMap] Error loading key from lang.yml: '%s' Default value used.", key));
                if (defaults == null || !defaults.contains(key)) return "[" + key + "] NOT FOUND";
                lang.set(key, defaults.get(key));
            }
            return lang.getString(key);
        }

        private String[] loadArray(String key) {
            List<String> msg = lang.getStringList(key);
            if (msg != null) return msg.toArray(new String[msg.size()]);
            Bukkit.getLogger().warning("[ArtMap] Error loading key from lang.yml: " + key);
            return null;
        }
    }
}
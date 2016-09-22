package me.Fupery.ArtMap.Config;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Protocol.Out.WrappedPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Lang implements LangSet<String> {

    COMMAND_SAVE, COMMAND_DELETE, COMMAND_PREVIEW, COMMAND_RESTORE, COMMAND_BACKUP, HELP, SAVE_SUCCESS, DELETED,
    PREVIEWING, RECIPE_HEADER, NEED_CANVAS, NO_CONSOLE, PLAYER_NOT_FOUND, NO_PERM, NOT_RIDING_EASEL, NOT_YOUR_EASEL,
    BREAK_CANVAS, MAP_NOT_FOUND, NO_ARTWORKS, NO_CRAFT_PERM, BAD_TITLE, TITLE_USED, EMPTY_HAND_PREVIEW, NO_WORLD,
    INVALID_DATA_TABLES, CANNOT_BUILD_DATABASE, MAP_ID_MISSING, RESTORED_SUCCESSFULY, MENU_RECIPE, MENU_ARTIST,
    MENU_ARTWORKS, MENU_DYES, MENU_HELP, MENU_TOOLS, BUTTON_CLICK, BUTTON_CLOSE, BUTTON_BACK, RECIPE_BUTTON,
    ADMIN_RECIPE, RECIPE_HELP, RECIPE_EASEL_NAME, RECIPE_CANVAS_NAME, RECIPE_PAINTBUCKET_NAME;

    public static String PREFIX = "§b[ArtMap] ";
    private String message = String.format("'%s' NOT FOUND", name());

    public static void load(ArtMap plugin, Configuration configuration) {
        LangLoader loader = new LangLoader(plugin, configuration);// TODO: 21/09/2016
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
        Filter.ILLEGAL_EXPRESSIONS.expressions = loader.loadRegex("ILLEGAL_EXPRESSIONS");
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
            isError = isErrorMessage;
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

    public enum Filter implements LangSet<String[]> {
        ILLEGAL_EXPRESSIONS;

        private String[] expressions = null;

        @Override
        public void send(CommandSender sender) {
            //redundant
        }

        @Override
        public String[] get() {
            return expressions;
        }
    }

}
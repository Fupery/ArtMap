package me.Fupery.ArtMap.Utils;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

import static me.Fupery.ArtMap.Utils.Reflection.ChatPacketBuilder;

public class Lang {
    public static String PREFIX = "§b[ArtMap] ";
    public final ActionBarHandler ACTION_BAR_MESSAGES;
    private final ConfigurationSection defaults;
    private final ConfigurationSection lang;

    public Lang(ConfigurationSection defaultLang, FileConfiguration langFile, Configuration configuration) {
        String language = configuration.LANGUAGE;
        if (!langFile.contains(language)) language = "english";

        if (!language.equals("english")) this.defaults = defaultLang;
        else this.defaults = null;

        lang = langFile.getConfigurationSection(language);
        if (configuration.HIDE_PREFIX) PREFIX = "";
        if (lang == null) {
            Bukkit.getLogger().warning("Error loading lang.yml!");
            ACTION_BAR_MESSAGES = null;
            return;
        }
        this.ACTION_BAR_MESSAGES = new ActionBarHandler(configuration.DISABLE_ACTION_BAR);
    }

    private static WrappedActionBarPacket buildPacket(ChatPacketBuilder builder, String message, boolean isError) {
        String formattedMessage = isError ? "§c§l✷ " + message + " §c§l✷" : "§6✾ " + message + " §6✾";
        return new WrappedActionBarPacket(message, builder.buildActionBarPacket(formattedMessage));
    }

    public String getMsg(String key) {
        if (!lang.contains(key)) {
            Bukkit.getLogger().warning("Error loading key from lang.yml: '" + key + "'! Default value used.");
            if (defaults == null || !defaults.contains(key)) return "[" + key + "] NOT FOUND";
            lang.set(key, defaults.get(key));
        }
        return lang.getString(key);
    }

    public void sendMsg(String key, CommandSender player) {
        player.sendMessage(PREFIX + getMsg(key));
    }

    public String[] getArray(String key) {
        List<String> msg = lang.getStringList(key);
        if (msg != null) return msg.toArray(new String[msg.size()]);
        Bukkit.getLogger().warning("Error loading key from lang.yml: " + key);
        return null;
    }

    public void sendArray(String key, CommandSender player) {
        player.sendMessage(getArray(key));
    }

    public static class MessageSender {
        protected final String message;

        private MessageSender(String message) {
            this.message = PREFIX + message.replaceAll("§l", "")
                    .replaceAll("§3", "§6").replaceAll("§4", "§c").replaceAll("§b", "§6");
        }

        public void send(Player player) {
            player.sendMessage(message);
        }
    }

    public static class WrappedActionBarPacket extends MessageSender {
        private final Object packet;

        private WrappedActionBarPacket(String message, Object packet) {
            super(message);
            this.packet = packet;
        }

        @Override
        public void send(Player player) {
            Channel channel;
            try {
                channel = ArtMap.getCacheManager().getChannel(player.getUniqueId());
            } catch (Exception e) {
                Bukkit.getLogger().info(
                        "[ArtMap] Error binding player channel! Check /plugins/ArtMap/error.log for info.");
                channel = null;
            }
            if (channel != null) channel.writeAndFlush(packet);
            else player.sendMessage(message);
        }
    }

    public final class ActionBarHandler {
        public final MessageSender EASEL_PUNCH,
                EASEL_NO_CANVAS,
                EASEL_MOUNT,
                EASEL_DISMOUNT,
                EASEL_USED,
                EASEL_PERMISSION,
                EASEL_NO_EDIT,
                EASEL_INVALID_POS;

        private ActionBarHandler(boolean disabled) {
            if (!disabled) {
                ChatPacketBuilder packetBuilder = new ChatPacketBuilder();
                EASEL_PUNCH = buildPacket(packetBuilder, getMsg("EASEL_HELP"), false);
                EASEL_NO_CANVAS = buildPacket(packetBuilder, getMsg("NEED_CANVAS"), true);
                EASEL_MOUNT = buildPacket(packetBuilder, getMsg("PAINTING"), false);
                EASEL_DISMOUNT = buildPacket(packetBuilder, getMsg("SAVE_USAGE"), false);
                EASEL_USED = buildPacket(packetBuilder, getMsg("ELSE_USING"), true);
                EASEL_PERMISSION = buildPacket(packetBuilder, getMsg("NO_PERM_ACTION"), true);
                EASEL_NO_EDIT = buildPacket(packetBuilder, getMsg("NO_EDIT_PERM"), true);
                EASEL_INVALID_POS = buildPacket(packetBuilder, getMsg("INVALID_POS"), true);
            } else {
                EASEL_PUNCH = new MessageSender(getMsg("EASEL_HELP"));
                EASEL_NO_CANVAS = new MessageSender(getMsg("NEED_CANVAS"));
                EASEL_MOUNT = new MessageSender(getMsg("PAINTING"));
                EASEL_DISMOUNT = new MessageSender(getMsg("SAVE_USAGE"));
                EASEL_USED = new MessageSender(getMsg("ELSE_USING"));
                EASEL_PERMISSION = new MessageSender(getMsg("NO_PERM_ACTION"));
                EASEL_NO_EDIT = new MessageSender(getMsg("NO_EDIT_PERM"));
                EASEL_INVALID_POS = new MessageSender(getMsg("INVALID_POS"));
            }
        }
    }
}
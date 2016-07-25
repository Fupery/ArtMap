package me.Fupery.ArtMap.Utils;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Lang {
    public static final String PREFIX = "§b[ArtMap] ";
    public final ActionBarHandler ACTION_BAR_MESSAGES;
    private final ConfigurationSection lang;

    public Lang(String language, FileConfiguration langFile) {
        if (!langFile.contains(language)) language = "english";
        lang = langFile.getConfigurationSection(language);
        if (lang == null) {
            Bukkit.getLogger().warning("Error loading lang.yml!");
            ACTION_BAR_MESSAGES = null;
            return;
        }
        this.ACTION_BAR_MESSAGES = new ActionBarHandler();
    }

    private static WrappedActionBarPacket buildPacket(Reflection.ChatPacketBuilder builder, String message) {
        return new WrappedActionBarPacket(builder.buildActionBarPacket(message));
    }

    public String getMsg(String key) {
        String msg = lang.getString(key);
        if (msg != null) return msg;
        Bukkit.getLogger().warning("Error loading key from lang.yml: " + key);
        return null;
    }

    public void sendMsg(String key, CommandSender player) {
        player.sendMessage(PREFIX + getMsg(key));
    }

    public String[] getArray(String key) {
        List<String> msg = lang.getStringList("ARRAY." + key);
        if (msg != null) return msg.toArray(new String[msg.size()]);
        Bukkit.getLogger().warning("Error loading key from lang.yml: " + key);
        return null;
    }

    public void sendArray(String key, CommandSender player) {
        player.sendMessage(getArray(key));
    }

    public static class WrappedActionBarPacket {
        private final Object packet;

        private WrappedActionBarPacket(Object packet) {
            this.packet = packet;
        }

        public void send(Player player) {
            ArtMap.getCacheManager().getChannel(player.getUniqueId()).writeAndFlush(packet);
        }
    }

    public final class ActionBarHandler {
        public final WrappedActionBarPacket EASEL_PUNCH,
                EASEL_NO_CANVAS,
                EASEL_MOUNT,
                EASEL_DISMOUNT,
                EASEL_USED,
                EASEL_PERMISSION,
                EASEL_NO_EDIT,
                EASEL_INVALID_POS;

        private ActionBarHandler() {
            Reflection.ChatPacketBuilder packetBuilder = new Reflection.ChatPacketBuilder();
            EASEL_PUNCH = buildPacket(packetBuilder, getMsg("EASEL_HELP"));
            EASEL_NO_CANVAS = buildPacket(packetBuilder, getMsg("NEED_CANVAS"));
            EASEL_MOUNT = buildPacket(packetBuilder, getMsg("PAINTING"));
            EASEL_DISMOUNT = buildPacket(packetBuilder, getMsg("SAVE_USAGE"));
            EASEL_USED = buildPacket(packetBuilder, getMsg("ELSE_USING"));
            EASEL_PERMISSION = buildPacket(packetBuilder, getMsg("NO_PERM"));
            EASEL_NO_EDIT = buildPacket(packetBuilder, getMsg("NO_EDIT_PERM"));
            EASEL_INVALID_POS = buildPacket(packetBuilder, getMsg("INVALID_POS"));
        }

    }
}
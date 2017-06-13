package me.Fupery.ArtMap.IO.Protocol.Out;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ErrorLogger;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static me.Fupery.ArtMap.Utils.VersionHandler.BukkitVersion.v1_12;

public class GenericPacketSender implements PacketSender {

    private ChatPacketBuilder builder = ArtMap.getBukkitVersion().getVersion().isGreaterOrEqualTo(v1_12)
            ? new ChatPacketBuilder() : new ChatPacketBuilderLegacy();

    private static void logFailure(Exception e) {
        ErrorLogger.log(e, "Failed to instantiate protocol! Is this version supported?");
    }

    @Override
    public WrappedPacket buildChatPacket(String message) {
        return new WrappedPacket<Object>(builder.buildActionBarPacket(message)) {
            private String rawMessage = message;

            @Override
            public void send(Player player) {
                Channel channel;
                try {
                    channel = ArtMap.getCacheManager().getChannel(player.getUniqueId());
                } catch (Exception e) {
                    ErrorLogger.log(e, String.format("Error binding player channel for '%s'!", player.getName()));
                    channel = null;
                }
                if (channel != null) channel.writeAndFlush(this.rawPacket);
                else player.sendMessage(rawMessage);
            }
        };
    }

    private static class ChatPacketBuilderLegacy extends ChatPacketBuilder {
        public ChatPacketBuilderLegacy() {
            this(Reflection.NMS);
        }


        public ChatPacketBuilderLegacy(String NMS_Prefix) {
            String packetClassName = NMS_Prefix + ".PacketPlayOutChat";
            String chatComponentName = NMS_Prefix + ".IChatBaseComponent";
            String chatSerializerName = chatComponentName + "$ChatSerializer";

            try {
                Class chatPacketClass = Class.forName(packetClassName);
                Class chatComponentClass = Class.forName(chatComponentName);
                chatSerializerClass = Class.forName(chatSerializerName);

                packetCons = chatPacketClass.getDeclaredConstructor(chatComponentClass, byte.class);
                chatSerializer = chatSerializerClass.getDeclaredMethod("a", String.class);

            } catch (ClassNotFoundException | NoSuchMethodException e) {
                logFailure(e);
            }
        }

        @Override
        public Object buildActionBarPacket(String message) {
            try {
                Object chatComponent = chatSerializer.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}");
                return packetCons.newInstance(chatComponent, (byte) 2);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logFailure(e);
                return null;
            }
        }
    }

    private static class ChatPacketBuilder {
        protected Constructor packetCons;
        protected Method chatSerializer;
        protected Class chatSerializerClass;
        protected Object chatType;

        public ChatPacketBuilder() {
            this(Reflection.NMS);
        }

        public ChatPacketBuilder(String NMS_Prefix) {
            String packetClassName = NMS_Prefix + ".PacketPlayOutChat";
            String chatComponentName = NMS_Prefix + ".IChatBaseComponent";
            String chatSerializerName = chatComponentName + "$ChatSerializer";
            String chatTypeClassName = NMS_Prefix + ".ChatMessageType";

            try {
                Class chatPacketClass = Class.forName(packetClassName);
                Class chatComponentClass = Class.forName(chatComponentName);
                chatSerializerClass = Class.forName(chatSerializerName);
                Class chatTypeClass = Class.forName(chatTypeClassName);

                Bukkit.getLogger().info(chatTypeClass.getName());//TODO remove logging
                for (Field field : chatTypeClass.getFields()) {
                    Bukkit.getLogger().info(field.getName());//TODO remove logging
                }

                packetCons = chatPacketClass.getDeclaredConstructor(chatComponentClass, chatTypeClass);
                chatSerializer = chatSerializerClass.getDeclaredMethod("a", String.class);
                Field chatTypeField = chatTypeClass.getDeclaredField("GAME_INFO");
                chatType = chatTypeField.get(null);

            } catch (ClassNotFoundException | NoSuchMethodException |
                    IllegalAccessException | NoSuchFieldException e) {
                logFailure(e);
            }
        }

        public Object buildActionBarPacket(String message) {
            try {
                Object chatComponent = chatSerializer.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}");
                return packetCons.newInstance(chatComponent, chatType);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logFailure(e);
                return null;
            }
        }
    }
}

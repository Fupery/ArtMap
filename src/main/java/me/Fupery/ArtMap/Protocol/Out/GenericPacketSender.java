package me.Fupery.ArtMap.Protocol.Out;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ErrorLogger;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GenericPacketSender implements PacketSender {

    private ChatPacketBuilder builder = new ChatPacketBuilder();

    @Override
    public WrappedPacket buildChatPacket(String message) {
        return new WrappedPacket(builder.buildActionBarPacket(message)) {
            @Override
            public void send(Player player) {
                Channel channel;
                try {
                    channel = ArtMap.getCacheManager().getChannel(player.getUniqueId());
                } catch (Exception e) {
                    ErrorLogger.log(e, "Error binding player channel!");
                    channel = null;
                }
                if (channel != null) channel.writeAndFlush(this.rawPacket);
                else player.sendMessage(message);
            }
        };
    }

    private static class ChatPacketBuilder {
        private Constructor packetCons;
        private Method chatSerializer;
        private Class chatSerializerClass;

        public ChatPacketBuilder() {
            this(Reflection.NMS);
        }

        public ChatPacketBuilder(String NMS_Prefix) {
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

        public Object buildActionBarPacket(String message) {
            try {
                Object chatComponent = chatSerializer.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}");
                return packetCons.newInstance(chatComponent, (byte) 2);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logFailure(e);
                return null;
            }
        }

        private void logFailure(Exception e) {
            ErrorLogger.log(e, "Failed to instantiate protocol! Is this version supported?");
        }
    }
}

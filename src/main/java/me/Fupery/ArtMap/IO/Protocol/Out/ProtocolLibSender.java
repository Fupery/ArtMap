package me.Fupery.ArtMap.IO.Protocol.Out;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ErrorLogger;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

import static me.Fupery.ArtMap.Utils.VersionHandler.BukkitVersion.v1_12;

public class ProtocolLibSender implements PacketSender {

    private PacketBuilder builder = ArtMap.getBukkitVersion().getVersion().isGreaterOrEqualTo(v1_12)
            ? new ChatPacketBuilder() : new ChatPacketBuilderLegacy();

    @Override
    public WrappedPacket buildChatPacket(String message) {
        return new WrappedPacket<PacketContainer>(builder.buildChatPacket(message)) {
            @Override
            public void send(Player player) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, this.rawPacket);
                } catch (InvocationTargetException e) {
                    ErrorLogger.log(e);
                }
            }
        };
    }

    interface PacketBuilder {
        PacketContainer buildChatPacket(String message);
    }

    private class ChatPacketBuilderLegacy implements PacketBuilder {
        @Override
        public PacketContainer buildChatPacket(String message) {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
            packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
            packet.getBytes().write(0, (byte) 2);
            return packet;
        }
    }

    private class ChatPacketBuilder implements PacketBuilder {
        @Override
        public PacketContainer buildChatPacket(String message) {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
            packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
            packet.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
            return packet;
        }
    }
}

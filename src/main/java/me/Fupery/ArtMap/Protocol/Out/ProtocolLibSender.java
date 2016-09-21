package me.Fupery.ArtMap.Protocol.Out;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.Fupery.ArtMap.IO.ErrorLogger;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtocolLibSender implements PacketSender {
    @Override
    public WrappedPacket buildChatPacket(String message) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        packet.getBytes().write(0, (byte) 2);

        return new WrappedPacket<PacketContainer>(packet) {
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
}

package me.Fupery.ArtMap.Protocol.Out;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.Fupery.ArtMap.IO.ErrorLogger;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtocolLibSender implements PacketSender {

    private ProtocolManager manager = ProtocolLibrary.getProtocolManager();

    @Override
    public WrappedPacket buildChatPacket(String message) {

        PacketContainer packet = manager.createPacket(PacketType.Play.Client.CHAT);
        packet.getStrings().write(0, message);
        packet.getBytes().write(0, (byte) 2);

        return new WrappedPacket(packet) {
            @Override
            void send(Player player) {
                try {
                    manager.sendServerPacket(player, ((PacketContainer) this.rawPacket));
                } catch (InvocationTargetException e) {
                    ErrorLogger.log(e);
                }
            }
        };
    }
}

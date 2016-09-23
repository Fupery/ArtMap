package me.Fupery.ArtMap.IO.Protocol;

import com.comphenix.protocol.ProtocolLibrary;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Protocol.In.GenericPacketReceiver;
import me.Fupery.ArtMap.IO.Protocol.In.PacketReceiver;
import me.Fupery.ArtMap.IO.Protocol.In.ProtocolLibReceiver;
import me.Fupery.ArtMap.IO.Protocol.Out.GenericPacketSender;
import me.Fupery.ArtMap.IO.Protocol.Out.PacketSender;
import me.Fupery.ArtMap.IO.Protocol.Out.ProtocolLibSender;
import org.bukkit.Bukkit;

public class ProtocolHandler {

    public final PacketReceiver PACKET_RECIEVER;
    public final PacketSender PACKET_SENDER;

    public ProtocolHandler() {
        boolean useProtocolLib = ArtMap.getCompatManager().isPluginLoaded("ProtocolLib");
        try {
            ProtocolLibrary.getProtocolManager();
        } catch (Exception e) {
            useProtocolLib = false;
        }
        if (useProtocolLib) {
            PACKET_RECIEVER = new ProtocolLibReceiver();
            PACKET_SENDER = new ProtocolLibSender();
            Bukkit.getLogger().info("[ArtMap] ProtocolLib hooks enabled.");
        } else {
            PACKET_RECIEVER = new GenericPacketReceiver();
            PACKET_SENDER = new GenericPacketSender();
        }
    }
}

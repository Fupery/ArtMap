package me.Fupery.ArtMap.Protocol;

import com.comphenix.protocol.ProtocolLibrary;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Protocol.In.GenericPacketReciever;
import me.Fupery.ArtMap.Protocol.In.PacketReciever;
import me.Fupery.ArtMap.Protocol.In.ProtocolLibReciever;
import me.Fupery.ArtMap.Protocol.Out.GenericPacketSender;
import me.Fupery.ArtMap.Protocol.Out.PacketSender;
import me.Fupery.ArtMap.Protocol.Out.ProtocolLibSender;
import org.bukkit.Bukkit;

public class ProtocolHandler {

    public final PacketReciever PACKET_RECIEVER;
    public final PacketSender PACKET_SENDER;

    public ProtocolHandler() {
        boolean useProtocolLib = ArtMap.getCompatManager().isPluginLoaded("ProtocolLib");
        try {
            ProtocolLibrary.getProtocolManager();
        } catch (Exception e) {
            useProtocolLib = false;
        }
        if (useProtocolLib) {
            PACKET_RECIEVER = new ProtocolLibReciever();
            PACKET_SENDER = new ProtocolLibSender();
            Bukkit.getLogger().info("[ArtMap] ProtocolLib hooks enabled.");
        } else {
            PACKET_RECIEVER = new GenericPacketReciever();
            PACKET_SENDER = new GenericPacketSender();
        }
    }
}

package me.Fupery.ArtMap.Protocol;

import me.Fupery.ArtMap.Protocol.In.GenericPacketReciever;
import me.Fupery.ArtMap.Protocol.In.PacketReciever;
import me.Fupery.ArtMap.Protocol.In.ProtocolLibReciever;
import me.Fupery.ArtMap.Protocol.Out.GenericPacketSender;
import me.Fupery.ArtMap.Protocol.Out.PacketSender;
import me.Fupery.ArtMap.Protocol.Out.ProtocolLibSender;

public class ProtocolHandler {

    public final PacketReciever PACKET_RECIEVER;
    public final PacketSender PACKET_SENDER;

    public ProtocolHandler(boolean useProtocolLib) {
        if (useProtocolLib) {
            PACKET_RECIEVER = new ProtocolLibReciever();
            PACKET_SENDER = new ProtocolLibSender();
        } else {
            PACKET_RECIEVER = new GenericPacketReciever();
            PACKET_SENDER = new GenericPacketSender();
        }
    }
}

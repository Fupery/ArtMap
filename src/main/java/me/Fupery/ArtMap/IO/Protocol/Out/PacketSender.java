package me.Fupery.ArtMap.IO.Protocol.Out;

public interface PacketSender {
    WrappedPacket buildChatPacket(String message);
}

package me.Fupery.ArtMap.Protocol.Out;

public interface PacketSender {
    WrappedPacket buildChatPacket(String message);
}

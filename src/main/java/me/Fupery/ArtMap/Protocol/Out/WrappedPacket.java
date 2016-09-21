package me.Fupery.ArtMap.Protocol.Out;

import org.bukkit.entity.Player;

public abstract class WrappedPacket<T> {

    protected final T rawPacket;

    public WrappedPacket(T packet) {
        this.rawPacket = packet;
    }

    public abstract void send(Player player);
}

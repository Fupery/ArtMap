package me.Fupery.ArtMap.Protocol.Out;

import org.bukkit.entity.Player;

public abstract class WrappedPacket {

    protected final Object rawPacket;

    public WrappedPacket(Object packet) {
        this.rawPacket = packet;
    }

    public abstract void send(Player player);
}

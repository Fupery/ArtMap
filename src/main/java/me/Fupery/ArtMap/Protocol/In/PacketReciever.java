package me.Fupery.ArtMap.Protocol.In;

import me.Fupery.ArtMap.Painting.ArtistHandler;
import me.Fupery.ArtMap.Protocol.In.Packet.ArtistPacket;
import org.bukkit.entity.Player;

public abstract class PacketReciever {

    public boolean injectPlayer(Player player) {
        return true;
    }

    public void uninjectPlayer(Player player) {
    }

    public abstract void close();

    /**
     * @param handler The artistHandler managing painting artists
     * @param player  The player sending the rawPacket
     * @param packet  The rawPacket recieved
     * @return true if the rawPacket should be passed on, false if the event should be cancelled
     */
    boolean onPacketPlayIn(ArtistHandler handler, Player player, ArtistPacket packet) {
        return handler.handlePacket(player, packet);
    }
}

package me.Fupery.ArtMap.Protocol;

import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import org.bukkit.entity.Player;

interface ProtocolHandler {

    boolean injectPlayer(Player player);

    void uninjectPlayer(Player player);

    void close();

    /**
     * @param player
     * @param packet
     * @return true if the packet should be passed on, false if the event should be cancelled
     */
    boolean onPacketPlayIn(Player player, ArtistPacket packet);
}

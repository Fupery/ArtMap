package me.Fupery.ArtMap.NMS;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import org.bukkit.entity.Player;

public interface NMSInterface {

    Channel getPlayerChannel(Player player);

    ArtistPacket getArtistPacket(Object packet);
}
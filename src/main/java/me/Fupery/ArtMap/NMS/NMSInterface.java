package me.Fupery.ArtMap.NMS;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import org.bukkit.entity.Player;

public interface NMSInterface {

    public Channel getPlayerChannel(Player player);

    public ArtistPacket getArtistPacket(Object packet);
}
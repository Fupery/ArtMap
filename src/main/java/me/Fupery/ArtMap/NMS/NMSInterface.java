package me.Fupery.ArtMap.NMS;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

public interface NMSInterface {

    Channel getPlayerChannel(Player player);

    ArtistPacket getArtistPacket(Object packet);

    byte[] getMap(MapView mapView);

    void setWorldMap(MapView mapView, byte[] colors);
}
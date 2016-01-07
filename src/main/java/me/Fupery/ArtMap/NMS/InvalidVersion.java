package me.Fupery.ArtMap.NMS;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

public class InvalidVersion implements NMSInterface {

    private final String version;

    InvalidVersion(String version) {
        this.version = version;
    }

    @Override
    public Channel getPlayerChannel(Player player) {
        return null;
    }

    @Override
    public ArtistPacket getArtistPacket(Object packet) {
        return null;
    }

    @Override
    public byte[] getMap(MapView mapView) {
        return new byte[0];
    }

    @Override
    public boolean isMapArt(MapView mapView) {
        return false;
    }

    @Override
    public void setWorldMap(MapView mapView, byte[] colors) {

    }

    public String getVersion() {
        return version;
    }
}

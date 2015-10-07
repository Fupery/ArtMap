package me.Fupery.ArtMap.NMS;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import org.bukkit.entity.Player;

public class InvalidVersion implements NMSInterface {

    private String version;

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

    public String getVersion() {
        return version;
    }
}

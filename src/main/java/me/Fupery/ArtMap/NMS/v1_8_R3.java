package me.Fupery.ArtMap.NMS;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Protocol.Packet.*;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class v1_8_R3 implements NMSInterface {
    @Override
    public Channel getPlayerChannel(Player player) {
        CraftPlayer craftPlayer = ((CraftPlayer) player);
        return craftPlayer.getHandle().playerConnection.networkManager.channel;
    }

    @Override
    public ArtistPacket getArtistPacket(Object packet) {
        PacketType type = PacketType.getPacketType(packet);

        if (type != null) {

            switch (type) {
                case LOOK :
                    PacketPlayInFlying.PacketPlayInLook packetLook
                            = (PacketPlayInFlying.PacketPlayInLook) packet;
                    return new ArtistPacket.PacketLook(packet, type, packetLook.d(), packetLook.e());

                case ARM_ANIMATION:
                    return new ArtistPacket.PacketArmSwing(packet, type);

                case STEER_VEHICLE:
                    PacketPlayInSteerVehicle packetVehicle
                            = (PacketPlayInSteerVehicle) packet;
                    return new ArtistPacket.PacketVehicle(packet, type, packetVehicle.d());

                default:
                    break;
            }
        }
        return null;
    }
}

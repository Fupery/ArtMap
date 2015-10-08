package me.Fupery.ArtMap.NMS;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Protocol.Packet.PacketType;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.map.CraftMapView;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;

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
                case LOOK:
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

    @Override
    public byte[] getMap(MapView mapView) {
        net.minecraft.server.v1_8_R3.WorldMap worldMap;

        try {
            Field wm = mapView.getClass().getDeclaredField("worldMap");
            wm.setAccessible(true);
            worldMap = ((net.minecraft.server.v1_8_R3.WorldMap) wm.get(mapView));

        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            worldMap = null;
        }
        if (worldMap == null) {
            return new byte[128 * 128];
        }
        return worldMap.colors;
    }

    @Override
    public void setWorldMap(MapView mapView, byte[] colors) {

        net.minecraft.server.v1_8_R3.WorldMap worldMap;

        try {
            Field wm = mapView.getClass().getDeclaredField("worldMap");
            wm.setAccessible(true);
            worldMap = ((net.minecraft.server.v1_8_R3.WorldMap) wm.get(mapView));

        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            worldMap = null;
        }
        if (worldMap == null) {
            return;
        }

        final CraftMapView craftMap = (CraftMapView) mapView;
        craftMap.setScale(MapView.Scale.FARTHEST);

        worldMap.centerX = -999999;
        worldMap.centerZ = -999999;
        worldMap.map = 5;
        worldMap.colors = colors;
    }
}

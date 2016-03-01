package me.Fupery.ArtMap.NMS;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Protocol.Packet.PacketType;
import net.minecraft.server.v1_9_R1.PacketPlayInFlying;
import net.minecraft.server.v1_9_R1.PacketPlayInUseEntity;
import net.minecraft.server.v1_9_R1.WorldMap;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.map.CraftMapView;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;

public class v1_9_R1 implements NMSInterface {
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
                    return new ArtistPacket.PacketLook(packetLook.a((float) 0), packetLook.b((float) 0));

                case ARM_ANIMATION:
                    return new ArtistPacket.PacketArmSwing();

                case INTERACT:
                    PacketPlayInUseEntity packetInteract =
                            (PacketPlayInUseEntity) packet;

                    ArtistPacket.PacketInteract.InteractType interactType =
                            (packetInteract.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) ?
                                    ArtistPacket.PacketInteract.InteractType.ATTACK :
                                    ArtistPacket.PacketInteract.InteractType.INTERACT;

                    return new ArtistPacket.PacketInteract(interactType);

                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public byte[] getMap(MapView mapView) {
        WorldMap worldMap;

        try {
            Field worldMapField = mapView.getClass().getDeclaredField("worldMap");
            worldMapField.setAccessible(true);
            worldMap = ((WorldMap) worldMapField.get(mapView));

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
    public boolean isMapArt(MapView mapView) {
        WorldMap worldMap;

        try {
            Field worldMapField = mapView.getClass().getDeclaredField("worldMap");
            worldMapField.setAccessible(true);
            worldMap = ((WorldMap) worldMapField.get(mapView));

        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            return false;
        }
        return (worldMap.centerX == -999999
                && worldMap.centerZ == -999999
                && worldMap.map == 5);
    }

    @Override
    public void setWorldMap(MapView mapView, byte[] colors) {
        WorldMap worldMap;

        try {
            Field worldMapField = mapView.getClass().getDeclaredField("worldMap");
            worldMapField.setAccessible(true);
            worldMap = ((WorldMap) worldMapField.get(mapView));

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

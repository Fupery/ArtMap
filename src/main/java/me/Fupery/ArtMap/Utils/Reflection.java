package me.Fupery.ArtMap.Utils;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Protocol.Packet.PacketType;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static Channel getPlayerChannel(Player player) {
        Object nmsPlayer, playerConnection, networkManager;
        Channel channel;

        try {
            nmsPlayer = invokeMethod(player, "getHandle");
            playerConnection = getField(nmsPlayer, "playerConnection");
            networkManager = getField(playerConnection, "networkManager");
            channel = (Channel) getField(networkManager, "channel");

        } catch (NoSuchFieldException | NoSuchMethodException
                | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return channel;
    }

    public static Object getField(Object obj, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    public static void setField(Object obj, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static Object invokeMethod(Object obj, String methodName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = obj.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(obj);
    }

    public static ArtistPacket getArtistPacket(Object packet) {
        PacketType type = PacketType.getPacketType(packet);

        if (type == null) {
            return null;
        }
        switch (type) {
            case LOOK:

                float yaw, pitch;
                try {
                    yaw = ArtMap.bukkitVersion.getYaw(packet);
                    pitch = ArtMap.bukkitVersion.getPitch(packet);

                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
                return new ArtistPacket.PacketLook(yaw, pitch);

            case ARM_ANIMATION:
                return new ArtistPacket.PacketArmSwing();

            case INTERACT:

                Object packetInteractType;

                try {
                    packetInteractType = invokeMethod(packet, "a");
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }

                Class enumEntityUseActionType = packetInteractType.getClass();
                Object[] enumValues = enumEntityUseActionType.getEnumConstants();
                int i;

                for (i = 0; i < enumValues.length; i++) {

                    if (packetInteractType == enumValues[i]) {
                        break;
                    }
                }

                ArtistPacket.PacketInteract.InteractType interactType = (i == 1) ?
                        ArtistPacket.PacketInteract.InteractType.ATTACK :
                        ArtistPacket.PacketInteract.InteractType.INTERACT;

                return new ArtistPacket.PacketInteract(interactType);

            default:
                break;
        }
        return null;
    }

    public static byte[] getMap(MapView mapView) {
        byte colors[];

        try {
            Object worldMap = getField(mapView, "worldMap");
            colors = (byte[]) getField(worldMap, "colors");

        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            colors = null;
        }
        if (colors == null) {
            return new byte[128 * 128];
        }
        return colors;
    }

    public static boolean isMapArt(MapView mapView) {
        int centerX, centerZ, map;

        try {
            Object worldMap = getField(mapView, "worldMap");
            centerX = (int) getField(worldMap, "centerX");
            centerZ = (int) getField(worldMap, "centerZ");
            map = (byte) getField(worldMap, "map");

        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            return false;
        }
        return (centerX == -999999
                && centerZ == -999999
                && map == 5);
    }

    public static void setWorldMap(MapView mapView, byte[] colors) {
        try {
            Object worldMap = getField(mapView, "worldMap");
            setField(worldMap, "centerX", -999999);
            setField(worldMap, "centerZ", -999999);
            setField(worldMap, "map", (byte) 5);
            setField(worldMap, "colors", colors);

        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        mapView.setScale(MapView.Scale.FARTHEST);
    }
}

package me.Fupery.ArtMap.Protocol;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

enum PacketType {
    LOOK("PacketPlayInFlying.PacketPlayInLook"),
    ARM_ANIMATION("PacketPlayInArmAnimation"),
    STEER_VEHICLE("PacketPlayInSteerVehicle");

    String className;

    PacketType(String className) {
        String server = Bukkit.getServer().getClass().getPackage().getName();
        String prefix = server.replace("org.bukkit.craftbukkit", "net.minecraft.server");
        this.className = prefix + "." + className;
    }
}

public class ArtistPacket {
    private Object packet;
    private PacketType type;

    private ArtistPacket(Object packet, PacketType type) {
        this.packet = packet;
        this.type = type;
    }

    public static ArtistPacket getArtistPacket(Object packet) {
        ArtistPacket ArtMapPacket = null;

        for (PacketType type : PacketType.values()) {
            String className = packet.getClass().getCanonicalName();

            if (className.equals(type.className)) {
                ArtMapPacket = new ArtistPacket(packet, type);
                break;
            }
        }
        return ArtMapPacket;
    }

    public <T> T getField(Class<T> fieldClass, String fieldName) {
        Object value = null;

        try {
            Field field = packet.getClass().getDeclaredField(fieldName);

            if (field.getType() == fieldClass) {
                field.setAccessible(true);
                value = field.get(packet);
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("Packet read error for field "
                    + fieldName + ", " + packet.getClass());
        }

        return (T) value;
    }

    public <T> T getSuperField(Class<T> fieldClass, String fieldName) {
        Object value = null;

        try {
            Field field = packet.getClass().getSuperclass().getDeclaredField(fieldName);

            if (field.getType() != fieldClass) {
                field = packet.getClass().getSuperclass().getDeclaredField(fieldName);
            }
            field.setAccessible(true);
            value = field.get(packet);

        } catch (Exception e) {
            Bukkit.getLogger().warning("Packet read error for field "
                    + fieldName + ", " + packet.getClass());
        }
        return (T) value;
    }

    public PacketType getType() {
        return type;
    }
}
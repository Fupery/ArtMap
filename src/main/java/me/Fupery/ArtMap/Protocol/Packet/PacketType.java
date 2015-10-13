package me.Fupery.ArtMap.Protocol.Packet;

import org.bukkit.Bukkit;

public enum PacketType {
    LOOK("PacketPlayInFlying.PacketPlayInLook"),
    ARM_ANIMATION("PacketPlayInArmAnimation"),
    STEER_VEHICLE("PacketPlayInSteerVehicle"),
    INTERACT("PacketPlayInUseEntity"),
    INVALID(null);

    private String className;

    PacketType(String className) {
        String server = Bukkit.getServer().getClass().getPackage().getName();
        String prefix = server.replace("org.bukkit.craftbukkit", "net.minecraft.server");
        this.className = prefix + "." + className;
    }

    public static PacketType getPacketType(Object packet) {
        String packetClassName = packet.getClass().getCanonicalName();

        for (PacketType type : PacketType.values()) {

            if (packetClassName.equals(type.className)) {
                return type;
            }
        }
        return INVALID;
    }
}

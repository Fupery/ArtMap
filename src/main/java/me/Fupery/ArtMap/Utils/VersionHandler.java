package me.Fupery.ArtMap.Utils;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum  VersionHandler {
    UNKNOWN(0, 0), v1_8(1.2, -2.22), v1_9(1.219, -2.24979);

    final double seatXOffset;
    final double seatZOffset;

    VersionHandler(double seatXOffset, double seatZOffset) {
        this.seatXOffset = seatXOffset;
        this.seatZOffset = seatZOffset;
    }

    public static VersionHandler getVersion() {
        String bukkit = Bukkit.getBukkitVersion();
        String version = bukkit.substring(0, bukkit.indexOf('-'));
        String superversion = version.substring(0, bukkit.indexOf('.'));
        String subversion = version.substring(bukkit.indexOf('.'), version.length());
        String result = superversion + "." + subversion.replace(".", "");
        Double verID = Double.parseDouble(result);
        if (verID > 1.79 && verID < 1.9) {
            return v1_8;
        } else if (verID < 2.0) {
            return v1_9;
        }
        return UNKNOWN;
    }

    public static VersionHandler getLatest() {
        VersionHandler[] handlers = values();
        return handlers[values().length - 1];
    }
    public float getEulerValue(Object packet, String methodName) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method method = packet.getClass().getMethod(methodName, float.class);
        method.setAccessible(true);
        return (float) method.invoke(packet, (float) 0);
    }

    public float getYaw(Object packet) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        return (this == v1_8) ? (float) Reflection.invokeMethod(packet, "d") : getEulerValue(packet, "a");
    }

    public float getPitch(Object packet) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        return (this == v1_8) ? (float) Reflection.invokeMethod(packet, "e") : getEulerValue(packet, "b");
    }

    public double getSeatXOffset() {
        return seatXOffset;
    }

    public double getSeatZOffset() {
        return seatZOffset;
    }
}

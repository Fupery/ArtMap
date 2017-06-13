package me.Fupery.ArtMap.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VersionHandler {


    private final BukkitVersion version;

    public VersionHandler() {
        version = checkVersion();
    }

    private static BukkitVersion checkVersion() {
        Version version = Version.getBukkitVersion();
        if (version.isLessThan(1, 9)) return BukkitVersion.v1_8;
        else if (version.isLessThan(1, 10)) return BukkitVersion.v1_9;
        else if (version.isLessThan(1, 11)) return BukkitVersion.v1_10;
        else if (version.isLessThan(1, 12)) return BukkitVersion.v1_11;
        else return BukkitVersion.v1_12;
    }

    public static BukkitVersion getLatest() {
        BukkitVersion[] handlers = BukkitVersion.values();
        return handlers[handlers.length - 1];
    }

    public BukkitVersion getVersion() {
        return version;
    }

    public enum BukkitVersion {
        UNKNOWN, v1_8, v1_9, v1_10, v1_11, v1_12;

        public boolean isGreaterThan(BukkitVersion version) {
            return ordinal() > version.ordinal();
        }

        public boolean isGreaterOrEqualTo(BukkitVersion version) {
            return ordinal() >= version.ordinal();
        }

        public boolean isEqualTo(BukkitVersion version) {
            return ordinal() == version.ordinal();
        }

        public boolean isLessOrEqualTo(BukkitVersion version) {
            return ordinal() <= version.ordinal();
        }

        public boolean isLessThan(BukkitVersion version) {
            return ordinal() < version.ordinal();
        }

        public float getEulerValue(Object packet, String methodName) throws NoSuchMethodException,
                InvocationTargetException, IllegalAccessException {
            Method method = packet.getClass().getMethod(methodName, float.class);
            method.setAccessible(true);
            return (float) method.invoke(packet, (float) 0);
        }

        private float getOldEulerValue(Object packet, String methodName) throws NoSuchMethodException,
                InvocationTargetException, IllegalAccessException {
            Method method = packet.getClass().getMethod(methodName);
            method.setAccessible(true);
            return (float) method.invoke(packet);
        }

        public float getYaw(Object packet) throws NoSuchMethodException,
                IllegalAccessException, InvocationTargetException {
            return (this == v1_8) ? getOldEulerValue(packet, "d") : getEulerValue(packet, "a");
        }

        public float getPitch(Object packet) throws NoSuchMethodException,
                IllegalAccessException, InvocationTargetException {
            return (this == v1_8) ? getOldEulerValue(packet, "e") : getEulerValue(packet, "b");
        }

        public double getSeatXOffset() {
            return this == v1_8 ? 1.2 : 1.219;
        }

        public double getSeatYOffset() {
            return this == v1_8 ? -2.22 : -2.24979;
        }

    }

}

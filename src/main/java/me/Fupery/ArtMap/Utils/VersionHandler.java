package me.Fupery.ArtMap.Utils;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum VersionHandler {
    UNKNOWN(0, 0), v1_8(1.2, -2.22), v1_9(1.219, -2.24979);

    final double seatXOffset;
    final double seatZOffset;

    VersionHandler(double seatXOffset, double seatZOffset) {
        this.seatXOffset = seatXOffset;
        this.seatZOffset = seatZOffset;
    }

    public static VersionHandler getVersion() {
        String bukkit = Bukkit.getBukkitVersion();
        String[] ver = bukkit.substring(0, bukkit.indexOf('-')).split("\\.");
        int[] verNumbers = new int[ver.length];
        for (int i = 0; i < ver.length; i++) {
            verNumbers[i] = Integer.parseInt(ver[i]);
        }
        Version version = new Version(verNumbers);
        if (version.isLessThan(1, 9)) return v1_8;
        else return v1_9;
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

    static class Version implements Comparable<Version> {
        final int[] numbers;

        Version(int... numbers) {
            this.numbers = numbers;
        }

        @Override
        public int compareTo(Version ver) {
            int len = (ver.numbers.length > numbers.length) ? ver.numbers.length : numbers.length;
            for (int i = 0; i < len; i++) {
                int a = i < numbers.length ? numbers[i] : 0;
                int b = i < ver.numbers.length ? ver.numbers[i] : 0;
                if (a != b) {
                    return (a > b) ? 1 : -1;
                }
            }
            return 0;
        }

        boolean isGreaterThan(int... numbers) {
            return compareTo(new Version(numbers)) == 1;
        }

        boolean isLessThan(int... numbers) {
            return compareTo(new Version(numbers)) == -11;
        }
    }
}

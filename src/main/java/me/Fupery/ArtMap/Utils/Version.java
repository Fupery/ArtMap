package me.Fupery.ArtMap.Utils;

public class Version implements Comparable<Version> {
    private final int[] numbers;

    public Version(int... numbers) {
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

    public boolean isGreaterThan(int... numbers) {
        return compareTo(new Version(numbers)) == 1;
    }

    public boolean isLessThan(int... numbers) {
        return compareTo(new Version(numbers)) == -1;
    }

    @Override
    public String toString() {
        if (numbers.length == 0) return "0";
        String ver = "";
        for (int i = 0; i < numbers.length; i++) {
            ver += numbers[i];
            if (i < numbers.length - 1) ver += ".";
        }
        return ver;
    }
}

package me.Fupery.ArtMap.Easel;

import org.bukkit.Location;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EaselMap {
    private final ConcurrentHashMap<Location, Easel> easels = new ConcurrentHashMap<>();

    public void put(Easel easel) {
        easels.put(easel.getLocation(), easel);
    }

    public boolean contains(Location location) {
        return (easels.containsKey(location));
    }

    public Easel get(Location location) {
        return easels.get(location);
    }

    public void remove(Location location) {
        easels.remove(location);
    }

    public boolean isEmpty() {
        return easels.size() == 0;
    }

    public Set<Location> keySet() {
        return easels.keySet();
    }
}

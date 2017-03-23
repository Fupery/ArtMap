package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.map.MapView;

public class Map {
    private short id;
    private MapView mapView;

    public Map(short id) {
        this.id = id;
        this.mapView = ArtMap.getMapManager().getMap(id);
    }

    public Map(short id, MapView mapView) {
        this.id = id;
        this.mapView = mapView;
    }

    public short getId() {
        return id;
    }

    public MapView getMapView() {
        return mapView;
    }
}

package me.Fupery.ArtMap.IO.Database;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.Painting.GenericMapRenderer;
import me.Fupery.ArtMap.Utils.BukkitGetter;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;
import java.util.Arrays;

public class Map {

    public static final byte[] BLANK_MAP = getBlankMap();

    private final short mapId;
    private MapView mapView;

    public Map(short mapId) {
        this.mapId = mapId;
        this.mapView = null;
    }

    Map(MapView mapView) {
        this.mapId = mapView.getId();
        this.mapView = mapView;
    }

    private static byte[] getBlankMap() {
        byte[] mapOutput = new byte[Size.MAX.value];
        Arrays.fill(mapOutput, ArtMap.getColourPalette().getDefaultColour().getColour());
        return mapOutput;
    }

    public static File getMapDataFolder() {
        return new File(ArtMap.instance()
                .getDataFolder().getParentFile().getParentFile().getParent() //Navigate to the server root folder
                + File.separator + ArtMap.getConfiguration().WORLD           //Navigate to the correct world
                + File.separator + "data"                                    //Navigate to this world's data folder
        );
    }

    public CompressedMap compress() {
        return CompressedMap.compress(getMap());
    }

    public byte[] getData() {
        return Reflection.getMap(getMap());
    }

    public void setRenderer(MapRenderer renderer) {
        MapView mapView = getMap();
        mapView.getRenderers().forEach(mapView::removeRenderer);
        if (renderer != null) mapView.addRenderer(renderer);
    }

    public Map cloneArtwork() {
        MapView newMapView = Bukkit.getServer().createMap(Bukkit.getWorld(ArtMap.getConfiguration().WORLD));
        Map newMap = new Map(newMapView);
        newMap.setMap(getData());
        return newMap;
    }

    public MapView getMap() {
        return Bukkit.getMap(mapId);
    }

    public void setMap(byte[] map) {
        MapView mapView = getMapView();
        Reflection.setWorldMap(mapView, map);
        setRenderer(new GenericMapRenderer(map));//todo sync?
    }

    public boolean exists() {
        return getMap() != null;
    }

    public void update(Player player) {
        ArtMap.getTaskManager().runSafely(() -> player.sendMap(getMap()));
    }

    public short getMapId() {
        return mapId;
    }

    private MapView getMapView() {
        //todo We probably don't need sophisticated mapView caching right now
        return (mapView != null) ? mapView :
                (mapView = new BukkitGetter<>(() -> Bukkit.getMap(mapId)).get());
    }

    public enum Size {
        MAX(128 * 128), STANDARD(32 * 32);
        public final int value;

        Size(int length) {
            this.value = length;
        }

        public int size() {
            return value;
        }
    }
}
